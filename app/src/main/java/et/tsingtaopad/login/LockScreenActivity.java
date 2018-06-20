package et.tsingtaopad.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.ExitAppUtils;
import et.tsingtaopad.MainActivity;
import et.tsingtaopad.R;
import et.tsingtaopad.login.domain.LoginSession;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.PrefUtils;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：LoginActivity.java</br>
 * 作者：@吴承磊    </br>
 * 创建时间：2013/11/26</br>      
 * 功能描述: 用户登录的控制层Activity</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("HandlerLeak")
public class LockScreenActivity extends Activity implements OnClickListener {

	private LoginService service;

	private RelativeLayout containerRl;
	private EditText uidEt;
	private EditText pwdEt;
	private Button loginBt;
	private Button exitBt;
	private AlertDialog dialog;

	private Handler handler = new Handler() {

		@SuppressLint("NewApi") @Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();

			super.handleMessage(msg);
			switch (msg.what) {

			// 提示信息
			case ConstValues.WAIT1:
				//Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
				if (dialog != null) {
					dialog.dismiss();
				}
				boolean isSuccess = bundle.getBoolean("isSuccess", false);
				String msg1 = bundle.getString("msg","登录");//登录信息
				String isrepassword = bundle.getString("isrepassword","11100");//还有多少天需更改密码  11000离线登录成功
				/*if (isSuccess) {
					BaseActivity.isTimeToLock = false;
					finish();
				}*/
				if (isSuccess) {
					
					/**
					 * 此处做跳转前判断 主界面/修改密码界面
					 * 
					 * 1 原始密码-->需修改-->跳到修改密码界面
					 * 2 密码不是8位-->需修改-->跳到修改密码界面
					 * 3 密码不是数字加英文组合-->需修改-->跳到修改密码界面
					 * 4 后台要求修改-->需修改-->跳到修改密码界面
					 * 5 后台不要求修改-->不修改-->跳到主界面
					 */
					// 1 原始密码-->需修改
					//if("a1234567".equals(ConstValues.loginSession.getUserPwd())){
					if("a1234567".equals(PrefUtils.getString(getApplicationContext(), "userPwd", ""))){
						// 跳到修改密码界面
						Toast.makeText(getBaseContext(), "请先修改原始密码", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(LockScreenActivity.this, RepasswordActivity.class);
						ConstValues.isDayThingWarn=true;
						startActivity(intent);
						LockScreenActivity.this.finish();
					// 2 密码不是8位-->需修改-->跳到修改密码界面
					//}else if(ConstValues.loginSession.getUserPwd().length()!=8){
					}else if(PrefUtils.getString(getApplicationContext(), "userPwd", "").length()<8){
						// 跳到修改密码界面
						Toast.makeText(getBaseContext(), "密码少于8位,请先修改密码", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(LockScreenActivity.this, RepasswordActivity.class);
						ConstValues.isDayThingWarn=true;
						startActivity(intent);
						LockScreenActivity.this.finish();
					// 3 密码不是数字加英文组合-->需修改-->跳到修改密码界面
					//}else if(!ConstValues.loginSession.getUserPwd().
					}else if(!PrefUtils.getString(getApplicationContext(), "userPwd", "").
							matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$")){
						// 跳到修改密码界面
						Toast.makeText(getBaseContext(), "密码不是数字加英文组合,请先修改密码", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(LockScreenActivity.this, RepasswordActivity.class);
						ConstValues.isDayThingWarn=true;
						startActivity(intent);
						LockScreenActivity.this.finish();
					// 4 后台要求修改-->需修改-->跳到修改密码界面
					}
					
					/*else if("1".equals(isrepassword)){
						// 跳到修改密码界面
						Toast.makeText(getBaseContext(), "密码需要最少3个月换一次", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(LoginActivity.this, RepasswordActivity.class);
						ConstValues.isDayThingWarn=true;
						startActivity(intent);
						finish();
					// 5 后台不要求修改-->不修改-->跳到主界面
					}*/
					
					else {
						if(Integer.parseInt(isrepassword)>=10){// 密码时间大于10天
							// 跳到主界面
							Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
							BaseActivity.isTimeToLock = false;
							LockScreenActivity.this.finish();
						}else if(Integer.parseInt(isrepassword)>0&&Integer.parseInt(isrepassword)<10){// 大于0小于10
							// 跳到主界面
							Toast.makeText(getBaseContext(), "登录成功,密码还有"+isrepassword+"天到期,请及时修改", Toast.LENGTH_LONG).show();
							BaseActivity.isTimeToLock = false;
							LockScreenActivity.this.finish();
						}else if("11000".equals(isrepassword)){// 大于0小于10
							// 跳到主界面
							Toast.makeText(getBaseContext(), msg1, Toast.LENGTH_SHORT).show();
							BaseActivity.isTimeToLock = false;
							LockScreenActivity.this.finish();
						}else{
							// 跳到修改密码界面
							Toast.makeText(getBaseContext(), "密码需要最少3个月换一次", Toast.LENGTH_SHORT).show();
							BaseActivity.isTimeToLock = false;
							Intent intent = new Intent(LockScreenActivity.this, RepasswordActivity.class);
							ConstValues.isDayThingWarn=true;
							startActivity(intent);
							LockScreenActivity.this.finish();
						}
					}
				}else{
					// 登录失败 提示信息
					Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
				}

				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_login_activity);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.initView();
		this.initData();
	}

	private void initView() {

		// 绑定界面组件
	    containerRl = (RelativeLayout) findViewById(R.id.login_rl_lock_container);
		uidEt = (EditText) findViewById(R.id.login_et_uid);
		pwdEt = (EditText) findViewById(R.id.login_et_pwd);
		loginBt = (Button) findViewById(R.id.login_bt_submit);
		exitBt = (Button) findViewById(R.id.login_bt_cancel);

		// 绑定事件
		loginBt.setOnClickListener(this);
		exitBt.setOnClickListener(this);

		// uidEt.addTextChangedListener(watcher);
		pwdEt.addTextChangedListener(watcher);
	}

	private void initData() {

		service = new LoginService(this, handler);
		service.setBackGround(containerRl);
		LoginSession loginSession = service.getLoginSession(getApplicationContext());
		uidEt.setEnabled(false);
		uidEt.setText(loginSession.getUserGongHao());
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.login_bt_submit:
			this.loginIn();
			break;

		case R.id.login_bt_cancel:
			// ExitAppUtils.getInstance().exit();
			finish();
			BaseActivity.isTimeToLock = false;
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseActivity.isTimeToLock = false;
	}

	/**
	 * 登录
	 */
	private void loginIn() {

		// 弹出进度框
		dialog = new AlertDialog.Builder(this).setCancelable(false).create();
		dialog.setView(getLayoutInflater().inflate(R.layout.login_progress, null), 0, 0, 0, 0);
		dialog.show();
		String version = null;
		try {
			version = "V" + this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 调用登录服务
		service.login(uidEt.getText().toString(), pwdEt.getText().toString(),version);
	}

	/**
	 * 更新登录按钮的状态
	 */
	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!(CheckUtil.isBlankOrNull(uidEt.getText().toString()) || CheckUtil.isBlankOrNull(pwdEt.getText().toString()))) {
				loginBt.setEnabled(true);
				loginBt.setBackgroundResource(R.drawable.bt_login_submit);
			} else {
				loginBt.setEnabled(false);
				loginBt.setBackgroundResource(R.drawable.bt_login_submit_invalid);
			}
		}
	};

	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), getString(R.string.mainactivity_lock_screen), Toast.LENGTH_SHORT).show();
	};
}
