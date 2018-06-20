package et.tsingtaopad.operation.promotion;

import java.util.List;

import et.tsingtaopad.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：FinishListAdapter.java</br> 作者：@ray </br>
 * 创建时间：2013-12-17</br> 功能描述: 完成终端未完成终端显示Adapter</br> 版本 V 1.0</br> 修改履历</br> 日期
 * 原因 BUG号 修改人 修改版本</br>
 */
public class FinishListAdapter extends BaseAdapter {
	private Context context;
	private List<String> list;
	private int flag;
	public FinishListAdapter(Context context, List<String> list ,int flag) {
		this.context = context;
		this.list = list;
		this.flag = flag;
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
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.operation_makeplan_viewpagger_lvitem, null);
			ViewHolder vh = new ViewHolder();
			vh.tv_num = (TextView) convertView.findViewById(R.id.vp_tv_product);
			vh.tv_name = (TextView) convertView.findViewById(R.id.vp_tv_num);
			vh.tv_state = (TextView) convertView.findViewById(R.id.vp_tv_terminal);
			convertView.setTag(vh);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.tv_num.setText((position+1)+"");
		if(list.get(position)==null||list.get(position).equals("null")){
			viewHolder.tv_name.setText("");
		}else{
			viewHolder.tv_name.setText(list.get(position));
		}
		
		if(flag == 1){
			viewHolder.tv_state.setText(context.getString(R.string.promotion_finish1));// 达成
		}else{
			viewHolder.tv_state.setText(context.getString(R.string.promotion_unfinished1));// 未达成
			
		}
		return convertView;
	}

	class ViewHolder {
		TextView tv_num;
		TextView tv_name;
		TextView tv_state;
		
	}
}
