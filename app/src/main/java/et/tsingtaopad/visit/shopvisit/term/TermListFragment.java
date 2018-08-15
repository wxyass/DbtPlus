package et.tsingtaopad.visit.shopvisit.term;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.GlobalValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.line.domain.MstRouteMStc;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.visit.shopvisit.term.domain.TermSequence;
import et.tsingtaopad.visit.shopvisit.termindex.TermIndexActivity;
import et.tsingtaopad.visit.termadd.TermAddFragment;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermListFragment.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-28</br>      
 * 功能描述: 巡店拜访_选择终端列表</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
//选择终端列表
public class TermListFragment extends BaseFragmentSupport implements OnClickListener, OnItemClickListener, TextWatcher
{

    String TAG="TermListFragment";
    private TermListService service;

    private MstTermListMStc termStc;
    private List<MstTermListMStc> termLst;
    private List<MstTermListMStc> seqTermList;
    private List<MstTermListMStc> tempLst;
    private Map<String, String> termPinyinMap;

    private TextView titleTv;
    private Button backBt;
    private TextView confirmBt;
    private TextView visitLine;
    private TextView visitDate;
    private TextView visitDay;
    private EditText searchEt;
    private Button searchBt;
    private Button addTermBt;
    private Button updateTermBt;

    private ListView termLv;
    private TermListAdapter adapter;
    private String termId;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {

            // 上传状态位
                case ConstValues.WAIT2:
                    if (!(CheckUtil.IsEmpty(tempLst) || adapter == null || adapter.getSelectItem() == -1))
                    {
                        tempLst.get(adapter.getSelectItem()).setSyncFlag(ConstValues.FLAG_1);
                        confirmBt.setTag(tempLst.get(adapter.getSelectItem()));
                        adapter.notifyDataSetChanged();
                    }
                    onResume();
                    break;

                // 新增终端后刷新界面
                case ConstValues.WAIT3:
                    onResume();
                    break;

                default:
                    break;
            }
        }

    };
	private RelativeLayout backRl;
	private RelativeLayout confirmRl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_terminal, null);
        this.initView(view);
        return view;
    }

    /**
     * 初始化界面组件
     */
    private void initView(View view)
    {

        DbtLog.logUtils(TAG, "initView");
        // 绑定页面组件
        titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
        backBt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
        confirmBt = (TextView) view.findViewById(R.id.banner_navigation_bt_confirm);
        visitLine = (TextView) view.findViewById(R.id.term_tv_visitline);
        visitDate = (TextView) view.findViewById(R.id.term_tv_visitline_date);
        visitDay = (TextView) view.findViewById(R.id.term_tv_visitline_day);
        termLv = (ListView) view.findViewById(R.id.term_lv);
        searchEt = (EditText) view.findViewById(R.id.term_et_search);
        searchBt = (Button) view.findViewById(R.id.term_bt_search);
        addTermBt = (Button) view.findViewById(R.id.term_bt_add);
        updateTermBt = (Button) view.findViewById(R.id.term_bt_update);
		backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		// 绑定事件
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
        
        backBt.setOnClickListener(this);
        //confirmBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        addTermBt.setOnClickListener(this);
        updateTermBt.setOnClickListener(this);
        termLv.setOnItemClickListener(this);
        searchEt.addTextChangedListener(this);
        view.setOnClickListener(null);
    }

    /**
     * 初始化界面数据
     */
    @Override
    public void onResume()
    {
        super.onResume();

        DbtLog.logUtils(TAG, "onResume");
        ConstValues.handler = handler;
        service = new TermListService(getActivity());
        
        // 根据后台配置的权限,设置新增终端是否显示
        String bfgl = PrefUtils.getString(getActivity(), "bfgl", "-1");
        if(bfgl.contains("1000001")){
        	addTermBt.setVisibility(View.VISIBLE);
        }else{
        	addTermBt.setVisibility(View.GONE);
        }

        // 获取参数
        MstRouteMStc lineStc = (MstRouteMStc) getArguments().get("lineStc");
        //		service.updateSequence(lineStc.getRoutekey());
        titleTv.setText(R.string.termlist_title);
        visitLine.setText(lineStc.getRoutename());
        Date prevDate = DateUtil.parse(lineStc.getPrevDate(), "yyyyMMddHHmmss");
        visitDate.setText(DateUtil.formatDate(prevDate, "yyyy-MM-dd"));
        if (CheckUtil.isBlankOrNull(lineStc.getPrevDate()))
        {
            visitDay.setText("0");
        }
        else if (lineStc.getPrevDate().length() >= 8)
        {

            int day = (int) DateUtil.diffDays(new Date(), DateUtil.parse(lineStc.getPrevDate().substring(0, 8), "yyyyMMdd"));
            visitDay.setText(String.valueOf(day));
        }
        
        updateTermBt.setBackgroundResource(R.drawable.bt_sequence_edit);
        updateTermBt.setText("修改");
        
        // 绑定TermList数据
        List<String> routes = new ArrayList<String>();
        routes.add(lineStc.getRoutekey());
        termLst = service.queryTerminal(routes);
        termPinyinMap = service.getTermPinyin(termLst);
        if(termStc!=null){
            for(MstTermListMStc term:termLst){
                if(termStc.getTerminalkey().equals(term.getTerminalkey())){
                    termStc=term;
                }
            }
        }
        this.searchTerm();
    }

    @Override
    public void onClick(View v)
    {

        Fragment fragment = null;

        switch (v.getId())
        {
        	case R.id.banner_navigation_rl_back:
            case R.id.banner_navigation_bt_back:
                getFragmentManager().popBackStack();
                DbtLog.logUtils(TAG, "返回");
                break;

            case R.id.banner_navigation_rl_confirm:
            //case R.id.banner_navigation_bt_confirm:
                //if (ViewUtil.isDoubleClick(v.getId(), 1000)) return;
                confirmVisit();

                break;

            // 查询终端
            case R.id.term_bt_search:
                DbtLog.logUtils(TAG, "查询终端");
                this.searchTerm();
                break;

            // 新增终端
            case R.id.term_bt_add:
                DbtLog.logUtils(TAG, "新增终端");
                fragment = new TermAddFragment();
                fragment.setArguments(getArguments());
                break;

            // 修改终端
            case R.id.term_bt_update:
                DbtLog.logUtils(TAG, "修改终端");
                adapter.setUpdate(true);
                String s = updateTermBt.getText().toString();
                if ("修改".equals(s))
                {
                    updateTermBt.setText("保存");
                    updateTermBt.setBackgroundResource(R.drawable.bt_sequence_fish);
                    adapter.setUpdate(true);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    updateTermSeq();
                    updateTermBt.setText("修改");
                    updateTermBt.setBackgroundResource(R.drawable.bt_sequence_edit);
                    adapter.setUpdate(false);
                    searchTerm();
                }
                break;

            default:
                break;
        }

        if (fragment != null)
        {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.shopvisit_term_container, fragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }



    /**
     * 查询
     * 
     * @return
     */
    public void searchTerm()
    {
        seqTermList = getNewMstTermListMStc();
        if(termStc==null || "3".equals(termStc.getStatus())){
            termId="";
        }else{
            termId=termStc.getTerminalkey();
            if(confirmBt.getTag()!=null){
                MstTermListMStc termStc2=(MstTermListMStc) confirmBt.getTag();
                if(termStc2.getTerminalkey().equals(termStc.getTerminalkey())){
                    confirmBt.setTag(termStc);
                }
            }
        }
        if (!CheckUtil.isBlankOrNull(searchEt.getText().toString()))
        {
            tempLst = service.searchTerm(getNewMstTermListMStc(), searchEt.getText().toString().replace(" ", ""), termPinyinMap);
        }
        else
        {
            tempLst = getNewMstTermListMStc();
        }
        adapter = new TermListAdapter(getActivity(), seqTermList, tempLst, confirmBt, termId);
        updateTermBt.setText("修改");
        termLv.setAdapter(adapter);
        List<String> termIdLst = FunUtil.getPropertyByName(tempLst, "terminalkey", String.class);
        if (termStc != null && termIdLst.contains(termId))
        {
            confirmBt.setVisibility(View.VISIBLE);
            confirmBt.setTag(termStc);
        }
        else
        {
            confirmBt.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 重复拜访二次确认
     */
    private void confirmVisit()
    {
        DbtLog.logUtils(TAG, "confirmVisit");
        termStc = (MstTermListMStc) confirmBt.getTag();
        if(termStc==null){
            return;
        }
        // 如果已结束拜访
        if (ConstValues.FLAG_1.equals(termStc.getUploadFlag()) || ConstValues.FLAG_1.equals(termStc.getSyncFlag()))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.termlist_confirm_title);
            builder.setMessage(R.string.termlist_confirm_msg);
            builder.setCancelable(false);
            // 重复拜访
            builder.setPositiveButton(R.string.termlist_confirm_sure, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    DbtLog.logUtils(TAG, "termkey:"+termStc.getTerminalkey()+" termname："+termStc.getTerminalname());
                    dialog.dismiss();
                    Intent intent = new Intent(getActivity(), TermIndexActivity.class);
                    intent.putExtra("isFirstVisit", "1");// 非第一次拜访1
                    intent.putExtra("termStc", termStc);
                    intent.putExtra("seeFlag", "0");
                    getActivity().startActivity(intent);
                }
            });
            // 取消重复拜访
            builder.setNegativeButton(R.string.termlist_confirm_cancel, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            // 查看
            builder.setNeutralButton(R.string.termlist_confirm_see, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    Intent intent = new Intent(getActivity(), TermIndexActivity.class);
                    intent.putExtra("isFirstVisit", "1");// 非第一次拜访1
                    intent.putExtra("termStc", termStc);
                    intent.putExtra("seeFlag", "1"); // 查看标识
                    getActivity().startActivity(intent);
                }
            });
            builder.create().show();

        }
        // 该终端当天第一次拜访
        else
        {
            Intent intent = new Intent(getActivity(), TermIndexActivity.class);
            intent.putExtra("isFirstVisit", "0");// 第一次拜访0
            intent.putExtra("termStc", termStc);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //        adapter.setSelectItem(position);
        //        adapter.notifyDataSetInvalidated();
        //        confirmBt.setVisibility(View.VISIBLE);
        //        confirmBt.setTag(tempLst.get(position));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    @Override
    public void afterTextChanged(Editable s)
    {
        this.searchTerm();
    }

    /***
     * 终端集合封装一个新的终端集合（防御联动修改）
     * @return
     */
    private List<MstTermListMStc> getNewMstTermListMStc()
    {
        List<MstTermListMStc> termList_new = new ArrayList<MstTermListMStc>();
        if(termLst!=null){
            for (MstTermListMStc item : termLst)
            {
                MstTermListMStc item_new = new MstTermListMStc();
                item_new.setRoutekey(item.getRoutekey());
                item_new.setTerminalkey(item.getTerminalkey());
                item_new.setTerminalcode(item.getTerminalcode());
                item_new.setTerminalname(item.getTerminalname());
                item_new.setStatus(item.getStatus());
                item_new.setSequence(item.getSequence());
                item_new.setMineFlag(item.getMineFlag());
                item_new.setVieFlag(item.getVieFlag());
                item_new.setMineProtocolFlag(item.getMineProtocolFlag());
                item_new.setVieProtocolFlag(item.getVieProtocolFlag());
                item_new.setSyncFlag(item.getSyncFlag());
                item_new.setTvolume(item.getTvolume());
                item_new.setTopnum(item.getTopnum());
                item_new.setUploadFlag(item.getUploadFlag());
                item_new.setMinorchannel(item.getMinorchannel());
                item_new.setTerminalType(item.getTerminalType());
                item_new.setVisitTime(item.getVisitTime());
                termList_new.add(item_new);
            }
        }
        return termList_new;
    }

    /***
     * 修改终端顺序
     */
    private void updateTermSeq()
    {
        termLst = seqTermList;
        List<TermSequence> termSequenceList = new ArrayList<TermSequence>();
        for (int i = 0; i < termLst.size(); i++)
        {
            MstTermListMStc term = termLst.get(i);
            if (!((i + 1) + "").equals(term.getSequence()))
            {
                term.setSequence((i + 1) + "");
            }
            TermSequence termSequence = new TermSequence();
            termSequence.setSequence(term.getSequence());
            termSequence.setTerminalkey(term.getTerminalkey());
            termSequenceList.add(termSequence);
        }
        service.updateTermSequence(termSequenceList);
    }

}
