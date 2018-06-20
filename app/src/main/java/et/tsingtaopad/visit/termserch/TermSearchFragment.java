package et.tsingtaopad.visit.termserch;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.visit.shopvisit.line.domain.MstRouteMStc;
import et.tsingtaopad.visit.shopvisit.term.TermListService;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.visit.shopvisit.termindex.TermIndexActivity;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：TermSearchFragment.java</br>
 * 作者：wf   </br>
 * 创建时间：2015-3-23</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermSearchFragment extends BaseFragmentSupport implements OnClickListener
{
	String TAG = "TermSearchFragment";
    List<MstTermListMStc> dataList;
    TermListService service;
    ListView termaddLv;
    TermSearchListAdapter adapter;
    Button backBt;
    
    private List<MstTermListMStc> termLst;
	private TextView confirmBt;
	private MstTermListMStc termStc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.visit_terminal_search, null);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view)
    {
        termaddLv = (ListView) view.findViewById(R.id.termListView);
        backBt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
        confirmBt = (TextView) view.findViewById(R.id.banner_navigation_bt_confirm);
        //confirmBt.setOnClickListener(this);
        backBt.setOnClickListener(this);
    }

    private void initData()
    {
        String seacrch = (String) getArguments().get("seacrch");
        service = new TermListService(getActivity());
        dataList = service.getTermListByName(seacrch);
        adapter = new TermSearchListAdapter(getActivity(), dataList);
        termaddLv.setAdapter(adapter);
        termaddLv.setOnItemClickListener(new TermSearchItemClickListener());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.banner_navigation_bt_back:
                getFragmentManager().popBackStack();
                break;
                
//            case R.id.banner_navigation_bt_confirm:
//            	// 如果已结束拜访
//    	        if (ConstValues.FLAG_1.equals(termStc.getUploadFlag()) || ConstValues.FLAG_1.equals(termStc.getSyncFlag()))
//    	        {
//    	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//    	            builder.setTitle(R.string.termlist_confirm_title);
//    	            builder.setMessage(R.string.termlist_confirm_msg);
//    	            builder.setCancelable(false);
//    	            builder.setPositiveButton(R.string.termlist_confirm_sure, new DialogInterface.OnClickListener()
//    	            {
//    	                public void onClick(DialogInterface dialog, int which)
//    	                {
//    	                    DbtLog.logUtils(TAG, "termkey:"+termStc.getTerminalkey()+" termname："+termStc.getTerminalname());
//    	                    dialog.dismiss();
//    	                    Intent intent = new Intent(getActivity(), TermIndexActivity.class);
//    	                    intent.putExtra("termStc", termStc);
//    	                    intent.putExtra("seeFlag", "0");
//    	                    getActivity().startActivity(intent);
//    	                }
//    	            });
//    	            builder.setNegativeButton(R.string.termlist_confirm_cancel, new DialogInterface.OnClickListener()
//    	            {
//    	                public void onClick(DialogInterface dialog, int which)
//    	                {
//    	                    dialog.dismiss();
//    	                }
//    	            });
//    	            builder.setNeutralButton(R.string.termlist_confirm_see, new DialogInterface.OnClickListener()
//    	            {
//
//    	                @Override
//    	                public void onClick(DialogInterface dialog, int which)
//    	                {
//    	                    dialog.dismiss();
//    	                    Intent intent = new Intent(getActivity(), TermIndexActivity.class);
//    	                    intent.putExtra("termStc", termStc);
//    	                    intent.putExtra("seeFlag", "1"); // 查看标识
//    	                    getActivity().startActivity(intent);
//    	                }
//    	            });
//    	            builder.create().show();
//
//    	        }
//    	        else
//    	        {
//    	            Intent intent = new Intent(getActivity(), TermIndexActivity.class);
//    	            intent.putExtra("termStc", termStc);
//    	            getActivity().startActivity(intent);
//    	        }
            default:
                break;
        }
    }
    
    class TermSearchItemClickListener implements OnItemClickListener{

		

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			adapter.setSelectItem(position);
    		adapter.notifyDataSetInvalidated();
//    		confirmBt.setVisibility(view.VISIBLE);
			termStc = dataList.get(position);
			TermListService service = new TermListService(getActivity());
			List<String> routes = new ArrayList<String>();
	        routes.add(termStc.getRoutekey());
			termLst = service.queryTerminal(routes);
			
			if(termStc!=null){
	            for(MstTermListMStc term:termLst){
	                if(termStc.getTerminalkey().equals(term.getTerminalkey())){
	                	termStc =term;
	                }
	            }
	        }
			 if (ConstValues.FLAG_1.equals(termStc.getUploadFlag()) || ConstValues.FLAG_1.equals(termStc.getSyncFlag()))
 	        {
 	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
 	            builder.setTitle(R.string.termlist_confirm_title);
 	            builder.setMessage(R.string.termlist_confirm_msg);
 	            builder.setCancelable(false);
 	            builder.setPositiveButton(R.string.termlist_confirm_sure, new DialogInterface.OnClickListener()
 	            {
 	                public void onClick(DialogInterface dialog, int which)
 	                {
 	                    DbtLog.logUtils(TAG, "termkey:"+termStc.getTerminalkey()+" termname："+termStc.getTerminalname());
 	                    dialog.dismiss();
 	                    Intent intent = new Intent(getActivity(), TermIndexActivity.class);
 	                    intent.putExtra("termStc", termStc);
 	                    intent.putExtra("seeFlag", "0");
 	                    getActivity().startActivity(intent);
 	                }
 	            });
 	            builder.setNegativeButton(R.string.termlist_confirm_cancel, new DialogInterface.OnClickListener()
 	            {
 	                public void onClick(DialogInterface dialog, int which)
 	                {
 	                    dialog.dismiss();
 	                }
 	            });
 	            builder.setNeutralButton(R.string.termlist_confirm_see, new DialogInterface.OnClickListener()
 	            {

 	                @Override
 	                public void onClick(DialogInterface dialog, int which)
 	                {
 	                    dialog.dismiss();
 	                    Intent intent = new Intent(getActivity(), TermIndexActivity.class);
 	                    intent.putExtra("termStc", termStc);
 	                    intent.putExtra("seeFlag", "1"); // 查看标识
 	                    getActivity().startActivity(intent);
 	                }
 	            });
 	            builder.create().show();

 	        }else{
 	        	
 	        	AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
 	        	builder.setTitle(R.string.termlist_confirm_title);
 	        	builder.setMessage(R.string.termlist_sure);
 	        	builder.setPositiveButton(R.string.termlist_confirm_sure, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), TermIndexActivity.class);
	    	            intent.putExtra("termStc", termStc);
	    	            getActivity().startActivity(intent);
					}
				});
 	        	
 	        	builder.setNegativeButton(R.string.termlist_confirm_cancel, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
 	        	builder.create().show();
 	        }

		}
    	
    }
    
}
