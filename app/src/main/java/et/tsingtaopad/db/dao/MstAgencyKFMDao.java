package et.tsingtaopad.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.tables.MstAgencyKFM;
import et.tsingtaopad.db.tables.MstCameraInfoM;
import et.tsingtaopad.visit.shopvisit.camera.domain.CameraDataStc;
import et.tsingtaopad.visit.shopvisit.camera.domain.MstCameraListMStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstAgencyKFMDao.java</br>
 * 作者：Administrator   </br>
 * 创建时间：2015-11-25</br>      
 * 功能描述: 经销商开发Dao层</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstAgencyKFMDao extends Dao<MstAgencyKFM, String> {

	/**
	 * 查询经销商开发表所有记录
	 * 
	 * @param helper
	 * @param status 0有效经销商  1无效经销商
	 * @return
	 */
	public ArrayList<MstAgencyKFM> queryMstAgencyKFMLst(SQLiteOpenHelper helper, String status);

	/**
	 * 更新一条经销商开发记录
	 * (根据经销商开发主键,更新上传状态为1(已上传))
	 * @param helper
	 * @param agencykfkey
	 */
	public void updataUploadbyAgencykfkey(
            SQLiteOpenHelper helper, String agencykfkey);
	/**
	 * 更新一条经销商开发有效记录
	 * (根据经销商开发主键,更新有效状态为1(无效))
	 * @param helper
	 * @param agencykfkey
	 */
	public void updatastatusbyAgencykfkey(
            SQLiteOpenHelper helper, String agencykfkey);
	
	/**
	 * 根据是否上传状态 查询所有未上传表记录
	 * @param helper
	 * @param upload 0未上传    1,null已上传
	 */
	public ArrayList<MstAgencyKFM> queryMstAgencyKFMLstbynotupload(
            SQLiteOpenHelper helper, String upload);
	
	
	/**
	 * 删除一条经销商开发记录
	 * 
	 * @param helper
	 * @param agencykfkey
	 */
	public void deleteAgencykfRecord(
            SQLiteOpenHelper helper, String agencykfkey);
	
	
	/**
	 * 根据经销商名称 模糊查询
	 * 
	 * @param helper
	 * @param AgencyName
	 * @return
	 */
	public ArrayList<MstAgencyKFM> queryAgencykfbyAgencyName(
            SQLiteOpenHelper helper, String AgencyName);

}
