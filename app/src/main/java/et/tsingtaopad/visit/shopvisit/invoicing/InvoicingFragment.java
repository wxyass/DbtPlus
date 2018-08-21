package et.tsingtaopad.visit.shopvisit.invoicing;

import java.lang.ref.SoftReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.MyApplication;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.ListViewKeyValueAdapter;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.ui.loader.LatteLoader;
import et.tsingtaopad.visit.shopvisit.invoicing.domain.InvoicingStc;
import et.tsingtaopad.visit.shopvisit.invoicing.domain.MonthSum;
import et.tsingtaopad.visit.shopvisit.sayhi.SayHiFragment;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：InvoicingFragment.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-6</br>
 * 功能描述: 巡店拜访进销存Fragment</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("HandlerLeak")
//进销存fragment
public class InvoicingFragment extends BaseFragmentSupport implements OnClickListener, OnTouchListener {


    private final String TAG = "InvoicingFragment";

    private InvoicingService service;
    private ScrollView invoicingSv;
    private Button addRelationBt;
    private ListView askGoodsLv;
    private ListView checkGoodsLv;
    private InvoicingAskGoodsAdapter askAdapter;
    private InvoicingCheckGoodsAdapter checkAdapter;

    // 进销存数据源
    //InvoicingStc--进销存数据显示的数据结构
    private List<InvoicingStc> dataLst = new ArrayList<InvoicingStc>();
    private String visitId;
    private String termId;
    private String seeFlag;

    // 供货关系弹出框相关实例
    private AlertDialog productDialgo;
    private View itemForm;
    private KvStc agency;
    private KvStc product;
    private ListView agencyLv;
    private ListView productLv;
    private ListViewKeyValueAdapter agencyAdapter;
    private InvoicingProductAdapter productAdapter;
    private EditText channelPriceEt;
    private EditText sellPriceEt;
    private List<MonthSum> getmonthSumList;
    // 获取用户选择的产品(多选)
    private ArrayList<KvStc> products;

    MyHandler handler;

    //是否在加载数据
    private boolean isLoadingData = true;

    /*private Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstValues.WAIT1:
                    String content = bundle.getString("content");
                    List<MonthSum> monthSumList = JsonUtil.parseList(content, MonthSum.class);
                    if (!CheckUtil.IsEmpty(monthSumList) && !CheckUtil.IsEmpty(dataLst)) {
                        for (MonthSum monthItem : monthSumList) {
                            for (InvoicingStc stcItem : dataLst) {
                                if (monthItem.getProductkey().equals(stcItem.getProId())) {
                                    stcItem.setPrevNumSum(monthItem.getMonthTotal());
                                    break;
                                }
                            }
                        }

                        if (checkAdapter != null)
                            checkAdapter.notifyDataSetChanged();
                    }
            }
        }
    };*/

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<InvoicingFragment> fragmentRef;

