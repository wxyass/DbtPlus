package et.tsingtaopad.db.dao;

import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.visit.shopvisit.line.domain.MstRouteMStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：MstRouteMDao.java</br> 
 * 作者：hongen </br>
 * 创建时间：2013-11-27</br> 
 * 功能描述: 线路档案主表DAO层</br> 
 * 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号修改人 修改版本</br>
 */
public interface MstRouteMDao extends Dao<MstRouteM, String> {
    
    /**
     * 获取线路列表信息，包含计划拜访时间
     * 
     * 用于：巡店拜访  -- 线路选择
     */
    public List<MstRouteMStc> queryLine(SQLiteOpenHelper helper);
    
    /**
     * 获取线路上次拜访的日期
     * 
     * @param helper
     * @param lineIdLst
     * @return              Key:线路ID、 Value:该线路上次拜访日期
     */
    public Map<String, String> queryPrevVisitDate(
            SQLiteOpenHelper helper, List<String> lineIdLst);
    
    /**
     * 获取某线路下的终端个数
     * 
     * @param helper
     * @param lineId    线路ID，为null时返回所有线路下的终端数
     * @return
     */
    public int queryForTermNum(SQLiteOpenHelper helper, String lineId);
    
    /**
     * 获取次渠道集合
     * @param helper
     * @return
     */
    public List<KvStc> querySecondSell(SQLiteOpenHelper helper);

}
