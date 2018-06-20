package et.tsingtaopad.db.dao;

import java.util.List;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstVistproductInfo;
import et.tsingtaopad.visit.shopvisit.chatvie.domain.ChatVieStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexQuicklyStc;
import et.tsingtaopad.visit.shopvisit.invoicing.domain.InvoicingStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstVistproductInfoDao.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-5</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstVistproductInfoDao extends Dao<MstVistproductInfo, String> {
    
    /**
     * 获取某次拜访的我品的进销存数据情况
     * 
     * @param helper
     * @param visitId   拜访主键
     * @return
     */
    public List<InvoicingStc> queryMinePro(DatabaseHelper helper, String visitId, String termKey);
    
    /**
     * 获取某次拜访的竞品的进销存数据情况
     * 
     * @param helper
     * @param visitId   拜访主键
     * @return
     */
    public List<ChatVieStc> queryViePro(DatabaseHelper helper, String visitId);
    
    /**
     * 获取巡店拜访-查指标的分项采集部分的产品指标数据
     * 
     * @param visitId       本次拜访ID
     * @param termId        本次拜访终端ID
     * @param channelId     本次拜访终端的次渠道ID
     * @param seeFlag       查看标识
     * @return
     */
    public List<CheckIndexCalculateStc> queryCalculateIndex(DatabaseHelper helper,
                                                            String visitId, String termId, String channelId, String seeFlag);
    
    /**
     * 获取巡店拜访-查指标的分项采集部分的产品指标对应的采集项目数据
     * 
     * @param visitId       本次拜访ID
     * @param visitId   上次拜访ID
     * @param channelId     本次拜访终端的次渠道ID
     * @return
     */
    public List<CheckIndexQuicklyStc> queryCalculateItem(
            DatabaseHelper helper, String visitId, String channelId);
    
    /**
     * 获取巡店拜访-查指标的分项采集部分的与产品无关的指标数据
     * 
     * @param visitId       拜访ID
     * @param channelId     本次拜访终端的次渠道ID
     * @param seeFlag       查看操作标识
     * @return
     */
    public List<CheckIndexCalculateStc> queryNoProIndex(
            DatabaseHelper helper, String visitId, String channelId, String seeFlag);

}
