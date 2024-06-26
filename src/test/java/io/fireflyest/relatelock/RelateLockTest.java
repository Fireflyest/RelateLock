package io.fireflyest.relatelock;

import java.time.Duration;
import java.time.Instant;

import org.bukkit.Location;
import org.junit.Test;

import io.fireflyest.relatelock.core.LocksmithImpl;
import io.fireflyest.relatelock.util.SerializationUtils;

public class RelateLockTest {


    @Test
    public void testTest() {
        Instant start = Instant.now();
        // RelateLock.test();
        String string = String.format("%s,%d,%d,%d", "world", 1, 2, 3);
        // String string = "world" + ',' + 1 + ',' + 2 + ',' + 3;
        long ms = Duration.between(start, Instant.now()).toMillis();
        System.out.println("ms=" + ms);
    }

    @Test
    public void enmuTest() {
        Location location = new Location(null, 0, 0, 0);
        LocksmithImpl lImpl = new LocksmithImpl();
        // String serialize = lImpl.locSerialMap.computeIfAbsent(location, k -> SerializationUtils.serialize(location));
        // String serialize2 = lImpl.locSerialMap.computeIfAbsent(location, k -> SerializationUtils.serialize(location));
        String serialize = SerializationUtils.serialize(location);
        Instant instant = Instant.now();
        String serialize2 = SerializationUtils.serialize(location);
        System.out.println(Duration.between(instant, instant.now()).toMillis());
        System.out.println(serialize2);

    }

}
