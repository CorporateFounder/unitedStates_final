package International_Trade_Union.model;

import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsFileSaveRead;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MyLogger {
    private static final Logger logger = LogManager.getLogger(MyLogger.class);

    public static void saveLog(String log, Throwable throwable){
//        logger.error(log, throwable);
        UtilsFileSaveRead.save(log + "\n" + throwable.toString() + "\n", Seting.ERROR_FILE, true);
    }

    public static void saveLog(String log){
        logger.error(log);
        UtilsFileSaveRead.save(log  + "\n", Seting.ERROR_FILE, true);

    }
}
