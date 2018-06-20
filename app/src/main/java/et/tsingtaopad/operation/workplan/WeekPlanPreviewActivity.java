package et.tsingtaopad.operation.workplan;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstPlanWeekforuserM;
import et.tsingtaopad.db.tables.PadPlantempcheckM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br> 文件名：PlanPreviewActivity.java</br> 作者：wangshiming </br>
 * 创建时间：2014年3月6日</br> 功能描述: 周工作计划预览</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
//周工作计划预览
public class WeekPlanPreviewActivity extends BaseActivity implements
		OnClickListener {
	private final static String TAG = "WeekPlanPreviewActivity";
	private LinearLayout parentContentLayout;
	private ProgressDialog pd;
	private DatabaseHelper helper;
	private List<PadPlantempcheckM> plantempcheckMs;
	private Dao<PadPlantempcheckM, String> padPlantempcheckMDao;
	private String startDate;
	private String endDate;
	private String date;
	private Context context;
	private LayoutInflater inflater;
	private TextView preview_plan_no_data_promotion;
	WebView webView;
	TextView subBtn;
	MstPlanWeekforuserM weekPlan;
	EditText remarksView;
	int oprateState;// 0:预览，1：制定，2：修改

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.operation_preview_makeplan);
		context = this;
		date = getIntent().getStringExtra("date");
		startDate = getIntent().getStringExtra("startDate");
		endDate = getIntent().getStringExtra("endDate");
		oprateState = getIntent().getIntExtra("oprateState", 0);
		weekPlan = (MstPlanWeekforuserM) getIntent().getSerializableExtra(
				"weekPlan");

		initView();

		pd = new ProgressDialog(this);
		pd.setMessage("载入数据中，请等待");
		// pd.show();
		helper = DatabaseHelper.getHelper(this);

		showWebView();
		initValues();

	}

	private void initView() {
		findViewById(R.id.banner_navigation_bt_back).setOnClickListener(this);
		subBtn = (TextView) findViewById(R.id.banner_navigation_bt_confirm);
		remarksView = (EditText) findViewById(R.id.remarks);
		TextView titleTV = (TextView) findViewById(R.id.banner_navigation_tv_title);
		TextView makeplan_tv_time = (TextView) findViewById(R.id.makeplan_tv_time);
		preview_plan_no_data_promotion = (TextView) findViewById(R.id.preview_plan_no_data_promotion);
		titleTV.setText(getString(R.string.work_plan_week_preview));
		makeplan_tv_time.setText(date);
		parentContentLayout = (LinearLayout) findViewById(R.id.work_plan_week_preview_contentllayout);
		inflater = (LayoutInflater) this
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		webView = (WebView) findViewById(R.id.webview);
		if (oprateState == 1 || oprateState == 2) {
			subBtn.setVisibility(View.VISIBLE);
			//subBtn.setOnClickListener(this);
		} else {
			remarksView.setEnabled(false);
		}
		remarksView.setText(weekPlan.getRemarks());
	}

	// 运用管理模块工作计划-->本周计划完成目标汇总预览
	private void showWebView() {
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(PropertiesUtil.getProperties("platform_web"));
		urlBuffer
				.append("bs/business/forms/BusinessForms!planWeekQueryInitPad.do");
		//
		urlBuffer.append("?model.businessFormsStc.startdate=")
				.append(startDate);
		urlBuffer.append("&model.businessFormsStc.enddate=").append(endDate);
		urlBuffer.append("&model.businessFormsStc.gridId=").append(
				//ConstValues.loginSession.getGridId());
				PrefUtils.getString(context, "gridId", ""));
		urlBuffer.append("&model.businessFormsStc.userId=").append(
				//ConstValues.loginSession.getUserCode());
				PrefUtils.getString(context, "userCode", ""));
		webView.loadUrl(urlBuffer.toString());
	}

	/**
     * 
     */
	private void initValues() {
		try {
			padPlantempcheckMDao = helper.getPadPlantempcheckMDao();
			// 查指标
			plantempcheckMs = padPlantempcheckMDao.queryForAll();
			Map<String, Map<String, Set<List<String>>>> checkKeyMap = queryCheckNum(
					startDate, endDate);
			if (!CheckUtil.IsEmpty(plantempcheckMs)) {
				preview_plan_no_data_promotion.setVisibility(View.GONE);
				for (int i = 0; i < plantempcheckMs.size(); i++) {
					PadPlantempcheckM plantempcheckM = plantempcheckMs.get(i);
					String checkname = plantempcheckM.getCheckname();
					String checkkey = plantempcheckM.getCheckkey();
					Map<String, Set<List<String>>> colitemKeyMap = checkKeyMap
							.get(checkkey);
					LinearLayout childContentLayout = (LinearLayout) inflater
							.inflate(R.layout.weekpreviewtitle, null);
					TextView titleTextView = (TextView) childContentLayout
							.findViewById(R.id.weekPreview_checkName);
					LinearLayout contentViewLayout = (LinearLayout) childContentLayout
							.findViewById(R.id.contentview);
					titleTextView.setText(checkname);
					parentContentLayout.addView(childContentLayout);
					// 显示终端数量
					if (colitemKeyMap == null) {
						LinearLayout conntentView = (LinearLayout) inflater
								.inflate(R.layout.plan_preview_content, null);
						TextView titleTV = (TextView) conntentView
								.findViewById(R.id.preview_title);
						titleTV.setText("当前指标无数据");
						contentViewLayout.addView(conntentView);
					} else {
						Iterator<Entry<String, Set<List<String>>>> iterator = colitemKeyMap
								.entrySet().iterator();
						while (iterator.hasNext()) {
							Entry<String, Set<List<String>>> next = iterator
									.next();
							String key = next.getKey();
							Set<List<String>> value = next.getValue();

							LinearLayout conntentView = (LinearLayout) inflater
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
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, Map<String, Set<List<String>>>> queryCheckNum(
			String startDate, String endDate) {
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append("select pc.checkkey, cm.checkname,pi.colitemkey itemkey,");
		stringBuilder.append("case pi.plantype ");
		stringBuilder.append("when '0' then ci.colitemname ");
		stringBuilder.append("when '1' then p.proname ");
		stringBuilder.append("when '2' then pm.promotname ");
		stringBuilder.append("when '3' then p.proname ");
		stringBuilder.append("when '4' then p.proname ");
		stringBuilder.append("else '' end itemname,pi.remarks ");
		stringBuilder.append("from mst_planforuser_m pu ");
		stringBuilder
				.append("inner join mst_plancheck_info pc on pu.plankey = pc.PLANKEY ");
		stringBuilder
				.append("inner join mst_plancollection_info pi on pc.pcheckkey = pi.pcheckkey and pi.deleteflag!='1' ");
		stringBuilder
				.append("inner join pad_plantempcheck_m cm on pc.checkkey = cm.checkkey ");
		stringBuilder
				.append("left join pad_plantempcollection_info ci on pi.colitemkey = ci.colitemkey and pi.plantype ='0' ");
		stringBuilder
				.append("left join mst_promotions_m pm on pm.promotkey = pi.colitemkey and pi.plantype = '2' ");
		stringBuilder
				.append("left join mst_product_m p on p.productkey = pi.colitemkey and pi.plantype in ('1','3','4') ");
		stringBuilder.append("where pu.plandate between '");
		stringBuilder.append(startDate).append("' and '").append(endDate)
				.append("' ").append("order by pi.checkkey, pi.colitemkey");
		Map<String, Map<String, Set<List<String>>>> checkKeyMap = new HashMap<String, Map<String, Set<List<String>>>>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(stringBuilder.toString(), null);
			while (cursor.moveToNext()) {
				String checkkey = cursor.getString(0);// 指标主键
				// String colitemkey = cursor.getString(2);//采集项主键
				String itemName = cursor.getString(3);// 采集项主键
				String remarks = cursor.getString(4);// 存储的终端List<String>
				Map<String, Set<List<String>>> itemkeyMap = checkKeyMap
						.get(checkkey);

				if (itemkeyMap == null) {
					itemkeyMap = new HashMap<String, Set<List<String>>>();
					checkKeyMap.put(checkkey, itemkeyMap);
				}
				Set hashSet = itemkeyMap.get(itemName);
				if (hashSet == null) {
					hashSet = new HashSet<List<String>>();
					itemkeyMap.put(itemName, hashSet);
				}
				List<String> terminalKeys = JsonUtil.parseList(remarks,
						String.class);
				if (terminalKeys != null) {
					hashSet.addAll(terminalKeys);
				}
				itemkeyMap.put(itemName, hashSet);
				checkKeyMap.put(checkkey, itemkeyMap);
			}

		}
		return checkKeyMap;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.banner_navigation_bt_back:
			finish();
			break;
		case R.id.banner_navigation_bt_confirm:
			String remarks = remarksView.getText().toString();
			//weekPlan.setUserid(ConstValues.loginSession.getUserCode());
			weekPlan.setUserid(PrefUtils.getString(context, "userCode", ""));
			weekPlan.setStartdate(startDate);
			weekPlan.setEnddate(endDate);
			weekPlan.setPlanstatus("0");
			weekPlan.setRemarks(remarks);
			weekPlan.setPadisconsistent(ConstValues.FLAG_0);
			weekPlan.setUpdatetime(new Date());
			//weekPlan.setUpdateuser(ConstValues.loginSession.getUserCode());
			weekPlan.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
			//weekPlan.setGridkey(ConstValues.loginSession.getGridId());
			weekPlan.setGridkey(PrefUtils.getString(context, "gridId", ""));
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
			finish();
			break;

		default:
			break;
		}
	}
}
