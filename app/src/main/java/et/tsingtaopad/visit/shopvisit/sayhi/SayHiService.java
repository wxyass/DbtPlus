package et.tsingtaopad.visit.shopvisit.sayhi;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.stmt.query.OrderBy;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.tables.CmmAreaM;
import et.tsingtaopad.db.tables.MstInvalidapplayInfo;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.syncdata.UploadDataService;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：SayHiService.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-3</br>      
 * 功能描述: 打招呼业务逻辑层</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class SayHiService extends ShopVisitService {
    
    private final String TAG = "SayHiService";
    
    public SayHiService(Context context, Handler handler) {
        super(context, handler);
    }
    
    /**
     * 保存拜访主表信息
     * 
     * @param visitInfo
     */
    public void updateVisit(MstVisitM visitInfo) {
        
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVisitMDao dao = helper.getDao(MstVisitM.class);
            dao.update(visitInfo);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访主表DAO对象失败", e);
        }
    }
    
    /**
     * 更新终端信息
     * 
     * @param termInfo      终端信息
     * @param prevSequence  修改前的拜访顺序
     */
    @SuppressWarnings("unchecked")
    public void updateTermInfo(MstTerminalinfoM termInfo,String prevSequence) {

        int msgId = -1;
        if (CheckUtil.isBlankOrNull(termInfo.getTerminalname())) {
            msgId = R.string.termadd_msg_invaltermname;
            
        } else if ("-1".equals(termInfo.getRoutekey()) ||
                          CheckUtil.isBlankOrNull(termInfo.getRoutekey())) {
            msgId = R.string.termadd_msg_invalbelogline;
            
        } else if ("-1".equals(termInfo.getTlevel()) ||
                            CheckUtil.isBlankOrNull(termInfo.getTlevel())) {
            msgId = R.string.termadd_msg_invaltermlevel;
            
        } else if ("-1".equals(termInfo.getProvince()) ||
                            CheckUtil.isBlankOrNull(termInfo.getProvince())) {
            msgId = R.string.termadd_msg_invalprov;
            
        } else if ("-1".equals(termInfo.getCity()) ||
                            CheckUtil.isBlankOrNull(termInfo.getCity())) {
            msgId = R.string.termadd_msg_invalcity;
            
        } else if ("-1".equals(termInfo.getCounty())) {
            msgId = R.string.termadd_msg_invalcountry;
            
        } else if (CheckUtil.isBlankOrNull(termInfo.getAddress())) {
            msgId = R.string.termadd_msg_invaladdress;
            
        } else if (CheckUtil.isBlankOrNull(termInfo.getContact())) {
            msgId = R.string.termadd_msg_invalcontact;
            
        } 
//        else if (CheckUtil.isBlankOrNull(termInfo.getMobile())) {
//            msgId = R.string.termadd_msg_invalmobile;
//            
//        }
/*        else if (CheckUtil.isBlankOrNull(termInfo.getSequence())) {
            msgId = R.string.termadd_msg_invalsequence;
            
        } */else if ("-1".equals(termInfo.getSellchannel()) ||
                    CheckUtil.isBlankOrNull(termInfo.getSellchannel())) {
            msgId = R.string.termadd_msg_invalsellchannel;
            
        } else if ("-1".equals(termInfo.getMainchannel()) ||
                    CheckUtil.isBlankOrNull(termInfo.getMainchannel())) {
            msgId = R.string.termadd_msg_invalmainchannel;
            
        } else if ("-1".equals(termInfo.getMinorchannel()) ||
                    CheckUtil.isBlankOrNull(termInfo.getMinorchannel())) {
            msgId = R.string.termadd_msg_invalminorchannel;
        }
        
        TextView sureBt = (TextView)((Activity)context).findViewById(R.id.banner_navigation_bt_confirm);
        sureBt.setTag(msgId);
        // 弹出提示信息
        if (msgId != -1) {
            sendMsg(context, msgId, ConstValues.WAIT2);
        } else {
            try {
                DatabaseHelper helper = DatabaseHelper.getHelper(context);
                MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
                /*
                SQLiteDatabase db = helper.getReadableDatabase();
                    List<MstTerminalinfoM> mstTerminalinfoMList = new ArrayList<MstTerminalinfoM>();
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("select  t.terminalkey,t.sequence from mst_terminalinfo_m t ");
                    buffer.append("where routekey = '");
                    buffer.append(termInfo.getRoutekey());
                    buffer.append("' ");
                    buffer.append("order by (sequence+0) asc, orderbyno, terminalname");
                    Cursor cursor  = db.rawQuery(buffer.toString(), null);
                    MstTerminalinfoM mstTerminalinfoM;
                    while (cursor.moveToNext()) {
                        mstTerminalinfoM =new MstTerminalinfoM();
                        mstTerminalinfoM.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
                        mstTerminalinfoM.setSequence(cursor.getString(cursor.getColumnIndex("sequence")));
                        mstTerminalinfoMList.add(mstTerminalinfoM);
                    }*/
                // 更新终端信息
                dao.update(termInfo);
            } catch (SQLException e) {
                Log.e(TAG, "获取终端表DAO对象失败", e);
            }
        }
    }


    
    /**
     * 打招呼中无效终端的提示框
     * @author 吴欣伟
     * @param visitM 拜访记录
     * @param termInfo 终端档案信息
     */
    public void dialogInValidTerm(final MstVisitM visitM, final MstTerminalinfoM termInfo ,String seeFlag) {

        // 加载弹出窗口layout
        final View itemForm = LayoutInflater.from(context).inflate(R.layout.shopvisit_sayhi_invalidterm,null);
        final AlertDialog invalidTermDialgo = new AlertDialog.Builder(context).setCancelable(false).create();
        invalidTermDialgo.setView(itemForm, 0, 0, 0, 0);
        invalidTermDialgo.show();
        // 确定
        Button sureBt = (Button)itemForm.findViewById(R.id.invalid_bt_baocun);
        if(ConstValues.FLAG_1.equals(seeFlag)){
            sureBt.setEnabled(false);
            }
        sureBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //AndroidDatabaseConnection connection = null;
                    try {
                        DatabaseHelper helper = DatabaseHelper.getHelper(context);
                        Dao<MstTerminalinfoM, String> terDao = helper.getMstTerminalinfoMDao();
                        Dao<MstInvalidapplayInfo, String> invalidDao = helper.getMstInvalidapplayInfoDao();
                        /*connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
                        connection.setAutoCommit(false);*/
                        
                        //更改终端档案主表的状态:无效
                        //终端状态还是有效，但是申请表为未审核
                        termInfo.setStatus(ConstValues.FLAG_3);
                        termInfo.setPadisconsistent("0");
                        
                        //添加无效申请
                        MstInvalidapplayInfo mstInvalid = new MstInvalidapplayInfo();
                        mstInvalid.setApplaykey(FunUtil.getUUID());
                        mstInvalid.setVisitkey(visitM.getVisitkey());
                        mstInvalid.setTerminalkey(termInfo.getTerminalkey());
                        mstInvalid.setStatus(ConstValues.FLAG_0);
                        mstInvalid.setVisitdate(visitM.getVisitdate());
                        mstInvalid.setApplaytype(ConstValues.FLAG_0);
                        RadioGroup rg = (RadioGroup) itemForm.findViewById(R.id.invalid_rg_reason);
                        RadioButton rb = (RadioButton) itemForm.findViewById(rg.getCheckedRadioButtonId());
                        mstInvalid.setApplaycause(rb.getText().toString());
                        EditText content = (EditText) itemForm.findViewById(R.id.invalid_et_reason);
                        mstInvalid.setContent(content.getText().toString());
                        //mstInvalid.setApplayuser(ConstValues.loginSession.getUserCode());
                        mstInvalid.setApplayuser(PrefUtils.getString(context, "userCode", ""));
                        mstInvalid.setApplaydate(DateUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                        mstInvalid.setPadisconsistent(ConstValues.FLAG_0);
                        mstInvalid.setCredate(new Date());
                        //mstInvalid.setCreuser(ConstValues.loginSession.getUserCode());
                        mstInvalid.setCreuser(PrefUtils.getString(context, "userCode", ""));
                        mstInvalid.setUpdatetime(new Date());
                        //mstInvalid.setUpdateuser(ConstValues.loginSession.getUserCode());
                        mstInvalid.setUpdateuser(PrefUtils.getString(context, "userCode", ""));

                        terDao.createOrUpdate(termInfo);
                        invalidDao.create(mstInvalid);
                        DbtLog.logUtils(TAG, "无效终端记录保存成功");
                        
                        //无效终端申请上传服务器
                        UploadDataService service = new UploadDataService(context, handler);
                        service.upload_terminal(false, termInfo.getTerminalkey(), ConstValues.WAIT2);
                        ViewUtil.sendMsg(context, R.string.agencyvisit_msg_oksave);
                        invalidTermDialgo.dismiss();
                        
                        ((Activity)context).finish();
                        DbtLog.logUtils(TAG, "无效终端申请返回终端列表");
                        
                        //connection.commit(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        DbtLog.logUtils(TAG, "无效终端申请失败");
                        try {
                            //connection.rollback(null);
                            ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
            }
        });
        
        // 取消
        Button cancelBt = (Button)itemForm.findViewById(R.id.invalid_bt_cancle);
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(ConstValues.WAIT3);
                invalidTermDialgo.dismiss();
            }
        });
    }
    
    /**
     * 向界面发送提示消息
     * 
     * @param context   上下文环境
     * @param msg       消息内容或对应的资源ID
     */
    public void sendMsg(Context context, Object msg, int what) {
        
        if (context == null || msg == null) return;
        
        Bundle bundle = new Bundle();
        Message message = new Message();

        if (msg instanceof Integer) {
            bundle.putString("msg", context
                    .getString(Integer.valueOf(msg.toString())));
            bundle.putInt("msgId", Integer.parseInt(msg.toString()));
        } else {
            bundle.putString("msg", String.valueOf(msg));
        }
        message.what = what;
        message.setData(bundle);
        handler.sendMessage(message);
    }
    
    // 根据areacode查询CMM_AREA_M表中记录
	public String getAreaName(String areacode) {
	        
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();

		String querySql = "SELECT *  FROM CMM_AREA_M WHERE areacode = ?";
		Cursor cursor = db.rawQuery(querySql, new String[]{areacode});
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex("areaname");
		String areaname;
		try {
			areaname = cursor.getString(cursor.getColumnIndex("areaname"));
		} catch (Exception e) {
			areaname = "";
		}
		return areaname;
    }
	// 根据areacode查询CMM_AREA_M表中记录
	public String getVisitpositionName(String diccode) {
		
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String querySql = "SELECT *  FROM CMM_DATADIC_M WHERE diccode = ?";
		Cursor cursor = db.rawQuery(querySql, new String[]{diccode});
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex("dicname");
		String VisitpositionName;
		try {
			VisitpositionName = cursor.getString(cursor.getColumnIndex("dicname"));
		} catch (Exception e) {
			VisitpositionName = "";
		}
		return VisitpositionName;
	}
}
