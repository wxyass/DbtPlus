package et.tsingtaopad.visit.shopvisit.term;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PinYin4jUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.visit.shopvisit.term.domain.TermSequence;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermListService.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-28</br>
 * 功能描述: 巡店拜访_终端列表的业务逻辑</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("DefaultLocale")
public class TermListService {

    private final String TAG = "TermListService";

    private Context context;

    public TermListService(Context context) {
        this.context = context;
    }

    /***
     * 通过终端名称模糊查询终端
     * @param termName
     * @return
     */
    public List<MstTermListMStc> getTermListByName(String termName) {

        List<MstTermListMStc> terminalList = new ArrayList<MstTermListMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            // 获取线中下各终端显示列表,并把拜访时间添加进去
            List<MstTermListMStc> termlst = dao.getTermListByName(helper, termName);
            terminalList.addAll(termlst);
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        return terminalList;
    }

    /**
     * 获取相应的数据列表数据
     *
     * @param lineKeys 线路表主键
     * @return
     */
    public List<MstTermListMStc> queryTerminal(List<String> lineKeys) {

        List<MstTermListMStc> terminalList = new ArrayList<MstTermListMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);

            // 各终端当天拜访的进店及离店时间, key:终端主键、value:拜访时间
            Map<String, String> visitTimeMap = new HashMap<String, String>();
            for (int i = 0; i < lineKeys.size(); i++) {

                // 获取某线路下的各终端当天的所有拜访进店及离店时间
                //List<MstTermListMStc> termlst = dao.queryTermVistTime(helper, lineKeys.get(i));
                List<MstTermListMStc> termlst = dao.queryTermVistTimeByLoginDate(helper, lineKeys.get(i), PrefUtils.getString(context, "loginDate", ""));
                StringBuffer buffer;
                for (MstTermListMStc item : termlst) {
                    buffer = new StringBuffer();
                    if (item.getVisitDate() != null && item.getVisitDate().length() >= 14) {
                        buffer.append(item.getVisitDate().substring(8, 10)).append(":");
                        buffer.append(item.getVisitDate().substring(10, 12)).append(":");
                        buffer.append(item.getVisitDate().substring(12, 14)).append("~");
                    }
                    if (item.getEndDate() != null && item.getEndDate().length() >= 14) {
                        buffer.append(item.getEndDate().substring(8, 10)).append(":");
                        buffer.append(item.getEndDate().substring(10, 12)).append(":");
                        buffer.append(item.getEndDate().substring(12, 14));
                    }
                    buffer.append("、 ");
                    if (visitTimeMap.containsKey(item.getTerminalkey())) {
                        buffer.insert(0, visitTimeMap.get(item.getTerminalkey()));
                    }
                    visitTimeMap.put(item.getTerminalkey(), buffer.toString());
                }


                // 获取路线下终端容量
                List<MstTermListMStc> terms = dao.getTermList_tvolnum(helper, lineKeys.get(i),false);

                // 设置容量排序
                for (int j = 0; j < terms.size(); j++) {
                    MstTermListMStc mStc = (MstTermListMStc)terms.get(j);
                    mStc.setTopnum((j+1)+"");
                }

                // 获取线中下各终端显示列表,并把拜访时间添加进去
                termlst = dao.queryTermLst(helper, lineKeys.get(i));
                String visitTime = "";
                for (MstTermListMStc item : termlst) {

                    // 设置top
                    for (MstTermListMStc termListMStc : terms) {
                        if (termListMStc.getTerminalkey().equals(item.getTerminalkey())) {
                            item.setTopnum(termListMStc.getTopnum());
                        }
                    }


                    // 设置拜访时间
                    if (visitTimeMap.containsKey(item.getTerminalkey())) {
                        visitTime = visitTimeMap.get(item.getTerminalkey());
                        visitTime = visitTime.substring(0, visitTime.length() - 2);
                        item.setVisitTime(visitTime);
                    }


                }
                terminalList.addAll(termlst);
            }
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }

        return terminalList;
    }

    /**
     * 获取相应的数据列表数据 今天已拜访终端
     *
     * @param lineKeys 线路表主键
     * @return
     */
    public List<MstTermListMStc> queryminal2(List<String> lineKeys) {

        List<MstTermListMStc> terminalList = new ArrayList<MstTermListMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);

            // 各终端当天拜访的进店及离店时间, key:终端主键、value:拜访时间
            Map<String, String> visitTimeMap = new HashMap<String, String>();
            for (int i = 0; i < lineKeys.size(); i++) {

                // 获取某线路下的各终端当天的所有拜访进店及离店时间
                //List<MstTermListMStc> termlst = dao.queryTermVistTime(helper, lineKeys.get(i));
                List<MstTermListMStc> termlst = dao.queryTermVistTimeByLoginDate(helper, lineKeys.get(i), PrefUtils.getString(context, "loginDate", ""));
                StringBuffer buffer;
                for (MstTermListMStc item : termlst) {
                    buffer = new StringBuffer();
                    if (item.getVisitDate() != null && item.getVisitDate().length() >= 14) {
                        buffer.append(item.getVisitDate().substring(8, 10)).append(":");
                        buffer.append(item.getVisitDate().substring(10, 12)).append(":");
                        buffer.append(item.getVisitDate().substring(12, 14)).append("~");
                    }
                    if (item.getEndDate() != null && item.getEndDate().length() >= 14) {
                        buffer.append(item.getEndDate().substring(8, 10)).append(":");
                        buffer.append(item.getEndDate().substring(10, 12)).append(":");
                        buffer.append(item.getEndDate().substring(12, 14));
                    }
                    buffer.append("、 ");
                    if (visitTimeMap.containsKey(item.getTerminalkey())) {
                        buffer.insert(0, visitTimeMap.get(item.getTerminalkey()));
                    }
                    visitTimeMap.put(item.getTerminalkey(), buffer.toString());
                }

                // 获取线中下各终端显示列表,并把拜访时间添加进去
                //termlst = dao.queryTermLst(helper, lineKeys.get(i));
                termlst = dao.queryTermUpflag(helper, lineKeys.get(i));
                //termlst = dao.queryTermVistTime(helper, lineKeys.get(i));
                String visitTime = "";
                for (MstTermListMStc item : termlst) {
                    if (visitTimeMap.containsKey(item.getTerminalkey())) {
                        visitTime = visitTimeMap.get(item.getTerminalkey());
                        visitTime = visitTime.substring(0, visitTime.length() - 2);
                        item.setVisitTime(visitTime);
                    }
                }
                terminalList.addAll(termlst);
            }
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }

        return terminalList;
    }

    /**
     * 获取相应的数据列表数据 今天未拜访终端
     *
     * @param lineKeys 线路表主键
     * @return
     */
    public List<MstTermListMStc> querinal3(List<String> lineKeys) {

        List<MstTermListMStc> terminalList = new ArrayList<MstTermListMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);

            // 各终端当天拜访的进店及离店时间, key:终端主键、value:拜访时间
            Map<String, String> visitTimeMap = new HashMap<String, String>();
            for (int i = 0; i < lineKeys.size(); i++) {

                // 获取某线路下的各终端当天的所有拜访进店及离店时间
                //List<MstTermListMStc> termlst = dao.queryTermVistTime(helper, lineKeys.get(i));
                List<MstTermListMStc> termlst = dao.queryTermVistTimeByLoginDate(helper, lineKeys.get(i), PrefUtils.getString(context, "loginDate", ""));
                StringBuffer buffer;
                for (MstTermListMStc item : termlst) {
                    buffer = new StringBuffer();
                    if (item.getVisitDate() != null && item.getVisitDate().length() >= 14) {
                        buffer.append(item.getVisitDate().substring(8, 10)).append(":");
                        buffer.append(item.getVisitDate().substring(10, 12)).append(":");
                        buffer.append(item.getVisitDate().substring(12, 14)).append("~");
                    }
                    if (item.getEndDate() != null && item.getEndDate().length() >= 14) {
                        buffer.append(item.getEndDate().substring(8, 10)).append(":");
                        buffer.append(item.getEndDate().substring(10, 12)).append(":");
                        buffer.append(item.getEndDate().substring(12, 14));
                    }
                    buffer.append("、 ");
                    if (visitTimeMap.containsKey(item.getTerminalkey())) {
                        buffer.insert(0, visitTimeMap.get(item.getTerminalkey()));
                    }
                    visitTimeMap.put(item.getTerminalkey(), buffer.toString());
                }

                // 获取线中下各终端显示列表,并把拜访时间添加进去
                List<MstTermListMStc> termlstAll = dao.queryTermLst(helper, lineKeys.get(i));
                //termlst = dao.queryTermUpflag(helper, lineKeys.get(i));
                //termlst = dao.queryTermVistTime(helper, lineKeys.get(i));
                //String visitTime = "";
                //    			for (MstTermListMStc item : termlst)
                //    			{
                //
                //    			}
                for (MstTermListMStc item1 : termlstAll)// 此路线所以终端
                {
                    if (termlst.size() > 0) {
                        for (MstTermListMStc item2 : termlst)// 今天已拜访终端
                        {
                            if (!item1.getTerminalkey().equals(item2.getTerminalkey())) {
                                terminalList.add(item1);
                                break; // 防止添加两遍
                            }
                        }
                    } else {
                        terminalList = termlstAll;
                    }

                }

            }
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }

        return terminalList;
    }

    /**
     * 终端选择 弹出选择列表数据查询
     *
     * @param checkkey
     * @param lineKeys
     * @param promotKey
     * @param isProductTerminal
     * @param isPromotion
     * @return
     */
    public List<MstTermListMStc> getTerMinalVisitResult(String checkkey, List<String> lineKeys, String promotKey, boolean isProductTerminal, boolean isPromotion, String proId, boolean isother, boolean isjingpintuijin, boolean isDaoju, boolean isChanpinhua, boolean isBingdonghua) {
        List<MstTermListMStc> terminals = new ArrayList<MstTermListMStc>();
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();
        StringBuffer buffer = new StringBuffer();
        String brackReplace = FunUtil.brackReplace(lineKeys);
        if (isPromotion) {
            //促销活动查询 加上活动状态
            /*		select distinct tm.terminalkey,tm.terminalname, pt.isaccomplish
                    from mst_terminalinfo_m tm
            		inner join mst_promoproduct_info ps on tm.sellchannel = ps.productkey and ps.promotkey = ? and ps.typekey='1' --当前活动主键
            		inner join mst_promoproduct_info pl on tm.tlevel = pl.productkey and pl.promotkey = ? and pl.typekey='2' --当前活动主键
            		left join v_visit_m_newest vm on tm.terminalkey = vm.TERMINALKEY
            		left join mst_promoterm_info pt on pt.visitkey = vm.VISITKEY and pt.ptypekey = ? --当前活动主键

            		where tm.routekey = ? --当前线路Id
            		       and coalesce(tm.status,'0') != '2'*/
            buffer.append("select distinct tm.terminalkey,tm.terminalname, pt.isaccomplish ");
            buffer.append("from mst_terminalinfo_m tm ");
            buffer.append("inner join mst_promoproduct_info ps on tm.sellchannel = ps.productkey and ps.promotkey = ? and ps.typekey='1' ");
            buffer.append("inner join mst_promoproduct_info pl on tm.tlevel = pl.productkey and pl.promotkey = ? and pl.typekey='2' ");
            buffer.append("left join v_visit_m_newest vm on tm.terminalkey = vm.TERMINALKEY ");
            buffer.append("left join mst_promoterm_info pt on pt.visitkey = vm.VISITKEY and pt.ptypekey = ? ");
            buffer.append("where tm.routekey in ( ");
            buffer.append(brackReplace);
            buffer.append(")");
            buffer.append("and coalesce(tm.status,'0') != '2' ");
            buffer.append("order by  (coalesce(tm.sequence,10000) + 0) asc, tm.orderbyno, tm.terminalname ");

            if (database.isOpen()) {
                Cursor cursor = database.rawQuery(buffer.toString(), new String[]{promotKey, promotKey, promotKey});//
                String s = buffer.toString();
                while (cursor.moveToNext()) {
                    MstTermListMStc terminal = new MstTermListMStc();
                    String terminalkey = cursor.getString(cursor.getColumnIndex("terminalkey"));
                    String terminalname = cursor.getString(cursor.getColumnIndex("terminalname"));
                    String isaccomplish = cursor.getString(cursor.getColumnIndex("isaccomplish"));
                    terminal.setTerminalname(terminalname);
                    terminal.setTerminalkey(terminalkey);
                    if ("1".equals(isaccomplish)) {

                        terminal.setTerminalStatus("达成");
                    } else if ("0".equals(isaccomplish)) {

                        terminal.setTerminalStatus("未达成");
                    } else {
                        //terminal.setTerminalStatus(isaccomplish);
                        terminal.setTerminalStatus("未达成");

                    }
                    terminals.add(terminal);
                }
            }

        } else if (isProductTerminal) {

            //空白终端查询加上铺货状态
            buffer.append("select tm.terminalkey,tm.terminalname, vm.VISITDATE, cr.checkkey, cr.acresult, cs.cstatusname ");
            buffer.append("from mst_terminalinfo_m tm ");
            buffer.append("left join v_visit_m_newest vm on tm.terminalkey = vm.TERMINALKEY ");
            //buffer.append("left join mst_checkexerecord_info cr on vm.VISITKEY = cr.visitkey and cr.checkkey = ?");
            buffer.append("left join mst_checkexerecord_info cr on cr.terminalkey = tm.terminalkey and cr.checkkey = ? ");
            buffer.append("  and cr.deleteflag!='" + ConstValues.delFlag + "' ");
            buffer.append("  and cr.enddate='30001201' and cr.resultstatus=0 ");
            buffer.append("  and cr.productkey ='").append(FunUtil.isBlankOrNullTo(proId, "-")).append("' ");
            buffer.append("left join pad_checkstatus_info cs on cr.acresult = cs.cstatuskey ");
            buffer.append("where tm.routekey in ( ");
            buffer.append(brackReplace);
            buffer.append(") ");
            buffer.append("and coalesce(tm.status,'0') != '2' ");
            buffer.append("order by (coalesce(tm.sequence,10000) + 0) asc, tm.orderbyno, tm.terminalname ");
            if (database.isOpen()) {
                Cursor cursor = database.rawQuery(buffer.toString(), new String[]{PropertiesUtil.getProperties("check_puhuo")});

                while (cursor.moveToNext()) {
                    MstTermListMStc terminal = new MstTermListMStc();
                    String terminalname = cursor.getString(cursor.getColumnIndex("terminalname"));
                    String terminalkey = cursor.getString(cursor.getColumnIndex("terminalkey"));
                    String visitdate = cursor.getString(cursor.getColumnIndex("visitdate"));
                    String cstatusname = cursor.getString(cursor.getColumnIndex("cstatusname"));
                    terminal.setTerminalkey(terminalkey);
                    terminal.setTerminalname(terminalname);
                    terminal.setVisitDate(visitdate);
                    terminal.setTerminalStatus(cstatusname);
                    terminals.add(terminal);
                }

            }
        } else if (isChanpinhua)// 产品生动化
        {

            //空白终端查询加上铺货状态
            buffer.append("select tm.terminalkey,tm.terminalname, vm.VISITDATE, cr.checkkey, cr.acresult, cs.cstatusname ");
            buffer.append("from mst_terminalinfo_m tm ");
            buffer.append("left join v_visit_m_newest vm on tm.terminalkey = vm.TERMINALKEY ");
            //buffer.append("left join mst_checkexerecord_info cr on vm.VISITKEY = cr.visitkey and cr.checkkey = ?");
            buffer.append("left join mst_checkexerecord_info cr on cr.terminalkey = tm.terminalkey and cr.checkkey = ? ");
            buffer.append("  and cr.deleteflag!='" + ConstValues.delFlag + "' ");
            buffer.append("  and cr.enddate='30001201' and cr.resultstatus=0 ");
            buffer.append("  and cr.productkey ='").append(FunUtil.isBlankOrNullTo(proId, "-")).append("' ");
            buffer.append("left join pad_checkstatus_info cs on cr.acresult = cs.cstatuskey ");
            buffer.append("where tm.routekey in ( ");
            buffer.append(brackReplace);
            buffer.append(") ");
            buffer.append("and coalesce(tm.status,'0') != '2' ");
            buffer.append("order by (coalesce(tm.sequence,10000) + 0) asc, tm.orderbyno, tm.terminalname ");
            if (database.isOpen()) {
                Cursor cursor = database.rawQuery(buffer.toString(), new String[]{PropertiesUtil.getProperties("check_chanpin")});

                while (cursor.moveToNext()) {
                    MstTermListMStc terminal = new MstTermListMStc();
                    String terminalname = cursor.getString(cursor.getColumnIndex("terminalname"));
                    String terminalkey = cursor.getString(cursor.getColumnIndex("terminalkey"));
                    String visitdate = cursor.getString(cursor.getColumnIndex("visitdate"));
                    String cstatusname = cursor.getString(cursor.getColumnIndex("cstatusname"));
                    terminal.setTerminalkey(terminalkey);
                    terminal.setTerminalname(terminalname);
                    terminal.setVisitDate(visitdate);
                    if (TextUtils.isEmpty(cstatusname) || cstatusname.length() <= 0) {
                        terminal.setTerminalStatus("");
                    } else {
                        terminal.setTerminalStatus(cstatusname);
                    }
                    terminals.add(terminal);
                }

            }
        } else if (isBingdonghua)// 冰冻化
        {

            //空白终端查询加上铺货状态
            buffer.append("select tm.terminalkey,tm.terminalname, vm.VISITDATE, cr.checkkey, cr.acresult, cs.cstatusname ");
            buffer.append("from mst_terminalinfo_m tm ");
            buffer.append("left join v_visit_m_newest vm on tm.terminalkey = vm.TERMINALKEY ");
            //buffer.append("left join mst_checkexerecord_info cr on vm.VISITKEY = cr.visitkey and cr.checkkey = ?");
            buffer.append("left join mst_checkexerecord_info cr on cr.terminalkey = tm.terminalkey and cr.checkkey = ? ");
            buffer.append("  and cr.deleteflag!='" + ConstValues.delFlag + "' ");
            buffer.append("  and cr.enddate='30001201' and cr.resultstatus=0 ");
            buffer.append("  and cr.productkey ='").append(FunUtil.isBlankOrNullTo(proId, "-")).append("' ");
            buffer.append("left join pad_checkstatus_info cs on cr.acresult = cs.cstatuskey ");
            buffer.append("where tm.routekey in ( ");
            buffer.append(brackReplace);
            buffer.append(") ");
            buffer.append("and coalesce(tm.status,'0') != '2' ");
            buffer.append("order by (coalesce(tm.sequence,10000) + 0) asc, tm.orderbyno, tm.terminalname ");
            if (database.isOpen()) {
                Cursor cursor = database.rawQuery(buffer.toString(), new String[]{PropertiesUtil.getProperties("check_bingdong")});

                while (cursor.moveToNext()) {
                    MstTermListMStc terminal = new MstTermListMStc();
                    String terminalname = cursor.getString(cursor.getColumnIndex("terminalname"));
                    String terminalkey = cursor.getString(cursor.getColumnIndex("terminalkey"));
                    String visitdate = cursor.getString(cursor.getColumnIndex("visitdate"));
                    String cstatusname = cursor.getString(cursor.getColumnIndex("cstatusname"));
                    terminal.setTerminalkey(terminalkey);
                    terminal.setTerminalname(terminalname);
                    terminal.setVisitDate(visitdate);
                    if (TextUtils.isEmpty(cstatusname) || cstatusname.length() <= 0) {
                        terminal.setTerminalStatus("");
                    } else {
                        terminal.setTerminalStatus(cstatusname);
                    }
                    terminals.add(terminal);
                }

            }
        }
        /*
        else if (isDaoju)// 道具生动化
        {
        	
        	//
        	buffer.append("select tm.terminalkey,tm.terminalname, vm.VISITDATE, cr.checkkey, cr.acresult, cs.cstatusname ");
        	buffer.append("from mst_terminalinfo_m tm ");
        	buffer.append("left join v_visit_m_newest vm on tm.terminalkey = vm.TERMINALKEY ");
        	//buffer.append("left join mst_checkexerecord_info cr on vm.VISITKEY = cr.visitkey and cr.checkkey = ?");
        	buffer.append("left join mst_checkexerecord_info cr on cr.terminalkey = tm.terminalkey and cr.checkkey = ? ");
        	buffer.append("  and cr.deleteflag!='" + ConstValues.delFlag + "' ");
        	buffer.append("  and cr.enddate='30001201' and cr.resultstatus=0 ");
        	
        	//buffer.append("  and cr.productkey ='").append(FunUtil.isBlankOrNullTo(proId, "-")).append("' ");
        	buffer.append("left join pad_checkstatus_info cs on cr.acresult = cs.cstatuskey ");
        	buffer.append("where tm.routekey in ( ");
        	buffer.append(brackReplace);
        	buffer.append(") ");
        	buffer.append("and coalesce(tm.status,'0') != '2' ");
        	buffer.append("order by (coalesce(tm.sequence,10000) + 0) asc, tm.orderbyno, tm.terminalname ");
        	if (database.isOpen())
        	{
        		Cursor cursor = database.rawQuery(buffer.toString(), new String[] { PropertiesUtil.getProperties("check_daoju") });
        		
        		while (cursor.moveToNext())
        		{
        			MstTermListMStc terminal = new MstTermListMStc();
        			String terminalname = cursor.getString(cursor.getColumnIndex("terminalname"));
        			String terminalkey = cursor.getString(cursor.getColumnIndex("terminalkey"));
        			String visitdate = cursor.getString(cursor.getColumnIndex("visitdate"));
        			String cstatusname = cursor.getString(cursor.getColumnIndex("cstatusname"));
        			terminal.setTerminalkey(terminalkey);
        			terminal.setTerminalname(terminalname);
        			terminal.setVisitDate(visitdate);
        			terminal.setTerminalStatus(cstatusname);
        			terminals.add(terminal);
        		}
        		
        	}
        }*/
        else if (isother) {// 其他
            /*
        	buffer.append("select m.terminalkey, m.terminalcode, m.terminalname,m.status,m.sequence,  ");
            buffer.append(" m.selftreaty,  ");
            buffer.append("m.minorchannel ");
            buffer.append("from mst_terminalinfo_m m ");
            buffer.append("where coalesce(m.status,'0') != '2' and m.routekey=?  and coalesce(m.deleteflag,'0') != '1' ");
            buffer.append("order by m.sequence+0 asc, m.orderbyno, m.terminalname ");
            */
            buffer.append("select m.terminalkey, m.terminalcode, m.terminalname,m.status,m.sequence,cr.checkkey,cr.acresult, vm.isself, ");
            buffer.append("vm.iscmp, m.selftreaty, vm.cmptreaty, vm.padisconsistent, vm.uploadFlag, ");
            buffer.append("m.minorchannel, dm.dicname terminalType, vm.visitdate ");
            buffer.append("from mst_terminalinfo_m m ");
            buffer.append("left join cmm_datadic_m dm on m.minorchannel = dm.diccode      and coalesce(dm.deleteflag,'0') != '1' ");
            //buffer.append("left join v_visit_m_newest vm on m.terminalkey = vm.terminalkey ");
            buffer.append("left join mst_visit_m vm on m.terminalkey = vm.terminalkey and vm.padisconsistent ='1' ");// 拜访已上传的 其他 显示是否合作到位

            // 新加是否合作到位 此取的值会错(新拜访时,但未确定,v_visit_m_newest产生新的visitkey,而mst_checkexerecord_info没生成新的VISITKEY的记录,所以取不到,会是null)
            buffer.append("left join mst_checkexerecord_info cr on cr.visitkey = vm.VISITKEY and cr.checkkey = ? ");
            //buffer.append("  and cr.deleteflag!='" + ConstValues.delFlag + "' ");
            //buffer.append("  and cr.enddate='30001201' and cr.resultstatus=0 ");
            //buffer.append("  and cr.productkey ='").append(FunUtil.isBlankOrNullTo(proId, "-")).append("' ");


            buffer.append("where coalesce(m.status,'0') != '2' and m.routekey=?  and coalesce(m.deleteflag,'0') != '1' ");
            buffer.append("group BY m.terminalkey ");// 分组 每个终端只取一条
            buffer.append("order by m.sequence+0 asc, m.orderbyno, m.terminalname, vm.visitdate ");// 同时以拜访时间排序

            if (database.isOpen()) {
                //Cursor cursor = database.rawQuery(buffer.toString(), new String[] {lineKeys.get(0)});//
                Cursor cursor = database.rawQuery(buffer.toString(), new String[]{"666b74b3-b221-4920-b549-d9ec39a463fd", lineKeys.get(0)});//
                String s = buffer.toString();
                MstTermListMStc item = new MstTermListMStc();
                String visitDate = "";
                String currDay = DateUtil.formatDate(new Date(), "yyyyMMdd");
                while (cursor.moveToNext()) {
                	
                	/*
                    item = new MstTermListMStc();
                    item.setRoutekey(lineKeys.get(0));
                    item.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
                    item.setTerminalcode(cursor.getString(cursor.getColumnIndex("terminalcode")));
                    item.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
                    item.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                    item.setSequence(cursor.getString(cursor.getColumnIndex("sequence")));
                    item.setMineProtocolFlag(cursor.getString(cursor.getColumnIndex("selftreaty")));
                    item.setMinorchannel(FunUtil.isNullSetSpace(cursor.getString(cursor.getColumnIndex("minorchannel"))));
                    String status = item.getStatus();
                    if (!"2".equals(status))
                    {//有效终端
                    	terminals.add(item);
                    }
                */

                    item = new MstTermListMStc();
                    item.setRoutekey(lineKeys.get(0));
                    item.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
                    item.setTerminalcode(cursor.getString(cursor.getColumnIndex("terminalcode")));
                    item.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
                    item.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                    item.setSequence(cursor.getString(cursor.getColumnIndex("sequence")));
                    item.setMineFlag(cursor.getString(cursor.getColumnIndex("isself")));
                    item.setVieFlag(cursor.getString(cursor.getColumnIndex("iscmp")));
                    item.setMineProtocolFlag(cursor.getString(cursor.getColumnIndex("selftreaty")));
                    item.setVieProtocolFlag(cursor.getString(cursor.getColumnIndex("cmptreaty")));


                    if ("9019cf03-4572-4559-9971-48a27a611c3d".equals(cursor.getString(cursor.getColumnIndex("acresult")))) {
                        item.setTerminalStatus("是");// 是否执行到位
                    } else if ("8d36d1e5-c776-452e-8893-589ad786d71d".equals(cursor.getString(cursor.getColumnIndex("acresult")))) {
                        item.setTerminalStatus("否");
                    } else {
                        item.setTerminalStatus("否");

                    }

                    visitDate = cursor.getString(cursor.getColumnIndex("visitdate"));
                    if (visitDate != null && currDay.equals(visitDate.substring(0, 8))) {
                        item.setSyncFlag(cursor.getString(cursor.getColumnIndex("padisconsistent")));
                        item.setUploadFlag(cursor.getString(cursor.getColumnIndex("uploadFlag")));
                    } else {
                        item.setSyncFlag(null);
                        item.setUploadFlag(null);
                    }
                    item.setMinorchannel(FunUtil.isNullSetSpace(cursor.getString(cursor.getColumnIndex("minorchannel"))));
                    item.setTerminalType(cursor.getString(cursor.getColumnIndex("terminalType")));


                    String status = item.getStatus();
                    if (!"2".equals(status)) {//有效终端
                        terminals.add(item);
                    }
                }
            }
        }
        /*
        else if(isjingpintuijin){// 竞品瓦解
        	
        	buffer.append("select m.terminalkey, m.terminalcode, m.terminalname,m.status,m.sequence, cr.cmpproductkey, vm.iscmpcollapse, vm.isself, ");
        	buffer.append("vm.iscmp, m.selftreaty, vm.cmptreaty, vm.padisconsistent, vm.uploadFlag, ");
        	buffer.append("m.minorchannel, dm.dicname terminalType, vm.visitdate ");
        	buffer.append("from mst_terminalinfo_m m ");
        	buffer.append("left join cmm_datadic_m dm on m.minorchannel = dm.diccode      and coalesce(dm.deleteflag,'0') != '1' ");
        	//buffer.append("left join v_visit_m_newest vm on m.terminalkey = vm.terminalkey ");
        	// 拜访已上传的 
        	buffer.append("left join mst_visit_m vm on m.terminalkey = vm.terminalkey and vm.padisconsistent ='1' ");
        	//buffer.append("left join mst_checkexerecord_info cr on cr.visitkey = vm.VISITKEY and cr.cmpproductkey = ? ");
        	//buffer.append("left join mst_vistproduct_info cr on cr.visitkey = vm.VISITKEY and cr.cmpproductkey = ? ");
        	buffer.append("left join mst_vistproduct_info cr on cr.visitkey = vm.VISITKEY and cr.cmpproductkey = ? ");
        	buffer.append("where coalesce(m.status,'0') != '2' and m.routekey=?  and coalesce(m.deleteflag,'0') != '1' ");
        	//buffer.append("order by m.sequence+0 asc, m.orderbyno, m.terminalname ");
        	
        	buffer.append("group BY m.terminalkey ");// 分组 每个终端只取一条
            buffer.append("order by m.sequence+0 asc, m.orderbyno, m.terminalname, vm.visitdate ");// 同时以拜访时间排序
        	
        	if (database.isOpen())
        	{
        		Cursor cursor = database.rawQuery(buffer.toString(), new String[] {proId,lineKeys.get(0)});//
        		String s = buffer.toString();
        		MstTermListMStc item = new MstTermListMStc();
        		String visitDate = "";
        		String currDay = DateUtil.formatDate(new Date(), "yyyyMMdd");
        		while (cursor.moveToNext())
        		{
        			
        			item = new MstTermListMStc();
        			item.setRoutekey(lineKeys.get(0));
        			item.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
        			item.setTerminalcode(cursor.getString(cursor.getColumnIndex("terminalcode")));
        			item.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
        			item.setStatus(cursor.getString(cursor.getColumnIndex("status")));
        			item.setSequence(cursor.getString(cursor.getColumnIndex("sequence")));
        			item.setMineFlag(cursor.getString(cursor.getColumnIndex("isself")));// 是否我品
        			item.setVieFlag(cursor.getString(cursor.getColumnIndex("iscmp")));// 是否竞品
        			item.setMineProtocolFlag(cursor.getString(cursor.getColumnIndex("selftreaty")));// 我品合作状态
        			item.setVieProtocolFlag(cursor.getString(cursor.getColumnIndex("cmptreaty")));// 竞品合作状态
        			visitDate = cursor.getString(cursor.getColumnIndex("visitdate"));// 进店时间
        			if (visitDate != null && currDay.equals(visitDate.substring(0, 8)))
        			{
        				item.setSyncFlag(cursor.getString(cursor.getColumnIndex("padisconsistent")));// 数据上传标识
        				item.setUploadFlag(cursor.getString(cursor.getColumnIndex("uploadFlag")));// 结束拜访上传按钮状态
        			}
        			else
        			{
        				item.setSyncFlag(null);
        				item.setUploadFlag(null);
        			}
        			item.setMinorchannel(FunUtil.isNullSetSpace(cursor.getString(cursor.getColumnIndex("minorchannel"))));
        			item.setTerminalType(cursor.getString(cursor.getColumnIndex("terminalType")));
        			
        			if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("cmpproductkey")))){
        				item.setTerminalStatus("无该竞品");// 已瓦解
        			}else{
        				item.setTerminalStatus("未瓦解");// 无该竞品
        			}
        			String status = item.getStatus();
        			if (!"2".equals(status))
        			{//有效终端
        				terminals.add(item);
        			}
        		}
        	}
        }
        */
        else {
            terminals = queryTerminal(lineKeys);
        }

        return terminals;
    }

    /**
     * 获取各终端名称的拼音
     *
     * @param termLst 终端列表
     */
    public Map<String, String> getTermPinyin(List<MstTermListMStc> termLst) {
        Map<String, String> termPinyinMap = new HashMap<String, String>();
        for (MstTermListMStc item : termLst) {
            termPinyinMap.put(item.getTerminalkey(), "," + PinYin4jUtil.converterToFirstSpell(item.getTerminalname()).toLowerCase());
        }

        return termPinyinMap;
    }

    /**
     * 按条件查询终端列表
     *
     * @param termLst       线路下所有终端
     * @param searchStr     查询条件
     * @param termPinyinMap 各终端名称的拼音
     */
    public List<MstTermListMStc> searchTerm(List<MstTermListMStc> termLst, String searchStr, Map<String, String> termPinyinMap) {

        List<MstTermListMStc> tempLst = new ArrayList<MstTermListMStc>();
        if (!CheckUtil.IsEmpty(termLst)) {
            if (CheckUtil.isBlankOrNull(searchStr)) {
                tempLst = termLst;
            } else {
                searchStr = searchStr.toLowerCase();
                for (MstTermListMStc item : termLst) {
                    Pattern pattern = Pattern.compile("[a-z]*");
                    if (pattern.matcher(searchStr).matches()) {
                        String pinyin = termPinyinMap.get(item.getTerminalkey());
                        if (pinyin.indexOf("," + searchStr) > -1 || pinyin.contains(searchStr)) {
                            tempLst.add(item);
                        }
                    } else {
                        if (item.getTerminalname().contains(searchStr)) {
                            tempLst.add(item);
                        }
                    }
                }
            }
        }
        return tempLst;
    }

    /**
     * 根据key得到终端名称
     *
     * @param terminalKeys
     * @return
     */
    public String getTerminalNames(List<String> terminalKeys) {
        String terminalNames = "";
        if (!CheckUtil.IsEmpty(terminalKeys)) {
            String keys = FunUtil.brackReplace(terminalKeys);
            StringBuffer sb = new StringBuffer();
            sb.append("select terminalname from mst_terminalinfo_m where terminalkey in(");
            sb.append(keys).append(") order by terminalkey");
            SQLiteDatabase readableDatabase = DatabaseHelper.getHelper(context).getReadableDatabase();
            Cursor cursor = readableDatabase.rawQuery(sb.toString(), null);
            List<String> names = new ArrayList<String>();
            while (cursor.moveToNext()) {
                names.add(cursor.getString(0));
            }
            if (!CheckUtil.IsEmpty(names)) {
                terminalNames = FunUtil.brackReplace(names).replace("'", "");
            }
        }
        return terminalNames;
    }

    /***
     * 更新终端顺序
     * @param list
     */
    public void updateTermSequence(List<TermSequence> list) {
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            SQLiteDatabase database = helper.getWritableDatabase();
            connection = new AndroidDatabaseConnection(database, true);
            Log.e(TAG, "更改巡店拜访顺序");
            connection.setAutoCommit(false);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            dao.updateTermSequence(helper, list);
            connection.commit(null);
            sendTermSequenceRequest(list);
        } catch (SQLException e) {
            Log.e(TAG, "更改巡店拜访顺序失败", e);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    /***
     * 发送修改终端顺序请求
     */
    public void sendTermSequenceRequest(final List<TermSequence> list) {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.configTimeout(1 * 60 * 1000);
        httpUtil.configResponseTextCharset("ISO-8859-1");
        String json = JsonUtil.toJson(list);
        Log.e(TAG, "更改巡店拜访顺序上传send:" + list.size() + json);
        httpUtil.send("opt_save_visit_sequence", json, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                Log.i(TAG, "终端顺序返回结果" + resObj.getResBody().getContent());
                try {
                    //                    DatabaseHelper helper = DatabaseHelper.getHelper(context);
                    //                    SQLiteDatabase db = helper.getWritableDatabase();
                    Log.e(TAG, "更改巡店拜访顺序");
                    if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                        //                        String ids="'";
                        //                        for(int i=0;i<list.size();i++){
                        //                            ids+=list.get(i).getTerminalkey();
                        //                            if(i!=list.size()-1){
                        //                                ids+=",";
                        //                            }
                        //                        }
                        //                        ids+="'";
                        //                        StringBuffer buffer = new StringBuffer();
                        //                        buffer.append("update mst_terminalinfo_m set padisconsistent='1' ");
                        //                        buffer.append("where terminalkey in ("+ids+")");
                        //                        db.execSQL(buffer.toString());
                    } else {
                        //                        //web端操作失败的终端
                        //                        String content = resObj.getResBody().getContent();
                        //                        List<String> errorIds = JsonUtil.parseList(content, String.class);
                        //                        String idIn = FunUtil.brackReplace(errorIds);
                        //                        StringBuffer buffer = new StringBuffer();
                        //                        buffer.append("update mst_terminalinfo_m set padisconsistent='0' ");
                        //                        buffer.append("where terminalkey in ("+idIn+")");
                        //                        db.execSQL(buffer.toString());
                        //                        //获取web端操作成功的终端
                        //                        List<String> succIds=new ArrayList<String>();
                        //                        for(TermSequence tSequence:list){
                        //                            if(!errorIds.contains(tSequence.getTerminalkey())){
                        //                                succIds.add(tSequence.getTerminalkey());
                        //                            }
                        //                        }
                        //                        if(succIds.size()>0){
                        //                            buffer = new StringBuffer();
                        //                            buffer.append("update mst_terminalinfo_m set padisconsistent='1' ");
                        //                            buffer.append("where terminalkey in ("+idIn+")");
                        //                            db.execSQL(buffer.toString());
                        //                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "更改巡店拜访顺序失败", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Log.e(TAG, msg, error);
            }
        });
    }
}
