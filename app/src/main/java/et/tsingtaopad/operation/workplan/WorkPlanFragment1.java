package et.tsingtaopad.operation.workplan;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import et.tsingtaopad.db.tables.PadPlantempcheckM;
import et.tsingtaopad.operation.workplan.domain.WeekPlanShowStc;
import et.tsingtaopad.operation.workplan.domain.WorkPlanStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
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
public class WorkPlanFragment1 extends BaseFragmentSupport implements OnClickListener
{

    private final String TAG = "WorkplanFragment1";

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
    public static final int ALLDAYUPLOADSUCCESS = 1001;
    
    // 制定周计划
    //private final static String TAG = "WeekPlanPreviewActivity";
	private LinearLayout parentContentLayout;
	private ProgressDialog pd;
	//private DatabaseHelper helper;
	private List<PadPlantempcheckM> plantempcheckMs;
	private List<PadPlantempcheckM> plantempcheckMes;// 未排序的计划指标
	private Dao<PadPlantempcheckM, String> padPlantempcheckMDao;
	private String startDate;
	private String endDate;
	private String date;
	private Context context;
	private LayoutInflater inflater;
	private LayoutInflater inflater1;
	private TextView preview_plan_no_data_promotion;
	WebView webView;
	TextView subBtn;
	//MstPlanWeekforuserM weekPlan;
	EditText remarksView;
	int oprateState;// 0:预览，1：制定，2：修改  周计划制定
	
	private String weekPlanTime;

	private TextView makeplan_tv_time;
	private AlertDialog dialog;
    
	String weekDateStart = "";
    String weekDateEnd = "";
    MstPlanWeekforuserM weekPlan;
    
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

	private String weekPlanstatusFlag;// 周计划制定的状态

	private ImageView week_im_state;
	private TextView week_tv_state;
	private TextView week_tv_time;

	private String weekPlanTime1;
	private String weekPlanTime2;
    
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
                	initData();
                	initViewData();
                    initWeekDataView();
                    initWeekPlanMake();
                    break;
                case ALLDAYUPLOADSUCCESS:
                	initData();
                	initViewData();
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
        View view = inflater.inflate(R.layout.operation_workplan1, null);
        view.setOnClickListener(null);
        // 获取系统时间

        calendar = Calendar.getInstance();
        yearr = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        
        // 初始化控件
        initView(view);
        
        // 获取工作计划上半部分 控件的数据(并未往控件上赋值,只是得到数据,存放在list中)
        initData();
        
        // 周计划数据获取以及显示
        initWeekDataView();//3
        
        // 初始化控件数据(将数据绑在控件上,将获取的数据绑在日计划list上)
        initViewData();//WorkPlanAdapter 设置日计划制定
        
        // 将周计划制定,放在本页面上,并出现确定按钮,提交周计划
        initWeekView(view);
        initWeekPlanMake();
        
