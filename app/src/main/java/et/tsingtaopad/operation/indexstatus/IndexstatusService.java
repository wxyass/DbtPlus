package et.tsingtaopad.operation.indexstatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstProductMDao;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.db.tables.PadChecktypeM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：IndexstatusService.java</br>
 * 作者：@吴欣伟</br>
 * 创建时间：2013/11/25</br>
 * 功能描述: 指标状态查询业务逻辑</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期       原因  BUG号    修改人 修改版本</br>
 */
public class IndexstatusService {
    
    
    public static final String TAG = "IndexstatusService";
    
    private Context context;
    private Handler handler;
    
    public IndexstatusService(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }
    
    /**
     * 获取可选择的产品列表数据
     * @return
     */
    public List<KvStc> getIndexProData() {
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
    
    /**
     * 指标状态查询
     * 
     * @param lineId        线路ID
     * @param searchDate    查询日期
     * @param checkId       指标ID
     * @param valueId       指标值ID
     * @param productIds    产品ID集合
     */
    public void searchIndex(String lineId, String searchDate,
                    String checkId, String valueId, String[] productIds, AlertDialog dialog) {
        
        if (CheckUtil.isBlankOrNull(lineId)) {
            ViewUtil.sendMsg(context, R.string.indexstatus_msg_line);
            
        } else if (CheckUtil.isBlankOrNull(checkId)) {
            ViewUtil.sendMsg(context, R.string.indexstatus_msg_check);
            
        } else if (CheckUtil.isBlankOrNull(valueId)) {
            ViewUtil.sendMsg(context, R.string.indexstatus_msg_value);
            
        } else {
            
            // 弹出等待框
            dialog.show();
            
            // 组建请求参数
            Map<String, String> args = new HashMap<String, String>();
            //args.put("gridkey", ConstValues.loginSession.getGridId());
            args.put("gridkey", PrefUtils.getString(context, "gridId", ""));
            args.put("routekey", lineId);
            args.put("checkkey", checkId);
            args.put("cstatuskey", valueId);
            args.put("prokeyLst", JsonUtil.toJson(productIds));
            args.put("searchdate", searchDate);
            
            // 请求网络
            HttpUtil httpUtil = new HttpUtil(5 * 60000);
            httpUtil.configResponseTextCharset("ISO-8859-1");
            httpUtil.send("opt_get_checkstatus", JsonUtil.toJson(args), new RequestCallBack<String>() {

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
    
    /**
     * 日期选择器的创建
     * 
     * @param context 上下文
     * @param tv 显示日期的TextView控件
     */
    public void showDatePicDialog(Context context, final TextView tv) {

        Calendar c = Calendar.getInstance();
        Dialog date = new DatePickerDialog(context,R.style.dialog_date, new OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {

                // 选择日期写入EditText
                tv.setText(year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        date.setCancelable(false);
        date.show();
    }

    
    /**
     * 初始化指标、指标值树级关系 对象
     */
    public List<KvStc> queryCheckTypeStatus() {        
        List<KvStc> checkTypeStatusList=new ArrayList<KvStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            PadChecktypeMDao dao = helper.getDao(PadChecktypeM.class);
            checkTypeStatusList= dao.queryCheckTypeStatus(helper, ConstValues.FLAG_1);            
        } catch (SQLException e) {
            Log.e(TAG, "获取访指标数据集合失败", e);
        }
        return checkTypeStatusList;
    }
}
