package et.tsingtaopad.visit.shopvisit.checkindex;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.AndroidDatabaseConnection;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.GlobalValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.cui.LongSlideSwitch;
import et.tsingtaopad.cui.LongSlideSwitch.OnLongSwitchChangedListener;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.cui.SlideSwitch;
import et.tsingtaopad.cui.SlideSwitch.OnSwitchChangedListener;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.tables.MstCameraInfoM;
import et.tsingtaopad.db.tables.MstGroupproductM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FileUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.ImageUtil;
import et.tsingtaopad.tools.PhotoUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.ui.loader.LatteLoader;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.shopvisit.camera.CameraService;
import et.tsingtaopad.visit.shopvisit.camera.TakeCameraActivity;
import et.tsingtaopad.visit.shopvisit.camera.container.TakeCameraAty;
import et.tsingtaopad.visit.shopvisit.camera.domain.CameraDataStc;
import et.tsingtaopad.visit.shopvisit.camera.domain.CameraImageBean;
import et.tsingtaopad.visit.shopvisit.camera.domain.PictypeDataStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProIndexValue;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.QuicklyProItem;
import et.tsingtaopad.visit.shopvisit.sayhi.SayHiFragment;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：CheckIndexFragment.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-18</br>
 * 功能描述: 巡店拜访-查指标功能模块</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
//查指标功能模块
@SuppressLint("ResourceAsColor")
public class CheckIndexFragment extends BaseFragmentSupport implements OnClickListener {

    private final String TAG = "CheckIndexFragment";

    private CheckIndexService service;
    private String visitId;
    private String termId;
    private String channelId;
    private String seeFlag;
    private String visitDate;
    private String lastTime;

    // 分期采集、采集项、快速采集、非关联产品指标、促销活动
    private List<ProIndex> calculateLst;
    private List<ProItem> proItemLst;
    private List<QuicklyProItem> quicklyProItemLst;
    private List<CheckIndexCalculateStc> noProIndexLst;
    private List<CheckIndexPromotionStc> promotionLst;

    private Button quickCollectBt;
    private Button activityPicBt;
    private NoScrollListView calculateLv;
    private NoScrollListView promotionLv;
    private CaculateAdapter calculateAdapter;
    private PromotionAdapter promotionAdapter;

    // 动态加载与产品无关指标采集数据相关组件
    private LinearLayout noProlayout;
    private LayoutInflater inflater;

    // 快速采集弹出框相关组件对象
    private AlertDialog quicklyDialog;
    private View itemForm;
    private LinearLayout quicklyDialogLv;
    private ScrollView dialogSv;
    //是否在加载数据
    private boolean isLoadingData = true;
    // 是否本次拜访第一次点开此界面 第一次1 非第一次2
    private int isfrist = 1;
    private MstTerminalinfoM term;

    // 图片保存路径
    private String path;
    private String name;

    private int idsuccess = -1; // 0在本地保存成功 1在本地还没保存成功

    AlertDialog dialog;

    MstGroupproductM vo = null;

    private CameraService cameraService;
    private List<CameraDataStc> piclst;
    private int position;

    private TextView groupproductNameTv;
    //private RadioGroup groupRg ;
    private LongSlideSwitch groupStatusSw;
    private LinearLayout progroupLL;

    private LongSlideSwitch hezuoSw;
    private Spinner zhanyoulvSp;
    private LongSlideSwitch peisongSw;

    String isbigerae = "0";  // 照片类型默认大区配置  0:大区配置   1:二级区域配置

    // 获取后台配置的照片类型张数
    List<PictypeDataStc> valueLst = new ArrayList<PictypeDataStc>();

    MyHandler handler;

    /*@SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            switch (msg.what) {

                // 自动计算
                case ConstValues.WAIT5:
                    if (!isLoadingData) {
                        String proId = "";
                        String indexId = "";
                        if (bundle != null) {
                            proId = FunUtil.isBlankOrNullTo(bundle.getString("proId"), "-1");// 产品主键
                            indexId = FunUtil.isBlankOrNullTo(bundle.getString("indexId"), "-1");// 指标主键: ad3030fb-e42e-47f8-a3ec-4229089aab5d
                        }
                        service.calculateIndex(channelId, proItemLst, calculateLst, proId, indexId);
                        calculateAdapter.notifyDataSetChanged();
                        ViewUtil.setListViewHeight(calculateLv);
                    }
                    break;
                // 自动计算
                case 2:
                    //Toast.makeText(getActivity(), "图片正在保存,请稍后", 0).show();
                    break;
                case 3:
                    // 弹出进度框
                    dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
                    dialog.setView(getActivity().getLayoutInflater().inflate(R.layout.camera_progress, null), 0, 0, 0, 0);
                    dialog.setCancelable(false); // 是否可以通过返回键 关闭
                    dialog.show();
                    break;
                case 4:// 图片保存成功, 滚动条消失
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    break;
                case 6:// 图片保存失败, 滚动条消失,并提示
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "图片保存失败,请先开启权限", Toast.LENGTH_SHORT).show();
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
        SoftReference<CheckIndexFragment> fragmentRef;

        public MyHandler(CheckIndexFragment fragment) {
            fragmentRef = new SoftReference<CheckIndexFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            CheckIndexFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            // 处理UI 变化
            Bundle bundle = msg.getData();
            switch (msg.what) {

                // 自动计算
                case ConstValues.WAIT5:
                    /*fragment.hideKeyboard();
                    fragment.autoSum(bundle);
                    LatteLoader.stopLoading(); // 若有进度条,关闭*/


                    fragment.hideKeyboard();
                    fragment.autoNewSum(bundle);

                    break;
                // 自动计算
                case 2:
                    //Toast.makeText(getActivity(), "图片正在保存,请稍后", 0).show();
                    break;
                case 3:
                    // 弹出进度框
                    fragment.showAlertDialog();

                    break;
                case 4:// 图片保存成功, 滚动条消失
                    // 关闭进度框
                    fragment.closeAlertDialog(false);// false:不吐司
                    break;
                case 6:// 图片保存失败, 滚动条消失,并提示
                    // 关闭进度框
                    fragment.closeAlertDialog(true);// true:吐司 解释为什么失败

                    break;
                case 7:// 关闭进度框
                    LatteLoader.stopLoading(); // 若有进度条,关闭
                    break;
                case 8:// 关闭进度框

                    fragment.adapterChanged();
                    break;

