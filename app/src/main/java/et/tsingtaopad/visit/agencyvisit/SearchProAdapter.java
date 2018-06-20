package et.tsingtaopad.visit.agencyvisit;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：SearchProAdapter.java</br> 作者：@吴欣伟 </br>
 * 创建时间：2014/1/10</br> 功能描述: 可调货产品Adapter</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
public class SearchProAdapter extends BaseAdapter {

    private Context context;
    private List<KvStc> dataLst;

    public SearchProAdapter(Context context, List<KvStc> dataLst) {
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
    public View getView(int position, View convertView, ViewGroup arg2) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.agencyvisit_transfer_searchtagagency_lvitem, null);
            holder = new ViewHolder();
            holder.proName = (TextView) convertView
                    .findViewById(R.id.searchtagagency_tv_agencyname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        KvStc info = dataLst.get(position);
        holder.proName.setText(info.getValue());
        holder.proName.setHint(info.getKey());
        holder.proName.setTag(position);

        return convertView;
    }

    private class ViewHolder {
        private TextView proName;
    }


}
