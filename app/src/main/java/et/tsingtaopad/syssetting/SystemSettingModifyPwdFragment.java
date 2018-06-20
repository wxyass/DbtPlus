package et.tsingtaopad.syssetting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：SystemSettingModifyPwdFragment.java</br>
 * 作者：dbt   </br>
 * 创建时间：2014-2-10</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class SystemSettingModifyPwdFragment 
        extends BaseFragmentSupport implements OnClickListener {
    
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
				
				break;
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	                    ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.system_setting_modifypwd, null);
		initView(view);
		return view;
	}
	
	/**
	 * @param view
	 */
	private void initView(View view) {
	    
        service = new SyssettingService(getActivity(),handler);
        
		backBt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
		titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		currentpsdEt = (EditText) view.findViewById(R.id.syssetting_et_currentpsd);
		newpsdEt = (EditText) view.findViewById(R.id.syssetting_et_newpsd);
		repeatpsdEt = (EditText) view.findViewById(R.id.syssetting_et_repeatpsd);
		submitBt = (Button) view.findViewById(R.id.syssetting_btn_submit);
		
		titleTv.setText(getString(R.string.modify_pwd));
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
        view.setOnClickListener(null);
		backBt.setOnClickListener(this);
		submitBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.banner_navigation_rl_back://返回
		case R.id.banner_navigation_bt_back://返回
			this.getFragmentManager().popBackStack();
			break;
			
		case R.id.syssetting_btn_submit://提交修改密码
			service.changePwd(currentpsdEt.getText().toString(), 
			        newpsdEt.getText().toString(), repeatpsdEt.getText().toString());
			break;
		}
		
	}
	
	
}
