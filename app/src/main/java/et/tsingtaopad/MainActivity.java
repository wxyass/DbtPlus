package et.tsingtaopad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.home.HomeFragment;
import et.tsingtaopad.initconstvalues.InitConstValues;
import et.tsingtaopad.msgpush.MQConnection;
import et.tsingtaopad.operation.OperationFragment1;
import et.tsingtaopad.syssetting.SystemSettingFragment1;
import et.tsingtaopad.syssetting.version.VersionService;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.visit.VisitFragment1;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.syncdata.MainService;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：MainActivity.java</br> 
 * 作者：@吴承磊 </br>
 * 创建时间：2013/11/26</br> 
 * 功能描述: 平台主界面</br>  
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
 
@SuppressLint("NewApi")
public class MainActivity extends BaseActivity {
  
	private FragmentTabHost tabHost; 
  
	private LayoutInflater layoutInflater;
	private TextView visitTermNumTv;
	private TextView planTermNumTv;
	private ProgressBar planPg;
	private TitleLayout titleLayout;
	@SuppressWarnings("rawtypes")
	private Class fragmentArray[] = { HomeFragment.class, VisitFragment1.class, OperationFragment1.class, SystemSettingFragment1.class };

	private int imageViewArray[] = { R.drawable.bt_frist_page, R.drawable.bt_platform_visit, R.drawable.bt_platform_operation,  R.drawable.bt_platform_syseting };

	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();

			super.handleMessage(msg);
			switch (msg.what) {

			// 提示信息
			case ConstValues.WAIT1:
				Toast.makeText(getApplicationContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.platform_main);
		initView();
		initMQ();
		//登录后的处理操作
		this.dealOther();
	}

	@Override
	//设置标题头相关信息
	protected void onResume() {
		super.onResume();
		ShopVisitService shopVisitService = new ShopVisitService(this, null);
		//获取当天结束且有效的拜访的终端 个数 
		int visitTermNum = shopVisitService.queryVisitTermNum();
		visitTermNumTv.setText(String.valueOf(visitTermNum));
		//获取当天计划拜访终端个数
		int planTermNum = shopVisitService.queryPlanTermNum();
		planTermNumTv.setText(String.valueOf(planTermNum));
		//设置进度条
		if (planTermNum == 0) {
			planPg.setProgress(100);
		} else {
			planPg.setProgress((int)((float)visitTermNum / planTermNum * 100));
		}
	}

	private void initView() {

		visitTermNumTv = (TextView) findViewById(R.id.banner_tv_visitnum);
		planTermNumTv = (TextView) findViewById(R.id.banner_tv_plannum);
		planPg = (ProgressBar) findViewById(R.id.banner_plan_pb);
		titleLayout = (TitleLayout) findViewById(R.id.main_title);

		// 获取tabIndex
		int tabIndex = 0;
		Bundle bundle = getIntent().getBundleExtra("mainArgs");
		if (bundle != null) {
			tabIndex = bundle.getInt("tabIndex", 0);
		}

		// 初始化Tab
		layoutInflater = LayoutInflater.from(this);
		tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		for (int i = 0; i < fragmentArray.length; i++) {
			TabSpec tabSpec = tabHost.newTabSpec(String.valueOf(i)).setIndicator(getTabItemView(i));
			tabHost.addTab(tabSpec, fragmentArray[i], bundle);
		}

		// 定位Tab
		tabHost.setCurrentTab(tabIndex);

		// 初始化消息handler
		ConstValues.msgHandler = this.handler;
	}

	/**
	 * 注册MQ服务
	 */
	private void initMQ() {

		/********* 持久化订阅mq***start *********/
		// 需要订阅的主题数组（以用户ID和所属区域ID为主题名）
		//        String[] topics = { ConstValues.loginSession.getUserCode(), ConstValues.loginSession.getParentDisIDs().substring(1, ConstValues.loginSession.getParentDisIDs().length()-1),
		//                // ConstValues.loginSession.getDisId() + "_public",
		//                ConstValues.loginSession.getDisId() + "_private", ConstValues.loginSession.getDisId() + "_public" };

		// 唯一连接编号，这里用用户编号在loginsession中取得
		//String clientId = ConstValues.loginSession.getUserGongHao();
		String clientId = PrefUtils.getString(getApplicationContext(), "userGongHao", "");
		// 启动子线程调用连接方法
		new MQConnection(this, clientId, getTopics()).start();
		Log.d("tag", "notification-->" + getIntent().getAction());
		/********* 持久化订阅mq***end *********/

	}

	private String[] getTopics() {
	    String[] result = new String[3];
	    String[] temp = new String[0];
	    //String pDiscs = PrefUtils.getString(getApplicationContext(), "pDiscs", "");
	    //if (!CheckUtil.isBlankOrNull(ConstValues.loginSession.getParentDisIDs())) {
	    if (!CheckUtil.isBlankOrNull(PrefUtils.getString(getApplicationContext(), "pDiscs", ""))) {
    		//temp = ConstValues.loginSession.getParentDisIDs().split(",");
    		temp = PrefUtils.getString(getApplicationContext(), "pDiscs", "").split(",");
    		result = new String[temp.length + 3];
    		for (int i = 0; i < temp.length; i++) {
    			result[i] = temp[i].substring(1, temp[i].length() - 1);
    		}
		}
        //result[temp.length] = ConstValues.loginSession.getUserCode();
        result[temp.length] = PrefUtils.getString(getApplicationContext(), "userCode", "");
        //result[temp.length + 1] = ConstValues.loginSession.getDisId() + "_private";
        result[temp.length + 1] = PrefUtils.getString(getApplicationContext(), "disId", "") + "_private";
        //result[temp.length + 2] = ConstValues.loginSession.getDisId() + "_public";
        result[temp.length + 2] = PrefUtils.getString(getApplicationContext(), "disId", "") + "_public";
		return result;
	}
	
	/**
	 * 登录后的处理操作
	 */
	private void dealOther() {

        // 初始化静态变量
        new InitConstValues(getApplicationContext()).start();
	    
        //启动服务
        Intent service = new Intent(this, MainService.class);
        startService(service);
        
        // 检查版本
        VersionService versionService = new VersionService(this, false, false);
        versionService.checkVersion();
	}

	/**
	 * 获取每一个Tab标签显示内容
	 * 
	 * @param index
	 * @return
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.platform_tab_item, null);
		//android:background="@drawable/bg_bottom_main"
		//view.setBackgroundResource(R.drawable.bg_main_bottom_navs);// 底部按钮背景图
		view.setBackgroundResource(R.drawable.bg_bottom_main);// 底部按钮背景图
		//view.setBackgroundResource(R.color.bg_main_bottom);// 底部按钮背景图
		ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
		imageView.setImageResource(imageViewArray[index]);
		return view;
	}

	@Override
	public void onBackPressed() {
		final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
		if (fragment != null && fragment instanceof BaseFragmentSupport && !((BaseFragmentSupport) fragment).allowBackPressed()) {
			Button banner_exit_bt = titleLayout.getBanner_exit_bt();
			banner_exit_bt.performClick();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent service = new Intent(this, MainService.class);
		stopService(service);
	}
}
