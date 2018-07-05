package et.tsingtaopad.operation.distirbution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.adapter.MultipleKeyValueAdapter;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.db.tables.MstPowerfulchannelInfo;
import et.tsingtaopad.db.tables.MstPowerfulterminalInfo;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DistirbutionActivity.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2013年12月18日</br>      
 * 功能描述: 万能铺货率</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("NewApi")
public class DistirbutionActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener
{
    String TAG = "DistirbutionActivity";
    private DistirbutionService service;

    private AlertDialog dialog;
    private Button backBt;
    private TextView titleTv;

    private Spinner typeSp;
    private Spinner lineSp;
    private Button searchDateBt;
    private Button sellChannelSp;
    private Button mainChannelSp;
    private Button minorChannelSp;
    private Button productSp;
    private Button levelSp;
    private Button searchBt;

    private LinearLayout channelLo;
    private LinearLayout levelLo;
    private LinearLayout channelTitleLo;
    private LinearLayout terminalTitleLo;
    private ListView resultLv;

    private List<KvStc> channelLst;
    private List<KvStc> childKvStcList;
    private List<String> selectedList;

    private String sell;
    private String main;
    private String minor;
    private String levelId;
    private String proId;
    private String searchDate;
    private String lineId;
    private String typeSelected;

    private Bundle dataBundle;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            dataBundle = null;
            switch (msg.what)
            {

            // 提示信息
                case ConstValues.WAIT1:
                    Toast.makeText(getBaseContext(), bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;

                case ConstValues.WAIT2:
                    DbtLog.logUtils(TAG, "WAIT2_1");
                    dataBundle = bundle;
                    DbtLog.logUtils(TAG, "WAIT2_2");
                    dealDateShow(bundle);
                    DbtLog.logUtils(TAG, "WAIT2_3");
                    if (dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                    DbtLog.logUtils(TAG, "WAIT2_4");
                    break;
                case ConstValues.WAIT3:
                    mainChannelSp.setTag(null);
                   mainChannelSp.setText(null);
                    minorChannelSp.setTag(null);
                    minorChannelSp.setText(null);
                    break;
                case ConstValues.WAIT4:
                    minorChannelSp.setTag(null);
                  minorChannelSp.setText(null);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        DbtLog.logUtils(TAG, "onCreate" + (arg0 == null));
        initView();
        initData();
       /* if (arg0 != null)
        {
            dataBundle = arg0.getBundle("bundle");
            typeSelected = arg0.getString("typeSelected");
            lineId = arg0.getString("lineId");
            searchDate = arg0.getString("searchDate");
            proId = arg0.getString("proId");
            levelId = arg0.getString("levelId");
            sell = arg0.getString("sell");
            main = arg0.getString("main");
            minor = arg0.getString("minor");
            if (dataBundle != null)
            {
                search(false);
            }
        }*/
    }

    private void initView()
    {

        DbtLog.logUtils(TAG, "initView");
        setContentView(R.layout.powerfuldistirbution);
        dialog = DialogUtil.progressDialog(this, R.string.dialog_msg_search);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
        titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);

        typeSp = (Spinner) findViewById(R.id.distirbution_sp_type);
        lineSp = (Spinner) findViewById(R.id.distirbution_sp_line);
        searchDateBt = (Button) findViewById(R.id.distirbution_bt_time);
        sellChannelSp = (Button) findViewById(R.id.distirbution_sp_sellchannel);
        mainChannelSp = (Button) findViewById(R.id.distirbution_sp_mainchannel);
        minorChannelSp = (Button) findViewById(R.id.distirbution_sp_minorchannel);
        productSp = (Button) findViewById(R.id.distirbution_sp_product);
        levelSp = (Button) findViewById(R.id.distirbution_sp_level);
        searchBt = (Button) findViewById(R.id.distirbution_bt_query);

        channelLo = (LinearLayout) findViewById(R.id.distirbution_lo_channel);
        levelLo = (LinearLayout) findViewById(R.id.distirbution_lo_level);
        channelTitleLo = (LinearLayout) findViewById(R.id.distirbution_lo_title_channel);
        terminalTitleLo = (LinearLayout) findViewById(R.id.distirbution_lo_title_terminal);
        resultLv = (ListView) findViewById(R.id.distirbution_lv_result);

        //创建ListView底部view
        View footview = LayoutInflater.from(this).inflate(R.layout.visit_agencyvisit_footview, null);
        resultLv.addFooterView(footview);

		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
        backBt.setOnClickListener(this);
        typeSp.setOnItemSelectedListener(this);
        lineSp.setOnItemSelectedListener(this);
        searchDateBt.setOnClickListener(this);
        sellChannelSp.setOnClickListener(this);
        mainChannelSp.setOnClickListener(this);
        minorChannelSp.setOnClickListener(this);
        productSp.setOnClickListener(this);
        levelSp.setOnClickListener(this);
        searchBt.setOnClickListener(this);
    }

    private void initData()
    {
        DbtLog.logUtils(TAG, "initData");
        childKvStcList = new ArrayList<KvStc>();
        service = new DistirbutionService(this, handler);

        titleTv.setText(R.string.distirbution_title);
        searchDateBt.setText(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));

        // 报表样式
        String[] fieldName = new String[] { "key", "value" };
        List<KvStc> typeLst = new ArrayList<KvStc>();
        typeLst.add(new KvStc("0", getString(R.string.distirbution_terminal_type), ""));
        typeLst.add(new KvStc("1", getString(R.string.distirbution_channel_type), ""));
        typeSp.setAdapter(new SpinnerKeyValueAdapter(this, typeLst, fieldName, null));

        // 线路
        List<MstRouteM> lineLst = new ArrayList<MstRouteM>(ConstValues.lineLst);
        if (CheckUtil.IsEmpty(lineLst))
        {
            lineLst = new ArrayList<MstRouteM>();
        }
        else
        {
            lineLst.remove(0);
        }
        MstRouteM mstRouteM1 = new MstRouteM();
        mstRouteM1.setRoutekey("1");
        mstRouteM1.setRoutename(getString(R.string.distirbution_all));
        lineLst.add(0, mstRouteM1);
        MstRouteM mstRouteM2 = new MstRouteM();
        mstRouteM2.setRoutekey("0");
        mstRouteM2.setRoutename(getString(R.string.distirbution_composite));
        lineLst.add(0, mstRouteM2);
        lineSp.setAdapter(new SpinnerKeyValueAdapter(this, lineLst, new String[] { "routekey", "routename" }, null));
        // 销售渠道
        channelLst = new ArrayList<KvStc>(ConstValues.dataDicMap.get("sellChannelLst"));
        String json = (String)JSON.toJSONString(channelLst);
        handleChannel2(channelLst);//不加次方法   渠道类型的就是请选择和待定，加了变成汇总和全部
        List<KvStc> mainchannelLst = new ArrayList<KvStc>();
        mainchannelLst.add(new KvStc("0", getString(R.string.distirbution_composite), "0"));

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        	case R.id.banner_navigation_rl_back:
            case R.id.banner_navigation_bt_back:
                finish();
                break;

            case R.id.distirbution_bt_time:
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(this,R.style.dialog_date, new OnDateSetListener()
                {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        String month = String.valueOf(monthOfYear + 1);
                        String day = String.valueOf(dayOfMonth);
                        if (month.length() == 1)
                        {
                            month = "0" + month;
                        }
                        if (day.length() == 1)
                        {
                            day = "0" + day;
                        }

                        searchDateBt.setText(year + "-" + month + "-" + day);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.distirbution_bt_query:
                this.search(true);
                break;
            //产品选择	
            case R.id.distirbution_sp_product:
                // 产品列表
                List<KvStc> productLst = service.queryProductLst();
                DialogUtil.showMultipleDialog(this, productSp, R.string.indexstatus_dialogpro_title, productLst, new String[] { "key", "value" }, null, R.string.indexstatus_bt_prolst);
                break;
            //渠道类型选择
            case R.id.distirbution_sp_sellchannel:
                showMultipleDialog(this, sellChannelSp, R.string.distirbution_dialogpro_title_sell, channelLst, new String[] { "key", "value" }, null, R.string.distirbution_prompt_sell);
                break;
            //主渠道选择
            case R.id.distirbution_sp_mainchannel:
                String sellChannel = (String) this.sellChannelSp.getText();
                String[] selectChannelArrayString = sellChannel.split(",");
                childKvStcList = new ArrayList<KvStc>();
                handleChannel(childKvStcList);
                for (String aselectChannel : selectChannelArrayString)
                {
                    for (KvStc asellChannel : channelLst)
                    {
                        if (aselectChannel.equalsIgnoreCase(asellChannel.getValue()))
                        {
                            List<KvStc> ChildLst = new ArrayList<KvStc>(asellChannel.getChildLst());
                            if (!CheckUtil.IsEmpty(ChildLst))
                            {
                                //把请选择移除掉
                                ChildLst.remove(0);
                            }
                            childKvStcList.addAll(ChildLst);
                        }
                    }
                }
                showMultipleDialog(this, mainChannelSp, R.string.distirbution_dialogpro_title_main, childKvStcList, new String[] { "key", "value" }, null, R.string.distirbution_prompt_main);
                break;
            //次渠道选择
            case R.id.distirbution_sp_minorchannel:
                String mainchannel = (String) this.mainChannelSp.getText();
                String[] selectminorchanner = mainchannel.split(",");//用,分隔主键
                List<KvStc> kvStcslList = new ArrayList<KvStc>();
                handleChannel(kvStcslList);
                for (String selectmainchanner : selectminorchanner)
                {//得到用来展示次渠道的父主键，主渠道键
                    for (KvStc selectmain : childKvStcList)
                    {//在渠道类型里面找对应主键的下面的子级及子子级
                        if (selectmainchanner.equalsIgnoreCase(selectmain.getValue()))
                        {
                            List<KvStc> ChildLst = new ArrayList<KvStc>(selectmain.getChildLst());
                            if (!CheckUtil.IsEmpty(ChildLst))
                            {
                                //把请选择移除掉
                                ChildLst.remove(0);
                            }
                            kvStcslList.addAll(ChildLst);
                        }

                    }
                }
                showMultipleDialog(this, minorChannelSp, R.string.distirbution_dialogpro_title_minor, kvStcslList, new String[] { "key", "value" }, null, R.string.distirbution_prompt_minor);
                break;
            case R.id.distirbution_sp_level:
                List<KvStc> leveLst = new ArrayList<KvStc>(ConstValues.dataDicMap.get("levelLst"));
                if (!CheckUtil.IsEmpty(leveLst))
                {
                    //移除掉（请选择）
                    leveLst.remove(0);
                }
                showMultipleDialog(this, levelSp, R.string.distirbution_dialogpro_title_level, leveLst, new String[] { "key", "value" }, null, R.string.distirbution_prompt_level);
                break;
            case R.id.common_multiple_lv:

                break;
            default:
                break;
        }
    }

    /***
     * 查询数据
     * @param isClick 是否点击触发
     */
    private void search(boolean isClick)
    {
        DbtLog.logUtils(TAG, "search");
        if (isClick)
        {
            //报表样式（渠道类型，终端类型）
            typeSelected = String.valueOf(typeSp.getSelectedItemPosition());
            //线路
            lineId = ((MstRouteM) lineSp.getSelectedItem()).getRoutekey();
            //选择时间
            searchDate = searchDateBt.getText().toString().replace("-", "");
            //产品
            proId = FunUtil.isBlankOrNullTo(productSp.getTag(), "");
            //终端等级
            levelId = FunUtil.isBlankOrNullTo(levelSp.getTag(), "");
            //销售渠道       
            sell = FunUtil.isBlankOrNullTo(sellChannelSp.getTag(), "");
            //主渠道
            main = FunUtil.isBlankOrNullTo(mainChannelSp.getTag(), "");
            //次渠道
            minor = FunUtil.isBlankOrNullTo(minorChannelSp.getTag(), "");
        }

        service.search(typeSelected, lineId, sell, main, minor, levelId, proId, searchDate, dialog);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int id, long position)
    {
        switch (parent.getId())
        {

        // 报表样式切换
            case R.id.distirbution_sp_type:
                resultLv.setVisibility(View.GONE);
                if (position == 0)
                {
                    channelLo.setVisibility(View.GONE);
                    levelLo.setVisibility(View.VISIBLE);
                    channelTitleLo.setVisibility(View.GONE);
                    terminalTitleLo.setVisibility(View.VISIBLE);
                }
                else if (position == 1)
                {
                    channelLo.setVisibility(View.VISIBLE);
                    levelLo.setVisibility(View.GONE);
                    channelTitleLo.setVisibility(View.VISIBLE);
                    terminalTitleLo.setVisibility(View.GONE);
                }
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
     * 多选的对话框：（分两部分：汇总和特定值）达到点击汇总的时候，下面的不能选。下面选择的时候汇总不能选
     * @param context
     * @param tiggerBt
     * @param titleId
     * @param dataLst
     * @param fieldName
     * @param selectedId
     * @param defaultTextId
     */
    public void showMultipleDialog(final Activity context, final Button tiggerBt, int titleId, final List dataLst, final String[] fieldName, List<String> selectedId, final int defaultTextId)
    {
        // 让触发者不可用，防重复单击
        tiggerBt.setEnabled(false);
        // 加载视图
        View formView = LayoutInflater.from(context).inflate(R.layout.distirbution_multiple_dialog, null);
        final ListView dataLv = (ListView) formView.findViewById(R.id.common_multiple_lv);
        final Button submitBt = (Button) formView.findViewById(R.id.common_multiple_bt_submit);
        final Button cancelBt = (Button) formView.findViewById(R.id.common_multiple_bt_cancel);
        TextView titleTv = (TextView) formView.findViewById(R.id.common_multiple_tv_title);
        final View headerView = LayoutInflater.from(this).inflate(R.layout.distirbution_dialog_footview, null);
        //头部汇总视图
        final CheckBox distirbutionCheckBox = (CheckBox) headerView.findViewById(R.id.distirbutionCheckBox);

        dataLv.addHeaderView(headerView);
        titleTv.setText(context.getString(titleId, ""));

        headerView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                distirbutionCheckBox.toggle();
                if (distirbutionCheckBox.isChecked())
                {
                    //当汇总选中的时候listView不可选
                    dataLv.setEnabled(false);
                    tiggerBt.setTag(0);
                }
                else
                {
                    tiggerBt.setTag("");
                    dataLv.setEnabled(true);
                }
            }
        });
        distirbutionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // TODO Auto-generated method stub
                if (isChecked)
                {
                    dataLv.setEnabled(false);
                }
                else
                {
                    dataLv.setEnabled(true);
                    tiggerBt.setTag("");
                }
            }
        });

        // 绑定列表数据
        if (tiggerBt.getTag() != null && !tiggerBt.getTag().equals(""))
        {
            if (tiggerBt.getText().equals(getString(R.string.distirbution_composite)))
            {
                distirbutionCheckBox.setChecked(true);
                dataLv.setEnabled(false);
            }
            else
            {
                headerView.setEnabled(false);
                distirbutionCheckBox.setEnabled(false);
                selectedId = Arrays.asList(tiggerBt.getTag().toString().replace("||", ",").replace("|", "").split(","));
                selectedList = new ArrayList<String>(selectedId);
            }
        }
        else
        {
            selectedList = new ArrayList<String>();
            tiggerBt.setTag("");
        }
        if (CheckUtil.IsEmpty(selectedId))
        {
            cancelBt.setTag(FunUtil.isBlankOrNullTo(tiggerBt.getTag(), ""));
        }
        else
        {
            StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < selectedId.size(); j++)
            {
                buffer.append("|").append(selectedId.get(j)).append("|");
            }

            tiggerBt.setTag(buffer.toString());
            cancelBt.setTag(buffer.toString().replace("||", ",").replace("|", ""));
        }
        MultipleKeyValueAdapter adapter = new MultipleKeyValueAdapter(context, dataLst, fieldName, selectedId);
        dataLv.setAdapter(adapter);
        dataLv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                CheckBox itemCB = (CheckBox) arg1.findViewById(R.id.common_multiple_cb_lvitem);
                TextView itemTv = (TextView) arg1.findViewById(R.id.common_multiple_tv_lvitem);
                itemCB.toggle();
                String idStr = "|" + FunUtil.isBlankOrNullTo(itemTv.getHint(), " ") + "|";
                if (itemCB.isChecked())
                {
                    tiggerBt.setTag(tiggerBt.getTag().toString() + idStr);
                    selectedList.add((String) itemTv.getHint());
                    headerView.setEnabled(false);
                    distirbutionCheckBox.setEnabled(false);
                }
                else
                {
                    selectedList.remove(itemTv.getHint());
                    if (selectedList.size() == 0)
                    {
                        dataLv.setEnabled(true);
                        headerView.setEnabled(true);
                        distirbutionCheckBox.setEnabled(true);
                    }
                    else
                    {
                        dataLv.setEnabled(true);
                        headerView.setEnabled(false);
                        distirbutionCheckBox.setEnabled(false);
                    }
                    tiggerBt.setTag(tiggerBt.getTag().toString().replace(idStr, ""));
                }
            }
        });

        // 显示对话框
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setView(formView, 0, 0, 0, 0);
        dialog.setCancelable(false);
        dialog.show();

        // 确定
        submitBt.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if (distirbutionCheckBox.isChecked())
                {
                    // 选择汇总
                    tiggerBt.setText(R.string.distirbution_composite);
                    tiggerBt.setTag("0");
                }
                else
                {//2EA4CFE7E48746BC9E9079E5990D222E  04F569BFB355A4FDF8B740A9B4563D4E3
                    // 选择其他12FCD77B1B63441793942F011F63D44C, 4F569BFB355A4FDF8B740A9B4563D4E3, CF40B8D9444F4DAB9F268C4B6C77F8B3, 2EA4CFE7E48746BC9E9079E5990D222E
                    if (!CheckUtil.IsEmpty(dataLst))
                    {
                        String[] checkIds = tiggerBt.getTag().toString().replace("||", ",").replace("|", "").split(",");
                        List<String> idLst = FunUtil.getPropertyByName(dataLst, fieldName[0], String.class);
                        List<String> nameLst = FunUtil.getPropertyByName(dataLst, fieldName[1], String.class);
                        StringBuffer keyBuffer = new StringBuffer();
                        StringBuffer nameBuffer = new StringBuffer();
                        int total = 0;
                        for (int i = 0; i < idLst.size(); i++)
                        {
                            for (String id : checkIds)
                            {
                                if (id.equals(idLst.get(i)))
                                {
                                    total = total + 1;
                                    keyBuffer.append(idLst.get(i)).append(",");
                                    nameBuffer.append(nameLst.get(i)).append(",");
                                    continue;
                                }
                            }

                            if (total == checkIds.length)
                            {
                                break;
                            }
                        }
                        if (keyBuffer.length() > 1 && nameBuffer.length() > 1)
                        {
                            tiggerBt.setText(nameBuffer.deleteCharAt(nameBuffer.length() - 1));
                            tiggerBt.setTag(keyBuffer.deleteCharAt(keyBuffer.length() - 1));
                        }
                        else
                        {
                            tiggerBt.setText(context.getString(defaultTextId, ""));
                            tiggerBt.setTag("");
                        }
                    }
                    else
                    {
                        tiggerBt.setText(context.getString(defaultTextId, ""));
                        tiggerBt.setTag("");
                    }
                }
                tiggerBt.setEnabled(true);
                Message message = new Message();
                if (tiggerBt.getId() == sellChannelSp.getId())
                {
                    // 点击的是销售渠道
                    message.what = ConstValues.WAIT3;
                }
                else if (tiggerBt.getId() == mainChannelSp.getId())
                {
                    message.what = ConstValues.WAIT4;
                }
                handler.sendMessage(message);
                dialog.dismiss();
            }
        });

        // 取消
        cancelBt.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                tiggerBt.setEnabled(true);
                tiggerBt.setTag(cancelBt.getTag());
                dialog.dismiss();
            }
        });
    }

    /**
     * 处理查询数据的显示 
     * 
     */
    private void dealDateShow(Bundle bundle)
    {

        DbtLog.logUtils(TAG, "dealDateShow");
        String json = bundle.getString("dataLst");
        if (!CheckUtil.isBlankOrNull(json))
        {
            String type = String.valueOf(typeSp.getSelectedItemPosition());
            resultLv.setVisibility(View.VISIBLE);
            if (ConstValues.FLAG_0.equals(type))
            {
                //终端
                List<MstPowerfulterminalInfo> dataLst = JsonUtil.parseList(json, MstPowerfulterminalInfo.class);

                List<MstPowerfulterminalInfo> newdataLst = dealTerminalDataList(dataLst);
                if (CheckUtil.IsEmpty(newdataLst))
                {
                    dataLst = new ArrayList<MstPowerfulterminalInfo>();
                    ViewUtil.sendMsg(getBaseContext(), R.string.msg_search_nodata);
                }
                DistirbutionTerminalListViewAdapter adapter = new DistirbutionTerminalListViewAdapter(getBaseContext(), newdataLst);
                resultLv.setAdapter(adapter);
            }
            else if (ConstValues.FLAG_1.equals(type))
            {
                List<MstPowerfulchannelInfo> dataLst = JsonUtil.parseList(json, MstPowerfulchannelInfo.class);
                List<MstPowerfulchannelInfo> newdataLst = dealChannelDataList(dataLst);
                if (CheckUtil.IsEmpty(newdataLst))
                {
                    dataLst = new ArrayList<MstPowerfulchannelInfo>();
                    ViewUtil.sendMsg(getBaseContext(), R.string.msg_search_nodata);
                }
                DistirbutionChannelListViewAdapter adapter = new DistirbutionChannelListViewAdapter(getBaseContext(), newdataLst);
                resultLv.setAdapter(adapter);
            }
        }
    }

    /**
     * 处理渠道中的值除去"选择"，"待定"，添加"汇总"，"全部"
     * @param channelLst
     */
    private void handleChannel(List<KvStc> channelLst)
    {
        if (!CheckUtil.IsEmpty(channelLst))
        {
            //移除掉（请选择，待定）
            channelLst.remove(0);
            channelLst.remove(0);
        }
    };
    /**
     * 处理渠道中的值除去"选择"，添加"汇总"，"全部"  //20160803 ywm  后台不在同步销售渠道中的待定
     * @param channelLst
     */
    private void handleChannel2(List<KvStc> channelLst)
    {
    	/*if (!CheckUtil.IsEmpty(channelLst))
    	{
    		//移除掉（请选择，待定）
    		channelLst.remove(0);
    		//channelLst.remove(0);
    	}*/
    	
    	if (!CheckUtil.IsEmpty(channelLst)) {
    		//移除掉（请选择，待定）
            KvStc kvStc0 = channelLst.get(0);// 判断第一个是否是不是 请选择 并把它移除
            if ("-1".equals(kvStc0.getKey())
                    || "请选择".equals(kvStc0.getValue())) {
            	channelLst.remove(0);
            }
        }
    };

    /**
     * 处理查询终端类型返回来的数据
     * @param dataLst
     * @return
     */
    private List<MstPowerfulterminalInfo> dealTerminalDataList(List<MstPowerfulterminalInfo> dataLst)
    {
        List<MstPowerfulterminalInfo> newDataList = new ArrayList<MstPowerfulterminalInfo>();
        if (!levelId.equals("0"))
        {
            //接口传回来的是全部这儿进行处理
            List<String> levelIdList = Arrays.asList(levelId.split(","));
            for (MstPowerfulterminalInfo mstPowerfulterminalItem : dataLst)
            {
                for (int j = 0; j < levelIdList.size(); j++)
                {
                    if (mstPowerfulterminalItem.getTlevelcode().equals(levelIdList.get(j)))
                    {
                        newDataList.add(mstPowerfulterminalItem);
                    }
                }
            }
        }
        else
        {
            newDataList = dataLst;
        }
        return newDataList;

    }

    /**
     * 处理查询渠道类型返回来的数据
     * @param dataLst
     * @return
     */
    private List<MstPowerfulchannelInfo> dealChannelDataList(List<MstPowerfulchannelInfo> dataLst)
    {
        //处理以后的list，

        List<MstPowerfulchannelInfo> newDataList = new ArrayList<MstPowerfulchannelInfo>();
        if (sell.equals("0"))
        {
            //销售渠道是汇总
            newDataList = dataLst;
        }
        else if (!(sell.equals("0")) && main.equals("0"))
        {
            //销售渠道不是汇总，主渠道是汇总
            List<String> sellList = Arrays.asList(sell.split(","));
            for (MstPowerfulchannelInfo mstPowerfulchannelItem : dataLst)
            {
                for (int j = 0; j < sellList.size(); j++)
                {
                    if (mstPowerfulchannelItem.getSellchannelcode().equals(sellList.get(j)))
                    {
                        newDataList.add(mstPowerfulchannelItem);
                    }
                }
            }
        }
        else if (!(sell.equals("0")) && (!main.equals("0")) && minor.equals("0"))
        {
            //销售渠道，主渠道都不是汇总，次渠道是汇总
            List<String> mainList = Arrays.asList(main.split(","));
            for (MstPowerfulchannelInfo mstPowerfulchannelItem : dataLst)
            {
                for (int i = 0; i < mainList.size(); i++)
                {
                    if (mstPowerfulchannelItem.getMainchannelcode().equals(mainList.get(i)))
                    {
                        newDataList.add(mstPowerfulchannelItem);
                    }
                }
            }

        }
        else
        {
            //销售渠道，主渠道,次渠道都不是汇总
            List<String> minorList = Arrays.asList(minor.split(","));
            for (MstPowerfulchannelInfo mstPowerfulchannelItem : dataLst)
            {
                for (int i = 0; i < minorList.size(); i++)
                {
                    if (mstPowerfulchannelItem.getMinorchannelcode().equals(minorList.get(i)))
                    {
                        newDataList.add(mstPowerfulchannelItem);
                    }
                }
            }
        }
        return newDataList;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        DbtLog.logUtils(TAG, "onDestroy");
        dialog=null;
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        DbtLog.logUtils(TAG, "onSaveInstanceState");
        outState.putBundle("bundle", dataBundle);
        outState.putString("typeSelected", typeSelected);
        outState.putString("lineId", lineId);
        outState.putString("searchDate", searchDate);
        outState.putString("proId", proId);
        outState.putString("levelId", levelId);
        outState.putString("sell", sell);
        outState.putString("main", main);
        outState.putString("minor", minor);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
}
