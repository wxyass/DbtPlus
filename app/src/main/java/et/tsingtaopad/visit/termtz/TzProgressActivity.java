package et.tsingtaopad.visit.termtz;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
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
import et.tsingtaopad.visit.syncdata.DownLoadDataService;
import et.tsingtaopad.visit.termtz.domain.TerminalLoad;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DownLoadDataProgressActivity.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2014年2月13日</br>      
 * 功能描述: 数据下载进度条</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */

public class TzProgressActivity extends BaseActivity {
	String TAG="TzProgressActivity";
	
	private long defaultDelayMillis = 1000;
    public static final int SAVE_Success = 105;
    public static final int SAVET_Failure = 125;
    public static final int SAVE_PROGRESS = 115;
    public static final int SAVE_PROGRESS_ZERO = 116;
    private ProgressBar download_progressBar;
    private TextView download_progressTV;
    private TextView download_percentTV;
    //private int progressMax = 44;//进度总数 按模块划分计算出来的数量/比如 联网 ，更新1表， 更新2表 可以通过计算DownLoadDataService 调用了sendProgressMessage多少次计算出来
    
    private String agencykey;// 经销商主键
    // 根据经销商从后台获取的终端列表 
 	List<MstTermLedgerInfo> termLedgerInfostosave = new ArrayList<MstTermLedgerInfo>();
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
  
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (TzProgressActivity.this.isFinishing()) {
                return;
            }
            switch (msg.what) {
            //同步结果比如一些错误提示
            case SAVET_Failure:// 同步出错：
                // 初始化静态变量
                /*new InitConstValues(TzProgressActivity.this).start();
                Intent prompt= new Intent();
                prompt.putExtra("prompt_content", (String) msg.obj);
                //prompt.setClass(getApplicationContext(), NetworkErrorPromptActivity.class);
                startActivity(prompt);
                TzProgressActivity.this.finish();*/
                break;
                //同步进度
            case SAVE_Success:    //同步成功结束
                 /*new InitConstValues(TzProgressActivity.this).start();
                  Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();*/
                  
            	/*Bundle data1 = msg.getData();
            	String progress1 = data1.getString("data");
                download_progressTV.setText(progress1);*/
            	
                  /*Toast.makeText(getApplicationContext(), "经销商下终端同步成功", 0).show();
					
					Intent intent = new Intent(getApplicationContext(),TzTermActivity.class);
					intent.putExtra("agencykey", agencykey);
					startActivity(intent);
                  
                  
                  TzProgressActivity.this.finish();*/
                break;
            case SAVE_PROGRESS:
                Bundle data = msg.getData();
                int progress = data.getInt("progress");
                int result = data.getInt("result");
                download_progressBar.setProgress(progress);
                download_progressTV.setText("正在努力加载...");
                //计算百分比
                float gress = progress;
                NumberFormat numberFormat = NumberFormat.getPercentInstance();
                numberFormat.setMinimumFractionDigits(0);
                //String percent = numberFormat.format(gress / progressMax);
                String percent = numberFormat.format(gress / result);
                Log.d(TAG, "percent:"+percent);
                download_percentTV.setText(percent);
                if("100%".equals(percent)){
                	 Toast.makeText(getApplicationContext(), "经销商下终端同步成功", Toast.LENGTH_SHORT).show();
 					
 					Intent intent1 = new Intent(getApplicationContext(),TzTermActivity.class);
 					intent1.putExtra("agencykey", agencykey);
 					startActivity(intent1);
                    
                   
                   TzProgressActivity.this.finish();
                }
                break;
            case SAVE_PROGRESS_ZERO:
            	
            	Toast.makeText(getApplicationContext(), "该经销商不为此定格供货", Toast.LENGTH_SHORT).show();
        		
        		Intent intent1 = new Intent(getApplicationContext(),TzTermActivity.class);
        		intent1.putExtra("agencykey", agencykey);
        		startActivity(intent1);
        		
        		
        		TzProgressActivity.this.finish();
            	break;
            default:
                break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloadprogress); 
        download_progressBar = (ProgressBar) findViewById(R.id.download_progressBar);
        download_progressTV = (TextView) findViewById(R.id.download_progressTV);
        download_percentTV = (TextView) findViewById(R.id.download_percentTV);
        
