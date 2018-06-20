package et.tsingtaopad.operation.indexstatus;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：IndexstatusAdapter.java</br>
 * 作者：@吴欣伟</br>
 * 创建时间：2013/11/26</br>
 * 功能描述: 指标状态查询Adapter</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期       原因  BUG号    修改人 修改版本</br>
 */
public class IndexstatusProDialogAdapter extends BaseAdapter {

    private Context context;
    private List<KvStc> dataLst;
    private static HashMap<Integer, Boolean> isSelected;

    public IndexstatusProDialogAdapter(Context context, List<KvStc> dataLst) {
        this.context = context;
        this.dataLst = dataLst;
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
    }

    /**
     * 初始化isSelected的数据
     */
    private void initDate() {
        for (int i = 0; i < dataLst.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {

        if (CheckUtil.IsEmpty(this.dataLst)) {
            return 0;

        } else {
            return this.dataLst.size();
        }
    }

    @Override
    public Object getItem(int arg0) {

        if (CheckUtil.IsEmpty(this.dataLst)) {
            return null;

        } else {
            return this.dataLst.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (arg1 == null) {
            arg1 = LayoutInflater.from(context).inflate(
                    R.layout.operation_indexstatus_dialogpro_lvitem, null);
            holder = new ViewHolder();
            holder.tv = (TextView) arg1
                    .findViewById(R.id.indexstatus_tv_dialogpro_lvitem);
            holder.cb = (CheckBox) arg1
                    .findViewById(R.id.indexstatus_cb_dialogpro_lvitem);
            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }
        KvStc kvStc = dataLst.get(arg0);
        holder.tv.setText(kvStc.getValue());

        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(isSelected.get(arg0));

        return arg1;
    }

    private class ViewHolder {
        private TextView tv;
        private CheckBox cb;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        IndexstatusProDialogAdapter.isSelected = isSelected;
    }
}
