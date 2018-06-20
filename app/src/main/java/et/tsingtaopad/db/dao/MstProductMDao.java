package et.tsingtaopad.db.dao;

import java.util.List;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.initconstvalues.domain.KvStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstProductMDao.java</br>
 * 作者：吴欣伟   </br>
 * 创建时间：2013-12-12</br>      
 * 功能描述: 青啤产品信息主表Dao层</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstProductMDao extends Dao<MstProductM, String> {

    /**
     * 获取指标状态查询中的可选产品列表
     * @param helper
     * @return
     */
    public List<KvStc> getIndexPro(DatabaseHelper helper);
}
