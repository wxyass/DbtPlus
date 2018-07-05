package et.tsingtaopad.operation.working;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;


import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstWorksummaryInfo;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PropertiesUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：WorkingFragment.java</br> 
 * 作者：@ray </br>
 * 创建时间：2013/11/26</br> 
 * 功能描述: 月工作推进界面</br> 
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
public class MonthWorkingFragment extends BaseFragmentSupport 
                        implements OnClickListener, OnCheckedChangeListener {

    private WorkingService service;
    
    private Button backBt;
    private TextView titleTv;
    private TextView weekTv;
    private Button weekBt;
    private Button searchBt;
    
    private RadioGroup tagRg;
    private LinearLayout workReportLl;
    private LinearLayout remarkLl;
    private LinearLayout summaryLl;
    
    private WebView workReportWv;
    private TextView remarkDateTv;
    private EditText remarkEt;
    private TextView summaryDateTv;
    private EditText summaryEt;
    private Button submitBt;
    
    private Date searchDate;
    private String summaryId;
    private String searchWeek;

    private AlertDialog dialog;
    private boolean dialogShow;
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
            case ConstValues.WAIT2://网络请求数据，显示工作总结
                summaryEt.setText(msg.obj+"");
                if (bundle != null) {
                    MstWorksummaryInfo info = 
                           (MstWorksummaryInfo)bundle.getSerializable("info");
                    if (info != null) {
                        summaryId = info.getWskey();
                        summaryEt.setText(info.getWscontent());
                        remarkEt.setText(info.getRemarks());
                    }
                }
                break;
            default:
                break;
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                        ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.operation_working_week, null);
        view.setOnClickListener(null);
        initView(view);
        initData();
        
        return view;
    }

    /**
     * 初始化界面组件
     */
    private void initView(View view) {

        // 绑定界面组件
        backBt = (Button)view.findViewById(R.id.banner_navigation_bt_back);
        titleTv = (TextView)view.findViewById(R.id.banner_navigation_tv_title);
        weekTv = (TextView)view.findViewById(R.id.weekwork_tv_week);
        weekBt = (Button)view.findViewById(R.id.weekwork_bt_week);
        searchBt = (Button)view.findViewById(R.id.weekwork_bt_search);
        tagRg = (RadioGroup)view.findViewById(R.id.weekwork_rg_tag);
        workReportLl = (LinearLayout)view.findViewById(R.id.weekwork_ll_work);
        remarkLl = (LinearLayout)view.findViewById(R.id.weekwork_ll_remark);
        summaryLl = (LinearLayout)view.findViewById(R.id.weekwork_ll_summary);
        workReportWv = (WebView) view.findViewById(R.id.weekwork_web_workreport);
        remarkDateTv = (TextView) view.findViewById(R.id.weekwork_tv_remarkdate);
        remarkEt = (EditText) view.findViewById(R.id.weekwork_et_remark);
        summaryDateTv = (TextView) view.findViewById(R.id.weekwork_tv_summarydate);
        summaryEt = (EditText) view.findViewById(R.id.weekwork_et_summary);
        submitBt = (Button)view.findViewById(R.id.weekwork_bt_submit);

        // 绑定事件
        backBt.setOnClickListener(this);
        weekBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        submitBt.setOnClickListener(this);
        tagRg.setOnCheckedChangeListener(this);
        
        // 实例化WebView查询等待dialog
        dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        View progressView = getActivity().getLayoutInflater()
                                  .inflate(R.layout.login_progress,null);
        dialog.setView(progressView, 0, 0, 0, 0);
        TextView tv = (TextView) progressView.findViewById(R.id.textView1);
        tv.setText(getString(R.string.progressing));
        dialog.show();
    }

    private void initData() {
        
        service = new WorkingService(getActivity(), handler);
        titleTv.setText(R.string.monthwork_title);
        weekTv.setText(R.string.monthwork_month);
        tagRg.getChildAt(0).setBackgroundResource(R.drawable.bg_workmonth_work);
        
        // 获取当前月
        Date currDate = new Date();
        weekBt.setText(DateUtil.formatDate(currDate,"yyyy年MM月"));
        searchDate = currDate;
        
        // 查询当前时间的状态
        this.search();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        
        // 界面返回按钮
        case R.id.banner_navigation_bt_back:
            this.getFragmentManager().popBackStack();
            break;
            
        // 选择周
        case R.id.weekwork_bt_week:
            this.datePicker();
            break;
            
        // 查询
        case R.id.weekwork_bt_search:
            this.search();
            break;
            
        // 提交
        case R.id.weekwork_bt_submit:
            service.sumbitSummary(summaryId, searchWeek,
                    summaryEt.getText().toString(), ConstValues.FLAG_2);
            break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int rbId) {

        switch (rbId) {
        case R.id.weekwork_rb_work:
            remarkLl.setVisibility(View.GONE);
            summaryLl.setVisibility(View.GONE);
            workReportLl.setVisibility(View.VISIBLE);
            break;
            
        case R.id.weekwork_rb_remark:
            workReportLl.setVisibility(View.GONE);
            summaryLl.setVisibility(View.GONE);
            remarkLl.setVisibility(View.VISIBLE);
            break;
            
        case R.id.weekwork_rb_summary:
            remarkLl.setVisibility(View.GONE);
            workReportLl.setVisibility(View.GONE);
            summaryLl.setVisibility(View.VISIBLE);
            break;

        default:
            break;
        }
    }
    
    private void datePicker() {

        // 日历控件显示的时间
        String date = DateUtil.formatDate(searchDate, "yyyyMMdd");
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6)) - 1;
        int day = Integer.parseInt(date.substring(6, 8));

        // 时间择取器事件监听
        new DatePickerDialog(getActivity(),R.style.dialog_date, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                searchDate = calendar.getTime();
                weekBt.setText(DateUtil.formatDate(searchDate, "yyyy年MM月"));
            }
        }, year, month, day).show();
    }
    
    /**
     * 查找
     */
    private void search() {
        String searchDateStr = DateUtil.formatDate(searchDate, "yyyy年MM月");
        remarkDateTv.setText(searchDateStr);
        summaryDateTv.setText(searchDateStr);
        
        searchWeek = DateUtil.formatDate(searchDate, "yyyyMM");
//        MstWorksummaryInfo info = service.findSummaryInfo(searchWeek, ConstValues.FLAG_2);
//        if (info != null) {
//            summaryId = info.getWskey();
//            weekBt.setText(info.getWscontent());
//            remarkEt.setText(info.getRemarks());
//        } else {
//            summaryId = FunUtil.getUUID();
//        }
        
        // 加载周工作推进报表内容
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(PropertiesUtil.getProperties("platform_web"));
        urlBuffer.append("bs/business/forms/BusinessForms!employeeDayWrokPad.do?");
        // TODO hongen 待完善参数
        dialogShow = true;
        workReportWv.loadUrl(urlBuffer.toString());
        workReportWv.setWebViewClient(new WebViewClient() {

          @Override
          public void onPageFinished(WebView view, String url) {
              super.onPageFinished(view, url);
              if (dialog.isShowing()) {
                  dialog.dismiss();
                  dialogShow = false;
              }
          }
      });
    }
}
