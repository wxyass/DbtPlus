///////////////////////////////////////////////////////////////////////////////
//Copyright ownership belongs to SINOSOFT CO.,LTD.shall not be reproduced ,  //
//copied,or used in other ways without permission.Otherwise SINOSOFT CO.,LTD.//
//will have the right to pursue legal responsibilities.                      //
//                                                                           //
//Copyright &copy; 2011 SINOSOFT CO., LTD. All rights reserved.              //
///////////////////////////////////////////////////////////////////////////////
package et.tsingtaopad.operation.linecardsearch;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.operation.termtz.SpinnerTermTzAdapter;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;
import et.tsingtaopad.visit.shopvisit.line.LineListService;
import et.tsingtaopad.visit.shopvisit.line.domain.MstRouteMStc;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：LineCardSearchFragment.java<br>
 * 作者：ywm 创建时间：2017/11/24 <br>
 * 功能描述: 路线卡查询<br>
 * 版本 V 1.0<br>
 * 修改履历<br>
 * 日期 原因 BUG号 修改人 修改版本<br>
 */
@SuppressLint("NewApi")
public class LineCardSearchFragment extends BaseActivity implements OnClickListener {

	private AlertDialog dialog;
	private RelativeLayout backRl;
	private TextView banner_title_tv;
	private RelativeLayout confirmRl;
	private TextView sureBt;


	private LinearLayout tiaojianLl;
	private CheckBox stateCheck;
	private CheckBox proCheck;
	private CheckBox iceCheck;
	private CheckBox daojuCheck;
	private CheckBox supplyCheck;
	private CheckBox jinhuoCheck;
	private CheckBox lingshouCheck;
	private CheckBox storeCheck;
	private Spinner lineSp;
	private EditText termnameEt;

	private Button searchBtn;

	private WebView linecardWv;


	private String isstateCheck = "0";
	private String isproCheck = "0";
	private String isiceCheck = "0";
	private String isdaojuCheck = "0";
	private String issupplyCheck = "0";
	private String isjinhuoCheck = "0";
	private String islingshouCheck = "0";
	private String isstoreCheck = "0";
	private LineListService service;
	private List<MstRouteMStc> lineLst;
	private String routeKey = "";

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();

			super.handleMessage(msg);
			switch (msg.what) {

			// 提示信息
			case ConstValues.WAIT1:
				Toast.makeText(getApplicationContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				break;

			// 关闭查询等待框
			case ConstValues.WAIT2:
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.operation_linecardsearch);
		this.initView();
		this.initData();
	}

	/**
	 * 初始化界面组件
	 */
	private void initView() {

		// 绑定页面组件
		dialog = DialogUtil.progressDialog(this, R.string.dialog_msg_search);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);

		backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		banner_title_tv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);


		tiaojianLl = (LinearLayout)findViewById(R.id.linecardsearch_ll_tiaojian);
		stateCheck = (CheckBox) findViewById(R.id.linecardsearch_check_state);
		proCheck = (CheckBox) findViewById(R.id.linecardsearch_check_pro);
		iceCheck = (CheckBox) findViewById(R.id.linecardsearch_check_ice);
		daojuCheck = (CheckBox) findViewById(R.id.linecardsearch_check_daoju);
		supplyCheck = (CheckBox) findViewById(R.id.linecardsearch_check_supply);
		jinhuoCheck = (CheckBox) findViewById(R.id.linecardsearch_check_jinhuo);
		lingshouCheck = (CheckBox) findViewById(R.id.linecardsearch_check_lingshou);
		storeCheck = (CheckBox) findViewById(R.id.linecardsearch_check_store);
		lineSp = (Spinner) findViewById(R.id.linecardsearch_sp_line);
		termnameEt = (EditText) findViewById(R.id.linecardsearch_et_termname);

		searchBtn = (Button) findViewById(R.id.linecardsearch_bt_search);
		linecardWv = (WebView) findViewById(R.id.linecardsearch_web);
		
		// 修改图标
		sureBt = (TextView) findViewById(R.id.banner_navigation_bt_confirm);
		sureBt.setVisibility(View.VISIBLE);
		sureBt.setBackgroundResource(R.drawable.bt_indexstatus_search);
		sureBt.setTag("-1");

		// 绑定事件

		searchBtn.setOnClickListener(this);
		stateCheck.setOnCheckedChangeListener(listener);
		proCheck.setOnCheckedChangeListener(listener);
		iceCheck.setOnCheckedChangeListener(listener);
		daojuCheck.setOnCheckedChangeListener(listener);
		supplyCheck.setOnCheckedChangeListener(listener);
		jinhuoCheck.setOnCheckedChangeListener(listener);
		lingshouCheck.setOnCheckedChangeListener(listener);
		storeCheck.setOnCheckedChangeListener(listener);

		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);

	}

	/**
	 * 初始化界面数据
	 */
	private void initData() {

		banner_title_tv.setText("路线卡查询");
		service = new LineListService(this);
		lineLst = service.queryLine();
		MstRouteMStc mStc = new MstRouteMStc();
		mStc.setRoutename("全部");
		lineLst.add(0, mStc);
		SpinnerLineCardAdapter adapter = new SpinnerLineCardAdapter(getApplicationContext(), lineLst);
		lineSp.setAdapter(adapter);

	}

	CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				//Toast.makeText(getApplicationContext(), "已选中", 0).show();
				if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_state))) {
					isstateCheck = "1";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_vivification))) {
					isproCheck = "1";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_frozen))) {
					isiceCheck = "1";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_props))) {
					isdaojuCheck = "1";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_supply))) {
					issupplyCheck = "1";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_jinhuo))) {
					isjinhuoCheck = "1";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_lingshou))) {
					islingshouCheck = "1";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_store))) {
					isstoreCheck = "1";
				}
			} else {
				//Toast.makeText(getApplicationContext(), "未选中", 0).show();
				if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_state))) {
					isstateCheck = "0";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_vivification))) {
					isproCheck = "0";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_frozen))) {
					isiceCheck = "0";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_props))) {
					isdaojuCheck = "0";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_supply))) {
					issupplyCheck = "0";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_jinhuo))) {
					isjinhuoCheck = "0";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_lingshou))) {
					islingshouCheck = "0";
				} else if (buttonView.getText().equals(getResources().getString(R.string.linecardsearch_store))) {
					isstoreCheck = "0";
				}
			}
		}
	};
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回
		case R.id.banner_navigation_rl_back:
			finish();
			break;
		// 查询  // 显现查询条件
		case R.id.banner_navigation_rl_confirm:
			tiaojianLl.setVisibility(View.VISIBLE);

			break;
		// 确定  // 隐藏查询条件 显示webview
		case R.id.linecardsearch_bt_search:
			tiaojianLl.setVisibility(View.GONE);
			this.search();
			break;
		default:
			break;
		}
	}

	/**
	 * 查找
	 */
	private void search() {

		StringBuffer urlBuffer = new StringBuffer();

		urlBuffer.append(PropertiesUtil.getProperties("platform_web"));
		urlBuffer.append("bs/business/forms/BusinessFormsPad!iterminalDataPad.do");
		// 区域id
		urlBuffer.append("?model.businessFormsStc.disId=").append(PrefUtils.getString(getApplicationContext(), "disId", ""));
		// 定格key
		urlBuffer.append("&model.businessFormsStc.gridId=").append(PrefUtils.getString(getApplicationContext(), "gridId", ""));
		// 路线key
		urlBuffer.append("&model.businessFormsStc.lineId=").append(routeKey);
		// 终端名称
		urlBuffer.append("&model.businessFormsStc.terminalName=").append(termnameEt.getText().toString());
		
		// 铺货状态
		if("1".equals(isstateCheck)){
			urlBuffer.append("&model.choice=").append("PHZT");
		}
		// 产品生动化
		if("1".equals(isproCheck)){
			urlBuffer.append("&model.choice=").append("CPSDH");
		}
		// 冰冻化
		if("1".equals(isiceCheck)){
			urlBuffer.append("&model.choice=").append("BDH");
		}
		// 道具生动化
		if("1".equals(isdaojuCheck)){
			urlBuffer.append("&model.choice=").append("DJSDH");
		}
		// 供货关系
		if("1".equals(issupplyCheck)){
			urlBuffer.append("&model.choice=").append("GHGX");
		}
		// 进货价
		if("1".equals(isjinhuoCheck)){
			urlBuffer.append("&model.choice=").append("JHJ");
		}
		// 零售价
		if("1".equals(islingshouCheck)){
			urlBuffer.append("&model.choice=").append("LSJ");
		}
		// 库存
		if("1".equals(isstoreCheck)){
			urlBuffer.append("&model.choice=").append("KC");
		}

		// ceshi
		/*
		 * StringBuffer urlBuffer1 = new StringBuffer();
		 * urlBuffer1.append(PropertiesUtil.getProperties("platform_web"));
		 * urlBuffer1.append(
		 * "/bs/business/forms/BusinessFormsPad!agencyAccountListPad.do?accountModel.AgencyAccount.disId=1-4PG8&accountModel.agencyAccount.purchasetime=2015-11-01&accountModel.agencyAccount.purchasetimeEnd=2017-11-28&accountModel.agencyAccount.gridkey=1-63UNDF"
		 * );
		 */

		//linecardWv.getSettings().setUseWideViewPort(true);// 关键点
		//linecardWv.getSettings().setLoadWithOverviewMode(true);

		linecardWv.loadUrl(urlBuffer.toString());
		linecardWv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				/*
				 * // 加载周工作推进报表内容 if (tagRg.getCheckedRadioButtonId() ==
				 * R.id.weekwork_rb_work) { dialog.show(); }
				 */
				dialog.show();

				DbtLog.d("webview", "onPageStarted");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				DbtLog.d("webview", "onPageFinished");
				super.onPageFinished(view, url);
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 定格选择监听
		lineSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				MstRouteMStc selectStc = (MstRouteMStc) parent.getItemAtPosition(position);// 获取所选
				if(!"全部".equals(selectStc.getRoutename().toString())){
					routeKey = selectStc.getRoutekey().toString();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

	}
}
