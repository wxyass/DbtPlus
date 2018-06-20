package et.tsingtaopad.db.dao;

import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.db.tables.MstCmpcompanyM;
import et.tsingtaopad.initconstvalues.domain.KvStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstCmpCompanyMDao.java</br>
 * 作者  :姜世杰   </br>
 * 创建时间：2013-12-17</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstCmpCompanyMDao  extends Dao<MstCmpcompanyM, String>{
    
    /**
     * 查询竞争对手可销售产品信息
     * @param helper
     * @return
     */
    public List<KvStc>  agencySellProQuery(SQLiteOpenHelper helper);
    
}
