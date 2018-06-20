///////////////////////////////////////////////////////////////////////////////
//Copyright ownership belongs to SINOSOFT CO.,LTD.shall not be reproduced ,  //
//copied,or used in other ways without permission.Otherwise SINOSOFT CO.,LTD.//
//will have the right to pursue legal responsibilities.                      //
//                                                                           //
//Copyright &copy; 2011 SINOSOFT CO., LTD. All rights reserved.              //
///////////////////////////////////////////////////////////////////////////////
package et.tsingtaopad.syssetting.todaything;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstVisitmemoInfo;
import et.tsingtaopad.service.support.ServiceSupport;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ReflectUtil;

/**
 * 项目名称：营销移动智能工作平台 文件名：ToDayThingService.java 作者：@沈潇 创建时间：2013/11/24 功能描述: 明日要事
 * 版本 V 1.0 修改履历 日期 原因 BUG号 修改人 修改版本
 */
public class ToDayThingService extends ServiceSupport {
    private ToDayThingAdapter toDay_adapter;
    private ListView todaything_lv_display;
    private Handler handler = null;
    private Context context = null;
    //更新时间
    private String updateTime = "20140124";

    /**是否正在查询*/
    private boolean isQuerying = false;
    /**服务器数据是否已经查询末尾*/
    private boolean isEnd = false;

    /**
     * @param handler
     * @param context
     */
    public ToDayThingService(Handler handler, Context context) {
        super(handler, context);
        this.handler = handler;
        this.context = context;
        updateTime = new SimpleDateFormat("yyyyMMdd").format(new Date());

    }

