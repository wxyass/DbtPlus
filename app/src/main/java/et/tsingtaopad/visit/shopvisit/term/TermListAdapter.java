package et.tsingtaopad.visit.shopvisit.term;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermListAdapter.java</br>
 * 作者：hongen   </br>
 * 创建时间：2014-1-19</br>      
 * 功能描述:终端列表adapter </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermListAdapter extends BaseAdapter implements OnClickListener
{

    private Activity context;
    private List<MstTermListMStc> dataLst;
    private List<MstTermListMStc> seqTermList;
    private TextView confirmBt;
    private String termId;
    private int selectItem = -1;
    private boolean isUpdate;//是否处于修改状态

    public TermListAdapter(Activity context, List<MstTermListMStc> seqTermList, List<MstTermListMStc> termialLst, TextView confirmBt, String termId)
    {
        this.context = context;
        this.seqTermList = seqTermList;
        this.dataLst = termialLst;
        this.confirmBt = confirmBt;
        this.termId = termId;
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
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.shopvisit_terminal_lvitem2, null);
            holder.terminalSequenceEt = (EditText) convertView.findViewById(R.id.term_item_et_sequence);
            holder.terminalNameTv = (TextView) convertView.findViewById(R.id.term_item_tv_name);
            holder.topTv = (TextView) convertView.findViewById(R.id.term_item_tv_top);
            holder.visitDateTv = (TextView) convertView.findViewById(R.id.term_item_tv_visitdate);
            holder.terminalRb = (RadioButton) convertView.findViewById(R.id.term_item_rb);
            holder.terminalTypeTv = (TextView) convertView.findViewById(R.id.term_item_tv_type);
            holder.updateIv = (ImageView) convertView.findViewById(R.id.term_item_iv_update);
            holder.mineIv = (ImageView) convertView.findViewById(R.id.term_item_iv_mime);
            holder.mineProtocolIv = (ImageView) convertView.findViewById(R.id.term_item_iv_mineprotocol);
            holder.vieIv = (ImageView) convertView.findViewById(R.id.term_item_iv_vie);
            holder.itermLayout = (LinearLayout) convertView.findViewById(R.id.itrm_item_ll);
            holder.itemCoverV = convertView.findViewById(R.id.term_item_v_cover);
            holder.vieProtocolIv = (ImageView) convertView.findViewById(R.id.term_item_iv_vieprotocol);
            holder.terminalSequenceEt.addTextChangedListener(new MyTextWatcher(holder)
            {
                @Override
                public void afterTextChanged(Editable s, ViewHolder holder)
                {
                    int position = (Integer) holder.terminalSequenceEt.getTag();
                    saveEditValue(s.toString(), position);
                }
            });
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.terminalSequenceEt.setTag(position);

        MstTermListMStc item = dataLst.get(position);
        holder.terminalNameTv.setHint(item.getTerminalkey());
        //是否允许修改
        if (isUpdate)
        {
            holder.terminalSequenceEt.setEnabled(true);
            holder.terminalSequenceEt.setBackgroundColor(Color.LTGRAY);
        }
        else
        {
            holder.terminalSequenceEt.setEnabled(false);
        }
        //失效终端且未审核通过的变灰,不可编辑顺序，不可选中
        if ("3".equals(item.getStatus()))
        {
            holder.terminalSequenceEt.setEnabled(false);
            holder.terminalSequenceEt.setBackgroundColor(Color.WHITE);
            holder.itemCoverV.setVisibility(View.VISIBLE);
            holder.itemCoverV.getBackground().setAlpha(50);
            holder.terminalRb.setEnabled(false);
            holder.terminalRb.setOnClickListener(null);
            holder.terminalRb.setTag(position);
            holder.itermLayout.setOnClickListener(null);
            holder.itermLayout.setTag(position);
        }
        else
        {
            holder.itemCoverV.setVisibility(View.GONE);
            holder.terminalRb.setOnClickListener(this);
            holder.terminalRb.setTag(position);
            holder.itermLayout.setOnClickListener(this);
            holder.itermLayout.setTag(position);
        }
        holder.terminalSequenceEt.setText(item.getSequence());
        holder.terminalNameTv.setText(item.getTerminalname());
        // top1 - top10
        if(Integer.parseInt(item.getTopnum())<10){
            holder.topTv.setText("top"+item.getTopnum());
        }else{
            holder.topTv.setText("");
        }
        holder.terminalTypeTv.setText(item.getTerminalType());
        if (!CheckUtil.isBlankOrNull(item.getVisitTime()))
        {
            holder.visitDateTv.setVisibility(View.VISIBLE);
            holder.visitDateTv.setText(item.getVisitTime());
        }
        else
        {
            holder.visitDateTv.setVisibility(View.GONE);
        }

        if (ConstValues.FLAG_1.equals(item.getSyncFlag()))
        {
            holder.updateIv.setVisibility(View.VISIBLE);
            holder.terminalNameTv.setTextColor(Color.RED);
        }
        else if (ConstValues.FLAG_0.equals(item.getSyncFlag()))
        {
            holder.terminalNameTv.setTextColor(Color.YELLOW);
            holder.updateIv.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.terminalNameTv.setTextColor(Color.BLACK);
            holder.updateIv.setVisibility(View.INVISIBLE);
        }

        if (ConstValues.FLAG_1.equals(item.getMineFlag()))
        {
            holder.mineIv.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.mineIv.setVisibility(View.INVISIBLE);
        }

        if (ConstValues.FLAG_1.equals(item.getMineProtocolFlag()))
        {
            holder.mineProtocolIv.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.mineProtocolIv.setVisibility(View.INVISIBLE);
        }

        if (ConstValues.FLAG_1.equals(item.getVieFlag()))
        {
            holder.vieIv.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.vieIv.setVisibility(View.INVISIBLE);
        }

        if (ConstValues.FLAG_1.equals(item.getVieProtocolFlag()))
        {
            holder.vieProtocolIv.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.vieProtocolIv.setVisibility(View.INVISIBLE);
        }

        if (selectItem == -1 && item.getTerminalkey().equals(termId))
        {
            selectItem = position;
        }
        if (position == selectItem)
        {
            holder.terminalRb.setChecked(true);
            holder.terminalNameTv.setTextColor(context.getResources().getColor(R.color.font_color_green));
            holder.terminalTypeTv.setTextColor(context.getResources().getColor(R.color.font_color_green));
            holder.visitDateTv.setTextColor(context.getResources().getColor(R.color.font_color_green));
            if (!CheckUtil.isBlankOrNull(item.getVisitTime()))
            {
                holder.visitDateTv.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            holder.terminalRb.setChecked(false);

            // 已提交过的
            if (ConstValues.FLAG_1.equals(item.getUploadFlag()))
            {
                holder.terminalNameTv.setTextColor(context.getResources().getColor(R.color.termlst_sync_font_color));
                holder.terminalTypeTv.setTextColor(context.getResources().getColor(R.color.termlst_sync_font_color));
                holder.visitDateTv.setTextColor(context.getResources().getColor(R.color.termlst_sync_font_color));

                // 已拜访过未上传的
            }
            else if (ConstValues.FLAG_0.equals(item.getUploadFlag()))
            {
                holder.terminalNameTv.setTextColor(context.getResources().getColor(R.color.termlst_insync_font_color));
                holder.terminalTypeTv.setTextColor(context.getResources().getColor(R.color.termlst_insync_font_color));
                holder.visitDateTv.setTextColor(context.getResources().getColor(R.color.termlst_insync_font_color));

                // 未拜访过的
            }
            else
            {
                holder.terminalNameTv.setTextColor(Color.BLACK);

                holder.terminalNameTv.setTextColor(context.getResources().getColor(R.color.listview_item_font_color));
                holder.terminalTypeTv.setTextColor(context.getResources().getColor(R.color.listview_item_font_color));
                holder.visitDateTv.setTextColor(context.getResources().getColor(R.color.listview_item_font_color));
            }
            holder.visitDateTv.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder
    {
        private EditText terminalSequenceEt;
        private RadioButton terminalRb;
        private TextView terminalNameTv;
        private TextView topTv;
        private TextView visitDateTv;
        private TextView terminalTypeTv;
        private ImageView updateIv;
        private ImageView mineIv;
        private ImageView mineProtocolIv;
        private ImageView vieIv;
        private ImageView vieProtocolIv;
        private LinearLayout itermLayout;
        private View itemCoverV;
    }

    @Override
    public void onClick(View v)
    {
        int position = Integer.parseInt(v.getTag().toString());
        setSelectItem(position);
        notifyDataSetChanged();
        confirmBt.setVisibility(View.VISIBLE);
        confirmBt.setTag(dataLst.get(position));
    }

    public int getSelectItem()
    {
        return selectItem;
    }

    public void setSelectItem(int selectItem)
    {
        this.selectItem = selectItem;
    }

    /**
     * @return the isUpdate
     */
    public boolean isUpdate()
    {
        return isUpdate;
    }

    /**
     * @param isUpdate the isUpdate to set
     */
    public void setUpdate(boolean isUpdate)
    {
        this.isUpdate = isUpdate;
    }

    private void saveEditValue(String str, int position)
    {
        MstTermListMStc option = dataLst.get(position);
        if (isUpdate && !str.equals(option.getSequence()))
        {
            resetSeq(option.getTerminalkey(), str);
        }
        option.setSequence(str);
    }

    /***
     * 重新终端设置顺序
     * @param termKey
     * @param newSeq
     */
    private void resetSeq(String termKey, String newSeq)
    {
        for (int i = 0; i < seqTermList.size(); i++)
        {
            MstTermListMStc term = seqTermList.get(i);
            if (termKey.equals(term.getTerminalkey()))
            {
                if (CheckUtil.isBlankOrNull(newSeq))
                {
                    seqTermList.remove(term);
                    term.setSequence(newSeq);
                    seqTermList.add(term);
                }
                else
                {
                    int newSeq_i = Integer.parseInt(newSeq);
                    if (newSeq_i >= seqTermList.size() - 1)
                    {
                        seqTermList.remove(term);
                        term.setSequence(newSeq);
                        seqTermList.add(term);
                    }
                    else
                    {
                        seqTermList.remove(term);
                        term.setSequence(newSeq);
                        if (newSeq_i == 0)
                        {
                            seqTermList.add(0, term);
                        }
                        else
                        {
                            seqTermList.add(newSeq_i - 1, term);
                        }
                    }
                }
                break;
            }
        }
    }

    abstract class MyTextWatcher implements TextWatcher
    {
        public MyTextWatcher(ViewHolder holder)
        {
            mHolder = holder;
        }

        private ViewHolder mHolder;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            afterTextChanged(s, mHolder);
        }

        public abstract void afterTextChanged(Editable s, ViewHolder holder);
    }
}
