package et.tsingtaopad.visit.agencyvisit;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：AgencySelectAdapter.java</br> 作者：@吴欣伟 </br>
 * 创建时间：2013/11/26</br> 功能描述: 经销商选择Adapter</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
public class AgencySelectAdapter extends BaseAdapter {

    private Context context;
    private List<AgencySelectStc> dataLst;
    private static HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();

    public AgencySelectAdapter(Context context, List<AgencySelectStc> dataLst) {
        this.context = context;
        this.dataLst = dataLst;
        initData();
    }

    // 初始化isSelected的数据
    private void initData() {
        for(int i = 0; i < dataLst.size(); i++) {
            getIsSelected().put(i, false);
        }
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
                    R.layout.agencyvisit_agencyselect_lvitem, null);
            holder = new ViewHolder();
            holder.strAgencyName = (TextView) convertView
                    .findViewById(R.id.agencyselect_tv_lvitem_agencyname);
            holder.strAddr = (TextView) convertView
                    .findViewById(R.id.agencyselect_tv_lvitem_addr);
            holder.strPhone = (TextView) convertView
                    .findViewById(R.id.agencyselect_tv_lvitem_phone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AgencySelectStc info = dataLst.get(position);
        holder.strAgencyName.setText(FunUtil.isNullSetSpace(info
                .getAgencyName()));
        holder.strAddr.setText(FunUtil.isNullSetSpace(info.getAddr()));
        holder.strPhone.setText(FunUtil.isNullSetSpace(info.getPhone()));

        RadioButton radio = (RadioButton) convertView
                .findViewById(R.id.agencyselect_rb_lvitem);
        
        holder.raidoRb = radio;
        boolean res = false;
        if (isSelected.get(position) == null
                || isSelected.get(position) == false) {
            res = false;
            isSelected.put(position, false);
        } else
            res = true;

        holder.raidoRb.setChecked(res);
        return convertView;
    }

    private class ViewHolder {
        private RadioButton raidoRb;
        private TextView strAgencyName;
        private TextView strAddr;
        private TextView strPhone;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        AgencySelectAdapter.isSelected = isSelected;
    }

}
