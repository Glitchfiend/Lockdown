package adubbz.lockdown.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LDLogger 
{
    private static Logger ldLogger = LogManager.getLogger("Lockdown");

    public static void log(Level level, String format, Object... data)
    {
        ldLogger.log(level, format, data);
    }
}
