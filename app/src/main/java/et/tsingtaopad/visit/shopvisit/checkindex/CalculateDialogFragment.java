package et.tsingtaopad.visit.shopvisit.checkindex;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.ui.loader.LatteLoader;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProIndexValue;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：CalculateDialogFragment.java</br> 作者：吴欣伟 </br>
 * 创建时间：2013-12-19</br> 功能描述: 单项采集</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
// 单项采集
public class CalculateDialogFragment extends DialogFragment implements OnClickListener {

    private TextView proTv;
    private NoScrollListView calculateDialogLv;
    private Button sureBt;
    private Button cancelBt;

    private ProIndexValue indexValue;
    private List<ProItem> tempLst;

    /***
     *
     */
    public static CalculateDialogFragment getInstance(ProIndexValue indexValue, List<ProItem> tempLst) {
        CalculateDialogFragment dialog = new CalculateDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("indexValue", indexValue);
        bundle.putSerializable("tempLst", (Serializable) tempLst);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*View view = LayoutInflater.from(getActivity()).inflate(R.layout.checkindex_calculate_puhuo, null);
        initView(view);
        initDate();
        return view;*/
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.checkindex_calculate_puhuo, null);
        initView(view);
        initDate();
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 初始化控件
     *
     * @param view 界面View
     */
    private void initView(View view) {

        // 界面组件初始化
        proTv = (TextView) view.findViewById(R.id.calculatedialog_tv_proname);
        calculateDialogLv = (NoScrollListView) view.findViewById(R.id.calculatedialog_lv_pro);

        // 确定
        sureBt = (Button) view.findViewById(R.id.calculatedialog_bt_sure);
        // 取消
        cancelBt = (Button) view.findViewById(R.id.calculatedialog_bt_cancel);

        sureBt.setOnClickListener(this);
        cancelBt.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        // 获取参数
        Bundle bundle = getArguments();
        indexValue = (ProIndexValue) bundle.getSerializable("indexValue");
        tempLst = (List<ProItem>) bundle.getSerializable("tempLst");
        proTv.setHint(indexValue.getProId());
        proTv.setText(indexValue.getProName());
        calculateDialogLv.setAdapter(new CalculateIndexItemPuhuoAdapter(getActivity(), tempLst));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculatedialog_bt_sure:// 确定
                if (ViewUtil.isDoubleClick(v.getId(), 2500))
                    return;
                ProItem item = null;
                EditText itemEt = null;
                EditText itemEt2 = null;
                TextView itemTv = null;
                int isAllIn = 0;
                for (int i = 0; i < tempLst.size(); i++) {
                    item = tempLst.get(i);
                    item.setCheckkey(indexValue.getIndexId());
                    // 获取采集文本框内容//
                    itemEt = (EditText) calculateDialogLv.getChildAt(i).findViewById(R.id.calculatedialog_et_changenum);
                    item.setChangeNum((FunUtil.isBlankOrNullToDouble(itemEt.getText().toString())));
                    //item.setChangeNum(Double.valueOf(FunUtil.isNullToZero(itemEt.getText().toString())));
                    item.setBianhualiang(itemEt.getText().toString());// 变化量
                    itemEt2 = (EditText) calculateDialogLv.getChildAt(i).findViewById(R.id.calculatedialog_et_finalnum);
                    item.setFinalNum((FunUtil.isBlankOrNullToDouble(itemEt2.getText().toString())));
                    //item.setFinalNum(Double.valueOf(FunUtil.isNullToZero(itemEt.getText().toString())));
                    item.setXianyouliang(itemEt.getText().toString());// 现有量

                    itemTv = (TextView) calculateDialogLv.getChildAt(i).findViewById(R.id.calculatedialog_et_xinxiandu);
                    item.setFreshness(FunUtil.isNullToZero(itemTv.getText().toString()));

                    //
                    if ("".equals(FunUtil.isNullSetSpace(itemEt.getText().toString())) || "".equals(FunUtil.isNullSetSpace(itemEt2.getText().toString()))) {
                        isAllIn = 1;
                    }
                }

                if (isAllIn == 1) {
                    Toast.makeText(getActivity(), "所有的现有量,变化量必须填值(没货填0)", Toast.LENGTH_SHORT).show();
                    return;
                }

                getDialog().dismiss();

                // 自动计算
                Bundle bundle = new Bundle();
                if (item != null) {
                    bundle.putString("proId", item.getProId());
                    bundle.putString("indexId", indexValue.getIndexId());
                }

                 LatteLoader.showLoading(getActivity(), true);// 处理数据中 ,在Handle中关闭
                Message msg = new Message();
                msg.what = ConstValues.WAIT5;
                msg.setData(bundle);
                ConstValues.handler.sendMessage(msg);
                break;
            case R.id.calculatedialog_bt_cancel:// 取消
                getDialog().dismiss();
                break;

            default:
                break;
        }
    }
}
