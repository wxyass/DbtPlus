package et.tsingtaopad.visit.agencyvisit.ledger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.visit.agencyvisit.AgencySelectFragment;
import et.tsingtaopad.visit.agencyvisit.InOutSaveAdapter;
import et.tsingtaopad.visit.agencyvisit.LedgerPagerAdapter;
import et.tsingtaopad.visit.agencyvisit.LedgerService;
import et.tsingtaopad.visit.agencyvisit.TransferAdapter;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;
import et.tsingtaopad.visit.agencyvisit.domain.InOutSaveStc;
import et.tsingtaopad.visit.agencyvisit.domain.TransferStc;
import et.tsingtaopad.visit.syncdata.UploadDataService;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：AddAgency.java</br> 作者：ywm </br>
 * 创建时间：2015-11-23</br> 
 * 功能描述: 新增/编辑经销商</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
@SuppressLint("HandlerLeak")
public class LedgerViewPagerActivity extends BaseActivity implements OnClickListener , OnKeyListener{

	private final String TAG = "LedgerViewPagerActivity";

	private Button backBt;
	private TextView titleTv;

	private RelativeLayout backRl;

	private RelativeLayout confirmRl;
	
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

    //private et.tsingtaopad.view.ScrollViewWithListView inOutSaveLv;
    private ListView inOutSaveLv;
    private ListView transferLv;
    private TransferAdapter transferAdapter;

    private TextView okBt;

    private ImageButton addIb;
    private ImageButton saveIb;

    private EditText remarksView;
    Map<String, InOutSaveStc> iosStcMap=new HashMap<String, InOutSaveStc>();

