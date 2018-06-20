///////////////////////////////////////////////////////////////////////////////
//Copyright ownership belongs to SINOSOFT CO.,LTD.shall not be reproduced ,  //
//copied,or used in other ways without permission.Otherwise SINOSOFT CO.,LTD.//
//will have the right to pursue legal responsibilities.                      //
//                                                                           //
//Copyright &copy; 2011 SINOSOFT CO., LTD. All rights reserved.              //
///////////////////////////////////////////////////////////////////////////////
package et.tsingtaopad.syssetting.notice;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.CmmBoardM;
import et.tsingtaopad.service.support.ServiceSupport;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.PrefUtils;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：NoticeService.java<br>
 * 作者：@沈潇<br>
 * 创建时间：2013/11/24<br>
 * 功能描述: 通知通告<br>
 * 版本 V 1.0<br>
 * 修改履历 日期 原因 BUG号 修改人 修改版本
 */
public class NoticeService extends ServiceSupport {

	private Handler handler = null;
	private Context context = null;
	/** 是否正在查询 */
	private boolean isQuerying = false;
	/** 服务器数据是否已经查询末尾 */
	private boolean isEnd = false;
	
	//private String lastRequestCreateTime =ConstValues.loginSession.getLoginDate();
	private String lastRequestCreateTime =PrefUtils.getString(context, "loginDate", "");

	public NoticeService(Handler handler, Context context) {
		super(handler, context);
		this.handler = handler;
		this.context = context;
	}

	/**
	 * 本地数据查询cmmboardm
	 * 
	 * @return
	 * */
	@SuppressWarnings("unchecked")
    public List<CmmBoardM> searchLocalData() {
		List<CmmBoardM> ls = new ArrayList<CmmBoardM>();
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			Dao<CmmBoardM, String> memoinfodao = helper.getCmmBoardMDao();
			QueryBuilder<CmmBoardM, String> queryBuilder = memoinfodao.queryBuilder();
			Where<CmmBoardM, String> where = queryBuilder.where();
			where.or(where.isNull("deleteflag"), where.eq("deleteflag", ConstValues.FLAG_0));
			queryBuilder.orderBy("credate", false);
			ls = queryBuilder.query();
		} catch (SQLException e) {
			Log.d("tag", "list-->" + e);
		}

		return ls;
	}

	/**
	 * 异步数据处理
	 * 
	 * @return
	 * */
	public class NoticeServiceThread extends Thread {

		@Override
		public void run() {

			getHandler().post(new Runnable() {

				@SuppressLint("NewApi")
				@Override
				public void run() {
					if (getStyle() == 1) {
						List l = searchLocalData();

						String json = JsonUtil.toJson(l);
						JSONTokener jsontokener = new JSONTokener(json);
						JSONArray ja = null;
						try {
							ja = (JSONArray) jsontokener.nextValue();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else if (getStyle() == 2) {
						// 如果同步成功修改数据库
					}
				}
			});
		}

	}

	
	public void searchRemoteData() {
		if(!NetStatusUtil.isNetValid(context)){//网络不可用
			return;
		}
		isQuerying = true;
		Log.d("tag","网络请求"+lastRequestCreateTime);
		StringBuffer buffer = new StringBuffer();
		//buffer.append("{userId:'").append(ConstValues.loginSession.getUserCode());
		buffer.append("{userId:'").append(PrefUtils.getString(context, "userCode", ""));
		//buffer.append("',areaId:'").append(ConstValues.loginSession.getGridId());// 
		buffer.append("',areaId:'").append(PrefUtils.getString(context, "gridId", ""));// 
		buffer.append("', startDate:'").append(lastRequestCreateTime);// kv
		buffer.append("', rows:'").append("60");//通知公告
		//buffer.append("', parentAreaid:'").append((ConstValues.loginSession.getParentDisIDs().replace("'", "").toString()+ "'}"));// kv
		buffer.append("', parentAreaid:'").append((PrefUtils.getString(context, "pDiscs", "").replace("'", "").toString()+ "'}"));// kv
		Log.d("tag","json-->"+buffer);
		// 请求网络
		HttpUtil httpUtil = new HttpUtil(6000);
		httpUtil.configResponseTextCharset("ISO-8859-1");
		httpUtil.send("opt_get_boards", buffer.toString(),
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						ResponseStructBean resObj = HttpUtil
								.parseRes(responseInfo.result);
						if (ConstValues.SUCCESS.equals(resObj.getResHead()
								.getStatus())) {//
							Log.d("tag", "success--"
									+ resObj.getResBody().getContent());

							List<CmmBoardM> list = JsonUtil.parseList(
									resObj.getResBody().getContent(), CmmBoardM.class);
							Log.d("tag", "success--size->"
									+list.size());
							if (list.size() == 0) {
								Log.d("tag", "isEnd-->");
								isEnd = true;
								isQuerying = false;
						 	} else {
								isEnd = false;
								Log.d("tag",
										"end-->"
												+ list.get(list.size() - 1)
														.getCredate());
								if(null!=list.get(list.size() - 1).getCredate())
									lastRequestCreateTime = new SimpleDateFormat(
										"yyyy-MM-dd").format(list.get(
										list.size() - 1).getCredate());
								sendMsg(list, ConstValues.WAIT1);
							}
							
						} else {
							sendMsg(resObj.getResBody().getContent(),
									ConstValues.WAIT5);
						}
					}

					@Override
					public void onFailure(HttpException error, String errMsg) {
						Log.e("tag", errMsg, error);
						sendMsg(R.string.msg_err_netfail, ConstValues.WAIT6);
					}
				});
	}

	/**
	 * 向界面发送提示消息
	 * 
	 * @param msg
	 *            提示消息
	 */
	private void sendMsg(Object msg, int what) {
		isQuerying = false;
		Message message = new Message();
		message.what = what;
		message.obj = msg;
		handler.sendMessage(message);
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	/**
	 * 是否正在查询
	 * 
	 * @return
	 */
	public boolean isQuerying() {
		return isQuerying;
	}

	/**
	 * @param isQuerying
	 */
	public void setQuerying(boolean isQuerying) {
		this.isQuerying = isQuerying;
	}

	@Override
	public void asynchronousDataHandler() {
		new NoticeServiceThread().start();
	}

	@Override
	public void onFailure() {

	}

}
