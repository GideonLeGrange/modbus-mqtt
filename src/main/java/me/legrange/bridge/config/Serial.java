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

import javax.validation.constraints.NotNull;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public class Serial {
    @NotNull(message = "The serial port must be defined")
    private String port;
    private int speed = 9600;

    /**
     * Get the value of speed
     *
     * @return the value of speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Set the value of speed
     *
     * @param speed new value of speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }


    /**
     * Get the value of port
     *
     * @return the value of port
     */
    public String getPort() {
        return port;
    }

    /**
     * Set the value of port
     *
     * @param port new value of port
     */
    public void setPort(String port) {
        this.port = port;
    }

}
