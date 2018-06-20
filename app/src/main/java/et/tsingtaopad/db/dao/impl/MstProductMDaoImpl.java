package et.tsingtaopad.db.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstProductMDao;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.initconstvalues.domain.KvStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstProductMDaoImpl.java</br>
 * 作者：吴欣伟   </br>
 * 创建时间：2013-12-12</br>      
 * 功能描述: 青啤产品信息主表Dao层</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class MstProductMDaoImpl extends BaseDaoImpl<MstProductM, String> implements MstProductMDao {

	public MstProductMDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MstProductM.class);
	}

	/**
	 * 获取指标状态查询中的可选择的产品数据
	 * 
	 * @param helper
	 * @return
	 */
	@Override
	public List<KvStc> getIndexPro(DatabaseHelper helper) {
		List<KvStc> proLst = new ArrayList<KvStc>();
		StringBuffer buffer = new StringBuffer();
		buffer.append("select mpm.productkey,mpm.proname from MST_PRODUCTAREA_INFO mpi ");
		buffer.append("join mst_product_m mpm on mpi.productkey = mpm.productkey ");
		buffer.append(" and coalesce(mpi.status,'0') != '1' ");
		buffer.append(" and coalesce(mpm.status,'0') != '1' ");
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(buffer.toString(), null);
		while (cursor.moveToNext()) {
			KvStc pro = new KvStc();
			pro.setKey(cursor.getString(cursor.getColumnIndex("productkey")));
			pro.setValue(cursor.getString(cursor.getColumnIndex("proname")));
			proLst.add(pro);
		}
		return proLst;
	}

}
