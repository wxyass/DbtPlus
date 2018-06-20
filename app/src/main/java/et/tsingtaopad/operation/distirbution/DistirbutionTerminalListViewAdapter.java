package et.tsingtaopad.operation.distirbution;

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
import et.tsingtaopad.db.tables.MstPowerfulterminalInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DistirbutionListViewAdapter.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2013年12月20日</br>      
 * 功能描述: 万能铺货率 TerminalAdapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class DistirbutionTerminalListViewAdapter extends BaseAdapter {
	private List<MstPowerfulterminalInfo> dataLst;
	private LayoutInflater inflater;
	Context context;

	public DistirbutionTerminalListViewAdapter(Context context,
	                    List<MstPowerfulterminalInfo> powerfulterminalInfos) {
		
		this.context = context;
		this.dataLst = powerfulterminalInfos;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = inflater.inflate(R.layout.powerfuldistirbution_terminal_item, null);
			holder.distirbution_gridid = (TextView) convertView.findViewById(R.id.distirbution_gridid);//定格id
			holder.distirbution_lineid = (TextView) convertView.findViewById(R.id.distirbution_lineid);//线路id
			holder.distirbution_product = (TextView) convertView.findViewById(R.id.distirbution_product);//产品名称
			holder.distirbution_terminal_level = (TextView) convertView.findViewById(R.id.distirbution_terminal_level);//终端等级
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
		MstPowerfulterminalInfo terminalInfo = dataLst.get(position);

		//holder.distirbution_gridid.setText(ConstValues.loginSession.getGridName());//定格id
		holder.distirbution_gridid.setText(PrefUtils.getString(context, "gridName", ""));//定格id
		holder.distirbution_lineid.setText(terminalInfo.getRoutekey());//线路id
		holder.distirbution_product.setText(terminalInfo.getProductname());//产品名称
		holder.distirbution_terminal_level.setText(FunUtil.isBlankOrNullTo(terminalInfo.getTlevel(), "-"));//终端等级
		long selectterms = FunUtil.isNullSetZero(terminalInfo.getSelectterms());
		holder.distirbution_have_select_terminal_amount.setText(String.valueOf(selectterms));//已选终端总数
		long bank = FunUtil.isNullSetZero(terminalInfo.getBlank());
		holder.distirbution_blank.setText(String.valueOf(bank));//空白
		long distribution = FunUtil.isNullSetZero(terminalInfo.getDistribution());
		holder.distirbution_puhuo.setText(String.valueOf(distribution));//铺货
		long effectdis = FunUtil.isNullSetZero(terminalInfo.getEffectdis());
		holder.distirbution_effective_distirbute.setText(String.valueOf(effectdis));//有效铺货
		long effectsale = FunUtil.isNullSetZero(terminalInfo.getEffectsale());
		holder.distirbution_effective_sale.setText(String.valueOf(effectsale));//有效销售
		long phTotal = distribution + effectdis + effectsale;
		holder.distirbution_terminal_amount.setText(String.valueOf(phTotal));//铺货终端总数
		double currPercent = 0.00d;
		
		long prevdistribution = FunUtil.isNullSetZero(terminalInfo.getPrevdistribution());//以前铺货
		long preveffectdis = FunUtil.isNullSetZero(terminalInfo.getPreveffectdis());//以前有效铺货
		long preveffectsale = FunUtil.isNullSetZero(terminalInfo.getPreveffectsale());//以前有效销售
		long prevphTotal = prevdistribution + preveffectdis + preveffectsale;//以前铺货终端总数
		
		double effectphPercent = 0.00d;
		if(selectterms !=0){
			double a =(double)(effectdis+effectsale)/selectterms*100;
			effectphPercent = roundDouble(a, 2);
		}
		holder.distirbution_effect_puhuolv.setText(String.format("%.2f", effectphPercent) + "%");//有效铺货率(包括有效铺货、有效销售)
		
		double preveffectphPercent = 0.00d;
		if(selectterms !=0){
			double b = (double)(preveffectdis+preveffectsale)/selectterms*100;
			preveffectphPercent = roundDouble(b, 2);
			
		}
		holder.distirbution_yiqianeffect_puhuolv.setText(String.format("%.2f", preveffectphPercent) + "%");//以前有效铺货率(包括有效铺货、有效销售)
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
		 
		
		if (selectterms != 0) {
		   // currPercent = Double.parseDouble(String.format("%.2f", (double)phTotal/selectterms * 100));
			double s = (double)phTotal/selectterms *100;
		    currPercent = roundDouble(s,2);
		} 
		holder.distirbution_percent.setText(String.format("%.2f", currPercent) + "%");//铺货率  "%.2f"是四舍五入得公式
		double prevPercent = FunUtil.isNullSetZero(terminalInfo.getPredisrate());//以前铺货率
        if (selectterms != 0) {
            //prevPercent = Double.parseDouble(String.format("%.2f", (double)prevPercent/selectterms * 100));
            double s = (double)prevPercent/selectterms *100;
            prevPercent = roundDouble(s,2);
        } 
        holder.distirbution_previously_percent.setText(String.format("%.2f", prevPercent) + "%");//以前铺货率
		holder.distirbution_variation_quantity.setText(String.format("%.2f", currPercent - prevPercent) + "%");//变化量
		return convertView;
	}

	static class ViewHolder {
		private TextView distirbution_gridid;//定格id
		private TextView distirbution_lineid;//线路id
		private TextView distirbution_product;//产品
		private TextView distirbution_terminal_level;//终端等级
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
