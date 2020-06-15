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
package me.legrange.bridge;

import me.legrange.bridge.config.Modbus;
import me.legrange.bridge.config.Slave;
import me.legrange.config.Configuration;
import me.legrange.services.logging.LoggingConfig;
import me.legrange.services.rabbitmq.RabbitMqConfig;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Configuration generated from YAML config file.
 *
 * @author gideon
 */
public class PowerMeterConfig extends Configuration {

    @NotNull(message = "RabbitMQ must be configured")
    private RabbitMqConfig rabbitMq;
    @NotNull(message = "Logging must be configured")
    private LoggingConfig logging;
    @NotNull(message = "Modbus must be configured")
    private Modbus modbus;
    @NotEmpty(message = "At least one Modbus slave must be configured")
    private List<Slave> slaves;
    @NotBlank(message = "The RabbitMQ exchange for transmitting results must be configured")
    private String resultsExchange;

    /**
     * Get the value of modbus
     *
     * @return the value of modbus
     */
    public Modbus getModbus() {
        return modbus;
    }

    /**
     * Set the value of modbus
     *
     * @param modbus new value of modbus
     */
    public void setModbus(Modbus modbus) {
        this.modbus = modbus;
    }

    public List<Slave> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<Slave> slaves) {
        this.slaves = slaves;
    }

    public RabbitMqConfig getRabbitMq() {
        return rabbitMq;
    }

    public void setRabbitMq(RabbitMqConfig rabbitMq) {
        this.rabbitMq = rabbitMq;
    }

    public String getResultsExchange() {
        return resultsExchange;
    }

    public void setResultsExchange(String resultsExchange) {
        this.resultsExchange = resultsExchange;
    }

    public LoggingConfig getLogging() {
        return logging;
    }

    public void setLogging(LoggingConfig logging) {
        this.logging = logging;
    }
}
