/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SetConfigurationObjectRequestData implements Serializable {

    private static final long serialVersionUID = -381163520662276868L;

    private ConfigurationObject configurationObject;

    public ConfigurationObject getConfigurationObject() {
        return configurationObject;
    }

    public void setConfigurationObject(ConfigurationObject configurationObject) {
        this.configurationObject = configurationObject;
    }
}