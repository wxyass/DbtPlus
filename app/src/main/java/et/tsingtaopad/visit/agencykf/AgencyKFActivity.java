package et.tsingtaopad.visit.agencykf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstAgencyKFM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：AddAgency.java</br> 作者：ywm </br>
 * 创建时间：2015-11-16</br> 功能描述: 经销商开发</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AgencyKFActivity extends BaseActivity implements OnClickListener {

	private final String TAG = "AgencyKFActivity";

	private Button banner2_back_bt;
	private TextView banner2_title_tv;

	private EditText agencyname;
	private Button addAgencyBt;
	private Button searchBt;
	private ListView addagencydetailsLv;

	private AgencyKFService service;
	private AlertDialog dialog;

	private MstAgencyKFM mstAgencyKFM = null;
	private ArrayList<MstAgencyKFM> kFMLst;// 经销商记录
	private String agencykftype;// 跳转类型 1新增 2编辑

	private int currentPosition;// 当前条目

	private AgencyKFAdapter adapter;// 经销商适配器

	// 经销商详情弹窗相关组件
	private AlertDialog quicklyDialog;
	private View itemForm;
	
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
	
	// 是否数一数二经销商 用户选择
	private int numposition = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.agencykf_main);
		this.initView();
		this.initData();
	}

	/**
	 * 初始化界面组件
	 */
	private void initView() {
		// 绑定页面组件
		banner2_back_bt = (Button) findViewById(R.id.banner_navigation_bt_back);
		banner2_title_tv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		agencyname = (EditText) findViewById(R.id.addagency_et_agencyname);

		addAgencyBt = (Button) findViewById(R.id.addagency_btn_add);
		searchBt = (Button) findViewById(R.id.addagency_btn_search);
		addagencydetailsLv = (ListView) findViewById(R.id.addagency_lv_details);
		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
		// 绑定点击事件
		banner2_back_bt.setOnClickListener(this);
		addAgencyBt.setOnClickListener(this);
		searchBt.setOnClickListener(this);
	}

	/**
	 * 初始化界面数据
	 */
	private void initData() {

		service = new AgencyKFService(getApplicationContext(), null);

		// 页面显示数据初始化
		banner2_title_tv.setText(R.string.agencykf_details);
		
		calendar = Calendar.getInstance();
		yearr = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

	}

	@Override
	protected void onResume() {
		super.onResume();

		mstAgencyKFM = null;

		// 查询所有经销商开发记录

		kFMLst = new ArrayList<MstAgencyKFM>();
		kFMLst = service.queryMstAgencyKFMAll();

		adapter = new AgencyKFAdapter(this, kFMLst);
		addagencydetailsLv.setAdapter(adapter);
		// addagencydetailsLv.setSelector(getResources().getDrawable(R.drawable.bg_agencykf_listview_item));

		// 条目的点击事件
		addagencydetailsLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取当前条目经销商对象
				mstAgencyKFM = new MstAgencyKFM();
				mstAgencyKFM = kFMLst.get(position);
				currentPosition = position;

				adapter.setSeletor(position);
				adapter.notifyDataSetChanged();

				// 更改条目背景颜色
				// view.setBackgroundResource(R.drawable.bg_agencykf_listview_item);
				// view.setSelected(R.drawable.home_listview_item_bg);
				// android:background="@drawable/bg_agencykf_listview_item"

				// 弹窗显示经销商详情
				showAgencyDialog(mstAgencyKFM);

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 添加
		case R.id.addagency_btn_add:
			Intent intent = new Intent(this, AddAgencyActivity.class);
			agencykftype = "1";
			intent.putExtra("agencykftype", agencykftype);
			startActivity(intent);
			break;

		// 查询
		case R.id.addagency_btn_search:
			String agencyName = agencyname.getText().toString().trim();
			if (agencyName == null || agencyName.length() <= 0) {
				// Toast.makeText(this, "填入要查询的经销商名称", 0).show();

				ArrayList<MstAgencyKFM> valueLst = new ArrayList<MstAgencyKFM>();
				kFMLst.removeAll(kFMLst);
				valueLst = service.queryMstAgencyKFMAll();
				kFMLst.addAll(valueLst);
				adapter.notifyDataSetChanged();

			} else {

				// 模糊查询
				ArrayList<MstAgencyKFM> valueLst = new ArrayList<MstAgencyKFM>();
				kFMLst.removeAll(kFMLst);
				valueLst = service.getAgencyKf(agencyName);
				kFMLst.addAll(valueLst);
				adapter.notifyDataSetChanged();
			}
			break;

		// 返回
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			this.getFragmentManager().popBackStack();
			this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 更改经销商有效记录(此条经销商不显示)
	 * 
	 * @param mstAgencyKFM
	 */
	public void deleteagencyRecode(MstAgencyKFM mstAgencyKFM) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("{agencykfkey:'").append(mstAgencyKFM.getAgencykfkey());
		buffer.append("', upload:'").append("3").append("'}");

		//
		HttpUtil httpUtil = new HttpUtil(60 * 1000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		httpUtil.send("opt_save_agencykf", buffer.toString(),
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						ResponseStructBean resObj = HttpUtil
								.parseRes(responseInfo.result);
						if (ConstValues.SUCCESS.equals(resObj.getResHead()
								.getStatus())) {

							// 本地展示更新
							kFMLst.remove(currentPosition);
							adapter.notifyDataSetChanged();

							// 修改本地数据库表 状态
							String agencykfkey = resObj
									.getResBody()
									.getContent()
									.substring(
											2,
											resObj.getResBody().getContent()
													.length() - 2);
							// 更改经销商有效状态为1无效(最好改为删除表中此条记录,有时间再改,一直未修改)
							service.updataagencyKfstatus2byAgencykfkey(agencykfkey);

							Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT)
									.show();

						} else {

							Toast.makeText(AgencyKFActivity.this,
									"网络返回E,经销商记录删除失败", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(HttpException error, String errMsg) {
						Log.e(TAG, errMsg, error);
						Toast.makeText(AgencyKFActivity.this, "网络异常,经销商记录删除失败",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	/**
	 * 删除 再次确定
	 */
	private void confirmdelete() {

		// 如果不是查看操作，返回需再次确定
		DbtLog.logUtils(TAG, "确定删除经销商开发记录");
		View view = LayoutInflater.from(this).inflate(
				R.layout.agencyvisit_total_overvisit_dialog, null);
		TextView title = (TextView) view
				.findViewById(R.id.agencyvisit_tv_over_title);
		TextView msg = (TextView) view
				.findViewById(R.id.agencyvisit_tv_over_msg);
		ImageView sure = (ImageView) view
				.findViewById(R.id.agencyvisit_bt_over_sure);
		ImageView cancle = (ImageView) view
				.findViewById(R.id.agencyvisit_bt_over_quxiao);
		title.setText(R.string.tongyong_qishi);
		msg.setText(R.string.agencykf_isdelete);
		dialog = new AlertDialog.Builder(this).setCancelable(false).create();
		dialog.setView(view, 0, 0, 0, 0);
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteagencyRecode(mstAgencyKFM);
				mstAgencyKFM = null;// 恢复初始化,经销商记录设置为null,需要用户重新选择条目
				adapter.setSeletor(-1);// 条目背景颜色恢复默认
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 弹出窗口 展示经销商详情以及编辑删除取消
	 */
	// public void showAgencyDialog(String termName,final String terminalkey) {
	public void showAgencyDialog(final MstAgencyKFM mstagencykfm) {
		DbtLog.logUtils(TAG, "qulicklyDialog()");
		// 如果弹出框已弹出，则不再弹出
		if (quicklyDialog != null && quicklyDialog.isShowing())
			return;

		// 加载弹出窗口layout
		itemForm = this.getLayoutInflater().inflate(
				R.layout.agencykf_detaildialog, null);
		quicklyDialog = new AlertDialog.Builder(this).setCancelable(false)
				.create();
		quicklyDialog.setView(itemForm, 0, 0, 0, 0);
		quicklyDialog.show();
		// 设置弹窗大小
		Window dialogWindow = quicklyDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.height = 800;
		dialogWindow.setAttributes(lp);
		// 初始化
		TextView title = (TextView) itemForm.findViewById(R.id.unit_title);
		title.setText(mstagencykfm.getAgencyname());
		//
		TextView agencyname = (TextView) itemForm
				.findViewById(R.id.agencykfdatial_tv_agencyname);
		TextView agencypeople = (TextView) itemForm
				.findViewById(R.id.agencykfdatial_tv_agencypeople);
		
		TextView mobile = (TextView) itemForm
				.findViewById(R.id.agencykfdatial_tv_mobile);
		TextView address = (TextView) itemForm
				.findViewById(R.id.agencykfdatial_tv_address);
		TextView area = (TextView) itemForm
				.findViewById(R.id.agencykfdatial_tv_area);
		TextView money = (TextView) itemForm
				.findViewById(R.id.agencykfdatial_tv_money);
		TextView carnum = (TextView) itemForm
				.findViewById(R.id.agencykfdatial_tv_carnum);
		TextView productname = (TextView) itemForm
				.findViewById(R.id.agencykfdatial_tv_productname);
		TextView kfdata = (TextView) itemForm
				.findViewById(R.id.agencykfdatial_tv_kfdata);
		// 
		//↓---经销商开发 新加字段 详情------------------------------------------------------------------------
		TextView persion = (TextView) itemForm.findViewById(R.id.agencykfdatial_tv_persion);
		TextView business = (TextView) itemForm.findViewById(R.id.agencykfdatial_tv_business);
		TextView isone = (TextView) itemForm.findViewById(R.id.agencykfdatial_tv_isone);
		TextView coverterms = (TextView) itemForm.findViewById(R.id.agencykfdatial_tv_coverterms);
		TextView supplyterms = (TextView) itemForm.findViewById(R.id.agencykfdatial_tv_supplyterms);
		TextView passdate = (TextView) itemForm.findViewById(R.id.agencykfdatial_tv_passdate);
		
		switch (mstagencykfm.getIsone()) {
		case 0:
			isone.setText("未选择");
			break;
		case 1:
			isone.setText("是");
			break;
		case 2:
			isone.setText("否");
			break;
		default:
			break;
		}
		if("".equals(mstagencykfm.getPassdate())||mstagencykfm.getPassdate()==null){
			passdate.setText("");
		}else{
			passdate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(mstagencykfm.getPassdate()))));
		}
		persion.setText(mstagencykfm.getPersion());
		business.setText(mstagencykfm.getBusiness());
		coverterms.setText(mstagencykfm.getCoverterms());
		supplyterms.setText(mstagencykfm.getSupplyterms());
		//↑---经销商开发 新加字段 详情---------------------------------------------------------------------------
		
		agencyname.setText(mstagencykfm.getAgencyname());
		agencypeople.setText(mstagencykfm.getContact());
		mobile.setText(mstagencykfm.getMobile());
		address.setText(mstagencykfm.getAddress());
		
		productname.setText(mstagencykfm.getProductname());
		kfdata.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(mstagencykfm.getKfdate()))));
		
		if(CheckUtil.isBlankOrNull(mstagencykfm.getArea())||"0".equals(mstagencykfm.getArea())){
			//area.setText(mstagencykfm.getArea());
		}else{
			area.setText(mstagencykfm.getArea()+" 平方米");
		}
		if(CheckUtil.isBlankOrNull(mstagencykfm.getMoney())||"0".equals(mstagencykfm.getMoney())){
			//money.setText(mstagencykfm.getMoney());
		}else{
			money.setText(mstagencykfm.getMoney()+" 万元");
		}
		if(CheckUtil.isBlankOrNull(mstagencykfm.getCarnum())||"0".equals(mstagencykfm.getCarnum())){
			//carnum.setText(mstagencykfm.getCarnum());
		}else{
			carnum.setText(mstagencykfm.getCarnum()+" 辆");
		}
		
		
		// 编辑
		Button editBt = (Button) itemForm
				.findViewById(R.id.agencykfdialog_bt_edit);
		editBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ViewUtil.isDoubleClick(arg0.getId(), 2500))
					return;

				// 弹框消失
				quicklyDialog.cancel();
				showEditDialog(mstagencykfm);
			}
		});
		// 删除
		Button deteleBt = (Button) itemForm
				.findViewById(R.id.agencykfdialog_bt_detele);
		deteleBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ViewUtil.isDoubleClick(arg0.getId(), 2500))
					return;

				// 弹框消失
				quicklyDialog.cancel();
				confirmdelete();
			}
		});

		// 取消
		Button cancelBt = (Button) itemForm
				.findViewById(R.id.agencykfdialog_bt_cancel);
		cancelBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewUtil.hideSoftInputFromWindow(AgencyKFActivity.this, v);
				quicklyDialog.cancel();
			}
		});
	}

	/**
	 * 弹出窗口 重新编辑该经销商
	 */
	public void showEditDialog(final MstAgencyKFM mstagencykfm) {
		DbtLog.logUtils(TAG, "qulicklyDialog()");
		// 如果弹出框已弹出，则不再弹出
		if (quicklyDialog != null && quicklyDialog.isShowing())
			return;

		// 加载弹出窗口layout
		itemForm = this.getLayoutInflater().inflate(R.layout.agencykf_editdialog, null);
		quicklyDialog = new AlertDialog.Builder(this).setCancelable(false)
				.create();
		quicklyDialog.setView(itemForm, 0, 0, 0, 0);
		quicklyDialog.show();
		// 设置弹窗大小
		Window dialogWindow = quicklyDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.height = 800;
		dialogWindow.setAttributes(lp);
		// 初始化
		TextView title = (TextView) itemForm.findViewById(R.id.unit_title);
		title.setText(mstagencykfm.getAgencyname());
		//
		final EditText agencyname = (EditText) itemForm.findViewById(R.id.agencykf_et_agencyname);
		final EditText contact = (EditText) itemForm.findViewById(R.id.agencykf_et_contact);
		final EditText mobile = (EditText) itemForm.findViewById(R.id.agencykf_et_mobile);
		final EditText address = (EditText) itemForm.findViewById(R.id.agencykf_et_address);
		final EditText area = (EditText) itemForm.findViewById(R.id.agencykf_et_area);
		final EditText money = (EditText) itemForm.findViewById(R.id.agencykf_et_money);
		final EditText carnum = (EditText) itemForm.findViewById(R.id.agencykf_et_carnum);
		final EditText productname = (EditText) itemForm.findViewById(R.id.agencykf_et_productname);
		final Button kfdata = (Button) itemForm.findViewById(R.id.agencykf_et_kfdata);
		
		//↓---经销商开发 新加字段 重新编辑 // ywm 20160411 ---------------------------------------------------------------
		final EditText persionEt = (EditText) itemForm.findViewById(R.id.agencykf_et_persion);// 人员
		final EditText businessEt = (EditText) itemForm.findViewById(R.id.agencykf_et_business);// 经营状况
		final Spinner isoneSp = (Spinner) itemForm.findViewById(R.id.agencykf_et_isone);// 是否数一数二经销商
		final EditText covertermsEt = (EditText) itemForm.findViewById(R.id.agencykf_et_coverterms);// 覆盖终端
		final EditText supplytermsEt = (EditText) itemForm.findViewById(R.id.agencykf_et_supplyterms);// 直供终端
		final Button passdateEt = (Button) itemForm.findViewById(R.id.agencykf_et_passdate);// 达成时间
		persionEt.setText(mstagencykfm.getPersion());
		businessEt.setText(mstagencykfm.getBusiness());
		isoneSp.setSelection(mstagencykfm.getIsone());
		covertermsEt.setText(mstagencykfm.getCoverterms());
		supplytermsEt.setText(mstagencykfm.getSupplyterms());
		if("".equals(mstagencykfm.getPassdate())||mstagencykfm.getPassdate()==null){
			//passdateEt.setText("");
			//passdateEt.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			passdateEt.setText("请选择时间");
		}else{
			passdateEt.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(mstagencykfm.getPassdate()))));
		}
		isoneSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				numposition = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
        	
		});
		passdateEt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(),
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								calendar.set(year, monthOfYear, dayOfMonth);
								yearr = year;
								month = monthOfYear;
								day = dayOfMonth;
								if (dayOfMonth < 10) {
									aday = "0" + dayOfMonth;
								} else {
									aday = Integer.toString(dayOfMonth);
								}
								dateselect = (Integer.toString(year)
										+ String.format("%02d", monthOfYear + 1) + aday);
								dateselects = (Integer.toString(year)
										+ String.format("%02d", monthOfYear + 1) + aday + "000000");
								dateselectx = (Integer.toString(year)
										+ String.format("%02d", monthOfYear + 1) + aday + "235959");
								selectDate = (Integer.toString(year) + "-"
										+ String.format("%02d", monthOfYear + 1) + "-" + aday);
								passdateEt.setText(selectDate);
							}
						}, yearr, month, day);
				if (!dateDialog.isShowing()) {
					dateDialog.show();
				}
			}
		});
		//↑---经销商开发 新加字段 重新编辑 // ywm 20160411 ------------------------------------------------------------------------
		
		agencyname.setText(mstagencykfm.getAgencyname());
		contact.setText(mstagencykfm.getContact());
		mobile.setText(mstagencykfm.getMobile());
		address.setText(mstagencykfm.getAddress());
		
		if(CheckUtil.isBlankOrNull(mstagencykfm.getArea())||"0".equals(mstagencykfm.getArea())){
			//area.setText(mstagencykfm.getArea());
		}else{
			area.setText(mstagencykfm.getArea());
		}
		if(CheckUtil.isBlankOrNull(mstagencykfm.getMoney())||"0".equals(mstagencykfm.getMoney())){
			//money.setText(mstagencykfm.getMoney());
		}else{
			money.setText(mstagencykfm.getMoney());
		}
		if(CheckUtil.isBlankOrNull(mstagencykfm.getCarnum())||"0".equals(mstagencykfm.getCarnum())){
			//carnum.setText(mstagencykfm.getCarnum());
		}else{
			carnum.setText(mstagencykfm.getCarnum());
		}
		
		productname.setText(mstagencykfm.getProductname());
		kfdata.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(mstagencykfm.getKfdate()))));

		// 选择开发时间
		kfdata.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(),
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								calendar.set(year, monthOfYear, dayOfMonth);
								yearr = year;
								month = monthOfYear;
								day = dayOfMonth;
								if (dayOfMonth < 10) {
									aday = "0" + dayOfMonth;
								} else {
									aday = Integer.toString(dayOfMonth);
								}
								dateselect = (Integer.toString(year)
										+ String.format("%02d", monthOfYear + 1) + aday);
								dateselects = (Integer.toString(year)
										+ String.format("%02d", monthOfYear + 1) + aday + "000000");
								dateselectx = (Integer.toString(year)
										+ String.format("%02d", monthOfYear + 1) + aday + "235959");
								selectDate = (Integer.toString(year) + "-"
										+ String.format("%02d", monthOfYear + 1) + "-" + aday);
								kfdata.setText(selectDate);
							}
						}, yearr, month, day);
				if (!dateDialog.isShowing()) {
					dateDialog.show();
				}
			}
		});
		
		// 保存按钮
		Button editBt = (Button) itemForm
				.findViewById(R.id.agencykfdialog_bt_edit);
		editBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ViewUtil.isDoubleClick(arg0.getId(), 2500))
					return;
				MstAgencyKFM agencyKFInfo = new MstAgencyKFM();
				// 校验经销商名称合法
				if (agencyname.getText().toString() == null
						|| agencyname.getText().toString().length() <= 0) {
					Toast.makeText(AgencyKFActivity.this, R.string.agencykf_add_noname, Toast.LENGTH_SHORT).show();
					return;
				} else if(agencyname.getText().toString().length()>=50){// 汉字长度
					Toast.makeText(AgencyKFActivity.this, R.string.agencykf_add_marename, Toast.LENGTH_SHORT).show();
					return;
				} 
				// 校验法定人合法
				else if (contact.getText().toString() == null
						|| contact.getText().toString().length() <= 0) {
					Toast.makeText(AgencyKFActivity.this, R.string.agencykf_add_nocontact, Toast.LENGTH_SHORT).show();
					return;
				}else if (contact.getText().toString().length() >=50) {// 汉字长度
					Toast.makeText(AgencyKFActivity.this, R.string.agencykf_add_morecontact, Toast.LENGTH_SHORT).show();
					return;
				}
				// 仓库地址不超过100长度
				else if (address.getText().toString().length() >=100) {// 汉字长度
					Toast.makeText(AgencyKFActivity.this, R.string.agencykf_add_area, Toast.LENGTH_SHORT).show();
					return;
				}
				// 销售产品不超过500长度
				else if (productname.getText().toString().length() >=500) {// 汉字长度
					Toast.makeText(AgencyKFActivity.this, R.string.agencykf_add_product, Toast.LENGTH_SHORT).show();
					return ;
				}
				// 业代选择达成时间
				if(!"请选择时间".equals(passdateEt.getText().toString())){
					// 校验达成时间不能大于开发时间
					long a = DateUtil.parse(kfdata.getText().toString(), "yyyy-MM-dd").getTime();
					long b = DateUtil.parse(passdateEt.getText().toString(), "yyyy-MM-dd").getTime();
					if(b<a){
						Toast.makeText(getApplicationContext(), "达成时间必须大于等于开发时间", Toast.LENGTH_SHORT).show();
						return;
					}
					agencyKFInfo.setPassdate(Long.toString(DateUtil
							.parse(passdateEt.getText().toString(), "yyyy-MM-dd").getTime()));
					agencyKFInfo.setKfdate(Long.toString(DateUtil.parse(
							kfdata.getText().toString(), "yyyy-MM-dd").getTime()));
				// 业代没选达成时间
				}else {
					agencyKFInfo.setPassdate("");
					agencyKFInfo.setKfdate(Long.toString(DateUtil.parse(
							kfdata.getText().toString(), "yyyy-MM-dd").getTime()));
				}
				
				agencyKFInfo.setAgencykfkey(mstagencykfm.getAgencykfkey());
				agencyKFInfo.setAgencyname(agencyname.getText().toString());
				agencyKFInfo.setContact(contact.getText().toString());
				agencyKFInfo.setMobile(mobile.getText().toString());
				agencyKFInfo.setAddress(address.getText().toString());
				agencyKFInfo.setArea(area.getText().toString());
				agencyKFInfo.setMoney(money.getText().toString());
				agencyKFInfo.setCarnum(carnum.getText().toString());
				agencyKFInfo.setProductname(productname.getText().toString());
				//↓️---经销商开发 新加字段保存 //ywm 20160411---------------------------------------------------
				agencyKFInfo.setPersion(persionEt.getText().toString());
				agencyKFInfo.setBusiness(businessEt.getText().toString());
				agencyKFInfo.setCoverterms(covertermsEt.getText().toString());
				agencyKFInfo.setSupplyterms(supplytermsEt.getText().toString());
				agencyKFInfo.setIsone(numposition);
				
				
				//↑️---经销商开发 新加字段经销商开发  //ywm 20160411----------------------------------------------------
				
				
				// 定格
				//agencyKFInfo.setGridkey(ConstValues.loginSession.getGridId());
				agencyKFInfo.setGridkey(PrefUtils.getString(getApplicationContext(), "gridId", ""));
				// 状态
				agencyKFInfo.setStatus("0");
				
				/*Date date1 = new Date(Long.parseLong(mstagencykfm.getCreatedate()));
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				String adf1 = sdf1.format(date1);*/

				agencyKFInfo.setCreateuser(mstagencykfm.getCreateuser());
				agencyKFInfo.setCreatedate(mstagencykfm.getCreatedate());
				
				agencyKFInfo.setUpdatedate(Long.toString(DateUtil.parse(
						DateUtil.getDateTimeStr(1), "yyyyMMdd HH:mm:ss").getTime()));
				//agencyKFInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
				agencyKFInfo.setUpdateuser(PrefUtils.getString(getApplicationContext(), "userCode", ""));
				
				// 上传状态
				agencyKFInfo.setUpload("0");

				// 将此条记录更新到数据库表
				service.insertAgencyKf(agencyKFInfo);
				
				// 将对象转成json
				String json = JSON.toJSONString(agencyKFInfo);

				// 请求网络 上传开发商信息 并保存到本地
				HttpUtil httpUtil = new HttpUtil(60 * 1000);
				httpUtil.configResponseTextCharset("ISO-8859-1");
				httpUtil.send("opt_save_agencykf", json, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						ResponseStructBean resObj = HttpUtil
								.parseRes(responseInfo.result);
						if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
							// 截取已上传成功的经销商记录key
							String agencykfkey = resObj
									.getResBody()
									.getContent()
									.substring(
											2,
											resObj.getResBody().getContent().length() - 2);
							// 更改经销商上传标记为1(已上传)
							service.updataagencyKfupstatusbyAgencykfkey(agencykfkey);
							Toast.makeText(AgencyKFActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
							// 刷新经销商列表
							ArrayList<MstAgencyKFM> valueLst = new ArrayList<MstAgencyKFM>();
							kFMLst.removeAll(kFMLst);
							valueLst = service.queryMstAgencyKFMAll();
							kFMLst.addAll(valueLst);
							adapter.notifyDataSetChanged();
							

						} else {
							// 刷新经销商列表
							ArrayList<MstAgencyKFM> valueLst = new ArrayList<MstAgencyKFM>();
							kFMLst.removeAll(kFMLst);
							valueLst = service.queryMstAgencyKFMAll();
							kFMLst.addAll(valueLst);
							adapter.notifyDataSetChanged();
							Toast.makeText(getApplicationContext(), "网络异常E 经销商数据上传失败", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(HttpException error, String errMsg) {
						Log.e(TAG, errMsg, error);
						// 刷新经销商列表
						ArrayList<MstAgencyKFM> valueLst = new ArrayList<MstAgencyKFM>();
						kFMLst.removeAll(kFMLst);
						valueLst = service.queryMstAgencyKFMAll();
						kFMLst.addAll(valueLst);
						adapter.notifyDataSetChanged();
						Toast.makeText(getApplicationContext(), "网络异常  经销商数据上传失败", Toast.LENGTH_SHORT).show();
					}
				});
				

				// 弹框消失
				quicklyDialog.cancel();
			}
		});

		// 取消
		Button cancelBt = (Button) itemForm
				.findViewById(R.id.agencykfdialog_bt_cancel);
		cancelBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewUtil.hideSoftInputFromWindow(AgencyKFActivity.this, v);
				quicklyDialog.cancel();
			}
		});
	}

}
