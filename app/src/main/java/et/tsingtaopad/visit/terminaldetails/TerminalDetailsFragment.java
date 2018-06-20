package et.tsingtaopad.visit.terminaldetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.ReflectUtil;

/**
 * 项目名称：营销移动智能工作平台 <br>
 * 文件名：TerminalDetailsFragment.java <br>
 * 作者：@沈潇 <br>   
 * 创建时间：2013/11/24 <br>        
 * 功能描述: 终端详情 <br>
 * 版本 V 1.0 <br>               
 * 修改履历 <br>
 * 日期      原因  BUG号    修改人 修改版本 <br>
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TerminalDetailsFragment extends BaseActivity 
                        implements OnClickListener, OnItemSelectedListener {

	private Button banner2_back_bt;
	private TextView banner2_title_tv;
	
	private Button terminaldetails_btn_timebegin;
	private Button terminaldetails_btn_timeend;
	private Button terminaldetails_btn_submit;
	private Spinner terminaldetails_spinner_selectrouteline;
	private TerminalDetailsService service;
	private ListView purchasedetails_lv_display;
    
	@Override
	protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.visit_terminaldetails);
		this.initView();
        this.initData();
    }
	
	 /**
     *  初始化界面组件
     */
    private void initView() {
    	service=new TerminalDetailsService(this);
    	
        // 绑定页面组件
    	banner2_back_bt=(Button)findViewById(R.id.banner_navigation_bt_back);
    	banner2_title_tv = (TextView) findViewById(R.id.banner_navigation_tv_title);
    	terminaldetails_btn_timebegin=(Button)findViewById(R.id.terminaldetails_btn_timebegin);
    	
    	terminaldetails_btn_timeend=(Button)findViewById(R.id.terminaldetails_btn_timeend);
    	terminaldetails_btn_submit=(Button)findViewById(R.id.terminaldetails_btn_submit);
    	terminaldetails_spinner_selectrouteline=(Spinner)findViewById(R.id.terminaldetails_spinner_selectrouteline);
    	purchasedetails_lv_display=(ListView)findViewById(R.id.purchasedetails_lv_display);

        // 添加底部view和绑定ListView数据
        View footview = LayoutInflater.from(this).inflate(R.layout.
                                             visit_agencyvisit_footview, null);
        purchasedetails_lv_display.addFooterView(footview);
        
        // 绑定事件
    	terminaldetails_btn_timebegin.setOnClickListener(this);
    	terminaldetails_btn_timeend.setOnClickListener(this);
    	terminaldetails_btn_submit.setOnClickListener(this);
    	terminaldetails_spinner_selectrouteline.setOnItemSelectedListener(this);
    	banner2_back_bt.setOnClickListener(this);
    	//异步数据处理
    	
    }
    
    /**
     * 初始化界面数据
     */
    private void initData() {
        
        //页面显示数据初始化
        banner2_title_tv.setText(R.string.terminal_jinhuodetails);
        String nowDate = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        terminaldetails_btn_timebegin.setText(nowDate);
        terminaldetails_btn_timeend.setText(nowDate);
        
    	//线路Spinner数据的绑定
        List<MstRouteM> lineLst = new ArrayList<MstRouteM>(ConstValues.lineLst);;
        if(lineLst.size()>1 && ReflectUtil.getFieldValueByName("routename", lineLst.get(0)).toString().equals("请选择")) {
            lineLst.remove(0);
        }
        SpinnerKeyValueAdapter spinneradapter = new SpinnerKeyValueAdapter(this, lineLst, new String[]{"routekey", "routename"}, null);
        terminaldetails_spinner_selectrouteline.setAdapter(spinneradapter);
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.terminaldetails_btn_submit:
		    if(terminaldetails_btn_timebegin.getText().toString().compareTo
		            (terminaldetails_btn_timeend.getText().toString()) > 0 || 
		            DateUtil.formatDate(new Date(), "yyyy-MM-dd").compareTo
		            (terminaldetails_btn_timeend.getText().toString()) < 0 || 
		            DateUtil.formatDate(new Date(), "yyyy-MM-dd").compareTo
                    (terminaldetails_btn_timebegin.getText().toString()) < 0) {
		        Toast.makeText(getBaseContext(), R.string.terminal_msg_date, Toast.LENGTH_LONG).show();
		    }else {
		        service.getSearchData(purchasedetails_lv_display, terminaldetails_spinner_selectrouteline, terminaldetails_btn_timebegin, terminaldetails_btn_timeend);
		    }
			break;
		case R.id.terminaldetails_btn_timebegin:
			service.getDatePickerDialog(terminaldetails_btn_timebegin);
			break;
		case R.id.terminaldetails_btn_timeend:
			service.getDatePickerDialog(terminaldetails_btn_timeend);
			break;
		case R.id.banner_navigation_bt_back:
		    this.getFragmentManager().popBackStack();
			this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	    Spinner sp = (Spinner)arg0;
	    if(arg1 !=null) {
	        sp.setTag(((TextView)arg1).getHint());
	    }else{
	        sp.setTag("");
	    }
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
}