                default:
                    break;
            }
        }
    }

    // 关闭软键盘
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (getActivity().getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    // 自动计算
    private void autoSum(Bundle bundle) {
        if (!isLoadingData) {
            String proId = "";
            String indexId = "";
            if (bundle != null) {
                proId = FunUtil.isBlankOrNullTo(bundle.getString("proId"), "-1");// 产品主键
                indexId = FunUtil.isBlankOrNullTo(bundle.getString("indexId"), "-1");// 指标主键: ad3030fb-e42e-47f8-a3ec-4229089aab5d
            }
            service.calculateIndex(channelId, proItemLst, calculateLst, proId, indexId);
            calculateAdapter.notifyDataSetChanged();
            ViewUtil.setListViewHeight(calculateLv);// 解决无响应,注释这一行
        }
    }

    // 自动计算
    private void autoNewSum(Bundle bundle) {
        if (!isLoadingData) {
            String proId = "";
            String indexId = "";
            if (bundle != null) {
                proId = FunUtil.isBlankOrNullTo(bundle.getString("proId"), "-1");// 产品主键
                indexId = FunUtil.isBlankOrNullTo(bundle.getString("indexId"), "-1");// 指标主键: ad3030fb-e42e-47f8-a3ec-4229089aab5d
            }
            /*service.calculateIndex(channelId, proItemLst, calculateLst, proId, indexId);
            calculateAdapter.notifyDataSetChanged();
            ViewUtil.setListViewHeight(calculateLv);// 解决无响应,注释这一行*/

            AutoSumTask autoSumTask = new AutoSumTask();
            autoSumTask.execute(channelId, proItemLst, calculateLst, proId, indexId,service);
        }
    }
    private void adapterChanged() {
        calculateAdapter.notifyDataSetChanged();
        ViewUtil.setListViewHeight(calculateLv);// 解决无响应,注释这一行

        handler.sendEmptyMessageDelayed(7, 2000);
        // LatteLoader.stopLoading(); // 若有进度条,关闭
    }

    // 弹出进度框
    private void showAlertDialog() {
        dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        dialog.setView(getActivity().getLayoutInflater().inflate(R.layout.camera_progress, null), 0, 0, 0, 0);
        dialog.setCancelable(false); // 是否可以通过返回键 关闭
        dialog.show();
    }

    // 关闭进度框
    private void closeAlertDialog(boolean type) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (type) {
            Toast.makeText(getActivity(), "图片保存失败,请先开启权限", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.shopvisit_checkindex, null);
        View view = inflater.inflate(R.layout.shopvisit_checkindex, container, false);
        DbtLog.logUtils(TAG, "onCreateView()");
        this.initView(view);

        // this.asynch();
        CheckIndexInitTask task = new CheckIndexInitTask();
        task.execute();

        return view;
    }

    /**
     * 异步加载 有下面的CheckIndexInitTask代替 2018年8月21日16:13:36
     */
    /*public void asynch() {
        DbtLog.logUtils(TAG, "asynch()");
        new AsyncTask<Void, Void, Void>() {
            // 异步执行前
            protected void onPreExecute() {
                LatteLoader.showLoading(getActivity(), true);// 处理数据中 ,在InvoicingFragment的initData中关闭

                initPreData();
                isLoadingData = true;
            }

            @Override
            protected Void doInBackground(Void... params) {
                initDoBackData();
                return null;
            }

            // 异步执行后
            protected void onPostExecute(Void result) {
                // initData();
                initPostData();
                handler.sendEmptyMessageDelayed(7, 3000);
                // LatteLoader.stopLoading(); // 若有进度条,关闭
                isLoadingData = false;
            }
        }.execute();
    }*/

    /**
     * 异步加载
     */
    private class CheckIndexInitTask extends AsyncTask<Void, Void, Void> {

        // 异步执行前
        protected void onPreExecute() {
            LatteLoader.showLoading(getActivity(), true);// 处理数据中 ,在Handle中关闭
            initPreData();
            isLoadingData = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            initDoBackData();
            return null;
        }

        // 异步执行后
        protected void onPostExecute(Void result) {
            // initData();
            initPostData();
            handler.sendEmptyMessageDelayed(7, 2000);
            isLoadingData = false;
        }

    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView()");
        //快速采集按钮
        quickCollectBt = (Button) view.findViewById(R.id.checkindex_bt_quickcollect);
        // 活动拍照
        activityPicBt = (Button) view.findViewById(R.id.checkindex_bt_activitypic);
        promotionTitle = (LinearLayout) view.findViewById(R.id.checkindex_ll_promotion_title);
        promotionHead = (LinearLayout) view.findViewById(R.id.checkindex_ll_promotion_head);
        //采项分集listView
        calculateLv = (NoScrollListView) view.findViewById(R.id.checkindex_lv_calculate);
        //促销活动listView
        promotionLv = (NoScrollListView) view.findViewById(R.id.checkindex_lv_promotion);

        // 产品组合是否达标
        groupproductNameTv = (TextView) view.findViewById(R.id.noproindex_tv_name);
        // 产品组合是否达标选项
        //groupRg = (RadioGroup)view.findViewById(R.id.noproindex_rg_value);
        // 产品组合是否达标 滑动

        groupStatusSw = (LongSlideSwitch) view.findViewById(R.id.noproindex_sw_prostatus);
        progroupLL = (LinearLayout) view.findViewById(R.id.checkindex_ll_group);

        hezuoSw = (LongSlideSwitch) view.findViewById(R.id.noproindex_sw_hezuo);
        zhanyoulvSp = (Spinner) view.findViewById(R.id.noproindex_sp_zhanyoulv);
        peisongSw = (LongSlideSwitch) view.findViewById(R.id.noproindex_sw_peisong);

        // 获取页面中用于动态添加的容器组件及模板
        noProlayout = (LinearLayout) view.findViewById(R.id.checkindex_lo_noproindex);
        noProlayout.removeAllViews();
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        quickCollectBt.setOnClickListener(this);
        activityPicBt.setOnClickListener(this);
    }

    // 异步执行前
    private void initPreData() {

        DbtLog.logUtils(TAG, "initData()");
        handler = new MyHandler(this);
        long time = new Date().getTime();
        service = new CheckIndexService(getActivity(), null);
        ConstValues.handler = handler;

    }

    // 异步执行中
    private void initDoBackData() {

        // 获取参数
        Bundle bundle = getArguments();
        seeFlag = FunUtil.isBlankOrNullTo(bundle.getString("seeFlag"), "");
        visitId = bundle.getString("visitKey");
        termId = bundle.getString("termId");

        isfrist = PrefUtils.getInt(getActivity(), "isfrist" + termId, 1);
        //  获取时间
        if (isfrist == 1) {
            // 获取上次拜访时间(原因:根据时间设置促销活动隔天关闭)若上次时间为null,设为"1901-01-01 17:31"
            visitDate = bundle.getString("visitDate");
            lastTime = bundle.getString("lastTime");
            if ("".equals(lastTime) && lastTime.length() <= 0) {
                lastTime = "1901-01-01 01:01";
            }
            PrefUtils.putInt(getActivity(), "isfrist" + termId, 2);
        } else {
            // 获取最后一次拜访的信息
            ShopVisitService shopVisitService = new ShopVisitService(getActivity(), null);
            MstVisitM visitM = shopVisitService.findNewVisit(termId, false);
            if (visitM == null) {// 若此终端从未拜访过 设置此终端的上次拜访时间为1901-01-01 01:01
                lastTime = "1901-01-01 01:01";// 2016-03-08 11:24
                // 读取上次拜访的时间 (查询拜访主表的关于此终端的所有拜访记录,取最新一次,不管是否上传成功过,查询此记录的拜访时间)
            } else {
                lastTime = DateUtil.formatDate(0, visitM.getVisitdate());// 2016-03-08 11:24
            }
        }

        // 获取最新的终端数据
        term = service.findTermById(termId);
        channelId = term.getMinorchannel();// 次渠道

        // 删除多余的采集项记录  比如:渠道C->B  考核的产品少了一个,界面上上了这个产品,要将数据库中的采集项表也删除
        // 1 查出mst_vistproduct_info考核产品
        // 2 查出 采集项表 产品记录
        // 3 把采集项表的产品记录 多出来的删除


        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 // 指标 产品 结果
        calculateLst = service.queryCalculateIndex(visitId, termId, channelId, seeFlag);
        // 获取分项采集部分的产品指标对应的采集项目数据 // 因为在ShopVisitActivity生成了供货关系,此时就能关联出各个产品的采集项,现有量变化量为0
        proItemLst = service.queryCalculateItem(visitId, channelId);
        // 删除多余的采集项记录  比如:渠道C->B  考核的产品少了一个,界面上没有了这个产品,要将数据库中的采集项表也删除
        service.deleteCollection(visitId, proItemLst);
        //CaculateAdapter---查指标的分项采集部分 一级Adapter
        calculateAdapter = new CaculateAdapter(
                getActivity(), calculateLst, ConstValues.indexLst, proItemLst);// 上下文,指标对象集合,同步下来的指标集合,所有产品集合


        //获取巡店拜访-查指标的促销活动页面部分的数据
        promotionLst = service.queryPromotion(visitId, term.getSellchannel(), term.getTlevel());

        // 获取后台配置的照片类型张数
        valueLst = new CameraService(getActivity(), null).queryPictypeMAll();
        // 根据图片类型表Mst_pictype_M  判断普通图片 是不是大区配置的
        if (valueLst.size() > 0) {// 有普通照片
            // 查看普通照片中,是否有二级区域的记录
            if (PrefUtils.getString(getActivity(), "disId", "").equals(valueLst.get(0).getAreaid())) {
                isbigerae = "1";// 1:二级区域配置
            }
        } else {// 没有普通照片,查看促销活动,
            for (CheckIndexPromotionStc pictypeDataStc : promotionLst) {
                // 活动表记录 是二级区域的且是拍照类型
                if (PrefUtils.getString(getActivity(), "disId", "").equals(pictypeDataStc.getAreaid()) && "1".equals(pictypeDataStc.getIspictype())) {
                    isbigerae = "1";// 1:二级区域配置
                }
            }
        }

        // 获取与产品无关指标采集数据
        //noProIndexLst = service.queryNoProIndex(visitId, term.getMinorchannel(), seeFlag);
        noProIndexLst = service.queryNoProIndex2(visitId, term.getMinorchannel(), seeFlag);

        // 先查询之前数据  判断终端该指标是否达标
        List<MstGroupproductM> listvo = new ArrayList<MstGroupproductM>();
        // 从表搂取一条数据,今天之前的包含今天
        listvo = service.queryMstGroupproductM(term.getTerminalcode(), (DateUtil.getDateTimeStr(7) + "  00:00:00"));

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
                vo.setCreateusereng(PrefUtils.getString(getActivity(), "userGongHao", "21000"));
                vo.setCreatedate(DateUtil.getDateTimeStr(6));
                vo.setUpdateusereng(PrefUtils.getString(getActivity(), "userGongHao", "21000"));
                vo.setUpdatetime(DateUtil.getDateTimeStr(6));
                vo.setUpdateusereng(PrefUtils.getString(getActivity(), "userGongHao", "21000"));
                vo.setUploadFlag("0");// 不上传
                vo.setPadisconsistent("0");// 未上传
                service.createMstGroupproductM(vo);
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
            vo.setCreateusereng(PrefUtils.getString(getActivity(), "userGongHao", "20000"));
            vo.setCreatedate(DateUtil.getDateTimeStr(6));
            vo.setUpdateusereng(PrefUtils.getString(getActivity(), "userGongHao", "20000"));
            vo.setUpdatetime(DateUtil.getDateTimeStr(6));
            vo.setUpdateusereng(PrefUtils.getString(getActivity(), "userGongHao", "20000"));
            vo.setUploadFlag("0");// 不上传
            vo.setPadisconsistent("0");// 未上传
            service.createMstGroupproductM(vo);
        }
    }

    // 异步执行后,展示页面数据
    private void initPostData() {

        progroupLL.setVisibility(View.VISIBLE);

        calculateLv.setAdapter(calculateAdapter);
        ViewUtil.setListViewHeight(calculateLv);// 解决无响应,注释这一行

        // 若没有促销活动,隐藏标题及表头
        if (promotionLst.size() > 0) {
            promotionTitle.setVisibility(View.VISIBLE);
            promotionHead.setVisibility(View.VISIBLE);
        } else {
            promotionTitle.setVisibility(View.GONE);
            promotionHead.setVisibility(View.GONE);
        }

        // 对每个活动的 拍照按钮监听
        promotionAdapter = new PromotionAdapter(getActivity(), promotionLst,
                lastTime, null, valueLst, isbigerae, seeFlag, new IClick() {

            @Override
            public void listViewItemClick(int position, View v) {

                int picindex = (Integer) v.getTag() + 4;
                String pictypekey = promotionLst.get((Integer) v.getTag()).getPromotKey();
                String pictypename = promotionLst.get((Integer) v.getTag()).getPromotName();
                // 拍照角标,拍照类型key,图片类型名称
                toItemCamera(picindex, pictypekey, pictypename);
            }
        });
        promotionLv.setAdapter(promotionAdapter);
        //  ViewUtil.setListViewHeight(promotionLv);


        // long time1 = new Date().getTime();
        // Log.e("Optimization", "查指标执行数据库" + (time1 - time));


        // 产品组合是否达标
        groupStatusSw.setOnLongSwitchChangedListener(new OnLongSwitchChangedListener() {

            @Override
            public void onLongSwitchChanged(LongSlideSwitch obj, int status) {
                if (status == SlideSwitch.SWITCH_ON) {
                    groupStatusSw.setStatus(true);
                } else {
                    groupStatusSw.setStatus(false);
                }
            }
        });

        // 合作执行是否到位
        hezuoSw.setOnLongSwitchChangedListener(new OnLongSwitchChangedListener() {

            @Override
            public void onLongSwitchChanged(LongSlideSwitch obj, int status) {
                if (status == SlideSwitch.SWITCH_ON) {
                    hezuoSw.setStatus(true);
                } else {
                    hezuoSw.setStatus(false);
                }
            }
        });

        // 是否高质量配送
        peisongSw.setOnLongSwitchChangedListener(new OnLongSwitchChangedListener() {

            @Override
            public void onLongSwitchChanged(LongSlideSwitch obj, int status) {
                if (status == SlideSwitch.SWITCH_ON) {
                    peisongSw.setStatus(true);
                } else {
                    peisongSw.setStatus(false);
                }
            }
        });

        // 初始化产品组合是否达标 若上次拜访没有数据,或者是N,null则设为false
        if (CheckUtil.isBlankOrNull(vo.getIfrecstand()) || "N".equals(vo.getIfrecstand()) || "null".equals(vo.getIfrecstand())) {
            groupStatusSw.setStatus(false);
        } else {
            groupStatusSw.setStatus(true);
        }

        // 初始化 合作是否到位 占有率 高质量配送数据
        for (CheckIndexCalculateStc item : noProIndexLst) {
            // 重新查询指标关联指标值 //因为原先的这3个指标 不再关联指标值(pad_checktype_m不再同步这三个指标)
            // 交由pad直接初始化 // ywm 20160407
            if ("666b74b3-b221-4920-b549-d9ec39a463fd".equals(item.getIndexId())) {// 合作是否到位
                //tempLst = service.queryNoProIndexValueId1();// 此处报错
                if (CheckUtil.isBlankOrNull(item.getIndexValueId())) {
                    hezuoSw.setStatus(false);
                } else if ("9019cf03-4572-4559-9971-48a27a611c3d".equals(item.getIndexValueId())) {// 合作是否到位 是
                    hezuoSw.setStatus(true);
                } else if ("8d36d1e5-c776-452e-8893-589ad786d71d".equals(item.getIndexValueId())) {// 合作是否到位 否
                    hezuoSw.setStatus(false);
                } else {
                    hezuoSw.setStatus(false);
                }
            } else if ("df2e88c9-246f-40e2-b6e5-08cdebf8c281".equals(item.getIndexId())) {// 是否高质量配送
                //tempLst = service.queryNoProIndexValueId2();
                if (CheckUtil.isBlankOrNull(item.getIndexValueId())) {
                    peisongSw.setStatus(false);
                } else if ("460647a9-283a-44ea-b11f-42efe1fd62e4".equals(item.getIndexValueId())) {// 是否高质量配送
                    peisongSw.setStatus(true);
                } else if ("bf600cfe-f70d-4170-857d-65dd59740d57".equals(item.getIndexValueId())) {// 是否高质量配送
                    peisongSw.setStatus(false);
                } else {
                    peisongSw.setStatus(false);
                }

            } else if ("59802090-02ac-4146-9cc3-f09570c36a26".equals(item.getIndexId())) {// 我品占有率
                List<KvStc>  tempLst = service.queryNoProIndexValueId3();
                zhanyoulvSp.setAdapter(new SpinnerKeyValueAdapter(getActivity(), tempLst, new String[]{"key", "value"}, item.getIndexValueId()));
            }
        }
    }


    // 由 initPreData() initDoBackData() initPostData() 替代这个方法  2018年8月21日16:04:25
    private void initData() {
        DbtLog.logUtils(TAG, "initData()");
        handler = new MyHandler(this);
        long time = new Date().getTime();
        service = new CheckIndexService(getActivity(), null);
        ConstValues.handler = handler;

        progroupLL.setVisibility(View.VISIBLE);

        // 获取参数
        Bundle bundle = getArguments();
        seeFlag = FunUtil.isBlankOrNullTo(bundle.getString("seeFlag"), "");
        visitId = bundle.getString("visitKey");
        termId = bundle.getString("termId");


        isfrist = PrefUtils.getInt(getActivity(), "isfrist" + termId, 1);
        //  获取时间
        if (isfrist == 1) {
            // 获取上次拜访时间(原因:根据时间设置促销活动隔天关闭)若上次时间为null,设为"1901-01-01 17:31"
            visitDate = bundle.getString("visitDate");
            lastTime = bundle.getString("lastTime");
            if ("".equals(lastTime) && lastTime.length() <= 0) {
                lastTime = "1901-01-01 01:01";
            }
            PrefUtils.putInt(getActivity(), "isfrist" + termId, 2);
        } else {
            // 获取最后一次拜访的信息
            ShopVisitService shopVisitService = new ShopVisitService(getActivity(), null);
            MstVisitM visitM = shopVisitService.findNewVisit(termId, false);
            if (visitM == null) {// 若此终端从未拜访过 设置此终端的上次拜访时间为1901-01-01 01:01
                lastTime = "1901-01-01 01:01";// 2016-03-08 11:24
                // 读取上次拜访的时间 (查询拜访主表的关于此终端的所有拜访记录,取最新一次,不管是否上传成功过,查询此记录的拜访时间)
            } else {
                lastTime = DateUtil.formatDate(0, visitM.getVisitdate());// 2016-03-08 11:24
            }
        }


        // 获取最新的终端数据

        term = service.findTermById(termId);
        channelId = term.getMinorchannel();// 次渠道

        // 删除多余的采集项记录  比如:渠道C->B  考核的产品少了一个,界面上上了这个产品,要将数据库中的采集项表也删除
        // 1 查出mst_vistproduct_info考核产品
        // 2 查出 采集项表 产品记录
        // 3 把采集项表的产品记录 多出来的删除


        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 // 铺货状态,道具生动化,产品生动化,冰冻化
        calculateLst = service.queryCalculateIndex(visitId, termId, channelId, seeFlag);
        // String sd = "visitId: "+ visitId +" termId: "+ termId +" channelId: "+ channelId +" seeFlag: "+ seeFlag ;
        // FileUtil.writeTxt(sd, FileUtil.getSDPath()+"/MST_CHECKEXERECORD_INFO_ZIPww1223.txt");
        // 获取分项采集部分的产品指标对应的采集项目数据 // 因为在ShopVisitActivity生成了供货关系,此时就能关联出各个产品的采集项,现有量变化量为0
        proItemLst = service.queryCalculateItem(visitId, channelId);

        // 删除多余的采集项记录  比如:渠道C->B  考核的产品少了一个,界面上上了这个产品,要将数据库中的采集项表也删除
        service.deleteCollection(visitId, proItemLst);

        //CaculateAdapter---查指标的分项采集部分一级Adapter
        calculateAdapter = new CaculateAdapter(
                getActivity(), calculateLst, ConstValues.indexLst, proItemLst);// 上下文,指标对象集合,同步下来的指标集合,所有产品集合
        calculateLv.setAdapter(calculateAdapter);
        ViewUtil.setListViewHeight(calculateLv);// 解决无响应,注释这一行

        //获取巡店拜访-查指标的促销活动页面部分的数据
        promotionLst = service.queryPromotion(visitId, term.getSellchannel(), term.getTlevel());

        // 若没有促销活动,隐藏标题及表头
        if (promotionLst.size() > 0) {
            promotionTitle.setVisibility(View.VISIBLE);
            promotionHead.setVisibility(View.VISIBLE);
        } else {
            promotionTitle.setVisibility(View.GONE);
            promotionHead.setVisibility(View.GONE);
        }

        // ↓判断活动拍照按钮是否出现160927-->拍照按钮不再出现170313
        int IsAccomplishcount1 = 0;
        for (int i = 0; i < promotionLst.size(); i++) {
            if ("1".equals(promotionLst.get(i).getIsAccomplish())) {// 有达成的活动 则+1;
                IsAccomplishcount1++;
            }
        }
        String todaytime = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        // 获取后台配置的照片类型张数
        // List<PictypeDataStc> valueLst = new ArrayList<PictypeDataStc>();
        valueLst = new CameraService(getActivity(), null).queryPictypeMAll();
        if (IsAccomplishcount1 > 0 && todaytime.equals(lastTime.substring(0, 10)) && valueLst.size() < 3 && (!"1".equals(seeFlag))) {// 有激活,当天拜访,拍照类型<3张,拜访模式(查看模式不显示拍照按钮)
            //activityPicBt.setVisibility(View.VISIBLE);// 如果有促销活动,拍照按钮显示
            activityPicBt.setVisibility(View.GONE);// 如果有促销活动,拍照按钮不显示170313
        } else {// 没激活,不是当天拜访
            activityPicBt.setVisibility(View.GONE);
        }
        // ↑判断锉削活动拍照是否出现160927-->拍照按钮不再出现170313

        // 根据图片类型表Mst_pictype_M  判断普通图片 是不是大区配置的
        // String isbigerae = "0";  // 默认大区配置  0:大区配置   1:二级区域配置
        if (valueLst.size() > 0) {// 有普通照片
            // 查看普通照片中,是否有二级区域的记录
            if (PrefUtils.getString(getActivity(), "disId", "").equals(valueLst.get(0).getAreaid())) {
                isbigerae = "1";// 1:二级区域配置
            }
        } else {// 没有普通照片,查看促销活动,
            for (CheckIndexPromotionStc pictypeDataStc : promotionLst) {
                // 活动表记录 是二级区域的且是拍照类型
                if (PrefUtils.getString(getActivity(), "disId", "").equals(pictypeDataStc.getAreaid()) && "1".equals(pictypeDataStc.getIspictype())) {
                    isbigerae = "1";// 1:二级区域配置
                }
            }
        }

        // 对每个活动的 拍照按钮监听
        promotionAdapter = new PromotionAdapter(getActivity(), promotionLst,
                lastTime, activityPicBt, valueLst, isbigerae, seeFlag, new IClick() {

            @Override
            public void listViewItemClick(int position, View v) {

                int picindex = (Integer) v.getTag() + 4;
                String pictypekey = promotionLst.get((Integer) v.getTag()).getPromotKey();
                String pictypename = promotionLst.get((Integer) v.getTag()).getPromotName();
                // 拍照角标,拍照类型key,图片类型名称
                toItemCamera(picindex, pictypekey, pictypename);
            }

        });

        promotionLv.setAdapter(promotionAdapter);
        //        ViewUtil.setListViewHeight(promotionLv);

        // 获取与产品无关指标采集数据
        //noProIndexLst = service.queryNoProIndex(visitId, term.getMinorchannel(), seeFlag);
        noProIndexLst = service.queryNoProIndex2(visitId, term.getMinorchannel(), seeFlag);
        long time1 = new Date().getTime();
        Log.e("Optimization", "查指标执行数据库" + (time1 - time));
        List<KvStc> tempLst = new ArrayList<KvStc>();

        // 产品组合是否达标
        // groupproductNameTv.setText("产品组合是否达标: ");

        // 先查询之前数据  判断终端该指标是否达标
        List<MstGroupproductM> listvo = new ArrayList<MstGroupproductM>();
        // 从表搂取一条数据,今天之前的包含今天
        listvo = service.queryMstGroupproductM(term.getTerminalcode(), (DateUtil.getDateTimeStr(7) + "  00:00:00"));

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
                vo.setCreateusereng(PrefUtils.getString(getActivity(), "userGongHao", "21000"));
                vo.setCreatedate(DateUtil.getDateTimeStr(6));
                vo.setUpdateusereng(PrefUtils.getString(getActivity(), "userGongHao", "21000"));
                vo.setUpdatetime(DateUtil.getDateTimeStr(6));
                vo.setUpdateusereng(PrefUtils.getString(getActivity(), "userGongHao", "21000"));
                vo.setUploadFlag("0");// 不上传
                vo.setPadisconsistent("0");// 未上传
                service.createMstGroupproductM(vo);
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
            vo.setCreateusereng(PrefUtils.getString(getActivity(), "userGongHao", "20000"));
            vo.setCreatedate(DateUtil.getDateTimeStr(6));
            vo.setUpdateusereng(PrefUtils.getString(getActivity(), "userGongHao", "20000"));
            vo.setUpdatetime(DateUtil.getDateTimeStr(6));
            vo.setUpdateusereng(PrefUtils.getString(getActivity(), "userGongHao", "20000"));
            vo.setUploadFlag("0");// 不上传
            vo.setPadisconsistent("0");// 未上传
            service.createMstGroupproductM(vo);
        }


        groupStatusSw.setOnLongSwitchChangedListener(new OnLongSwitchChangedListener() {

            @Override
            public void onLongSwitchChanged(LongSlideSwitch obj, int status) {
                if (status == SlideSwitch.SWITCH_ON) {
                    groupStatusSw.setStatus(true);
                } else {
                    groupStatusSw.setStatus(false);
                }

            }

        });

        hezuoSw.setOnLongSwitchChangedListener(new OnLongSwitchChangedListener() {

            @Override
            public void onLongSwitchChanged(LongSlideSwitch obj, int status) {
                if (status == SlideSwitch.SWITCH_ON) {
                    hezuoSw.setStatus(true);
                } else {
                    hezuoSw.setStatus(false);
                }

            }
        });

        peisongSw.setOnLongSwitchChangedListener(new OnLongSwitchChangedListener() {

            @Override
            public void onLongSwitchChanged(LongSlideSwitch obj, int status) {
                if (status == SlideSwitch.SWITCH_ON) {
                    peisongSw.setStatus(true);
                } else {
                    peisongSw.setStatus(false);
                }

            }
        });

        // 初始化产品组合是否达标
        // 上次拜访没有数据,或者是N,null
        if (CheckUtil.isBlankOrNull(vo.getIfrecstand()) || "N".equals(vo.getIfrecstand()) || "null".equals(vo.getIfrecstand())) {
            groupStatusSw.setStatus(false);
        } else {
            groupStatusSw.setStatus(true);
        }

        // 初始化 合作是否到位 占有率 高质量配送数据
        for (CheckIndexCalculateStc item : noProIndexLst) {
            // 重新查询指标关联指标值 //因为原先的这3个指标 不再关联指标值(pad_checktype_m不再同步这三个指标)
            // 交由pad直接初始化 // ywm 20160407
            if ("666b74b3-b221-4920-b549-d9ec39a463fd".equals(item.getIndexId())) {// 合作是否到位
                //tempLst = service.queryNoProIndexValueId1();// 此处报错
                if (CheckUtil.isBlankOrNull(item.getIndexValueId())) {
                    hezuoSw.setStatus(false);
                } else if ("9019cf03-4572-4559-9971-48a27a611c3d".equals(item.getIndexValueId())) {// 合作是否到位 是
                    hezuoSw.setStatus(true);
                } else if ("8d36d1e5-c776-452e-8893-589ad786d71d".equals(item.getIndexValueId())) {// 合作是否到位 否
                    hezuoSw.setStatus(false);
                } else {
                    hezuoSw.setStatus(false);
                }
            } else if ("df2e88c9-246f-40e2-b6e5-08cdebf8c281".equals(item.getIndexId())) {// 是否高质量配送
                //tempLst = service.queryNoProIndexValueId2();
                if (CheckUtil.isBlankOrNull(item.getIndexValueId())) {
                    peisongSw.setStatus(false);
                } else if ("460647a9-283a-44ea-b11f-42efe1fd62e4".equals(item.getIndexValueId())) {// 是否高质量配送
                    peisongSw.setStatus(true);
                } else if ("bf600cfe-f70d-4170-857d-65dd59740d57".equals(item.getIndexValueId())) {// 是否高质量配送
                    peisongSw.setStatus(false);
                } else {
                    peisongSw.setStatus(false);
                }

            } else if ("59802090-02ac-4146-9cc3-f09570c36a26".equals(item.getIndexId())) {// 我品占有率
                tempLst = service.queryNoProIndexValueId3();
                zhanyoulvSp.setAdapter(new SpinnerKeyValueAdapter(getActivity(), tempLst, new String[]{"key", "value"}, item.getIndexValueId()));
            }
        }

    }


    private int picindex;
    private String pictypekey;
    private String pictypename;
    private LinearLayout promotionTitle;
    private LinearLayout promotionHead;

    // 去条目拍照  位置,类型key,名称
    private void toItemCamera(int position, String pictypekey, String pictypename) {
        this.picindex = position;
        this.pictypekey = pictypekey;
        this.pictypename = pictypename;

        DbtLog.logUtils(TAG, "toItemCamera()-跳到摄像头界面");
        ViewUtil.clearDoubleClick();

        cameraService = new CameraService(getActivity(), null);
        // 已经拍了多少张照片
        piclst = cameraService.queryCurrentPicRecord1(term.getTerminalkey(), DateUtil.getDateTimeStr(0), "1", "0", visitId);

        ViewUtil.clearDoubleClick();

        // 位置是从0起计算的
        position = new CameraService(getActivity(), null).queryPictypeMAll().size();


        if (hasPermission(GlobalValues.HARDWEAR_CAMERA_PERMISSION)) {
            // 拥有了此权限,那么直接执行业务逻辑
            toCamera4();
        } else {
            // 还没有对一个权限(请求码,权限数组)这两个参数都事先定义好
            requestPermission(GlobalValues.HARDWEAR_CAMERA_CODE, GlobalValues.HARDWEAR_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");
        long time3 = new Date().getTime();
        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag))
            return;
        if (isLoadingData)
            return;

        ProIndex indexItem;
        ProIndexValue valueItem;
        ListView indexValueLv;
        KvStc kvItem;
        RadioGroup resultRg;
        RadioButton resultRb;
        EditText resultEt;
        Spinner resultSp;

        // 遍历lv，获取各指标的指标值
        for (int i = 0; i < calculateLst.size(); i++) {
            if (calculateLv == null || calculateLv.getChildAt(i) == null
                    || calculateLv.getChildAt(i).findViewById(R.id.caculate_lv_indexvalue) == null)
                continue;
            indexItem = calculateLst.get(i);
            indexValueLv = (ListView) calculateLv.getChildAt(i).findViewById(R.id.caculate_lv_indexvalue);
            if (indexValueLv == null)
                continue;
            for (int j = 0; j < indexItem.getIndexValueLst().size(); j++) {
                valueItem = indexItem.getIndexValueLst().get(j);
                if (valueItem == null)
                    continue;

                // 判定显示方式及初始化显示数据 0:计算单选、 1:单选、 2:文本框、 3:数值、 4:下拉单选
                if (ConstValues.FLAG_1.equals(valueItem.getIndexType())) {
                    if (indexValueLv.getChildAt(j).findViewById(R.id.caculate_rg_indexvalue) == null)
                        continue;
                    resultRg = (RadioGroup) indexValueLv.getChildAt(j).findViewById(R.id.caculate_rg_indexvalue);
                    for (int k = 0; k < resultRg.getChildCount(); k++) {
                        resultRb = (RadioButton) resultRg.getChildAt(k);
                        if (resultRb.isChecked()) {
                            valueItem.setIndexValueId(resultRb.getHint().toString());
                            break;
                        }
                    }
                } else if (ConstValues.FLAG_2.equals(valueItem.getIndexType())) {
                    if (indexValueLv.getChildAt(j).findViewById(R.id.caculate_et_indexvalue) == null)
                        continue;
                    resultEt = (EditText) indexValueLv.getChildAt(j).findViewById(R.id.caculate_et_indexvalue);
                    valueItem.setIndexValueId(resultEt.getText().toString());

                } else if (ConstValues.FLAG_3.equals(valueItem.getIndexType())) {
                    if (indexValueLv.getChildAt(j).findViewById(R.id.caculate_et_indexvalue_num) == null)
                        continue;
                    resultEt = (EditText) indexValueLv.getChildAt(j).findViewById(R.id.caculate_et_indexvalue_num);
                    valueItem.setIndexValueId(resultEt.getText().toString());

                } else if (ConstValues.FLAG_4.equals(valueItem.getIndexType()) || ConstValues.FLAG_0.equals(valueItem.getIndexType())) {
                    if (indexValueLv.getChildAt(j).findViewById(R.id.caculate_bt_indexvalue) == null)
                        continue;
                    valueItem.setIndexValueId(FunUtil.isBlankOrNullTo(
                            indexValueLv.getChildAt(j).findViewById(R.id.caculate_bt_indexvalue).getTag(), "-1"));
                }
            }
        }

        // 遍历获取与产品无关指标采集数据部分
        CheckIndexCalculateStc itemStc;
        View itemV;
        /*for (int i = 0; i < noProIndexLst.size(); i++) {
            itemStc = noProIndexLst.get(i);
            itemV = noProlayout.getChildAt(i);
            if (itemV == null) continue;
            // 判定显示方式及初始化显示数据 0:计算单选、 1:单选、 2:文本框、 3:数值、 4:下拉单选
            if (ConstValues.FLAG_1.equals(itemStc.getIndexType())) {
                if (itemV.findViewById(R.id.noproindex_rg_indexvalue) == null) continue;
                resultRg = (RadioGroup)itemV.findViewById(R.id.noproindex_rg_indexvalue);
                for (int k = 0; k < resultRg.getChildCount(); k++) {
                    resultRb = (RadioButton)resultRg.getChildAt(k);
                    if (resultRb.isChecked()) {
                        itemStc.setIndexValueId(resultRb.getHint().toString());
                        break;
                    }
                }
            } else if (ConstValues.FLAG_2.equals(itemStc.getIndexType())) {
                if (itemV.findViewById(R.id.noproindex_et_indexvalue) == null) continue;
                resultEt = (EditText)itemV.findViewById(R.id.noproindex_et_indexvalue);
                if (CheckUtil.isBlankOrNull(resultEt.getText().toString())) {
                    itemStc.setIndexValueId(resultEt.getHint().toString());
                } else {
                    itemStc.setIndexValueId(resultEt.getText().toString());
                }
                
            } else if (ConstValues.FLAG_3.equals(itemStc.getIndexType())) {
                if (itemV.findViewById(R.id.noproindex_et_indexvalue_num) == null) continue;
                resultEt = (EditText)itemV.findViewById(R.id.noproindex_et_indexvalue_num);
                if (CheckUtil.isBlankOrNull(resultEt.getText().toString())) {
                    itemStc.setIndexValueId(resultEt.getHint().toString());
                } else {
                    itemStc.setIndexValueId(resultEt.getText().toString());
                }
                
            } else if (ConstValues.FLAG_4.equals(itemStc.getIndexType()) 
                                    || ConstValues.FLAG_0.equals(itemStc.getIndexType())) {
                if (itemV.findViewById(R.id.noproindex_sp_indexvalue) == null) continue;
                resultSp = (Spinner)itemV.findViewById(R.id.noproindex_sp_indexvalue);
                kvItem = (KvStc)resultSp.getSelectedItem();
                itemStc.setIndexValueId(kvItem.getKey());
            } 
        }*/

        // 根据页面上用户选的值  保存数据到表
        for (int i = 0; i < noProIndexLst.size(); i++) {
            itemStc = noProIndexLst.get(i);
            if ("666b74b3-b221-4920-b549-d9ec39a463fd".equals(itemStc.getIndexId())) {// 合作是否到位
                if (hezuoSw.getStatus()) {// 合作是否到位
                    itemStc.setIndexValueId("9019cf03-4572-4559-9971-48a27a611c3d");//合作是否到位 shi
                } else {
                    itemStc.setIndexValueId("8d36d1e5-c776-452e-8893-589ad786d71d");        // 合作是否到位否															// 是
                }
            } else if ("df2e88c9-246f-40e2-b6e5-08cdebf8c281".equals(itemStc.getIndexId())) {// 是否高质量配送
                if (peisongSw.getStatus()) {// 合作是否到位
                    itemStc.setIndexValueId("460647a9-283a-44ea-b11f-42efe1fd62e4");//是否高质量配送 shi
                } else {
                    itemStc.setIndexValueId("bf600cfe-f70d-4170-857d-65dd59740d57");        // 是否高质量配送否															// 是
                }

            } else if ("59802090-02ac-4146-9cc3-f09570c36a26".equals(itemStc.getIndexId())) {// 我品占有率
                kvItem = (KvStc) zhanyoulvSp.getSelectedItem();
                itemStc.setIndexValueId(kvItem.getKey());
            }
        }

        // 保存查指标页面的数据
        service.saveCheckIndex(visitId, termId, calculateLst, proItemLst, noProIndexLst);

        // 获取产品组合是否达标值
        /*for (int k = 0; k < groupRg.getChildCount(); k++) {
            RadioButton groupRgChildRb = (RadioButton)groupRg.getChildAt(k);
            if (groupRgChildRb.isChecked()) {
            	vo.setIfrecstand(groupRgChildRb.getHint().toString());
                break;
            }
        }*/

        if (groupStatusSw.getStatus()) {
            vo.setIfrecstand("Y");// 产品组合  未达标
        } else {
            vo.setIfrecstand("N");// 产品组合  达标
        }
        // 保存产品组合是否达标
        service.saveMstGroupproductM(vo);


        // 遍历活动状态的达成情况
        SlideSwitch statusSw;
        EditText reachNum;
        for (int i = 0; i < promotionLst.size(); i++) {
            itemV = promotionLv.getChildAt(i);
            if (itemV == null || itemV.findViewById(R.id.promotion_sw_status) == null)
                continue;
            statusSw = (SlideSwitch) itemV.findViewById(R.id.promotion_sw_status);
            reachNum = (EditText) itemV.findViewById(R.id.promotion_et_reachnum);
            if (statusSw.getStatus()) {
                promotionLst.get(i).setIsAccomplish(ConstValues.FLAG_1);//达成
            } else {
                promotionLst.get(i).setIsAccomplish(ConstValues.FLAG_0);//未达成

                // 根据是否有拍照,删除
                // 删除记录
                String pictypekey = promotionLst.get(i).getPromotKey();
                AndroidDatabaseConnection connection = null;
                try {
                    DatabaseHelper helper = DatabaseHelper
                            .getHelper(getActivity());
                    MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper
                            .getMstCameraiInfoMDao();
                    connection = new AndroidDatabaseConnection(
                            helper.getWritableDatabase(), true);
                    connection.setAutoCommit(false);
                    // 删除该记录
                    dao.deletePicByCamerakeyATerminal(helper, pictypekey, termId);
                    connection.commit(null);

                } catch (Exception e) {
                    Log.e(TAG, "删除图片表记录失败", e);
                    try {
                        connection.rollback(null);
                    } catch (SQLException e1) {
                        Log.e(TAG, "删除图片表记录失败", e1);
                    }
                }
                // 删除本地照片(以为每次确定上传都会删除文件夹,就不做操作了)
            }
            promotionLst.get(i).setReachNum(reachNum.getText().toString());
        }
        service.savePromotion(visitId, termId, promotionLst);
        long time4 = new Date().getTime();
        Log.e("Optimization", "查指标执行数据库" + (time4 - time3));
    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClic()");
        switch (v.getId()) {

            // 快速采集按钮
            case R.id.checkindex_bt_quickcollect:
                if (ViewUtil.isDoubleClick(v.getId(), 2500))
                    return;
                qulicklyDialog();
                break;
            //
            case R.id.checkindex_bt_activitypic:
                if (ViewUtil.isDoubleClick(v.getId(), 2500))
                    return;
                // activityPicDialog();
                break;

            default:
                break;
        }
    }

    /**
     * 活动拍照
     * <p>
     * private void activityPicDialog() {
     * DbtLog.logUtils(TAG, "activityPicDialog()-跳到摄像头界面");
     * ViewUtil.clearDoubleClick();
     * <p>
     * cameraService = new CameraService(getActivity(), null);
     * // 已经拍了多少张照片
     * piclst = cameraService.queryCurrentPicRecord1(term.getTerminalkey(), DateUtil.getDateTimeStr(0), "1", "0", visitId);
     * <p>
     * ViewUtil.clearDoubleClick();
     * if (!FunUtil.cameraIsCanUse()) {// true
     * //cameraIsCanUse()
     * Toast.makeText(getActivity(), "请先开启拍照权限", Toast.LENGTH_SHORT).show();
     * return;
     * }
     * <p>
     * <p>
     * // 位置是从0起计算的
     * position = new CameraService(getActivity(), null).queryPictypeMAll().size();
     * <p>
     * if (hasPermission(GlobalValues.HARDWEAR_CAMERA_PERMISSION)) {
     * // 拥有了此权限,那么直接执行业务逻辑
     * toCamera2(position);
     * } else {
     * // 还没有对一个权限(请求码,权限数组)这两个参数都事先定义好
     * requestPermission(GlobalValues.HARDWEAR_CAMERA_CODE, GlobalValues.HARDWEAR_CAMERA_PERMISSION);
     * }
     * }
     */

    @Override
    public void doOpenCamera() {
        toCamera4();
    }

    /**
     * 拍照返回处理
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        DbtLog.logUtils(TAG, "onActivityResult()-拍照后照片处理");
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            // 每个促销活动拍照 返回处理
            case 800:
				/*Bundle extras = data.getExtras();
				Bitmap  mImageBitmap = (Bitmap) extras.get("data");  
				Bitmap bmp = FunUtil.zoomImg(mImageBitmap, 480, 640);*/

                if (resultCode == getActivity().RESULT_OK) {

                    idsuccess = -1;// -1 表示本次拍照图片还未保存到本地
                    Message message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);// 提示:图片正在保存,请稍后

                    new Thread() {

                        @Override
                        public void run() {
                            super.run();
                            //----
                            try {

                                // 获取照片文件路径
                                CameraImageBean cameraImageBean = CameraImageBean.getInstance();
                                String picname = cameraImageBean.getPicname();
                                Uri uri = cameraImageBean.getmPath();


                                // 裁剪后台所需大小
                                // Bitmap bmp = FunUtil.convertToBitmap(path + name, 480, 640);
                                Bitmap bmp = getBitmapFormUri(getContext(), uri);
								/*Bitmap bmp = PhotoUtil.readBitmapFromPath(getActivity(), path + name);
								bmp = FunUtil.scaleBitmap(bmp, 480, 640);*/
                                DbtLog.logUtils(TAG, "onActivityResult()-裁剪成功800");
                                // 删除图片(因为图片太大,物理删除)
                                if (bmp != null) {
                                    FileUtil.deleteFile(new File(path + name));
                                }

                                // 压缩
                                // 设置清晰度
                                int definition = 25;
                                bmp = FunUtil.compressBitmap(bmp, definition);// 压缩
                                String gonghao = PrefUtils.getString(getActivity(), "userGongHao", "");

                                if (pictypename.length() > 7) {
                                    pictypename = pictypename.substring(0, 7);
                                }

                                // 加水印
                                Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_picc);
                                waterBitmap = FunUtil.zoomImg(waterBitmap, ViewUtil.dip2px(getActivity(), 640), ViewUtil.dip2px(getActivity(), 30));
                                bmp = ImageUtil.createWaterMaskLeftBottom(getActivity(), bmp, waterBitmap, 0, 0);
                                bmp = ImageUtil.drawTextToLeftBottom(getActivity(), bmp, "日期: " + DateUtil.getDateTimeStr(3), 10, Color.WHITE, ViewUtil.dip2px(getActivity(), 5), ViewUtil.dip2px(getActivity(), 16));
                                //bmp = ImageUtil.drawTextToLeftBottom(getActivity(), bmp, "业代: "+ConstValues.loginSession.getUserGongHao(), 10, Color.WHITE, ViewUtil.px2dip(getActivity(),288), ViewUtil.dip2px(getActivity(),12));
                                bmp = ImageUtil.drawTextToLeftBottom(getActivity(), bmp, "业代: " + gonghao, 10, Color.WHITE, ViewUtil.px2dip(getActivity(), 288), ViewUtil.dip2px(getActivity(), 16));
                                bmp = ImageUtil.drawTextToLeftBottom(getActivity(), bmp, "终端: " + term.getTerminalname(), 10, Color.WHITE, ViewUtil.dip2px(getActivity(), 5), ViewUtil.dip2px(getActivity(), 3));
                                bmp = ImageUtil.drawTextToLeftBottom(getActivity(), bmp, "照片: " + pictypename, 10, Color.WHITE, ViewUtil.px2dip(getActivity(), 288), ViewUtil.dip2px(getActivity(), 3));

                                // 保存压缩
                                FunUtil.saveHeadImg(path, bmp, name, 100, definition);

                                // 添加到集合中
								/*cameraPicMap.put(positionTag, bmp);
								isCameraMap.put(positionTag, "1");*/
                                //gridAdapter.notifyDataSetChanged();
                                // 保存图片记录到到本地数据库表
                                MstCameraInfoM cameraListMStc = new MstCameraInfoM();
                                //PicFileUpM cameraListMStc = new PicFileUpM();

                                // 保存本地时 拍照图片主键不能相同
                                cameraListMStc.setCamerakey(FunUtil.getUUID());
                                cameraListMStc.setTerminalkey(termId);// #
                                cameraListMStc.setVisitkey(visitId);// #
                                //cameraListMStc.setPicname(ConstValues.loginSession.getUserGongHao()+"_"+name);// #
                                cameraListMStc.setPicname(gonghao + "_" + name);// #
                                cameraListMStc.setLocalpath(path + name);// #
                                cameraListMStc.setNetpath(path + name);// 该字段 无用
                                cameraListMStc.setCameradata(DateUtil.getDateTimeStr(0));// #
                                cameraListMStc.setIsupload("0");
                                cameraListMStc.setIstakecamera("1");
                                cameraListMStc.setPicindex(String.valueOf(picindex));
                                cameraListMStc.setPictypekey(pictypekey);
                                cameraListMStc.setPictypename(pictypename);
                                cameraListMStc.setSureup("1");// 确定上传 0确定上传 1未确定上传 会在点击确定时更改为0

                                // 将图片转成字符串
                                String imagefileString;
                                try {
                                    imagefileString = FileUtil.fileToString(path + name);
                                    cameraListMStc.setImagefileString(imagefileString);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // 插入图片表一条图片记录
                                DbtLog.logUtils(TAG, "将要插入表");
                                cameraService.saveCamera1(cameraListMStc);
                                DbtLog.logUtils(TAG, "saveCamera1-插入表正常");
                                //saveCamera(cameraListMStc);

                                // 若是重拍的,要删除该位置上一张图片及记录
                                // 从拍过的照片中查找,关于活动这张照片是否拍过,若拍过获取图片路径
                                String filepath = "";
                                if (piclst != null) {
                                    for (CameraDataStc cameraDataStc : piclst) {
                                        int n = 0;
                                        try {
                                            n = Integer.parseInt(cameraDataStc.getPicindex());
                                        } catch (NumberFormatException ex) {

                                        }
                                        if (n == picindex) {
                                            filepath = cameraDataStc.getLocalpath();
                                            break;
                                        }
                                    }
                                }
                                // 如果图片路径不为空,则表示促销活动图片已拍过,需要删除,只保留最新的图片及记录
                                if (!TextUtils.isEmpty(filepath)) {
                                    // 若是重拍的,要删除该位置上一张图片及记录
                                    FileUtil.deleteFile(new File(filepath));
                                    // 若是重拍的,要删除该位置的上一条记录
                                    //deleteLastRecord(position,DateUtil.getDateTimeStr(0),termId);
                                    if (piclst != null) {
                                        for (CameraDataStc cameraDataStc : piclst) {
                                            int n = 0;
                                            try {
                                                n = Integer.parseInt(cameraDataStc.getPicindex());
                                            } catch (NumberFormatException ex) {

                                            }
                                            if (n == picindex) {
                                                String localpath = cameraDataStc.getLocalpath();
                                                String camerakey = cameraDataStc.getCamerakey();

                                                AndroidDatabaseConnection connection = null;
                                                try {
                                                    DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
                                                    MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
                                                    connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
                                                    connection.setAutoCommit(false);
                                                    // 删除该角标的上一条记录
                                                    dao.deleteCameraRecord(helper, localpath, camerakey);
                                                    connection.commit(null);

                                                } catch (Exception e) {
                                                    Log.e(TAG, "删除图片表记录失败", e);
                                                    try {
                                                        connection.rollback(null);
                                                    } catch (SQLException e1) {
                                                        Log.e(TAG, "删除图片表记录失败", e1);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                // 重新读取当天已保存的图片记录
                                DbtLog.logUtils(TAG, "删除重拍正常");
                                piclst = cameraService.queryCurrentPicRecord1(termId, DateUtil.getDateTimeStr(0), "1", "0", visitId);
                                DbtLog.logUtils(TAG, "查询正常");
                                PrefUtils.putInt(getActivity(), "idsavesuccess", 1);// 2:开始保存照片, 1:保存成功

                                // 更新UI界面 刷新适配器
                                Message message1 = new Message();
                                message1.what = 4;
                                handler.sendMessage(message1);// 刷新UI

                                // 删除DCIM文件夹,因为Android5.0 之前的版本会在DCIM下生成照片
                                String DCIMPath = Environment.getExternalStorageDirectory() + "" + "/DCIM/";
                                FileUtil.deleteFile(new File(DCIMPath));

                                // 删除照片缓存 /DCIM/.thumbnails
                                FileUtil.deleteFile(new File(DCIMPath, ".thumbnails"));
                                File file = new File(DCIMPath, ".thumbnails");
                                try {
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // 重新扫描磁盘
                                /*Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI //ACTION_MEDIA_SCANNER_SCAN_FILE
                                String path = Environment.getExternalStorageDirectory() + "";
                                Uri newuri = Uri.fromFile(new File(path));
                                //Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                intent.setData(newuri);
                                getActivity().sendBroadcast(intent);*/

                            } catch (Exception e) {
                                // 更新UI界面 刷新适配器
                                Message message1 = new Message();
                                message1.what = 6;
                                handler.sendMessage(message1);// 刷新UI
                            }
                            //----

                        }
                    }.start();

                } else {
                    Toast.makeText(getActivity(), "拍照失败或初始化相机失败,请重新拍照", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }


    /**
     * 快速采集弹出窗口
     */
    public void qulicklyDialog() {
        DbtLog.logUtils(TAG, "qulicklyDialog()");
        // 如果弹出框已弹出，则不再弹出
        if (quicklyDialog != null && quicklyDialog.isShowing())
            return;

        // 加载弹出窗口layout
        itemForm = getActivity().getLayoutInflater().inflate(R.layout.checkindex_quicklydialog, null);
        quicklyDialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        quicklyDialog.setView(itemForm, 0, 0, 0, 0);
        quicklyDialog.show();
        Window dialogWindow = quicklyDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = 600;
        dialogWindow.setAttributes(lp);
        // 获取快速采集的数据源
        quicklyDialogLv = (LinearLayout) itemForm.findViewById(R.id.quicklydialog_lv);
        quicklyDialogLv.removeAllViews();
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                quicklyProItemLst = service.initQuicklyProItem(proItemLst);
                quicklyDialogLv.setOrientation(LinearLayout.VERTICAL);

                for (int i = 0; i < quicklyProItemLst.size(); i++) {
                    View layout = inflater.inflate(R.layout.checkindex_quicklydialog_lvitem1, null);
                    TextView indexNameTv = (TextView) layout.findViewById(R.id.quicklydialog_tv_itemname);
                    ListView proItemLv = (ListView) layout.findViewById(R.id.quicklydialog_lv_pro);
                    QuicklyProItem item = quicklyProItemLst.get(i);
                    indexNameTv.setHint(item.getItemId());
                    indexNameTv.setText(item.getItemName());
                    proItemLv.setAdapter(new QuicklyDialogItemAdapter(getActivity(), item.getProItemLst(), item.getItemId()));
                    ViewUtil.setListViewHeight(proItemLv);
                    quicklyDialogLv.addView(layout);
                }
            }
        }, 30);*/
        asynitem();


        // 确定
        Button sureBt = (Button) itemForm.findViewById(R.id.quicklydialog_bt_sure);
        sureBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (ViewUtil.isDoubleClick(arg0.getId(), 2500))
                    return;
                if (isasynitem)
                    return;
                QuicklyProItem itemI;
                ProItem itemJ;
                List<ProItem> itemJLst;
                ListView itemLv;
                EditText itemEt;
                EditText itemEt2;
                TextView itemTv;
                int isAllIn = 0;
                for (int i = 0; i < quicklyProItemLst.size(); i++) {
                    itemI = quicklyProItemLst.get(i);
                    itemJLst = itemI.getProItemLst();
                    itemLv = (ListView) quicklyDialogLv.getChildAt(i).findViewById(R.id.quicklydialog_lv_pro);
                    for (int j = 0; j < itemJLst.size(); j++) {
                        itemJ = itemJLst.get(j);

                        // 获取文本框的值
                        itemEt = (EditText) itemLv.getChildAt(j).findViewById(R.id.quicklydialog_et_changenum);
                        itemJ.setChangeNum((FunUtil.isBlankOrNullToDouble(itemEt.getText().toString())));
                        //itemJ.setChangeNum(Double.valueOf(FunUtil.isNullToZero(itemEt.getText().toString())));
                        itemJ.setBianhualiang(itemEt.getText().toString());// 现有量
                        itemEt2 = (EditText) itemLv.getChildAt(j).findViewById(R.id.quicklydialog_et_finalnum);
                        itemJ.setFinalNum((FunUtil.isBlankOrNullToDouble(itemEt2.getText().toString())));
                        //itemJ.setFinalNum(Double.valueOf(FunUtil.isNullToZero(itemEt2.getText().toString())));
                        itemJ.setXianyouliang(itemEt2.getText().toString());// 现有量
                        // 新鲜度
                        itemTv = (TextView) itemLv.getChildAt(j).findViewById(R.id.quicklydialog_et_xinxiandu);
                        itemJ.setFreshness(itemTv.getText().toString());

                        if ("".equals(FunUtil.isNullSetSpace(itemEt.getText().toString())) || "".equals(FunUtil.isNullSetSpace(itemEt2.getText().toString()))) {
                            isAllIn = 1;
                        }
                    }
                }
                if (isAllIn == 1) {
                    Toast.makeText(getActivity(), "所有的现有量,变化量必须填值(没货填0)", Toast.LENGTH_SHORT).show();
                    return;
                }
                ViewUtil.hideSoftInputFromWindow(getActivity(), arg0);
                quicklyDialog.cancel();

                handler.sendEmptyMessage(ConstValues.WAIT5);
            }
        });

        // 取消
        Button cancelBt = (Button) itemForm.findViewById(R.id.quicklydialog_bt_cancel);
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.hideSoftInputFromWindow(getActivity(), v);
                quicklyDialog.cancel();
            }
        });
    }

    /**
     * 异步加载
     */
    private Boolean isasynitem = true;

    public void asynitem() {
        DbtLog.logUtils(TAG, "asynch()");
        new AsyncTask<Void, Void, Void>() {
            protected void onPreExecute() {
                isasynitem = true;
            }

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            protected void onPostExecute(Void result) {
                quicklyProItemLst = service.initQuicklyProItem(proItemLst);
                quicklyDialogLv.setOrientation(LinearLayout.VERTICAL);

                for (int i = 0; i < quicklyProItemLst.size(); i++) {
                    View layout = inflater.inflate(R.layout.checkindex_quicklydialog_lvitem1, null);
                    TextView indexNameTv = (TextView) layout.findViewById(R.id.quicklydialog_tv_itemname);
                    NoScrollListView proItemLv = (NoScrollListView) layout.findViewById(R.id.quicklydialog_lv_pro);
                    QuicklyProItem item = quicklyProItemLst.get(i);
                    indexNameTv.setHint(item.getItemId());
                    indexNameTv.setText(item.getItemName());
                    proItemLv.setAdapter(new QuicklyDialogItemAdapter(getActivity(), item.getProItemLst(), item.getItemId()));
                    ViewUtil.setListViewHeight(proItemLv);
                    quicklyDialogLv.addView(layout);
                }
                isasynitem = false;
            }
        }.execute();
    }

    /**
     * 打开系统相机,去拍照

     private void toCamera2(int position) {
     DbtLog.logUtils(TAG, "toCamera2()-跳到系统摄像头界面");

     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
     // 根据文件地址创建文件

     String sdcardPath = Environment.getExternalStorageDirectory() + "";
     path = sdcardPath + "/dbt/et.tsingtaopad" + "/photo" + File.separator;
     //path = getActivity().getFilesDir().getAbsolutePath() + File.separator + "photo" + File.separator;
     name = DateUtil.formatDate(new Date(), null) + ".jpg";

     //saveHeadImg(path, bm, nam
     File file = new File(path + name);
     if (file.exists()) {
     file.delete();
     }
     // 把文件地址转换成Uri格式
     Uri uri = Uri.fromFile(file);
     // 设置系统相机拍摄照片完成后图片文件的存放地址
     intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
     startActivityForResult(intent, 100);
     }*/

    /**
     * 打开系统相机,去拍照
     */
    private void toCamera4() {
        DbtLog.logUtils(TAG, "toCamera4()-跳到系统摄像头界面");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 根据文件地址创建文件

        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        path = sdcardPath + "/dbt/et.tsingtaopad" + "/photo/";
        name = DateUtil.formatDate(new Date(), null) + ".jpg";

        File file = new File(path, name);
        DbtLog.logUtils(TAG, "创建File: " + path + name);


        Uri fileuri;
        // 兼容7.0及以上的写法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //intent = toCameraByFileProvider(intent,tempFile);
            //intent = toCameraByContentResolver(intent,tempFile,currentPhotoName);

            /*fileuri = toCameraByContentResolverUri(tempFile,currentPhotoName);
            CameraImageBean cameraImageBean = CameraImageBean.getInstance();
            cameraImageBean.setmPath(fileuri);
            cameraImageBean.setPicname(currentPhotoName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri);
            DELEGATE.startActivityForResult(intent, RequestCodes.TAKE_PHONE);*/

            intent = toCameraByContentResolver(intent, file, name);
            startActivityForResult(intent, 800);
        } else {
            fileuri = Uri.fromFile(file);// 将File转为Uri
            //CameraImageBean.getInstance().setmPath(fileUri);
            CameraImageBean cameraImageBean = CameraImageBean.getInstance();
            cameraImageBean.setmPath(fileuri);
            cameraImageBean.setPicname(name);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri);
            startActivityForResult(intent, 800);
        }
    }

}
