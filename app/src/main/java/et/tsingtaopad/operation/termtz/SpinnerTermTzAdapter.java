package et.tsingtaopad.operation.termtz;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import et.tsingtaopad.R;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：SpinnerKeyValueAdapter.java</br> 作者：吴承磊 </br>
 * 创建时间：2013-11-29</br> 功能描述: Spinner的Adapter的扩展应用</br> 版本 V 1.0</br> 修改履历</br>
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
@SuppressWarnings("rawtypes")
public class SpinnerTermTzAdapter extends BaseAdapter {

	private List<AgencySelectStc> mList = null;
	private Context mContext = null;

	public  SpinnerTermTzAdapter(Context pContext, List<AgencySelectStc> pList) {
		this.mContext = pContext;
		this.mList = pList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 利用getView方法给我们提供的convertView，也可以自己新建一个Layout
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_termtz_item, null);
			holder.itemTv = (TextView) convertView.findViewById(R.id.termtz_tv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.itemTv.setText(mList.get(position).getAgencyName());

		return convertView;
	}

	private class ViewHolder {
		private TextView itemTv;
	}
}
