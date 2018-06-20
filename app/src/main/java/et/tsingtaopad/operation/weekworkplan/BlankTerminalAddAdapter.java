package et.tsingtaopad.operation.weekworkplan;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstProductM;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：BlankTerminalAddAdapter.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2014年2月11日</br>      
 * 功能描述: 空白终端指标 添加终端页面的adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class BlankTerminalAddAdapter extends BaseAdapter {

	private Context context;
	private List<MstProductM> list;
	private LayoutInflater inflater;

	public BlankTerminalAddAdapter(Context context, List<MstProductM> list) {
		this.context = context;
		this.list = list;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.operation_pop_lvitem, null);
			ViewHolder vh = new ViewHolder();
			vh.tv = (TextView) convertView.findViewById(R.id.pop_tv_lvitem);
			convertView.setTag(vh);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.tv.setText(list.get(position).getProname());

		return convertView;
	}

	class ViewHolder {
		TextView tv;
	}
}
