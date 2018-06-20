package et.tsingtaopad.home.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.tools.CheckUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：WeatherService.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2014年2月26日</br>      
 * 功能描述: 获取天气预报的webservice</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class HomeService {
	// Webservice服务器地址
	private static final String SERVER_URL = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx";
	// 调用的webservice命令空间
	private static final String PACE = "http://WebXml.com.cn/";
	// 获取所有省份的方法名
	private static final String M_NAME = "getSupportProvince";
	// 获取省份包含的城市的方法名
	private static final String MC_NAME = "getSupportCity";
	// 获取天气详情的方法名
	private static final String W_NAME = "getWeatherbyCityName";
	private Context context;

	public HomeService(Context context) {
		this.context = context;
	}

	// 获取三天之内的天气详情
	public List<String> getWeather(String cityName) throws HttpResponseException, IOException, XmlPullParserException {
		HttpTransportSE httpSe = new HttpTransportSE(SERVER_URL);
		httpSe.debug = true;
		SoapObject soapObject = new SoapObject(PACE, W_NAME);
		soapObject.addProperty("theCityName", cityName);
		final SoapSerializationEnvelope serializa = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		serializa.bodyOut = soapObject;
		serializa.dotNet = true;
		List<String> list = new ArrayList<String>();
		// 调用webservice
		httpSe.call(PACE + W_NAME, serializa);
		// 获取返回信息
		if (serializa.getResponse() != null) {
			SoapObject result = (SoapObject) serializa.bodyIn;
			SoapObject deialt = (SoapObject) result.getProperty("getWeatherbyCityNameResult");
			// 解析数据
			for (int i = 0; i < deialt.getPropertyCount(); i++) {
				list.add(deialt.getProperty(i).toString());
			}
		}
		return list;

	}

	public int getImageId(String ids) {

		int id = 0;
		if (ids.contains(".")) {

			int ided = Integer.parseInt(ids.substring(0, ids.lastIndexOf(".")));

			switch (ided) {
			case 1:
				id = R.drawable.a_one;
				break;
			case 2:
				id = R.drawable.a_two;
				break;
			case 3:
				id = R.drawable.a_three;
				break;
			case 4:
				id = R.drawable.a_four;
				break;
			case 5:
				id = R.drawable.a_five;
				break;
			case 6:
				id = R.drawable.a_six;
				break;
			case 7:
				id = R.drawable.a_seven;
				break;
			case 8:
				id = R.drawable.a_eight;
				break;
			case 9:
				id = R.drawable.a_nine;
				break;
			case 10:
				id = R.drawable.a_ten;
				break;
			case 11:
				id = R.drawable.a_eleven;
				break;
			case 12:
				id = R.drawable.a_twelve;
				break;
			case 13:
				id = R.drawable.a_thirteen;
				break;
			case 14:
				id = R.drawable.a_fourteen;
				break;
			case 15:
				id = R.drawable.a_fifteen;
				break;
			case 16:
				id = R.drawable.a_sixteen;
				break;
			case 17:
				id = R.drawable.a_seventeen;
				break;
			case 18:
				id = R.drawable.a_eighteen;
				break;
			case 19:
				id = R.drawable.a_nineteen;
				break;
			case 20:
				id = R.drawable.a_twenty;
				break;
			case 21:
				id = R.drawable.a_twenty_one;
				break;
			case 22:
				id = R.drawable.a_twenty_two;
				break;
			case 23:
				id = R.drawable.a_twenty_three;
				break;
			case 24:
				id = R.drawable.a_twenty_four;
				break;
			case 25:
				id = R.drawable.a_twenty_five;
				break;
			case 26:
				id = R.drawable.a_twenty_six;
				break;
			case 27:
				id = R.drawable.a_twenty_seven;
				break;
			case 28:
				id = R.drawable.a_twenty_eight;
				break;
			case 29:
				id = R.drawable.a_twenty_nine;
				break;
			case 30:
				id = R.drawable.a_thirty;
				break;
			}
		}
		return id;
	}

	/**
	 * 
	 * @param channelKey 数据字典表渠道的id 用来移除垃圾数据
	 * @return
	 */
	public Map<String, Integer> querySellchannelNum(Map<String, String> channelKey) {
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();

		String querySql = "select tm.sellchannel, count(tm.terminalkey) from mst_terminalinfo_m tm where coalesce(tm.status,'0') != 2 group by tm.sellchannel";

		Map<String, Integer> sellchannelMap = new HashMap<String, Integer>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(querySql, null);
			int count = 0;
			while (cursor.moveToNext()) {
				String sellchannel = cursor.getString(0);

				if (channelKey.get(sellchannel) != null) {
					int sellchannelNum = cursor.getInt(1);
					if (sellchannelNum > 0) {
						sellchannelMap.put(sellchannel, sellchannelNum);
						count += sellchannelNum;
					}
				}
			}

			sellchannelMap.put("Total", count);
		}
		return sellchannelMap;
	}

	/**
	 * 查询有多少家终端
	 * @return
	 */
	public Long getTermNum() {
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();

		String querySql = "SELECT COUNT(*)  FROM MST_TERMINALINFO_M";
		Cursor cursor = db.rawQuery(querySql, null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}
}
