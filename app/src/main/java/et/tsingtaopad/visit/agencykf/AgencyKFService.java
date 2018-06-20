package et.tsingtaopad.visit.agencykf;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencyKFMDao;
import et.tsingtaopad.db.tables.MstAgencyKFM;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：AgencyKFService.java</br>
 * 作者：Administrator   </br>
 * 创建时间：2015-11-25</br>      
 * 功能描述: 经销商开发逻辑</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class AgencyKFService extends ShopVisitService {
    
    private final String TAG = "AgencyKFService";

    public AgencyKFService(Context context, Handler handler) {
        super(context, handler);
    }

    /***
     * 查询经销商开发表所有记录
     */
    public ArrayList<MstAgencyKFM> queryMstAgencyKFMAll(){
    	ArrayList<MstAgencyKFM> valueLst =new ArrayList<MstAgencyKFM>();
        try {
        	
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyKFMDao dao = (MstAgencyKFMDao) helper.getMstAgencyKFMDao();
            
            valueLst =dao.queryMstAgencyKFMLst(helper,"0");
        } catch (SQLException e) {
        	e.printStackTrace();
            Log.e(TAG, "查询图片类型表中所有记录", e);
        }
        return valueLst;
    }

    /**
     * 更新经销商开发表记录上传状态
     * 
     * @param agencykfkey   经销商开发主键
     * @return
     */
    public void updataagencyKfupstatusbyAgencykfkey(String agencykfkey) {
        
        try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyKFMDao dao = (MstAgencyKFMDao) helper.getMstAgencyKFMDao();
            dao.updataUploadbyAgencykfkey(helper, agencykfkey);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
    }
    /**
     * 更新经销商开发表有效状态
     * 
     * @param agencykfkey   经销商开发主键
     * @return
     */
    public void updataagencyKfstatus2byAgencykfkey(String agencykfkey) {
    	
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstAgencyKFMDao dao = (MstAgencyKFMDao) helper.getMstAgencyKFMDao();
    		dao.updatastatusbyAgencykfkey(helper, agencykfkey);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    }
    
    /**
     * 经销商开发表插入一条记录
     * 
     * @param mstAgencyKFM
     */
    public void insertAgencyKf(MstAgencyKFM mstAgencyKFM){
    	try {
        	DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyKFMDao dao = (MstAgencyKFMDao) helper.getMstAgencyKFMDao();
            
            dao.createOrUpdate(mstAgencyKFM);
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
    }
    /**
     * 经销商开发表  模糊查询
     * 
     * @param agencyname
     */
    public ArrayList<MstAgencyKFM> getAgencyKf(String agencyname){
    	ArrayList<MstAgencyKFM> valueLst =new ArrayList<MstAgencyKFM>();
    	try {
    		DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		MstAgencyKFMDao dao = (MstAgencyKFMDao) helper.getMstAgencyKFMDao();
    		
    		valueLst = dao.queryAgencykfbyAgencyName(helper,agencyname);
    	} catch (SQLException e) {
    		Log.e(TAG, "获取终端表DAO对象失败", e);
    	}
    	
    	 return valueLst;
    }
    
    
}
