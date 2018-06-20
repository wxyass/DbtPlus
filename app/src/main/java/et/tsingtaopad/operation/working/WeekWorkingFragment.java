package et.tsingtaopad.operation.working;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstWorksummaryInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：WorkingFragment.java</br> 
 * 作者：@ray </br>
 * 创建时间：2013/11/26</br> 
 * 功能描述: 周工作推进界面</br> 
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
public class WeekWorkingFragment extends BaseFragmentSupport implements OnClickListener, OnCheckedChangeListener
{

    private WorkingService service;

    private Button backBt;
    private TextView titleTv;
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

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();
            switch (msg.what)
            {
                case ConstValues.WAIT1://网络请求数据，显示工作总结
                    summaryEt.setText(msg.obj + "");
                    if (bundle != null)
                    {
                        MstWorksummaryInfo info = (MstWorksummaryInfo) bundle.getSerializable("info");
                        if (info != null)
                        {
                            summaryId = info.getWskey();
                            summaryEt.setText(info.getWscontent());
                            remarkEt.setText(info.getRemarks());
                        }
                    }
                case ConstValues.WAIT2://网络请求数据，显示工作总结
                    if (bundle != null)
                    {
                        if (bundle.getSerializable("info") != null)
                        {
                            MstWorksummaryInfo info = (MstWorksummaryInfo) bundle.getSerializable("info");
                            summaryId = info.getWskey();
                            summaryEt.setText(info.getWscontent());
                            remarkEt.setText(info.getRemarks());
                        }
                        else
                        {
                            summaryId = FunUtil.getUUID();
                            summaryEt.setText("");
                            remarkEt.setText("");
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.operation_working_week, null);
        view.setOnClickListener(null);
        initView(view);
        initData();

        return view;
    }

    /**
     * 初始化界面组件
     */
    private void initView(View view)
    {

        // 绑定界面组件
        backBt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
        titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
        weekBt = (Button) view.findViewById(R.id.weekwork_bt_week);
        searchBt = (Button) view.findViewById(R.id.weekwork_bt_search);
        tagRg = (RadioGroup) view.findViewById(R.id.weekwork_rg_tag);
        workReportLl = (LinearLayout) view.findViewById(R.id.weekwork_ll_work);
        remarkLl = (LinearLayout) view.findViewById(R.id.weekwork_ll_remark);
        summaryLl = (LinearLayout) view.findViewById(R.id.weekwork_ll_summary);
        workReportWv = (WebView) view.findViewById(R.id.weekwork_web_workreport);
        remarkDateTv = (TextView) view.findViewById(R.id.weekwork_tv_remarkdate);
        remarkEt = (EditText) view.findViewById(R.id.weekwork_et_remark);
        summaryDateTv = (TextView) view.findViewById(R.id.weekwork_tv_summarydate);
        summaryEt = (EditText) view.findViewById(R.id.weekwork_et_summary);
        submitBt = (Button) view.findViewById(R.id.weekwork_bt_submit);
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
        // 绑定事件
        backBt.setOnClickListener(this);
        weekBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        submitBt.setOnClickListener(this);
        tagRg.setOnCheckedChangeListener(this);

        // 实例化WebView查询等待dialog
        dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        View progressView = getActivity().getLayoutInflater().inflate(R.layout.login_progress, null);
        dialog.setView(progressView, 0, 0, 0, 0);
        TextView tv = (TextView) progressView.findViewById(R.id.textView1);
        tv.setText(getString(R.string.progressing));
        //        dialog.show();
    }

    private void initData()
    {

        service = new WorkingService(getActivity(), handler);
        titleTv.setText(R.string.weekwork_title);

        // 获取当前周
        Date currDate = new Date();
        weekBt.setText(DateUtil.formatDate(currDate, "yyyy年MM月") + DateUtil.getCurrentWeekOfMonth(1));
        searchDate = currDate;

        // 查询当前时间的状态
        //        this.search();
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {

        // 界面返回按钮
        case R.id.banner_navigation_rl_back:
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
                if (CheckUtil.isBlankOrNull(searchWeek))
                {
                    searchWeek = DateUtil.formatDate(searchDate, "yyyyMMdd");
                }
                String ss = summaryEt.getText().toString();
                if(ss==null || ss.equals("")){
                    //Toast.makeText(getActivity(), R.string.weekwork_msg_summary, 1).show();
                    ViewUtil.sendMsg(getActivity(), R.string.weekwork_msg_summary);
                    return;
                }else {
                    
                    service.sumbitSummary(summaryId, CalculationDate(searchWeek), ss, ConstValues.FLAG_1);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int rbId)
    {

        switch (rbId)
        {
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

    private void datePicker()
    {

        // 日历控件显示的时间
        String date = DateUtil.formatDate(searchDate, "yyyyMMdd");
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6)) - 1;
        int day = Integer.parseInt(date.substring(6, 8));

        // 时间择取器事件监听
        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                searchDate = calendar.getTime();
                weekBt.setText(DateUtil.formatDate(searchDate, "yyyy年MM月") + DateUtil.getCurrentWeekOfMonth(searchDate, 1));
            }
        }, year, month, day).show();
    }

