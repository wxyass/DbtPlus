package et.tsingtaopad.visit.termadd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstInvalidapplayInfo;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.NetStatusUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.visit.shopvisit.sayhi.domain.MstTerminalInfoMStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermAddService.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-28</br>
 * 功能描述: 新增终端业务逻辑</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermAddService {

    private final String TAG = "TermAddService";

    private Context context;
    private Handler handler;
    private ProgressDialog saveTermPd;

    public TermAddService(Context context, Handler handler, ProgressDialog saveTermPd) {
        this.context = context;
        this.handler = handler;
        this.saveTermPd = saveTermPd;
    }

    /**
     * 新增终端
     *
     * @param info 终端信息
     */
    public void addTerm(final MstTerminalinfoM info) {

        int msgId = -1;
        if (CheckUtil.isBlankOrNull(info.getTerminalname())) {
            msgId = R.string.termadd_msg_invaltermname;

        } else if ("-1".equals(info.getRoutekey())) {
            msgId = R.string.termadd_msg_invalbelogline;

        } else if ("-1".equals(info.getTlevel())) {
            msgId = R.string.termadd_msg_invaltermlevel;

        }
        /*else if ("-1".equals(info.getProvince()))
        {
            msgId = R.string.termadd_msg_invalprov;

        }
        else if ("-1".equals(info.getCity()))
        {
            msgId = R.string.termadd_msg_invalcity;

        }*/
        else if ("-1".equals(info.getCounty())) {
            msgId = R.string.termadd_msg_invalcountry;

        } else if (CheckUtil.isBlankOrNull(info.getAddress())) {
            msgId = R.string.termadd_msg_invaladdress;

        } else if (CheckUtil.isBlankOrNull(info.getContact())) {
            msgId = R.string.termadd_msg_invalcontact;

        } else if (CheckUtil.isBlankOrNull(info.getMobile())) {
            msgId = R.string.termadd_msg_invalmobile;

        } else if (info.getMobile().length() > 30) {
            msgId = R.string.termadd_msg_invalmobilelength;

        }
        /*else if (CheckUtil.isBlankOrNull(info.getSequence())) {
            msgId = R.string.termadd_msg_invalsequence;
            
          }*/
        else if ("-1".equals(info.getSellchannel())) {
            msgId = R.string.termadd_msg_invalsellchannel;

        } else if ("-1".equals(info.getMainchannel())) {
            msgId = R.string.termadd_msg_invalmainchannel;

        } else if ("-1".equals(info.getMinorchannel())) {
            msgId = R.string.termadd_msg_invalminorchannel;
        } else if (TermAddService.isRepeatTermAdd(context, info)) {
            msgId = R.string.termadd_msg_repeat;
        }
        /**
         else if (!NetStatusUtil.isNetValid(context)) {
         msgId = R.string.msg_err_nettatusfail;
         }**/

        // 弹出提示信息
        if (msgId != -1) {
            sendMsg(context, msgId, ConstValues.WAIT4);

        } else {
            //info.setCreuser(ConstValues.loginSession.getUserCode());
            info.setCreuser(PrefUtils.getString(context, "userCode", ""));
            //info.setUpdateuser(ConstValues.loginSession.getUserCode());
            info.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
            // 上传数据
            if (!NetStatusUtil.isNetValid(context)) {
                sendMsg(context, R.string.msg_err_nettatusfail, ConstValues.WAIT3);
                saveTermAddFromShared(info);
            } else {
                //进行上传数据
                upload_add_terminal(info, ConstValues.WAIT3);
            }
        }
    }

    /**
     * 新增终端进行上传(上传成功才能保存到本地)
     *
     * @param info
     * @param whatId
     */
    public void upload_add_terminal(final MstTerminalinfoM info, final int whatId) {
        //增加终端ProgressDialog
        if (saveTermPd != null) {
            saveTermPd.show();
        }
        Map<String, String> childDatas = new HashMap<String, String>();
        List<Map<String, String>> mainDatas = new ArrayList<Map<String, String>>();
        List<MstInvalidapplayInfo> mInvalidapplayInfos_terminal = new ArrayList<MstInvalidapplayInfo>();
        info.setPadisconsistent("1");
        MstInvalidapplayInfo childInvalidapplayInfos = new MstInvalidapplayInfo();
        childDatas.put("MST_TERMINALINFO_M", JsonUtil.toJson(info));
        mInvalidapplayInfos_terminal.add(childInvalidapplayInfos);
        childInvalidapplayInfos.setApplaykey(FunUtil.getUUID());
        childInvalidapplayInfos.setStatus("1");
        childInvalidapplayInfos.setApplaycause("1");
        childInvalidapplayInfos.setDeleteflag("0");
        childDatas.put("MST_INVALIDAPPLAY_INFO", JsonUtil.toJson(mInvalidapplayInfos_terminal));
        mainDatas.add(childDatas);
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.configTimeout(3 * 60 * 1000);
        httpUtil.configResponseTextCharset("ISO-8859-1");
        String json = JsonUtil.toJson(mainDatas);
        Log.e(TAG, "终端上传send:" + mainDatas.size() + json);
        httpUtil.send("opt_save_terminal", json, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (saveTermPd != null || saveTermPd.isShowing()) {
                    saveTermPd.dismiss();
                }
                ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                Log.i(TAG, "新增终端上传" + resObj.getResBody().getContent());
                Map<String, Object> termIdMap = JsonUtil.parseMap(resObj.getResBody().getContent());
                String[] val;
                if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {

                    val = String.valueOf(termIdMap.get(info.getTerminalkey())).split(",");
                    if (val.length == 3) {
                        delTermAddFromShared(context, info);
                        info.setTerminalkey(val[1]);
                        info.setTerminalcode(val[2]);
                        //上传成功以后进行保存
                        saveTerm(info);
                    }
                } else {
                    String msg = (String) termIdMap.get(info.getTerminalkey());
                    String errorMsg = "新建终端出错，出错信息为:相同路线、相同名称、相同类型的终端、一天只能新建一次，请注意是否重复新建了请联系管理员";
                    if (errorMsg.equals(msg)) {
                        sendMsg(context, errorMsg, ConstValues.WAIT4);
                    } else {
                        sendMsg(context, R.string.termadd_msg_addfail, ConstValues.WAIT3);
                        saveTermAddFromShared(info);
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                saveTermAddFromShared(info);
                if (saveTermPd != null || saveTermPd.isShowing()) {
                    saveTermPd.dismiss();
                }
                sendMsg(context, R.string.termadd_msg_addfail, ConstValues.WAIT3);
            }

        });
    }

    //    /***
    //     * 保存新增终端信息到本地数据库
    //     * @param info
    //     */
    //    public boolean saveTermAdd(MstTerminalinfoM info)
    //    {
    //        boolean isFlag = false;
    //        AndroidDatabaseConnection connection = null;
    //        try
    //        {
    //            DatabaseHelper helper = DatabaseHelper.getHelper(context);
    //            Dao<MstTerminalinfoM, String> termDao = helper.getMstTerminalinfoMDao();
    //            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
    //            connection.setAutoCommit(false);
    //            // 保存终端
    //            termDao.createOrUpdate(info);
    //            connection.commit(null);
    //            isFlag = true;
    //        }
    //        catch (Exception e)
    //        {
    //            e.printStackTrace();
    //            Log.e(TAG, "新增终端保存失败", e);
    //        }
    //        return isFlag;
    //    }

    //    /***
    //     * 删除新增终端信息
    //     * @param info
    //     * @return
    //     */
    //    public boolean delTermAdd(MstTerminalinfoM info)
    //    {
    //        boolean isFlag = false;
    //        AndroidDatabaseConnection connection = null;
    //        try
    //        {
    //            DatabaseHelper helper = DatabaseHelper.getHelper(context);
    //            Dao<MstTerminalinfoM, String> termDao = helper.getMstTerminalinfoMDao();
    //            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
    //            connection.setAutoCommit(false);
    //            // 保存终端
    //            termDao.delete(info);
    //            connection.commit(null);
    //            isFlag = true;
    //        }
    //        catch (Exception e)
    //        {
    //            e.printStackTrace();
    //            Log.e(TAG, "删除新增终端保存", e);
    //        }
    //        return isFlag;
    //
    //    }

    //    /**
    //     * 获取新增终端
    //     * @return
    //     */
    //    public List<MstTerminalinfoM> getTermAddList()
    //    {
    //        List<MstTerminalinfoM> list = new ArrayList<MstTerminalinfoM>();
    //        // 保存
    //        try
    //        {
    //            DatabaseHelper helper = DatabaseHelper.getHelper(context);
    //            Dao<MstTerminalinfoM, String> dao = helper.getMstTerminalinfoMDao();
    //            QueryBuilder<MstTerminalinfoM, String> qb = dao.queryBuilder();
    //            Where<MstTerminalinfoM, String> where = qb.where();
    //            where.eq("terminalcode", "temp");
    //            list = qb.query();
    //        }
    //        catch (Exception e)
    //        {
    //            e.printStackTrace();
    //            Log.e(TAG, "获取新增终端列表异常", e);
    //        }
    //        return list;
    //    }

    /***
     * 判断相同路线、相同名称、相同类型的终端、一天只能新建一次
     * @param context
     * @param term
     * @return
     */
    public static boolean isRepeatTermAdd(Context context, MstTerminalinfoM term) {
        List<MstTerminalinfoM> list = getTermAddListFromShared(context);
        boolean isRepeat = false;
        for (MstTerminalinfoM terminalinfoM : list) {
            //新建终端出错，出错信息为:相同路线、相同名称、相同类型的终端、一天只能新建一次，请注意是否重复新建了请联系管理员
            if (terminalinfoM.getRoutekey().equals(term.getRoutekey()) && terminalinfoM.getTerminalname().equals(term.getTerminalname()) && terminalinfoM.getTlevel().equals(term.getTlevel())) {
                String oldDate = DateUtil.formatDate(terminalinfoM.getCredate(), DateUtil.DEFAULT_DATE_FORMAT);
                String newDate = DateUtil.formatDate(term.getCredate(), DateUtil.DEFAULT_DATE_FORMAT);
                if (oldDate.equals(newDate) && !terminalinfoM.getTerminalkey().equals(term.getTerminalkey())) {
                    isRepeat = true;
                }
            }
        }
        return isRepeat;
    }

    /***
     * 获取终端失败集合
     * @return
     */
    public static List<MstTerminalinfoM> getTermAddListFromShared(Context context) {
        List<MstTerminalinfoM> list = new ArrayList<MstTerminalinfoM>();
        try {
            SharedPreferences sp = context.getSharedPreferences("termAddList", 0);
            if (sp != null) {
                String termListString = sp.getString("termlist", "");
                if (!"".equals(termListString)) {
                    list = JsonUtil.parseList(termListString, MstTerminalinfoM.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /***
     * 保存终端失败信息
     * @param term
     */
    public void saveTermAddFromShared(MstTerminalinfoM term) {
        try {
            SharedPreferences sp = context.getSharedPreferences("termAddList", 0);
            List<MstTerminalinfoM> list = getTermAddListFromShared(context);
            boolean isUpdate = false;
            for (int i = list.size() - 1; i >= 0; i--) {
                MstTerminalinfoM mstTerminalinfoM = list.get(i);
                if (mstTerminalinfoM.getTerminalkey().equals(term.getTerminalkey())) {
                    list.remove(i);
                    list.add(i, term);
                    isUpdate = true;
                }
            }
            if (!isUpdate) {
                list.add(term);
            }
            sp.edit().putString("termlist", JsonUtil.toJson(list)).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 删除终端失败信息
     * @param term
     */
    public static void delTermAddFromShared(Context context, MstTerminalinfoM term) {
        try {
            SharedPreferences sp = context.getSharedPreferences("termAddList", 0);
            List<MstTerminalinfoM> list = getTermAddListFromShared(context);
            for (int i = list.size() - 1; i >= 0; i--) {
                MstTerminalinfoM mstTerminalinfoM = list.get(i);
                if (mstTerminalinfoM.getTerminalkey().equals(term.getTerminalkey())) {
                    list.remove(i);
                }
            }
            sp.edit().putString("termlist", JsonUtil.toJson(list)).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存终端到本地
     *
     * @param info
     */
    private void saveTerm(final MstTerminalinfoM info) {
        // 保存
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstTerminalinfoM, String> termDao = helper.getMstTerminalinfoMDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            // 保存终端
            termDao.createOrUpdate(info);
            connection.commit(null);

            //通知终端列表界面刷新
            if (ConstValues.handler != null) {
                ConstValues.handler.sendEmptyMessage(ConstValues.WAIT3);
            }
            sendMsg(context, R.string.termadd_msg_addsuccess, ConstValues.WAIT2);
        } catch (Exception e) {
            Log.e(TAG, "新增终端保存失败", e);
            try {
                connection.rollback(null);
                sendMsg(context, R.string.termadd_msg_addfail, ConstValues.WAIT3);
            } catch (SQLException e1) {
                Log.e(TAG, "新增终端保存失败后回滚失败", e1);
            }
        }
    }

    /**
     * 向界面发送提示消息
     *
     * @param context 上下文环境
     * @param msg     消息内容或对应的资源ID
     */
    public void sendMsg(Context context, Object msg, int what) {

        if (context == null || msg == null)
            return;

        Bundle bundle = new Bundle();
        Message message = new Message();

        if (msg instanceof Integer) {
            bundle.putString("msg", context.getString(Integer.valueOf(msg.toString())));
            bundle.putInt("msgId", Integer.parseInt(msg.toString()));
        } else {
            bundle.putString("msg", String.valueOf(msg));
        }
        message.what = what;
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public MstTerminalInfoMStc getFirstTermnalData() {

        MstTerminalInfoMStc stc = new MstTerminalInfoMStc();
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String querySql = "select * from MST_TERMINALINFO_M LEFT JOIN CMM_AREA_M ON MST_TERMINALINFO_M.province=CMM_AREA_M.areacode LIMIT 1";
        Cursor cursor = db.rawQuery(querySql, null);
        while (cursor.moveToNext()) {
            stc.setProvince(cursor.getString(cursor.getColumnIndex("province")));
            stc.setProvName(cursor.getString(cursor.getColumnIndex("areaname")));

        }
        String querySql2 = "select * from MST_TERMINALINFO_M LEFT JOIN CMM_AREA_M ON MST_TERMINALINFO_M.city=CMM_AREA_M.areacode LIMIT 1";
        Cursor cursor2 = db.rawQuery(querySql2, null);
        while (cursor2.moveToNext()) {

            stc.setCity(cursor2.getString(cursor.getColumnIndex("city")));
            stc.setCityName(cursor2.getString(cursor.getColumnIndex("areaname")));

        }
        String querySql3 = "select * from MST_TERMINALINFO_M LEFT JOIN CMM_AREA_M ON MST_TERMINALINFO_M.county=CMM_AREA_M.areacode LIMIT 1";
        Cursor cursor3 = db.rawQuery(querySql3, null);
        while (cursor3.moveToNext()) {

            stc.setCounty(cursor3.getString(cursor.getColumnIndex("county")));
            stc.setCountryName(cursor3.getString(cursor.getColumnIndex("areaname")));
        }
        return stc;
    }
}
