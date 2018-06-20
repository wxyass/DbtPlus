///////////////////////////////////////////////////////////////////////////////
//Copyright ownership belongs to SINOSOFT CO.,LTD.shall not be reproduced ,  //
//copied,or used in other ways without permission.Otherwise SINOSOFT CO.,LTD.//
//will have the right to pursue legal responsibilities.                      //
//                                                                           //
//Copyright &copy; 2011 SINOSOFT CO., LTD. All rights reserved.              //
///////////////////////////////////////////////////////////////////////////////
package et.tsingtaopad.visit.tomorrowworkrecord;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DialogUtil;
/**
 * 项目名称：营销移动智能工作平台<br> 
 * 文件名：TomorrowWorkRecordFragment.java<br>
 * 作者：@沈潇   <br>
 * 创建时间：2013/11/24  <br>      
 * 功能描述: 每日工作<br>
 * 版本 V 1.0<br>               
 * 修改履历<br>
 * 日期      原因  BUG号    修改人 修改版本<br>
 */
@SuppressLint("NewApi")
public class TomorrowWorkRecordFragment extends BaseActivity implements OnClickListener {
    
    private TomorrowWorkRecordService service;

    private AlertDialog dialog;
    private Button banner2_back_bt;
    private TextView banner2_title_tv;
    
	private Button tomorrowworksearch_btn_submit;
	private Button tomorrowwork_bt_timeselect;
	private ListView tomorrow_work_listView;
    
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
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                break;

            // 关闭查询等待框
            case ConstValues.WAIT2:
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                break;
            }
        }
    };
    
	@Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.visit_tomorrowworkrecord);
        this.initView();
        this.initData();
    }
	
	 /**
     *  初始化界面组件
     */
    private void initView() {
    	
        // 绑定页面组件
        dialog = DialogUtil.progressDialog(this, R.string.dialog_msg_search);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        banner2_back_bt=(Button)findViewById(R.id.banner_navigation_bt_back);
        banner2_title_tv=(TextView)findViewById(R.id.banner_navigation_tv_title);
        
    	tomorrowworksearch_btn_submit=(Button)findViewById(R.id.calendar_bt_search);
    	tomorrowwork_bt_timeselect=(Button)findViewById(R.id.tomorrowwork_bt_timeselect);
    	tomorrow_work_listView=(ListView)findViewById(R.id.tomorrow_work_listView);
		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
        
        // 绑定事件
    	tomorrowworksearch_btn_submit.setOnClickListener(this);
    	tomorrowwork_bt_timeselect.setOnClickListener(this);
    	banner2_back_bt.setOnClickListener(this);
    	
    }
    
    /**
     * 初始化界面数据
     */
    private void initData() {
    	service=new TomorrowWorkRecordService(this, handler);
    	
    	//页面显示数据初始化
    	banner2_title_tv.setText(R.string.tomorrowwork_title);
    	tomorrowwork_bt_timeselect.setText(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.calendar_bt_search:
		    if(DateUtil.formatDate(new Date(), "yyyy-MM-dd").compareTo(tomorrowwork_bt_timeselect.getText().toString()) < 0) {
		        Toast.makeText(getBaseContext(), R.string.terminal_msg_date, Toast.LENGTH_LONG).show();
		    }else {
		        service.getSearchData(tomorrow_work_listView, tomorrowwork_bt_timeselect, dialog);
		    }
			break;
		case R.id.tomorrowwork_bt_timeselect:
			service.getDatePickerDialog(tomorrowwork_bt_timeselect);
			break;
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
		    this.getFragmentManager().popBackStack();
			this.finish();
			break;
		default:
			break;
		}
	}
}
