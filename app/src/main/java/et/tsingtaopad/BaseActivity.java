package et.tsingtaopad;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import et.tsingtaopad.login.LockScreenActivity;


 /**
  * 
  * 项目名称：营销移动智能工作平台 </br>
  * 文件名：BaseActivity.java</br>
  * 作者：xuejingfei   </br>
  * 创建时间：2014-9-3</br>      
  * 功能描述: </br>      
  * 版本 V 1.0</br>               
  * 修改履历</br>
  * 日期      原因  BUG号    修改人 修改版本</br>
  */
public class BaseActivity extends FragmentActivity {
	private final static String TAG = "BaseActivity";
	protected static final int LOCKMESSAGEID = 13;
	private long lockTaskDelay = 120 * 60 * 1000;//锁屏时间
	//private long lockTaskDelay =1 * 60 * 1000;//锁屏时间
	protected static Timer lockTimer = new Timer();
	protected static LockTask lockTask;
	public static boolean isTimeToLock = false;
	private MyApplication application;
	final class LockTask extends TimerTask { 

		@Override
		public void run() {
			Log.i(TAG, "time down show dialog lock");
			handler.sendEmptyMessage(LOCKMESSAGEID);
		
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOCKMESSAGEID:
				isTimeToLock = true;
				break;

			default:
				break;
			}
		}

	};

	private HomeWatcher mHomeWatcher; 
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ExitAppUtils.getInstance().createActivity(this);
		lockTask = new LockTask();
	
	}

	@Override
	protected void onResume() {
		super.onResume();
		unLockScreen();
		
		// 按home键监听
		/*mHomeWatcher = new HomeWatcher(this);  
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {  
            @Override  
            public void onHomePressed() {  
                Log.e(TAG, "onHomePressed"); 
                handler.sendEmptyMessage(LOCKMESSAGEID);
            }  
  
            @Override  
            public void onHomeLongPressed() {  
                Log.e(TAG, "onHomeLongPressed");  
            }  
        });  
        mHomeWatcher.startWatch();*/
	}

	@Override
	protected void onPause() {
		super.onPause();
		lockScreen();// 把之前的锁销毁,创建新锁
		try {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			View currentFocus = this.getCurrentFocus();
			if (currentFocus != null) {
				IBinder windowToken = currentFocus.getWindowToken();
				imm.hideSoftInputFromWindow(windowToken, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*if(mHomeWatcher!=null){
			mHomeWatcher.stopWatch();// 销毁当前页面的home监听 在onPause中停止监听，不然会报错的。
		}*/
	}

	@Override
	protected void onDestroy() {
		lockTask.cancel();
		//lockTimer.cancel();
		super.onDestroy();
		ExitAppUtils.getInstance().destroyActivity(this);
	}

	public void unLockScreen() {
		if (isTimeToLock) {
			//isTimeToLock = false;
			Intent intent = new Intent(this, LockScreenActivity.class);
			startActivity(intent);
		}
		lockTask.cancel();
		lockTimer.cancel();
		Log.i(TAG, "unlockScreen  将原任务从队列中移除");

	}

	public void lockScreen() {

		if (lockTask != null) {
			Log.i(TAG, "lockScreen 将原任务从队列中移除");
			lockTask.cancel(); //将原任务从队列中移除
		}
		Log.i(TAG, "lockScreen");
		lockTask = new LockTask();
		lockTimer = new Timer();
		lockTimer.schedule(lockTask, lockTaskDelay);
	}
	
	@Override  
    protected void onStop() {  
            // TODO Auto-generated method stub  
            super.onStop();  

            /*if (!isAppOnForeground()) {  
                    //app 进入后台  
            	handler.sendEmptyMessage(LOCKMESSAGEID);
            } */ 
    }
	
	/** 
     * 程序是否在前台运行 
     *  
     * @return 
     */  
    public boolean isAppOnForeground() {  
            // Returns a list of application processes that are running on the  
            // device  
               
            ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);  
            String packageName = getApplicationContext().getPackageName();  

            // 正在运行的程序
            List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();  
            if (appProcesses == null)  
                    return false;  

            for (RunningAppProcessInfo appProcess : appProcesses) {  
            	// com.android.gallery3d
            		
            	// 相机
                /*Intent infoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
                String res = getPackageManager().resolveActivity(infoIntent, 0).activityInfo.packageName;
                if (appProcess.processName.equals(res)) {
                	// com.thundersoft.ucam
                	return true;  
                } */
                // The name of the process that this object is associated with.  
                if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {  
                        return true;  
                } 
            }  

            return false;  
    }
    
    /**
     * 判断程序是否在前台运行
     * 
     * @param packageName
     * @return
     */
	public boolean isOpen(String packageName) {
		if (packageName.equals("") | packageName == null)
			return false;
		ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runinfo : runningAppProcesses) {
			String pn = runinfo.processName;
			if (pn.equals(packageName) && runinfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
				return true;
		}
		return false;
	}
	
}
