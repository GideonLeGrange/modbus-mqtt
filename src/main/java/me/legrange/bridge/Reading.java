package me.legrange.bridge;


import java.time.Instant;

public final class Reading {

    private final Instant time;
    private final String slave;
    private final String register;
    private final double value;

    Reading(String slave, String register, double value) {
        time = Instant.now();
        this.slave = slave;
        this.register = register;
        this.value = value;
    }
}
