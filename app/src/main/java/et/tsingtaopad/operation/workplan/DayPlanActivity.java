package et.tsingtaopad.operation.workplan;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.ListViewKeyValueAdapter;
import et.tsingtaopad.adapter.ListViewProductAdapter;
import et.tsingtaopad.adapter.MakePlanProductAdapter;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstCmpcompanyM;
import et.tsingtaopad.db.tables.MstCmproductinfoM;
import et.tsingtaopad.db.tables.MstCollectionitemM;
import et.tsingtaopad.db.tables.MstPlanTerminalM;
import et.tsingtaopad.db.tables.MstPlancheckInfo;
import et.tsingtaopad.db.tables.MstPlancollectionInfo;
import et.tsingtaopad.db.tables.MstPlanforuserM;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.PadPlantempcheckM;
import et.tsingtaopad.db.tables.PadPlantempcollectionInfo;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.operation.workplan.domain.TypeStc;
import et.tsingtaopad.operation.workplan.domain.VpLvItemStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.agencyvisit.LedgerPagerAdapter;
import et.tsingtaopad.visit.shopvisit.chatvie.ChatVieProductAdapter;
import et.tsingtaopad.visit.shopvisit.checkindex.IClick;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br> 文件名：MakePlanActivity.java</br> 作者：ywm </br>
 * 创建时间：2017年4月26日</br> 功能描述:新日计划界面< </br> 版本 V 1.0</br> 修改履历</br> 促销活动
 * 产品选择查询当前区域下有效产品 日期 原因 BUG号 修改人 修改版本</br>
 */
@SuppressLint("ShowToast")
public class DayPlanActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener {
	private final static String TAG = "MakePlanActivity";
	private Boolean make_or_modify = true;// 制定: true ,修改:false
	private Boolean preview_plan = false;// make_or_modify=false;
											// preview_plan=true 的时候表示预览
											// 通过修改功能达到回显数据 preview_plan 禁止触发事件
	private String dingDanCheckkey = "5e76aa22-d6a5-48e4-a198-9e836191bae7";
	private Button btn_back;
	private TextView btn_sure;// 确认
	private Button btn_am1;
	private Button btn_am2;
	private Button btn_pm1;
	private Button btn_pm2;
	private TextView tv_title;
	private TextView tv_time;
	private TextView tv_total;
	private Spinner mSpinner;
	private ListView mListView;
	private ViewPager mViewPager;
	private PagerTabStrip tabStrip;
	private ProgressDialog progressDialog;
	private PopupWindow mPopupWindow;
	private Calendar canlendar;
	// private String weekStartDate;//本周开始日期
	// private String weekEndDate;//本周结束日期
	private String date;// 计划日期
	private String planDate;// 由date 转成yyyyMMdd的形式计划日期
	private int week;// 周几 0 代表周日 1代表周一依次往下排
	private String plankey;// 计划人员主键
	private List<String> checkkeys = new ArrayList<String>();// 指标集合
	private List<View> viewList = new ArrayList<View>();// viewPager中view
	private List<String> viewTitles = new ArrayList<String>();// viewPager中view各个标题
	private List<MakePlanViewPagerAdapter> adapters = new ArrayList<MakePlanViewPagerAdapter>();// 存放每一个页面的adapter
	private List<List<VpLvItemStc>> list_VpLvItemStcs = new ArrayList<List<VpLvItemStc>>();// 存放每一个页面的List<VpLvItemStc>
	private List<PadPlantempcheckM> planTempChecks = new ArrayList<PadPlantempcheckM>(); // 指标list
	// private List<MstPromotionsM> validPromotions = new
	// ArrayList<MstPromotionsM>();
	private CopyOnWriteArrayList<MstProductM> blankProductList; // 空白终端产品list
	private CopyOnWriteArrayList<MstProductM> puhuoProductList; // 有效铺货产品list
	private CopyOnWriteArrayList<MstProductM> xiaoshouProductList; // 有效销售产品list
	private CopyOnWriteArrayList<MstProductM> dingdanProductList; // 订单目标list
	private CopyOnWriteArrayList<MstProductM> huaProductList; // 各种化产品list
	private int blankPosition = 0;// 空白终端所在的位置
	private int puhuoPosition = 0;// 有效铺货目标终端所在的位置
	private int xiaoshouPosition = 0;// 有效销售终端所在的位置
	private int daojuPosition = 0;// 道具生动化所在的位置
	private int chanpinPosition = 0;// 产品生动化所在的位置
	private int bingdongPosition = 0;// 冰冻化所在的位置
	private int sdhbzPosition = 0;// 生动化布置所在的位置
	private int jingpinPosition = 0;// 竞品清除计划(QC竞品)所在的位置
	private int dingdanPosition = 0;// 订单目标所在的位置
	private String defaultLineKey;// 线路主键
	private String editLineKey; // 编辑线路主键
	private List<String> colKeyLst = new ArrayList<String>(); // 编辑计划的采集项配置的id集合
	private int amStart = 800, amEnd = 1200, pmStart = 1300, pmEnd = 1800;
	private String amStartStr = "08:00", amEndStr = "12:00", pmStartStr = "13:00", pmEndStr = "18:00";
	private MakePlanService service;
	// 制定计划终端统计结构体集合
	private List<TypeStc> typeList = new ArrayList<TypeStc>();
	private LinearLayout root;
	private DatabaseHelper helper;
	private Dao<MstPlancheckInfo, String> mstPlancheckInfoDao;// 指标采集
	private Dao<MstPlancollectionInfo, String> mstPlancollectionInfoDao;// 计划指标与采集项信息
	private Dao<PadPlantempcollectionInfo, String> padPlantempcollectionInfoDao;// pad端计划模版指标对应采集项关联信息表
	private Dao<MstProductM, String> mstProductMDao;// 产品信息主表
	private Dao<MstCmproductinfoM, String> mstCmproductinfoMDao;// 产品信息主表
	private Dao<MstCmpcompanyM, String> mstCmpcompanyMDao;// 竞品公司主表
	private Dao<MstCollectionitemM, String> MstCollectionitemMDao;// 采集项主表
	private Dao<MstProductM, String> MstProductMDao;// 青啤产品信息主表
	private Dao<MstTerminalinfoM, String> MstTerminalinfoMDao;// 终端表
	private Dao<MstPlanTerminalM, String> MstPlanTerminalMDao;// 计划终端表
	private final int INITFINISH = 1;
	private final int DATAFINISH = 2;
	CopyOnWriteArrayList<MstRouteM> routres = new CopyOnWriteArrayList<MstRouteM>();
	private String planType_blank = "1";// 有效空白
	private String planType_puhuo = "3";// 有效铺货
	private String planType_xiaoshou = "4";// 有效销售

	// 供货关系弹出框相关实例
	private AlertDialog productDialgo;
	private View itemForm;
	private ListView proLv;// 采集项列表
	private ListView productLv1;// 采集项 产品名称列表

	private ListViewProductAdapter caijixiangAdapter;
	private MakePlanProductAdapter caijixiangproductAdapter;

	private VpLvItemStc pro;
	private MstProductM product1;
	private List<MstProductM> productes;

	private ListView agencyLv;// 品牌列表
	private ListView productLv;// 品牌 产品名称列表
	private List<KvStc> jp_productes;
	private ListViewKeyValueAdapter agencyAdapter;
	private ChatVieProductAdapter productAdapter;
	private KvStc agency;//
	private KvStc product;

	private et.tsingtaopad.view.ScrollViewWithListView mPlanList;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {

			// 提示信息
			case INITFINISH:
				// progressDialog.dismiss();
				initView();
				initViewData();

				break;

			case DATAFINISH:
				progressDialog.dismiss();

				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.operation_daymakeplan);
		// 制定工作计划业务处理
		service = new MakePlanService(this, handler);

		// 获取传递过来的数据
		date = this.getIntent().getExtras().getString("date");
		week = this.getIntent().getExtras().getInt("position");
		plankey = this.getIntent().getExtras().getString("plankey");
		make_or_modify = this.getIntent().getExtras().getBoolean("make_or_modify", true);
		preview_plan = this.getIntent().getExtras().getBoolean("preview_plan", false);

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
			mstCmproductinfoMDao = helper.getMstCmproductinfoMDao();
			mstCmpcompanyMDao = helper.getMstCmpcompanyMDao();
			mstPromotionsMDao = helper.getMstPromotionsMDao();
			mstPlanforuserMDao = helper.getMstPlanforuserMDao();// 人员计划主表
			MstCollectionitemMDao = helper.getMstCollectionitemMDao();// 采集项主表
			MstProductMDao = helper.getMstProductMDao();//
			MstTerminalinfoMDao = helper.getMstTerminalinfoMDao();
			MstPlanTerminalMDao = helper.getMstPlanTerminalM();
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
				// 计划日期转成yyyyMMddd的形式(将上一页面传递过来的时间数据,转换成"yyyy-MM-dd"格式)
				planDate = DateUtil.formatDate(DateUtil.parse(date, "yyyy-MM-dd"), "yyyyMMdd");

