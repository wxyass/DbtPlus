package et.tsingtaopad.db.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.db.dao.MstPromomiddleInfoDao;
import et.tsingtaopad.db.tables.MstPromomiddleInfo;
import et.tsingtaopad.db.tables.MstPromotermInfo;
import et.tsingtaopad.tools.DateUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：MstPromotermInfoDaoImpl.java</br> 作者：@ray </br>
 * 创建时间：2013-12-7</br> 功能描述:终端参加活动信息表Dao层 </br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
public class MstPromotermInfoDaoImpl extends BaseDaoImpl<MstPromomiddleInfo, String> implements MstPromomiddleInfoDao {

	public MstPromotermInfoDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MstPromomiddleInfo.class);
	}

	/**
	 * 获取活动中间表中的终端
	 * 
	 * @param helper
	 * @param promKey
	 *            活动主键
	 * @param lineKey
	 *            线路主键字符串
	 * @param type
	 *            类型字符串
	 * @param date
	 *            时间 
	 *            MST_PROMOMIDDLE_INFO
	 * @return
	 */  
	@Override
	public List<MstPromomiddleInfo> queryTermMiddleLst(SQLiteOpenHelper helper, String promKey, String lineKey, String type, String date) {
		List<MstPromomiddleInfo> middleLst = new ArrayList<MstPromomiddleInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from mst_promomiddle_info m where m.ptypekey=" + "'"+promKey +"'"+ " and m.routekey in (" + lineKey
				+ ") and m.tlevel in(" + type + ")  and substr(m.searchdate,1,10) = '"+date+"'";
//		String sql = "select * from mst_promomiddle_info m where m.ptypekey=" + "'"+promKey +"'"+ " and m.routekey in (" + lineKey
//				+ ") and m.tlevel in(" + type + ")";
//		String sql = "select * from mst_promomiddle_info m where m.ptypekey='1212' and m.routekey in ('11'"
//				+ ") and m.tlevel in('1')  and m.searchdate = date( '2013-12-03','yyyy-mm-dd')";
		Log.e("MstPromotermInfoDaoImpl", sql);
		Cursor cursor = db.rawQuery(sql, null);
		
		while (cursor.moveToNext()) {
			Log.e("MstPromotermInfoDaoImpl", "cursor--while>"+cursor.getCount());
			MstPromomiddleInfo middleInfo = new MstPromomiddleInfo();
			middleInfo.setCompleteterms(cursor.getString(cursor.getColumnIndex("completeterms")));
			middleInfo.setNotcomterms(cursor.getString(cursor.getColumnIndex("notcomterms")));
			middleInfo.setCompletenum(cursor.getInt(cursor.getColumnIndex("completenum")));
			middleInfo.setNotcomnum(cursor.getInt(cursor.getColumnIndex("notcomnum")));
			middleLst.add(middleInfo);
		}
		cursor.close();
		return middleLst;
	}

//	/**
//	 * 获取活动中间表中的终端(多表查询)
//	 * 
//	 * @param helper
//	 * @param promKey
//	 *            活动主键
//	 * @param lineKey
//	 *            线路主键字符串
//	 * @param type
//	 *            类型字符串
//	 * @param date
//	 *            时间 MST_PROMOMIDDLE_INFO
//	 * @return
//	 */
//	@Override
//	public List<MstPromomiddleInfo> queryTermLst(SQLiteOpenHelper helper, String promKey, String lineKey, String type, String date) {
//		return null;
//	}

	// public MstPromotionsM queryTime(SQLiteOpenHelper helper, String promStr)
	// {
	// MstPromotionsM info = new MstPromotionsM();
	// SQLiteDatabase db = helper.getReadableDatabase();
	// String sql =
	// "select MST_PROMOTIONS_M.STARTDATE,MST_PROMOTIONS_M.ENDDATE FROM MST_PROMOTIONS_M WHERE MST_PROMOTIONS_M.PROMOTNAME=?";
	// Cursor cursor = db.rawQuery(sql, new String[] { promStr });
	// info.setStartdate(cursor.getString(cursor.getColumnIndex("startdate")));
	// info.setEnddate(cursor.getString(cursor.getColumnIndex("enddate")));
	// return info;
	// }

}
