package et.tsingtaopad;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ibm.mqtt.MqttPersistenceException;


/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MyApplication.java</br>
 * 作者：dbt   </br>
 * 创建时间：2013-12-25</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class MyApplication extends Application
{
    private static MyApplication instance;
    private static Context mContext;

    public static MyApplication getInstance()
    {
        return instance;
    }

    //activity管理list
    ArrayList<Activity> activityList = new ArrayList<Activity>();




    @Override
    public void onCreate()
    {
        // new ANRWatchDog().start();
        super.onCreate();

        // LeakCanary.install(this);
        // 在主进程初始化调用哈
        //BlockCanary.install(this, new AppBlockCanaryContext()).start();

        instance = this;
        mContext = getApplicationContext();
        //设置该CrashHandler为程序的默认处理器    
        UnCeHandler catchExcep = new UnCeHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
    }

    // 创建服务用于捕获崩溃异常  
    @Override
    public void onTerminate()
    {
        super.onTerminate();
        try
        {
            //在常量中取得登陆成功以后存储的MQ连接，断开
            if (ConstValues.mqttClient != null)
            {
                ConstValues.mqttClient.disconnect();
            }
        }
        catch (MqttPersistenceException e)
        {
            Log.e("MQConnection", "MQ断开连接失败或者已经断开", e);
        }
    }
    
    public static Context getContext(){ 
        
    	  return mContext;     
    	 }
}
