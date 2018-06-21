package et.tsingtaopad.visit.shopvisit.termindex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.GlobalValues;
import et.tsingtaopad.R;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.ShopVisitActivity;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.visit.shopvisit.termindex.domain.TermIndexStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：TermIndexActivity.java</br> 作者：hongen </br>
 * 创建时间：2013-12-30</br> 功能描述: 巡店拜访--终端指标完成状态</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
// 终端指标完成状态
public class TermIndexActivity extends BaseActivity implements OnClickListener {

	private TermIndexService service;
	private ShopVisitService shopVisitService;
	private final String TAG = "TermIndexActivity";

	private TextView titleTv;
	private Button backBt;
	private TextView confirmBt;
	private TextView visitDateTv;
	private TextView visitDayTv;
	private LinearLayout layout;
	private LayoutInflater inflater;

	private String seeFlag;
	private String isFirstVisit;
	private MstTermListMStc termStc;
	private MstVisitM visitM;

	// 上次拜访时间
	private String lastTime;

	AlertDialog dialog;
	private Long starttime;
	private RelativeLayout backRl;
	private RelativeLayout confirmRl;
	private ProgressDialog progressDialog;
	private final int INITFINISH = 1;
	private final int DATAFINISH = 2;
	private final int DATACHULI = 3;

	private List<KvStc> indexValueLst;
	private Map<String, List<TermIndexStc>> proMap;
	private Map<String, List<TermIndexStc>> IndexMap;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {

			// 提示信息
			case INITFINISH:
				// initDate();

				break;

			// 主线程更新UI数据
			case DATACHULI:
				initViewDate();
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
		starttime = new Date().getTime();
		setContentView(R.layout.shopvisit_termindex);
		
		

		// 弹出进度框
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("初始化中...");
		progressDialog.show();

		// 子线程中 初始化控件, 初始化数据
		this.initDate2();
		
		/*this.initView();
		this.initDate();*/
        
	}

	// 子线程中 初始化控件, 初始化数据
	private void initDate2() {

		Thread thread = new Thread() {

			@Override
			public void run() {

				try {

					DbtLog.logUtils(TAG, "initDate2: run thread" );
					initView();

					// 查询之前时间
					Long time = new Date().getTime();
					service = new TermIndexService(getBaseContext());
					shopVisitService = new ShopVisitService(getBaseContext(), null);

					// 获取传送参数
					seeFlag = getIntent().getExtras().getString("seeFlag");
					isFirstVisit = getIntent().getExtras().getString("isFirstVisit");
					termStc = (MstTermListMStc) getIntent().getExtras().getSerializable("termStc");
					
					DbtLog.logUtils(TAG, "initDate2:" + termStc.getTerminalname());

					// 获取最后一次拜访的信息
					visitM = shopVisitService.findNewVisit(termStc.getTerminalkey(), false);
					// 获取上一次结束拜访信息
					MstVisitM visitM_end = shopVisitService.findNewVisit(termStc.getTerminalkey(), true);

					if (visitM == null) {// 若此终端从未拜访过 设置此终端的上次拜访时间为1901-01-01
											// 01:01
						lastTime = "1901-01-01 01:01";// 2016-03-08 11:24
						// 读取上次拜访的时间
						// (查询拜访主表的关于此终端的所有拜访记录,取最新一次,不管是否上传成功过,查询此记录的拜访时间)
					} else {
						lastTime = DateUtil.formatDate(0, visitM.getVisitdate());// 2016-03-08
																					// 11:24
					}

					// 计算访终端上次拜访日期
					titleTv.setText(termStc.getTerminalname());
					if (visitM_end != null) {
						if (visitM_end == null || CheckUtil.isBlankOrNull(visitM_end.getVisitdate())) {
							visitDateTv.setText("");
							visitDayTv.setText("0");
						} else {
							visitDateTv.setText(DateUtil.formatDate(0, visitM_end.getVisitdate()));
							int day = (int) DateUtil.diffDays(new Date(),
									DateUtil.parse(visitM_end.getVisitdate().substring(0, 8), "yyyyMMdd"));
							visitDayTv.setText(String.valueOf(day));
						}

					} else {
						visitDateTv.setText("");
						visitDayTv.setText("0");
					}

					if (visitM != null) {
						// 设置当前拜访线路
						visitM.setRoutekey(termStc.getRoutekey());
					}
					// 获取各渠道下对应的指标及指标值关系

					indexValueLst = service.queryCheckType(termStc.getMinorchannel());
					DbtLog.logUtils(TAG, "indexValueLst 指标的结构数据:" + indexValueLst.size());

					// 获取终端上一次的指标采集数据
					List<TermIndexStc> termIndexLst = new ArrayList<TermIndexStc>();
					if (visitM != null) { // 终端指标完成状态值的获取
						termIndexLst = service.queryTermIndex(visitM.getVisitkey(), visitM.getUploadFlag(),
								visitM.getTerminalkey(), termStc.getMinorchannel());
					}
					proMap = service.distinctByProduct(termIndexLst);
					IndexMap = service.initTermIndex(termIndexLst);
					// 查询之后时间
					Long time1 = new Date().getTime();
					Log.e("message", "查询数据库时间1" + (time1 - time));

				} catch (Exception e) {
					Log.e(TAG, "MakePlanActivity INIT EXCEPTION:", e);
				} finally {
					handler.sendEmptyMessage(DATACHULI);
				}
			}
		};
		thread.start();
	}

