package et.tsingtaopad.db.dao;

import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.visit.agencystorage.domain.AgencystorageStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstAgencyinfoMDao.java</br>
 * 作者：吴欣伟   </br>
 * 创建时间：2013-12-4</br>      
 * 功能描述: 分经销商信息主表Dao层</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstAgencyinfoMDao extends Dao<MstAgencyinfoM, String> {
    
    /**
     * 获取经销商库存的查询显示数据
     * @param helper
     * @param agencyKey 经销商ID
     * @return
     */
    public List<AgencystorageStc> agencyStorageQuery(SQLiteOpenHelper helper, String datecureents, String datecureentx, String agencyKey, String datecureent, String dateselects, String dateselectx, String agencyKeys, String dateselect);
    
    
    /**
     * 我方经销商销售产品查询
     * @author 姜世杰 
     * @since    2013-12-17
     * @return
     */
    public List<KvStc>  agencySellProQuery(SQLiteOpenHelper helper);
    
    /**
     * 获取可拜访经销商及产品列表
     * 
     * @param helper
     * @return
     */
    public List<KvStc> queryVisitAgencyPro(SQLiteOpenHelper helper);
    
    /**
     * 获取当前定格的请经销商
     * 
     * @param helper
     * @param gridId
     * @return
     */
    public MstAgencyinfoM queryMainAgency(SQLiteOpenHelper helper, String gridId);
    
}
