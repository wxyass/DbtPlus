package et.tsingtaopad.visit.agencykf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.PrefUtils;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：AddAgency.java</br> 作者：ywm </br>
 * 创建时间：2015-11-23</br> 
 * 功能描述: 新增/编辑经销商</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
@SuppressLint("HandlerLeak")
public class AddAgencyActivity extends BaseActivity implements OnClickListener {

	private final String TAG = "AddAgencyActivity";

	private Button backBt;
	private TextView titleTv;
	private Button commitBt;

	private EditText agencynameEt;
	private EditText contactEt;
	private EditText mobileEt;
	private EditText addressEt;
	private EditText areaEt;
	private EditText moneyEt;
	private EditText carnumEt;
	private EditText productnameEt;
	private Button kfdataEt;
	// 经销商开发 新加字段
	private EditText persionEt;
	private EditText businessEt;
	private Spinner isoneEt;
	private EditText covertermsEt;
	private EditText supplytermsEt;
	private Button passdataEt;

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
	private int    isone = 0;
	
	private AgencyKFService service;//

	private MstAgencyKFM mstAgencyKFM;// 经销商记录

	private String agencykftype;// 模式 1新增 2编辑

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.agencykf_add);
		this.initView();
		this.initData();
	}

	private void initView() {

		// 初始化界面组件
		backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
		titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		commitBt = (Button) findViewById(R.id.agencykf_bt_save);

		agencynameEt = (EditText) findViewById(R.id.agencykf_et_agencyname);
		contactEt = (EditText) findViewById(R.id.agencykf_et_contact);
		mobileEt = (EditText) findViewById(R.id.agencykf_et_mobile);
		addressEt = (EditText) findViewById(R.id.agencykf_et_address);
		areaEt = (EditText) findViewById(R.id.agencykf_et_area);
		moneyEt = (EditText) findViewById(R.id.agencykf_et_money);
		carnumEt = (EditText) findViewById(R.id.agencykf_et_carnum);
		productnameEt = (EditText) findViewById(R.id.agencykf_et_productname);
		kfdataEt = (Button) findViewById(R.id.agencykf_et_kfdata);
		// 经销商开发 新加字段
		persionEt = (EditText) findViewById(R.id.agencykf_et_persion);
		businessEt = (EditText) findViewById(R.id.agencykf_et_business);
		isoneEt = (Spinner) findViewById(R.id.agencykf_et_isone);
		covertermsEt = (EditText) findViewById(R.id.agencykf_et_coverterms);
		supplytermsEt = (EditText) findViewById(R.id.agencykf_et_supplyterms);
		passdataEt = (Button) findViewById(R.id.agencykf_et_passdate);
		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);

		// 绑定事件
		backBt.setOnClickListener(this);
		commitBt.setOnClickListener(this);
		kfdataEt.setOnClickListener(this);
		passdataEt.setOnClickListener(this);
	}

	private void initData() {

		// 获取传递过来的数据
		Intent intent = getIntent();
		agencykftype = intent.getStringExtra("agencykftype");
		// 如果是编辑模式,获取经销商记录
		if ("2".equals(agencykftype)) {
			mstAgencyKFM = (MstAgencyKFM) intent
					.getSerializableExtra("MstAgencyKFM");
		}

		service = new AgencyKFService(getApplicationContext(), null);
		titleTv.setText(R.string.agencykf_details);

		calendar = Calendar.getInstance();
		yearr = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		// 初始化开发时间
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");// 把选择控件也设置成系统时间
		Date date = calendar.getTime();
		String dateStr = sDateFormat.format(date);
		kfdataEt.setText(dateStr);
		passdataEt.setText("请选择时间");
		
		// 经销商名称正则过滤
		final int mMaxLenth = 50;// 设置经销商名称允许输入的字符长度
		agencynameEt.addTextChangedListener(new TextWatcher() {
			private int cou = 0;
			int selectionEnd = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				cou = before + count;
				String editable = agencynameEt.getText().toString();
				String str = CheckUtil.stringFilter(editable); // 过滤特殊字符
				if (!editable.equals(str)) {
					Toast.makeText(getApplicationContext(), "不能输入特殊字符", Toast.LENGTH_SHORT).show();
					agencynameEt.setText(str);
				}
				agencynameEt.setSelection(agencynameEt.length());
				cou = agencynameEt.length();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (cou > mMaxLenth) {
					selectionEnd = agencynameEt.getSelectionEnd();
					s.delete(mMaxLenth, selectionEnd);
				}
			}
		});
		
		isoneEt.setOnItemSelectedListener(new OnItemSelectedListener() {@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				// 获取选择的值
				/*String result = parent.getItemAtPosition(position).toString();
				private.setText(result);
				isoneEt.setText(Integer.toString(position));*/
				isone = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}});
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// 编辑模式打开,复制此经销商记录,显示到界面
		if ("2".equals(agencykftype)) {
			agencynameEt.setText(mstAgencyKFM.getAgencyname());
			contactEt.setText(mstAgencyKFM.getContact());
			mobileEt.setText(mstAgencyKFM.getMobile());
			addressEt.setText(mstAgencyKFM.getAddress());
			areaEt.setText(mstAgencyKFM.getArea());
			moneyEt.setText(mstAgencyKFM.getMoney());
			carnumEt.setText(mstAgencyKFM.getCarnum());
			productnameEt.setText(mstAgencyKFM.getProductname());
			
			Date date = new Date(Long.parseLong(mstAgencyKFM.getKfdate()));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String adf = sdf.format(date);
			kfdataEt.setText(adf);
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// 返回
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			this.finish();
			break;

		// 确定上传
		case R.id.agencykf_bt_save:
			this.saveAgency();
			break;

		// 选择时间
		case R.id.agencykf_et_kfdata:
			setData(v);
			break;
			// 选择时间
		case R.id.agencykf_et_passdate:
			setData2(v);
			break;

		default:
			break;
		}
	}

	/**
	 * 设置开发时间
	 */
	private void setData(View v) {
		DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(),R.style.dialog_date,
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
						kfdataEt.setText(selectDate);
					}
				}, yearr, month, day);
		if (!dateDialog.isShowing()) {
			dateDialog.show();
		}
	}
	/**
	 * 设置达成时间
	 */
	private void setData2(View v) {
		DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(),R.style.dialog_date,
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
						passdataEt.setText(selectDate);
					}
				}, yearr, month, day);
		if (!dateDialog.isShowing()) {
			dateDialog.show();
		}
	}

	/**
	 * 保存开发商信息,并上传
	 * 
	 * 插入本地数据库 上传到服务器
	 */
	private void saveAgency() {

		MstAgencyKFM agencyKFInfo = new MstAgencyKFM();
		// 将此经销商插入本地数据库

		// 将此经销商上传服务器

		// 校验输入的合法性
		if(checkInRight())return;
		
		// 业代选择达成时间
		if(!"请选择时间".equals(passdataEt.getText().toString())){
			// 校验达成时间不能大于开发时间
			long a = DateUtil.parse(kfdataEt.getText().toString(), "yyyy-MM-dd").getTime();
			long b = DateUtil.parse(passdataEt.getText().toString(), "yyyy-MM-dd").getTime();
			if(b<a){
				Toast.makeText(getApplicationContext(), "达成时间必须大于等于开发时间", Toast.LENGTH_SHORT).show();
				return;
			}
			agencyKFInfo.setKfdate(Long.toString(DateUtil.parse(
					kfdataEt.getText().toString(), "yyyy-MM-dd").getTime()));
			agencyKFInfo.setPassdate(Long.toString(DateUtil.parse(
					passdataEt.getText().toString(), "yyyy-MM-dd").getTime()));
		// 业代没选达成时间
		}else {
			agencyKFInfo.setKfdate(Long.toString(DateUtil.parse(
					kfdataEt.getText().toString(), "yyyy-MM-dd").getTime()));
		}
		
		if ("2".equals(agencykftype)) {// 编辑模式
			agencyKFInfo.setAgencykfkey(mstAgencyKFM.getAgencykfkey());
		} else {
			agencyKFInfo.setAgencykfkey(FunUtil.getUUID());
		}
		agencyKFInfo.setAgencyname(agencynameEt.getText().toString());
		agencyKFInfo.setContact(contactEt.getText().toString());
		agencyKFInfo.setMobile(mobileEt.getText().toString());
		agencyKFInfo.setAddress(addressEt.getText().toString());
		agencyKFInfo.setArea(areaEt.getText().toString());
		agencyKFInfo.setMoney(moneyEt.getText().toString());
		agencyKFInfo.setCarnum(carnumEt.getText().toString());
		agencyKFInfo.setProductname(productnameEt.getText().toString());
		
		
		
		agencyKFInfo.setPersion(persionEt.getText().toString());
		agencyKFInfo.setBusiness(businessEt.getText().toString());
		agencyKFInfo.setIsone(isone);
		agencyKFInfo.setCoverterms(covertermsEt.getText().toString());
		agencyKFInfo.setSupplyterms(supplytermsEt.getText().toString());
		
		/*
		 * agencyKFInfo.setKfdate(Long.toString(DateUtil.parse(kfdataEt.getText()
		 * .toString()+" 00:00:00","yyyy-MM-dd HH:mm:ss").getTime())); Date
		 * date= new
		 * Date(Long.parseLong(Long.toString(DateUtil.parse(kfdataEt.getText
		 * ().toString()+" 00:00:00","yyyy-MM-dd HH:mm:ss").getTime())));
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * String adf = sdf.format(date);
		 */
		// 定格
		//agencyKFInfo.setGridkey(ConstValues.loginSession.getGridId());
		agencyKFInfo.setGridkey(PrefUtils.getString(getApplicationContext(), "gridId", ""));
		// 状态 0有效 1失效
		agencyKFInfo.setStatus("0");
		// 人,时间
		if ("2".equals(agencykftype)) {// 编辑模式

			String createdate = mstAgencyKFM.getCreatedate();
			
			Date date1 = new Date(Long.parseLong(mstAgencyKFM.getCreatedate()));
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			String adf1 = sdf1.format(date1);

			agencyKFInfo.setCreateuser(mstAgencyKFM.getCreateuser());
			//agencyKFInfo.setCreatedate(adf1);
			agencyKFInfo.setCreatedate(mstAgencyKFM.getCreatedate());
			
			agencyKFInfo.setUpdatedate(Long.toString(DateUtil.parse(
					DateUtil.getDateTimeStr(1), "yyyyMMdd HH:mm:ss").getTime()));
			//agencyKFInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
			agencyKFInfo.setUpdateuser(PrefUtils.getString(getApplicationContext(), "userCode", ""));
			
		} else if ("1".equals(agencykftype)) {// 新增模式
			//agencyKFInfo.setCreateuser(ConstValues.loginSession.getUserCode());
			agencyKFInfo.setCreateuser(PrefUtils.getString(getApplicationContext(), "userCode", ""));
			long time = DateUtil.parse(DateUtil.getDateTimeStr(1), "yyyyMMdd HH:mm:ss").getTime();
			agencyKFInfo.setCreatedate(Long.toString(DateUtil.parse(
					DateUtil.getDateTimeStr(1), "yyyyMMdd HH:mm:ss").getTime()));//1448847565000
			//agencyKFInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
			agencyKFInfo.setUpdateuser(PrefUtils.getString(getApplicationContext(), "userCode", ""));
			
			agencyKFInfo.setUpdatedate(Long.toString(DateUtil.parse(
					DateUtil.getDateTimeStr(1), "yyyyMMdd HH:mm:ss").getTime()));//1448847581000
		}

		// 上传状态
		agencyKFInfo.setUpload("0");

		// 将此条记录插入数据库表
		service.insertAgencyKf(agencyKFInfo);

		//agencyKFInfo.setUpload("1");
		// 将对象转成json
		String json = JSON.toJSONString(agencyKFInfo);

		// 请求网络 上传开发商信息 并保存到本地
		HttpUtil httpUtil = new HttpUtil(60 * 1000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		httpUtil.send("opt_save_agencykf", json, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
				if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
					// 截取已上传成功的经销商记录key
					String agencykfkey = resObj.getResBody().getContent()
										.substring(2,resObj.getResBody().getContent().length() - 2);
					// 更改经销商上传标记upload为1(已上传)
					service.updataagencyKfupstatusbyAgencykfkey(agencykfkey);
					Toast.makeText(AddAgencyActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(getApplicationContext(), "网络异常E 经销商数据上传失败", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(HttpException error, String errMsg) {
				Log.e(TAG, errMsg, error);
				Toast.makeText(getApplicationContext(), "网络异常  经销商数据上传失败", Toast.LENGTH_SHORT).show();
			}
		});

		finish();
	}
	
	/**
	 * 校验经销商输入的合法性
	 * 
	 * @return true 输入不合法  false
	 */
	private boolean checkInRight() {
		// 校验经销商名称
		if (agencynameEt.getText().toString() == null
				|| agencynameEt.getText().toString().length() <= 0) {
			Toast.makeText(this, R.string.agencykf_add_noname, Toast.LENGTH_SHORT).show();
			return true;
		} else if(agencynameEt.getText().toString().length()>=50){// 汉字长度
			Toast.makeText(this, R.string.agencykf_add_marename, Toast.LENGTH_SHORT).show();
			return true;
		} 
		// 校验法定人
		else if (contactEt.getText().toString() == null
				|| contactEt.getText().toString().length() <= 0) {
			Toast.makeText(this, R.string.agencykf_add_nocontact, Toast.LENGTH_SHORT).show();
			return true;
		}else if (contactEt.getText().toString().length() >=50) {// 汉字长度
			Toast.makeText(this, R.string.agencykf_add_morecontact, Toast.LENGTH_SHORT).show();
			return true;
		}
		// 仓库地址不超过100长度
		else if (addressEt.getText().toString().length() >=100) {// 汉字长度
			Toast.makeText(this, R.string.agencykf_add_area, Toast.LENGTH_SHORT).show();
			return true;
		}
		// 销售产品不超过500长度
		else if (productnameEt.getText().toString().length() >=500) {// 汉字长度
			Toast.makeText(this, R.string.agencykf_add_product, Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

}