	// 加载数据
	private void initViewDate() {
		TextView itemTv;
		TermIndexAdapter adapter;
		NoScrollListView itemLv;
		try {
			if (!CheckUtil.IsEmpty(indexValueLst)) {
				View itemV;
				for (KvStc item : indexValueLst) {

					// 动态指标名称
					itemV = inflater.inflate(R.layout.termindex_label, null);
					itemTv = (TextView) itemV.findViewById(R.id.termindex_tv_label);
					itemTv.setText(item.getValue());
					layout.addView(itemV);

					// 显示指标下各指标值
					for (KvStc valueItem : item.getChildLst()) {
						itemV = inflater.inflate(R.layout.termindex_lvtitle, null);
						itemTv = (TextView) itemV.findViewById(R.id.termindex_tv_title);
						// 空白 不合格 不再显示
						if (!("空白".equals(valueItem.getValue()) || "不合格".equals(valueItem.getValue()))) {
							itemTv.setText(item.getValue() + " (" + valueItem.getValue() + ")");
							adapter = new TermIndexAdapter(TermIndexActivity.this, proMap.get(item.getKey() + valueItem.getKey()), IndexMap);
							itemLv = (NoScrollListView) itemV.findViewById(R.id.termindex_lv);
							itemLv.setAdapter(adapter);
							layout.addView(itemV);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "initViewDate :", e);
		} finally {
			DbtLog.logUtils(TAG, "数据加载成功");
			handler.sendEmptyMessage(DATAFINISH);
		}
	}

	private void initView() {

		titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
		confirmBt = (TextView) findViewById(R.id.banner_navigation_bt_confirm);
		visitDateTv = (TextView) findViewById(R.id.termindex_tv_visitterm_date);
		visitDayTv = (TextView) findViewById(R.id.termindex_tv_visitterm_day);

		// 获取页面中用于动态添加的容器组件及指标模板
		layout = (LinearLayout) findViewById(R.id.termindex_layout);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		backBt.setOnClickListener(this);
		//confirmBt.setOnClickListener(this);

		confirmBt.setVisibility(View.VISIBLE);

	}

	private void initDate() {
		// 查询之前时间
		Long time = new Date().getTime();
		service = new TermIndexService(getBaseContext());
		shopVisitService = new ShopVisitService(getBaseContext(), null);

		// 获取传送参数
		seeFlag = getIntent().getExtras().getString("seeFlag");
		termStc = (MstTermListMStc) getIntent().getExtras().getSerializable("termStc");

		// 获取最后一次拜访的信息
		visitM = shopVisitService.findNewVisit(termStc.getTerminalkey(), false);
		// 获取上一次结束拜访信息
		MstVisitM visitM_end = shopVisitService.findNewVisit(termStc.getTerminalkey(), true);

		if (visitM == null) {// 若此终端从未拜访过 设置此终端的上次拜访时间为1901-01-01 01:01
			lastTime = "1901-01-01 01:01";// 2016-03-08 11:24
			// 读取上次拜访的时间 (查询拜访主表的关于此终端的所有拜访记录,取最新一次,不管是否上传成功过,查询此记录的拜访时间)
		} else {
			lastTime = DateUtil.formatDate(0, visitM.getVisitdate());// 2016-03-08
																		// 11:24
		}

		// 计算访终端上次拜访日期
		titleTv.setText(termStc.getTerminalname());
		if (visitM_end != null) {
			if (visitM_end == null || CheckUtil.isBlankOrNull(visitM_end.getVisitdate())) {
				visitDateTv.setText("");
				visitDayTv.setText("0");
			} else {
				visitDateTv.setText(DateUtil.formatDate(0, visitM_end.getVisitdate()));
				int day = (int) DateUtil.diffDays(new Date(),
						DateUtil.parse(visitM_end.getVisitdate().substring(0, 8), "yyyyMMdd"));
				visitDayTv.setText(String.valueOf(day));
			}

		} else {
			visitDateTv.setText("");
			visitDayTv.setText("0");
		}

		if (visitM != null) {
			// 设置当前拜访线路
			visitM.setRoutekey(termStc.getRoutekey());
		}
		// 获取各渠道下对应的指标及指标值关系
		List<KvStc> indexValueLst = service.queryCheckType(termStc.getMinorchannel());

		// 获取终端上一次的指标采集数据
		List<TermIndexStc> termIndexLst = new ArrayList<TermIndexStc>();
		if (visitM != null) { // 终端指标完成状态值的获取
			termIndexLst = service.queryTermIndex(visitM.getVisitkey(), visitM.getUploadFlag(), visitM.getTerminalkey(),
					termStc.getMinorchannel());
		}
		Map<String, List<TermIndexStc>> proMap = service.distinctByProduct(termIndexLst);
		Map<String, List<TermIndexStc>> IndexMap = service.initTermIndex(termIndexLst);
		// 查询之后时间
		Long time1 = new Date().getTime();
		Log.e("message", "查询数据库时间1" + (time1 - time));

		TextView itemTv;
		TermIndexAdapter adapter;
		NoScrollListView itemLv;
		if (!CheckUtil.IsEmpty(indexValueLst)) {
			View itemV;
			for (KvStc item : indexValueLst) {

				// 动态指标名称
				itemV = inflater.inflate(R.layout.termindex_label, null);
				itemTv = (TextView) itemV.findViewById(R.id.termindex_tv_label);
				itemTv.setText(item.getValue());
				layout.addView(itemV);

				// 显示指标下各指标值
				for (KvStc valueItem : item.getChildLst()) {
					itemV = inflater.inflate(R.layout.termindex_lvtitle, null);
					itemTv = (TextView) itemV.findViewById(R.id.termindex_tv_title);
					// 空白 不合格 不再显示
					if (!("空白".equals(valueItem.getValue()) || "不合格".equals(valueItem.getValue()))) {
						itemTv.setText(item.getValue() + " (" + valueItem.getValue() + ")");
						adapter = new TermIndexAdapter(this, proMap.get(item.getKey() + valueItem.getKey()), IndexMap);
						itemLv = (NoScrollListView) itemV.findViewById(R.id.termindex_lv);
						itemLv.setAdapter(adapter);
						layout.addView(itemV);
					}

				}
			}
		}
		Long time2 = new Date().getTime();
		Log.e("message", "查询数据库时间2" + (time2 - time1));
	}

	@Override
	protected void onResume() {
		super.onResume();

		/*
		 * if (dialog != null) { dialog.dismiss(); }
		 */
		Long endtime = new Date().getTime();
		Log.e("message", "查询数据库时间3" + (endtime - starttime));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			DbtLog.logUtils(TAG, "返回");
			finish();
			break;

		case R.id.banner_navigation_rl_confirm:
		//case R.id.banner_navigation_bt_confirm:
			if (ViewUtil.isDoubleClick(v.getId(), 2500))
				return;
			if (hasPermission(GlobalValues.LOCAL_PERMISSION)) {
				// 拥有了此权限,那么直接执行业务逻辑
				confirmVisit();
			} else {
				// 还没有对一个权限(请求码,权限数组)这两个参数都事先定义好
				requestPermission(GlobalValues.LOCAL_CODE, GlobalValues.LOCAL_PERMISSION);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void doLocation() {
		confirmVisit();
	}

	private void  confirmVisit(){
		DbtLog.logUtils(TAG, "进入拜访");
		Intent intent = new Intent(this, ShopVisitActivity.class);
		intent.putExtra("isFirstVisit", isFirstVisit);//  是否第一次拜访  0:第一次拜访   1:重复拜访
		intent.putExtra("seeFlag", seeFlag);
		intent.putExtra("termStc", termStc);
		intent.putExtra("visitM", visitM);
		intent.putExtra("visitDate", visitDateTv.getText().toString());
		intent.putExtra("visitDay", visitDayTv.getText().toString());
		intent.putExtra("lastTime", lastTime);
		startActivity(intent);
		finish();
	}

}
