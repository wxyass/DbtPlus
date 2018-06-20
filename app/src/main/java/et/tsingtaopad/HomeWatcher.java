package et.tsingtaopad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：HomeWatcher.java</br>
 * 作者：yangwenmin   </br>
 * 创建时间：2016-7-8</br>      
 * 功能描述: Home键监听封装</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class HomeWatcher {  
  
    static final String TAG = "HomeWatcher";  
    private Context mContext;  
    private IntentFilter mFilter;  
    private OnHomePressedListener mListener;  
    private InnerRecevier mRecevier;  
  
    // 回调接口  
    public interface OnHomePressedListener {  
        public void onHomePressed();  
  
        public void onHomeLongPressed();  
    }  
  
    public HomeWatcher(Context context) {  
        mContext = context;  
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);  
    }  
  
    /** 
     * 设置监听 
     *  
     * @param listener 
     */  
    public void setOnHomePressedListener(OnHomePressedListener listener) {  
        mListener = listener;  
        mRecevier = new InnerRecevier();  
    }  
  
    /** 
     * 开始监听，注册广播 
     */  
    public void startWatch() {  
        if (mRecevier != null) {  
            mContext.registerReceiver(mRecevier, mFilter);  
        }  
    }  
  
    /** 
     * 停止监听，注销广播 
     */  
    public void stopWatch() {  
        if (mRecevier != null) {  
            mContext.unregisterReceiver(mRecevier);  
        }  
    }  
  
    /** 
     * 广播接收者 
     */  
    class InnerRecevier extends BroadcastReceiver {  
        final String SYSTEM_DIALOG_REASON_KEY = "reason";  
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";  
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";  
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {  
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);  
                if (reason != null) {  
                    Log.e(TAG, "action:" + action + ",reason:" + reason);  
                    if (mListener != null) {  
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {  
                            // 短按home键  
                            mListener.onHomePressed();  
                        } else if (reason  
                                .equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {  
                            // 长按home键  
                            mListener.onHomeLongPressed();  
                        }  
                    }  
                }  
            }  
        }  
    }  
}