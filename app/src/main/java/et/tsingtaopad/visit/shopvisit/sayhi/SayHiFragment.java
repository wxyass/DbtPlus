package et.tsingtaopad.visit.shopvisit.sayhi;

import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.cui.LongSlideSwitch;
import et.tsingtaopad.cui.LongSlideSwitch.OnLongSwitchChangedListener;
import et.tsingtaopad.cui.SlideSwitch;
import et.tsingtaopad.cui.SlideSwitch.OnSwitchChangedListener;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstInvalidapplayInfo;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.DbtUtils;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.VisitFragment;
import et.tsingtaopad.visit.syncdata.UploadDataService;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：SayHiFragment.java</br> 作者：吴承磊 </br>
 * 创建时间：2013-12-3</br> 功能描述: 巡店拜访_打招呼Activity</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
@SuppressLint({"HandlerLeak", "NewApi"})
//打招呼界面
public class SayHiFragment extends BaseFragmentSupport implements
        OnItemSelectedListener, OnCheckedChangeListener, OnTouchListener, OnLongSwitchChangedListener,
        OnSwitchChangedListener, OnClickListener {

    private SayHiService service;
    private MstVisitM visitM;
    private MstTerminalinfoM termInfo;
    private String prevSequence;
    private String seeFlag;

    private ScrollView SayHiSv;
    private LongSlideSwitch termStatusSw;
    private LongSlideSwitch visitStatusSw;
    private LongSlideSwitch mineproductSW;
    private Button mineProductTime;
    private CheckBox mineCb;
    private CheckBox vieCb;
    private CheckBox mineProtocolCb;
    private CheckBox vieProtocolCb;

    private TextView termCodeTv;
    private EditText termNameEt;
    private Spinner belongLineSp;
    private Spinner levelSp;
    private Spinner laobanSp;
    private Spinner provSp;
    private TextView provTv;
    private TextView cityTv;
    private TextView countryTv;
    private Spinner citySp;
    private Spinner countrySp;
    private EditText addressEt;
    private EditText linkmanEt;
    private EditText telEt;
    private EditText sequenceEt;
    private EditText cycleEt;
    private EditText hvolumeEt;
    private EditText mvolumeEt;
    private EditText pvolumeEt;
    private EditText lvolumeEt;
    private EditText tvolumeEt;
    private Spinner areaTypeSp;
    private Spinner sellChannelSp;
    private Spinner mainChannelSp;
    private Spinner minorChannelSp;
    private EditText visitPersonEt;
    private Button sayhi_text;

    private String selectDate;
    private String aday;
    private Calendar calendar;
    private int yearr;
    private int month;
    private int day;
    private String ifminedate;

    private final String TAG = "SayHiFragment";

    private long startTime;
    private long subTime;

    MyHandler handler;

    //是否在加载数据
    private boolean isLoadingData = true;

	/*@SuppressLint({ "HandlerLeak", "Recycle" })
    public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();

			super.handleMessage(msg);
			switch (msg.what) {

			// 未校验通过属性获取焦点
			case ConstValues.WAIT2:
				requestFocus(bundle.getInt("msgId"));
				break;

			// 终端有效状态
			case ConstValues.WAIT3:
				MotionEvent event = MotionEvent.obtain(
						SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_UP, termStatusSw.getLeft(),
						termStatusSw.getTop(), termStatusSw.getId());
				termStatusSw.onTouchEvent(event);
				break;

			default:
				break;
			}
		}
	};*/

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<SayHiFragment> fragmentRef;

        public MyHandler(SayHiFragment fragment) {
            fragmentRef = new SoftReference<SayHiFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            SayHiFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            // 处理UI 变化
            Bundle bundle = msg.getData();

            super.handleMessage(msg);
            switch (msg.what) {

                // 未校验通过属性获取焦点
                case ConstValues.WAIT2:
                    fragment.requestFocus(bundle.getInt("msgId"));
                    break;

                // 终端有效状态
                case ConstValues.WAIT3:
                    fragment.requestMotionEvent();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // View view = inflater.inflate(R.layout.shopvisit_sayhi, null);
        View view = inflater.inflate(R.layout.shopvisit_sayhi, container, false);
        DbtLog.logUtils(TAG, "onCreateView()");
        this.initView(view);
        // this.initData();
        this.asynch();
        return view;
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
                initData();
                isLoadingData = false;
            }

            ;

        }.execute();
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");

        // 绑界面组件
        SayHiSv = (ScrollView) view.findViewById(R.id.sayhi_scrollview);
        termStatusSw = (LongSlideSwitch) view.findViewById(R.id.sayhi_sw_termstatus);
        visitStatusSw = (LongSlideSwitch) view.findViewById(R.id.sayhi_sw_visitstatus);
        mineproductSW = (LongSlideSwitch) view.findViewById(R.id.sayhi_sw_wopindianzhao);
        mineProductTime = (Button) view.findViewById(R.id.sayhi_btn_time);
        sayhi_text = (Button) view.findViewById(R.id.sayhi_btn_text);
        mineCb = (CheckBox) view.findViewById(R.id.sayhi_cb_mine);
        vieCb = (CheckBox) view.findViewById(R.id.sayhi_cb_vie);
        mineProtocolCb = (CheckBox) view.findViewById(R.id.sayhi_cb_mineprotocol);
        vieProtocolCb = (CheckBox) view.findViewById(R.id.sayhi_cb_vieprotocol);
        termCodeTv = (TextView) view.findViewById(R.id.sayhi_tv_termCode);
        termNameEt = (EditText) view.findViewById(R.id.sayhi_et_termName);
        belongLineSp = (Spinner) view.findViewById(R.id.sayhi_sp_belongLine);
        levelSp = (Spinner) view.findViewById(R.id.sayhi_sp_termLevel);

        provSp = (Spinner) view.findViewById(R.id.sayhi_sp_prov);
        provTv = (TextView) view.findViewById(R.id.sayhi_tv_prov);
        cityTv = (TextView) view.findViewById(R.id.sayhi_tv_city);
        countryTv = (TextView) view.findViewById(R.id.sayhi_tv_country);
        citySp = (Spinner) view.findViewById(R.id.sayhi_sp_city);
        countrySp = (Spinner) view.findViewById(R.id.sayhi_sp_country);
        addressEt = (EditText) view.findViewById(R.id.sayhi_et_address);
        linkmanEt = (EditText) view.findViewById(R.id.sayhi_et_person);
        telEt = (EditText) view.findViewById(R.id.sayhi_et_contactTel);
        sequenceEt = (EditText) view.findViewById(R.id.sayhi_et_sequence);
        cycleEt = (EditText) view.findViewById(R.id.sayhi_et_cycle);
        hvolumeEt = (EditText) view.findViewById(R.id.sayhi_et_hvolume);
        mvolumeEt = (EditText) view.findViewById(R.id.sayhi_et_mvolume);
        pvolumeEt = (EditText) view.findViewById(R.id.sayhi_et_pvolume);
        lvolumeEt = (EditText) view.findViewById(R.id.sayhi_et_lvolume);
        tvolumeEt = (EditText) view.findViewById(R.id.sayhi_et_tvolume);
        areaTypeSp = (Spinner) view.findViewById(R.id.sayhi_sp_termArea);
        sellChannelSp = (Spinner) view.findViewById(R.id.sayhi_sp_sellChannel);
        mainChannelSp = (Spinner) view.findViewById(R.id.sayhi_sp_mainChannel);
        minorChannelSp = (Spinner) view.findViewById(R.id.sayhi_sp_minorChannel);
        visitPersonEt = (EditText) view.findViewById(R.id.sayhi_et_visitPerson);
        laobanSp = (Spinner) view.findViewById(R.id.sayhi_sp_laoban1);

        // 绑定事件
        termStatusSw.setOnLongSwitchChangedListener(this);
        mineproductSW.setOnLongSwitchChangedListener(this);
        belongLineSp.setOnItemSelectedListener(this);
        areaTypeSp.setOnItemSelectedListener(this);
        levelSp.setOnItemSelectedListener(this);
        provSp.setOnItemSelectedListener(this);
        citySp.setOnItemSelectedListener(this);
        countrySp.setOnItemSelectedListener(this);
        sellChannelSp.setOnItemSelectedListener(this);
        mainChannelSp.setOnItemSelectedListener(this);
        minorChannelSp.setOnItemSelectedListener(this);
        laobanSp.setOnItemSelectedListener(this);
        mineCb.setOnCheckedChangeListener(this);
        vieCb.setOnCheckedChangeListener(this);
        mineProtocolCb.setOnCheckedChangeListener(this);
        vieProtocolCb.setOnCheckedChangeListener(this);
        telEt.addTextChangedListener(watcher);
        hvolumeEt.addTextChangedListener(watcher);
        mvolumeEt.addTextChangedListener(watcher);
        pvolumeEt.addTextChangedListener(watcher);
        lvolumeEt.addTextChangedListener(watcher);
        SayHiSv.setOnTouchListener(this);
        mineProductTime.setOnClickListener(this);
    }

    private void initData() {
        DbtLog.logUtils(TAG, "initData()");

        handler = new MyHandler(this);
        service = new SayHiService(getActivity(), handler);
        //设置时间
        calendar = Calendar.getInstance();
        yearr = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            aday = "0" + day;
        } else {
            aday = Integer.toString(day);
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");// 把选择控件也设置成系统时间
        Date date = calendar.getTime();
        ifminedate = sDateFormat.format(date);

        // 获取参数
        Bundle bundle = getArguments();
        seeFlag = FunUtil.isBlankOrNullTo(bundle.getString("seeFlag"), "");
        String visitKey = bundle.getString("visitKey");
        String termId = bundle.getString("termId");

        // 初始相应终端信息
        termInfo = service.findTermById(termId);

        // 初始化拜访主表信息
        Long time = new Date().getTime();
        visitM = service.findVisitById(visitKey);
        Long time1 = new Date().getTime();
        Log.e("Optimization", "查找巡店拜访表" + (time1 - time));
        if (visitM != null) {
            // <Status 拜访状态>  //进行判断(如果Status拜访状态为1 或者 拜访状态不为空设置为选中状态)
            if (ConstValues.FLAG_1.equals(visitM.getStatus()) || CheckUtil.isBlankOrNull(visitM.getStatus())) {
                visitStatusSw.setStatus(true);// 是否有效拜访
            } else {//否则为未选中状态
                visitStatusSw.setStatus(false);
            }

            // 当我品店招选中的时候,显示对应的控件 文字
            if (ConstValues.FLAG_1.equals(termInfo.getIfmine())) {
                mineproductSW.setStatus(true);
                mineProductTime.setVisibility(View.VISIBLE);
                mineProductTime.setText(termInfo.getIfminedate());
                sayhi_text.setVisibility(View.VISIBLE);

            } else {
                //当店招未选中的时候,隐藏对应的控件 文字
                mineproductSW.setStatus(false);
                mineProductTime.setText(ifminedate);
                mineProductTime.setVisibility(View.GONE);
                sayhi_text.setVisibility(View.GONE);
            }

            //<isself> // 销售产品范围我品
            mineCb.setChecked(ConstValues.FLAG_1.equals(visitM.getIsself()) ? true : false);
            //销售产品状态   :  iscmp是否为竞品,是1 否0
            vieCb.setChecked(ConstValues.FLAG_1.equals(visitM.getIscmp()) ? true : false);
            //vieProtocolCb:终端合作状态(SELFTREATY 我品合作状态) // 我品合作状态的值从终端表获取 ywm20160706
            //mineProtocolCb.setChecked(ConstValues.FLAG_1 .equals(visitM.getSelftreaty()) ? true : false);
            mineProtocolCb.setChecked(ConstValues.FLAG_1.equals(termInfo.getSelftreaty()) ? true : false);
            mineProtocolCb.setTag(termInfo.getSelftreaty());
            //vieProtocolCb:终端合作状态选择 (cmptreaty竞品合作状态)
            vieProtocolCb.setChecked(ConstValues.FLAG_1.equals(termInfo.getCmpselftreaty()) ? true : false);
            vieProtocolCb.setTag(termInfo.getCmpselftreaty());
            visitPersonEt.setText(visitM.getVisituser());
        }
        Long time2 = new Date().getTime();
        Log.e("Optimization", "巡店拜访赋值" + (time2 - time1));

        Long time3 = new Date().getTime();
        Log.e("Optimization", "查找终端数据" + (time3 - time2));
        if (termInfo != null) {

            // 保留修改关的拜访顺序，用于判定是不更改同线路下的各终端的拜访顺序
            prevSequence = termInfo.getSequence();
            termCodeTv.setText(termInfo.getTerminalcode());
            termNameEt.setText(termInfo.getTerminalname());
            addressEt.setText(termInfo.getAddress());
            linkmanEt.setText(termInfo.getContact());
            telEt.setText(termInfo.getMobile());
            sequenceEt.setText(termInfo.getSequence());
            cycleEt.setText(termInfo.getCycle());
            Long tvolume = 0l;
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfo.getHvolume()))) {
                hvolumeEt.setHint(FunUtil.isNullToZero(termInfo.getHvolume()));
            } else {
                hvolumeEt.setText(FunUtil.isNullToZero(termInfo.getHvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((termInfo.getHvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfo.getMvolume()))) {
                mvolumeEt.setHint(FunUtil.isNullToZero(termInfo.getMvolume()));
            } else {
                mvolumeEt.setText(FunUtil.isNullToZero(termInfo.getMvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((termInfo.getMvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfo.getPvolume()))) {
                pvolumeEt.setHint(FunUtil.isNullToZero(termInfo.getPvolume()));
            } else {
                pvolumeEt.setText(FunUtil.isNullToZero(termInfo.getPvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero((termInfo.getPvolume()));
            }
            if (ConstValues.FLAG_0.equals(FunUtil.isNullToZero(termInfo.getLvolume()))) {
                lvolumeEt.setHint(FunUtil.isNullToZero(termInfo.getLvolume()));
            } else {
                lvolumeEt.setText(FunUtil.isNullToZero(termInfo.getLvolume()));
                tvolume = tvolume + FunUtil.isNullSetZero(termInfo.getLvolume());
            }
            tvolumeEt.setText(String.valueOf(tvolume));

            // 所属线路
            belongLineSp.setTag(termInfo.getRoutekey());
            SpinnerKeyValueAdapter adapter = new SpinnerKeyValueAdapter(
                    getActivity(), ConstValues.lineLst, new String[]{
                    "routekey", "routename"}, termInfo.getRoutekey());
            belongLineSp.setAdapter(adapter);

            // 区域类型
            SpinnerKeyValueAdapter termAreaAdapter = new SpinnerKeyValueAdapter(
                    getActivity(), ConstValues.dataDicMap.get("areaTypeLst"),
                    new String[]{"key", "value"}, termInfo.getAreatype());
            areaTypeSp.setAdapter(termAreaAdapter);

            // 老板老板娘
            SpinnerKeyValueAdapter laobanAdapter = new SpinnerKeyValueAdapter(
                    getActivity(), ConstValues.dataDicMap.get("visitPositionLst"),
                    //new String[] { "key", "value" }, "66AA9D3A55374232891C964350610927");// 默认的值是key
                    //new String[] { "key", "value" }, TextUtils.isEmpty(FunUtil.isNullSetSpace(visitM.getVisitposition()))?
                    new String[]{"key", "value"}, TextUtils.isEmpty(visitM.getVisitposition()) ?
                    "-1" : visitM.getVisitposition());// 默认的值是key,-1:请选择
            laobanSp.setAdapter(laobanAdapter);

            // 终端等级
            levelSp.setTag(termInfo.getTlevel());
            SpinnerKeyValueAdapter termTypeAdapter = new SpinnerKeyValueAdapter(
                    getActivity(), ConstValues.dataDicMap.get("levelLst"),
                    new String[]{"key", "value"}, termInfo.getTlevel());
            levelSp.setAdapter(termTypeAdapter);

            // 销售渠道
            sellChannelSp.setTag(termInfo.getSellchannel());
            mainChannelSp.setTag(termInfo.getMainchannel());
            minorChannelSp.setTag(termInfo.getMinorchannel());
            SpinnerKeyValueAdapter sellChannelAdapter = new SpinnerKeyValueAdapter(
                    getActivity(),
                    getSellChannelList(ConstValues.dataDicMap
                            .get("sellChannelLst")), new String[]{"key",
                    "value"}, termInfo.getSellchannel());
            sellChannelSp.setAdapter(sellChannelAdapter);

            // 获取省市县数据
            SpinnerKeyValueAdapter provAdapter = new SpinnerKeyValueAdapter(
                    getActivity(), ConstValues.provLst, new String[]{"key",
                    "value"}, termInfo.getProvince());
            provSp.setAdapter(provAdapter);

            // 直接设置终端的省市县(解决切换跳转反应慢)
            provTv.setText(service.getAreaName(termInfo.getProvince()));
            cityTv.setText(service.getAreaName(termInfo.getCity()));
            countryTv.setText(service.getAreaName(termInfo.getCounty()));

            Long time4 = new Date().getTime();
            Log.e("Optimization", "给终端数据赋值" + (time4 - time3));
        }
    }

    /**
     * 监听页面单选/复选按钮变化
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DbtLog.logUtils(TAG, "onCheckedChanged()");
        switch (buttonView.getId()) {

            case R.id.sayhi_cb_mine:
                if (!isChecked) {
                    mineProtocolCb.setChecked(false);
                }
                break;

            case R.id.sayhi_cb_vie:
                if (!isChecked) {
                    vieProtocolCb.setChecked(false);
                }
                break;

            case R.id.sayhi_cb_mineprotocol:
                if (isChecked) {
                    mineCb.setChecked(true);
                    mineCb.setEnabled(false);
                } else {
                    mineCb.setEnabled(true);
                }
                break;

            case R.id.sayhi_cb_vieprotocol:
                if (isChecked) {
                    vieCb.setChecked(true);
                    vieCb.setEnabled(false);
                } else {
                    vieCb.setEnabled(true);
                }
                break;

            default:
                break;
        }
    }

    @Override
    //点击对应spinner控件,对应的显示规则
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        Spinner sp = (Spinner) parent;
        if (view != null) {
            sp.setTag(((TextView) view).getHint());
        } else {
            sp.setTag("");
        }
        switch (sp.getId()) {
            case R.id.sayhi_sp_prov:
                startTime = new Date().getTime();
                KvStc prov = (KvStc) provSp.getSelectedItem();
                SpinnerKeyValueAdapter cityAdapter = new SpinnerKeyValueAdapter(
                        getActivity(), prov.getChildLst(), new String[]{"key",
                        "value"}, termInfo.getCity());
                citySp.setAdapter(cityAdapter);
                subTime = new Date().getTime() - startTime;
                DbtLog.write("SayHiFragment====解析市耗时:" + subTime);
                break;

            case R.id.sayhi_sp_city:
                startTime = new Date().getTime();
                KvStc city = (KvStc) citySp.getSelectedItem();
                SpinnerKeyValueAdapter countryAdapter = new SpinnerKeyValueAdapter(
                        getActivity(), city.getChildLst(), new String[]{"key",
                        "value"}, termInfo.getCounty());
                countrySp.setAdapter(countryAdapter);
                subTime = new Date().getTime() - startTime;
                DbtLog.write("SayHiFragment====解析县耗时:" + subTime);
                break;

            case R.id.sayhi_sp_sellChannel:
                KvStc sell = (KvStc) sellChannelSp.getSelectedItem();
                SpinnerKeyValueAdapter mainsellAdapter = new SpinnerKeyValueAdapter(
                        getActivity(), getSellChannelList(sell.getChildLst()),
                        new String[]{"key", "value"}, termInfo.getMainchannel());
                mainChannelSp.setAdapter(mainsellAdapter);
                break;

            case R.id.sayhi_sp_mainChannel:
                KvStc mainSell = (KvStc) mainChannelSp.getSelectedItem();
                SpinnerKeyValueAdapter minorSellAdapter = new SpinnerKeyValueAdapter(
                        getActivity(), getSellChannelList(mainSell.getChildLst()),
                        new String[]{"key", "value"}, termInfo.getMinorchannel());
                minorChannelSp.setAdapter(minorSellAdapter);
                break;
            case R.id.sayhi_sp_laoban1:
                KvStc laoban = (KvStc) laobanSp.getSelectedItem();
                if ("66AA9D3A55374232891C964350610930".equals(laoban.getKey())) {// 其他
                    // 其他选择  变成两栏
                    //Toast.makeText(getActivity(), "请输入", 0).show();
                    visitPersonEt.setVisibility(View.VISIBLE);
                    laobanSp.setLayoutParams(new LinearLayout.LayoutParams(0, ViewUtil.dip2px(getActivity(), 43), 4));
                    //setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));

                } else {
                    // 变成一栏
                    visitPersonEt.setVisibility(View.GONE);
                    laobanSp.setLayoutParams(new LinearLayout.LayoutParams(0, ViewUtil.dip2px(getActivity(), 43), 7));
                }
                break;

            default:
                break;
        }

    }

    @Override
    //触摸监听事件(当滑动到了底部时,)
    public boolean onTouch(View v, MotionEvent event) {
        DbtLog.logUtils(TAG, "onTouch()");
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int scrollY = v.getScrollY();
                int height = v.getHeight();
                int scrollViewMeasuredHeight = SayHiSv.getChildAt(0).getMeasuredHeight();
                if ((scrollY + height) == scrollViewMeasuredHeight) {
                    System.out.println("滑动到了底部 scrollY=" + scrollY);
                    // 隐藏软键盘
                    ViewUtil.hideSoftInputFromWindow(getActivity(), v);
                }
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 计算总容量
     */
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            Double total = 0d;
            if (telEt.getText().toString().length() > 30) {
                //Toast.makeText(getActivity(), "电话长度不能超过30位", 0).show();
                //telEt.setText(telEt.getText().toString().substring(0, 30));
            }
            if (!CheckUtil.isBlankOrNull(hvolumeEt.getText().toString())) {
                total += Double.parseDouble(hvolumeEt.getText().toString());
            }
            if (!CheckUtil.isBlankOrNull(mvolumeEt.getText().toString())) {
                total += Double.parseDouble(mvolumeEt.getText().toString());
            }
            if (!CheckUtil.isBlankOrNull(pvolumeEt.getText().toString())) {
                total += Double.parseDouble(pvolumeEt.getText().toString());
            }
            if (!CheckUtil.isBlankOrNull(lvolumeEt.getText().toString())) {
                total += Double.parseDouble(lvolumeEt.getText().toString());
            }
            if (total.intValue() != 0) {
                tvolumeEt.setText(String.valueOf(total.intValue()));
            }
        }
    };

    @Override
    //失去焦点时调用
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");
        Long time5 = new Date().getTime();

        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag))
            return;

        if (isLoadingData)
            return;

        // 终端状态为有效时才保存相关信息
        if (termStatusSw.getStatus()) {
            // 保存拜访主表信息
            if (visitStatusSw.getStatus()) {
                visitM.setStatus(ConstValues.FLAG_1);// status:1,true时为有效拜访
            } else {
                visitM.setStatus(ConstValues.FLAG_0);
            }

            // 店招
            /*if (mineproductSW.getStatus()) {
                visitM.setIfmine(ConstValues.FLAG_1);// 是否我品店招 0不是 1是
				if(mineProductTime.getText().toString()!=null&&mineProductTime.getText().toString().length()>0){
					visitM.setIfminedate(mineProductTime.getText().toString());
				}else{
					visitM.setIfminedate(ifminedate);
				}
			} else {
				visitM.setIfmine(ConstValues.FLAG_0);
			}*/

            visitM.setIsself(mineCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            visitM.setIscmp(vieCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            // 如果初始状态为选中，保存进为未选中则状态为0：流失
            if (mineProtocolCb.getTag() != null && ConstValues.FLAG_1.equals(mineProtocolCb.getTag().toString())) {
                visitM.setSelftreaty(mineProtocolCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            } else {
                visitM.setSelftreaty(mineProtocolCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            }

            if (vieProtocolCb.getTag() != null && ConstValues.FLAG_1.equals(vieProtocolCb.getTag().toString())) {
                visitM.setCmptreaty(vieProtocolCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            } else {
                visitM.setCmptreaty(vieProtocolCb.isChecked() ? ConstValues.FLAG_1 : ConstValues.FLAG_0);
            }
            visitM.setVisituser(visitPersonEt.getText().toString());

            // 当页面切换太快,页面数据(拜访对象职位)来不及初始化,导致页面数据保存时,系统崩溃
            String visitpotion = "-1";
            try {
                visitpotion = laobanSp.getTag().toString();
            } catch (Exception e) {
                Log.e(TAG, "当页面切换太快,页面数据(拜访对象职位)来不及初始化", e);
            }

            // "-1",其他 根据原先用户是什么,不做处理
            if (!("-1".equals(visitpotion) || "66AA9D3A55374232891C964350610930".equals(visitpotion))) {
                String visitpositionName = service.getVisitpositionName(visitpotion);
                visitM.setVisituser(visitpositionName);
            }
            visitM.setVisitposition(visitpotion);

            if (belongLineSp.getTag() != null && !CheckUtil.isBlankOrNull(belongLineSp.getTag().toString())) {
                visitM.setRoutekey(belongLineSp.getTag().toString());
            }
            service.updateVisit(visitM);

            // 保存终端信息
            if (termInfo != null) {
                termInfo.setTerminalname(FunUtil.isNullSetSpace(termNameEt.getText()).toString());
                termInfo.setRoutekey(FunUtil.isBlankOrNullTo(belongLineSp.getTag(), termInfo.getRoutekey()));
                /*termInfo.setProvince(FunUtil.isBlankOrNullTo(provSp.getTag(),
                        termInfo.getProvince()));
				termInfo.setCity(FunUtil.isBlankOrNullTo(citySp.getTag(),
						termInfo.getCity()));
				termInfo.setCounty(FunUtil.isBlankOrNullTo(countrySp.getTag(),
						termInfo.getCounty()));*/
                termInfo.setProvince(termInfo.getProvince());
                termInfo.setCity(termInfo.getCity());
                termInfo.setCounty(termInfo.getCounty());
                // if (citySp.getSelectedItem() != null) {
                // KvStc cityKvStc = (KvStc)citySp.getSelectedItem();
                // if ("全省".equals(cityKvStc.getValue())) {
                // termInfo.setCounty("");
                // } else {
                // termInfo.setCounty(FunUtil.isBlankOrNullTo(countrySp.getTag(),
                // termInfo.getCounty()));
                // }
                // }
                termInfo.setAddress(FunUtil.isNullSetSpace(addressEt.getText()).toString());
                termInfo.setContact(FunUtil.isNullSetSpace(linkmanEt.getText()).toString());
                if (telEt.getText().toString().length() > 30)
                    telEt.setText(telEt.getText().toString().substring(0, 30));
                termInfo.setMobile(FunUtil.isNullSetSpace(telEt.getText()).toString());
                termInfo.setTlevel(FunUtil.isBlankOrNullTo(levelSp.getTag(), termInfo.getTlevel()));
                termInfo.setSequence(FunUtil.isNullSetSpace(sequenceEt.getText()).toString());
                termInfo.setCycle(FunUtil.isNullSetSpace(cycleEt.getText()).toString());
                termInfo.setHvolume(FunUtil.isNullToZero(hvolumeEt.getText()).toString());
                termInfo.setMvolume(FunUtil.isNullToZero(mvolumeEt.getText()).toString());
                termInfo.setPvolume(FunUtil.isNullToZero(pvolumeEt.getText()).toString());
                termInfo.setLvolume(FunUtil.isNullToZero(lvolumeEt.getText()).toString());
                if (termStatusSw.getStatus()) {
                    termInfo.setStatus(ConstValues.FLAG_1);
                } else {
                    termInfo.setStatus(ConstValues.FLAG_0);
                }

                // 店招
                if (mineproductSW.getStatus()) {
                    termInfo.setIfmine(ConstValues.FLAG_1);// 是否我品店招 0不是 1是
                    if (mineProductTime.getText().toString() != null && mineProductTime.getText().toString().length() > 0) {
                        termInfo.setIfminedate(mineProductTime.getText().toString());
                    } else {
                        termInfo.setIfminedate(ifminedate);
                    }
                } else {
                    termInfo.setIfmine(ConstValues.FLAG_0);
                }

                termInfo.setSellchannel(FunUtil.isBlankOrNullTo(sellChannelSp.getTag(), termInfo.getSellchannel()));
                termInfo.setMainchannel(FunUtil.isBlankOrNullTo(mainChannelSp.getTag(), termInfo.getMainchannel()));
                termInfo.setMinorchannel(FunUtil.isBlankOrNullTo(minorChannelSp.getTag(), termInfo.getMinorchannel()));
                termInfo.setAreatype(FunUtil.isBlankOrNullTo(areaTypeSp.getTag(), termInfo.getAreatype()));
                //termInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
                termInfo.setUpdateuser(PrefUtils.getString(getActivity(), "userCode", ""));
                termInfo.setPadisconsistent(ConstValues.FLAG_0);
                termInfo.setUpdatetime(DateUtil.getDateTimeDte(1));
                // --- 更改mst_terminalinfo_m表中selftreaty字段 ywm 20160426-----------------
                termInfo.setSelftreaty(visitM.getSelftreaty());
                termInfo.setCmpselftreaty(visitM.getCmptreaty());
                // --- 更改mst_terminalinfo_m表中selftreaty字段 ywm 20160426-----------------
                service.updateTermInfo(termInfo, prevSequence);
            }
        }
        Long time6 = new Date().getTime();
        Log.e("Optimization", "保存数据" + (time6 - time5));
    }

    /**
     * 由错误提示定位焦点
     *
     * @param msgId
     */
    private void requestFocus(int msgId) {
        DbtLog.logUtils(TAG, "requestFocus()");
        switch (msgId) {
            case R.string.termadd_msg_invaltermname:
                termNameEt.requestFocus();
                break;

            case R.string.termadd_msg_invaladdress:
                addressEt.requestFocus();
                break;

            case R.string.termadd_msg_invalcontact:
                linkmanEt.requestFocus();
                break;
            case R.string.termadd_msg_invalsequence:
                sequenceEt.requestFocus();
                break;

            default:
                break;
        }
    }

    private void requestMotionEvent() {
        MotionEvent event = MotionEvent.obtain(
                SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, termStatusSw.getLeft(),
                termStatusSw.getTop(), termStatusSw.getId());
        termStatusSw.onTouchEvent(event);
    }


    //我品店招打开按钮打开状态下,时间控件的点击事件
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sayhi_btn_time:
                DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(),R.style.dialog_date,
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
                                ifminedate = (Integer.toString(year) + "-"
                                        + String.format("%02d", monthOfYear + 1)
                                        + "-" + aday);
                                // ifminedate = (Integer.toString(year) +
                                // String.format("%02d", monthOfYear + 1) + aday);
                                mineProductTime.setText(ifminedate);
                            }
                        }, yearr, month, day);
                if (!dateDialog.isShowing()) {
                    dateDialog.show();
                }

                break;

            default:
                break;
        }

    }

    @Override
    //自定义按钮控件状态发生改变时的事件
    public void onSwitchChanged(SlideSwitch obj, int status) {
        DbtLog.logUtils(TAG, "onSwitchChanged()");
        /*
         * if(status==SlideSwitch.SWITCH_OFF){ service.dialogInValidTerm(visitM,
		 * termInfo,seeFlag); }
		 */

        switch (obj.getId()) {
            //是否有效终端按钮
        /*case R.id.sayhi_sw_termstatus:
            if (status == SlideSwitch.SWITCH_OFF) {
				service.dialogInValidTerm(visitM, termInfo, seeFlag);
			}
			break;
		//是否我品店招
		case R.id.sayhi_sw_wopindianzhao:
			if (status == SlideSwitch.SWITCH_OFF) {
				mineProductTime.setVisibility(View.GONE);
				sayhi_text.setVisibility(View.GONE);
			} else {
				mineProductTime.setVisibility(View.VISIBLE);
				sayhi_text.setVisibility(View.VISIBLE);
			}
			break;*/


        }
    }

    /***
     * 获取渠道集合
     *
     * @param kvStcList
     * @return
     */
    private List<KvStc> getSellChannelList(List<KvStc> kvStcList) {
        List<KvStc> list = new ArrayList<KvStc>();
        for (KvStc kvStc : kvStcList) {
            if (!DbtUtils.getInvalidChannelList().contains(kvStc.getKey())) {
                list.add(kvStc);
            }
        }
        return list;
    }

    @Override
    public void onLongSwitchChanged(LongSlideSwitch obj, int status) {
        switch (obj.getId()) {
            //是否有效终端按钮
            case R.id.sayhi_sw_termstatus:
                if (status == SlideSwitch.SWITCH_OFF) {
                    service.dialogInValidTerm(visitM, termInfo, seeFlag);
                }
                break;
            //是否我品店招
            case R.id.sayhi_sw_wopindianzhao:
                if (status == SlideSwitch.SWITCH_OFF) {
                    mineProductTime.setVisibility(View.GONE);
                    sayhi_text.setVisibility(View.GONE);
                } else {
                    mineProductTime.setVisibility(View.VISIBLE);
                    sayhi_text.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
