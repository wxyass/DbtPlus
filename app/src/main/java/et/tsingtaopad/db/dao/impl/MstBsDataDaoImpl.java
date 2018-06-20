package et.tsingtaopad.db.dao.impl;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.db.dao.MstBsDataDao;
import et.tsingtaopad.db.tables.MstBsData;
import et.tsingtaopad.db.tables.MstRouteM;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：MstRouteMDao.java</br> 
 * 作者：hongen </br>
 * 创建时间：2013-11-27</br> 
 * 功能描述: 线路档案主表DAO层</br> 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
public class MstBsDataDaoImpl extends 
            BaseDaoImpl<MstBsData, String> implements MstBsDataDao {

	public MstBsDataDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MstBsData.class);
	}
	
}
