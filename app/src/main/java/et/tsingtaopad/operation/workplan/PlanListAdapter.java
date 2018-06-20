package et.tsingtaopad.operation.workplan;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：LedgerPagerAdapter.java</br> 作者：@吴欣伟 </br>
 * 创建时间：2013/12/02</br> 功能描述: 进销存台账和调货台账ViewPager界面</br> 版本 V 1.0</br> 修改履历</br>
 * 日期 原因 BUG号 修改人 修改版本</br>
 */

class PlanListAdapter extends BaseAdapter {

	private List<View> viewLst;
	private List<String> titleLst;

	public PlanListAdapter(List<View> viewLst, List<String> titleLst) {
		this.viewLst = viewLst;
		this.titleLst = titleLst;
	}

	@Override
	public int getCount() {
		return viewLst.size();
	}

	@Override
	public View getItem(int position) {
		return viewLst.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = viewLst.get(position);

		return convertView;
	}

}
