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
import et.tsingtaopad.db.dao.MstRouteMDao;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.visit.shopvisit.line.domain.MstRouteMStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：MstRouteMDao.java</br> 
 * 作者：hongen </br>
 * 创建时间：2013-11-27</br> 
 * 功能描述: 线路档案主表DAO层</br> 版本 V 1.0</br> 
 * 修改履历</br> 
 * 日期 原因 BUG号 修改人 修改版本</br>
 */
public class MstRouteMDaoImpl extends 
            BaseDaoImpl<MstRouteM, String> implements MstRouteMDao {

	public MstRouteMDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MstRouteM.class);
	}

	/**
     * 获取线路列表信息，包含计划拜访时间
     * 
     * 用于：巡店拜访  -- 线路选择
     */
    @Override
    public List<MstRouteMStc> queryLine(SQLiteOpenHelper helper) {
        
        List<MstRouteMStc> lst = new ArrayList<MstRouteMStc>();
        
        StringBuffer buffer = new StringBuffer();
        buffer.append("select m.routekey, m.routecode, m.routename, pd.plandate, pd.plandate2 ");
        buffer.append("from mst_route_m m ");
        buffer.append("left join ( select p.linekey, max(p.plandate) plandate, ");
        buffer.append(" min(case when p.plandate >= ? then p.plandate else '' end) plandate2 ");
        buffer.append(" from mst_planforuser_m p ");
        buffer.append(" where p.plandate is not null and p.planstatus != '1' ");
        buffer.append(" and p.plandate between ? and ? ");
        buffer.append(" group by p.linekey) pd ");
        buffer.append(" on m.routekey = pd.linekey ");
        buffer.append("where coalesce(m.routestatus, '0') != '1' ");
        buffer.append("   and coalesce(m.deleteflag, '0') != '1' ");
        buffer.append("order by m.orderbyno, m.routeName ");

        // 获取登录日期所在周的开始及结束时间
        String[] args = new String[3];
        Date currDate = new Date();
        args[0] = DateUtil.formatDate(currDate, "yyyyMMdd");
        args[1] = DateUtil.getWeekBegin(currDate, "yyyyMMdd");
        args[2] = DateUtil.getWeekEnd(currDate, "yyyyMMdd");
        
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), args);
        MstRouteMStc item;
        String planDate = "";
        String planDate2 = "";
        while (cursor.moveToNext()) {
            item = new MstRouteMStc();
            item.setRoutekey(cursor.getString(cursor.getColumnIndex("routekey")));
            item.setRoutecode(cursor.getString(cursor.getColumnIndex("routecode")));
            item.setRoutename(cursor.getString(cursor.getColumnIndex("routename")));
            planDate = cursor.getString(cursor.getColumnIndex("plandate"));
            planDate2 = cursor.getString(cursor.getColumnIndex("plandate2"));
            if (!CheckUtil.isBlankOrNull(planDate2)) {
                planDate = planDate2;
            }
            if (!CheckUtil.isBlankOrNull(planDate) && planDate.length() >= 8) {
                item.setPlanDate(planDate.substring(0,4) + "-"
                    + planDate.substring(4, 6) + "-" + planDate.substring(6, 8));
            }
            lst.add(item);
        }
        
        return lst;
    }
    
    
    /**
     * 获取线路上次拜访的日期
     * 
     * @param helper
     * @param lineIdLst
     * @return              Key:线路ID、 Value:该线路上次拜访日期
     */
    public Map<String, String> queryPrevVisitDate(
                        SQLiteOpenHelper helper, List<String> lineIdLst) {
        
        Map<String, String> visitMap = new HashMap<String, String>();
        if (!CheckUtil.IsEmpty(lineIdLst)) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("select m.routekey, max(m.visitdate) visitdate ");
            buffer.append("from v_visit_m m where m.routekey in ( ");
            for (String item : lineIdLst) {
                buffer.append("'").append(item).append("', ");
            }
            buffer.deleteCharAt(buffer.length() - 2);
            buffer.append(")");
            buffer.append(" and padisconsistent='1' ");
            buffer.append(" group by m.routekey ");
            
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery(buffer.toString(), null);
            while (cursor.moveToNext()) {
                visitMap.put(cursor.getString(0), cursor.getString(1));
            }
        }
        
        return visitMap;
    }

    
    /**
     * 获取某线路下的终端个数
     * 
     * @param helper
     * @param lineId    线路ID，为null时返回所有线路下的终端数
     * @return
     */
    public int queryForTermNum(SQLiteOpenHelper helper, String lineId) {
        
        List<String> args = new ArrayList<String>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("select count(distinct tm.terminalkey) num ");
        buffer.append("from mst_terminalinfo_m tm ");
        if (!CheckUtil.isBlankOrNull(lineId)) {
            args.add(lineId);
            buffer.append("where tm.routekey = ? ");
        } else {
            buffer.append("inner join mst_route_m rm on rm.routekey = tm.routekey ");
        }
        buffer.append(" and  coalesce(tm.status,'0') != '2' ");
        
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), args.toArray(new String[]{}));
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    // 获取次渠道集合
	@Override
	public List<KvStc> querySecondSell(SQLiteOpenHelper helper) {
		List<KvStc> dataDicLst = new ArrayList<KvStc>();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("select c.dicname,c.diccode,c.parentcode from CMM_DATADIC_M a ");
		buffer.append(" inner join  CMM_DATADIC_M b   ");
		buffer.append(" on  b.parentcode  = a.diccode  ");
		buffer.append(" inner join  CMM_DATADIC_M c  ");
		buffer.append(" on  c.parentcode  = b.diccode ");
		buffer.append(" where a.parentcode = ?  ");
		Cursor cursor = helper.getReadableDatabase().rawQuery(buffer.toString(), new String[] { "ce4be953-2a89-4c48-b416-7a7adc808690" });
		KvStc item;
		while (cursor.moveToNext()) {
			item = new KvStc();
			item.setValue(cursor.getString(cursor.getColumnIndex("dicname")));
			item.setKey(cursor.getString(cursor.getColumnIndex("diccode")));
			item.setParentKey(cursor.getString(cursor.getColumnIndex("parentcode")));
			dataDicLst.add(item);
		}
		return dataDicLst;
		
	}
}
