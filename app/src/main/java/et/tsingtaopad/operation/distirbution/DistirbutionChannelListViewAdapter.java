package et.tsingtaopad.operation.distirbution;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstPowerfulchannelInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DistirbutionChannelListViewAdapter.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2013年12月20日</br>      
 * 功能描述: 万能铺货率 Channel Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class DistirbutionChannelListViewAdapter extends BaseAdapter {
	private Context context;
	private List<MstPowerfulchannelInfo> dataLst = new ArrayList<MstPowerfulchannelInfo>();

	public DistirbutionChannelListViewAdapter(Context context, List<MstPowerfulchannelInfo> powerfulchannelInfos) {
		this.context = context;
		this.dataLst = powerfulchannelInfos;
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
	public Object getItem(int position) {
        if (CheckUtil.IsEmpty(dataLst)) {
            return null;
        } else {
            return dataLst.get(position);
        }
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.powerfuldistirbution_channel_item, null);
			holder.distirbution_gridid = (TextView) convertView.findViewById(R.id.distirbution_gridid);//定格id
			holder.distirbution_lineid = (TextView) convertView.findViewById(R.id.distirbution_lineid);//线路id
			holder.distirbution_product =  (TextView) convertView.findViewById(R.id.distirbution_product);//产品
			holder.distirbution_channel_type = (TextView) convertView.findViewById(R.id.distirbution_channel_type);//渠道类型
			holder.distirbution_main_channel = (TextView) convertView.findViewById(R.id.distirbution_main_channel);//主渠道
			holder.distirbution_poor_channel = (TextView) convertView.findViewById(R.id.distirbution_poor_channel);//次渠道
			holder.distirbution_have_select_terminal_amount = (TextView) convertView.findViewById(R.id.distirbution_have_select_terminal_amount);//已选终端总数
			holder.distirbution_blank = (TextView) convertView.findViewById(R.id.distirbution_blank);//空白
			holder.distirbution_puhuo = (TextView) convertView.findViewById(R.id.distirbution_puhuo);//铺货
			holder.distirbution_effective_distirbute = (TextView) convertView.findViewById(R.id.distirbution_effective_distirbute);//有效铺货
			holder.distirbution_effective_sale = (TextView) convertView.findViewById(R.id.distirbution_effective_sale);//有效销售
			holder.distirbution_terminal_amount = (TextView) convertView.findViewById(R.id.distirbution_terminal_amount);//铺货终端总数
			holder.distirbution_percent = (TextView) convertView.findViewById(R.id.distirbution_percent);//铺货率
			holder.distirbution_previously_percent = (TextView) convertView.findViewById(R.id.distirbution_previously_percent);//以前铺货率
			holder.distirbution_variation_quantity = (TextView) convertView.findViewById(R.id.distirbution_variation_quantity);//变化量
			
			holder.distirbution_effect_puhuolv = (TextView) convertView.findViewById(R.id.distirbution_youxiao_puhuolv);//有效铺货率
			holder.distirbution_yiqianeffect_puhuolv = (TextView) convertView.findViewById(R.id.distirbution_yiqianyouxiao_puhuolv);//以前有效铺货率
			holder.distirbution_effect_puhuochangelang = (TextView) convertView.findViewById(R.id.distirbution_youxiao_puhuobianhualiang);//有效铺货变化量
			holder.distirbution_effect_salelv = (TextView) convertView.findViewById(R.id.distirbution_youxiao_salelv);//有效销售率
			holder.distirbution_yiqianeffect_salelv = (TextView) convertView.findViewById(R.id.distirbution_yiqianyouxiao_salelv);//以前有效销售率
			holder.distirbution_effect_salechanglang = (TextView) convertView.findViewById(R.id.distirbution_youxiao_salechange);//有效销售变化量
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MstPowerfulchannelInfo channelInfo = dataLst.get(position);
        //holder.distirbution_gridid.setText(ConstValues.loginSession.getGridName());//定格id
        holder.distirbution_gridid.setText(PrefUtils.getString(context, "gridName", ""));//定格id
        holder.distirbution_lineid.setText(channelInfo.getRoutekey());//线路id
        holder.distirbution_product.setText(channelInfo.getProductname());// 产品名称
        holder.distirbution_channel_type.setText(FunUtil.isBlankOrNullTo(channelInfo.getSellchannel(), "-"));//渠道类型
        holder.distirbution_main_channel.setText(FunUtil.isBlankOrNullTo(channelInfo.getMainchannel(), "-"));//主渠道
        holder.distirbution_poor_channel.setText(FunUtil.isBlankOrNullTo(channelInfo.getMinorchannel(), "-"));//次渠道
        long selectterms = FunUtil.isNullSetZero(channelInfo.getSelectterms());
        holder.distirbution_have_select_terminal_amount.setText(String.valueOf(selectterms));//已选终端总数
        long bank = FunUtil.isNullSetZero(channelInfo.getBlank());
        holder.distirbution_blank.setText(String.valueOf(bank));//空白
        long distribution = FunUtil.isNullSetZero(channelInfo.getDistribution());
        holder.distirbution_puhuo.setText(String.valueOf(distribution));//铺货
        long effectdis = FunUtil.isNullSetZero(channelInfo.getEffectdis());
        holder.distirbution_effective_distirbute.setText(String.valueOf(effectdis));//有效铺货
        long effectsale = FunUtil.isNullSetZero(channelInfo.getEffectsale());
        holder.distirbution_effective_sale.setText(String.valueOf(effectsale));//有效销售
        long phTotal = distribution + effectdis + effectsale;
        holder.distirbution_terminal_amount.setText(String.valueOf(phTotal));//铺货终端总数
        double currPercent = 0d;
        if (selectterms != 0) {
            currPercent = Double.parseDouble(String.format("%.2f", (double)phTotal/selectterms * 100));
        } 
        holder.distirbution_percent.setText(String.format("%.2f", currPercent) + "%");//铺货率
        double prevPercent = FunUtil.isNullSetZero(channelInfo.getPredisrate());
        if (selectterms != 0) {
            prevPercent = Double.parseDouble(String.format("%.2f", (double)prevPercent/selectterms * 100));
        } 
        holder.distirbution_previously_percent.setText(String.format("%.2f", prevPercent) + "%");//以前铺货率
        holder.distirbution_variation_quantity.setText(String.format("%.2f", currPercent - prevPercent) + "%");//变化量
        
        long prevdistribution = FunUtil.isNullSetZero(channelInfo.getPrevdistribution());//以前铺货
		long preveffectdis = FunUtil.isNullSetZero(channelInfo.getPreveffectdis());//以前有效铺货
		long preveffectsale = FunUtil.isNullSetZero(channelInfo.getPreveffectsale());//以前有效销售
		long prevphTotal = prevdistribution + preveffectdis + preveffectsale;//以前铺货终端总数
		
		double effectphPercent = 0.00d;
		if(selectterms !=0){
			double a =(double)(effectdis+effectsale)/selectterms*100;
			effectphPercent = roundDouble(a, 2);
		}
		holder.distirbution_effect_puhuolv.setText(String.format("%.2f", effectphPercent) + "%");//有效铺货率
		
		double preveffectphPercent = 0.00d;
		if(selectterms !=0){
			double b = (double)(preveffectdis+preveffectsale)/selectterms*100;
			preveffectphPercent = roundDouble(b, 2);
			
		}
		holder.distirbution_yiqianeffect_puhuolv.setText(String.format("%.2f", preveffectphPercent) + "%");//以前有效铺货率
		holder.distirbution_effect_puhuochangelang.setText(String.format("%.2f",effectphPercent - preveffectphPercent) + "%");//有效铺货变化量
		
		double effectsalePercent = 0.00d;
		if(selectterms !=0){
			double c = (double)effectsale/selectterms*100;
			effectsalePercent = roundDouble(c, 2);
			
		}
		holder.distirbution_effect_salelv.setText(String.format("%.2f", effectsalePercent) + "%");//有效销售率
		
		double preveffectsalePercent = 0.00d;
		if(selectterms != 0){
			double d = (double)preveffectsale/selectterms*100;
			preveffectsalePercent = roundDouble(d, 2);
			
		}
		holder.distirbution_yiqianeffect_salelv.setText(String.format("%.2f", preveffectsalePercent) + "%");//以前有效销售率
		holder.distirbution_effect_salechanglang.setText(String.format("%.2f",effectsalePercent - preveffectsalePercent) + "%");//有效销售变化量
		return convertView;
	}

	static class ViewHolder {
		private TextView distirbution_gridid;//定格
		private TextView distirbution_lineid;//线路
		private TextView distirbution_product;//产品名称
		private TextView distirbution_channel_type;//渠道类型
		private TextView distirbution_main_channel;//主渠道
		private TextView distirbution_poor_channel;//次渠道
		private TextView distirbution_have_select_terminal_amount;//已选终端总数
		private TextView distirbution_blank;//空白
		private TextView distirbution_puhuo;//铺货
		private TextView distirbution_effective_distirbute;//有效铺货
		private TextView distirbution_effective_sale;//有效销售
		private TextView distirbution_terminal_amount;//铺货终端总数
		private TextView distirbution_percent;//铺货率
		private TextView distirbution_previously_percent;//以前铺货率
		private TextView distirbution_variation_quantity;//变化量
		
		private TextView distirbution_effect_puhuolv;//有效铺货率
		private TextView distirbution_yiqianeffect_puhuolv;//以前有效铺货率
		private TextView distirbution_effect_puhuochangelang;//有效铺货变化量
		private TextView distirbution_effect_salelv;//有效销售率
		private TextView distirbution_yiqianeffect_salelv;//以前有效销售率
		private TextView distirbution_effect_salechanglang;//有效销售变化量
	}
	
	static Double roundDouble(double val, int precision)  
	{  
	 Double ret = null;  
	 try  
	 {  
	  double factor = Math.pow(10, precision);  
	  ret = Math.floor(val * factor + 0.5) / factor;  
	 }  
	 catch (Exception e)  
	 {  
	  e.printStackTrace();  
	 }  
	 return ret;  
	} 
}
