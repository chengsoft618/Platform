/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

public class GetConfigurationResponseContainer extends ActionResponse {

    private static final long serialVersionUID = 8942300410552414718L;

    private String configurationData;

    public GetConfigurationResponseContainer(final String configurationData) {
        this.configurationData = configurationData;
    }

    public String getConfigurationData() {
        return this.configurationData;
    }
}