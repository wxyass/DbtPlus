/**
 * 
 */
package et.tsingtaopad.service.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstSynckvM;
import et.tsingtaopad.tools.HttpUtil;

/**
 * 项目名称：营销移动智能工作平台 文件名：TerminalDetailsFragment.java 作者：@沈潇 创建时间：2013/11/24 功能描述:
 * 终端详情 版本 V 1.0 修改履历 日期 原因 BUG号 修改人 修改版本
 */
public abstract class ServiceSupport {
	private Handler handler;
	private Context context;
	private String currentDate;
	private String getResBody;// http返回 值
	private int style;// 异步处理类型

	public ServiceSupport(Handler handler, Context context) {
		this.context = context;
		this.handler = handler;
		init();
	}

	public void init() {
		Calendar d = Calendar.getInstance(Locale.CHINA);
		int year = d.get(Calendar.YEAR);
		int month = d.get(Calendar.MONTH);
		int day = d.get(Calendar.DAY_OF_MONTH);
		currentDate = year + "-" + (month + 1) + "-" + day;
	}

	/**
	 * 设置文本
	 * 
	 * @return
	 * */
	public void setViewCurrentDate(Button[] v) {

		for (int i = 0; i < v.length; i++) {
			v[i].setText(currentDate);
		}
	}

	/**
	 * DatePickerDialog日期弹出框
	 * 
	 * @return
	 * */
	public void getDatePickerDialog(String currentDate, final Button currentbt) {
		String p[] = currentDate.split("-");
		new DatePickerDialog(context, R.style.dialog_date, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				currentbt.setText(year + "-"
						+ String.format("%02d", monthOfYear + 1) + "-"
						+ String.format("%02d", dayOfMonth));

			}
		}, Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1,
				Integer.parseInt(p[2])).show();
	}
	/**
	 * Format(1999-01-01)
	 * 
	 * @return
	 * */
	public Map<String, Date> getTimeKV() {
		Map<String, Date> map = new HashMap<String, Date>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Calendar d = Calendar.getInstance(Locale.CHINA);
			MstSynckvM kv_obj = ConstValues.kvMap.get("MST_VISIT_M");
			if (kv_obj == null) {
				kv_obj=new MstSynckvM();
			}
				map.put("getUpdatetime",
						sdf.parse(kv_obj.getUpdatetime() == null ? sdf
								.format(new Date()) : kv_obj.getUpdatetime()));// 更新结束时间
				d.setTime(sdf.parse(kv_obj.getUpdatetime() == null ? sdf
						.format(new Date()) : kv_obj.getUpdatetime()));
				d.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(kv_obj
						.getSyncDay() == null ? "30" : kv_obj.getSyncDay()));
				map.put("getTime", d.getTime());// 更新开始时间
			
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return map;
	}
	/**
	 * Format(1999-01-01)
	 * 
	 * @return
	 * */
	public Date dateFormat(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}



	public void getHttp(String requestcode, String json) {
		// 请求网络
		HttpUtil httpUtil = new HttpUtil(60000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		httpUtil.send(requestcode, json, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				ResponseStructBean resObj = HttpUtil
						.parseRes(responseInfo.result);
				if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
					getResBody = resObj.getResBody().getContent();
					Log.d("tag","success--上传>"+getResBody);
					setStyle(2);
					asynchronousDataHandler();
				} else {
					Log.e("tag","faile-->"+resObj.getResHead().getStatus());
				}
			}

			@Override
			public void onFailure(HttpException error, String errMsg) {
				Log.e("tag","onFailure-->");
			}
		});
	}
	public abstract void asynchronousDataHandler() ;
	public abstract void onFailure() ;

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}


	public String getGetResBody() {
		return getResBody;
	}

	public void setGetResBody(String getResBody) {
		this.getResBody = getResBody;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}
	
}