    //计算当天时间所在的一周时间
    public static String CalculationDate(String date)
    {
        StringBuffer returnDate = new StringBuffer();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1, Integer.parseInt(date.substring(6, 8)));
        int number = calendar.get(Calendar.DAY_OF_WEEK);//获取当前日期是这周的第几天
        try
        {
            calendar.setTime(df.parse(date));
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_WEEK, 1 - number);
        returnDate.append(df.format(calendar.getTime()));
        returnDate.append("-");
        try
        {
            calendar.setTime(df.parse(date));
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_WEEK, 7 - number);
        
        returnDate.append(df.format(calendar.getTime()));
        return returnDate.toString();
    }

    /**
     * 查找
     */
    private void search()
    {
    	//formatDate--格式化日期      getCurrentWeekOfMonth--得到月的周份
        String searchDateStr = DateUtil.formatDate(searchDate, "yyyy年MM月") + DateUtil.getCurrentWeekOfMonth(searchDate, 1);
        remarkDateTv.setText(searchDateStr);
        summaryDateTv.setText(searchDateStr);

        searchWeek = DateUtil.formatDate(searchDate, "yyyyMMdd");
        //查找工作总结
        service.findSummaryInfo(CalculationDate(searchWeek), ConstValues.FLAG_1);

        // 加载周工作推进报表内容
        StringBuffer urlBuffer = new StringBuffer();
        
        /* 2.3.9(周工作总结) 正式
        urlBuffer.append(PropertiesUtil.getProperties("platform_web"));
        urlBuffer.append("bs/business/forms/BusinessFormsPad!employeeWeekWorkPad.do");
        urlBuffer.append("?model.businessFormsStc.visitDate=").append(searchWeek);
        urlBuffer.append("&model.businessFormsStc.gridId=");
        urlBuffer.append(ConstValues.loginSession.getGridId());
        */
        ///*// 2.3.9.1(周工作总结) 测试 已改好
        urlBuffer.append(PropertiesUtil.getProperties("platform_web"));
        //urlBuffer.append("http://192.168.2.71:8080/FSA_WEB_2/");
        urlBuffer.append("bs/business/forms/BusinessFormsPad!weekSummaryReportPad.do");
        urlBuffer.append("?padmodelWeekSummary.weekSummaryStc.visitDate=").append(searchWeek);
        urlBuffer.append("&padmodelWeekSummary.weekSummaryStc.gridId=");
        //urlBuffer.append(ConstValues.loginSession.getGridId());
        urlBuffer.append(PrefUtils.getString(getActivity(), "gridId", ""));
        urlBuffer.append("&padmodelWeekSummary.operationParam=Padquery");
        urlBuffer.append("&padmodelWeekSummary.weekSummaryStc.checked=BFZDJS,ZDYWTJ,SDHTJ,SDHHG,ZDHZTJWH,CXHDTJ,WJJP,SCZBBZ,SCZBSD,PCFX,DDDC,");
        //*/
        
        workReportWv.loadUrl(urlBuffer.toString());
        workReportWv.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                // 加载周工作推进报表内容
                if (tagRg.getCheckedRadioButtonId() == R.id.weekwork_rb_work)
                {
                    dialog.show();
                }

                DbtLog.d("webview", "onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                DbtLog.d("webview", "onPageFinished");
                super.onPageFinished(view, url);
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        });
    }
}
