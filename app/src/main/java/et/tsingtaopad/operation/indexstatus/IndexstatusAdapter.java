package et.tsingtaopad.operation.indexstatus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.operation.indexstatus.domain.IndexStatusStc;
import et.tsingtaopad.tools.CheckUtil;

/**
 * 项目名称：营销移动智能工作平台</br> 文件名：IndexstatusAdapter.java</br> 作者：@吴欣伟</br>
 * 创建时间：2013/11/26</br> 功能描述: 指标状态查询Adapter</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
public class IndexstatusAdapter extends BaseAdapter {

    private Context context;
    private List<IndexStatusStc> dataLst;
    private int totalNum;
    private String typeFlag;
    private double prevRate = 0.0d;
    private double currRate = 0.0d;

    public IndexstatusAdapter(Context context, List<IndexStatusStc> dataLst,
            int totalNum, String typeFlag) {
        this.context = context;
        this.dataLst = dataLst;
        this.totalNum = totalNum;
        this.typeFlag = typeFlag;
    }

    @Override
    public int getCount() {

        if (CheckUtil.IsEmpty(this.dataLst)) {
            return 0;

        } else {
            return this.dataLst.size();
        }
    }

    @Override
    public Object getItem(int arg0) {

        if (CheckUtil.IsEmpty(this.dataLst)) {
            return null;

        } else {
            return this.dataLst.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.operation_indexstatus_lvitem, null);
            holder = new ViewHolder();
            holder.product = (TextView) convertView
                    .findViewById(R.id.indexstatus_tv_item_pro);
            holder.oldRate = (TextView) convertView
                    .findViewById(R.id.indexstatus_tv_item_prorate);
            holder.proRate = (TextView) convertView
                    .findViewById(R.id.indexstatus_tv_item_date);
            holder.change = (TextView) convertView
                    .findViewById(R.id.indexstatus_tv_item_change);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        IndexStatusStc info = dataLst.get(position);
        holder.product.setText(info.getProName());

        // 线路
        if (ConstValues.FLAG_0.equals(typeFlag)) {
            if (info.getLinePrevNum() == 0 || totalNum == 0) {
                prevRate = 0d;
                holder.oldRate.setText("-");
            } else {
                prevRate = (double) info.getLinePrevNum() / totalNum * 100.0d;
                holder.oldRate.setText(String.format("%.2f", prevRate) + "%");
            }
            if (info.getLineCurrNum() == 0 || totalNum == 0) {
                currRate = 0f;
                holder.proRate.setText("-");
            } else {
                currRate = (double) info.getLineCurrNum() / totalNum * 100.0d;
                holder.proRate.setText(String.format("%.2f", currRate) + "%");
            }

        }
        // 定格
        else {
            if (info.getGridPrevNum() == 0 || totalNum == 0) {
                prevRate = 0f;
                holder.oldRate.setText("-");
            } else {
                prevRate = (double) info.getGridPrevNum() / totalNum * 100.0d;
                holder.oldRate.setText(String.format("%.2f", prevRate) + "%");
            }
            if (info.getGridCurrNum() == 0 || totalNum == 0) {
                currRate = 0f;
                holder.proRate.setText("-");
            } else {
                currRate = (double) info.getGridCurrNum() / totalNum * 100.0d;
                holder.proRate.setText(String.format("%.2f", currRate) + "%");
            }
        }
        if (currRate == 0 && prevRate == 0) {
            holder.change.setText("-");
        } else {
            holder.change.setText(String.format("%.2f", currRate - prevRate));
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView product;
        private TextView oldRate;
        private TextView proRate;
        private TextView change;
    }
}
