package et.tsingtaopad.syssetting.notice;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.business.DateUtils;
import et.tsingtaopad.db.tables.CmmBoardM;
import et.tsingtaopad.syssetting.todaything.ToDayThingFragmentTest;
import et.tsingtaopad.tools.CheckUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：NoticeFragmentListView.java</br>
 * 作者：dbt   </br>
 * 创建时间：2014-1-19</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class NoticeFragmentListView extends BaseFragmentSupport implements
OnClickListener, OnItemClickListener, OnScrollListener, OnCheckedChangeListener{
	
	private NoticeService service;
	
//	private Button banner2_back_bt;
//	private TextView banner2_title_tv;
	
//	private ImageView notice_bt_todaythingicon;
	private ListView notice_lv_display;
	
	
	private List<CmmBoardM> list = new ArrayList<CmmBoardM>();
	
	private NoticeAdapter adapter = null;
	
	private TextView dialog_title_tv;
	 /**对话框内容*/
    private TextView dialog_content_tv;
    /**开始时间，结束时间*/
    private TextView dialog_startTime_tv,dialog_endTime_tv;
    
    private boolean hasFootView = false;
    
    private View footView = null;
    
    private LinearLayout dialog_memoryTime_ll;
    
    private CheckBox dialog_remeber_cb = null;

    private Handler handler = new Handler() {

 		@Override
 		public void handleMessage(Message msg) {
 			switch (msg.what) {
 			case ConstValues.WAIT1://从网络获取数据
 				List<CmmBoardM> list1 = (List<CmmBoardM>) msg.obj;
 				list.addAll(remove(list1));
 				Log.d("tag",list.size()+"数据显示"+list1.size());
 				adapter.setData(list);
 				break;
 			case ConstValues.WAIT2:
 				break;
 			}
 		}

 	};
 	
 	
	private List<CmmBoardM> remove(List<CmmBoardM> list1){
		for(int j = 0;j<list.size();j++){
			for(int i =0;i<list1.size();i++){
				if(list1.get(i).getMessageid().equals(list.get(j).getMessageid())){//主键一致
					synchronized(list1){
						list1.remove(i);
					}
					
				}
			}
		}
		return list1;
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.business_notice_listview, null);
		this.initView(view);
		this.initData();
		return view;

	}

	/**
	 * 初始化界面组件
	 */
	private void initView(View view) {
		// 绑定页面组件
//		banner2_back_bt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
//		banner2_title_tv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		
//		notice_bt_todaythingicon = (ImageView) view
//				.findViewById(R.id.notice_bt_todaythingicon);
		notice_lv_display = (ListView) view
				.findViewById(R.id.notice_lv_display);
//		footView =View.inflate(getActivity(), R.layout.visit_agencyvisit_footview, null);
//		notice_lv_display.addFooterView(footView);
//		hasFootView = true;
//		addFootView();
		// 绑定事件
//		banner2_back_bt.setOnClickListener(this);
//		notice_bt_todaythingicon.setOnClickListener(this);
		notice_lv_display.setOnItemClickListener(this);
		notice_lv_display.setOnScrollListener(this);
		view.setOnClickListener(null);
		
	}

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		// 初始化service
		service = new NoticeService(handler, getActivity());
		//页面显示数据初始化
//		banner2_title_tv.setText(R.string.business_notice);
		list = service.searchLocalData();
		adapter = new NoticeAdapter(getActivity(), list, R.drawable.icon_horn_bg);
//		addFootView();
		notice_lv_display.setAdapter(adapter);
		
//		service.searchRemoteData();
	}
	
	private void addFootView(){
		Log.d("tag","AddFootView"+list);
		if(list.size()>0){
			Log.d("tag","AddFootView");
//			notice_lv_display.addFooterView(footView);
			footView.setVisibility(View.VISIBLE);
			hasFootView = true;
		}else{
			service.searchRemoteData(); 
			
		}
		
			
	}
	

	@Override
	public void onClick(View v) {
		Fragment fm = null;

		switch (v.getId()) {
		case R.id.banner_navigation_bt_back:
			// 退出
			this.getFragmentManager().popBackStack();
			break;
		case R.id.notice_bt_todaythingicon:
			// 跳转
			fm = new ToDayThingFragmentTest();
			break;
		case R.id.business_tadaything_dialog_img_close://关闭弹出框
			detailDialog.dismiss();
			break;
		default:
			break;
		}
		if (fm != null) {
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.business_container, fm);
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		showItemDialog((int)arg3);
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
	
	/**详情弹出框*/
	private Dialog detailDialog = null;
	
	// 弹窗展示通知公告
	private void showItemDialog(int position) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(
				R.layout.business_todaything_detail_dialog, null);
		detailDialog = new Dialog(getActivity());
		detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		detailDialog.setContentView(layout);
		detailDialog.show();
		layout.findViewById(R.id.business_tadaything_dialog_img_close).setOnClickListener(this);
		dialog_title_tv = (TextView) layout.findViewById(R.id.title);
		dialog_memoryTime_ll = (LinearLayout) layout.findViewById(R.id.business_tadaything_dialog_ll_time);
		dialog_content_tv = (TextView) layout.findViewById(R.id.business_tadaything_dialog_tv_content);
		dialog_startTime_tv = (TextView) layout.findViewById(R.id.business_tadaything_dialog_tv_start_time);
		dialog_remeber_cb = (CheckBox) layout.findViewById(R.id.business_tadaything_dialog_checkbox_memo);	
		layout.findViewById(R.id.textview1).setVisibility(View.GONE);
		dialog_remeber_cb.setVisibility(View.GONE);
		dialog_title_tv.setText(list.get(position).getMesstitle());
		dialog_content_tv.setText(list.get(position).getMessagedesc());
		String startDate = DateUtils.divide(list.get(position).getStartdate());
		String endDate = DateUtils.divide(list.get(position).getEnddate());
		if (!CheckUtil.isBlankOrNull(startDate) && !CheckUtil.isBlankOrNull(endDate)) {
			dialog_startTime_tv.setText(startDate + " -- " + endDate);
		}
		// 是否提醒(已经隐藏)
		dialog_remeber_cb.setOnCheckedChangeListener(this);
	}
	

	

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
