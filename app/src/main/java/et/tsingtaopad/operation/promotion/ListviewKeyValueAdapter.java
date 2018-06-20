package et.tsingtaopad.operation.promotion;

import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.ReflectUtil;
/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：ListviewKeyValueAdapter.java</br>
 * 作者：@ray  </br>
 * 创建时间：2013-11-29</br>      
 * 功能描述:  Listview的Adapter的扩展多选使用</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressWarnings("rawtypes")
public class ListviewKeyValueAdapter extends BaseAdapter {
    
    private Activity context;
    private List dataLst;
    private String[] fieldName;

    public ListviewKeyValueAdapter(Activity context, List dataLst, String fieldName[]) {
        this.context = context;
        if (CheckUtil.IsEmpty(dataLst)) {
            this.dataLst = new ArrayList();
        } else {
            this.dataLst = dataLst;
            this.fieldName = fieldName;
        }
    }

    @Override
    public int getCount() {
        return dataLst.size();
    }

    @Override
    public Object getItem(int arg0) {
        return dataLst.get(arg0);
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
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, null);
            holder.itemTv = (TextView)convertView.findViewById(android.R.id.text1);
//            holder.itemTv.setTextSize(22);
            holder.itemTv.setTextAppearance(context, R.style.dialog_tv_item);
            holder.itemTv.setGravity(Gravity.CENTER);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Object item = dataLst.get(position);
        holder.itemTv.setHint(ReflectUtil.getFieldValueByName(fieldName[0], item).toString());
        holder.itemTv.setText(ReflectUtil.getFieldValueByName(fieldName[1], item).toString());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, null);
        CheckedTextView item = (CheckedTextView)view.findViewById(android.R.id.text1);
        item.setText(ReflectUtil.getFieldValueByName(fieldName[1], getItem(position)).toString());
        item.setHint(ReflectUtil.getFieldValueByName(fieldName[0], getItem(position)).toString());
        return view;
    }

    private class ViewHolder {
        private TextView itemTv;
    }
}
