package et.tsingtaopad.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstTermLedgerInfo;
import et.tsingtaopad.visit.termtz.domain.MstTzTermInfo;
import et.tsingtaopad.visit.termtz.domain.TzGridAreaInfo;
import et.tsingtaopad.visit.termtz.domain.TzTermProInfo;


/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstTermLedgerInfoDao.java</br>
 * 作者：ywm   </br>
 * 创建时间：2015-12-8</br>      
 * 功能描述: 终端台账表操作</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstTermLedgerInfoDao extends Dao<MstTermLedgerInfo, String> {

	/**
	 * 根据经销商主键删除记录
	 * 
	 * @param helper
	 * @param agencykey 经销商主键
	 */
	public void deleteRecord(
            SQLiteOpenHelper helper, String agencykey);
	
	/**
	 * 根据经销商主键终端主键产品主键,更改进货量
	 * 
	 * @param helper
	 * @param agencykey   经销商主键
	 * @param terminalkey 终端主键
	 * @param productkey  产品主键
	 */
	public void updataPurchase(SQLiteOpenHelper helper, String purchase, String agencykey, String terminalkey, String productkey);

	/**
	 * 查询所有需要上传的记录
	 * 
	 * @param helper
	 * @param padisconsistent 是否需要上传(0需要上传 , 1不需要上传)
	 * @return
	 */
	public ArrayList<MstTermLedgerInfo> getNeedUpAll(
            SQLiteOpenHelper helper, String padisconsistent, String terminalkey, String agencykey);
	
	/**
	 * 根据经销商主键终端主键产品主键  更新padisconsistent上传状态
	 * (根据经销商主键终端主键产品主键,更新上传状态为1(不需要上传))
	 * 
	 * @param helper
	 * @param agencykey   经销商主键
	 * @param terminalkey 终端主键
	 * @param productkey  产品主键
	 */
	public void updataPadisConsis(SQLiteOpenHelper helper, String padisconsistent, String agencykey, String terminalkey, String productkey);

	/**
	 * 根据销商主键终端主键产品主键   更改上传成功状态yesup
	 * 
	 * 0上传成功  1上传失败
	 * 
	 * @param helper
	 * @param yesup       0上传成功  1上传失败
	 * @param agencykey   经销商主键
	 * @param terminalkey 终端主键
	 * @param productkey  产品主键
	 */
	public void updataYesUp(SQLiteOpenHelper helper, String yesup, String agencykey, String terminalkey, String productkey);

	/**
	 * 根据经销商主键,查询所有终端数据(注意排序)
	 * 
	 * @param helper
	 * @param agencykey 经销商主键
	 * @return
	 */
	public ArrayList<MstTzTermInfo> queryTermAll(
            SQLiteOpenHelper helper, String agencykey);
	
	/**
	 * 根据终端主键,查询此终端下产品
	 * 
	 * @param helper
	 * @param terminalkey 终端主键
	 * @return
	 */
	public ArrayList<TzTermProInfo> queryTermProAll(
            SQLiteOpenHelper helper, String terminalkey, String agencykey);
	
	/**
	 * 根据终端名称,模糊查询终端数据
	 * 
	 * @param helper
	 * @param terminalname
	 * @return
	 */
	public ArrayList<MstTzTermInfo> queryTermByName(SQLiteOpenHelper helper, String terminalname, String agencykey);
	
	/**
	 * 根据经销商主键,查询所以区域名称定格名称,以定格主键分组
	 * 
	 * @param helper
	 * @param agencykey 经销商主键
	 * @return
	 */
	public ArrayList<TzGridAreaInfo> queryAreaGridAll(
            SQLiteOpenHelper helper, String agencykey);

	/**
	 * 根据区域名称定格名称,查询终端
	 * 
	 * @param helper
	 * @param areaname
	 * @param gridname
	 * @return
	 */
	public ArrayList<MstTzTermInfo> queryTermByAreaGrid(DatabaseHelper helper,
                                                        String areaname, String gridname, String agencykey);

	/**
	 * 根据记录下载时间,删除一条记录
	 * 
	 * @param helper
	 * @param downTime
	 */
	public void deleteRecordByDowntime(DatabaseHelper helper, String downTime);

	/**
	 * 根据定格名称  以及终端名称 模糊查询终端列表
	 * 
	 * @param helper
	 * @param areaname 区域
	 * @param gridname 定格
	 * @param termName 终端名称
	 * @param agencykey 经销商主键
	 * @return
	 */
	public ArrayList<MstTzTermInfo> queryTermByAreaGridAndname(
            DatabaseHelper helper, String areaname, String gridname,
            String termName, String agencykey);
	
	/**
	 * 根据经销商主键终端主键产品主键  更新purchasetime进货时间
	 * (根据经销商主键终端主键产品主键,更新上传状态为1(不需要上传))
	 * 
	 * @param helper
	 * @param agencykey   经销商主键
	 * @param terminalkey 终端主键
	 * @param productkey  产品主键
	 */
	public void updataPurchasetime(SQLiteOpenHelper helper, String purchasetime, String agencykey, String terminalkey, String productkey);
	
	/**
	 * 通过区域定格名称 获取该定格下的路线
	 * 
	 * @param helper
	 * @param areaname
	 * @param gridname
	 * @param agencykey
	 * @return
	 */
	public ArrayList<String> queryAllRouteByAreaGrid(DatabaseHelper helper,
                                                     String areaname, String gridname, String agencykey);

	/**
	 * 通过区域定格名称及线路 获取终端
	 * 
	 * @param helper
	 * @param areaname
	 * @param gridname
	 * @param routename
	 * @param agencykey
	 * @return
	 */
	public ArrayList<MstTzTermInfo> queryTermByAreaGridAndRoutename(
            DatabaseHelper helper, String areaname, String gridname,
            String routename, String agencykey);

	/**
	 * 通过区域定格名称及线路及终端名称  获取终端(模糊查询)
	 * 
	 * @param helper
	 * @param areaname
	 * @param gridname
	 * @param termName
	 * @param routename
	 * @param agencykey
	 * @return
	 */
	public ArrayList<MstTzTermInfo> queryTermByAreaGridAndTermnameAndRoutename(
            DatabaseHelper helper, String areaname, String gridname,
            String termName, String routename, String agencykey);

	/**
	 * @param helper
	 * @param terminalkey
	 * @param agencykey
	 * @param yesup
	 * @return
	 */
	public ArrayList<TzTermProInfo> queryTermProByUpyes(DatabaseHelper helper,
                                                        String terminalkey, String agencykey, String yesup);

	
}
