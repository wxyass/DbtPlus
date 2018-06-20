package et.tsingtaopad.db.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.db.dao.MstAgencysupplyInfoDao;
import et.tsingtaopad.db.tables.MstAgencysupplyInfo;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：MstAgencysupplyinfoDaoImpl.java</br> 作者：chouyajie
 * </br> 创建时间：2015-8-27</br> 功能描述: </br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
public class MstAgencysupplyinfoDaoImpl extends
		BaseDaoImpl<MstAgencysupplyInfo, String> implements MstAgencysupplyInfoDao {

	/**
	 * @param connectionSource
	 * @throws SQLException
	 */
	public MstAgencysupplyinfoDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MstAgencysupplyInfo.class);
	}

	@Override
	public List<MstAgencysupplyInfo> agencysupply(SQLiteOpenHelper helper,String terminalkey) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("select * from mst_agencysupply_info ap, mst_agencyinfo_m am ");
		buffer.append(" where ap.upperkey = am.agencykey and coalesce(ap.deleteflag,'0') != '1'  ");
		buffer.append(" and ap.uppertype != '2' and ap.lowerkey= ? and ap.lowertype = '2' ");
//		buffer.append(" and ap.lowerkey= ? and ap.lowertype = '2' ");
		buffer.append(" and coalesce(ap.status, '0') != '1' and coalesce(am.deleteflag,'0') != '1' ");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(buffer.toString(), new String[]{terminalkey});
		List<MstAgencysupplyInfo> asList=new ArrayList<MstAgencysupplyInfo>();
		MstAgencysupplyInfo masInfo = null;
		while(cursor.moveToNext()){
			masInfo= new MstAgencysupplyInfo();
			masInfo.setUpperkey(cursor.getString(cursor.getColumnIndex("upperkey")));
			masInfo.setProductkey(cursor.getString(cursor.getColumnIndex("productkey")));
			asList.add(masInfo);
		}
		return asList;
	}

	
	
	
}
