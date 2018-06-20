package et.tsingtaopad.operation.weekworkplan;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstPlancheckInfo;
import et.tsingtaopad.db.tables.MstPlancollectionInfo;
import et.tsingtaopad.db.tables.MstPlanforuserM;
import et.tsingtaopad.db.tables.MstPlanrouteInfo;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.db.tables.PadPlantempcheckM;
import et.tsingtaopad.db.tables.PadPlantempcollectionInfo;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.operation.workplan.MakePlanViewPagerAdapter;
import et.tsingtaopad.operation.workplan.domain.TypeStc;
import et.tsingtaopad.operation.workplan.domain.VpLvItemStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ReflectUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.agencyvisit.LedgerPagerAdapter;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br> 文件名：MakePlanActivity.java</br> 作者：wangshiming </br>
 * 创建时间：2014年1月2日</br> 功能描述:做计划界面< </br> 版本 V 1.0</br> 修改履历</br> 促销活动
 * 产品选择查询当前区域下有效产品 日期 原因 BUG号 修改人 修改版本</br>
 */
public class MakeWeekPlanActivity extends BaseActivity implements OnClickListener {
	private final static String TAG = "MakeWeekPlanActivity";
	private Boolean make_or_modify = true;// 制定: true ,修改:false
	private Boolean preview_plan = false;// make_or_modify=false;
											// preview_plan=true 的时候表示预览
											// 通过修改功能达到回显数据 preview_plan 禁止触发事件
	private Button btn_back;
	private TextView btn_sure;// 确认
	private TextView tv_title;
	private TextView tv_time;
	private TextView tv_total;
	private TextView tv_lineselect;
	private TextView tv_worktime;
	private ListView mListView;
	private ViewPager mViewPager;
	private PagerTabStrip tabStrip;
	private ProgressDialog progressDialog;
	private PopupWindow mPopupWindow;
	private Calendar canlendar;
	private String visitStartDate;// 上午开始日期
	private String visitEndDate;// 下午开始日期
	private String date;// 计划日期
	private String planDate;// 由date 转成yyyyMMdd的形式计划日期
	private int week;// 周几 0 代表周日 1代表周一依次往下排
	private String plankey;// 计划人员主键
	private List<String> checkkeys = new ArrayList<String>();
	private List<View> viewList = new ArrayList<View>();
	private List<String> viewTitles = new ArrayList<String>();
	private List<BaseAdapter> adapters = new ArrayList<BaseAdapter>();// 存放每一个页面的adapter
	private List<List<VpLvItemStc>> list_VpLvItemStcs = new ArrayList<List<VpLvItemStc>>();// 存放每一个页面的List<VpLvItemStc>
	private CopyOnWriteArrayList<MstProductM> productList; // 产品list
	private List<PadPlantempcheckM> planTempChecks = new ArrayList<PadPlantempcheckM>(); // 指标list
	private List<MstPromotionsM> validPromotions = new ArrayList<MstPromotionsM>();
	private int blankPosition = 0;// 空白终端所在的位置
	private int daojuPosition = 0;// 道具生动化所在的位置
    private int chanpinPosition = 0;// 产品生动化所在的位置
    private int bingdongPosition = 0;// 冰冻化所在的位置
    private int jingpinPosition = 0;// 竞品推进计划所在的位置
	private String hourtime;
	private String minuteStr;
	private List<String> defaultLineKeyList;// 线路主键
	// private int hour_am1 = 8, minute_am1 = 0, minute_am2 = 0, hour_am2 = 12,
	// minute_pm1 = 0, hour_pm1 = 13, hour_pm2 = 18, minute_pm2 = 0;
	private MakeWeekPlanService service;

	private List<MstRouteM> lineSelectLst = new ArrayList<MstRouteM>();// 拜访线路的所选择线路列表
	private List<MstPlanrouteInfo> planrouteInfos;// 修改时，查询上次制定的线路列表
	private List<TypeStc> tyepList = new ArrayList<TypeStc>();
	private LinearLayout root;
	private DatabaseHelper helper;
	private Dao<MstPlancheckInfo, String> mstPlancheckInfoDao;
	private Dao<MstPlancollectionInfo, String> mstPlancollectionInfoDao;
	private Dao<MstProductM, String> mstProductMDao;
	private Dao<PadPlantempcollectionInfo, String> padPlantempcollectionInfoDao;
	private Dao<MstPlanrouteInfo, String> mstPlanrouteInfoDao;
	private Dao<MstPromotionsM, String> mstPromotionsMDao;
	private Dao<MstPlanforuserM, String> mstPlanforuserMDao;
	private final int INITFINISH = 1;
	CopyOnWriteArrayList<MstRouteM> routres = new CopyOnWriteArrayList<MstRouteM>();
	private List<BlankTermLevelStc> levelLst = new ArrayList<BlankTermLevelStc>();// 空白终端中的终端等级
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {

			// 提示信息
			case INITFINISH:
				progressDialog.dismiss();
				initView();
				initViewData();
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.operation_makeweekplan);
		service = new MakeWeekPlanService(this, handler);
		Intent intent = this.getIntent();
		date = intent.getExtras().getString("date");

		visitStartDate = intent.getExtras().getString("visitStartDate");
		visitEndDate = intent.getExtras().getString("visitEndDate");
		week = intent.getExtras().getInt("position");
		plankey = intent.getExtras().getString("plankey");
		make_or_modify = intent.getExtras().getBoolean("make_or_modify", true);
		preview_plan = intent.getExtras().getBoolean("preview_plan", false);
		canlendar = Calendar.getInstance();
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("初始化中...");
		progressDialog.show();
		helper = DatabaseHelper.getHelper(this);

		try {
			mstPlancheckInfoDao = helper.getMstPlancheckInfoDao();
			mstPlancollectionInfoDao = helper.getMstPlancollectionInfoDao();
			padPlantempcollectionInfoDao = helper.getPadPlantempcollectionInfoDao();
			mstProductMDao = helper.getMstProductMDao();
			mstPromotionsMDao = helper.getMstPromotionsMDao();
			mstPlanforuserMDao = helper.getMstPlanforuserMDao();
			mstPlanrouteInfoDao = helper.getMstPlanrouteInfoDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		initData();

	}

