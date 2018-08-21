package et.tsingtaopad.visit.shopvisit.checkindex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.SoftInputUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProIndexValue;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：CaculateItemAdapter.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-13</br>
 * 功能描述: 巡店拜访-查指标的分项采集部分二级Adapter</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class CaculateItemAdapter extends BaseAdapter implements OnClickListener {

    private Activity context;
    private List<ProIndexValue> dataLst;
    private List<KvStc> indexValuelst;
    private List<ProItem> proItemLst;
    private String IndexId;

    // 弹出框
    private AlertDialog calculateDialog;
    private NoScrollListView calculateDialogLv;
    private List<ProItem> tempLst;
    //    private TextView proTv;

    /**
     * 构造函数
     *
     * @param context
     * @param dataLst       产品及指标值显示数据
     * @param indexValuelst 当前指标对应的指标值集合
     * @param proItemLst    当前产品下对应的采集项
     */
    public CaculateItemAdapter(Activity context, List<ProIndexValue> dataLst, List<KvStc> indexValuelst, List<ProItem> proItemLst, String IndexId) {
        this.context = context;
        this.dataLst = dataLst;
        this.indexValuelst = indexValuelst;
        this.IndexId = IndexId;
        if (CheckUtil.IsEmpty(proItemLst)) {
            this.proItemLst = new ArrayList<ProItem>();
        } else {
            this.proItemLst = proItemLst;
        }
    }

    @Override
    public int getCount() {
        if (CheckUtil.IsEmpty(dataLst)) {
            return 0;
        } else {
            return dataLst.size();
        }
    }

    @Override
    public Object getItem(int arg0) {
        if (CheckUtil.IsEmpty(dataLst)) {
            return null;
        } else {
            return dataLst.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("calculateItem ", position + "  " + DateUtil.getDateTimeStr(6));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            //convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_caculate_lvitem2, null);
            convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_caculate_lvitem2, parent, false);
            holder.proTv = (TextView) convertView.findViewById(R.id.caculate_tv_proname);
            holder.indexValueRg = (RadioGroup) convertView.findViewById(R.id.caculate_rg_indexvalue);//展示指标值集合
            holder.indexValueEt = (EditText) convertView.findViewById(R.id.caculate_et_indexvalue);
            holder.indexValueNumEt = (EditText) convertView.findViewById(R.id.caculate_et_indexvalue_num);
            holder.indexValueSp = (Button) convertView.findViewById(R.id.caculate_bt_indexvalue);//指标值集合
            holder.indexValueTv = (TextView) convertView.findViewById(R.id.caculate_tv_indexvalue);
            holder.calculateBt = (Button) convertView.findViewById(R.id.caculate_bt_caculate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ProIndexValue item = dataLst.get(position);
        holder.proTv.setHint(item.getProId());
        holder.proTv.setText(item.getProName());
        holder.calculateBt.setTag(position);
        holder.calculateBt.setOnClickListener(this);

        // 判定显示方式及初始化显示数据 0:计算单选、 1:单选、 2:文本框、 3:数值、 4:下拉单选
        if (ConstValues.FLAG_1.equals(item.getIndexType())) {
            holder.indexValueRg.setVisibility(View.VISIBLE);
            RadioButton rb;
            boolean isExist = false;
            for (KvStc kvItem : indexValuelst) {
                for (int i = 0; i < holder.indexValueRg.getChildCount(); i++) {
                    rb = (RadioButton) holder.indexValueRg.getChildAt(i);
                    if (kvItem.getKey().equals(rb.getHint().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    rb = new RadioButton(context);
                    rb.setTextColor(context.getResources()
                            .getColor(R.color.listview_item_font_color));
                    rb.setTextSize(17);
                    rb.setText(kvItem.getValue());
                    rb.setHint(kvItem.getKey());
                    holder.indexValueRg.addView(rb);
                }
            }

        } else if (ConstValues.FLAG_2.equals(item.getIndexType())) {
            holder.indexValueEt.setVisibility(View.VISIBLE);

        } else if (ConstValues.FLAG_3.equals(item.getIndexType())) {
            holder.indexValueNumEt.setVisibility(View.VISIBLE);

        } else if (ConstValues.FLAG_4.equals(item.getIndexType()) || ConstValues.FLAG_0.equals(item.getIndexType())) {
            //修改铺货状态指标变化量小于WEB配置的变化量结果显示为空白, 修改产品生动化变化量小于WEB配置的变化量值为不合格
            if (!CheckUtil.IsEmpty(indexValuelst) && "-1".equals(FunUtil.isBlankOrNullTo(item.getIndexValueId(), "-1"))) {
                /*item.setIndexValueId(indexValuelst.get(0).getKey());
                item.setIndexValueName(indexValuelst.get(0).getValue());*/
                item.setIndexValueId("313");// 什么都不是
                item.setIndexValueName("");
            }

            holder.indexValueSp.setVisibility(View.VISIBLE);
            holder.indexValueSp.setTag(item.getIndexValueId());
            holder.indexValueSp.setText(item.getIndexValueName());
            // 铺货状态 结果设置为不可手动更改(只能通过业代手填数据,经PAD自动计算得出铺货状态) 20160316
            if (!"ad3030fb-e42e-47f8-a3ec-4229089aab5d".equals(item.getIndexId())) {
                holder.indexValueSp.setOnClickListener(this);
            }

        } else {
            holder.indexValueTv.setVisibility(View.VISIBLE);

        }
        return convertView;
    }

    private class ViewHolder {
        private TextView proTv;
        private RadioGroup indexValueRg;
        private EditText indexValueEt;
        private EditText indexValueNumEt;
        private Button indexValueSp;
        private TextView indexValueTv;
        private Button calculateBt;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.caculate_bt_indexvalue:
                //单选弹出框
                DialogUtil.showSingleDialog((Activity) context, (Button) v, indexValuelst, new String[]{"key", "value", "isDefault"});
                break;

            case R.id.caculate_bt_caculate:
                if (calculateDialog != null && calculateDialog.isShowing())
                    return;

                // 加判断 是不是铺货状态,弹出不同列的弹窗
                if ("ad3030fb-e42e-47f8-a3ec-4229089aab5d".equals(IndexId)) {// 铺货状态

                    // 加载弹出窗口layout
                    View itemForm = context.getLayoutInflater().inflate(R.layout.checkindex_calculate_puhuo, null);

                    // 获取产品信息
                    int position = (Integer) v.getTag();
                    final ProIndexValue indexValue = dataLst.get(position);
                    TextView proTv = (TextView) itemForm.findViewById(R.id.calculatedialog_tv_proname);
                    proTv.setHint(indexValue.getProId());
                    proTv.setText(indexValue.getProName());


                    // 获取该产品采集项
                    tempLst = new ArrayList<ProItem>();
                    //TODO
                    for (ProItem item : proItemLst) {
                        if (indexValue.getProId().equals(item.getProId()) && item.getIndexIdLst().contains(indexValue.getIndexId())) {
                            tempLst.add(item);
                        }
                    }

                    calculateDialogLv = (NoScrollListView) itemForm.findViewById(R.id.calculatedialog_lv_pro);
                    calculateDialogLv.setAdapter(new CalculateIndexItemPuhuoAdapter(context, tempLst));

                    // 确定
                    Button sureBt = (Button) itemForm.findViewById(R.id.calculatedialog_bt_sure);
                    sureBt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            if (ViewUtil.isDoubleClick(arg0.getId(), 2500))
                                return;
                            // SoftInputUtil.hideSoftInput(context);
                            ProItem item = null;
                            EditText itemEt = null;
                            EditText itemEt2 = null;
                            TextView itemTv = null;
                            int isAllIn = 0;
                            for (int i = 0; i < tempLst.size(); i++) {
                                item = tempLst.get(i);
                                item.setCheckkey(indexValue.getIndexId());
                                // 获取采集文本框内容
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
                                Toast.makeText(context, "所有的现有量,变化量必须填值(没货填0)", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            calculateDialog.cancel();

                            // 自动计算
                            Bundle bundle = new Bundle();
                            if (item != null) {
                                bundle.putString("proId", item.getProId());
                                bundle.putString("indexId", indexValue.getIndexId());
                            }
                            Message msg = new Message();
                            msg.what = ConstValues.WAIT5;
                            msg.setData(bundle);
                            ConstValues.handler.sendMessage(msg);
                        }
                    });

                    // 取消
                    Button cancelBt = (Button) itemForm.findViewById(R.id.calculatedialog_bt_cancel);
                    cancelBt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calculateDialog.cancel();
                        }
                    });

                    // 实现化并弹出窗口
                    calculateDialog = new AlertDialog.Builder(context).setCancelable(false).create();
                    calculateDialog.setView(itemForm, 0, 0, 0, 0);
                    calculateDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        public void onShow(DialogInterface dialog) {
                            // SoftInputUtil.showSoftInput(context);
                        }
                    });
                    calculateDialog.show();

                } else {// 非铺货状态(比如:生动化,冰冻化...)

                    // 加载弹出窗口layout
                    View itemForm = context.getLayoutInflater().inflate(R.layout.checkindex_calculatedialog, null);

                    // 获取产品信息
                    int position = (Integer) v.getTag();
                    final ProIndexValue indexValue = dataLst.get(position);
                    TextView proTv = (TextView) itemForm.findViewById(R.id.calculatedialog_tv_proname);
                    proTv.setHint(indexValue.getProId());
                    proTv.setText(indexValue.getProName());

                    // 实现化并弹出窗口
                    calculateDialog = new AlertDialog.Builder(context).setCancelable(false).create();
                    calculateDialog.setView(itemForm, 0, 0, 0, 0);
                    calculateDialog.show();

                    // 获取该产品采集项
                    tempLst = new ArrayList<ProItem>();
                    //TODO
                    for (ProItem item : proItemLst) {
                        if (indexValue.getProId().equals(item.getProId()) && item.getIndexIdLst().contains(indexValue.getIndexId())) {
                            tempLst.add(item);
                        }
                    }

                    calculateDialogLv = (NoScrollListView) itemForm.findViewById(R.id.calculatedialog_lv_pro);
                    calculateDialogLv.setAdapter(new CalculateIndexItemAdapter(context, tempLst));

                    // 确定
                    Button sureBt = (Button) itemForm.findViewById(R.id.calculatedialog_bt_sure);
                    sureBt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            if (ViewUtil.isDoubleClick(arg0.getId(), 2500))
                                return;
                            ProItem item = null;
                            EditText itemEt = null;
                            EditText itemEt2 = null;
                            int isAllIn = 0;
                            for (int i = 0; i < tempLst.size(); i++) {
                                item = tempLst.get(i);
                                item.setCheckkey(indexValue.getIndexId());
                                // 获取采集文本框内容
                                itemEt = (EditText) calculateDialogLv.getChildAt(i).findViewById(R.id.calculatedialog_et_changenum);
                                item.setChangeNum((FunUtil.isBlankOrNullToDouble(itemEt.getText().toString())));
                                //item.setChangeNum(Double.valueOf(FunUtil.isNullToZero(itemEt.getText().toString())));
                                item.setBianhualiang(itemEt.getText().toString());// 变化量
                                itemEt2 = (EditText) calculateDialogLv.getChildAt(i).findViewById(R.id.calculatedialog_et_finalnum);
                                item.setFinalNum((FunUtil.isBlankOrNullToDouble(itemEt2.getText().toString())));
                                //item.setFinalNum(Double.valueOf(FunUtil.isNullToZero(itemEt.getText().toString())));
                                item.setXianyouliang(itemEt.getText().toString());// 现有量

                                //
                                if ("".equals(FunUtil.isNullSetSpace(itemEt.getText().toString())) || "".equals(FunUtil.isNullSetSpace(itemEt2.getText().toString()))) {
                                    isAllIn = 1;
                                }
                            }

                            if (isAllIn == 1) {
                                Toast.makeText(context, "所有的现有量,变化量必须填值(没货填0)", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            calculateDialog.cancel();

                            // 自动计算
                            Bundle bundle = new Bundle();
                            if (item != null) {
                                bundle.putString("proId", item.getProId());
                                bundle.putString("indexId", indexValue.getIndexId());
                            }
                            Message msg = new Message();
                            msg.what = ConstValues.WAIT5;
                            msg.setData(bundle);
                            ConstValues.handler.sendMessage(msg);
                        }
                    });

                    // 取消
                    Button cancelBt = (Button) itemForm.findViewById(R.id.calculatedialog_bt_cancel);
                    cancelBt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calculateDialog.cancel();
                        }
                    });
                }

                break;

            default:
                break;
        }

    }


}
