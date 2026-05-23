package com.myptai.global.time;

import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class CurrentDateProvider {

    private final Clock clock;

    public CurrentDateProvider(Clock clock) {
        this.clock = clock;
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }
}