        return view;
    }

    /**
     * 初始化界面组件
     */
    private void initView(View view)
    {
        // 绑定界面组件
        btn_back = (Button) view.findViewById(R.id.banner_navigation_bt_back);
        // 确定按钮
     	subBtn = (TextView) view.findViewById(R.id.banner_navigation_bt_confirm);
     	//subBtn.setVisibility(View.VISIBLE);
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
        //subBtn.setOnClickListener(this);
        btn_time.setOnClickListener(this);

        //底部周工作计划(原)
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
        
        // 顶部工作计划 状态
        
        week_tv_time = (TextView)view.findViewById(R.id.workplan_tv_time1);
        week_im_state = (ImageView)view.findViewById(R.id.workplan_im_state1);
        week_tv_state = (TextView)view.findViewById(R.id.workplan_tv_state1);
    }

    /**
     * 加载日计划(一周内的)的数据
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
            // 初始化本周7天计划(即在MstPlanforuserM表中生成7条记录),若这周7条记录存在过则复用这7条记录
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
                	// 获取本周的开始时间(定义为全局)
                    weekDateStart = plandate;
                }
                else if (i == 7)
                {
                	// 获取本周的结束时间(定义为全局)
                    weekDateEnd = plandate;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("plandate", plandate);
                map.put("plantype", "0");
                // 查询MstPlanforuserM表中是否有plandate这天的计划
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
                
                //把等到的     预计拜访时间，拜访开始日期，拜访结束日期，操作，状态，拜访(计划)主键，日期时间    添加到workPlanStcs中
                workPlanStcs.add(workPlanStc);//把等到的     预计拜访时间，拜访开始日期，拜访结束日期，操作，状态，拜访(计划)主键，日期时间    添加到workPlanStcs中
            }
            
            //↓---根据查看周计划,修改日计划的制定,修改,预览按钮显示 // ywm 20160412-------------------------------------
            Dao<MstPlanWeekforuserM, String> dao2 = helper.getMstPlanWeekforuserMDao();
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("startdate", weekDateStart);
            map2.put("enddate", weekDateEnd);
            // 查看周计划
            List<MstPlanWeekforuserM> WeekplanList2 = dao2.queryForFieldValues(map2);
            
            // 如果有本周计划并且状态为 审核通过3,已提交4,自动审核通过5,等待提交0
            if (WeekplanList2.size() > 0 && ("3".equals(WeekplanList2.get(0).getPlanstatus())||
            		"4".equals(WeekplanList2.get(0).getPlanstatus())||
            		"0".equals(WeekplanList2.get(0).getPlanstatus())||
            		"5".equals(WeekplanList2.get(0).getPlanstatus()))){
            	// 将本周各个日计划中 状态为2(未制定)设为6(按钮消失)
            	for (WorkPlanStc mstPlanforuserM : workPlanStcs) {
            		if(mstPlanforuserM.getState()==2){
            			mstPlanforuserM.setState(6);// 日计划的按钮消失
            		}else if(mstPlanforuserM.getState()==0){// 此时的条件: 周计划345,日计划0
            			mstPlanforuserM.setState(5);// 预览
            		}
				}
            // 如果本周计划审核未通过,修改本周各个日计划状态
            }else if(WeekplanList2.size() > 0 && ("1".equals(WeekplanList2.get(0).getPlanstatus()))){
            	for (WorkPlanStc mstPlanforuserM : workPlanStcs) {
            		// 如果日计划制定过4(已提交),则改日计划为审核未通过
            		if(4==mstPlanforuserM.getState()||3==mstPlanforuserM.getState()||5==mstPlanforuserM.getState()){
            			mstPlanforuserM.setState(1);// 修改
            		}
				}
            }
            //↑---根据查看周计划,修改日计划的制定,修改,预览按 // ywm 20160412-------------------------------------
            
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
    
    /**
     * 周计划状态 获取以及显示
     */
    private void initWeekDataView()//3
    {
    	//
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
                //oprateState = 2;// 修改周计划
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

        // 时间格式转换(如:5月22日~5月28日)
        weekPlanTime1 = DateUtil.formatDate(dateFirst, "M月dd日") + "~" + DateUtil.formatDate(dateLast, "M月dd日");
        weekPlanTime = DateUtil.formatDate(dateFirst, "yyyy年M月dd日") + "~" + DateUtil.formatDate(dateLast, "yyyy年M月dd日");
        //weekPlanTime2 = DateUtil.formatDate(dateFirst, "yyyy/M/dd") + "~" + DateUtil.formatDate(dateLast, "yyyy/M/dd");
        weekPlanTime2 = DateUtil.formatDate(dateFirst, "yyyyMMdd") + "~" + DateUtil.formatDate(dateLast, "yyyyMMdd");
        weekplan_tv_time.setText(weekPlanTime1);//给本周目标汇总时间赋值
        week_tv_time.setText(weekPlanTime1);// 给顶部周计划状态设置时间
        //
        if (weekPlan.getPlankey() == null)
        {
            // 未制定
            im_state.setBackgroundResource(R.drawable.ico_plan_add);
            tv_state.setText(R.string.workplan_statea);
            ll_a.setVisibility(View.VISIBLE);// 制定
            ll_b.setVisibility(View.GONE);// 修改 提交
            ll_c.setVisibility(View.GONE);// 预览
            
            oprateState = 1;// 制定周计划
            week_im_state.setBackgroundResource(R.drawable.ico_plan_add);
            week_tv_state.setText(R.string.workplan_statea);
            subBtn.setVisibility(View.VISIBLE);
        }
        else
        {
        	weekPlanstatusFlag = weekPlan.getPlanstatus();
        	oprateState = 2; // 修改周计划
        	// 根据已制定周计划的状态(Planstatus) 修改页面按钮显示
            if ("5".equals(weekPlan.getPlanstatus()))
            {
                // 自动通过
                im_state.setBackgroundResource(R.drawable.ico_plan_right);
                tv_state.setText(R.string.workplan_auto_statef);
                ll_a.setVisibility(View.GONE);// 制定
                ll_b.setVisibility(View.GONE);// 修改 预览
                ll_c.setVisibility(View.VISIBLE);// 提交
                
                week_im_state.setBackgroundResource(R.drawable.ico_plan_right);
                week_tv_state.setText(R.string.workplan_auto_statef);// 自动通过
                subBtn.setVisibility(View.GONE);
            }
            else if ("3".equals(weekPlan.getPlanstatus()))
            {
                // 审核通过
                im_state.setBackgroundResource(R.drawable.ico_plan_right);
                tv_state.setText(R.string.workplan_statef);
                ll_a.setVisibility(View.GONE);
                ll_b.setVisibility(View.GONE);
                ll_c.setVisibility(View.VISIBLE);
                
                week_im_state.setBackgroundResource(R.drawable.ico_plan_right);
                week_tv_state.setText(R.string.workplan_statef);
                subBtn.setVisibility(View.GONE);
            }
            else if ("1".equals(weekPlan.getPlanstatus()))
            {
                // 审核未通过
                im_state.setBackgroundResource(R.drawable.ico_plan_wrong);
                tv_state.setText(R.string.workplan_stated);
                ll_a.setVisibility(View.GONE);
                ll_b.setVisibility(View.VISIBLE);
                ll_c.setVisibility(View.GONE);
                
                week_im_state.setBackgroundResource(R.drawable.ico_plan_wrong);
                week_tv_state.setText(R.string.workplan_stated);
                subBtn.setVisibility(View.VISIBLE);
            }
            else if ("4".equals(weekPlan.getPlanstatus()))
            {
                // 已经提交
                im_state.setBackgroundResource(R.drawable.ico_plan_updating);
                tv_state.setText(R.string.workplan_statec);
                ll_a.setVisibility(View.GONE);
                ll_b.setVisibility(View.GONE);
                ll_c.setVisibility(View.VISIBLE);
                
                week_im_state.setBackgroundResource(R.drawable.ico_plan_updating);
                week_tv_state.setText(R.string.workplan_statec);
                subBtn.setVisibility(View.GONE);
            }
            else if ("0".equals(weekPlan.getPlanstatus()))
            { // 等待提交 --(已经点过确定按钮,应该按钮消失)
                im_state.setBackgroundResource(R.drawable.ico_plan_writing);
                tv_state.setText(R.string.workplan_stateb);
                ll_a.setVisibility(View.GONE);
                ll_b.setVisibility(View.VISIBLE);
                ll_c.setVisibility(View.GONE);
                
                week_im_state.setBackgroundResource(R.drawable.ico_plan_writing);
                week_tv_state.setText(R.string.workplan_stateb);
                subBtn.setVisibility(View.GONE);
            }
            else if (StringUtils.isBlank(weekPlan.getPlanstatus()) || "2".equals(weekPlan.getPlanstatus()))
            {
                // 未制定
                im_state.setBackgroundResource(R.drawable.ico_plan_add);
                tv_state.setText(R.string.workplan_statea);
                ll_a.setVisibility(View.VISIBLE);
                ll_b.setVisibility(View.GONE);
                ll_c.setVisibility(View.GONE);
                
                week_im_state.setBackgroundResource(R.drawable.ico_plan_add);
                week_tv_state.setText(R.string.workplan_statea);
                subBtn.setVisibility(View.VISIBLE);
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
    
    /**
	 * 初始化制定周计划控件
	 */
	private void initWeekView(View view) {

		// 返回按钮
		//view.findViewById(R.id.banner_navigation_bt_back).setOnClickListener(this);
		
		// 备注输入框
		remarksView = (EditText) view.findViewById(R.id.remarks);
		// 标题
		//TextView titleTV = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		// 工作时间:
		
		makeplan_tv_time = (TextView) view.findViewById(R.id.makeplan_tv_time);
		
		// 暂无数据 LinearLayout
		parentContentLayout = (LinearLayout) view.findViewById(R.id.work_plan_week_preview_contentllayout);
		// 暂无数据 TextView
		preview_plan_no_data_promotion = (TextView) view.findViewById(R.id.preview_plan_no_data_promotion);
		//inflater = (LayoutInflater) this.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		inflater1 = (LayoutInflater) getActivity().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		webView = (WebView) view.findViewById(R.id.webview);
		
	}
	
	/**
	 * 制定周计划
	 */
	private void initWeekPlanMake() {
		
		initWeekPlanDate();// 初始化周计划 初步数据(最简单的数据)
		showWebView();// 本周计划完成目标汇总预览WebView
		initValues2();// 周计划 数据显示
	}
	
	/**
	 * 初始化周计划 初步数据(最简单的数据)
	 */
	private void initWeekPlanDate() {
		
		makeplan_tv_time.setText(weekPlanTime);
		if (oprateState == 1 || oprateState == 2) {// 0:预览，1：制定，2：修改
			//subBtn.setVisibility(View.VISIBLE);
			//subBtn.setOnClickListener(this);
		} else {
			remarksView.setEnabled(false);
		}
		remarksView.setText(weekPlan.getRemarks());
	}
	
	/**
	 * 运用管理模块工作计划-->本周计划完成目标汇总预览
	 */
	private void showWebView() {
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(PropertiesUtil.getProperties("platform_web"));
		urlBuffer
				.append("bs/business/forms/BusinessForms!planWeekQueryInitPad.do");
		//
		urlBuffer.append("?model.businessFormsStc.startdate=")
				.append(weekDateStart);
		urlBuffer.append("&model.businessFormsStc.enddate=").append(weekDateEnd);
		urlBuffer.append("&model.businessFormsStc.gridId=").append(
				//ConstValues.loginSession.getGridId());
				PrefUtils.getString(context, "gridId", ""));
		urlBuffer.append("&model.businessFormsStc.userId=").append(
				//ConstValues.loginSession.getUserCode());
				PrefUtils.getString(context, "userCode", ""));
		webView.loadUrl(urlBuffer.toString());
	}
	
	/**
	 * 周计划 数据显示
	 */
	private void initValues2() {// 第二次修改后的
		try {
			padPlantempcheckMDao = helper.getPadPlantempcheckMDao();
			// 查指标
			plantempcheckMes = padPlantempcheckMDao.queryForAll();
			
			// ---排序-------------------------------------------------
			String[] str = new String[]{"空白终端开发","有效铺货目标终端","有效销售目标终端","产品生动化","道具生动化",
											"冰冻化","促销活动推进","竞品清除计划(QC竞品)","其他","订单目标"};
			plantempcheckMs = new ArrayList<PadPlantempcheckM>();
			plantempcheckMs.clear();
			for (String string : str) {
				for (PadPlantempcheckM padplantempcheckm : plantempcheckMes) {
					if(string.equals(padplantempcheckm.getCheckname())){
						plantempcheckMs.add(padplantempcheckm);
					}
				}
			}
			// ---排序-------------------------------------------------
			//Map<String, Map<String, Set<List<String>>>> checkKeyMap = queryCheckNum(weekDateStart, weekDateEnd);
			Map<String,Map<String, Map<String,String>>> checkKeyMap = queryCheckNum2(weekDateStart, weekDateEnd);
			//订单
			
			if (!CheckUtil.IsEmpty(plantempcheckMs)) {
				preview_plan_no_data_promotion.setVisibility(View.GONE);
				parentContentLayout.removeAllViews();//
				for (int i = 0; i < plantempcheckMs.size(); i++) {
					PadPlantempcheckM plantempcheckM = plantempcheckMs.get(i);
					String checkname = plantempcheckM.getCheckname();
					String checkkey = plantempcheckM.getCheckkey();
					//Map<String, Set<List<String>>> colitemKeyMap = checkKeyMap.get(checkkey);
					//List<WeekPlanShowStc> colitemKeyMap = checkKeyMap.get(checkkey);
					//List<WeekPlanShowStc> colitemKeyMap = checkKeyMap.get(checkkey);
					Map<String, Map<String,String>> colitemKeyMap = checkKeyMap.get(checkkey);
					
					
					
					//LinearLayout childContentLayout = (LinearLayout) inflater1.inflate(R.layout.weekpreviewtitle, null);
					//LinearLayout childContentLayout = (LinearLayout) View.inflate(getActivity(),R.layout.weekpreviewtitle, null);
					//(LayoutInflater) Context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
					LinearLayout childContentLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.weekpreviewtitle, null);
					TextView titleTextView = (TextView) childContentLayout.findViewById(R.id.weekPreview_checkName);
					LinearLayout contentViewLayout = (LinearLayout) childContentLayout.findViewById(R.id.contentview);
					titleTextView.setText(checkname);
					parentContentLayout.addView(childContentLayout);
					// 显示终端数量
					//if (colitemKeyMap == null) {
					if (colitemKeyMap==null || colitemKeyMap.size() == 0) {
						
						
						
						
						
						//LinearLayout conntentView = (LinearLayout) View
						//LinearLayout conntentView = (LinearLayout) inflater1
						LinearLayout conntentView = (LinearLayout) LayoutInflater.from(getActivity())
								.inflate(R.layout.plan_preview_content, null);
						TextView titleTV = (TextView) conntentView
								.findViewById(R.id.preview_title);
						titleTV.setText("当前指标无数据");
						contentViewLayout.addView(conntentView);
					} else {
						/*
						Iterator<Entry<String, Set<List<String>>>> iterator = colitemKeyMap
								.entrySet().iterator();
						while (iterator.hasNext()) {
							Entry<String, Set<List<String>>> next = iterator
									.next();
							String key = next.getKey();
							Set<List<String>> value = next.getValue();
							
							//LinearLayout conntentView = (LinearLayout) inflater
							LinearLayout conntentView = (LinearLayout) LayoutInflater.from(getActivity())
									.inflate(R.layout.plan_preview_content,
											null);
							
							TextView titleTV = (TextView) conntentView
									.findViewById(R.id.preview_title);
							TextView preview_content = (TextView) conntentView
									.findViewById(R.id.preview_content);
							
							if (value != null) {
								titleTV.setText(key);
								preview_content.setText("终端数量： " + value.size()
										+ "家");
							}
							contentViewLayout.addView(conntentView);
							
						}
						 */
						
						
						//LinearLayout conntentView = (LinearLayout) View
						
						// Map<String, Map<String,String>> colitemKeyMap // Map<采集项, Map<产品名称,终端数量>>
						Iterator iter = colitemKeyMap.entrySet().iterator();
							while (iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							// 采集项名称
							String key = (String) entry.getKey();
							Map<String,String> val = (Map<String, String>) entry.getValue();
							
							Iterator iter2 = val.entrySet().iterator();
								while (iter2.hasNext()) {
								Map.Entry entry2 = (Map.Entry) iter2.next();
								// 产品名称
								String key2 = (String) entry2.getKey();
								// 终端数量
								String val2 = (String) entry2.getValue();
								
								
								LinearLayout conntentView = (LinearLayout) LayoutInflater.from(getActivity())
										.inflate(R.layout.plan_preview_content,
												null);
								
								TextView titleTV = (TextView) conntentView.findViewById(R.id.preview_title);
								TextView preview_proname = (TextView) conntentView.findViewById(R.id.preview_proname);
								TextView preview_content = (TextView) conntentView.findViewById(R.id.preview_content);
								
								titleTV.setText(key);
								// 根据指标主键判断是否显示产品名称,道具生动化,产品生动化,冰冻化,竞品推进计划,4大计划指标显示产品名称
								if("ad3030fb-e42e-47f8-a3ec-4229089aab6d".equals(checkkey)
										||"ad3030fb-e42e-47f8-a3ec-4229089aab7d".equals(checkkey)
										||"ad3030fb-e42e-47f8-a3ec-4229089aab8d".equals(checkkey)
										||"a16ccbc3-4be7-42b8-a338-56da74c6ddf4".equals(checkkey)
										){
									preview_proname.setText(key2);
								}else{
									preview_proname.setText("");
								}
								if("5e76aa22-d6a5-48e4-a198-9e836191bae7".equals(checkkey)){
									preview_content.setText("订单数量： " + val2 + "箱");
								}else{
									preview_content.setText("终端数量： " + val2 + "家");
								}
								contentViewLayout.addView(conntentView);
								
								
								}
							
							
							}
						
						
						/*
						for (WeekPlanShowStc weekPlanShowStc : colitemKeyMap) {
							LinearLayout conntentView = (LinearLayout) LayoutInflater.from(getActivity())
									.inflate(R.layout.plan_preview_content,
											null);
							
							TextView titleTV = (TextView) conntentView.findViewById(R.id.preview_title);
							TextView preview_proname = (TextView) conntentView.findViewById(R.id.preview_proname);
							TextView preview_content = (TextView) conntentView.findViewById(R.id.preview_content);
							
							titleTV.setText(weekPlanShowStc.getColitemname());
							if(weekPlanShowStc.getCheckkey().equals("ad3030fb-e42e-47f8-a3ec-4229089aab6d")||
									weekPlanShowStc.getCheckkey().equals("ad3030fb-e42e-47f8-a3ec-4229089aab7d")||
									weekPlanShowStc.getCheckkey().equals("ad3030fb-e42e-47f8-a3ec-4229089aab8d")||
									weekPlanShowStc.getCheckkey().equals("a16ccbc3-4be7-42b8-a338-56da74c6ddf4")){
								preview_proname.setText(weekPlanShowStc.getProductname());
							}else{
								preview_proname.setText("");
							}
							preview_content.setText("终端数量： " + weekPlanShowStc.getTermnum()
									+ "家");
							contentViewLayout.addView(conntentView);
						}*/
						
						
					}
					
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 汇总周计划  Map<采集项, Map<产品名称,终端数量>>
	private Map<String,Map<String, Map<String,String>>> queryCheckNum2(
			String startDate, String endDate) {
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		StringBuilder stringBuilder = new StringBuilder();
		// 计划指标主键, 计划指标名称, 计划采集项主键,计划采集项名称, 终端key列表, 产品名称
		stringBuilder.append("select pc.checkkey, cm.checkname,pi.colitemkey itemkey,");
		stringBuilder.append("case pi.checkkey ");
		stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753047e' then ci.colitemname ");// 0其他
		stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753c47d' then p.proname ");// 1空白终端
		stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753047p' then pm.promotname ");// 2活动
		stringBuilder.append("when '513a8b13-abbb-4a21-84f6-0ddcdaa3ec1f' then p.proname ");// 3有效铺货
		stringBuilder.append("when '5e76aa22-d6a5-48e4-a198-9e836191bae7' then p.proname ");// 订单目标
		stringBuilder.append("when 'b7170a87-68d8-453b-a67a-33827bca5bc6' then p.proname ");// 4有效销售
		stringBuilder.append("when 'a16ccbc3-4be7-42b8-a338-56da74c6ddf4' then mcm.cmpcomname ");// 5竞品推进计划品牌
		stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab6d' then ci.colitemname ");// 6道具生动化
		stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab7d' then ci.colitemname ");// 7产品生动化
		stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab8d' then ci.colitemname ");// 8冰冻化
		stringBuilder.append("else '' end itemname,pi.remarks, ");
		// 添加新字段 产品名称
		stringBuilder.append("case pi.checkkey ");
		stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753047e' then ci.colitemname ");// 0其他
		stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753c47d' then p.proname ");// 1空白终端
		stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753047p' then pm.promotname ");// 2活动
		stringBuilder.append("when '513a8b13-abbb-4a21-84f6-0ddcdaa3ec1f' then p.proname ");// 3有效铺货
		stringBuilder.append("when '5e76aa22-d6a5-48e4-a198-9e836191bae7' then p.proname ");// 订单目标
		stringBuilder.append("when 'b7170a87-68d8-453b-a67a-33827bca5bc6' then p.proname ");// 4有效销售
		stringBuilder.append("when 'a16ccbc3-4be7-42b8-a338-56da74c6ddf4' then mcm.cmpproname ");// 5竞品推进计划品牌
		stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab6d' then mpm.proname ");// 6道具生动化
		stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab7d' then mpm.proname ");// 7产品生动化
		stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab8d' then mpm.proname ");// 8冰冻化
		stringBuilder.append("else '' end productname ,pi.ordernum ");
		stringBuilder.append("from mst_planforuser_m pu ");
		stringBuilder
			.append("inner join mst_plancheck_info pc on pu.plankey = pc.PLANKEY ");
		stringBuilder
			.append("inner join mst_plancollection_info pi on pc.pcheckkey = pi.pcheckkey and pi.deleteflag!='1' ");
		stringBuilder
			.append("inner join pad_plantempcheck_m cm on pc.checkkey = cm.checkkey ");
		stringBuilder
			// 其他 道具生动化 产品生动化 冰冻化
			.append("left join pad_plantempcollection_info ci on pi.colitemkey = ci.colitemkey and pi.checkkey=ci.checkkey and pi.checkkey in ('884d0d53-2cdb-4214-b24d-137d8753047e','ad3030fb-e42e-47f8-a3ec-4229089aab6d','ad3030fb-e42e-47f8-a3ec-4229089aab7d','ad3030fb-e42e-47f8-a3ec-4229089aab8d') ");
		stringBuilder
			// 道具生动化 产品生动化 冰冻化 加载产品名称
			.append("left join mst_product_m mpm on mpm.productkey = pi.productkey and pi.checkkey in ('ad3030fb-e42e-47f8-a3ec-4229089aab6d','ad3030fb-e42e-47f8-a3ec-4229089aab7d','ad3030fb-e42e-47f8-a3ec-4229089aab8d') ");
		stringBuilder
			// 竞品推进计划 加载产品名称
			.append("left join mst_cmproductinfo_m mcm on mcm.cmpproductkey = pi.productkey and pi.checkkey in ('a16ccbc3-4be7-42b8-a338-56da74c6ddf4') ");
		stringBuilder
			// 活动
			.append("left join mst_promotions_m pm on pm.promotkey = pi.colitemkey and pi.checkkey = '884d0d53-2cdb-4214-b24d-137d8753047p' ");
		stringBuilder
			// 竞品推进计划
			.append("left join MST_CMPCOMPANY_M mcm on mcm.cmpcomkey = pi.colitemkey and pi.checkkey = 'a16ccbc3-4be7-42b8-a338-56da74c6ddf4' ");
		stringBuilder
			// 空白终端 有效铺货 有效销售
			.append("left join mst_product_m p on p.productkey = pi.productkey and pi.checkkey in ('884d0d53-2cdb-4214-b24d-137d8753c47d','5e76aa22-d6a5-48e4-a198-9e836191bae7','513a8b13-abbb-4a21-84f6-0ddcdaa3ec1f','b7170a87-68d8-453b-a67a-33827bca5bc6') ");
		
		stringBuilder.append("where pu.plandate between '");
		stringBuilder.append(startDate).append("' and '").append(endDate)
			.append("' ").append("order by pi.checkkey, pi.colitemkey");
		Map<String, Map<String, Set<List<String>>>> checkKeyMap = new HashMap<String, Map<String, Set<List<String>>>>();
		Map<String,List<WeekPlanShowStc>> checkKeyMap2 = new HashMap<String, List<WeekPlanShowStc>>();
		Map<String,Map<String, Map<String,String>>> checkKeyMap3 = new HashMap<String, Map<String,Map<String,String>>>();
		
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(stringBuilder.toString(), null);
			while (cursor.moveToNext()) {
				String checkkey = cursor.getString(0);// 指标主键
				// String colitemkey = cursor.getString(2);//采集项主键
				String itemName = cursor.getString(3);// 采集项主键
				String remarks = cursor.getString(4);// 存储的终端List<String>
				String productname = cursor.getString(5);// 产品名称(若该条记录没有产品名称,以采集项名称作为产品名称)
				String ordernum = cursor.getString(6);// 订单计划数量
				
				// 获取终端数量
				String termnum = "";
				if(remarks == null||"".equals(remarks)){
					termnum = "0";
				}else{
					List<String> terminalKeys = JsonUtil.parseList(remarks,String.class);
					termnum = terminalKeys.size()+"";
				}
				
				//   
				Map<String, Map<String, String>> popmap = checkKeyMap3.get(checkkey);
				Map<String, String> proMap = null;
				
				
				if(popmap==null){// 判断是否有大集合
					popmap = new HashMap<String, Map<String,String>>();// 创建大集合 存储每个计划指标
					checkKeyMap3.put(checkkey, popmap);// (计划指标,)
					proMap = new HashMap<String, String>();// 创建小集合 存储每个指标各个产品
					popmap.put(itemName, proMap);// (采集项 ,产品集合(产品名称,数量))
					if("5e76aa22-d6a5-48e4-a198-9e836191bae7".equals(checkkey)){// 如果是订单目标,则存储订单数量而不是终端数量
						proMap.put(productname, ordernum);
					}else{
						proMap.put(productname, termnum);
					}
				}else{// 
					proMap = popmap.get(itemName);
					if(proMap == null){// 大集合中是否已存在该计划指标 (不存在)
						proMap = new HashMap<String, String>();
						popmap.put(itemName, proMap);
						if("5e76aa22-d6a5-48e4-a198-9e836191bae7".equals(checkkey)){// 如果是订单目标,则存储订单数量而不是终端数量
							proMap.put(productname, ordernum);
						}else{
							proMap.put(productname, termnum);
						}
					}else{// 存在
						String proname = proMap.get(productname);
						if(proname == null){// 判断计划指标是否已存在该产品
							if("5e76aa22-d6a5-48e4-a198-9e836191bae7".equals(checkkey)){// 如果是订单目标,则存储订单数量而不是终端数量
								proMap.put(productname, ordernum);
							}else{
								proMap.put(productname, termnum);
							}
						}else{
							if("5e76aa22-d6a5-48e4-a198-9e836191bae7".equals(checkkey)){// 如果是订单目标,则存储订单数量而不是终端数量
								int x = Integer.parseInt(proname);
								int z = Integer.parseInt(ordernum);
								int y = x+z;
								String s = String.valueOf(y);
								proMap.put(productname, s);
							}else{
								int x = Integer.parseInt(proname);
								int z = Integer.parseInt(termnum);
								int y = x+z;
								String s = String.valueOf(y);
								proMap.put(productname, s);
							}
						}
					}
				}
				
				
			}
		}
		return checkKeyMap3;
	}
	
	@Override
    public void onResume()
    {

        super.onResume();
        Log.i(TAG, "onresume");
        
        initData();
        initWeekDataView();
        
        // 将周计划制定,放在本页面上,并出现确定按钮,提交周计划
        initWeekPlanMake();
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
                DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener()
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
                        initWeekPlanMake();

                    }
                }, yearr, month, day);
                if (!dateDialog.isShowing())
                {
                    dateDialog.show();
                }

                break;
                
            case R.id.banner_navigation_rl_confirm:// 确定--上传
            //case R.id.banner_navigation_bt_confirm:// 确定--上传
            	
            	// 弹窗提示是否真的上传
            	showNotifyDialog();
    			break;
        }
    }
    
    /**
     * 上传日/周计划
     */
    public void showNotifyDialog(){
		if (dialog != null && dialog.isShowing())
			return;
		View conview = LayoutInflater.from(getActivity()).inflate(
				R.layout.agencyvisit_total_overvisit_dialog, null);
		TextView title = (TextView) conview
				.findViewById(R.id.agencyvisit_tv_over_title);
		TextView msg = (TextView) conview
				.findViewById(R.id.agencyvisit_tv_over_msg);
		ImageView sure = (ImageView) conview
				.findViewById(R.id.agencyvisit_bt_over_sure);
		ImageView cancle = (ImageView) conview
				.findViewById(R.id.agencyvisit_bt_over_quxiao);
		title.setText(R.string.dialog_title);
		//msg.setText(R.string.dialog_msg_overworkplan);
		msg.setText("确定上传 "+weekPlanTime1+" 的日/周计划");
		dialog = new AlertDialog.Builder(getActivity()).setCancelable(false)
				.create();
		dialog.setView(conview, 0, 0, 0, 0);
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 保存周计划到本地
    			String remarks = remarksView.getText().toString();
    			//weekPlan.setUserid(ConstValues.loginSession.getUserCode());
    			weekPlan.setUserid(PrefUtils.getString(context, "userCode", ""));
    			weekPlan.setStartdate(weekDateStart);//weekDateStart startDate
    			weekPlan.setEnddate(weekDateEnd);//weekDateEnd endDate
    			weekPlan.setPlanstatus("0");
    			weekPlan.setRemarks(remarks);
    			weekPlan.setPadisconsistent(ConstValues.FLAG_0);
    			weekPlan.setUpdatetime(new Date());
    			//weekPlan.setUpdateuser(ConstValues.loginSession.getUserCode());
    			weekPlan.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
    			//weekPlan.setGridkey(ConstValues.loginSession.getGridId());
    			weekPlan.setGridkey(PrefUtils.getString(context, "gridId", ""));
    			//weekPlan.setUploadFlag("1");
    			if (oprateState == 1)// 制定
    			{
    				try {
    					weekPlan.setPlankey(FunUtil.getUUID());
    					weekPlan.setCredate(new Date());
    					weekPlan.setDeleteflag(ConstValues.FLAG_0);
    					//weekPlan.setCreuser(ConstValues.loginSession.getUserCode());
    					weekPlan.setCreuser(PrefUtils.getString(context, "userCode", ""));
    					Dao<MstPlanWeekforuserM, String> dao = helper
    							.getMstPlanWeekforuserMDao();
    					dao.create(weekPlan);
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    			} else if (oprateState == 2)// 修改
    			{
    				try {
    					Dao<MstPlanWeekforuserM, String> dao = helper
    							.getMstPlanWeekforuserMDao();
    					dao.createOrUpdate(weekPlan);
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    			}
    			//finish();
    			// 上传日计划
    			//onsubmitClickPosition = position;
                upservice = new UploadDataService(context, handler);
                //Log.e(TAG, "提交查询的 plankey = " + planKey);
                Toast.makeText(getActivity(), "上传中，请等待", Toast.LENGTH_SHORT).show();
                
                // 将本周日计划padiscontent改为2 
                WorkPlanService workPlanService = new WorkPlanService(getActivity(), handler);
				try {
					// 本周时间 日计划上传
					String[] weekdate = weekPlanTime2.split("~");
					workPlanService.setPlanforuserpadiscon(weekdate);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                upservice.upload_work_plans(false, null);
                
    			// 上传周计划
                upservice.upload_weekwork_plans(false, weekPlan.getPlankey());
                dialog.dismiss();
                //this.finish();
                // 确定按钮消失
                subBtn.setVisibility(View.GONE);
                
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "取消", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				
			}
		});
		dialog.show();
	}

    // 日计划 适配器
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
                    //Intent intent = new Intent(context, DayPlanActivity.class);
                    //MakePlanLinearLayout
                    //Intent intent = new Intent(context, MakePlanLinearLayout.class);
                    //Intent intent = new Intent(context, VerticalMakePlanActivity.class);
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
                    //Intent intent = new Intent(context, DayPlanActivity.class);
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
                    //Intent intent = new Intent(context, DayPlanActivity.class);
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
                        /*viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);*/
                        
                        /*viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.VISIBLE);
                        viewHolder.ll_c.setVisibility(View.GONE);*/
                        
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        // 审核未通过
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_wrong);
                        viewHolder.tv_state.setText(R.string.workplan_stated);
                        /*viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.VISIBLE);
                        viewHolder.ll_c.setVisibility(View.GONE);*/
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.VISIBLE);
                        viewHolder.ll_c.setVisibility(View.GONE);
                        break;
                    case 3:
                        // 审核通过
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_right);
                        viewHolder.tv_state.setText(R.string.workplan_statef);
                        /*viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);*/
                        
                        /*viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.VISIBLE);
                        viewHolder.ll_c.setVisibility(View.GONE);*/
                        
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        // 自动通过
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_right);
                        viewHolder.tv_state.setText(R.string.workplan_auto_statef);
                        /*viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);*/
                        
                        /*viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.VISIBLE);
                        viewHolder.ll_c.setVisibility(View.GONE);*/
                        
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        // 周计划已提交,但此日计划未制定过
                        viewHolder.im_state.setBackgroundResource(R.drawable.ico_plan_right);
                        viewHolder.tv_state.setText(R.string.workplan_auto_statef);
                        /*viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.VISIBLE);*/
                        
                        /*viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.VISIBLE);
                        viewHolder.ll_c.setVisibility(View.GONE);*/
                        
                        viewHolder.ll_a.setVisibility(View.GONE);
                        viewHolder.ll_b.setVisibility(View.GONE);
                        viewHolder.ll_c.setVisibility(View.GONE);
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
	     * 
	     */
		private void initValues() {// 修改后的周计划 数据显示 已废弃 改用2了
			try {
				padPlantempcheckMDao = helper.getPadPlantempcheckMDao();
				// 查指标
				plantempcheckMs = padPlantempcheckMDao.queryForAll();
				//Map<String, Map<String, Set<List<String>>>> checkKeyMap = queryCheckNum(weekDateStart, weekDateEnd);
				Map<String, List<WeekPlanShowStc>> checkKeyMap = queryCheckNum(weekDateStart, weekDateEnd);
				
				
				if (!CheckUtil.IsEmpty(plantempcheckMs)) {
					preview_plan_no_data_promotion.setVisibility(View.GONE);
					parentContentLayout.removeAllViews();//
					for (int i = 0; i < plantempcheckMs.size(); i++) {
						PadPlantempcheckM plantempcheckM = plantempcheckMs.get(i);
						String checkname = plantempcheckM.getCheckname();
						String checkkey = plantempcheckM.getCheckkey();
						//Map<String, Set<List<String>>> colitemKeyMap = checkKeyMap.get(checkkey);
						List<WeekPlanShowStc> colitemKeyMap = checkKeyMap.get(checkkey);
						
						
						
						//LinearLayout childContentLayout = (LinearLayout) inflater1.inflate(R.layout.weekpreviewtitle, null);
						//LinearLayout childContentLayout = (LinearLayout) View.inflate(getActivity(),R.layout.weekpreviewtitle, null);
						//(LayoutInflater) Context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
						LinearLayout childContentLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.weekpreviewtitle, null);
						TextView titleTextView = (TextView) childContentLayout.findViewById(R.id.weekPreview_checkName);
						LinearLayout contentViewLayout = (LinearLayout) childContentLayout.findViewById(R.id.contentview);
						titleTextView.setText(checkname);
						parentContentLayout.addView(childContentLayout);
						// 显示终端数量
						//if (colitemKeyMap == null) {
						if (colitemKeyMap==null || colitemKeyMap.size() == 0) {
							
							
							
							
							
							//LinearLayout conntentView = (LinearLayout) View
							//LinearLayout conntentView = (LinearLayout) inflater1
							LinearLayout conntentView = (LinearLayout) LayoutInflater.from(getActivity())
									.inflate(R.layout.plan_preview_content, null);
							TextView titleTV = (TextView) conntentView
									.findViewById(R.id.preview_title);
							titleTV.setText("当前指标无数据");
							contentViewLayout.addView(conntentView);
						} else {
							/*
							Iterator<Entry<String, Set<List<String>>>> iterator = colitemKeyMap
									.entrySet().iterator();
							while (iterator.hasNext()) {
								Entry<String, Set<List<String>>> next = iterator
										.next();
								String key = next.getKey();
								Set<List<String>> value = next.getValue();
								
								//LinearLayout conntentView = (LinearLayout) inflater
								LinearLayout conntentView = (LinearLayout) LayoutInflater.from(getActivity())
										.inflate(R.layout.plan_preview_content,
												null);
								
								TextView titleTV = (TextView) conntentView
										.findViewById(R.id.preview_title);
								TextView preview_content = (TextView) conntentView
										.findViewById(R.id.preview_content);
								
								if (value != null) {
									titleTV.setText(key);
									preview_content.setText("终端数量： " + value.size()
											+ "家");
								}
								contentViewLayout.addView(conntentView);
								
							}
							*/
							
							
							//LinearLayout conntentView = (LinearLayout) View
							
							
							for (WeekPlanShowStc weekPlanShowStc : colitemKeyMap) {
								LinearLayout conntentView = (LinearLayout) LayoutInflater.from(getActivity())
										.inflate(R.layout.plan_preview_content,
												null);

								TextView titleTV = (TextView) conntentView.findViewById(R.id.preview_title);
								TextView preview_proname = (TextView) conntentView.findViewById(R.id.preview_proname);
								TextView preview_content = (TextView) conntentView.findViewById(R.id.preview_content);
								
								titleTV.setText(weekPlanShowStc.getColitemname());
								if(weekPlanShowStc.getCheckkey().equals("ad3030fb-e42e-47f8-a3ec-4229089aab6d")||
										weekPlanShowStc.getCheckkey().equals("ad3030fb-e42e-47f8-a3ec-4229089aab7d")||
										weekPlanShowStc.getCheckkey().equals("ad3030fb-e42e-47f8-a3ec-4229089aab8d")||
										weekPlanShowStc.getCheckkey().equals("a16ccbc3-4be7-42b8-a338-56da74c6ddf4")){
									preview_proname.setText(weekPlanShowStc.getProductname());
								}else{
									preview_proname.setText("");
								}
								preview_content.setText("终端数量： " + weekPlanShowStc.getTermnum()
										+ "家");
								contentViewLayout.addView(conntentView);
							}
							 
							
						}
						
						
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//private Map<String, Map<String, Set<List<String>>>> queryCheckNum2(// 修改后的周计划 查询 已废弃
		private Map<String, List<WeekPlanShowStc>> queryCheckNum(
				String startDate, String endDate) {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			SQLiteDatabase db = helper.getReadableDatabase();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder
			.append("select pc.checkkey, cm.checkname,pi.colitemkey itemkey,");
			stringBuilder.append("case pi.checkkey ");
			/*
			stringBuilder.append("when '0' then ci.colitemname ");// 0采集项
			stringBuilder.append("when '1' then p.proname ");// 1产品
			stringBuilder.append("when '2' then pm.promotname ");// 2活动
			stringBuilder.append("when '3' then p.proname ");// 3有效铺货
			stringBuilder.append("when '4' then p.proname ");// 4有效销售
			stringBuilder.append("when '5' then mcm.cmpcomname ");// 5竞品推进计划品牌
			stringBuilder.append("when '6' then ci.colitemname ");// 6道具生动化,产品生动化,冰冻化
			 */
			stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753047e' then ci.colitemname ");// 0其他
			stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753c47d' then p.proname ");// 1空白终端
			stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753047p' then pm.promotname ");// 2活动
			stringBuilder.append("when '513a8b13-abbb-4a21-84f6-0ddcdaa3ec1f' then p.proname ");// 3有效铺货
			stringBuilder.append("when 'b7170a87-68d8-453b-a67a-33827bca5bc6' then p.proname ");// 4有效销售
			stringBuilder.append("when 'a16ccbc3-4be7-42b8-a338-56da74c6ddf4' then mcm.cmpcomname ");// 5竞品推进计划品牌
			stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab6d' then ci.colitemname ");// 6道具生动化
			stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab7d' then ci.colitemname ");// 7产品生动化
			stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab8d' then ci.colitemname ");// 8冰冻化
			
			stringBuilder.append("else '' end itemname,pi.remarks, ");
			
			// 添加新字段
			stringBuilder.append("case pi.checkkey ");
			/*
			stringBuilder.append("when '0' then ci.colitemname ");// 0采集项
			stringBuilder.append("when '1' then p.proname ");// 1产品
			stringBuilder.append("when '2' then pm.promotname ");// 2活动
			stringBuilder.append("when '3' then p.proname ");// 3有效铺货
			stringBuilder.append("when '4' then p.proname ");// 4有效销售
			stringBuilder.append("when '5' then mcm.cmpcomname ");// 5竞品推进计划品牌
			stringBuilder.append("when '6' then ci.colitemname ");// 6道具生动化,产品生动化,冰冻化
			 */
			stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753047e' then ci.colitemname ");// 0其他
			stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753c47d' then p.proname ");// 1空白终端
			stringBuilder.append("when '884d0d53-2cdb-4214-b24d-137d8753047p' then pm.promotname ");// 2活动
			stringBuilder.append("when '513a8b13-abbb-4a21-84f6-0ddcdaa3ec1f' then p.proname ");// 3有效铺货
			stringBuilder.append("when 'b7170a87-68d8-453b-a67a-33827bca5bc6' then p.proname ");// 4有效销售
			stringBuilder.append("when 'a16ccbc3-4be7-42b8-a338-56da74c6ddf4' then mcm.cmpproname ");// 5竞品推进计划品牌
			stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab6d' then mpm.proname ");// 6道具生动化
			stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab7d' then mpm.proname ");// 7产品生动化
			stringBuilder.append("when 'ad3030fb-e42e-47f8-a3ec-4229089aab8d' then mpm.proname ");// 8冰冻化
			
			stringBuilder.append("else '' end productname ");
			
			
			
			
			
			
			
			stringBuilder.append("from mst_planforuser_m pu ");
			stringBuilder
			.append("inner join mst_plancheck_info pc on pu.plankey = pc.PLANKEY ");
			stringBuilder
			.append("inner join mst_plancollection_info pi on pc.pcheckkey = pi.pcheckkey and pi.deleteflag!='1' ");
			stringBuilder
			.append("inner join pad_plantempcheck_m cm on pc.checkkey = cm.checkkey ");
			
			/*
			stringBuilder
					//.append("left join pad_plantempcollection_info ci on pi.colitemkey = ci.colitemkey and pi.plantype ='0' ");
					// 其他 道具生动化 产品生动化 冰冻化
					.append("left join pad_plantempcollection_info ci on pi.colitemkey = ci.colitemkey and pi.plantype in ('0','6') ");
			stringBuilder
					// 活动
					.append("left join mst_promotions_m pm on pm.promotkey = pi.colitemkey and pi.plantype = '2' ");
			stringBuilder
					// 竞品推进计划
					.append("left join MST_CMPCOMPANY_M mcm on mcm.cmpcomkey = pi.colitemkey and pi.plantype = '5' ");
			stringBuilder
					// 空白终端 有效铺货 有效销售
					.append("left join mst_product_m p on p.productkey = pi.colitemkey and pi.plantype in ('1','3','4') ");
			 */
			
			
			stringBuilder
			//.append("left join pad_plantempcollection_info ci on pi.colitemkey = ci.colitemkey and pi.plantype ='0' ");
			// 其他 道具生动化 产品生动化 冰冻化
			.append("left join pad_plantempcollection_info ci on pi.colitemkey = ci.colitemkey and pi.checkkey=ci.checkkey and pi.checkkey in ('884d0d53-2cdb-4214-b24d-137d8753047e','ad3030fb-e42e-47f8-a3ec-4229089aab6d','ad3030fb-e42e-47f8-a3ec-4229089aab7d','ad3030fb-e42e-47f8-a3ec-4229089aab8d') ");
			
			stringBuilder
			// 道具生动化 产品生动化 冰冻化 加载产品名称
			.append("left join mst_product_m mpm on mpm.productkey = pi.productkey and pi.checkkey in ('ad3030fb-e42e-47f8-a3ec-4229089aab6d','ad3030fb-e42e-47f8-a3ec-4229089aab7d','ad3030fb-e42e-47f8-a3ec-4229089aab8d') ");
			
			stringBuilder
			// 竞品推进计划 加载产品名称
			.append("left join mst_cmproductinfo_m mcm on mcm.cmpproductkey = pi.productkey and pi.checkkey in ('a16ccbc3-4be7-42b8-a338-56da74c6ddf4') ");
			
			
			
			
			
			stringBuilder
			// 活动
			.append("left join mst_promotions_m pm on pm.promotkey = pi.colitemkey and pi.checkkey = '884d0d53-2cdb-4214-b24d-137d8753047p' ");
			stringBuilder
			// 竞品推进计划
			.append("left join MST_CMPCOMPANY_M mcm on mcm.cmpcomkey = pi.colitemkey and pi.checkkey = 'a16ccbc3-4be7-42b8-a338-56da74c6ddf4' ");
			stringBuilder
			// 空白终端 有效铺货 有效销售
			.append("left join mst_product_m p on p.productkey = pi.productkey and pi.checkkey in ('884d0d53-2cdb-4214-b24d-137d8753c47d','513a8b13-abbb-4a21-84f6-0ddcdaa3ec1f','b7170a87-68d8-453b-a67a-33827bca5bc6') ");
			
			stringBuilder.append("where pu.plandate between '");
			stringBuilder.append(startDate).append("' and '").append(endDate)
			.append("' ").append("order by pi.checkkey, pi.colitemkey");
			Map<String, Map<String, Set<List<String>>>> checkKeyMap = new HashMap<String, Map<String, Set<List<String>>>>();
			Map<String,List<WeekPlanShowStc>> checkKeyMap2 = new HashMap<String, List<WeekPlanShowStc>>();
			if (db.isOpen()) {
				Cursor cursor = db.rawQuery(stringBuilder.toString(), null);
				while (cursor.moveToNext()) {
					String checkkey = cursor.getString(0);// 指标主键
					// String colitemkey = cursor.getString(2);//采集项主键
					String itemName = cursor.getString(3);// 采集项主键
					String remarks = cursor.getString(4);// 存储的终端List<String>
					String productname = cursor.getString(5);// 产品名称(若该条记录没有产品名称,以采集项名称作为产品名称)
					
					/*
					Map<String, Set<List<String>>> itemkeyMap = checkKeyMap
							.get(checkkey);
					
					if (itemkeyMap == null) {
						itemkeyMap = new HashMap<String, Set<List<String>>>();
						checkKeyMap.put(checkkey, itemkeyMap);
					}
					
					Set hashSet = itemkeyMap.get(itemName);
					if (hashSet == null) {
						hashSet = new HashSet<List<String>>();
						//itemkeyMap.put(itemName, hashSet);
						itemkeyMap.put(productname, hashSet);
					}
					List<String> terminalKeys = JsonUtil.parseList(remarks,
							String.class);
					if (terminalKeys != null) {
						hashSet.addAll(terminalKeys);
					}
					//itemkeyMap.put(itemName, hashSet);
					itemkeyMap.put(productname, hashSet);
					checkKeyMap.put(checkkey, itemkeyMap);
					
					
					*/
					
					
					
					
					// 周计划各个指标下各个采集项记录的数据
					WeekPlanShowStc  weekPlanShowStc = new WeekPlanShowStc();
					weekPlanShowStc.setColitemname(itemName);
					weekPlanShowStc.setProductname(productname);
					weekPlanShowStc.setCheckkey(checkkey);
					String termnum = "";
					if(remarks == null){
						termnum = "0";
						
					}else{
					List<String> terminalKeys = JsonUtil.parseList(remarks,
							String.class);
					termnum = terminalKeys.size()+"";
					}
					weekPlanShowStc.setTermnum(termnum);
					
					List<WeekPlanShowStc> weekPlanShowStcs = checkKeyMap2.get(checkkey);
					
					if(weekPlanShowStcs==null){
						weekPlanShowStcs = new ArrayList<WeekPlanShowStc>();
						weekPlanShowStcs.add(weekPlanShowStc);
					}else{
						weekPlanShowStcs.add(weekPlanShowStc);
					}
					checkKeyMap2.put(checkkey, weekPlanShowStcs);
					
					 
				}
				
			}
			return checkKeyMap2;
		}
		
		public static void initworkplan(){
			//initData();
        	//initViewData();
		}
    
}
