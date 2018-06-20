package et.tsingtaopad.syssetting.notice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.CmmBoardM;
import et.tsingtaopad.tools.DateUtil;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：NoticeAdapter.java<br>
 * 作者：@沈潇<br>
 * 创建时间：2013/11/24<br>
 * 功能描述: 通知通告NoticeAdapter<br>
 * 版本 V 1.0<br>
 * 修改履历 日期 原因 BUG号 修改人 修改版本
 */
public class NoticeAdapter extends BaseAdapter {
	private int drawable_img;

	private List<CmmBoardM> list = new ArrayList<CmmBoardM>();

	private LayoutInflater inflater = null;

	public NoticeAdapter(Context context, List<CmmBoardM> list, int drawable_img) {
		this.drawable_img = drawable_img;
		this.list =list;
		inflater = LayoutInflater.from(context);
	}
	
	public void setData(List<CmmBoardM> list){
		this.list=(list);
		this.notifyDataSetChanged();
	}
	

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup g) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.business_todaything_lvitem,
					null);
			viewHolder = new ViewHolder();
			viewHolder.tv_content = (TextView) convertView
					.findViewById(R.id.business_todaything_lvitem_tv_content);
			viewHolder.tv_date = (TextView) convertView
					.findViewById(R.id.business_todaything_lvitem_tv_date);
			viewHolder.img_icon = (ImageView) convertView
					.findViewById(R.id.business_todaything_lvitem_img_icon);
			viewHolder.tv_creater = (TextView) convertView
					.findViewById(R.id.business_todaything_lvitem_tv_creater);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.img_icon.setImageResource(drawable_img);
		viewHolder.tv_content.setText(list.get(position).getMesstitle());
		viewHolder.tv_creater.setText(list.get(position).getUsername()+"");
		if (list.get(position).getCredate() != null)
			viewHolder.tv_date.setText(DateUtil.formatDate(list.get(position)
					.getCredate(), "yyyy-MM-dd"));
		return convertView;
	}
	
	static class ViewHolder {
		ImageView img_icon = null;
		TextView tv_content =null ;
		TextView tv_date = null;
		TextView tv_creater = null;
	}
}
