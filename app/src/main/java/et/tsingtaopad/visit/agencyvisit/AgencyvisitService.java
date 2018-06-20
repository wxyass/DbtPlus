package et.tsingtaopad.visit.agencyvisit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencyinfoMDao;
import et.tsingtaopad.db.dao.MstAgencyvisitMDao;
import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.visit.agencyvisit.domain.AgencySelectStc;

/**
 * 项目名称：营销移动智能工作平台</br>
 * 文件名：AgencyvisitService.java</br>
 * 作者：@吴欣伟</br>
 * 创建时间：2013/11/26</br>
 * 功能描述: 经销商拜访选择经销商业务逻辑</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期       原因  BUG号    修改人 修改版本</br>
 */
public class AgencyvisitService {
    
    private final String TAG = "AgencyvisitService";

    private Context context;
    
    public AgencyvisitService(Context context) {
        
        this.context = context;
    }
    
    /**
     * 选择经销商的数据查询
     * @return
     */
    public List<AgencySelectStc> agencySelectLstQuery() {
        List<AgencySelectStc> agencySelectStcLst = new ArrayList<AgencySelectStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyvisitMDao dao = helper.getDao(MstAgencyvisitM.class);
            //agencySelectStcLst = dao.agencySelectQuery(helper, ConstValues.loginSession.getGridId());
            agencySelectStcLst = dao.agencySelectQuery(helper, PrefUtils.getString(context, "gridId", ""));
        } catch (SQLException e) {
            Log.e(TAG, "选择经销商的数据查询时报错", e);
        }
        return agencySelectStcLst;
    }
    
    /**
     * 获取当前定格的主供货商
     * @return
     */
    public MstAgencyinfoM queryMainAgencyForGrid() {
        MstAgencyinfoM agencyM = new MstAgencyinfoM();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyinfoMDao dao = helper.getDao(MstAgencyinfoM.class);
            //agencyM = dao.queryMainAgency(helper, ConstValues.loginSession.getGridId());
            agencyM = dao.queryMainAgency(helper, PrefUtils.getString(context, "gridId", ""));
        } catch (SQLException e) {
            Log.e(TAG, "获取当前定格的主供货商时报错", e);
        }
        return agencyM;
    }
}
