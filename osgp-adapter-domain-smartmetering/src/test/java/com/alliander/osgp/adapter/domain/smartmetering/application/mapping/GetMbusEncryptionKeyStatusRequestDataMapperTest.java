/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;

public class GetMbusEncryptionKeyStatusRequestDataMapperTest {

    private static final String MAPPED_OBJECT_VALUE_MESSAGE = "Mapped object should not be null.";
    private static final String MAPPED_FIELD_VALUE_MESSAGE = "Mapped field should have the same value.";

    private static final String MBUS_DEVICE_IDENTIFICATION = "TestMbusDevice";
    private static final Short CHANNEL = 1;

    private ConfigurationMapper mapper = new ConfigurationMapper();

    @Test
    public void shouldConvertValueObjectToDto() {
        final GetMbusEncryptionKeyStatusRequestData source = this.makeRequest();
        final GetMbusEncryptionKeyStatusRequestDto result = this.mapper.map(source,
                GetMbusEncryptionKeyStatusRequestDto.class);

        assertNotNull(MAPPED_OBJECT_VALUE_MESSAGE, result);
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, source.getMbusDeviceIdentification(),
                result.getMbusDeviceIdentification());
        assertEquals(MAPPED_FIELD_VALUE_MESSAGE, source.getChannel(), result.getChannel());
    }

    private GetMbusEncryptionKeyStatusRequestData makeRequest() {
        final GetMbusEncryptionKeyStatusRequestData request = new GetMbusEncryptionKeyStatusRequestData(
                MBUS_DEVICE_IDENTIFICATION);
        request.setChannel(CHANNEL);
        return request;
    }
}
