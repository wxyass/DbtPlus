package et.tsingtaopad.operation.workplan;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstPlanWeekforuserM;
import et.tsingtaopad.db.tables.MstPlanforuserM;
import et.tsingtaopad.operation.workplan.domain.WorkPlanStc;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.visit.syncdata.UploadDataService;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：workplanFragment.java</br> 
 * 作者：@ray </br>
 * 创建时间：2013-11-29</br> 
 * 功能描述: 工作计划界面</br> 
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class WorkPlanFragment extends BaseFragmentSupport implements OnClickListener
{

    private final String TAG = "WorkplanFragment";

    private TextView tv_title;
    private TextView workplan_tv_rounds;
    private TextView tv_plantitle;
    private Button btn_time;
    private Button btn_back;
    private ListView listView;
    private String time;
    private String aday;
    private Calendar calendar;
    private int yearr;
    private int month;
    private int day;
    private WorkPlanAdapter adapter;
    private UploadDataService upservice;
    private DatabaseHelper helper;
    List<WorkPlanStc> workPlanStcs = new ArrayList<WorkPlanStc>();
    private int onsubmitClickPosition = -1;
    public static final int UPLOADSUCCESS = 0;
    public static final int WeekUPLOADSUCCESS = 100;
    private Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {

            Bundle bundle = msg.getData();

            super.handleMessage(msg);
            switch (msg.what)
            {

            // 提示信息
                case ConstValues.WAIT1:
                    Toast.makeText(getActivity(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    break;
                case UPLOADSUCCESS:
                    // 上传成功会修改数据库修改状态为4 所以不再此处修改数据库的状态
                    if (onsubmitClickPosition != -1)
                    {
                        WorkPlanStc workPlanStc = workPlanStcs.get(onsubmitClickPosition);
                        workPlanStc.setState(4);
                        adapter.notifyDataSetChanged();
                    }

                    break;
                case WeekUPLOADSUCCESS:
                    initWeekDataView();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.operation_workplan, null);
        view.setOnClickListener(null);
        // 获取系统时间

        calendar = Calendar.getInstance();
        yearr = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        initView(view);
        initData();
        initWeekDataView();
        initViewData();
        return view;
    }

    String weekDateStart = "";
    String weekDateEnd = "";
    MstPlanWeekforuserM weekPlan;

    private void initWeekDataView()
    {
        weekPlan = new MstPlanWeekforuserM();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateFirst = null;
        Date dateLast = null;
        if (StringUtils.isBlank(weekDateStart) || StringUtils.isBlank(weekDateEnd))
        {
            return;
        }
        try
        {
            dateFirst = sdf.parse(weekDateStart);
            dateLast = sdf.parse(weekDateEnd);
            //周计划
            Dao<MstPlanWeekforuserM, String> dao = helper.getMstPlanWeekforuserMDao();
            //            weekPlan = dao.queryForId(weekDateStart + weekDateEnd);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("startdate", weekDateStart);
            map.put("enddate", weekDateEnd);
            List<MstPlanWeekforuserM> planList = dao.queryForFieldValues(map);
            if (planList.size() > 0)
            {
                weekPlan = planList.get(0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        //        TextView week_tv_state = (TextView) view.findViewById(R.id.workplan_tv_state);//状态，未制定，审核通过等在这里操作
        //        week_tv_state.setText("周工作计划汇总");
        //        LinearLayout ll_c = (LinearLayout) view.findViewById(R.id.workplan_ll_c);//预览，制定，修改，提交都在这栏进行
        //        ll_c.setVisibility(View.VISIBLE);
        //        view.findViewById(R.id.workplan_im_state).setVisibility(View.GONE);// 周工作计划汇总item的状态隐藏

        String weekPlanTime1 = DateUtil.formatDate(dateFirst, "M月dd日") + "~" + DateUtil.formatDate(dateLast, "M月dd日");
        final String weekPlanTime = DateUtil.formatDate(dateFirst, "yyyy年M月dd日") + "~" + DateUtil.formatDate(dateLast, "yyyy年M月dd日");
        weekplan_tv_time.setText(weekPlanTime1);//给本周目标汇总时间赋值
        //
        if (weekPlan == null)
        {
            // 未制定
            im_state.setBackgroundResource(R.drawable.ico_plan_add);
            tv_state.setText(R.string.workplan_statea);
            ll_a.setVisibility(View.VISIBLE);
            ll_b.setVisibility(View.GONE);
            ll_c.setVisibility(View.GONE);
        }
        else
        {
            if ("5".equals(weekPlan.getPlanstatus()))
            {
                // 自动通过
                im_state.setBackgroundResource(R.drawable.ico_plan_right);
                tv_state.setText(R.string.workplan_auto_statef);
                ll_a.setVisibility(View.GONE);
                ll_b.setVisibility(View.GONE);
                ll_c.setVisibility(View.VISIBLE);
            }
            else if ("3".equals(weekPlan.getPlanstatus()))
            {
                // 审核通过
                im_state.setBackgroundResource(R.drawable.ico_plan_right);
                tv_state.setText(R.string.workplan_statef);
                ll_a.setVisibility(View.GONE);
                ll_b.setVisibility(View.GONE);
                ll_c.setVisibility(View.VISIBLE);
            }
            else if ("1".equals(weekPlan.getPlanstatus()))
            {
                // 审核未通过
                im_state.setBackgroundResource(R.drawable.ico_plan_wrong);
                tv_state.setText(R.string.workplan_stated);
                ll_a.setVisibility(View.GONE);
                ll_b.setVisibility(View.VISIBLE);
                ll_c.setVisibility(View.GONE);
            }
            else if ("4".equals(weekPlan.getPlanstatus()))
            {
                // 已经提交
                im_state.setBackgroundResource(R.drawable.ico_plan_updating);
                tv_state.setText(R.string.workplan_statec);
                ll_a.setVisibility(View.GONE);
                ll_b.setVisibility(View.GONE);
                ll_c.setVisibility(View.VISIBLE);
            }
            else if ("0".equals(weekPlan.getPlanstatus()))
            { // 等待提交
                im_state.setBackgroundResource(R.drawable.ico_plan_writing);
                tv_state.setText(R.string.workplan_stateb);
                ll_a.setVisibility(View.GONE);
                ll_b.setVisibility(View.VISIBLE);
                ll_c.setVisibility(View.GONE);
            }
            else if (StringUtils.isBlank(weekPlan.getPlanstatus()) || "2".equals(weekPlan.getPlanstatus()))
            {
                // 未制定
                im_state.setBackgroundResource(R.drawable.ico_plan_add);
                tv_state.setText(R.string.workplan_statea);
                ll_a.setVisibility(View.VISIBLE);
                ll_b.setVisibility(View.GONE);
                ll_c.setVisibility(View.GONE);
            }
        }
        //预览
        btn_preview.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), WeekPlanPreviewActivity.class);
                intent.putExtra("date", weekPlanTime);
                intent.putExtra("startDate", weekDateStart);
                intent.putExtra("endDate", weekDateEnd);
                intent.putExtra("weekPlan", weekPlan);
                intent.putExtra("oprateState", 0);

                startActivity(intent);
            }
        });
        // 制定
        btn_make.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), WeekPlanPreviewActivity.class);
                intent.putExtra("date", weekPlanTime);
                intent.putExtra("startDate", weekDateStart);
                intent.putExtra("endDate", weekDateEnd);
                intent.putExtra("weekPlan", weekPlan);
                intent.putExtra("oprateState", 1);

                startActivity(intent);
            }
        });
        // 修改
        btn_amend.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), WeekPlanPreviewActivity.class);
                intent.putExtra("date", weekPlanTime);
                intent.putExtra("startDate", weekDateStart);
                intent.putExtra("endDate", weekDateEnd);
                intent.putExtra("weekPlan", weekPlan);
                intent.putExtra("oprateState", 2);

                startActivity(intent);
            }
        });
        // 提交
        btn_submit.setOnClickListener(new OnClickListener()
        {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v)
            {
                upservice = new UploadDataService(getActivity(), handler);
                //                Log.e(TAG, "提交查询的 plankey = " + planKey);
                Toast.makeText(getActivity(), "上传中，请等待", Toast.LENGTH_SHORT).show();
                upservice.upload_weekwork_plans(false, weekPlan.getPlankey());
            }
        });

    }

    /**
     * 加载所有需要用到的数据
     */
    @SuppressWarnings("deprecation")
    private void initData()
    {
        helper = DatabaseHelper.getHelper(getActivity());

        try
        {
        	///MST_PLANFORUSER_M(人员计划主表)
            Dao<MstPlanforuserM, String> dao = helper.getMstPlanforuserMDao();
            workPlanStcs.clear();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.calendar.getTime());//把随意设置的时间或者是系统的时间赋值给    caleendar
            for (int i = 1; i <= 7; i++)
            {
                WorkPlanStc workPlanStc = new WorkPlanStc();
                //				Date date1= calendar.getTime();
                //int c=calendar.get(Calendar.DAY_OF_WEEK);
                calendar.add(Calendar.DAY_OF_WEEK, i - calendar.get(Calendar.DAY_OF_WEEK));
                Date date = calendar.getTime();
                String plandate = DateUtil.formatDate(date, "yyyyMMdd");
                if (i == 1)
                {
                    weekDateStart = plandate;
                }
                else if (i == 7)
                {
                    weekDateEnd = plandate;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("plandate", plandate);
                map.put("plantype", "0");
                List<MstPlanforuserM> planList = dao.queryForFieldValues(map);
                Log.e(TAG, "某天工作计划仅有一条数据:" + planList.size());
                workPlanStc.setState(2);
                workPlanStc.setPlanKey(FunUtil.getUUID());
                workPlanStc.setDate(date);
                StringBuilder builder = new StringBuilder();
                builder.append(date.getMonth() + 1).append("月").append(date.getDate()).append("日 周").append(datePreview(i));
                workPlanStc.setVisitTime(builder.toString());
                // 如有计划状态值以及plankey 取表中的数值
                if (planList.size() > 0)
                {
                    int state = Integer.parseInt(planList.get(0).getPlanstatus());
                    String plankey = planList.get(0).getPlankey();
                    workPlanStc.setState(state);
                    workPlanStc.setPlanKey(plankey);
                }
                workPlanStcs.add(workPlanStc);//把等到的     预计拜访时间，拜访开始日期，拜访结束日期，操作，状态，拜访(计划)主键，日期时间    添加到workPlanStcs中
            }
            if (adapter != null)
            {
                adapter.notifyDataSetChanged();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private String datePreview(int num)
    {
        String temp = null;
        switch (num)
        {
            case 1:
                temp = "日";

                break;
            case 2:
                temp = "一";

                break;
            case 3:
                temp = "二";

                break;
            case 4:
                temp = "三";

                break;
            case 5:
                temp = "四";

                break;
            case 6:
                temp = "五";

                break;
            case 7:
                temp = "六";
                break;

        }
        return temp;
    }

    //周工作计划
    Button btn_make;//制定按钮
    Button btn_amend;//修改
    Button btn_submit;//提交

    Button btn_preview;//预览
    TextView weekplan_tv_time;//预计拜访时间
    TextView tv_state;//提交状态
    ImageView im_state;//提交状态
    LinearLayout ll_a;//制定
    LinearLayout ll_b;//修改 、提交
    LinearLayout ll_c;//预览

    /**
     * 初始化界面组件
     */
    private void initView(View view)
    {
        // 绑定界面组件
        btn_back = (Button) view.findViewById(R.id.banner_navigation_bt_back);
        btn_time = (Button) view.findViewById(R.id.workplan_btn_time);
        tv_title = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
        tv_plantitle = (TextView) view.findViewById(R.id.workplan_tv_plantitle);
        workplan_tv_rounds = (TextView) view.findViewById(R.id.workplan_tv_rounds);//是第几周
        listView = (ListView) view.findViewById(R.id.workplan_lv_content);
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
        btn_back.setOnClickListener(this);
        btn_time.setOnClickListener(this);

        //底部周工作计划
        weekplan_tv_time = (TextView) view.findViewById(R.id.workplan_tv_time);//预计拜访时间
        btn_make = (Button) view.findViewById(R.id.workplan_btn_make);
        btn_amend = (Button) view.findViewById(R.id.workplan_btn_amend);
        btn_submit = (Button) view.findViewById(R.id.workplan_btn_submit);
        btn_preview = (Button) view.findViewById(R.id.workplan_btn_preview);
        tv_state = (TextView) view.findViewById(R.id.workplan_tv_state);
        im_state = (ImageView) view.findViewById(R.id.workplan_im_state);
        ll_a = (LinearLayout) view.findViewById(R.id.workplan_ll_a);//制定
        ll_b = (LinearLayout) view.findViewById(R.id.workplan_ll_b);//修改 、提交
        ll_c = (LinearLayout) view.findViewById(R.id.workplan_ll_c);//预览
    }

    private void setListViewHeightBasedOnChildren(ListView listView)
    {

        WorkPlanAdapter listAdapter = (WorkPlanAdapter) listView.getAdapter();

        if (listAdapter == null)
        {

            return;

        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0);

            totalHeight += listItem.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);

        listView.setLayoutParams(params);

    }

    /**
     * 初始化界面数据
     */
    private void initViewData()
    {
        tv_title.setText(getString(R.string.workplan_title));
        workplan_tv_rounds.setText(getString(R.string.workplan_msg1) + calendar.get(Calendar.WEEK_OF_MONTH) + getString(R.string.workplan_msg2));
        tv_plantitle.setText(calendar.get(Calendar.YEAR) + getString(R.string.workplan_msg3) + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.workplan_msg4) + calendar.get(Calendar.WEEK_OF_MONTH) + getString(R.string.workplan_msg5));
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = calendar.getTime();
        String dateStr = sDateFormat.format(date);
        btn_time.setText(dateStr);

        adapter = new WorkPlanAdapter(getActivity(), workPlanStcs);
        listView.setAdapter(adapter);
        //        setListViewHeightBasedOnChildren(listView);

    }

    @SuppressWarnings("deprecation")
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
            case R.id.workplan_btn_time:
                DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(),R.style.dialog_date, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        yearr = year;
                        month = monthOfYear;
                        day = dayOfMonth;
                        if (dayOfMonth < 10)
                        {
                            aday = "0" + dayOfMonth;
                        }
                        else
                        {
                            aday = Integer.toString(dayOfMonth);
                        }
                        time = (Integer.toString(year) + "-" + String.format("%02d", monthOfYear + 1) + "-" + aday);
                        btn_time.setText(time);
                        workplan_tv_rounds.setText(getString(R.string.workplan_msg1) + calendar.get(Calendar.WEEK_OF_MONTH) + getString(R.string.workplan_msg2));
                        tv_plantitle.setText(calendar.get(Calendar.YEAR) + getString(R.string.workplan_msg3) + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.workplan_msg4) + calendar.get(Calendar.WEEK_OF_MONTH) + getString(R.string.workplan_msg5));

                        initData();
                        initViewData();
                        initWeekDataView();

                    }
                }, yearr, month, day);
                if (!dateDialog.isShowing())
                {
                    dateDialog.show();
                }

                break;
        }
    }

    @Override
    public void onResume()
    {
        initData();
        initWeekDataView();

        super.onResume();
        Log.i(TAG, "onresume");
    }

    class WorkPlanAdapter extends BaseAdapter
    {
        private Context context;
        private Boolean flag = true;
        private List<WorkPlanStc> workPlanStcs;
        LayoutInflater inflater;

        public WorkPlanAdapter(Context context, List<WorkPlanStc> workPlanStcs)
        {
            this.context = context;
            this.workPlanStcs = workPlanStcs;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount()
        {
            return workPlanStcs.size();
        }

        @Override
        public Object getItem(int position)
        {
            return workPlanStcs.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        ViewHolder viewHolder = null;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {

            // 查询人员主表 取得计划状态值 以及plankey
            final WorkPlanStc workPlanStc = workPlanStcs.get(position);
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.operation_workplan_lvitem, null);
                viewHolder = new ViewHolder();
                viewHolder.btn_make = (Button) convertView.findViewById(R.id.workplan_btn_make);
                viewHolder.btn_amend = (Button) convertView.findViewById(R.id.workplan_btn_amend);
                viewHolder.btn_submit = (Button) convertView.findViewById(R.id.workplan_btn_submit);
                viewHolder.btn_preview = (Button) convertView.findViewById(R.id.workplan_btn_preview);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.workplan_tv_time);
                viewHolder.tv_state = (TextView) convertView.findViewById(R.id.workplan_tv_state);
                viewHolder.im_state = (ImageView) convertView.findViewById(R.id.workplan_im_state);
                viewHolder.ll_a = (LinearLayout) convertView.findViewById(R.id.workplan_ll_a);//制定
                viewHolder.ll_b = (LinearLayout) convertView.findViewById(R.id.workplan_ll_b);//修改 、提交
                viewHolder.ll_c = (LinearLayout) convertView.findViewById(R.id.workplan_ll_c);//预览
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final String planKey = workPlanStc.getPlanKey();
            viewHolder.tv_time.setText(workPlanStc.getVisitTime());
            // 制定
            viewHolder.btn_make.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    Intent intent = new Intent(context, MakePlanActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("date", (new SimpleDateFormat("yyyy-MM-dd").format(workPlanStc.getDate()) + ""));// 计划日期
                    String weekStartDate = DateUtil.formatDate(workPlanStcs.get(0).getDate(), "yyyyMMdd");
                    String weekEndDate = DateUtil.formatDate(workPlanStcs.get(6).getDate(), "yyyyMMdd");
                    bundle.putString("weekStartDate", weekStartDate);
                    bundle.putString("weekEndDate", weekEndDate);
                    bundle.putInt("position", position);
                    bundle.putString("plankey", planKey);
                    bundle.putBoolean("make_or_modify", true);
                    intent.putExtras(bundle);
                    ((FragmentActivity) context).startActivityForResult(intent, 0);
                }
            });
            // 修改
            viewHolder.btn_amend.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, MakePlanActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("date", (new SimpleDateFormat("yyyy-MM-dd").format(workPlanStc.getDate()) + ""));
                    bundle.putInt("position", position);
                    bundle.putString("plankey", planKey);
                    String weekStartDate = DateUtil.formatDate(workPlanStcs.get(0).getDate(), "yyyyMMdd");
                    String weekEndDate = DateUtil.formatDate(workPlanStcs.get(6).getDate(), "yyyyMMdd");
                    bundle.putString("weekStartDate", weekStartDate);
                    bundle.putString("weekEndDate", weekEndDate);
                    bundle.putBoolean("make_or_modify", false);
                    intent.putExtras(bundle);
                    ((FragmentActivity) context).startActivityForResult(intent, 0);

                }
            });
            // 提交
            // final View view = convertView;
            viewHolder.btn_submit.setOnClickListener(new OnClickListener()
            {

                @SuppressLint("ShowToast")
                @Override
                public void onClick(View v)
                {
                    onsubmitClickPosition = position;
                    upservice = new UploadDataService(context, handler);
                    Log.e(TAG, "提交查询的 plankey = " + planKey);
                    Toast.makeText(getActivity(), "上传中，请等待", Toast.LENGTH_SHORT).show();
                    upservice.upload_work_plans(false, planKey);
                }
            });
            // 预览
            viewHolder.btn_preview.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, MakePlanActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("date", (new SimpleDateFormat("yyyy-MM-dd").format(workPlanStc.getDate()) + ""));
                    bundle.putInt("position", position);
                    bundle.putString("plankey", planKey);
                    bundle.putBoolean("make_or_modify", false);
                    bundle.putBoolean("preview_plan", true);
                    intent.putExtras(bundle);
                    ((FragmentActivity) context).startActivityForResult(intent, 0);
                }

            });

            // 0未审核 1 未通过 3 通过 2 未制定 4已提交
            if (flag)
            {
                switch (workPlanStc.getState())
                {
                    case 2:
                        // 未制定
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_add);
                        viewHolder.tv_state.setText(R.string.workplan_statea);
                        viewHolder.ll_a.setVisibility(View.VISIBLE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.GONE);
                        break;
                    case 0:
                        // 等待提交
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_writing);
                        viewHolder.tv_state.setText(R.string.workplan_stateb);
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.VISIBLE);
                        viewHolder.ll_c.setVisibility(View.GONE);
                        break;
                    case 4:
                        // 已经提交
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_updating);
                        viewHolder.tv_state.setText(R.string.workplan_statec);
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        // 审核未通过
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_wrong);
                        viewHolder.tv_state.setText(R.string.workplan_stated);
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.VISIBLE);
                        viewHolder.ll_c.setVisibility(View.GONE);
                        break;
                    case 3:
                        // 审核通过
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_right);
                        viewHolder.tv_state.setText(R.string.workplan_statef);
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        // 自动通过
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_right);
                        viewHolder.tv_state.setText(R.string.workplan_auto_statef);
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);
                        break;

                }
            }

            return convertView;
        }

        class ViewHolder
        {
            Button btn_make;
            Button btn_amend;
            Button btn_submit;
            Button btn_preview;
            TextView tv_time;
            TextView tv_state;
            ImageView im_state;
            LinearLayout ll_a;
            LinearLayout ll_b;
            LinearLayout ll_c;

        }

    }
}
