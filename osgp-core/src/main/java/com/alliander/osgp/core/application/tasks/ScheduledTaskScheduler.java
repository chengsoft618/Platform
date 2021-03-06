/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.tasks;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.alliander.osgp.core.application.config.SchedulingConfig;
import com.alliander.osgp.core.application.services.DeviceRequestMessageService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.ScheduledTask;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.valueobjects.ScheduledTaskStatusType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;

@Component
public class ScheduledTaskScheduler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskScheduler.class);

    @Autowired
    private DeviceRequestMessageService deviceRequestMessageService;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SchedulingConfig schedulingConfig;

    @Override
    public void run() {
        LOGGER.info("Processing scheduled tasks");

        this.processScheduledTasks(ScheduledTaskStatusType.NEW);
        this.processScheduledTasks(ScheduledTaskStatusType.RETRY);
    }

    private void processScheduledTasks(final ScheduledTaskStatusType type) {
        List<ScheduledTask> scheduledTasks = this.getScheduledTasks(type);

        while (!scheduledTasks.isEmpty()) {
            for (ScheduledTask scheduledTask : scheduledTasks) {
                LOGGER.info("Processing scheduled task for device [{}] to perform [{}]  ",
                        scheduledTask.getDeviceIdentification(), scheduledTask.getMessageType());
                try {
                    scheduledTask.setPending();
                    scheduledTask = this.scheduledTaskRepository.save(scheduledTask);
                    final ProtocolRequestMessage protocolRequestMessage = this
                            .createProtocolRequestMessage(scheduledTask);
                    this.deviceRequestMessageService.processMessage(protocolRequestMessage);
                } catch (final FunctionalException e) {
                    LOGGER.error("Processing scheduled task failed.", e);
                    this.scheduledTaskRepository.delete(scheduledTask);
                }
            }
            scheduledTasks = this.getScheduledTasks(type);
        }
    }

    /**
     * Fetch scheduled tasks for given scheduledTaskStatusTypes: NEW and RETRY.
     * The processed tasks are set to PENDING, so they will not be fetched by
     * this method.
     *
     * @param type
     *            ScheduledTaskStatusType (NEW, PENDING, COMPLETE, FAILED,
     *            RETRY)
     * @return
     */
    private List<ScheduledTask> getScheduledTasks(final ScheduledTaskStatusType type) {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // configurable page size for scheduled tasks
        final Pageable pageable = new PageRequest(0, this.schedulingConfig.scheduledTaskPageSize());

        return this.scheduledTaskRepository.findByStatusAndScheduledTimeLessThan(type, timestamp, pageable);
    }

    private ProtocolRequestMessage createProtocolRequestMessage(final ScheduledTask scheduledTask) {
        final Device device = this.deviceRepository.findByDeviceIdentification(scheduledTask.getDeviceIdentification());

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                scheduledTask.getDeviceIdentification(), scheduledTask.getOrganisationIdentification(),
                scheduledTask.getCorrelationId(), scheduledTask.getMessageType(), scheduledTask.getMessagePriority());

        final String ipAddress;
        if (device.getNetworkAddress() == null) {
            ipAddress = null;
        } else {
            ipAddress = device.getNetworkAddress().getHostAddress();
        }

        return new ProtocolRequestMessage.Builder().deviceMessageMetadata(deviceMessageMetadata)
                .domain(scheduledTask.getDomain()).domainVersion(scheduledTask.getDomainVersion()).ipAddress(ipAddress)
                .request(scheduledTask.getMessageData()).retryCount(scheduledTask.getRetry()).scheduled(true).build();
    }

}
