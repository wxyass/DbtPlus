package et.tsingtaopad.db.dao;

import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.tables.MstPromomiddleInfo;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：MstPromotermInfoDao.java</br> 作者：@ray </br>
 * 创建时间：2013-12-7</br> 功能描述:终端参加活动信息表Dao层 </br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 * 
 */
public interface MstPromomiddleInfoDao extends Dao<MstPromomiddleInfo, String> {
	/**
	 * 获取活动中间表终端LIST
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
	 * @return
	 */

	public List<MstPromomiddleInfo> queryTermMiddleLst(SQLiteOpenHelper helper, String promKey, String lineKey, String type,
                                                       String date);
//	/**
//	 * 获取活动终端LIST
//	 * 
//	 * @param helper
//	 * @param promKey
//	 *            活动主键
//	 * @param lineKey
//	 *            线路主键字符串
//	 * @param type
//	 *            类型字符串
//	 * @param date
//	 *            时间
//	 * @return
//	 */
//	
//	public List<MstPromomiddleInfo> queryTermLst(SQLiteOpenHelper helper, String promKey, String lineKey,String type,
//			String date);

//	/**
//	 * 获取活动起止时间
//	 * @param promKey 活动主键
//	 * 
//	 */
//	public MstPromotionsM queryTime( SQLiteOpenHelper helper,String promKey);

}
