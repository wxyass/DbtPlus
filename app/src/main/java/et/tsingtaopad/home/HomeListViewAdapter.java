package et.tsingtaopad.home;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import et.tsingtaopad.R;

public class HomeListViewAdapter extends ArrayAdapter<HomeListViewStc> {

	/**
	 * @param context
	 * @param resource
	 */
	private LayoutInflater inflater;
	public HomeListViewAdapter(Context context, int resource) {
		super(context, resource);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.home_listview_item, null);
			holder.home_list_title = (TextView) convertView.findViewById(R.id.home_list_title);
			holder.home_list_createDate = (TextView) convertView.findViewById(R.id.home_list_createDate);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		HomeListViewStc item = getItem(position);
		holder.home_list_title.setText(item.getTitle());
		holder.home_list_createDate.setText(item.getUpdateTime());
		return convertView;
	}

	static class ViewHolder {
		TextView home_list_title;
		TextView home_list_createDate;
	}
}
