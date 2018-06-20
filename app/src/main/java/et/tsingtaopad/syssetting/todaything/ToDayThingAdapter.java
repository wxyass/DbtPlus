package et.tsingtaopad.syssetting.todaything;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstVisitmemoInfo;

public class ToDayThingAdapter extends BaseAdapter {
	private int drawable_img;
	private LayoutInflater inflater = null;

	private List<MstVisitmemoInfo> list = new ArrayList<MstVisitmemoInfo>();

	private SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
	 
	
	public ToDayThingAdapter() {
		super();
	}

	public ToDayThingAdapter(Context context,List<MstVisitmemoInfo> list, int drawable_img) {
		super();
		this.drawable_img = drawable_img;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	/**
	 * 设置显示的数据
	 * @param list
	 */
	public void setData(List<MstVisitmemoInfo> list){
		this.list = list;
		this.notifyDataSetChanged();
	}	
	
	public void addData(List<MstVisitmemoInfo> list){
		this.list.addAll(list);
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.img_icon.setImageResource(drawable_img);
		viewHolder.tv_content.setText(list.get(position).getContent());
		Log.d("tag","date-->"+list.get(position).getUpdatetime());
		String temp = list.get(position).getUpdatetime()==null?"":toFormat.format(list.get(position).getUpdatetime());
		viewHolder.tv_date.setText(temp);
		return convertView;
	}

	static class ViewHolder {
		ImageView img_icon;
		TextView tv_content;
		TextView tv_date;
	}

}
