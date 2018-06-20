///////////////////////////////////////////////////////////////////////////////
//Copyright ownership belongs to SINOSOFT CO.,LTD.shall not be reproduced ,  //
//copied,or used in other ways without permission.Otherwise SINOSOFT CO.,LTD.//
//will have the right to pursue legal responsibilities.                      //
//                                                                           //
//Copyright &copy; 2011 SINOSOFT CO., LTD. All rights reserved.              //
///////////////////////////////////////////////////////////////////////////////
package et.tsingtaopad.operation.termtz;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.visit.agencyvisit.AgencyvisitService;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：TermtzSearchFragment.java<br>
 * 作者：ywm
 * 创建时间：2013/11/24 <br>
 * 功能描述: 终端进货台账查询<br>
 * 版本 V 1.0<br>
 * 修改履历<br>
 * 日期 原因 BUG号 修改人 修改版本<br>
 */
@SuppressLint("NewApi")
public class TermtzSearchFragment extends BaseActivity implements OnClickListener {

	private AlertDialog dialog;
	private RelativeLayout backRl;
	private TextView banner_title_tv;
	private RelativeLayout confirmRl;
	

	private Spinner agencySp;
	private TextView startTimeBtn;
	private TextView endTimeBtn;
	private CheckBox xieyiCheck;
	private EditText termnameEt;
	private Button searchBtn;

	private WebView termTzWv;

	private String isxieyi = ""; // "1":选中        ""未选中
	private String agencyKey = "";

	// 时间控件
	private String selectDate;
	private String aday;
	private Calendar calendar;
	private int yearr;
	private int month;
	private int day;
	private String dateselect;
	private String dateselects;
	private String dateselectx;

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
		setContentView(R.layout.operation_termtzsearch);
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

		banner_title_tv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		

		agencySp = (Spinner) findViewById(R.id.termtzsearch_sp_agency);
		startTimeBtn = (TextView) findViewById(R.id.termtzsearch_bt_start);
		endTimeBtn = (TextView) findViewById(R.id.termtzsearch_bt_end);
		xieyiCheck = (CheckBox) findViewById(R.id.termtzsearch_check_xieyi);
		termnameEt = (EditText) findViewById(R.id.termtzsearch_et_termname);
		searchBtn = (Button) findViewById(R.id.termtzsearch_bt_search);
		termTzWv = (WebView) findViewById(R.id.termtzsearch_web);
		
		

		// 绑定事件

		startTimeBtn.setOnClickListener(this);
		endTimeBtn.setOnClickListener(this);
		searchBtn.setOnClickListener(this);
		xieyiCheck.setOnCheckedChangeListener(xieyilistener);

		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);

	}

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		
		banner_title_tv.setText("终端进货台账查询");

		calendar = Calendar.getInstance();
		yearr = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		// 获取经销商数据 经销商列表初始化 查数据库
		AgencyvisitService agencyvisitService = new AgencyvisitService(this);
		List<AgencySelectStc> selectLst = agencyvisitService.agencySelectLstQuery();
		AgencySelectStc stc = new AgencySelectStc();
		stc.setAgencyName("全部");
		selectLst.add(0,stc);
		SpinnerTermTzAdapter adapter = new SpinnerTermTzAdapter(getApplicationContext(), selectLst);
		agencySp.setAdapter(adapter);
		
		String todayTime = DateUtil.getDateTimeStr(7);
		startTimeBtn.setText(todayTime);
		endTimeBtn.setText(todayTime);

	}
	
	CompoundButton.OnCheckedChangeListener xieyilistener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				isxieyi = "1"; // Toast.makeText(getApplicationContext(),"已选中",0).show();
			} else {
				isxieyi = ""; // Toast.makeText(getApplicationContext(),"未选中",0).show();
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
		// 确定
		case R.id.banner_navigation_rl_confirm:

			break;
		// 开始时间
		case R.id.termtzsearch_bt_start:

			DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					calendar.set(year, monthOfYear, dayOfMonth);
					yearr = year;
					month = monthOfYear;
					day = dayOfMonth;
					if (dayOfMonth < 10) {
						aday = "0" + dayOfMonth;
					} else {
						aday = Integer.toString(dayOfMonth);
					}
					dateselect = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday);
					dateselects = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "000000");
					dateselectx = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "235959");
					selectDate = (Integer.toString(year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + aday);
					startTimeBtn.setText(selectDate);
				}
			}, yearr, month, day);
			if (!dateDialog.isShowing()) {
				dateDialog.show();
			}

			break;
		// 结束时间
		case R.id.termtzsearch_bt_end:
			DatePickerDialog enddateDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					calendar.set(year, monthOfYear, dayOfMonth);
					yearr = year;
					month = monthOfYear;
					day = dayOfMonth;
					if (dayOfMonth < 10) {
						aday = "0" + dayOfMonth;
					} else {
						aday = Integer.toString(dayOfMonth);
					}
					dateselect = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday);
					dateselects = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "000000");
					dateselectx = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "235959");
					selectDate = (Integer.toString(year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + aday);
					endTimeBtn.setText(selectDate);
				}
			}, yearr, month, day);
			if (!enddateDialog.isShowing()) {
				enddateDialog.show();
			}

			break;
		// 是否协议店
		case R.id.termtzsearch_check_xieyi:

			break;
		// 查询
		case R.id.termtzsearch_bt_search:
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
		urlBuffer.append("bs/business/forms/BusinessFormsPad!agencyAccountListPad.do");
		// 区域id
		urlBuffer.append("?accountModel.AgencyAccount.disId=").append(PrefUtils.getString(getApplicationContext(), "disId", ""));
		// 经销商key
		urlBuffer.append("&accountModel.AgencyAccount.agencykey=").append(agencyKey);
		// 开始时间
		urlBuffer.append("&accountModel.AgencyAccount.purchasetime=").append(startTimeBtn.getText().toString());
		// 结束时间
		urlBuffer.append("&accountModel.AgencyAccount.purchasetimeEnd=").append(endTimeBtn.getText().toString());
		// 是否协议店
		urlBuffer.append("&accountModel.AgencyAccount.selftreaty=").append(isxieyi);
		// 定格key
		urlBuffer.append("&accountModel.AgencyAccount.gridkey=").append(PrefUtils.getString(getApplicationContext(), "gridId", ""));
		// 终端名称
		urlBuffer.append("&accountModel.agencyAccount.terminalname=").append(termnameEt.getText().toString());

		// ceshi
		/*StringBuffer urlBuffer1 = new StringBuffer();
		urlBuffer1.append(PropertiesUtil.getProperties("platform_web"));
		urlBuffer1.append("/bs/business/forms/BusinessFormsPad!agencyAccountListPad.do?accountModel.AgencyAccount.disId=1-4PG8&accountModel.agencyAccount.purchasetime=2015-11-01&accountModel.agencyAccount.purchasetimeEnd=2017-11-28&accountModel.agencyAccount.gridkey=1-63UNDF");
		 */
		
		termTzWv.getSettings().setUseWideViewPort(true);// 关键点
		termTzWv.getSettings().setLoadWithOverviewMode(true);

		termTzWv.loadUrl(urlBuffer.toString());
		termTzWv.setWebViewClient(new WebViewClient() {
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
		agencySp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				AgencySelectStc selectStc = (AgencySelectStc) parent.getItemAtPosition(position);// 获取所选
				if(!"全部".equals(selectStc.getAgencyName().toString())){
					agencyKey = selectStc.getAgencyKey().toString();
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

	}
}
