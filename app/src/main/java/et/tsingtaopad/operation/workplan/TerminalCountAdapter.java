package et.tsingtaopad.operation.workplan;

import java.util.List;


import et.tsingtaopad.R;
import et.tsingtaopad.operation.workplan.domain.TypeStc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MakePlanAdapter.java</br>
 * 作者：@ray   </br>
 * 创建时间：2013-12-13</br>      
 * 功能描述: 制定计划显示终端统计Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TerminalCountAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private Context context;
	private List<TypeStc> list;
	private LayoutInflater inflater;

	public TerminalCountAdapter(Context context, List<TypeStc> list) {
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
		ViewHolder vh = null;
		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.operation_makeplan_lvitem, null);
			vh = new ViewHolder();
			vh.tv_terminal = (TextView) convertView.findViewById(R.id.makeplan_lvitem_tv_type);
			vh.tv_num = (TextView) convertView.findViewById(R.id.makeplan_lvitem_tv_num);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.tv_terminal.setText(list.get(position).getType());
		vh.tv_num.setText(list.get(position).getNum());

		return convertView;
	}

	class ViewHolder {
		TextView tv_terminal;
		TextView tv_num;
	}

}
