package io.fireflyest.relatelock;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.apache.logging.log4j.util.Base64Util;
import org.bukkit.Location;
import org.bukkit.util.StringUtil;
import org.junit.Test;

import io.fireflyest.relatelock.util.StringUtils;
import io.fireflyest.relatelock.util.YamlUtils;

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
        String serialize = YamlUtils.serialize(location);
        System.out.println(serialize);
        String encodeToString = Base64.getEncoder().encodeToString(serialize.getBytes());
        System.out.println(encodeToString);
        String string = new String(Base64.getDecoder().decode(encodeToString));
        System.out.println(string);

        System.out.println(StringUtils.toJson(Integer.valueOf(10)));
        System.out.println(StringUtils.toJson(10));
    }

}
