package et.tsingtaopad.visit.termtz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.termtz.TzService.onPurchaseSuccess;
import et.tsingtaopad.visit.termtz.domain.MstTzTermInfo;
import et.tsingtaopad.visit.termtz.domain.TzGridAreaInfo;
import et.tsingtaopad.visit.termtz.domain.TzTermProInfo;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TzTermActivity.java</br>
 * 作者：ywm   </br>
 * 创建时间：2015-12-4</br>      
 * 功能描述: 终端进货台账_终端列表</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("NewApi")
public class TzTermActivity extends Activity implements
		OnClickListener {

	String TAG = "TzTermActivity";

	private TextView titleTv; // 标题
	private Button backBt;// 返回
	private TextView confirmBt;// 确定(实际不用)
	
	// 查询数据库得到的终端集合
	private ArrayList<MstTzTermInfo> mList; 
	// 终端列表
	private ListView tztermLst;
	// 终端列表适配器
	private TzTermAdapter tzTermAdapter;
	//当前条目
	private int currentPosition;
	
	// 终端产品快速填写弹出框相关组件对象
    private AlertDialog quicklyDialog;
    private View itemForm;
    
    private TzService tzService;
    private ArrayList<TzTermProInfo> mProList ;
    
    private EditText tztermEt;

	private Button tztermBtn;

	private String agencykey;// 经销商主键
	private Spinner gridSp;// 定格下拉菜单
	private Spinner routeSp;// 路线下拉菜单
	
	private String selectgrid = null;// 用户所选定格 
	private String selectroute = null;// 用户所选路线
	
	// 时间控件
	private String selectDate;
	private String aday;
	private Calendar calendar;
	private int yearr;
	private int month;
	private int day;
	private String dateselect;
	private String dateselects;
	private String dateselectx;

	private ArrayList<String> routenames;//路线集合
	private String[] routes; // 线路集合
	ArrayAdapter<String> routeAdapter ;//
	
	 

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DbtLog.logUtils(TAG, "onCreateView()");
		 setContentView(R.layout.termtz_termlist);
		this.initView();
		this.initData();
	}

	/**
	 * 初始化界面组件
	 */
	private void initView() {
		DbtLog.logUtils(TAG, "initView()");
		// 绑定页面组件
		titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
		confirmBt = (TextView) findViewById(R.id.banner_navigation_bt_confirm);
		gridSp = (Spinner) findViewById(R.id.tzterm_et1_search);
		routeSp = (Spinner) findViewById(R.id.tzterm_et2_search);
		tztermEt = (EditText) findViewById(R.id.tzterm_et3_search);
		tztermBtn = (Button) findViewById(R.id.tzterm_bt2_search);
		tztermLst = (ListView) findViewById(R.id.tzterm_lv);
		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
		// 绑定事件
		backBt.setOnClickListener(this);
		//confirmBt.setOnClickListener(this);
		tztermBtn.setOnClickListener(this);
	}

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		titleTv.setText("选择终端");
		// 获取跳转过来携带的数据
		agencykey = getIntent().getStringExtra("agencykey");
				
		tzService = new TzService(getApplicationContext(), null);
		
		calendar = Calendar.getInstance();
		yearr = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		// 定格集合
		ArrayList<TzGridAreaInfo> tzGridAreaInfos = new ArrayList<TzGridAreaInfo>();
		tzGridAreaInfos = tzService.getAreaGridAll(agencykey);// 查数据库(获取区域id,name和定格id,name)
		ArrayList<String> gridNames = new ArrayList<String>(); 
		// 获取此经销商下所有定格
		for (TzGridAreaInfo tzGridAreaInfo : tzGridAreaInfos) {
			gridNames.add(tzGridAreaInfo.getAreaname()+"_"+tzGridAreaInfo.getGridname());
		}
		gridNames.add(0, "请选择");
		// 设置定格Spinner适配器
		String[] mItems = (String[])gridNames.toArray(new String[gridNames.size()]);// 生成Spinner所需字符串数组
		ArrayAdapter<String> gridAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
		gridAdapter.setDropDownViewResource(android.R.layout. simple_spinner_dropdown_item );
		gridSp.setAdapter(gridAdapter);
		// 终端集合
		mList = new ArrayList<MstTzTermInfo>();
		// 线路集合
		routenames = new ArrayList<String>();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 定格选择监听
		gridSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				selectgrid = parent.getItemAtPosition(position).toString();// 获取所选
				if("请选择".equals(selectgrid)){
					// 查询数据库得到经销商下的所有终端列表
					mList.removeAll(mList);
					mList = tzService.getTermAll(agencykey);
					tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
					tztermLst.setAdapter(tzTermAdapter);
					// 根据用户选择的定格,获取此定格下的所有线路
					routenames.removeAll(routenames);
					routenames.add(0, "请选择");
					routes = (String[])routenames.toArray(new String[routenames.size()]);
					// 设置路线sp适配
					routeAdapter = new ArrayAdapter<String>(TzTermActivity.this,android.R.layout.simple_spinner_item, routes);
					routeAdapter.setDropDownViewResource(android.R.layout. simple_spinner_dropdown_item );
					routeSp.setAdapter(routeAdapter);
				}else{
					// 根据定格名称查询终端列表
					String[] strs = selectgrid.split ("_");
					mList.removeAll(mList);
					mList =  tzService.getTermByAreaGrid(strs[0],strs[1],agencykey);
					tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
					tztermLst.setAdapter(tzTermAdapter);
					// 根据用户选择的定格,查询此定格下的所有线路
					routenames.removeAll(routenames);
					routenames = tzService.getAllRouteByAreaGrid(strs[0],strs[1],agencykey);
					routenames.add(0, "请选择");
					routes = (String[])routenames.toArray(new String[routenames.size()]);// 生成Spinner所需字符串数组
					// 设置路线sp适配
					routeAdapter = new ArrayAdapter<String>(TzTermActivity.this,android.R.layout.simple_spinner_item, routes);
					routeAdapter.setDropDownViewResource(android.R.layout. simple_spinner_dropdown_item );
					routeSp.setAdapter(routeAdapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		
		// 路线选择监听
		routeSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				selectroute = parent.getItemAtPosition(position).toString();
				if(!"请选择".equals(selectroute)){
					// 根据区域定格线路,获取经销商下终端
					String[] strs = selectgrid.split ("_");
					mList.removeAll(mList);
					mList =  tzService.getTermByAreaGridAndRoute(strs[0],strs[1],selectroute,agencykey);
					tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
					tztermLst.setAdapter(tzTermAdapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		// 终端列表条目点击监听
		tztermLst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 2秒内多次点击不予执行 防止按钮快速重复单击
				if (ViewUtil.isDoubleClick(view.getId(),2000)) return;
				
				// 改变选中条目的背景颜色
				currentPosition = position;
				tzTermAdapter.setSeletor(position);
				tzTermAdapter.notifyDataSetChanged();
				// 弹窗填写终端产品数据
				String name = mList.get(position).getTerminalname();
				String terminalkey = mList.get(position).getTerminalkey();
				qulicklyDialog(name,terminalkey);
			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// 返回
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
			finish();
			break;
		
		// 根据终端名称模糊搜索
		case R.id.tzterm_bt2_search:
			String termName = tztermEt.getText().toString().trim();
			
			/**
			 * 逻辑整理
			 * 1 用户选择了-"请选择"- a 未输入终端名称 - 查询经销商下所有终端
			 * 					b 输入了终端名称 - 查询经销商下所有与用户输入相关的终端
			 * 2 用户选择了 -"某个定格" - 已修改
			 * 						a 未输入终端名称 - 查询经销商下该定格所有终端
			 * 					   b 输入了终端名称 - 查询经销商下该定格下所有与用户输入相关的终端
			 */
			if("请选择".equals(selectgrid)){//用户选择了-"请选择"
				if(termName==null||termName.length()<=0){
					// 如果终端名称为空 查询经销商下所有终端 (不管用户是否选择了定格)
					ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
					mList.removeAll(mList);
					valueLst = tzService.getTermAll(agencykey);
					mList.addAll(valueLst);
					tzTermAdapter.notifyDataSetChanged();
				}else{
					// 模糊查询
					ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
					mList.removeAll(mList);
					valueLst = tzService.getTermsByName(termName,agencykey);
					mList.addAll(valueLst);
					tzTermAdapter.notifyDataSetChanged();
				}
			}else{//用户选择了 -"某个定格"
				if("请选择".equals(selectroute)){// 用户在路线下拉框中选择了"请选择"
					if(termName==null||termName.length()<=0){
						String[] strs = selectgrid.split ("_");
						mList.removeAll(mList);
						mList =  tzService.getTermByAreaGrid(strs[0],strs[1],agencykey);
						tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
						tztermLst.setAdapter(tzTermAdapter);
					}else{ 
						// 根据定格名称  以及终端名称 模糊查询终端列表
						String[] strs = selectgrid.split ("_");
						mList.removeAll(mList);
						mList =  tzService.getTermByAreaGridAndname(strs[0],strs[1],termName,agencykey);
						tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
						tztermLst.setAdapter(tzTermAdapter);
					}
				} else{ // 用户在路线下拉框中选择了"某条路线"
					if(termName==null||termName.length()<=0){
						// 根据区域定格线路,获取经销商下终端
						String[] strs = selectgrid.split ("_");
						mList.removeAll(mList);
						mList =  tzService.getTermByAreaGridAndRoute(strs[0],strs[1],selectroute,agencykey);
						tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
						tztermLst.setAdapter(tzTermAdapter);
					}else{
						// 根据区域定格线路  以及终端名称 模糊查询终端列表
						String[] strs = selectgrid.split ("_");
						mList.removeAll(mList);
						mList =  tzService.getTermByAreaGridAndnameAndRoutename(strs[0],strs[1],termName,selectroute,agencykey);
						tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
						tztermLst.setAdapter(tzTermAdapter);
					}
				}
			}
			
			break;

		default:
			break;
		}
	}
	
	/**
     * 终端产品填写 弹出窗口
     */
    public void qulicklyDialog(String termName,final String terminalkey) {
        DbtLog.logUtils(TAG, "qulicklyDialog()");
        
        /*final int yearr1;
        final int month1;
        final int day1;*/
        
        // 如果弹出框已弹出，则不再弹出
        if (quicklyDialog != null && quicklyDialog.isShowing()) return;
        
        // 加载弹出窗口layout
        itemForm = this.getLayoutInflater()
                    .inflate(R.layout.tzterm_inproductdialog,null);
        quicklyDialog = new AlertDialog.Builder(
                        this).setCancelable(false).create();
        quicklyDialog.setView(itemForm, 0, 0, 0, 0);
        quicklyDialog.show();
        // 设置弹窗大小
        Window dialogWindow = quicklyDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height=600;
        dialogWindow.setAttributes(lp);
        // 初始化
        TextView title = (TextView)itemForm.findViewById(R.id.unit_title);
        title.setText(termName);
        
		// 产品列表
        final LinearLayout proDialogLl = (LinearLayout)itemForm.findViewById(R.id.quicklydialog_lv);
        final Button purchasetime = (Button)itemForm.findViewById(R.id.term_bt_purchasetime);
        
        // 终端下上传成功的产品
        ArrayList<TzTermProInfo> mProyesList= new ArrayList<TzTermProInfo>();
        mProyesList = new TzService(getApplicationContext(), null).getTermProByUpyes(terminalkey,agencykey,"0");
        if(mProyesList.size()!=0){
        	if(((TzTermProInfo)mProyesList.get(0)).getPurchasetime()==null&&((TzTermProInfo)mProyesList.get(0)).getPurchasetime().length()<=0){
        		// 设置用户时间选择
                purchasetime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime()));
        	}else{
        		// 设置用户时间选择
                purchasetime.setText(mProyesList.get(0).getPurchasetime());
        	}
        }else{
        	// 设置用户时间选择
            purchasetime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime()));
        }
       
        // 选择开发时间
        purchasetime.setOnClickListener(new OnClickListener() {

 			@Override
 			public void onClick(View v) {
 				
 				
 				
 				// 初始化弹框显示日期
 				String selecttime = (String)purchasetime.getText();
 				if(selecttime.contains("-")){
 					String[] time = selecttime.split("-");
 	 				yearr = Integer.parseInt(time[0]);
 	 				month = Integer.parseInt(time[1])-1;
 	 				day = Integer.parseInt(time[2]);
 				}
 				// 设置日期弹框  
 				DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(),
 						new DatePickerDialog.OnDateSetListener() {
 							@Override
 							public void onDateSet(DatePicker view, int year,
 									int monthOfYear, int dayOfMonth) {
 								calendar.set(year, monthOfYear, dayOfMonth);
 								yearr = year;
 								month = monthOfYear;
 								day = dayOfMonth;
 								if (dayOfMonth < 10) {
 									aday = "0" + dayOfMonth;
 								} else {
 									aday = Integer.toString(dayOfMonth);
 								}
 								
 								selectDate = (Integer.toString(year) + "-"+ String.format("%02d", monthOfYear + 1) + "-" + aday);
 								purchasetime.setText(selectDate);
 								Toast.makeText(getApplicationContext(), "您选择的日期是 "+selectDate, Toast.LENGTH_SHORT).show();
 							}
 						}, yearr, month, day);
 				
 				/*
 				DatePicker datePicker = dateDialog.getDatePicker();  
 	            datePicker.setMinDate(DateUtil.parse(DateUtil.formatDate(new Date(),"yyyy-MM-dd "), "yyyy-MM-dd").getTime()-(14*24*60*60*1000L));  
 	            datePicker.setMinDate(new Date().getTime()-(14*24*60*60*1000L));  
 	            if(DateUtil.parse (selecttime , "yyyy-MM-dd" ).getTime() > new Date().getTime()){
 	            	datePicker.setMaxDate(DateUtil.parse (selecttime , "yyyy-MM-dd" ).getTime());
 	            }else{
 	            	datePicker.setMaxDate(new Date().getTime());
 	            }
 	            //datePicker.setMaxDate(new Date().getTime());
 	            //datePicker.setMaxDate(DateUtil.parse(DateUtil.formatDate(new Date(),"yyyy-MM-dd "), "yyyy-MM-dd").getTime()+(1*24*60*60*1000L)-(1*24*60*60*1000L));
 	            //datePicker.setMaxDate(DateUtil.parse (selecttime , "yyyy-MM-dd" ).getTime());
 	            
 	             */
 				/*
 				Date date = new Date();//当前时间
 				long time111 = date.getTime();
 				//dateDialog.getDatePicker().setMaxDate(new Date().getTime()+(1*24*60*60*1000L)-(1*24*60*60*1000L));
 				//dateDialog.getDatePicker().setMinDate(DateUtil.parse(DateUtil.formatDate(new Date(),"yyyy-MM-dd "), "yyyy-MM-dd").getTime()-(14*24*60*60*1000L));  
 				//dateDialog.getDatePicker().setMinDate(System.currentTimeMillis()-(1*24*60*60*1000));
 				//dateDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
 				// 2017-01-09 
 				String Maxtime = DateUtil.formatDate(date,"yyyy-MM-dd ");
 				
 				//String Mintime = "2017-01-09 01:01";
 				String yuefen = Maxtime.substring(5, 7);
 				// 获取当月第一天的时间long型 2017-01-01
 				String fristday = Maxtime.substring(0, 7)+"-01 11:01";
 				Date firstdate = DateUtil.parse(fristday);
 				long time = firstdate.getTime();
 				
 				// 根据月份,设置截取日期
 				if("01".equals(yuefen)){// 1月 截取时间段:12月31日~当天 ()
 					dateDialog.getDatePicker().setMinDate(time);
 					
 					String shierday = "2016-12-31";
 	 				Date shierdate = DateUtil.parse(shierday);
 	 				long shiertime = shierdate.getTime();//+(1*24*60*60*1000L)
 					Mintime = new SimpleDateFormat("yyyy-MM-dd").format(shiertime);
 					
 					//Mintime = new SimpleDateFormat("yyyy-MM-dd").format(time-(1*24*60*60*1000L));
 					//Mintime = new SimpleDateFormat("yyyy-MM-dd").format(time);
 					//dateDialog.getDatePicker().setMinDate(DateUtil.parse (Mintime , "yyyy-MM-dd" ).getTime());
 					//dateDialog.getDatePicker().setMinDate(time);
 					//dateDialog.getDatePicker().setMinDate(DateUtil.parse ("2017-01-05" , "yyyy-MM-dd" ).getTime());
 				}else if("03".equals(yuefen)){// 3月 截取时间段:2月26日~当天
 					String eryueday = Maxtime.substring(0, 6)+"2-27 11:01";
 					Date eryuedate = DateUtil.parse(eryueday);
 	 				long eryuetime = eryuedate.getTime();
 					//long sdf= time-(2*24*60*60*1000L);
 					dateDialog.getDatePicker().setMinDate(eryuetime);
 					//long sdf= time111-(15*24*60*60*1000L)+ DateUtils.MILLIS_IN_DAY;
 					//long sdf= calendar.getTimeInMillis()-(12*24*60*60*1000);
 					//long sdf= time-(2*24*60*60*1000L);
 					//long sdf1= calendar.getTimeInMillis()-1000;
 					//SimpleDateFormat sdff= new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
 					//SimpleDateFormat sdff= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
 			        //String timeStr=sdff.format(sdf);
 					//Mintime = new SimpleDateFormat("yyyy-MM-dd").format(sdf);
 					//dateDialog.getDatePicker().setMinDate(DateUtil.parse (timeStr , "yyyy-MM-dd" ).getTime());
 					//dateDialog.getDatePicker().setMinDate(sdf);
 				}else if("05".equals(yuefen)||"07".equals(yuefen)||"08".equals(yuefen)||"10".equals(yuefen)||"12".equals(yuefen)){// 其他da月份 截取时间段:上月28日~当天
 					long sdf= time-(2*24*60*60*1000L);
 					dateDialog.getDatePicker().setMinDate(sdf);
 					Mintime = new SimpleDateFormat("yyyy-MM-dd").format(time-(4*24*60*60*1000L));
 					dateDialog.getDatePicker().setMinDate(DateUtil.parse (Mintime , "yyyy-MM-dd" ).getTime());
 				}else if("02".equals(yuefen)||"04".equals(yuefen)||"06".equals(yuefen)||"09".equals(yuefen)||"11".equals(yuefen)){// 其他xiao月份 截取时间段:上月28日~当天
 					long sdf= time-(3*24*60*60*1000L);
 					dateDialog.getDatePicker().setMinDate(sdf);
 					Mintime = new SimpleDateFormat("yyyy-MM-dd").format(time-(3*24*60*60*1000L));
 					dateDialog.getDatePicker().setMinDate(DateUtil.parse (Mintime , "yyyy-MM-dd" ).getTime());
 				}else{
 					long sdf= time-(3*24*60*60*1000L);
 					dateDialog.getDatePicker().setMinDate(sdf);
 					Mintime = new SimpleDateFormat("yyyy-MM-dd").format(time-(4*24*60*60*1000L));
 					dateDialog.getDatePicker().setMinDate(DateUtil.parse (Mintime , "yyyy-MM-dd" ).getTime());
 				}
 				//String Maxtime = new SimpleDateFormat("yyyy-MM-dd").format(time111-(1*24*60*60*1000L));
 				//long mintime = new Date().getTime()-(13*24*60*60*1000L);
 				
 				//Mintime = new SimpleDateFormat("yyyy-MM-dd").format(time111-(15*24*60*60*1000L));
 				 
 				//dateDialog.getDatePicker().setMinDate();
 				dateDialog.getDatePicker().setMinDate(DateUtil.parse ("2016-02-14" , "yyyy-MM-dd" ).getTime());
 				dateDialog.getDatePicker().setMaxDate(DateUtil.parse ("2016-02-28" , "yyyy-MM-dd" ).getTime());
 				
 				//dateDialog.getDatePicker().setMinDate(DateUtil.parse (Mintime , "yyyy-MM-dd" ).getTime());
 				dateDialog.getDatePicker().setMaxDate(DateUtil.parse (Maxtime , "yyyy-MM-dd" ).getTime());
 				//dateDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
 				dateDialog.getDatePicker().setCalendarViewShown(false);
 				//dateDialog.getDatePicker().setMinDate(new Date().getTime()-(14*24*60*60*1000L));
 				//if (!dateDialog.isShowing()) {
 				dateDialog.show();
 				//dateDialog.getDatePicker().setCalendarViewShown(true);
 				//}
 				 */ 		
 				Date date = new Date();//当前时间
 				long time111 = date.getTime();
 				//dateDialog.getDatePicker().setMaxDate(new Date().getTime()+(1*24*60*60*1000L)-(1*24*60*60*1000L));
 				//dateDialog.getDatePicker().setMinDate(DateUtil.parse(DateUtil.formatDate(new Date(),"yyyy-MM-dd "), "yyyy-MM-dd").getTime()-(14*24*60*60*1000L));  
 				//dateDialog.getDatePicker().setMinDate(System.currentTimeMillis()-(1*24*60*60*1000));
 				//dateDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
 				
 				String Maxtime = DateUtil.formatDate(date,"yyyy-MM-dd ");
 				//String Maxtime = new SimpleDateFormat("yyyy-MM-dd").format(time111-(1*24*60*60*1000L));
 				//long mintime = new Date().getTime()-(13*24*60*60*1000L);
 				String Mintime = new SimpleDateFormat("yyyy-MM-dd").format(time111-(15*24*60*60*1000L));
 				
 				//dateDialog.getDatePicker().setMinDate();
 				/*dateDialog.getDatePicker().setMinDate(DateUtil.parse ("2016-02-14" , "yyyy-MM-dd" ).getTime());
 				dateDialog.getDatePicker().setMaxDate(DateUtil.parse ("2016-02-28" , "yyyy-MM-dd" ).getTime());*/
 				
 				dateDialog.getDatePicker().setMinDate(DateUtil.parse (Mintime , "yyyy-MM-dd" ).getTime());
 				dateDialog.getDatePicker().setMaxDate(DateUtil.parse (Maxtime , "yyyy-MM-dd" ).getTime());
 				
 				//dateDialog.getDatePicker().setMinDate(new Date().getTime()-(14*24*60*60*1000L));
 				//if (!dateDialog.isShowing()) {
 					dateDialog.getDatePicker().setCalendarViewShown(false);
 					dateDialog.show();
 				//}
 			}
 		});
        
        // 查询该终端下的产品
        mProList= new ArrayList<TzTermProInfo>();
		mProList = new TzService(getApplicationContext(), null).getTermProAll(terminalkey,agencykey);
		for(int i=0; i<mProList.size();i++){
            View view = View.inflate(this,R.layout.termtz_dialog_lvitem,null);
            
            TextView proNameTv = (TextView) view
    				.findViewById(R.id.quicklydialog_tv_proname);
    		EditText changeNumEt = (EditText) view
    				.findViewById(R.id.quicklydialog_et_changenum);
            
    		TzTermProInfo item = mProList.get(i);
            
    		// 设置初始值
    		proNameTv.setText(item.getProname());
    		if("0".equals(String.valueOf(item.getPurchase()))){// 
    			changeNumEt.setHint(String.valueOf(item.getPurchase()));
    		}else{
    			changeNumEt.setText(String.valueOf(item.getPurchase()));
    		}
    		proDialogLl.addView(view);
        }

        // 确定  
        Button sureBt = (Button)itemForm.findViewById(R.id.quicklydialog_bt_sure);
        sureBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	if (ViewUtil.isDoubleClick(arg0.getId(), 2500)) return;
            	// 如果网络不可用
        		if (!NetStatusUtil.isNetValid(TzTermActivity.this)){
        			Toast.makeText(TzTermActivity.this, "网络异常,请先连接网络", Toast.LENGTH_SHORT).show();
        			return;
        		}
            	// 查询数据库 上传数据
            	EditText itemEt;
                for (int i = 0; i < mProList.size(); i++) {

                	TzTermProInfo tztermproinfo = mProList.get(i);
                	// 获取文本框的值
                	itemEt = (EditText)proDialogLl.getChildAt(i).findViewById(R.id.quicklydialog_et_changenum);
                	// 更改该条记录数据(进货量)
                	tzService.toChangePurchase(FunUtil
                            .isNullToZero(itemEt.getText().toString()), tztermproinfo.getAgencykey(), terminalkey, tztermproinfo.getProductkey());
                	// 更改该条记录数据(padisconsistent = 0 可上传)
                	String af = itemEt.getText().toString();
                	//if(!"".equals(itemEt.getText().toString())){
                		tzService.toChangePadisconsi("0",tztermproinfo.getAgencykey(), terminalkey, tztermproinfo.getProductkey());
                		// 修改该产品的上报时间
                		tzService.toChangePurchasetime((String)purchasetime.getText(),tztermproinfo.getAgencykey(), terminalkey, tztermproinfo.getProductkey());
                	//}
                }
                // 上传终端进货台账表MST_TERMLEDGER_INFO中所有 padisconsistent = 0 的记录
                tzService.upTermData("0",quicklyDialog,terminalkey,agencykey,new onPurchaseSuccess(){

					@Override
					public void setTerminalTextColor() {
						refreshAdapter();
						//tzTermAdapter.notifyDataSetChanged();
					}
                	
                });
                
                
                // 弹框消失
                //quicklyDialog.cancel();
            }
        });
        
        // 取消
        Button cancelBt = (Button)itemForm.findViewById(R.id.quicklydialog_bt_cancel);
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.hideSoftInputFromWindow(TzTermActivity.this,v);
                quicklyDialog.cancel();
            }
        });
    }
    
   public void refreshAdapter(){
	   String termName = tztermEt.getText().toString().trim();
		
		/**
		 * 逻辑整理
		 * 1 用户选择了-"请选择"- a 未输入终端名称 - 查询经销商下所有终端
		 * 					b 输入了终端名称 - 查询经销商下所有与用户输入相关的终端
		 * 2 用户选择了 -"某个定格" - 已修改
		 * 						a 未输入终端名称 - 查询经销商下该定格所有终端
		 * 					   b 输入了终端名称 - 查询经销商下该定格下所有与用户输入相关的终端
		 */
		if("请选择".equals(selectgrid)){//用户选择了-"请选择"
			if(termName==null||termName.length()<=0){
				// 如果终端名称为空 查询经销商下所有终端 (不管用户是否选择了定格)
				ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
				mList.removeAll(mList);
				valueLst = tzService.getTermAll(agencykey);
				mList.addAll(valueLst);
				tzTermAdapter.notifyDataSetChanged();
			}else{
				// 模糊查询
				ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
				mList.removeAll(mList);
				valueLst = tzService.getTermsByName(termName,agencykey);
				mList.addAll(valueLst);
				tzTermAdapter.notifyDataSetChanged();
			}
		}else{//用户选择了 -"某个定格"
			if("请选择".equals(selectroute)){// 用户在路线下拉框中选择了"请选择"
				if(termName==null||termName.length()<=0){ // termName.length()<=0
					String[] strs = selectgrid.split ("_");
					ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
					mList.removeAll(mList);
					valueLst =  tzService.getTermByAreaGrid(strs[0],strs[1],agencykey);
					mList.addAll(valueLst);
					//tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
					//tztermLst.setAdapter(tzTermAdapter);
					tzTermAdapter.notifyDataSetChanged();
				}else{ 
					
					ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
					// 根据定格名称  以及终端名称 模糊查询终端列表
					String[] strs = selectgrid.split ("_");
					mList.removeAll(mList);
					valueLst =  tzService.getTermByAreaGridAndname(strs[0],strs[1],termName,agencykey);
					mList.addAll(valueLst);
					//tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
					//tztermLst.setAdapter(tzTermAdapter);
					tzTermAdapter.notifyDataSetChanged();
				}
			} else{ // 用户在路线下拉框中选择了"某条路线"
				if(termName==null||termName.length()<=0){
					ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
					// 根据区域定格线路,获取经销商下终端
					String[] strs = selectgrid.split ("_");
					mList.removeAll(mList);
					valueLst =  tzService.getTermByAreaGridAndRoute(strs[0],strs[1],selectroute,agencykey);
					mList.addAll(valueLst);
					//tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
					//tztermLst.setAdapter(tzTermAdapter);
					tzTermAdapter.notifyDataSetChanged();
				}else{
					ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
					// 根据区域定格线路  以及终端名称 模糊查询终端列表
					String[] strs = selectgrid.split ("_");
					mList.removeAll(mList);
					valueLst =  tzService.getTermByAreaGridAndnameAndRoutename(strs[0],strs[1],termName,selectroute,agencykey);
					mList.addAll(valueLst);
					//tzTermAdapter = new TzTermAdapter(TzTermActivity.this, mList);
					//tztermLst.setAdapter(tzTermAdapter);
					tzTermAdapter.notifyDataSetChanged();
				}
			}
		}
   }
    
}
