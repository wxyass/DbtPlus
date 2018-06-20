package et.tsingtaopad.visit.terminaldetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstRouteM;

/**
 * 项目名称：营销移动智能工作平台 <br>
 * 文件名：TerminalDetailsSpinnerAdapter.java <br>
 * 作者：@沈潇 <br>
 * 创建时间：2013/11/24 <br>
 * 功能描述: SpinnerAdapter <br>
 * 版本 V 1.0 <br>
 * 修改履历 日期 原因 BUG号 修改人 修改版本
 */
public class TerminalDetailsSpinnerAdapter extends BaseAdapter {
	private List<MstRouteM> routeline;
	private Context context;

	public TerminalDetailsSpinnerAdapter(List<MstRouteM> routeline,
			Context context) {
		this.routeline = routeline;
		this.context = context;
	}

	@Override
	public int getCount() {
		return routeline.size();
	}

	@Override
	public Object getItem(int position) {
		return routeline.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView spinner_tv = null;
		if (convertView == null) {
			LinearLayout l = new LinearLayout(context);
			spinner_tv = new TextView(context, null, R.style.dialog_tv_item);
			l.addView(spinner_tv);
			convertView = l;
			convertView.setTag(spinner_tv);
		} else {
			spinner_tv = (TextView) convertView.getTag();

		}
		spinner_tv.setText(routeline.get(position).getRoutename());

		Map hm = new HashMap();
		hm.put("lineId", routeline.get(position).getRoutename() + "");
		hm.put("lineName", routeline.get(position).getRoutekey() + "");
		spinner_tv.setTag(hm);
		return convertView;
	}

}
