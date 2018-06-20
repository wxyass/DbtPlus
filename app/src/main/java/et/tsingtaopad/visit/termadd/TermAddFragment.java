package et.tsingtaopad.visit.termadd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.initconstvalues.InitConstValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtUtils;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.visit.agencyvisit.AgencyvisitService;
import et.tsingtaopad.visit.shopvisit.line.domain.MstRouteMStc;
import et.tsingtaopad.visit.shopvisit.sayhi.domain.MstTerminalInfoMStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermAddActivity.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-28</br>      
 * 功能描述: 新增终端</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("HandlerLeak")
public class TermAddFragment extends BaseFragmentSupport implements OnClickListener, OnItemSelectedListener
{

    private TermAddService service;

    private Button backBt;
    private TextView titleTv;

    private EditText termNameEt;
    private Spinner belongLineSp;
    private Spinner levelSp;
    private Spinner provSp;
    private Spinner citySp;
    private Spinner countrySp;
    private TextView provTv;
    private TextView cityTv;
    private TextView countryTv;
    private EditText addressEt;
    private EditText linkmanEt;
    private EditText telEt;
    private EditText sequenceEt;
    private EditText cycleEt;
    private EditText hvolumeEt;
    private EditText mvolumeEt;
    private EditText pvolumeEt;
    private EditText lvolumeEt;
    private Spinner areaTypeSp;
    private Spinner sellChannelSp;
    private Spinner mainChannelSp;
    private Spinner minorChannelSp;
    private Button commitBt;

    private ProgressDialog saveTermPd;
    // 当前定格的主供货商
    private MstAgencyinfoM agencyM;
    MstTerminalinfoM term = null;
    
    private MstTerminalInfoMStc stc;// 终端表第一条数据
    
