package et.tsingtaopad.syssetting.notice.notification;

import java.util.ArrayList;
import java.util.List;


import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.CmmBoardM;
import et.tsingtaopad.syssetting.notice.NoticeAdapter;
import et.tsingtaopad.syssetting.notice.NoticeService;
import et.tsingtaopad.syssetting.todaything.ToDayThingFragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：NotificationNoticeActivity.java</br> 作者：dbt </br>
 * 创建时间：2013-12-26</br> 功能描述: </br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
public class NotificationNoticeActivity extends Activity implements
		OnClickListener, OnItemClickListener, OnScrollListener, OnCheckedChangeListener {

	private NoticeService service;

	private Button banner2_back_bt;
	private TextView banner2_title_tv;

	private ImageView notice_bt_todaythingicon;
	private ListView notice_lv_display;

	private List<CmmBoardM> list = new ArrayList<CmmBoardM>();

	private NoticeAdapter adapter = null;

	/** 对话框内容 */
	private TextView dialog_content_tv;
	/** 开始时间，结束时间 */

	private LinearLayout dialog_memoryTime_ll;

	private CheckBox dialog_remeber_cb = null;

	private boolean isNotification = false;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstValues.WAIT1:// 从网络获取数据
				List<CmmBoardM> list1 = (List<CmmBoardM>) msg.obj;
				list.addAll(list1);
				Log.d("tag", "数据显示" + list1.size());
				adapter.setData(list);
				break;
			case ConstValues.WAIT2:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_notice);
		initView();
	}

	/**
	 * 
	 */
	private void initView() {
		banner2_back_bt = (Button) findViewById(R.id.banner_navigation_bt_back);
		banner2_title_tv = (TextView) findViewById(R.id.banner_navigation_tv_title);

		notice_bt_todaythingicon = (ImageView) findViewById(R.id.notice_bt_todaythingicon);
		notice_lv_display = (ListView) findViewById(R.id.notice_lv_display);
//		findViewById(R.id.rl).setVisibility(View.GONE);
		// 绑定事件
		banner2_back_bt.setOnClickListener(this);
		notice_bt_todaythingicon.setOnClickListener(this);
		notice_lv_display.setOnItemClickListener(this);
		notice_lv_display.setOnScrollListener(this);

		// 初始化service
		service = new NoticeService(handler, this);
		// 页面显示数据初始化
		banner2_title_tv.setText(R.string.business_notice);
		list = service.searchLocalData();
		adapter = new NoticeAdapter(this, list, R.drawable.bg_todaywords_fill);
		notice_lv_display.setAdapter(adapter);
//		service.searchRemoteData();
		showItemDialog(list.size()-1);
	}

	@Override
	public void onClick(View v) {
		Fragment fm = null;

		switch (v.getId()) {
		case R.id.banner_navigation_bt_back:
			// 退出
			this.finish();
			break;
		case R.id.notice_bt_todaythingicon:
			// 跳转
			fm = new ToDayThingFragment();
			break;
		case R.id.business_tadaything_dialog_img_close://关闭弹出框
			detailDialog.dismiss();
			break;
		default:
			break;
			
		}
		
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		showItemDialog(arg2);
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		Scroll_arg1=arg1;
		Scroll_arg2=arg2;
		Scroll_arg3=arg3;
	}

	/**滑动状态*/
	private int Scroll_arg1,Scroll_arg2,Scroll_arg3;
	
	
	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		Log.d("tag","scroll--state->"+arg1);
		if((arg1==SCROLL_STATE_TOUCH_SCROLL||arg1==SCROLL_STATE_FLING)&&(Scroll_arg3==Scroll_arg1+Scroll_arg2)&&service!=null){//当滑动时，触摸时
			if(service.isEnd()){//如果已经到末尾了
				return;
			}else if(!service.isQuerying()){//没有正在查询
				service.searchRemoteData();
			}
		}
	}
	
	private void showItemDialog(int position) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(
				R.layout.business_todaything_detail_dialog, null);
		detailDialog = new Dialog(this);
		detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		detailDialog.setContentView(layout);
		detailDialog.show();
		layout.findViewById(R.id.business_tadaything_dialog_img_close).setOnClickListener(this);
		dialog_memoryTime_ll = (LinearLayout) layout.findViewById(R.id.business_tadaything_dialog_ll_time);
		dialog_content_tv = (TextView) layout.findViewById(R.id.business_tadaything_dialog_tv_content);
		dialog_remeber_cb = (CheckBox) layout.findViewById(R.id.business_tadaything_dialog_checkbox_memo);	
		dialog_content_tv.setText(list.get(position).getMessagedesc());
		dialog_remeber_cb.setOnCheckedChangeListener(this);
	}
	/**详情弹出框*/
	private Dialog detailDialog = null;



	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// 弹出框中--隐藏，，
		if(arg1){
			dialog_memoryTime_ll.setVisibility(View.VISIBLE);
		}else{
			dialog_memoryTime_ll.setVisibility(View.GONE);
		}
		
	}

}
