package International_Trade_Union.utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class UtilsTime {
    public static long getUniversalTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
    }


    // Возвращает временную метку в текстовом формате UTC
    String getUniversalTimestampString() {
        return ZonedDateTime.now(ZoneOffset.UTC).toString();
    }
}
