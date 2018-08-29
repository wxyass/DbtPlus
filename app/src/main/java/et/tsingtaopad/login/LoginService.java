package et.tsingtaopad.login;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.android.volley.VolleyError;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.MainActivity;
import et.tsingtaopad.R;
import et.tsingtaopad.VolleyListenerInterface;
import et.tsingtaopad.db.tables.MstLoginpicInfo;
import et.tsingtaopad.login.domain.BsVisitEmpolyeeStc;
import et.tsingtaopad.login.domain.LoginSession;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：LoginService.java</br>
 * 作者：@吴承磊    </br>
 * 创建时间：2013/11/24</br>      
 * 功能描述: 用户登录的业务逻辑</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class LoginService {

	private final String TAG = "LoginService";

	private Context context;
	private Handler handler;

	public LoginService(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	/**
     * 
     */
    public LoginService() {
    }

    /**
	 * 登录
	 * 
	 * @param uid   用户名
	 * @param pwd   密码
     * @param version 
	 * @return
	 */
	public void login(final String uid, final String pwd, String version) {

		int msgId = -1;

		// 获取上次登录者信息
		final LoginSession loginSession = this.getLoginSession(context);

		if (CheckUtil.isBlankOrNull(uid)) {
			msgId = R.string.login_msg_invaluid;

		} else if (CheckUtil.isBlankOrNull(pwd)) {
			msgId = R.string.login_msg_invalpwd;

		} else if (!uid.equals(loginSession.getUserGongHao()) && loginSession.getUserGongHao().length() > 0) {
			msgId = R.string.login_msg_invaluser;
		}

		// 弹出提示信息
		if (msgId != -1) {
			this.sendMsg(msgId);

		} else {

			// 获取PAD设备序列号，并拼接登录请求参数
			// final String padId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			final String padId = android.os.Build.SERIAL + "";
			StringBuffer buffer = new StringBuffer();
			buffer.append("{userid:'").append(uid);
			buffer.append("', password:'").append(pwd);
			buffer.append("', padid:'").append(padId);
			buffer.append("', version:'").append(version).append("'}");

			//String ds ="{userid:'20000', password:'a1234567', padid:'866523014316149'}";
			// 请求网络
			HttpUtil httpUtil = new HttpUtil(60 * 1000);
			httpUtil.configResponseTextCharset("ISO-8859-1");
			
			httpUtil.send("opt_get_login", buffer.toString(), new RequestCallBack<String>() {
			//httpUtil.send("opt_get_login",  ds, new RequestCallBack<String>() {

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {

					//ResponseStructBean resObj = null;
					ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
					//String resObjasd = HttpUtil.parseJsonResToString(responseInfo.result);
					
					if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
					    
					    // 保存登录者信息
					    BsVisitEmpolyeeStc emp = JsonUtil.parseJson(
					            resObj.getResBody().getContent(), BsVisitEmpolyeeStc.class);
					    
					    
					    if (emp == null) {
                            sendMsg(R.string.login_msg_usererror);//服务器回应的内容为空时界面会收到   用户信息异常，不能正常登录
					    
					    } else { 
					        // 服务器时间与pad端时间差
					        long timeDiff = Math.abs(System.currentTimeMillis()
					                - DateUtil.parse(emp.getLoginDate(), "yyyy-MM-dd HH:mm:ss").getTime());
					        
					        // 校验用户的定格是否一致
					        if (!CheckUtil.isBlankOrNull(loginSession.getGridId())
    					                && !loginSession.getGridId().equals(emp.getGridId())) {
    					        sendMsg(R.string.login_msg_invalgrid);//现在登录的定格与上次的不一样时界面收到      用户所属定格变更，请先清除上次登录账户的缓存数据    
    					        
    					    } else if (timeDiff > 5 * 60000) {
                                sendMsg(R.string.login_msg_invaldate);
    					        
    					    } else {
    					        saveLoginSession(emp, pwd, padId);
    					        ConstValues.loginSession = getLoginSession(context);
    					        
    					        // 保存用户权限到缓存
    					        PrefUtils.putString(context, "bfgl", emp.getBfgl());
    					        PrefUtils.putString(context, "yxgl", emp.getYxgl());
    					        PrefUtils.putString(context, "xtgl", emp.getXtgl());
    					        
    					        // 跳转到平台主界面
    					        //sendMsg(R.string.login_msg_online, true);
    					        sendMsg1(R.string.login_msg_online, true,emp.getIsrepassword());//Isrepassword:剩余多少天修改密码 2393版本返回null
    					    }
					    }

					} else {
						String msg = FunUtil.isNullSetSpace(resObj.getResBody().getContent());
						if (msg.contains("用户已离职或冻结")) {
							updateLoginSession("userStatus", ConstValues.FLAG_1);
						} else if (msg.contains("设备已冻结")) {
							updateLoginSession("devStatus", ConstValues.FLAG_1);
						}
						sendMsg(msg);
					}
				}

				@Override
				public void onFailure(HttpException error, String errMsg) {
					Log.e(TAG, errMsg, error);

					// 网络原因登录失败后，用离开方式登录
					ConstValues.loginSession = getLoginSession(context);

					//if (ConstValues.FLAG_1.equals(ConstValues.loginSession.getUserStatus())) {
					if (ConstValues.FLAG_1.equals(PrefUtils.getString(context, "userStatus", ""))) {
						sendMsg(R.string.login_msg_userice);//状态为1时的情况   该用户已离职或冻结！

					//} else if (ConstValues.FLAG_1.equals(ConstValues.loginSession.getDevStatus())) {
					} else if (ConstValues.FLAG_1.equals(PrefUtils.getString(context, "devStatus", ""))) {
						sendMsg(R.string.login_msg_device);// 设置冻结状态 : 0:未冻结，1:已冻结

						// 如果缓存的用户名是空，是为第一次登录
					//} else if (CheckUtil.isBlankOrNull(ConstValues.loginSession.getUserGongHao())) {
					} else if (CheckUtil.isBlankOrNull(PrefUtils.getString(context, "userCode", ""))) {
						sendMsg(R.string.msg_err_netfail);

						// 否则尝试离线登录 
					} else {

						// 判定与上次登录密码是否一至, 是则跳转平台主界面
						//if (uid.equals(ConstValues.loginSession.getUserGongHao()) && pwd.equals(ConstValues.loginSession.getUserPwd())) {
						if (uid.equals(PrefUtils.getString(context, "userGongHao", "")) && pwd.equals(PrefUtils.getString(context, "userPwd", ""))) {
							ConstValues.loginSession.setLoginDate(DateUtil.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
							//sendMsg(R.string.login_msg_offline,true);//离线登录成功
							sendMsg1(R.string.login_msg_offline,true,"11000");//离线登录成功 
							Intent intent = new Intent(context, MainActivity.class);
							context.startActivity(intent);
							((Activity) context).finish();
							return;

						} else {
							sendMsg(R.string.login_msg_pwdfail);
						}
					}
				}
			});
			
			
			/*httpUtil.postDate(context, "opt_get_login", buffer.toString(), new VolleyListenerInterface( VolleyListenerInterface.mListener, VolleyListenerInterface.mErrorListener) {

				@Override
				public void onMySuccess(String result) {
					ResponseStructBean resObj = HttpUtil.parseRes(result);
					if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
					    
					    // 保存登录者信息
					    BsVisitEmpolyeeStc emp = JsonUtil.parseJson(
					            resObj.getResBody().getContent(), BsVisitEmpolyeeStc.class);
					    
					    
					    if (emp == null) {
                            sendMsg(R.string.login_msg_usererror);//服务器回应的内容为空时界面会收到   用户信息异常，不能正常登录
					    
					    } else { 
					        // 服务器时间与pad端时间差
					        long timeDiff = Math.abs(System.currentTimeMillis()
					                - DateUtil.parse(emp.getLoginDate(), "yyyy-MM-dd HH:mm:ss").getTime());
					        
					        // 校验用户的定格是否一致
					        if (!CheckUtil.isBlankOrNull(loginSession.getGridId())
    					                && !loginSession.getGridId().equals(emp.getGridId())) {
    					        sendMsg(R.string.login_msg_invalgrid);//现在登录的定格与上次的不一样时界面收到      用户所属定格变更，请先清除上次登录账户的缓存数据    
    					        
    					    } else if (timeDiff > 5 * 60000) {
                                sendMsg(R.string.login_msg_invaldate);
    					        
    					    } else {
    					        saveLoginSession(emp, pwd, padId);
    					        ConstValues.loginSession = getLoginSession(context);
    					        
    					        // 保存用户权限到缓存
    					        PrefUtils.putString(context, "bfgl", emp.getBfgl());
    					        PrefUtils.putString(context, "yxgl", emp.getYxgl());
    					        PrefUtils.putString(context, "xtgl", emp.getXtgl());
    					        
    					        // 跳转到平台主界面
    					        //sendMsg(R.string.login_msg_online, true);
    					        sendMsg1(R.string.login_msg_online, true,emp.getIsrepassword());//Isrepassword:剩余多少天修改密码 2393版本返回null
    					    }
					    }

					} else {
						String msg = FunUtil.isNullSetSpace(resObj.getResBody().getContent());
						if (msg.contains("用户已离职或冻结")) {
							updateLoginSession("userStatus", ConstValues.FLAG_1);
						} else if (msg.contains("设备已冻结")) {
							updateLoginSession("devStatus", ConstValues.FLAG_1);
						}
						sendMsg(msg);
					}
					
				}

				@Override
				public void onMyError(VolleyError error) {
					// TODO Auto-generated method stub
					
				}});*/
		}
	}

	/**
	 * 向界面发送提示消息
	 * 
	 * @param msg   提示消息
	 */
	private void sendMsg(Object msg) {

		Bundle bundle = new Bundle();
		Message message = new Message();

		if (msg instanceof Integer) {
			bundle.putString("msg", context.getString(Integer.valueOf(msg.toString())));
		} else {
			bundle.putString("msg", String.valueOf(msg));
		}
		message.what = ConstValues.WAIT1;
		message.setData(bundle);
		handler.sendMessage(message);
	}

	private void sendMsg(Object msg, boolean isSuccess) {

		Bundle bundle = new Bundle();
		Message message = new Message();
		bundle.putBoolean("isSuccess", isSuccess);
		if (msg instanceof Integer) {
			bundle.putString("msg", context.getString(Integer.valueOf(msg.toString())));
		} else {
			bundle.putString("msg", String.valueOf(msg));
		}
		message.what = ConstValues.WAIT1;
		message.setData(bundle);
		handler.sendMessage(message);

	}
	
	/**
	 * 登录成功 向界面发送提示消息
	 * 
	 * @param msg
	 * @param isSuccess    是否跳转
	 * @param isrepassword 是否修改密码 0:不需要 1:需要
	 */
	private void sendMsg1(Object msg, boolean isSuccess,String isrepassword) {
		
		Bundle bundle = new Bundle();
		Message message = new Message();
		bundle.putBoolean("isSuccess", isSuccess);
		bundle.putString("isrepassword", isrepassword);
		if (msg instanceof Integer) {
			bundle.putString("msg", context.getString(Integer.valueOf(msg.toString())));
		} else {
			bundle.putString("msg", String.valueOf(msg));
		}
		message.what = ConstValues.WAIT1;
		message.setData(bundle);
		handler.sendMessage(message);
		
	}

	/**
	 * 记录登录者信息
	 * 
	 * @param emp 登录成功后，返回的当前用户信息
	 */
	private void saveLoginSession(BsVisitEmpolyeeStc emp, String pwd, String padId) {
		if (emp != null) {
			SharedPreferences sp = context.getSharedPreferences(ConstValues.LOGINSESSIONKEY, 0);
			sp.edit().putString("userCode", emp.getUserid()).commit();
			sp.edit().putString("padId", padId).commit();
			sp.edit().putString("userName", emp.getUsername()).commit();
			sp.edit().putString("userGongHao", emp.getUserengname()).commit();
			sp.edit().putString("userPwd", pwd).commit();
			sp.edit().putString("disId", emp.getDepartmentid()).commit();
			sp.edit().putString("gridId", emp.getGridId()).commit();
			sp.edit().putString("gridName", emp.getGridName()).commit();
			sp.edit().putString("loginDate", emp.getLoginDate()).commit();
			sp.edit().putString("pDiscs", emp.getpDiscs()).commit();
			sp.edit().putString("isDel", emp.getIsDel()).commit();
			sp.edit().putString("userStatus", ConstValues.FLAG_0).commit();
			sp.edit().putString("devStatus", ConstValues.FLAG_0).commit();
			
			// 因为老是报错,怀疑loginsession保存出错,重新保存 修改  20170317
			PrefUtils.putString(context, "userCode", emp.getUserid());
			PrefUtils.putString(context, "padId", padId);
			PrefUtils.putString(context, "userName", emp.getUsername());
			PrefUtils.putString(context, "userGongHao", emp.getUserengname());
			PrefUtils.putString(context, "userPwd", pwd);
			PrefUtils.putString(context, "disId", emp.getDepartmentid());
			PrefUtils.putString(context, "gridId", emp.getGridId());
			PrefUtils.putString(context, "gridName", emp.getGridName());
			PrefUtils.putString(context, "loginDate", emp.getLoginDate());
			PrefUtils.putString(context, "pDiscs", emp.getpDiscs());
			PrefUtils.putString(context, "isDel", emp.getIsDel());
			PrefUtils.putString(context, "userStatus", ConstValues.FLAG_0);
			PrefUtils.putString(context, "devStatus", ConstValues.FLAG_0);
			
		}
	}

	/**
	 * 记录登录者信息
	 * 
	 * @param proName 登录成功后，返回的当前用户信息
	 */
	private void updateLoginSession(String proName, String proValue) {

		SharedPreferences sp = context.getSharedPreferences(ConstValues.LOGINSESSIONKEY, 0);
		if (sp != null) {
			sp.edit().putString(proName, proValue).commit();
		}
	}

	/**
	 * 获取上次用户登录信息
	 * 
	 * @param context
	 * @return
	 */
	public LoginSession getLoginSession(Context context) {

		LoginSession session = new LoginSession();
		SharedPreferences sp = context.getSharedPreferences(ConstValues.LOGINSESSIONKEY, 0);
		if (sp != null) {
			session.setUserCode(sp.getString("userCode", ""));
			session.setPadId(sp.getString("padId", ""));
			session.setUserName(sp.getString("userName", ""));
			session.setUserGongHao(sp.getString("userGongHao", ""));
			session.setUserPwd(sp.getString("userPwd", ""));
			session.setDisId(sp.getString("disId", ""));
			session.setGridId(sp.getString("gridId", ""));
			session.setGridName(sp.getString("gridName", ""));
			session.setLoginDate(sp.getString("loginDate", DateUtil.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss")));
			session.setParentDisIDs(sp.getString("pDiscs", ""));
			session.setUserStatus(sp.getString("userStatus", "0"));
			session.setDevStatus(sp.getString("devStatus", "0"));
			session.setIsDel(sp.getString("isDel", "0"));
		}
		return session;
	}

	public void setBackGround(final View container) {
		// 请求网络
		HttpUtil httpUtil = new HttpUtil(60 * 1000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		httpUtil.send("opt_update_loginpic", "", new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
				if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
					String json = resObj.getResBody().getContent();
					MstLoginpicInfo loginpicInfo = JsonUtil.parseJson(json, MstLoginpicInfo.class);
					String picurl = loginpicInfo.getPicurl();
					if (!CheckUtil.isBlankOrNull(picurl)) {
						String url = PropertiesUtil.getProperties("platform_web");
						BitmapUtils bitmapUtils = new BitmapUtils(context);
						bitmapUtils.configDefaultLoadingImage(R.drawable.bg_login);
						bitmapUtils.configDefaultLoadFailedImage(R.drawable.bg_login);
						bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
						bitmapUtils.display(container, url + picurl, null, null);
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {

			}
		});
	}
}
