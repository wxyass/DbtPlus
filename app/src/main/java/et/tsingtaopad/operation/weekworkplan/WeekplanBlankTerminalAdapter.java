package et.tsingtaopad.operation.weekworkplan;

import java.util.List;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.operation.workplan.domain.VpLvItemStc;
import et.tsingtaopad.tools.CheckUtil;

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
public class WeekplanBlankTerminalAdapter extends BaseAdapter {

	private Context context;
	private List<VpLvItemStc> list;
	private LayoutInflater inflater;

	public WeekplanBlankTerminalAdapter(Context context, List<VpLvItemStc> list) {
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

	    ViewHolder holder = null;
		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.operation_makeweekplanvp_listviewitem, null);
			holder = new ViewHolder();
			holder.proname = (TextView) convertView.findViewById(R.id.makeweekplanvp_tv_proname);
			holder.count = (TextView) convertView.findViewById(R.id.makeweekplanvp_tv_temp_listviewitem_rate);
			holder.terminalLv = (NoScrollListView) convertView.findViewById(R.id.makeweekplanvp_lv_listview);
			holder.countLl = (LinearLayout) convertView.findViewById(R.id.makeweekplanvp_ll_temp);
			convertView.setTag(holder);
		}else {
		    holder = (ViewHolder) convertView.getTag();
		}
		VpLvItemStc stc = list.get(position);
		holder.proname.setText(stc.getName());
		if(CheckUtil.IsEmpty(stc.getTerminals()))
		    holder.countLl.setVisibility(View.GONE);
		else {
		    TempTerminalCountAdapter adapter = new TempTerminalCountAdapter(context, stc.getTerminals());
		    holder.terminalLv .setAdapter(adapter);
		    holder.count.setText(stc.getNum());
		}

		return convertView;
	}

	class ViewHolder {
		TextView proname;
		TextView count;
		NoScrollListView terminalLv;
		LinearLayout countLl;
	}
}
