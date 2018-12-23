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
package me.legrange.bridge.mqtt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import me.legrange.bridge.ModbusMqttService;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public class MqttConnector implements Runnable, MqttCallback {

    public MqttConnector(String broker, ModbusMqttService service) {
        this(broker, null, null, service);
    }

    public MqttConnector(String broker, String username, String password, ModbusMqttService service) {
        this.broker = broker;
        this.service = service;
        this.username = username;
        this.password = password;
    }

    public void addListener(String topic, MqttListener listener) {
        List<MqttListener> forTopic = listeners.get(topic);
        if (forTopic == null) {
            forTopic = new LinkedList<>();
            listeners.put(topic, forTopic);
        }
        forTopic.add(listener);
    }

    @Override
    public void run() {
        try {
            mqtt = new MqttClient(broker, service.getName(), new MemoryPersistence());
            MqttConnectOptions opts = new MqttConnectOptions();
            if (username != null) {
                opts.setUserName(username);
            }
            if (password != null) {
                opts.setPassword(password.toCharArray());
            }
            opts.setCleanSession(true);
            mqtt.connect(opts);
            mqtt.setCallback(this);
        } catch (MqttException ex) {
            ModbusMqttService.error(ex.getMessage(), ex);
        }

    }

    @Override
    public void connectionLost(Throwable e) {
        ModbusMqttService.warn("MQTT connection lost [%s]", e.getMessage());
        long time = 2500;
        while (!mqtt.isConnected()) {
            try {
                TimeUnit.MILLISECONDS.sleep(time);
                ModbusMqttService.info("MQTT re-connecting");
                mqtt.connect();
                ModbusMqttService.info("MQTT re-connected");
            } catch (InterruptedException ex) {
                ModbusMqttService.error("MQTT interruption error: " + ex.getMessage(), ex);
            } catch (MqttException ex) {
                ModbusMqttService.error("MQTT reconnection error: " + ex.getMessage(), ex);
            }
        }
    }

    public void start() {
        Thread t = new Thread(this, "MQTT updater");
        t.setDaemon(true);
        t.start();
    }

    public void stop() {
        if (mqtt.isConnected()) {
            try {
                mqtt.disconnect();
            } catch (MqttException ex) {
                ModbusMqttService.error(ex.getMessage(), ex);
            } finally {
                ModbusMqttService.info("MQTT disconnected");
            }
        }
    }

    public void publish(String topic, String msg) {
        try {
            mqtt.publish(topic, new MqttMessage(msg.getBytes()));
        } catch (MqttException ex) {
            ModbusMqttService.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        service.submit(() -> {
            List<MqttListener> forTopic = listeners.get(topic);
            if (forTopic != null) {
                forTopic.stream().forEach((l) -> {
                    l.received(topic, mm.toString());
                });
            }
        });
    }

    private MqttClient mqtt;
    private final String broker;
    private final String username;
    private final String password;  
    private final ModbusMqttService service;
    private final Map<String, List<MqttListener>> listeners = new HashMap<>();

}
