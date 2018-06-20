package et.tsingtaopad.visit.shopvisit.checkindex;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;


import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexCalculateStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：CaculateItemAdapter.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-13</br>      
 * 功能描述: 巡店拜访-无关系产品指标采集部分的Adapter(暂时未用)</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class NoProIndexItemAdapter extends BaseAdapter {
    
    private Activity context;
    private List<CheckIndexCalculateStc> dataLst;
    private List<KvStc> indexValuelst;
    private List<KvStc> templst;
    
    /**
     * 构造函数
     * 
     * @param context
     * @param dataLst       产品及指标值显示数据
     * @param indexValuelst 当前指标对应的指标值集合
     */
    public NoProIndexItemAdapter(Activity context,
            List<CheckIndexCalculateStc> dataLst, List<KvStc> indexValuelst) {
        this.context = context;
        this.dataLst = dataLst;
        this.indexValuelst = indexValuelst;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_noproindex_lvitem, null);
            holder.indexTv = (TextView)convertView.findViewById(R.id.noproindex_tv_indexname);
            holder.indexValueRg = (RadioGroup)convertView.findViewById(R.id.noproindex_rg_indexvalue);
            holder.indexValueEt = (EditText)convertView.findViewById(R.id.noproindex_et_indexvalue);
            holder.indexValueNumEt = (EditText)convertView.findViewById(R.id.noproindex_et_indexvalue_num);
            holder.indexValueSp = (Spinner)convertView.findViewById(R.id.noproindex_sp_indexvalue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        CheckIndexCalculateStc item = dataLst.get(position);
        holder.indexTv.setHint(item.getIndexId());
        holder.indexTv.setText(item.getIndexName());
        
        for (KvStc kvItem : indexValuelst) {
            if (kvItem.getKey().equals(item.getIndexId())) {
                templst = kvItem.getChildLst();
                break;
            }
        }
        
        // 判定显示方式及初始化显示数据 0:计算单选、 1:单选、 2:文本框、 3:数值、 4:下拉单选
        if (ConstValues.FLAG_1.equals(item.getIndexType())) {
            holder.indexValueRg.setVisibility(View.VISIBLE);
            RadioButton rb;
            boolean isExist = false;
            for (KvStc kvItem : templst) {
                for (int i = 0; i < holder.indexValueRg.getChildCount(); i++) {
                    rb = (RadioButton)holder.indexValueRg.getChildAt(i);
                    if (kvItem.getKey().equals(rb.getHint().toString())) {
                        isExist = true;
                        break;
                    }
                } 
                if (!isExist) {
                    rb = new RadioButton(context);
                    rb.setTextColor(context.getResources()
                            .getColor(R.color.listview_item_font_color));
                    rb.setTextSize(17);
                    rb.setWidth(100);
                    rb.setText(kvItem.getValue());
                    rb.setHint(kvItem.getKey());
                    holder.indexValueRg.addView(rb);
                }
            }
            
        } else if (ConstValues.FLAG_2.equals(item.getIndexType())) {
            holder.indexValueEt.setVisibility(View.VISIBLE);
            
        } else if (ConstValues.FLAG_3.equals(item.getIndexType())) {
            holder.indexValueNumEt.setVisibility(View.VISIBLE);
            
        } else if (ConstValues.FLAG_4.equals(item.getIndexType())) {
            holder.indexValueSp.setVisibility(View.VISIBLE);
            holder.indexValueSp.setAdapter(new SpinnerKeyValueAdapter(
                    context, templst, new String[]{"key","value"}, item.getIndexValueId()));
            
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView indexTv;
        private RadioGroup indexValueRg;
        private EditText indexValueEt;
        private EditText indexValueNumEt;
        private Spinner indexValueSp;
    }
}
