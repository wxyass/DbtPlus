package et.tsingtaopad.visit.shopvisit.chatvie;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.tables.MstCmpagencyInfo;
import et.tsingtaopad.db.tables.MstCmpsupplyInfo;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.db.tables.MstVistproductInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.shopvisit.chatvie.domain.ChatVieStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：ChatVieService.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-19</br>      
 * 功能描述: 巡店拜访-聊竞品功能模块业务逻辑层</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class ChatVieService extends ShopVisitService {
    
    private final String TAG = "ChatVieService";

    public ChatVieService(Context context, Handler handler) {
        super(context, handler);
    }
    
    /**
     * 获取某次拜访的我品的进销存数据情况
     * 
     * @param visitId   拜访主键
     * @return
     */
    public List<ChatVieStc> queryViePro(String visitId) {
        
        List<ChatVieStc> lst = new ArrayList<ChatVieStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            lst = dao.queryViePro(helper, visitId);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
        
        return lst;
    }
    
    /***
     * 删除竞品重复拜访产品
     * @param visitkey
     */
    public void delRepeatVistProduct(String visitkey){
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVistproductInfoDao dao = helper.getDao(MstVistproductInfo.class);
            QueryBuilder<MstVistproductInfo, String> qb=dao.queryBuilder();
            Where<MstVistproductInfo, String> where=  qb.where();
            where.eq("visitkey", visitkey);
            where.and();
            where.isNull("productkey");
            where.and();
            where.ne("deleteflag", "1");
            qb.orderBy("productkey", true);
            qb.orderBy("updatetime", false);
            List<MstVistproductInfo> valueLst =qb.query();
            Map<String, MstVistproductInfo> map=new HashMap<String, MstVistproductInfo>();
            if (!CheckUtil.IsEmpty(valueLst)) {
                for (MstVistproductInfo item : valueLst) {
                	String key=item.getCmpproductkey();
                	if(!map.containsKey(key)){
                		map.put(key, item);
                	}else{
                		dao.delete(item);
                	}
                }
            }
        } catch (SQLException e) {
        	e.printStackTrace();
            Log.e(TAG, "删除重复拜访产品", e);
        }
    }
    
    /**
     * 保存销存页面数据，MST_VISTPRODUCT_INFO
     * 
     * @param dataLst       我品进度销存数据
     * @param visitId       拜访主键
     * @param termId        终端主键
     * @param visitM        拜访主表相关信息
     */
    public void saveVie(List<ChatVieStc> dataLst, 
                String visitId, String termId, MstVisitM visitM) {

        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstVistproductInfo, String> proDao = helper.getMstVistproductInfoDao();
            Dao<MstVisitM, String> visitDao = helper.getMstVisitMDao();
            Dao<MstCmpsupplyInfo, String> supplyDao = helper.getMstCmpsupplyInfoDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            
            // 维护拜访产品-竞品记录
            StringBuffer buffer;
            MstVistproductInfo proItem;
            for(ChatVieStc item : dataLst) {
                
                // 新增
                buffer = new StringBuffer();
                if (CheckUtil.isBlankOrNull(item.getRecordId())) {
                    proItem = new MstVistproductInfo();
                    proItem.setRecordkey(FunUtil.getUUID());
                    proItem.setVisitkey(visitId);
                    proItem.setCmpproductkey(item.getProId());
                    proItem.setCmpcomkey(item.getCommpayId());
                    proItem.setAgencykey(item.getAgencyId());
                    proItem.setAgencyname(item.getAgencyName());
                    proItem.setPurcprice(Double.valueOf(
                            FunUtil.isNullToZero(item.getChannelPrice())));
                    proItem.setRetailprice(Double.valueOf(
                            FunUtil.isNullToZero(item.getSellPrice())));
                    proItem.setCurrnum(Double.valueOf(
                            FunUtil.isNullToZero(item.getCurrStore())));
                    proItem.setSalenum(Double.valueOf(
                            FunUtil.isNullToZero(item.getMonthSellNum())));
                    proItem.setPadisconsistent(ConstValues.FLAG_0);
                    proItem.setDeleteflag(ConstValues.FLAG_0);
                    //proItem.setCreuser(ConstValues.loginSession.getUserCode());
                    proItem.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    //proItem.setUpdateuser(ConstValues.loginSession.getUserCode());
                    proItem.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                    proItem.setRemarks(item.getDescribe());
                    proDao.create(proItem);
                    
                // 更新
                } else {
                    buffer.append("update mst_vistproduct_info set ");
                    buffer.append("purcprice=?, retailprice=?, ");
                    buffer.append(" currnum=?, salenum=?, remarks=?, agencykey=?, agencyname=?,");
                    buffer.append("padisconsistent='0' ");
                    buffer.append("where recordkey=? ");
                    String[] args = new String[8];
                    args[0] = FunUtil.isNullToZero(item.getChannelPrice());
                    args[1] = FunUtil.isNullToZero(item.getSellPrice());
                    args[2] = FunUtil.isNullToZero(item.getCurrStore());
                    args[3] = FunUtil.isNullToZero(item.getMonthSellNum());
                    args[4] = FunUtil.isNullSetSpace(item.getDescribe());
                    args[5] = FunUtil.isNullSetSpace(item.getAgencyId());
                    args[6] = FunUtil.isNullSetSpace(item.getAgencyName());
                    args[7] = FunUtil.isNullSetSpace(item.getRecordId());
                    proDao.executeRaw(buffer.toString(), args);
                }
            }

            // 维护竞品供货关系表
            List<MstCmpsupplyInfo> supplyLst = 
                        supplyDao.queryForEq("terminalkey", termId);
            MstCmpsupplyInfo supply;
            for (ChatVieStc pro : dataLst) {
                supply = null;
                for (MstCmpsupplyInfo item : supplyLst) {
                    if (pro.getProId().equals(item.getCmpproductkey())) {
                        supply = item;
                        break;
                    }
                }
                if (supply == null) {
                    supply = new MstCmpsupplyInfo();
                    supply.setCmpsupplykey(FunUtil.getUUID());
                    supply.setCmpproductkey(pro.getProId());
                    supply.setTerminalkey(termId);
                    //supply.setCreuser(ConstValues.loginSession.getUserCode());
                    supply.setCreuser(PrefUtils.getString(context, "userCode", ""));
                }
                supply.setCmpcomkey(pro.getAgencyId());
                supply.setStatus(ConstValues.FLAG_0);
                supply.setInprice(pro.getChannelPrice());
                supply.setReprice(pro.getSellPrice());
                supply.setPadisconsistent(ConstValues.FLAG_0);
                supply.setDeleteflag(ConstValues.FLAG_0);
                //supply.setUpdateuser(ConstValues.loginSession.getUserCode());
                supply.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                supplyDao.createOrUpdate(supply);
            }
            
            // 更新拜访主表拜访记录
            buffer = new StringBuffer();
            buffer.append("update mst_visit_m set status=?, iscmpcollapse=?, remarks=? ,padisconsistent = '0' ");
            buffer.append("where visitkey= ? ");
            visitDao.executeRaw(buffer.toString(), new String[] {
                visitM.getStatus(), visitM.getIscmpcollapse(), visitM.getRemarks(), visitId});
            
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "保存聊竞品数据发生异常", e);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                Log.e(TAG, "回滚聊竞品数据发生异常", e1);
            }
        }
    }
    
    /**
     * 删除经销商与终端的产品供应关系
     * 
     * @param recordKey 拜访产品-竞品我品记录表主键
     * @param termId    终端ID
     * @param proId     产品ID
     */
    public boolean deleteSupply(String recordKey, String termId, String proId) {
        boolean isFlag=false;
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstVistproductInfo, String> proDao = 
                            helper.getDao(MstVistproductInfo.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            // 删除拜访产品-竞品我品记录表，相关数据
            StringBuffer buffer = new StringBuffer();
            buffer.append("delete from mst_vistproduct_info ");
            buffer.append("where RECORDKEY=? ");
            proDao.executeRaw(buffer.toString(), new String[] {recordKey});
//            StringBuffer buffer = new StringBuffer();
//            buffer.append("update mst_vistproduct_info set ");
//            buffer.append("padisconsistent ='0', deleteflag = '1' ");
//            buffer.append("where recordkey=? ");
//            proDao.executeRaw(buffer.toString(), new String[] {recordKey});
            
            // 更新竞品供货关系 
            String currDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
            buffer = new StringBuffer();
            buffer.append("update mst_cmpsupply_info set ");
            buffer.append("status ='1', cmpinvaliddate = ? ,padisconsistent ='0' ");
            buffer.append("where terminalkey = ? and cmpproductkey = ? ");
            proDao.executeRaw(buffer.toString(), new String[] {currDate, termId, proId});
            
            connection.commit(null);
            isFlag=true;
        } catch (Exception e) {
            isFlag=false;
            DbtLog.logUtils(TAG,"解除供货关系失败");
            DbtLog.logUtils(TAG,e.getMessage());
            e.printStackTrace();
            Log.e(TAG, "保存进销存数据发生异常", e);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                Log.e(TAG, "回滚进销存数据发生异常", e1);
            }
        }
        return isFlag;
    }
    
    /**
     * 获取竞品经销售商
     * 
     * @return
     */
    public List<MstCmpagencyInfo> queryCmpAgency() {

        List<MstCmpagencyInfo> lst = new ArrayList<MstCmpagencyInfo>();        
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);        
            Dao<MstCmpagencyInfo, String> dao = helper.getMstCmpagencyInfoDao();
            lst = dao.queryForEq("cmpagencystatus", "0");
        } catch (Exception e) {
            Log.e(TAG, "获取竞品经销售商失败", e);
        }
        
        // 添加请选择
        MstCmpagencyInfo info = new MstCmpagencyInfo();
        info.setCmpagencykey("-1");
        info.setCmpagencyname("请选择");
        
        if (lst == null) {
            lst = new ArrayList<MstCmpagencyInfo>();
            lst.add(info);
        } else {
            lst.add(0, info);
        }
        
        return lst;
    }
}
