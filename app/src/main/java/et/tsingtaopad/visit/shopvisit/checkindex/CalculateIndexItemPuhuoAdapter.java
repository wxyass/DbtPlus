package et.tsingtaopad.visit.shopvisit.checkindex;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：QuicklyDialogAdapter.java</br> 作者：hongen </br>
 * 创建时间：2013-12-17</br> 功能描述: 分项采集列表中计算弹出框中列表的Adapter</br> 版本 V 1.0</br>
 * 修改履历</br> 日期 原因 BUG号 修改人 修改版本</br>
 */
public class CalculateIndexItemPuhuoAdapter extends BaseAdapter {

	private Activity context;
	private List<ProItem> dataLst;
	
	// 时间控件
	private String selectDate;
	private String aday;
	private Calendar calendar;
	private int yearr;
	private int month;
	private int day;
	private String dateselect;
	private String dateselects;
	private String dateselectx;

	public CalculateIndexItemPuhuoAdapter(Activity context, List<ProItem> dataLst) {
		this.context = context;
		this.dataLst = dataLst;
		
		calendar = Calendar.getInstance();
		yearr = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public int getCount() {
		if (CheckUtil.IsEmpty(dataLst)) {
			return 0;
		} else {
			return dataLst.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
		if (CheckUtil.IsEmpty(dataLst)) {
			return null;
		} else {
			return dataLst.get(arg0);
		}
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_calculatepuhuo_lvitem, null);
			holder.itemNameTv = (TextView) convertView.findViewById(R.id.calculatedialog_tv_itemname);
			holder.changeNumEt = (EditText) convertView.findViewById(R.id.calculatedialog_et_changenum);
			holder.finalNumEt = (EditText) convertView.findViewById(R.id.calculatedialog_et_finalnum);
			holder.xinxianduTv = (TextView) convertView.findViewById(R.id.calculatedialog_et_xinxiandu);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ProItem item = dataLst.get(position);
		holder.itemNameTv.setTag(item.getItemId());
		holder.itemNameTv.setText(item.getItemName());
		
		if("库存".equals(item.getItemName())){
			holder.xinxianduTv.setVisibility(View.VISIBLE);
		}else {
			holder.xinxianduTv.setVisibility(View.INVISIBLE);
		}
		
		DecimalFormat df = new DecimalFormat("0");
		// 变化量
		String changeNum = "";
		if (item.getChangeNum() != null) {
			changeNum = df.format(item.getChangeNum());
		}
		if ("0".equals(changeNum)) {
			holder.changeNumEt.setHint(null);
			holder.changeNumEt.setText("0");
		} else {
			holder.changeNumEt.setText(changeNum);
		}
		// 现有量
		String finalNum = "";
		if (item.getFinalNum() != null) {
			finalNum = df.format(item.getFinalNum());
		}
		if ("0".equals(finalNum)) {
			holder.finalNumEt.setHint(null);
			holder.finalNumEt.setText("0");
		} else {
			holder.finalNumEt.setText(finalNum);
		}

		// 
		holder.xinxianduTv.setTag(position);
		holder.xinxianduTv.setText(item.getFreshness());
		holder.xinxianduTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				DatePickerDialog dateDialog = new DatePickerDialog(context,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								calendar.set(year, monthOfYear, dayOfMonth);
								yearr = year;
								month = monthOfYear;
								day = dayOfMonth;
								if (dayOfMonth < 10) {
									aday = "0" + dayOfMonth;
								} else {
									aday = Integer.toString(dayOfMonth);
								}
								dateselect = (Integer.toString(year)
										+ String.format("%02d", monthOfYear + 1) + aday);
								dateselects = (Integer.toString(year)
										+ String.format("%02d", monthOfYear + 1) + aday + "000000");
								dateselectx = (Integer.toString(year)
										+ String.format("%02d", monthOfYear + 1) + aday + "235959");
								selectDate = (Integer.toString(year) + "-"
										+ String.format("%02d", monthOfYear + 1) + "-" + aday);
								TextView xinxianduTv = (TextView)v;
								xinxianduTv.setText(selectDate);
							}
						}, yearr, month, day);
				if (!dateDialog.isShowing()) {
					dateDialog.show();
				}
			}
		});

		return convertView;
	}

	private class ViewHolder {
		private TextView itemNameTv;
		private TextView xinxianduTv;
		private EditText changeNumEt;
		private EditText finalNumEt;
	}

}
