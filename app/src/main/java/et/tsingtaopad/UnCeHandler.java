package et.tsingtaopad;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.LogUtils;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：UnCeHandler.java</br>
 * 作者：薛敬飞   </br>
 * 创建时间：2014-8-11</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class UnCeHandler implements UncaughtExceptionHandler
{
    private UncaughtExceptionHandler mDefaultHandler;
    public static final String TAG = "CatchExcep";
    MyApplication application;

    public UnCeHandler(MyApplication application)
    {
        //获取系统默认的UncaughtException处理器    
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.application = application;
    }

    public void uncaughtException(Thread thread, Throwable ex)
    {
        Log.e("ex", ex + "");
        if (!handleException(ex) && mDefaultHandler != null)
        {
            //如果用户没有处理则让系统默认的异常处理器来处理    
            mDefaultHandler.uncaughtException(thread, ex);
        }
        else
        {
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, "error : ", e);
            }
            //关闭所有的activtys并结束进程
            ExitAppUtils.getInstance().exit();// 自定义方法，关闭当前打开的所有avtivity
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**  
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.  
     *   
     * @param ex  
     * @return true:如果处理了该异常信息;否则返回false.  
     */
    private boolean handleException(Throwable ex)
    {
        if (ex == null)
        {
            return false;
        }
        //使用Toast来显示异常信息    
        new Thread()
        {
            @Override
            public void run()
            {
                Looper.prepare();
                Toast.makeText(application.getApplicationContext(), "很抱歉,程序出现异常,请重新登陆", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        saveCrashInfoToFile(ex);
        return true;
    }

    /**记录崩溃日志
     * 保存错误信息到文件中
     * @param ex
     * @return    返回文件名称,便于将文件传送到服务器
    */
    private void saveCrashInfoToFile(Throwable ex)
    {
        if (!ConstValues.isOnline)
            return;
        try
        {
            //用于格式化日期,作为日志文件名的一部分
            DateFormat formatter = new SimpleDateFormat("yyyy_MM_dd-HH.mm.ss");
            String sdcardPath = Environment.getExternalStorageDirectory() + "";
            String DbtPATH = sdcardPath + "/dbt/et.tsingtaopad";
            String BUGPATH = DbtPATH + "/bug/";
            StringBuffer sb = new StringBuffer();
            sb.append("\n手机型号:" + android.os.Build.MODEL);
            sb.append("\nSDK版本:" + android.os.Build.VERSION.SDK);
            sb.append("\n系统版本:" + android.os.Build.VERSION.RELEASE);
            //String banben = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            sb.append("\n崩溃时间:" + DateUtil.getDateTimeStr(1)+"\n");
            sb.append("\n软件版本:" + DbtLog.getVersion()+"\n");
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null)
            {
                Log.i(TAG, "cause:" + cause.toString() + "--");
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            Log.i(TAG, "result:" + result);
            sb.append(result);
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + ".log";
            //            if (ConstantUtils.isOnline)
            //            {
            //                fileName = "crash-online.log";
            //            }
            //            FileOutputStream fos = new FileOutputStream(BUGPATH + fileName);
            //            fos.write(sb.toString().getBytes());
            //            fos.close();
            LogUtils.getInstances().write(BUGPATH, fileName, sb.toString());
        }
        catch (Exception e)
        {
            Log.e(TAG, "an error occured while writing file...", e);
        }
    }
    
}
