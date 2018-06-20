package et.tsingtaopad.operation.promotion;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：PromotionActivity.java</br> 
 * 作者：@ray </br>
 * 创建时间：2013-11-29</br> 
 * 功能描述: 促销活动查询界面</br> 
 * 版本 V 1.0</br> 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
public class PromotionActivity extends BaseActivity
                implements OnClickListener, OnItemSelectedListener {
	public static final String TAG = "PromotionActivity";
    private PromotionService service;
    
	private Button backBt;
	private TextView titleTv;
	
	private AlertDialog dialog;
	private Spinner promSp;
	private Button lineBt;
	private Button termTypeBt;
	private Button searchDateBt;
	private Button termSellBt;
	private Button startDateBt;
	private Button searchBt;
	private TextView searchDateTv;
	private TextView startdateTv;
	private TextView finishTv;
	private TextView unfinishTv;
	private TextView totalTv;
	private TextView rateTv;
	private TextView promBeginTv;
	private TextView promEndTv;
	
	private ListView finishLv;
	private ListView unfinishLv;
	
	
	private String searchDate;
	private String[] lineIds;
	private String[] termLevels;
	private String[] termSecSells;
	private MstPromotionsM promInfo;
    private List<MstPromotionsM> promLst = new ArrayList<MstPromotionsM>();
	
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
                {
                    dialog.dismiss();
                    };
			    break;

			// 查询本地数据库返回对象
			case ConstValues.WAIT2:
			    dealDateShow(ConstValues.FLAG_0, bundle.getString("termMap"));
			    if (dialog != null && dialog.isShowing()) dialog.dismiss();
			    {
			        dialog.dismiss();
                    };
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.operation_promotion);
		initview();
		initData();
	}

	/**
	 * 初始化界面
	 */
	private void initview() {
	    dialog = DialogUtil.progressDialog(this, R.string.dialog_msg_search);
	    dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
	    backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
	    titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
	    promSp = (Spinner) findViewById(R.id.promotion_sp_promotion);
	    lineBt = (Button) findViewById(R.id.promotion_btn_line);
	    startDateBt = (Button) findViewById(R.id.promotion_btn_startdate);
		termSellBt = (Button) findViewById(R.id.promotion_btn_qudao);
	    searchDateBt = (Button) findViewById(R.id.promotion_btn_date);
		termTypeBt = (Button) findViewById(R.id.promotion_btn_type);
		searchBt = (Button) findViewById(R.id.promotion_btn_search);
		startdateTv = (TextView) findViewById(R.id.promotion_tv_startdate);
		searchDateTv = (TextView) findViewById(R.id.promotion_tv_date);
		finishTv = (TextView) findViewById(R.id.promotion_tv_finish);
		unfinishTv = (TextView) findViewById(R.id.promotion_tv_unfinished);
		totalTv = (TextView) findViewById(R.id.promotion_tv_total);
		rateTv = (TextView) findViewById(R.id.promotion_tv_rate);
		promBeginTv = (TextView) findViewById(R.id.promotion_tv_begin);
		promEndTv = (TextView) findViewById(R.id.promotion_tv_to);
		finishLv = (ListView) findViewById(R.id.promotion_lv_finish);
		unfinishLv = (ListView) findViewById(R.id.promotion_lv_unfinished);
		ConstValues.msgHandler = this.handler;
		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);

		promSp.setOnItemSelectedListener(this);
		backBt.setOnClickListener(this);
		lineBt.setOnClickListener(this);
		termTypeBt.setOnClickListener(this);
		searchDateBt.setOnClickListener(this);
		searchBt.setOnClickListener(this);
		startDateBt.setOnClickListener(this);
		termSellBt.setOnClickListener(this);
	}

	/**
	 * 初始化界面数据
	 */
	private void initData() {

        service = new PromotionService(this, handler);
        titleTv.setText(R.string.promotion_promotion1);
        searchDate = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        searchDateBt.setText(searchDate);
        searchDateTv.setText(searchDate);
        promLst = service.queryPromo(searchDate.replace("-", ""), true);
        promSp.setAdapter(new SpinnerKeyValueAdapter(this,
                promLst, new String[]{"promotkey", "promotname"}, null));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 界面返回按钮
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			this.finish();
			break;

		// 选择线路
		case R.id.promotion_btn_line:
			lineDialog();
			break;
			
			// 选择销售渠道
		case R.id.promotion_btn_qudao:
			QudaoDialog();
			break;
			
		// 选择开始日期
		case R.id.promotion_btn_startdate:
			// 日历
	        int startyear = Integer.parseInt(searchDate.substring(0, 4));
	        int startmonth = Integer.parseInt(searchDate.substring(5, 7)) - 1;
	        int startday = Integer.parseInt(searchDate.substring(8, 10));

			// 日期选择框
			new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	                Calendar calendar = Calendar.getInstance();
	                calendar.set(year, monthOfYear, dayOfMonth);
	                String startDate = DateUtil.formatDate(calendar.getTime(), "yyyy-MM-dd");
	                startDateBt.setText(startDate);
	                startdateTv.setText(startDate);
				}
			}, startyear, startmonth, startday).show();
			break;
		// 选择终端等级
		case R.id.promotion_btn_type:
			TypeDialog();
			break;
			
		// 选择截止日期
		case R.id.promotion_btn_date:
			// 日历
	        int year = Integer.parseInt(searchDate.substring(0, 4));
	        int month = Integer.parseInt(searchDate.substring(5, 7)) - 1;
	        int day = Integer.parseInt(searchDate.substring(8, 10));

			// 日期选择框
			new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	                Calendar calendar = Calendar.getInstance();
	                calendar.set(year, monthOfYear, dayOfMonth);
	                String endDate = DateUtil.formatDate(calendar.getTime(), "yyyy-MM-dd");
	                searchDateBt.setText(endDate);
	                searchDateTv.setText(endDate);
				}
			}, year, month, day).show();
			break;
			
		// 查询促销活动
		case R.id.promotion_btn_search:
		    if (lineBt.getTag() != null){ 
		    	lineIds = lineBt.getTag().toString().split(",");
		    }
		    if (termTypeBt.getTag() != null) {
		    	termLevels = termTypeBt.getTag().toString().split(",");
		    	if(termLevels.length>0&&"-1".equals(termLevels[0])){
		    		termLevels = FunUtil.deleteFirst(termLevels);
		    	}
		    }
		    if (termSellBt.getTag() != null) {
		    	termSecSells = termSellBt.getTag().toString().split(",");
		    	if(termSecSells.length>0&&"-1".equals(termSecSells[0])){
		    		termSecSells = FunUtil.deleteFirst(termSecSells);
		    	}
		    }
		    String time= searchDate.replace("-","");
	        service.search(promInfo, lineIds, termLevels,termSecSells, 
	        		startdateTv.getText().toString().replace("-",""),
	        		searchDateTv.getText().toString().replace("-","")
	        		, dialog);
			break;
		}
	}
	
    /**
     * 线路对话框
     */
    private void lineDialog(){
        List<MstRouteM> lineLst = new ArrayList<MstRouteM>(ConstValues.lineLst);
        if (!CheckUtil.IsEmpty(lineLst)) {
            lineLst.remove(0);
        }
        DialogUtil.showMultipleDialog(this, 
                lineBt, R.string.promotion_title_line, lineLst, 
                    new String[]{"routekey","routename"}, R.string.promotion_line1);
    }
    
    /**
     * 终端等级选择对话框
     */
    private void TypeDialog(){

        List<KvStc> typeLst = new ArrayList<KvStc>( ConstValues.dataDicMap.get("levelLst"));
        if (!CheckUtil.IsEmpty(ConstValues.dataDicMap.get("levelLst"))) {
            Collections.copy(typeLst, ConstValues.dataDicMap.get("levelLst"));
            typeLst.remove(0);
        }
        // 把默认值去掉
        for (KvStc kvStc : typeLst) {
			if("1".equals(kvStc.getIsDefault())){
				kvStc.setIsDefault("");
			}
		}
        //DialogUtil.showMultipleDialog(this,termTypeBt, R.string.promotion_title_level, typeLst, new String[]{"key","value"}, R.string.promotion_type1);
        typeLst.add(0, new KvStc("-1", "全选", "-1"));
        DialogUtil.showAllMultipleDialog(this,termTypeBt, R.string.promotion_title_level, typeLst, new String[]{"key","value"}, null,R.string.promotion_type1);
        
    
    }
    
    /**
     * 渠道类型选择对话框
     */
    private void QudaoDialog(){

        List<KvStc> typeLst = new ArrayList<KvStc>( ConstValues.secSellLst);
        /*if (!CheckUtil.IsEmpty(ConstValues.secSellLst)) {
            Collections.copy(typeLst, ConstValues.secSellLst);
            //typeLst.remove(0);
        }*/
        // 把默认值去掉 
        for (KvStc kvStc : typeLst) {
			if("1".equals(kvStc.getIsDefault())){
				kvStc.setIsDefault("");
			}
		}
        typeLst.add(0, new KvStc("-1", "全选", "-1"));
        DialogUtil.showAllMultipleDialog(this,termSellBt, R.string.promotion_title_secSell, typeLst, new String[]{"key","value"}, null,R.string.promotion_type1);
    }
    
    /**
     * 促销活动查询界面 数据显示
     * 
     * @param type  0：处理本地查询结果， 1：处理网络查询返回结果
     */
    @SuppressWarnings("unchecked")
    private void dealDateShow(String type, String json) {
        
        if (ConstValues.FLAG_0.equals(type) && !CheckUtil.isBlankOrNull(json)) {
            Map<String, Object> termMap = JsonUtil.parseMap(json);
            List<String> finishLst = new ArrayList<String>();
            if (termMap.get("Y") != null ) {
                finishLst = (List<String>)termMap.get("Y");
            }
            List<String> unfinishLst = new ArrayList<String>();
            if (termMap.get("N") != null ) {
                unfinishLst = (List<String>)termMap.get("N");
            }
            if (termMap != null) {
                int finish = finishLst.size();
                int nufinish = unfinishLst.size();
                int total = finish + nufinish;
                finishTv.setText(String.valueOf(finish));
                unfinishTv.setText(String.valueOf(nufinish));
                totalTv.setText(String.valueOf(total));
                if (total == 0) {
                    rateTv.setText("0");
                    ViewUtil.sendMsg(this, "查询范围内无符合该活动的终端");
                } else if (finish == 0 ) {
                    rateTv.setText("0");
                } else {
                    DecimalFormat df = new DecimalFormat("##0.00");
                    rateTv.setText(df.format((double)finish/total * 100.0d) + "%");
                }
                PromotionAdapter finishAdapter = new PromotionAdapter(
                                    this, finishLst, R.drawable.ico_plan_right);
                finishLv.setAdapter(finishAdapter);
                PromotionAdapter unfinishAdapter = new PromotionAdapter(
                                    this, unfinishLst, R.drawable.ico_plan_wrong);
                unfinishLv.setAdapter(unfinishAdapter);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int id, long position) {
        
        promInfo = (MstPromotionsM)promSp.getSelectedItem();
        if ("-1".equals(promInfo.getPromotkey())) {
            promBeginTv.setText("");
            promEndTv.setText("");
        } else {
            promBeginTv.setText(DateUtil.formatDate(ConstValues.WAIT1, promInfo.getStartdate()));
            promEndTv.setText(DateUtil.formatDate(ConstValues.WAIT1, promInfo.getEnddate()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        
    }
}
