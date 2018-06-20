package et.tsingtaopad.operation.weekworkplan;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
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
@SuppressLint("SimpleDateFormat")
public class WeekWorkPlanFragment extends BaseFragmentSupport implements OnClickListener {

	private final String TAG = "WorkplanFragment";

	private TextView tv_title;
	private TextView workplan_tv_rounds;
	private TextView tv_plantitle;
	private Button btn_time;
	private Button btn_back;
	private ListView listView;
	private String time;
	private String date;
	private String aday;
	private Calendar calendar;
	private int year;
	private int month;
	private int day;
	private WorkPlanAdapter adapter;
	private WeekWorkPlanService service;
	private UploadDataService upservice;
	private DatabaseHelper helper;
	List<WorkPlanStc> workPlanStcs = new ArrayList<WorkPlanStc>();
	private int onsubmitClickPosition = 0;
	public static final int UPLOADSUCCESS = 0;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();

			super.handleMessage(msg);
			switch (msg.what) {

			// 提示信息
			case ConstValues.WAIT1:
				Toast.makeText(getActivity(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
				break;
			case UPLOADSUCCESS:
				//上传成功会修改数据库修改状态为4 所以不再此处修改数据库的状态
				WorkPlanStc workPlanStc = workPlanStcs.get(onsubmitClickPosition);
				workPlanStc.setState(4);
				adapter.notifyDataSetChanged();

				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.operation_workplan, null);
		view.setOnClickListener(null);
		// 获取系统时间
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		date = sDateFormat.format(new Date());
		calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		service = new WeekWorkPlanService(getActivity(), handler);
		initView(view);
		initData();
		initViewData();
		return view;
	}

	/**
	 * 加载所有需要用到的数据
	 */

	private void initData() {
		helper = DatabaseHelper.getHelper(getActivity());

		try {
			Dao<MstPlanforuserM, String> dao = helper.getMstPlanforuserMDao();
			if (!workPlanStcs.isEmpty()) {
				workPlanStcs.clear();
			}
			Calendar c = calendar;
			c.add(Calendar.DATE, -c.get(Calendar.WEEK_OF_MONTH) * 7);
			for (int i = 1; i <= calendar.getActualMaximum(Calendar.WEEK_OF_MONTH); i++) {
				WorkPlanStc workPlanStc = new WorkPlanStc();
				c.add(Calendar.DAY_OF_YEAR, 7);
				Date date1 = c.getTime();
				

				workPlanStc.setState(2);
				workPlanStc.setPlanKey(FunUtil.getUUID());
				workPlanStc.setDate(date1);
				StringBuilder builder = new StringBuilder();
				String weekBegin = DateUtil.getWeekBegin(date1, "yyyy年MM月dd日");
				String weekEnd = DateUtil.getWeekEnd(date1, "yyyy年MM月dd日");
				if (weekBegin.substring(6).equals("0")) {
					weekBegin = weekBegin.substring(6, 10);
				} else {
					weekBegin = weekBegin.substring(5, 10);
				}
				if (weekEnd.substring(6).equals("0")) {
					weekEnd = weekEnd.substring(6, 10);
				} else {
					weekEnd = weekEnd.substring(5, 10);
				}
				workPlanStc.setVisitStartDate(weekBegin);
				workPlanStc.setVisitEndDate(weekEnd);
				builder.append(weekBegin).append("~").append(weekEnd).append(" ").append(datePreview(i));
				workPlanStc.setVisitTime(builder.toString());
				// 如有计划状态值以及plankey 取表中的数值
				Map<String, Object> fieldValues = new HashMap<String, Object>();
				fieldValues.put("planamf", weekBegin);
				fieldValues.put("planpmf", weekEnd);
				fieldValues.put("plantype", "1");
				List<MstPlanforuserM> planList = dao.queryForFieldValues(fieldValues);
				if (planList.size() > 0) {
					int state = Integer.parseInt(planList.get(0).getPlanstatus());
					String plankey = planList.get(0).getPlankey();
					workPlanStc.setState(state);
					workPlanStc.setPlanKey(plankey);
				}
				workPlanStcs.add(workPlanStc);
			}
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String datePreview(int num) {
		String temp = null;
		switch (num) {
		case 1:
			temp = "第一周";

			break;
		case 2:
			temp = "第二周";

			break;
		case 3:
			temp = "第三周";

			break;
		case 4:
			temp = "第四周";

			break;
		case 5:
			temp = "第五周";

			break;
		case 6:
			temp = "第六周";

			break;

		}
		return temp;
	}

	/**
	 * 初始化界面组件
	 */
	private void initView(View view) {
		// 绑定界面组件
		btn_back = (Button) view.findViewById(R.id.banner_navigation_bt_back);
		btn_time = (Button) view.findViewById(R.id.workplan_btn_time);
		tv_title = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
		workplan_tv_rounds = (TextView) view.findViewById(R.id.workplan_tv_rounds);
		tv_plantitle = (TextView) view.findViewById(R.id.workplan_tv_plantitle);
		listView = (ListView) view.findViewById(R.id.workplan_lv_content);
		// 绑定事件
		btn_back.setOnClickListener(this);
		btn_time.setOnClickListener(this);
	}

	/**
	 *  装载界面数据
	 */
	private void initViewData() {
		tv_title.setText(getString(R.string.weekworkplan_label_title));
		workplan_tv_rounds.setText(getString(R.string.workplan_msg1) + (month + 1) + getString(R.string.workplan_month));
		tv_plantitle.setText(calendar.get(Calendar.YEAR) + getString(R.string.workplan_msgyear) + (month + 1) + getString(R.string.workplan_rmonth));

		btn_time.setText(date);

		adapter = new WorkPlanAdapter(getActivity(), workPlanStcs);
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 界面返回按钮
		case R.id.banner_navigation_bt_back:

			this.getFragmentManager().popBackStack();

			break;
		case R.id.workplan_btn_time:
			Calendar c = Calendar.getInstance();
			DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
				@SuppressLint("SimpleDateFormat")
				@Override
				public void onDateSet(DatePicker view, int year1, int monthOfYear, int dayOfMonth) {

					time = (year1 + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth));
					btn_time.setText(time);
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					try {
						calendar.setTime(format.parse(time));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					//tv_week.setText(getString(R.string.workplan_msg1) + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.weekworkplan_msg1));
					tv_plantitle.setText(year1 + getString(R.string.workplan_msg3) + (monthOfYear + 1) + getString(R.string.workplan_msgyear));

					initData();

				}
			}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
			if (!dateDialog.isShowing()) {
				dateDialog.show();
			}

			break;
		}
	}

	@Override
	public void onResume() {
		initData();

		super.onResume();
		Log.i(TAG, "onresume");
	}

	class WorkPlanAdapter extends BaseAdapter {
		private Context context;
		private Boolean flag = true;
		private List<WorkPlanStc> workPlanStcs;

		public WorkPlanAdapter(Context context, List<WorkPlanStc> workPlanStcs) {
			this.context = context;
			this.workPlanStcs = workPlanStcs;
		}

		@Override
		public int getCount() {
			return workPlanStcs.size();
		}

		@Override
		public Object getItem(int position) {
			return workPlanStcs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;

			// 查询人员主表 取得计划状态值 以及plankey
			final WorkPlanStc workPlanStc = workPlanStcs.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.operation_workplan_lvitem, null);
				viewHolder = new ViewHolder();
				viewHolder.btn_make = (Button) convertView.findViewById(R.id.workplan_btn_make);
				viewHolder.btn_amend = (Button) convertView.findViewById(R.id.workplan_btn_amend);
				viewHolder.btn_submit = (Button) convertView.findViewById(R.id.workplan_btn_submit);
				viewHolder.btn_preview = (Button) convertView.findViewById(R.id.workplan_btn_preview);
				viewHolder.tv_time = (TextView) convertView.findViewById(R.id.workplan_tv_time);
				viewHolder.tv_state = (TextView) convertView.findViewById(R.id.workplan_tv_state);
				viewHolder.im_state = (ImageView) convertView.findViewById(R.id.workplan_im_state);
				viewHolder.ll_a = (LinearLayout) convertView.findViewById(R.id.workplan_ll_a);
				viewHolder.ll_b = (LinearLayout) convertView.findViewById(R.id.workplan_ll_b);
				viewHolder.ll_c = (LinearLayout) convertView.findViewById(R.id.workplan_ll_c);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final String planKey = workPlanStc.getPlanKey();
			viewHolder.tv_time.setText(workPlanStc.getVisitTime());
			// 制定
			viewHolder.btn_make.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(context, MakeWeekPlanActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("date", (new SimpleDateFormat("yyyy-MM-dd").format(workPlanStc.getDate()) + ""));//计划日期
					String visitStartDate = workPlanStc.getVisitStartDate();
					String visitEndDate = workPlanStc.getVisitEndDate();
					bundle.putString("visitStartDate", visitStartDate);
					bundle.putString("visitEndDate", visitEndDate);
					bundle.putInt("position", position);
					bundle.putString("plankey", planKey);
					bundle.putBoolean("make_or_modify", true);
					intent.putExtras(bundle);
					((FragmentActivity) context).startActivityForResult(intent, 0);
				}
			});
			// 修改
			viewHolder.btn_amend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, MakeWeekPlanActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("date", (new SimpleDateFormat("yyyy-MM-dd").format(workPlanStc.getDate()) + ""));
					bundle.putInt("position", position);
					bundle.putString("plankey", planKey);
					String visitStartDate = workPlanStc.getVisitStartDate();
					String visitEndDate = workPlanStc.getVisitEndDate();
					bundle.putString("visitStartDate", visitStartDate);
					bundle.putString("visitEndDate", visitEndDate);
					bundle.putBoolean("make_or_modify", false);
					intent.putExtras(bundle);
					((FragmentActivity) context).startActivityForResult(intent, 0);

				}
			});
			// 提交
			viewHolder.btn_submit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onsubmitClickPosition = position;
					upservice = new UploadDataService(context, handler);
					Log.e(TAG, "提交查询的 plankey = " + planKey);
					Toast.makeText(getActivity(), "上传中，请等待", Toast.LENGTH_SHORT).show();
					upservice.upload_work_plans(false, planKey);
				}
			});
			// 预览
			viewHolder.btn_preview.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, MakeWeekPlanActivity.class);
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
			if (flag) {
				switch (workPlanStc.getState()) {
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

				}
			}

			return convertView;
		}

		class ViewHolder {
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
