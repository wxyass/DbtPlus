package et.tsingtaopad.home;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.business.DateUtils;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.CmmBoardM;
import et.tsingtaopad.db.tables.MstMonthtargetInfo;
import et.tsingtaopad.db.tables.MstVisitmemoInfo;
import et.tsingtaopad.home.service.HomeService;
import et.tsingtaopad.initconstvalues.InitConstValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.visit.memorybook.MemoDialogFragment;
import et.tsingtaopad.visit.memorybook.MemoDialogService;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.TerminalName;

public class HomeFragment extends BaseFragmentSupport implements
        OnClickListener, OnItemClickListener, OnCheckedChangeListener {
    private static int[] COLORS = new int[] { R.color.home_et, R.color.home_sp,
            R.color.home_ot, R.color.home_mt, R.color.home_tt, R.color.home_op,
            R.color.home_channel_c9f8dfa, R.color.home_channel_c10b594,
            R.color.home_channel_cb00c52 };
    private static int[] drawables = new int[] {
            R.drawable.bg_orange_yellow_circle, R.drawable.bg_red_circle,
            R.drawable.bg_blue_circle, R.drawable.bg_green_circle,
            R.drawable.bg_purple_circle, R.drawable.bg_black_circle,
            R.drawable.bg_cfdfa_circle, R.drawable.bg_cb_circle,
            R.drawable.bg_cbc_circle };
    private CategorySeries mSeries;
    private DefaultRenderer mRenderer;
    private GraphicalView mChartView;
    private ListView homeListView;
    private TextView home_weather_tv;
    private TextView home_weather_temperature;
    private TextView home_weather_date;
    private TextView home_weather_weekly;
    private TextView home_listview_promotion;
    private TextView home_termnum;//终端数量
    private ImageView home_weather_img;
    private List<String> weather;
    private Dao<CmmBoardM, String> cmmBoardMDao;
    private Dao<MstMonthtargetInfo, String> mstMonthtargetInfoDao;
    private Dao<MstVisitmemoInfo, String> mstVisitmemoInfoDao;
    private Button board_btn;
    private Button todaything_btn;
    private Button month_target_btn;
    private int whatIsCheck = 0;
    private List<List<HomeListViewStc>> homeListDatas = new ArrayList<List<HomeListViewStc>>();
    private HomeListViewAdapter homeListViewAdapter;
    private List<KvStc> sellChannelLst;
	List<KvStc> sellChannelLst1=new ArrayList<KvStc>();
    private LinearLayout channel_analyze_llayout;
    private LayoutInflater inflater;
    private String[] stringValues;// 百分比
    private double[] doubleValues;// 渠道数
    private int[] string_double_colors;// 对应的颜色值
    private int drawableLength = drawables.length;// 和COLORS的长度是一样的
    private Map<String, Integer> querySellchannelNum;
    private HomeService homeService;
    private boolean iswarn=false;//备忘录提醒
    private final String TAG = "HomeFragment";
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Date datetime = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(datetime);
            // 获取星期几
            int i = calendar.get(Calendar.DAY_OF_WEEK);
            String wekNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
                    "星期六" };
            home_weather_weekly.setText(wekNames[i - 1]);
            home_weather_date.setText(DateUtil.formatDate(datetime, "MM月dd日"));

            // 天气预报(通过第三方接口获取)
            // if (!CheckUtil.IsEmpty(weather)) {
            // if (!isWeatherStr(weather.toString())) {
            // try {
            // String date = weather.get(4);// 获取日期
            // String tempture = weather.get(5);// 获取温度
            // String date_weather = weather.get(6);// 获取天气预报
            // String weather_icon = weather.get(8);// 获取天气预报图标.
            // int imageId = homeService.getImageId(weather_icon);
            // String[] dateAndweather = date_weather.split(" ");
            // Date datetime = DateUtil.parse(date);
            // Calendar calendar = Calendar.getInstance();
            // calendar.setTime(datetime);
            // // 获取星期几
            // int i = calendar.get(Calendar.DAY_OF_WEEK);
            // String wekNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
            // "星期六" };
            // home_weather_weekly.setText(wekNames[i - 1]);
            // home_weather_date.setText(dateAndweather[0]);
            // home_weather_tv.setText(dateAndweather[1]);
            // home_weather_temperature.setText(tempture.replace("/", "~"));
            // if (imageId != 0) {
            // home_weather_img.setImageDrawable(getResources().getDrawable(imageId));
            // }
            // } catch (Exception e) {
            // Log.e(TAG, "天气预报数据有异常:" + weather.toString().toString(), e);
            // }
            // }
            // }
        }
    };

    @Override
    public void onCreate(Bundle savedState) {

        super.onCreate(savedState);
        DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
        try {
            cmmBoardMDao = helper.getCmmBoardMDao();
            mstMonthtargetInfoDao = helper.getMstMonthtargetInfoDao();
            mstVisitmemoInfoDao = helper.getMstVisitmemoInfoDao();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        homeService = new HomeService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedState) {

        View view = inflater.inflate(R.layout.home_page_fragment, container,
                false);
        this.inflater = inflater;
        this.initView(view);
        initData();
        initViewDate();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChartView != null) {
            mChartView.repaint();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * @param view
     */
    private void initView(View view) {
        homeListViewAdapter = new HomeListViewAdapter(getActivity(),
                R.layout.home_listview_item);
        seriousLayout = (RelativeLayout) view.findViewById(R.id.chart);
        homeListView = (ListView) view.findViewById(R.id.home_listView);
        home_weather_tv = (TextView) view.findViewById(R.id.home_weather_tv);
        home_weather_temperature = (TextView) view
                .findViewById(R.id.home_weather_temperature);
        home_weather_date = (TextView) view
                .findViewById(R.id.home_weather_date);
        home_weather_weekly = (TextView) view
                .findViewById(R.id.home_weather_weekly);
        home_listview_promotion = (TextView) view
                .findViewById(R.id.home_listview_promotion);
        home_weather_img = (ImageView) view.findViewById(R.id.home_weather_img);
        board_btn = (Button) view.findViewById(R.id.board_btn);
        todaything_btn = (Button) view.findViewById(R.id.todaything_btn);
        month_target_btn = (Button) view.findViewById(R.id.month_target_btn);
        channel_analyze_llayout = (LinearLayout) view
                .findViewById(R.id.channel_analyze_llayout);
        
        home_termnum = (TextView) view.findViewById(R.id.home_termnum);
        
        board_btn.setOnClickListener(this);
        todaything_btn.setOnClickListener(this);
        month_target_btn.setOnClickListener(this);
        homeListView.setOnItemClickListener(this);

    }

    /**
     * 
     */
    private void initViewDate() {
        // 计算定格下的各渠道百分比
        mSeries = new CategorySeries("");
        mRenderer = new DefaultRenderer();
        mRenderer.setStartAngle(180);
        mRenderer.setZoomEnabled(false);
        mRenderer.setPanEnabled(false);// 禁止拖动
        mRenderer.setShowLabels(true);// 显示上面文字信息
        mRenderer.setLabelsTextSize(20);
        mRenderer.setLabelsColor(getResources().getColor(
                android.R.color.darker_gray));
        mRenderer.setShowLegend(false);// 禁止显示下面文字
        mRenderer.setZoomRate(5);
        mRenderer.setZoomEnabled(false);

        for (int i = 0; i < stringValues.length; i++) {
            mSeries.add(stringValues[i], doubleValues[i]);
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(getResources().getColor(string_double_colors[i]));
            mRenderer.addSeriesRenderer(renderer);
        }
//<<<<<<< .mine
//        mChartView = ChartFactory.getPieChartView(getActivity(), mSeries,
//                mRenderer);
//        // 老pad的
//        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
//                340, 340);
        // 新pad的
        // RelativeLayout.LayoutParams params1 = new
        // RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
        // LayoutParams.WRAP_CONTENT);
        mChartView = ChartFactory.getPieChartView(getActivity(), mSeries, mRenderer);
        /**
         * 去除饼图外圈
        DisplayMetrics dm = getResources().getDisplayMetrics();  
        int screenWidth = dm.widthPixels;  
        RelativeLayout.LayoutParams params1;
        if(screenWidth==800){
            //新pad的
            params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }else {
            //老pad的
            params1 = new RelativeLayout.LayoutParams(340, 340);
        }
>>>>>>> .r410
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);

        seriousLayout.addView(mChartView, params1);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params2.topMargin = 43;
<<<<<<< .mine

=======
>>>>>>> .r410
        ImageView imageView1 = new ImageView(getActivity());
        imageView1.setBackgroundResource(R.drawable.bg_fristpage_circle_middle);
        seriousLayout.addView(imageView1, params2);
         */
        seriousLayout.addView(mChartView);
        mChartView.repaint();
        if (mChartView != null) {
            mChartView.repaint();
        }
        homeListView.setAdapter(homeListViewAdapter);
        if (!CheckUtil.IsEmpty(homeListDatas)) {
            List<HomeListViewStc> list = homeListDatas.get(0);
            if (!CheckUtil.IsEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    homeListViewAdapter.add(list.get(i));
                }
                homeListViewAdapter.notifyDataSetChanged();
                home_listview_promotion.setVisibility(View.GONE);
                homeListView.setVisibility(View.VISIBLE);
            } else {
                home_listview_promotion.setVisibility(View.VISIBLE);
                homeListView.setVisibility(View.GONE);
            }

        }

        channel_analyze_llayout.removeAllViews();
        if (!CheckUtil.IsEmpty(sellChannelLst)) {
        
       
    
            int channel_size = sellChannelLst.size();
            int remainder = channel_size % 3;
            int forSize = remainder == 0 ? (channel_size / 3)
                    : channel_size / 3 + 1;

            for (int i = 0; i < forSize; i++) {
                View view = inflater.inflate(R.layout.homelineartv, null);
                TextView home_channel_tv0 = (TextView) view
                        .findViewById(R.id.home_channel_tv0);
                TextView home_channel_tv1 = (TextView) view
                        .findViewById(R.id.home_channel_tv1);
                TextView home_channel_tv2 = (TextView) view
                        .findViewById(R.id.home_channel_tv2);
                // 0 1,2 i=0
                // 3,4,5 i=1
                // 6, i=2
                // 6,7 i=2
                if (remainder == 0) {
                    int position0 = i * 3;
                    int position1 = i * 3 + 1;
                    int position2 = i * 3 + 2;

                    KvStc kvStc0 = sellChannelLst.get(position0);
                    KvStc kvStc1 = sellChannelLst.get(position1);
                    KvStc kvStc2 = sellChannelLst.get(position2);

                    Drawable drawable0 = getResources().getDrawable(
                            drawables[position0 % drawableLength]);
                    Drawable drawable1 = getResources().getDrawable(
                            drawables[position1 % drawableLength]);
                    Drawable drawable2 = getResources().getDrawable(
                            drawables[position2 % drawableLength]);
                    drawable0.setBounds(0, 0, drawable0.getMinimumWidth(),
                            drawable0.getMinimumHeight());
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
                            drawable1.getMinimumHeight());
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),
                            drawable2.getMinimumHeight());

                    
                    home_channel_tv1.setCompoundDrawables(drawable1, null,
                            null, null);
                    home_channel_tv2.setCompoundDrawables(drawable2, null,
                            null, null);

          home_channel_tv0.setText(kvStc0.getValue());
                    home_channel_tv1.setText(kvStc1.getValue());
                    home_channel_tv2.setText(kvStc2.getValue());

                } else {
                    if (i != forSize - 1) {
                        int position0 = i * 3;
                        int position1 = i * 3 + 1;
                        int position2 = i * 3 + 2;

                        KvStc kvStc0 = sellChannelLst.get(position0);
                        KvStc kvStc1 = sellChannelLst.get(position1);
                        KvStc kvStc2 = sellChannelLst.get(position2);

                        Drawable drawable0 = getResources().getDrawable(
                                drawables[position0 % drawableLength]);
                        Drawable drawable1 = getResources().getDrawable(
                                drawables[position1 % drawableLength]);
                        Drawable drawable2 = getResources().getDrawable(
                                drawables[position2 % drawableLength]);
                        drawable0.setBounds(0, 0, drawable0.getMinimumWidth(),
                                drawable0.getMinimumHeight());
                        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
                                drawable1.getMinimumHeight());
                        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),
                                drawable2.getMinimumHeight());

                        home_channel_tv0.setCompoundDrawables(drawable0, null,
                                null, null);
                        home_channel_tv1.setCompoundDrawables(drawable1, null,
                                null, null);
                        home_channel_tv2.setCompoundDrawables(drawable2, null,
                                null, null);
                        home_channel_tv0.setText(kvStc0.getValue());
                        home_channel_tv1.setText(kvStc1.getValue());
                        home_channel_tv2.setText(kvStc2.getValue());
                    } else {
                        if (remainder == 1) {
                            int position0 = i * 3;
                            KvStc kvStc0 = sellChannelLst.get(position0);
                            Drawable drawable0 = getResources().getDrawable(
                                    drawables[position0 % drawableLength]);
                            drawable0.setBounds(0, 0,
                                    drawable0.getMinimumWidth(),
                                    drawable0.getMinimumHeight());
                            home_channel_tv0.setCompoundDrawables(drawable0,
                                    null, null, null);
                            home_channel_tv0.setText(kvStc0.getValue());
                        } else if (remainder == 2) {
                            int position0 = i * 3;
                            int position1 = i * 3 + 1;

                            KvStc kvStc0 = sellChannelLst.get(position0);
                            KvStc kvStc1 = sellChannelLst.get(position1);

                            Drawable drawable0 = getResources().getDrawable(
                                    drawables[position0 % drawableLength]);
                            Drawable drawable1 = getResources().getDrawable(
                                    drawables[position1 % drawableLength]);
                            drawable0.setBounds(0, 0,
                                    drawable0.getMinimumWidth(),
                                    drawable0.getMinimumHeight());
                            drawable1.setBounds(0, 0,
                                    drawable1.getMinimumWidth(),
                                    drawable1.getMinimumHeight());

                            home_channel_tv0.setCompoundDrawables(drawable0,
                                    null, null, null);
                            home_channel_tv1.setCompoundDrawables(drawable1,
                                    null, null, null);
                            home_channel_tv0.setText(kvStc0.getValue());
                            home_channel_tv1.setText(kvStc1.getValue());
                        }

                    }

                }
                channel_analyze_llayout.addView(view);
            }
        }
        
    }
    private boolean isWeatherStr(String weatherDetail) {
        return weatherDetail.contains("高速访问被限制")
                || weatherDetail.contains("免费用户不能使用高速访问")
                || weatherDetail.contains("免费用户24小时内")
                || weatherDetail.contains("超过规定数量")
                || weatherDetail.contains("系统维护")
                || weatherDetail.contains("服务暂停");
    }

    /**
     * 
     */
    private void initData() {

        try {
            // 查询渠道有多少个终端 并按渠道分组
            whatIsCheck = 0;
            // 获取渠道列表 不能删 ManinActivity 在初始化的时候 没有执行完 此处执行
            // ConstValues.dataDicMap.get("sellChannelLst"); 获取渠道会有可能出空指针异常
            InitConstValues initConstValues = new InitConstValues(getActivity());
            initConstValues.initDataDictionary();
            sellChannelLst = ConstValues.dataDicMap.get("sellChannelLst");
            sellChannelLst1 = sellChannelLst;
            Iterator<KvStc>iterator = sellChannelLst.iterator();
            if (!CheckUtil.IsEmpty(sellChannelLst)) {
            	//89F32F77BDAD414E849966B95E5D3055// 待定
            	while(iterator.hasNext()) {  
           		KvStc it=iterator.next();
            	   if(it.getKey().equals("89F32F77BDAD414E849966B95E5D3055")) 
            	  
            	  iterator.remove();
            	   
            	}  }
            	
            if (!CheckUtil.IsEmpty(sellChannelLst)) {
                KvStc kvStc0 = sellChannelLst.get(0);// 判断第一个是否是不是 请选择 并把它移除
                if ("-1".equals(kvStc0.getKey())
                        || "请选择".equals(kvStc0.getValue())) {
                    sellChannelLst.remove(0);
                }
            }
            Map<String, String> channelKey = new HashMap<String, String>();
            for (int i = 0; i < sellChannelLst.size(); i++) {
                String key = sellChannelLst.get(i).getKey();
                channelKey.put(key, key);
            }
            querySellchannelNum = homeService.querySellchannelNum(channelKey);

            if (CheckUtil.IsEmpty(sellChannelLst)) {
                stringValues = new String[] { "0%" };
                doubleValues = new double[] { 1 };
                string_double_colors = new int[] { R.color.home_ot };
            } else {
                int totalChannel = 0;
                if (!CheckUtil.IsEmpty(querySellchannelNum)) {
                    totalChannel = querySellchannelNum.get("Total");
                }
                if (totalChannel != 0) {
                    int size = querySellchannelNum.size();
                    int channelCount = size - 1;// 有实际数据的渠道的个数 map存放渠道对应的id和对应数量
                                                // 其中map一个是总数 不属于渠道id
                    stringValues = new String[channelCount];
                    doubleValues = new double[channelCount];
                    string_double_colors = new int[channelCount];
                    NumberFormat numberFormat = NumberFormat
                            .getPercentInstance();
                    numberFormat.setMinimumFractionDigits(2);
                    int doubleValuePos = 0;
                    for (int i = 0; i < sellChannelLst.size(); i++) {
                        KvStc kvStc = sellChannelLst.get(i);
                        // 计算百分比
                        Integer integer = querySellchannelNum.get(kvStc
                                .getKey());
                        if (integer != null) {
                            int valuePos = doubleValuePos++;
                            float sellChanneNum = integer;
//                            DecimalFormat df = new DecimalFormat("0.00");
//                            double d = 123.9078; 
//                            double db = df.format(d);
//                            则db=123.90；
                            String percent = numberFormat.format(sellChanneNum
                                    / totalChannel);
                            Log.i("percent", percent + "----" + integer);
                            stringValues[valuePos] = percent;
                            doubleValues[valuePos] = sellChanneNum;
                            string_double_colors[valuePos] = COLORS[i
                                    % drawableLength];
                        }

                    }
                } else {
                    stringValues = new String[] { "0%" };
                    doubleValues = new double[] { 1 };
                    string_double_colors = new int[] { R.color.home_ot };
                }
            }

            // 底部listview数据
            QueryBuilder<CmmBoardM, String> cmmboardbuilder = cmmBoardMDao
                    .queryBuilder();
            cmmboardbuilder.orderBy("updatetime", false);
            QueryBuilder<MstVisitmemoInfo, String> visitmemoInfobuilder = mstVisitmemoInfoDao
                    .queryBuilder();
            Where<MstVisitmemoInfo, String> where=visitmemoInfobuilder.where();
            String nowDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
            where.le("startdate", nowDate).and().ge("enddate", nowDate);
            visitmemoInfobuilder.orderBy("updatetime", false);
            
            QueryBuilder<MstMonthtargetInfo, String> monthtargetInfobuilder = mstMonthtargetInfoDao
                    .queryBuilder();
            monthtargetInfobuilder.orderBy("updatetime", false);

            List<CmmBoardM> cmmboards = cmmboardbuilder.query();
            List<MstVisitmemoInfo> visitmemoInfos = visitmemoInfobuilder
                    .query();
            List<MstMonthtargetInfo> monthtargetInfos = monthtargetInfobuilder
                    .query();
            String format = "yyyy-MM-dd HH:mm";
            homeListDatas.clear();
            // 通知公告0
            if (!CheckUtil.IsEmpty(cmmboards)) {
                List<HomeListViewStc> home_boards = new ArrayList<HomeListViewStc>();
                for (int i = 0; i < cmmboards.size(); i++) {

                    CmmBoardM cmmBoardM = cmmboards.get(i);
                    String startDate = DateUtils.divide(cmmBoardM
                            .getStartdate());
                    String endDate = DateUtils.divide(cmmBoardM.getEnddate());// 计划日期转成yyyy-MM-dd的形式
                    HomeListViewStc board = new HomeListViewStc();
                    board.setKey(cmmBoardM.getMessageid());
                    board.setTitle(cmmBoardM.getMesstitle());
                    board.setContent(cmmBoardM.getMessagedesc());//
                    board.setStartDate(startDate);
                    board.setEndDate(endDate);
                    Date date = cmmBoardM.getUpdatetime();
                    String formatDate = DateUtil.formatDate(date, format);
                    board.setUpdateTime(formatDate);
                    home_boards.add(board);
                }
                homeListDatas.add(home_boards);
            } else {
                homeListDatas.add(null);
            }

            // 今日要事（客情备忘录）1
            if (!CheckUtil.IsEmpty(visitmemoInfos)) {
                List<HomeListViewStc> memoInfos = new ArrayList<HomeListViewStc>();
                for (int i = 0; i < visitmemoInfos.size(); i++) {
                    MstVisitmemoInfo visitMemoInfo = visitmemoInfos.get(i);
                    HomeListViewStc memoInfo = new HomeListViewStc();
                    memoInfo.setTerminalKey(visitMemoInfo.getTerminalkey());
                    memoInfo.setKey(visitMemoInfo.getMemokey());
                    memoInfo.setTitle(visitMemoInfo.getContent());
                    memoInfo.setContent(visitMemoInfo.getContent());
                    String startDate = DateUtils.divide(visitMemoInfo
                            .getStartdate());
                    String endDate = DateUtils.divide(visitMemoInfo
                            .getEnddate());// 计划日期转成yyyy-MM-dd的形式
                    memoInfo.setStartDate(startDate);
                    memoInfo.setEndDate(endDate);
                    Date date = visitMemoInfo.getUpdatetime();
                    String formatDate = DateUtil.formatDate(date, format);
                    memoInfo.setUpdateTime(formatDate);
                    if("1".equals(visitMemoInfo.getIswarn())){
                        iswarn=true;
                    }
                    memoInfos.add(memoInfo);
                }
                homeListDatas.add(memoInfos);
            } else {
                homeListDatas.add(null);
            }
            // 月目标 2
            if (!CheckUtil.IsEmpty(monthtargetInfos)) {
                List<HomeListViewStc> monthTargets = new ArrayList<HomeListViewStc>();

                for (int i = 0; i < monthtargetInfos.size(); i++) {
                    MstMonthtargetInfo mstMonthtargetInfo = monthtargetInfos
                            .get(i);
                    HomeListViewStc monthtarget = new HomeListViewStc();
                    monthtarget.setKey(mstMonthtargetInfo.getTargetkey());
                    monthtarget.setContent(mstMonthtargetInfo
                            .getTargetcontent());
                    Date date = mstMonthtargetInfo.getUpdatetime();
                    String formatDate = DateUtil.formatDate(date, format);
                    monthtarget.setTitle(mstMonthtargetInfo.getTargetcontent());
                    monthtarget.setUpdateTime(formatDate);
                    monthTargets.add(monthtarget);
                }
                homeListDatas.add(monthTargets);
            } else {
                homeListDatas.add(null);
            }

            handler.sendEmptyMessage(100);
            if(iswarn && ConstValues.isDayThingWarn){
                new  AlertDialog.Builder(getActivity())    
                .setTitle("温馨提醒" )
                .setMessage("今日有备忘录,请查看 今日要事" )
                .setPositiveButton("确定" ,  null ).show();  
            }
            ConstValues.isDayThingWarn=false;
            // 通过第三方接口获取天气预报
            // new Thread() {
            //
            // @Override
            // public void run() {
            // try {
            // weather = homeService.getWeather("北京");
            // handler.sendEmptyMessage(100);
            // } catch (HttpResponseException e) {
            // e.printStackTrace();
            // } catch (IOException e) {
            // e.printStackTrace();
            // } catch (XmlPullParserException e) {
            // e.printStackTrace();
            // }
            // }
            // }.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 查询有多少家终端
        Long termnum = homeService.getTermNum();
        home_termnum.setText("终端数量 : "+termnum+" 家");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.board_btn:
            if (whatIsCheck != 0) {
                whatIsCheck = 0;
                board_btn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btns_tztg_lh_down));
                todaything_btn.setBackgroundDrawable(getResources()
                        .getDrawable(R.drawable.btns_jrys_lh));
                month_target_btn.setBackgroundDrawable(getResources()
                        .getDrawable(R.drawable.btns_ymb_lh));
                if (!CheckUtil.IsEmpty(homeListDatas)) {
                    List<HomeListViewStc> list = homeListDatas.get(0);
                    if (homeListViewAdapter != null) {
                        homeListViewAdapter.clear();
                    }
                    if (!CheckUtil.IsEmpty(list)) {
                        for (int i = 0; i < list.size(); i++) {
                            homeListViewAdapter.add(list.get(i));
                        }
                        homeListViewAdapter.notifyDataSetChanged();
                        home_listview_promotion.setVisibility(View.GONE);
                        homeListView.setVisibility(View.VISIBLE);
                    } else {
                        home_listview_promotion.setVisibility(View.VISIBLE);
                        homeListView.setVisibility(View.GONE);
                    }
                } else {
                    home_listview_promotion.setVisibility(View.VISIBLE);
                    homeListView.setVisibility(View.GONE);
                }
            }
            break;
        case R.id.todaything_btn:
            if (whatIsCheck != 1) {
                whatIsCheck = 1;
                board_btn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btns_tztg_lh));
                todaything_btn.setBackgroundDrawable(getResources()
                        .getDrawable(R.drawable.btns_jrys_lh_down));
                month_target_btn.setBackgroundDrawable(getResources()
                        .getDrawable(R.drawable.btns_ymb_lh));
                if (homeListViewAdapter != null) {
                    homeListViewAdapter.clear();
                }
                if (!CheckUtil.IsEmpty(homeListDatas)
                        && homeListDatas.size() >= 2) {
                    List<HomeListViewStc> list = homeListDatas.get(1);

                    if (!CheckUtil.IsEmpty(list)) {
                        for (int i = 0; i < list.size(); i++) {
                            homeListViewAdapter.add(list.get(i));
                        }
                        homeListViewAdapter.notifyDataSetChanged();
                        home_listview_promotion.setVisibility(View.GONE);
                        homeListView.setVisibility(View.VISIBLE);
                    } else {
                        home_listview_promotion.setVisibility(View.VISIBLE);
                        homeListView.setVisibility(View.GONE);
                    }

                } else {
                    home_listview_promotion.setVisibility(View.VISIBLE);
                    homeListView.setVisibility(View.GONE);
                }
            }
            break;
        case R.id.month_target_btn:
            if (whatIsCheck != 2) {
                whatIsCheck = 2;
                board_btn.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btns_tztg_lh));
                todaything_btn.setBackgroundDrawable(getResources()
                        .getDrawable(R.drawable.btns_jrys_lh));
                month_target_btn.setBackgroundDrawable(getResources()
                        .getDrawable(R.drawable.btns_ymb_lh_down));
                if (homeListViewAdapter != null) {
                    homeListViewAdapter.clear();
                }
                if (!CheckUtil.IsEmpty(homeListDatas)
                        && homeListDatas.size() > 2) {
                    List<HomeListViewStc> list = homeListDatas.get(2);
                    if (!CheckUtil.IsEmpty(list)) {
                        for (int i = 0; i < list.size(); i++) {
                            homeListViewAdapter.add(list.get(i));
                        }
                        homeListViewAdapter.notifyDataSetChanged();
                        home_listview_promotion.setVisibility(View.GONE);
                        homeListView.setVisibility(View.VISIBLE);
                    } else {
                        home_listview_promotion.setVisibility(View.VISIBLE);
                        homeListView.setVisibility(View.GONE);
                    }
                } else {
                    home_listview_promotion.setVisibility(View.VISIBLE);
                    homeListView.setVisibility(View.GONE);
                }
            }
            break;
        case R.id.business_tadaything_dialog_img_close:
            detailDialog.dismiss();
            break;
        default:
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {

        showOnItemClickDialog(position);

    }

    /** 详情弹出框 */
    private Dialog detailDialog = null;
    private LinearLayout dialog_memoryTime_ll;
    View dialogView;
    private RelativeLayout seriousLayout;
    private TextView dialog_content_tv;
    private TextView dialog_startTime_tv;
    private TextView dialog_title_tv;
    private CheckBox dialog_remeber_cb;
    private TextView meomoTv;
    private TextView promotime;

    private void showOnItemClickDialog(int position) {

        if (detailDialog == null) {
            dialogView = inflater.inflate(
                    R.layout.business_todaything_detail_dialog, null);
            detailDialog = new Dialog(getActivity());
            detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            detailDialog.setContentView(dialogView);
            dialogView.findViewById(R.id.business_tadaything_dialog_img_close)
                    .setOnClickListener(this);
            dialog_title_tv = (TextView) dialogView.findViewById(R.id.title);
            dialog_memoryTime_ll = (LinearLayout) dialogView
                    .findViewById(R.id.business_tadaything_dialog_ll_time);
            dialog_content_tv = (TextView) dialogView
                    .findViewById(R.id.business_tadaything_dialog_tv_content);
            promotime = (TextView) dialogView.findViewById(R.id.promotime);
            dialog_startTime_tv = (TextView) dialogView
                    .findViewById(R.id.business_tadaything_dialog_tv_start_time);
            dialog_remeber_cb = (CheckBox) dialogView
                    .findViewById(R.id.business_tadaything_dialog_checkbox_memo);
            meomoTv = (TextView) dialogView.findViewById(R.id.textview1);
        }

        HomeListViewStc homeListViewStc = homeListDatas.get(whatIsCheck).get(
                position);
        dialogView.findViewById(R.id.business_tadaything_dialog_img_close)
                .setOnClickListener(this);

        dialog_content_tv.setText(homeListViewStc.getContent());
        String startDate = homeListViewStc.getStartDate();
        String endDate = homeListViewStc.getEndDate();
        switch (whatIsCheck) {
        case 0:
            promotime.setVisibility(View.GONE);
            dialog_remeber_cb.setVisibility(View.GONE);
            dialog_remeber_cb.setChecked(true);
            meomoTv.setVisibility(View.GONE);
            dialog_title_tv.setText(homeListViewStc.getTitle());
            // if (!CheckUtil.isBlankOrNull(startDate) &&
            // !CheckUtil.isBlankOrNull(endDate)) {
            //
            // dialog_startTime_tv.setText(startDate + " -- " + endDate);
            // }
            if (!detailDialog.isShowing()) {
                detailDialog.show();
            }
            break;
        case 1:
//            meomoTv.setVisibility(View.GONE);
//            dialog_remeber_cb.setVisibility(View.GONE);
//            dialog_remeber_cb.setChecked(true);
//            dialog_title_tv.setText("今日要事");
//            if (!CheckUtil.isBlankOrNull(startDate)
//                    && !CheckUtil.isBlankOrNull(endDate)) {
//                dialog_startTime_tv.setText(startDate + " -- " + endDate);
//            }
//            dialog_remeber_cb.setOnCheckedChangeListener(this);
            MemoDialogFragment fragment = MemoDialogFragment.getInstance(false, homeListViewStc.getTerminalKey(), "", homeListViewStc.getKey());
            fragment.show(getFragmentManager(), "dialog");
            break;
        case 2:
            dialog_title_tv.setText("月目标");
            meomoTv.setVisibility(View.GONE);
            dialog_remeber_cb.setVisibility(View.GONE);
            dialog_startTime_tv.setVisibility(View.GONE);
            promotime.setVisibility(View.GONE);        
            if (!detailDialog.isShowing()) {
                detailDialog.show();
            }
            break;
        }

    }

    /**
     * 是否记住
     */
    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        if (arg1) {
            dialog_memoryTime_ll.setVisibility(View.VISIBLE);
        } else {
            dialog_memoryTime_ll.setVisibility(View.GONE);

        }

    }
}
