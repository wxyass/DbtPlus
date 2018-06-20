///////////////////////////////////////////////////////////////////////////////
//Copyright ownership belongs to SINOSOFT CO.,LTD.shall not be reproduced ,  //
//copied,or used in other ways without permission.Otherwise SINOSOFT CO.,LTD.//
//will have the right to pursue legal responsibilities.                      //
//                                                                           //
//Copyright &copy; 2011 SINOSOFT CO., LTD. All rights reserved.              //
///////////////////////////////////////////////////////////////////////////////
package et.tsingtaopad.syssetting.queryfeedback;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;


import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstQuestionsanswersInfo;
import et.tsingtaopad.tools.FunUtil;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：QueryFeedbackFragment.java<br>
 * 作者：@沈潇<br>
 * 创建时间：2013/11/24<br>
 * 功能描述: 问题反馈<br>
 * 版本 V 1.0<br>
 * 修改履历 日期 原因 BUG号 修改人 修改版本
 */
@SuppressLint("NewApi")
public class QueryFeedbackFragment extends BaseFragmentSupport implements
		OnClickListener, OnScrollListener, OnCheckedChangeListener {
    private Button banner2_back_bt;
    private TextView banner2_title_tv;
    
    private RadioGroup queryfeedback_rg;
    private RadioButton[] radioButton;
//	private RadioButton querfeedback_cb1_qs;
//	private RadioButton querfeedback_cb2_qs;
//	private RadioButton querfeedback_cb3_qs;
    private EditText querfeedback_et_question;
	private EditText mobileEt;
	private Button querfeedback_bt_submit;
	private ListView querfeedback_listview;
	private QueryFeedbackService service;
	private RelativeLayout querfeedback_rl_form;
	private RelativeLayout bg_up_arrow_rl;
	private Button bg_up_arrow;
	private Button bg_up_arrow_top_line;
	
	private QueryFeedbackAdapter Query_adapter= null;
	
	private List<MstQuestionsanswersInfo> list = new ArrayList<MstQuestionsanswersInfo>();
	
	private List<CmmDatadicM> cmmDatadicM = new ArrayList<CmmDatadicM>();
	
	private int checkedQuerryType = -1;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConstValues.WAIT1:
				@SuppressWarnings("unchecked")
				List<MstQuestionsanswersInfo> list1 = (List<MstQuestionsanswersInfo>) msg.obj;
				list.addAll(remove(list1));
				Query_adapter.setData(list);
				break;
			case ConstValues.WAIT2:
				List<MstQuestionsanswersInfo> list2 = (List<MstQuestionsanswersInfo>) msg.obj;
				list2 = remove(list2);
				list2.addAll(list);
				list.clear();
				list = list2;
				Query_adapter.setData(list);
				break;
			}
		}

	};
	
	private List<MstQuestionsanswersInfo> remove(List<MstQuestionsanswersInfo> list1){
		for(int j = 0;j<list.size();j++){
			for(int i =0;i<list1.size();i++){
				if(list1.get(i).getQakey().equals(list.get(j).getQakey())){//主键一致
					synchronized(list1){
						list1.remove(i);
					}
					
				}
			}
		}
		return list1;
		
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null); 

	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.business_queryfeedback_test, null);

		this.initView(view);

		this.initData();

		return view;

	}

	/**
	 * 初始化界面组件
	 */
	private void initView(View view) {

		// 绑定页面组件
	    banner2_back_bt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
	    banner2_title_tv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
	    
	    queryfeedback_rg = (RadioGroup) view
	    		.findViewById(R.id.business_queryfeedback_radiogroup);
	    
//		querfeedback_cb1_qs = (RadioButton) view
//				.findViewById(R.id.querfeedback_cb1_qs);
//		querfeedback_cb2_qs = (RadioButton) view
//				.findViewById(R.id.querfeedback_cb2_qs);
//		querfeedback_cb3_qs = (RadioButton) view
//				.findViewById(R.id.querfeedback_cb3_qs);
	    querfeedback_et_question = (EditText) view
	            .findViewById(R.id.querfeedback_et_question);
		mobileEt = (EditText) view
				.findViewById(R.id.querfeedback_et_mobile);
		querfeedback_bt_submit = (Button) view
				.findViewById(R.id.querfeedback_bt_submit);
		querfeedback_listview = (ListView) view
				.findViewById(R.id.querfeedback_listview);
		bg_up_arrow_rl = (RelativeLayout) view.findViewById(R.id.ll1);
		bg_up_arrow = (Button) view.findViewById(R.id.bg_up_arrow);
		bg_up_arrow_top_line = (Button) view
				.findViewById(R.id.bg_up_arrow_top_line);

		querfeedback_rl_form = (RelativeLayout) view
				.findViewById(R.id.querfeedback_rl_form);
		
		// 返回确定按钮
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
		// 绑定事件
		queryfeedback_rg.setOnCheckedChangeListener(this);
		querfeedback_bt_submit.setOnClickListener(this);
		banner2_back_bt.setOnClickListener(this);
		bg_up_arrow.setOnClickListener(this);
		querfeedback_listview.setOnScrollListener(this);
		view.setOnClickListener(null);
		Query_adapter = new QueryFeedbackAdapter(getActivity(), list);
//		addFootView();
		querfeedback_listview.setAdapter(Query_adapter);
		
	}

	private void addFootView(){
		View view = View.inflate(getActivity(), R.layout.visit_agencyvisit_footview, null);
		
		querfeedback_listview.addFooterView(view);
		
	}
	
	private void addRadioBox(){
		cmmDatadicM = service.searchLocalQuestionType();
		Log.d("tag","questionType--->"+cmmDatadicM.size());
		radioButton =new RadioButton[cmmDatadicM.size()];
		for(int i = 0;i<cmmDatadicM.size();i++){
			radioButton[i] = new RadioButton(getActivity());
//			radioButton[i].setTextAlignment(R.style.banner_main);
			radioButton[i].setText(cmmDatadicM.get(i).getDicname());
			radioButton[i].setTextColor(Color.parseColor("#000000"));
			radioButton[i].setId(i);
			queryfeedback_rg.addView(radioButton[i]);
		}
		service.setRadiobuttons(radioButton);		
	}
	
	/**
	 * 初始化界面数据
	 */
	private void initData() {
		service = new QueryFeedbackService(handler, getActivity());
		service.setQuerfeedback_listview(querfeedback_listview);
		service.setQuerfeedback_et_question(querfeedback_et_question);
		service.setQuerfeedback_rl_form(querfeedback_rl_form);
		service.setBg_up_arrow(bg_up_arrow);
		service.setBg_up_arrow_top_line(bg_up_arrow_top_line);
		service.setBg_up_arrow_rl(bg_up_arrow_rl);
		// 本地读取数据
		service.setStyle(1);
		service.asynchronousDataHandler();
		
		//页面显示数据初始化
		banner2_title_tv.setText(R.string.business_quesionfeedback);
		addRadioBox();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bg_up_arrow:
			// 隐藏展示 提交按钮
			service.display();
			break;
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			// 退出
			this.getFragmentManager().popBackStack();
			break;
		case R.id.querfeedback_bt_submit:
			// 提交
			try {
				if(checkedQuerryType==-1){//有选项但是没有选择 cmmDatadicM.size()>0&&
					Toast.makeText(getActivity(), getString(R.string.business_please_select_question_type), Toast.LENGTH_SHORT).show();
					return;
				}
				// 选择问题类型
				String type = cmmDatadicM.size()==0?"":cmmDatadicM.get(checkedQuerryType).getDiccode();
				service.newDataInsert(type, FunUtil.isBlankOrNullTo(mobileEt.getText(), ""));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;

		}
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
		if((arg1==SCROLL_STATE_TOUCH_SCROLL||arg1==SCROLL_STATE_FLING)&&(Scroll_arg3==Scroll_arg1+Scroll_arg2)&&service!=null){//当滑动时，触摸时
			if(service.isEnd()){//如果已经到末尾了
				Log.d("tag","end");
				return;
			}else if(!service.isQuerying()){//没有正在查询
				service.QueryFeedbackServiceGet();
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d("tag","checked->"+checkedId);
		checkedQuerryType = checkedId;
		
	}
}
