package io.fireflyest.relatelock;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;
import org.junit.Test;
import io.fireflyest.relatelock.util.TextUtils;

/**
 * 临时测试
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class TempTest {
    
    @Test
    public void matchTest() {
        assertTrue(TextUtils.match(Pattern.compile("^[\\w]+$"), "playerName"));
        assertTrue(TextUtils.match(Pattern.compile("^[\\w]+$"), "player_name"));
        assertTrue(TextUtils.match(Pattern.compile("^[\\w]+$"), "player1"));
    }

}
