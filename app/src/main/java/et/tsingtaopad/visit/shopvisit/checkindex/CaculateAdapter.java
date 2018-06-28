package et.tsingtaopad.visit.shopvisit.checkindex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import et.tsingtaopad.R;
import et.tsingtaopad.cui.NoScrollListView;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：CaculateAdapter.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-13</br>      
 * 功能描述: 巡店拜访-查指标的分项采集部分一级Adapter</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class CaculateAdapter extends BaseAdapter {

    private Activity context;
    private List<ProIndex> dataLst;
    private List<KvStc> indexValuelst;
    private List<KvStc> valueLst;
    private List<ProItem> itemLst;

    /**
     *  构造方法
     *  
     * @param context
     * @param dataLst       分项采集部分要显示的数据
     * @param indexValuelst 指标、指标值关系数据
     * @param itemLst       每个产品对应的采集项及数据
     */
    public CaculateAdapter(Activity context, List<ProIndex> dataLst, List<KvStc> indexValuelst, List<ProItem> itemLst) {
        this.context = context;
        this.dataLst = dataLst;
        this.itemLst = itemLst;
        if (CheckUtil.IsEmpty(indexValuelst)) {
            this.indexValuelst = new ArrayList<KvStc>();
        } else {
            this.indexValuelst = indexValuelst;
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
        Log.d("calculate ", position+"===  "+DateUtil.formatDate(new Date(), "yyyyMMdd HH:mm:ss:SSS"));
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.checkindex_caculate_lvitem1, null);
            holder.indexNameTv = (TextView) convertView.findViewById(R.id.caculate_tv_indexname);
            holder.indexValueLv = (NoScrollListView) convertView.findViewById(R.id.caculate_lv_indexvalue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 获取每一个指标对象
        ProIndex item = dataLst.get(position);
        holder.indexNameTv.setHint(item.getIndexId());
        holder.indexNameTv.setText(item.getIndexName());
        for (KvStc kvItem : indexValuelst) {
            if (kvItem.getKey().equals(item.getIndexId())) {
                valueLst = kvItem.getChildLst();
            }
        }
        // 
        holder.indexValueLv.setAdapter(new CaculateItemAdapter(context, item.getIndexValueLst(), valueLst, itemLst,item.getIndexId()));
        ViewUtil.setListViewHeight(holder.indexValueLv);
        return convertView;
    }

    private class ViewHolder {
        private TextView indexNameTv;
        private NoScrollListView indexValueLv;
    }
}