	/**
	 * 查询数据库初始化页面所有数据
	 */
	private void initData() {

		Thread thread = new Thread() {

			@Override
			public void run() {
				planDate = DateUtil.formatDate(DateUtil.parse(date, "yyyy-MM-dd"), "yyyyMMdd");// 计划日期转成yyyyMMddd的形式

				// 初始化计划模板数据
				try {
					//MST_PLANTEMPCHECK_INFO(计划模板指标信息表)
					Dao<PadPlantempcheckM, String> padPlantempcheckMDao = helper.getPadPlantempcheckMDao();
					if (make_or_modify) {
						planTempChecks = padPlantempcheckMDao.queryForAll();
					} else {
						//MST_PLANFORUSER_M(人员计划主表)
						MstPlanforuserM planforuserM = mstPlanforuserMDao.queryForId(plankey);
						//查询人员计划主表得到计划模板主键,在根据计划模板主键来查询计划模板指标信息表
						String plantempkey = planforuserM.getPlantempkey();
						
						//根据用户查询计划
						
						planTempChecks = padPlantempcheckMDao.queryForEq("plantempkey", plantempkey);

						// 获取上次制定后的线路数据   MST_PLANROUTE_INFO(计划相关线路信息)
						planrouteInfos = mstPlanrouteInfoDao.queryForEq("plankey", plankey);

					}
					//planTempChecks.add(new PadPlantempcheckM("kongbaizhongduanf12321", "7c510b7a-405d-47f0-b265-eceb48acc1a9", "20140211temp", "空白终端", "", "2"));

					// 初始化线路数据
					List<MstRouteM> lineLst = new ArrayList<MstRouteM>(ConstValues.lineLst);
					if (lineLst.size() > 0) {
						for (int i = 0; i < lineLst.size(); i++) {
							if (!"请选择".equals(lineLst.get(i).getRoutename()) || !"-1".equals(lineLst.get(i).getRoutekey())) {
								routres.add(lineLst.get(i));
							}
						}
					}
					
					if (routres.isEmpty()) {
						preview_plan = true;// 当没有线路选择的时候 当做预览 什么都不允许操作
					}
					// 初始化有效活动 查询指定计划在有效范围内
					QueryBuilder<MstPromotionsM, String> queryBuilder = mstPromotionsMDao.queryBuilder();
					queryBuilder.where().ge("enddate", planDate).and().le("startdate", planDate);
					validPromotions = queryBuilder.query();
					//List<MstProductM> products = service.queryProduct(MakeWeekPlanActivity.this, ConstValues.loginSession.getGridId(), ConstValues.loginSession.getDisId());
					List<MstProductM> products = service.queryProduct(MakeWeekPlanActivity.this, PrefUtils.getString(getApplicationContext(), "gridId", ""), PrefUtils.getString(getApplicationContext(), "disId", ""));
					productList = new CopyOnWriteArrayList<MstProductM>(products);

					// productList =
					// mstProductMDao.queryForAll();//修改成查询当前区域的可销售产品

					// 初始化空白终端终端等级
					List<KvStc> kvStcLst = ConstValues.dataDicMap.get("levelLst");
					if (kvStcLst.size() > 1 && ReflectUtil.getFieldValueByName("value", kvStcLst.get(0)).toString().equals("请选择"))
						kvStcLst.remove(0);
					for (KvStc kvStc : kvStcLst) {
						BlankTermLevelStc blankTermLevelStc = new BlankTermLevelStc();
						blankTermLevelStc.setKey(kvStc.getKey());
						blankTermLevelStc.setValue(kvStc.getValue() + "类终端");
						levelLst.add(blankTermLevelStc);
					}

				} catch (SQLException e) {
					Log.e(TAG, "MakePlanActivity INIT EXCEPTION:", e);
				} finally {
					handler.sendEmptyMessage(INITFINISH);
				}
			}
		};
		thread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		btn_back = (Button) findViewById(R.id.banner_navigation_bt_back);
		btn_sure = (TextView) findViewById(R.id.banner_navigation_bt_confirm);
		root = (LinearLayout) findViewById(R.id.root);
		tv_title = (TextView) findViewById(R.id.banner_navigation_tv_title);
		tv_time = (TextView) findViewById(R.id.makeweekplan_tv_time);
		tv_total = (TextView) findViewById(R.id.makeweekplan_tv_totalnum);
		tv_lineselect = (TextView) findViewById(R.id.makeweekplan_tv_line);
		tv_worktime = (TextView) findViewById(R.id.makeweekplan_tv_worktime);
		mListView = (ListView) findViewById(R.id.makeweekplan_lv_terminaltype);
		mListView.setEnabled(true);
		if (preview_plan) {
			tv_lineselect.setEnabled(false);
			btn_sure.setVisibility(View.GONE);
		} else {

			//btn_sure.setOnClickListener(this);
		}
		btn_back.setOnClickListener(this);// 返回

	}

