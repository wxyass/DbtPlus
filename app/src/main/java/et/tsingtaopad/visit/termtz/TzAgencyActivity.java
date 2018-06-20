package et.tsingtaopad.visit.termtz;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.j256.ormlite.dao.Dao;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstTermLedgerInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PinYin4jUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.agencyvisit.AgencyvisitService;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;
import et.tsingtaopad.visit.syncdata.DownLoadDataService;
import et.tsingtaopad.visit.termtz.domain.TerminalLoad;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TzActivity.java</br>
 * 作者：ywm   </br>
 * 创建时间：2015-11-30</br>      
 * 功能描述: 终端进货台账_经销商列表</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TzAgencyActivity extends BaseActivity 
                        implements OnClickListener {
	
	private final String TAG = "TermTZActivity";
	
	public static final int SAVE_Success = 1001;
	private long defaultDelayMillis = 1000;
	
	private AgencyvisitService service;

	private List<AgencySelectStc> selectLst;
	private int temp = -1;

	private Button banner2_back_bt;// 返回
	private TextView banner2_title_tv;// 界面标题
	private TextView gridnameTv;// 定格
	private ListView termtzLst;// 经销商列表

	private TzAgencyAdapter termTzAgencyAdapter;
	
	private int currentPosition;// 当前条目

	private AlertDialog dialog;
	
	private AlertDialog dialog1;
	
	private TextView msgTv;
	
	//private String agencykey;
    
	@Override
	protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.termtz_agencylist);
		this.initView();
        this.initData();
    }
	
	 /**
     *  初始化界面组件
     */
    private void initView() {
    	// 绑定页面组件
    	banner2_back_bt=(Button)findViewById(R.id.banner_navigation_bt_back);
    	banner2_title_tv = (TextView) findViewById(R.id.banner_navigation_tv_title);
    	
    	gridnameTv = (TextView) findViewById(R.id.termtz_tv_gridname);
    	//termtzSearchEt = (EditText) findViewById(R.id.termtz_et_search);
    	//searchBt = (Button) findViewById(R.id.termtz_bt_search);
    	termtzLst = (ListView) findViewById(R.id.termtz_lv);
    	
		RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
		RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
		backRl.setOnClickListener(this);
		confirmRl.setOnClickListener(this);
		
    	// 绑定点击事件
    	banner2_back_bt.setOnClickListener(this);
    	//searchBt.setOnClickListener(this);
    	
    }
    
    /**
     * 初始化界面数据
     */
	private void initData() {
		
		service = new AgencyvisitService(this);
		// 获取拜访经销商数据  经销商列表初始化   查数据库
		selectLst = service.agencySelectLstQuery();

		// 页面显示数据初始化
		banner2_title_tv.setText(R.string.termtz_details);
		//gridnameTv.setText(ConstValues.loginSession.getGridName());
		gridnameTv.setText(PrefUtils.getString(getApplicationContext(), "gridName", ""));

		termTzAgencyAdapter = new TzAgencyAdapter(this, selectLst);
		termtzLst.setAdapter(termTzAgencyAdapter);
		
		// 经销商条目点击监听
		termtzLst.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// 2秒内多次点击不予执行 防止按钮快速重复单击
				if (ViewUtil.isDoubleClick(view.getId(),2000)) return;
				// 改变选中条目的背景颜色
				currentPosition = position;
				termTzAgencyAdapter.setSeletor(position);
				termTzAgencyAdapter.notifyDataSetChanged();
				
				// 弹窗
				String agencyName = selectLst.get(currentPosition).getAgencyName();
				String agencykey = selectLst.get(currentPosition).getAgencyKey();
				confirmquery(agencyName,agencykey);
			}
		});

	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			
	    // 返回
		case R.id.banner_navigation_rl_back:
		case R.id.banner_navigation_bt_back:
		    this.getFragmentManager().popBackStack();
			this.finish();
			break;
		default:
			break;
		}
	}
	
	private long startTime;
	/**
	 * 确定同步经销商数据
	 * 
	 * @param agencyName 经销商名称
	 */
	private void confirmquery(String agencyName,final String agencykey) {

		// 如果不是查看操作，返回需再次确定
			DbtLog.logUtils(TAG, "确定同步经销商数据");
			View view = LayoutInflater.from(this).inflate(
					R.layout.agency_tz_dialog, null);
			TextView title = (TextView) view
					.findViewById(R.id.agencyvisit_tv_over_title);
			TextView msg = (TextView) view
					.findViewById(R.id.agencyvisit_tv_over_msg);
			ImageView sure = (ImageView) view
					.findViewById(R.id.agencyvisit_bt_over_sure);
			ImageView cancle = (ImageView) view
					.findViewById(R.id.agencyvisit_bt_over_quxiao);
			ImageView gengxin = (ImageView) view
					.findViewById(R.id.agencyvisit_bt_over_gengxin);
			title.setText(R.string.termtz_sure);
			msg.setText("确定查询 "+agencyName+" 下的所有终端");
			dialog = new AlertDialog.Builder(this).setCancelable(false).create();
			dialog.setView(view, 0, 0, 0, 0);
			sure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 通过点击经销商打开所有该经销商下终端列表界面
					termTzAgencyAdapter.setSeletor(-1);// 条目背景颜色恢复默认
					termTzAgencyAdapter.notifyDataSetChanged();
					dialog.dismiss();
					
					// 缓冲界面
					
	            	/*dialog1 = new DialogUtil().progressDialog(TzAgencyActivity.this, R.string.dialog_msg_sync);
	                dialog1.setCancelable(false);
	                dialog1.setCanceledOnTouchOutside(false);
	                dialog1.show();*/
	            	
	                
	                
	                /*
	             
	             // 下载终端列表--------------------------
					StringBuffer buffer = new StringBuffer();
					buffer.append("{agencykey:'").append(agencykey).append("'}");
					
					// 请求网络
					HttpUtil httpUtil = new HttpUtil(60 * 1000);
					httpUtil.configResponseTextCharset("ISO-8859-1");
					httpUtil.send("opt_get_agencysupply", buffer.toString(), new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {

							ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
							if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
								
								// 删除属于自己的经销商的终端记录
								String agencykey1 = agencykey;
								new TzService(getApplicationContext(), null).deleteAllByAgencykey(agencykey1);
								// 删除不是今天下载的记录
								final String qureyTime = DateUtil.getDateTimeStr(0);//1 20151208 11:05:48
								new TzService(getApplicationContext(), null).deleteNotToday(qureyTime);
								
								
								// 重新插入经销商新的记录
								String json = resObj.getResBody().getContent();
								List<MstTermLedgerInfo> termLedgerInfos = null;
								termLedgerInfos = JsonUtil.parseList(json, MstTermLedgerInfo.class);
								
								DownLoadDataService loadDataService = new DownLoadDataService(getApplicationContext(), null);
								 
								 // 更新表
								 try {
									saveData(loadDataService.mstTermLedgerInfoDao, termLedgerInfos);
								
								 } catch (SQLException e) {
									e.printStackTrace();
								}
								
								Toast.makeText(getApplicationContext(), "经销商下终端同步成功", 0).show();
								
								Intent intent = new Intent(getApplicationContext(),TzTermActivity.class);
								intent.putExtra("agencykey", agencykey);
								startActivity(intent);
								dialog1.dismiss();
							} else {
								
								Toast.makeText(getApplicationContext(), "网络异常E 经销商下终端同步失败", 0).show();
								dialog1.dismiss();
								
							}
						}

						@Override
						public void onFailure(HttpException error, String errMsg) {
							Log.e(TAG, errMsg, error);
							// 网络原因登录失败后，用离开方式登录
							Toast.makeText(getApplicationContext(), "网络异常 经销商下终端同步失败", 0).show();
							dialog1.dismiss();
						}
					});
					// 丄下载终端列表--------------------------
	               
					 */
	                // 先将经销商下的终端清空(内存中数据清空) 
	                termLedgerInfostosave.clear();
	                
					// 查询当前时间  20151208 
					String qureyTime = DateUtil.getDateTimeStr(0);//0 20151208 20170111
					String querykey = agencykey+qureyTime;// 1-62AHD720170111
					// 当天只查询一次
					String value = PrefUtils.getString(getApplicationContext(), agencykey+qureyTime, "0");
					//if(qureyTime.equals(value)||"0".equals(value)){
					if(qureyTime.equals(value)){
						PrefUtils.putString(TzAgencyActivity.this, querykey, qureyTime);
						// 打开终端列表
						Intent intent = new Intent(getApplicationContext(),TzTermActivity.class);
						intent.putExtra("agencykey", agencykey);
						// 打开终端列表
						startActivity(intent);
						//dialog1.dismiss();
					}else{
		                
						//int count =1;
						// 下载终端列表
						//loadTerminals(agencykey,0,3000);
						Intent tzprogressactivity = new Intent(getApplicationContext(),TzProgressActivity.class);
		                tzprogressactivity.putExtra("agencykey", agencykey);
						startActivity(tzprogressactivity);
					
					
					// 丄下载终端列表--------------------------
					}
					
					
					 
					
					
					
					
				}

				
			});
			cancle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			
			// 更新
			gengxin.setOnClickListener(new OnClickListener() {
				

				

				@Override
				public void onClick(View v) {

					// 通过点击经销商打开所有该经销商下终端列表界面
					termTzAgencyAdapter.setSeletor(-1);// 条目背景颜色恢复默认
					termTzAgencyAdapter.notifyDataSetChanged();
					dialog.dismiss();
					
					
	             // 缓冲界面
					/*new Thread(new Runnable(){
						@Override
						public void run() {
						}
					}).start();*/
					
					/*dialog1 = new DialogUtil().progressDialog(TzAgencyActivity.this, R.string.dialog_msg_sync);
	                dialog1.setCancelable(false);
	                dialog1.setCanceledOnTouchOutside(false);
	                dialog1.show();*/
	        
	                
	                Intent tzprogressactivity = new Intent(getApplicationContext(),TzProgressActivity.class);
	                tzprogressactivity.putExtra("agencykey", agencykey);
					startActivity(tzprogressactivity);
					
	                
	                /*
	                dialog1 = new AlertDialog.Builder(TzAgencyActivity.this).setCancelable(false).create();
	                View view = TzAgencyActivity.this.getLayoutInflater().inflate(R.layout.login_progress,null);
	                msgTv = (TextView)view.findViewById(R.id.textView1);
	                msgTv.setText(getApplicationContext().getString(R.string.dialog_msg_sync, ""));
	                //dialog1.setCancelable(false);
		            dialog1.setCanceledOnTouchOutside(false);
	                dialog1.setView(view, 0, 0, 0, 0);
	                dialog1.show();
	                */
	                
	                
	                // 先将经销商下的终端清空( 内存数据清空) 
	                //termLedgerInfostosave.clear();
	                //startTime = new Date().getTime();
	                // 同步终端
	                //loadTerminals(agencykey,0,3000);
					
					/*
					// 下载终端列表--------------------------
					StringBuffer buffer = new StringBuffer();
					buffer.append("{agencykey:'").append(agencykey).append("'}");
					
					// 请求网络
					HttpUtil httpUtil = new HttpUtil(60 * 1000);
					httpUtil.configResponseTextCharset("ISO-8859-1");
					httpUtil.send("opt_get_agencysupply", buffer.toString(), new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {

							ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
							if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
								String qureyTime1 = DateUtil.getDateTimeStr(0);//0 20151208
								String agencykey1 = agencykey;
								PrefUtils.putString(TzAgencyActivity.this, agencykey1+qureyTime1, qureyTime1);
								
								// 删除属于自己的经销商的终端记录
								new TzService(getApplicationContext(), null).deleteAllByAgencykey(agencykey1);
								// 删除不是今天下载的记录
								//final String qureyTime = DateUtil.getDateTimeStr(0);//1 20151208 11:05:48
								new TzService(getApplicationContext(), null).deleteNotToday(qureyTime1);
								
								
								// 重新插入经销商新的记录
								String json = resObj.getResBody().getContent();
								//FileUtil.savFileFike(json, "termLedgerInfos");
								List<MstTermLedgerInfo> termLedgerInfos = null;
								termLedgerInfos = JsonUtil.parseList(json, MstTermLedgerInfo.class);
								
								DownLoadDataService loadDataService = new DownLoadDataService(getApplicationContext(), null);
								 
								 // 更新表
								 try {
									long time1 = new Date().getTime() - startTime;
									saveData(loadDataService.mstTermLedgerInfoDao, termLedgerInfos);
								
									long time2 = new Date().getTime() - startTime;
									long time3 = new Date().getTime() - time1;
									DbtLog.write("====下载到写入终端进货台账数据time2:" + time2);
									DbtLog.write("====写入终端进货台账数据time3:" + time3);
								 } catch (SQLException e) {
									e.printStackTrace();
								}
								
								Toast.makeText(getApplicationContext(), "经销商下终端同步成功", 0).show();
								
								Intent intent = new Intent(getApplicationContext(),TzTermActivity.class);
								intent.putExtra("agencykey", agencykey);
								startActivity(intent);
								dialog1.dismiss();
							} else {
								
								Toast.makeText(getApplicationContext(), "网络异常E 经销商下终端同步失败", 0).show();
								dialog1.dismiss();
								
							}
						}

						@Override
						public void onFailure(HttpException error, String errMsg) {
							Log.e(TAG, errMsg, error);
							// 网络原因登录失败后，用离开方式登录
							Toast.makeText(getApplicationContext(), "网络异常 经销商下终端同步失败", 0).show();
							dialog1.dismiss();
						}
					});
					// 丄下载终端列表--------------------------
					 
					 */
					}
			});
			dialog.show();
	}
	
	// 根据经销商从后台获取的终端列表 
	List<MstTermLedgerInfo> termLedgerInfostosave = new ArrayList<MstTermLedgerInfo>();
	
	/** 
	 * 下载经销商下的终端
	 * 
	 * @param agencykey
	 * @param startraw 第一条
	 * @param endraw	最后一条
	 */
	private void loadTerminals(final String agencykey, int startraw,int endraw) {
		
		// 下载终端列表--------------------------
		StringBuffer buffer = new StringBuffer();
		//buffer.append("{agencykey:'").append(agencykey).append("'}");
		
		
		buffer.append("{agencykey:'").append(agencykey);
		buffer.append("', startrow:").append(startraw);
		buffer.append(", endrow:").append(endraw).append("}");
		
		// 请求网络
		HttpUtil httpUtil = new HttpUtil(60 * 1000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		httpUtil.send("opt_get_agencysupply", buffer.toString(), new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
				if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
					
					// 重新插入经销商新的记录
					String json = resObj.getResBody().getContent();
					//FileUtil.writeTxt(json,FileUtil.getSDPath()+"/agencysupply1.txt");//上传巡店拜访的json
					List<MstTermLedgerInfo> termLedgerInfos = null;
					//termLedgerInfos = JsonUtil.parseList(json, MstTermLedgerInfo.class);
					TerminalLoad parseJson = JsonUtil.parseJson(json, TerminalLoad.class);
					termLedgerInfos = JsonUtil.parseList(parseJson.getData(), MstTermLedgerInfo.class);
					termLedgerInfostosave.addAll(termLedgerInfos);
					
					// 如果返回终端条数大于0 说明还要继续请求, 如果条数<=0 说明后台已经没有数据了 跳转TzTermActivity
					if (termLedgerInfos.size() > 0) {
						loadTerminals(agencykey, parseJson.getStartrow()+3000, parseJson.getEndrow()+3000);
						
					}else{
						
						String qureyTime1 = DateUtil.getDateTimeStr(0);//0 20151208
						String agencykey1 = agencykey;
						PrefUtils.putString(TzAgencyActivity.this, agencykey1+qureyTime1, qureyTime1);
						
						// 删除不是今天下载的记录
						//final String qureyTime = DateUtil.getDateTimeStr(0);//1 20151208 11:05:48
						new TzService(getApplicationContext(), null).deleteNotToday(qureyTime1);
						// 删除属于自己的经销商的终端记录 // 更新时需要把自己经销商下的终端先删除 // 注释掉是因为删除不是今天的已经把自己下的终端删除了
						new TzService(getApplicationContext(), null).deleteAllByAgencykey(agencykey1);
						
						DownLoadDataService loadDataService = new DownLoadDataService(getApplicationContext(), null);
						 
						 // 更新表
						 try {
							long startTime1 = new Date().getTime();
							//saveData(loadDataService.mstTermLedgerInfoDao, termLedgerInfostosave);
							saveData(loadDataService.mstTermLedgerInfoDao, termLedgerInfostosave);
							long subTime = new Date().getTime() - startTime;
							long subTime1 = new Date().getTime() - startTime1;
							DbtLog.write("====下载到写入终端进货台账数据:" + subTime);
							DbtLog.write("====写入终端进货台账数据:" + subTime1);
						
						 } catch (SQLException e) {
							e.printStackTrace();
						}
						
						Toast.makeText(getApplicationContext(), "经销商下终端同步成功", Toast.LENGTH_SHORT).show();
						
						Intent intent = new Intent(getApplicationContext(),TzTermActivity.class);
						intent.putExtra("agencykey", agencykey);
						startActivity(intent);
						//dialog1.dismiss();
					}
					
				} else {
					
					Toast.makeText(getApplicationContext(), "网络异常E 经销商下终端同步失败", Toast.LENGTH_SHORT).show();
					//dialog1.dismiss();
					
				}
			}

			@Override
			public void onFailure(HttpException error, String errMsg) {
				Log.e(TAG, errMsg, error);
				// 网络原因登录失败后，用离开方式登录
				Toast.makeText(getApplicationContext(), "网络异常 经销商下终端同步失败", Toast.LENGTH_SHORT).show();
				//dialog1.dismiss();
			}
		});
	}
	
	/**
	 * 保存经销商终端数据到数据库 MST_TERMLEDGER_INFO
	 * 
	 * @param dao
	 * @param datas
	 * @throws SQLException
	 */
	public <T> void saveData(Dao<T, String> dao, List<T> datas)
			throws SQLException {

		if (!CheckUtil.IsEmpty(datas)) {
			Log.i("updateData", "更新 " + dao.getDataClass().getName()
					+ " size :" + datas.size());
			for (int i = 0; i < datas.size(); i++) {
				T data = datas.get(i);
				setPadisconsistentAndTermledgerkey(data);
					try {
						dao.create(data);
						
					} catch (Exception e) {
						DbtLog.write(e.getMessage());
						e.printStackTrace();
				}
			}
		} else {
			String listStatus = datas == null ? "传入对象为：null" : "数据size = 0";
			Log.i("updateData", "更新list " + dao.getDataClass().getName() + "为："
					+ listStatus);
		}
	}
	
	private <T> void setPadisconsistentAndTermledgerkey(T data) {
		// 反射将 padisconsistent , uploadFlag;
		Class<? extends Object> clazz = data.getClass();
		// 所有记录设置上传状态为不上传
		try {
			Method padisconsistentMethod = data.getClass().getMethod(
					"setPadisconsistent", String.class);
			padisconsistentMethod.invoke(data, ConstValues.FLAG_0);

		} catch (Exception e) {
			// e.printStackTrace();
		}
		// 为什么分开写？ 不写在同一个try 里面 因为 setPadisconsistent
		// 方法不存在就抛异常跳出去了而setUploadFlag可能存在的造成改方法无法执行
		
		// 终端进货台账主键为UUID
		try {
			Method padisconsistentMethod = data.getClass().getMethod(
					"setTermledgerkey", String.class);
			padisconsistentMethod.invoke(data, FunUtil.getUUID());

		} catch (Exception e) {
			// e.printStackTrace();
		}
		
		// 所有记录设置终端名称首字母字段
		
		try {
			
			MstTermLedgerInfo mstTermLedgerInfo = (MstTermLedgerInfo)data;
			String firstzm = PinYin4jUtil.converterToFirstSpell(mstTermLedgerInfo.getTerminalname());
			Method padisconsistentMethod = data.getClass().getMethod(
					"setFirstzm", String.class);
			padisconsistentMethod.invoke(data, firstzm);
			
		} catch (Exception e) {
			// e.printStackTrace();
		}
		
		// 所有记录设置下载时间为当前时间
		try {
			String qureyTime1 = DateUtil.getDateTimeStr(0);//0 20151208
			
			Method padisconsistentMethod = data.getClass().getMethod(
					"setDowndate", String.class);
			padisconsistentMethod.invoke(data, qureyTime1);
			
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
	
}
