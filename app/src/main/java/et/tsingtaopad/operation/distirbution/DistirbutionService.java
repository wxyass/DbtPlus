package et.tsingtaopad.operation.distirbution;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstProductMDao;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DistirbutionService.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2013年12月20日</br>      
 * 功能描述: 万能铺货率查询</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class DistirbutionService {

	private Context context;
	private Handler handler;

	public DistirbutionService(Context context, Handler handler) {
	    this.context = context;
		this.handler = handler;
	}
	
	/**
	 * 终端万能铺货率查询
	 * 
	 * @param type             类型：0-按终端、1-按渠道
	 * @param lineId           线路Id:0-汇总、1-全部、具体线路Id
	 * @param sellChannelText    销售渠道Id:0-汇总、1-全部
	 * @param mainChannelText    主渠道Id:0-汇总、1-全部
	 * @param minorChannelText   次渠道Id:0-汇总、1-全部
	 * @param tlevel           终端等级Id:0-汇总、1-全部
	 * @param proId            产品Id
	 * @param searchDate       查询日期
	 */
	public void search(String type, String lineId, String sellChannelText, 
	        String mainChannelText, String minorChannelText, String tlevel, String proId, String searchDate, AlertDialog dialog) {
	    
	    String sellChannelId = null;
	    String mainChannelId =null;
	    String minorChannelId =null;
	    if (!NetStatusUtil.isNetValid(context)) {
            ViewUtil.sendMsg(context, R.string.msg_err_nettatusfail);
            
        // 如果没有产品, 提示更新数据    
	    } 
	    if(type.equals("0")){
	        //按终端查询,销售渠道，主渠道，次渠道接口没用到，随便赋值。防止下面的条件出现提示
	        sellChannelText="0";
	        mainChannelText="0";
	        minorChannelText="0";
	        
	    }else if(type.equals("1")){
	        //按渠道查询终端等级赋值同上
	        tlevel="0";
	    }
	    //如果没有选择销售渠道
	    if(CheckUtil.isBlankOrNull(sellChannelText)){
	        ViewUtil.sendMsg(context, R.string.distirbution_prompt_sell);
	    }//如果没有选择主渠道
	    else if(CheckUtil.isBlankOrNull(mainChannelText)){
	        ViewUtil.sendMsg(context, R.string.distirbution_prompt_main);
        }//如果没有选择次渠道
	    else if(CheckUtil.isBlankOrNull(minorChannelText)){
	        ViewUtil.sendMsg(context, R.string.distirbution_prompt_minor);
	    }//如果没有选择等级
	    else if(CheckUtil.isBlankOrNull(tlevel)){
            ViewUtil.sendMsg(context, R.string.distirbution_prompt_level);
        }//如果没有选择产品
	    else if (CheckUtil.isBlankOrNull(proId)) {
            ViewUtil.sendMsg(context, R.string.distirbution_prompt_pro);
        }else {
	        // 弹出等待框
	        dialog.show();
	        //根据接口：汇总传0，全部传1
	        //终端查询
	        if(!tlevel.equals("0")){
	            //如果终端等级为定值，则传全部
	            tlevel="1";
	        }
	        //渠道查询
	        if(sellChannelText.equals("0")){
	            //如果销售渠道为汇总，主渠道和次渠道都应该为汇总
	            
	            sellChannelId="0";
	            mainChannelId="0";
	            minorChannelId="0";
	            
	        }else if(!(sellChannelText.equals("0"))&&mainChannelText.equals("0")){
	            //如果销售渠道不是汇总，主渠道是汇总，次渠道应该是汇总
	            sellChannelId="1";
	            mainChannelId="0";
                minorChannelId="0";
	            
	        }else if(!(sellChannelText.equals("0"))&&!(mainChannelText.equals("0"))&&minorChannelText.equals("0")){
	            //如果销售渠道，主渠道不是汇总，次渠道是汇总
	            sellChannelId="1";
                mainChannelId="1";
                minorChannelId="0";
	        }else{
	            //销售渠道，主渠道，次渠道都不是汇总
	            sellChannelId="1";
                mainChannelId="1";
                minorChannelId="1";
	        }
	            
            // 组建请求参数
            Map<String, String> args = new HashMap<String, String>();
            args.put("reportType", type);
            //args.put("gridkey", ConstValues.loginSession.getGridId());
            args.put("gridkey", PrefUtils.getString(context, "gridId", ""));
            args.put("routekey", lineId); 
            args.put("searchdate", searchDate); 
            args.put("sellchannel", sellChannelId); 
            args.put("mainchannel", mainChannelId); 
            args.put("minorchannel", minorChannelId); 
            args.put("productkey", proId); 
            args.put("tlevel", tlevel); 
            
            // 请求网络
            HttpUtil httpUtil = new HttpUtil(3 * 60000);
            httpUtil.configResponseTextCharset("ISO-8859-1");
            httpUtil.send("opt_get_powerful", JsonUtil.toJson(args), new RequestCallBack<String>() {

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    ResponseStructBean resObj = 
                            HttpUtil.parseRes(responseInfo.result);
                    if (ConstValues.SUCCESS.equals(
                                  resObj.getResHead().getStatus())) {
                        Bundle bundle = new Bundle();
                        bundle.putString("dataLst", resObj.getResBody().getContent());
                        Message message = new Message();
                        message.what = ConstValues.WAIT2;
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else {
                        ViewUtil.sendMsg(context, resObj.getResBody().getContent(), handler);
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    ViewUtil.sendMsg(context, R.string.msg_search_netfail, handler);
                }
            });
	    }
	}

	/**
     * 获取可选择的产品列表数据
     * @return
     */
    public List<KvStc> queryProductLst() {
        List<KvStc> proLst = new ArrayList<KvStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstProductMDao dao = helper.getDao(MstProductM.class);
            proLst = dao.getIndexPro(helper);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proLst;
    }
}
