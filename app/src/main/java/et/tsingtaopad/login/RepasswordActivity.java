package et.tsingtaopad.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.syssetting.SyssettingService;


@SuppressLint("HandlerLeak")
public class RepasswordActivity extends BaseActivity implements
		OnClickListener {
	private SyssettingService service;

	private Button backBt;
	private TextView titleTv;
	private EditText currentpsdEt;
	private EditText newpsdEt;
	private EditText repeatpsdEt;
	private Button submitBt;
	
	public Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ConstValues.WAIT1:
				currentpsdEt.setText("");
				newpsdEt.setText("");
				repeatpsdEt.setText("");
				// 跳到登录
				Intent intent = new Intent(RepasswordActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_setting_modifypwd);
		this.initView();
		this.initData();

	}

	private void initView() {
	    
        service = new SyssettingService(getApplication(),handler);
        
		backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
		titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		
		currentpsdEt = (EditText) findViewById(R.id.syssetting_et_currentpsd);
		newpsdEt = (EditText) findViewById(R.id.syssetting_et_newpsd);
		repeatpsdEt = (EditText) findViewById(R.id.syssetting_et_repeatpsd);
		submitBt = (Button) findViewById(R.id.syssetting_btn_submit);
		LinearLayout ruleLl = (LinearLayout) findViewById(R.id.syssetting_ll_rule);
		
		titleTv.setText(getString(R.string.modify_pwd));

		backBt.setVisibility(View.GONE);
		ruleLl.setVisibility(View.VISIBLE);
		backBt.setOnClickListener(this);
		submitBt.setOnClickListener(this);
	
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	private void initData() {
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.syssetting_btn_submit://提交修改密码
			service.changePwd(currentpsdEt.getText().toString(), 
			        newpsdEt.getText().toString(), repeatpsdEt.getText().toString());
			
			break;

		default:
			break;
		}
	}

}
