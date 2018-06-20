package et.tsingtaopad.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;


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
@SuppressLint("UseSparseArrays")
@SuppressWarnings("rawtypes")
public class MultipleKeyValueAdapter extends BaseAdapter {
    
    private Activity context;
    private List dataLst;
    private String[] fieldName;
    private List<String> selectedId;//存状态.
 

    /**
     * 构造函数
     * 
     * @param context
     * @param dataLst       要显示的数据源
     * @param fieldName     fileName[0]:主键属性名称、fileName[1]:显示内容属性名称、
     * @param selectedId    默认选项对应的主键值
     */
    public MultipleKeyValueAdapter(Activity context, 
                List dataLst, String fieldName[],List<String> selectedId) {
        this.context = context;
        this.dataLst = dataLst;
        this.fieldName = fieldName;
        if(CheckUtil.IsEmpty(selectedId)){
           this.selectedId=new ArrayList<String>();
        }else{
           this.selectedId=new ArrayList<String>(selectedId);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.common_multiple_lvitem, null);
            holder.itemTv = (TextView)convertView.findViewById(R.id.common_multiple_tv_lvitem);
            holder.itemCb = (CheckBox)convertView.findViewById(R.id.common_multiple_cb_lvitem);
         
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        addListener(holder,position);//添加事件响应
        holder.itemCb.setTag(position);
        Object item = dataLst.get(position);
        holder.itemTv.setHint(FunUtil.isNullSetSpace(ReflectUtil
                            .getFieldValueByName(fieldName[0], item)).toString());
        holder.itemTv.setText(FunUtil.isNullSetSpace(ReflectUtil
                            .getFieldValueByName(fieldName[1], item)).toString());
    
        if (!CheckUtil.IsEmpty(selectedId)) {
            String keyStr = "";
            for (int i = 0; i < dataLst.size(); i++) {
                keyStr = ReflectUtil.getFieldValueByName(fieldName[0], dataLst.get(i)).toString();
                for (int j=0; j<selectedId.size();j++) {
                    if (selectedId.get(j).equals(keyStr) && i == position) {
                        selectedId.remove(j);
                        selectedId.add(j,position+"");
                    }
                }
            }
        }
        if (selectedId.contains(position+"")) {
            holder.itemCb.setChecked(true);
        } else {
            holder.itemCb.setChecked(false);
        }
        return convertView;
    }
 
    private void addListener(ViewHolder holder,final int position){
        holder.itemCb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

              @Override
              public void onCheckedChanged(CompoundButton buttonView,
                          boolean isChecked) {
                    // TODO Auto-generated method stub
                    if(isChecked){
                       if(!selectedId.contains(position+"")){
                           selectedId.add(position+"");
                       }
                    }
                    else{
                    	//未选中时移除位置
                        selectedId.remove(position+"");
                    }
              } 
             
        });
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
        private CheckBox itemCb;
    }
}
