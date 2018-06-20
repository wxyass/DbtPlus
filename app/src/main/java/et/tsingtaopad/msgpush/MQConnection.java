package et.tsingtaopad.msgpush;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttSimpleCallback;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.MainActivity;
import et.tsingtaopad.R;
import et.tsingtaopad.syssetting.notice.notification.NotificationNoticeActivity;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PropertiesUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MQConnection.java</br>
 * 作者：wangjian   </br>
 * 创建时间：2013-12-20</br>      
 * 功能描述: 消息接收</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("NewApi")
public class MQConnection extends Thread{
	
	private final String TAG = "MQConnection";

	//连接mq服务器url
	private String mq_url = "";
	
	//MQ客户端唯一连接编号
	private String clientId = "";
	
	//需要订阅的主题数组
	private String[] topics;
	
	private Context context ;
	
    public MQConnection(Context context,String clientId,String[] topics) {
    	this.mq_url = PropertiesUtil.getProperties("mq_url");
    	this.clientId = clientId;
    	this.topics = topics;
    	this.context = context;
    }
    
    @Override
    public void run() {
    	this.doConnection(this.clientId, this.mq_url, this.topics);
    }
    
    /**
	 * 连接mq服务器方法
	 * @param clientId
	 * @param url
	 * @param topics
	 */
	private void doConnection(String clientId,String url,String[] topics) {
		try{
			
        	//客户端非持久化消息
        	MqttPersistence MQTT_PERSISTENCE = null;
        	
        	//持久化订阅标示
    		boolean MQTT_CLEAN_START = false;
    		
    		//低耗网络,又需要及时获取数据，心跳30s
    		short MQTT_KEEP_ALIVE = 30;
    		
    		//接收消息级别
    		int[] MQTT_QUALITIES_OF_SERVICE =new int[topics.length];
    		
    		for(int i = 0;i<topics.length;i++){
    			MQTT_QUALITIES_OF_SERVICE[i]=0;
    		}
    		
    		//创建客户端
    		IMqttClient mqttClient = MqttClient.createMqttClient(url, MQTT_PERSISTENCE);
//    		Log.d("tag","client-->"+mqttClient+"]]"+clientId+"url"+url);
    		//建立连接
    		mqttClient.connect(clientId, MQTT_CLEAN_START, MQTT_KEEP_ALIVE);
    		
    		//创建回调函数类（内部类），处理接收到服务端的消息
    		WSMQTTClientCallBack callBack = new WSMQTTClientCallBack();
    		
    		//注册回调函数
    		mqttClient.registerSimpleHandler(callBack);
    		
    		//开始订阅
			mqttClient.subscribe(topics, MQTT_QUALITIES_OF_SERVICE);
			
			//初始化连接对象存入系统常量
			ConstValues.mqttClient = mqttClient;
			
			Log.e(TAG, "MQ成功");
    	} catch (Exception e){
    		Log.e(TAG, "MQ持久化订阅失败", e);
    	}
	}
    
	/**
	 * 内部类
	 * 项目名称：营销移动智能工作平台 </br>
	 * 文件名：MQConnection.java</br>
	 * 作者：wangjian   </br>
	 * 创建时间：2013-12-20</br>      
	 * 功能描述: 简单回调函数，处理client接收到的主题消息</br>
	 * 版本 V 1.0</br>               
	 * 修改履历</br>
	 * 日期      原因  BUG号    修改人 修改版本</br>
	 */
	private class WSMQTTClientCallBack implements MqttSimpleCallback{
		
		/**
	     * 客户端订阅消息后，该方法负责回调接收处理消息
	     */
		@Override
		public void publishArrived(String topicName, byte[] payload, int Qos,
				boolean retained) throws Exception {
		    PushMessageEntity messageEntity= parse(new String(payload,"UTF-8"));
		    notification(messageEntity.getMessageTitle(),messageEntity.getMessageType());
		    new PushMessageInsertDb(context).insertDb(messageEntity);
		}
		
		@Override
		public void connectionLost() throws Exception {
			// TODO Auto-generated method stub
			
		}
	}
	
	/**
	 * 显示通知栏
	 * @param title
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void notification(String title,int type){
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);				
		Intent i = null;
		if(type == 0){//通知公告
			//i = new Intent(context, NotificationNoticeActivity.class);
		}else{
			//i = new Intent(context, MainActivity.class);
		}
		i = new Intent(context, MainActivity.class);
//		Intent i = new Intent(context, NotificationNoticeActivity.class);
		i.setAction("com.dbt.notification");
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);			
		//PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 
				R.string.app_name, 
				i, 
				PendingIntent.FLAG_UPDATE_CURRENT);

		/*Notification n = null;
		n = new Notification(R.drawable.ic_launcher, title,
				System.currentTimeMillis());
		n.setLatestEventInfo(context, title, "", contentIntent);
		n.flags = Notification.FLAG_AUTO_CANCEL;
		nm.notify(R.string.app_name, n);*/


		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker("")// 新消息
				.setContentTitle(title)// 通知标题
				.setContentText("")// 主内容区
				.setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
				.setContentIntent(contentIntent)
				.setContentInfo("");// 补充信息
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(R.string.app_name, builder.build());
	}

	public PushMessageEntity parse(String json){
		PushMessageEntity pushMessage = new PushMessageEntity();
		try {
			JSONObject object = new JSONObject(json);
			pushMessage.setMessageTitle(object.getString("MESSTITLE"));
			pushMessage.setMessageContent(object.getString("MESSAGEDESC"));
			pushMessage.setMessageType(object.getInt("MESSAGE_TYPE"));
			pushMessage.setMessageState(object.getInt("MESSAGE_STATE"));//审核状态
			pushMessage.setStartDate(object.getString("startdate"));
			pushMessage.setEndDate(FunUtil.isBlankOrNullTo(object.getString("enddate"), ""));
			if (pushMessage.getMessageType() == 0) {
    			if (!CheckUtil.isBlankOrNull(object.getString("credate"))) {
    			    pushMessage.setCreDate(new Date(object.getLong("credate")));
    			}
    			pushMessage.setCreUser(object.getString("username"));
			}
			String array = object.getString("MESSAGE_MAINKEY");
			pushMessage.setMessageMainKey(java.util.Arrays.asList(array.split(",")));
		} catch (JSONException e) {
		    Log.e(TAG, e.getMessage(), e);
		}
		return pushMessage;
	}
	
}
