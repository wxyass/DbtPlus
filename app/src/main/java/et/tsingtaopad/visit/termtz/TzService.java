package et.tsingtaopad.visit.termtz;

import java.sql.SQLException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstTermLedgerInfoDao;
import et.tsingtaopad.db.tables.MstTermLedgerInfo;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.termtz.domain.MstTzTermInfo;
import et.tsingtaopad.visit.termtz.domain.TzGridAreaInfo;
import et.tsingtaopad.visit.termtz.domain.TzTermProInfo;
import et.tsingtaopad.visit.termtz.domain.TzUpTermData;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TzService.java</br>
 * 作者：ywm   </br>
 * 创建时间：2015-12-8</br>      
 * 功能描述: 终端台账逻辑</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TzService extends ShopVisitService {
    
    private final String TAG = "TzService";

    public TzService(Context context, Handler handler) {
        super(context, handler);
    }

    // 根据经销商主键删除记录
    public void deleteAllByAgencykey(String agencykey){
    	try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
            
            dao.deleteRecord(helper, agencykey);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端t台账表DAO对象失败", e);
        }
    }
    
    // 根据经销商主键终端主键产品主键,更改进货量
    public void toChangePurchase(String purchase, String agencykey,
			String terminalkey, String productkey){
    	try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
            
            dao.updataPurchase(helper, purchase, agencykey, terminalkey, productkey);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端t台账表DAO对象失败", e);
        }
    }
    
    //查询所有需要上传的记录
    public ArrayList<MstTermLedgerInfo> queryNeedUpAll(String padisconsistent,String terminalkey,String agencykey){
    	ArrayList<MstTermLedgerInfo> valueLst =new ArrayList<MstTermLedgerInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
        	valueLst = dao.getNeedUpAll(helper, padisconsistent,terminalkey,agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	
    	 return valueLst;
    }
    
    /**
     * 根据经销商主键终端主键产品主键  更新padisconsistent上传状态
     * 
     * @param padisconsistent  0需要上传  1不需要上传
     * @param agencykey
     * @param terminalkey
     * @param productkey
     */
    public void toChangePadisconsi(String padisconsistent,String agencykey,String terminalkey, String productkey){
    	try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
            
            dao.updataPadisConsis(helper, padisconsistent,agencykey, terminalkey, productkey);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端t台账表DAO对象失败", e);
        }
    }
    
    /**
     * 根据销商主键终端主键产品主键   更改上传成功状态yesup 
     * 
     * @param yesup  	   (0上传成功  1上传失败)
     * @param agencykey
     * @param terminalkey
     * @param productkey
     */
    public void toChangeYesUp(String yesup,String agencykey,String terminalkey, String productkey){
    	try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
            
            dao.updataYesUp(helper, yesup ,agencykey, terminalkey, productkey);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端t台账表DAO对象失败", e);
        }
    }
    
    // 根据经销商主键,查询所有终端数据(注意排序)
    public ArrayList<MstTzTermInfo> getTermAll(String agencykey){
    	ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
        	valueLst = dao.queryTermAll(helper, agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	 return valueLst;
    }
    
    // 根据终端主键,查询此终端下产品(注意排序)
    public ArrayList<TzTermProInfo> getTermProAll(String terminalkey,String agencykey){
    	ArrayList<TzTermProInfo> valueLst =new ArrayList<TzTermProInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
        	valueLst = dao.queryTermProAll(helper, terminalkey,agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	 return valueLst;
    }
    
    // 根据终端名字,查询终端
    public ArrayList<MstTzTermInfo> getTermsByName(String terminalname,String agencykey){
    	ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
    		valueLst = dao.queryTermByName(helper, terminalname,agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	return valueLst;
    }
    
    // 上传终端进货量数据到服务器
    public void upTermData(String padisconsistent,final AlertDialog quicklyDialog,String terminalkey,String agencykey,final onPurchaseSuccess purchaseSuccess){
    	ArrayList<MstTermLedgerInfo> valueLst =new ArrayList<MstTermLedgerInfo>();
    	valueLst = queryNeedUpAll(padisconsistent,terminalkey,agencykey);
    	ArrayList<TzUpTermData> tzUpTermDatas =new ArrayList<TzUpTermData>();
    	TzUpTermData tzUpTermData ;
    	// 遍历要上传的条目
    	for (MstTermLedgerInfo mstTermLedgerInfo : valueLst) {
    		tzUpTermData = new TzUpTermData();
    		
    		tzUpTermData.setAccountkey(FunUtil.getUUID());
    		//tzUpTermData.setUsergridkey(ConstValues.loginSession.getGridId());
    		tzUpTermData.setUsergridkey(PrefUtils.getString(context, "gridId", ""));
    		tzUpTermData.setTerminalkey(mstTermLedgerInfo.getTerminalkey());
    		
    		tzUpTermData.setAgencykey(mstTermLedgerInfo.getAgencykey());
    		tzUpTermData.setAgencyname(mstTermLedgerInfo.getAgencyname());
    		tzUpTermData.setAgencycode(mstTermLedgerInfo.getAgencycode());
    		
    		tzUpTermData.setProductkey(mstTermLedgerInfo.getProductkey());
    		tzUpTermData.setProname(mstTermLedgerInfo.getProname());
    		tzUpTermData.setProcode(mstTermLedgerInfo.getProcode());
    		tzUpTermData.setPurchasenum(mstTermLedgerInfo.getPurchase());
    		
    		//tzUpTermData.setCreateuser(ConstValues.loginSession.getUserCode());
    		tzUpTermData.setCreateuser(PrefUtils.getString(context, "userCode", ""));
    		tzUpTermData.setCreatedate(Long.toString(DateUtil.parse(
					DateUtil.getDateTimeStr(1), "yyyyMMdd HH:mm:ss").getTime()));
    		
    		tzUpTermData.setGridareaid(mstTermLedgerInfo.getAreaid());
    		tzUpTermData.setTergridkey(mstTermLedgerInfo.getGridkey());
    		tzUpTermData.setGridname(mstTermLedgerInfo.getGridname());
    		tzUpTermData.setRoutename(mstTermLedgerInfo.getRoutename());
    		String time = Long.toString(DateUtil.parse(mstTermLedgerInfo.getPurchasetime(), "yyyy-MM-dd").getTime());
    		tzUpTermData.setPurchasetime(time);
    		
    		tzUpTermDatas.add(tzUpTermData);
		}
    	
    	// 封装json
    	String json = JSON.toJSONString(tzUpTermDatas);
		// 发送请求,上传
		HttpUtil httpUtil = new HttpUtil(60 * 1000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		httpUtil.send("opt_save_agencyAccount", json, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				ResponseStructBean resObj = HttpUtil
						.parseRes(responseInfo.result);
				if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
					
					Toast.makeText(context, "终端进货量上传成功", Toast.LENGTH_SHORT).show();
					
					// 获取上传成功的图片主键数组
					
					String json = resObj.getResBody().getContent();
					if(json !=null && json.length()>0){
						JSONArray numberList = JSON.parseArray(json);
						for(int i=0; i< numberList.size(); i++){
							String pro = numberList.getString(i);
							TzUpTermData tzUpTermData = JSON.parseObject(pro, TzUpTermData.class);
							// 更改不需要上传
							toChangePadisconsi("1", tzUpTermData.getAgencykey(), tzUpTermData.getTerminalkey(), tzUpTermData.getProductkey());
							// 更改上传状态为成功
							toChangeYesUp("0", tzUpTermData.getAgencykey(), tzUpTermData.getTerminalkey(), tzUpTermData.getProductkey());

							// 更新上传标记
							purchaseSuccess.setTerminalTextColor();
						}
					}
					
					quicklyDialog.dismiss();
				} else {
					Toast.makeText(context, "网络异常E 上传失败 需手动再次确定上传", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(HttpException error, String errMsg) {
				Log.e(TAG, errMsg, error);
				Toast.makeText(context, "网络异常 上传失败 需手动再次确定上传", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 根据经销商主键,查询所以区域名称和定格名称,以定格主键分组
	 * 
	 * @param agencykey
	 * @return
	 */
	public ArrayList<TzGridAreaInfo> getAreaGridAll(String agencykey) {
		
		ArrayList<TzGridAreaInfo> valueLst =new ArrayList<TzGridAreaInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
    		valueLst = dao.queryAreaGridAll(helper,agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	return valueLst;
	}

	/**
	 * 根据区域名称定格名称,查询终端
	 * 
	 * @param areaname
	 * @param gridname
	 * @return
	 */
	public ArrayList<MstTzTermInfo> getTermByAreaGrid(String areaname,
			String gridname,String agencykey) {
		ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
    		valueLst = dao.queryTermByAreaGrid(helper, areaname,gridname,agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	return valueLst;
	}

	/**
	 * 根据记录下载时间,删除一条记录
	 * 
	 * @param downTime
	 */
	public void deleteNotToday(String downTime) {
		try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
            
            dao.deleteRecordByDowntime(helper, downTime);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端t台账表DAO对象失败", e);
        }
	}

	/**
	 * 根据定格名称  以及终端名称 模糊查询终端列表
	 * 
	 * @param areaname
	 * @param gridname
	 * @param termName
	 * @param agencykey
	 * @return
	 */
	public ArrayList<MstTzTermInfo> getTermByAreaGridAndname(String areaname,
			String gridname, String termName, String agencykey) {
		ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
    		valueLst = dao.queryTermByAreaGridAndname(helper, areaname,gridname,termName,agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	return valueLst;
	}
	
	/**
     * 根据经销商主键终端主键产品主键  更新Uploadtime选择上传时间
     * 
     * @param purchasetime  选择上传时间
     * @param agencykey
     * @param terminalkey
     * @param productkey
     */
    public void toChangePurchasetime(String purchasetime,String agencykey,String terminalkey, String productkey){
    	try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
            
            dao.updataPurchasetime(helper, purchasetime,agencykey, terminalkey, productkey);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端t台账表DAO对象失败", e);
        }
    }

	/**
	 * 通过区域定格名称 获取该定格下的路线
	 * 
	 * @param areaname
	 * @param gridname
	 * @param agencykey
	 * @return
	 */
	public ArrayList<String> getAllRouteByAreaGrid(String areaname,
			String gridname, String agencykey) {
		ArrayList<String> valueLst =new ArrayList<String>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
    		valueLst = dao.queryAllRouteByAreaGrid(helper, areaname,gridname,agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	return valueLst;
	}

	/**
	 * 通过区域定格名称及线路 获取终端
	 * 
	 * @param areaname
	 * @param gridname
	 * @param routename
	 * @param agencykey
	 * @return
	 */
	public ArrayList<MstTzTermInfo> getTermByAreaGridAndRoute(String areaname,
			String gridname, String routename, String agencykey) {
		
		ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
    		valueLst = dao.queryTermByAreaGridAndRoutename(helper, areaname,gridname,routename,agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	return valueLst;
	}

	/**
	 * 通过区域定格名称及线路及终端名称  获取终端(模糊查询)
	 * 
	 * @param areaname
	 * @param gridname
	 * @param termName
	 * @param routename
	 * @param agencykey
	 * @return
	 */
	public ArrayList<MstTzTermInfo> getTermByAreaGridAndnameAndRoutename(
			String areaname, String gridname, String termName, String routename,
			String agencykey) {
		ArrayList<MstTzTermInfo> valueLst =new ArrayList<MstTzTermInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
    		valueLst = dao.queryTermByAreaGridAndTermnameAndRoutename(helper, areaname,gridname,termName,routename,agencykey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	return valueLst;
	}
	
	// 根据终端主键和上传成功,查询此终端下产品(注意排序)
    public ArrayList<TzTermProInfo> getTermProByUpyes(String terminalkey,String agencykey,String upyes){
    	ArrayList<TzTermProInfo> valueLst =new ArrayList<TzTermProInfo>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
        	MstTermLedgerInfoDao dao = (MstTermLedgerInfoDao) helper.getTermLedgerInfoDao();
    		
        	valueLst = dao.queryTermProByUpyes(helper, terminalkey,agencykey,"0");
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	 return valueLst;
    }
    
   
    public interface onPurchaseSuccess {
        
        void setTerminalTextColor();
    }
    
    onPurchaseSuccess purchaseSuccess= new onPurchaseSuccess(){

		@Override
		public void setTerminalTextColor() {
			
		}
    };
}
