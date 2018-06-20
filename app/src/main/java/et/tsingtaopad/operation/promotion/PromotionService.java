package et.tsingtaopad.operation.promotion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstSynckvM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：PromotionService.java</br> 
 * 作者：@ray </br>
 * 创建时间：2013-11-29</br> 
 * 功能描述: 促销活动查询业务逻辑处理</br> 
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
@SuppressLint("UseValueOf")
public class PromotionService {

	private final String TAG = "PromotionService";
	protected Context context;
	protected Handler handler;

	public PromotionService(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	public void search(final MstPromotionsM prom, final String[] lineIds, 
	        final String[] termLevels,final String[] termSecSells, String startDate,final String searchDate, AlertDialog dialog) {
	    if ("-1".equals(prom.getPromotkey())) {
	        ViewUtil.sendMsg(context, R.string.promotion_valid_prom, handler);
	        
	    } else if (CheckUtil.IsEmpty(lineIds)) {
	        ViewUtil.sendMsg(context, R.string.promotion_valid_line, handler);
	        
	    } else {

	        // 弹出提示对话框
	        if (dialog != null && !dialog.isShowing()) {
	            dialog.show();
	        } else {
	            return ;
	        }
	        
	        // 拜访数据缓存最大日期
	        String visitED = "";
	        MstSynckvM visitKv = ConstValues.kvMap.get("MST_VISIT_M");
	        if (visitKv != null && !CheckUtil.isBlankOrNull(visitKv.getUpdatetime())) {
	            visitED = visitKv.getUpdatetime().substring(0, 10).replace("-", "");
	        }
	        
	        // 查询日期小于活动开日期
	        if (searchDate.compareTo(prom.getStartdate()) <= -1) {
	            ViewUtil.sendMsg(context, "查询日期不在活动时间范围内");
	            
	        // 如果visitED <= 活动开始日期， 则查本地数据
	        } /*else if (!CheckUtil.isBlankOrNull(visitED)  && visitED.compareTo(prom.getStartdate()) <= 0) {
	            new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        DatabaseHelper helper = DatabaseHelper.getHelper(context);
                        try {
                            MstPromotionsmDao dao = (MstPromotionsmDao)helper.getDao(MstPromotionsM.class);
                            Map<String, List<String>> termMap = dao.promotionSearch(helper, 
                                    prom.getPromotkey(), prom.getStartdate(), searchDate, lineIds, termLevels);
                            Bundle bundle = new Bundle();
                            bundle.putString("termMap", JsonUtil.toJson(termMap));
                            Message message = new Message();
                            message.what = ConstValues.WAIT2;
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (SQLException e) {
                            Log.e(TAG, "促销活动查询失败", e);
                        }
                    }
	            }.start();
	            
	        // 查网络数据
	        }*/ else {
	            
	            // 组建请求Json
	            StringBuffer buffer = new StringBuffer();
                //buffer.append("{gridid:'").append(ConstValues.loginSession.getGridId());
                buffer.append("{gridid:'").append(PrefUtils.getString(context, "gridId", ""));
                buffer.append("', ptypekey:'").append(prom.getPromotkey());
                buffer.append("', searchdate:'").append(searchDate);
                buffer.append("', startdate:'").append(startDate);
                buffer.append("', promstartdate:'").append(prom.getStartdate());
                if (!CheckUtil.IsEmpty(lineIds)) {
                    buffer.append("', routekey:'").append(Arrays.toString(lineIds));
                }
                if (!CheckUtil.IsEmpty(termLevels)) {
                    buffer.append("', tlevel:'").append(Arrays.toString(termLevels));
                }
                if (!CheckUtil.IsEmpty(termSecSells)) {
                    buffer.append("', secsells:'").append(Arrays.toString(termSecSells));
                }
                buffer.append("'}");
                
                // 请求网络
                HttpUtil httpUtil = new HttpUtil(60 * 1000);
                httpUtil.configResponseTextCharset("ISO-8859-1");
                httpUtil.send("opt_get_promotion", buffer.toString(), new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        ResponseStructBean resObj = 
                                HttpUtil.parseRes(responseInfo.result);

                        if (ConstValues.SUCCESS.equals(
                                      resObj.getResHead().getStatus())) {
                            Bundle bundle = new Bundle();
                            bundle.putString("termMap", resObj.getResBody().getContent());
                            Message message = new Message();
                            message.what = ConstValues.WAIT2;
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } else {
                            ViewUtil.sendMsg(context, resObj.getResBody().getContent());
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        ViewUtil.sendMsg(context, R.string.msg_search_netfail, handler);
                    }
                });
	        }
	    }
	}

	/**
	 * 查询促销活动主表
	 * 
	 * @param date         查询时间
	 * @param selectFlag   请选择标志位
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public List<MstPromotionsM> queryPromo(String date, Boolean selectFlag) {
	    
	    List<MstPromotionsM> lst = new ArrayList<MstPromotionsM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstPromotionsmDao dao = helper.getDao(MstPromotionsM.class);
            QueryBuilder<MstPromotionsM, String> query = dao.queryBuilder();
            Where<MstPromotionsM, String> where = query.where();
            where.and(where.le("startdate", date), where.ge("enddate", date));
            lst = query.query();
            
        } catch (SQLException e) {
            Log.e(TAG, "获取促销活动信息失败", e);
        }
        
        if (selectFlag) {
            MstPromotionsM promInfo = new MstPromotionsM();
            promInfo.setPromotkey("-1");
            promInfo.setPromotname(context.getString(R.string.promotion_valid_prom));
            lst.add(0,promInfo);
        }
        return lst;
	}
}
