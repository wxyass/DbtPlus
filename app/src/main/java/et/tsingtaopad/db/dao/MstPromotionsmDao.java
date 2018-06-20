package et.tsingtaopad.db.dao;

import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexPromotionStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstPromotionsM.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-20</br>      
 * 功能描述: 活动主表DAO</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstPromotionsmDao extends Dao<MstPromotionsM, String> {

    /**
     * 查询终端参与的活动及活动达成状态情况
     * 
     * 用于：巡店拜访-查指标-促销活动
     * 
     * @param helper
     * @param visitId       拜访主键
     * @param channelId     终端销售渠道ID
     * @param termLevel     终端等级ID
     * @return
     */
    public List<CheckIndexPromotionStc> queryPromotionByterm(
            SQLiteOpenHelper helper, String visitId, String channelId, String termLevel);
    
    /**
     * 促销活动查询
     * 
     * @param promId        活动ID
     * @param promSd        活动开始时间
     * @param searchD       查询日期
     * @param lineIds       线路ID
     * @param termLevels    终端等级
     * @return  YES:活动达成终端， NO:活动未终端
     */
    public Map<String, List<String>> promotionSearch(SQLiteOpenHelper helper,
                                                     String promId, String promSd, String searchD, String[] lineIds, String[] termLevels);
}
