package io.fireflyest.relatelock;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

public class RelateLockTest {


    @Test
    public void testTest() {
        Instant start = Instant.now();
        RelateLock.test();
        long ms = Duration.between(start, Instant.now()).toMillis();
        System.out.println("ms=" + ms);
    }

    @Test
    public void enmuTest() {
        Print.RELATE_LOCK.info("null");
    }

}
