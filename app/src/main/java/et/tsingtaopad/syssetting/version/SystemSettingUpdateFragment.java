package et.tsingtaopad.syssetting.version;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.R;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：SystemSettingUpdateFragment.java</br>
 * 作者：dbt   </br>
 * 创建时间：2014-2-10</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class SystemSettingUpdateFragment 
        extends BaseFragmentSupport implements OnClickListener {

    private VersionService service;
    
	private Button backBt;
	private TextView titleTv;
	private TextView versionTv;
	private Button checkVersionBt;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
	                    ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.system_setting_upload, null);
		initView(view);
		initData();
		return view;
	}
	
	private void initView(View view) {
	    backBt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
		titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		versionTv = (TextView) view.findViewById(R.id.syssetting_tv_update);
		checkVersionBt = (Button) view.findViewById(R.id.syssetting_btn_check);
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
		backBt.setOnClickListener(this);
		checkVersionBt.setOnClickListener(this);
        view.setOnClickListener(null);
	}
	
	private void initData() {
	    service = new VersionService(getActivity(), true, true);
	    
		titleTv.setText(getString(R.string.check_update));
		versionTv.setText(service.getVersion());
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.banner_navigation_rl_back://返回
		case R.id.banner_navigation_bt_back://返回
			this.getFragmentManager().popBackStack();
			break;
		case R.id.syssetting_btn_check://检查更新
		    service.checkVersion();
			break;
		}
	}
}
