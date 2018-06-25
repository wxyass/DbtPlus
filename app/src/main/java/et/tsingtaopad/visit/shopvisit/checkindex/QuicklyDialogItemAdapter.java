package et.tsingtaopad.visit.shopvisit.checkindex;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：QuicklyDialogAdapter.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-17</br>      
 * 功能描述: 快速采集二级Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class QuicklyDialogItemAdapter extends BaseAdapter {
    
    private Activity context;
    private List<ProItem> dataLst;
    private String itemid;// 指标id 库存101 堆头105
    
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
    
    public QuicklyDialogItemAdapter(Activity context, List<ProItem> dataLst,String itemid) {
        this.context = context;
        this.dataLst = dataLst;
        this.itemid = itemid;
        
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            //convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_quicklydialog_lvitem2, null);
            convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_quicklydialog_lvitem2, parent,false);
            holder.proNameTv = (TextView)convertView.findViewById(R.id.quicklydialog_tv_proname);
            holder.changeNumEt = (EditText)convertView.findViewById(R.id.quicklydialog_et_changenum);
            holder.finalNumEt = (EditText)convertView.findViewById(R.id.quicklydialog_et_finalnum);
            holder.xinxianduBtn = (TextView)convertView.findViewById(R.id.quicklydialog_et_xinxiandu);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        ProItem item = dataLst.get(position);
        holder.proNameTv.setHint(item.getProId());
        holder.proNameTv.setText(item.getProName());
        DecimalFormat df = new DecimalFormat("0");
        /*NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);*/

        String changeNum = "";
        
        // 改变量
        if (item.getChangeNum() != null) {
            changeNum = df.format(item.getChangeNum());
            // changeNum = item.getChangeNum()+"";
            //changeNum = nf.format(item.getChangeNum());
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
            //finalNum = item.getFinalNum()+"";
            //finalNum = nf.format(item.getFinalNum());
        }
        if ("0".equals(finalNum)) {
            holder.finalNumEt.setHint(null);
            holder.finalNumEt.setText("0");
        } else {
            holder.finalNumEt.setText(finalNum);
        }
        
        /*if("101".equals(itemid)){// 库存 则显示新鲜度按钮
        	holder.xinxianduBtn.setVisibility(View.VISIBLE);
        }else{
        	holder.xinxianduBtn.setVisibility(View.INVISIBLE);
        }
        
        holder.xinxianduBtn.setTag(position);
		holder.xinxianduBtn.setText(item.getFreshness());
		holder.xinxianduBtn.setOnClickListener(new OnClickListener() {
			
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
								TextView xinxianduBtn = (TextView)v;
								xinxianduBtn.setText(selectDate);
							}
						}, yearr, month, day);
				if (!dateDialog.isShowing()) {
					dateDialog.show();
				}
			}
		});*/
        
        return convertView;
    }

    private class ViewHolder {
        private TextView proNameTv;
        private EditText changeNumEt;
        private EditText finalNumEt;
        private TextView xinxianduBtn;
    }
}
