package et.tsingtaopad.visit.agencyvisit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.db.tables.MstInvoicingInfo;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;
import et.tsingtaopad.visit.agencyvisit.domain.InOutSaveStc;
import et.tsingtaopad.visit.agencyvisit.domain.TransferStc;
import et.tsingtaopad.visit.syncdata.UploadDataService;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：LedgerViewPagerFragment.java</br>
 * 作者：@吴欣伟    </br>
 * 创建时间：2013/11/26</br>      
 * 功能描述: 进销存台账和调货台账界面</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class LedgerViewPagerFragment extends BaseFragmentSupport implements OnClickListener, OnKeyListener
{

    private LedgerService service;
    private AgencySelectStc asStc;
    private MstAgencyvisitM visitM;
    private String visitDate;

    //再没点击确定上传离开页面数值为 1,点击确定上传数值为2
    private int isUpload = 1;
    private boolean succeedUpload = false;

    //记录上传拜访的主键
    private String prevVisitKey;
    private ViewPager viewPager;
    private PagerTabStrip pagerTab;
    private View inOutSaveView;
    private View transferView;
    private List<InOutSaveStc> iosStcLst;
    private List<TransferStc> transStcLst;

    private ListView inOutSaveLv;
    private ListView transferLv;
    private TransferAdapter transferAdapter;

    private Button backBt;
    private TextView okBt;
    private TextView titleTv;

    private ImageButton addIb;
    private ImageButton saveIb;

    private EditText remarksView;
    Map<String, InOutSaveStc> iosStcMap=new HashMap<String, InOutSaveStc>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.agencyvisit_total_viewpage, null);

        //ViewPager中的子View创建
        inOutSaveView = LayoutInflater.from(getActivity()).inflate(R.layout.agencyvisit_inoutsave_ll, null);
        transferView = LayoutInflater.from(getActivity()).inflate(R.layout.agencyvisit_transfer_rl, null);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    /**
     * 初始化界面组件
     */
    private void initView()
    {

        //绑定界面组件
        viewPager = (ViewPager) getView().findViewById(R.id.agencyvisit_vp_viewpager);
        pagerTab = (PagerTabStrip) getView().findViewById(R.id.agencyvisit_pts_viewpager);
        backBt = (Button) getView().findViewById(R.id.banner_navigation_bt_back);
        okBt = (TextView) getView().findViewById(R.id.banner_navigation_bt_confirm);
        okBt.setVisibility(View.VISIBLE);
        titleTv = (TextView) getView().findViewById(R.id.banner_navigation_tv_title);

        //进销存台账和调货台账界面组件绑定
        inOutSaveLv = (ListView) inOutSaveView.findViewById(R.id.inoutsave_lv_inoutsave);
        transferLv = (ListView) transferView.findViewById(R.id.transfer_lv_transfer);
        saveIb = (ImageButton) inOutSaveView.findViewById(R.id.inoutsave_ib_save);
        addIb = (ImageButton) transferView.findViewById(R.id.transfer_ib_add);
        remarksView = (EditText) inOutSaveView.findViewById(R.id.remarks);
		RelativeLayout backRl = (RelativeLayout) getView().findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) getView().findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
        //绑定事件
        backBt.setOnClickListener(this);
        //okBt.setOnClickListener(this);
        saveIb.setOnClickListener(this);
        addIb.setOnClickListener(this);
        getView().setOnClickListener(null);
        getView().setOnKeyListener(this);
    }

    /**
     * 初始化界面数据
     */
    private void initData()
    {
        service = new LedgerService(getActivity());

        //记录拜访开始日期
        visitDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");

        //获取参数
        asStc = (AgencySelectStc) getArguments().get(AgencySelectFragment.TAG);
        Map<String, Object> mapLedger = service.getMstAgencyvisitM(asStc.getAgencyKey(), visitDate);//一天多次拜访显示数据
        visitM = (MstAgencyvisitM) mapLedger.get("LsMstAgencyvisitM");
        prevVisitKey = (String) mapLedger.get("LsprevVisitKey");

        //设置页面
        titleTv.setText(asStc.getAgencyName());

        //获取进销存台账数据
        iosStcLst = service.getInOutSave(asStc.getAgencyKey(), prevVisitKey, visitM,mapLedger);
        for(InOutSaveStc iosStc:iosStcLst){
            InOutSaveStc mstInfo = new InOutSaveStc();
            mstInfo.setAgencykey(iosStc.getAgencykey());
            mstInfo.setProductkey(iosStc.getProductkey());
            mstInfo.setStorenum(iosStc.getStorenum());
            mstInfo.setPrestorenum(iosStc.getPrestorenum());
            iosStcMap.put(mstInfo.getAgencykey()+"_"+mstInfo.getProductkey(), mstInfo);
        }
        transStcLst = service.getTransfer(visitM, prevVisitKey);

        //创建ViewPager的进销存台账和调货台账的View
        List<View> viewLst = new ArrayList<View>();
        viewLst.add(inOutSaveView);
        viewLst.add(transferView);

        //创建并设置ViewPager的进销存台账和调货台账的Title
        List<String> titleList = new ArrayList<String>();
        titleList.add(getResources().getString(R.string.inoutsave_label_title));
        titleList.add(getResources().getString(R.string.transfer_label_title));
        pagerTab.setTextColor(getResources().getColor(R.color.agency_viewpager_title));
        pagerTab.setTextSize(0, 28);

        //绑定ViewPager的数据
        LedgerPagerAdapter adapter = new LedgerPagerAdapter(viewLst, titleList);
        viewPager.setAdapter(adapter);

        //添加底部view和绑定进销存台账的Listview数据
        View footview = LayoutInflater.from(getActivity()).inflate(R.layout.visit_agencyvisit_footview, null);
        inOutSaveLv.addFooterView(footview);
        InOutSaveAdapter iosAdapter = new InOutSaveAdapter(getActivity(), iosStcLst);//进销存台账
        inOutSaveLv.setAdapter(iosAdapter);
        remarksView.setText(visitM.getRemarks());

        //添加底部view和绑定调货台账的Listview数据
        View footview2 = LayoutInflater.from(getActivity()).inflate(R.layout.visit_agencyvisit_footview, null);
        transferLv.addFooterView(footview2);
        transferAdapter = new TransferAdapter(getActivity(), transStcLst, asStc.getAgencyKey());
        transferLv.setAdapter(transferAdapter);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        	case R.id.banner_navigation_rl_back:
            case R.id.banner_navigation_bt_back:

                if (!succeedUpload)
                {
                    isUpload = 1;
                    checkedData();
                }
                break;

            case R.id.banner_navigation_rl_confirm:
            //case R.id.banner_navigation_bt_confirm:
                isUpload = 2;
                checkedData();
                break;

            case R.id.inoutsave_ib_save:
                service.saveIOSData(iosStcLst, visitM, prevVisitKey,iosStcMap,false);
                break;

            case R.id.transfer_ib_add:
                TransferStc transferInfo = new TransferStc();
                transStcLst.add(transferInfo);
                transferAdapter.notifyDataSetChanged();
                break;

            default:
                break;
        }
    }

    /**
     * 点击确定按钮弹出提示框
     */
    private void showOKDialog()
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.agencyvisit_total_overvisit_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.agencyvisit_tv_over_title);
        TextView msg = (TextView) view.findViewById(R.id.agencyvisit_tv_over_msg);
        ImageView sure = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_sure);
        ImageView cancle = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_quxiao);
        title.setText(R.string.transfer_msg_title);
        msg.setText(R.string.transfer_msg_isout);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        dialog.setView(view, 0, 0, 0, 0);
        sure.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                succeedUpload = true;

                //设置上次库存
                for(InOutSaveStc i:iosStcLst){
                    i.setPrestorenum(i.getStorenumTemp());
                }
                //保存进销存台账和调货台账数据
                service.saveIOSData(iosStcLst, visitM, prevVisitKey,iosStcMap,true);
                service.saveTransferData(visitM, transStcLst);

                //设置分经销商拜访主表的单击上传按钮状态和最后操作时间并保存更新数据
                String visitEndDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
                visitM.setEnddate(visitEndDate);
                visitM.setUpdatetime(new Date());
                visitM.setUploadFlag(ConstValues.FLAG_1);
                visitM.setPadisconsistent(ConstValues.FLAG_1);
                String remarks = remarksView.getText().toString();
                visitM.setRemarks(remarks);
                service.saveMstAgencyvisitM(visitM);

                //往服务器上传经销商拜访数据
                UploadDataService uploadService = new UploadDataService(getActivity(), null);
                uploadService.upload_agency_visit(false, visitM.getAgevisitkey());
                dialog.dismiss();
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();

            }
        });
        cancle.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //如果没有点击确定上传按钮就自动保存数据
        if (!succeedUpload)
        {
            service.saveIOSData(iosStcLst, visitM, prevVisitKey,iosStcMap,false);
            service.saveTransferData(visitM, transStcLst);
        }
    }

    /**
     * 在离开台账页面时判断数据输入是否正确
     */
    private void checkedData()
    {

        for (int i = 0; i < transStcLst.size(); i++)
        {
            TransferStc info = transStcLst.get(i);
            if (info.getProductkey() == null || info.getTagencykey() == null || (info.getTranin().intValue() == 0 && info.getTranout().intValue() == 0))
            {
                showDialog();
                return;
            }
            else
            {
                for (int j = i + 1; j < transStcLst.size(); j++)
                {
                    TransferStc info1 = transStcLst.get(j);
                    if (info1.getProductkey() == null || info1.getTagencykey() == null || (info1.getTranin().intValue() == 0 && info1.getTranout().intValue() == 0) && (info.getProductkey().equals(info1.getProductkey()) && info.getTagencykey().equals(info1.getTagencykey())))
                    {
                        showDialog();
                        return;
                    }
                }
            }
        }
        if (isUpload == 1)
        {
            //                service.saveIOSData(iosStcLst, visitM, prevVisitKey);
            //                service.saveTransferData(visitM, transStcLst);
            getFragmentManager().popBackStack();
        }
        else if (isUpload == 2)
        {
            showOKDialog();
        }
    }

    private void showDialog()
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.agencyvisit_total_overvisit_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.agencyvisit_tv_over_title);
        TextView msg = (TextView) view.findViewById(R.id.agencyvisit_tv_over_msg);
        ImageView sure = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_sure);
        ImageView cancle = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_quxiao);
        title.setText(R.string.transfer_msg_title);
        msg.setText(R.string.agencyvisit_dialog_over);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        dialog.setView(view, 0, 0, 0, 0);
        sure.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                switch (isUpload)
                {
                    case 1:
                        service.saveIOSData(iosStcLst, visitM, prevVisitKey,iosStcMap,false);
                        service.saveTransferData(visitM, transStcLst);
                        dialog.dismiss();
                        getFragmentManager().popBackStack();
                        break;

                    case 2:
                        dialog.dismiss();
                        showOKDialog();
                        break;

                    default:
                        break;
                }
            }
        });
        cancle.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        Log.i("facg", "ffffffffff");
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {

            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
    
    
}
