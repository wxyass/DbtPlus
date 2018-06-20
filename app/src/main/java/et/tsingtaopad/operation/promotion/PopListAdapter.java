/**
 * 
 */
package et.tsingtaopad.operation.promotion;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstPromotionsM;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：PopListAdapter.java</br> 作者：@ray </br>
 * 创建时间：2013-12-4</br> 功能描述: popupwindow listview Adapter</br> 版本 V 1.0</br>
 * 修改履历</br> 日期 原因 BUG号 修改人 修改版本</br>
 */
public class PopListAdapter extends BaseAdapter {

	private Activity context;
	private List<MstPromotionsM> list = new ArrayList<MstPromotionsM>();

	private List<String> listStr = new ArrayList<String>();
	
	
	public PopListAdapter(Activity context, List<MstPromotionsM> list) {
		this.context = context;
		this.list = list;
	}
	
	public PopListAdapter(List<String> list,Activity context) {
		this.context = context;
		this.listStr = list;
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listStr.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listStr.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(android.R.layout.simple_list_item_single_choice, null);
			
			viewHolder.checkedTv = (CheckedTextView) convertView.findViewById(android.R.id.text1);
			viewHolder.checkedTv.setTextAppearance(context, R.style.listview_tv_item_text);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.checkedTv.setText(listStr.get(position)+"");
		
		return convertView;
	}

	static class ViewHolder {
		TextView tv;
		CheckedTextView checkedTv;
	}

}
