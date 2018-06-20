package et.tsingtaopad.operation.working;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstSynckvM;
import et.tsingtaopad.db.tables.MstWorksummaryInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.syncdata.UploadDataService;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：WorkingService.java</br> 作者：@ray </br>
 * 创建时间：2013-11-26</br> 功能描述: 日工作推进逻辑</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
public class WorkingService {
	private final String TAG = "WorkingService";
	private Context context;
	private String content;
	private Handler handler;
	private int result;
	private UploadDataService service;
	private MstSynckvM mstSynckvM = new MstSynckvM();

	
	public WorkingService(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	/**
	 * 查询日工作推进报表
	 * 
	 * @param today
	 *            今天
	 * @param last
	 *            上一工作日
	 * @return
	 */
	public String searchWeb(String today, String last) {
		return PropertiesUtil.getProperties("platform_web").subSequence(0,
				PropertiesUtil.getProperties("platform_web").length() - 1)
				+ "_2/bs/business/forms/BusinessForms!employeeDayWrokPad.do?model.businessFormsStc.visitDate="
				+ today
				+ "&model.businessFormsStc.strDate="
				+ last
				+ "&model.businessFormsStc.gridId="
				//+ ConstValues.loginSession.getGridId();
				+ PrefUtils.getString(context, "gridId", "");
	}

	/**
	 * 查询日工作总结
	 * 
	 * @param today
	 *            今天
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public String searchSummary(String today, String userId) {

		// 如果本地表没有 去服务器端获取 如服务器端也没有 则显示暂无数据
		try {
			Dao<MstWorksummaryInfo, String> mstWorksummaryInfoDao = DatabaseHelper.getHelper(context).getMstWorksummaryInfoDao();
			QueryBuilder<MstWorksummaryInfo, String> queryBuilder = mstWorksummaryInfoDao.queryBuilder();
			queryBuilder.where().eq("startdate", today);
			List<MstWorksummaryInfo> query = queryBuilder.query();
			mstSynckvM = ConstValues.kvMap.get("MST_WORKSUMMARY_INFO");
			
			String updateTime = mstSynckvM.getUpdatetime();
			String syncDay = mstSynckvM.getSyncDay();
			if(updateTime==null||syncDay==null){//如果为空，请求网络
				result =-1;
			}else{
				// 比较时间大小
				result = today.compareTo(DateUtil.formatDate(
						1,
						DateUtil.formatDate(
								DateUtil.addDays(DateUtil.parse(updateTime),
										-(Integer.parseInt(syncDay))), "yyyy-MM-dd")));
				// 选择日期>KV表更新时间
			}
			
			
//			result = -3;
			if (result > 0) {
				ViewUtil.sendMsg(context, R.string.working_msg1);
				content = R.string.working_msg2 + "";
				// 选择日期 在KV表更新时间范围内
			} 
			else if (result <= 0 && result >= Integer.parseInt(mstSynckvM.getSyncDay())) {
				content = query.get(0).getWscontent();
			}
			else {

				StringBuffer buffer = new StringBuffer();
				buffer.append("{userId:'").append(userId);
				buffer.append("', startDate:'").append(today).append("'}");
				Log.d("tag","request-->"+buffer);
				HttpUtil httpUtil = new HttpUtil(600000);
				httpUtil.configResponseTextCharset("ISO-8859-1");
				httpUtil.send("opt_get_worksummary", buffer.toString(), new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
						if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
						
							Log.e("tag", resObj.getResBody().getContent().toString());
							
							List<MstWorksummaryInfo> list = JsonUtil.parseList(
							        resObj.getResBody().getContent(),MstWorksummaryInfo.class);
							if(list.size()>0){
								content = list.get(0).getWscontent();
							}else{
								content = "";
							}
								
							Message msg = handler.obtainMessage();
							msg.what = ConstValues.WAIT2;
							if(content==null){
								msg.obj = "";
							}else{
								msg.obj = content;
							}
							
							handler.sendMessage(msg);
							
						} else {
							ViewUtil.sendMsg(context, resObj.getResBody().getContent());
						}

					}

					@Override
					public void onFailure(HttpException error, String errMsg) {
						Log.e(TAG, errMsg, error);
						ViewUtil.sendMsg(context, R.string.msg_err_netfail);
					}
				});

			}

			// StringBuffer buffer = new StringBuffer();
			// buffer.append("{userid:'").append(userId);
			// buffer.append("', startdate:'").append(today);
			//
			// HttpUtil httpUtil = new HttpUtil(600000);
			// httpUtil.configResponseTextCharset("ISO-8859-1");
			// httpUtil.send("opt_get_worksummary", buffer.toString(), new
			// RequestCallBack<String>() {
			//
			// @Override
			// public void onSuccess(ResponseInfo<String> responseInfo) {
			//
			// ResponseStructBean resObj =
			// HttpUtil.parseRes(responseInfo.result);
			// if (ConstValues.SUCCESS.equals(
			// resObj.getResHead().getStatus())) {
			//
			// MstWorksummaryInfo summaryInfo =
			// JsonUtil.parseJson(resObj.getResBody().getContent(),MstWorksummaryInfo.class);
			//
			// content = summaryInfo.getWscontent();
			//
			// } else {
			// sendMsg(resObj.getResBody().getContent());
			// }
			//
			// }
			//
			// @Override
			// public void onFailure(HttpException error, String errMsg) {
			// Log.e(TAG, errMsg, error);
			// sendMsg(errMsg);
			// }
			// });

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	/**
	 * 提交总结
	 * 
	 * @param today
	 *            今天
	 * @param userId
	 *            用户ID
	 * @param string
	 *            文本框输入内容
	 * @return
	 */
	public void sumbitSummary(final String today, String userId, String string) {
	    try {
	        // 保存本地 
	        Dao<MstWorksummaryInfo, String> mstWorksummaryInfoDao = DatabaseHelper.getHelper(context).getMstWorksummaryInfoDao();
	        
	        MstWorksummaryInfo mstWorksummaryInfo = new MstWorksummaryInfo();
	        mstWorksummaryInfo.setWskey(FunUtil.getUUID());
	        mstWorksummaryInfo.setStartdate(today);
	        mstWorksummaryInfo.setUserid(userId);
	        mstWorksummaryInfo.setWscontent(string);
	        mstWorksummaryInfoDao.createOrUpdate(mstWorksummaryInfo);
	        
	        //上传到服务器
	        service = new UploadDataService(context,handler);
	        service.upload_worksummary_infos(false, mstWorksummaryInfo);
	        
	        
	        //// 用list传送
////
////			List<MstWorksummaryInfo> list = new ArrayList<MstWorksummaryInfo>();
////			list.add(mstWorksummaryInfo);
	        
//			StringBuffer buffer = new StringBuffer();
//			buffer.append("{userid:'").append(userId);
//			buffer.append("', startdate:'").append(today);
//			buffer.append("', wscontent:'").append(string);
//
//			// 请求网络
//			HttpUtil httpUtil = new HttpUtil(600000);
//			httpUtil.configResponseTextCharset("ISO-8859-1");
//			httpUtil.send("opt_save_worksummary", buffer.toString(), new RequestCallBack<String>() {
//
//				@Override
//				public void onSuccess(ResponseInfo<String> responseInfo) {
//
//					ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
//					if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
//
//						// mstSynckvM.setTablename("MST_WORKSUMMARY_INFO");
//						// mstSynckvM.setUpdatetime(today);
//						// mstSynckvM.setSynctime(DateUtil.getNowStr());
//
//						// ConstValues.kvMap.put("MST_WORKSUMMARY_INFO",
//						// mstSynckvM);
//
//						ViewUtil.sendMsg(context, R.string.succes);
//					} else {
//						ViewUtil.sendMsg(context, resObj.getResBody().getContent());
//					}
//				}
//
//				@Override
//				public void onFailure(HttpException error, String errMsg) {
//					Log.e(TAG, errMsg, error);
//					ViewUtil.sendMsg(context, errMsg);
//				}
//			});
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	}

	/**
	 * 保存或更新总结内容 
	 * 
	 * @param infoId       总结Id
	 * @param summaryDate  总结日期
	 * @param content      总结内容
	 * @param type         总结类型：0日，1周，2月
	 */
	public void sumbitSummary(String infoId,
	        String summaryDate, String content, String type) {
	    
	   /* if (CheckUtil.isBlankOrNull(content)) {
	        ViewUtil.sendMsg(context, R.string.weekwork_msg_summary);
	        return;
	    }*/
	    
		try {
			Dao<MstWorksummaryInfo, String> dao = 
			        DatabaseHelper.getHelper(context).getMstWorksummaryInfoDao();
			if (CheckUtil.isBlankOrNull(infoId)) infoId = FunUtil.getUUID();
			MstWorksummaryInfo info = dao.queryForId(infoId);
			if (info != null) {
			    info.setWscontent(content);
                info.setTypeflag(type);
			} else {
			    info = new MstWorksummaryInfo();
			    info.setWskey(infoId);
			    info.setStartdate(type.equals("0")?summaryDate.replace("-", ""):summaryDate);
			    //info.setUserid(ConstValues.loginSession.getUserCode());
			    info.setUserid(PrefUtils.getString(context, "userCode", ""));
			    info.setWscontent(content);
			    info.setTypeflag(type);
			    info.setSisconsistent(ConstValues.FLAG_0);
			    info.setPadisconsistent(ConstValues.FLAG_0);
			    info.setDeleteflag(ConstValues.FLAG_0);
			    info.setCreuser(info.getUserid());
			    info.setUpdateuser(info.getUserid());
			}
			dao.update(info);
			
			//上传到服务器
			service = new UploadDataService(context,handler);
			// 上传所有日工作总结
			service.upload_worksummary_infos(false, info);

		} catch (SQLException e) {
		    Log.e(TAG, "添加工作总结失败", e);
		}

	}
	
	/**
	 * 查找工作总结
	 * 
	 * @param date 日期
	 * @param type 类型：0:日、1:周、2:月    
	 * @return
	 */
	public void findSummaryInfo(String date, String type) {
	    if (CheckUtil.isBlankOrNull(date)) {
	        date = DateUtil.formatDate(new Date(), "yyyyMMdd");
	    }
	    if (CheckUtil.isBlankOrNull(type)) {
	        type = ConstValues.FLAG_0;
	    }
	    
	    // 查询标志位
	    boolean searchNet = false;
	    
	    // 与本地缓存数据比较，判定数据来源为本地或网络
	    MstSynckvM kv = ConstValues.kvMap.get("MST_WORKSUMMARY_INFO");
	    if (CheckUtil.isBlankOrNull(kv.getUpdatetime())) {
	        searchNet = true;
	        
	    } else {
	        
	        // 查询数据库
	        try {
                DatabaseHelper helper = DatabaseHelper.getHelper(context);
                Dao<MstWorksummaryInfo, String> dao = helper.getMstWorksummaryInfoDao();
                Map<String, Object> args = new HashMap<String, Object>();
                args.put("startdate", date);
                args.put("typeflag", type);
                List<MstWorksummaryInfo> infoLst = dao.queryForFieldValues(args);
                Bundle bundle = new Bundle();
                if (!CheckUtil.IsEmpty(infoLst)) {
                    bundle.putSerializable("info", infoLst.get(0));
                } else {
                    searchNet = true;
                }
                Message msg = handler.obtainMessage();
                msg.what = ConstValues.WAIT2;
                msg.setData(bundle);
                handler.sendMessage(msg);
            } catch (SQLException e) {
                Log.e(TAG, "获取日工作总结表DAO对象失败", e);
            }
	    }
	    
	    // 获取数据
	    if (searchNet) {
            StringBuffer buffer = new StringBuffer();
            //buffer.append("{userId:'").append(ConstValues.loginSession.getUserCode());
            buffer.append("{userId:'").append(PrefUtils.getString(context, "userCode", ""));
            buffer.append("', startDate:'").append(date);
            buffer.append("', typeflag:'").append(type).append("'}");
            HttpUtil httpUtil = new HttpUtil(600000);
            httpUtil.configResponseTextCharset("ISO-8859-1");
            httpUtil.send("opt_get_worksummary", buffer.toString(), new RequestCallBack<String>() {

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {

                    ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                    if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                        
                        List<MstWorksummaryInfo> infoLst = JsonUtil.parseList(
                                resObj.getResBody().getContent(), MstWorksummaryInfo.class);
                        Bundle bundle = new Bundle();
                        if (!CheckUtil.IsEmpty(infoLst)) {
                            bundle.putSerializable("info", infoLst.get(0));
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = ConstValues.WAIT2;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    } else {
                        ViewUtil.sendMsg(context, resObj.getResBody().getContent());
                    }

                }

                @Override
                public void onFailure(HttpException error, String errMsg) {
                    Log.e(TAG, errMsg, error);
                    ViewUtil.sendMsg(context, R.string.msg_err_netfail);
                }
            });
	    } 
	}
}
