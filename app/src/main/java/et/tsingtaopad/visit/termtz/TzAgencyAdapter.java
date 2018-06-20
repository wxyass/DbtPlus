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
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;

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
class TzAgencyAdapter extends BaseAdapter{
	
	
	private List<AgencySelectStc>  mList;
	private Activity context;
	private int select = -1;

	public TzAgencyAdapter(Activity context,  List<AgencySelectStc> mList2) {
        this.context = context;
        this.mList = mList2;
    }
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public AgencySelectStc getItem(int position) {
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
			convertView=View.inflate(context, R.layout.termtz_agency_lvitem, null);
			holder=new DoctorHolder();
			holder.agencyname=(TextView) convertView.findViewById(R.id.termtz_tv_agencyname);
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
		
		holder.agencyname.setText(mList.get(position).getAgencyName());
		
		return convertView;
		
	}
	
	static class DoctorHolder{
		TextView agencyname;
	}
	public void setSeletor(int selector) {
		this.select = selector;
	}
}
