package et.tsingtaopad.visit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
import et.tsingtaopad.tools.DataCleanManager;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ZipUtil;
import et.tsingtaopad.visit.agencykf.AgencyKFActivity;
import et.tsingtaopad.visit.agencystorage.AgencyStorageActivity;
import et.tsingtaopad.visit.agencyvisit.AgencySelectFragment;
import et.tsingtaopad.visit.shopvisit.line.LineListFragment;
import et.tsingtaopad.visit.showproduct.ShowProductFragment;
import et.tsingtaopad.visit.syncdata.DownLoadDataProgressActivity;
import et.tsingtaopad.visit.syncdata.DownLoadDataService;
import et.tsingtaopad.visit.syncdata.UploadDataService;
import et.tsingtaopad.visit.termadd.TermAddFragment;
import et.tsingtaopad.visit.termadd.TermAddListFragment;
import et.tsingtaopad.visit.terminaldetails.TerminalDetailsFragment;
import et.tsingtaopad.visit.termtz.TzAgencyActivity;

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
public class VisitFragment extends BaseFragmentSupport implements OnClickListener
{
    String TAG = "VisitFragment";
    private Button shopVisitBt;
    private Button termAddBt;
    private Button termAddListBt;
    private Button termdetailBt;
    private Button agencyBt;
    private Button storeBt;
    private Button productBt;
    private Button syncBt;
    private Button copydb;
    private Button addagencyBt;
    private Button upImageBt;
    private Button termtaizhangBt;
    private AlertDialog dialog1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        DbtLog.logUtils(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.platform_visit, null);
        this.initView(view);
        this.initDate();

        return view;
    }
    
    Handler handler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case ConstValues.WAIT2:
				// 
				 if (dialog1 != null && dialog1.isShowing()) {
			            return ;
			        } else {
			            dialog1.dismiss();
			        }
				
				break;

			default:
				break;
			}
    	};
    };

    private void initView(View view)
    {
        DbtLog.logUtils(TAG, "initView");
        shopVisitBt = (Button) view.findViewById(R.id.visit_bt_shopvisit);
        termAddBt = (Button) view.findViewById(R.id.visit_bt_addterm);
        termAddListBt = (Button) view.findViewById(R.id.visit_bt_addterm_list);
        termdetailBt = (Button) view.findViewById(R.id.visit_bt_termdetail);
        agencyBt = (Button) view.findViewById(R.id.visit_bt_agency);
        storeBt = (Button) view.findViewById(R.id.visit_bt_store);
        productBt = (Button) view.findViewById(R.id.visit_bt_product);
        syncBt = (Button) view.findViewById(R.id.visit_bt_sync);
        copydb = (Button) view.findViewById(R.id.visit_bt_copydb);
        addagencyBt = (Button) view.findViewById(R.id.visit_bt_addagency);
        upImageBt = (Button) view.findViewById(R.id.visit_bt_upImage);
        termtaizhangBt = (Button) view.findViewById(R.id.visit_bt_termtaizhang);

        shopVisitBt.setOnClickListener(this);
        termAddBt.setOnClickListener(this);
        termdetailBt.setOnClickListener(this);
        agencyBt.setOnClickListener(this);
        storeBt.setOnClickListener(this);
        productBt.setOnClickListener(this);
        syncBt.setOnClickListener(this);
        copydb.setOnClickListener(this);
        termAddListBt.setOnClickListener(this);
        addagencyBt.setOnClickListener(this);
        upImageBt.setOnClickListener(this);
        termtaizhangBt.setOnClickListener(this);
    }

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

    @Override
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
                break;

            // 新增终端
            case R.id.visit_bt_addterm:
            	if(getTermnalNum()>0){
            		fragment = new TermAddFragment();
            	} else {
            		Toast.makeText(getActivity(), "基础信息不完整,请先同步数据", Toast.LENGTH_SHORT).show();
            	}
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
    }
    
	public void copy2() {
		try {
			int bytesum = 0;
			int byteread = 0;
			// 手机存储空间/dbt/et.tsingtaopad/log/
			String sdcardPath = Environment.getExternalStorageDirectory() + "";
			String DbtPATH = sdcardPath + "/dbt/et.tsingtaopad";
			String BUGPATH = DbtPATH + "/log/";
			// 内存卡根目录
			String path = "sdcard/";
			// 要复制的数据库文件
			File oldfile = new File("/data/data/et.tsingtaopad/databases/FsaDBT.db");
			if (oldfile.exists()) { // 文件存在时
				// 数据源
				InputStream inStream = new FileInputStream(
						"/data/data/et.tsingtaopad/databases/FsaDBT.db"); 
				// 目的地
				FileOutputStream fs = new FileOutputStream(BUGPATH + "FsaDBT1.db");
				// 进行复制
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
	 * 上传文件
	 */
	private void upFile(String src,String dest ) {
		// 压缩文件
		try {
			ZipUtil.zip(src, dest);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制数据库
	 */
	private void copydb() {
		File f = new File("/data/data/et.tsingtaopad/databases/FsaDBT.db"); // 比如
																			// "/data/data/com.hello/databases/test.db"

		 //String sdcardPath =Environment.getExternalStorageDirectory().getAbsolutePath();
		 String sdcardPath = Environment.getExternalStorageDirectory() + "";
         String DbtPATH = sdcardPath + "/dbt/et.tsingtaopad";
         String BUGPATH = DbtPATH + "/bug/";
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File o = new File(path, "DBT.db"); // sdcard上的目标地址
		//File o = new File(BUGPATH,"abc.db"); // sdcard上的目标地址

		if (f.exists()) {
			FileChannel outF;

			try {

				outF = new FileOutputStream(o).getChannel();

				new FileInputStream(f).getChannel().transferTo(0, f.length(),
						outF);

			} catch (FileNotFoundException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}

			Toast.makeText(getActivity(), "完成复制", Toast.LENGTH_SHORT).show();

		}

	}

	/**
	 * 同步数据
	 */
	private void syncData() {
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
							// 删除缓存数据
							new DeleteTools().deleteDatabase(getActivity());
							new DataCleanManager().cleanSharedPreference(getActivity());

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
