package et.tsingtaopad.db.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.TerminalName;
import et.tsingtaopad.visit.shopvisit.sayhi.domain.MstTerminalInfoMStc;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.visit.shopvisit.term.domain.TermSequence;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstTerminalinfoMDaoImpl.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-28</br>
 * 功能描述: 终端表的DAO层</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class MstTerminalinfoMDaoImpl extends BaseDaoImpl<MstTerminalinfoM, String> implements MstTerminalinfoMDao {

    public MstTerminalinfoMDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, MstTerminalinfoM.class);
    }

    /**
     * 获取某线路下的终端列表
     * <p>
     * 用于：巡店拜访  -- 终端选择
     *
     * @param helper
     * @param lineId 线路主键
     * @return
     */
    public List<MstTermListMStc> queryTermLst(SQLiteOpenHelper helper, String lineId) {
        List<MstTermListMStc> lst = new ArrayList<MstTermListMStc>();
//        lst.addAll(getTermList_sequence(helper, lineId, true));
//        lst.addAll(getTermList_sequence(helper, lineId, false));
        lst.addAll(getTermList_sequence(helper, lineId, false));// true:已排序   false:未排序
        return lst;
    }

    @Override
    public List<MstTermListMStc> getTermListByName(SQLiteOpenHelper helper, String termName) {
        List<MstTermListMStc> lst = new ArrayList<MstTermListMStc>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("select tm.terminalkey,tm.terminalcode,tm.terminalname,tm.mobile,tm.status,tm.contact,tm.address,tm.routekey,rm.routename ");
        buffer.append("from mst_terminalinfo_m tm ");
        buffer.append("inner join mst_route_m rm ");
        buffer.append("on tm.routekey=rm.routekey and coalesce(rm.deleteflag, '0') != '1' and coalesce(rm.routestatus, '0') != '1' ");
        buffer.append("where coalesce(tm.status,'0') != '2' ");
        buffer.append("and tm.terminalname like '%" + termName + "%' ");
        buffer.append("and coalesce(tm.deleteflag,'0') != '1' ");

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), null);
        MstTermListMStc item;
        while (cursor.moveToNext()) {
            item = new MstTermListMStc();
            item.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
            item.setTerminalcode(cursor.getString(cursor.getColumnIndex("terminalcode")));
            item.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
            item.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
            item.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            item.setContact(cursor.getString(cursor.getColumnIndex("contact")));
            item.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            item.setRoutekey(FunUtil.isNullSetSpace(cursor.getString(cursor.getColumnIndex("routekey"))));
            item.setRoutename(cursor.getString(cursor.getColumnIndex("routename")));
            String status = item.getStatus();
            if (!"2".equals(status)) {//有效终端
                lst.add(item);
            }
        }
        return lst;
    }

    /***
     * 获取线路下的终端集合
     * @param helper
     * @param lineId      线路主键
     * @param isSequence  是否查询已排序的终端
     * @return
     */
    private List<MstTermListMStc> getTermList_sequence(SQLiteOpenHelper helper, String lineId, boolean isSequence) {
        List<MstTermListMStc> lst = new ArrayList<MstTermListMStc>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("select m.terminalkey, m.terminalcode, m.terminalname,m.status,m.sequence, ");
        //buffer.append("vm.isself, vm.iscmp, vm.selftreaty, vm.cmptreaty, ");
        buffer.append("vm.isself, vm.iscmp, m.selftreaty, vm.cmptreaty, ");
        buffer.append("m.hvolume, m.mvolume, m.pvolume, m.lvolume, ");// 高中普低
        buffer.append("nullif(nullif(m.hvolume,0)+nullif(m.mvolume,0)+nullif(m.pvolume,0)+nullif(m.lvolume,0),0) as tvolume,  ");// 高中普低的和
        buffer.append("vm.padisconsistent, vm.uploadFlag, m.minorchannel, ");
        buffer.append("dm.dicname terminalType, vm.visitdate ");
        buffer.append("from mst_terminalinfo_m m ");
        buffer.append("left join cmm_datadic_m dm on m.minorchannel = dm.diccode ");
        buffer.append("     and coalesce(dm.deleteflag,'0') != '1' ");
        buffer.append("left join v_visit_m_newest vm on m.terminalkey = vm.terminalkey ");
        buffer.append("where coalesce(m.status,'0') != '2' and m.routekey=? ");
        buffer.append(" and coalesce(m.deleteflag,'0') != '1' ");
        // 不需要区分是否已排序
        /*if (isSequence) {
            buffer.append(" and m.sequence!='' and m.sequence not null ");
        } else {
            buffer.append(" and (m.sequence='' or m.sequence is null) ");
        }*/
        // buffer.append("order by m.sequence+0 asc, m.orderbyno, m.terminalname ");
        buffer.append("order by case when (m.sequence + 0) is null then 1 else 0 end ,m.sequence + 0  , m.orderbyno, m.terminalname  ");

        String visitDate = "";
        String currDay = DateUtil.formatDate(new Date(), "yyyyMMdd");

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), new String[]{lineId});
        MstTermListMStc item;
        while (cursor.moveToNext()) {
            item = new MstTermListMStc();
            item.setRoutekey(lineId);
            item.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
            item.setTerminalcode(cursor.getString(cursor.getColumnIndex("terminalcode")));
            item.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
            item.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            item.setSequence(cursor.getString(cursor.getColumnIndex("sequence")));
            item.setMineFlag(cursor.getString(cursor.getColumnIndex("isself")));
            item.setVieFlag(cursor.getString(cursor.getColumnIndex("iscmp")));
            item.setMineProtocolFlag(cursor.getString(cursor.getColumnIndex("selftreaty")));
            item.setVieProtocolFlag(cursor.getString(cursor.getColumnIndex("cmptreaty")));
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

            item.setHvolume(cursor.getString(cursor.getColumnIndex("hvolume")));
            item.setMvolume(cursor.getString(cursor.getColumnIndex("mvolume")));
            item.setPvolume(cursor.getString(cursor.getColumnIndex("pvolume")));
            item.setLvolume(cursor.getString(cursor.getColumnIndex("lvolume")));
            item.setTvolume(cursor.getString(cursor.getColumnIndex("tvolume")));

            String status = item.getStatus();
            if (!"2".equals(status)) {//有效终端
                lst.add(item);
            }
        }
        return lst;
    }

    /**
     * 获取某线路下的各终端当天的所有拜访进店及离店时间
     * <p>
     * 用于：巡店拜访  -- 终端选择
     *
     * @param helper
     * @param lineId 线路主键
     * @return
     */
    public List<MstTermListMStc> queryTermVistTime(SQLiteOpenHelper helper, String lineId) {

        List<MstTermListMStc> lst = new ArrayList<MstTermListMStc>();

        // 获取该线路下所有终端当天单击过上传按钮的数据
        StringBuffer buffer = new StringBuffer();
        buffer.append("select m.terminalkey, vm.visitdate, vm.enddate  ");
        buffer.append("from mst_terminalinfo_m m  ");
        buffer.append("inner join mst_visit_m vm on m.terminalkey = vm.terminalkey ");
        buffer.append("     and (vm.uploadflag='1' or vm.padisconsistent ='1') ");
        buffer.append("     and substr(vm.visitdate,1,8) = ? ");
        buffer.append("where m.routekey = ? and coalesce(m.deleteflag,'0') != '1'");
        buffer.append("order by m.terminalkey, vm.visitdate ");

        String[] args = new String[2];
        args[0] = ConstValues.loginSession.getLoginDate().substring(0, 10).replace("-", "");
        args[1] = lineId;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), args);
        MstTermListMStc item;
        while (cursor.moveToNext()) {
            item = new MstTermListMStc();
            item.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
            item.setVisitDate(cursor.getString(cursor.getColumnIndex("visitdate")));
            item.setEndDate(cursor.getString(cursor.getColumnIndex("enddate")));
            lst.add(item);
        }
        return lst;
    }

    @Override
    public List<MstTermListMStc> queryTermVistTimeByLoginDate(DatabaseHelper helper, String lineId, String userGongHao) {


        List<MstTermListMStc> lst = new ArrayList<MstTermListMStc>();

        // 获取该线路下所有终端当天单击过上传按钮的数据
        StringBuffer buffer = new StringBuffer();
        buffer.append("select m.terminalkey, vm.visitdate, vm.enddate  ");
        buffer.append("from mst_terminalinfo_m m  ");
        buffer.append("inner join mst_visit_m vm on m.terminalkey = vm.terminalkey ");
        buffer.append("     and (vm.uploadflag='1' or vm.padisconsistent ='1') ");
        buffer.append("     and substr(vm.visitdate,1,8) = ? ");
        buffer.append("where m.routekey = ? and coalesce(m.deleteflag,'0') != '1'");
        buffer.append("order by m.terminalkey, vm.visitdate ");

        String[] args = new String[2];
        args[0] = userGongHao.substring(0, 10).replace("-", "");
        args[1] = lineId;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), args);
        MstTermListMStc item;
        while (cursor.moveToNext()) {
            item = new MstTermListMStc();
            item.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
            item.setVisitDate(cursor.getString(cursor.getColumnIndex("visitdate")));
            item.setEndDate(cursor.getString(cursor.getColumnIndex("enddate")));
            lst.add(item);
        }
        return lst;
    }

    /**
     * 获取某线路下的当天已上传终端
     * <p>
     * 用于：巡店拜访  -- 终端选择
     *
     * @param helper
     * @param lineId 线路主键
     * @return
     */
    public List<MstTermListMStc> queryTermUpflag(SQLiteOpenHelper helper, String lineId) {

        List<MstTermListMStc> lst = new ArrayList<MstTermListMStc>();

        // 获取该线路下所有终端当天单击过上传按钮的数据
        StringBuffer buffer = new StringBuffer();
        //select m.terminalkey, m.terminalcode, m.terminalname,m.status,m.sequence, 
        //vm.isself, vm.iscmp, m.selftreaty, vm.cmptreaty, vm.padisconsistent, vm.uploadFlag, 
        //m.minorchannel, dm.dicname terminalType, vm.visitdate
        buffer.append("select m.terminalkey, m.terminalcode, m.terminalname,m.status,m.sequence, vm.enddate,  ");
        buffer.append("vm.isself, vm.iscmp, m.selftreaty, vm.cmptreaty, vm.padisconsistent, vm.uploadFlag,");
        buffer.append("m.minorchannel, dm.dicname terminalType, vm.visitdate ");
        //buffer.append("select m.terminalkey, vm.visitdate, vm.enddate  ");  
        buffer.append("from mst_terminalinfo_m m  ");
        buffer.append("inner join mst_visit_m vm on m.terminalkey = vm.terminalkey ");
        buffer.append("     and (vm.uploadflag='1' or vm.padisconsistent ='1') ");
        buffer.append("     and substr(vm.visitdate,1,8) = ? ");
        buffer.append("left join cmm_datadic_m dm on m.minorchannel = dm.diccode ");
        buffer.append("     and coalesce(dm.deleteflag,'0') != '1' ");
        buffer.append("where m.routekey = ? and coalesce(m.deleteflag,'0') != '1' ");
        buffer.append("group by m.terminalkey ");

        buffer.append("order by m.terminalkey, vm.visitdate ");

        String[] args = new String[2];
        args[0] = ConstValues.loginSession.getLoginDate().substring(0, 10).replace("-", "");
        args[1] = lineId;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), args);
        MstTermListMStc item;
        String visitDate = "";
        String currDay = DateUtil.formatDate(new Date(), "yyyyMMdd");
        while (cursor.moveToNext()) {
            // ----------

            item = new MstTermListMStc();
            item.setRoutekey(lineId);
            item.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
            item.setTerminalcode(cursor.getString(cursor.getColumnIndex("terminalcode")));
            item.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
            item.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            item.setSequence(cursor.getString(cursor.getColumnIndex("sequence")));
            item.setMineFlag(cursor.getString(cursor.getColumnIndex("isself")));
            item.setVieFlag(cursor.getString(cursor.getColumnIndex("iscmp")));
            item.setMineProtocolFlag(cursor.getString(cursor.getColumnIndex("selftreaty")));
            item.setVieProtocolFlag(cursor.getString(cursor.getColumnIndex("cmptreaty")));
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
                lst.add(item);
            }

        }
        return lst;
    }

    /**
     * 依据终端主键获取终端 信息（包含数据字典对应的名称）
     *
     * @param helper
     * @param termId 终端主键
     * @return
     */
    public MstTerminalInfoMStc findById(SQLiteOpenHelper helper, String termId) {

        MstTerminalInfoMStc stc = new MstTerminalInfoMStc();

        StringBuffer buffer = new StringBuffer();
        buffer.append("select tm.*, prov.areaname provname, city.areaname cityname, country.areaname countryname, ");
        buffer.append("sell.dicname sellchannelname, mains.dicname mainchannelname, minors.dicname minorchannelname ");
        buffer.append("from mst_terminalinfo_m tm ");
        buffer.append("left join cmm_area_m prov on tm.province = prov.areacode ");
        buffer.append("left join cmm_area_m city on tm.city = city.areacode and city.parentcode = tm.province ");
        buffer.append("left join cmm_area_m country on tm.county = country.areacode and country.parentcode = tm.city ");
        buffer.append("left join cmm_datadic_m sell on tm.sellchannel = sell.diccode ");
        buffer.append("left join cmm_datadic_m mains on tm.mainchannel = mains.diccode and mains.parentcode = tm.sellchannel ");
        buffer.append("left join cmm_datadic_m minors on tm.minorchannel = minors.diccode and minors.parentcode = tm.mainchannel ");
        buffer.append("where tm.terminalkey = ? ");

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), new String[]{termId});
        while (cursor.moveToNext()) {
            stc.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
            stc.setRoutekey(cursor.getString(cursor.getColumnIndex("routekey")));
            stc.setTerminalcode(cursor.getString(cursor.getColumnIndex("terminalcode")));
            stc.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
            stc.setProvince(cursor.getString(cursor.getColumnIndex("province")));
            stc.setProvName(cursor.getString(cursor.getColumnIndex("provname")));
            stc.setCity(cursor.getString(cursor.getColumnIndex("city")));
            stc.setCityName(cursor.getString(cursor.getColumnIndex("cityname")));
            stc.setCounty(cursor.getString(cursor.getColumnIndex("county")));
            stc.setCountryName(cursor.getString(cursor.getColumnIndex("countryname")));
            stc.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            stc.setContact(cursor.getString(cursor.getColumnIndex("contact")));
            stc.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
            stc.setTlevel(cursor.getString(cursor.getColumnIndex("tlevel")));
            stc.setSequence(cursor.getString(cursor.getColumnIndex("sequence")));
            stc.setCycle(cursor.getString(cursor.getColumnIndex("cycle")));
            stc.setMvolume(cursor.getString(cursor.getColumnIndex("mvolume")));
            stc.setPvolume(cursor.getString(cursor.getColumnIndex("pvolume")));
            stc.setLvolume(cursor.getString(cursor.getColumnIndex("lvolume")));
            stc.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            stc.setSellchannel(cursor.getString(cursor.getColumnIndex("sellchannel")));
            stc.setSellChannelName(cursor.getString(cursor.getColumnIndex("sellchannelname")));
            stc.setMainchannel(cursor.getString(cursor.getColumnIndex("mainchannel")));
            stc.setMainChannelName(cursor.getString(cursor.getColumnIndex("mainchannelname")));
            stc.setMinorchannel(cursor.getString(cursor.getColumnIndex("minorchannel")));
            stc.setMinorChannelName(cursor.getString(cursor.getColumnIndex("minorchannelname")));
            stc.setAreatype(cursor.getString(cursor.getColumnIndex("areatype")));
            stc.setSisconsistent(cursor.getString(cursor.getColumnIndex("sisconsistent")));
            stc.setScondate(DateUtil.parse(cursor.getString(cursor.getColumnIndex("datetime(scondate,'localtime')")), "yyyy-MM-dd HH:mm:ss"));
            stc.setPadisconsistent(cursor.getString(cursor.getColumnIndex("padisconsistent")));
            stc.setPadcondate(DateUtil.parse(cursor.getString(cursor.getColumnIndex("datetime(padcondate,'localtime')")), "yyyy-MM-dd HH:mm:ss"));
            stc.setComid(cursor.getString(cursor.getColumnIndex("comid")));
            stc.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
            stc.setOrderbyno(cursor.getString(cursor.getColumnIndex("orderbyno")));
            stc.setDeleteflag(cursor.getString(cursor.getColumnIndex("deleteflag")));
            stc.setVersion(new BigDecimal(cursor.getInt(cursor.getColumnIndex("version"))));
            stc.setCredate(DateUtil.parse(cursor.getString(cursor.getColumnIndex("datetime(credate,'localtime')")), "yyyy-MM-dd HH:mm:ss"));
            stc.setCreuser(cursor.getString(cursor.getColumnIndex("creuser")));
            stc.setUpdatetime(DateUtil.parse(cursor.getString(cursor.getColumnIndex("datetime(updatetime,'localtime')")), "yyyy-MM-dd HH:mm:ss"));
            stc.setUpdateuser(cursor.getString(cursor.getColumnIndex("updateuser")));
        }

        return stc;
    }

    /**
     * 依据终端主键获取终端 信息（包含数据字典对应的名称）
     * <p>
     * 上传图片时,由于之前的方法(findById)出错,所以重写,注释掉会出错的代码
     *
     * @param helper
     * @param termId 终端主键
     * @return
     */
    public MstTerminalInfoMStc findByTermId(SQLiteOpenHelper helper, String termId) {

        MstTerminalInfoMStc stc = new MstTerminalInfoMStc();

        StringBuffer buffer = new StringBuffer();
        buffer.append("select tm.*, prov.areaname provname, city.areaname cityname, country.areaname countryname, ");
        buffer.append("sell.dicname sellchannelname, mains.dicname mainchannelname, minors.dicname minorchannelname ");
        buffer.append("from mst_terminalinfo_m tm ");
        buffer.append("left join cmm_area_m prov on tm.province = prov.areacode ");
        buffer.append("left join cmm_area_m city on tm.city = city.areacode and city.parentcode = tm.province ");
        buffer.append("left join cmm_area_m country on tm.county = country.areacode and country.parentcode = tm.city ");
        buffer.append("left join cmm_datadic_m sell on tm.sellchannel = sell.diccode ");
        buffer.append("left join cmm_datadic_m mains on tm.mainchannel = mains.diccode and mains.parentcode = tm.sellchannel ");
        buffer.append("left join cmm_datadic_m minors on tm.minorchannel = minors.diccode and minors.parentcode = tm.mainchannel ");
        buffer.append("where tm.terminalkey = ? ");

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), new String[]{termId});
        while (cursor.moveToNext()) {
            stc.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
            stc.setRoutekey(cursor.getString(cursor.getColumnIndex("routekey")));
            stc.setTerminalcode(cursor.getString(cursor.getColumnIndex("terminalcode")));
            stc.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
            stc.setProvince(cursor.getString(cursor.getColumnIndex("province")));
            stc.setProvName(cursor.getString(cursor.getColumnIndex("provname")));
            stc.setCity(cursor.getString(cursor.getColumnIndex("city")));
            stc.setCityName(cursor.getString(cursor.getColumnIndex("cityname")));
            stc.setCounty(cursor.getString(cursor.getColumnIndex("county")));
            stc.setCountryName(cursor.getString(cursor.getColumnIndex("countryname")));
            stc.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            stc.setContact(cursor.getString(cursor.getColumnIndex("contact")));
            stc.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
            stc.setTlevel(cursor.getString(cursor.getColumnIndex("tlevel")));
            stc.setSequence(cursor.getString(cursor.getColumnIndex("sequence")));
            stc.setCycle(cursor.getString(cursor.getColumnIndex("cycle")));
            stc.setMvolume(cursor.getString(cursor.getColumnIndex("mvolume")));
            stc.setPvolume(cursor.getString(cursor.getColumnIndex("pvolume")));
            stc.setLvolume(cursor.getString(cursor.getColumnIndex("lvolume")));
            stc.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            stc.setSellchannel(cursor.getString(cursor.getColumnIndex("sellchannel")));
            stc.setSellChannelName(cursor.getString(cursor.getColumnIndex("sellchannelname")));
            stc.setMainchannel(cursor.getString(cursor.getColumnIndex("mainchannel")));
            stc.setMainChannelName(cursor.getString(cursor.getColumnIndex("mainchannelname")));
            stc.setMinorchannel(cursor.getString(cursor.getColumnIndex("minorchannel")));
            stc.setMinorChannelName(cursor.getString(cursor.getColumnIndex("minorchannelname")));
            stc.setAreatype(cursor.getString(cursor.getColumnIndex("areatype")));
            stc.setSisconsistent(cursor.getString(cursor.getColumnIndex("sisconsistent")));
            //stc.setScondate(DateUtil.parse(cursor.getString(cursor.getColumnIndex("datetime(scondate,'localtime')")), "yyyy-MM-dd HH:mm:ss"));
            stc.setPadisconsistent(cursor.getString(cursor.getColumnIndex("padisconsistent")));
            //stc.setPadcondate(DateUtil.parse(cursor.getString(cursor.getColumnIndex("datetime(padcondate,'localtime')")), "yyyy-MM-dd HH:mm:ss"));
            stc.setComid(cursor.getString(cursor.getColumnIndex("comid")));
            stc.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
            stc.setOrderbyno(cursor.getString(cursor.getColumnIndex("orderbyno")));
            stc.setDeleteflag(cursor.getString(cursor.getColumnIndex("deleteflag")));
            stc.setVersion(new BigDecimal(cursor.getInt(cursor.getColumnIndex("version"))));
            //stc.setCredate(DateUtil.parse(cursor.getString(cursor.getColumnIndex("datetime(credate,'localtime')")), "yyyy-MM-dd HH:mm:ss"));
            stc.setCreuser(cursor.getString(cursor.getColumnIndex("creuser")));
            //stc.setUpdatetime(DateUtil.parse(cursor.getString(cursor.getColumnIndex("datetime(updatetime,'localtime')")), "yyyy-MM-dd HH:mm:ss"));
            stc.setUpdateuser(cursor.getString(cursor.getColumnIndex("updateuser")));
        }

        return stc;
    }

    @Override
    public void updateTermSequence(SQLiteOpenHelper helper, List<TermSequence> list) {
        for (TermSequence term : list) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("update mst_terminalinfo_m set sequence=?");
            buffer.append("where terminalkey=?");
            SQLiteDatabase db = helper.getReadableDatabase();
            db.execSQL(buffer.toString(), new Object[]{term.getSequence(), term.getTerminalkey()});
        }
    }

    @Override
    public TerminalName findByIdName(DatabaseHelper databaseHelper, String termId) {
        TerminalName terminalName = new TerminalName();
        StringBuffer buffer = new StringBuffer();
        buffer.append("select a.terminalname,b.routename ");
        buffer.append("from mst_terminalinfo_m a ");
        buffer.append("join mst_route_m b on a.routekey = b.routekey ");
        buffer.append("where terminalkey=? ");

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), new String[]{termId});

        while (cursor.moveToNext()) {
            terminalName.setRouteName(cursor.getString(cursor.getColumnIndex("routename")));
            terminalName.setTerminalName(cursor.getString(cursor.getColumnIndex("terminalname")));

        }

        return terminalName;
    }


}
