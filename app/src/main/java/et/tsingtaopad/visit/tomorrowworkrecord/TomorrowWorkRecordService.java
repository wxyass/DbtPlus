///////////////////////////////////////////////////////////////////////////////
//Copyright ownership belongs to SINOSOFT CO.,LTD.shall not be reproduced ,  //
//copied,or used in other ways without permission.Otherwise SINOSOFT CO.,LTD.//
//will have the right to pursue legal responsibilities.                      //
//                                                                           //
//Copyright &copy; 2011 SINOSOFT CO., LTD. All rights reserved.              //
///////////////////////////////////////////////////////////////////////////////
package et.tsingtaopad.visit.tomorrowworkrecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

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
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.tomorrowworkrecord.domain.DayWorkDetailStc;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：TomorrowWorkRecordService.java<br>
 * 作者：@沈潇<br>
 * 创建时间：2013/11/24<br>
 * 功能描述: 每日工作<br>
 * 版本 V 1.0<br>
 * 修改履历<br>
 * 日期 原因 BUG号 修改人 修改版本<br>
 */
public class TomorrowWorkRecordService {

    private final String TAG = "TomorrowRecordService";
    
    private Context context;
    private Handler handler;
    
	public TomorrowWorkRecordService(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	/**
	 * 获取日工作明细数据
	 * @param listView 
	 * @param dateBt 选择日期的按钮
	 */
    public void getSearchData(ListView listView, Button dateBt, AlertDialog dialog) {
        // 弹射查询等框 
        if (dialog != null && dialog.isShowing()) {
            return ;
        } else {
            dialog.show();
        }
        
        boolean result=false;
        MstSynckvM mstSynckvM = ConstValues.kvMap.get("MST_VISIT_M");
        if(CheckUtil.isBlankOrNull(mstSynckvM.getUpdatetime()))
        {
//            String endDate = mstSynckvM.getUpdatetime();
//            String startDate = DateUtil.formatDate(DateUtil.addDays(DateUtil
//                    .parse(endDate, "yyyy-MM-dd HH:mm:ss"), -Integer.parseInt(mstSynckvM.getSyncDay())), "yyyy-MM-dd HH:mm:ss");
//            String searchDate = dateBt.getText().toString() + " 23:59:59";
//            result = (startDate.compareTo(searchDate) <= 0);
        }
        //查询日期在本地数据存储日期内    为true时查本地
        if(result) {
            String strStartDate = dateBt.getText().toString().substring(0, 10).replace("-", "") + "000000";
            String strEndDate = dateBt.getText().toString().substring(0, 10).replace("-", "") + "235959";
            //List<DayWorkDetailStc> detailDataLst = searchLocalData(context, strStartDate, strEndDate, ConstValues.loginSession.getUserCode());
            List<DayWorkDetailStc> detailDataLst = searchLocalData(context, strStartDate, strEndDate, PrefUtils.getString(context, "userCode", ""));
            TomorrowWorkRecordAdapter adapter = new TomorrowWorkRecordAdapter(context, detailDataLst);
            listView.setAdapter(adapter);
            handler.sendEmptyMessage(ConstValues.WAIT2);
            
            //查询日期在本地数据存储日期外，请求网络
        } else {
            
            //查询服务器
            String strSearchDate = dateBt.getText().toString().substring(0, 10).replace("-", "");
            //String json = "{userId:'" + ConstValues.loginSession.getUserCode()
            String json = "{userId:'" + PrefUtils.getString(context, "userCode", "")
                    + "', date:'" + strSearchDate + "'}";
            getHttp(json, listView);
        }  
    }

    /**
     * 请求网络获取显示列表数据
     * @param json 请求的Json串
     * @param listView 显示数据的LIstView控件
     */
    private void getHttp(String json, final ListView listView) {
        
        // 请求网络
        HttpUtil httpUtil = new HttpUtil(3 * 60000);
        httpUtil.configResponseTextCharset("ISO-8859-1");
        httpUtil.send("opt_get_tomorrowworkrecord", json, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                ResponseStructBean resObj = HttpUtil
                        .parseRes(responseInfo.result);
                if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                    String content = resObj.getResBody().getContent();
                    //savFile(content, "visitrecord");
                    //绑定ListView数据
                    List<DayWorkDetailStc> dataLst = JsonUtil.parseList(content, DayWorkDetailStc.class);
                    TomorrowWorkRecordAdapter adapter = new TomorrowWorkRecordAdapter(context, dataLst);
                    listView.setAdapter(adapter);
                    handler.sendEmptyMessage(ConstValues.WAIT2);
                } else {
                    ViewUtil.sendMsg(context, resObj.getResBody().getContent(), handler);
                }
            }

            @Override
            public void onFailure(HttpException error, String errMsg) {
                Log.e(TAG, errMsg, error);
                ViewUtil.sendMsg(context, R.string.msg_err_netfail, handler);
            }
        });
    }

	/**
	 * 本地数据查询
	 * 
	 * @return
	 * */
	private List<DayWorkDetailStc> searchLocalData(Context c, String start, String end, String useId) {
		List<DayWorkDetailStc> detailLst = new ArrayList<DayWorkDetailStc>();
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(c);
			MstVisitMDao dao = helper.getDao(MstVisitM.class);
			detailLst = dao.searchTomorrowWorkRecord(helper, start, end, useId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return detailLst;
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
        },  c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
    }
    
    /**
     * 生成文件
     * 
	 * @param json
	 */
	private void savFile(String json,String name) {
		
		String sdcardPath = Environment.getExternalStorageDirectory() + "";
        String DbtPATH = sdcardPath + "/dbt/et.tsingtaopad";
        String BUGPATH = DbtPATH + "/log/";

		File txt = new File(BUGPATH+name+".txt");
		if (!txt.exists()) {
			try {
				txt.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		byte bytes[] = new byte[3072];
		bytes = json.getBytes(); // 新加的
		int b = json.length(); // 改
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(txt);
			fos.write(bytes, 0, b);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