    // 新增终端名称正则过滤
 	final int mMaxLenth = 20;// 设置经销商名称允许输入的字符长度
    
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {

            Bundle bundle = msg.getData();

            super.handleMessage(msg);
            switch (msg.what)
            {

            // 从终端列表进来的保存成功提示
                case ConstValues.WAIT2:
                    Toast.makeText(getActivity(), R.string.termadd_msg_addsuccess, Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                    break;

                // 提示信息
                case ConstValues.WAIT3:
                    Toast.makeText(getActivity(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    requestFocus(bundle.getInt("msgId"));
                    getFragmentManager().popBackStack();
                    break;

                // 提示信息
                case ConstValues.WAIT4:
                    Toast.makeText(getActivity(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    requestFocus(bundle.getInt("msgId"));
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.visit_terminaladd, null);
        this.initView(view);
        this.initData();
        return view;
    }

    private void initView(View view)
    {

        // 初始化界面组件
        backBt = (Button) view.findViewById(R.id.banner_navigation_bt_back);
        titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
        termNameEt = (EditText) view.findViewById(R.id.termAdd_et_termName);
        belongLineSp = (Spinner) view.findViewById(R.id.termAdd_sp_belongLine);
        levelSp = (Spinner) view.findViewById(R.id.termAdd_sp_termType);
        provSp = (Spinner) view.findViewById(R.id.termAdd_sp_prov);
        citySp = (Spinner) view.findViewById(R.id.termAdd_sp_city);
        countrySp = (Spinner) view.findViewById(R.id.termAdd_sp_country);
        
        provTv = (TextView) view.findViewById(R.id.termAdd_tv_prov);
        cityTv = (TextView) view.findViewById(R.id.termAdd_tv_city);
        countryTv = (TextView) view.findViewById(R.id.termAdd_tv_country);
        
        addressEt = (EditText) view.findViewById(R.id.termAdd_et_address);
        linkmanEt = (EditText) view.findViewById(R.id.termAdd_et_contactPerson);
        telEt = (EditText) view.findViewById(R.id.termAdd_et_tel);
        sequenceEt = (EditText) view.findViewById(R.id.termAdd_et_sequence);
        cycleEt = (EditText) view.findViewById(R.id.termAdd_et_cycle);
        hvolumeEt = (EditText) view.findViewById(R.id.termAdd_et_hvolume);
        mvolumeEt = (EditText) view.findViewById(R.id.termAdd_et_mvolume);
        pvolumeEt = (EditText) view.findViewById(R.id.termAdd_et_pvolume);
        lvolumeEt = (EditText) view.findViewById(R.id.termAdd_et_lvolume);
        areaTypeSp = (Spinner) view.findViewById(R.id.termAdd_sp_termArea);
        sellChannelSp = (Spinner) view.findViewById(R.id.termAdd_sp_sellChannel);
        mainChannelSp = (Spinner) view.findViewById(R.id.termAdd_sp_mainChannel);
        minorChannelSp = (Spinner) view.findViewById(R.id.termAdd_sp_minorChannel);
        commitBt = (Button) view.findViewById(R.id.termAdd_bt_save);
        //初始化ProgressDialog
        initProgressDialog();
        RelativeLayout backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);

        // 绑定事件
        backBt.setOnClickListener(this);
        belongLineSp.setOnItemSelectedListener(this);
        areaTypeSp.setOnItemSelectedListener(this);
        levelSp.setOnItemSelectedListener(this);
        provSp.setOnItemSelectedListener(this);
        citySp.setOnItemSelectedListener(this);
        countrySp.setOnItemSelectedListener(this);
        sellChannelSp.setOnItemSelectedListener(this);
        mainChannelSp.setOnItemSelectedListener(this);
        minorChannelSp.setOnItemSelectedListener(this);
        commitBt.setOnClickListener(this);
        view.setOnClickListener(null);
    }

    /**
     * 初始化ProgressDialog
     */
    private void initProgressDialog()
    {
        saveTermPd = new ProgressDialog(getActivity());
        saveTermPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        saveTermPd.setMessage("正在保存终端....");
        saveTermPd.setCancelable(false);
    }

    // TODO hongen 添加ConstValues的非空判断
    private void initData()
    {

        service = new TermAddService(getActivity(), handler, saveTermPd);

        titleTv.setText(R.string.termadd_title);

        // 获取当前定格的主供货商
        AgencyvisitService agencyService = new AgencyvisitService(getActivity());
        agencyM = agencyService.queryMainAgencyForGrid();

        // 获取线路参数
        MstRouteMStc lineStc = new MstRouteMStc();
        
        // 设置省市县(取终端表的第一条记录的省市县 作为新增终端的省市县)
 		stc = new MstTerminalInfoMStc();
 		stc = service.getFirstTermnalData();
 		provTv.setText(stc.getProvName());
 		cityTv.setText(stc.getCityName());
 		//countryTv.setText(stc.getCountryName());

        Bundle bundle = getArguments();
        if (bundle != null)
        {
            if (bundle.get("lineStc") != null)
            {
                lineStc = (MstRouteMStc) bundle.get("lineStc");
            }
            if (bundle.get("term") != null)
            {
                term = (MstTerminalinfoM) bundle.get("term");
            }
        }
        if (term == null)
        {
            add(lineStc);
        }
        else
        {
            update(term);
        }

        commitBt.setEnabled(true);
        
        // 联系人 过滤
        linkmanEt.addTextChangedListener(new TextWatcher() {
			private int cou = 0;
			int selectionEnd = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				cou = before + count;
				String editable = linkmanEt.getText().toString();
				String str = CheckUtil.stringFilter(editable); // 过滤特殊字符
				if (!editable.equals(str)) {
					Toast.makeText(getActivity(), "不能输入特殊字符", Toast.LENGTH_SHORT).show();
					linkmanEt.setText(str);
				}
				linkmanEt.setSelection(linkmanEt.length());
				cou = linkmanEt.length();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (cou > mMaxLenth) {
					selectionEnd = linkmanEt.getSelectionEnd();
					s.delete(mMaxLenth, selectionEnd);
				}
			}
		});
        
        // 终端名称 过滤
		termNameEt.addTextChangedListener(new TextWatcher() {
			private int cou = 0;
			int selectionEnd = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				cou = before + count;
				String editable = termNameEt.getText().toString();
				String str = CheckUtil.stringFilter(editable); // 过滤特殊字符
				if (!editable.equals(str)) {
					Toast.makeText(getActivity(), "不能输入特殊字符", Toast.LENGTH_SHORT).show();
					termNameEt.setText(str);
				}
				termNameEt.setSelection(termNameEt.length());
				cou = termNameEt.length();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (cou > mMaxLenth) {
					selectionEnd = termNameEt.getSelectionEnd();
					s.delete(mMaxLenth, selectionEnd);
				}
			}
		});
		
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

            case R.id.termAdd_bt_save:
                this.saveTerm();
                break;

            default:
                break;
        }
    }

    private void saveTerm()
    {

        MstTerminalinfoM termInfo = new MstTerminalinfoM();
        termInfo.setTerminalkey(termNameEt.getTag().toString());
        termInfo.setTerminalcode("temp");
        termInfo.setTerminalname(termNameEt.getText().toString());
        termInfo.setRoutekey(belongLineSp.getTag().toString());
        
        termInfo.setProvince(stc.getProvince());
        //termInfo.setCity(stc.getCity());
        termInfo.setCity(citySp.getTag().toString());
        //termInfo.setCounty(stc.getCounty());
/*        termInfo.setProvince(provSp.getTag().toString());
        termInfo.setCity(citySp.getTag().toString());*/ 
        termInfo.setCounty(countrySp.getTag().toString());
       //        KvStc cityKvStc = (KvStc)citySp.getSelectedItem();
        //        if ("全省".equals(cityKvStc.getValue())) {
        //            termInfo.setCounty("");
        //        } else {
        //            termInfo.setCounty(countrySp.getTag().toString());
        //        }
        termInfo.setAddress(addressEt.getText().toString());
        termInfo.setContact(linkmanEt.getText().toString());
        termInfo.setMobile(telEt.getText().toString());
        termInfo.setTlevel(levelSp.getTag().toString());
        termInfo.setSequence(FunUtil.isNullSetSpace(sequenceEt.getText()).toString());
        termInfo.setCycle(cycleEt.getText().toString());
        termInfo.setHvolume(FunUtil.isNullToZero(hvolumeEt.getText()).toString());
        termInfo.setMvolume(FunUtil.isNullToZero(mvolumeEt.getText()).toString());
        termInfo.setPvolume(FunUtil.isNullToZero(pvolumeEt.getText()).toString());
        termInfo.setLvolume(FunUtil.isNullToZero(lvolumeEt.getText()).toString());
        termInfo.setStatus(ConstValues.FLAG_0);
        termInfo.setSellchannel(sellChannelSp.getTag().toString());
        termInfo.setMainchannel(mainChannelSp.getTag().toString());
        termInfo.setMinorchannel(minorChannelSp.getTag().toString());
        termInfo.setAreatype(areaTypeSp.getTag().toString());
        termInfo.setCredate(DateUtil.getDateTimeDte(1));
        termInfo.setUpdatetime(DateUtil.getDateTimeDte(1));
        service.addTerm(termInfo);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        Spinner sp = (Spinner) parent;
        if (view != null)
        {
            sp.setTag(((TextView) view).getHint());
        }
        else
        {
            sp.setTag("");
        }
        switch (sp.getId())
        {
            case R.id.termAdd_sp_prov:
                KvStc prov = (KvStc) provSp.getSelectedItem();
                String defaultCity = agencyM.getCity();
                if (term != null)
                {
                    defaultCity = term.getCity();
                    
                }
                //SpinnerKeyValueAdapter cityAdapter = new SpinnerKeyValueAdapter(getActivity(), prov.getChildLst(), new String[] { "key", "value" }, defaultCity);
                SpinnerKeyValueAdapter cityAdapter = new SpinnerKeyValueAdapter(getActivity(), prov.getChildLst(), new String[] { "key", "value" }, stc.getCity());
                citySp.setAdapter(cityAdapter);
                break;

            case R.id.termAdd_sp_city:
                KvStc city = (KvStc) citySp.getSelectedItem();
                String defaultCountry = null;
                if (term != null)
                {
                    defaultCountry = term.getCounty();
                }
                SpinnerKeyValueAdapter countryAdapter = new SpinnerKeyValueAdapter(getActivity(), city.getChildLst(), new String[] { "key", "value" }, defaultCountry);
                countrySp.setAdapter(countryAdapter);
                break;

            case R.id.termAdd_sp_sellChannel:
                KvStc sell = (KvStc) sellChannelSp.getSelectedItem();
                String defaultMainChannel = null;
                if (term != null)
                {
                    defaultMainChannel = term.getMainchannel();
                }
                SpinnerKeyValueAdapter mainsellAdapter = new SpinnerKeyValueAdapter(getActivity(), getSellChannelList(sell.getChildLst()), new String[] { "key", "value" }, defaultMainChannel);
                mainChannelSp.setAdapter(mainsellAdapter);
                break;

            case R.id.termAdd_sp_mainChannel:
                KvStc mainSell = (KvStc) mainChannelSp.getSelectedItem();
                String minorChannel = null;
                if (term != null)
                {
                    minorChannel = term.getMinorchannel();
                }
                SpinnerKeyValueAdapter minorSellAdapter = new SpinnerKeyValueAdapter(getActivity(), getSellChannelList(mainSell.getChildLst()), new String[] { "key", "value" }, minorChannel);
                minorChannelSp.setAdapter(minorSellAdapter);
                break;

            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {

    }

    /**
     * 由错误提示定位焦点
     * 
     * @param msgId
     */
    private void requestFocus(int msgId)
    {
        switch (msgId)
        {
            case R.string.termadd_msg_invaltermname:
                termNameEt.requestFocus();
                break;

            case R.string.termadd_msg_invaladdress:
                addressEt.requestFocus();
                break;

            case R.string.termadd_msg_invalcontact:
                linkmanEt.requestFocus();
                break;

            case R.string.termadd_msg_invalmobile:
                telEt.requestFocus();
                break;

            case R.string.termadd_msg_invalsequence:
                sequenceEt.requestFocus();
                break;

            default:
                break;
        }
    }

    private void add(MstRouteMStc lineStc)
    {

        // 生成新增终端的主键
        termNameEt.setTag(FunUtil.getUUID());
        hvolumeEt.setHint("0");
        mvolumeEt.setHint("0");
        pvolumeEt.setHint("0");
        lvolumeEt.setHint("0");

        // 所属线路
        SpinnerKeyValueAdapter adapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.lineLst, new String[] { "routekey", "routename" }, lineStc.getRoutekey());
        belongLineSp.setAdapter(adapter);

        // 区域类型
        SpinnerKeyValueAdapter termAreaAdapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.dataDicMap.get("areaTypeLst2"), new String[] { "key", "value" }, null);
        areaTypeSp.setAdapter(termAreaAdapter);

        // 终端等级
        SpinnerKeyValueAdapter termTypeAdapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.dataDicMap.get("levelLst"), new String[] { "key", "value" }, null);
        levelSp.setAdapter(termTypeAdapter);

        // 销售渠道
        // 添加请选择选项,因为在点击首页时会将"请选择"删除,所以在此处添加 就是在重新走一遍初始化 20160314
        /*List<KvStc> dataLst = getSellChannelList(ConstValues.dataDicMap.get("sellChannelLst"));
        if (!CheckUtil.IsEmpty(dataLst)) {
            KvStc kvStc0 = (KvStc) dataLst.get(0);// 判断第一个是否是不是 请选择 并把它移除
            if ("-1".equals(kvStc0.getKey())
                    || "请选择".equals(kvStc0.getValue())) {
                
            }else{
            	dataLst.add(0, new KvStc("-1", "请选择", "-1"));
            }
        }*/
        InitConstValues initConstValues = new InitConstValues(getActivity());
        initConstValues.initDataDictionary();
        List<KvStc> dataLst = ConstValues.dataDicMap.get("sellChannelLst");
        Iterator<KvStc>iterator = dataLst.iterator();
        if (!CheckUtil.IsEmpty(dataLst)) {
        	//89F32F77BDAD414E849966B95E5D3055// 待定
        	while(iterator.hasNext()) {  
       		KvStc it=iterator.next();
        	   if(it.getKey().equals("89F32F77BDAD414E849966B95E5D3055")) 
        	  
        	  iterator.remove();
        	   
        	}  }
        
        //SpinnerKeyValueAdapter sellChannelAdapter = new SpinnerKeyValueAdapter(getActivity(), getSellChannelList(ConstValues.dataDicMap.get("sellChannelLst")), new String[] { "key", "value" }, null);
        SpinnerKeyValueAdapter sellChannelAdapter = new SpinnerKeyValueAdapter(getActivity(), dataLst, new String[] { "key", "value" }, null);
        sellChannelSp.setAdapter(sellChannelAdapter);

        // 获取省市县数据
        //SpinnerKeyValueAdapter provAdapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.provLst, new String[] { "key", "value" }, agencyM.getProvince());
        SpinnerKeyValueAdapter provAdapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.provLst, new String[] { "key", "value" }, stc.getProvince());
        provSp.setAdapter(provAdapter);
        
    }

    private void update(MstTerminalinfoM term)
    {
        // 所属线路
        SpinnerKeyValueAdapter adapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.lineLst, new String[] { "routekey", "routename" }, term.getRoutekey());
        belongLineSp.setAdapter(adapter);

        // 区域类型
        SpinnerKeyValueAdapter termAreaAdapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.dataDicMap.get("areaTypeLst"), new String[] { "key", "value" }, term.getAreatype());
        areaTypeSp.setAdapter(termAreaAdapter);

        // 终端等级
        SpinnerKeyValueAdapter termTypeAdapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.dataDicMap.get("levelLst"), new String[] { "key", "value" }, term.getTlevel());
        levelSp.setAdapter(termTypeAdapter);

        // 销售渠道
        SpinnerKeyValueAdapter sellChannelAdapter = new SpinnerKeyValueAdapter(getActivity(), getSellChannelList(ConstValues.dataDicMap.get("sellChannelLst")), new String[] { "key", "value" }, term.getSellchannel());
        sellChannelSp.setAdapter(sellChannelAdapter);

        // 获取省市县数据
        SpinnerKeyValueAdapter provAdapter = new SpinnerKeyValueAdapter(getActivity(), ConstValues.provLst, new String[] { "key", "value" }, term.getProvince());
        provSp.setAdapter(provAdapter);
        
        

        termNameEt.setTag(term.getTerminalkey());
        termNameEt.setText(term.getTerminalname());
        addressEt.setText(term.getAddress());
        linkmanEt.setText(term.getContact());
        telEt.setText(term.getMobile());
        cycleEt.setText(term.getCycle());
        if ("0".equals(term.getHvolume()))
        {
            hvolumeEt.setHint("0");
        }
        else
        {
            hvolumeEt.setText(term.getHvolume());
        }
        if ("0".equals(term.getMvolume()))
        {
            mvolumeEt.setHint("0");
        }
        else
        {
            mvolumeEt.setText(term.getMvolume());
        }
        if ("0".equals(term.getPvolume()))
        {
            pvolumeEt.setHint("0");
        }
        else
        {
            pvolumeEt.setText(term.getPvolume());
        }
        if ("0".equals(term.getLvolume()))
        {
            lvolumeEt.setHint("0");
        }
        else
        {
            lvolumeEt.setText(term.getLvolume());
        }
    }

    /***
     * 获取渠道集合
     * @param kvStcList
     * @return
     */
    private List<KvStc> getSellChannelList(List<KvStc> kvStcList)
    {
        List<KvStc> list = new ArrayList<KvStc>();
        for (KvStc kvStc : kvStcList)
        {
            if (!DbtUtils.getInvalidChannelList().contains(kvStc.getKey()))
            {
                list.add(kvStc);
            }
        }
        return list;
    }
}