				// 初始化计划模板数据
				try {
					Dao<PadPlantempcheckM, String> padPlantempcheckMDao = helper.getPadPlantempcheckMDao();// pad端计划模版指标主表
					if (make_or_modify)// true 制定模式
					{
						// 查询同步下来的表,获取指标集合 20160315
						planTempChecks = padPlantempcheckMDao.queryForAll();
					} else // false 修改模式
					{

						MstPlanforuserM planforuserM = mstPlanforuserMDao.queryForId(plankey);// 计划主键
						String plantempkey = planforuserM.getPlantempkey();
						planTempChecks = padPlantempcheckMDao.queryForEq("plantempkey", plantempkey);// 计划模板主键plantempkey
					}
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
					// QueryBuilder<MstPromotionsM, String> queryBuilder =
					// mstPromotionsMDao.queryBuilder();
					// queryBuilder.where().ge("enddate",
					// planDate.replace("-","")).and().le("startdate",
					// planDate.replace("-", ""));
					// validPromotions = queryBuilder.query();

					// 查询空白终端添加产品list
					// List<MstProductM> products =
					// service.queryProduct(MakePlanActivity.this,
					// ConstValues.loginSession.getGridId(),
					// ConstValues.loginSession.getDisId());
					List<MstProductM> products = service.queryProduct(DayPlanActivity.this, PrefUtils.getString(getApplicationContext(), "gridId", ""), PrefUtils.getString(getApplicationContext(), "disId", ""));

					blankProductList = new CopyOnWriteArrayList<MstProductM>(products);
					puhuoProductList = new CopyOnWriteArrayList<MstProductM>(products);
					xiaoshouProductList = new CopyOnWriteArrayList<MstProductM>(products);
					dingdanProductList = new CopyOnWriteArrayList<MstProductM>(products);

					huaProductList = new CopyOnWriteArrayList<MstProductM>(products);

				} catch (SQLException e) {
					Log.e(TAG, "MakePlanActivity INIT EXCEPTION:", e);
				} finally {
					handler.sendEmptyMessage(INITFINISH);// 1
															// onCreate:获取上一页面传递过来的数据
															// 2
															// initDate:查数据库获取本页面所需基础数据
															// 3
															// 发送handler触发initView();
															// initViewData();
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
		makeplansv = (ScrollView) findViewById(R.id.makeplan_sv_all);
		btn_am1 = (Button) findViewById(R.id.makeplan_btn_am1);
		btn_am2 = (Button) findViewById(R.id.workplan_btn_am2);
		btn_pm1 = (Button) findViewById(R.id.workplan_btn_pm1);
		btn_pm2 = (Button) findViewById(R.id.makeplan_btn_pm2);
		tv_title = (TextView) findViewById(R.id.banner_navigation_tv_title);
		tv_time = (TextView) findViewById(R.id.makeplan_tv_time);
		tv_total = (TextView) findViewById(R.id.makeplan_tv_totalnum);
		mSpinner = (Spinner) findViewById(R.id.makeplan_sp_line);
		mListView = (ListView) findViewById(R.id.makeplan_lv_terminaltype);
		mListView.setItemsCanFocus(true);
		mListView.setEnabled(true);
		if (preview_plan)// 查看模式
		{
			mSpinner.setEnabled(false);
			btn_sure.setVisibility(View.GONE);
			btn_am1.setEnabled(false);
			btn_am2.setEnabled(false);
			btn_pm1.setEnabled(false);
			btn_pm2.setEnabled(false);
		} else {

			//btn_sure.setOnClickListener(this);
			btn_am1.setOnClickListener(this);
			btn_am2.setOnClickListener(this);
			btn_pm1.setOnClickListener(this);
			btn_pm2.setOnClickListener(this);
		}
		btn_back.setOnClickListener(this);// 返回

		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);

	}

	/**
	 * 初始化界面数据
	 */
	private void initViewData() {
		// 工作计划标题
		tv_title.setText(getString(R.string.workplan_daytitle));
		// 拜访线路设置Adapter
		if (!routres.isEmpty()) {
			mSpinner.setOnItemSelectedListener(this);
			SpinnerKeyValueAdapter adapter = new SpinnerKeyValueAdapter(this, routres, new String[] { "routekey", "routename" }, null);
			mSpinner.setAdapter(adapter);
		} else {
			mSpinner.setEnabled(false);
			MstRouteM mstRouteM = new MstRouteM("-2");
			mstRouteM.setRoutename("没有线路可供选择");
			routres.add(mstRouteM);
			SpinnerKeyValueAdapter adapter = new SpinnerKeyValueAdapter(this, routres, new String[] { "routekey", "routename" }, null);
			mSpinner.setAdapter(adapter);

		}
		// 工作日期
		tv_time.setText(date + " " + getWeek(week));
		// 拜访线路设置默认值
		if (make_or_modify)// 制定或修改 获取默认路线key
		{

			if (!routres.isEmpty())// 如果路线不为空(路线会在应用启动时进行初始化,放在ConstValues.lineLst中)
			{
				// 设置线路列表的第一条数据 作为默认线路
				defaultLineKey = routres.get(0).getRoutekey();
			}

		} else // 修改模式下 获取默认路线key
		{
			try {
				// 显示工作时间
				MstPlanforuserM planforuserM = mstPlanforuserMDao.queryForId(plankey);
				if (planforuserM != null) {
					amStartStr = planforuserM.getPlanamf();
					amEndStr = planforuserM.getPlanamt();
					pmStartStr = planforuserM.getPlanpmf();
					pmEndStr = planforuserM.getPlanpmt();
					btn_am1.setText(amStartStr);
					btn_am2.setText(amEndStr);

					btn_pm1.setText(pmStartStr);
					btn_pm2.setText(pmEndStr);

					amStart = Integer.valueOf(amStartStr.replace(":", ""));
					amEnd = Integer.valueOf(amEndStr.replace(":", ""));
					pmStart = Integer.valueOf(pmStartStr.replace(":", ""));
					pmEnd = Integer.valueOf(pmEndStr.replace(":", ""));

					defaultLineKey = planforuserM.getLinekey();
					editLineKey = planforuserM.getLinekey();

					Log.e(TAG, "修改计划线路主键  = " + defaultLineKey.toString());
					// 设置线路SP的默认值
					for (int i = 0; i < routres.size(); i++) {
						if (defaultLineKey.equals(routres.get(i).getRoutekey())) {
							mSpinner.setSelection(i, true);
							break;
						}
					}
				} else {
					Log.i(TAG, "查无此数据 ，出错了或者数据库被外部改过了");
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (defaultLineKey != null) {
			// 初始化终端数量显示内容
			// 初始化此渠道数量显示内容 // ywm 20160413
			initTypeData2(defaultLineKey);
		}
		// initViewPager();
		initListView();
		// 关闭缓冲界面
		handler.sendEmptyMessage(DATAFINISH);

	}

	/**
	 * 初始化listview
	 */
	private void initListView() {

		// viewPager中viewList (viewList:这是个list集合)
		viewList.clear();

		// 存放标题的List // viewPager中view各个标题 (viewTitles:这是个list集合)
		viewTitles.clear();

		// 存放每一个页面的adapter // 存放每一个页面的adapter (adapters:这是个list集合)
		adapters.clear();

		// 存放每一个页面的List<VpLvItemStc> (list_VpLvItemStcs:这是个list集合)
		list_VpLvItemStcs.clear();

		// 指标集合 // 指标如:有效铺货目标终端,生动化布置,其他,促销活动推进 指标都有各自的key(checkkeys:这是个list集合)
		checkkeys.clear();

		mPlanList = (et.tsingtaopad.view.ScrollViewWithListView) findViewById(R.id.makeplan_lv_list1);

		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 指标list
		if (planTempChecks != null && !planTempChecks.isEmpty())// 计划指标集合planTempChecks是查PlantempcheckM表得到的
		{
			// 如果是预览的,隐藏确定按钮
			if (preview_plan) {
				btn_sure.setVisibility(View.GONE);// 右上角确定按钮 设置隐藏
			} else {
				btn_sure.setVisibility(View.VISIBLE);
			}

			// 根据计划指标集合planTempChecks,初始化各个viewpager条目的数据(一个条目其实就是一个计划指标)
			// 另外预览模式下的一些按钮消失也在此for循环中处理
			mPlanList.setVisibility(View.VISIBLE);

			// 初始化数据
			// 根据计划指标集合planTempChecks,初始化各个viewpager条目的数据(一个条目其实就是一个计划指标)
			// 另外预览模式下的一些按钮消失也在此for循环中处理
			for (int i = 0; i < planTempChecks.size(); i++) {

				// 具体的一个计划指标
				PadPlantempcheckM padPlantempcheckM = planTempChecks.get(i);
				checkname = padPlantempcheckM.getCheckname();
				String checkKey = padPlantempcheckM.getCheckkey();

				// 下一界面(MakePlanViewPagerAdapter)用到的变量
				boolean isProductTerminal = false;// 产品终端(空白终端,有效销售,有效铺货)
				boolean isDaojuhua = false;// 道具生动化
				boolean isGezhonghua = false;// 各种XX化(产品生动化,冰冻化)
				boolean isChanpinhua = false;// 产品生动化
				boolean isBingdonghua = false;// 冰冻化
				boolean isjingpintuijin = false;// 竞品清除计划(QC竞品)
				boolean isPromotionhua = false;// 促销活动
				boolean isotherhua = false;// 其他
				boolean isdingdan = false;// 订单目标

				// 本界面用到的变量
				// 空白终端
				boolean isBlank = getResources().getString(R.string.plan_blantitle).equals(checkname);
				// 有效销售
				boolean isPuHuo = getResources().getString(R.string.plan_puhuotitle).equals(checkname);
				// 有效铺货
				boolean isXiaoShou = getResources().getString(R.string.plan_xiaoshoutitle).equals(checkname);
				// 道具生动化
				boolean isDaoJu = getResources().getString(R.string.plan_daojutitle).equals(checkname);
				// 产品生动化
				boolean isChanPin = getResources().getString(R.string.plan_product).equals(checkname);
				// 冰冻化
				boolean isBingDong = getResources().getString(R.string.plan_bingdongtitle).equals(checkname);
				// 竞品清除计划(QC竞品)
				boolean isJingPin = getResources().getString(R.string.plan_jingpintitle).equals(checkname);
				// 生动化布置
				boolean issdhbz = getResources().getString(R.string.plan_sdhbz).equals(checkname);
				// 促销活动推进
				boolean isPromotion = getResources().getString(R.string.plan_promotion).equals(checkname);
				// 其他
				boolean isother = getResources().getString(R.string.plan_other).equals(checkname);
				// 订单目标
				boolean isdingdanmb = getResources().getString(R.string.plan_dingdan).equals(checkname);

				if (isBlank || isPuHuo || isXiaoShou)// 空白终端跟checkname一样 当前是空白终端
				{
					isProductTerminal = true;
				}
				// 产品生动化,冰冻化
				else if (isChanPin || isBingDong) {

					if (isChanPin) {
						chanpinPosition = i;
						isChanpinhua = true;
					} else if (isBingDong) {
						bingdongPosition = i;
						isBingdonghua = true;
					}
					isGezhonghua = true;// 各种XX化
				}

				// 道具生动化
				else if (isDaoJu) {

					if (issdhbz) {
						sdhbzPosition = i;
					}
					isDaojuhua = true;// 生动化布置

				}
				// 竞品清除计划(QC竞品)
				else if (isJingPin) {

					if (isJingPin) {
						jingpinPosition = i;
					}
					isjingpintuijin = true;
				} else if (isdingdanmb) {

					if (isdingdanmb) {
						dingdanPosition = i;
					}
					isdingdan = true;

				}
				// 其他 促销活动推进

				// 促销活动推进
				if (isPromotion) {
					isPromotionhua = true;
				} else {
					isPromotionhua = false;
				}

				// 其他
				if (isother) {
					isotherhua = true;
				} else {
					isotherhua = false;
				}

				// ViewPager里listview item显示内容结构体
				ArrayList<VpLvItemStc> vpLvItemStcS = new ArrayList<VpLvItemStc>();

				// String checkUse = planTempChecks.get(i).getCheckuse();
				List<String> lineKeys = new ArrayList<String>();
				lineKeys.add(defaultLineKey);
				// 显示采集项下面的item
				MakePlanViewPagerAdapter adapter = new MakePlanViewPagerAdapter(this, vpLvItemStcS, checkKey, lineKeys, preview_plan, isProductTerminal, isPromotionhua, isotherhua, isGezhonghua, isjingpintuijin, isDaojuhua, isChanpinhua, isBingdonghua, isdingdan);
				// mNoScrollListView.setAdapter(adapter);
				viewTitles.add(checkname);// 放入指标名称
				adapters.add(adapter);
				list_VpLvItemStcs.add(vpLvItemStcS);
				// 在底部添加viewpager
				// viewList.add(view);
				checkkeys.add(checkKey);
			}

			// 初始化读取每个item的数据
			initDataViewpager();

			// 上面是先把数据创建出来, 下面直接用

			planListViewAdapter = new PlanListViewAdapter(planTempChecks, viewTitles, new IClick() {

				@Override
				public void listViewItemClick(int position, View v) {

					/*
					 * Toast.makeText(DayPlanActivity.this, "添加按钮被点击了，位置是-->" +
					 * (Integer) v.getTag() + ",内容是-->" +
					 * planTempChecks.get(position).getCheckname()+""+
					 * planTempChecks.get((Integer) v.getTag()).getCheckname(),
					 * Toast.LENGTH_SHORT).show();
					 */

					// 添加产品

					String checkname = planTempChecks.get((Integer) v.getTag()).getCheckname();
					// 空白终端
					boolean isBlank = getResources().getString(R.string.plan_blantitle).equals(checkname);
					// 有效铺货目标终端
					boolean isPuHuo = getResources().getString(R.string.plan_puhuotitle).equals(checkname);
					// 有效销售目标终端
					boolean isXiaoShou = getResources().getString(R.string.plan_xiaoshoutitle).equals(checkname);

					// 道具生动化(废弃了)
					boolean isDaoJu = getResources().getString(R.string.plan_daojutitle).equals(checkname);
					// 产品生动化
					boolean isChanPin = getResources().getString(R.string.plan_product).equals(checkname);
					// 冰冻化
					boolean isBingDong = getResources().getString(R.string.plan_bingdongtitle).equals(checkname);
					// 竞品清除计划(QC竞品)
					boolean isJingPin = getResources().getString(R.string.plan_jingpintitle).equals(checkname);
					// 订单目标
					boolean dingdan = getResources().getString(R.string.plan_dingdan).equals(checkname);

					if (isBlank) {
						if (blankProductList == null || blankProductList.isEmpty()) {
							Toast.makeText(getApplicationContext(), "没有可选择的产品", Toast.LENGTH_SHORT).show();
						} else {
							ShowPopuptWindow(blankProductList, checkname);
						}
					} else if (isPuHuo) {
						if (puhuoProductList == null || puhuoProductList.isEmpty()) {
							Toast.makeText(getApplicationContext(), "没有可选择的产品", Toast.LENGTH_SHORT).show();
						} else {
							ShowPopuptWindow(puhuoProductList, checkname);
						}
					} else if (isXiaoShou) {
						if (xiaoshouProductList == null || xiaoshouProductList.isEmpty()) {
							Toast.makeText(getApplicationContext(), "没有可选择的产品", Toast.LENGTH_SHORT).show();
						} else {
							ShowPopuptWindow(xiaoshouProductList, checkname);

						}
					} else if (dingdan)// 订单目标
					{
						if (dingdanProductList == null || dingdanProductList.isEmpty()) {
							Toast.makeText(getApplicationContext(), "没有可选择的产品", Toast.LENGTH_SHORT).show();
						} else {
							ShowPopuptWindow(dingdanProductList, checkname);

						}
					} else if (isDaoJu)// (此判断废弃了)
					{
						ShowDiologWindow(huaProductList, checkname);
					} else if (isChanPin) {
						ShowDiologWindow(huaProductList, checkname);
					}

					else if (isBingDong) {
						ShowDiologWindow(huaProductList, checkname);
					} else if (isJingPin) {
						dialogProduct();
					}

				}

			});

			mPlanList.setAdapter(planListViewAdapter);

			// 修改制定日计划时,删除某个产品的终端铺货而该产品在产品列表不出现的bug 20160728
			/*
			 * String check_name1 = viewTitles.get(0); isBlank =
			 * getResources().getString
			 * (R.string.plan_blantitle).equals(check_name1); isPuHuo =
			 * getResources
			 * ().getString(R.string.plan_puhuotitle).equals(check_name1);
			 * isXiaoShou =
			 * getResources().getString(R.string.plan_xiaoshoutitle)
			 * .equals(check_name1);
			 */

			// 修改制定日计划时,删除某个产品的终端铺货而该产品在产品列表不出现的bug 20160728

		} else {
			mViewPager.setVisibility(View.GONE);// 隐藏不给触碰，集合没有数据滚动会出现数组越界问题
			btn_sure.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 初始化viewpagger
	 */
	private void initViewPager() {

		// viewPager中viewList (viewList:这是个list集合)
		viewList.clear();

		// 存放标题的List // viewPager中view各个标题 (viewTitles:这是个list集合)
		viewTitles.clear();

		// 存放每一个页面的adapter // 存放每一个页面的adapter (adapters:这是个list集合)
		adapters.clear();

		// 存放每一个页面的List<VpLvItemStc> (list_VpLvItemStcs:这是个list集合)
		list_VpLvItemStcs.clear();

		// 指标集合 // 指标如:有效铺货目标终端,生动化布置,其他,促销活动推进 指标都有各自的key(checkkeys:这是个list集合)
		checkkeys.clear();

		mViewPager = (ViewPager) findViewById(R.id.makeplan_vp);
		// 设置viewpager文字
		tabStrip = (PagerTabStrip) findViewById(R.id.makeplan_vp_tab);
		tabStrip.setTextColor(getResources().getColor(R.color.agency_viewpager_title));
		tabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);

		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 指标list
		if (planTempChecks != null && !planTempChecks.isEmpty())// 计划指标集合planTempChecks是查PlantempcheckM表得到的
		{
			// 如果是预览的,隐藏确定按钮
			if (preview_plan) {
				btn_sure.setVisibility(View.GONE);// 右上角确定按钮 设置隐藏
			} else {
				btn_sure.setVisibility(View.VISIBLE);
			}

			// 根据计划指标集合planTempChecks,初始化各个viewpager条目的数据(一个条目其实就是一个计划指标)
			// 另外预览模式下的一些按钮消失也在此for循环中处理
			for (int i = 0; i < planTempChecks.size(); i++) {
				//
				View view = inflater.inflate(R.layout.operation_makeplan_viewpagger, null);
				// 采集项名称
				TextView tv_name = (TextView) view.findViewById(R.id.vp_tv_title_name1);
				// 终端
				TextView tv_termname = (TextView) view.findViewById(R.id.vp_tv_title_name3);
				// 产品名称
				TextView tv_produname = (TextView) view.findViewById(R.id.vp_tv_title_name0);
				View vp_v_title_view = (View) view.findViewById(R.id.vp_v_title_view);
				// 添加
				Button btn_add = (Button) view.findViewById(R.id.vp_btn_add);
				// 操作
				TextView vp_tv_title_operation = (TextView) view.findViewById(R.id.vp_tv_title_operation);
				View operation_divider = (View) view.findViewById(R.id.operation_divider);

				NoScrollListView mNoScrollListView = (NoScrollListView) view.findViewById(R.id.vp_lv);

				// 具体的一个计划指标
				PadPlantempcheckM padPlantempcheckM = planTempChecks.get(i);
				checkname = padPlantempcheckM.getCheckname();
				String checkKey = padPlantempcheckM.getCheckkey();

				// 下一界面(MakePlanViewPagerAdapter)用到的变量
				boolean isProductTerminal = false;// 产品终端(空白终端,有效销售,有效铺货)
				boolean isDaojuhua = false;// 道具生动化
				boolean isGezhonghua = false;// 各种XX化(产品生动化,冰冻化)
				boolean isChanpinhua = false;// 产品生动化
				boolean isBingdonghua = false;// 冰冻化
				boolean isjingpintuijin = false;// 竞品清除计划(QC竞品)
				boolean isPromotionhua = false;// 促销活动
				boolean isotherhua = false;// 其他
				boolean isdingdan = false;// 订单目标

				// 本界面用到的变量
				// 空白终端
				boolean isBlank = getResources().getString(R.string.plan_blantitle).equals(checkname);
				// 有效销售
				boolean isPuHuo = getResources().getString(R.string.plan_puhuotitle).equals(checkname);
				// 有效铺货
				boolean isXiaoShou = getResources().getString(R.string.plan_xiaoshoutitle).equals(checkname);
				// 道具生动化
				boolean isDaoJu = getResources().getString(R.string.plan_daojutitle).equals(checkname);
				// 产品生动化
				boolean isChanPin = getResources().getString(R.string.plan_product).equals(checkname);
				// 冰冻化
				boolean isBingDong = getResources().getString(R.string.plan_bingdongtitle).equals(checkname);
				// 竞品清除计划(QC竞品)
				boolean isJingPin = getResources().getString(R.string.plan_jingpintitle).equals(checkname);
				// 生动化布置
				boolean issdhbz = getResources().getString(R.string.plan_sdhbz).equals(checkname);
				// 促销活动推进
				boolean isPromotion = getResources().getString(R.string.plan_promotion).equals(checkname);
				// 其他
				boolean isother = getResources().getString(R.string.plan_other).equals(checkname);
				// 订单目标
				boolean isdingdanmb = getResources().getString(R.string.plan_dingdan).equals(checkname);

				// 根据计划指标不同,设置不同的Listview
				if (isBlank || isPuHuo || isXiaoShou)// 空白终端跟checkname一样 当前是空白终端
				{
					btn_add.setVisibility(View.VISIBLE);// 添加按钮设置显示
					btn_add.setTag(checkname);
					tv_name.setText(getString(R.string.makeplan_proname));// 并把名称改为产品名称
					if (preview_plan) {
						btn_add.setVisibility(View.GONE);// 添加按钮设置显示
					} else {
						// 操作可见// 删除按钮设置显示
						vp_tv_title_operation.setVisibility(View.VISIBLE);
						operation_divider.setVisibility(View.VISIBLE);
						btn_add.setOnClickListener(this);
					}
					if (isBlank) {
						blankPosition = i;
					} else if (isPuHuo) {
						puhuoPosition = i;
					} else if (isXiaoShou) {
						xiaoshouPosition = i;
					}
					isProductTerminal = true;
				}
				// 产品生动化,冰冻化
				else if (isChanPin || isBingDong) {
					btn_add.setVisibility(View.VISIBLE);
					btn_add.setTag(checkname);
					tv_produname.setVisibility(View.VISIBLE);
					vp_v_title_view.setVisibility(View.VISIBLE);
					tv_name.setText(getString(R.string.makeplan_productname));// 并把名称改为采集项名称
					tv_produname.setText(getString(R.string.makeplan_proname));// 并把名称改为产品名称
					if (preview_plan) {
						btn_add.setVisibility(View.GONE);
					} else {
						// 操作可见// 删除按钮设置显示
						vp_tv_title_operation.setVisibility(View.VISIBLE);
						operation_divider.setVisibility(View.VISIBLE);
						btn_add.setOnClickListener(this);
					}
					if (isDaoJu) {
						daojuPosition = i;
					} else if (isChanPin) {
						chanpinPosition = i;
						isChanpinhua = true;
					} else if (isBingDong) {
						bingdongPosition = i;
						isBingdonghua = true;
					}
					isGezhonghua = true;// 各种XX化
				}
				/*
				 * else if(isDaoJu || isChanPin || isBingDong){
				 * btn_add.setVisibility(View.VISIBLE);
				 * btn_add.setTag(checkname);
				 * tv_produname.setVisibility(View.VISIBLE);
				 * vp_v_title_view.setVisibility(View.VISIBLE);
				 * tv_name.setText(getString
				 * (R.string.makeplan_productname));//并把 名称 改为 采集项名称
				 * tv_produname
				 * .setText(getString(R.string.makeplan_proname));//并把 名称 改为
				 * 产品名称 if (preview_plan) { btn_add.setVisibility(View.GONE); }
				 * else { // vp_tv_title_operation.setVisibility(View.VISIBLE);
				 * //操作可见 operation_divider.setVisibility(View.VISIBLE);
				 * btn_add.setOnClickListener(this); } if (isDaoJu) {
				 * daojuPosition = i; } else if (isChanPin) { chanpinPosition =
				 * i; } else if (isBingDong) { bingdongPosition = i; }
				 * isGezhonghua = true;//各种XX化 }
				 */
				// 道具生动化
				else if (isDaoJu) {

					btn_add.setVisibility(View.GONE);
					btn_add.setTag(checkname);
					tv_name.setText(getString(R.string.makeplan_productname));// 并把名称改为采集项名称
					if (issdhbz) {
						sdhbzPosition = i;
					}
					isDaojuhua = true;// 生动化布置

				}
				// 竞品清除计划(QC竞品)
				else if (isJingPin) {

					btn_add.setVisibility(View.VISIBLE);
					tv_produname.setVisibility(View.VISIBLE);
					btn_add.setTag(checkname);
					tv_name.setText(getString(R.string.makeplan_pinpai));// 并把名称改为品牌
					tv_produname.setText(getString(R.string.makeplan_proname));// 并把名称改为产品名称
					if (preview_plan) {
						btn_add.setVisibility(View.GONE);
					} else {
						// 操作可见// 删除按钮设置显示
						vp_tv_title_operation.setVisibility(View.VISIBLE);
						operation_divider.setVisibility(View.VISIBLE);
						btn_add.setOnClickListener(this);
					}
					if (isJingPin) {
						jingpinPosition = i;
					}
					isjingpintuijin = true;
				} else if (isdingdanmb) {
					btn_add.setVisibility(View.VISIBLE);
					btn_add.setTag(checkname);

					// 采集项(产品名称),订单目标数量,操作
					tv_termname.setVisibility(View.GONE);

					if (preview_plan) {
						btn_add.setVisibility(View.GONE);
					} else {
						// 操作可见// 删除按钮设置显示
						vp_tv_title_operation.setVisibility(View.VISIBLE);
						operation_divider.setVisibility(View.VISIBLE);
						btn_add.setOnClickListener(this);
					}

					if (isdingdanmb) {
						dingdanPosition = i;
					}
					isdingdan = true;

				}
				// 其他 促销活动推进
				else {
					tv_name.setText(getString(R.string.makeplan_name));// 不是空白终端时tv_name赋值为名称添加按钮也看不见
					btn_add.setVisibility(View.GONE);
				}

				// ViewPager里listview item显示内容结构体
				ArrayList<VpLvItemStc> vpLvItemStcS = new ArrayList<VpLvItemStc>();

				// 促销活动推进
				if (isPromotion) {
					isPromotionhua = true;
				} else {
					isPromotionhua = false;
				}

				// 其他
				if (isother) {
					isotherhua = true;
				} else {
					isotherhua = false;
				}

				// String checkUse = planTempChecks.get(i).getCheckuse();
				List<String> lineKeys = new ArrayList<String>();
				lineKeys.add(defaultLineKey);
				// 显示采集项下面的item
				MakePlanViewPagerAdapter adapter = new MakePlanViewPagerAdapter(this, vpLvItemStcS, checkKey, lineKeys, preview_plan, isProductTerminal, isPromotionhua, isotherhua, isGezhonghua, isjingpintuijin, isDaojuhua, isChanpinhua, isBingdonghua, isdingdan);
				mNoScrollListView.setAdapter(adapter);
				viewTitles.add(checkname);// 放入指标名称
				adapters.add(adapter);
				list_VpLvItemStcs.add(vpLvItemStcS);
				// 在底部添加viewpager
				viewList.add(view);
				checkkeys.add(checkKey);
			}
			mViewPager.setVisibility(View.VISIBLE);
			// initDataViewpager();// f
			mViewPager.setAdapter(new LedgerPagerAdapter(viewList, viewTitles));
			mViewPager.setCurrentItem(0);

			// 修改制定日计划时,删除某个产品的终端铺货而该产品在产品列表不出现的bug 20160728
			String check_name1 = viewTitles.get(0);
			isBlank = getResources().getString(R.string.plan_blantitle).equals(check_name1);
			isPuHuo = getResources().getString(R.string.plan_puhuotitle).equals(check_name1);
			isXiaoShou = getResources().getString(R.string.plan_xiaoshoutitle).equals(check_name1);
			mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageScrollStateChanged(int arg0) {//

				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageSelected(int arg0) {
					String check_name = viewTitles.get(arg0);
					isBlank = getResources().getString(R.string.plan_blantitle).equals(check_name);
					isPuHuo = getResources().getString(R.string.plan_puhuotitle).equals(check_name);
					isXiaoShou = getResources().getString(R.string.plan_xiaoshoutitle).equals(check_name);
				}
			});
			// 修改制定日计划时,删除某个产品的终端铺货而该产品在产品列表不出现的bug 20160728

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
	private Dao<MstPromotionsM, String> mstPromotionsMDao;
	private Dao<MstPlanforuserM, String> mstPlanforuserMDao;
	private LayoutInflater inflater;

	private void initDataViewpager() {
		Log.i(TAG, "initDataViewpager :" + (++count));
		if (make_or_modify || !defaultLineKey.equals(editLineKey))// 制定模式
		{
			// 制定时走这里
			// 根据计划指标集合planTempChecks,初始化各个viewpager条目的数据(viewpager的一个条目其实就是一个计划指标)
			for (int i = 0; i < planTempChecks.size(); i++) {
				// 动态加载指标模板下对应指标的值
				PadPlantempcheckM padPlantempcheckM = planTempChecks.get(i);
				String title = viewTitles.get(i);
				Log.i(TAG, "initDataViewpager title 制定" + title);

				// 对应模板主键根据它取得模板对应的指标状态(名称)
				String checkkey = padPlantempcheckM.getCheckkey();

				// 取出存的页面的值
				List<VpLvItemStc> vpLvItemStcS = list_VpLvItemStcs.get(i);
				MakePlanViewPagerAdapter adapter = adapters.get(i);

				// 产品终端(空白终端,有效销售,有效铺货)
				if (isBlank(viewTitles.get(i)) || isPuhuo(viewTitles.get(i)) || isXiaoShou(viewTitles.get(i))) {
					// 空白终端
				}
				// 促销活动
				else if (getResources().getString(R.string.plan_promotion).equals(viewTitles.get(i))) {
					// 促销活动
					// checkkey查数据
					// if (validPromotions.size() > 0) {
					// for (int j = 0; j < validPromotions.size(); j++) {
					// VpLvItemStc item = new VpLvItemStc();
					// item.setPcolitemkey(FunUtil.getUUID());
					// item.setName(validPromotions.get(j).getPromotname());
					// item.setKey(validPromotions.get(j).getPromotkey());
					// vpLvItemStcS.add(item);
					// }
					// }

					List<PadPlantempcollectionInfo> colitemList = service.queryPromotionsColitemname(checkkey, planDate);// pad端计划模板指标对应采集项关联信息表的数据
					if (colitemList.size() > 0) {
						// 指标选项
						for (int j = 0; j < colitemList.size(); j++) {
							VpLvItemStc item = new VpLvItemStc();
							item.setPcolitemkey(FunUtil.getUUID());
							item.setName(colitemList.get(j).getColitemname());
							item.setKey(colitemList.get(j).getColitemkey());
							vpLvItemStcS.add(item);
						}

					}

				}
				// 产品生动化,冰冻化
				else if (isChanPin(viewTitles.get(i)) || isBingDong(viewTitles.get(i))) {
					// 产品生动化,冰冻化
				}
				// 道具生动化
				else if (isDaoJu(viewTitles.get(i))) {
					// 道具生动化
					List<PadPlantempcollectionInfo> colitemList = service.queryColitemname(checkkey);// pad端计划模板指标对应采集项关联信息表的数据
					if (colitemList.size() > 0) {
						// 指标选项
						for (int j = 0; j < colitemList.size(); j++) {
							VpLvItemStc item = new VpLvItemStc();
							item.setPcolitemkey(FunUtil.getUUID());
							item.setName(colitemList.get(j).getColitemname());
							item.setKey(colitemList.get(j).getColitemkey());
							vpLvItemStcS.add(item);
						}
					}
				}
				// 竞品清除计划(QC竞品)
				else if (isJingPin(viewTitles.get(i))) {
					// 竞品清除计划(QC竞品)
				}
				// 订单目标
				else if (isDaoJu(viewTitles.get(i))) {

					List<PadPlantempcollectionInfo> colitemList = service.queryColitemname(checkkey);// pad端计划模板指标对应采集项关联信息表的数据
					if (colitemList.size() > 0) {
						// 指标选项
						for (int j = 0; j < colitemList.size(); j++) {
							VpLvItemStc item = new VpLvItemStc();
							item.setPcolitemkey(FunUtil.getUUID());
							item.setName(colitemList.get(j).getColitemname());
							item.setKey(colitemList.get(j).getColitemkey());
							vpLvItemStcS.add(item);
						}
					}
				}
				// 其他
				else {
					List<PadPlantempcollectionInfo> colitemList = service.queryColitemname(checkkey);// pad端计划模板指标对应采集项关联信息表的数据
					if (colitemList.size() > 0) {
						// 指标选项
						for (int j = 0; j < colitemList.size(); j++) {
							VpLvItemStc item = new VpLvItemStc();
							item.setPcolitemkey(FunUtil.getUUID());
							item.setName(colitemList.get(j).getColitemname());
							item.setKey(colitemList.get(j).getColitemkey());
							vpLvItemStcS.add(item);
						}

					}
				}
				adapter.notifyDataSetChanged();

			}
		}
		// 修改模式 make_or_modify = false
		else {
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
				// 修改/预览时走这里
				// 查询当前计划下MstPlancheckInfo 计划指标信息(关联计划主键)
				// 解释:
				// 每确定制定一天的日计划会在MstPlancheckInfo表中生成多条记录(有几个计划指标,就生成几条记录,以plankey做关联),
				List<MstPlancheckInfo> plancheckInfos = mstPlancheckInfoDao.queryForEq("plankey", plankey);
				// planTempChecks
				if (!planTempChecks.isEmpty()) {
					for (int i = 0; i < planTempChecks.size(); i++) {
						String checkkey = planTempChecks.get(i).getCheckkey();
						String pcheckkey = "";

						for (int j = 0; j < plancheckInfos.size(); j++) {// 不在查数据库
																			// 通过所有数据循环对比
							MstPlancheckInfo mstPlancheckInfo = plancheckInfos.get(j);
							if (checkkey.equals(mstPlancheckInfo.getCheckkey())) {// checkkey指标主键
								pcheckkey = mstPlancheckInfo.getPcheckkey();// 计划指标主键
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
						fieldValues.put("checkkey", checkkey);// f
						fieldValues.put("deleteflag", ConstValues.FLAG_0);// f
						// 计划指标与采集项信息
						List<MstPlancollectionInfo> plancollectionInfos = mstPlancollectionInfoDao.queryForFieldValues(fieldValues);
						List<VpLvItemStc> vpLvItemStcS = list_VpLvItemStcs.get(i);
						MakePlanViewPagerAdapter adapter = adapters.get(i);
						Map<String, List<String>> selectTerminalsMap = adapter.getSelectTerminalsMap();

						List<String> promotionKeyLst = new ArrayList<String>();// 促销活动
						List<String> otherKeyLst = new ArrayList<String>();// 其他
						List<String> daojuKeyLst = new ArrayList<String>();// 道具

						for (int j = 0; j < plancollectionInfos.size(); j++) {
							// 一个MstPlancollectionInfo对象代表 一个计划指标下的一条记录
							MstPlancollectionInfo plancollectionInfo = plancollectionInfos.get(j);
							String colitemkey = plancollectionInfo.getColitemkey();// 采集项主键
							String pcolitemkey = plancollectionInfo.getPcolitemkey();// 计划采集项主键
							String plantype = plancollectionInfo.getPlantype();
							String remarks = plancollectionInfo.getRemarks();// 此字段记录所选终端
							String productkey = plancollectionInfo.getProductkey();

							// 记录进入编辑页面时的计划采集项表主键
							colKeyLst.add(pcolitemkey);

							if (!TextUtils.isEmpty(remarks)) {// 如果remarks为空,表示该采集项没有选择终端
																// 就不处理 // ywm
																// 2016/04/05

								selectTerminalsMap.put(pcolitemkey, JsonUtil.parseList(remarks, String.class));// 不太明白
							}

							// 0采集项1产品2活动3有效铺货4有效销售5竞品推进计划品牌
							// if (planType_blank.equals(plantype) ||
							// planType_puhuo.equals(plantype) ||
							// planType_xiaoshou.equals(plantype))
							if ("884d0d53-2cdb-4214-b24d-137d8753c47d".equals(checkkey) || // 空白终端
									"b7170a87-68d8-453b-a67a-33827bca5bc6".equals(checkkey) || // 有效销售目标终端
									"513a8b13-abbb-4a21-84f6-0ddcdaa3ec1f".equals(checkkey) // 有效铺货目标终端
									|| dingDanCheckkey.equals(checkkey)) {
								MstProductM product = new MstProductM();
								if (productkey != null) {
									product = mstProductMDao.queryForId(productkey);
								} else {
									// 兼容之前的老数据(之前没有productkey字段) ywm 20160416
									product = mstProductMDao.queryForId(colitemkey);
								}
								if (!preview_plan)// 预览状态下就不执行移除产品 ，因为不需要选择产品
								{
									if ("884d0d53-2cdb-4214-b24d-137d8753c47d".equals(checkkey)) {// 空白终端
										for (MstProductM mproduct : blankProductList) {
											// 修改产品 的时候移除已经选择的产品
											if (colitemkey.equals(mproduct.getProductkey())) {
												// 选择产品的弹窗列表展示少一个产品条目
												blankProductList.remove(mproduct);
											}
										}

										// }else
										// if(planType_puhuo.equals(plantype)){//有效铺货
									} else if ("513a8b13-abbb-4a21-84f6-0ddcdaa3ec1f".equals(checkkey)) {// 有效铺货
										for (MstProductM mproduct : puhuoProductList) {
											// 修改产品 的时候移除已经选择的产品
											if (colitemkey.equals(mproduct.getProductkey()) || productkey.equals(mproduct.getProductkey())) {
												puhuoProductList.remove(mproduct);
											}
										}

									} else if ("b7170a87-68d8-453b-a67a-33827bca5bc6".equals(checkkey)) {// 有效销售
										for (MstProductM mproduct : xiaoshouProductList) {
											// 修改产品 的时候移除已经选择的产品
											if (colitemkey.equals(mproduct.getProductkey())) {
												xiaoshouProductList.remove(mproduct);
											}
										}

									} else if (dingDanCheckkey.equals(checkkey)) {// 订单目标
										for (MstProductM mproduct : dingdanProductList) {
											// 修改产品 的时候移除已经选择的产品
											if (colitemkey.equals(mproduct.getProductkey())) {
												dingdanProductList.remove(mproduct);
											}
										}

									}
								}

								// productList.remove(product);
								if (product != null) {
									VpLvItemStc item = new VpLvItemStc();
									item.setPcolitemkey(pcolitemkey);
									item.setName(product.getProname());
									item.setTerm(plancollectionInfo.getTermnames());
									item.setKey(plancollectionInfo.getColitemkey());
									item.setPronamekey(plancollectionInfo.getColitemkey());
									item.setNum(String.valueOf(plancollectionInfo.getTermnum()));
									item.setDingdannum(String.valueOf(plancollectionInfo.getOrdernum()));// 订单目标
									vpLvItemStcS.add(item);

								}

							} else if ("884d0d53-2cdb-4214-b24d-137d8753047p".equals(checkkey))// 促销活动推进
							{
								MstPromotionsM pormotion = mstPromotionsMDao.queryForId(colitemkey);// 通过采集项主键查询
								if (pormotion != null) {
									VpLvItemStc item = new VpLvItemStc();
									item.setName(pormotion.getPromotname());
									item.setPcolitemkey(pcolitemkey);
									item.setTerm(plancollectionInfo.getTermnames());
									item.setKey(plancollectionInfo.getColitemkey());
									item.setNum(String.valueOf(plancollectionInfo.getTermnum()));
									vpLvItemStcS.add(item);
									promotionKeyLst.add(item.getKey());
								}

							} else if ("a16ccbc3-4be7-42b8-a338-56da74c6ddf4".equals(checkkey)) {// 竞品清除计划(QC竞品)
																									// 未改

								List<MstCmpcompanyM> mstCmpcompanyM = mstCmpcompanyMDao.queryForEq("cmpcomkey", colitemkey);
								List<MstCmproductinfoM> CmproductinfoM = new ArrayList<MstCmproductinfoM>();
								if (productkey != null) {
									CmproductinfoM = mstCmproductinfoMDao.queryForEq("cmpproductkey", productkey);
								} else {
									CmproductinfoM = mstCmproductinfoMDao.queryForEq("cmpproductkey", colitemkey);
								}
								if ((!CmproductinfoM.isEmpty()) && mstCmpcompanyM.size() > 0) {
									// KvStc agency =
									VpLvItemStc item = new VpLvItemStc();
									item.setPcolitemkey(pcolitemkey);
									item.setName(mstCmpcompanyM.get(0).getCmpcomname());// 竞品清除计划(QC竞品)
																						// 品牌
									item.setKey(plancollectionInfo.getColitemkey());// 竞品清除计划(QC竞品)
																					// 品牌key
									item.setProname(CmproductinfoM.get(0).getCmpproname());// 竞品清除计划(QC竞品)
																							// 产品名称
									item.setPronamekey(plancollectionInfo.getProductkey());// 竞品清除计划(QC竞品)
																							// 产品名称key

									item.setTerm(plancollectionInfo.getTermnames());
									item.setNum(String.valueOf(plancollectionInfo.getTermnum()));
									vpLvItemStcS.add(item);
								}

							} else if ("ad3030fb-e42e-47f8-a3ec-4229089aab7d".equals(checkkey) || // 产品生动化
									"ad3030fb-e42e-47f8-a3ec-4229089aab8d".equals(checkkey)) {// 冰冻化

								List<PadPlantempcollectionInfo> padPlantempcollectionInfo = padPlantempcollectionInfoDao.queryForEq("colitemkey", colitemkey);

								List<MstProductM> MstProductM = new ArrayList<MstProductM>();
								if (productkey != null) {
									MstProductM = MstProductMDao.queryForEq("productkey", productkey);
								} else {
									MstProductM = MstProductMDao.queryForEq("productkey", colitemkey);
								}
								if ((!padPlantempcollectionInfo.isEmpty()) && MstProductM.size() > 0) {
									// KvStc agency =
									VpLvItemStc item = new VpLvItemStc();
									item.setPcolitemkey(pcolitemkey);
									item.setName(padPlantempcollectionInfo.get(0).getColitemname());//
									item.setKey(plancollectionInfo.getColitemkey());// key
									item.setProname(MstProductM.get(0).getProname());// 产品名称
									item.setPronamekey(plancollectionInfo.getProductkey());// 产品名称key

									item.setTerm(plancollectionInfo.getTermnames());
									item.setNum(String.valueOf(plancollectionInfo.getTermnum()));
									vpLvItemStcS.add(item);

								}
							} else if ("ad3030fb-e42e-47f8-a3ec-4229089aab6d".equals(checkkey)) {// 道具

								List<PadPlantempcollectionInfo> padPlantempcollectionInfo = padPlantempcollectionInfoDao.queryForEq("colitemkey", colitemkey);

								if (!padPlantempcollectionInfo.isEmpty()) {
									// KvStc agency =
									VpLvItemStc item = new VpLvItemStc();
									item.setPcolitemkey(pcolitemkey);
									item.setName(padPlantempcollectionInfo.get(0).getColitemname());//
									item.setKey(plancollectionInfo.getColitemkey());// key

									item.setTerm(plancollectionInfo.getTermnames());
									item.setNum(String.valueOf(plancollectionInfo.getTermnum()));
									vpLvItemStcS.add(item);
									daojuKeyLst.add(item.getKey());

								}
							} else { // 其他指标
								List<PadPlantempcollectionInfo> plantempcollectionInfo = padPlantempcollectionInfoDao.queryForEq("colitemkey", colitemkey);
								if (!plantempcollectionInfo.isEmpty()) {
									VpLvItemStc item = new VpLvItemStc();
									item.setName(plantempcollectionInfo.get(0).getColitemname());
									item.setPcolitemkey(pcolitemkey);
									item.setTerm(plancollectionInfo.getTermnames());
									item.setKey(plancollectionInfo.getColitemkey());
									item.setNum(String.valueOf(plancollectionInfo.getTermnum()));
									vpLvItemStcS.add(item);
									otherKeyLst.add(item.getKey());
								}

							}
						}

						// 促销活动 没终端的
						// 解释:
						// 因为日计划确定后,只会将每个计划指标下有终端的记录保存到MstPlancollectionInfo表中,没有终端的不保存
						// 所以修改模式下
						// 其他,促销活动,道具生动化3个计划指标从MstPlancollectionInfo读到的数据,可能不是全面的
						if (getResources().getString(R.string.plan_promotion).equals(viewTitles.get(i))) {
							// pad端计划模板指标对应采集项关联信息表的数据
							List<PadPlantempcollectionInfo> colitemList = service.queryPromotionsColitemname(checkkey, planDate);
							if (colitemList.size() > 0) {
								// 指标选项
								for (int j = 0; j < colitemList.size(); j++) {

									if (!promotionKeyLst.contains(colitemList.get(j).getColitemkey())) {
										VpLvItemStc item = new VpLvItemStc();
										item.setPcolitemkey(FunUtil.getUUID());
										item.setName(colitemList.get(j).getColitemname());
										item.setKey(colitemList.get(j).getColitemkey());
										vpLvItemStcS.add(item);
									}
								}

							}
						}

						// 其他 没终端的
						if (getResources().getString(R.string.plan_other).equals(viewTitles.get(i))) {

							List<PadPlantempcollectionInfo> colitemList = service.queryotherColitemname(checkkey);// pad端计划模板指标对应采集项关联信息表的数据
							if (colitemList.size() > 0) {
								// 指标选项
								for (int j = 0; j < colitemList.size(); j++) {

									if (!otherKeyLst.contains(colitemList.get(j).getColitemkey())) {
										VpLvItemStc item = new VpLvItemStc();
										item.setPcolitemkey(FunUtil.getUUID());
										item.setName(colitemList.get(j).getColitemname());
										item.setKey(colitemList.get(j).getColitemkey());
										vpLvItemStcS.add(item);
									}
								}

							}

						}
						// 道具生动化 没终端的
						if (getResources().getString(R.string.plan_daojutitle).equals(viewTitles.get(i))) {

							List<PadPlantempcollectionInfo> colitemList = service.queryotherColitemname(checkkey);// pad端计划模板指标对应采集项关联信息表的数据
							if (colitemList.size() > 0) {
								// 指标选项
								for (int j = 0; j < colitemList.size(); j++) {

									if (!daojuKeyLst.contains(colitemList.get(j).getColitemkey())) {
										VpLvItemStc item = new VpLvItemStc();
										item.setPcolitemkey(FunUtil.getUUID());
										item.setName(colitemList.get(j).getColitemname());
										item.setKey(colitemList.get(j).getColitemkey());
										vpLvItemStcS.add(item);
									}
								}
							}
						}
						adapter.notifyDataSetChanged();
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
	public void initTypeData(String lineKey) {

		if (typeList.size() != 0) {
			typeList.clear();
		}
		Map<String, Integer> telvelMap = service.queryTelvel(lineKey);
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
		typeList.add(typeStcA);
		TypeStc typeStcB = new TypeStc();
		typeStcB.setType("B类");
		typeStcB.setNum(b + "");
		typeList.add(typeStcB);
		TypeStc typeStcC = new TypeStc();
		typeStcC.setType("C类");
		typeStcC.setNum(c + "");
		typeList.add(typeStcC);
		TypeStc typeStcD = new TypeStc();
		typeStcD.setType("D类");
		typeStcD.setNum(d + "");
		typeList.add(typeStcD);
		mListView.setAdapter(new TerminalCountAdapter(this, typeList));
		// 解决scrollview与listview滑动冲突
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					makeplansv.requestDisallowInterceptTouchEvent(false);
				} else {
					makeplansv.requestDisallowInterceptTouchEvent(true);
				}
				return false;
			}
		});

		tv_total.setText(String.valueOf(abcd));
	}

	/**
	 * 初始化此渠道数量显示内容
	 */
	public void initTypeData2(String lineKey) {
		if (typeList.size() != 0) {
			typeList.clear();
		}
		Map<String, Integer> telvelMap = service.queryMinorchannel1(lineKey);
		Integer abcd = telvelMap.get("ABCD");
		if (abcd == null) {
			abcd = 0;
		}
		Iterator iter = telvelMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			Integer val = (Integer) entry.getValue();
			if (val != null && !"ABCD".equals(key)) {
				TypeStc typeStcA = new TypeStc();
				typeStcA.setType(key);
				typeStcA.setNum(val + "");
				typeList.add(typeStcA);
			}
		}
		mListView.setAdapter(new TerminalCountAdapter(this, typeList));
		// 解决scrollview与listview滑动冲突
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					makeplansv.requestDisallowInterceptTouchEvent(false);
				} else {
					makeplansv.requestDisallowInterceptTouchEvent(true);
				}
				return false;
			}
		});
		tv_total.setText(String.valueOf(abcd));
	}

	// 13:12 23:20 2300+100=2400 03:00
	// int i = 0300;

	// hhmm比较
	// private int amfrom, amto, pmfrom, pmto;
	public void amDateCompare(int dateFrom, int dateTo) {
		if (dateFrom > dateTo) {
			Toast.makeText(getApplicationContext(), "选择的时间不能小于开始时间", Toast.LENGTH_SHORT).show();
			if (dateFrom + 100 < 2400) {
				// dateFrom;
				btn_am2.setText("24:00");
			}
		}
		if (dateFrom + 100 > 2400) {
			btn_am2.setText("24:00");
			btn_pm1.setText("24:00");
			btn_pm2.setText("24:00");
		}

	}

	/**
	 * 点击事件处理
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			this.finish();
			break;
		case R.id.list_pop_btn_back:// 弹窗 取消按钮
			mPopupWindow.dismiss();
			break;
		case R.id.list_pop_btn_ok:// 弹窗 确定按钮
			if (v.getTag() != null) {
				// TODO
				String checkname = (String) v.getTag();
				isBlank = getResources().getString(R.string.plan_blantitle).equals(checkname);
				isPuHuo = getResources().getString(R.string.plan_puhuotitle).equals(checkname);
				isXiaoShou = getResources().getString(R.string.plan_xiaoshoutitle).equals(checkname);
				isDingDan = getResources().getString(R.string.plan_dingdan).equals(checkname);
				if (isBlank) {
					// 空白终端显示的列表
					List<VpLvItemStc> blankVpLvItemStcs = list_VpLvItemStcs.get(blankPosition);
					MakePlanViewPagerAdapter blankAdapter = adapters.get(blankPosition);
					// 解决添加过一次还重复添加的问题
					if (blankItem != null && !blankVpLvItemStcs.contains(blankItem)) {
						blankVpLvItemStcs.add(blankItem);
						if (blankClickPosition != -1) {
							blankProductList.remove(blankClickPosition);
						}
						// 添加一个的产品的时候 就添加对应一个 已经记录选择终端的list<terminal>
						Map<String, List<String>> selectTerminalsMap = blankAdapter.getSelectTerminalsMap();
						selectTerminalsMap.put(blankItem.getPcolitemkey(), new ArrayList<String>());
						blankAdapter.notifyDataSetChanged();
						blankClickPosition = -1;
					} else {
						Toast.makeText(getApplicationContext(), "请选择产品", Toast.LENGTH_SHORT).show();
					}
				} else if (isPuHuo) {
					// 修改地方
					List<VpLvItemStc> blankVpLvItemStcs = list_VpLvItemStcs.get(puhuoPosition);
					MakePlanViewPagerAdapter puhuoAdapter = adapters.get(puhuoPosition);
					if (puhuoItem != null && !blankVpLvItemStcs.contains(puhuoItem)) {
						blankVpLvItemStcs.add(puhuoItem);
						if (puhuoClickPosition != -1) {
							puhuoProductList.remove(puhuoClickPosition);
						}
						// 添加一个的产品的时候 就添加对应一个 已经记录选择终端的list<terminal>
						Map<String, List<String>> selectTerminalsMap = puhuoAdapter.getSelectTerminalsMap();
						selectTerminalsMap.put(puhuoItem.getPcolitemkey(), new ArrayList<String>());
						puhuoAdapter.notifyDataSetChanged();
						puhuoClickPosition = -1;
					} else {
						Toast.makeText(getApplicationContext(), "请选择产品", Toast.LENGTH_SHORT).show();
					}

				} else if (isXiaoShou) {
					List<VpLvItemStc> blankVpLvItemStcs = list_VpLvItemStcs.get(xiaoshouPosition);
					MakePlanViewPagerAdapter xiaoshouAdapter = adapters.get(xiaoshouPosition);
					// 解决重复添加问题
					if (xiaoshouItem != null && !blankVpLvItemStcs.contains(xiaoshouItem)) {
						blankVpLvItemStcs.add(xiaoshouItem);
						if (xiaoshouClickPosition != -1) {
							xiaoshouProductList.remove(xiaoshouClickPosition);
						}
						// 添加一个的产品的时候 就添加对应一个 已经记录选择终端的list<terminal>
						Map<String, List<String>> selectTerminalsMap = xiaoshouAdapter.getSelectTerminalsMap();
						selectTerminalsMap.put(xiaoshouItem.getPcolitemkey(), new ArrayList<String>());
						xiaoshouAdapter.notifyDataSetChanged();
						xiaoshouClickPosition = -1;
					} else {
						Toast.makeText(getApplicationContext(), "请选择产品", Toast.LENGTH_SHORT).show();
					}
				} else if (isDingDan) {
					List<VpLvItemStc> blankVpLvItemStcs = list_VpLvItemStcs.get(dingdanPosition);
					MakePlanViewPagerAdapter dingdanAdapter = adapters.get(dingdanPosition);
					// 解决重复添加问题
					if (dingdanItem != null && !blankVpLvItemStcs.contains(dingdanItem)) {
						blankVpLvItemStcs.add(dingdanItem);
						if (dingdanClickPosition != -1) {
							dingdanProductList.remove(dingdanClickPosition);
						}
						// 添加一个的产品的时候 就添加对应一个 已经记录选择终端的list<terminal>
						Map<String, List<String>> selectTerminalsMap = dingdanAdapter.getSelectTerminalsMap();
						selectTerminalsMap.put(dingdanItem.getPcolitemkey(), new ArrayList<String>());
						dingdanAdapter.notifyDataSetChanged();
						// 整个listview适配刷新
						planListViewAdapter.notifyDataSetChanged();
						dingdanClickPosition = -1;
					} else {
						Toast.makeText(getApplicationContext(), "请选择产品", Toast.LENGTH_SHORT).show();
					}
				}
			}
			mPopupWindow.dismiss();
			break;
		// 添加按钮
		case R.id.vp_btn_add:
			if (v.getTag() != null) {
				String checkname = (String) v.getTag();
				// 空白终端
				boolean isBlank = getResources().getString(R.string.plan_blantitle).equals(checkname);
				// 有效铺货目标终端
				boolean isPuHuo = getResources().getString(R.string.plan_puhuotitle).equals(checkname);
				// 有效销售目标终端
				boolean isXiaoShou = getResources().getString(R.string.plan_xiaoshoutitle).equals(checkname);

				// 道具生动化(废弃了)
				boolean isDaoJu = getResources().getString(R.string.plan_daojutitle).equals(checkname);
				// 产品生动化
				boolean isChanPin = getResources().getString(R.string.plan_product).equals(checkname);
				// 冰冻化
				boolean isBingDong = getResources().getString(R.string.plan_bingdongtitle).equals(checkname);
				// 竞品清除计划(QC竞品)
				boolean isJingPin = getResources().getString(R.string.plan_jingpintitle).equals(checkname);
				// 订单目标
				boolean dingdan = getResources().getString(R.string.plan_dingdan).equals(checkname);

				if (isBlank) {
					if (blankProductList == null || blankProductList.isEmpty()) {
						Toast.makeText(getApplicationContext(), "没有可选择的产品", Toast.LENGTH_SHORT).show();
					} else {
						ShowPopuptWindow(blankProductList, checkname);
					}
				} else if (isPuHuo) {
					if (puhuoProductList == null || puhuoProductList.isEmpty()) {
						Toast.makeText(getApplicationContext(), "没有可选择的产品", Toast.LENGTH_SHORT).show();
					} else {
						ShowPopuptWindow(puhuoProductList, checkname);
					}
				} else if (isXiaoShou) {
					if (xiaoshouProductList == null || xiaoshouProductList.isEmpty()) {
						Toast.makeText(getApplicationContext(), "没有可选择的产品", Toast.LENGTH_SHORT).show();
					} else {
						ShowPopuptWindow(xiaoshouProductList, checkname);

					}
				} else if (dingdan)// 订单目标
				{
					if (dingdanProductList == null || dingdanProductList.isEmpty()) {
						Toast.makeText(getApplicationContext(), "没有可选择的产品", Toast.LENGTH_SHORT).show();
					} else {
						ShowPopuptWindow(dingdanProductList, checkname);

					}
				} else if (isDaoJu)// (此判断废弃了)
				{
					ShowDiologWindow(huaProductList, checkname);
				} else if (isChanPin) {
					ShowDiologWindow(huaProductList, checkname);
				}

				else if (isBingDong) {
					ShowDiologWindow(huaProductList, checkname);
				} else if (isJingPin) {
					dialogProduct();
				}
			}
			break;
		case R.id.makeplan_sp_line:
			Toast.makeText(getApplicationContext(), "没有可用线路选择", Toast.LENGTH_SHORT).show();
			break;

		case R.id.makeplan_btn_am1:
			int hour_am1 = Integer.valueOf(amStartStr.substring(0, 2));
			int minute_am1 = Integer.valueOf(amStartStr.substring(3, 5));

			TimePickerDialog tpdam1 = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

					String hourOfDayStr = hourOfDay + "";
					if (hourOfDay < 10) {
						hourOfDayStr = "0" + hourOfDay;
					}
					String minuteStr = minute + "";
					if (minute < 10) {
						minuteStr = "0" + minute;
					}
					String copyamStartStr = amStartStr;
					amStartStr = hourOfDayStr + ":" + minuteStr;

					amStart = Integer.valueOf(amStartStr.replace(":", ""));
					// 跟a2比较
					if (amStart > amEnd) {
						Toast.makeText(getApplicationContext(), "开始时间大于结束时间，请修改", Toast.LENGTH_SHORT).show();
						amStartStr = copyamStartStr;
						return;
					}
					btn_am1.setText(amStartStr);

				}
			}, hour_am1, minute_am1, true);
			tpdam1.setCanceledOnTouchOutside(false);
			if (!tpdam1.isShowing()) {
				tpdam1.show();
			}

			break;
		case R.id.workplan_btn_am2:

			int hour_am2 = Integer.valueOf(amEndStr.substring(0, 2));
			int minute_am2 = Integer.valueOf(amEndStr.substring(3, 5));
			TimePickerDialog tpdam2 = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					String hourOfDayStr = hourOfDay + "";
					if (hourOfDay < 10) {
						hourOfDayStr = "0" + hourOfDay;
					}
					String minuteStr = minute + "";
					if (minute < 10) {
						minuteStr = "0" + minute;
					}
					// 跟a1 p1 比较
					String copyamEndStr = amEndStr;
					amEndStr = hourOfDayStr + ":" + minuteStr;
					amEnd = Integer.valueOf(amEndStr.replace(":", ""));
					if (amEnd < amStart) {
						amEndStr = copyamEndStr;
						Toast.makeText(getApplicationContext(), "结束时间小于开始时间,请修改", Toast.LENGTH_SHORT).show();
						return;
					} else if (amEnd > pmStart) {
						amEndStr = copyamEndStr;
						Toast.makeText(getApplicationContext(), "上午结束时间大于下午开始时间,请修改", Toast.LENGTH_SHORT).show();
						return;
					}
					btn_am2.setText(amEndStr);
				}
			}, hour_am2, minute_am2, true);
			tpdam2.setCanceledOnTouchOutside(false);
			if (!tpdam2.isShowing()) {
				tpdam2.show();
			}

			break;
		case R.id.workplan_btn_pm1:
			int hour_pm1 = Integer.valueOf(pmStartStr.substring(0, 2));
			int minute_pm1 = Integer.valueOf(pmStartStr.substring(3, 5));
			TimePickerDialog tpdpm1 = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					String minuteStr = minute + "";
					if (minute < 10) {
						minuteStr = "0" + minute;
					}
					// 跟//a2 /p2 比较
					String copypmStartStr = pmStartStr;
					pmStartStr = hourOfDay + ":" + minuteStr;
					pmStart = Integer.valueOf(pmStartStr.replace(":", ""));
					if (pmStart < 1200) {
						pmStartStr = copypmStartStr;
						Toast.makeText(getApplicationContext(), "下午时间必须12：00之后", Toast.LENGTH_SHORT).show();
						return;
					} else if (pmStart < amEnd) {
						pmStartStr = copypmStartStr;
						Toast.makeText(getApplicationContext(), "下午开始时间小于上午结束时间，请修改", Toast.LENGTH_SHORT).show();
						return;
					} else if (pmStart > pmEnd) {
						pmStartStr = copypmStartStr;
						Toast.makeText(getApplicationContext(), "开始时间大于结束时间，请修改", Toast.LENGTH_SHORT).show();
						return;
					}
					btn_pm1.setText(pmStartStr);

				}
			}, hour_pm1, minute_pm1, true);
			tpdpm1.setCanceledOnTouchOutside(false);
			if (!tpdpm1.isShowing()) {
				tpdpm1.show();
			}

			break;
		case R.id.makeplan_btn_pm2:
			int hour_pm2 = Integer.valueOf(pmEndStr.substring(0, 2));
			int minute_pm2 = Integer.valueOf(pmEndStr.substring(3, 5));
			TimePickerDialog tpdpm2 = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					String minuteStr = minute + "";
					if (minute < 10) {
						minuteStr = "0" + minute;
					}
					// 跟p1比较
					String copypmEndStr = pmEndStr;
					pmEndStr = hourOfDay + ":" + minuteStr;
					pmEnd = Integer.valueOf(pmEndStr.replace(":", ""));
					if (pmEnd < pmStart) {
						pmEndStr = copypmEndStr;
						Toast.makeText(getApplicationContext(), "结束时间小于开始时间，请修改", Toast.LENGTH_SHORT).show();
						return;
					} else {
						btn_pm2.setText(pmEndStr);
					}

				}
			}, hour_pm2, minute_pm2, true);
			tpdpm2.setCanceledOnTouchOutside(false);
			if (!tpdpm2.isShowing()) {
				tpdpm2.show();
			}
			break;
		// 确定日计划
		case R.id.banner_navigation_rl_confirm:
		//case R.id.banner_navigation_bt_confirm:
			savePlan();
			break;
		}
	}

	/**
	 * 空白终端添加 弹出popupwindow
	 * 
	 * @param lsit
	 */
	private VpLvItemStc blankItem = null;
	private VpLvItemStc puhuoItem = null;
	private VpLvItemStc xiaoshouItem = null;
	private VpLvItemStc dingdanItem = null;
	private int blankClickPosition = -1;
	private int puhuoClickPosition = -1;
	private int xiaoshouClickPosition = -1;
	private int dingdanClickPosition = -1;
	private String checkname;
	private boolean isBlank;
	private boolean isPuHuo;
	private boolean isXiaoShou;
	private boolean isDingDan;
	private ListView listView;
	private ScrollView makeplansv;
	private PlanListViewAdapter planListViewAdapter;

	/***
	 * 展示产品界面 弹窗展示产品列表 空白终端,有效销售,有效铺货
	 */
	private void ShowPopuptWindow(CopyOnWriteArrayList<MstProductM> productList, String checkname) {
		// 点击添加之后的popupWindow
		// inflater = (LayoutInflater)
		// this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View popupWindow = inflater.inflate(R.layout.operation_popupwindow, null);
		mPopupWindow = new PopupWindow(popupWindow, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent));
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		// 取消
		Button btn_back = (Button) popupWindow.findViewById(R.id.list_pop_btn_back);
		// 确定添加那些
		Button btn_ok = (Button) popupWindow.findViewById(R.id.list_pop_btn_ok);
		btn_ok.setTag(checkname);
		btn_back.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		TextView tv_title = (TextView) popupWindow.findViewById(R.id.pop_tv_title);
		tv_title.setText(getString(R.string.makeplan_productselect));
		listView = (ListView) popupWindow.findViewById(R.id.pop_lv);
		// 设置弹出的popupWindow的列表方式为单选
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		BlankTerminalAddAdapter popAddAdapter = new BlankTerminalAddAdapter(this, productList, checkname);
		listView.setAdapter(popAddAdapter);
		mPopupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);

	}

	/**
	 * 空白终端删除的时候把产品添加回原来集合里面
	 * 
	 * @param product
	 */
	public void addProductList(MstProductM product) {
		// if(list_VpLvItemStcs.get(puhuoPosition) != null){
		//
		// puhuoProductList.add(product);
		// }else if(list_VpLvItemStcs.get(xiaoshouPosition) !=null){
		// xiaoshouProductList.add(product);
		// }

		//
		if (isBlank) {

			blankProductList.add(product);
		} else if (isXiaoShou) {
			xiaoshouProductList.add(product);
		} else if (isPuHuo) {
			puhuoProductList.add(product);
		} else if (isDingDan) {
			dingdanProductList.add(product);
		}

	}

	public class BlankTerminalAddAdapter extends BaseAdapter {

		private Context context;
		private List<MstProductM> list;
		private LayoutInflater inflater;
		boolean isBlank = false;// 空白终端
		boolean isPuHuo = false;// 有效铺货目标终端
		boolean isXiaoShou = false;// 有效销售目标终端
		boolean isDingDan = false;// 订单目标

		public BlankTerminalAddAdapter(Context context, List<MstProductM> list, String checkname) {
			this.context = context;
			this.list = list;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			isBlank = getResources().getString(R.string.plan_blantitle).equals(checkname);// 空白终端
			isPuHuo = getResources().getString(R.string.plan_puhuotitle).equals(checkname);// 有效铺货目标终端
			isXiaoShou = getResources().getString(R.string.plan_xiaoshoutitle).equals(checkname);// 有效销售目标终端
			isDingDan = getResources().getString(R.string.plan_dingdan).equals(checkname);// 订单目标

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.operation_pop_lvitem, null);
				ViewHolder vh = new ViewHolder();
				vh.tv = (TextView) convertView.findViewById(R.id.pop_tv_lvitem);
				convertView.setTag(vh);
			}
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			final MstProductM mstProductM = list.get(position);
			viewHolder.tv.setText(mstProductM.getProname());

			if (isBlank && position == blankClickPosition) {
				convertView.setBackgroundColor(context.getResources().getColor(R.color.bg_content_color_orange));
			} else if (isPuHuo && position == puhuoClickPosition) {
				convertView.setBackgroundColor(context.getResources().getColor(R.color.bg_content_color_orange));
			} else if (isXiaoShou && position == xiaoshouClickPosition) {
				convertView.setBackgroundColor(context.getResources().getColor(R.color.bg_content_color_orange));
			} else if (isDingDan && position == dingdanClickPosition)// 订单目标
			{
				convertView.setBackgroundColor(context.getResources().getColor(R.color.bg_content_color_orange));
			} else {
				convertView.setBackgroundColor(Color.WHITE);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isBlank) {
						blankClickPosition = position;
						Log.i(TAG, "空白终端产品选择" + blankClickPosition);
						blankItem = new VpLvItemStc();
						blankItem.setPcolitemkey(FunUtil.getUUID());
						blankItem.setName(mstProductM.getProname());
						blankItem.setKey(mstProductM.getProductkey());
					} else if (isPuHuo) {
						puhuoClickPosition = position;
						Log.i(TAG, "有效铺货终端产品选择" + puhuoClickPosition);
						puhuoItem = new VpLvItemStc();
						puhuoItem.setPcolitemkey(FunUtil.getUUID());
						puhuoItem.setName(mstProductM.getProname());
						puhuoItem.setKey(mstProductM.getProductkey());
					} else if (isXiaoShou) {
						xiaoshouClickPosition = position;
						Log.i(TAG, "有效销售终端产品选择" + xiaoshouClickPosition);
						xiaoshouItem = new VpLvItemStc();
						xiaoshouItem.setPcolitemkey(FunUtil.getUUID());
						xiaoshouItem.setName(mstProductM.getProname());
						xiaoshouItem.setKey(mstProductM.getProductkey());
					} else if (isDingDan) {
						dingdanClickPosition = position;
						Log.i(TAG, "有效销售终端产品选择" + xiaoshouClickPosition);
						dingdanItem = new VpLvItemStc();
						dingdanItem.setPcolitemkey(FunUtil.getUUID());
						dingdanItem.setName(mstProductM.getProname());
						dingdanItem.setKey(mstProductM.getProductkey());
						dingdanItem.setPronamekey(mstProductM.getProductkey());
					}
					v.setBackgroundColor(context.getResources().getColor(R.color.bg_content_color_orange));
					BlankTerminalAddAdapter.this.notifyDataSetChanged();
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView tv;
		}
	}

	/**
	 * 保存计划
	 */

	private void savePlan() {
		AndroidDatabaseConnection connection = null;
		try {
			MstPlancollectionInfo plancollectionInfo = new MstPlancollectionInfo();// c
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);// 屏蔽掉自动提交模式只有调用
											// connection.commit();才会提交
			Dao<MstPlancollectionInfo, String> collectionInfoDao = helper.getMstPlancollectionInfoDao();

			// String userCode = ConstValues.loginSession.getUserCode();
			String userCode = PrefUtils.getString(getApplicationContext(), "userCode", "");
			String plantempkey = planTempChecks.get(0).getPlantempkey();// 对应计划模板主键,
																		// 多个计划指标对应一个计划模板主键
			Date createDate = canlendar.getTime();

			// 保存用户计划表
			saveMstPlanforuserM(userCode, plantempkey, createDate);

			// 计划主键PLANKEY
			Log.e(TAG, "plantempkey PAD端计划模板指标主表 中的模板主键" + plantempkey);
			for (int i = 0; i < planTempChecks.size(); i++) {// b
				String checkkey = planTempChecks.get(i).getCheckkey();//
				String pcheckkey = FunUtil.getUUID();// 计划指标主键

				// 保存计划指标信息
				pcheckkey = saveMstPlancheckInfo(pcheckkey, checkkey, createDate, userCode, i);

				List<VpLvItemStc> plancheckInfoDatas = list_VpLvItemStcs.get(i); // 获取当前view里的list
				Log.e(TAG, "pcheckkey MstPlancheckInfo B主键" + pcheckkey);
				Log.e(TAG, "checkkey PAD端计划模板指标主表中的主键" + checkkey);
				String viewPagertitle = viewTitles.get(i);
				Log.i(TAG, "处理“" + viewPagertitle + "”的数据");
				if (!make_or_modify)// 修改模式 数据删除
				{
					// 编辑工作计划 空白终端删除 制定时不走这里 3
					// 空白终端删除已经移除的产品
					if (isBlank(viewPagertitle) || isPuhuo(viewPagertitle) || isXiaoShou(viewPagertitle)) {
						// MakePlanViewPagerAdapter makePlanViewPagerAdapter =
						// adapters.get(i);
						// List<String> blankDeleteIds =
						// makePlanViewPagerAdapter.getBlankDeleteIds();
						// collectionInfoDao.deleteIds(blankDeleteIds);
						StringBuffer buffer = new StringBuffer();
						// buffer.append("update mst_plancollection_info set deleteflag='1',padisconsistent='0'  where ");
						buffer.append("delete from mst_plancollection_info  where ");
						if (!defaultLineKey.equals(editLineKey)) {
							List<MstPlancollectionInfo> lst = collectionInfoDao.queryForEq("pcheckkey", pcheckkey);
							if (!CheckUtil.IsEmpty(lst)) {
								buffer.append("pcolitemkey in (");
								for (MstPlancollectionInfo item : lst) {
									buffer.append("'").append(item.getPcolitemkey()).append("',");// 计划采集项主键
								}
								buffer.deleteCharAt(buffer.length() - 1).append(")");
								collectionInfoDao.executeRawNoArgs(buffer.toString());
							}
						} else {
							MakePlanViewPagerAdapter makePlanViewPagerAdapter = adapters.get(i);
							List<String> blankDeleteIds = makePlanViewPagerAdapter.getBlankDeleteIds();
							if (!CheckUtil.IsEmpty(blankDeleteIds)) {
								buffer.append("pcolitemkey in (");
								for (String id : blankDeleteIds) {
									buffer.append("'").append(id).append("',");
								}
								buffer.deleteCharAt(buffer.length() - 1).append(")");
								collectionInfoDao.executeRawNoArgs(buffer.toString());
							}
						}
					}
					// 竞品清除计划(QC竞品) 道具生动化 产品生动化 冰冻化 订单目标 删除已经移除的产品
					else if (isJingPin(viewPagertitle) || isDaoJu(viewPagertitle) || isChanPin(viewPagertitle) || isBingDong(viewPagertitle) || isDingDan(viewPagertitle)) {

						// MakePlanViewPagerAdapter makePlanViewPagerAdapter =
						// adapters.get(i);
						// List<String> blankDeleteIds =
						// makePlanViewPagerAdapter.getBlankDeleteIds();
						// collectionInfoDao.deleteIds(blankDeleteIds);
						StringBuffer buffer = new StringBuffer();
						// buffer.append("update mst_plancollection_info set deleteflag='1',padisconsistent='0'  where ");
						buffer.append("delete from mst_plancollection_info  where ");

						if (!defaultLineKey.equals(editLineKey)) {
							List<MstPlancollectionInfo> lst = collectionInfoDao.queryForEq("pcheckkey", pcheckkey);
							if (!CheckUtil.IsEmpty(lst)) {
								buffer.append("pcolitemkey in (");
								for (MstPlancollectionInfo item : lst) {
									buffer.append("'").append(item.getPcolitemkey()).append("',");// 计划采集项主键
								}
								buffer.deleteCharAt(buffer.length() - 1).append(")");
								collectionInfoDao.executeRawNoArgs(buffer.toString());
							}
						} else {
							MakePlanViewPagerAdapter makePlanViewPagerAdapter = adapters.get(i);
							List<String> blankDeleteIds = makePlanViewPagerAdapter.getBlankDeleteIds();
							if (!CheckUtil.IsEmpty(blankDeleteIds)) {
								buffer.append("pcolitemkey in (");
								for (String id : blankDeleteIds) {
									buffer.append("'").append(id).append("',");
								}
								buffer.deleteCharAt(buffer.length() - 1).append(")");
								collectionInfoDao.executeRawNoArgs(buffer.toString());
							}
						}

					}
				}

				// 计划指标与采集项信息 数据保存
				if (plancheckInfoDatas != null && plancheckInfoDatas.size() > 0) {
					// 查MstPlancollectionInfo表数据 id不能变

					for (int j = 0; j < plancheckInfoDatas.size(); j++) {// c 1
																			// 2
																			// plancheckInfoDatas//
																			// 0152e64bc6f447c0a18b0413ed486cfd
						VpLvItemStc vpLvItemStc = plancheckInfoDatas.get(j);
						String colitemkey = vpLvItemStc.getKey();// 道具生动化,110对不对
						String pcolitemkey = vpLvItemStc.getPcolitemkey();
						Log.e(TAG, "colitemkey MstPlancollectionInfo // 产品主键/组合主键  KT版主键 产品主键" + colitemkey);
						plancollectionInfo = new MstPlancollectionInfo();
						plancollectionInfo.setPcolitemkey(pcolitemkey);
						if (make_or_modify) {// 1 2
												// 指定
												// 制定模式下 其他 促销活动推进 为第一档
												// 剩下的指标为第二档
												// 第一档可以不上传终端
												// 第二档必须上传终端

							// 其他 促销活动推进 为第一档
							/*
							 * if(getResources().getString(R.string.plan_promotion
							 * ).equals(viewPagertitle) ||
							 * getResources().getString
							 * (R.string.plan_other).equals(viewPagertitle)){
							 * 
							 * plancollectionInfo.setCredate(createDate);//
							 * plancollectionInfo.setPcheckkey(pcheckkey);//
							 * 计划指标主键 // pad端生成的随机数
							 * plancollectionInfo.setCheckkey(checkkey);// 指标主键
							 * plancollectionInfo.setPcolitemkey(pcolitemkey);//
							 * 计划采集项主键 Log.e(TAG,
							 * "pcolitemkey MstPlancollectionInfo 生成C主键" +
							 * pcolitemkey);
							 * plancollectionInfo.setTermnum(Long.parseLong
							 * (plancheckInfoDatas.get(j).getNum()));// 数量
							 * plancollectionInfo
							 * .setTermnames(plancheckInfoDatas
							 * .get(j).getTerm());// 终端名称
							 * plancollectionInfo.setColitemkey(colitemkey);//
							 * 空白终端产品主键/采集项主键
							 * plancollectionInfo.setProductkey(plancheckInfoDatas
							 * .get(j).getPronamekey());// 道具生动化 产品名称key
							 * plancollectionInfo.setUpdateuser(userCode);
							 * plancollectionInfo.setCreuser(userCode); if
							 * (getResources
							 * ().getString(R.string.plan_blantitle)
							 * .equals(viewPagertitle)) {// 空白终端 1 // 产品主键
							 * plancollectionInfo.setPlantype(planType_blank);
							 * 
							 * // 空白终端写入productkey
							 * plancollectionInfo.setProductkey(colitemkey); }
							 * else if
							 * (getResources().getString(R.string.plan_promotion
							 * ).equals(viewPagertitle)) {// 促销活动推进
							 * plancollectionInfo.setPlantype("2"); } else if
							 * (getResources
							 * ().getString(R.string.plan_puhuotitle
							 * ).equals(viewPagertitle)) {// 有效铺货目标终端
							 * plancollectionInfo.setPlantype(planType_puhuo);
							 * 
							 * // 有效铺货目标终端写入productkey
							 * plancollectionInfo.setProductkey(colitemkey); }
							 * else if
							 * (getResources().getString(R.string.plan_xiaoshoutitle
							 * ).equals(viewPagertitle)) {// 有效销售目标终端
							 * plancollectionInfo
							 * .setPlantype(planType_xiaoshou);
							 * 
							 * // 有效销售目标终端写入productkey
							 * plancollectionInfo.setProductkey(colitemkey); }
							 * else if
							 * (getResources().getString(R.string.plan_jingpintitle
							 * ).equals(viewPagertitle)) {// 竞品推进计划
							 * plancollectionInfo.setPlantype("5"); } else if
							 * (getResources
							 * ().getString(R.string.plan_daojutitle
							 * ).equals(viewPagertitle)
							 * ||getResources().getString
							 * (R.string.plan_product).equals(viewPagertitle)
							 * ||getResources
							 * ().getString(R.string.plan_bingdongtitle
							 * ).equals(viewPagertitle)) {// 各种化
							 * plancollectionInfo.setPlantype("6"); } else { //
							 * 其它什么的 plancollectionInfo.setPlantype("0");// 1 }
							 * plancollectionInfo
							 * .setPadisconsistent(ConstValues.FLAG_0);
							 * plancollectionInfo
							 * .setDeleteflag(ConstValues.FLAG_0);
							 * 
							 * }else{// 第二档必须上传终端
							 */

							// // 终端数目大于0 或者 订单目标
							if (Long.parseLong(plancheckInfoDatas.get(j).getNum()) > 0 || dingDanCheckkey.equals(checkkey)) {
								plancollectionInfo.setCredate(createDate);//
								plancollectionInfo.setPcheckkey(pcheckkey);// 计划指标主键
																			// pad端生成的随机数
								plancollectionInfo.setCheckkey(checkkey);// 指标主键
								plancollectionInfo.setPcolitemkey(pcolitemkey);// 计划采集项主键
								Log.e(TAG, "pcolitemkey MstPlancollectionInfo 生成C主键" + pcolitemkey);
								plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
								plancollectionInfo.setOrdernum(plancheckInfoDatas.get(j).getDingdannum());// 数量
								plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
								plancollectionInfo.setColitemkey(colitemkey);// 空白终端产品主键/采集项主键
								plancollectionInfo.setProductkey(plancheckInfoDatas.get(j).getPronamekey());// 道具生动化
																											// 产品名称key
								plancollectionInfo.setUpdateuser(userCode);
								plancollectionInfo.setCreuser(userCode);
								if (getResources().getString(R.string.plan_blantitle).equals(viewPagertitle)) {// 空白终端
																												// 1
																												// 产品主键
									plancollectionInfo.setPlantype(planType_blank);

									// 空白终端写入productkey
									plancollectionInfo.setProductkey(colitemkey);
								} else if (getResources().getString(R.string.plan_promotion).equals(viewPagertitle)) {// 促销活动推进
									plancollectionInfo.setPlantype("2");
								} else if (getResources().getString(R.string.plan_puhuotitle).equals(viewPagertitle)) {// 有效铺货目标终端
									plancollectionInfo.setPlantype(planType_puhuo);

									// 有效铺货目标终端写入productkey
									plancollectionInfo.setProductkey(colitemkey);
								} else if (getResources().getString(R.string.plan_xiaoshoutitle).equals(viewPagertitle)) {// 有效销售目标终端
									plancollectionInfo.setPlantype(planType_xiaoshou);

									// 有效销售目标终端写入productkey
									plancollectionInfo.setProductkey(colitemkey);
								} else if (getResources().getString(R.string.plan_jingpintitle).equals(viewPagertitle)) {// 竞品清除计划(QC竞品)
									plancollectionInfo.setPlantype("5");
								} else if (getResources().getString(R.string.plan_daojutitle).equals(viewPagertitle) || getResources().getString(R.string.plan_product).equals(viewPagertitle) || getResources().getString(R.string.plan_bingdongtitle).equals(viewPagertitle)) {// 各种化
									plancollectionInfo.setPlantype("6");
								} else {
									// 其它什么的
									plancollectionInfo.setPlantype("0");// 1
								}
								plancollectionInfo.setPadisconsistent(ConstValues.FLAG_0);
								plancollectionInfo.setDeleteflag(ConstValues.FLAG_0);
							}
							// }

						} else {// 修改模式
								// 产品生动化 冰冻化 的修改模式要单独保存
								// if(isDaoJu(viewPagertitle) ||
								// isChanPin(viewPagertitle) ||
								// isBingDong(viewPagertitle)){
							if (isChanPin(viewPagertitle) || isBingDong(viewPagertitle)) {
								// 产品生动化 冰冻化 的修改要单独保存
								Map<String, Object> hashMap = new HashMap<String, Object>(); // 2
								hashMap.put("checkkey", checkkey);
								hashMap.put("pcheckkey", pcheckkey);
								hashMap.put("pcolitemkey", pcolitemkey);
								List<MstPlancollectionInfo> plancollectionInfos = collectionInfoDao.queryForFieldValues(hashMap);

								// 若是查询没有这个采集项 当终端数目大于0时做
								if (plancollectionInfos.isEmpty()) {
									if (Long.parseLong(plancheckInfoDatas.get(j).getNum()) > 0) {// 终端数目大于0时才保存
										plancollectionInfo.setCredate(createDate);
										plancollectionInfo.setPcheckkey(pcheckkey);// 计划指标主键
										plancollectionInfo.setCheckkey(checkkey);// 指标主键
										plancollectionInfo.setPcolitemkey(pcolitemkey);// 计划采集项主键
										Log.e(TAG, "pcolitemkey MstPlancollectionInfo 生成C主键" + pcolitemkey);
										plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
										plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
										plancollectionInfo.setColitemkey(colitemkey);// 空白终端产品主键/采集项主键
										plancollectionInfo.setProductkey(plancheckInfoDatas.get(j).getPronamekey());// 道具生动化
																													// 产品名称key
										plancollectionInfo.setUpdateuser(userCode);
										plancollectionInfo.setCreuser(userCode);
										if (isBlank(viewPagertitle)) {// 空白终端
																		// 产品主键
											plancollectionInfo.setPlantype(planType_blank);
										} else if (isPuhuo(viewPagertitle)) {// 有效铺货目标终端
											plancollectionInfo.setPlantype("3");
										} else if (isXiaoShou(viewPagertitle)) {// 有效销售目标终端
											plancollectionInfo.setPlantype("4");
										} else if (isDaoJu(viewPagertitle) || isChanPin(viewPagertitle) || isBingDong(viewPagertitle)) {
											plancollectionInfo.setPlantype("6");
										}
									}

								} else // 若是查询有这个采集项
								{
									// 如果终端数大于0 更新
									plancollectionInfo = plancollectionInfos.get(0); // 2
									Log.i(TAG, "查plancollectionInfos size:" + plancollectionInfos.size() + "实际 有一个数据");
									Log.e(TAG, "MstPlancollectionInfo 修改的C主键" + plancollectionInfo.getPcolitemkey());
									plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
									plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
									plancollectionInfo.setDeleteflag(ConstValues.FLAG_0);
									plancollectionInfo.setPadisconsistent(ConstValues.FLAG_0);
									plancollectionInfo.setUpdateuser(userCode);
									plancollectionInfo.setColitemkey(colitemkey);// 采集项主键

									// 小于0 删除该条记录

								}

							} else if (isDaoJu(viewPagertitle)) {

								Map<String, Object> hashMap = new HashMap<String, Object>(); // 2
								hashMap.put("checkkey", checkkey);
								hashMap.put("pcheckkey", pcheckkey);
								hashMap.put("colitemkey", colitemkey);

								List<MstPlancollectionInfo> plancollectionInfos = collectionInfoDao.queryForFieldValues(hashMap);
								Log.i(TAG, "MstPlancollectionInfo 实际只有一条数据,出现多个个说明存储有错：空白终端除外" + plancollectionInfos.size() + "--" + viewPagertitle);

								if (plancollectionInfos.isEmpty()) {

									Log.i(TAG, viewPagertitle + "---查询不到此数据，说明是空白终端新增的数据或者数据库表被修改了");

									if (Long.parseLong(plancheckInfoDatas.get(j).getNum()) > 0) { // 终端数目大于0
										plancollectionInfo.setCredate(createDate);
										plancollectionInfo.setPcheckkey(pcheckkey);// 计划指标主键
										plancollectionInfo.setCheckkey(checkkey);// 指标主键
										plancollectionInfo.setPcolitemkey(pcolitemkey);// 计划采集项主键
										Log.e(TAG, "pcolitemkey MstPlancollectionInfo 生成C主键" + pcolitemkey);
										plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
										plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
										plancollectionInfo.setColitemkey(colitemkey);// 空白终端产品主键/采集项主键
										plancollectionInfo.setUpdateuser(userCode);
										plancollectionInfo.setCreuser(userCode);
									}

								} else {
									// 终端大于0 做修改
									plancollectionInfo = plancollectionInfos.get(0); // 2
									Log.i(TAG, "查plancollectionInfos size:" + plancollectionInfos.size() + "实际 有一个数据");
									Log.e(TAG, "MstPlancollectionInfo 修改的C主键" + plancollectionInfo.getPcolitemkey());
									plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
									plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
									plancollectionInfo.setDeleteflag(ConstValues.FLAG_0);
									plancollectionInfo.setPadisconsistent(ConstValues.FLAG_0);
									plancollectionInfo.setUpdateuser(userCode);
									plancollectionInfo.setColitemkey(colitemkey);// 采集项主键
									// 终端小于0 做删除
								}

							} else if (isDingDan(viewPagertitle)) {

								Map<String, Object> hashMap = new HashMap<String, Object>(); // 2
								hashMap.put("checkkey", checkkey);
								hashMap.put("pcheckkey", pcheckkey);
								hashMap.put("colitemkey", colitemkey);

								List<MstPlancollectionInfo> plancollectionInfos = collectionInfoDao.queryForFieldValues(hashMap);
								Log.i(TAG, "MstPlancollectionInfo 实际只有一条数据,出现多个个说明存储有错：空白终端除外" + plancollectionInfos.size() + "--" + viewPagertitle);

								if (plancollectionInfos.isEmpty()) {

									Log.i(TAG, viewPagertitle + "---查询不到此数据，说明是空白终端新增的数据或者数据库表被修改了");

									if (Long.parseLong(plancheckInfoDatas.get(j).getNum()) > 0 || dingDanCheckkey.equals(checkkey)) { // 终端数目大于0
										plancollectionInfo.setCredate(createDate);
										plancollectionInfo.setPcheckkey(pcheckkey);// 计划指标主键
										plancollectionInfo.setCheckkey(checkkey);// 指标主键
										plancollectionInfo.setPcolitemkey(pcolitemkey);// 计划采集项主键
										Log.e(TAG, "pcolitemkey MstPlancollectionInfo 生成C主键" + pcolitemkey);
										plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
										plancollectionInfo.setOrdernum(plancheckInfoDatas.get(j).getDingdannum());// 数量
										plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
										plancollectionInfo.setProductkey(plancheckInfoDatas.get(j).getPronamekey());// 道具生动化
																													// 产品名称key
										plancollectionInfo.setColitemkey(colitemkey);// 空白终端产品主键/采集项主键
										plancollectionInfo.setUpdateuser(userCode);
										plancollectionInfo.setCreuser(userCode);
									}

								} else {
									// 终端大于0 做修改
									plancollectionInfo = plancollectionInfos.get(0); // 2
									Log.i(TAG, "查plancollectionInfos size:" + plancollectionInfos.size() + "实际 有一个数据");
									Log.e(TAG, "MstPlancollectionInfo 修改的C主键" + plancollectionInfo.getPcolitemkey());
									plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
									plancollectionInfo.setOrdernum(plancheckInfoDatas.get(j).getDingdannum());// 数量
									plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
									plancollectionInfo.setProductkey(plancheckInfoDatas.get(j).getPronamekey());// 道具生动化
																												// 产品名称key
									plancollectionInfo.setDeleteflag(ConstValues.FLAG_0);
									plancollectionInfo.setPadisconsistent(ConstValues.FLAG_0);
									plancollectionInfo.setUpdateuser(userCode);
									plancollectionInfo.setColitemkey(colitemkey);// 采集项主键
									// 终端小于0 做删除
								}

							}

							else if (isJingPin(viewPagertitle)) {// 竞品清除计划(QC竞品)的修改要单独保存

								// 竞品清除计划(QC竞品)的修改要单独保存
								Map<String, Object> hashMap = new HashMap<String, Object>(); // 2
								hashMap.put("checkkey", checkkey);
								hashMap.put("pcheckkey", pcheckkey);
								hashMap.put("pcolitemkey", pcolitemkey);
								List<MstPlancollectionInfo> plancollectionInfos = collectionInfoDao.queryForFieldValues(hashMap);

								if (plancollectionInfos.isEmpty()) {
									if (Long.parseLong(plancheckInfoDatas.get(j).getNum()) > 0) {// 终端数目大于0
										plancollectionInfo.setCredate(createDate);
										plancollectionInfo.setPcheckkey(pcheckkey);// 计划指标主键
										plancollectionInfo.setCheckkey(checkkey);// 指标主键
										plancollectionInfo.setPcolitemkey(pcolitemkey);// 计划采集项主键
										Log.e(TAG, "pcolitemkey MstPlancollectionInfo 生成C主键" + pcolitemkey);
										plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
										plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
										plancollectionInfo.setProductkey(plancheckInfoDatas.get(j).getPronamekey());// 竞品清除计划(QC竞品)
																													// 产品名称key
										plancollectionInfo.setColitemkey(colitemkey);// 空白终端产品主键/采集项主键
										plancollectionInfo.setUpdateuser(userCode);
										plancollectionInfo.setCreuser(userCode);
										if (isBlank(viewPagertitle)) {// 空白终端
																		// 产品主键
											plancollectionInfo.setPlantype(planType_blank);
										} else if (isPuhuo(viewPagertitle)) {// 有效铺货目标终端
											plancollectionInfo.setPlantype("3");
										} else if (isXiaoShou(viewPagertitle)) {// 有效销售目标终端
											plancollectionInfo.setPlantype("4");
										} else if (isJingPin(viewPagertitle)) {// 有效销售目标终端
											plancollectionInfo.setPlantype("5");
										} else if (getResources().getString(R.string.plan_daojutitle).equals(viewPagertitle) || getResources().getString(R.string.plan_product).equals(viewPagertitle) || getResources().getString(R.string.plan_bingdongtitle).equals(viewPagertitle)) {// 各种化
											plancollectionInfo.setPlantype("6");
										}
									}

								} else {
									plancollectionInfo = plancollectionInfos.get(0); // 2
									Log.i(TAG, "查plancollectionInfos size:" + plancollectionInfos.size() + "实际 有一个数据");
									Log.e(TAG, "MstPlancollectionInfo 修改的C主键" + plancollectionInfo.getPcolitemkey());
									plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
									plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
									plancollectionInfo.setDeleteflag(ConstValues.FLAG_0);
									plancollectionInfo.setPadisconsistent(ConstValues.FLAG_0);
									plancollectionInfo.setUpdateuser(userCode);
									plancollectionInfo.setColitemkey(colitemkey);// 采集项主键

									//

								}

								// 除道具生动化,产品生动化,道具生动化,竞品清除计划(QC竞品) 的修改保存
							} else {

								Map<String, Object> hashMap = new HashMap<String, Object>(); // 2
								hashMap.put("checkkey", checkkey);
								hashMap.put("pcheckkey", pcheckkey);
								hashMap.put("colitemkey", colitemkey);

								List<MstPlancollectionInfo> plancollectionInfos = collectionInfoDao.queryForFieldValues(hashMap);
								Log.i(TAG, "MstPlancollectionInfo 实际只有一条数据,出现多个个说明存储有错：空白终端除外" + plancollectionInfos.size() + "--" + viewPagertitle);

								if (plancollectionInfos.isEmpty()) {

									Log.i(TAG, viewPagertitle + "---查询不到此数据，说明是空白终端新增的数据或者数据库表被修改了");
									if (isBlank(viewPagertitle) || isPuhuo(viewPagertitle) || isXiaoShou(viewPagertitle)) {// 空白终端修改新增的数据
																															// 添加

										if (Long.parseLong(plancheckInfoDatas.get(j).getNum()) > 0) { // 终端数目大于0
											plancollectionInfo.setCredate(createDate);
											plancollectionInfo.setPcheckkey(pcheckkey);// 计划指标主键
											plancollectionInfo.setCheckkey(checkkey);// 指标主键
											plancollectionInfo.setPcolitemkey(pcolitemkey);// 计划采集项主键
											Log.e(TAG, "pcolitemkey MstPlancollectionInfo 生成C主键" + pcolitemkey);
											plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
											plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
											plancollectionInfo.setColitemkey(colitemkey);// 空白终端产品主键/采集项主键
											plancollectionInfo.setUpdateuser(userCode);
											plancollectionInfo.setCreuser(userCode);
											if (isBlank(viewPagertitle)) {// 空白终端
																			// 产品主键
												plancollectionInfo.setPlantype(planType_blank);

												plancollectionInfo.setProductkey(colitemkey);
											} else if (isPuhuo(viewPagertitle)) {// 有效铺货目标终端
												plancollectionInfo.setPlantype("3");
												plancollectionInfo.setProductkey(colitemkey);
											} else if (isXiaoShou(viewPagertitle)) {// 有效销售目标终端
												plancollectionInfo.setPlantype("4");
												plancollectionInfo.setProductkey(colitemkey);
											}
										}

									} else {// 其他 促销活动

										if (Long.parseLong(plancheckInfoDatas.get(j).getNum()) > 0) { // 终端数目大于0
											plancollectionInfo.setCredate(createDate);
											plancollectionInfo.setPcheckkey(pcheckkey);// 计划指标主键
											plancollectionInfo.setCheckkey(checkkey);// 指标主键
											plancollectionInfo.setPcolitemkey(pcolitemkey);// 计划采集项主键
											Log.e(TAG, "pcolitemkey MstPlancollectionInfo 生成C主键" + pcolitemkey);
											plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
											plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
											plancollectionInfo.setColitemkey(colitemkey);// 空白终端产品主键/采集项主键
											plancollectionInfo.setUpdateuser(userCode);
											plancollectionInfo.setCreuser(userCode);
										}
									}

								} else {
									// 终端大于0 做修改
									plancollectionInfo = plancollectionInfos.get(0); // 2
									Log.i(TAG, "查plancollectionInfos size:" + plancollectionInfos.size() + "实际 有一个数据");
									Log.e(TAG, "MstPlancollectionInfo 修改的C主键" + plancollectionInfo.getPcolitemkey());
									plancollectionInfo.setTermnum(Long.parseLong(plancheckInfoDatas.get(j).getNum()));// 数量
									plancollectionInfo.setTermnames(plancheckInfoDatas.get(j).getTerm());// 终端名称
									plancollectionInfo.setDeleteflag(ConstValues.FLAG_0);
									plancollectionInfo.setPadisconsistent(ConstValues.FLAG_0);
									plancollectionInfo.setUpdateuser(userCode);
									plancollectionInfo.setColitemkey(colitemkey);// 采集项主键
									// 终端小于0 做删除
								}
							}

						}

						// 取出需要回显的终端的id 存到remarts 字段 ，在编辑的时候可以重新还原数据
						MakePlanViewPagerAdapter makePlanViewPagerAdapter = adapters.get(i);
						Map<String, List<String>> selectTerminalsMap = makePlanViewPagerAdapter.getSelectTerminalsMap();
						// k 计划采集项主键，之前用position作为主键 但是空白终端可编辑造成position变动
						// 对应的回显list的数据出错
						List<String> list = selectTerminalsMap.get(pcolitemkey);// 该pcolitemkey采集项对应的
																				// 选择的终端key
						// 将终端保存到另一张表中 mst_planterminal_m
						// 1 先将mst_planterminal_m表中关于此pcolitemkey的记录全部删除
						// 2 再将新纪录写入表中
						// 1
						List<MstPlanTerminalM> MstPlanTerminalMs = MstPlanTerminalMDao.queryForEq("pcolitemkey", pcolitemkey);
						MstPlanTerminalMDao.delete(MstPlanTerminalMs);
						// 2
						if (!CheckUtil.IsEmpty(list)) {
							MstPlanTerminalM mstPlanTerminalM;
							for (int k = 0; k < list.size(); k++) {
								List<MstTerminalinfoM> MstTerminalinfoMs = MstTerminalinfoMDao.queryForEq("terminalkey", list.get(k));
								mstPlanTerminalM = new MstPlanTerminalM();
								mstPlanTerminalM.setPlanterminalkey(FunUtil.getUUID());
								mstPlanTerminalM.setPcolitemkey(pcolitemkey);
								mstPlanTerminalM.setTerminalkey(MstTerminalinfoMs.get(0).getTerminalkey());
								mstPlanTerminalM.setTerminalname(MstTerminalinfoMs.get(0).getTerminalname());
								mstPlanTerminalM.setTlevel(MstTerminalinfoMs.get(0).getTlevel());
								// mstPlanTerminalM.setCreuser(ConstValues.loginSession.getUserCode());
								mstPlanTerminalM.setCreuser(PrefUtils.getString(getApplicationContext(), "userCode", ""));
								mstPlanTerminalM.setPadisconsistent("0");
								// mstPlanTerminalM.setUpdateuser(ConstValues.loginSession.getUserCode());
								mstPlanTerminalM.setUpdateuser(PrefUtils.getString(getApplicationContext(), "userCode", ""));
								mstPlanTerminalM.setCretime(new Date());
								mstPlanTerminalM.setUpdatetime(new Date());
								MstPlanTerminalMDao.createOrUpdate(mstPlanTerminalM);
							}
						}

						if (!CheckUtil.IsEmpty(list)) {
							List<String> colitemkeys = new ArrayList<String>(list.size());// 1
																							// 2
							for (int k = 0; k < list.size(); k++) {
								colitemkeys.add(list.get(k));
							}
							plancollectionInfo.setRemarks(JsonUtil.toJson(colitemkeys));
						} else {
							plancollectionInfo.setRemarks("");
							plancollectionInfo.setTermnum(0l);
							// 终端数目为0 删除该条记录

						}

						// collectionInfoDao.createOrUpdate(plancollectionInfo);
						// 终端数目大于0才保存 (除非是订单目标指标)
						if (plancollectionInfo.getTermnum() > 0 || dingDanCheckkey.equals(checkkey)) {
							collectionInfoDao.createOrUpdate(plancollectionInfo);
						} else {
							collectionInfoDao.deleteById(plancollectionInfo.getPcolitemkey());
						}
					}

				}
			}

			connection.commit(null);
			ViewUtil.sendMsg(this, R.string.succes);
		} catch (SQLException e) {
			ViewUtil.sendMsg(this, R.string.saveerror);
			e.printStackTrace();
			try {
				if (connection != null) {
					connection.rollback(null);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		DayPlanActivity.this.finish();

	}

	/***
	 * 保存计划指标信息
	 * 
	 * @param pcheckkey
	 *            计划主键
	 * @param pcheckkey
	 *            计划指标主键
	 * @param checkkey
	 *            指标主键
	 * @param createDate
	 *            时间
	 * @param userCode
	 *            用户编码
	 * @param i
	 * @return
	 * @throws SQLException
	 */
	private String saveMstPlancheckInfo(String pcheckkey, String checkkey, Date createDate, String userCode, int i) throws SQLException {
		Dao<MstPlancheckInfo, String> checkInfoDao = helper.getMstPlancheckInfoDao();
		MstPlancheckInfo plancheckInfo = new MstPlancheckInfo();
		// 获取B表的主键, 当不是修改重新生成主键
		if (make_or_modify) {// 1
			plancheckInfo.setPlankey(plankey); // 计划主键
			plancheckInfo.setPcheckkey(pcheckkey); // 计划指标主键
			plancheckInfo.setCheckkey(checkkey); // 指标主键
			plancheckInfo.setCredate(createDate);
			plancheckInfo.setUpdatetime(createDate);
			plancheckInfo.setCreuser(userCode);
			plancheckInfo.setUpdateuser(userCode);
			plancheckInfo.setPadisconsistent(ConstValues.FLAG_0);
			plancheckInfo.setDeleteflag(ConstValues.FLAG_0);
		} else {// 不是修改状态下 为预览从数据库获取
				// 修改 2
			Map<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("checkkey", checkkey);
			hashMap.put("plankey", plankey);
			List<MstPlancheckInfo> mstplancheckinfos = checkInfoDao.queryForFieldValues(hashMap);
			Log.i(TAG, "mstplancheckinfos 实际只有一条数据出现多个说明数据有变化：以前计划查询不到此指标 如果为0 说明 指标删除或者是新增加的指标" + mstplancheckinfos.size() + "---" + planTempChecks.get(i).getCheckname());
			if (!mstplancheckinfos.isEmpty()) { // 2
				plancheckInfo = mstplancheckinfos.get(0);
				pcheckkey = plancheckInfo.getPcheckkey();// 查询
				plancheckInfo.setPadisconsistent(ConstValues.FLAG_0);
				plancheckInfo.setUpdatetime(new Date());
				plancheckInfo.setUpdateuser(userCode);
			} else {
				// 当前计划下查询不到此指标 这个指标是新增加的
				plancheckInfo.setPlankey(plankey); // 计划主键
				plancheckInfo.setPcheckkey(pcheckkey); // 计划指标主键
				plancheckInfo.setCheckkey(checkkey); // 指标主键
				plancheckInfo.setPadisconsistent(ConstValues.FLAG_0);
				plancheckInfo.setCredate(createDate);
				plancheckInfo.setUpdatetime(createDate);
				plancheckInfo.setCreuser(userCode);
				plancheckInfo.setUpdateuser(userCode);
			}
		}
		checkInfoDao.createOrUpdate(plancheckInfo);// 操作数据库
		return pcheckkey;
	}

	/***
	 * 保存用户计划表
	 * 
	 * @param userCode
	 * @param plantempkey
	 * @param createDate
	 * @throws SQLException
	 */
	private void saveMstPlanforuserM(String userCode, String plantempkey, Date createDate) throws SQLException {
		Dao<MstPlanforuserM, String> planforuserDao = helper.getMstPlanforuserMDao();
		MstPlanforuserM planforuserM = new MstPlanforuserM();// a
		if (make_or_modify) {// 1(制定)
								// 制定
								// 保存到人员计划主表
			planforuserM.setPlankey(plankey); // 计划主键
			Log.i(TAG, "plankey 保存/修改MstPlanforuserMA主键" + plankey);
			planforuserM.setPlantitle(getWeek(week));// 计划标题
			planforuserM.setPlandate(planDate);// 计划日期
			// 通过PAD_PLANTEMPCHECK得到4个主键都一样
			planforuserM.setPlantempkey(plantempkey);// 计划模版主键
			planforuserM.setUserid(userCode);
			planforuserM.setCredate(createDate);
			planforuserM.setPadisconsistent(ConstValues.FLAG_0);
			planforuserM.setDeleteflag(ConstValues.FLAG_0);
			planforuserM.setCreuser(userCode);

		} else {
			// 修改 2
			planforuserM = planforuserDao.queryForId(plankey);
			if (planforuserM == null) {
				Log.e(TAG, "需要修改的对象为null 数据存在有问题请检查");
			}
		}
		planforuserM.setPadisconsistent(ConstValues.FLAG_0);
		planforuserM.setUpdatetime(createDate);
		planforuserM.setUpdateuser(userCode);
		planforuserM.setLinekey(defaultLineKey);// 线路主键
		planforuserM.setPlanamf(btn_am1.getText().toString()); // 上午from
		planforuserM.setPlanamt(btn_am2.getText().toString()); // 上午to
		planforuserM.setPlanpmf(btn_pm1.getText().toString()); // 下午from
		planforuserM.setPlanpmt(btn_pm2.getText().toString()); // 下午to
		planforuserM.setPlanstatus("0"); // 计划状态 = 0 未审核
		planforuserM.setPlantype("0");// 标志日工作计划
		planforuserDao.createOrUpdate(planforuserM);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		// 点击SP得到线路主键
		defaultLineKey = routres.get(position).getRoutekey();
		// 初始化此渠道数量显示内容 // ywm 20160413
		initTypeData2(defaultLineKey);
		// List<MstProductM> products =
		// service.queryProduct(MakePlanActivity.this,
		// ConstValues.loginSession.getGridId(),
		// ConstValues.loginSession.getDisId());
		List<MstProductM> products = service.queryProduct(DayPlanActivity.this, PrefUtils.getString(getApplicationContext(), "gridId", ""), PrefUtils.getString(getApplicationContext(), "disId", ""));
		blankProductList = new CopyOnWriteArrayList<MstProductM>(products);
		puhuoProductList = new CopyOnWriteArrayList<MstProductM>(products);
		xiaoshouProductList = new CopyOnWriteArrayList<MstProductM>(products);
		dingdanProductList = new CopyOnWriteArrayList<MstProductM>(products);

		huaProductList = new CopyOnWriteArrayList<MstProductM>(products);
		// initViewPager();
		initListView();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	/***
	 * 是否是空白终端
	 * 
	 * @param checkname
	 * @return
	 */
	public boolean isBlank(String checkname) {
		return getResources().getString(R.string.plan_blantitle).equals(checkname);
	}

	/***
	 * 是否有效铺货目标终端
	 * 
	 * @param checkname
	 * @return
	 */
	public boolean isPuhuo(String checkname) {
		return getResources().getString(R.string.plan_puhuotitle).equals(checkname);
	}

	/***
	 * 是否有效销售目标终端
	 * 
	 * @param checkname
	 * @return
	 */
	public boolean isXiaoShou(String checkname) {
		return getResources().getString(R.string.plan_xiaoshoutitle).equals(checkname);
	}

	/***
	 * 是否是道具生动化
	 * 
	 * @param checkname
	 * @return
	 */
	public boolean isDaoJu(String checkname) {
		return getResources().getString(R.string.plan_daojutitle).equals(checkname);
	}

	/***
	 * 是否是生动化布置
	 * 
	 * @param checkname
	 * @return
	 */
	public boolean issdhbuz(String checkname) {
		return getResources().getString(R.string.plan_sdhbz).equals(checkname);
	}

	/***
	 * 是否是产品生动化
	 * 
	 * @param checkname
	 * @return
	 */
	public boolean isChanPin(String checkname) {
		return getResources().getString(R.string.plan_product).equals(checkname);
	}

	/***
	 * 是否是冰冻化
	 * 
	 * @param checkname
	 * @return
	 */
	public boolean isBingDong(String checkname) {
		return getResources().getString(R.string.plan_bingdongtitle).equals(checkname);
	}

	/***
	 * 是否是竞品清除计划(QC竞品)
	 * 
	 * @param checkname
	 * @return
	 */
	public boolean isJingPin(String checkname) {
		return getResources().getString(R.string.plan_jingpintitle).equals(checkname);
	}

	/***
	 * 是否订单目标
	 * 
	 * @param checkname
	 * @return
	 */
	public boolean isDingDan(String checkname) {
		return getResources().getString(R.string.plan_dingdan).equals(checkname);
	}

	/**
	 * 产品列表弹出窗口 一个2级菜单 产品生动化,冰冻化,竞品清除计划
	 */
	public void ShowDiologWindow(final CopyOnWriteArrayList<MstProductM> productList, final String checkname) {
		DbtLog.logUtils(TAG, "dialogProduct()");
		// 如果已打开，直接返回
		if (productDialgo != null && productDialgo.isShowing()) {
			return;
		}
		pro = null;
		productes = null;

		// 加载弹出窗口layout
		itemForm = DayPlanActivity.this.getLayoutInflater().inflate(R.layout.operation_addrelation, null);
		productDialgo = new AlertDialog.Builder(
		// 设置对话框不能被取消
				DayPlanActivity.this).setCancelable(false).create();
		// 设置视图显示在对话框中
		productDialgo.setView(itemForm, 0, 0, 0, 0);
		productDialgo.show();

		// 采集项列表(第一列)
		proLv = (ListView) itemForm.findViewById(R.id.addrelation_lv_agency);
		// 产品列表(第二列)
		productLv1 = (ListView) itemForm.findViewById(R.id.addrelation_lv_product);

		// channelPriceEt =
		// (EditText)itemForm.findViewById(R.id.addrelation_et_channelprice);
		// sellPriceEt =
		// (EditText)itemForm.findViewById(R.id.addrelation_et_sellprice);
		for (int i = 0; i < planTempChecks.size(); i++)// 遍历每个指标
		{
			if (checkname.equals(planTempChecks.get(i).getCheckname())) {//
				PadPlantempcheckM padPlantempcheckM = planTempChecks.get(i);
				String checkkey = padPlantempcheckM.getCheckkey();// 对应模板主键根据它取得模板对应的指标状态(名称)

				List<PadPlantempcollectionInfo> colitemList = service.queryColitemname(checkkey);// pad端计划模板指标对应采集项关联信息表的数据
				if (colitemList.size() > 0) {
					// 指标选项
					final List<VpLvItemStc> vpLvItemStcS = new ArrayList<VpLvItemStc>();
					for (int j = 0; j < colitemList.size(); j++) {
						VpLvItemStc item = new VpLvItemStc();
						item.setPcolitemkey(FunUtil.getUUID());
						item.setName(colitemList.get(j).getColitemname());
						item.setKey(colitemList.get(j).getColitemkey());
						vpLvItemStcS.add(item);
					}

					proLv.setDividerHeight(0);
					if (!CheckUtil.IsEmpty(vpLvItemStcS)) {
						pro = vpLvItemStcS.get(0);
					}
					caijixiangAdapter = new ListViewProductAdapter(this, vpLvItemStcS);
					proLv.setAdapter(caijixiangAdapter);

					productes = new ArrayList<MstProductM>();
					//
					proLv.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							productes = new ArrayList<MstProductM>();
							pro = (VpLvItemStc) proLv.getItemAtPosition(position);
							if (!CheckUtil.IsEmpty(vpLvItemStcS)) {
								product1 = productList.get(0);
							}
							caijixiangproductAdapter = new MakePlanProductAdapter(DayPlanActivity.this, productList);
							productLv1.setAdapter(caijixiangproductAdapter);
							productLv1.refreshDrawableState();

							caijixiangAdapter.setSelectItemId(position);
							caijixiangAdapter.notifyDataSetChanged();
						}
					});

					// 产品
					if (!(pro == null || CheckUtil.IsEmpty(productList))) {
						product1 = productList.get(0);
					}
					productLv1.setDividerHeight(0);
					if (!CheckUtil.IsEmpty(ConstValues.agencyMineLst)) {
						caijixiangproductAdapter = new MakePlanProductAdapter(DayPlanActivity.this, productList);
						productLv1.setAdapter(caijixiangproductAdapter);
					}

					productLv1.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							product1 = (MstProductM) productLv1.getItemAtPosition(position);

							// 遍历用户选择的产品集合
							int count = 0;
							for (int i = 0; i < productes.size(); i++) {
								// 该产品用户选择过,数量加1
								if (product1.equals(productes.get(i))) {
									count++;
								}
							}
							// 用户选择过就在集合中移除,没选择过就添加
							if (count == 1) {
								productes.remove(product1);
							} else {
								productes.add(product1);
							}

							// productAdapter.setSelectItemId(position);
							caijixiangproductAdapter.addSelected(position);
							caijixiangproductAdapter.notifyDataSetChanged();

						}
					});

					// 确定
					Button sureBt = (Button) itemForm.findViewById(R.id.addrelation_bt_confirm);
					sureBt.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (ViewUtil.isDoubleClick(arg0.getId(), 2500))
								return;
							DbtLog.logUtils(TAG, "确认添加");
							if (productes.size() == 0) {
								// Toast.makeText(getApplicationContext(),
								// "请选择产品", 0).show();
								productDialgo.cancel();

							} else {

								if (isDaoJu(checkname)) {// 添加道具 产品名称

									List<VpLvItemStc> daojuVpLvItemStcs = list_VpLvItemStcs.get(daojuPosition);
									MakePlanViewPagerAdapter daojuAdapter = adapters.get(daojuPosition);
									List<String> productnames = new ArrayList<String>();
									if (daojuVpLvItemStcs.size() > 0) {
										// 获取已有产品名称
										for (VpLvItemStc vpLvItemStc : daojuVpLvItemStcs) {
											// 用户选择的采集项 与原有条目中采集项是一样的 添加到集合中
											// 寻找原有的采集项
											if (pro.getName().equals(vpLvItemStc.getName())) {
												productnames.add(vpLvItemStc.getProname());// 添加产品名称
											}
										}
									}

									// 添加新的采集项
									for (MstProductM product : productes) {
										if (productnames.size() > 0 && productnames.contains(product.getProname())) {
											Toast.makeText(DayPlanActivity.this, getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();
										} else {
											VpLvItemStc vpLvItemStc = new VpLvItemStc();
											vpLvItemStc.setPcolitemkey(FunUtil.getUUID());//
											vpLvItemStc.setName(pro.getName());
											vpLvItemStc.setKey(pro.getKey());// 采集项主键
											vpLvItemStc.setProname(product.getProname());// 产品名称
											vpLvItemStc.setPronamekey(product.getProductkey());// 产品名称key

											daojuVpLvItemStcs.add(vpLvItemStc);
											daojuAdapter.notifyDataSetChanged();
										}
									}
								}

								if (isChanPin(checkname)) {// 添加产品生动化 产品名称
									List<VpLvItemStc> chanpinVpLvItemStcs = list_VpLvItemStcs.get(chanpinPosition);
									MakePlanViewPagerAdapter chanpinAdapter = adapters.get(chanpinPosition);
									List<String> productnames = new ArrayList<String>();
									if (chanpinVpLvItemStcs.size() > 0) {
										// 获取产品名称集合

										for (VpLvItemStc vpLvItemStc : chanpinVpLvItemStcs) {
											// 用户选择的采集项 与原有条目中采集项是一样的 添加到集合中
											// 寻找原有的采集项
											if (pro.getName().equals(vpLvItemStc.getName())) {
												productnames.add(vpLvItemStc.getProname());// 添加产品名称
											}
										}
									}

									for (MstProductM product : productes) {
										if (productnames.size() > 0 && productnames.contains(product.getProname())) {
											Toast.makeText(DayPlanActivity.this, getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();

										} else {
											VpLvItemStc vpLvItemStc = new VpLvItemStc();
											vpLvItemStc.setPcolitemkey(FunUtil.getUUID());// 计划采集项主键
											vpLvItemStc.setName(pro.getName());// 采集项名称
											vpLvItemStc.setKey(pro.getKey());// 采集项主键
											vpLvItemStc.setProname(product.getProname());// 产品名称
											vpLvItemStc.setPronamekey(product.getProductkey());// 产品名称key

											chanpinVpLvItemStcs.add(vpLvItemStc);
											chanpinAdapter.notifyDataSetChanged();
										}
									}
								}
								if (isBingDong(checkname)) {// 添加冰冻化 产品名称
									List<VpLvItemStc> bingdongVpLvItemStcs = list_VpLvItemStcs.get(bingdongPosition);
									MakePlanViewPagerAdapter bingdongAdapter = adapters.get(bingdongPosition);
									List<String> productnames = new ArrayList<String>();
									if (bingdongVpLvItemStcs.size() > 0) {
										// 获取产品名称集合

										for (VpLvItemStc vpLvItemStc : bingdongVpLvItemStcs) {
											// 用户选择的采集项 与原有条目中采集项是一样的 添加到集合中
											// 寻找原有的采集项
											if (pro.getName().equals(vpLvItemStc.getName())) {
												productnames.add(vpLvItemStc.getProname());// 添加产品名称
											}
										}
									}

									for (MstProductM product : productes) {
										if (productnames.size() > 0 && productnames.contains(product.getProname())) {
											Toast.makeText(DayPlanActivity.this, getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();

										} else {
											VpLvItemStc vpLvItemStc = new VpLvItemStc();
											vpLvItemStc.setPcolitemkey(FunUtil.getUUID());//
											vpLvItemStc.setName(pro.getName());
											vpLvItemStc.setKey(pro.getKey());// 采集项主键
											vpLvItemStc.setProname(product.getProname());// 产品名称
											vpLvItemStc.setPronamekey(product.getProductkey());// 产品名称key

											bingdongVpLvItemStcs.add(vpLvItemStc);
											bingdongAdapter.notifyDataSetChanged();
										}
									}
								}
								if (isJingPin(checkname)) {// 添加竞品清除计划(QC竞品)
															// 产品名称
									List<VpLvItemStc> jingpinVpLvItemStcs = list_VpLvItemStcs.get(jingpinPosition);
									MakePlanViewPagerAdapter jingpinAdapter = adapters.get(jingpinPosition);
									List<String> productnames = new ArrayList<String>();
									if (jingpinVpLvItemStcs.size() > 0) {
										// 获取产品名称集合

										for (VpLvItemStc vpLvItemStc : jingpinVpLvItemStcs) {
											// 用户选择的采集项 与原有条目中采集项是一样的 添加到集合中
											// 寻找原有的采集项
											if (pro.getName().equals(vpLvItemStc.getName())) {
												productnames.add(vpLvItemStc.getProname());// 添加产品名称
											}
										}
									}

									for (MstProductM product : productes) {
										if (productnames.size() > 0 && productnames.contains(product.getProname())) {
											Toast.makeText(DayPlanActivity.this, getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();

										} else {
											VpLvItemStc vpLvItemStc = new VpLvItemStc();
											vpLvItemStc.setPcolitemkey(FunUtil.getUUID());//
											vpLvItemStc.setName(pro.getName());
											vpLvItemStc.setProname(product.getProname());

											jingpinVpLvItemStcs.add(vpLvItemStc);
											jingpinAdapter.notifyDataSetChanged();
										}
									}
								}
								if (isDingDan(checkname)) {
									// 订单目标 产品名称
									List<VpLvItemStc> DingDanVpLvItemStcs = list_VpLvItemStcs.get(dingdanPosition);
									MakePlanViewPagerAdapter jingpinAdapter = adapters.get(dingdanPosition);
									List<String> productnames = new ArrayList<String>();
									if (DingDanVpLvItemStcs.size() > 0) {
										// 获取产品名称集合

										for (VpLvItemStc vpLvItemStc : DingDanVpLvItemStcs) {
											// 用户选择的采集项 与原有条目中采集项是一样的 添加到集合中
											// 寻找原有的采集项
											if (pro.getName().equals(vpLvItemStc.getName())) {
												productnames.add(vpLvItemStc.getProname());// 添加产品名称
											}
										}
									}

									for (MstProductM product : productes) {
										if (productnames.size() > 0 && productnames.contains(product.getProname())) {
											Toast.makeText(DayPlanActivity.this, getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();

										} else {
											VpLvItemStc vpLvItemStc = new VpLvItemStc();
											vpLvItemStc.setPcolitemkey(FunUtil.getUUID());//
											vpLvItemStc.setName(pro.getName());
											vpLvItemStc.setProname(product.getProname());

											DingDanVpLvItemStcs.add(vpLvItemStc);
											jingpinAdapter.notifyDataSetChanged();
										}
									}

								}
							}

							productDialgo.cancel();
						}
					});

					// 取消
					Button cancelBt = (Button) itemForm.findViewById(R.id.addrelation_bt_cancel);
					cancelBt.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							DbtLog.logUtils(TAG, "取消");
							productDialgo.cancel();
							productDialgo.dismiss();
						}
					});
				} else { // 处理没有采集项时,点击事件不响应的问题
					// 确定
					Button sureBt = (Button) itemForm.findViewById(R.id.addrelation_bt_confirm);
					sureBt.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							DbtLog.logUtils(TAG, "无采集项");
							productDialgo.cancel();
							// productDialgo.dismiss();
						}
					});
					// 取消
					Button cancelBt = (Button) itemForm.findViewById(R.id.addrelation_bt_cancel);
					cancelBt.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							DbtLog.logUtils(TAG, "取消");
							productDialgo.cancel();
							// productDialgo.dismiss();
						}
					});
				}
				break;
			}
		}
	}

	/**
	 * 竞品推进产品列表弹出窗口
	 */
	public void dialogProduct() {
		DbtLog.logUtils(TAG, "dialogProduct()");
		// 如果已打开，直接返回
		if (productDialgo != null && productDialgo.isShowing())
			return;

		// 加载弹出窗口layout
		itemForm = DayPlanActivity.this.getLayoutInflater().inflate(R.layout.shopvisit_addrelation, null);
		productDialgo = new AlertDialog.Builder(DayPlanActivity.this).setCancelable(false).create();
		productDialgo.setView(itemForm, 0, 0, 0, 0);
		productDialgo.show();

		agencyLv = (ListView) itemForm.findViewById(R.id.addrelation_lv_agency);
		productLv = (ListView) itemForm.findViewById(R.id.addrelation_lv_product);
		// channelPriceEt =
		// (EditText)itemForm.findViewById(R.id.addrelation_et_channelprice);
		// sellPriceEt =
		// (EditText)itemForm.findViewById(R.id.addrelation_et_sellprice);

		// 代理商
		agencyLv.setDividerHeight(0);
		if (!CheckUtil.IsEmpty(ConstValues.agencyVieLst)) {
			agency = ConstValues.agencyVieLst.get(0);
		}
		agencyAdapter = new ListViewKeyValueAdapter(DayPlanActivity.this, ConstValues.agencyVieLst, new String[] { "key", "value" }, new int[] { R.drawable.bg_agency_up, R.drawable.bg_agency_down });
		agencyLv.setAdapter(agencyAdapter);

		jp_productes = new ArrayList<KvStc>();
		// 选择不同的经销商,列出不同的产品表
		agencyLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				jp_productes = new ArrayList<KvStc>();
				agency = (KvStc) agencyLv.getItemAtPosition(position);
				if (!CheckUtil.IsEmpty(agency.getChildLst())) {
					product = agency.getChildLst().get(0);
				}
				productAdapter = new ChatVieProductAdapter(DayPlanActivity.this, agency.getChildLst(), new String[] { "key", "value" }, new int[] { R.drawable.bg_product_up, R.drawable.bg_product_down });
				productLv.setAdapter(productAdapter);
				productLv.refreshDrawableState();

				agencyAdapter.setSelectItemId(position);
				agencyAdapter.notifyDataSetChanged();
			}
		});

		// 产品
		if (!(agency == null || CheckUtil.IsEmpty(agency.getChildLst()))) {
			product = agency.getChildLst().get(0);
		}
		productLv.setDividerHeight(0);
		if (!CheckUtil.IsEmpty(ConstValues.agencyVieLst)) {
			productAdapter = new ChatVieProductAdapter(DayPlanActivity.this, ConstValues.agencyVieLst.get(0).getChildLst(), new String[] { "key", "value" }, new int[] { R.drawable.bg_product_up, R.drawable.bg_product_down });
			productLv.setAdapter(productAdapter);
		}

		productLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				product = (KvStc) productLv.getItemAtPosition(position);

				// 遍历用户选择的产品集合
				int count = 0;
				for (int i = 0; i < jp_productes.size(); i++) {
					// 该产品用户选择过,数量加1
					if (product.equals(jp_productes.get(i))) {
						count++;
					}
				}
				// 用户选择过就在集合中移除,没选择过就添加
				if (count == 1) {
					jp_productes.remove(product);
				} else {
					jp_productes.add(product);
				}

				// productAdapter.setSelectItemId(position);
				productAdapter.addSelected(position);
				productAdapter.notifyDataSetChanged();
			}
		});

		// 确定
		Button sureBt = (Button) itemForm.findViewById(R.id.addrelation_bt_confirm);
		sureBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ViewUtil.isDoubleClick(arg0.getId(), 2500))
					return;
				DbtLog.logUtils(TAG, "确认添加");
				if (jp_productes.size() == 0) {
					productDialgo.cancel();
				} else {

					// for (KvStc product : jp_productes) {
					/*
					 * 
					 * 
					 * 
					 * DbtLog.logUtils(TAG,"经销商key:"+agency.getKey()+"、经销商名称:"+
					 * agency
					 * .getValue()+"-->产品key："+product.getKey()+"、产品名称："+product
					 * .getValue()); List<String> proIdLst =
					 * FunUtil.getPropertyByName(dataLst, "proId",
					 * String.class); if (proIdLst.contains(product.getKey())) {
					 * ViewUtil.sendMsg(getActivity(),
					 * R.string.addrelation_msg_repetitionadd);
					 * 
					 * } else { ChatVieStc supplyStc = new ChatVieStc();
					 * supplyStc.setProId(product.getKey());
					 * supplyStc.setProName(product.getValue());
					 * supplyStc.setCommpayId(agency.getKey());
					 * supplyStc.setChannelPrice
					 * (channelPriceEt.getText().toString());
					 * supplyStc.setSellPrice(sellPriceEt.getText().toString());
					 * dataLst.add(supplyStc);
					 * 
					 * statusAdapter.notifyDataSetChanged();
					 * ViewUtil.setListViewHeight(vieStatusLv);
					 * sourceAdapter.notifyDataSetChanged();
					 * ViewUtil.setListViewHeight(vieSourceLv);
					 * productDialgo.cancel(); }
					 */

					// 添加道具 产品名称

					List<VpLvItemStc> jingpinVpLvItemStcs = list_VpLvItemStcs.get(jingpinPosition);
					MakePlanViewPagerAdapter jingpinAdapter = adapters.get(jingpinPosition);
					List<String> productnames = new ArrayList<String>();
					if (jingpinVpLvItemStcs.size() > 0) {
						// 获取已有产品名称
						for (VpLvItemStc vpLvItemStc : jingpinVpLvItemStcs) {
							// 用户选择的采集项 与原有条目中采集项是一样的 添加到集合中
							// 寻找原有的采集项
							/*
							 * if
							 * (agency.getName().equals(vpLvItemStc.getName()))
							 * { productnames.add(vpLvItemStc.getProname());//
							 * 添加产品名称 }
							 */
							String agencyname = agency.getValue();
							if (agency.getValue().equals(vpLvItemStc.getName())) {
								productnames.add(vpLvItemStc.getProname());// 添加产品名称
							}
						}
					}

					// 添加新的采集项
					for (KvStc jpproduct : jp_productes) {
						if (productnames.size() > 0 && productnames.contains(jpproduct.getValue())) {
							Toast.makeText(DayPlanActivity.this, getString(R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();
						} else {
							VpLvItemStc vpLvItemStc = new VpLvItemStc();
							vpLvItemStc.setPcolitemkey(FunUtil.getUUID());//
							vpLvItemStc.setName(agency.getValue());
							vpLvItemStc.setProname(jpproduct.getValue());
							vpLvItemStc.setKey(agency.getKey());// 采集项主键
							vpLvItemStc.setPronamekey(jpproduct.getKey());// 竞品产品主键

							jingpinVpLvItemStcs.add(vpLvItemStc);
							jingpinAdapter.notifyDataSetChanged();
						}
					}

					// }

				}
				productDialgo.cancel();
			}
		});

		// 取消
		Button cancelBt = (Button) itemForm.findViewById(R.id.addrelation_bt_cancel);
		cancelBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DbtLog.logUtils(TAG, "取消");
				productDialgo.cancel();
			}
		});
	}

	private String getWeek(int week) {
		String dayOfWeek = "";
		switch (week) {
		case 0:
			dayOfWeek = "星期日";
			break;
		case 1:
			dayOfWeek = "星期一";
			break;
		case 2:
			dayOfWeek = "星期二";
			break;
		case 3:
			dayOfWeek = "星期三";
			break;
		case 4:
			dayOfWeek = "星期四";
			break;
		case 5:
			dayOfWeek = "星期五";
			break;
		case 6:
			dayOfWeek = "星期六";
			break;

		default:
			break;
		}
		return dayOfWeek;
	}

	class PlanListViewAdapter extends BaseAdapter {

		private List<PadPlantempcheckM> viewLst;
		private List<String> titleLst;
		private IClick mListener;

		public PlanListViewAdapter(List<PadPlantempcheckM> viewLst, List<String> titleLst, IClick listener) {
			this.viewLst = viewLst;
			this.titleLst = titleLst;
			this.mListener = listener;
		}

		@Override
		public int getCount() {
			return viewLst.size();
		}

		@Override
		public PadPlantempcheckM getItem(int position) {
			return viewLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			DoctorHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(DayPlanActivity.this, R.layout.operation_makeplan_listitem, null);
				holder = new DoctorHolder();
				// 计划指标
				holder.tv_planname = (TextView) convertView.findViewById(R.id.vp_tv_title_planname);
				// 采集项名称
				holder.tv_name = (TextView) convertView.findViewById(R.id.vp_tv_title_name1);
				// 终端
				holder.tv_termname = (TextView) convertView.findViewById(R.id.vp_tv_title_name3);
				// 产品名称
				holder.tv_produname = (TextView) convertView.findViewById(R.id.vp_tv_title_name0);
				holder.vp_v_title_view = (View) convertView.findViewById(R.id.vp_v_title_view);
				// 添加
				holder.btn_add = (Button) convertView.findViewById(R.id.vp_btn_add);
				// 操作
				holder.vp_tv_title_operation = (TextView) convertView.findViewById(R.id.vp_tv_title_operation);
				holder.operation_divider = (View) convertView.findViewById(R.id.operation_divider);

				holder.mNoScrollListView = (NoScrollListView) convertView.findViewById(R.id.vp_lv);

				convertView.setTag(holder);
			} else {
				holder = (DoctorHolder) convertView.getTag();
			}

			// 具体的一个计划指标
			PadPlantempcheckM padPlantempcheckM = planTempChecks.get(position);
			checkname = padPlantempcheckM.getCheckname();
			String checkKey = padPlantempcheckM.getCheckkey();

			holder.tv_planname.setText(checkname);

			// 下一界面(MakePlanViewPagerAdapter)用到的变量
			boolean isProductTerminal = false;// 产品终端(空白终端,有效销售,有效铺货)
			boolean isDaojuhua = false;// 道具生动化
			boolean isGezhonghua = false;// 各种XX化(产品生动化,冰冻化)
			boolean isChanpinhua = false;// 产品生动化
			boolean isBingdonghua = false;// 冰冻化
			boolean isjingpintuijin = false;// 竞品清除计划(QC竞品)
			boolean isPromotionhua = false;// 促销活动
			boolean isotherhua = false;// 其他
			boolean isdingdan = false;// 订单目标

			// 本界面用到的变量
			// 空白终端
			boolean isBlank = getResources().getString(R.string.plan_blantitle).equals(checkname);
			// 有效销售
			boolean isPuHuo = getResources().getString(R.string.plan_puhuotitle).equals(checkname);
			// 有效铺货
			boolean isXiaoShou = getResources().getString(R.string.plan_xiaoshoutitle).equals(checkname);
			// 道具生动化
			boolean isDaoJu = getResources().getString(R.string.plan_daojutitle).equals(checkname);
			// 产品生动化
			boolean isChanPin = getResources().getString(R.string.plan_product).equals(checkname);
			// 冰冻化
			boolean isBingDong = getResources().getString(R.string.plan_bingdongtitle).equals(checkname);
			// 竞品清除计划(QC竞品)
			boolean isJingPin = getResources().getString(R.string.plan_jingpintitle).equals(checkname);
			// 生动化布置
			boolean issdhbz = getResources().getString(R.string.plan_sdhbz).equals(checkname);
			// 促销活动推进
			boolean isPromotion = getResources().getString(R.string.plan_promotion).equals(checkname);
			// 其他
			boolean isother = getResources().getString(R.string.plan_other).equals(checkname);
			// 订单目标
			boolean isdingdanmb = getResources().getString(R.string.plan_dingdan).equals(checkname);

			// 根据计划指标不同,设置不同的Listview
			if (isBlank || isPuHuo || isXiaoShou)// 空白终端跟checkname一样 当前是空白终端
			{
				holder.btn_add.setVisibility(View.VISIBLE);// 添加按钮设置显示
				holder.btn_add.setTag(checkname);
				holder.tv_name.setText(getString(R.string.makeplan_proname));// 并把名称改为产品名称
				holder.tv_produname.setVisibility(View.GONE);
				holder.vp_v_title_view.setVisibility(View.GONE);
				if (preview_plan) {
					holder.btn_add.setVisibility(View.GONE);// 添加按钮设置显示
				} else {
					// 操作可见// 删除按钮设置显示
					holder.vp_tv_title_operation.setVisibility(View.VISIBLE);
					holder.operation_divider.setVisibility(View.VISIBLE);
					// holder.btn_add.setOnClickListener(this);
					holder.btn_add.setOnClickListener(mListener);
					holder.btn_add.setTag(position);
				}
				if (isBlank) {
					blankPosition = position;
				} else if (isPuHuo) {
					puhuoPosition = position;
				} else if (isXiaoShou) {
					xiaoshouPosition = position;
				}
				isProductTerminal = true;
			}
			// 产品生动化,冰冻化
			else if (isChanPin || isBingDong) {
				holder.btn_add.setVisibility(View.VISIBLE);
				holder.btn_add.setTag(checkname);
				holder.tv_produname.setVisibility(View.VISIBLE);
				holder.vp_v_title_view.setVisibility(View.VISIBLE);
				holder.tv_name.setText(getString(R.string.makeplan_productname));// 并把名称改为采集项名称
				holder.tv_produname.setText(getString(R.string.makeplan_proname));// 并把名称改为产品名称
				if (preview_plan) {
					holder.btn_add.setVisibility(View.GONE);
				} else {
					// 操作可见// 删除按钮设置显示
					holder.vp_tv_title_operation.setVisibility(View.VISIBLE);
					holder.operation_divider.setVisibility(View.VISIBLE);
					// holder.btn_add.setOnClickListener(this);
					holder.btn_add.setOnClickListener(mListener);
					holder.btn_add.setTag(position);
				}
				if (isDaoJu) {
					daojuPosition = position;
				} else if (isChanPin) {
					chanpinPosition = position;
					isChanpinhua = true;
				} else if (isBingDong) {
					bingdongPosition = position;
					isBingdonghua = true;
				}
				isGezhonghua = true;// 各种XX化
			}

			// 道具生动化
			else if (isDaoJu) {

				holder.btn_add.setVisibility(View.GONE);
				holder.btn_add.setTag(checkname);
				holder.tv_name.setText(getString(R.string.makeplan_productname));// 并把名称改为采集项名称
				holder.tv_produname.setVisibility(View.GONE);
				holder.vp_v_title_view.setVisibility(View.GONE);
				if (issdhbz) {
					sdhbzPosition = position;
				}
				isDaojuhua = true;// 生动化布置

			}
			// 竞品清除计划(QC竞品)
			else if (isJingPin) {

				holder.btn_add.setVisibility(View.VISIBLE);
				holder.tv_produname.setVisibility(View.VISIBLE);
				holder.btn_add.setTag(checkname);
				holder.tv_produname.setVisibility(View.VISIBLE);
				holder.vp_v_title_view.setVisibility(View.VISIBLE);
				holder.tv_name.setText(getString(R.string.makeplan_pinpai));// 并把名称改为品牌
				holder.tv_produname.setText(getString(R.string.makeplan_proname));// 并把名称改为产品名称
				if (preview_plan) {
					holder.btn_add.setVisibility(View.GONE);
				} else {
					// 操作可见// 删除按钮设置显示
					holder.vp_tv_title_operation.setVisibility(View.VISIBLE);
					holder.operation_divider.setVisibility(View.VISIBLE);
					// holder.btn_add.setOnClickListener(this);
					holder.btn_add.setOnClickListener(mListener);
					holder.btn_add.setTag(position);
				}
				if (isJingPin) {
					jingpinPosition = position;
				}
				isjingpintuijin = true;
			} else if (isdingdanmb) {
				holder.btn_add.setVisibility(View.VISIBLE);
				holder.btn_add.setTag(checkname);

				// 采集项(产品名称),订单目标数量,操作
				holder.tv_termname.setVisibility(View.GONE);
				holder.tv_produname.setVisibility(View.GONE);
				holder.vp_v_title_view.setVisibility(View.GONE);

				if (preview_plan) {
					holder.btn_add.setVisibility(View.GONE);
				} else {
					// 操作可见// 删除按钮设置显示
					holder.vp_tv_title_operation.setVisibility(View.VISIBLE);
					holder.operation_divider.setVisibility(View.VISIBLE);
					// holder.btn_add.setOnClickListener(this);
					holder.btn_add.setOnClickListener(mListener);
					holder.btn_add.setTag(position);
				}

				if (isdingdanmb) {
					dingdanPosition = position;
				}
				isdingdan = true;

			}
			// 其他 促销活动推进
			else {
				holder.tv_name.setText(getString(R.string.makeplan_name));// 不是空白终端时tv_name赋值为名称添加按钮也看不见
				holder.tv_produname.setVisibility(View.GONE);
				holder.vp_v_title_view.setVisibility(View.GONE);
				holder.btn_add.setVisibility(View.GONE);
			}

			// ViewPager里listview item显示内容结构体
			ArrayList<VpLvItemStc> vpLvItemStcS = new ArrayList<VpLvItemStc>();

			// 促销活动推进
			if (isPromotion) {
				isPromotionhua = true;
			} else {
				isPromotionhua = false;
			}

			// 其他
			if (isother) {
				isotherhua = true;
			} else {
				isotherhua = false;
			}

			// 显示采集项下面的item
			/*
			 * MakePlanViewPagerAdapter adapter = new
			 * MakePlanViewPagerAdapter(DayPlanActivity.this, vpLvItemStcS,
			 * checkKey, lineKeys, preview_plan, isProductTerminal,
			 * isPromotionhua, isotherhua, isGezhonghua, isjingpintuijin,
			 * isDaojuhua, isChanpinhua, isBingdonghua, isdingdan);
			 */
			MakePlanViewPagerAdapter adapter = adapters.get(position);

			holder.mNoScrollListView.setAdapter(adapter);
			/*
			 * viewTitles.add(checkname);// 放入指标名称 adapters.add(adapter);
			 * list_VpLvItemStcs.add(vpLvItemStcS); // 在底部添加viewpager
			 * viewList.add(view); checkkeys.add(checkKey);
			 */

			return convertView;
		}
	}

	static class DoctorHolder {
		TextView tv_planname;
		TextView tv_name;
		TextView tv_termname;
		TextView tv_produname;
		Button btn_add;
		View vp_v_title_view;
		View operation_divider;
		TextView vp_tv_title_operation;
		NoScrollListView mNoScrollListView;

	}
}
