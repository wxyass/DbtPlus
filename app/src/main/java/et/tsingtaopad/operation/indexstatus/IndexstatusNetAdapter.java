package et.tsingtaopad.operation.indexstatus;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstCheckmiddleInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：IndexstatusNetAdapter.java</br>
 * 作者：@吴欣伟</br>
 * 创建时间：2013/11/26</br>
 * 功能描述: 指标状态查询Adapter</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期       原因  BUG号    修改人 修改版本</br>
 */
public class IndexstatusNetAdapter extends BaseAdapter {
    

    private Context context;
    private List<MstCheckmiddleInfo> dataLst;
    
    public IndexstatusNetAdapter(Context context, List<MstCheckmiddleInfo> dataLst) {
        this.context = context;
        this.dataLst = dataLst;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.operation_indexstatus_lvitem, null);
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
        
        MstCheckmiddleInfo info = dataLst.get(position);
        holder.product.setText(info.getProname());
        if(CheckUtil.isBlankOrNull(info.getOldrate())) {
            holder.oldRate.setText("");
        }else {
            holder.oldRate.setText(info.getOldrate() + "%");//以前
        }
        if(CheckUtil.isBlankOrNull(info.getProrate())) {
            holder.proRate.setText("");
        }else {
            holder.proRate.setText(info.getProrate() + "%");//当前时间
        }
        holder.change.setText(String.format("%.2f",
                Float.parseFloat(FunUtil.isBlankOrNullTo(info.getProrate(), "0")) 
                        - Float.parseFloat(FunUtil.isBlankOrNullTo(info.getOldrate(), "0"))) + "%");
        return convertView;
    }

    private class ViewHolder {
        private TextView product;
        private TextView oldRate;
        private TextView proRate;
        private TextView change;
    }
}
