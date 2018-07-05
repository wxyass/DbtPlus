package et.tsingtaopad.operation.working;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
 * 功能描述: 日工作推进界面</br> 
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
public class DayWorkingFragment extends BaseFragmentSupport implements OnClickListener, OnCheckedChangeListener
{

    private WorkingService service;

    private Button backBt;
    private TextView titleTv;
    private TextView weekTv;
    private Button weekBt;
    private TextView dayPrevTv;
    private Button dayPrevBt;
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

    private Date currDate;
    private Date prevDate;
    private String summaryId;
    private String searchWeek;
    
    private CheckBox bfzdjs, zdywtj, sdhtj, sdhhg,zdhztjwh,cxhdtj,wjjp,sczbbz,sczbsd,jpsczb,dddc,zdkc;
    // 声明一个集合
 	private List<CheckBox> checkBoxs = new ArrayList<CheckBox>();

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
        View view = inflater.inflate(R.layout.operation_working_day, null);
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
        weekTv = (TextView) view.findViewById(R.id.weekwork_tv_week);
        weekBt = (Button) view.findViewById(R.id.weekwork_bt_week);
        dayPrevTv = (TextView) view.findViewById(R.id.weekwork_tv_day_prev);
        dayPrevBt = (Button) view.findViewById(R.id.weekwork_bt_day_prev);
        searchBt = (Button) view.findViewById(R.id.weekwork_bt_search);
        tagRg = (RadioGroup) view.findViewById(R.id.weekwork_rg_tag);
        workReportLl = (LinearLayout) view.findViewById(R.id.weekwork_ll_work);
        remarkLl = (LinearLayout) view.findViewById(R.id.weekwork_ll_remark);
        summaryLl = (LinearLayout) view.findViewById(R.id.weekwork_ll_summary);
        workReportWv = (WebView) view.findViewById(R.id.weekwork_web_workreport);
        remarkDateTv = (TextView) view.findViewById(R.id.weekwork_tv_remarkdate);
        remarkEt = (EditText) view.findViewById(R.id.weekwork_et_remark);//工作点评
        summaryDateTv = (TextView) view.findViewById(R.id.weekwork_tv_summarydate);
        summaryEt = (EditText) view.findViewById(R.id.weekwork_et_summary);//工作总结内容
        submitBt = (Button) view.findViewById(R.id.weekwork_bt_submit);
        
        bfzdjs = (CheckBox) view.findViewById(R.id.owd_bfzdjs);
        zdywtj = (CheckBox) view.findViewById(R.id.owd_zdywtj);
        sdhtj = (CheckBox) view.findViewById(R.id.owd_sdhtj);
        sdhhg = (CheckBox) view.findViewById(R.id.owd_sdhhg);
        
        zdhztjwh = (CheckBox) view.findViewById(R.id.owd_zdhztjwh);
        cxhdtj = (CheckBox) view.findViewById(R.id.owd_cxhdtj);
        sczbsd = (CheckBox) view.findViewById(R.id.owd_sczbsd);
        sczbbz = (CheckBox) view.findViewById(R.id.owd_sczbbz);
        
        wjjp = (CheckBox) view.findViewById(R.id.owd_wjjp);
        dddc = (CheckBox) view.findViewById(R.id.owd_dddc);
        jpsczb = (CheckBox) view.findViewById(R.id.owd_jpsczb);// 竞品市场指标
        zdkc = (CheckBox) view.findViewById(R.id.owd_zdkc);// 终端库存

        
        // 注册事件
        bfzdjs.setOnCheckedChangeListener(listener);
        zdywtj.setOnCheckedChangeListener(listener);
        sdhtj.setOnCheckedChangeListener(listener);
        sdhhg.setOnCheckedChangeListener(listener);
        
        zdhztjwh.setOnCheckedChangeListener(listener);
        cxhdtj.setOnCheckedChangeListener(listener);
        sczbsd.setOnCheckedChangeListener(listener);
        sczbbz.setOnCheckedChangeListener(listener);
        
