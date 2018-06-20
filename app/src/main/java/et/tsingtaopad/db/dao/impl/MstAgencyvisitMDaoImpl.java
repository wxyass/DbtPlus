package et.tsingtaopad.db.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.db.dao.MstAgencyvisitMDao;
import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;
import et.tsingtaopad.visit.agencyvisit.domain.TransferStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：MstAgencyvisitMDaoImpl.java</br> 作者：吴欣伟 </br>
 * 创建时间：2013-11-28</br> 功能描述: 分经销商拜访Dao层</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
public class MstAgencyvisitMDaoImpl extends
        BaseDaoImpl<MstAgencyvisitM, String> implements MstAgencyvisitMDao {

    public MstAgencyvisitMDaoImpl(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, MstAgencyvisitM.class);
    }

    /**
     * 获取经销商拜访中所拜访经销商列表
     * 
     * @param helper
     * @param gridId    定格ID
     * @return
     */
    @Override
    public List<AgencySelectStc> agencySelectQuery(SQLiteOpenHelper helper,
            String gridId) {

        List<AgencySelectStc> asStcLst = new ArrayList<AgencySelectStc>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select a.agencykey,a.agencyname,prov.areaname as province,city.areaname as city," +
        		"county.areaname as county,a.address,a.mobile from MST_VISITAUTHORIZE_INFO" +
        		" m inner join MST_AGENCYINFO_M a on m.agencykey = a.agencykey " +
        		"left join CMM_AREA_M prov on a.province = prov.areacode left " +
        		"join CMM_AREA_M city on a.city = city.areacode left join " +
        		"CMM_AREA_M county on a.county = county.areacode ";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            AgencySelectStc asStc = new AgencySelectStc();
            asStc.setAgencyKey(cursor.getString(cursor.getColumnIndex("agencykey")));
            asStc.setAgencyName(cursor.getString(cursor.getColumnIndex("agencyname")));
            String province = cursor.getString(cursor.getColumnIndex("province"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String county =  cursor.getString(cursor.getColumnIndex("county"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            
            String addr = FunUtil.isNullSetSpace(province) + FunUtil.
                       isNullSetSpace(city) + FunUtil.isNullSetSpace(county)
                                             + FunUtil.isNullSetSpace(address);
            asStc.setAddr(addr);
            asStc.setPhone(cursor.getString(cursor.getColumnIndex("mobile")));
            asStcLst.add(asStc);
        }

        return asStcLst;
    }
    

    /**
     * 依据拜访请键获取经销商调货记录
     * 
     * @param helper
     * @param visitId   经销商拜访主键
     * @return
     */
    public List<TransferStc> queryTransByVisitId(SQLiteOpenHelper helper, String visitId) {
        List<TransferStc> lst = new ArrayList<TransferStc>();
        if (!CheckUtil.isBlankOrNull(visitId)) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("select ar.trankey, ar.agevisitkey, ar.agencykey, ar.tagencykey, am.agencyname, ");
            buffer.append("ar.productkey, pm.proname,ar.trandate,ar.tranin, ar.tranout ");
            buffer.append("from mst_agencytransfer_info ar, mst_agencyinfo_m am, mst_product_m pm ");
            buffer.append("where ar.tagencykey = am.agencykey and ar.productkey = pm.productkey ");
            buffer.append("and coalesce(ar.deleteflag,'0') != '1' and ar.agevisitkey = ? ");
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery(buffer.toString(), new String[]{visitId});
            TransferStc item;
            while (cursor.moveToNext()) {
                item = new TransferStc();
                item.setTrankey(cursor.getString(cursor.getColumnIndex("trankey")));
                item.setAgevisitkey(cursor.getString(cursor.getColumnIndex("agevisitkey")));
                item.setAgencykey(cursor.getString(cursor.getColumnIndex("agencykey")));
                item.setTagencykey(cursor.getString(cursor.getColumnIndex("tagencykey")));
                item.setTagAgencyName(cursor.getString(cursor.getColumnIndex("agencyname")));
                item.setProductkey(cursor.getString(cursor.getColumnIndex("productkey")));
                item.setProName(cursor.getString(cursor.getColumnIndex("proname")));
                item.setTrandate(cursor.getString(cursor.getColumnIndex("trandate")));
                item.setTranin(cursor.getDouble(cursor.getColumnIndex("tranin")));
                item.setTranout(cursor.getDouble(cursor.getColumnIndex("tranout")));
                lst.add(item);
            }
        }
        return lst;
    }   
    
    @Override
    public boolean isExistAgencyVisit(SQLiteOpenHelper helper,String agencyKey, String visitDate)
    {
        boolean isExist=false;
        StringBuffer buffer = new StringBuffer();
        SQLiteDatabase db = helper.getReadableDatabase();
        buffer.append("select * from mst_agencyvisit_m am where am.agencyKey = ? ");
        buffer.append("and substr(am.agevisitdate,0,9)=? ");
        Cursor cursor = db.rawQuery(buffer.toString(), new String[] {agencyKey,visitDate});
        
        if (cursor.getCount() > 0) {
            isExist=true;
        }
        return isExist;
    }
}
