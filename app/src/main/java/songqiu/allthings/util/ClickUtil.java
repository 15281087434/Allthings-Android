package songqiu.allthings.util;

import android.os.SystemClock;
import android.util.Log;

public class ClickUtil {
    private static final String TAG = "ClickUtil";

    private static final long minimumClickInterval = 500;

    private static long lastClickTimestamp = 0;

    /**
     * @return false if is clicking too fast and the on click task should not be
     * performed
     */
    public static boolean onClick() {
        long current = SystemClock.elapsedRealtime();
        long interval = current - lastClickTimestamp;
        lastClickTimestamp = current;
        if (interval < minimumClickInterval) {
            return false;
        }
        return true;
    }
}
