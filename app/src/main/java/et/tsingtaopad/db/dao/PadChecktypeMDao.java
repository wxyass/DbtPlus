package et.tsingtaopad.db.dao;

import java.util.List;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.PadChecktypeM;
import et.tsingtaopad.initconstvalues.domain.KvStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：PadChecktypeM.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-4</br>      
 * 功能描述: PAD端采集用指标主表DAO</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface PadChecktypeMDao extends Dao<PadChecktypeM, String> {

    /**
     * 获取Pad端采集指标的结构数据， value:指标及指标值的层级表现形式
     * 
     * @param helper 
     * @param channelId  当前终端次渠道ID
     * @return
     */
    public List<KvStc> queryCheckType(DatabaseHelper helper, String channelId);
    
    /**
     * 获取并组建所有指标、指标值的树级关系
     * 
     * @param helper
     * @param productFlag   关于产品标志，1：只列出与产品相关的指标
     * @return
     */
    public List<KvStc> queryCheckTypeStatus(DatabaseHelper helper, String productFlag);
}
