package et.tsingtaopad.visit.shopvisit.checkindex;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCheckexerecordInfoDao;
import et.tsingtaopad.db.dao.MstGroupproductMDao;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.tables.MstCheckexerecordInfo;
import et.tsingtaopad.db.tables.MstCheckexerecordInfoTemp;
import et.tsingtaopad.db.tables.MstCollectionexerecordInfo;
import et.tsingtaopad.db.tables.MstGroupproductM;
import et.tsingtaopad.db.tables.MstPromotermInfo;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.db.tables.MstVistproductInfo;
import et.tsingtaopad.db.tables.PadCheckstatusInfo;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexQuicklyStc;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProIndexValue;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.QuicklyProItem;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：CheckIndexService.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-12</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class CheckIndexService extends ShopVisitService {
    
    private final String TAG = "CheckIndexService";
    
    public CheckIndexService(Context context, Handler handler) {
        super(context, handler);
    }
    
    /**
     * 判定是否为本次拜访第一次采集指标数据
     * 
     * @param visitId   拜访ID
     * @return
     */
    public boolean isFirstCalculate(String visitId) {
        boolean flag = false;
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstCheckexerecordInfo, String> dao = helper.getMstCheckexerecordInfoDao();
            
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
			
            List<MstCheckexerecordInfo> lst = dao.queryForEq("visitkey", visitId);
            if (CheckUtil.IsEmpty(lst)) {
                flag = true;
            } else {
                flag = false;
            }
            
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }
        
        return flag;
    }
    
    /**
     * 获取分项采集页面显示数据
     * 
     * @param visitId           本次拜访主键
     * @param termId            本次拜访终端ID
     * @param channelId         本次拜访终端所属渠道
     * @param seeFlag           查看标识
     * @return
     */
    public List<ProIndex> queryCalculateIndex(String visitId,String termId, String channelId, String seeFlag) {
        
        // 获取分项采集数据
        List<CheckIndexCalculateStc> stcLst = new ArrayList<CheckIndexCalculateStc>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
			
            stcLst = dao.queryCalculateIndex(helper, visitId, termId, channelId, seeFlag);
            
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }
        
        // 组建成界面显示所需要的数据结构
        List<ProIndex> proIndexLst = new ArrayList<ProIndex>();
        String indexId = "";
        ProIndex indexItem = new ProIndex();
        ProIndexValue indexValueItem;
        for (CheckIndexCalculateStc item : stcLst) {
        	// 
            if (!indexId.equals(item.getIndexId())) {
                indexItem = new ProIndex();
                indexItem.setIndexId(item.getIndexId());
                indexItem.setIndexName(item.getIndexName());
                indexItem.setIndexType(item.getIndexType());
                indexItem.setIndexValueLst(new ArrayList<ProIndexValue>());
                proIndexLst.add(indexItem);
                indexId = item.getIndexId();
            }
            indexValueItem = new ProIndexValue();
            indexValueItem.setIndexResultId(item.getRecordId());
            indexValueItem.setIndexId(item.getIndexId());
            indexValueItem.setIndexType(item.getIndexType());
            indexValueItem.setIndexValueId(item.getIndexValueId());
            indexValueItem.setIndexValueName(item.getIndexValueName());
            indexValueItem.setProId(item.getProId());
            indexValueItem.setProName(item.getProName());
            indexItem.getIndexValueLst().add(indexValueItem);
        }
        return proIndexLst;
    }
    
    /**
     * 删除渠道下 不考核的产品 采集项记录
     * 
     * @param visitkey    拜访key
     * @param proItemLst    通过拜访产品表关联指标模板 出来的采集项集合
     * @return
     */
    public List<String>  deleteCollection(String visitkey,List<ProItem> proItemLst) {
    	
        // 1 查出 采集项表 产品记录
        // 2 把采集项表的产品记录 多出来的删除
        
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        List<MstCollectionexerecordInfo> collectionexerecordinfoLst = new ArrayList<MstCollectionexerecordInfo>();
        List<String> deletepro = new ArrayList<String>();
        try {
            
        	// 查询这次拜访,采集项表已经生成的记录
            Dao<MstCollectionexerecordInfo, String> proItemDao = helper.getMstCollectionexerecordInfoDao();
            QueryBuilder<MstCollectionexerecordInfo, String> collectionexerecordBuilder = proItemDao.queryBuilder();
            collectionexerecordBuilder.where().eq("visitkey", visitkey);
            collectionexerecordBuilder.groupBy("productkey");
            collectionexerecordinfoLst = collectionexerecordBuilder.query();
            
            List<String> proLst = FunUtil.getPropertyByName(proItemLst, "proId", String.class);
            List<String> newList = new ArrayList<String>(new HashSet(proLst)); 
            List<String> collectionLst = FunUtil.getPropertyByName(collectionexerecordinfoLst, "productkey", String.class);
            // 查出数据库的采集项表 新渠道下没有的产品  去删掉
            for(String producekey : collectionLst){
                if(!newList.contains(producekey)){
                    deletepro.add(producekey);
                }
            }
            
            for (String prokey : deletepro) {
				proItemDao.executeRaw("delete from mst_collectionexerecord_info where visitkey = ? and productkey = ? ", new String[]{visitkey, prokey});
			}
            
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访请表DAO异常", e);
        }
        return deletepro;
    }

    /**
     * 获取分项采集部分的产品指标对应的采集项目数据
     * 
     * @param visitId           拜访主键
     * @param channelId         渠道ID
     * @return
     */
    public List<ProItem> queryCalculateItem(String visitId, String channelId) {
        
        // 获取分项采集各指标对应的采集项目数据
        List<CheckIndexQuicklyStc> stcLst = new ArrayList<CheckIndexQuicklyStc>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
            stcLst = dao.queryCalculateItem(helper, visitId, channelId);
            
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }
        
        // 组建界面要显示的数据
        List<ProItem> proItemLst = new ArrayList<ProItem>();
        ProItem proItem = new ProItem();
        String Id = "", proItemId;
        for (CheckIndexQuicklyStc stc : stcLst) {
            proItemId = stc.getProductkey() + stc.getColitemkey();
            if (!Id.equals(proItemId)) {
                proItem = new ProItem();
                proItem.setColRecordKey(stc.getColRecordId());
                proItem.setCheckkey(stc.getCheckkey());
                proItem.setItemId(stc.getColitemkey());
                proItem.setItemName(stc.getColitemname());
                proItem.setProId(stc.getProductkey());
                proItem.setProName(stc.getProName());
                proItem.setChangeNum(stc.getChangeNum());
                proItem.setFinalNum(stc.getFinalNum());
                proItem.setXianyouliang(stc.getXianyouliang());
                proItem.setBianhualiang(stc.getBianhualiang());
                proItem.setFreshness(FunUtil.isNullSetDate(stc.getFreshness()));// 新鲜度
                proItem.setIndexIdLst(new ArrayList<String>());
                proItemLst.add(proItem);
                Id = proItemId;
            }
            if (!proItem.getIndexIdLst().contains(stc.getCheckkey())) {
                proItem.getIndexIdLst().add(stc.getCheckkey());
            }
        }
        return proItemLst;
    }
    
    /**
     * 初始化快速采集所需要数据(将产品按采集项归类)
     * 
     * @param proItemLst 分项采集部分的产品指标对应的采集项目数据
     * @return
     */
    public List<QuicklyProItem> initQuicklyProItem(List<ProItem> proItemLst) {
        
        List<QuicklyProItem> quicklyLst = new ArrayList<QuicklyProItem>();
        if (!CheckUtil.IsEmpty(proItemLst)) {
            int index;
            QuicklyProItem quicklyItem;
            List<String> itemIdLst = new ArrayList<String>();
            for (ProItem item : proItemLst) {
                if (!itemIdLst.contains(item.getItemId())) {
                    quicklyItem = new QuicklyProItem();
                    quicklyItem.setItemId(item.getItemId());
                    quicklyItem.setItemName(item.getItemName());
                    quicklyItem.setProItemLst(new ArrayList<ProItem>());
                    quicklyLst.add(quicklyItem);
                    itemIdLst.add(item.getItemId());
                } 
                index = itemIdLst.indexOf(item.getItemId());
                quicklyItem = quicklyLst.get(index);
                quicklyItem.getProItemLst().add(item);
            }
        }
        
        return quicklyLst;
    }
    
    /**
     * 保存查指标页面数据
     * 
     * @param visitId       拜访主键
     * @param termId        终端ID
     * @param calculateLst  指标、指标值记录结果
     * @param proItemLst    产品、采集项记录结果
     * @param noProIndexLst 与产品无关的指标采集值
     */
    public void saveCheckIndex(String visitId, String termId,
                    List<ProIndex> calculateLst,List<ProItem> proItemLst, 
                                List<CheckIndexCalculateStc> noProIndexLst) {
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstCheckexerecordInfoTemp, String>  indexValueDao = 
                                       helper.getMstCheckexerecordInfoTempDao();
            Dao<MstCollectionexerecordInfo, String> proItemDao = 
                                    helper.getMstCollectionexerecordInfoDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            // 拜访指标执行记录表更新语句及参数
            String[] args = new String[3];
            args[0] = visitId;
            StringBuffer buffer = new StringBuffer();
            buffer.append("update mst_checkexerecord_info_temp set ");
            buffer.append("visitkey = ?, acresult = ?, padisconsistent = '0' ");
            buffer.append("where recordkey = ? ");
            
            // 保存拜访指标执行记录表
            MstCheckexerecordInfoTemp indexInfo;
            for (ProIndex indexItem : calculateLst) {
                for (ProIndexValue valueItem : indexItem.getIndexValueLst()) {
                    
                    // 如果true,是新增
                    if (CheckUtil.isBlankOrNull(valueItem.getIndexResultId())) {
                        valueItem.setIndexResultId(FunUtil.getUUID());
                        indexInfo = new MstCheckexerecordInfoTemp();
                        indexInfo.setRecordkey(valueItem.getIndexResultId());
                        indexInfo.setVisitkey(visitId);
                        indexInfo.setProductkey(valueItem.getProId());
                        indexInfo.setCheckkey(valueItem.getIndexId());
                        indexInfo.setChecktype(valueItem.getIndexType());
                        indexInfo.setAcresult(valueItem.getIndexValueId());
                        indexInfo.setIscom(ConstValues.FLAG_0);
                        indexInfo.setTerminalkey(termId);
                        indexInfo.setPadisconsistent(ConstValues.FLAG_0);
                        indexInfo.setDeleteflag(ConstValues.FLAG_0);
                        //indexInfo.setCreuser(ConstValues.loginSession.getUserCode());
                        indexInfo.setCreuser(PrefUtils.getString(context, "userCode", ""));
                        //indexInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
                        indexInfo.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                        indexValueDao.create(indexInfo);
                        
                    // 否则更新
                    } else {
                        args[1] = valueItem.getIndexValueId();
                        args[2] = valueItem.getIndexResultId();
                        indexValueDao.executeRaw(buffer.toString(), args);
                    }
                }
            }
            
            // 清楚本地mst_collectionexerecord_info 的数据， 并插入新的数据
            proItemDao.executeRaw("delete from mst_collectionexerecord_info where visitkey = ?", visitId);

            // 保产品及对应的采集项数据
            Map<String, Integer> proStockMap = new HashMap<String, Integer>();
            int currNum, changeNum;
            MstCollectionexerecordInfo itemInfo;
            for (ProItem proItem : proItemLst) {
                
            	 for (String chekcKey : proItem.getIndexIdLst()){
            		 
        			 proItem.setColRecordKey(FunUtil.getUUID());
        			 itemInfo = new MstCollectionexerecordInfo();
        			 itemInfo.setColrecordkey(proItem.getColRecordKey());
        			 itemInfo.setVisitkey(visitId);
        			 itemInfo.setProductkey(proItem.getProId());
        			 itemInfo.setCheckkey(chekcKey);
        			 itemInfo.setColitemkey(proItem.getItemId());
        			 itemInfo.setAddcount(proItem.getChangeNum());// 
        			 itemInfo.setTotalcount(proItem.getFinalNum());
        			 itemInfo.setBianhualiang(proItem.getBianhualiang());
        			 itemInfo.setXianyouliang(proItem.getXianyouliang());
        			 //itemInfo.setFreshness(proItem.getFreshness());// 新鲜度 
        			 itemInfo.setFreshness(FunUtil.isDateString(proItem.getFreshness()));// 新鲜度
        			 itemInfo.setPadisconsistent(ConstValues.FLAG_0);
        			 itemInfo.setDeleteflag(ConstValues.FLAG_0);
        			 //itemInfo.setCreuser(ConstValues.loginSession.getUserCode());
        			 itemInfo.setCreuser(PrefUtils.getString(context, "userCode", ""));
        			 //itemInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
        			 itemInfo.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
        			 proItemDao.create(itemInfo);
            	 }
                
                // 记录每个产品的现有库存
                if (PropertiesUtil.getProperties("checkItem_stock", "").equals(proItem.getItemId()) 
                        || PropertiesUtil.getProperties("checkItem_stock1", "").equals(proItem.getItemId())) {
                    currNum = proItem.getFinalNum() == null ? 0 : proItem.getFinalNum().intValue();
                    changeNum = proItem.getChangeNum() == null ? 0 : proItem.getChangeNum().intValue();
                    proStockMap.put(proItem.getProId(), currNum + changeNum);
                }
            }
            
            // 更新我品的现有库存
            buffer = new StringBuffer();
            buffer.append("update mst_vistproduct_info set currnum = ?,padisconsistent = '0' ");
            buffer.append("where productkey = ? and visitkey = ? ");
            args = new String[3];
            args[2] = visitId;
            for (String proId : proStockMap.keySet()) {
                args[0] = proStockMap.get(proId).toString();
                args[1] = proId;
                proItemDao.executeRaw(buffer.toString(), args);
            }
            
            // 保存或更新与产品无关的指标的指标值数据
            args = new String[3];
            args[0] = visitId;
            buffer = new StringBuffer();
            buffer.append("update mst_checkexerecord_info_temp set  ");
            buffer.append("visitkey = ?, acresult = ? ,padisconsistent = '0' ");
            buffer.append("where recordkey = ? ");
            
            String currDate = DateUtil.formatDate(new Date(), "yyyyMMdd");
            // 保存拜访指标执行记录表
            for (CheckIndexCalculateStc itemStc : noProIndexLst) {
                
                // 如果true,是新增
                if (!visitId.equals(itemStc.getVisitId())) {
                    itemStc.setVisitId(visitId);
                    indexInfo = new MstCheckexerecordInfoTemp();
                    indexInfo.setRecordkey(FunUtil.getUUID());
                    indexInfo.setVisitkey(visitId);
                    indexInfo.setStartdate(currDate);
                    indexInfo.setEnddate(currDate);
                    indexInfo.setCheckkey(itemStc.getIndexId());
                    indexInfo.setChecktype(itemStc.getIndexType());
                    indexInfo.setAcresult(itemStc.getIndexValueId());
                    indexInfo.setIscom(ConstValues.FLAG_0);
                    indexInfo.setTerminalkey(termId);
                    indexInfo.setPadisconsistent(ConstValues.FLAG_0);
                    indexInfo.setDeleteflag(ConstValues.FLAG_0);
                    //indexInfo.setCreuser(ConstValues.loginSession.getUserCode());
                    indexInfo.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    //indexInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
                    indexInfo.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                    indexValueDao.create(indexInfo);
                    
                // 否则更新
                } else {
                    args[1] = itemStc.getIndexValueId();
                    args[2] = itemStc.getRecordId();
                    indexValueDao.executeRaw(buffer.toString(), args);
                }
            }
            
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "保存进销存数据发生异常", e);
            ViewUtil.sendMsg(context, R.string.checkindex_save_fail);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                Log.e(TAG, "回滚进销存数据发生异常", e1);
            }
        }
    }

    /**
     * 获取巡店拜访-查指标的分项采集部分的与产品无关的指标数据
     * 
     * @param visitId       拜访ID
     * @param channelId     本次拜访终端的次渠道ID
     * @param seeFlag       查看操作标识
     * @return
     */
    public List<CheckIndexCalculateStc> queryNoProIndex(
                                String visitId, String channelId, String seeFlag) { 
        // 获取分项采集数据
        List<CheckIndexCalculateStc> stcLst = new ArrayList<CheckIndexCalculateStc>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
			
            stcLst = dao.queryNoProIndex(helper, visitId, channelId, seeFlag);
            
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }
        
        return stcLst;
    }
    /**
     * 获取巡店拜访-查指标的分项采集部分的与产品无关的指标数据
     * 
     * @param visitId       拜访ID
     * @param channelId     本次拜访终端的次渠道ID
     * @param seeFlag       查看操作标识
     * @return
     */
    public List<CheckIndexCalculateStc> queryNoProIndex2(
    		String visitId, String channelId, String seeFlag) { 
    	// 获取分项采集数据
    	List<CheckIndexCalculateStc> stcLst = new ArrayList<CheckIndexCalculateStc>();
    	stcLst.add(new CheckIndexCalculateStc("666b74b3-b221-4920-b549-d9ec39a463fd", "1", "合作执行是否到位", "8d36d1e5-c776-452e-8893-589ad786d71d"));
    	stcLst.add(new CheckIndexCalculateStc("59802090-02ac-4146-9cc3-f09570c36a26", "4", "我品单店占有率", "eeffb1af-51c0-4954-98de-2cc62043e4d2"));
    	stcLst.add(new CheckIndexCalculateStc("df2e88c9-246f-40e2-b6e5-08cdebf8c281", "1", "是否高质量配送", "bf600cfe-f70d-4170-857d-65dd59740d57"));
    	
    	AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		
    		MstCheckexerecordInfoDao dao = helper.getDao(MstCheckexerecordInfo.class);
    		Dao<MstCheckexerecordInfoTemp, String> CheckexerecordInfoTempdao = helper.getMstCheckexerecordInfoTempDao();
    		
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
    		//
    		if(ConstValues.FLAG_1.equals(seeFlag)){//查看模式
    			
        		List<MstCheckexerecordInfo> Checkexerecordlist = dao.queryForEq("visitkey", visitId);
        		for (MstCheckexerecordInfo mstCheckexerecordInfo : Checkexerecordlist) {
        			for (CheckIndexCalculateStc CheckIndexCalculate : stcLst) {
    					if(mstCheckexerecordInfo.getCheckkey().equals(CheckIndexCalculate.getIndexId())){
    						CheckIndexCalculate.setVisitId(mstCheckexerecordInfo.getVisitkey());
    						CheckIndexCalculate.setRecordId(mstCheckexerecordInfo.getRecordkey());
    						CheckIndexCalculate.setIndexValueId(mstCheckexerecordInfo.getAcresult());
    					}
    				}
        		}
    		}else{
    			
        		List<MstCheckexerecordInfoTemp> Checkexerecordlist = CheckexerecordInfoTempdao.queryForEq("visitkey", visitId);
        		for (MstCheckexerecordInfoTemp mstCheckexerecordInfo : Checkexerecordlist) {
    				for (CheckIndexCalculateStc CheckIndexCalculate : stcLst) {
    					if(mstCheckexerecordInfo.getCheckkey().equals(CheckIndexCalculate.getIndexId())){
    						CheckIndexCalculate.setVisitId(mstCheckexerecordInfo.getVisitkey());
    						CheckIndexCalculate.setRecordId(mstCheckexerecordInfo.getRecordkey());
    						if(mstCheckexerecordInfo.getAcresult()!=null){
    							// 如果上次拜访不为空,此指标取上次拜访的值 // 修改新终端进入时,合作状态为空的bug 20160809
    							CheckIndexCalculate.setIndexValueId(mstCheckexerecordInfo.getAcresult());
    						}
    					}
    				}
    			}
    		}
    		
    		connection.commit(null);
    		
    	} catch (SQLException e) {
    		Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
    	}
    	
    	return stcLst;
    }
    /**
     * 是否合作终端 指标关联指标值
     */
    public List<KvStc> queryNoProIndexValueId1(){// 

    	List<KvStc> stcLst = new ArrayList<KvStc>();
    	AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		
    		Dao<PadCheckstatusInfo, String> dao = helper.getPadCheckstatusInfoDao();
    		
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
    		
    		List<PadCheckstatusInfo> queryForEq = dao.queryForEq("checkkey", "666b74b3-b221-4920-b549-d9ec39a463fd");// 是否合作终端
    		KvStc kvStc = null;
    		for (PadCheckstatusInfo padCheckstatusInfo : queryForEq) {
    			kvStc = new KvStc();
    			kvStc.setKey(padCheckstatusInfo.getCstatuskey());
    			kvStc.setValue(padCheckstatusInfo.getCstatusname());
    			stcLst.add(kvStc);
			}
    		connection.commit(null);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
    	}
		return stcLst;
    	
    }
    /**
     * 是否高质量配送 指标关联指标值
     */
    public List<KvStc> queryNoProIndexValueId2(){// 

    	List<KvStc> stcLst = new ArrayList<KvStc>();
    	AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		
    		Dao<PadCheckstatusInfo, String> dao = helper.getPadCheckstatusInfoDao();
    		
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
    		
    		List<PadCheckstatusInfo> queryForEq = dao.queryForEq("checkkey", "df2e88c9-246f-40e2-b6e5-08cdebf8c281");// 是否高质量配送
    		KvStc kvStc = null;
    		for (PadCheckstatusInfo padCheckstatusInfo : queryForEq) {
    			kvStc = new KvStc();
    			kvStc.setKey(padCheckstatusInfo.getCstatuskey());
    			kvStc.setValue(padCheckstatusInfo.getCstatusname());
    			stcLst.add(kvStc);
			}
    		
    		connection.commit(null);
    		
    	} catch (SQLException e) {
    		Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
    	}
		return stcLst;
    	
    }
    /**
     * 我品占有率 指标关联指标值
     */
    public List<KvStc> queryNoProIndexValueId3(){// 

    	List<KvStc> stcLst = new ArrayList<KvStc>();
    	AndroidDatabaseConnection connection = null;
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		
    		Dao<PadCheckstatusInfo, String> dao = helper.getPadCheckstatusInfoDao();
    		connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
			
    		QueryBuilder<PadCheckstatusInfo, String> collectionQB = dao.queryBuilder();
            Where<PadCheckstatusInfo, String> collectionWhere=collectionQB.where();
            collectionWhere.eq("checkkey", "59802090-02ac-4146-9cc3-f09570c36a26");
            collectionQB.orderBy("orderbyno", true);
            List<PadCheckstatusInfo> queryForEq =collectionQB.query();
    		//List<PadCheckstatusInfo> queryForEq = dao.queryForEq("checkkey", "59802090-02ac-4146-9cc3-f09570c36a26");// 我品占有率
    		KvStc kvStc = null;
    		for (PadCheckstatusInfo padCheckstatusInfo : queryForEq) {
    			kvStc = new KvStc();
    			kvStc.setKey(padCheckstatusInfo.getCstatuskey());
    			kvStc.setValue(padCheckstatusInfo.getCstatusname());
    			stcLst.add(kvStc);
			}
    		
    		connection.commit(null);
    		
            
    	} catch (SQLException e) {
    		Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
    	}
		return stcLst;
    	
    }

    /**
     * 获取巡店拜访-查指标的促销活动页面部分的数据
     * 
     * @param visitId   拜访ID
     * @param channelId     本次拜访终端的次渠道ID
     * @param termLevel     本次拜访终端的终端类型ID(终端等级)
     * @return
     */
    public List<CheckIndexPromotionStc> queryPromotion(
                    String visitId, String channelId, String termLevel) { 
        // 获取分项采集数据
        List<CheckIndexPromotionStc> stcLst = new ArrayList<CheckIndexPromotionStc>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstPromotionsmDao dao = helper.getDao(MstPromotionsM.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
            stcLst = dao.queryPromotionByterm(helper, visitId, channelId, termLevel);
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }
        
        // 处理组合进店的情况
        List<CheckIndexPromotionStc> tempLst = new ArrayList<CheckIndexPromotionStc>();
        String promotionKey = "";
        CheckIndexPromotionStc itemStc = new CheckIndexPromotionStc();
        for (CheckIndexPromotionStc item : stcLst) {
            if (!promotionKey.equals(item.getPromotKey())) {
                promotionKey = item.getPromotKey();
                itemStc = item;
                tempLst.add(itemStc);
            } else {
                itemStc.setProId(itemStc.getProId() + "," + item.getProId());
                itemStc.setProName(itemStc.getProName() + "\n" + item.getProName());
            }
           
        }
        
        return tempLst;
    }
    
    /**
     * 保存终端参与的活动的达成状态记录
     * 
     * @param visitId       拜访主键
     * @param termId        终端ID
     * @param promotionLst  活动达成情况记录
     */
    public void savePromotion(String visitId, 
                String termId,List<CheckIndexPromotionStc> promotionLst) {
    	
    	AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstPromotermInfo, String> dao = helper.getMstPromotermInfoDao();
            
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
			
            StringBuffer buffer;
            MstPromotermInfo info;
            String currDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
            for(CheckIndexPromotionStc item : promotionLst) {
                if (CheckUtil.isBlankOrNull(item.getRecordKey())) {
                    item.setRecordKey(FunUtil.getUUID());
                    info = new MstPromotermInfo();
                    info.setRecordkey(item.getRecordKey());
                    info.setPtypekey(item.getPromotKey());
                    info.setTerminalkey(termId);
                    info.setVisitkey(visitId);
                    info.setStartdate(currDate);
                    info.setIsaccomplish(item.getIsAccomplish());
                    info.setRemarks(item.getReachNum());
                    info.setPadisconsistent(ConstValues.FLAG_0);
                    //info.setCreuser(ConstValues.loginSession.getUserCode());
                    info.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    //info.setUpdateuser(ConstValues.loginSession.getUserCode());
                    info.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                    dao.create(info);
                    
                } else {
                    buffer = new StringBuffer();
                    buffer.append("update mst_promoterm_info set ");
                    buffer.append("visitkey=?, isaccomplish=?, startdate=?,remarks=?,padisconsistent ='0' ");
                    buffer.append("where recordkey = ? ");
                    dao.executeRaw(buffer.toString(), new String[]{
                        visitId, item.getIsAccomplish(), currDate, item.getReachNum(),item.getRecordKey()});
                }
            }
            
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }
    }

    /**
     * 自动计算各指标的指标值 
     * 
     * @param channelId         终端次渠道ID
     * @param proItemLst        各产品对应的采集项对应的现有量及变化量
     * @param calculateLst      分项采集显示的数据源
     * @param productKey        要计算的产品Id,null时计算所有
     * @param currIndexId       当前指标主键
     */
    public void calculateIndex(String channelId, 
                    List<ProItem> proItemLst, List<ProIndex> calculateLst, String productKey,String currIndexId) {
        //当前指标在数据库里是否存在
        boolean isCurrIndexExsist=false;
        
        //String proItemLst1 = JSON.toJSONString(proItemLst);
        //String calculateLst1 = JSON.toJSONString(calculateLst);
        
        // 获取要自动计算的指标的主键的集合
        List<String> calIndexIdLst = new ArrayList<String>();
        if (!CheckUtil.IsEmpty(calculateLst)) {
            for (ProIndex item : calculateLst) {
                if (ConstValues.FLAG_0.equals(item.getIndexType()) || ConstValues.FLAG_1.equals(item.getIndexType()) 
                            || ConstValues.FLAG_4.equals(item.getIndexType())) {
                    calIndexIdLst.add(item.getIndexId());
                }
            }
        }
        
        // 如果无需要计算的则返回
        if (CheckUtil.IsEmpty(calIndexIdLst)) {
            return;
        }
        
        // 拼接SQL
        String key = "";
        StringBuffer buffer = new StringBuffer();
        Map<String, String> sqlMap = new HashMap<String, String>();
        //Map<String, List<String>> checkItemMap = new HashMap<String, List<String>>();
        Map<String, Map<String, List<String>>> checkcheckItemMap = new HashMap<String, Map<String, List<String>>>();// 指标集合
        DecimalFormat df = new DecimalFormat("0");
        if (!(CheckUtil.IsEmpty(proItemLst) || CheckUtil.IsEmpty(calIndexIdLst))) {
            for (ProItem proItem : proItemLst) {
                // 如果计算单个产品且，产品不相等时continue
                if ("-1".equals(productKey) || productKey.equals(proItem.getProId())) {// ProId:产品主键
                    for (String indexId : proItem.getIndexIdLst()) {
                        if (calIndexIdLst.contains(indexId)) {
                            
                            // 拼SQL // key: ad3030fb-e42e-47f8-a3ec-4229089aab5d,1-9065(指标主键,产品主键)
                            key = indexId + "," + proItem.getProId();
                            if (sqlMap.containsKey(key)) {
                                buffer = new StringBuffer(sqlMap.get(key));
                                buffer.append(" or ");
                            } else {
                                buffer = new StringBuffer();
                            }
                            buffer.append(" (cc.colitemkey='").append(proItem.getItemId());
                            Double cnum = FunUtil.isNullToZero(proItem.getChangeNum());
                            Double fnum = FunUtil.isNullToZero(proItem.getFinalNum());
                            /*buffer.append("' and coalesce(cc.addcount, 0) <= ").append(proItem.getChangeNum().intValue());
                            buffer.append(" and coalesce(cc.totalcount, 0) <= ").append(
                                    (proItem.getChangeNum().intValue() + proItem.getFinalNum().intValue())).append(") ");*/
                            buffer.append("' and coalesce(cc.addcount, 0) <= ").append(cnum.intValue());
                            buffer.append(" and coalesce(cc.totalcount, 0) <= ").append(
                                    (cnum.intValue()+fnum.intValue())).append(") ");
                            
                            
                            sqlMap.put(key, buffer.toString());
                            
                            
                            // 计算每个指标需要采集的采集几个数据
                            /*if (!checkItemMap.containsKey(indexId)) {
                            	// (指标,集合(堆头,库存啥的))
                                checkItemMap.put(indexId, new ArrayList<String>());
                            } 
                            if (!checkItemMap.get(indexId).contains(proItem.getItemId())) {
                                checkItemMap.get(indexId).add(proItem.getItemId());
                            }*/
                            
                            // 每个产品有几个采集项  indexId:指标主键(ad3030fb-e42e-47f8-a3ec-4229089aab5d)
                            if (!checkcheckItemMap.containsKey(indexId)) {// 判断集合中是否有该产品对应的指标,当没有该指标时 
                            	Map<String, List<String>> procheckItemMap = new HashMap<String, List<String>>();// 产品采集项集合
                            	ArrayList<String> proItemIds = new ArrayList<String>();
                            	proItemIds.add(proItem.getItemId());// ItemId: 每个采集项的id(比如:101,102)
                            	procheckItemMap.put(proItem.getProId(), proItemIds);
                            	checkcheckItemMap.put(indexId, procheckItemMap);
                            } else {//当有该指标时
	                            Iterator iter = checkcheckItemMap.entrySet().iterator();
								while (iter.hasNext()) {
									Map.Entry entry = (Map.Entry) iter.next();
									// 指标id
									String checkId = (String) entry.getKey();
									if(indexId.equals(checkId)){
										Map<String, List<String>> val = (Map<String, List<String>>) entry.getValue();
										if(val.keySet().contains(proItem.getProId())){
											checkcheckItemMap.get(indexId).get(proItem.getProId()).add(proItem.getItemId());
										}else{
			                            	ArrayList<String> proItemIds = new ArrayList<String>();
			                            	proItemIds.add(proItem.getItemId());
			                            	checkcheckItemMap.get(indexId).put(proItem.getProId(), proItemIds);
										}
									}
								}
                            }
                        }
                    }
                }
            }
        }
        
        //String checkcheckItemMap1 = JSON.toJSONString(checkcheckItemMap);
        
        buffer = new StringBuffer();
        String[] keys = null;
        buffer.append("select cc.checkkey, cc.productkey, cc.cstatuskey, max(cs.cstatusname) cstatusname, count(cc.colitemkey) ciNum, max(cc.orderbyno) orderbyno ");
        buffer.append("from pad_checkaccomplish_info cc, pad_checkstatus_info cs ");
        buffer.append("where cc.cstatuskey = cs.cstatuskey and cc.minorchannel like '%").append(channelId).append("%' and ( 1 != 1 ");
        for (String keyItem : sqlMap.keySet()) {
            keys = keyItem.split(",");
            buffer.append(" or (cc.checkkey = '").append(keys[0]).append("' and cc.productkey = '").append(keys[1]).append("' ");
            buffer.append(" and (").append(sqlMap.get(keyItem)).append(")) ");
        }
        buffer.append(") group by cc.checkkey, cc.productkey, cc.cstatuskey having ( 1 != 1 ");
        /*for (String itemNum : checkItemMap.keySet()) {
            buffer.append(" or (cc.checkkey ='").append(itemNum).append("' and ciNum = ").append(checkItemMap.get(itemNum).size()).append(") ");
        }*/
        for (String itemNum : checkcheckItemMap.keySet()) {
        	for (String prokey : checkcheckItemMap.get(itemNum).keySet()) {
        		buffer.append(" or (cc.checkkey ='").append(itemNum).append("' and ciNum = ").append(checkcheckItemMap.get(itemNum).get(prokey).size()).append(") ");
			}
        }
        buffer.append(") order by orderbyno desc ");
        
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), null);
        String indexId = "";
        String proId = "";
        String valueId = "";
        String valueName = "";
        boolean findFlag = false;
        List<String> indexProIdLst = new ArrayList<String>();
        
        while (cursor.moveToNext()) {
            indexId = cursor.getString(cursor.getColumnIndex("checkkey"));
            proId = cursor.getString(cursor.getColumnIndex("productkey"));
            valueId = cursor.getString(cursor.getColumnIndex("cstatuskey"));
            valueName = cursor.getString(cursor.getColumnIndex("cstatusname"));

            if ("-1".equals(productKey) || productKey.equals(proId)) {
                if (indexProIdLst.contains(indexId + proId)) continue;
                for (ProIndex item : calculateLst) {
                    findFlag = false;
                    if (indexId.equals(item.getIndexId())) {
                        for (ProIndexValue proItem : item.getIndexValueLst()) {
                            if (proId.equals(proItem.getProId())) {
                                //判断修改指标值否是当前修改的指标
                                if(!"-1".equals(currIndexId) && proItem.getIndexId().equals(currIndexId)){
                                    isCurrIndexExsist=true;
                                }
                                proItem.setIndexValueId(valueId);
                                proItem.setIndexValueName(valueName);
                                findFlag = true;
                                indexProIdLst.add(indexId + proId);
                                break;
                            }
                        }
                        if (findFlag) break;
                    }
                }
            }
        }

        
        //针对铺货状态中没有空白和有效销售
        /*for(ProIndex item : calculateLst){
            for(ProIndexValue proItem : item.getIndexValueLst()){
                String data  = item.getIndexId()+proItem.getProId();
                if("-1".equals(productKey) ){
                    if(!indexProIdLst.contains(data)){
                        proItem.setIndexValueId(null);
                        proItem.setIndexValueName(null);
                    }
                }else{
                    //当前指标状态在数据里不存在，值设置为空
                    if(data.equals(currIndexId+productKey) && !isCurrIndexExsist){
                        proItem.setIndexValueId(null);
                        proItem.setIndexValueName(null); 
                    }
                }
            }
            
        }*/
        
        //针对铺货状态中没有空白和有效销售  冰冻化没有不合格
        for(ProIndex item : calculateLst){
            for(ProIndexValue proItem : item.getIndexValueLst()){
                String data  = item.getIndexId()+proItem.getProId();
                if("-1".equals(productKey) ){
                    if(!indexProIdLst.contains(data)){
                        /*proItem.setIndexValueId(null);
                        proItem.setIndexValueName(null);*/
                        setIndexValue(proItem, item.getIndexId());
                    }
                }else{
                    //当前指标状态在数据里不存在，值设置为空
                    if(data.equals(currIndexId+productKey) && !isCurrIndexExsist){
                        /*proItem.setIndexValueId(null);
                        proItem.setIndexValueName(null); */
                        setIndexValue(proItem, currIndexId);
                    }
                }
            }
        }

        //
    }
    
    // 
	public void setIndexValue(ProIndexValue proItem, String currIndexId) {
		if ("ad3030fb-e42e-47f8-a3ec-4229089aab5d".equals(currIndexId)) {// 铺货状态
			proItem.setIndexValueId("301");
			proItem.setIndexValueName("空白");
		} else if ("ad3030fb-e42e-47f8-a3ec-4229089aab6d".equals(currIndexId)) {// 道具生动化
			proItem.setIndexValueId("307");
			proItem.setIndexValueName("不合格");
		} else if ("ad3030fb-e42e-47f8-a3ec-4229089aab7d".equals(currIndexId)) {// 产品生动化
			proItem.setIndexValueId("309");
			proItem.setIndexValueName("不合格");
		} else if ("ad3030fb-e42e-47f8-a3ec-4229089aab8d".equals(currIndexId)) {// 冰冻化
			proItem.setIndexValueId("311");
			proItem.setIndexValueName("不合格");
		}
	}
    
    /**
     * 从checkexerecord表中删除没有供货关系,却有铺货指标的记录
     */
    public void deleteFromcheckexerecord() {
    	
    	AndroidDatabaseConnection connection = null;
    	
	    try {
	        DatabaseHelper helper = DatabaseHelper.getHelper(context);
	        MstCheckexerecordInfoDao dao = helper.getDao(MstCheckexerecordInfo.class);
	        
	        connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
			
	        // 获取checkexerecord表没有供货关系,却有铺货指标的记录
	        List<MstCheckexerecordInfo> stcLst = dao.queryFromcheckexerecord(helper);
	        // 删除记录
	        for (MstCheckexerecordInfo mstCheckexerecordInfo : stcLst) {
	        	dao.deletecheckexerecord(helper,mstCheckexerecordInfo.getRecordkey());
			}
	        
	        connection.commit(null);
	    } catch (SQLException e) {
	        Log.e(TAG, "删除没有供货关系,却有铺货指标的记录失败", e);
	    }
	}
    
    /**
     * 通过terminalcode查询MstGroupproductM表
     * 
     * @return
     */
    public List<MstGroupproductM> queryMstGroupproductM(String terminalcode,String startdate) { 
        // 获取分项采集数据
        List<MstGroupproductM> stcLst = new ArrayList<MstGroupproductM>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstGroupproductMDao dao = helper.getDao(MstGroupproductM.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
            stcLst = dao.queryMstGroupproductMByCreatedate(helper, terminalcode,startdate);
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取失败", e);
        }
        return stcLst;
    }
    
    /**
     * 
     * @param groupproductM
     */
	public void createMstGroupproductM(MstGroupproductM groupproductM) {
		AndroidDatabaseConnection connection = null;
		try {
		    DatabaseHelper helper = DatabaseHelper.getHelper(context);
		    
		    Dao<MstGroupproductM, String>  indexValueDao = helper.getMstGroupproductMDao();
		    Dao<MstCollectionexerecordInfo, String> proItemDao = helper.getMstCollectionexerecordInfoDao();
		    connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
		    connection.setAutoCommit(false);
		    
            indexValueDao.create(groupproductM);
            connection.commit(null);
		} catch (SQLException e) {
            Log.e(TAG, "获取失败", e);
        }
	}

	/**
	 * @param vo
	 */
	public void saveMstGroupproductM(MstGroupproductM vo) {
		
		AndroidDatabaseConnection connection = null;
        try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
		    Dao<MstGroupproductM, String>  indexValueDao = helper.getMstGroupproductMDao();
		    connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
		    connection.setAutoCommit(false);
		
			// 更新我品的现有库存
	        StringBuffer buffer = new StringBuffer();
	        buffer.append("update MST_GROUPPRODUCT_M set ifrecstand = ? ");
	        buffer.append("where gproductid = ?  ");
	        indexValueDao.executeRaw(buffer.toString(), new String[]{vo.getIfrecstand(),vo.getGproductid()});
	        connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取失败", e);
        }
	}
	
	/**
	 * 修改产品组合是否达标记录  uploadFlag = ? , padisconsistent = ?, updatetime = ?
	 * @param terminalcode
	 * @param createdate
	 */
	public void updateMstGroupproductM(String terminalcode,String createdate) {
		
		
		AndroidDatabaseConnection connection = null;
        try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
		    
		    Dao<MstGroupproductM, String>  indexValueDao = helper.getMstGroupproductMDao();
		    connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
		    connection.setAutoCommit(false);
		
			// 更新我品的现有库存
	        StringBuffer buffer = new StringBuffer();
	        buffer.append("update MST_GROUPPRODUCT_M set uploadflag = ? , padisconsistent = ?, updatetime = ?  ");
	        buffer.append("where terminalcode = ? and startdate = ?  ");
	        indexValueDao.executeRaw(buffer.toString(), new String[]{"1","0",DateUtil.getDateTimeStr(6),terminalcode,createdate});
        
	        connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取失败", e);
        }
	}
}