	/**
	 * 初始化界面数据
	 */
	private void initViewData() {
		defaultLineKeyList = new ArrayList<String>();
		tv_title.setText(getString(R.string.weekworkplan_label_title));
		if (!routres.isEmpty()) {
			tv_lineselect.setHint(R.string.weekworkplan_line_select);
			tv_lineselect.setOnClickListener(this);
		} else {
			tv_lineselect.setEnabled(false);
			tv_lineselect.setHint("没有线路可供选择");
		}

		// 工作日期显示内容
		String monthDate = "";
		if (date.substring(6).equals("0"))
			monthDate = date.substring(7);
		else
			monthDate = date.substring(6, 7);
		tv_time.setText(date.subSequence(0, 4) + getResources().getString(R.string.weekworkplan_msg2) + monthDate + getResources().getString(R.string.weekworkplan_msg1) + " " + getWeek(week));

		Date date1 = DateUtil.parse(date, "yyyy-MM-dd");
		//	visitStartDate = DateUtil.getWeekBegin(date1, "yyyyMMdd");
		//	visitEndDate = DateUtil.getWeekBegin(date1, "yyyyMMdd");

		// 工作时间显示内容
		StringBuilder builder = new StringBuilder();
		String weekBegin = DateUtil.getWeekBegin(date1, "yyyy年MM月dd日");
		String weekEnd = DateUtil.getWeekEnd(date1, "yyyy年MM月dd日");
		builder.append(weekBegin).append("~").append(weekEnd);
		tv_worktime.setText(builder.toString());

		if (make_or_modify) {
			if (!routres.isEmpty()) {
				defaultLineKeyList.add(routres.get(0).getRoutekey());
				lineSelectLst.add(routres.get(0));
				tv_lineselect.setText(routres.get(0).getRoutename());
			}

		} else {
			// 设置拜访线路的已选择列表
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < planrouteInfos.size(); i++) {
				String routeKey = planrouteInfos.get(i).getCheckkey();
				defaultLineKeyList.add(routeKey);
				for (MstRouteM info : routres) {
					if (routeKey.equals(info.getRoutekey())) {
						sb.append(info.getRoutename()).append(",");
						break;
					}
				}
			}
			sb.delete(sb.length()-1, sb.length());
			tv_lineselect.setText(sb.toString());
		}
		if (!CheckUtil.IsEmpty(defaultLineKeyList)) {
			initTypeData(defaultLineKeyList);
		}
		initViewPager();

	}

	private String getWeek(int week) {
		String dayOfWeek = "";
		switch (week) {
		case 0:
			dayOfWeek = "第一周";
			break;
		case 1:
			dayOfWeek = "第二周";
			break;
		case 2:
			dayOfWeek = "第三周";
			break;
		case 3:
			dayOfWeek = "第四周";
			break;
		case 4:
			dayOfWeek = "第五周";
			break;

		default:
			break;
		}
		return dayOfWeek;

	}

	/**
	 * 初始化viewpagger
	 */
	private void initViewPager() {

		viewList.clear();
		viewTitles.clear();
		adapters.clear();
		list_VpLvItemStcs.clear();
		checkkeys.clear();

		mViewPager = (ViewPager) findViewById(R.id.makeweekplan_vp);
		tabStrip = (PagerTabStrip) findViewById(R.id.makeweekplan_vp_tab);
		tabStrip.setTextColor(getResources().getColor(R.color.agency_viewpager_title));
		tabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
		LayoutInflater inflater = LayoutInflater.from(this);
		if (planTempChecks != null && !planTempChecks.isEmpty()) {
			for (int i = 0; i < planTempChecks.size(); i++) {
				View view = inflater.inflate(R.layout.operation_makeplan_viewpagger, null);
				NoScrollListView mNoScrollListView = (NoScrollListView) view.findViewById(R.id.vp_lv);
				//采集项名称
                TextView tv_caijiname = (TextView) view.findViewById(R.id.vp_tv_title_name0);
              //名称
                TextView tv_name = (TextView) view.findViewById(R.id.vp_tv_title_name1);
				Button btn_add = (Button) view.findViewById(R.id.vp_btn_add);
				PadPlantempcheckM padPlantempcheckM = planTempChecks.get(i);
				String checkname = padPlantempcheckM.getCheckname();
				String checkkey = padPlantempcheckM.getCheckkey();
				//操作
                TextView vp_tv_title_operation = (TextView) view.findViewById(R.id.vp_tv_title_operation);
                View operation_divider = (View) view.findViewById(R.id.operation_divider);
				// 道具生动化
               boolean isDaoJu = getResources().getString(R.string.plan_daojutitle).equals(checkname);
               // 产品生动化
               boolean isChanPin = getResources().getString(R.string.plan_product).equals(checkname);
               // 冰冻化
               boolean isBingDong = getResources().getString(R.string.plan_bingdongtitle).equals(checkname);
               // 竞品推进计划
               boolean isJingPin = getResources().getString(R.string.plan_jingpintitle).equals(checkname);
               
				//初始化viewPager 上的view
				//空白终端显示按钮不是空白终端隐藏按钮
				if (getResources().getString(R.string.plan_blantitle).equals(checkname)) {
					// 修改列表名称
					//TextView tv_name = (TextView) view.findViewById(R.id.vp_tv_title_name1);
					TextView tv_terminaltype = (TextView) view.findViewById(R.id.vp_tv_title_name2);
					TextView tv_rate = (TextView) view.findViewById(R.id.vp_tv_title_name3);
					tv_name.setText(getString(R.string.makeplan_proname));
					tv_terminaltype.setText(R.string.weekworkplan_temp_name2);
					tv_rate.setText(R.string.weekworkplan_temp_name3);

					// 显示添加按钮
					btn_add.setVisibility(View.VISIBLE);
					if (preview_plan) {
						btn_add.setVisibility(View.GONE);
					} else {
						btn_add.setOnClickListener(this);
					}
					blankPosition = i;
				} else {
					btn_add.setVisibility(View.GONE);
				}

				//初始化viewPager 上的view 的adapter
				ArrayList<VpLvItemStc> vpLvItemStcS = new ArrayList<VpLvItemStc>();
				boolean isPromotion = false;
				if (getResources().getString(R.string.plan_promotion).equals(planTempChecks.get(i).getCheckname())) {
					isPromotion = true;
				} else {
					isPromotion = false;
				}
				// 其它
				boolean isother = false;
                if(getResources().getString(R.string.plan_other).equals(planTempChecks.get(i).getCheckname())){
                	isother = true;
                }else{
                	isother = false;
                }
                
                boolean isGezhonghua = false;//各种XX化
                boolean isjingpintuijin = false;
                // 各种化
                if(isDaoJu || isChanPin || isBingDong){
                	btn_add.setVisibility(View.VISIBLE);
                    btn_add.setTag(checkname);
                    tv_caijiname.setVisibility(View.VISIBLE);
                    tv_caijiname.setText(getString(R.string.makeplan_proname));//并把   采集项名称  改为   采集项名称
                    tv_name.setText(getString(R.string.makeplan_proname));//并把   名称  改为   产品名称
                    if (preview_plan)
                    {
                        btn_add.setVisibility(View.GONE);
                    }
                    else
                    {
                    	//
                        vp_tv_title_operation.setVisibility(View.VISIBLE);
                        //操作可见
                        operation_divider.setVisibility(View.VISIBLE);
                        btn_add.setOnClickListener(this);
                    }
                    if (isDaoJu)
                    {
                    	daojuPosition = i;
                    }
                    else if (isChanPin)
                    {
                    	chanpinPosition = i;
                    }
                    else if (isBingDong)
                    {
                    	bingdongPosition = i;
                    }
                    isGezhonghua = true;//各种XX化
                }
                // 竞品推进计划
                if(isJingPin){

                	btn_add.setVisibility(View.VISIBLE);
                	tv_caijiname.setVisibility(View.VISIBLE);
                    btn_add.setTag(checkname);
                    tv_caijiname.setText(getString(R.string.makeplan_pinpai));//并把   采集项名称  改为   品牌
                    tv_name.setText(getString(R.string.makeplan_proname));//并把   名称  改为   产品名称
                    if (preview_plan)
                    {
                        btn_add.setVisibility(View.GONE);
                    } 
                    else
                    {
                    	//
                        vp_tv_title_operation.setVisibility(View.VISIBLE);
                        //操作可见
                        operation_divider.setVisibility(View.VISIBLE);
                        btn_add.setOnClickListener(this);
                    }
                    if (isJingPin)
                    {
                    	jingpinPosition = i;
                    }
                    
                    isjingpintuijin = true;//各种XX化
                
                }
				
				if (getResources().getString(R.string.plan_blantitle).equals(checkname)) {//空白终端 创建不同的adapter
					WeekplanBlankTerminalAdapter adapter = new WeekplanBlankTerminalAdapter(this, vpLvItemStcS);
					mNoScrollListView.setAdapter(adapter);
					adapters.add(adapter);

				} else {
					String checkUse = planTempChecks.get(i).getCheckuse();
					MakePlanViewPagerAdapter adapter = new MakePlanViewPagerAdapter(this, vpLvItemStcS, checkkey, defaultLineKeyList, preview_plan, false, isPromotion,isother,isGezhonghua,isjingpintuijin,false,false,false,false);
					mNoScrollListView.setAdapter(adapter);
					adapters.add(adapter);

				}

				list_VpLvItemStcs.add(vpLvItemStcS);
				viewList.add(view);
				checkkeys.add(checkkey);
				viewTitles.add(checkname);
			}

			mViewPager.setVisibility(View.VISIBLE);
			initDataViewpager();// f
			mViewPager.setAdapter(new LedgerPagerAdapter(viewList, viewTitles));
			mViewPager.setCurrentItem(0);
			btn_sure.setVisibility(View.VISIBLE);
		} else {
			mViewPager.setVisibility(View.GONE);// 隐藏不给触碰，集合没有数据滚动会出现数组越界问题
			btn_sure.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Viewpager界面数据
	 * 
	 */
	int count = 0;

	private void initDataViewpager() {
		Log.i(TAG, "initDataViewpager :" + (++count));
		if (make_or_modify) {
			for (int i = 0; i < planTempChecks.size(); i++) {
				PadPlantempcheckM padPlantempcheckM = planTempChecks.get(i);
				String title = viewTitles.get(i);
				Log.i(TAG, "initDataViewpager title 制定" + title);
				String checkkey = padPlantempcheckM.getCheckkey();// 对应模板主键

				List<VpLvItemStc> vpLvItemStcS = list_VpLvItemStcs.get(i);
				//MakePlanViewPagerAdapter adapter = (MakePlanViewPagerAdapter) adapters.get(i);
				BaseAdapter adapter = adapters.get(i);
				if (getResources().getString(R.string.plan_blantitle).equals(viewTitles.get(i))) {
					// 空白终端数据
					for (int j = 0; j < 4; j++) {
						//VpLvItemStc stc = new VpLvItemStc("青岛啤酒青岛啤酒青岛啤酒青岛啤酒青岛啤酒青岛啤酒青岛啤酒青岛啤酒" + j, "key" + j, "5桶", "终端名称", levelLst);
						//vpLvItemStcS.add(stc);
					}

				} else if (getResources().getString(R.string.plan_promotion).equals(viewTitles.get(i))) {
					// 促销活动
					// checkkey查数据
					if (validPromotions.size() > 0) {
						for (int j = 0; j < validPromotions.size(); j++) {
							VpLvItemStc item = new VpLvItemStc();
							item.setName(validPromotions.get(j).getPromotname());
							item.setKey(validPromotions.get(j).getPromotkey());
							vpLvItemStcS.add(item);
						}
					}

				} else {
					List<PadPlantempcollectionInfo> colitemList = service.queryColitemname(checkkey);
					if (colitemList.size() > 0) {
						// 指标选项
						for (int j = 0; j < colitemList.size(); j++) {
							VpLvItemStc item = new VpLvItemStc();
							item.setName(colitemList.get(j).getColitemname());
							item.setKey(colitemList.get(j).getColitemkey());
							vpLvItemStcS.add(item);
						}

					}
				}
				adapter.notifyDataSetChanged();

			}
		} else {
			// /******************pad********************/
			// PadPlantempcheckM implements java.io.Serializable {
			// private String checkkey;主键
			// private String plantempkey;
			// A主键-->B多-->C多
			// /******************a MstPlanforuserM********************/
			// PLANKEY 已知主键
			// plantempkey 得到已知
			// /******************B MstPlancheckInfo********************/
			// private String pcheckkey;//
			// private String plankey;//得到模板表 checkkey 和表主键pcheckkey
			// private String checkkey;//
			// /******************c MstPlancollectionInfo********************/
			// private String pcolitemkey;//主键
			// private String pcheckkey;//已经知道
			// private String checkkey;// 已经知道
			// private String colitemkey;//
			// private String productkey;
			/**************
			 * colitemkey*******************PAD_PLANTEMPCOLLECTION_INFO
			 * 获取colitemname名称** ptcollkey
			 **/
			try {
				List<MstPlancheckInfo> plancheckInfos = mstPlancheckInfoDao.queryForEq("plankey", plankey);
				// planTempChecks
				if (!planTempChecks.isEmpty()) {
					for (int i = 0; i < planTempChecks.size(); i++) {
						PadPlantempcheckM padPlantempcheckM = planTempChecks.get(i);
						String checkkey = padPlantempcheckM.getCheckkey();
						String checkName = padPlantempcheckM.getCheckname();// 对应模板mincheng
						String pcheckkey = "";
						for (int j = 0; j < plancheckInfos.size(); j++) {// 不在查数据库
																			// 通过所有数据循环对比
							MstPlancheckInfo mstPlancheckInfo = plancheckInfos.get(j);
							if (checkkey.equals(mstPlancheckInfo.getCheckkey())) {
								pcheckkey = mstPlancheckInfo.getPcheckkey();
								break;
							}
						}
						// 找不到pcheckkey 数据对应不上了
						// planTempChecks
						String title = viewTitles.get(i);
						Log.i(TAG, "initDataViewpager title 回显" + title);
						// 取MstPlancollectionInfo 数据
						Map<String, Object> fieldValues = new HashMap<String, Object>();
						fieldValues.put("pcheckkey", pcheckkey);
						fieldValues.put("checkkey", checkkey);
						List<MstPlancollectionInfo> plancollectionInfos = mstPlancollectionInfoDao.queryForFieldValues(fieldValues);

						if ("空白终端".equals(checkName)) {
						} else {

							List<VpLvItemStc> vpLvItemStcS = list_VpLvItemStcs.get(i);
							MakePlanViewPagerAdapter adapter = (MakePlanViewPagerAdapter) adapters.get(i);
							for (int j = 0; j < plancollectionInfos.size(); j++) {
								MstPlancollectionInfo plancollectionInfo = plancollectionInfos.get(j);
								String colitemkey = plancollectionInfo.getColitemkey();
								String plantype = plancollectionInfo.getPlantype();
								// 0采集项1产品2活动
								if ("1".equals(plantype)) {

									MstProductM product = mstProductMDao.queryForId(colitemkey);
									for (MstProductM mproduct : productList) {
										if (colitemkey.equals(mproduct.getProductkey())) {
											productList.remove(mproduct);
										}
									}
									productList.remove(product);// 修改产品 的时候移除已经选择的产品
									if (product != null) {
										VpLvItemStc item = new VpLvItemStc();
										item.setName(product.getProname());
										item.setTerm(plancollectionInfo.getTermnames());
										item.setKey(plancollectionInfo.getProductkey());
										item.setNum(String.valueOf(plancollectionInfo.getTermnum()));
										vpLvItemStcS.add(item);
									}

								} else if ("2".equals(plantype)) {
									MstPromotionsM pormotion = mstPromotionsMDao.queryForId(colitemkey);
									if (pormotion != null) {
										VpLvItemStc item = new VpLvItemStc();
										item.setName(pormotion.getPromotname());
										item.setTerm(plancollectionInfo.getTermnames());
										item.setKey(plancollectionInfo.getColitemkey());
										item.setNum(String.valueOf(plancollectionInfo.getTermnum()));
										vpLvItemStcS.add(item);
									}

								} else {
									List<PadPlantempcollectionInfo> plantempcollectionInfo = padPlantempcollectionInfoDao.queryForEq("colitemkey", colitemkey);
									if (!plantempcollectionInfo.isEmpty()) {
										VpLvItemStc item = new VpLvItemStc();
										item.setName(plantempcollectionInfo.get(0).getColitemname());
										item.setTerm(plancollectionInfo.getTermnames());
										item.setKey(plancollectionInfo.getColitemkey());
										item.setNum(String.valueOf(plancollectionInfo.getTermnum()));
										vpLvItemStcS.add(item);
									}

								}
							}

							adapter.notifyDataSetChanged();
						}
					}
				}

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		}

	}

	/**
	 * 初始化终端数量显示内容
	 */
	public void initTypeData(List<String> keyList) {

		if (tyepList.size() != 0) {
			tyepList.clear();
		}
		Map<String, Integer> telvelMap = service.queryTelvel(keyList);
		Integer a = telvelMap.get("A");
		Integer b = telvelMap.get("B");
		Integer c = telvelMap.get("C");
		Integer d = telvelMap.get("D");
		Integer abcd = telvelMap.get("ABCD");
		if (a == null) {
			a = 0;
		}
		if (b == null) {
			b = 0;
		}
		if (c == null) {
			c = 0;
		}
		if (d == null) {
			d = 0;
		}
		if (abcd == null) {
			abcd = 0;
		}

		TypeStc typeStcA = new TypeStc();
		typeStcA.setType("A类");
		typeStcA.setNum(a + "");
		tyepList.add(typeStcA);
		TypeStc typeStcB = new TypeStc();
		typeStcB.setType("B类");
		typeStcB.setNum(b + "");
		tyepList.add(typeStcB);
		TypeStc typeStcC = new TypeStc();
		typeStcC.setType("C类");
		typeStcC.setNum(c + "");
		tyepList.add(typeStcC);
		TypeStc typeStcD = new TypeStc();
		typeStcD.setType("D类");
		typeStcD.setNum(d + "");
		tyepList.add(typeStcD);
		mListView.setAdapter(new TerminalCountAdapter(this, tyepList));
		tv_total.setText(String.valueOf(abcd));
	}

	/**
	 * 点击事件处理
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.banner_navigation_bt_back:
			this.finish();
			break;
		case R.id.vp_btn_add:
			// 空白终端添加
			if (productList == null || productList.isEmpty()) {
				Toast.makeText(getApplicationContext(), "没有可选择的产品", Toast.LENGTH_SHORT).show();
			} else {
				ShowPopuptWindow();
				mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
			}
			break;
		case R.id.makeweekplan_tv_line:
			showLineDialog();
			break;

		// case R.id.makeweekplan_btn_am1:
		// TimePickerDialog tpdam1 = new TimePickerDialog(v.getContext(), new
		// TimePickerDialog.OnTimeSetListener() {
		//
		// @Override
		// public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// hour_am1 = hourOfDay;
		// minute_am1 = minute;
		// if (hour_am1 < 12) {//开始必须小于12点
		// if (hour_am1 <= hour_am2) {//必须小于或者等于结束的小时
		// if (hour_am1 == hour_am2) {//同一个小时内
		// if (minute_am1 >= minute_am2) {//必须小于结束分钟 否则置成初始化
		// hour_am1 = 8;
		// hour_am2 = 12;
		// minute_am1 = 0;
		// minute_am2 = 0;
		// btn_am1.setText("08:00");
		// btn_am2.setText("12:00");
		// Toast.makeText(getApplicationContext(), "开始时间应早于结束时间",
		// Toast.LENGTH_SHORT).show();
		// } else {
		// if (hour_am1 < 10) {
		// hourtime = "0" + Integer.toString(hour_am1);
		// } else {
		// hourtime = Integer.toString(hour_am1);
		// }
		// if (minute_am1 < 10) {
		// minuteStr = "0" + minute_am1;
		// } else {
		// minuteStr = Integer.toString(minute_am1);
		// }
		// btn_am1.setText(hourtime + ":" + minuteStr);
		// }
		//
		// } else {//不是同一个小时
		// if (hour_am1 < 10) {
		// hourtime = "0" + Integer.toString(hour_am1);
		// } else {
		// hourtime = Integer.toString(hour_am1);
		// }
		// if (minute < 10) {
		// minuteStr = "0" + minute_am1;
		// } else {
		// minuteStr = Integer.toString(minute_am1);
		// }
		// btn_am1.setText(hourtime + ":" + minuteStr);
		// }
		//
		// } else {
		// hour_am1 = 8;
		// hour_am2 = 12;
		// minute_am1 = 0;
		// minute_am2 = 0;
		// btn_am1.setText("08:00");
		// btn_am2.setText("12:00");
		// Toast.makeText(getApplicationContext(), "开始时间应早于结束时间",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// } else {
		// hour_am1 = 8;
		// hour_am2 = 12;
		// minute_am1 = 0;
		// minute_am2 = 0;
		// btn_am1.setText("08:00");
		// btn_am2.setText("12:00");
		// Toast.makeText(getApplicationContext(), "开始时间不能超过12:00",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// }
		// }, hour_am1, minute_am1, true);
		// tpdam1.setCanceledOnTouchOutside(false);
		// if (!tpdam1.isShowing()) {
		// tpdam1.show();
		// }
		//
		// break;
		// case R.id.makeweekplan_btn_am2:
		// TimePickerDialog tpdam2 = new TimePickerDialog(v.getContext(), new
		// TimePickerDialog.OnTimeSetListener() {
		//
		// @Override
		// public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// hour_am2 = hourOfDay;
		// minute_am2 = minute;
		// if (hour_am2 <= 12) {//结束 必须小于等于12点
		// if (hour_am2 == 12) {//12的时候
		// if (minute_am2 > 0) {
		// Toast.makeText(getApplicationContext(), "结束时间不能超过12:00",
		// Toast.LENGTH_SHORT).show();
		// }
		// minute_am2 = 0;
		// btn_am2.setText("12:00");
		// } else if (hour_am1 <= hour_am2) {//必须小于或者等于结束的小时
		// if (hour_am1 == hour_am2) {//同一个小时内
		// if (minute_am1 < minute_am2) {//必须小于结束分钟 否则置成初始化
		// if (hour_am2 < 10) {
		// hourtime = "0" + Integer.toString(hour_am2);
		// } else {
		// hourtime = Integer.toString(hour_am2);
		// }
		// if (minute_am2 < 10) {
		// minuteStr = "0" + minute_am2;
		// } else {
		// minuteStr = Integer.toString(minute_am2);
		// }
		// btn_am2.setText(hourtime + ":" + minuteStr);
		//
		// } else {
		// hour_am1 = 8;
		// hour_am2 = 12;
		// minute_am1 = 0;
		// minute_am2 = 0;
		// btn_am1.setText("08:00");
		// btn_am2.setText("12:00");
		// Toast.makeText(getApplicationContext(), "开始时间应早于结束时间",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// } else {//不是同一个小时
		// if (hour_am2 < 10) {
		// hourtime = "0" + Integer.toString(hour_am2);
		// } else {
		// hourtime = Integer.toString(hour_am2);
		// }
		// if (minute_am2 < 10) {
		// minuteStr = "0" + minute_am2;
		// } else {
		// minuteStr = Integer.toString(minute_am2);
		// }
		// btn_am2.setText(hourtime + ":" + minuteStr);
		// }
		//
		// } else {
		// hour_am1 = 8;
		// hour_am2 = 12;
		// minute_am1 = 0;
		// minute_am2 = 0;
		// btn_am1.setText("08:00");
		// btn_am2.setText("12:00");
		// Toast.makeText(getApplicationContext(), "开始时间应早于结束时间",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// } else {
		// hour_am1 = 8;
		// hour_am2 = 12;
		// minute_am1 = 0;
		// minute_am2 = 0;
		// btn_am1.setText("08:00");
		// btn_am2.setText("12:00");
		// Toast.makeText(getApplicationContext(), "开始时间不能超过12:00",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// }
		// }, hour_am2, minute_am2, true);
		// tpdam2.setCanceledOnTouchOutside(false);
		// if (!tpdam2.isShowing()) {
		// tpdam2.show();
		// }
		//
		// break;
		// case R.id.makeweekplan_btn_pm1:
		// TimePickerDialog tpdpm1 = new TimePickerDialog(v.getContext(), new
		// TimePickerDialog.OnTimeSetListener() {
		//
		// @Override
		// public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// hour_pm1 = hourOfDay;
		// minute_pm1 = minute;
		// if (hour_pm1 >= 12) {//开始必须大于12点
		// if (hour_pm1 <= hour_pm2) {//必须小于或者等于结束的小时
		// if (hour_pm1 == hour_pm2) {//同一个小时内
		// if (minute_pm1 < minute_pm2) {//必须小于结束分钟 否则置成初始化
		// hourtime = Integer.toString(hour_pm1);
		// if (minute_pm1 < 10) {
		// minuteStr = "0" + minute_pm1;
		// } else {
		// minuteStr = Integer.toString(minute_pm1);
		// }
		// btn_pm1.setText(hourtime + ":" + minuteStr);
		// } else {
		// hour_pm1 = 13;
		// hour_pm2 = 18;
		// minute_pm1 = 0;
		// minute_pm2 = 0;
		// btn_pm1.setText("13:00");
		// btn_pm2.setText("18:00");
		// Toast.makeText(getApplicationContext(), "开始时间应早于结束时间",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// } else {//不是同一个小时
		// if (minute_pm1 < 10) {
		// minuteStr = "0" + minute_pm1;
		// } else {
		// minuteStr = Integer.toString(minute_pm1);
		// }
		// hourtime = Integer.toString(hour_pm1);
		// btn_pm1.setText(hourtime + ":" + minuteStr);
		// }
		//
		// } else {
		// hour_pm1 = 13;
		// hour_pm2 = 18;
		// minute_pm1 = 0;
		// minute_pm2 = 0;
		// btn_pm1.setText("13:00");
		// btn_pm2.setText("18:00");
		// Toast.makeText(getApplicationContext(), "开始时间应早于结束时间",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// } else {
		// hour_pm1 = 13;
		// hour_pm2 = 18;
		// minute_pm1 = 0;
		// minute_pm2 = 0;
		// btn_pm1.setText("13:00");
		// btn_pm2.setText("18:00");
		// Toast.makeText(getApplicationContext(), "开始时间必须超过12:00",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// }
		// }, hour_pm1, minute_pm1, true);
		// tpdpm1.setCanceledOnTouchOutside(false);
		// if (!tpdpm1.isShowing()) {
		// tpdpm1.show();
		// }
		//
		// break;
		// case R.id.makeweekplan_btn_pm2:
		// TimePickerDialog tpdpm2 = new TimePickerDialog(v.getContext(), new
		// TimePickerDialog.OnTimeSetListener() {
		//
		// @Override
		// public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// hour_pm2 = hourOfDay;
		// minute_pm2 = minute;
		// if (hour_pm2 >= 12) {//结束 必须大于等于12点
		// if (hour_pm2 >= hour_pm1) {//必须大于或者等于开始的小时
		// if (hour_pm1 == hour_pm2) {//同一个小时内
		// if (minute_pm2 > minute_pm1) {//必须大于开始分钟 否则置成初始化
		// hourtime = Integer.toString(hour_pm2);
		// if (minute_pm2 < 10) {
		// minuteStr = "0" + minute_pm2;
		// } else {
		// minuteStr = Integer.toString(minute_pm2);
		// }
		// btn_pm2.setText(hourtime + ":" + minuteStr);
		//
		// } else {
		// hour_pm1 = 13;
		// hour_pm2 = 18;
		// minute_pm1 = 0;
		// minute_pm2 = 0;
		// btn_pm1.setText("13:00");
		// btn_pm2.setText("18:00");
		// Toast.makeText(getApplicationContext(), "开始时间应早于结束时间",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// } else {//不是同一个小时
		//
		// hourtime = Integer.toString(hour_pm2);
		// if (minute_pm2 < 10) {
		// minuteStr = "0" + minute_pm2;
		// } else {
		// minuteStr = Integer.toString(minute_pm2);
		// }
		// btn_pm2.setText(hourtime + ":" + minuteStr);
		// }
		//
		// } else {
		// hour_pm1 = 13;
		// hour_pm2 = 18;
		// minute_pm1 = 0;
		// minute_pm2 = 0;
		// btn_pm1.setText("13:00");
		// btn_pm2.setText("18:00");
		// Toast.makeText(getApplicationContext(), "开始时间应早于结束时间",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// } else {
		// hour_pm1 = 13;
		// hour_pm2 = 18;
		// minute_pm1 = 0;
		// minute_pm2 = 0;
		// btn_pm1.setText("13:00");
		// btn_pm2.setText("18:00");
		// Toast.makeText(getApplicationContext(), "结束时间必须超过12:00",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		// }
		// }, hour_pm2, minute_pm2, true);
		// tpdpm2.setCanceledOnTouchOutside(false);
		// if (!tpdpm2.isShowing()) {
		// tpdpm2.show();
		// }
		// break;
		case R.id.banner_navigation_bt_confirm:
			if (CheckUtil.IsEmpty(lineSelectLst)) {
				Toast.makeText(this, "请选择线路", Toast.LENGTH_SHORT).show();
			} else
				savePlan();
			break;
		}
	}

	/**
	 * 空白终端添加 弹出popupwindow
	 * 
	 */
	private void ShowPopuptWindow() {

		View popupWindow = LayoutInflater.from(this).inflate(R.layout.operation_popupwindow, null);
		mPopupWindow = new PopupWindow(popupWindow, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent));
		mPopupWindow.setTouchable(true);
		Button btn_back = (Button) popupWindow.findViewById(R.id.list_pop_btn_back);
		btn_back.setVisibility(View.GONE);
		Button btn_ok = (Button) popupWindow.findViewById(R.id.list_pop_btn_ok);
		btn_ok.setVisibility(View.GONE);
		TextView tv_title = (TextView) popupWindow.findViewById(R.id.pop_tv_title);
		tv_title.setText(getString(R.string.makeplan_productselect));
		ListView listView = (ListView) popupWindow.findViewById(R.id.pop_lv);
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		BlankTerminalAddAdapter popAddAdapter = new BlankTerminalAddAdapter(this, productList);
		listView.setAdapter(popAddAdapter);

		final List<VpLvItemStc> blankVpLvItemStcs = list_VpLvItemStcs.get(blankPosition);
		final MakePlanViewPagerAdapter blankAdapter = (MakePlanViewPagerAdapter) adapters.get(blankPosition);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg1 = arg0.getChildAt(arg2);

				VpLvItemStc item = new VpLvItemStc();
				item.setName(productList.get(arg2).getProname());
				item.setKey(productList.get(arg2).getProductkey());
				blankVpLvItemStcs.add(item);
				productList.remove(arg2);
				blankAdapter.notifyDataSetChanged();
				mPopupWindow.dismiss();
			}
		});

	}

	/**
	 * 保存计划
	 */

	private void savePlan() {

		AndroidDatabaseConnection connection = null;
		try {
			MstPlanforuserM planforuserM = new MstPlanforuserM();// a
			MstPlancheckInfo plancheckInfo = new MstPlancheckInfo();// b
			MstPlancollectionInfo plancollectionInfo = new MstPlancollectionInfo();// c

			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);

			//String userCode = ConstValues.loginSession.getUserCode();
			String userCode = PrefUtils.getString(getApplicationContext(), "userCode", "");
			String plantempkey = planTempChecks.get(0).getPlantempkey();
			Date createDate = canlendar.getTime();

			if (make_or_modify) {
				// 制定
				// 保存到人员计划主表
				planforuserM.setPlankey(plankey); // 计划主键
				Log.i(TAG, "plankey 保存/修改MstPlanforuserMA主键" + plankey);
				planforuserM.setPlantitle(getWeek(week));// 计划标题
				StringBuilder builder = new StringBuilder();
				builder.append(planDate.substring(0, 6));
				builder.append(week + 1);
				planforuserM.setPlandate(builder.toString());// 计划日期
				// 通过PAD_PLANTEMPCHECK得到4个主键都一样
				planforuserM.setPlantempkey(plantempkey);// 计划模版主键
				planforuserM.setPlanamf(visitStartDate);
				planforuserM.setPlanpmf(visitEndDate);
				planforuserM.setPlantype(ConstValues.FLAG_1);
				planforuserM.setPlanstatus(ConstValues.FLAG_0);
				planforuserM.setUserid(userCode);
				planforuserM.setCredate(createDate);
				planforuserM.setPadisconsistent("0");
				planforuserM.setDeleteflag("0");
				planforuserM.setCreuser(userCode);

			} else {
				// 修改
				planforuserM = mstPlanforuserMDao.queryForId(plankey);
				if (planforuserM == null) {
					Log.e(TAG, "需要修改的对象为null 数据存在有问题请检查");
				}
			}
			planforuserM.setUpdateuser(userCode);
			planforuserM.setUpdatetime(createDate);
			planforuserM.setPlanstatus("0"); // 计划状态 = 0 未审核
			mstPlanforuserMDao.createOrUpdate(planforuserM);

			// 保存计划相关线路信息
			if (make_or_modify) {
				MstPlanrouteInfo mstPlanrouteInfo = new MstPlanrouteInfo();
				for (MstRouteM routeM : lineSelectLst) {
					mstPlanrouteInfo.setPlanroutekey(FunUtil.getUUID());
					mstPlanrouteInfo.setPlankey(plankey);
					mstPlanrouteInfo.setCheckkey(routeM.getRoutekey());
					mstPlanrouteInfo.setCredate(createDate);
					mstPlanrouteInfo.setCreuser(userCode);
					mstPlanrouteInfo.setUpdatetime(createDate);
					mstPlanrouteInfo.setUpdateuser(userCode);
					mstPlanrouteInfoDao.create(mstPlanrouteInfo);
				}
			} else {

				// if(//之前选过的路线作比较
				// true ) {
				//
				// } else {
				// for(MstRouteM routeM : lineSelectLst) {
				// MstPlanrouteInfo mstPlanrouteInfo = new MstPlanrouteInfo();
				// mstPlanrouteInfo.setPlanroutekey(FunUtil.getUUID());
				// mstPlanrouteInfo.setPlankey(plankey);
				// mstPlanrouteInfo.setCheckkey(routeM.getRoutekey());
				// mstPlanrouteInfo.setCredate(createDate);
				// mstPlanrouteInfo.setCreuser(userCode);
				// mstPlanrouteInfo.setUpdatetime(createDate);
				// mstPlanrouteInfo.setUpdateuser(userCode);
				// planrouteInfoDao.createOrUpdate(mstPlanrouteInfo);
				// }
				// }

			}

			// 保存计划指标信息
			// 计划主键PLANKEY
			Log.e(TAG, "plantempkey PAD端计划模板指标主表 中的模板主键" + plantempkey);
			for (int i = 0; i < planTempChecks.size(); i++) {// b
				String checkkey = planTempChecks.get(i).getCheckkey();
				String pcheckkey = FunUtil.getUUID();
				// 获取B表的主键, 当不是修改重新生成主键
				if (make_or_modify) {
					plancheckInfo.setPlankey(plankey); // 计划主键
					plancheckInfo.setPcheckkey(pcheckkey); // 计划指标主键
					plancheckInfo.setCheckkey(checkkey); // 指标主键
					plancheckInfo.setCredate(createDate);
					plancheckInfo.setCreuser(userCode);
					plancheckInfo.setUpdateuser(userCode);
				} else {
					Map<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("checkkey", checkkey);
					hashMap.put("plankey", plankey);
					List<MstPlancheckInfo> mstplancheckinfos = mstPlancheckInfoDao.queryForFieldValues(hashMap);
					Log.i(TAG, "mstplancheckinfos 实际只有一条数据出现多个说明存储有错：" + mstplancheckinfos.size());
					if (!mstplancheckinfos.isEmpty()) {
						plancheckInfo = mstplancheckinfos.get(0);
						pcheckkey = plancheckInfo.getPcheckkey();// 查询
						plancheckInfo.setUpdatetime(new Date());
						plancheckInfo.setUpdateuser(userCode);
					}

				}
				mstPlancheckInfoDao.createOrUpdate(plancheckInfo);
				List<VpLvItemStc> plancheckInfoDatas = list_VpLvItemStcs.get(i); // 获取当前view里的list
				Log.e(TAG, "pcheckkey MstPlancheckInfo B主键" + pcheckkey);
				Log.e(TAG, "checkkey PAD端计划模板指标主表中的主键" + checkkey);
				String viewPagertitle = viewTitles.get(i);
				if (getResources().getString(R.string.plan_blantitle).equals(viewPagertitle)) {
					// 空白终端
					if (make_or_modify) {
						List<VpLvItemStc> list = list_VpLvItemStcs.get(i);
						for (VpLvItemStc stc : list) {
							//MstPlancollectionInfo plancollectionInfo = new MstPlancollectionInfo();
							for (BlankTermLevelStc levelStc : stc.getTerminals()) {
								plancollectionInfo.setPcolitemkey(FunUtil.getUUID());
								plancollectionInfo.setPcheckkey(pcheckkey);
								plancollectionInfo.setCheckkey(checkkey);
								plancollectionInfo.setColitemkey(stc.getKey());
								plancollectionInfo.setPlantype(ConstValues.FLAG_1);
								plancollectionInfo.setTermlevel(levelStc.getKey());
								plancollectionInfo.setTermnames(levelStc.getValue());
								if (CheckUtil.isBlankOrNull(levelStc.getRate())) {
									plancollectionInfo.setTermnum(Long.valueOf("0"));
								} else
									plancollectionInfo.setTermnum(Long.valueOf(levelStc.getRate()));

								plancollectionInfo.setCredate(createDate);
								plancollectionInfo.setUpdatetime(createDate);
								plancollectionInfo.setCreuser(userCode);
								plancollectionInfo.setUpdatetime(createDate);
								plancollectionInfo.setUpdateuser(userCode);
								mstPlancollectionInfoDao.createOrUpdate(plancollectionInfo);
							}
						}
					} else {
						//和上次查询产品的数据比较

					}

				} else {

					if (plancheckInfoDatas != null && plancheckInfoDatas.size() > 0) {
						// 查MstPlancollectionInfo表数据 id不能变
						Log.i(TAG, "处理“" + viewPagertitle + "”的数据");
						for (int j = 0; j < plancheckInfoDatas.size(); j++) {
							// c
							// plancheckInfoDatas
							String pcolitemkey = FunUtil.getUUID();
							String colitemkey = plancheckInfoDatas.get(j).getKey();
							Log.e(TAG, "colitemkey MstPlancollectionInfo // 产品主键/组合主键  KT版主键 产品主键" + colitemkey);
							if (make_or_modify) {
								plancollectionInfo.setCredate(createDate);
								plancollectionInfo.setPcheckkey(pcheckkey);// 计划指标主键
								plancollectionInfo.setCheckkey(checkkey);// 指标主键
								plancollectionInfo.setPcolitemkey(pcolitemkey);// 计划采集项主键
								Log.e(TAG, "pcolitemkey MstPlancollectionInfo 生成C主键" + pcolitemkey);

								plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
								plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
								plancollectionInfo.setColitemkey(colitemkey);// 采集项主键
								plancollectionInfo.setUpdateuser(userCode);
								plancollectionInfo.setCreuser(userCode);

								if (getResources().getString(R.string.plan_promotion).equals(viewPagertitle)) {// 促销活动推进
									plancollectionInfo.setPlantype("2");
								} else {
									plancollectionInfo.setPlantype("0");
								}
							} else {
								Map<String, Object> hashMap = new HashMap<String, Object>();
								hashMap.put("checkkey", checkkey);
								hashMap.put("pcheckkey", pcheckkey);
								hashMap.put("colitemkey", colitemkey);

								List<MstPlancollectionInfo> plancollectionInfos = mstPlancollectionInfoDao.queryForFieldValues(hashMap);
								Log.i(TAG, "MstPlancollectionInfo 实际只有一条数据,出现多个个说明存储有错：" + plancollectionInfos.size());

								if (plancollectionInfos.isEmpty()) {
									Log.i(TAG, viewPagertitle + "---查询不到此数据，说明是空白终端新增的数据或者数据库表被修改了");
									// plancollectionInfo.setPcheckkey(pcheckkey);//
									// 计划指标主键
									// plancollectionInfo.setCheckkey(checkkey);//
									// 指标主键
									// plancollectionInfo.setPcolitemkey(pcolitemkey);//
									// 计划采集项主键
								} else {
									plancollectionInfo = plancollectionInfos.get(0);
									Log.i(TAG, "查plancollectionInfos size:" + plancollectionInfos.size() + "实际 有一个数据");
									Log.e(TAG, "MstPlancollectionInfo 修改的C主键" + pcolitemkey);
									plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
									plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
									plancollectionInfo.setUpdateuser(userCode);
									plancollectionInfo.setColitemkey(colitemkey);// 采集项主键
								}

							}

							mstPlancollectionInfoDao.createOrUpdate(plancollectionInfo);
						}
					}
				}
			}

			connection.commit(null);
			ViewUtil.sendMsg(this, R.string.succes);
		} catch (SQLException e) {
			e.printStackTrace();
			ViewUtil.sendMsg(this, R.string.saveerror);
			try {
				if (connection != null) {
					connection.rollback(null);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		MakeWeekPlanActivity.this.finish();

	}

	/**
	 * 线路列表选择框
	 */
	public void showLineDialog() {
		final List<MstRouteM> lineDialogLst = new ArrayList<MstRouteM>();
		View proView = LayoutInflater.from(this).inflate(R.layout.operation_indexstatus_dialogpro, null);
		final ListView proDialogListView = (ListView) proView.findViewById(R.id.indexstatus_lv_dialogpro);
		Button ok = (Button) proView.findViewById(R.id.indexstatus_bt_dialogpro_ok);
		Button quxiao = (Button) proView.findViewById(R.id.indexstatus_bt_dialogpro_quxiao);

		// 绑定线路选择列表的ListView数据
		WeekworkplanLineDialogAdapter adapter = new WeekworkplanLineDialogAdapter(this, routres);
		proDialogListView.setAdapter(adapter);
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setView(proView, 0, 0, 0, 0);
		dialog.show();
		proDialogListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				if (lineDialogLst.contains(routres.get(arg2))) {
					lineDialogLst.remove(routres.get(arg2));
					arg0.getChildAt(arg2).setBackgroundColor(getResources().getColor(R.color.bg_page_color));
				} else {
					lineDialogLst.add(routres.get(arg2));
					arg0.getChildAt(arg2).setBackgroundColor(getResources().getColor(R.color.bg_content_color_orange));
				}
			}
		});

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!CheckUtil.IsEmpty(lineDialogLst)) {
					lineSelectLst = lineDialogLst;
					List<String> stcList = new ArrayList<String>();
					String text = "";

					for (int i = 0; i < lineSelectLst.size(); i++) {
						text += lineSelectLst.get(i).getRoutename() + ",";
						stcList.add(lineSelectLst.get(i).getRoutekey());
					}
					text = text.substring(0, text.length() - 1);
					tv_lineselect.setText(text);
					initTypeData(stcList);
					initViewPager();
					dialog.dismiss();
				} else {
					Toast.makeText(MakeWeekPlanActivity.this, "请选择一条线路", Toast.LENGTH_SHORT).show();
				}
			}
		});
		quxiao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();
			}
		});
	}
}
