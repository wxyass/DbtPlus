package et.tsingtaopad.visit.terminaldetails;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.com.benyoyo.manage.bs.IntStc.DataresultTerPurchaseDetailsStc;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
/**
 * 项目名称：营销移动智能工作平台 <br> 
 * 文件名：PurchaseDetailsAdapter.java <br>
 * 作者：@沈潇 <br>   
 * 创建时间：2013/11/24 <br>        
 * 功能描述: 适配器 <br>
 * 版本 V 1.0 <br>               
 * 修改履历 <br>
 * 日期      原因  BUG号    修改人 修改版本 <br>
 */
public class PurchaseDetailsAdapter extends BaseAdapter {
    
	private Context context;
	private List<DataresultTerPurchaseDetailsStc> dataLst;

	public PurchaseDetailsAdapter(Context context, List<DataresultTerPurchaseDetailsStc> dataLst) {
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
	public View getView(int position, View convertView, ViewGroup g) {
	    ViewHolder holder = null;
	    if(convertView == null) {
	        convertView = LayoutInflater.from(context).inflate(
	                R.layout.visit_terminaldetails_lvitem, null);
	        holder = new ViewHolder();
	        holder.terminalName = (TextView) convertView.findViewById(R.id.terminaldetails_tv_lvitem_terminalname);
	        holder.proName = (TextView) convertView.findViewById(R.id.terminaldetails_tv_lvitem_proname);
	        holder.procode = (TextView) convertView.findViewById(R.id.terminaldetails_tv_lvitem_procode);
	        holder.agency = (TextView) convertView.findViewById(R.id.terminaldetails_tv_lvitem_agency);
	        holder.enternum = (TextView) convertView.findViewById(R.id.terminaldetails_tv_lvitem_enternum);
	        convertView.setTag(holder);
	    }else {
	        holder = (ViewHolder) convertView.getTag();
	    }
	    DataresultTerPurchaseDetailsStc stc = dataLst.get(position);
	    holder.terminalName.setText(stc.getStrTerminalName());
	    holder.proName.setText(stc.getStrProName());
	    holder.procode.setText(stc.getStrProId());
	    holder.agency.setText(stc.getStrAgencyName());
	    holder.enternum.setText(stc.getStrNum());

		return convertView;
	}
	
	private class ViewHolder {
	    private TextView terminalName;
	    private TextView proName;
	    private TextView procode;
	    private TextView agency;
	    private TextView enternum;
	}
}
