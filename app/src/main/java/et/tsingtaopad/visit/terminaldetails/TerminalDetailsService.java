package et.tsingtaopad.visit.terminaldetails;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;
import cn.com.benyoyo.manage.bs.IntStc.DataresultTerPurchaseDetailsStc;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.tables.MstSynckvM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;

/**
 * 项目名称：营销移动智能工作平台 <br>
 * 文件名：TerminalDetailsService.java <br>
 * 作者：@沈潇 <br>
 * 创建时间：2013/11/24 <br>
 * 功能描述: 终端详情 <br>
 * 版本 V 1.0 <br>
 * 修改履历 日期 原因 BUG号 修改人 修改版本
 */
public class TerminalDetailsService {
    
    private final String TAG = "TerminalDetailsService";
    
	private Context context;
	private AlertDialog searchDialog;

	public TerminalDetailsService(Context context) {
		this.context = context;
		this.searchDialog = DialogUtil.progressDialog(
		        (FragmentActivity)context, R.string.dialog_msg_search);
	}

	/**
	 * DatePickerDialog日期弹出框
	 * @param currentbt 显示日期的控件
	 */
	public void getDatePickerDialog(final Button currentbt) {
	    Calendar c = Calendar.getInstance();
		new DatePickerDialog(context,R.style.dialog_date, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				currentbt.setText(year + "-"
						+ String.format("%02d", monthOfYear + 1) + "-"
						+ String.format("%02d", dayOfMonth));

			}
		}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
	}

	/**
	 * 请求网络获取显示列表数据
	 * @param json 请求的Json串
	 * @param listView 显示数据的LIstView控件
	 */
	private void getHttp(String json, final ListView listView) {
	    
		// 请求网络
		HttpUtil httpUtil = new HttpUtil(600000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		httpUtil.send("opt_get_terminaldetails", json, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				ResponseStructBean resObj = HttpUtil
						.parseRes(responseInfo.result);
				if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
					String content = resObj.getResBody().getContent();
					List<DataresultTerPurchaseDetailsStc> dataLst = JsonUtil.parseList(content, DataresultTerPurchaseDetailsStc.class);
				    PurchaseDetailsAdapter adapter = new PurchaseDetailsAdapter(context, dataLst);
				    listView.setAdapter(adapter);
				} else {
				    ViewUtil.sendMsg(context, resObj.getResBody().getContent());
				}
				if (searchDialog != null && searchDialog.isShowing()) searchDialog.dismiss();
			}

			@Override
			public void onFailure(HttpException error, String errMsg) {
                Log.e(TAG, errMsg, error);
                if (searchDialog != null && searchDialog.isShowing()) searchDialog.dismiss();
                ViewUtil.sendMsg(context, R.string.msg_err_netfail);
			}
		});
	}

	/**
	 * 获取终端进货明细的查询列表
	 * @param listView 显示数据的ListView
	 * @param sp 选择路线的Spinner
	 * @param startDateBt 查询开始日期的选择按钮
	 * @param endDateBt 查询结束日期的选择按钮
	 */
	public void getSearchData(ListView listView, Spinner sp, Button startDateBt, Button endDateBt) {
	    if (!searchDialog.isShowing()) {
	        searchDialog.show();
	        
	        boolean result=false;
            MstSynckvM mstSynckvM = ConstValues.kvMap.get("MST_VISIT_M");
            if (CheckUtil.isBlankOrNull(mstSynckvM.getUpdatetime())) 
            {
//                String endDate = mstSynckvM.getUpdatetime();
//                String startDate = DateUtil.formatDate(DateUtil.addDays(
//                            DateUtil.parse(endDate, "yyyy-MM-dd HH:mm:ss"), 
//                                -Integer.parseInt(mstSynckvM.getSyncDay())), "yyyy-MM-dd HH:mm:ss");
//                String selectStartDate = startDateBt.getText().toString() + " 23:59:59";
//                result = (startDate.compareTo(selectStartDate) <= 0);
            }
            
            String strStartDate = startDateBt.getText().toString().substring(0, 10).replace("-", "") + "000000";
            String strEndDate =  endDateBt.getText().toString().substring(0, 10).replace("-", "") + "235959";
            
            //查询日期在本地数据存储日期内
            if(result) {
                
                List<DataresultTerPurchaseDetailsStc> detailDataLst = searchLocalData(new String[]{sp.getTag().toString(), strStartDate, strEndDate});
                PurchaseDetailsAdapter adapter = new PurchaseDetailsAdapter(context, detailDataLst);
                listView.setAdapter(adapter);
                if (searchDialog != null && searchDialog.isShowing())  searchDialog.dismiss();
                
                //查询日期在本地数据存储日期外，请求网络
            } else {
                
                //查询服务器
                String json = "{userId:'" 
                        //+ ConstValues.loginSession.getUserCode() 
                        + PrefUtils.getString(context, "userCode", "") 
                        + "', startDate:'"
                        + strStartDate
                        + "', endDate:'"
                        + strEndDate
                        + "', routeKey:'"
                        + sp.getTag().toString()
                        + "'}";
        
                getHttp(json, listView);
            }  
	    }
	}

	/**
	 * 查询本地数据库获取终端进货明细的数据
	 * @param key sql查询条件
	 * @return
	 */
	private List<DataresultTerPurchaseDetailsStc> searchLocalData(String [] key) {
		List<DataresultTerPurchaseDetailsStc> terminalDetailsVO = new ArrayList<DataresultTerPurchaseDetailsStc>();
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			MstVisitMDao dao = helper.getDao(MstVisitM.class);
			terminalDetailsVO = dao.searchTerminalDetails(helper, key );
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return terminalDetailsVO;
	}
}
