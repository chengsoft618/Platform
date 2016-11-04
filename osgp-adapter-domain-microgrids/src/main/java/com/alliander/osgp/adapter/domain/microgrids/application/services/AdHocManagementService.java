/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.microgrids.application.services;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.microgrids.application.mapping.DomainMicrogridsMapper;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.microgrids.entities.RtuDevice;
import com.alliander.osgp.domain.microgrids.valueobjects.EmptyResponse;
import com.alliander.osgp.domain.microgrids.valueobjects.GetDataRequest;
import com.alliander.osgp.domain.microgrids.valueobjects.GetDataResponse;
import com.alliander.osgp.domain.microgrids.valueobjects.SetDataRequest;
import com.alliander.osgp.dto.valueobjects.microgrids.EmptyResponseDto;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataRequestDto;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataResponseDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SetDataRequestDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainMicrogridsAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

    @Autowired
    private DomainMicrogridsMapper mapper;

    /**
     * Constructor
     */
    public AdHocManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === GET DATA ===

    public void getData(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final GetDataRequest dataRequest)
            throws FunctionalException {

        LOGGER.info("Get data for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final GetDataRequestDto dto = this.mapper.map(dataRequest, GetDataRequestDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto), messageType,
                device.getIpAddress());
    }

    public void handleGetDataResponse(final GetDataResponseDto dataResponseDto, final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        GetDataResponse dataResponse = null;
        OsgpException exception = null;

        try {
            if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            this.handleResponseMessageReceived(deviceIdentification);

            dataResponse = this.mapper.map(dataResponseDto, GetDataResponse.class);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            exception = new TechnicalException(ComponentType.DOMAIN_MICROGRIDS, "Exception occurred while getting data",
                    e);
        }

        // Support for Push messages, generate correlationUid
        String actualCorrelationUid = correlationUid;
        if ("no-correlationUid".equals(actualCorrelationUid)) {
            actualCorrelationUid = this.getCorrelationId("DeviceGenerated", deviceIdentification);
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(actualCorrelationUid, organisationIdentification,
                deviceIdentification, result, exception, dataResponse), messageType);
    }

    // === SET DATA ===

    public void handleSetDataRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String messageType, final SetDataRequest setDataRequest)
            throws FunctionalException {

        LOGGER.info("Set data for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final SetDataRequestDto dto = this.mapper.map(setDataRequest, SetDataRequestDto.class);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto), messageType,
                device.getIpAddress());
    }

    public void handleSetDataResponse(final EmptyResponseDto emptyResponseDto, final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException) {

        LOGGER.info("handleResponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        EmptyResponse emptyResponse = null;
        OsgpException exception = null;

        try {
            if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            this.handleResponseMessageReceived(deviceIdentification);

            emptyResponse = this.mapper.map(emptyResponseDto, EmptyResponse.class);

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            exception = new TechnicalException(ComponentType.DOMAIN_MICROGRIDS, "Exception occurred while setting data",
                    e);
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, exception, emptyResponse), messageType);
    }

    private String getCorrelationId(final String organisationIdentification, final String deviceIdentification) {

        return organisationIdentification + "|||" + deviceIdentification + "|||" + UUID.randomUUID().toString();
    }

    private void handleResponseMessageReceived(final String deviceIdentification) {
        final RtuDevice device = this.rtuDeviceRepository.findByDeviceIdentification(deviceIdentification);
        device.messageReceived();
        this.rtuDeviceRepository.save(device);
    }
}
