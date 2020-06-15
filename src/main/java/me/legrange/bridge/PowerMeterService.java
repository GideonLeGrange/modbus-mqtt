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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.legrange.bridge.config.Register;
import me.legrange.bridge.config.Slave;
import me.legrange.modbus.*;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.service.ServiceException;
import me.legrange.services.logging.WithLogging;
import me.legrange.services.rabbitmq.WithRabbitMq;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public class PowerMeterService extends Service<PowerMeterConfig> implements WithLogging, WithRabbitMq {
    private boolean running;
    private SerialModbusPort port;
    private Gson gson;

    public static void main(String... args)  {
        Service.main(args);
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    /**
     * Start the service.
     *
     * @throws ServiceException
     */
    public void start() throws ServiceException {
        gson = new GsonBuilder().setPrettyPrinting().create();
        running = true;
        try {
            startModbus();
        } catch (ModbusReaderException | SerialException e) {
            throw new ServiceException(e.getMessage(), e);
        }
        info("service started");
    }

    private void publish(String slave, RegistersResult result) {
        List<Reading> readings = new ArrayList();
        for (ModbusRegister register : result.getRegisters()) {
            byte data[] = result.getData(register);
            double value = ModbusRegister.decode(register, data);
            readings.add(new Reading(slave, register.getName(), value));
        }
        try {
            rabbitMq().getChannel().basicPublish(getConfig().getResultsExchange(), "", null, toJsonBytes(readings));
        } catch (IOException | ComponentException | PowerMeterException e) {
            error(e);
        }
    }

    private byte[] toJsonBytes(Object data) throws PowerMeterException {
        try {
            return gson.toJson(data).getBytes(Charset.forName("utf8"));
        }
        catch (Exception ex) {
            throw new PowerMeterException(ex.getMessage(), ex);
        }
    }

    private void startModbus() throws ModbusReaderException, SerialException {
        port = SerialModbusPort.open(getConfig().getModbus().getSerial().getPort(),
                getConfig().getModbus().getSerial().getSpeed());
        for (Slave slave : getConfig().getSlaves()) {
            ModbusReader mbus = new ModbusReader(port, slave.getName(),
                    slave.getDeviceId(),
                    slave.isZeroBased());
            mbus.addListener(new ModbusListener() {

                @Override
                public void received(RegistersResult result) {
                    publish(slave.getName(), result);
                }

                @Override
                public void error(Throwable e) {
                    PowerMeterService.this.error(e, e.getMessage());
                }
            });
            for (Register reg : slave.getRegisters()) {
                mbus.addRegister(makeRegister(reg));
                debug("reg: " + reg.getName());
            }
            mbus.setPollInterval(slave.getPollInterval());
            mbus.start();
        }

    }

    private void stopModbus() throws SerialException {
        port.close();
    }

    private ModbusRegister makeRegister(final Register reg) {
        return new ModbusRegister() {
            @Override
            public String getName() {
                return reg.getName();
            }

            @Override
            public int getAddress() {
                return reg.getAddress();
            }

            @Override
            public int getLength() {
                return reg.getLength();
            }

            @Override
            public ModbusRegister.Type getType() {
                switch (reg.getType()) {
                    case "float":
                        return Type.FLOAT;
                    case "int":
                        return Type.INT;
                    default:
                        throw new RuntimeException("Unknown register type '" + reg.getType() + "'");
                }
            }
        };
    }


}
