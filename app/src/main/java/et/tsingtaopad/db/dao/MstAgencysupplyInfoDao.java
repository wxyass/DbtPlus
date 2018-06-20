package et.tsingtaopad.db.dao;

import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstAgencysupplyInfo;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstAgencysupplyInfoDao.java</br>
 * 作者：chouyajie   </br>	
 * 创建时间：2015-8-27</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstAgencysupplyInfoDao extends Dao<MstAgencysupplyInfo, String> {
	
	public List<MstAgencysupplyInfo> agencysupply(SQLiteOpenHelper helper, String terminalkey);

	
}
