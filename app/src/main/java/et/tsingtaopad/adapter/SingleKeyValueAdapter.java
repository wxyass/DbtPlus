package et.tsingtaopad.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.RadioButton;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.ReflectUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：SingleKeyValueAdapter.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-29</br>      
 * 功能描述: 单选的Adapter的扩展应用</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressWarnings("rawtypes")
public class SingleKeyValueAdapter extends BaseAdapter {
    
    private Activity context;
    private List dataLst;
    private String[] fieldName;
    private String selectedId = null;

    /**
     * 构造函数
     * 
     * @param context
     * @param dataLst       要显示的数据源
     * @param fieldName     fileName[0]:主键属性名称、fileName[1]:显示内容属性名称、
     * @param selectedId    默认选项对应的主键值
     */
    public SingleKeyValueAdapter(Activity context, 
                List dataLst, String fieldName[], String selectedId) {
        this.context = context;
        this.dataLst = dataLst;
        this.fieldName = fieldName;
        this.selectedId = selectedId;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.common_single_lvitem, null);
            holder.itemTv = (TextView)convertView.findViewById(R.id.common_single_tv_lvitem);
            holder.itemRb = (RadioButton)convertView.findViewById(R.id.common_single_rb_lvitem);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        Object item = dataLst.get(position);
        holder.itemTv.setTag(FunUtil.isNullSetSpace(ReflectUtil
                            .getFieldValueByName(fieldName[0], item)).toString());
        holder.itemTv.setText(FunUtil.isNullSetSpace(ReflectUtil
                            .getFieldValueByName(fieldName[1], item)).toString());
        holder.itemRb.setChecked(false);
        if (!CheckUtil.isBlankOrNull(selectedId)) {
            String keyStr = "";
            for (int i = 0; i < dataLst.size(); i++) {
                keyStr = ReflectUtil.getFieldValueByName(fieldName[0], dataLst.get(i)).toString();
                if (selectedId.equals(keyStr) && i == position) {
                    holder.itemRb.setChecked(true);
                }
            }
        }
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
        private RadioButton itemRb;
    }
}
