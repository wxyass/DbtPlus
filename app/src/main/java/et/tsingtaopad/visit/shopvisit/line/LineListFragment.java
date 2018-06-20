package et.tsingtaopad.visit.shopvisit.line;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.line.domain.MstRouteMStc;
import et.tsingtaopad.visit.shopvisit.term.TermListFragment;
import et.tsingtaopad.visit.termserch.TermSearchFragment;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：LineListFragment.java</br> 作者：吴承磊 </br>
 * 创建时间：2013-11-28</br> 功能描述: 巡店拜访_选择线路列表</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
// 选点拜访_选择线路列表
public class LineListFragment extends BaseFragmentSupport implements
		OnClickListener, OnItemClickListener {

	String TAG = "LineListFragment";
	private LineListService service;

	private MstRouteMStc lineStc;
	private List<MstRouteMStc> lineLst;
	private ListView lineLv;
	private LineListAdapter adapter;

	private TextView titleTv;
	private Button backBt;
	private TextView confirmBt;
	private TextView gridNameTv;

	

	private Button searchBt;
	private EditText searchEt;
	private RelativeLayout backRl;
	private RelativeLayout confirmRl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		DbtLog.logUtils(TAG, "onCreateView()");
		View view = inflater.inflate(R.layout.shopvisit_line, null);
		this.initView(view);
		this.initData();
		return view;
	}

	/**
	 * 初始化界面组件
	 */
	private void initView(View view) {
		DbtLog.logUtils(TAG, "initView()");
		// 绑定页面组件
		titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		backBt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
		confirmBt = (TextView) view.findViewById(R.id.banner_navigation_bt_confirm);
		
		gridNameTv = (TextView) view.findViewById(R.id.line_tv_gridname);

		lineLv = (ListView) view.findViewById(R.id.line_lv);

		searchBt = (Button) view.findViewById(R.id.term_bt_search);
		searchEt = (EditText) view.findViewById(R.id.term_et_search);
		searchBt.setOnClickListener(this);
		// 绑定事件
		backBt.setOnClickListener(this);
		//confirmBt.setOnClickListener(this);
		backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		lineLv.setOnItemClickListener(this);
		view.setOnClickListener(null);
	}

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		DbtLog.logUtils(TAG, "initData()");
		service = new LineListService(getActivity());

		titleTv.setText(R.string.linelist_title);
		//gridNameTv.setText(ConstValues.loginSession.getGridName());
		gridNameTv.setText(PrefUtils.getString(getActivity(), "gridName", ""));

		// 绑定LineList数据
		lineLst = service.queryLine();
		adapter = new LineListAdapter(getActivity(), lineLst, confirmBt);
		lineLv.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {

		// 如果快速重复单击
		// if (ViewUtil.isDoubleClick(v.getId(), 2000)) return;

		Fragment fragment = null;

		switch (v.getId()) {
		// 查询终端
		case R.id.term_bt_search:
			DbtLog.logUtils(TAG, "查询终端");
			if (CheckUtil.isBlankOrNull(searchEt.getText().toString())) {
				Toast.makeText(getActivity(), "查询终端不能为空", Toast.LENGTH_SHORT)
						.show();
			} else {
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						searchEt.getWindowToken(), 0);
				fragment = new TermSearchFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable("seacrch", searchEt.getText().toString());
				fragment.setArguments(bundle);
			}
			break;

		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			getFragmentManager().popBackStack();
			DbtLog.logUtils(TAG, "返回");
			break;

		case R.id.banner_navigation_rl_confirm:
		//case R.id.banner_navigation_bt_confirm:
			if (ViewUtil.isDoubleClick(v.getId(), 1000)) return;
			fragment = new TermListFragment();
			lineStc = lineLst.get(adapter.getSelectItem());
			Bundle bundle = new Bundle();
			bundle.putSerializable("lineStc", lineStc);
			fragment.setArguments(bundle);
			DbtLog.logUtils(TAG, "进入终端列表");
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			// 解决Fragment中嵌套fragment
			//FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			transaction.replace(R.id.shopvisit_line_container, fragment);
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		adapter.setSelectItem(position);
		adapter.notifyDataSetInvalidated();
		confirmBt.setVisibility(View.VISIBLE);
	}
}
