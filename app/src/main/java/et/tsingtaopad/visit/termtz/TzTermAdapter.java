package et.tsingtaopad.visit.termtz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstAgencyKFM;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.visit.agencykf.domain.MstAgencyKFInfo;
import et.tsingtaopad.visit.termtz.domain.MstTzTermInfo;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TzAgencyAdapter.java</br>
 * 作者：ywm   </br>
 * 创建时间：2015-11-23</br>      
 * 功能描述: 终端进货台账_经销商Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
class TzTermAdapter extends BaseAdapter{
	
	
	private List<MstTzTermInfo>  mList;
	private Activity context;
	private int select = -1;//

	public TzTermAdapter(Activity context,  ArrayList<MstTzTermInfo> mList2) {
        this.context = context;
        this.mList = mList2;
    }
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public MstTzTermInfo getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DoctorHolder holder=null;
		if(convertView==null){
			convertView=View.inflate(context, R.layout.termtz_term_lvitem, null);
			holder=new DoctorHolder();
			holder.termid=(TextView) convertView.findViewById(R.id.tzterm_tv_termid);
			holder.termname=(TextView) convertView.findViewById(R.id.tzterm_tv_termname);
			holder.sequence=(TextView) convertView.findViewById(R.id.tzterm_tv_sequence);
			holder.grid=(TextView) convertView.findViewById(R.id.tzterm_tv_grid);
			holder.route=(TextView) convertView.findViewById(R.id.tzterm_tv_route);
			holder.address=(TextView) convertView.findViewById(R.id.tzterm_tv_address);
			holder.people=(TextView) convertView.findViewById(R.id.tzterm_tv_people);
			holder.phone=(TextView) convertView.findViewById(R.id.tzterm_tv_phone);
			convertView.setTag(holder);
		}else{
			holder=(DoctorHolder) convertView.getTag();
		}
		if (position == select) {
			convertView.setBackgroundDrawable(context.getResources()
					.getDrawable(R.drawable.bg_listitem));
		}else{
			convertView.setBackgroundColor(Color.WHITE);
		}
		
		holder.termid.setText(mList.get(position).getTerminalcode());
		holder.termname.setText(mList.get(position).getTerminalname());
		holder.sequence.setText(mList.get(position).getSequence());
		holder.grid.setText(mList.get(position).getGridname());
		holder.route.setText(mList.get(position).getRoutename());
		holder.address.setText(mList.get(position).getAddress());
		holder.people.setText(mList.get(position).getContact());
		holder.phone.setText(mList.get(position).getMobile());
		if("1".equals(mList.get(position).getPadisconsistent())){
			holder.termid.setTextColor(context.getResources().getColor(R.color.home_mt));
			holder.termname.setTextColor(context.getResources().getColor(R.color.home_mt));
			holder.grid.setTextColor(context.getResources().getColor(R.color.home_mt));
			holder.route.setTextColor(context.getResources().getColor(R.color.home_mt));
			holder.address.setTextColor(context.getResources().getColor(R.color.home_mt));
			holder.people.setTextColor(context.getResources().getColor(R.color.home_mt));
			holder.phone.setTextColor(context.getResources().getColor(R.color.home_mt));
		}else{
			holder.termid.setTextColor(Color.BLACK);
			holder.termname.setTextColor(Color.BLACK);
			holder.grid.setTextColor(Color.BLACK);
			holder.route.setTextColor(Color.BLACK);
			holder.address.setTextColor(Color.BLACK);
			holder.people.setTextColor(Color.BLACK);
			holder.phone.setTextColor(Color.BLACK);
		}
		
		return convertView;
		
	}
	
	static class DoctorHolder{
		TextView termid;
		TextView termname;
		TextView grid;
		TextView route;
		TextView address;
		TextView people;
		TextView phone;
		TextView sequence;
	}
	public void setSeletor(int selector) {
		this.select = selector;
	}
}
