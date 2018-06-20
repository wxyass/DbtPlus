package et.tsingtaopad.visit.termadd;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermListAdapter.java</br>
 * 作者：admin  </br>
 * 创建时间：2014-1-19</br>      
 * 功能描述:终端列表adapter </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermAddListAdapter extends BaseAdapter
{
    private Activity context;
    private List<MstTerminalinfoM> dataLst;
    private TermAddService service;
    private TermAddListFragment termAddListFragment;

    public TermAddListAdapter(TermAddListFragment fragment, Activity context, List<MstTerminalinfoM> dataLst, TermAddService service)
    {
        this.context = context;
        this.dataLst = dataLst;
        this.service = service;
        this.termAddListFragment = fragment;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.shopvisit_terminal_add_lvitem, null);
            holder.termKeyTv = (TextView) convertView.findViewById(R.id.term_item_tv_key);
            holder.termNameTv = (TextView) convertView.findViewById(R.id.term_item_tv_name);
            holder.updateBt = (Button) convertView.findViewById(R.id.term_item_bt_update);
            holder.delBt = (Button) convertView.findViewById(R.id.term_item_bt_del);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        final MstTerminalinfoM term = this.dataLst.get(position);
        holder.termKeyTv.setText(term.getTerminalkey());
        holder.termNameTv.setText(term.getTerminalname());
        holder.delBt.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                delDialog(term);
            }
        });

        holder.updateBt.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Fragment fragment = new TermAddFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("term", term);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = termAddListFragment.getFragmentManager().beginTransaction();
                transaction.replace(R.id.visit_container, fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return convertView;
    }

    private class ViewHolder
    {
        private TextView termKeyTv;
        private TextView termNameTv;
        private Button updateBt;
        private Button delBt;
    }

    /***
     * 删除提示框
     * @param term
     */
    private void delDialog(final MstTerminalinfoM term)
    {
        Builder builder = new Builder(context);
        builder.setTitle("温馨提示");
        builder.setMessage("是否删除'" + term.getTerminalname());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                TermAddService.delTermAddFromShared(context,term);
                dataLst.remove(term);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
