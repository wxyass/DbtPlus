package et.tsingtaopad.db.dao;

import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.db.tables.MstPictypeM;
import et.tsingtaopad.visit.shopvisit.camera.domain.PictypeDataStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstPictypeMDao.java</br>
 * 作者：Administrator   </br>
 * 创建时间：2015-11-19</br>      
 * 功能描述: 图片类型表Dao层</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstPictypeMDao extends Dao<MstPictypeM, String> {
    
    /**
     * 查询图片类型表中所有记录,用于初始化要拍几张图片
     * 
     * @param helper
     * @return
     */
    public List<PictypeDataStc> queryAllPictype(SQLiteOpenHelper helper);
    
}
