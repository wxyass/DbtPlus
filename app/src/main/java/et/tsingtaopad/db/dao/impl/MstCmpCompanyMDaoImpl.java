package et.tsingtaopad.db.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.db.dao.MstCmpCompanyMDao;
import et.tsingtaopad.db.tables.MstCmpcompanyM;
import et.tsingtaopad.initconstvalues.domain.KvStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstCmpCompanyMDaoImpl.java</br>
 * 作者：姜世杰   </br>
 * 创建时间：2013-12-17</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class MstCmpCompanyMDaoImpl extends BaseDaoImpl<MstCmpcompanyM, String> 
                                                            implements MstCmpCompanyMDao{
       
    public  MstCmpCompanyMDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, MstCmpcompanyM.class);
    }

    /**
     * 查询竞争对手可销售产品信息
     */
    public List<KvStc>  agencySellProQuery(SQLiteOpenHelper helper){        
        SQLiteDatabase db = helper.getReadableDatabase();
        
        String sql="select a.CMPCOMKEY as cmpcomkey,a.CMPCOMNAME as cmpcomname, " +
        		                " p.CMPPRODUCTKEY as cmpproductkey,p.CMPPRONAME as cmpproname " +
                       "  from  MST_CMPCOMPANY_M a,MST_CMPBRANDS_M ap,MST_CMPRODUCTINFO_M p " +
                        " where a.CMPCOMKEY=ap.CMPCOMKEY AND ap.CMPBRANDKEY=p.CMPBRANDKEY " +
                       " and coalesce(a.deleteflag,'0') != '1' and coalesce(ap.deleteflag,'0') != '1' and coalesce(p.deleteflag,'0') != '1' " +
                        " order by a.ORDERBYNO,p.ORDERBYNO";
        Cursor cursor = db.rawQuery(sql, null);
        List<KvStc>   agencyKvStcList=new ArrayList<KvStc>();
        Map<String,KvStc> filter=new HashMap<String,KvStc>();        
        while(cursor.moveToNext()) {
          
            String agencykey = cursor.getString(cursor.getColumnIndex("cmpcomkey"));
            String agencyname = cursor.getString(cursor.getColumnIndex("cmpcomname"));
            
            KvStc exsitKvStc = filter.get(agencykey);
            if(exsitKvStc==null){
                exsitKvStc = new KvStc();
                exsitKvStc.setKey(agencykey);            
                exsitKvStc.setValue(agencyname);
                filter.put(agencykey, exsitKvStc);
                agencyKvStcList.add(exsitKvStc);//加到缓存中
            }        
            
            //添加产品
            KvStc agencyProKvStc = new KvStc();
            String productkey=cursor.getString(cursor.getColumnIndex("cmpproductkey"));
            String proname=cursor.getString(cursor.getColumnIndex("cmpproname"));
            agencyProKvStc.setKey(productkey);            
            agencyProKvStc.setValue(proname);
            
            exsitKvStc.getChildLst().add(agencyProKvStc);
           // tagAgencyStcLst.add(tagAgency);
        }
        return agencyKvStcList;
    
    }
}
