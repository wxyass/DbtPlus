package et.tsingtaopad.visit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.DeleteTools;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.operation.distirbution.DistirbutionActivity;
import et.tsingtaopad.operation.indexstatus.IndexstatusFragment;
import et.tsingtaopad.operation.linecardsearch.LineCardSearchFragment;
import et.tsingtaopad.operation.orders.OrdersFragment;
import et.tsingtaopad.operation.promotion.PromotionActivity;
import et.tsingtaopad.operation.termtz.TermtzSearchFragment;
import et.tsingtaopad.operation.working.DayWeekingSdFragment;
import et.tsingtaopad.operation.working.DayWorkingFragment;
import et.tsingtaopad.operation.working.WeekWorkingFragment;
import et.tsingtaopad.operation.workplan.WorkPlanFragment;
import et.tsingtaopad.syssetting.SystemSettingInfor;
import et.tsingtaopad.syssetting.SystemSettingModifyPwdFragment;
import et.tsingtaopad.syssetting.notice.NoticeFragmentTest;
import et.tsingtaopad.syssetting.queryfeedback.QueryFeedbackFragment;
import et.tsingtaopad.syssetting.version.SystemSettingUpdateFragment;
import et.tsingtaopad.syssetting.version.VersionService;
import et.tsingtaopad.tools.DataCleanManager;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.FileUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.GZIP;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ZipHelper;
import et.tsingtaopad.tools.ZipUtil;
import et.tsingtaopad.visit.agencykf.AgencyKFActivity;
import et.tsingtaopad.visit.agencystorage.AgencyStorageActivity;
import et.tsingtaopad.visit.agencyvisit.AgencySelectFragment;
import et.tsingtaopad.visit.shopvisit.line.LineListFragment;
import et.tsingtaopad.visit.showproduct.ShowProductFragment;
import et.tsingtaopad.visit.syncdata.DownLoadDataProgressActivity;
import et.tsingtaopad.visit.syncdata.DownLoadDataService;
import et.tsingtaopad.visit.termadd.TermAddFragment;
import et.tsingtaopad.visit.termadd.TermAddListFragment;
import et.tsingtaopad.visit.terminaldetails.TerminalDetailsFragment;
import et.tsingtaopad.visit.termtz.TzAgencyActivity;
import et.tsingtaopad.visit.tomorrowworkrecord.TomorrowWorkRecordFragment;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：VisitFragment.java</br> 
 * 作者：吴承磊 </br>
 * 创建时间：2013-11-28</br> 
 * 功能描述: 拜访工作主界面</br>	
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期     原因  BUG号     修改人    修改版本</br>
 * 
 */
//拜访工作主界面
@SuppressLint("HandlerLeak")
public class VisitFragment1 extends BaseFragmentSupport //implements OnClickListener
{
    String TAG = "VisitFragment";
    
    private AlertDialog dialog1;
    private GridView platform_gv_visit;
	private ArrayList<Integer> images;
	private HashMap<String, Integer> authorityMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        DbtLog.logUtils(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.platform_visit_gridview, null);
        this.initView(view);
        this.initData();

