package utils;

import java.util.TimerTask;

public class ScheduledTask extends TimerTask {

    public final static long MINUTE = 1000 * 60; // 1 minute
    public final static long HOUR = MINUTE * 60; // 1 hour

    public void run() {
        ExcelStreamReader.getInstance().updateData();
    }
}
