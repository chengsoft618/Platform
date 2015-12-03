/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects;

public enum DeviceFunction {
    START_SELF_TEST,
    STOP_SELF_TEST,
    SET_LIGHT,
    GET_DEVICE_AUTHORIZATION,
    SET_EVENT_NOTIFICATIONS,
    SET_DEVICE_AUTHORIZATION,
    GET_EVENT_NOTIFICATIONS,
    UPDATE_FIRMWARE,
    GET_FIRMWARE_VERSION,
    SET_TARIFF_SCHEDULE,
    SET_LIGHT_SCHEDULE,
    SET_CONFIGURATION,
    GET_CONFIGURATION,
    GET_STATUS,
    GET_LIGHT_STATUS,
    GET_TARIFF_STATUS,
    REMOVE_DEVICE,
    GET_ACTUAL_POWER_USAGE,
    GET_POWER_USAGE_HISTORY,
    RESUME_SCHEDULE,
    SET_REBOOT,
    SET_TRANSITION,
    UPDATE_KEY,
    REVOKE_KEY,
    FIND_SCHEDULED_TASKS,
    REGISTER_DEVICE,
    ADD_EVENT_NOTIFICATION,
    ADD_METER,
    FIND_EVENTS,
    REQUEST_PERIODIC_METER_DATA,
    SYNCHRONIZE_TIME,
    REQUEST_SPECIAL_DAYS,
    SET_ALARM_NOTIFICATIONS,
    SET_CONFIGURATION_OBJECT,
    SET_ACTIVITY_CALENDAR,
    REQUEST_ACTUAL_METER_DATA,
    READ_ALARM_REGISTER,
}
