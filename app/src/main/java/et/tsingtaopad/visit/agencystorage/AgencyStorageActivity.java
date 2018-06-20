package et.tsingtaopad.visit.agencystorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.initconstvalues.InitConstValues;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.agencystorage.domain.AgencystorageStc;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：AgencyStorageActivity.java</br>
 * 作者：@吴欣伟</br>
 * 创建时间：2013/11/26</br>
 * 功能描述: 经销商库存页面</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期       原因  BUG号    修改人 修改版本</br>
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class AgencyStorageActivity extends BaseActivity 
                    implements OnClickListener, OnItemSelectedListener {

    private AgencyStorageService service;

    private AlertDialog searchDialog;
    private Button backBt,btTimeA,btTimeB;
    private TextView titleTv;
    
    private String cureentDate;
    private String selectDate;
	private String aday;
	private Calendar calendar;
	private int yearr;
	private int month;
	private int day;
	private String datecureent;
	private String datecureents;
	private String datecureentx;
	
	private String dateselect;
	private String dateselects;
	private String dateselectx;
	
	
	private TextView tvTime;
	private TextView creenttvtTime;

    private ImageButton searchIb;
    private Spinner agencySelectSp;
    private ListView agencyStorageLv;
    
    Boolean flag = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.visit_agencystorage);
        
        this.initView();
        this.initData();
       
    }
    
    /**
     * 初始化界面组件
     */
    private void initView() {
       
        //绑定界面组件
        searchDialog = DialogUtil.progressDialog(this, R.string.dialog_msg_search);
        backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
        btTimeA = (Button) findViewById(R.id.agencystorage_btn_time);//经销商库存选时间
        btTimeB = (Button) findViewById(R.id.agencystorage_btn_timeb);//经销商库存选时间
        titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
        tvTime = (TextView) findViewById(R.id.listview_tv_title_selecttime);
        creenttvtTime = (TextView) findViewById(R.id.listview_tv_title_creenttime);//当前时间
        //查询按钮
        searchIb = (ImageButton) findViewById(R.id.agencystorage_ib_search);
        agencySelectSp = (Spinner) findViewById(R.id.agencystorage_sp_selectagency);
        agencyStorageLv = (ListView) findViewById(R.id.agencystorage_lv_agencystorage);
		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);        

        //绑定事件
        backBt.setOnClickListener(this);
        btTimeA.setOnClickListener(this);
        btTimeB.setOnClickListener(this);
        searchIb.setOnClickListener(this);
        agencySelectSp.setOnItemSelectedListener(this);
    }

    /**
     * 初始化界面数据
     */
    private void initData() {
    	calendar = Calendar.getInstance();
		yearr = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
    	if (day < 10) {
				aday = "0" + day;
			} else {
				aday = Integer.toString(day);
			}
    	datecureent=  (Integer.toString(yearr)  + String.format("%02d", month + 1)  + aday);//没转变格式之前de20140908
    	dateselect =  (Integer.toString(yearr)  + String.format("%02d", month + 1)  + aday);//没转变格式之前de20140908
    	datecureents=  (Integer.toString(yearr)  + String.format("%02d", month + 1)  + aday + "000000");//没转变格式之前de20140908000000 系统时间
    	datecureentx=  (Integer.toString(yearr)  + String.format("%02d", month + 1)  + aday + "235959");//没转变格式之前de20140908000000系统
    	dateselects = (Integer.toString(yearr) + String.format("%02d", month + 1) + aday + "000000");//选择时间
		dateselectx = (Integer.toString(yearr) + String.format("%02d", month + 1) + aday + "235959");//选择
    	cureentDate = (Integer.toString(yearr) + "-" + String.format("%02d", month + 1) + "-" + aday);
    	
    	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");//把选择控件也设置成系统时间
		Date date = calendar.getTime();
		String dateStr = sDateFormat.format(date);
		btTimeA.setText(dateStr);//选择
		btTimeB.setText(dateStr);//选择
		tvTime.setText(dateStr);//选择
    	creenttvtTime.setText(cureentDate);
    	
        service = new AgencyStorageService(this);
        //界面title显示内容
        titleTv.setText(R.string.agencystorage_banner_title);
        //绑定Spinner数据
        if (CheckUtil.IsEmpty(ConstValues.agencyVisitLst)) {
            InitConstValues initService = new InitConstValues(this);
            initService.initVisitAgencyPro();
        }
        SpinnerKeyValueAdapter selectSpAdapter = new SpinnerKeyValueAdapter
                         (this, ConstValues.agencyVisitLst, new String[]{"key", "value"}, null);
        agencySelectSp.setAdapter(selectSpAdapter);
    }
    
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        
        //界面返回按钮
        case R.id.banner_navigation_rl_back:
        case R.id.banner_navigation_bt_back:
            this.getFragmentManager().popBackStack();
            this.finish();
            break;

            //界面数据查询  注意这个查询是查询本地数据库,不请求网络
        case R.id.agencystorage_ib_search:
            if(agencySelectSp.getTag() == null) {
                ViewUtil.sendMsg(this, R.string.indexstatus_msg_update);
                
            } else {
                String agencyKey=agencySelectSp.getTag().toString();
                if (searchDialog !=null && !searchDialog.isShowing()) {
                    searchDialog.show();
                    
                    //B时间
                    boolean isBSameDate=true;
                    String aDatecureent=datecureent;
                    //是否存在经销商拜访
                    if(!service.isExistAgencyVisit(agencyKey, datecureent)){
                    	// 获取小于此日期的最后一次拜访
                        String newVisitDate=service.getMaxVisitDate(agencyKey, datecureent);
                        if(StringUtils.isNotBlank(newVisitDate)){
                            isBSameDate=false;
                            datecureents=newVisitDate+"000000";
                            datecureentx=newVisitDate+"235959";
                            aDatecureent=newVisitDate;
                        }
                    }

                    //A时间
                    boolean isASameDate=true;
                    String bBateselect=dateselect;
                    //是否存在经销商拜访
                    if(!service.isExistAgencyVisit(agencyKey, dateselect)){
                    	//获取小于此日期的最后一次拜访
                        String newVisitDate=service.getMaxVisitDate(agencyKey, dateselect);
                        if(StringUtils.isNotBlank(newVisitDate)){
                            isASameDate=false;
                            dateselects=newVisitDate+"000000";
                            dateselectx=newVisitDate+"235959";
                            bBateselect=newVisitDate;
                        }
                    }
                    List<AgencystorageStc> storLst = service
                            .getAgencyStorage( datecureents,datecureentx, agencyKey, aDatecureent,dateselects,dateselectx,agencyKey,bBateselect);
                    AgencyStorageAdapter storAdapter = new AgencyStorageAdapter(this, storLst,isASameDate,isBSameDate);
                    agencyStorageLv.setAdapter(storAdapter);
                    searchDialog.dismiss();
                }
            }
            break;
         //选择时间a
         case R.id.agencystorage_btn_time:
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
 					btTimeA.setText(selectDate);
 					tvTime.setText(selectDate);
 				}
 			}, yearr, month, day);
 			if (!dateDialog.isShowing()) {
 				dateDialog.show();
 			}

        	 break;
        //选择时间b	 
        case R.id.agencystorage_btn_timeb:
        	 DatePickerDialog dateDialog1 = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
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
  					datecureent = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday);
  					datecureents = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "000000");
  					datecureentx = (Integer.toString(year) + String.format("%02d", monthOfYear + 1) + aday + "235959");
  					cureentDate = (Integer.toString(year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + aday);
  					btTimeB.setText(cureentDate);
  					creenttvtTime.setText(cureentDate);
  				}
  			}, yearr, month, day);
  			if (!dateDialog1.isShowing()) {
  				dateDialog1.show();
  			}
        	break;

        default:
            break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        Spinner sp = (Spinner)arg0;
        if (arg1 != null) {
            sp.setTag(((TextView)arg1).getHint());
        } else {
            sp.setTag("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        
    }
}
