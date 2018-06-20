package et.tsingtaopad.visit.syncdata;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.msgpush.MQConnection;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.PrefUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 *项目名称：营销移动智能工作平台 </br>
 *文件名:MainService 
 *作者：@王士铭  </br>
 *创建时间： 2013-12-5 上午9:26:50</br> 
 *功能描述: 监听网络变化 并上传数据</br>
 *版本 V 1.0</br> 
 *修改履历</br>
 *日期    2013-12-5 上午9:26:50  原因  BUG号    修改人@王士铭 修改版本</br>
 */
public class MainService extends Service {

	private MainServiceReceiver mMainServiceReceiver;
	private final String TAG = "MainService";
	// public static final String EXIT_UPDATE =
	// "com.dbt.tsingtao.exit_update";// 退出更新广播acition
	// public static final String EXIT_APP = "com.dbt.tsingtao.exit";// 退出广播
	private int firstLogin = 0;
	private long timerTaskDelay = 1000 * 60 * 60;// 定时任务执行间隔时间 1小时
	private Timer timer;
	private UploadDataService upload;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		firstLogin = 0;
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mMainServiceReceiver = new MainServiceReceiver();// 网络改变监听
		registerReceiver(mMainServiceReceiver, filter);// 注册监听
		timer = new Timer();
		upload = new UploadDataService(getApplicationContext(), null);
		UploadTimerTask task = new UploadTimerTask();
		timer.schedule(task, timerTaskDelay, timerTaskDelay);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mMainServiceReceiver);
		if (timer != null) {
			timer.cancel();
		}
	}

	class UploadTimerTask extends TimerTask {

		@Override
		public void run() {

			try {
				Log.i(TAG, "UploadTimerTask upload");
				if (NetStatusUtil.isNetValid(getApplicationContext())) {
					if (!upload.isAllEmpty()) {
						DbtLog.logUtils(TAG, "上传数据-UploadTimerTask-定时");
						upload.uploadTables(false);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void initMQ() throws Exception {

		/********* 持久化订阅mq***start *********/
		// 需要订阅的主题数组（以用户ID和所属区域ID为主题名）
		// String[] topics = { ConstValues.loginSession.getUserCode(),
		// ConstValues.loginSession.getParentDisIDs().substring(1,
		// ConstValues.loginSession.getParentDisIDs().length()-1),
		// // ConstValues.loginSession.getDisId() + "_public",
		// ConstValues.loginSession.getDisId() + "_private",
		// ConstValues.loginSession.getDisId() + "_public" };

		// 唯一连接编号，这里用用户编号在loginsession中取得
		//String clientId = ConstValues.loginSession.getUserGongHao();
		String clientId = PrefUtils.getString(getApplicationContext(), "userGongHao", "");
		// 启动子线程调用连接方法
		new MQConnection(this, clientId, getTopics()).start();
		/********* 持久化订阅mq***end *********/

	}

	private String[] getTopics() throws Exception {
		String[] result = new String[3];
		String[] temp = new String[0];
		//if (!CheckUtil.isBlankOrNull(ConstValues.loginSession.getParentDisIDs())) {
			if (!CheckUtil.isBlankOrNull(PrefUtils.getString(getApplicationContext(), "pDiscs", ""))) {
			//temp = ConstValues.loginSession.getParentDisIDs().split(",");
			temp = PrefUtils.getString(getApplicationContext(), "pDiscs", "").split(",");
			result = new String[temp.length + 3];
			for (int i = 0; i < temp.length; i++) {
				result[i] = temp[i].substring(1, temp[i].length() - 1);
			}
		}
		
		
		//result[temp.length] = ConstValues.loginSession.getUserCode();
		result[temp.length] = PrefUtils.getString(getApplicationContext(), "userCode", "");
		//result[temp.length + 1] = ConstValues.loginSession.getDisId() + "_private";
		result[temp.length + 1] = PrefUtils.getString(getApplicationContext(), "disId", "") + "_private";
		//result[temp.length + 2] = ConstValues.loginSession.getDisId() + "_public";
		result[temp.length + 2] = PrefUtils.getString(getApplicationContext(), "disId", "") + "_public";
		return result;
	}

	class MainServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			ConnectivityManager connctMananger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

				NetworkInfo activeNetworkInfo = connctMananger.getActiveNetworkInfo();

				if (null != activeNetworkInfo && activeNetworkInfo.getState() == State.CONNECTED) {

					try {
						initMQ();
						firstLogin++;
						if (!upload.isAllEmpty() && firstLogin > 1) {
							Log.i(TAG, "net work upload");
							DbtLog.logUtils(TAG, "上传数据-onReceive-监听网络变化");
							upload.uploadTables(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
	}

}
