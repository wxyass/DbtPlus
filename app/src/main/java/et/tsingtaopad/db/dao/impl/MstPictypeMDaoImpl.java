package et.tsingtaopad.db.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.db.dao.MstPictypeMDao;
import et.tsingtaopad.db.tables.MstPictypeM;
import et.tsingtaopad.visit.shopvisit.camera.domain.CameraDataStc;
import et.tsingtaopad.visit.shopvisit.camera.domain.PictypeDataStc;


/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstPictypeMDaoImpl.java</br>
 * 作者：Administrator   </br>
 * 创建时间：2015-11-19</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class MstPictypeMDaoImpl extends BaseDaoImpl<MstPictypeM, String>
		implements MstPictypeMDao {

	public MstPictypeMDaoImpl(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, MstPictypeM.class);
	}

	/**
	 * 查询图片类型表中所有记录,用于初始化要拍几张图片
	 */
	@Override
	public List<PictypeDataStc> queryAllPictype(SQLiteOpenHelper helper) {
		List<PictypeDataStc> lst = new ArrayList<PictypeDataStc>();

		StringBuffer buffer = new StringBuffer();
		buffer.append("select areaid,pictypekey,pictypename,focus,orderno from MST_PICTYPE_M ORDER BY orderno");
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(buffer.toString(), new String[] {});
		PictypeDataStc pictypeDataStc;
		while (cursor.moveToNext()) {
			pictypeDataStc = new PictypeDataStc();
			pictypeDataStc.setAreaid(cursor.getString(cursor.getColumnIndex("areaid")));
			pictypeDataStc.setPictypekey(cursor.getString(cursor.getColumnIndex("pictypekey")));
			pictypeDataStc.setPictypename(cursor.getString(cursor
					.getColumnIndex("pictypename")));
			pictypeDataStc.setFocus(cursor.getString(cursor
					.getColumnIndex("focus")));
			pictypeDataStc.setOrderno(cursor.getString(cursor
					.getColumnIndex("orderno")));
			lst.add(pictypeDataStc);
		}
		return lst;
	}
}