	private ScrollView scroll;
    

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.agencyvisit_ledger_viewpage);
		
		//ViewPager中的子View创建
        //inOutSaveView = LayoutInflater.from(this).inflate(R.layout.agencyvisit_inoutsave_ll, null);
        inOutSaveView = LayoutInflater.from(this).inflate(R.layout.agencyvisit_inoutsave, null);
        transferView = LayoutInflater.from(this).inflate(R.layout.agencyvisit_transfer_rl, null);
        
		this.initView();
		this.initData();
	}

	private void initView() {

		// 初始化界面组件
		backBt = (Button) findViewById(R.id.banner_navigation_bt_back);
		backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		titleTv = (TextView) findViewById(R.id.banner_navigation_tv_title);
		
		
		// 绑定事件
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		backBt.setOnClickListener(this);
		
		//------
		//绑定界面组件
        viewPager = (ViewPager) findViewById(R.id.agencyvisit_vp_viewpager);
        pagerTab = (PagerTabStrip) findViewById(R.id.agencyvisit_pts_viewpager);
        okBt = (TextView) findViewById(R.id.banner_navigation_bt_confirm);
        okBt.setVisibility(View.VISIBLE);

        //进销存台账和调货台账界面组件绑定
        //scroll = (ScrollView)inOutSaveView.findViewById(R.id.agencyvisit_inout);
        //inOutSaveLv = (et.tsingtaopad.view.ScrollViewWithListView) inOutSaveView.findViewById(R.id.inoutsave_lv_inoutsave);
        inOutSaveLv = (ListView) inOutSaveView.findViewById(R.id.inoutsave_lv_inoutsave);
        transferLv = (ListView) transferView.findViewById(R.id.transfer_lv_transfer);
        saveIb = (ImageButton) inOutSaveView.findViewById(R.id.inoutsave_ib_save);
        addIb = (ImageButton) transferView.findViewById(R.id.transfer_ib_add);
        remarksView = (EditText) inOutSaveView.findViewById(R.id.remarks);
        
		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
        //绑定事件
        backBt.setOnClickListener(this);
        //okBt.setOnClickListener(this);
        saveIb.setOnClickListener(this);
        addIb.setOnClickListener(this);
        //getView().setOnClickListener(null);
        //getView().setOnKeyListener(this);
        
	}

	private void initData() {
		titleTv.setText("测试分销商");
		
		// ------------
		
		

        service = new LedgerService(this);

        //记录拜访开始日期
        visitDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");

        //获取参数
        Intent intent = getIntent();
        //先通过intent获取Bundle对象
        Bundle bundle = intent.getExtras();
        
        // 获取传递过来的 经销商主键,名称,地址,联系电话
        asStc = (AgencySelectStc) bundle.get(AgencySelectFragment.TAG);
        // 获取当天的这次和上次拜访记录 mapLedger 
        Map<String, Object> mapLedger = service.getMstAgencyvisitM(asStc.getAgencyKey(), visitDate);//一天多次拜访显示数据
        // 获取这次经销商拜访的记录,包含主键,拜访时间等等
        visitM = (MstAgencyvisitM) mapLedger.get("LsMstAgencyvisitM");
        // 获取上次经销商拜访的主键
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
            
            mstInfo.setSelfsales(iosStc.getSelfsales());
            mstInfo.setUnselfsales(iosStc.getUnselfsales());
            mstInfo.setOthersales(iosStc.getOthersales());
            
            iosStcMap.put(mstInfo.getAgencykey()+"_"+mstInfo.getProductkey(), mstInfo);
        }
        
        // 获取调货台账数据
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
        //View headview = LayoutInflater.from(this).inflate(R.layout.visit_agencyvisit_headview, null);
        //inOutSaveLv.addHeaderView(headview);
        View footview = LayoutInflater.from(this).inflate(R.layout.visit_agencyvisit_footview, null);
        inOutSaveLv.addFooterView(footview);
        InOutSaveAdapter iosAdapter = new InOutSaveAdapter(this, iosStcLst);//进销存台账
        inOutSaveLv.setAdapter(iosAdapter);
        remarksView.setText(visitM.getRemarks());

        //添加底部view和绑定调货台账的Listview数据
        View footview2 = LayoutInflater.from(this).inflate(R.layout.visit_agencyvisit_footview, null);
        transferLv.addFooterView(footview2);
        transferAdapter = new TransferAdapter(this, transStcLst, asStc.getAgencyKey());
        transferLv.setAdapter(transferAdapter);
    
        // 解决scrollView内嵌listView, 第一次进入界面时动态加载listview的items后页面会跳转到listview的第一个子项
        /*scroll.post(new Runnable() {  
		      //让scrollview跳转到顶部，必须放在runnable()方法中
		    @Override  
		    public void run() {  
		    	//scroll.scrollTo(0, 0);  
		    	scroll.smoothScrollTo(0, 0);  
		     }  
		   });*/
	}
	

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		
		// 返回
		case R.id.banner_navigation_rl_back:
        case R.id.banner_navigation_bt_back:

            if (!succeedUpload)
            {
                isUpload = 1;
                // 检测调货台账数据是否完整
                checkedData();
            }
            break;

            // 确定
        case R.id.banner_navigation_rl_confirm:
        //case R.id.banner_navigation_bt_confirm:
            isUpload = 2;
            // 检测调货台账数据是否完整
            checkedData();
            break;
          
            // 无用的按钮
        case R.id.inoutsave_ib_save:
            service.saveIOSData(iosStcLst, visitM, prevVisitKey,iosStcMap,false);
            break;

            // 调货台账 新增
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
     * 弹窗->是否结束本次拜访?->确定上传
     */
    private void showOKDialog()
    {
        View view = LayoutInflater.from(this).inflate(R.layout.agencyvisit_total_overvisit_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.agencyvisit_tv_over_title);
        TextView msg = (TextView) view.findViewById(R.id.agencyvisit_tv_over_msg);
        ImageView sure = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_sure);
        ImageView cancle = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_quxiao);
        title.setText(R.string.transfer_msg_title);
        msg.setText(R.string.transfer_msg_isout);
        final AlertDialog dialog = new AlertDialog.Builder(this).setCancelable(false).create();
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
                UploadDataService uploadService = new UploadDataService(LedgerViewPagerActivity.this, null);
                uploadService.upload_agency_visit(false, visitM.getAgevisitkey());
                dialog.dismiss();
                //getFragmentManager().popBackStack();
                //getFragmentManager().popBackStack();
                LedgerViewPagerActivity.this.finish();

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
            	// 如果 调货台账数据不完整,弹框提示
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
                    	// 如果 调货台账数据不完整,弹框提示
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
            //getFragmentManager().popBackStack();
            this.finish();
        }
        else if (isUpload == 2)
        {
        	// 是否结束本次拜访?->确定上传
            showOKDialog();
        }
    }

    // 如果 调货台账数据不完整,弹框提示
    private void showDialog()
    {
        View view = LayoutInflater.from(this).inflate(R.layout.agencyvisit_total_overvisit_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.agencyvisit_tv_over_title);
        TextView msg = (TextView) view.findViewById(R.id.agencyvisit_tv_over_msg);
        ImageView sure = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_sure);
        ImageView cancle = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_quxiao);
        title.setText(R.string.transfer_msg_title);
        msg.setText(R.string.agencyvisit_dialog_over);
        final AlertDialog dialog = new AlertDialog.Builder(this).setCancelable(false).create();
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
                        //getFragmentManager().popBackStack();
                        LedgerViewPagerActivity.this.finish();
                       
                        break;

                    case 2:
                        dialog.dismiss();
                        // 是否结束本次拜访?->确定上传
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
