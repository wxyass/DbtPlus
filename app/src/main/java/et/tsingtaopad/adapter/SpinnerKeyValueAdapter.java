package et.tsingtaopad.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.TextView;


import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.ReflectUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：SpinnerKeyValueAdapter.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-29</br>      
 * 功能描述: Spinner的Adapter的扩展应用</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressWarnings("rawtypes")
public class SpinnerKeyValueAdapter extends BaseAdapter {
    
    private Activity context;
    private List dataLst;
    private String[] fieldName;
    private String selectedId = null;

    /**
     * 构造函数
     * 
     * @param context
     * @param dataLst       要显示的数据源
     * @param fieldName     fileName[0]:主键属性名称、fileName[1]:显示内容属性名称、fileName[2]:默认标识属性名称
     * @param selectedId    默认选项对应的主键值
     */
    public SpinnerKeyValueAdapter(Activity context, List dataLst, String fieldName[], String selectedId) {
        this.context = context;
        this.dataLst = dataLst;
        this.fieldName = fieldName;
        this.selectedId = selectedId;
        
        // 处理默认选项的问题
        if (!CheckUtil.IsEmpty(dataLst) && CheckUtil.isBlankOrNull(selectedId)) {
            if (fieldName.length >= 3) {
                String isDefault = fieldName[2];
                String defVal = "";
                for (Object obj : dataLst) {
                    defVal = FunUtil.isNullSetSpace(ReflectUtil
                            .getFieldValueByName(isDefault, obj)).toString();
                    if (ConstValues.FLAG_1.equals(defVal)) {
                        this.selectedId = ReflectUtil
                                .getFieldValueByName(fieldName[0], obj).toString();
                        break;
                    }
                }
            }
        } 
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
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_adapter_item, null);
            holder.itemTv = (TextView)convertView.findViewById(android.R.id.text1);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        if (!CheckUtil.isBlankOrNull(selectedId)) {
            for (int i = 0; i < dataLst.size(); i++) {
                if (selectedId.equals(ReflectUtil.getFieldValueByName(fieldName[0], dataLst.get(i)).toString())) {
                    position = i;
                    ((Spinner)parent).setTag(selectedId);
                    ((Spinner)parent).setSelection(position);
                    selectedId = null;
                    break;
                }
            }
        }
        Object item = dataLst.get(position);
        holder.itemTv.setHint(FunUtil.isNullSetSpace(ReflectUtil
                            .getFieldValueByName(fieldName[0], item)).toString());
        holder.itemTv.setTag(FunUtil.isNullSetSpace(ReflectUtil
                            .getFieldValueByName(fieldName[0], item)).toString());
        ((Spinner)parent).setTag(FunUtil.isNullSetSpace(ReflectUtil
                            .getFieldValueByName(fieldName[0], item)).toString());
        holder.itemTv.setText(FunUtil.isNullSetSpace(ReflectUtil
                            .getFieldValueByName(fieldName[1], item)).toString());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, null);
        CheckedTextView item = (CheckedTextView)view.findViewById(android.R.id.text1);
        item.setText(FunUtil.isNullSetSpace(ReflectUtil.getFieldValueByName(fieldName[1], getItem(position))).toString());
        item.setHint(FunUtil.isNullSetSpace(ReflectUtil.getFieldValueByName(fieldName[0], getItem(position))).toString());
        item.setTag(FunUtil.isNullSetSpace(ReflectUtil.getFieldValueByName(fieldName[0], getItem(position))).toString());
        return view;
    }

    private class ViewHolder {
        private TextView itemTv;
    }
}
