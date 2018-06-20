package et.tsingtaopad.visit.shopvisit.termindex;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCheckexerecordInfoDao;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.tables.MstCheckexerecordInfo;
import et.tsingtaopad.db.tables.PadChecktypeM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.visit.shopvisit.termindex.domain.TermIndexStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermIndexService.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-4</br>      
 * 功能描述: 终端指标状态列表业务逻辑</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermIndexService {
    
    private final String TAG = "TermIndexService";
    
    private Context context;
    
    public TermIndexService(Context context) {
        this.context = context;
    }
    
    /**
     * 获取终端某次拜访下产品对应的各指标的采集项目状态
     * 
     * @param visitId       拜访主键
     * @param uploadFlag    结束拜访标识，1：结束拜访，0：未结束拜访
     * @param termId        终端主键
     * @param channelId     终端次渠道ID
     * @return
     */
    public List<TermIndexStc> queryTermIndex(String visitId, 
                        String uploadFlag, String termId, String channelId) {
        List<TermIndexStc> lst = new ArrayList<TermIndexStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstCheckexerecordInfoDao dao = helper.getDao(MstCheckexerecordInfo.class);
            lst = dao.queryTermIndex(helper, visitId, uploadFlag, termId, channelId);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访指标执行记录表表DAO对象失败", e);
        }
        return lst;
    }
    
    /**
     * 依据产品ID、指标ID去重
     * 
     * @param termIndexLst  终端指标状态数据集合
     * @return Key: 指标值ID + 指标值ID, value:对应的指标状态数据
     */
    public Map<String, List<TermIndexStc>> 
            distinctByProduct(List<TermIndexStc> termIndexLst) {
        
        // 要返的数据实例 
        Map<String, List<TermIndexStc>> indexMap = 
                        new HashMap<String, List<TermIndexStc>>();
        List<String> indexProLst = new ArrayList<String>();
        String key;
        String indexPro;
        for (TermIndexStc item : termIndexLst) {
            key = item.getIndexKey() + item.getIndexValueKey();
            indexPro = key + item.getProKey();
            if (!indexMap.containsKey(key)) {
                indexMap.put(key, new ArrayList<TermIndexStc>());
            } 
            if (!indexProLst.contains(indexPro)) {
                indexProLst.add(indexPro);
                indexMap.get(key).add(item);
            }
        }
        return indexMap;
    }
    
    /**
     * 初始化指标状态查看页面要显示的数据结构
     * 
     * @param termindexLst   终端指标状态数据集合
     * @return  Key: indexKey + indexValueKey + proKey, value:对应的指标状态数据
     */
    public Map<String, List<TermIndexStc>> initTermIndex(
                                    List<TermIndexStc> termindexLst) {
        
        Map<String, List<TermIndexStc>> indexMap 
                    = new HashMap<String, List<TermIndexStc>>();
        String id;
        for(TermIndexStc item : termindexLst) {
            id = item.getIndexKey() + item.getIndexValueKey() + item.getProKey();
            if (!indexMap.containsKey(id)) {
                indexMap.put(id, new ArrayList<TermIndexStc>());
            } 
            indexMap.get(id).add(item);
        }
        
        return indexMap;
    }
    
    /**
     * 获取Pad端采集指标的结构数据，
     * 
     * @param channelId  当前终端次渠道ID
     * @return
     */
    public List<KvStc> queryCheckType(String channelId) {
        
        List<KvStc> indexLst = new ArrayList<KvStc>();
        try {
            if (!CheckUtil.isBlankOrNull(channelId)) {
                DatabaseHelper helper = DatabaseHelper.getHelper(context);
                PadChecktypeMDao dao = helper.getDao(PadChecktypeM.class);
                indexLst = dao.queryCheckType(helper, channelId);
            }
            
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访指标执行记录表表DAO对象失败", e);
        }
        return indexLst;
    }
}
