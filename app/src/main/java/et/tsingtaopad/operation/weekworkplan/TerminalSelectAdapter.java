package et.tsingtaopad.operation.weekworkplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：PopAdapter.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2014年2月11日</br>      
 * 功能描述: 终端列表选择adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TerminalSelectAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<MstTermListMStc> termListMStcs = new ArrayList<MstTermListMStc>();//所有的
	private Map<Integer, Boolean> isSelectMap = new HashMap<Integer, Boolean>();//单击过的对象
	private List<MstTermListMStc> popwListViewSelectS = new ArrayList<MstTermListMStc>();//单击选择的
	private boolean isSelect = false;

	/**
	 * 返回单击选择的对象
	 * @return
	 */
	public List<MstTermListMStc> getPopwListViewSelectStr() {
		return popwListViewSelectS;
	}

	/**
	 * 
	 * @param context
	 * @param termlist 显示的数据
	 */
	public TerminalSelectAdapter(Context context, List<MstTermListMStc> termlist) {
		this.context = context;
		this.termListMStcs = termlist;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return termListMStcs.size();
	}

	@Override
	public Object getItem(int position) {
		return termListMStcs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	//View view = null;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		//view = convertView;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.operation_pop_lvitem, null);
			holder = new ViewHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.pop_tv_lvitem);
			holder.pop_tv_lvitem_statuse = (TextView) convertView.findViewById(R.id.pop_tv_lvitem_statuse);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Boolean select = isSelectMap.get(position);
		if (select != null) {
			if (select) {
				convertView.setBackgroundColor(context.getResources().getColor(R.color.bg_content_color_orange));

			} else {
				convertView.setBackgroundColor(Color.WHITE);
			}
		} else {
			convertView.setBackgroundColor(Color.WHITE);
		}
		final MstTermListMStc mstTermListMStc = termListMStcs.get(position);
		holder.tv.setText(mstTermListMStc.getTerminalname());
		String terminalStatus = mstTermListMStc.getTerminalStatus();
		if (!CheckUtil.isBlankOrNull(terminalStatus)) {
			holder.pop_tv_lvitem_statuse.setText(terminalStatus);
		}
		final int clickPositoin = position;
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popwListViewSelectS.contains(mstTermListMStc)) {//已经选中
					isSelect = false;
					v.setBackgroundColor(Color.WHITE);
					popwListViewSelectS.remove(mstTermListMStc);
				} else {
					v.setBackgroundColor(context.getResources().getColor(R.color.bg_content_color_orange));
					popwListViewSelectS.add(mstTermListMStc);
					isSelect = true;
				}

				//				if (isSelect) {
				//					isSelect = false;
				//					v.setBackgroundColor(Color.WHITE);
				//					popwListViewSelectS.remove(mstTermListMStc);
				//				} else {
				//					v.setBackgroundColor(Color.GREEN);
				//					popwListViewSelectS.add(mstTermListMStc);
				//					isSelect = true;
				//				}
				isSelectMap.put(clickPositoin, isSelect);
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView tv;
		TextView pop_tv_lvitem_statuse;
	}

}
