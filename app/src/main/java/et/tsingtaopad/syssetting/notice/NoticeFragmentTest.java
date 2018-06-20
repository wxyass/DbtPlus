package et.tsingtaopad.syssetting.notice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.R;
import et.tsingtaopad.syssetting.todaything.ToDayThingFragmentTest;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：NoticeFragmentTest.java</br>
 * 作者：dbt   </br>
 * 创建时间：2014-1-19</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 
 */
public class NoticeFragmentTest extends 
            BaseFragmentSupport implements OnClickListener {
	
	private NoticeFragmentListView noticeFragment = null;
	
	private ToDayThingFragmentTest todayFragment = null;
	
	private Button banner2_back_bt;
	private TextView banner2_title_tv;
	
	private ImageButton imgNotice,imgToday;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
	                    ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.business_notice_test, null);
		this.initView(view);
		this.initData();
		return view;
	}

	/**
	 * @param view
	 */
	private void initView(View view) {
		
		banner2_title_tv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		imgNotice = (ImageButton)view.findViewById(R.id.notice_bt_noticeicon);
		imgToday = (ImageButton) view.findViewById(R.id.notice_bt_todaythingicon);
		
		view.findViewById(R.id.banner_navigation_bt_back).setOnClickListener(this);
		
		imgNotice.setOnClickListener(this);
		imgToday.setOnClickListener(this);
		
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
		view.setOnClickListener(null);
	}

	/**
	 * 
	 */
	private void initData() {
		banner2_title_tv.setText(R.string.business_notice);
		noticeFragment = new NoticeFragmentListView();
		todayFragment = new ToDayThingFragmentTest();
		translate(noticeFragment);
	}

	// 
	private void translate(Fragment fragment){
		FragmentTransaction transaction = getChildFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.notice_tablelayout_title, fragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			this.getFragmentManager().popBackStack();
			break;
		case R.id.notice_bt_noticeicon://通知公告
			banner2_title_tv.setText(R.string.business_notice);
			translate(noticeFragment);
			imgNotice.setBackgroundResource(R.drawable.bg_notice_icon);
			imgToday.setBackgroundResource(R.drawable.bg_todaything_hit);
			break;
		case R.id.notice_bt_todaythingicon://今日要事
			banner2_title_tv.setText(R.string.business_todaything);
			translate(todayFragment);
			imgNotice.setBackgroundResource(R.drawable.bg_notice_hit);
			imgToday.setBackgroundResource(R.drawable.bg_todaything_icon);
			break;
		}
	}
}
