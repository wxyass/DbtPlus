package et.tsingtaopad.visit.agencykf;

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

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DevelopAgencyAdapter.java</br>
 * 作者：Administrator   </br>
 * 创建时间：2015-11-23</br>      
 * 功能描述: 经销商开发,经销商Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
class AgencyKFAdapter extends BaseAdapter{
	
	
	private List<MstAgencyKFM>  mList;
	private Activity context;
	private int select = -1;

	public AgencyKFAdapter(Activity context,  ArrayList<MstAgencyKFM> mList2) {
        this.context = context;
        this.mList = mList2;
    }
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public MstAgencyKFM getItem(int position) {
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
			convertView=View.inflate(context, R.layout.agencykf_lvitem, null);
			holder=new DoctorHolder();
			holder.agencyname=(TextView) convertView.findViewById(R.id.developagency_tv_agencyname);
			holder.contact=(TextView) convertView.findViewById(R.id.developagency_tv_legalperson);
			holder.mobile=(TextView) convertView.findViewById(R.id.developagency_tv_telnum);
			holder.kfdate=(TextView) convertView.findViewById(R.id.developagency_tv_developdata);
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
		
		holder.agencyname.setText(mList.get(position).getAgencyname());
		holder.contact.setText(mList.get(position).getContact());
		holder.mobile.setText(mList.get(position).getMobile());
		//holder.kfdate.setText(mList.get(position).getKfdate());
		//holder.kfdate.setText(DateUtil.formatDate(1,mList.get(position).getKfdate()));
		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//Date date1 = df.parse(mList.get(position).getKfdate());
		/*
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.parseLong(mList.get(position).getKfdate()));
		Formatter ft=new Formatter(Locale.CHINA);
		String a = ft.format("%1$tY年%1$tm月%1$td日%1$tA，%1$tT %1$tp", cal).toString();
		holder.kfdate.setText(a);*/
		//Date date= new Date(Long.parseLong(mList.get(position).getKfdate()));
		Date date= new Date(Long.parseLong(mList.get(position).getKfdate()));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String adf = sdf.format(date);
		holder.kfdate.setText(adf);
		return convertView;
		
	}
	
	static class DoctorHolder{
		TextView agencyname;
		TextView contact;
		TextView mobile;
		TextView kfdate;
	}
	public void setSeletor(int selector) {
		this.select = selector;
	}
}
