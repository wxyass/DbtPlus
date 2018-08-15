package et.tsingtaopad.db.dao;

import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.TerminalName;
import et.tsingtaopad.visit.shopvisit.sayhi.domain.MstTerminalInfoMStc;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.visit.shopvisit.term.domain.TermSequence;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstTerminalinfoMDao.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-28</br>      
 * 功能描述: 终端表的DAO层</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public interface MstTerminalinfoMDao extends Dao<MstTerminalinfoM, String>
{

    /**
     * 获取某线路下的终端列表
     * 
     * 用于：巡店拜访  -- 终端选择
     * @param helper
     * @param lineId    线路主键
     * @return
     */
    public List<MstTermListMStc> queryTermLst(SQLiteOpenHelper helper, String lineId);

    /**
     * 获取某线路下的终端列表 根据总容量排序
     *
     * 用于：巡店拜访  -- 终端选择
     * @param helper
     * @param lineId    线路主键
     * @return
     */
    public List<MstTermListMStc> getTermList_tvolnum(SQLiteOpenHelper helper, String lineId, boolean isSequence);

    /***
     * 通过终端名称查询终端集合（模糊查询）
     * @param helper
     * @param termName
     * @return
     */
    public List<MstTermListMStc> getTermListByName(SQLiteOpenHelper helper, String termName);

    /**
     * 获取某线路下的各终端当天的所有拜访进店及离店时间
     * 
     * 用于：巡店拜访  -- 终端选择
     * @param helper
     * @param lineId    线路主键
     * @return
     */
    public List<MstTermListMStc> queryTermVistTime(SQLiteOpenHelper helper, String lineId);
    
    /**
     * 获取某线路下的当天已上传终端
     * 
     * 用于：巡店拜访  -- 终端选择
     * @param helper
     * @param lineId    线路主键
     * @return
     */
    public List<MstTermListMStc> queryTermUpflag(SQLiteOpenHelper helper, String lineId);

    /**
     * 依据终端主键获取终端 信息（包含数据字典对应的名称）
     * 
     * @param helper
     * @param termId    终端主键
     * @return
     */
    public MstTerminalInfoMStc findById(SQLiteOpenHelper helper, String termId);
    
    /**
     * 依据终端主键获取终端 信息（包含数据字典对应的名称）
     * 
     * 上传图片时,由于之前的方法(findById)出错,所以重写,注释掉会出错的代码
     * 
     * @param helper
     * @param termId    终端主键
     * @return
     */
    public MstTerminalInfoMStc findByTermId(SQLiteOpenHelper helper, String termId);

    /***
     *  修改终端顺序
     * @param helper
     * @param list
     */
    public void updateTermSequence(SQLiteOpenHelper helper, List<TermSequence> list);

    /**获取名称
     * @param databaseHelper
     * @param termId
     * @return
     */
    public TerminalName findByIdName(DatabaseHelper databaseHelper, String termId);

	/**
	 * @param helper
	 * @param string
	 * @param string2
	 * @return
	 */
	public List<MstTermListMStc> queryTermVistTimeByLoginDate(DatabaseHelper helper, String string, String string2);

}
