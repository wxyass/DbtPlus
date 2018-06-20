package et.tsingtaopad.visit.shopvisit.termindex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.termindex.domain.TermIndexStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermIndexAdapter.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-5</br>      
 * 功能描述: 终端指标状态查看的一级Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermIndexAdapter extends BaseAdapter {
    
    private Activity context;
    private List<TermIndexStc> dataLst;
    private Map<String, List<TermIndexStc>> dataMap;
    
    public TermIndexAdapter(Activity context, List<TermIndexStc> 
                            dataLst, Map<String, List<TermIndexStc>> dataMap) {
        this.context = context;
        if (CheckUtil.IsEmpty(dataLst)) {
            this.dataLst = new ArrayList<TermIndexStc>();
            dataMap = new HashMap<String, List<TermIndexStc>>();
        } else {
            this.dataLst = dataLst;
            this.dataMap = dataMap;
        }
    }

    @Override
    public int getCount() {
        return dataLst.size();
    }

    @Override
    public Object getItem(int arg0) {
        return dataLst.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.termindex_lvitem1, null);
            holder.proNameTv = (TextView)convertView
                    .findViewById(R.id.termindex_tv_proname);
            holder.itemLv = (NoScrollListView)convertView
                        .findViewById(R.id.termindex_lv_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        TermIndexStc item = dataLst.get(position);
        holder.proNameTv.setText(item.getProName());
        holder.itemLv.setAdapter(new TermIndexItemAdapter(context, 
                        dataMap.get(item.getIndexKey() + item.getIndexValueKey() + item.getProKey())));
        return convertView;
    }

    private class ViewHolder {
        private TextView proNameTv;
        private NoScrollListView itemLv;
    }

}
