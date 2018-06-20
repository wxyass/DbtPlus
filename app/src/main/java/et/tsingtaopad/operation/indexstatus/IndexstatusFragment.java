package et.tsingtaopad.operation.indexstatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.db.tables.MstCheckmiddleInfo;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.JsonUtil;

/**
 * 项目名称：营销移动智能工作平台</br> 文件名：IndexstatusFragment.java</br> 作者：@吴欣伟</br>
 * 创建时间：2013/11/26</br> 功能描述: 指标状态查询页面</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
@SuppressLint("HandlerLeak")
public class IndexstatusFragment extends BaseFragmentSupport implements
        OnClickListener, TextWatcher, OnItemSelectedListener {

    private IndexstatusService service;
    private List<MstRouteM> linelst;
    private List<KvStc> dialogProLst;

    private AlertDialog dialog;
    private Button backBt;
    private TextView titleTv;

    private Button productTv;
    private TextView dateTv;
    private ImageButton searchBt;
    private TextView lineDateTv;
    private TextView gridDateTv;
    private TextView lineTableTv;
    private Spinner lineSp;
    private Spinner indexSp;
    private Spinner valueSp;
    private ListView linelv;
    private ListView gridlv;
    private String  nowdate;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();

            super.handleMessage(msg);
            switch (msg.what) {

            // 提示信息
            case ConstValues.WAIT1:
                Toast.makeText(getActivity(), bundle.getString("msg"),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                break;

            // 查询网络数据库返回对象
            case ConstValues.WAIT2:
                dealDateShow(ConstValues.FLAG_1, bundle);
                dialog.dismiss();
                break;
                
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.operation_indexstatus, null);
        initView(view);
        initData();
        return view;
    }

    /**
     * 初始化界面组件
     */
    private void initView(View view) {

        // 绑定界面组件
        dialog = DialogUtil.progressDialog(getActivity(),
                R.string.dialog_msg_search);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        backBt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
        titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
        searchBt = (ImageButton) view.findViewById(R.id.indexstatus_ib_search);
        dateTv = (TextView) view.findViewById(R.id.indexstatus_tv_date);
        lineDateTv = (TextView) view
                .findViewById(R.id.indexstatus_tv_linepart_date);
        gridDateTv = (TextView) view
                .findViewById(R.id.indexstatus_tv_gridpart_date);
        lineTableTv = (TextView) view
                .findViewById(R.id.indexstatus_tv_linepart);
        productTv = (Button) view
                .findViewById(R.id.indexstatus_tv_select_product);
        lineSp = (Spinner) view.findViewById(R.id.indexstatus_sp_line);
        indexSp = (Spinner) view.findViewById(R.id.indexstatus_sp_index);
        valueSp = (Spinner) view.findViewById(R.id.indexstatus_sp_value);
        linelv = (ListView) view.findViewById(R.id.indexstatus_lv_linepart);
        gridlv = (ListView) view.findViewById(R.id.indexstatus_lv_gridpart);
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);

        // 绑定事件
        view.setOnClickListener(null);
        backBt.setOnClickListener(this);
        dateTv.setOnClickListener(this);
        dateTv.addTextChangedListener(this);
        productTv.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        lineSp.setOnItemSelectedListener(this);
        indexSp.setOnItemSelectedListener(this);
        valueSp.setOnItemSelectedListener(this);
    }

    /**
     * 初始化界面数据
     */
    private void initData() {

        service = new IndexstatusService(getActivity(), handler);
        dialogProLst = service.getIndexProData();
       
        // 页面显示数据初始化
        titleTv.setText(R.string.indexstatus_banner_title);
        nowdate = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        dateTv.setText(nowdate);
        lineDateTv.setText(nowdate);
        gridDateTv.setText(nowdate);

        // 所属线路
        linelst = new ArrayList<MstRouteM>(ConstValues.lineLst);
        if (!CheckUtil.IsEmpty(linelst)) {
            linelst.remove(0);
        }
        SpinnerKeyValueAdapter lineSpAdapter = new SpinnerKeyValueAdapter(
                getActivity(), linelst,
                new String[] { "routekey", "routename" }, null);
        lineSp.setAdapter(lineSpAdapter);

        // 指标状态
        List<KvStc> indexlst = service.queryCheckTypeStatus();
        if (!CheckUtil.IsEmpty(indexlst)) {
            indexlst.remove(0);
        }
        SpinnerKeyValueAdapter indexSpAdapter = new SpinnerKeyValueAdapter(
                getActivity(), indexlst, new String[] { "key", "value" }, null);
        indexSp.setAdapter(indexSpAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.banner_navigation_rl_back:
        case R.id.banner_navigation_bt_back:
            this.getFragmentManager().popBackStack();
            break;

        case R.id.indexstatus_tv_date:
            service.showDatePicDialog(getActivity(), dateTv);
            break;

        case R.id.indexstatus_ib_search:
        	lineDateTv.setText(dateTv.getText().toString());
    		gridDateTv.setText(dateTv.getText().toString());
            searchIndex();
            break;

        case R.id.indexstatus_tv_select_product:
            DialogUtil.showMultipleDialog(getActivity(), productTv,
                    R.string.indexstatus_dialogpro_title, dialogProLst,
                    new String[] { "key", "value" }, null,
                    R.string.indexstatus_bt_prolst);
            break;
        default:
            break;
        }
    }

    //此方法是监听searchBt按钮   只要一触发次按钮可直接让serchbt选择的时间直接显示在lineDateTv,gridDateTv控件上
    @Override
    public void afterTextChanged(Editable s) {
    	
//    		lineDateTv.setText(s.toString());
//    		gridDateTv.setText(s.toString());

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int arg2,
            long arg3) {
        Spinner sp = (Spinner) parent;
        if (view != null) {
            sp.setTag(((TextView) view).getHint());
        } else {
            sp.setTag("");
        }// 
        switch (sp.getId()) {
        case R.id.indexstatus_sp_line:
            if (!CheckUtil.isBlankOrNull(sp.getTag().toString())) {
                lineTableTv.setText(linelst.get((int) sp.getSelectedItemId())
                        .getRoutename());
            }
            break;

        case R.id.indexstatus_sp_index:
            List<KvStc> valueLst = new ArrayList<KvStc>(
                    ((KvStc) indexSp.getSelectedItem()).getChildLst());
            SpinnerKeyValueAdapter valueSpAdapter = new SpinnerKeyValueAdapter(
                    getActivity(), valueLst, new String[] { "key", "value" },
                    null);
            valueSp.setAdapter(valueSpAdapter);
            break;

        default:
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    private void searchIndex() {

        // 线路参数
        String lineId = null;
        if (lineSp.getTag() != null) {
            lineId = lineSp.getTag().toString();
        }

        // 时间参数
        String searchDate = dateTv.getText().toString().replace("-", "");

        // 指标及指标值参数
        String checkId = null;
        if (indexSp.getTag() != null) {
            checkId = indexSp.getTag().toString();
        }
        String valueId = null;
        if (valueSp.getTag() != null) {
            valueId = valueSp.getTag().toString();
        }

        // 查询产品
        String[] productId = null;
        if (productTv.getTag() != null) {
            productId = productTv.getTag().toString().split(",");
        } else if (!CheckUtil.IsEmpty(dialogProLst)) {
            productId = FunUtil.getPropertyByName(dialogProLst, "key",
                    String.class).toArray(new String[] {});
        }
        service.searchIndex(lineId, searchDate, checkId, valueId, productId,
                dialog);
    }

    /**
     * 处理查询结果的显示
     * 
     * @param typeFlag
     *            0:本地数据查询， 1:网络数据查询
     * @param bundle
     */
    private void dealDateShow(String typeFlag, Bundle bundle) {

        // 线路
        String json = bundle.getString("dataLst");
        if (JsonUtil.getByKey(json, "indexlinelist") != null) {
            List<MstCheckmiddleInfo> lineIndexLst = JsonUtil.parseList(JsonUtil
                    .getByKey(json, "indexlinelist").toString(),
                    MstCheckmiddleInfo.class);
            IndexstatusNetAdapter lineAdapter = new IndexstatusNetAdapter(
                    getActivity(), lineIndexLst);
            linelv.setAdapter(lineAdapter);
        }

        // 定格
        if (JsonUtil.getByKey(json, "indexgridIdlist") != null) {
            List<MstCheckmiddleInfo> gridIndexLst = JsonUtil.parseList(JsonUtil
                    .getByKey(json, "indexgridIdlist").toString(),
                    MstCheckmiddleInfo.class);
            IndexstatusNetAdapter gridAdapter = new IndexstatusNetAdapter(
                    getActivity(), gridIndexLst);
            gridlv.setAdapter(gridAdapter);
        }
    }
}
