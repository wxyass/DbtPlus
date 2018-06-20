/**
 * 
 */
package et.tsingtaopad.operation.workplan;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.MstPlanforuserM;
import et.tsingtaopad.operation.workplan.domain.ResultStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：WorkplanService.java</br> 作者：@ray </br>
 * 创建时间：2013-11-29</br> 功能描述: 工作计划业务处理</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
public class WorkPlanService {
	private final String TAG = "WorkPlanService";
	private Context context;
	@SuppressWarnings("unused")
	private Handler handler;

	/**
	 * @param context
	 * @param handler
	 */
	public WorkPlanService(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	/**
	 * 查询计划内容
	 * 
	 * @param uuid
	 *            计划主键
	 */

	public List<ResultStc> queryPlan(String uuid) {
		
//		MstPlanforuserM userM = new MstPlanforuserM();
//		MstPlancheckInfo checkM = new MstPlancheckInfo();
//		MstPlancollectionInfo collectionM = new MstPlancollectionInfo();
		List<ResultStc> resultList = new ArrayList<ResultStc>();
//		List<MstPlancollectionInfo> colleLst = new ArrayList<MstPlancollectionInfo>();; // 采集项
//		List<MstPlancheckInfo> checkLst= new ArrayList<MstPlancheckInfo>();//指标
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from Mst_Planforuser_m am "
					+"left join MST_PLANCHECK_INFO bm on am.plankey = bm.plankey "
					+"left join MST_PLANCOLLECTION_INFO cm on bm.pcheckkey = cm.pcheckkey and bm.checkkey = cm.checkkey "
					+"left join PAD_PLANTEMPCHECK_M dm on bm.checkkey = dm.checkkey "
					+"left join PAD_PLANTEMPCOLLECTION_INFO em on em.checkkey = dm.checkkey "
					+" where am.plankey = ?";
		Log.w(TAG, sql);
		Cursor cursor = db.rawQuery(sql, new String[] { uuid });
		while (cursor.moveToNext()) {
			ResultStc result = new ResultStc();
			result.setPlantitle(cursor.getString(cursor.getColumnIndex("plantitle")));
			result.setLinekey(cursor.getString(cursor.getColumnIndex("linekey")));
			result.setPlanamf(cursor.getString(cursor.getColumnIndex("planamf")));
			result.setPlanamt(cursor.getString(cursor.getColumnIndex("planamt")));
			result.setPlanpmf(cursor.getString(cursor.getColumnIndex("planpmf")));
			result.setPlanpmt(cursor.getString(cursor.getColumnIndex("planpmt")));
			result.setPlandate(cursor.getString(cursor.getColumnIndex("plandate")));
			result.setPlanstatus(cursor.getString(cursor.getColumnIndex("planstatus")));
			result.setPcheckkey(cursor.getString(cursor.getColumnIndex("pcheckkey")));
			result.setCheckkey(cursor.getString(cursor.getColumnIndex("checkkey")));
			result.setCheckname(cursor.getString(cursor.getColumnIndex("checkname")));
			result.setPcolitemkey(cursor.getString(cursor.getColumnIndex("pcolitemkey")));
			result.setColitemkey(cursor.getString(cursor.getColumnIndex("colitemkey")));
			result.setColitemname(cursor.getString(cursor.getColumnIndex("colitemname")));
			result.setTermnum(cursor.getLong(cursor.getColumnIndex("termnum")));
			result.setTermnames(cursor.getString(cursor.getColumnIndex("termnames")));
			result.setProductkey(cursor.getString(cursor.getColumnIndex("productkey")));
			resultList.add(result);
		}
		return resultList;
	}
	
	/**
	 * 修改人员计划表中的这周日计划 padisconsistent = '2' ,planstatus = '5' 
	 * @param date
	 */
	public void setPlanforuserpadiscon(String[] date) {
		
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String sql = "update Mst_Planforuser_m set padisconsistent = '2' ,planstatus = '5' "
				+" where plandate >= '"+date[0]+"' and plandate <= '"+date[1]
				+"' and padisconsistent = '0'";
		Log.w(TAG, sql);
		db.execSQL(sql);
		
	}
}
