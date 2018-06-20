package et.tsingtaopad.operation.weekworkplan;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.PadPlantempcheckM;
import et.tsingtaopad.db.tables.PadPlantempcollectionInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：MakePlanService.java</br> 作者：@ray </br>
 * 创建时间：2013-12-13</br> 功能描述: 制定工作计划业务处理</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
public class MakeWeekPlanService {
	private final String TAG = "MakePlanService";
	private Context context;
	private Handler handler;
	private List<MstProductM> productList = new ArrayList<MstProductM>();
	private List<MstTerminalinfoM> mstTerminalList = new ArrayList<MstTerminalinfoM>();
	private List<PadPlantempcheckM> plantempList = new ArrayList<PadPlantempcheckM>();
	private List<PadPlantempcollectionInfo> colitemList = new ArrayList<PadPlantempcollectionInfo>();
	private DatabaseHelper helper;

	public MakeWeekPlanService(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		helper = DatabaseHelper.getHelper(context);
	}

	/**
	 * 查询终端类型数量
	 * 
	 * @param lineKeys
	 *            线路主键
	 */
	public Map<String, Integer> queryTelvel(List<String> lineKeys) {
		SQLiteDatabase db = helper.getReadableDatabase();
		if (!CheckUtil.IsEmpty(lineKeys)) {
		   String brackReplace = FunUtil.brackReplace(lineKeys);
		
		StringBuilder sb = new StringBuilder("select tlevel,count(tlevel) from  MST_TERMINALINFO_M where routekey in ( ").append(brackReplace).append(" ) ").append("group by tlevel");
		Map<String, Integer> telvelMap = new HashMap<String, Integer>();
		try {
			Dao<CmmDatadicM, String> cmmDatadicMDao = helper.getCmmDatadicMDao();
			if (db.isOpen()) {
				Cursor cursor = db.rawQuery(sb.toString(), null);
				int count = 0;
				while (cursor.moveToNext()) {
					String tlevelID = cursor.getString(0);
					//查询数据字典表
					if (!CheckUtil.isBlankOrNull(tlevelID)) {
						CmmDatadicM datadicM = cmmDatadicMDao.queryForId(tlevelID);
						if (datadicM != null) {
							Integer tlevelCount = cursor.getInt(1);
							count += tlevelCount;
							if ("A".equals(datadicM.getDicname())) {
								telvelMap.put("A", tlevelCount);
							} else if ("B".equals(datadicM.getDicname())) {
								telvelMap.put("B", tlevelCount);
							} else if ("C".equals(datadicM.getDicname())) {
								telvelMap.put("C", tlevelCount);
							} else if ("D".equals(datadicM.getDicname())) {
								telvelMap.put("D", tlevelCount);
							}
						}
					}

				}
				telvelMap.put("ABCD", count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return telvelMap;
		}
        return null;
	}

	/**
	 * 查询计划模版指标名称
	 */
	public List<PadPlantempcheckM> queryCheckname(Context context) {
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select * from PAD_PLANTEMPCHECK_M ";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			PadPlantempcheckM plantemp = new PadPlantempcheckM();
			plantemp.setCheckkey(cursor.getString(cursor.getColumnIndex("checkkey")));
			plantemp.setCheckname(cursor.getString(cursor.getColumnIndex("checkname")));
			plantemp.setPlantempkey(cursor.getString(cursor.getColumnIndex("plantempkey")));
			plantempList.add(plantemp);
		}

		return plantempList;

	}

	/**
	 * 查询计划模版采集项名称
	 * 
	 * @param checkkey
	 *            指标主键
	 */

	public List<PadPlantempcollectionInfo> queryColitemname(String checkkey) {
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			Dao<PadPlantempcollectionInfo, String> collectionDao = helper.getPadPlantempcollectionInfoDao();
			colitemList = collectionDao.queryForEq("checkkey", checkkey);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return colitemList;

	}

	/**
	 * 
	 * 查询空白终端添加产品list
	 * 
	 * @param gridkey
	 *            定格主键
	 * @param areaid
	 *            营销区域主键
	 * @return
	 */
	public List<MstProductM> queryProduct(Context context, String gridkey, String areaid) {
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		StringBuffer buffer = new StringBuffer();
		buffer.append("select distinct ap.productkey, ap.proname ");
		buffer.append("from mst_agencygrid_info am, v_agencyselproduct_info ap ");
		buffer.append("where am.agencykey = ap.agencykey and am.gridkey = ? ");
		buffer.append("and ((ap.enddate is null and ap.startdate <= ?) or ( ? between ap.startdate and ap.enddate)) ");
		buffer.append("order by am.agencykey, ap.proname ");
		String currDay = DateUtil.formatDate(new Date(), "yyyyMMdd");
		Cursor cursor = db.rawQuery(buffer.toString(), new String[] { gridkey, currDay, currDay });
		while (cursor.moveToNext()) {
			MstProductM mstProductM = new MstProductM();
			mstProductM.setProname(cursor.getString(cursor.getColumnIndex("proname")));
			mstProductM.setProductkey(cursor.getString(cursor.getColumnIndex("productkey")));
			productList.add(mstProductM);
		}
		return productList;
	}

}
