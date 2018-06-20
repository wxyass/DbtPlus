package et.tsingtaopad.operation.orders;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import et.tsingtaopad.R;
import et.tsingtaopad.operation.orders.domain.OrdersDataStcN;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermProductAdapter.java</br>
 * 作者：yangwenmin   </br>
 * 创建时间：2016-11-15</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermProductAdapter extends BaseAdapter {
	
	private Activity context;
    private List<OrdersDataStcN> dataLst;
    private int type; // 0:产品 订单量  1:拜访信息

	/**
	 * @param context
	 * @param dataLst
	 */
	public TermProductAdapter(Activity context, List<OrdersDataStcN> dataLst,int type) {
		super();
		this.context = context;
		this.dataLst = dataLst;
		this.type = type;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.orders_lvitem, null);
            holder.termNameTv = (TextView) convertView.findViewById(R.id.orders_item_tv1);
            holder.productNameTv = (TextView) convertView.findViewById(R.id.orders_item_tv2);
            holder.preNumTv = (TextView) convertView.findViewById(R.id.orders_item_tv3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 获取每一个指标对象
        OrdersDataStcN item = dataLst.get(position);
        if(0 == type){// 0:产品 订单量  1:拜访信息
        	holder.termNameTv.setText(item.getTerminalname());
            holder.productNameTv.setText(item.getProname());
            holder.preNumTv.setText(Long.toString(item.getOrdernum()));
        }else {// 1:拜访信息
        	holder.termNameTv.setText(item.getTerminalname());
            holder.productNameTv.setText(item.getVisituser());
            holder.preNumTv.setText(item.getVisittime());
        }
        
        
        return convertView;
	}
	
	private class ViewHolder {
        private TextView termNameTv;
        private TextView productNameTv;
        private TextView preNumTv;
    }

}
