package et.tsingtaopad.visit.agencystorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencyinfoMDao;
import et.tsingtaopad.db.dao.MstAgencyvisitMDao;
import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.visit.agencystorage.domain.AgencystorageStc;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：AgencyStorageService.java</br>
 * 作者：@吴欣伟</br>
 * 创建时间：2013/11/25</br>
 * 功能描述: 经销商库存业务逻辑</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class AgencyStorageService
{

    private Context context;

    public AgencyStorageService(Context context)
    {

        this.context = context;
    }

    /**
     * 获取经销商库存显示数据
     * @param agencyKey 经销商ID
     * @return
     */
    public List<AgencystorageStc> getAgencyStorage(String datecureents, String datecureentx, String agencyKey, String datecureent, String dateselects, String dateselectx, String agencyKeys, String dateselect)
    {
        List<AgencystorageStc> agencystorageStcLst = new ArrayList<AgencystorageStc>();
        try
        {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyinfoMDao dao = helper.getDao(MstAgencyinfoM.class);
            agencystorageStcLst = dao.agencyStorageQuery(helper, datecureents, datecureentx, agencyKey, datecureent, dateselects, dateselectx, agencyKeys, dateselect);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return agencystorageStcLst;
    }

    /***
     * 是否存在经销商拜访
     * @param agencyKey
     * @param visitDate 拜访日期(格式：yyyy-mm-dd)
     * @return
     */
    public boolean isExistAgencyVisit(String agencyKey, String visitDate)
    {
        boolean isExist = false;
        try
        {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyvisitMDao dao = helper.getDao(MstAgencyvisitM.class);
            isExist = dao.isExistAgencyVisit(helper, agencyKey, visitDate);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return isExist;
    }

    /***
     * 获取小于此日期的最后一次拜访
     * @param agencyKey
     * @param visitDate
     * @return
     */
    public String getMaxVisitDate(String agencyKey, String visitDate)
    {
        String newVisitDate = "";
        try
        {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyvisitMDao dao = helper.getDao(MstAgencyvisitM.class);
            QueryBuilder<MstAgencyvisitM, String> qb = dao.queryBuilder();
            Where<MstAgencyvisitM, String> where = qb.where();
            where.eq("agencykey", agencyKey);
            where.and();
            where.lt("agevisitdate", visitDate + "000000");
            qb.orderBy("agevisitdate", false);
            MstAgencyvisitM info = qb.queryForFirst();
            if (info != null)
            {
                newVisitDate = info.getAgevisitdate().substring(0, 8);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return newVisitDate;
    }
}
