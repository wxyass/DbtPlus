package et.tsingtaopad.watchdog;

import android.os.Looper;

import et.tsingtaopad.tools.FileUtil;

/**
 * Created by dapen on 2017/11/8.
 */

public class ANRException extends RuntimeException {
    public ANRException() {
        super("应用程序无响应，快来改BUG啊！！");
        Thread mainThread = Looper.getMainLooper().getThread();
        setStackTrace(mainThread.getStackTrace());
        // FileUtil.writeTxt(, FileUtil.getSDPath()+"/setStackTrace.txt");
    }
}
