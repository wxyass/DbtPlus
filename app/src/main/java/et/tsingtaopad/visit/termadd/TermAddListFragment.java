package et.tsingtaopad.visit.termadd;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstTerminalinfoM;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermAddListFragment.java</br>
 * 作者：admin   </br>
 * 创建时间：2014-12-23</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermAddListFragment extends BaseFragmentSupport implements OnClickListener
{
    List<MstTerminalinfoM> dataLst;
    TermAddService service;
    ListView termaddLv;
    TermAddListAdapter adapter;
    Button backBt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.visit_terminaladd_list, null);
        this.initView(view);
        this.initData();
        return view;
    }

    private void initView(View view)
    {
        termaddLv = (ListView) view.findViewById(R.id.termadd_lv);
        backBt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
		RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
        backBt.setOnClickListener(this);
    }

    private void initData()
    {
        service = new TermAddService(getActivity(), null, null);
        dataLst = TermAddService.getTermAddListFromShared(getActivity());
        adapter = new TermAddListAdapter(this, getActivity(), dataLst, service);
        termaddLv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        	case R.id.banner_navigation_rl_back:
            case R.id.banner_navigation_bt_back:
                getFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }
}
