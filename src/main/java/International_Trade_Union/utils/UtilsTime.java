package International_Trade_Union.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class UtilsTime {
    public static long getUniversalTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() * 1000L;
    }

    public static String timeToString(long secs) {
        long hour = secs / 3600;
        long min = secs / 60 % 60;
        long sec = secs % 60;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }
    // Возвращает временную метку в текстовом формате UTC
    public static String  getUniversalTimestampString() {
        return ZonedDateTime.now(ZoneOffset.UTC).toString();
    }

    public static long differentMillSecondTime(long first, long second){
        return second - first;
    }
}