        return view;
    }
    
    Handler handler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case ConstValues.WAIT2:
				// 
				 if (dialog1 != null && dialog1.isShowing()) {
					 
					 dialog1.dismiss();
					 
					 
			            /*// 同步数据
			            Intent download = new Intent(getActivity(),DownLoadDataProgressActivity.class);
						startActivity(download);*/
			        } else {
			            dialog1.dismiss();
			            
			            
			        }
				
				break;

			default:
				break;
			}
    	};
    };

    // 初始化界面控件
    private void initView(View view)
    {
        DbtLog.logUtils(TAG, "initView");
        
        platform_gv_visit = (GridView) view.findViewById(R.id.platform_gv_visit);
        // 初始化按钮对应图片
        initPic();
    }
    
    // 初始化按钮图片对应
	private void initPic() {
		// 初始化按钮图片对应
		authorityMap = new HashMap<String, Integer>();
		authorityMap.put("1000000", R.drawable.bt_visit_shopvist);// 巡店拜访
		authorityMap.put("1000001", R.drawable.bt_visit_addterm);// 新增终端
		authorityMap.put("1000002", R.drawable.bt_visit_termdetail);// 终端进货明细
		authorityMap.put("1000003", R.drawable.bt_visit_agency);// 经销商拜访
		authorityMap.put("1000004", R.drawable.bt_visit_store);// 经销商库存
		authorityMap.put("1000005", R.drawable.bt_visit_agencykf);// 经销商开发
		authorityMap.put("1000006", R.drawable.bt_visit_sync);// 数据同步
		authorityMap.put("1000007", R.drawable.bt_visit_termtaizhang);// 终端进货台账
		authorityMap.put("1000008", R.drawable.bt_business_product);// 产品展示
		authorityMap.put("1000009", R.drawable.bt_visit_addterm_other);// 其它
		
		authorityMap.put("1000010", R.drawable.bt_operation_workplan);// 日/周工作计划
		authorityMap.put("1000011", R.drawable.bt_operation_workdetail1);// 日工作推进(标准)
		authorityMap.put("1000012", R.drawable.bt_operation_workdetail);// 周工作总结
		authorityMap.put("1000013", R.drawable.bt_visit_work);// 日工作记录
		authorityMap.put("1000014", R.drawable.bt_operation_omnipotent);// 万能铺货率查询
		authorityMap.put("1000015", R.drawable.bt_operation_indexsearch);// 指标状态查询
		authorityMap.put("1000016", R.drawable.bt_operation_promotion);// 促销活动查询
		authorityMap.put("1000017", R.drawable.bt_operation_workdetail11);// 日工作推进(山东) 不用了
		
		authorityMap.put("1000018", R.drawable.bt_business_notice);// 通知公告
		authorityMap.put("1000019", R.drawable.bt_business_question);// 问题反馈
		authorityMap.put("1000020", R.drawable.bt_syssetting_update);// 检查更新
		authorityMap.put("1000021", R.drawable.bt_syssetting_modify_pwd);// 修改密码
		authorityMap.put("1000022", R.drawable.bt_syssetting_info);// 关于系统
		
		authorityMap.put("1000023", R.drawable.bt_operation_dingdan);// 订单详细
		authorityMap.put("1000024", R.drawable.bt_operation_load);// 路线卡查询
		authorityMap.put("1000025", R.drawable.bt_operation_taizhang);// 终端进货台账查询
	}

	// 初始化控件数据
	private void initData() {
		
		// 获取tabIndex、funId
        Bundle bundle = getArguments();
        int tabIndex = -1;
        String funId = "";
        if (bundle != null)
        {
            tabIndex = bundle.getInt("tabIndex");
            funId = FunUtil.isBlankOrNullTo(bundle.getString("funId"), "");
        }

        if (tabIndex == 1)
        {
            bundle.remove("funId");
            if ("shopVisit".equals(funId))
            {
            	// 巡店拜访
                shopVisitClick();

            }
            else if ("sync".equals(funId))
            {
            	// 同步数据
            	syncData();
            }
        }
		
		
		
		
		
		
		// 接口获取权限
		String bfgl = PrefUtils.getString(getActivity(), "bfgl", "");
		
		if(bfgl!=null&&bfgl.length()>0){
			if(bfgl.contains(",")){
				String[] visitAuthority = bfgl.split(",");
				images = new ArrayList<Integer>();
				images.removeAll(images);
				for (String string : visitAuthority) {
			        images.add(authorityMap.get(string));
				}
				JieMianAdapter jieMianAdapter = new JieMianAdapter();
				platform_gv_visit.setSelector(new ColorDrawable(Color.TRANSPARENT));
				platform_gv_visit.setAdapter(jieMianAdapter);
				// 设置item的点击监听
				platform_gv_visit.setOnItemClickListener(new ItemClickListener() );
			}else{
				String[] visitAuthority = {bfgl};
				images = new ArrayList<Integer>();
				images.removeAll(images);
				for (String string : visitAuthority) {
			        images.add(authorityMap.get(string));
				}
				JieMianAdapter jieMianAdapter = new JieMianAdapter();
				platform_gv_visit.setSelector(new ColorDrawable(Color.TRANSPARENT));
				platform_gv_visit.setAdapter(jieMianAdapter);
				// 设置item的点击监听
				platform_gv_visit.setOnItemClickListener(new ItemClickListener() );
			}
		}
		
		/*
		String logpath = FileUtil.getLogPathNoX();
		String json = ZipUtil.readFile(logpath + "/termys.txt");
		String jsonZip;
		try {
			jsonZip = GZIP.compress(json);
			String sd = GZIP.unCompress(jsonZip);
			String sdf = "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String logLog = FileUtil.getLogPathNoX();
		String logLogjson = ZipUtil.readFile(logLog + "/termys.txt");
		try {
			String jsonZip = new String(ZipHelper.zipString(logLogjson), "ISO-8859-1");
			ResponseStructBean sd = HttpUtil.parseRes(jsonZip);
			String sdf = "";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
	}
	
	class ItemClickListener implements OnItemClickListener {
        /**
         * 点击项时触发事件
         * 
         * @param parent  发生点击动作的AdapterView
         * @param view 在AdapterView中被点击的视图(它是由adapter提供的一个视图)。
         * @param position 视图在adapter中的位置。
         * @param rowid 被点击元素的行id。
         */
        public void onItemClick(AdapterView<?> parent, View view, int position, long rowid) {
        	
        	Fragment fragment = null;
            Intent intent = null;

			switch (images.get(position)) {
			
			// 巡店拜访
			case R.drawable.bt_visit_shopvist:
				// 先检测是否需要更新
				VersionService service = new VersionService(getActivity(), true, false);
				service.checkVersion();
				
				if(getTermnalNum()>0){// 判断是否同步过数据(即:终端表有数据)
            		//fragment = new TermAddFragment();
					//格式化日期
	                String nowDate = DateUtil.formatDate(new Date(), DateUtil.DEFAULT_DATE_FORMAT);
	                //获取同步更新日期
	                String datasynTime = DownLoadDataService.getDatasynTime(getActivity());
	                long daygap = 0;
	                Date date1 = null;
	                Date date2 = null;
	                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	                if (!datasynTime.equals("") && !datasynTime.equals(null))
	                {
	                    GregorianCalendar cal1 = new GregorianCalendar();
	                    GregorianCalendar cal2 = new GregorianCalendar();

	                    try
	                    {
	                        date1 = sdf.parse(nowDate);
	                        date2 = sdf.parse(datasynTime);
	                    }
	                    catch (ParseException e)
	                    {
	                        // TODO Auto-generated catch block
	                        e.printStackTrace();
	                    }
	                    cal1.setTime(date1);
	                    cal2.setTime(date2);
	                    daygap = (cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000 * 3600 * 24);
	                }
	                //当大于5时才同步,超过5天未同步会弹窗提示去同步
	                //if (daygap >= 5)
	                if (!nowDate.equals(datasynTime))// 上次同步时间与当前时间
	                {
	                    fragment = null;
	                    intent = null;

	                    Builder builder = new Builder(getActivity());
	                    builder.setTitle("温馨提示");
	                    builder.setMessage("今日未同步数据，请先同步数据再拜访.");
	                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
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
	                else
	                {
	                    fragment = new LineListFragment();
	                    //fragment = new TermListFragment();
	                    DbtLog.logUtils(TAG, "巡店拜访");
	                }
	                //每天必须同步
	                /*if(nowDate.equals(datasynTime)){
	                    fragment = new LineListFragment();
	                    DbtLog.logUtils(TAG, "巡店拜访");
	                }else{
	                    fragment=null;
	                    intent=null;

	                    AlertDialog.Builder builder = new Builder(getActivity());
	                    builder.setTitle("温馨提示");
	                    builder.setMessage("今日未同步数据，请先同步数据再拜访.");
	                    builder.setNegativeButton("确定", new android.content.DialogInterface.OnClickListener() {           
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            dialog.dismiss();               
	                        }
	                    });
	                    AlertDialog dialog=builder.create();
	                    dialog.show();
	                }*/
            	} else {
            		Toast.makeText(getActivity(), "基础信息不完整,请先同步数据", Toast.LENGTH_SHORT).show();
            	}
				
				break;
			// 新增终端
			case R.drawable.bt_visit_addterm:
				if(getTermnalNum()>0){
            		fragment = new TermAddFragment();
            	} else {
            		Toast.makeText(getActivity(), "基础信息不完整,请先同步数据", Toast.LENGTH_SHORT).show();
            	}
                DbtLog.logUtils(TAG, "新增终端");
				break;
			// 终端进货明细
			case R.drawable.bt_visit_termdetail:
				intent = new Intent(getActivity(), TerminalDetailsFragment.class);
                DbtLog.logUtils(TAG, "终端详情");
				break;
			// 经销商拜访
			case R.drawable.bt_visit_agency:
				fragment = new AgencySelectFragment();
                DbtLog.logUtils(TAG, "经销商拜访");
				break;
			// 经销商库存
			case R.drawable.bt_visit_store:
				intent = new Intent(getActivity(), AgencyStorageActivity.class);
                DbtLog.logUtils(TAG, "经销商库存");
				break;
			// 经销商开发
			case R.drawable.bt_visit_agencykf:
				intent = new Intent(getActivity(), AgencyKFActivity.class);
                DbtLog.logUtils(TAG, "新增经销商");
				break;
			// 手动同步
			case R.drawable.bt_visit_sync:
				DbtLog.logUtils(TAG, "数据同步");
				syncData();
				break;
			// 终端进货台账
			case R.drawable.bt_visit_termtaizhang:
				DbtLog.logUtils(TAG, "终端进货台账");
            	intent = new Intent(getActivity(), TzAgencyActivity.class);
				break;
			// 产品展示
			case R.drawable.bt_business_product:
				fragment = new ShowProductFragment();
                DbtLog.logUtils(TAG, "产品展示");
				break;
			// 其它
			case R.drawable.bt_visit_addterm_other:
				fragment = new TermAddListFragment();
                DbtLog.logUtils(TAG, "新增终端列表");
				break;
				
			// 日/周工作计划
			case R.drawable.bt_operation_workplan:
				fragment = new WorkPlanFragment();
				break;
				
				// 订单详细
			case R.drawable.bt_operation_dingdan:
				fragment = new OrdersFragment();
				//intent = new Intent(getActivity(), OrdersActivity.class);
				break;
				
			// 日工作推进(标准)
			case R.drawable.bt_operation_workdetail1:
				fragment = new DayWorkingFragment();
				break;
			// 周工作总结
			case R.drawable.bt_operation_workdetail:
				fragment = new WeekWorkingFragment();
                DbtLog.logUtils(TAG, "周工作总结");
				break;
			// 日工作记录
			case R.drawable.bt_visit_work:
				intent = new Intent(getActivity(), TomorrowWorkRecordFragment.class);
				break;
				
				// 路线卡查询
			case R.drawable.bt_operation_load:
				intent = new Intent(getActivity(), LineCardSearchFragment.class);
				break;
				
				// 终端进货台账查询
			case R.drawable.bt_operation_taizhang:
				intent = new Intent(getActivity(), TermtzSearchFragment.class);
				break;
			// 万能铺货率查询
			case R.drawable.bt_operation_omnipotent:
				intent = new Intent(getActivity(), DistirbutionActivity.class);
				break;
			// 指标状态查询
			case R.drawable.bt_operation_indexsearch:
				fragment = new IndexstatusFragment();
				break;
			// 促销活动查询
			case R.drawable.bt_operation_promotion:
				intent = new Intent(getActivity(), PromotionActivity.class);
				break;
			// 日工作推进(山东)
			case R.drawable.bt_operation_workdetail11:
				fragment=new DayWeekingSdFragment();
				break;
				
			// 通知公告
			case R.drawable.bt_business_notice:
				fragment = new NoticeFragmentTest();
				break;
				
			// 问题反馈
			case R.drawable.bt_business_question:
				fragment = new QueryFeedbackFragment();
				break;
			// 检查更新
			case R.drawable.bt_syssetting_update:
				fragment = new SystemSettingUpdateFragment();
                DbtLog.logUtils(TAG, "周工作总结");
				break;
			// 修改密码
			case R.drawable.bt_syssetting_modify_pwd:
				fragment = new SystemSettingModifyPwdFragment();
				break;
			// 关于系统
			case R.drawable.bt_syssetting_info:
				fragment = new SystemSettingInfor();
				break;

			default:
				break;
			}
			
			if (intent != null)
	        {
	            getActivity().startActivity(intent);
	        }

	        if (fragment != null)
	        {
	            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
	            transaction.replace(R.id.visit_container, fragment);
	            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	            transaction.addToBackStack(null);
	            transaction.commit();
	        }
        }
    }
	
    /*
    @SuppressLint("NewApi")
    private void initDate()
    {
        DbtLog.logUtils(TAG, "initDate");
        // 获取tabIndex、funId
        Bundle bundle = getArguments();
        int tabIndex = -1;
        String funId = "";
        if (bundle != null)
        {
            tabIndex = bundle.getInt("tabIndex");
            funId = FunUtil.isBlankOrNullTo(bundle.getString("funId"), "");
        }

        if (tabIndex == 1)
        {
            bundle.remove("funId");
            if ("shopVisit".equals(funId))
            {
            	//performClick 模拟人手去触摸控件
                shopVisitBt.performClick();

            }
            else if ("sync".equals(funId))
            {
                syncBt.performClick();
            }
        }
    }
    */

    /*@Override
    public void onClick(View v)
    {

        Fragment fragment = null;
        Intent intent = null;

        switch (v.getId())
        {

        // 巡店拜访
            case R.id.visit_bt_shopvisit:
            	//格式化日期
                String nowDate = DateUtil.formatDate(new Date(), DateUtil.DEFAULT_DATE_FORMAT);
                //获取同步更新日期
                String datasynTime = DownLoadDataService.getDatasynTime(getActivity());
                long daygap = 0;
                Date date1 = null;
                Date date2 = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

                if (!datasynTime.equals("") && !datasynTime.equals(null))
                {
                    GregorianCalendar cal1 = new GregorianCalendar();
                    GregorianCalendar cal2 = new GregorianCalendar();

                    try
                    {
                        date1 = sdf.parse(nowDate);
                        date2 = sdf.parse(datasynTime);
                    }
                    catch (ParseException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    cal1.setTime(date1);
                    cal2.setTime(date2);
                    daygap = (cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000 * 3600 * 24);
                }
                //当大于5时才同步
                if (daygap >= 5)
                {
                    fragment = null;
                    intent = null;

                    AlertDialog.Builder builder = new Builder(getActivity());
                    builder.setTitle("温馨提示");
                    builder.setMessage("今日未同步数据，请先同步数据再拜访.");
                    builder.setNegativeButton("确定", new android.content.DialogInterface.OnClickListener()
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
                else
                {
                    fragment = new LineListFragment();
                    DbtLog.logUtils(TAG, "巡店拜访");
                }
                //每天必须同步
                if(nowDate.equals(datasynTime)){
                    fragment = new LineListFragment();
                    DbtLog.logUtils(TAG, "巡店拜访");
                }else{
                    fragment=null;
                    intent=null;

                    AlertDialog.Builder builder = new Builder(getActivity());
                    builder.setTitle("温馨提示");
                    builder.setMessage("今日未同步数据，请先同步数据再拜访.");
                    builder.setNegativeButton("确定", new android.content.DialogInterface.OnClickListener() {           
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();               
                        }
                    });
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
                break;

            // 新增终端
            case R.id.visit_bt_addterm:
                fragment = new TermAddFragment();
                DbtLog.logUtils(TAG, "新增终端");
                break;

            // 终端详情
            case R.id.visit_bt_termdetail:
                intent = new Intent(getActivity(), TerminalDetailsFragment.class);
                DbtLog.logUtils(TAG, "终端详情");
                break;

            // 经销商拜访
            case R.id.visit_bt_agency:
                fragment = new AgencySelectFragment();
                DbtLog.logUtils(TAG, "经销商拜访");
                break;

            // 经销商库存
            case R.id.visit_bt_store:
                intent = new Intent(getActivity(), AgencyStorageActivity.class);
                DbtLog.logUtils(TAG, "经销商库存");
                break;

            // 产品展示
            case R.id.visit_bt_product:
                fragment = new ShowProductFragment();
                DbtLog.logUtils(TAG, "产品展示");

                break;

            // 数据同步
			case R.id.visit_bt_sync:
				DbtLog.logUtils(TAG, "数据同步");
				syncData();
 
                break;
            // 新增终端
            case R.id.visit_bt_addterm_list:
                fragment = new TermAddListFragment();
                DbtLog.logUtils(TAG, "新增终端列表");
                break;
                // 复制数据库
            case R.id.visit_bt_copydb:
            	//copydb();
            	copy2();
            	break;
            // 经销商开发	
            case R.id.visit_bt_addagency:
            	intent = new Intent(getActivity(), AgencyKFActivity.class);
                DbtLog.logUtils(TAG, "新增经销商");
            	break;
            // 所有图片上传	
            case R.id.visit_bt_upImage:
            	//intent = new Intent(getActivity(), DevelopAgency.class);
            	UploadDataService uploadDataService = new UploadDataService(getActivity(), null);
            	//uploadDataService.upload_visit_camera(false, "0");
            	//DbtLog.logUtils(TAG, "新增经销商");
            	break;
            // 终端进货台账
            case R.id.visit_bt_termtaizhang:
            	DbtLog.logUtils(TAG, "终端进货台账");
            	intent = new Intent(getActivity(), TzAgencyActivity.class);
            	break;
            default:
                break;
        }

        if (intent != null)
        {
            getActivity().startActivity(intent);
        }

        if (fragment != null)
        {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.visit_container, fragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }*/
    
	public void copy2() {
		try {
			int bytesum = 0;
			int byteread = 0;
			String sdcardPath = Environment.getExternalStorageDirectory() + "";
			String DbtPATH = sdcardPath + "/dbt/et.tsingtaopad";
			String BUGPATH = DbtPATH + "/log/";
			String path = "sdcard/";
			String photopath = Environment.getExternalStorageDirectory()+ ""+ "/dbt/et.tsingtaopad"+ "/photo/";
			File oldfile = new File(
					"/data/data/et.tsingtaopad/databases/FsaDBT.db");
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(
						"/data/data/et.tsingtaopad/databases/FsaDBT.db"); // 读入原文件
				FileOutputStream fs = new FileOutputStream(BUGPATH + "FsaDBT1.db");
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				Toast.makeText(getActivity(), "完成复制准备上传", Toast.LENGTH_SHORT)
						.show();
				
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}
	}
	
	/**
	 * 同步数据
	 */
	private void syncData() {
		//showNotifyDialog();
		
		// 如果网络可用
		if (NetStatusUtil.isNetValid(getActivity())) {
			// 根据后台标识   "0":需清除数据 ,"1":不需清除数据,直接同步
			if ("0".equals(ConstValues.loginSession.getIsDel())) {
				//弹窗是否删除之前所有数据
				showNotifyDialog();
			} else {
				Intent download = new Intent(getActivity(),
						DownLoadDataProgressActivity.class);
				startActivity(download);
			}
		} else {
			// 提示修改网络
			Builder builder = new Builder(getActivity());
			builder.setTitle("网络错误");
			builder.setMessage("请连接好网络再同步数据");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							getActivity()
									.startActivityForResult(
											new Intent(
													android.provider.Settings.ACTION_WIRELESS_SETTINGS),
											0);
						}
					}).create().show();
			builder.setCancelable(false); // 是否可以通过返回键 关闭
		}
		
	}

	/**
     * 弹窗显示需清除数据
     * 
     */
    public void showNotifyDialog(){
        
    	//提示删除数据
        Builder builder = new Builder(getActivity());
        builder.setTitle("初始化");
        builder.setMessage("初次登陆，您的账号需要初始化。");
        builder.setCancelable(false);
        //builder.setCanceledOnTouchOutside(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {

			@Override
            public void onClick(DialogInterface dialog, int which)
            {
            	// 缓冲界面
            	dialog1 = new DialogUtil().progressDialog(getActivity(), R.string.dialog_msg_delete);
                dialog1.setCancelable(false);
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
            	
            	// 网络请求 传递参数
            	HttpUtil httpUtil = new HttpUtil(60*1000);
				httpUtil.configResponseTextCharset("ISO-8859-1");
				
				// 
				StringBuffer buffer = new StringBuffer();
				//buffer.append("{userid:'").append(ConstValues.loginSession.getUserGongHao());
				buffer.append("{userid:'").append(PrefUtils.getString(getActivity(), "userGongHao", ""));
				buffer.append("', isdel:'").append("1").append("'}");
				
				// qingqiu
				httpUtil.send("opt_get_status", buffer.toString(),new RequestCallBack<String>() {
					public void onSuccess(ResponseInfo<String> responseInfo) {
						
						ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
						if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
							// 删除数据库表数据 然后直接同步
							// new DeleteTools().deleteDatabase(getActivity());
							
							// 删除数据库表数据 然后重启
							new DeleteTools().deleteDatabaseAll(getActivity());
							
							// 删除缓存数据
							new DataCleanManager().cleanSharedPreference(getActivity());
							
							// 删除bug文件夹
							String bugPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dbt/et.tsingtaopad" + "/bug/" ;
							FileUtil.deleteFile(new File(bugPath));
							
							// 删除log文件夹
							String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dbt/et.tsingtaopad" + "/log/" ;
							FileUtil.deleteFile(new File(logPath));
							
							// 重新启动本应用
							restartApplication();
							android.os.Process.killProcess(android.os.Process.myPid());
							
							// 关闭缓冲界面
							Message message = new Message();
							message.what = ConstValues.WAIT2;
							handler.sendMessage(message);
						}
					}

					@Override
					public void onFailure(HttpException error,
							String msg) {

					}
				});
            }
        }).create().show();
        
        //builder.setCancelable(false); // 是否可以通过返回键 关闭
        
        // 直接show();
        //builder.show();
    }
    
    /**
     * 重新启动本应用
     */
    private void restartApplication() {
    	final Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("et.tsingtaopad");
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    } 
    
    /**
	 * GridView适配器
	 * 
	 */
	class JieMianAdapter extends BaseAdapter {

		// item的个数
		@Override
		public int getCount() {
			return images.size();
		}

		// 根据位置获取对象
		@Override
		public Integer getItem(int position) {
			return images.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 初始化每一个item的布局
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getActivity(),
					R.layout.visit_gv_item, null);
			ImageView iv_visit = (ImageView) view.findViewById(R.id.iv_visit);
			View iv_visit_right = (View) view.findViewById(R.id.iv_visit_right);
			View iv_visit_below = (View) view.findViewById(R.id.iv_visit_below);
			
			// 根据不同位置设置分割线是否出现
			if((position+1)%3==0){//3
				iv_visit_right.setVisibility(view.GONE);
				iv_visit_below.setVisibility(view.VISIBLE);
			}else{// 1 2
				iv_visit_right.setVisibility(view.VISIBLE);
				iv_visit_below.setVisibility(view.VISIBLE);
			}

			iv_visit.setImageResource(images.get(position));
			//iv_visit.setBackgroundResource(images.get(position));
			return view;
		}
	}
	
	/**
	 * 巡店拜访
	 */
	private void shopVisitClick() {

		 Fragment fragment = null;
	     
	   //格式化日期
         String nowDate = DateUtil.formatDate(new Date(), DateUtil.DEFAULT_DATE_FORMAT);
         //获取同步更新日期
         String datasynTime = DownLoadDataService.getDatasynTime(getActivity());
         long daygap = 0;
         Date date1 = null;
         Date date2 = null;
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

         if (!datasynTime.equals("") && !datasynTime.equals(null))
         {
             GregorianCalendar cal1 = new GregorianCalendar();
             GregorianCalendar cal2 = new GregorianCalendar();

             try
             {
                 date1 = sdf.parse(nowDate);
                 date2 = sdf.parse(datasynTime);
             }
             catch (ParseException e)
             {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
             cal1.setTime(date1);
             cal2.setTime(date2);
             daygap = (cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000 * 3600 * 24);
         }
         //当大于5时才同步
         if (daygap >= 5)
         {
             fragment = null;

             Builder builder = new Builder(getActivity());
             builder.setTitle("温馨提示");
             builder.setMessage("今日未同步数据，请先同步数据再拜访.");
             builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
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
         else
         {
             fragment = new LineListFragment();
             //fragment = new TermListFragment();
             DbtLog.logUtils(TAG, "巡店拜访");
         }
         //每天必须同步
         /*if(nowDate.equals(datasynTime)){
             fragment = new LineListFragment();
             DbtLog.logUtils(TAG, "巡店拜访");
         }else{
             fragment=null;
             intent=null;

             AlertDialog.Builder builder = new Builder(getActivity());
             builder.setTitle("温馨提示");
             builder.setMessage("今日未同步数据，请先同步数据再拜访.");
             builder.setNegativeButton("确定", new android.content.DialogInterface.OnClickListener() {           
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();               
                 }
             });
             AlertDialog dialog=builder.create();
             dialog.show();
         }*/
         
         if (fragment != null) {
 			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
 			transaction.replace(R.id.visit_container, fragment);
 			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
 			transaction.addToBackStack(null);
 			transaction.commit();
 		}
	}
	
	// 查询终端表数量
	private long getTermnalNum(){
		
		DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
		SQLiteDatabase db = helper.getReadableDatabase();

		String querySql = "SELECT COUNT(*)  FROM MST_TERMINALINFO_M";
		Cursor cursor = db.rawQuery(querySql, null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}
}
