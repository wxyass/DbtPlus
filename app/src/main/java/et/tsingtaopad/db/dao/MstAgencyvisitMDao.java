package et.tsingtaopad.db.dao;

import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;
import et.tsingtaopad.visit.agencyvisit.domain.TransferStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstAgencyvisitMDao.java</br>
 * 作者：吴欣伟   </br>
 * 创建时间：2013-11-28</br>      
 * 功能描述: 分经销商拜访主表DAO层</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstAgencyvisitMDao extends Dao<MstAgencyvisitM, String> {

    /**
     * 获取当前定格下的拜访经销商
     * @param helper
     * @param gridId 定格Id
     * @return
     */
    public List<AgencySelectStc> agencySelectQuery(SQLiteOpenHelper helper, String gridId);
    
    /**
     * 依据拜访请键获取经销商调货记录
     * 
     * @param helper
     * @param visitId   经销商拜访主键
     * @return
     */
    public List<TransferStc> queryTransByVisitId(SQLiteOpenHelper helper, String visitId);
    
    /***
     * 经销商某天拜访是否存在
     * @param agencyKey
     * @param visitDate
     * @return
     */
    public boolean isExistAgencyVisit(SQLiteOpenHelper helper, String agencyKey, String visitDate);
}
