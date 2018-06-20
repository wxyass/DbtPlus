package et.tsingtaopad.operation.promotion;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：PromotionAdapter.java</br>
 * 作者：hongen   </br>
 * 创建时间：2014-2-22</br>      
 * 功能描述: 促销活动查询Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class PromotionAdapter extends BaseAdapter {
    
    private Activity context;
    private List<String> dataLst;
    private int imgId;

    /**
     * 构造函数
     * 
     * @param context
     * @param dataLst       要显示的数据源
     * @param imgId         达成、未达成图片的资源Id
     */
    public PromotionAdapter(Activity context, List<String> dataLst, int imgId) {
        this.context = context;
        this.dataLst = dataLst;
        this.imgId = imgId;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.promotion_lv_item, null);
            holder.numTv = (TextView)convertView.findViewById(R.id.promotion_lv_tv_num);
            holder.termTv = (TextView)convertView.findViewById(R.id.promotion_lv_tv_term);
            holder.flagImg = (ImageView)convertView.findViewById(R.id.promotion_lv_img_flag);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        String item = dataLst.get(position);
        holder.numTv.setText(String.valueOf(position + 1));
        holder.termTv.setText(item);
        holder.flagImg.setBackgroundResource(imgId);
        return convertView;
    }

    private class ViewHolder {
        private TextView numTv;
        private TextView termTv;
        private ImageView flagImg;
    }
}
