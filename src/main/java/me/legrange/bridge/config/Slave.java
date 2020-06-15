/*
 * Copyright 2016 gideon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.legrange.bridge.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 *
 * @author gideon
 */
public class Slave {

    @NotBlank(message = "Slave name must be supplied")
    private String name;
    @Min(message = "Device ID must be between 1 and 255", value = 1)
    @Max(message = "Device ID must be between 1 and 255", value = 255)
    private int deviceId = 1;
    @Min(message = "Poll interval must be bigger than 0", value = 1)
    private int pollInterval = 60;
    private boolean zeroBased = false;
    @NotEmpty(message = "At least one register needs to be defined")
    private List<Register> registers; 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getPollInterval() {
        return pollInterval;
    }

    public void setPollInterval(int pollInterval) {
        this.pollInterval = pollInterval;
    }

    public boolean isZeroBased() {
        return zeroBased;
    }

    public void setZeroBased(boolean zeroBased) {
        this.zeroBased = zeroBased;
    }

    public List<Register> getRegisters() {
        return registers;
    }

    public void setRegisters(List<Register> registers) {
        this.registers = registers;
    }


}
