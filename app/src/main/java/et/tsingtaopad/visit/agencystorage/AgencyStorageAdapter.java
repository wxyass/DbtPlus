package et.tsingtaopad.visit.agencystorage;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.visit.agencystorage.domain.AgencystorageStc;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：AgencyStorageAdapter.java</br>
 * 作者：@吴欣伟</br>
 * 创建时间：2013/11/26</br>
 * 功能描述: 经销商库存Adapter</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期       原因  BUG号    修改人 修改版本</br>
 */
public class AgencyStorageAdapter extends BaseAdapter {

    private List<AgencystorageStc> dataLst;
    private Context context;
    private boolean isASameDate;
    private boolean isBSameDate;
    
    public AgencyStorageAdapter(Context context, List<AgencystorageStc> dataLst,boolean isASameDate,boolean isBSameDate) {
        this.context = context;
        this.dataLst = dataLst;
        this.isASameDate = isASameDate;
        this.isBSameDate = isBSameDate;
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
	    if(convertView == null) {
	        convertView = LayoutInflater.from(context).inflate(R.layout.visit_agencystorage_lvitem, null);
	        holder = new ViewHolder();
	        holder.proName = (TextView) convertView.findViewById(R.id.agencystorage_tv_proname);
	        holder.proCode = (TextView) convertView.findViewById(R.id.agencystorage_tv_procode);
	        holder.storenum = (TextView) convertView.findViewById(R.id.agencystorage_tv_storenum);
	        //holder.date = (TextView) convertView.findViewById(R.id.agencystorage_tv_date);
	        holder.agencyName = (TextView) convertView.findViewById(R.id.agencystorage_tv_agencyname);
	        holder.agencyCode = (TextView) convertView.findViewById(R.id.agencystorage_tv_agencycode);
	        holder.agencyUser = (TextView) convertView.findViewById(R.id.agencystorage_tv_agencyuser);
	        holder.phone = (TextView) convertView.findViewById(R.id.agencystorage_tv_phone);
	        holder.ingoodsnum = (TextView) convertView.findViewById(R.id.agencystorage_tv_ingoodsnum);
	        holder.salenum = (TextView) convertView.findViewById(R.id.agencystorage_tv_salesnum);
	        holder.selectstorenmu = (TextView) convertView.findViewById(R.id.agencystorage_tv_creentstorenum);
	        holder.selectingoodsnum = (TextView) convertView.findViewById(R.id.agencystorage_tv_creentingoodsnum);
	        holder.selectsalenum = (TextView) convertView.findViewById(R.id.agencystorage_tv_creentsalesnum);
	        holder.preStorenum = (TextView) convertView.findViewById(R.id.agencystorage_tv_prestorenum);
	        holder.preSelectStorenumu = (TextView) convertView.findViewById(R.id.agencystorage_tv_precreentstorenum);
	        convertView.setTag(holder);
	    }else {
	        holder = (ViewHolder) convertView.getTag();
	    }
	    AgencystorageStc storStc = dataLst.get(position);
	    holder.proName.setText(storStc.getProName());
	    holder.proCode.setText(storStc.getProCode());
	    /*if(storStc.getDate() == null) {
	        holder.date.setText("");
	    }else*/
	    //holder.date.setText(DateUtil.formatDate(1, storStc.getDate()));//生效时间
	    holder.agencyName.setText(storStc.getAgencyName());
	    holder.agencyCode.setText(storStc.getAgencyCode());
	    holder.agencyUser.setText(storStc.getAgencyUser());
	    holder.phone.setText(storStc.getPhone());
	    
	    //库存
	    //A时间
	    if(isASameDate){
	        holder.storenum.setText(storStc.getStorenum());//期末库存
	        holder.preStorenum.setText(storStc.getPrestorenum());//期初库存
	        holder.ingoodsnum.setText(storStc.getIngoodsnum());//进货量
	        holder.salenum.setText(storStc.getSalenum());//销售量
	    }else{
            holder.storenum.setText("0");//期末库存
            holder.preStorenum.setText(storStc.getStorenum());//期初库存
            holder.ingoodsnum.setText("0");//进货量
            holder.salenum.setText("0");//销售量
        
	    }
	    //B时间
	    if(isBSameDate){
	        holder.selectstorenmu.setText(storStc.getCreentstorenmu());//期末库存
	        holder.preSelectStorenumu.setText(storStc.getPrecreentstorenum());//期初库存
	        holder.selectingoodsnum.setText(storStc.getCreentingoodsnum());//进货量
	        holder.selectsalenum.setText(storStc.getCreentsalenum());//销售量
	    }else{
            holder.selectstorenmu.setText("0");//期末库存
            holder.preSelectStorenumu.setText(storStc.getCreentstorenmu());//期初库存
            holder.selectingoodsnum.setText("0");//进货量
            holder.selectsalenum.setText("0");//销售量
        
	    }
	    

	    //上次库存，进货量，销售量
	    
		return convertView;
	}

	private class ViewHolder {
	    
	    private TextView proName;
	    private TextView proCode;
	    private TextView storenum;
	    private TextView ingoodsnum;
	    private TextView salenum;
	    private TextView agencyName;
	    private TextView agencyCode;
	    private TextView agencyUser;
	    private TextView phone;
	    private TextView selectstorenmu;
	    private TextView selectingoodsnum;
	    private TextView selectsalenum;
	    private TextView preStorenum;
	    private TextView preSelectStorenumu;
	}
}