    /**
     * 所有线路--本地数据库查询
     * 
     * @return
     */
    public List<MstVisitmemoInfo> searchLocalDate() {
        String nowDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
        Log.d("tag", "loginStr-->" + nowDate);
        List<MstVisitmemoInfo> ls = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstVisitmemoInfo, String> memoinfodao = helper.getMstVisitmemoInfoDao();

            QueryBuilder<MstVisitmemoInfo, String> queryBuilder = memoinfodao.queryBuilder();
            // 查询开始时间，结束时间
            queryBuilder.where().lt("startdate", nowDate).and().gt("enddate", nowDate);
            ls = queryBuilder.query();
            Log.d("tag", "list-->" + ls);
        } catch (SQLException e) {
            Log.d("tag", "list-->" + e);
            e.printStackTrace();
        }
        return ls;

    }

    //gridKey updateTime yyyy-MM-dd days lineId

    /**
     * 本地数据查询--线路
     * 
     * @return
     * */
    public List<MstVisitmemoInfo> searchLocalData(String terminalkey) {
        List<MstVisitmemoInfo> queryForFieldValues = new ArrayList<MstVisitmemoInfo>();
        try {
            String[] args;
            String nowDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstVisitmemoInfo, String> memoinfodao = helper.getMstVisitmemoInfoDao();
            memoinfodao.queryBuilder();
            //
            StringBuffer buffer = new StringBuffer();
            buffer.append("select m.terminalkey,m.credate,m.updatetime,m.enddate,m.startdate,m.content,m.comid,m.memokey ");
            buffer.append(" from mst_visitmemo_info m ");
            if ("-1".equals(terminalkey)) {
                args = new String[2];
                args[0] = nowDate;
                args[1] = nowDate;
                buffer.append(" where startdate<=?");
                buffer.append(" and enddate>=?");
            } else {
                args = new String[3];
                args[0] = terminalkey;
                args[1] = nowDate;
                args[2] = nowDate;
                buffer.append(" left join mst_terminalinfo_m p ");
                buffer.append(" on m.terminalkey = p.terminalkey ");
                buffer.append(" where p.routekey=?");
                buffer.append(" and startdate<=?");
                buffer.append(" and enddate>=?");
            }

            Log.d("tag", "buffer-sql->" + buffer.toString() + terminalkey);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery(buffer.toString(), args);

            MstVisitmemoInfo item;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            String startDate, endDate;
            while (cursor.moveToNext()) {
                item = new MstVisitmemoInfo();
                try {
                    String memokey = cursor.getString(cursor.getColumnIndex("memokey"));
                    item.setMemokey(memokey);
                    String new_terminalkey = cursor.getString(cursor.getColumnIndex("terminalkey"));
                    item.setTerminalkey(new_terminalkey);
                    String updatetime = cursor.getString(cursor.getColumnIndex("updatetime"));
                    Date date = updatetime == null ? new Date() : format.parse(updatetime);
                    item.setUpdatetime(date);
                    startDate = cursor.getString(cursor.getColumnIndex("startdate"));
                    if (!CheckUtil.isBlankOrNull(startDate)) {
                        item.setStartdate(cursor.getString(cursor.getColumnIndex("startdate")).substring(0, 8));
                    }
                    endDate = cursor.getString(cursor.getColumnIndex("enddate"));
                    if (!CheckUtil.isBlankOrNull(endDate)) {
                        item.setEnddate(cursor.getString(cursor.getColumnIndex("enddate")).substring(0, 8));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                item.setContent(cursor.getString(cursor.getColumnIndex("content")));
                item.setComid(cursor.getString(cursor.getColumnIndex("comid")));

                queryForFieldValues.add(item);
            }
            cursor.close();
            Log.d("tag", "list-size->" + queryForFieldValues.size());
        } catch (SQLException e) {
            Log.d("tag", "list-->" + e);
            e.printStackTrace();
        }
        return queryForFieldValues;
    }

    /**
     * 获取远程的数据
     */
    public void searchRemoteData(int lineNumber) {

        if (!NetStatusUtil.isNetValid(context)) {//网络不可用
            return;
        }
        /**线路*/
        String lineNumberStr = "";
        if (lineNumber != -1) {
            lineNumberStr = ReflectUtil.getFieldValueByName("routekey", ConstValues.lineLst.get(lineNumber)).toString();
            Log.d("tag", "search--1>" + lineNumberStr);
        }
        lineNumberStr = lineNumberStr == "-1" ? "" : lineNumberStr;
        Log.d("tag", "search-->" + lineNumberStr);
        int days = Integer.parseInt(ConstValues.kvMap.get("MST_VISITMEMO_INFO").getSyncDay());
        days = days == 0 ? 30 : days;
        isQuerying = true;
        StringBuffer buffer = new StringBuffer();
        //buffer.append("{gridKey:'").append(ConstValues.loginSession.getGridId());// "1-EPUI1");
        buffer.append("{gridKey:'").append(PrefUtils.getString(context, "gridId", ""));// "1-EPUI1");
        buffer.append("', updateTime:'").append(updateTime);// kv
        buffer.append("', lineId:'").append(lineNumberStr);
        buffer.append("', days:'").append((days + "'}"));// kv
        Log.d("tag", "request-->" + buffer);
        // 请求网络
        HttpUtil httpUtil = new HttpUtil(6000);
        httpUtil.configResponseTextCharset("ISO-8859-1");
        httpUtil.send("opt_get_visitmemo", buffer.toString(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                    Log.d("tag", "success--" + resObj.getResBody().getContent());

                    List<MstVisitmemoInfo> list = JsonUtil.parseList(resObj.getResBody().getContent(), MstVisitmemoInfo.class);
                    if (list.size() == 0) {
                        Log.d("tag", "isEnd-->");
                        isEnd = true;
                        isQuerying = false;
                    } else {
                        isEnd = false;
                        Log.d("tag", "end-->" + list.get(list.size() - 1).getUpdatetime());
                        updateTime = new SimpleDateFormat("yyyyMMdd").format(list.get(list.size() - 1).getUpdatetime());
                        Log.d("tag", "updateTime-->" + updateTime);
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

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
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

    /**
     * 异步数据处理
     * 
     * @return
     * */
    public class ToDayThingServiceThread extends Thread {

        @Override
        public void run() {

            getHandler().post(new Runnable() {

                @SuppressLint("NewApi")
                @Override
                public void run() {
                    if (getStyle() == 1) {// 本地数据库查询
                        // String json = JsonUtil
                        // .toJson(searchLocalData(getContext()));

                        // JSONTokener jsontokener = new JSONTokener(json);
                        // JSONArray ja = null;
                        // try {
                        // ja = (JSONArray) jsontokener.nextValue();
                        // } catch (JSONException e) {
                        // e.printStackTrace();
                        // }
                        // toDay_adapter = new ToDayThingAda/pter(getContext(),
                        // ja, R.drawable.bg_todaywords_fill);
                        todaything_lv_display.setAdapter(toDay_adapter);
                        todaything_lv_display.deferNotifyDataSetChanged();
                    } else if (getStyle() == 2) {// 网络请求
                        // getHttp("get_ter_details", getJson());
                        getGetResBody();// 返回值json
                    }
                }
            });
        }

    }

    @Override
    public void asynchronousDataHandler() {
        new ToDayThingServiceThread().start();
    }

    /**
     * 是否正在查询
     * @return
     */
    public boolean isQuerying() {
        return isQuerying;
    }

    /**
     * 
     * @param isQuerying
     */
    public void setQuerying(boolean isQuerying) {
        this.isQuerying = isQuerying;
    }

    @Override
    public void onFailure() {

    }

    public ToDayThingAdapter getToDay_adapter() {
        return toDay_adapter;
    }

    public void setToDay_adapter(ToDayThingAdapter toDay_adapter) {
        this.toDay_adapter = toDay_adapter;
    }

    public ListView getTodaything_lv_display() {
        return todaything_lv_display;
    }

    public void setTodaything_lv_display(ListView todaything_lv_display) {
        this.todaything_lv_display = todaything_lv_display;
    }

}
