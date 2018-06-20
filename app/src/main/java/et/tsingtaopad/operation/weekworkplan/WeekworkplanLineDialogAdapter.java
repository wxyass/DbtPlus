package et.tsingtaopad.operation.weekworkplan;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.tools.CheckUtil;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：WeekworkplanLineDialogAdapter.java</br>
 * 作者：@吴欣伟</br>
 * 创建时间：2014/2/13</br>
 * 功能描述: 周工作计划线路选择Adapter</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期       原因  BUG号    修改人 修改版本</br>
 */
public class WeekworkplanLineDialogAdapter extends BaseAdapter {

    private Context context;
    private List<MstRouteM> dataLst;
    
    public WeekworkplanLineDialogAdapter(Context context, List<MstRouteM> dataLst) {
        this.context = context;
        this.dataLst = dataLst;
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
        
        ViewHolder holder = null;
        if (arg1 == null) {
            arg1 = LayoutInflater
                    .from(context)
                    .inflate(
                            R.layout.operation_weekworkplan_linedialog_lvitem, null);
            holder = new ViewHolder();
            holder.tv = (TextView) arg1.findViewById(R.id.weekworkplan_tv_linedialog_item);
            arg1.setTag(holder);
        }else {
            holder = (ViewHolder) arg1.getTag();
        }
        MstRouteM route = dataLst.get(arg0);
        holder.tv.setText(route.getRoutename());
        holder.tv.setHint(route.getRoutekey());

        return arg1;
    }
    private class ViewHolder{
        private TextView tv;
    }
}
