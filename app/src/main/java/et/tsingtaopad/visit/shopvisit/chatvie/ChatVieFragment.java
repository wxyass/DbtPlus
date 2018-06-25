package et.tsingtaopad.visit.shopvisit.chatvie;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.ListViewKeyValueAdapter;
import et.tsingtaopad.db.tables.MstCmpagencyInfo;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.chatvie.domain.ChatVieStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：ChatVieFragment.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-19</br>
 * 功能描述: 巡店拜访-聊竞品</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class ChatVieFragment extends BaseFragmentSupport implements OnClickListener {


    private final String TAG = "ChatVieFragment";

    private ChatVieService service;
    private Button addRelationBt;
    private ListView vieStatusLv;
    private ListView vieSourceLv;
    private VieStatusAdapter statusAdapter;
    private VieSourceAdapter sourceAdapter;
    private RadioGroup clearVieRg;
    private EditText remarkEt;

    private List<ChatVieStc> dataLst;
    private String visitId;
    private String termId;
    private MstVisitM visitM;
    private String seeFlag;

    // 供货关系弹出框相关实例
    private AlertDialog productDialgo;
    private View itemForm;
    private KvStc agency;
    private KvStc product;
    private ListView agencyLv;
    private ListView productLv;
    private ListViewKeyValueAdapter agencyAdapter;
    private ChatVieProductAdapter productAdapter;
    private EditText channelPriceEt;
    private EditText sellPriceEt;

    // 获取用户选择的竞品(多选)
    private ArrayList<KvStc> products;

    //是否在加载数据
    private boolean isLoadingData = true;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        // View view = inflater.inflate(R.layout.shopvisit_chatvie, null);
        View view = inflater.inflate(R.layout.shopvisit_chatvie, container, false);
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
        DbtLog.logUtils(TAG, "initView()");
        addRelationBt = (Button) view.findViewById(R.id.chatvie_bt_addrelation);
        vieStatusLv = (ListView) view.findViewById(R.id.chatvie_lv_viestatus);
        vieSourceLv = (ListView) view.findViewById(R.id.chatvie_lv_viesource);
        clearVieRg = (RadioGroup) view.findViewById(R.id.chatvie_rg_clearvie);
        remarkEt = (EditText) view.findViewById(R.id.chatvie_et_visitreport);

        addRelationBt.setOnClickListener(this);
        clearVieRg.setOnClickListener(this);
        clearVieRg.getChildAt(0).setOnClickListener(this);
        ((RadioButton) clearVieRg.getChildAt(1)).setOnClickListener(this);
    }

    private void initData() {
        DbtLog.logUtils(TAG, "initData()");
        service = new ChatVieService(getActivity(), null);

        // 获取参数
        Bundle bundle = getArguments();
        seeFlag = FunUtil.isBlankOrNullTo(bundle.getString("seeFlag"), "");
        visitId = bundle.getString("visitKey");
        termId = bundle.getString("termId");
        service.delRepeatVistProduct(visitId);
        dataLst = service.queryViePro(visitId);
        statusAdapter = new VieStatusAdapter(getActivity(), dataLst);//竞品情况
        vieStatusLv.setAdapter(statusAdapter);
        ViewUtil.setListViewHeight(vieStatusLv);

        List<MstCmpagencyInfo> agencyLst = service.queryCmpAgency();
        sourceAdapter = new VieSourceAdapter(
                getActivity(), seeFlag, dataLst, termId, statusAdapter, agencyLst, vieStatusLv, vieSourceLv);//竞品来源
        vieSourceLv.setAdapter(sourceAdapter);
        ViewUtil.setListViewHeight(vieSourceLv);

        // 获取拜访主表信息的拜访记录
        visitM = service.findVisitById(visitId);
        if (visitM != null) {
            if (ConstValues.FLAG_1.equals(visitM.getIscmpcollapse())) {
                clearVieRg.check(R.id.chatvie_rb_clearvie_true);
            } else if (ConstValues.FLAG_0.equals(visitM.getIscmpcollapse())) {
                clearVieRg.check(R.id.chatvie_rb_clearvie_false);
            } else {
                clearVieRg.check(R.id.chatvie_rb_clearvie_false);
            }
            remarkEt.setText(visitM.getRemarks());
        } else {
            clearVieRg.check(R.id.chatvie_rb_clearvie_false);
        }
    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick()");
        switch (v.getId()) {
            case R.id.chatvie_bt_addrelation:
                this.dialogProduct();
                break;

            case R.id.chatvie_rb_clearvie_true:
                ViewUtil.initRadioButton2(clearVieRg, v.getId());
                break;

            case R.id.chatvie_rb_clearvie_false:
                ViewUtil.initRadioButton2(clearVieRg, v.getId());
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
        itemForm = getActivity().getLayoutInflater()
                .inflate(R.layout.shopvisit_addrelation, null);
        productDialgo = new AlertDialog.Builder(
                getActivity()).setCancelable(false).create();
        productDialgo.setView(itemForm, 0, 0, 0, 0);
        productDialgo.show();

        agencyLv = (ListView) itemForm.findViewById(R.id.addrelation_lv_agency);
        productLv = (ListView) itemForm.findViewById(R.id.addrelation_lv_product);
        channelPriceEt = (EditText) itemForm.findViewById(R.id.addrelation_et_channelprice);
        sellPriceEt = (EditText) itemForm.findViewById(R.id.addrelation_et_sellprice);

        // 代理商
        agencyLv.setDividerHeight(0);
        if (!CheckUtil.IsEmpty(ConstValues.agencyVieLst)) {
            agency = ConstValues.agencyVieLst.get(0);
        }
        agencyAdapter = new ListViewKeyValueAdapter(getActivity(),
                ConstValues.agencyVieLst, new String[]{"key", "value"},
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
                productAdapter = new ChatVieProductAdapter(
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
        if (!CheckUtil.IsEmpty(ConstValues.agencyVieLst)) {
            productAdapter = new ChatVieProductAdapter(getActivity(),
                    ConstValues.agencyVieLst.get(0).getChildLst(), new String[]{"key", "value"},
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
                if (product == null) {
                    productDialgo.cancel();
                } else {

                    for (KvStc product : products) {


                        DbtLog.logUtils(TAG, "经销商key:" + agency.getKey() + "、经销商名称:" + agency.getValue() + "-->产品key：" + product.getKey() + "、产品名称：" + product.getValue());
                        List<String> proIdLst = FunUtil.getPropertyByName(dataLst, "proId", String.class);
                        if (proIdLst.contains(product.getKey())) {
                            ViewUtil.sendMsg(getActivity(), R.string.addrelation_msg_repetitionadd);

                        } else {
                            ChatVieStc supplyStc = new ChatVieStc();
                            supplyStc.setProId(product.getKey());
                            supplyStc.setProName(product.getValue());
                            supplyStc.setCommpayId(agency.getKey());
                            supplyStc.setChannelPrice(channelPriceEt.getText().toString());
                            supplyStc.setSellPrice(sellPriceEt.getText().toString());
                            dataLst.add(supplyStc);

                            statusAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(vieStatusLv);
                            sourceAdapter.notifyDataSetChanged();
                            ViewUtil.setListViewHeight(vieSourceLv);
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
        ChatVieStc item;
        // 遍历LV,获取采集数据
        for (int i = 0; i < dataLst.size(); i++) {
            item = dataLst.get(i);
            view = vieSourceLv.getChildAt(i);
            if (view == null)
                continue;

            // 聊竞品-进店价
            itemEt = (EditText) view.findViewById(R.id.viesource_et_channelprice);
            String content = itemEt.getText().toString();
            item.setChannelPrice(FunUtil.getDecimalsData(content));
            //item.setChannelPrice(itemEt.getText().toString());

            // 聊竞品-零售价
            itemEt = (EditText) view.findViewById(R.id.viesource_et_sellprice);
            content = itemEt.getText().toString();
            item.setSellPrice(FunUtil.getDecimalsData(content));
            //item.setSellPrice(itemEt.getText().toString());

            itemEt = (EditText) view.findViewById(R.id.viesource_et_agency);
            item.setAgencyName(itemEt.getText().toString());

            view = vieStatusLv.getChildAt(i);
            if (view == null)
                continue;
            itemEt = (EditText) view.findViewById(R.id.viestatus_et_currstore);
            item.setCurrStore(itemEt.getText().toString());
            itemEt = (EditText) view.findViewById(R.id.viestatus_et_monthsell);
            item.setMonthSellNum(itemEt.getText().toString());
            itemEt = (EditText) view.findViewById(R.id.viestatus_et_describle);
            item.setDescribe(itemEt.getText().toString());
        }

        // 拜访主表相关数据
        if (clearVieRg.getCheckedRadioButtonId()
                == R.id.chatvie_rb_clearvie_true) {
            visitM.setIscmpcollapse(ConstValues.FLAG_1);
        } else {
            visitM.setIscmpcollapse(ConstValues.FLAG_0);
        }
        visitM.setRemarks(remarkEt.getText().toString());

        service.saveVie(dataLst, visitId, termId, visitM);
    }
}