        download_progressTV.setText("正在同步经销商数据...");
        //download_progressBar.setMax(progressMax);
        //下载数据
 
        //DownLoadDataService down = new DownLoadDataService(this, handler);
        //三个线程进行下载
        //down.asyndatas();
      
//        down.synDatas();
        
     // 获取跳转过来携带的数据
     		agencykey = getIntent().getStringExtra("agencykey");
     		termLedgerInfostosave.clear();
     		
     		loadTerminals(agencykey,0,3000);
     		
     		/*new Thread(new Runnable() {
				
				@Override
				public void run() {
					loadTerminals(agencykey,0,3000);
					
				}
			}).start();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "下载数据中", Toast.LENGTH_LONG).show();
    }
    
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
					
					if(termLedgerInfostosave.size()>=2900){
						download_progressTV.setText("数据太多,请稍后...");
					}
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
						
						/*Message msg = new Message();
		        		Bundle bundler = new Bundle();
		        		bundler.putString("data", "正在处理下载数据");
		        		//bundler.putInt("result", datas.size());
	        		
		                msg.setData(bundler);
		                msg.what = TzProgressActivity.SAVE_Success;
		        		handler.sendMessageDelayed(msg, 0);*/	
						
						
						String qureyTime1 = DateUtil.getDateTimeStr(0);//0 20151208
						String agencykey1 = agencykey;
						PrefUtils.putString(TzProgressActivity.this, agencykey1+qureyTime1, qureyTime1);
						
						// 删除不是今天下载的记录
						//final String qureyTime = DateUtil.getDateTimeStr(0);//1 20151208 11:05:48
						new TzService(getApplicationContext(), null).deleteNotToday(qureyTime1);
						// 删除属于自己的经销商的终端记录 // 更新时需要把自己经销商下的终端先删除 // 注释掉是因为删除不是今天的已经把自己下的终端删除了
						new TzService(getApplicationContext(), null).deleteAllByAgencykey(agencykey1);
						
						final DownLoadDataService loadDataService = new DownLoadDataService(getApplicationContext(), null);
						 
						 // 更新表
						 /*try {
							saveData(loadDataService.mstTermLedgerInfoDao, termLedgerInfostosave);
						 } catch (SQLException e) {
							e.printStackTrace();
						}*/
						 
						 new Thread(new Runnable() {
								
								@Override
								public void run() {
									try {
										saveData(loadDataService.mstTermLedgerInfoDao, termLedgerInfostosave);
									 } catch (SQLException e) {
										e.printStackTrace();
									}
									
								}
							}).start();
						 
						
						/*Toast.makeText(getApplicationContext(), "经销商下终端同步成功", 0).show();
						
						Intent intent = new Intent(getApplicationContext(),TzTermActivity.class);
						intent.putExtra("agencykey", agencykey);
						startActivity(intent);*/
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
			for (int i = 1; i <= datas.size(); i++) {
				T data = datas.get(i-1);
				setPadisconsistentAndTermledgerkey(data);
					try {
						dao.create(data);
		                
						if(i%1000==0||i==datas.size()){
							Message msg = new Message();
			        		Bundle bundler = new Bundle();
			        		bundler.putInt("progress", i);
			        		bundler.putInt("result", datas.size());
			        		
			                msg.setData(bundler);
			                msg.what = TzProgressActivity.SAVE_PROGRESS;
			        		handler.sendMessageDelayed(msg, defaultDelayMillis);
			        		
			                /*if(i==datas.size()){
			                	msg.what = TzProgressActivity.SAVE_Success;
				        		handler.sendMessageDelayed(msg, 5000);
			                }else{
			                	msg.what = TzProgressActivity.SAVE_PROGRESS;
				        		handler.sendMessageDelayed(msg, defaultDelayMillis);
			                }*/
						}
		                
		        		
		                
					} catch (Exception e) {
						DbtLog.write(e.getMessage());
						e.printStackTrace();
				}
			}
			
			
            
            /*Message msg = new Message();
    		Bundle bundler = new Bundle();
    		bundler.putInt("result", 100);
    		
            msg.setData(bundler);
    		msg.what = TzProgressActivity.SAVE_Success;
    		handler.sendMessageDelayed(msg, 0);*/
    		
		} else {
			handler.sendEmptyMessage(TzProgressActivity.SAVE_PROGRESS_ZERO);
			
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
