package et.tsingtaopad.visit.agencyvisit;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;
import et.tsingtaopad.visit.agencyvisit.ledger.LedgerViewPagerActivity;
import et.tsingtaopad.visit.terminaldetails.TerminalDetailsFragment;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：AgencySelectFragment.java</br> 作者：@吴欣伟 </br>
 * 创建时间：2013/11/26</br> 功能描述: 经销商选择界面</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
public class AgencySelectFragment extends BaseFragmentSupport 
                            implements OnClickListener, OnItemClickListener {

	public static final String TAG = "AgencySelectFragment";

	private AgencyvisitService service;

	private List<AgencySelectStc> selectLst;
	private int temp = -1;

	private Button backBt;
	private TextView titleTv;

	private ImageButton ok;
	private RadioButton radio;
	private ListView agencyLv;
	private AgencySelectAdapter selectAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.agencyvisit_agencyselect, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

    // 初始化界面组件
	private void initView() {

		// 绑定界面组件
		backBt = (Button) getView().findViewById(R.id.banner_navigation_bt_back);
		titleTv = (TextView) getView().findViewById(R.id.banner_navigation_tv_title);
		ok = (ImageButton) getView().findViewById(R.id.agencyselect_ib_ok);
		agencyLv = (ListView) getView().findViewById(R.id.agencyselect_lv_select);
		RelativeLayout backRl = (RelativeLayout) getView().findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) getView().findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
		// 绑定事件
		backBt.setOnClickListener(this);
		ok.setOnClickListener(this);
		this.getView().setOnClickListener(null);
		agencyLv.setOnItemClickListener(this);
	}

	// 初始化界面数据
	private void initData() {

		service = new AgencyvisitService(getActivity());

		// 界面title显示内容
		titleTv.setText(R.string.agencyvisit_banner_title);

		// 获取拜访经销商数据
		selectLst = service.agencySelectLstQuery();

		// 添加底部view和绑定ListView数据
		View footview = LayoutInflater.from(getActivity()).inflate(R.layout.visit_agencyvisit_footview, null);
		agencyLv.addFooterView(footview);
		selectAdapter = new AgencySelectAdapter(getActivity(), selectLst);
		agencyLv.setAdapter(selectAdapter);
	}

	@Override
	public void onClick(View v) {
	    
	    if (!ViewUtil.isDoubleClick(v.getId())) {
    		switch (v.getId()) {
    		case R.id.agencyselect_ib_ok:
    			if (radio == null || temp == -1) {
    				Toast.makeText(getActivity(), R.string.agencyselect_toask_agencyselect, Toast.LENGTH_SHORT).show();
    			} else {
    			    
    				/*
    				// 跳转到进销存台账和调货台账页面
    				FragmentTransaction transaction = getFragmentManager().beginTransaction();
    				LedgerViewPagerFragment fragment = new LedgerViewPagerFragment();
    				Bundle b = new Bundle();
    				AgencySelectStc asStc = selectLst.get(temp);
    				b.putSerializable(TAG, asStc);
    				fragment.setArguments(b);
    				transaction.replace(R.id.agencyselect_fl_content, fragment);
    				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    				transaction.addToBackStack(null);
    				transaction.commit();
    				*/
    				Intent intent = new Intent(getActivity(), LedgerViewPagerActivity.class);
    				
    				Bundle bundle = new Bundle();
    				AgencySelectStc asStc = selectLst.get(temp);
    				bundle.putSerializable(TAG, asStc);

                    //把bundle封装至intent中
                    intent.putExtras(bundle);
                    
    				getActivity().startActivity(intent);
    			}
    			break;
    
    		case R.id.banner_navigation_rl_back:
    		case R.id.banner_navigation_bt_back:
    			getFragmentManager().popBackStack();
    			break;
    			
    		default:
    			break;
    		}
	    }
	}

	/**
	 * OnItemClickListener
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		radio = (RadioButton) arg1.findViewById(R.id.agencyselect_rb_lvitem);

		// RadioButton的选中状态重置，确保最多只有一项被选中
		for (int key : AgencySelectAdapter.getIsSelected().keySet()) {
			AgencySelectAdapter.getIsSelected().put(key, false);

		}
		AgencySelectAdapter.getIsSelected().put(arg2, true);
		selectAdapter.notifyDataSetChanged();
		temp = arg2;
	}
}
