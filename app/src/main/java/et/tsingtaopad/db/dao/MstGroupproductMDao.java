/**
 * 
 */
package et.tsingtaopad.db.dao;

import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;
import cn.com.benyoyo.manage.bs.IntStc.DataresultTerPurchaseDetailsStc;
import et.tsingtaopad.visit.tomorrowworkrecord.domain.DayWorkDetailStc;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstGroupproductM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.operation.indexstatus.domain.IndexStatusStc;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：MstVisitMDao.java<br>
 * 作者：@沈潇<br>
 * 创建时间：2013/11/24<br>     
 * 功能描述: 终端进货详情DAO接口<br>
 * 版本 V 1.0<br>     
 * 修改履历 <br>
 * 日期      原因  BUG号    修改人 修改版本 <br>
 */
public interface MstGroupproductMDao  extends Dao<MstGroupproductM, String> {
	/**
	 * 查询终端详细信息
	 * listview多表查询
	 * @param helper
	 * @param key  参数where条件数组 bdt.ROUTEKEY=? and ( pdv.VISITDATE>=? and pdv.VISITDATE<=? )
	 * @return
	 */
	public List<DataresultTerPurchaseDetailsStc> searchTerminalDetails(SQLiteOpenHelper helper, String[] key);
	/**
	 * 查询每日工作明细
	 * listview多表查询
	 * @param helper
	 * @param // 参数where条件数组 a.VISITDATE = ? and a.user_id = ?
	 * @return
	 */
	public List<DayWorkDetailStc> searchTomorrowWorkRecord(SQLiteOpenHelper helper, String startdate, String enddate, String useId);
	
	/**
	 * 获取当天计划拜访终端个数
	 * 
	 * @param helper
	 * @param planDate     计划日期：YYYYMMDD
	 * @return
	 */
    public int queryPlanTermNum(SQLiteOpenHelper helper, String planDate);
    
    /**
     * 获取当天结束拜访且有效的拜访终端个数
     * 
     * @param helper
     * @param visitDate     拜访日期：YYYYMMDD
     * @return
     */
    public int queryVisitTermNum(SQLiteOpenHelper helper, String visitDate);
    
    /**
     * 获取某线路的最近或最久拜访的N条数据
     * 
     * @param helper
     * @param routeKey      线路ID
     * @param visitDate     拜访时间：yyyyMMdd
     * @param ascFlag       升序标识：true:最久、 false:最新
     * @param limitNum      获取信息条数
     * @return
     */
    public List<MstVisitM> queryForNum(SQLiteOpenHelper helper, String routeKey, String visitDate, boolean ascFlag, int limitNum);
    
    /**
     * 指标状态查询
     * 
     * @param lineId        线路ID
     * @param lineCurrDate  线路当前拜访日期：yyyyMMdd
     * @param linePrevDate  线路上一次拜访日期：yyyyMMdd
     * @param gridCurrDate  定格下各线路当前拜访日期：yyyyMMdd
     * @param gridPrevDate  定格下各线路上一次拜访日期：yyyyMMdd
     * @param checkId       指标ID
     * @param valueId       指标值ID
     * @param productIds    产品ID集合
     */
    public List<IndexStatusStc> queryForIndexStatus(SQLiteOpenHelper helper, String lineId, String lineCurrDate,
                                                    String linePrevDate, String[] gridCurrDate, String[] gridPrevDate, String checkId, String valueId, String[] productIds);
	
    
    /**
	 * @param helper
	 * @param terminalcode
	 * @param startdate
	 * @return
	 */
	public List<MstGroupproductM> queryMstGroupproductMByCreatedate(DatabaseHelper helper, String terminalcode, String startdate);
}
