package et.tsingtaopad.visit.shopvisit.checkindex;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.QuicklyProItem;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：QuicklyDialogAdapter.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-17</br>      
 * 功能描述: 快速采集一级Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class QuicklyDialogAdapter extends BaseAdapter {
    
    private Activity context;
    private List<QuicklyProItem> dataLst;
    
    public QuicklyDialogAdapter(Activity context, List<QuicklyProItem> dataLst) {
        this.context = context;
        this.dataLst = dataLst;
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

    @SuppressLint("WrongViewCast")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_quicklydialog_lvitem1, null);
            holder.indexNameTv = (TextView)convertView.findViewById(R.id.quicklydialog_tv_itemname);
            holder.proItemLv = (NoScrollListView)convertView.findViewById(R.id.quicklydialog_lv_pro);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        QuicklyProItem item = dataLst.get(position);
        holder.indexNameTv.setHint(item.getItemId());
        holder.indexNameTv.setText(item.getItemName());
        holder.proItemLv.setAdapter(new QuicklyDialogItemAdapter(context, item.getProItemLst(),item.getItemId()));
        return convertView;
    }

    private class ViewHolder {
        private TextView indexNameTv;
        private NoScrollListView proItemLv;
    }
}
