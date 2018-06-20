package et.tsingtaopad.db.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexPromotionStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstPromotionsmDaoImpl.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-20</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class MstPromotionsmDaoImpl extends 
            BaseDaoImpl<MstPromotionsM, String> implements MstPromotionsmDao {

    public MstPromotionsmDaoImpl(ConnectionSource
                        connectionSource) throws SQLException {
        super(connectionSource, MstPromotionsM.class);
    }
    
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
    @Override
    public List<CheckIndexPromotionStc> queryPromotionByterm(
            SQLiteOpenHelper helper, String visitId, String channelId, String termLevel) {
        
        List<CheckIndexPromotionStc> lst = new ArrayList<CheckIndexPromotionStc>();
        
        StringBuffer buffer = new StringBuffer();
        buffer.append("select distinct pm.promotkey, pm.areaid, pm.ispictype, pm.promottype, ");
        buffer.append("pm.promotname, pm.startdate, pm.enddate, p.productkey, ");
        buffer.append("p.proname, pt.recordkey, pt.remarks, pt.isaccomplish  ");
        buffer.append("from mst_promotions_m pm ");
        buffer.append("inner join mst_promoproduct_info ps ");
        buffer.append(" on pm.promotkey = ps.promotkey and ps.typekey='1' and ps.productkey=? ");// 产品，渠道，终端类型
        buffer.append("inner join mst_promoproduct_info pl ");
        buffer.append(" on pm.promotkey = pl.promotkey and pl.typekey='2' and pl.productkey=? ");
        buffer.append("inner join mst_promoproduct_info pp ");
        buffer.append(" on pm.promotkey = pp.promotkey and pp.typekey='0' ");
        buffer.append("inner join mst_product_m p ");
        buffer.append(" on pp.productkey = p.productkey ");
        buffer.append("left join mst_promoterm_info pt ");
        buffer.append(" on pm.promotkey = pt.ptypekey and pt.visitkey =? ");
        buffer.append("where ? between pm.startdate and pm.enddate ");
        buffer.append("order by pm.promotkey ");

        String[] args = new String[4];
        args[0] = channelId;
        args[1] = termLevel;
        args[2] = visitId;
        args[3] = DateUtil.formatDate(new Date(), "yyyyMMdd");
        
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), args);
        CheckIndexPromotionStc item;
        String date = "";
        while (cursor.moveToNext()) {
            item = new CheckIndexPromotionStc();
            item.setPromotKey(cursor.getString(cursor.getColumnIndex("promotkey")));
            item.setAreaid(cursor.getString(cursor.getColumnIndex("areaid")));
            item.setIspictype(cursor.getString(cursor.getColumnIndex("ispictype")));
            item.setPromotType(cursor.getString(cursor.getColumnIndex("promottype")));
            item.setPromotName(cursor.getString(cursor.getColumnIndex("promotname")));
            item.setStartDate(cursor.getString(cursor.getColumnIndex("startdate")));
            item.setEndDate(cursor.getString(cursor.getColumnIndex("enddate")));
            item.setProId(cursor.getString(cursor.getColumnIndex("productkey")));
            item.setProName(cursor.getString(cursor.getColumnIndex("proname")));
            item.setRecordKey(cursor.getString(cursor.getColumnIndex("recordkey")));
            item.setReachNum(cursor.getString(cursor.getColumnIndex("remarks")));
            item.setIsAccomplish(cursor.getString(cursor.getColumnIndex("isaccomplish")));
            date = cursor.getString(cursor.getColumnIndex("startdate"));
            if (!CheckUtil.isBlankOrNull(date) && date.length() >= 8) {
                item.setStartDate(date.substring(0,4) + "."
                        + date.substring(4, 6) + "." + date.substring(6, 8));
            }
            date = cursor.getString(cursor.getColumnIndex("enddate"));
            if (!CheckUtil.isBlankOrNull(date) && date.length() >= 8) {
                item.setEndDate(date.substring(0,4) + "."
                        + date.substring(4, 6) + "." + date.substring(6, 8));
            }
            lst.add(item);
        }
        
        return lst;
    }
    
    /**
     * 促销活动查询
     * 
     * @param promId        活动ID
     * @param promSd        活动开始时间
     * @param searchD       查询日期
     * @param lineIds       线路ID
     * @param termLevels    终端等级
     * @return  Y:活动达成终端， N:活动未终端
     */
    public Map<String, List<String>> promotionSearch(SQLiteOpenHelper helper,
            String promId, String promSd, String searchD, String[] lineIds, String[] termLevels) {
        
        // 查询时间范围
        promSd = promSd + "000000";
        searchD = searchD + "235959";
        
        Map<String, List<String>> termMap = new HashMap<String, List<String>>();
        termMap.put("Y", new ArrayList<String>());
        termMap.put("N", new ArrayList<String>());
        
        List<String> args = new ArrayList<String>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("select tmm.terminalkey,tmm.terminalname, dd.isaccomplish ");
        buffer.append("from mst_route_m rm ");
        buffer.append("inner join mst_terminalinfo_m tmm on rm.routekey = tmm.routekey and tmm.status != '2' ");
        buffer.append("inner join mst_promoproduct_info ps on ps.promotkey= ? ");
        args.add(promId);
        buffer.append("  and ps.typekey='1' and tmm.sellchannel = ps.productkey ");
        buffer.append("inner join mst_promoproduct_info pl on pl.promotkey= ? ");
        args.add(promId);
        buffer.append("  and pl.typekey='2' and tmm.tlevel = pl.productkey ");
        buffer.append("left join ( ");
        buffer.append(" select ptm.ptypekey, ptm.terminalkey, ptm.isaccomplish from mst_promoterm_info ptm ");
        buffer.append(" inner join ( ");
        buffer.append("   select max(pt.ptypekey) ptypekey, max(pt.terminalkey) terminalkey, max(pt.startdate) startdate ");
        buffer.append(" from (select m.visitkey,m.visitdate from mst_visit_m m ");
        buffer.append(" inner join (select max(visitdate) maxvisitdate, max(terminalkey) maxterminalkey from mst_visit_m ");
        buffer.append(" where visitdate between '").append(promSd).append("' and '").append(searchD).append("' ");
        buffer.append(" and coalesce(deleteflag, '0') != '1' and coalesce(status,'1') = '1' ");
        if (!CheckUtil.IsEmpty(lineIds)){
            buffer.append(" and routekey in ('");
            for(String lineId : lineIds) {
                buffer.append(lineId).append("','");
            }
            buffer.deleteCharAt(buffer.length() - 1)
                .deleteCharAt(buffer.length() - 1).append(") ");
        }
        buffer.append("group by terminalkey) v on m.terminalkey = v.maxterminalkey and m.visitdate = v.maxvisitdate) vm, mst_promoterm_info pt ");
        buffer.append(" where vm.visitkey = pt.visitkey and pt.ptypekey = ? and vm.visitdate between ? and ? ");
        args.add(promId);
        args.add(promSd);
        args.add(searchD);
        buffer.append("   group by pt.ptypekey, pt.terminalkey, pt.startdate) d ");
        buffer.append(" on ptm.ptypekey = d.ptypekey and ptm.terminalkey = d.terminalkey and ptm.startdate = d.startdate) dd ");
        buffer.append("on tmm.terminalkey = dd.terminalkey  ");
        buffer.append("where 1 = 1 ");
        
        // 线路
        if (!CheckUtil.IsEmpty(lineIds)){
            buffer.append(" and  rm.routekey in (");
            for(String lineId : lineIds) {
                buffer.append("?,");
                args.add(lineId);
            }
            buffer.deleteCharAt(buffer.length() - 1).append(") ");
        }
        
        // 终端等级 
        if (!CheckUtil.IsEmpty(termLevels)) {
            buffer.append(" and tmm.tlevel in (");
            for (String levelId : termLevels) {
                buffer.append("?,");
                args.add(levelId);
            }
            buffer.deleteCharAt(buffer.length() - 1).append(") ");
        }
        buffer.append("order by rm.routename, tmm.sequence ");
        
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), args.toArray(new String[]{}));
        while (cursor.moveToNext()) {
            if (ConstValues.FLAG_1.equals(
                    cursor.getString(cursor.getColumnIndex("isaccomplish")))) {
                termMap.get("Y").add(cursor.getString(cursor.getColumnIndex("terminalname")));
            } else {
                termMap.get("N").add(cursor.getString(cursor.getColumnIndex("terminalname")));
            }
        }
        return termMap;
    }

}
