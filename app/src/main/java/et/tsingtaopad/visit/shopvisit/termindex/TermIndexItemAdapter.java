package et.tsingtaopad.visit.shopvisit.termindex;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.termindex.domain.TermIndexStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermIndexItemAdapter.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-5</br>      
 * 功能描述: 终端指标状态查看二级Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermIndexItemAdapter extends BaseAdapter {
    
    private Activity context;
    private List<TermIndexStc> dataLst;
    
    public TermIndexItemAdapter(Activity context, List<TermIndexStc> dataLst) {
        this.context = context;
        if (CheckUtil.IsEmpty(dataLst)) {
            this.dataLst = new ArrayList<TermIndexStc>();
        } else {
            this.dataLst = dataLst;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.termindex_lvitem2, null);
            holder.itemNameTv = (TextView)convertView.findViewById(R.id.termindex_tv_itemname);
            holder.changNumTv = (TextView)convertView.findViewById(R.id.termindex_tv_changenum);
            holder.finalNumTv = (TextView)convertView.findViewById(R.id.termindex_tv_finalnum);
            holder.provChangeNumTv = (TextView)convertView.findViewById(R.id.termindex_tv_provchangenum);
            holder.provFinalNumTv = (TextView)convertView.findViewById(R.id.termindex_tv_provfinalnum);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        TermIndexStc item = dataLst.get(position);
        holder.itemNameTv.setText(item.getItemName());
        holder.changNumTv.setText(item.getAddCount());
        holder.finalNumTv.setText(item.getTotalCount());
        holder.provChangeNumTv.setText(item.getPrevAddCount());
        if (CheckUtil.isBlankOrNull(item.getPrevAddCount()) && 
                    CheckUtil.isBlankOrNull(item.getPrevAddCount())) {
            holder.provFinalNumTv.setText("");
            
        } else {
          Long total = 0l;
            if (!CheckUtil.isBlankOrNull(item.getPrevAddCount())) {
                total = total + Long.parseLong(item.getPrevAddCount());
            }
            if (!CheckUtil.isBlankOrNull(item.getPrevTotalCount())) {
                total = total + Long.parseLong(item.getPrevTotalCount());
            }
            holder.provFinalNumTv.setText(total+"");
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView itemNameTv;
        private TextView changNumTv;
        private TextView finalNumTv;
        private TextView provChangeNumTv;
        private TextView provFinalNumTv;
    }
}
