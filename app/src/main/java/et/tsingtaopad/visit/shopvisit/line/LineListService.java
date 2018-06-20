package et.tsingtaopad.visit.shopvisit.line;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstRouteMDao;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.visit.shopvisit.line.domain.MstRouteMStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：LineListService.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-28</br>      
 * 功能描述: 巡店拜访_线路列表的业务逻辑</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class LineListService {
    
    private final String TAG = "LineListService";
    
    private Context context;
    
    public LineListService(Context context) {
        this.context = context;
    }
    
    /**
     *  获取相应的数据列表数据
     */
    public List<MstRouteMStc> queryLine() {
        
        List<MstRouteMStc> lst = new ArrayList<MstRouteMStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstRouteMDao dao = helper.getDao(MstRouteM.class);
            lst = dao.queryLine(helper);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        
        // 获取线路ID
        List<String> lineIdLst = FunUtil
                .getPropertyByName(lst, "routekey", String.class);
        
        // 获取每条线路上次拜访日期
        Map<String, String> visitMap = this.queryPrevVisitDate(lineIdLst);
        for (MstRouteMStc item : lst) {
            item.setPrevDate(visitMap.get(item.getRoutekey()));
        }
        
        return lst;
    }
    
    /**
     * 获取线路上次拜访日期
     * 
     * @param lineIdLst
     */
    public Map<String, String> queryPrevVisitDate(List<String> lineIdLst) {
        Map<String, String> visitMap = new HashMap<String, String>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstRouteMDao dao = helper.getDao(MstRouteM.class);
            visitMap = dao.queryPrevVisitDate(helper, lineIdLst);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取线路表DAO对象失败", e);
        }
        return visitMap;
    }

}
