package et.tsingtaopad.visit.shopvisit;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import cn.com.benyoyo.manage.Struct.ResponseStructBean;
import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstGroupproductM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.db.tables.MstVistproductInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FileUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.memorybook.MemoDialogFragment;
import et.tsingtaopad.visit.shopvisit.camera.CameraFragment;
import et.tsingtaopad.visit.shopvisit.camera.CameraService;
import et.tsingtaopad.visit.shopvisit.camera.domain.CameraDataStc;
import et.tsingtaopad.visit.shopvisit.camera.domain.PictypeDataStc;
import et.tsingtaopad.visit.shopvisit.chatvie.ChatVieFragment;
import et.tsingtaopad.visit.shopvisit.checkindex.CheckIndexFragment;
import et.tsingtaopad.visit.shopvisit.checkindex.CheckIndexService;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;
import et.tsingtaopad.visit.shopvisit.domain.AddressStc;
import et.tsingtaopad.visit.shopvisit.invoicing.InvoicingFragment;
import et.tsingtaopad.visit.shopvisit.invoicing.InvoicingService;
import et.tsingtaopad.visit.shopvisit.invoicing.domain.InvoicingStc;
import et.tsingtaopad.visit.shopvisit.sayhi.SayHiFragment;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.visit.shopvisit.termindex.TermIndexService;
import et.tsingtaopad.visit.shopvisit.termindex.domain.TermIndexStc;
import et.tsingtaopad.visit.syncdata.UploadDataService;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：ShopVisitActivity.java</br> 作者：吴承磊 </br>
 * 创建时间：2013-12-3</br> 功能描述: 巡店拜访主界面</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