        public MyHandler(InvoicingFragment fragment) {
            fragmentRef = new SoftReference<InvoicingFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            InvoicingFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            // 处理UI 变化
            Bundle bundle = msg.getData();
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstValues.WAIT1:
                    String content = bundle.getString("content");
                    fragment.dealMonthSum(content);
                    break;
                case 7:// 关闭进度框
                    LatteLoader.stopLoading(); // 若有进度条,关闭
                    break;

                default:
                    break;
            }
        }
    }

    private void dealMonthSum(String content) {
        List<MonthSum> monthSumList = JsonUtil.parseList(content, MonthSum.class);
        if (!CheckUtil.IsEmpty(monthSumList) && !CheckUtil.IsEmpty(dataLst)) {
            for (MonthSum monthItem : monthSumList) {
                for (InvoicingStc stcItem : dataLst) {
                    if (monthItem.getProductkey().equals(stcItem.getProId())) {
                        stcItem.setPrevNumSum(monthItem.getMonthTotal());
                        break;
                    }
                }
            }

            if (checkAdapter != null)
                checkAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.shopvisit_invoicing, container, false);
        DbtLog.logUtils(TAG, "onCreateView()");
        this.initView(view);
        // this.asynch();
        InvoicingInitTask task = new InvoicingInitTask();
        task.execute();
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
                // initData();
                isLoadingData = false;
            }

            ;

        }.execute();
    }

    /**
     * 异步加载
     */
    private class InvoicingInitTask extends AsyncTask<Void, Void, Void> {

        // 异步执行前
        protected void onPreExecute() {
            LatteLoader.showLoading(getActivity(), true);// 处理数据中 ,在InvoicingFragment的Handle中关闭
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

    private void initPreData() {
        //InvoicingService--进销存业务逻辑
        service = new InvoicingService(getActivity(), null);
        handler = new MyHandler(this);
    }

    private void initDoBackData() {
        DbtLog.logUtils(TAG, "initDoBackData()");

        // 获取参数
        Bundle bundle = getArguments();
        seeFlag = FunUtil.isBlankOrNullTo(bundle.getString("seeFlag"), "");
        visitId = bundle.getString("visitKey");
        termId = bundle.getString("termId");
        //删除重复拜访产品
        service.delRepeatVistProduct(visitId);
        //获取某次拜访的我品的进销存数据情况
        dataLst = service.queryMinePro(visitId, termId);

        //订单推荐Adapter(原先名字是核查进销存Adapter)
        checkAdapter = new InvoicingCheckGoodsAdapter(getActivity(), dataLst);//核查进销存
        //问货源Adapter
        askAdapter = new InvoicingAskGoodsAdapter(
                getActivity(), seeFlag, dataLst, termId, visitId, checkAdapter, askGoodsLv, checkGoodsLv);//问货源
    }

    private void initPostData() {

        //给订单推荐(原先是核查进销存)设置数据适配器
        checkGoodsLv.setAdapter(checkAdapter);
        //设置ListView的高度
        ViewUtil.setListViewHeight(checkGoodsLv);

        askGoodsLv.setAdapter(askAdapter);
        ViewUtil.setListViewHeight(askGoodsLv);

    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView()");
        //新增供货关系按钮
        addRelationBt = (Button) view.findViewById(R.id.invoicing_bt_addrelation);
        //问货源
        askGoodsLv = (ListView) view.findViewById(R.id.invoicing_lv_askgoods);
        //订单推荐(原是核查进销存)
        checkGoodsLv = (ListView) view.findViewById(R.id.invoicing_lv_checkgoods);
        invoicingSv = (ScrollView) view.findViewById(R.id.invoicing_scrollView);


        addRelationBt.setOnClickListener(this);
        invoicingSv.setOnTouchListener(this);
    }

    private void initData() {
        DbtLog.logUtils(TAG, "initData()");

        handler = new MyHandler(this);

        //InvoicingService--进销存业务逻辑
        service = new InvoicingService(getActivity(), null);

        // 获取参数
        Bundle bundle = getArguments();
        seeFlag = FunUtil.isBlankOrNullTo(bundle.getString("seeFlag"), "");
        visitId = bundle.getString("visitKey");
        termId = bundle.getString("termId");
        //删除重复拜访产品
        service.delRepeatVistProduct(visitId);
        //获取某次拜访的我品的进销存数据情况
        dataLst = service.queryMinePro(visitId, termId);
        //if(dataLst.size()>0){
        //请求网络获取本月合计数值
        //getmonthSum();
        //订单推荐Adapter(原先名字是核查进销存Adapter)
        checkAdapter = new InvoicingCheckGoodsAdapter(getActivity(), dataLst);//核查进销存
        //给订单推荐(原先是核查进销存)设置数据适配器
        checkGoodsLv.setAdapter(checkAdapter);
        //设置ListView的高度
        ViewUtil.setListViewHeight(checkGoodsLv);
        //问货源Adapter
        askAdapter = new InvoicingAskGoodsAdapter(
                getActivity(), seeFlag, dataLst, termId, visitId, checkAdapter, askGoodsLv, checkGoodsLv);//问货源
        askGoodsLv.setAdapter(askAdapter);
        ViewUtil.setListViewHeight(askGoodsLv);
        //}

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invoicing_bt_addrelation:
                DbtLog.logUtils(TAG, "添加产品");
                this.dialogProduct();
                break;

            default:
                break;
        }
    }


    /**
     * 产品列表弹出窗口
     */
    public void dialogProduct() {
        DbtLog.logUtils(TAG, "dialogProduct()");
        // 如果已打开，直接返回
        if (productDialgo != null && productDialgo.isShowing())
            return;

        // 加载弹出窗口layout
        itemForm = getActivity().getLayoutInflater().inflate(R.layout.shopvisit_addrelation, null);
        //设置对话框不能被取消
        productDialgo = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        //设置视图显示在对话框中
        productDialgo.setView(itemForm, 0, 0, 0, 0);
        productDialgo.show();

        agencyLv = (ListView) itemForm.findViewById(R.id.addrelation_lv_agency);
        productLv = (ListView) itemForm.findViewById(R.id.addrelation_lv_product);

        channelPriceEt = (EditText) itemForm.findViewById(R.id.addrelation_et_channelprice);
        sellPriceEt = (EditText) itemForm.findViewById(R.id.addrelation_et_sellprice);

        // 代理商
        agencyLv.setDividerHeight(0);
        if (!CheckUtil.IsEmpty(ConstValues.agencyMineLst)) {
            agency = ConstValues.agencyMineLst.get(0);
        }
        agencyAdapter = new ListViewKeyValueAdapter(getActivity(),
                ConstValues.agencyMineLst, new String[]{"key", "value"},
                new int[]{R.drawable.bg_agency_up, R.drawable.bg_agency_down});
        agencyLv.setAdapter(agencyAdapter);

        products = new ArrayList<KvStc>();
        // 选择不同的经销商,列出不同的产品表
        agencyLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                products = new ArrayList<KvStc>();
                agency = (KvStc) agencyLv.getItemAtPosition(position);
                if (!CheckUtil.IsEmpty(agency.getChildLst())) {
                    product = agency.getChildLst().get(0);
                }
                productAdapter = new InvoicingProductAdapter(
                        getActivity(), agency.getChildLst(), new String[]{"key", "value"},
                        new int[]{R.drawable.bg_product_up, R.drawable.bg_product_down});
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
        if (!CheckUtil.IsEmpty(ConstValues.agencyMineLst)) {
            productAdapter = new InvoicingProductAdapter(getActivity(),
                    ConstValues.agencyMineLst.get(0).getChildLst(), new String[]{"key", "value"},
                    new int[]{R.drawable.bg_product_up, R.drawable.bg_product_down});
            productLv.setAdapter(productAdapter);
        }


        productLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                product = (KvStc) productLv.getItemAtPosition(position);


                // 遍历用户选择的产品集合
                int count = 0;
                for (int i = 0; i < products.size(); i++) {
                    // 该产品用户选择过,数量加1
                    if (product.equals(products.get(i))) {
                        count++;
                    }
                }
                // 用户选择过就在集合中移除,没选择过就添加
                if (count == 1) {
                    products.remove(product);
                } else {
                    products.add(product);
                }

                //productAdapter.setSelectItemId(position);
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
                if (products.size() == 0) {
                    productDialgo.cancel();

                } else {
                    for (KvStc product : products) {

                        DbtLog.logUtils(TAG, "经销商key:" + agency.getKey() + "、经销商名称:" + agency.getValue() + "-->产品key：" + product.getKey() + "、产品名称：" + product.getValue());
                        List<String> proIdLst = FunUtil.getPropertyByName(dataLst, "proId", String.class);
                        if (proIdLst.contains(product.getKey())) {
                            DbtLog.logUtils(TAG, "产品重复提示");
                            Toast.makeText(getActivity(), getString(
                                    R.string.addrelation_msg_repetitionadd), Toast.LENGTH_LONG).show();
                        } else {
                            InvoicingStc supplyStc = new InvoicingStc();
                            supplyStc.setProId(product.getKey());
                            supplyStc.setProName(product.getValue());
                            supplyStc.setAgencyId(agency.getKey());
                            supplyStc.setAgencyName(agency.getValue());
                            supplyStc.setChannelPrice(channelPriceEt.getText().toString());
                            supplyStc.setSellPrice(sellPriceEt.getText().toString());
                            supplyStc.setPrevStore("0");
                            dataLst.add(supplyStc);
                            //新增供货关系指标记录表进行更新
                            service.updateMstcheckexerecordInfoTemp(visitId, product.getKey());
                            askAdapter.setDelPosition(-1);
                            askAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(askGoodsLv);

                            checkAdapter.setDelPosition(-1);
                            checkAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(checkGoodsLv);
                            productDialgo.cancel();
                        }

                    }
                }
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int scrollY = v.getScrollY();
                int height = v.getHeight();
                int scrollViewMeasuredHeight = invoicingSv.getChildAt(0).getMeasuredHeight();
                if ((scrollY + height) == scrollViewMeasuredHeight) {
                    System.out.println("滑动到了底部 scrollY=" + scrollY);
                    //隐藏软键盘
                    ViewUtil.hideSoftInputFromWindow(getActivity(), v);
                }
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");
        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag))
            return;

        if (isLoadingData)
            return;

        View view;
        EditText itemEt;
        Button itemBtn;
        InvoicingStc item;
        for (int i = 0; i < dataLst.size(); i++) {
            item = dataLst.get(i);
            view = askGoodsLv.getChildAt(i);
            if (view != null) {
                itemEt = (EditText) view.findViewById(R.id.askgoods_et_channelprice);//渠道价

                String content = itemEt.getText().toString();
                item.setChannelPrice(FunUtil.getDecimalsData(content));
                //item.setChannelPrice(itemEt.getText().toString());

                itemEt = (EditText) view.findViewById(R.id.askgoods_et_sellproce);// 零售价
                content = itemEt.getText().toString();
                item.setSellPrice(FunUtil.getDecimalsData(content));
                //item.setSellPrice(itemEt.getText().toString());
            }
            view = checkGoodsLv.getChildAt(i);
            if (view != null) {
                //itemBtn = (Button)view.findViewById(R.id.checkgoods_et_firstdate);
                //item.setFristdate(itemBtn.getText().toString());
                itemEt = (EditText) view.findViewById(R.id.checkgoods_et_prevnum);// 订单量
                item.setPrevNum(itemEt.getText().toString());
                //item.setPrevNum(FunUtil.isBlankOrNullToDouble(itemEt.getText().toString()));
                itemEt = (EditText) view.findViewById(R.id.checkgoods_et_daysell);// 日销量
                item.setDaySellNum(itemEt.getText().toString());

                itemEt = (EditText) view.findViewById(R.id.checkgoods_et_addcard);// 累计卡
                item.setAddcard(itemEt.getText().toString());

            }
        }
        service.saveInvoicing(dataLst, visitId, termId);
    }

    public void getmonthSum() {

        HttpUtil httpUtil = new HttpUtil(60 * 1000);
        httpUtil.configResponseTextCharset("ISO-8859-1");
        StringBuffer buffer = new StringBuffer();
        buffer.append("{terminalkey:'").append(termId).append("'}");
        httpUtil.send("opt_get_sum", buffer.toString(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                ResponseStructBean resObj = HttpUtil
                        .parseRes(responseInfo.result);
                if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                    String content = resObj.getResBody().getContent();

                    //绑定ListView数据
                    //List<MonthSum> dataLst = JsonUtil.parseList(content, MonthSum.class);
                    Message message = new Message();
                    message.what = ConstValues.WAIT1;
                    Bundle bundle = new Bundle();
                    bundle.putString("content", content);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {

            }
        });
    }
}