        wjjp.setOnCheckedChangeListener(listener);
        dddc.setOnCheckedChangeListener(listener);
        jpsczb.setOnCheckedChangeListener(listener);
        zdkc.setOnCheckedChangeListener(listener);
 		// 把四个组件添加到集合中去
 		checkBoxs.add(bfzdjs);
 		checkBoxs.add(zdywtj);
 		checkBoxs.add(sdhtj);
 		checkBoxs.add(sdhhg);
 		checkBoxs.add(zdhztjwh);
 		checkBoxs.add(cxhdtj);
 		checkBoxs.add(sczbsd);
 		checkBoxs.add(sczbbz);
 		checkBoxs.add(wjjp);
 		checkBoxs.add(dddc);
 		checkBoxs.add(jpsczb);
 		checkBoxs.add(zdkc);

		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
        // 绑定事件
        backBt.setOnClickListener(this);
        weekBt.setOnClickListener(this);
        dayPrevBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        submitBt.setOnClickListener(this);
        tagRg.setOnCheckedChangeListener(this);

        // 实例化WebView查询等待dialog
        dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);//以上两句是控制dialog在访问网络超时时，按返回键可消失dialog
        View progressView = getActivity().getLayoutInflater().inflate(R.layout.login_progress, null);
        dialog.setView(progressView, 0, 0, 0, 0);
        TextView tv = (TextView) progressView.findViewById(R.id.textView1);
        tv.setText(getString(R.string.progressing));
    }
    
    // checkbox的点击监听
 	CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

 		@Override
 		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 			CheckBox box = (CheckBox) buttonView;
 			//Toast.makeText(getActivity(), "获取的值:" + box.getText(),0).show();
 		}
 	};

    private void initData()
    {
        service = new WorkingService(getActivity(), handler);
        titleTv.setText(R.string.daywork_title);
        weekTv.setText(R.string.daywork_day);
        dayPrevTv.setVisibility(View.VISIBLE);
        dayPrevTv.setText(R.string.daywork_day_prev);
        dayPrevBt.setVisibility(View.VISIBLE);
        //tagRg.getChildAt(0).setBackgroundResource(R.drawable.bg_workday_work); //给第一个按钮设置图片

        // 获取当前日期
        currDate = new Date();
        prevDate = DateUtil.addDays(currDate, -1);//上一工作日期
        weekBt.setText(DateUtil.formatDate(currDate, "yyyy-MM-dd"));
        dayPrevBt.setText(DateUtil.formatDate(prevDate, "yyyy-MM-dd"));

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

            // 选择今天
            case R.id.weekwork_bt_week:
                this.datePicker(R.id.weekwork_bt_week);
                break;

            // 选择上一个工作日
            case R.id.weekwork_bt_day_prev:
                this.datePicker(R.id.weekwork_bt_day_prev);
                break;

            // 查询
            case R.id.weekwork_bt_search:
                this.search();
                break;

            // 提交
            case R.id.weekwork_bt_submit:
                if (CheckUtil.isBlankOrNull(searchWeek))
                {
                    searchWeek = DateUtil.formatDate(prevDate, "yyyy-MM-dd");
                }
                
                String str = summaryEt.getText().toString();
                if(str==null || str.equals("")){
                    ViewUtil.sendMsg(getActivity(), R.string.daywork_msg_summary);
                    return;
                }else {
                    
                    service.sumbitSummary(summaryId, searchWeek, str, ConstValues.FLAG_0);
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

    private void datePicker(final int btId)
    {

        // 日历控件显示的时间
        String date = "";
        if (btId == R.id.weekwork_bt_week)
        {
            date = DateUtil.formatDate(currDate, "yyyyMMdd");
        }
        else
        {
            date = DateUtil.formatDate(prevDate, "yyyyMMdd");
        }
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6)) - 1;
        int day = Integer.parseInt(date.substring(6, 8));

        // 时间择取器事件监听
        new DatePickerDialog(getActivity(), R.style.dialog_date,new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                String selectDate = DateUtil.formatDate(calendar.getTime(), "yyyy-MM-dd");
                if (btId == R.id.weekwork_bt_week)//当天
                {
                    if (selectDate.compareTo(dayPrevBt.getText().toString()) < 0)
                    {
                        ViewUtil.sendMsg(getActivity(), R.string.daywork_msg_currdate);
                    }
                    else
                    {
                        currDate = calendar.getTime();
                        weekBt.setText(selectDate);
                    }
                }
                else
                {
                    if (selectDate.compareTo(weekBt.getText().toString()) > 0)//前一个工作日
                    {
                        ViewUtil.sendMsg(getActivity(), R.string.daywork_msg_prevdate);
                    }
                    else
                    {
                        prevDate = calendar.getTime();
                        searchWeek = DateUtil.formatDate(prevDate, "yyyy-MM-dd");
                        dayPrevBt.setText(searchWeek);
                        remarkDateTv.setText(searchWeek);
                        remarkEt.setText("");
                        summaryDateTv.setText(searchWeek);
                        summaryEt.setText("");
                        //service.findSummaryInfo(searchWeek.replace("-", ""), ConstValues.FLAG_0);
                    }
                }
            }
        }, year, month, day).show();
    }

    /**
     * 查找
     */
    private void search()
    {
        searchWeek = DateUtil.formatDate(prevDate, "yyyy-MM-dd");
        remarkDateTv.setText(searchWeek);
        summaryDateTv.setText(searchWeek);
        service.findSummaryInfo(searchWeek.replace("-", ""), ConstValues.FLAG_0);
        StringBuffer urlBuffer = new StringBuffer();
        
        /* 2.3.9(日工作推进) 正式
        urlBuffer.append(PropertiesUtil.getProperties("platform_web"));
        urlBuffer.append("bs/business/forms/BusinessFormsPad!employeeDayWrokPad.do");
        urlBuffer.append("?model.businessFormsStc.visitDate=").append(weekBt.getText().toString());
        System.out.println("WEEKBT"+weekBt.getText().toString());
        urlBuffer.append("&model.businessFormsStc.strDate=").append(dayPrevBt.getText().toString());
        System.out.println("DAYBT"+dayPrevBt.getText().toString());
        urlBuffer.append("&model.businessFormsStc.gridId=");
        urlBuffer.append(ConstValues.loginSession.getGridId());
        */
        
        // 2.3.9.1(日工作推进) 测试 已改好
        urlBuffer.append(PropertiesUtil.getProperties("platform_web"));
        //urlBuffer.append("http://192.168.2.71:8080/FSA_WEB_2/");
        urlBuffer.append("bs/business/forms/BusinessFormsPad!dailyWorkPromotionPad.do");
        urlBuffer.append("?padModel.dailyWorkPromotionStc.visitDate=").append(weekBt.getText().toString());
        System.out.println("WEEKBT"+weekBt.getText().toString());
        urlBuffer.append("&padModel.dailyWorkPromotionStc.strDate=").append(dayPrevBt.getText().toString());
        System.out.println("DAYBT"+dayPrevBt.getText().toString());
        urlBuffer.append("&padModel.dailyWorkPromotionStc.gridId=");
        //urlBuffer.append(ConstValues.loginSession.getGridId());
        urlBuffer.append(PrefUtils.getString(getActivity(), "gridId", ""));
        urlBuffer.append("&padModel.operationParam=Padquery&");
        //urlBuffer.append("padModel.dailyWorkPromotionStc.checked=BFZDJS,ZDYWTJ,SDHTJ,SDHHG,ZDHZTJWH,CXHDTJ,WJJP,SCZBBZ,SCZBSD,PCFX,DDDC,");
        StringBuffer activity = new StringBuffer();
        for (CheckBox cbx : checkBoxs) {
			if (cbx.isChecked()) {// 该项若被选中
				if("终端拜访管理".equals(cbx.getText())){
					activity.append("BFZDJS"+",");
				}else if("终端业务推进".equals(cbx.getText())){
					activity.append("ZDYWTJ"+",");
				}
				else if("生动化推进".equals(cbx.getText())){
					activity.append("SDHTJ"+",");
				}
				else if("生动化合格".equals(cbx.getText())){
					activity.append("SDHHG"+",");
				}
				else if("终端合作推进维护".equals(cbx.getText())){
					activity.append("ZDHZTJWH"+",");
				}
				else if("促销活动推进".equals(cbx.getText())){
					activity.append("CXHDTJ"+",");
				}
				else if("瓦解竞品".equals(cbx.getText())){
					activity.append("WJJP"+",");
				}
				else if("我品市场指标".equals(cbx.getText())){
					activity.append("SCZBBZ"+",");
				}
				else if("市场指标山东".equals(cbx.getText())){
					activity.append("SCZBSD"+",");
				}
				else if("订单达成".equals(cbx.getText())){
					activity.append("DDDC"+",");
				}
				else if("竞品市场指标".equals(cbx.getText())){
					activity.append("JPSCZB"+",");
				}
				else if("终端库存".equals(cbx.getText())){
					activity.append("ZDKC"+",");
				}
			}
		}
        urlBuffer.append("padModel.dailyWorkPromotionStc.checked=");
        urlBuffer.append(activity.toString());
        
        
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