// 巡店拜访主界面
// @SuppressLint("NewApi")
public class ShopVisitActivity extends BaseActivity implements OnClickListener,
        OnTabChangeListener, OnTouchListener {

    private final String TAG = "ShopVisitActivity";

    private ShopVisitService service;
    private InvoicingService invoicingservice;

    private String visitId;
    private int screenWidth, screenHeight;
    private int lastX, lastY;
    private float visitMemoIvXY;
    private String seeFlag;// 1:查看 ,0:拜访
    private String isFirstVisit;//  是否第一次拜访  0:第一次拜访   1:重复拜访
    private MstTermListMStc termStc;

    private AlertDialog dialog;
    private TextView titleTv;
    private Button backBt;
    private TextView sureBt;

    private TextView visitDateTv;
    private TextView visitDayTv;
    private ImageView visitMemoIv;

    private FragmentTabHost tabHost;

    private LayoutInflater layoutInflater;

    private String visitDate;
    private String lastTime;

    private ProgressDialog progressDialog;
    private final int INITFINISH = 1;
    private final int DATAFINISH = 2;
    private final int DATACHULI = 3;


    private Bundle date;

    private CameraService cameraService;

    private RelativeLayout backRl;

    private RelativeLayout confirmRl;

    private CheckIndexService checkindexservice;

    private String channelId;// 该终端的 渠道

    // 进销存数据源
    //InvoicingStc--进销存数据显示的数据结构
    private List<InvoicingStc> dataLst = new ArrayList<InvoicingStc>();

    @SuppressWarnings("rawtypes")
    private Class fragmentArray[] = {SayHiFragment.class,
            InvoicingFragment.class, CheckIndexFragment.class,
            ChatVieFragment.class, CameraFragment.class};

    private int imageViewArray[] = {R.drawable.bt_shopvisit_sayhi,
            R.drawable.bt_shopvisit_invoicing,
            R.drawable.bt_shopvisit_checkindex, R.drawable.bt_shopvisit_chatvie,
            R.drawable.bt_shopvisit_camera};


    //是否在加载数据
    private boolean isLoadingData = true;

    /*private Handler handler = new Handler() {

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
                    Bundle data = msg.getData();
                    initBandleDate(data);
                    break;

                case DATAFINISH:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                default:
                    break;
            }
        }
    };*/

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //加载布局
        setContentView(R.layout.visit_shopvisit_main);
        DbtLog.logUtils(TAG, "onCreate()");
        this.initView();

        // 弹出进度框
        /*progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("初始化中...");
		progressDialog.show();*/


        if (!ConstValues.FLAG_1.equals(seeFlag)) {
            // initBaiduXY();
            yuanshengLocation2();
        }

        // 初始化界面并组建Bundle参数
        /*Bundle bundle = this.initData();
        initBandleDate(bundle);*/

        this.asynch();

        // 初始化界面并组建Bundle参数
        //this.initVisitData();
        //this.initViewDate();// 因为注释掉handler,所以这句话会导致报错


    }

    /**
     * 异步加载
     */
    public void asynch() {
        DbtLog.logUtils(TAG, "asynch()");
        new AsyncTask<Void, Void, Void>() {
            protected void onPreExecute() {
                isLoadingData = true;
            }

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            protected void onPostExecute(Void result) {
                // 初始化界面并组建Bundle参数
                Bundle bundle = initData();
                initBandleDate(bundle);
                isLoadingData = false;
            }

            ;

        }.execute();
    }


    private void initViewDate() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // 组建打招呼、进销存、聊竞品、查指标页面所需参数
                    Bundle bundle = returnBundle();
                    // 初始化TabHost
                    initBandleDate(bundle);

                } catch (Exception e) {
                    Log.e(TAG, "MakePlanActivity INIT EXCEPTION:", e);
                } finally {
                    // handler.sendEmptyMessage(DATAFINISH);
                }
            }
        };
        thread.start();
    }

    private void initBandleDate(Bundle bundle) {
        // 初始化TabHost
        tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        tabHost.setup(ShopVisitActivity.this, getSupportFragmentManager(), R.id.realtabcontent);
        for (int i = 0; i < fragmentArray.length; i++) {
            TabSpec tabSpec = tabHost.newTabSpec(String.valueOf(i))
                    .setIndicator(getTabItemView(i));
            tabHost.addTab(tabSpec, fragmentArray[i], bundle);
        }

        tabHost.setOnTabChangedListener(ShopVisitActivity.this);// 用来监听 打招呼、进销存、差指标、聊竞品 页面的操作事件
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.platform_tab_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(imageViewArray[index]);
        return view;
    }

    /**
     * 初始化界面组件
     */
    private void initView() {

        DbtLog.logUtils(TAG, "initView()");

        // 绑定界面组件
        layoutInflater = LayoutInflater.from(this);
        titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
        backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
        sureBt = (TextView) findViewById(R.id.banner_navigation_bt_confirm);
        visitDateTv = (TextView) findViewById(R.id.shopvisit_tv_visitterm_date);
        visitDayTv = (TextView) findViewById(R.id.shopvisit_tv_visitterm_day);
        visitMemoIv = (ImageView) findViewById(R.id.shopvisit_bt_memo);

        backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
        // 添加事件
        //sureBt.setOnClickListener(this);
        backBt.setOnClickListener(this);
        visitMemoIv.setOnClickListener(this);
        visitMemoIv.setOnTouchListener(this);

        // 获取参数“终端信息”
        Bundle bundle = getIntent().getExtras();
        visitDate = FunUtil.isBlankOrNullTo(bundle.getString("visitDate"), "");
        seeFlag = bundle.getString("seeFlag");
        isFirstVisit = bundle.getString("isFirstVisit");
        termStc = (MstTermListMStc) bundle.getSerializable("termStc");
        titleTv.setText(termStc.getTerminalname());
        visitDateTv.setText(FunUtil.isBlankOrNullTo(
                bundle.getString("visitDate"), ""));
        visitDayTv.setText(FunUtil.isBlankOrNullTo(
                bundle.getString("visitDay"), "0"));

        lastTime = bundle.getString("lastTime");

        // 初始化显示状态
        if (!ConstValues.FLAG_1.equals(seeFlag)) {
            sureBt.setVisibility(View.VISIBLE);
            sureBt.setBackgroundResource(R.drawable.bt_finish);
            sureBt.setTag("-1");
        }

        // 获取屏幕长宽
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 创建拍照文件夹(在CameraFragment界面也创建了一遍)
        String path1 = getFilesDir().getAbsolutePath() + File.separator + "photo" + File.separator;
        String path2 = Environment.getExternalStorageDirectory() + "/dbt/et.tsingtaopad" + "/photo" + File.separator;
        // 调用自定义相机,图片保存路径 /data/data/et.tsingtaopad/files/photo/
        FileUtil.createphotoFile(new File(path1));
        // 调用系统相机,图片保存路径 /storage/emulated/0/dbt/et.tsingtaopad/photo/
        FileUtil.createphotoFile(new File(path2));

        // 开启系统GPS位置
        // initGPS();
        // isHavepermission();
    }

    // 将这个方法分成主线程一部分(initVisitData),子线程一部分(returnBundle) 20170516
    private Bundle initData() {

        DbtLog.logUtils(TAG, "initData()");

        PrefUtils.putInt(getApplicationContext(), "idsavesuccess", 1);

        Bundle bundle = getIntent().getExtras();
        service = new ShopVisitService(this, null);
        cameraService = new CameraService(this, null);
        checkindexservice = new CheckIndexService(this, null);

        MstTerminalinfoM term = service.findTermById(termStc.getTerminalkey());
        channelId = term.getMinorchannel();

        // 如果不是查看操作， 配置本次拜访基础数据
        if (!ConstValues.FLAG_1.equals(seeFlag)) {
            // 配置本次拜访的相关表的数据
            visitId = service.configVisitData(bundle);
            DbtLog.logUtils(TAG, "visitId:" + visitId);


            // 获取分项采集有多少行(查出有什么指标(比如:铺货状态,冰冻化),每个指标下的IndexValueLst是有多少个产品)
            List<ProIndex> calculateLst = checkindexservice.queryCalculateIndex(visitId, termStc.getTerminalkey(), channelId, seeFlag);
            // (查出所有采集项)
            List<ProItem> proItemLst = checkindexservice.queryCalculateItem(visitId, channelId);
            // 获取与产品无关的指标
            List<CheckIndexCalculateStc> noProIndexLst = checkindexservice.queryNoProIndex(visitId, channelId, seeFlag);
            // 保存查指标页面的数据
            checkindexservice.saveCheckIndex(visitId, termStc.getTerminalkey(), calculateLst, proItemLst, noProIndexLst);

            //----↓ 进入终端拜访  配置 产品组合是否达标--2017年10月16日17:08:11------------------
            MstGroupproductM vo = null;
            // 先查询之前数据  判断终端该指标是否达标
            List<MstGroupproductM> listvo = new ArrayList<MstGroupproductM>();
            listvo = checkindexservice.queryMstGroupproductM(term.getTerminalcode(), (DateUtil.getDateTimeStr(7) + "  00:00:00"));
            // 有上次数据
            if (listvo.size() > 0) {
                // 数据是今天创建的
                if ((DateUtil.getDateTimeStr(7) + "  00:00:00").equals(listvo.get(0).getStartdate())) {
                    vo = listvo.get(0);
                    // 之前创建的一条数据
                } else {
                    vo = listvo.get(0);// 先复制再重新赋值
                    vo.setGproductid(FunUtil.getUUID());
                    vo.setTerminalcode(term.getTerminalcode());
                    vo.setTerminalname(term.getTerminalname());
                    vo.setStartdate(DateUtil.getDateTimeStr(7) + "  00:00:00");
                    vo.setEnddate("3000-12-01" + "  00:00:00");
                    vo.setCreateusereng(PrefUtils.getString(getApplicationContext(), "userGongHao", "20000"));
                    vo.setCreatedate(DateUtil.getDateTimeStr(6));
                    vo.setUpdateusereng(PrefUtils.getString(getApplicationContext(), "userGongHao", "20000"));
                    vo.setUpdatetime(DateUtil.getDateTimeStr(6));
                    vo.setUpdateusereng(PrefUtils.getString(getApplicationContext(), "userGongHao", "20000"));
                    vo.setUploadFlag("0");// 不上传
                    vo.setPadisconsistent("0");// 未上传
                    checkindexservice.createMstGroupproductM(vo);
                }
            }
            // 没有上次数据
            else {
                // 插入一条今天新数据
                vo = new MstGroupproductM();
                vo.setGproductid(FunUtil.getUUID());
                vo.setTerminalcode(term.getTerminalcode());
                vo.setTerminalname(term.getTerminalname());
                vo.setIfrecstand("N");// 未达标
                vo.setStartdate(DateUtil.getDateTimeStr(7) + "  00:00:00");
                vo.setEnddate("3000-12-01" + "  00:00:00");
                vo.setCreateusereng(PrefUtils.getString(getApplicationContext(), "userGongHao", "20000"));
                vo.setCreatedate(DateUtil.getDateTimeStr(6));
                vo.setUpdateusereng(PrefUtils.getString(getApplicationContext(), "userGongHao", "20000"));
                vo.setUpdatetime(DateUtil.getDateTimeStr(6));
                vo.setUpdateusereng(PrefUtils.getString(getApplicationContext(), "userGongHao", "20000"));
                vo.setUploadFlag("0");// 不上传
                vo.setPadisconsistent("0");// 未上传
                checkindexservice.createMstGroupproductM(vo);
            }
            //----↑ 进入终端拜访   配置 产品组合是否达标--2017年10月16日17:08:20------------------

        } else {
            //MST_VISIT_M(拜访主表)
            MstVisitM visitM = (MstVisitM) bundle.getSerializable("visitM");
            visitId = visitM.getVisitkey();
            // 去除重复产品指标表
            service.delRepeatMstCheckexerecordInfo(visitM);
            // 查看状态下的提示框
            if (ConstValues.FLAG_1.equals(PropertiesUtil.getSharedPreferences(
                    this, TAG, "1"))) {
                this.showSeeDialg();
            }
        }

        // 初始化供货关系 直接点击结束 生成供货关系  (注意:生成供货关系, 也就各个产品的采集项数据就有数据了MST_COLLECTIONEXERECORD_INFO)

        invoicingservice = new InvoicingService(getApplicationContext(), null);
        dataLst = invoicingservice.queryMinePro(visitId, termStc.getTerminalkey());
        invoicingservice.saveInvoicing(dataLst, visitId, termStc.getTerminalkey());

        // 组建打招呼、进销存、聊竞品、查指标页面所需参数
        bundle = new Bundle();

        bundle.putSerializable("termId", termStc.getTerminalkey());
        bundle.putSerializable("visitKey", visitId);
        bundle.putSerializable("seeFlag", seeFlag);
        bundle.putSerializable("isFirstVisit", isFirstVisit);
        bundle.putSerializable("termname", termStc.getTerminalname());

        bundle.putSerializable("visitDate", visitDate);// 上一次的拜访时间(用于促销活动 状态隔天关闭)
        bundle.putSerializable("lastTime", lastTime);// 上一次的拜访时间(用于促销活动 状态隔天关闭)
        return bundle;
    }

    String sdf = "";

	
	/*private void initVisitData() {

		DbtLog.logUtils(TAG, "initData()");
		
		PrefUtils.putInt(getApplicationContext(), "idsavesuccess", 1);

		Bundle bundle = getIntent().getExtras();
		service = new ShopVisitService(getApplicationContext(), null);
		cameraService = new CameraService(this, null);

		// 如果不是查看操作， 配置本次拜访基础数据
		if (!ConstValues.FLAG_1.equals(seeFlag)) {
			// 配置本次拜访的相关表的数据
			visitId = service.configVisitData(bundle);
			DbtLog.logUtils(TAG, "visitId:" + visitId);
			
			CheckIndexService service = new CheckIndexService(this, null);
			MstTerminalinfoM term = service.findTermById(termStc.getTerminalkey());
		    String channelId = term.getMinorchannel();
		    // 获取分项采集有多少行
		    List<ProIndex> calculateLst  = service.queryCalculateIndex(visitId, termStc.getTerminalkey(), channelId, seeFlag);
		    // 获取每一行中有多少条目
		    List<ProItem> proItemLst = service.queryCalculateItem(visitId, channelId);
		    // 获取与产品无关的指标
		    List<CheckIndexCalculateStc> noProIndexLst=service.queryNoProIndex(visitId, term.getMinorchannel(), seeFlag);
			// 保存查指标页面的数据
	        service.saveCheckIndex(visitId, termStc.getTerminalkey(), calculateLst, proItemLst, noProIndexLst);
		
		} else {
			//MST_VISIT_M(拜访主表)
			MstVisitM visitM = (MstVisitM) bundle.getSerializable("visitM");
			visitId = visitM.getVisitkey();
			// 去除重复产品指标表
			service.delRepeatMstCheckexerecordInfo(visitM);
			// 查看状态下的提示框
			if (ConstValues.FLAG_1.equals(PropertiesUtil.getSharedPreferences(
					this, TAG, "1"))) {
				this.showSeeDialg();
			}
		}
		
		// 初始化供货关系 直接点击结束 生成供货关系  (注意:生成供货关系, 也就各个产品的采集项数据就有数据了MST_COLLECTIONEXERECORD_INFO)
		InvoicingService invoicingservice = new InvoicingService(getApplicationContext(), null);
		dataLst = invoicingservice.queryMinePro(visitId,termStc.getTerminalkey());
		invoicingservice.saveInvoicing(dataLst, visitId, termStc.getTerminalkey());
		 
	}*/

    // 组建打招呼、进销存、聊竞品、查指标页面所需参数
    private Bundle returnBundle() {

        // 组建打招呼、进销存、聊竞品、查指标页面所需参数
        Bundle bundle = new Bundle();

        bundle.putSerializable("termId", termStc.getTerminalkey());
        bundle.putSerializable("visitKey", visitId);
        bundle.putSerializable("seeFlag", seeFlag);
        bundle.putSerializable("termname", termStc.getTerminalname());

        bundle.putSerializable("visitDate", visitDate);// 上一次的拜访时间(用于促销活动 状态隔天关闭)
        bundle.putSerializable("lastTime", lastTime);// 上一次的拜访时间(用于促销活动 状态隔天关闭)
        return bundle;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.banner_navigation_rl_back:
            case R.id.banner_navigation_bt_back:
                DbtLog.logUtils(TAG, "返回");

                confirmBack();
                //finish();
                break;

            case R.id.banner_navigation_rl_confirm:
                //case R.id.banner_navigation_bt_confirm:
                DbtLog.logUtils(TAG, "确定");
                //confirmUplad();

                if (1 == PrefUtils.getInt(getApplicationContext(), "idsavesuccess", 3) && checkTakeCamera()/*&&checkCollectionexrecord()*/) {// 拍照图片已成功保存到本地
                    //if(1==PrefUtils.getInt(getApplicationContext(), "idsavesuccess", 3)){// 拍照图片已成功保存到本地
                    // 查询拜访主表的remarks字段是否有值

                    confirmUplad();
                } else if (!checkTakeCamera()) {// 未拍照
                    Toast.makeText(getApplicationContext(), "拍照任务未完成,不能上传", Toast.LENGTH_SHORT).show();
                }/*else if(!checkCollectionexrecord()){// 未拍照
                Toast.makeText(getApplicationContext(), "所有的现有量,变化量必须填值(没货填0)", Toast.LENGTH_SHORT).show();
			}*/ else {
                    Toast.makeText(getApplicationContext(), "图片在本地还未保存,请稍后上传", Toast.LENGTH_SHORT).show();
                }

                break;
            // 客情备忘录
            case R.id.shopvisit_bt_memo:
                if (visitMemoIvXY == (visitMemoIv.getLeft() + visitMemoIv.getTop())) {
                    MemoDialogFragment fragment = MemoDialogFragment.getInstance(true, termStc.getTerminalkey(), seeFlag, "");
                    fragment.show(getSupportFragmentManager(), "dialog");
                }
                break;

            default:
                break;
        }
    }


    // true:可以上传   false:不可上传
    private boolean checkCollectionexrecord() {
        // 获取终端最新渠道
        MstTerminalinfoM term = service.findTermById(termStc.getTerminalkey());
        String michannelId = term.getMinorchannel();

        boolean isallIn = true;
        // (查出所有采集项)
        List<ProItem> proItemLst = checkindexservice.queryCalculateItem(visitId, michannelId);
        for (ProItem proitem : proItemLst) {
            if ("".equals(FunUtil.isNullSetSpace(proitem.getBianhualiang())) || "".equals(FunUtil.isNullSetSpace(proitem.getXianyouliang()))) {
                isallIn = false;// 为空不能上传
                break;
            }
        }
        return isallIn;
    }

    // // 进销存中渠道价零售价需>0   true:可以上传   false:不可上传
    private boolean checkInvoicingCheckGoods() {

        //List<MstVistproductInfo> mstVistproductInfoss = invoicingservice.queryVisitproMinePro(visitId, termStc.getTerminalkey());
        List<InvoicingStc> mstVistproductInfos = invoicingservice.queryMinePro(visitId, termStc.getTerminalkey());
        boolean isallIn = true;
        for (InvoicingStc invoicingStc : mstVistproductInfos) {
            if (!(Double.parseDouble(invoicingStc.getChannelPrice()) > 0
                    && Double.parseDouble(invoicingStc.getSellPrice()) > 0)) {
                isallIn = false;//
                break;
            }
        }
        return isallIn;
    }


    /**
     * 检测该终端本次拜访是否已拍照
     *
     * @return true:已拍照  false:未拍照
     */
    private boolean checkTakeCamera() {
        List<PictypeDataStc> valueLst = new ArrayList<PictypeDataStc>();
        List<CameraDataStc> piclst = new ArrayList<CameraDataStc>();

        // 已拍张数
        piclst = cameraService.queryCurrentPicRecord1(termStc.getTerminalkey(), DateUtil.getDateTimeStr(0), "1", "0", visitId);
        // 后台配置需拍多少张
        valueLst = cameraService.queryPictypeMAll();

        // 根据促销活动 重新设置需要拍多少张照片
        /*int piccount = valueLst.size();
		List<CheckIndexPromotionStc> promotionLst = new ArrayList<CheckIndexPromotionStc>();
		MstTerminalinfoM term = service.findTermById(termStc.getTerminalkey());
		promotionLst = new CheckIndexService(this, null).queryPromotion(visitId, term.getSellchannel(), term.getTlevel());
        if(promotionLst.size()>0) {
        	piccount = valueLst.size()+1;
        }*/
		/*if(valueLst.size()==0&&piclst.size()>=piccount){// 没配照片且没促销活动,允许上传
			return true;
		}else if(valueLst.size()>0&&piclst.size()>=piccount){// 
			return true;
		}*/

        // 重新定义已拍张数(若有促销活动拍照,则-1)
        int piccount = piclst.size();
        if (piccount > 0) {
            for (CameraDataStc cameraDataStc : piclst) {
                if ("42f44fg3-42s5-458d-a32e-622e393o76d6".equals(cameraDataStc.getPictypekey())) {// 促销活动拍照
                    piccount--;
                }
            }
        }
        if (valueLst.size() == 0) {// 没配照片且没促销活动,允许上传  &&piclst.size()>=piccount
            return true;
        } else if (valueLst.size() > 0 && piccount > 0) {//
            return true;
        }
		
		/*
        if(valueLst.size()==0){// 没配照片,允许上传
			return true;
		}else if(valueLst.size()>0&&piclst.size()>0){// 
			return true;
		}
		*/
        /*if(piclst.size()>=piccount){// 没配照片且没促销活动,允许上传
			return true;
		}*/
        return false;
    }

    /**
     * 确定返回上一界面
     */
    private void confirmBack() {

        // 如果不是查看操作，返回需再次确定
        if (!ConstValues.FLAG_1.equals(seeFlag)) {
            DbtLog.logUtils(TAG, "返回选择终端界面");
            View view = LayoutInflater.from(this).inflate(
                    R.layout.agencyvisit_total_overvisit_dialog, null);
            TextView title = (TextView) view
                    .findViewById(R.id.agencyvisit_tv_over_title);
            TextView msg = (TextView) view
                    .findViewById(R.id.agencyvisit_tv_over_msg);
            ImageView sure = (ImageView) view
                    .findViewById(R.id.agencyvisit_bt_over_sure);
            ImageView cancle = (ImageView) view
                    .findViewById(R.id.agencyvisit_bt_over_quxiao);
            title.setText(R.string.dialog_back);
            msg.setText(R.string.dialog_msg_backdesc);
            dialog = new AlertDialog.Builder(this).setCancelable(false).create();
            dialog.setView(view, 0, 0, 0, 0);
            sure.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 返回也要将活动的激活状态移走
                    PrefUtils.remove(getApplicationContext(), "isfrist" + termStc.getTerminalkey());

                    // 拜访返回时将本次拍的照片删除 (修改拍照时间不在拜访时间区间内)
                    if (!ConstValues.FLAG_1.equals(seeFlag)) {// 如果不是查看状态

                        // 将orderbyno字段改为1 表示返回了 再次进入需要重记拜访时间
						/*MstVisitM visitM = service.findVisitById(visitId);
						visitM.setOrderbyno("1");
						service.saveorUpdate(visitM);*/
                        // 返回时将终端拜访状态置为1 进入拜访时终端拜访状态置为0(默认为0) 确定上传时移除终端拜访状态
                        PrefUtils.putString(ShopVisitActivity.this, "back" + termStc.getTerminalkey(), "1");

                        // 删除照片
                        List<CameraDataStc> piclst = new ArrayList<CameraDataStc>();
                        piclst = cameraService.queryCurrentPicRecord1(termStc.getTerminalkey(), DateUtil.getDateTimeStr(0), "1", "0", visitId);
                        for (CameraDataStc cameraDataStc : piclst) {
                            // 删除拍照表记录
                            cameraService.deletePicByCameratype(cameraDataStc.getPictypekey(), termStc.getTerminalkey(), visitId);
                            // 若是重拍的,要删除该位置上一张图片
                            FileUtil.deleteFile(new File(cameraDataStc.getLocalpath()));
                        }
                    }
                    dialog.dismiss();
                    ShopVisitActivity.this.finish();

                }
            });
            cancle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            PrefUtils.remove(getApplicationContext(), "isfrist" + termStc.getTerminalkey());
            finish();
        }
    }

    /**
     * 确定是否要上传数据
     */
    private void confirmUplad() {
        super.onPause();
        DbtLog.logUtils(TAG, "结束拜访");
        // 如果校验通过
        if (!"-1".equals(sureBt.getTag().toString())) {
            ViewUtil.sendMsg(getApplicationContext(),
                    Integer.parseInt(sureBt.getTag().toString()));
            ShopVisitActivity.this.onResume();
        } else {
            if (dialog != null && dialog.isShowing())
                return;
            View view = LayoutInflater.from(this).inflate(
                    R.layout.agencyvisit_total_overvisit_dialog, null);
            TextView title = (TextView) view
                    .findViewById(R.id.agencyvisit_tv_over_title);
            TextView msg = (TextView) view
                    .findViewById(R.id.agencyvisit_tv_over_msg);
            ImageView sure = (ImageView) view
                    .findViewById(R.id.agencyvisit_bt_over_sure);
            ImageView cancle = (ImageView) view
                    .findViewById(R.id.agencyvisit_bt_over_quxiao);
            title.setText(R.string.dialog_title);
            msg.setText(R.string.dialog_msg_overshopvisit);
            dialog = new AlertDialog.Builder(this).setCancelable(false)
                    .create();
            dialog.setView(view, 0, 0, 0, 0);
            sure.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ViewUtil.isDoubleClick(v.getId(), 2500))
                        return;
                    DbtLog.logUtils(TAG, "结束拜访：是");

                    if (!checkInvoicingCheckGoods()) {// 进销存中渠道价零售价需>0
                        Toast.makeText(getApplicationContext(), "问货源的渠道价零售价必须大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!checkCollectionexrecord()) {// 未填写现有量变化量
                        Toast.makeText(getApplicationContext(), "所有的现有量,变化量必须填值(没货填0)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //
                    PrefUtils.remove(getApplicationContext(), "isfrist" + termStc.getTerminalkey());
                    // 返回时将终端拜访状态置为1 进入拜访时终端拜访状态置为0(默认为0) 确定上传时移除终端拜访状态
                    PrefUtils.remove(ShopVisitActivity.this, "back" + termStc.getTerminalkey());
                    service.updateGps(visitId, longitude, latitude, "");
                    String visitEndDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");

                    // CheckIndexService checkindexservice = new CheckIndexService(ShopVisitActivity.this, null);
                    MstTerminalinfoM term = service.findTermById(termStc.getTerminalkey());
                    String channelId = term.getMinorchannel();
                    List<ProIndex> calculateLst = checkindexservice.queryCalculateIndex(visitId, termStc.getTerminalkey(), channelId, seeFlag);
                    List<ProItem> proItemLst = checkindexservice.queryCalculateItem(visitId, channelId);
                    List<CheckIndexCalculateStc> noProIndexLst = checkindexservice.queryNoProIndex(visitId, term.getMinorchannel(), seeFlag);
                    // 保存查指标页面的数据
                    checkindexservice.saveCheckIndex(visitId, termStc.getTerminalkey(), calculateLst, proItemLst, noProIndexLst);

                    // 更新拜访离店时间及是否要上传标志 以及对去除拜访指标采集项重复(collectionexerecord表)
                    service.confirmUpload(visitId, termStc.getTerminalkey(), visitEndDate, "1");

                    // 删除没有供货关系,却有铺货指标的记录
                    checkindexservice.deleteFromcheckexerecord();
                    // checkindexservice.deleteFromcheckexerecord();
                    // 修改产品组合是否达标状态 变成需上传状态 // 终端编码 今天(2011-04-11)
                    checkindexservice.updateMstGroupproductM(termStc.getTerminalcode(), DateUtil.getDateTimeStr(7) + "  00:00:00");

                    UploadDataService upService = new UploadDataService(getApplicationContext(), null);
                    // 修改此次拜访所有图片sureup为0 确定上传状态
                    // CameraService cameraService2 = new CameraService(getApplicationContext(), null);
                    // new CameraService(getApplicationContext(),null).updataSureup(visitId);
                    cameraService.updataSureup(visitId);
                    // 上传所有的巡店拜访
                    if (1 == PrefUtils.getInt(getApplicationContext(), "idsavesuccess", 3)) {
                        upService.upload_visit(false, visitId, ConstValues.WAIT2);
                        // upService.upload_visit(false, visitId,ConstValues.WAIT2);
                    } else {
                        Toast.makeText(getApplicationContext(), "图片在本地还未保存,请稍后上传", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 上传所有未上传图片
                    // 检测促销活动是否有打开,没打开就把该终端所有促销活动图片删除
                    checkhaveactivity();
                    // 上传照片
                    upService.upload_visit_camera(false, "0", visitId);
                    // 删除photo文件夹所有文件,因为图片已经转化成字符串保存在表中,上传只是表数据上传,图片已经没用了
                    String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dbt/et.tsingtaopad" + "/photo/";
                    FileUtil.deleteFile(new File(sdcardPath));

                    // 删除这家终端,不是本次拜访的照片记录
                    cameraService.deletePicBytermkey(termStc.getTerminalkey(), visitId);

                    dialog.dismiss();
                    ShopVisitActivity.this.finish();
                    // finish();
                }

            });
            cancle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DbtLog.logUtils(TAG, "结束拜访：否");
                    String visitEndDate = DateUtil.formatDate(new Date(),
                            "yyyyMMddHHmmss");
                    service.confirmUpload(visitId, termStc.getTerminalkey(),
                            visitEndDate, "0");
                    dialog.dismiss();
                    ShopVisitActivity.this.onResume();
                }
            });
            dialog.show();
        }
    }

    //检测促销活动是否有打开
    private void checkhaveactivity() {
        // TODO Auto-generated method stub
        // 判断活动拍照按钮是否出现 ---------------160927
        CheckIndexService service = new CheckIndexService(this, null);
        List<CheckIndexPromotionStc> promotionLst = new ArrayList<CheckIndexPromotionStc>();
        MstTerminalinfoM term = service.findTermById(termStc.getTerminalkey());
        promotionLst = service.queryPromotion(visitId, term.getSellchannel(), term.getTlevel());
        int IsAccomplishcount1 = 0;
        for (int i = 0; i < promotionLst.size(); i++) {

            if ("1".equals(promotionLst.get(i).getIsAccomplish())) {// 有达成的活动 则+1;
                IsAccomplishcount1++;
            }
        }
        String todaytime = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        if (IsAccomplishcount1 > 0) {// 有激活,当天拜访//&&todaytime.equals(lastTime.substring(0, 10))
            //activityPicBt.setVisibility(View.VISIBLE);// 如果有促销活动,拍照按钮显示
        } else {// 没激活,不是当天拜访
            //activityPicBt.setVisibility(View.GONE);
            // 把该终端所有促销活动图片删除
            deleteactivitypic();
        }
        // 判断锉削活动拍照是否出现 ---------------160927
    }

    /**
     * 把该终端所有促销活动图片删除
     */
    private void deleteactivitypic() {

        // 删除本地照片
        String filepath = cameraService.getPathbyCameratype("42f44fg3-42s5-458d-a32e-622e393o76d6", termStc.getTerminalkey(), visitId);
        if (!TextUtils.isEmpty(filepath)) {
            // 删除拍照表记录
            cameraService.deletePicByCameratype("42f44fg3-42s5-458d-a32e-622e393o76d6", termStc.getTerminalkey(), visitId);
            // 若是重拍的,要删除该位置上一张图片
            FileUtil.deleteFile(new File(filepath));

        }
    }

    @Override
    public void onTabChanged(String tabId) {

        if (tabId.equals("0")) {
            DbtLog.logUtils(TAG, "SayHiFragment:打招呼");
        } else if (tabId.equals("1")) {
            DbtLog.logUtils(TAG, "InvoicingFragment:进销存");
        } else if (tabId.equals("2")) {
            DbtLog.logUtils(TAG, "CheckIndexFragment:查指标");
        } else if (tabId.equals("3")) {
            DbtLog.logUtils(TAG, "ChatVieFragment:聊竞品");
        }

        // 如果是查看操作，则不做数据校验
        if (ConstValues.FLAG_1.equals(seeFlag))
            return;

        BaseActivity view = (ShopVisitActivity) tabHost.getCurrentView()
                .getContext();
        EditText termNameTv = (EditText) view
                .findViewById(R.id.sayhi_et_termName);
        if (termNameTv != null) {

            Spinner belongLineSp = (Spinner) view.findViewById(R.id.sayhi_sp_belongLine);
            Spinner levelSp = (Spinner) view.findViewById(R.id.sayhi_sp_termLevel);
            Spinner provSp = (Spinner) view.findViewById(R.id.sayhi_sp_prov);
            Spinner citySp = (Spinner) view.findViewById(R.id.sayhi_sp_city);
            Spinner countrySp = (Spinner) view.findViewById(R.id.sayhi_sp_country);
            EditText addressEt = (EditText) view.findViewById(R.id.sayhi_et_address);
            EditText linkmanEt = (EditText) view.findViewById(R.id.sayhi_et_person);
            EditText telEt = (EditText) view.findViewById(R.id.sayhi_et_contactTel);
            EditText sequenceEt = (EditText) view.findViewById(R.id.sayhi_et_sequence);
            Spinner sellChannelSp = (Spinner) view.findViewById(R.id.sayhi_sp_sellChannel);
            Spinner mainChannelSp = (Spinner) view.findViewById(R.id.sayhi_sp_mainChannel);
            Spinner minorChannelSp = (Spinner) view.findViewById(R.id.sayhi_sp_minorChannel);

            int msgId = -1;
            /*if ("".equals(FunUtil.isNullSetSpace(termNameTv.getText())
                    .toString())) {
                termNameTv.requestFocus();
                msgId = R.string.termadd_msg_invaltermname;

            } else if ("".equals(FunUtil.isNullSetSpace(addressEt.getText())
                    .toString())) {
                addressEt.requestFocus();
                msgId = R.string.termadd_msg_invaladdress;

            } else if ("".equals(FunUtil.isNullSetSpace(linkmanEt.getText())
                    .toString())) {
                linkmanEt.requestFocus();
                msgId = R.string.termadd_msg_invalcontact;
            }
            if ("-1".equals(FunUtil.isBlankOrNullTo(belongLineSp.getTag(), "-1"))) {
                msgId = R.string.termadd_msg_invalbelogline;

            } else if ("-1".equals(FunUtil.isBlankOrNullTo(levelSp.getTag(),
                    "-1"))) {
                msgId = R.string.termadd_msg_invaltermlevel;

            } else if ("-1".equals(FunUtil.isBlankOrNullTo(sellChannelSp.getTag(), "-1"))) {
                msgId = R.string.termadd_msg_invalsellchannel;

            } else if ("-1".equals(FunUtil.isBlankOrNullTo(mainChannelSp.getTag(), "-1"))) {
                msgId = R.string.termadd_msg_invalmainchannel;

            } else if ("-1".equals(FunUtil.isBlankOrNullTo(minorChannelSp.getTag(), "-1"))) {
                msgId = R.string.termadd_msg_invalminorchannel;
            }*/

            if (msgId != -1) {
                tabHost.setCurrentTab(0);
                ViewUtil.sendMsg(getApplicationContext(), msgId);

            } else {
                // if (citySp.getSelectedItem() == null) {
                // msgId = R.string.termadd_msg_invalcountry;
                //
                // } else {
                // KvStc cityKvStc = (KvStc)citySp.getSelectedItem();
                // if (!"全省".equals(cityKvStc.getValue()) &&
                // "-1".equals(FunUtil.isBlankOrNullTo(countrySp.getTag(),
                // "-1"))) {
                // msgId = R.string.termadd_msg_invalcountry;
                // }
                // }

                if (msgId != -1) {
                    tabHost.setCurrentTab(0);
                    ViewUtil.sendMsg(getApplicationContext(), msgId);
                }
            }

            // 结束按钮提示信息
            sureBt.setTag(msgId);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();

                // 获取客户备忘的位置数据
                visitMemoIvXY = v.getLeft() + v.getTop();
                break;

            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                int left = v.getLeft() + dx;
                int top = v.getTop() + dy;
                int right = v.getRight() + dx;
                int bottom = v.getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - v.getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + v.getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;

            default:
                break;
        }

        return false;
    }

    // 弹出查看信息提示框
    private void showSeeDialg() {

        DbtLog.logUtils(TAG, "showSeeDialg()");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.dialog_msg_see);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_bt_close,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(R.string.dialog_bt_nervertips,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        PropertiesUtil.updateSharedPreferences(
                                getApplicationContext(), TAG,
                                ConstValues.FLAG_0);
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DbtLog.logUtils(TAG, "onDestroy()");
        if (dialog != null) {
            dialog.dismiss();
        }
        try {
            if (mLocationClient != null) {
                mLocationClient.stop();
            }
            // 移除原生定位的监听
            lm.removeUpdates(locationListener);
            lm.removeGpsStatusListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ///////////////////////////获取经纬度////////////////////////////////////
    private LocationClient mLocationClient;
    private BaiDuLocationListener baiDuLocationListener;
    private double longitude;// 经度
    private double latitude;// 维度

    /***
     * 初始化百度经纬度配置
     */
    public void initBaiduXY() {
        try {
            if (!isOpenGPS()) {
                //openGPS();
                //getAppDetailSettingIntent(getApplicationContext());
                //openDingWei();
                //isHavepermission();
            }
            initLocationListener();
            initLocation();
            if (mLocationClient != null) {
                mLocationClient.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 初始化监听
     */
    public void initLocationListener() {
        mLocationClient = new LocationClient(this);// 声明LocationClient类
        baiDuLocationListener = new BaiDuLocationListener();
        mLocationClient.registerLocationListener(baiDuLocationListener);// 注册监听函数
    }

    /**
     * 实现实位回调监听
     */
    public class BaiDuLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            /**
             * 61 ： GPS定位结果 62 ： 扫描整合定位依据失败。此时定位结果无效。 63 ：
             * 网络异常，没有成功向服务器发起请求。此时定位结果无效。 65 ： 定位缓存的结果。 66 ：
             * 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果 67 ：
             * 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果 68 ：
             * 网络连接失败时，查找本地离线定位时对应的返回结果 161： 表示网络定位结果 162~167： 服务端定位失败
             * 502：key参数错误 505：key不存在或者非法 601：key服务被开发者自己禁用 602：key mcode不匹配
             * 501～700：key验证失败
             */
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude()); // 获取维度
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());// 获取经度
            sb.append("\nradius : ");
            sb.append(location.getRadius());// 获取定位精度半径，单位是米
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append(location.getDirection());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 获取地址
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            Log.i("baiDuLocation", sb.toString());
            // if(!ConstValues.isOnline){
            // Toast.makeText(ShopVisitActivity.this, sb.toString(),
            // Toast.LENGTH_SHORT).show();
            // }
        }

    }

    /***
     * 初始化位置
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        /***
         * Hight_Accuracy:高精度定位模式下，会同时使用GPS、Wifi和基站定位，返回的是当前条件下精度最好的定位结果
         * Battery_Saving:低功耗定位模式下，仅使用网络定位即Wifi和基站定位，返回的是当前条件下精度最好的网络定位结果
         * Device_Sensors
         * :仅用设备定位模式下，只使用用户的GPS进行定位。这个模式下，由于GPS芯片锁定需要时间，首次定位速度会需要一定的时间
         */
        option.setLocationMode(LocationMode.Hight_Accuracy);
        /***
         * gcj02:国测局加密经纬度坐标 bd09ll:百度加密经纬度坐标 bd09:百度加密墨卡托坐标
         */
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(false);// 是否获取地址
        mLocationClient.setLocOption(option);
    }

    /***
     * 是否打开GPS
     *
     * @return
     */
    public boolean isOpenGPS() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /***
     * 打开GPS
     */
    public void openGPS() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled", true);
            this.sendBroadcast(intent);
            String provider = Settings.Secure.getString(
                    this.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (!provider.contains("gps")) { // if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                this.sendBroadcast(poke);
            }
        }
    }


    // 若系统GPS模块没打开, 跳到app应用详情
    public static void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

    // 方法失败 因为这事查看注册文件有没有 判断是否有android.permission.ACCESS_FINE_LOCATION权限
    private void isHavepermission() {
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.ACCESS_FINE_LOCATION", "et.tsingtaopad"));
        if (permission) {
            Toast.makeText(getApplicationContext(), "有这个权限", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "木有这个权限", Toast.LENGTH_SHORT).show();
        }
    }

    // 若系统GPS模块没打开, 跳到安全界面
    private void openDingWei() {
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.SecuritySettings");
        Intent intent = new Intent();
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        startActivity(intent);
    }

    // 弹窗: 手动开启系统GPS定位模块
    private void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(ShopVisitActivity.this, "请打开GPS", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            // 弹出Toast
            //          Toast.makeText(TrainDetailsActivity.this, "GPS is ready",
            //                  Toast.LENGTH_LONG).show();
            //          // 弹出对话框
            //          new AlertDialog.Builder(this).setMessage("GPS is ready")
            //                  .setPositiveButton("OK", null).show();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something...
            // 确定返回上一界面
            confirmBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ---原生GPS定位 ywm 20160505----------------------------------------------------
    private LocationManager lm;

    public void yuanshengLocation2() {

        if (!isOpenGPS()) {
            // openGPS();// 2018年6月26日16:55:20   进入这个页面肯定获取到了权限


            //getAppDetailSettingIntent(getApplicationContext());
            //openDingWei();
            //isHavepermission();
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //判断GPS是否正常启动
        /*if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }*/

        //为获取地理位置信息时设置查询条件
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        //获取位置信息
        //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location = lm.getLastKnownLocation(bestProvider);
        //        Location location= lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        updateView(location);
        // 监听状态
        lm.addGpsStatusListener(listener);
        // 绑定监听，有4个参数
        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        // 参数2，位置信息更新周期，单位毫秒
        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        // 参数4，监听
        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

        // 1秒更新一次，或最小位移变化超过1米更新一次；
        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000,
                120,
                locationListener);
    }

    // 位置监听
    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateView(location);
            // 经度
            longitude = location.getLongitude();
            // 纬度
            latitude = location.getLatitude();
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    //Log.i(TAG, "当前GPS状态为可见状态");

                    //setLog("当前GPS状态为可见状态");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    //Log.i(TAG, "当前GPS状态为服务区外状态");
                    //setLog("当前GPS状态为服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    //Log.i(TAG, "当前GPS状态为暂停服务状态");
                    //setLog("当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = lm.getLastKnownLocation(provider);
            updateView(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateView(null);
        }

    };

    // 状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    //Log.i(TAG, "第一次定位");
                    //setLog("第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    //Log.i(TAG, "卫星状态改变");
                    //setLog("卫星状态改变");
                    // 获取当前状态
				/*GpsStatus gpsStatus = lm.getGpsStatus(null);
				// 获取卫星颗数的默认最大值
				int maxSatellites = gpsStatus.getMaxSatellites();
				// 创建一个迭代器保存所有卫星
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}*/
                    //System.out.println("搜索到：" + count + "颗卫星");
                    //setLog("搜索到：" + count + "颗卫星");
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    //Log.i(TAG, "定位启动");
                    //setLog("定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    //Log.i(TAG, "定位结束");
                    //setLog("定位结束");
                    break;
            }
        }

        ;
    };


    /**
     * 实时更新文本内容
     *
     * @param location
     */
    int type = 0;// 0需要上传(默认)  1不需要上传

    private void updateView(Location location) {
        if (location != null) {
			/*editText.setText("设备位置信息\n\n经度：");
			editText.append(String.valueOf(location.getLongitude()));
			editText.append("\n纬度：");
			editText.append(String.valueOf(location.getLatitude()));*/
            // 经度
            longitude = location.getLongitude();
            // 纬度
            latitude = location.getLatitude();
            if (type == 0 && longitude > 0 && latitude > 0) {
                type = 1;
                // 上传经纬度,获取地理位置
                updateLocation();
            }
        } else {
            // 清空EditText对象
            //editText.getEditableText().clear();
            // 经度
            //longitude = 0.0;
            // 纬度
            //latitude = 0.0;
        }
    }

    /**
     *
     */
    public void updateLocation() {

        //longitude = 117.090734350000005000;
        // 纬度
        //latitude = 24.050067309999999300;

        String content = "{" +
                "areaid:'" + PrefUtils.getString(this, "departmentid", "") + "'," +
                "sourcelon:'" + longitude + "'," +  //"117.090734350000005000"  longitude
                "sourcelat:'" + latitude + "'," +  // "24.050067309999999300"   latitude
                "attencetime:'" + DateUtil.getDateTimeStr(8) + "'," +
                "creuser:'" + PrefUtils.getString(this, "userid", "") + "'" +
                "}";

        // 请求网络
        HttpUtil httpUtil = new HttpUtil(60 * 1000);
        httpUtil.configResponseTextCharset("ISO-8859-1");

        httpUtil.send("get_address", content, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                    AddressStc addressStc = JsonUtil.parseJson(resObj.getResBody().getContent(), AddressStc.class);
                    service.updateAddress(visitId, addressStc.getAddress());
                    // Toast.makeText(ShopVisitActivity.this, "地理位置获取----成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopVisitActivity.this, "地理位置获取----失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String errMsg) {
                Log.e(TAG, errMsg, error);
                Toast.makeText(ShopVisitActivity.this, getString(R.string.msg_err_netfail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(true);
        // 设置是否允许运营商收费 true:不允许付费
        criteria.setCostAllowed(true);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
    // ---原生GPS定位 ywm 20160505----------------------------------------------------
}
