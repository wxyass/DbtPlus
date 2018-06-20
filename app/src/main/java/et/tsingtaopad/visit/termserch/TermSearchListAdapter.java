package et.tsingtaopad.visit.termserch;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：TermSearchListAdapter.java</br>
 * 作者：wf   </br>
 * 创建时间：2015-3-23</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermSearchListAdapter extends BaseAdapter
{
    private Activity context;
    private List<MstTermListMStc> dataLst;
    private int selectItem = -1;
    /**
     * @param context
     * @param termList
     */
    public TermSearchListAdapter(Activity context, List<MstTermListMStc> termList)
    {
        super();
        this.context = context;
        this.dataLst = termList;
    }

    @Override
    public int getCount()
    {

        if (CheckUtil.IsEmpty(this.dataLst))
        {
            return 0;

        }
        else
        {
            return this.dataLst.size();
        }
    }

    @Override
    public Object getItem(int arg0)
    {

        if (CheckUtil.IsEmpty(this.dataLst))
        {
            return null;

        }
        else
        {
            return this.dataLst.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0)
    {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        Log.e("message", ">>>>>" + position);
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.visit_terminal_serch_list, null);
            holder.termNameTv = (TextView) convertView.findViewById(R.id.termName);
            holder.lineNameTv = (TextView) convertView.findViewById(R.id.lineName);
            holder.termContactTv = (TextView) convertView.findViewById(R.id.termContact);
            holder.termAddressTv = (TextView) convertView.findViewById(R.id.termAddress);
            holder.termCodeTv = (TextView) convertView.findViewById(R.id.termCode);
            holder.termMobileTv = (TextView) convertView.findViewById(R.id.termMobile);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        
        if (position == selectItem) {
        	holder.termNameTv.setTextColor(context
                    .getResources().getColor(R.color.font_color_green));
            holder.lineNameTv.setTextColor(context
                    .getResources().getColor(R.color.font_color_green));
            holder.termContactTv.setTextColor(context
                    .getResources().getColor(R.color.font_color_green));
            holder.termAddressTv.setTextColor(context
                    .getResources().getColor(R.color.font_color_green));
            holder.termCodeTv.setTextColor(context
                    .getResources().getColor(R.color.font_color_green));
            holder.termMobileTv.setTextColor(context
                    .getResources().getColor(R.color.font_color_green));
        }else{
        	holder.termNameTv.setTextColor(context
                    .getResources().getColor(R.color.listview_item_font_color));
            holder.lineNameTv.setTextColor(context
                    .getResources().getColor(R.color.listview_item_font_color));
            holder.termContactTv.setTextColor(context
                    .getResources().getColor(R.color.listview_item_font_color));
            holder.termAddressTv.setTextColor(context
                    .getResources().getColor(R.color.listview_item_font_color));
            holder.termCodeTv.setTextColor(context
                    .getResources().getColor(R.color.listview_item_font_color));
            holder.termMobileTv.setTextColor(context
                    .getResources().getColor(R.color.listview_item_font_color));
        }
        MstTermListMStc term = this.dataLst.get(position);
        holder.termNameTv.setText(term.getTerminalname());
        holder.lineNameTv.setText(term.getRoutename());
        holder.termContactTv.setText(term.getContact());
        holder.termAddressTv.setText(term.getAddress());
        holder.termCodeTv.setText(term.getTerminalcode());
        holder.termMobileTv.setText(term.getMobile());

        return convertView;
    }

    private class ViewHolder
    {
        private TextView termNameTv;
        private TextView lineNameTv;
        private TextView termContactTv;
        private TextView termAddressTv;
        private TextView termCodeTv;
        private TextView termMobileTv;
    }
    
    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }
}
