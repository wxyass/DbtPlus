package et.tsingtaopad.visit.syncdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;
import cn.com.benyoyo.manage.bs.IntStc.DataSynStc;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.lidroid.xutils.http.ResponseInfo;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.db.DBManager;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.CmmAreaM;
import et.tsingtaopad.db.tables.CmmBoardM;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstAgencyKFM;
import et.tsingtaopad.db.tables.MstAgencygridInfo;
import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.db.tables.MstAgencysupplyInfo;
import et.tsingtaopad.db.tables.MstAgencytransferInfo;
import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.db.tables.MstCheckexerecordInfo;
import et.tsingtaopad.db.tables.MstCheckmiddleInfo;
import et.tsingtaopad.db.tables.MstCmpagencyInfo;
import et.tsingtaopad.db.tables.MstCmpbrandsM;
import et.tsingtaopad.db.tables.MstCmpcompanyM;
import et.tsingtaopad.db.tables.MstCmproductinfoM;
import et.tsingtaopad.db.tables.MstCmpsupplyInfo;
import et.tsingtaopad.db.tables.MstCollectionexerecordInfo;
import et.tsingtaopad.db.tables.MstGroupproductM;
import et.tsingtaopad.db.tables.MstInvoicingInfo;
import et.tsingtaopad.db.tables.MstMonthtargetInfo;
import et.tsingtaopad.db.tables.MstPictypeM;
import et.tsingtaopad.db.tables.MstPlanTerminalM;
import et.tsingtaopad.db.tables.MstPlanWeekforuserM;
import et.tsingtaopad.db.tables.MstPlancheckInfo;
import et.tsingtaopad.db.tables.MstPlancollectionInfo;
import et.tsingtaopad.db.tables.MstPlanforuserM;
import et.tsingtaopad.db.tables.MstPlanrouteInfo;
import et.tsingtaopad.db.tables.MstPowerfulchannelInfo;
import et.tsingtaopad.db.tables.MstPowerfulterminalInfo;
import et.tsingtaopad.db.tables.MstPriceM;
import et.tsingtaopad.db.tables.MstPricedetailsInfo;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.db.tables.MstProductareaInfo;
import et.tsingtaopad.db.tables.MstProductshowM;
import et.tsingtaopad.db.tables.MstPromoproductInfo;
import et.tsingtaopad.db.tables.MstPromotermInfo;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstPromotionstypeM;
import et.tsingtaopad.db.tables.MstQuestionsanswersInfo;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.db.tables.MstShowpicInfo;
import et.tsingtaopad.db.tables.MstSynckvM;
import et.tsingtaopad.db.tables.MstTermLedgerInfo;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.db.tables.MstVisitauthorizeInfo;
import et.tsingtaopad.db.tables.MstVisitmemoInfo;
import et.tsingtaopad.db.tables.MstVistproductInfo;
import et.tsingtaopad.db.tables.MstWorksummaryInfo;
import et.tsingtaopad.db.tables.PadCheckaccomplishInfo;
import et.tsingtaopad.db.tables.PadCheckproInfo;
import et.tsingtaopad.db.tables.PadCheckstatusInfo;
import et.tsingtaopad.db.tables.PadChecktypeM;
import et.tsingtaopad.db.tables.PadPlantempcheckM;
import et.tsingtaopad.db.tables.PadPlantempcollectionInfo;
import et.tsingtaopad.login.LoginService;
import et.tsingtaopad.login.domain.LoginSession;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FileUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.tools.ZipUtil;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br> 文件名：DownLoadDataService.java</br> 作者：wangshiming </br>
 * 创建时间：2014年1月10日</br> 功能描述: 数据下载</br> 版本 V 1.0</br> 修改履历加功能 xxM_delete 得到ids
 * 删除主表数据 删除字表数据</br> 日期 原因 BUG号 修改人 修改版本</br>
 */
@SuppressLint({ "DefaultLocale", "NewApi" })
public class DownLoadDataService {
	private final String TAG = "DownLoadDataService";

	private int threadCount;
	private static Context context;
	private DatabaseHelper helper;
	private Dao<MstVisitmemoInfo, String> mstVisitmemoInfoDao = null;
	private Dao<MstVisitM, String> mstVisitMDao = null;
	private Dao<MstQuestionsanswersInfo, String> mstQuestionsanswersInfoDao = null;
	private Dao<MstWorksummaryInfo, String> mstWorksummaryInfoDao = null;
	private Dao<MstTerminalinfoM, String> mstTerminalinfoMDao = null;
	private Dao<MstInvoicingInfo, String> mstInvoicingInfoDao = null;
	private Dao<MstAgencytransferInfo, String> mstAgencytransferInfoDao = null;
	private Dao<MstVistproductInfo, String> mstVistproductInfoDao = null;
	private Dao<MstCheckexerecordInfo, String> mstCheckexerecordInfoDao = null;
	private Dao<MstPromotermInfo, String> mstPromotermInfoDao = null;
	private Dao<MstCollectionexerecordInfo, String> mstCollectionexerecordInfoDao = null;
	private Dao<MstPlancollectionInfo, String> mstPlancollectionInfoDao = null;
	private Dao<MstPlanforuserM, String> mstPlanforuserMDao = null;
	private Dao<MstPlanWeekforuserM, String> mstPlanWeekforuserMDao = null;
	private Dao<MstSynckvM, String> mstSynckvMDao = null;
	private Dao<CmmBoardM, String> cmmBoardMDao = null;
	private Dao<CmmDatadicM, String> cmmDatadicMDao = null;
	private Dao<CmmAreaM, String> cmmAreaMDao = null;
	private Dao<MstRouteM, String> mstRouteMDao = null;
	private Dao<MstAgencysupplyInfo, String> mstAgencysupplyInfoDao = null;
	private Dao<MstAgencyvisitM, String> mstAgencyvisitMDao = null;
	private Dao<MstAgencyinfoM, String> mstAgencyinfoMDao = null;
	private Dao<MstAgencygridInfo, String> mstAgencygridInfoDao = null;
	private Dao<MstPromotionsM, String> mstPromotionsMDao = null;
	private Dao<MstPromotionstypeM, String> mstPromotionstypeMDao = null;
	private Dao<MstPromoproductInfo, String> mstPromoproductInfoDao = null;
	private Dao<MstCmpbrandsM, String> mstCmpbrandsMDao = null;
	private Dao<MstCmpcompanyM, String> mstCmpcompanyMDao = null;
	private Dao<MstCmproductinfoM, String> mstCmproductinfoMDao = null;
	private Dao<MstProductM, String> mstProductMDao = null;
	private Dao<MstCheckmiddleInfo, String> mstCheckmiddleInfoDao = null;
	private Dao<MstVisitauthorizeInfo, String> mstVisitauthorizeInfoDao = null;
	private Dao<PadChecktypeM, String> padChecktypeMDao = null;
	private Dao<PadCheckstatusInfo, String> padCheckstatusInfoDao = null;
	private Dao<PadCheckaccomplishInfo, String> padCheckaccomplishInfoDao = null;
	private Dao<PadCheckproInfo, String> padCheckproInfoDao = null;
	private Dao<PadPlantempcheckM, String> padPlantempcheckMDao = null;
	private Dao<PadPlantempcollectionInfo, String> padPlantempcollectionInfoDao = null;
	private Dao<MstPowerfulterminalInfo, String> mstPowerfulterminalInfoDao = null;
	private Dao<MstPowerfulchannelInfo, String> mstPowerfulchannelInfoDao = null;
	private Dao<MstPriceM, String> mstPriceMDao = null;
	private Dao<MstPricedetailsInfo, String> mstPricedetailsInfoDao = null;
	private Dao<MstProductareaInfo, String> mstProductareaInfoDao = null;
	private Dao<MstPlancheckInfo, String> mstPlancheckInfoDao = null;
	private Dao<MstPlanTerminalM, String> MstPlanTerminalMDao = null;
	private Dao<MstCmpsupplyInfo, String> mstCmpsupplyInfoDao = null;
	private Dao<MstPlanrouteInfo, String> mstPlanrouteInfodDao = null;
	private Dao<MstMonthtargetInfo, String> mstMonthtargetInfoDao = null;
	private Dao<MstCmpagencyInfo, String> mstCmpagencyInfoDao = null;
	private Dao<MstProductshowM, String> mstProductshowMDao = null;
	private Dao<MstShowpicInfo, String> mstShowpicInfoDao = null;
	private Dao<MstPictypeM, String> mstpictypeMDao = null;
	private Dao<MstAgencyKFM, String> mstAgencyKFMDao = null;
	public Dao<MstTermLedgerInfo, String> mstTermLedgerInfoDao = null;
	public Dao<MstGroupproductM, String> mstGroupproductMDao = null;

	private List<MstSynckvM> synTables = new ArrayList<MstSynckvM>();
	private DBManager dBManager = null;
	// MST_CMPAGENCY_INFO(竞品供应商管理信息表)
	// MST_PRODUCTSHOW_M(产品展示信息主表)
	// MST_SHOWPIC_INFO(展示图片信息表)
	private long defaultDelayMillis = 1000;
	private String synctime;// 点击同步记录时间
	private Handler handler;
	private String zipPath;
	private boolean zipFlag = false;
	/*** 是否同步过操作 ***/
	private boolean isDownLoadFlag = false;

	public DownLoadDataService(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		helper = DatabaseHelper.getHelper(context);
		dBManager = DBManager.getDBManager(context);
		Date date = new Date();
		synctime = DateUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
		String padxinghao = android.os.Build.MODEL;// 根据pad型号,设置同步数据时的线程数(老pad起一个线程)
		if (padxinghao.contains("2207") || padxinghao.contains("2007") || padxinghao.contains("3000")) {
			threadCount = 1;
		} else {
			threadCount = 1;
		}
		try {
			mstVisitmemoInfoDao = helper.getMstVisitmemoInfoDao();
			mstQuestionsanswersInfoDao = helper.getMstQuestionsanswersInfoDao();
			mstVisitMDao = helper.getMstVisitMDao();
			mstWorksummaryInfoDao = helper.getMstWorksummaryInfoDao();
			mstTerminalinfoMDao = helper.getMstTerminalinfoMDao();
			mstInvoicingInfoDao = helper.getMstInvoicingInfoDao();
			mstAgencytransferInfoDao = helper.getMstAgencytransferInfoDao();
			mstVistproductInfoDao = helper.getMstVistproductInfoDao();
			mstCheckexerecordInfoDao = helper.getMstCheckexerecordInfoDao();
			mstCollectionexerecordInfoDao = helper.getMstCollectionexerecordInfoDao();
			mstPromotermInfoDao = helper.getMstPromotermInfoDao();
			mstPlancollectionInfoDao = helper.getMstPlancollectionInfoDao();
			mstPlanforuserMDao = helper.getMstPlanforuserMDao();
			mstPlanWeekforuserMDao = helper.getMstPlanWeekforuserMDao();
			mstSynckvMDao = helper.getMstSynckvMDao();
			cmmBoardMDao = helper.getCmmBoardMDao();
			cmmDatadicMDao = helper.getCmmDatadicMDao();
			cmmAreaMDao = helper.getCmmAreaMDao();
			mstRouteMDao = helper.getMstRouteMDao();
			mstAgencysupplyInfoDao = helper.getMstAgencysupplyInfoDao();
			mstAgencyinfoMDao = helper.getMstAgencyinfoMDao();
			mstAgencygridInfoDao = helper.getMstAgencygridInfoDao();
			mstPromotionsMDao = helper.getMstPromotionsMDao();
			mstPromotionstypeMDao = helper.getMstPromotionstypeMDao();
			mstPromoproductInfoDao = helper.getMstPromoproductInfoDao();
			mstCmpbrandsMDao = helper.getMstCmpbrandsMDao();
			mstCmproductinfoMDao = helper.getMstCmproductinfoMDao();
			mstProductMDao = helper.getMstProductMDao();
			mstCheckmiddleInfoDao = helper.getMstCheckmiddleInfoDao();
			mstVisitauthorizeInfoDao = helper.getMstVisitauthorizeInfoDao();
			padChecktypeMDao = helper.getPadChecktypeMDao();
			padCheckstatusInfoDao = helper.getPadCheckstatusInfoDao();
			padCheckaccomplishInfoDao = helper.getPadCheckaccomplishInfoDao();
			padCheckproInfoDao = helper.getPadCheckproInfoDao();
			padPlantempcheckMDao = helper.getPadPlantempcheckMDao();
			padPlantempcollectionInfoDao = helper.getPadPlantempcollectionInfoDao();
			mstAgencyvisitMDao = helper.getMstAgencyvisitMDao();
			mstCmpcompanyMDao = helper.getMstCmpcompanyMDao();
			mstPowerfulterminalInfoDao = helper.getMstPowerfulterminalInfoDao();
			mstPowerfulchannelInfoDao = helper.getMstPowerfulchannelInfoDao();
			mstPriceMDao = helper.getMstPriceMDao();
			mstPricedetailsInfoDao = helper.getMstPricedetailsInfoDao();
			mstProductareaInfoDao = helper.getMstProductareaInfoDao();
			mstPlancheckInfoDao = helper.getMstPlancheckInfoDao();
			MstPlanTerminalMDao = helper.getMstPlanTerminalM();
			mstCmpsupplyInfoDao = helper.getMstCmpsupplyInfoDao();
			mstPlanrouteInfodDao = helper.getMstPlanrouteInfoDao();
			mstMonthtargetInfoDao = helper.getMstMonthtargetInfoDao();
			mstCmpagencyInfoDao = helper.getMstCmpagencyInfoDao();
			mstProductshowMDao = helper.getMstProductshowMDao();
			mstShowpicInfoDao = helper.getMstShowpicInfoDao();

			mstpictypeMDao = helper.getMstpictypeMDao();
			mstAgencyKFMDao = helper.getMstAgencyKFMDao();

			mstAgencyKFMDao = helper.getMstAgencyKFMDao();

			mstTermLedgerInfoDao = helper.getTermLedgerInfoDao();
			mstGroupproductMDao = helper.getMstGroupproductMDao();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	long start;

	// //三个线程进行下载
	public void asyndatas() {
		// 获取同步更新日期
		String datasynTime = DownLoadDataService.getDatasynTime(context);
		// 判断字符串是否为空或者null
		if (!CheckUtil.isBlankOrNull(datasynTime)) {
			isDownLoadFlag = true;
		}
		DbtLog.write("同步时间：" + datasynTime + ",是否同步：" + isDownLoadFlag);
		start = System.currentTimeMillis();

		try {
			synTables = mstSynckvMDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		List<DataSynStc> datasyns = new ArrayList<DataSynStc>();
		for (int i = synTables.size() - 1; i >= 0; i--) {
			MstSynckvM mstSynckvM = synTables.get(i);
			// if ("MST_CHECKEXERECORD_INFO".equals(mstSynckvM.getTablename()))
			// {
			DataSynStc datasyn = new DataSynStc();
			datasyn.setStrDays(mstSynckvM.getSyncDay());
			datasyn.setStrLastTime(mstSynckvM.getUpdatetime());
			datasyn.setStrTableName(mstSynckvM.getTablename());
			datasyn.setStrTempKey(mstSynckvM.getRemarks());
			datasyns.add(datasyn);
			// }else{
			// synTables.remove(mstSynckvM);
			// }
		}
		new Thread(new Runnable() {
			private List<DataSynStc> datasyns;

			@Override
			public void run() {

				DownLoadPool downLoadPool = new DownLoadPool(datasyns, handler, context);
				downLoadPool.downLoad();
			}

			public Runnable setDatasyn(List<DataSynStc> datasyns) {
				this.datasyns = datasyns;
				return this;
			}
		}.setDatasyn(datasyns)).start();
	}

	/**
	 * CMM_BOARD_M(公告管理表)CmmBoard
	 * 
	 */
	private void createOrUpdate_CMM_BOARD_M(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新公告数据中...", defaultDelayMillis);

		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_CMM_BOARD_M start transataion");
			// do中...

			String json = "";
			List<CmmBoardM> cmmBoardMs = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("CMM_BOARD_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					cmmBoardMs = JsonUtil.parseList(json, CmmBoardM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(cmmBoardMDao, cmmBoardMs);
					} else {
						updateData(cmmBoardMDao, cmmBoardMs);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("CMM_BOARD_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "CMM_BOARD_M");
			}

			// 删除数据
			String strCmmBoardMIDS = (String) responseHashMap.get("CMM_BOARD_M_DELETEFLAG");
			List<String> cmmBoardMids = JsonUtil.parseList(strCmmBoardMIDS, String.class);
			deleteIds(cmmBoardMDao, cmmBoardMids);
			connection.commit(null);
			Log.e(TAG, "createOrUpdate_CMM_BOARD_M commit transataion");

		} catch (Exception e) {
			try {
				Log.e(TAG, "createOrUpdate_CMM_BOARD_M rollback transataion", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新公告管理表出现错误：" + e.toString());
		}
	}

	/**
	 * 更新MST_MONTHTARGET_INFO(月目标管理信息表)
	 * 
	 * @param responseHashMap
	 */
	private void createOrUpdate_MST_MONTHTARGET_INFO(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新月目标管理信息表中...", defaultDelayMillis);

		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_MONTHTARGET_INFO start transataion");

			String json = "";
			List<MstMonthtargetInfo> monthtargetInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_MONTHTARGET_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					monthtargetInfos = JsonUtil.parseList(json, MstMonthtargetInfo.class);
					updateData(mstMonthtargetInfoDao, monthtargetInfos);
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_MONTHTARGET_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_MONTHTARGET_INFO");
			}

			// 删除
			String strMstMonthtargetInfoIDS = (String) responseHashMap.get("MST_MONTHTARGET_INFO_DELETEFLAG");
			List<String> ids = JsonUtil.parseList(strMstMonthtargetInfoIDS, String.class);
			deleteIds(mstMonthtargetInfoDao, ids);
			connection.commit(null);
			Log.e(TAG, "createOrUpdate_MST_MONTHTARGET_INFO commit transataion");

		} catch (Exception e) {
			try {
				Log.e(TAG, "createOrUpdate_MST_MONTHTARGET_INFO rollback transataion", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新月目标管理信息表错误：" + e.toString());
		}
	}

	/**
	 * MST_CMPAGENCY_INFO(竞品供应商管理信息表)
	 * 
	 * @param responseHashMap
	 */
	private void createOrUpdate_MST_CMPAGENCY_INFO(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新竞品供应商管理信息表中...", defaultDelayMillis);

		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_CMPAGENCY_INFO start transataion");

			String json = "";
			List<MstCmpagencyInfo> cmpagencyinfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_CMPAGENCY_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					cmpagencyinfos = JsonUtil.parseList(json, MstCmpagencyInfo.class);
					updateData(mstCmpagencyInfoDao, cmpagencyinfos);
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_CMPAGENCY_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_CMPAGENCY_INFO");
			}

			// 删除数据
			String cmpagencyinfoids = (String) responseHashMap.get("MST_CMPAGENCY_INFO_DELETEFLAG");
			List<String> ids = JsonUtil.parseList(cmpagencyinfoids, String.class);
			deleteIds(mstCmpagencyInfoDao, ids);
			connection.commit(null);
			Log.e(TAG, "createOrUpdate_MST_CMPAGENCY_INFO commit transataion");

		} catch (Exception e) {
			try {
				Log.e(TAG, "createOrUpdate_MST_CMPAGENCY_INFO rollback transataion", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新竞品供应商管理信息表错误：" + e.toString());
		}
	}

	/**
	 * MST_PRODUCTSHOW_M(产品展示信息主表) MST_SHOWPIC_INFO(展示图片信息表)
	 * 
	 * @param responseHashMap
	 */
	private void createOrUpdate_MST_PRODUCTSHOW_M(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新产品展示信息表中...", defaultDelayMillis);

		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_PRODUCTSHOW_M start transataion");

			String json = "";
			List<MstProductshowM> productshowms = null;
			List<MstShowpicInfo> showpic_infos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_PRODUCTSHOW_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					productshowms = JsonUtil.parseList(json, MstProductshowM.class);
					updateData(mstProductshowMDao, productshowms);

				} else if (key.toUpperCase().indexOf("MST_SHOWPIC_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					showpic_infos = JsonUtil.parseList(json, MstShowpicInfo.class);
					updateData(mstShowpicInfoDao, showpic_infos);

				}
			}

			// 更新Kv表最后更新时间
			String updateTime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PRODUCTSHOW_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updateTime)) {
				// 更新kv表
				updateKVData(database, updateTime, "MST_PRODUCTSHOW_M");
			}
			updateTime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_SHOWPIC_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updateTime)) {
				// 更新kv表
				updateKVData(database, updateTime, "MST_SHOWPIC_INFO");
			}

			// 删除
			String mst_productshow_mdeleteidstr = (String) responseHashMap.get("MST_PRODUCTSHOW_M_DELETEFLAG");
			List<String> mids = JsonUtil.parseList(mst_productshow_mdeleteidstr, String.class);

			String showpic_infodeleteidstr = (String) responseHashMap.get("MST_SHOWPIC_INFO_DELETEFLAG");
			List<String> ids = JsonUtil.parseList(showpic_infodeleteidstr, String.class);

			deleteIds(mstShowpicInfoDao, ids);
			// deleteInids(database, "MST_SHOWPIC_INFO", "showkey", mids);//保留字段
			deleteIds(mstProductshowMDao, mids);
			connection.commit(null);
			Log.e(TAG, "createOrUpdate_MST_PRODUCTSHOW_M commit transataion");

		} catch (Exception e) {
			try {
				Log.e(TAG, "createOrUpdate_MST_PRODUCTSHOW_M rollback transataion", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新产品展示信息表错误：" + e.toString());
		}
	}

	/**
	 * 
	 * @param progressStr
	 *            进度提示文字
	 * @param delayMillis
	 *            handler延迟发送时间
	 */
	int progress = 1;

	private void sendProgressMessage(String progressStr, long delayMillis) {
		/**
		 * progress++;
		 * 
		 * if (handler != null) { Log.i("sendProgressMessage",
		 * "handler current progress:" + progress + "-----" + progressStr);
		 * Message msg = new Message(); Bundle bundler = new Bundle();
		 * bundler.putInt("progress", progress);
		 * bundler.putString("progressStr", progressStr); msg.setData(bundler);
		 * msg.what = DownLoadDataProgressActivity.SYNDATA_PROGRESS;
		 * handler.sendMessageDelayed(msg, delayMillis); }
		 **/
	}

	/**
	 * 根据主/外 键删除数据
	 * 
	 * @param tableName
	 * @param keyName
	 * @param ids
	 * @throws Exception
	 * @throws SQLException
	 */
	private void deleteInids(SQLiteDatabase database, String tableName, String keyName, List<String> ids)
			throws SQLException {

		// String sql = + tableName + " where " + keyName + "in (" +
		// " 'id1','id2','id3' " + " )";

		if (!CheckUtil.IsEmpty(ids)) {
			int total = ids.size();
			int start = 0;
			int end = 0;
			for (int i = 0; i < total; i++) {
				start = i * 500;
				end = (i + 1) * 500;
				if (end >= total) {
					end = total;
					i = total;
				}

				// 删除
				String idIn = FunUtil.brackReplace(ids.subList(start, end));
				StringBuilder sqlBuilder = new StringBuilder("delete from ");
				sqlBuilder.append(tableName);
				sqlBuilder.append(" where ");
				sqlBuilder.append(keyName);
				sqlBuilder.append(" in ( ");
				sqlBuilder.append(idIn);
				sqlBuilder.append(" )");

				Log.i("in 删除",
						"deleteInids 删除 ：" + tableName + "表为： key 为 " + keyName + ids.size() + sqlBuilder.toString());
				if (database.isOpen()) {
					database.execSQL(sqlBuilder.toString());
				}
			}
		} else {
			String listStatus = ids == null ? "传入对象为：null" : "数据size = 0";
			Log.i("in 删除", "deleteInids 删除 ：" + tableName + "表为：" + listStatus);
		}

	}

	/**
	 * 根据主键删除表数据
	 * 
	 * @param dao
	 * @param ids
	 * @throws SQLException
	 */
	private <T> void deleteIds(Dao<T, String> dao, List<String> ids) throws SQLException {

		if (!CheckUtil.IsEmpty(ids)) {
			Log.i("deleteIds", "删除 " + dao.getDataClass().getName() + "size :" + ids.size());
			int total = ids.size();
			int start = 0;
			int end = 0;
			for (int i = 0; i < total; i++) {
				start = i * 500;
				end = (i + 1) * 500;
				if (end >= total) {
					end = total;
					i = total;
				}
				dao.deleteIds(ids.subList(start, end));
			}
		} else {
			String listStatus = ids == null ? "传入对象为：null" : "数据size = 0";
			Log.i("deleteIds", "删除 " + dao.getDataClass().getName() + "为：" + listStatus);
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param dao
	 * @throws SQLException
	 */
	public <T> void updateData(Dao<T, String> dao, List<T> datas) throws SQLException {

		if (!CheckUtil.IsEmpty(datas)) {
			Log.i("updateData", "更新 " + dao.getDataClass().getName() + " updateData676 :" + datas.size());
			for (int i = 0; i < datas.size(); i++) {
				T data = datas.get(i);
				setPadisconsistentAndUploadFlag(data);
				dao.createOrUpdate(data);
			}
		} else {
			String listStatus = datas == null ? "传入对象为：null" : "数据size = 0";
			Log.i("updateData", "更新list " + dao.getDataClass().getName() + "为：" + listStatus);
		}
	}

	/**
	 * 保存数据
	 * 
	 * @param dao
	 * @throws SQLException
	 */
	public <T> void saveData(Dao<T, String> dao, List<T> datas) throws SQLException {

		if (!CheckUtil.IsEmpty(datas)) {
			//Log.i("updateData", "更新 " + dao.getDataClass().getName() + " size :" + datas.size());
			Log.i("updateData", "更新 " + dao.getDataClass().getName() + " saveData699 :" + datas.size());
			for (int i = 0; i < datas.size(); i++) {
				Log.i("updateData", "更新 " + dao.getDataClass().getName() + " saveData701 :" + i);
				T data = datas.get(i);
				setPadisconsistentAndUploadFlag(data);
				if (isDownLoadFlag) {
					updateData(dao, datas);
				} else {
					try {
						dao.create(data);
					} catch (Exception e) {
						DbtLog.write(e.getMessage());
						e.printStackTrace();
						dao.createOrUpdate(data);
					}
				}
			}
		} else {
			String listStatus = datas == null ? "传入对象为：null" : "数据size = 0";
			Log.i("updateData", "更新list " + dao.getDataClass().getName() + "为：" + listStatus);
		}
	}

	/**
	 * 保存数据
	 * 
	 * @param dao
	 * @param isFull
	 *            是否全量
	 * @throws SQLException
	 */
	private <T> void saveData(Dao<T, String> dao, List<T> datas, boolean isFull) throws SQLException {

		if (!CheckUtil.IsEmpty(datas)) {
			Log.i("updateData", "更新 " + dao.getDataClass().getName() + " size :" + datas.size());
			for (int i = 0; i < datas.size(); i++) {
				T data = datas.get(i);
				setPadisconsistentAndUploadFlag(data);
				try {
					dao.create(data);
				} catch (Exception e) {
					DbtLog.write(e.getMessage());
					/*MstCheckexerecordInfo ds =(MstCheckexerecordInfo)data;
					DbtLog.write(ds.getRecordkey());*/
					e.printStackTrace();
					dao.createOrUpdate(data);
				}
			}
		} else {
			String listStatus = datas == null ? "传入对象为：null" : "数据size = 0";
			Log.i("updateData", "更新list " + dao.getDataClass().getName() + "为：" + listStatus);
		}
	}

	@SuppressWarnings("unused")
	private <T> void setPadisconsistentAndUploadFlag(T data) {
		// 反射将 padisconsistent , uploadFlag;
		Class<? extends Object> clazz = data.getClass();
		try {
			Method padisconsistentMethod = data.getClass().getMethod("setPadisconsistent", String.class);
			padisconsistentMethod.invoke(data, ConstValues.FLAG_1);

		} catch (Exception e) {
			// e.printStackTrace();
		}
		// 为什么分开写？ 不写在同一个try 里面 因为 setPadisconsistent
		// 方法不存在就抛异常跳出去了而setUploadFlag可能存在的造成改方法无法执行
		try {
			Method uploadMethod = clazz.getMethod("setUploadFlag", String.class);
			uploadMethod.invoke(data, ConstValues.FLAG_1);

		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private void updateKVData(SQLiteDatabase database, String updatetime, String tableName) throws SQLException {

		Log.i("updateKVData", "更新kv表:" + tableName + "更新日期:" + updatetime);
		String sql = "UPDATE MST_SYNCKV_M SET updatetime= ?,synctime= ? where tablename= ?";
		if (database.isOpen()) {
			database.execSQL(sql, new String[] { updatetime, synctime, tableName });
		}
	}

	/**
	 * MST_PROMOMIDDLE_INFO(新增终端活动达成状态中间表)
	 * 
	 * @param promomiddleInfos
	 */
	// private void createOrUpdate_MST_PROMOMIDDLE_INFO(Map<String, Object>
	// responseHashMap) {
	// sendProgressMessage("更新新增终端活动达成状数据中...", defaultDelayMillis);
	// String strMST_PROMOMIDDLE_INFO = (String)
	// responseHashMap.get("MST_PROMOMIDDLE_INFO");
	// List<MstPromomiddleInfo> promomiddleInfos =
	// JsonUtil.parseList(strMST_PROMOMIDDLE_INFO, new
	// TypeToken<List<MstPromomiddleInfo>>() {
	// }.getType());
	// String strMST_PROMOMIDDLE_INFOIDS = (String)
	// responseHashMap.get("MST_PROMOMIDDLE_INFO_DELETEFLAG");
	// List<String> promomiddleInfoids =
	// JsonUtil.parseList(strMST_PROMOMIDDLE_INFOIDS, new
	// TypeToken<List<String>>() {
	// }.getType());
	//
	// AndroidDatabaseConnection connection = null;
	// try {
	// SQLiteDatabase database = helper.getWritableDatabase();
	// connection = new AndroidDatabaseConnection(database, true);
	// connection.setAutoCommit(false);
	// Log.e(TAG, "createOrUpdate_MST_PROMOMIDDLE_INFO start transataion");
	// // do中...
	//
	// if (!CheckUtil.IsEmpty(promomiddleInfos)) {
	// updateData(mstPromomiddleInfoDao, promomiddleInfos);
	// //更新kv表
	// Date updatetime = promomiddleInfos.get(0).getUpdatetime();
	// updateKVData(database, updatetime, "MST_PROMOMIDDLE_INFO");
	// }
	// //删除数据
	// deleteIds(mstPromomiddleInfoDao, promomiddleInfoids);
	//
	// connection.commit(null);
	// Log.e(TAG, "createOrUpdate_MST_PROMOMIDDLE_INFO commit transataion");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// try {
	// connection.rollback(null);
	// Log.e(TAG, "createOrUpdate_MST_PROMOMIDDLE_INFO rollback transataion");
	// } catch (SQLException e1) {
	// e1.printStackTrace();
	// }
	// }
	// }

	/**
	 * CmmDatadicM 更新字典表
	 * 
	 */
	private void createOrUpdate_CMM_DATADIC_M(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新数据字典数据中...", defaultDelayMillis);
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_CMM_DATADIC_M start transataion");
			// do中...
			String json = "";
			List<CmmDatadicM> cmmDatadicMs = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("CMM_DATADIC_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					cmmDatadicMs = JsonUtil.parseList(json, CmmDatadicM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(cmmDatadicMDao, cmmDatadicMs);
					} else {
						updateData(cmmDatadicMDao, cmmDatadicMs);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("CMM_DATADIC_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "CMM_DATADIC_M");
			}

			// 删除
			String strCMM_DATADIC_MIDS = (String) responseHashMap.get("CMM_DATADIC_M_DELETEFLAG");
			List<String> cmmDatadicMids = JsonUtil.parseList(strCMM_DATADIC_MIDS, String.class);
			deleteIds(cmmDatadicMDao, cmmDatadicMids);
			connection.commit(null);
			Log.e(TAG, "createOrUpdate_CMM_DATADIC_M commit transataion");
		} catch (Exception e) {
			try {
				Log.e(TAG, "createOrUpdate_CMM_DATADIC_M rollback transataion", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新数据字典表出现错误：" + e.toString());

		}
	}

	/**
	 * 更新客情备忘录 mstVisitmemoInfos
	 * 
	 */
	private void createOrUpdate_MST_VISITMEMO_INFO(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新客情备忘录数据中...", defaultDelayMillis);

		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_VISITMEMO_INFO start transataion");

			String json = "";
			List<MstVisitmemoInfo> mstVisitmemoInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_VISITMEMO_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					mstVisitmemoInfos = JsonUtil.parseList(json, MstVisitmemoInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstVisitmemoInfoDao, mstVisitmemoInfos);
					} else {
						updateData(mstVisitmemoInfoDao, mstVisitmemoInfos);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_VISITMEMO_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_VISITMEMO_INFO");
			}

			// 删除数据
			String strMST_VISITMEMO_INFOIDS = (String) responseHashMap.get("MST_VISITMEMO_INFO_DELETEFLAG");
			List<String> mstVisitmemoInfoids = JsonUtil.parseList(strMST_VISITMEMO_INFOIDS, String.class);
			deleteIds(mstVisitmemoInfoDao, mstVisitmemoInfoids);
			connection.commit(null);
			Log.e(TAG, "createOrUpdate_MST_VISITMEMO_INFO commit transataion");
		} catch (Exception e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdate_MST_VISITMEMO_INFO rollback transataion", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新客情备忘录表出现错误：" + e.toString());
		}
	}

	/**
	 * 更新问题反馈 mstQuestionsanswersInfos
	 * 
	 */
	private void createOrUpdate_MST_QUESTIONSANSWERS_INFO(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新问题反馈数据中...", defaultDelayMillis);

		AndroidDatabaseConnection connection = null;
		try {

			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_QUESTIONSANSWERS_INFO start transataion");

			String json = "";
			List<MstQuestionsanswersInfo> mstQuestionsanswersInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_QUESTIONSANSWERS_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					mstQuestionsanswersInfos = JsonUtil.parseList(json, MstQuestionsanswersInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstQuestionsanswersInfoDao, mstQuestionsanswersInfos);
					} else {
						updateData(mstQuestionsanswersInfoDao, mstQuestionsanswersInfos);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_QUESTIONSANSWERS_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_QUESTIONSANSWERS_INFO");
			}

			// 删除数据
			String strMST_QUESTIONSANSWERS_INFOIDS = (String) responseHashMap
					.get("MST_QUESTIONSANSWERS_INFO_DELETEFLAG");
			List<String> mstQuestionsanswersInfoids = JsonUtil.parseList(strMST_QUESTIONSANSWERS_INFOIDS, String.class);
			deleteInids(database, "MST_QUESTIONSANSWERS_INFO", "qakey", mstQuestionsanswersInfoids);
			connection.commit(null);
			Log.e(TAG, "createOrUpdate_MST_QUESTIONSANSWERS_INFO commit transataion");

		} catch (Exception e) {
			try {
				Log.e(TAG, "createOrUpdate_MST_QUESTIONSANSWERS_INFO rollback transataion", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新问题反馈表出现错误：" + e.toString());
		}
	}

	/**
	 * 更新日工作总结 mstWorksummaryInfos
	 * 
	 * @param
	 */
	private void createOrUpdate_MST_WORKSUMMARY_INFO(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新日工作总结数据中...", defaultDelayMillis);

		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_WORKSUMMARY_INFO start transataion");

			String json = "";
			List<MstWorksummaryInfo> mstWorksummaryInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_WORKSUMMARY_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					mstWorksummaryInfos = JsonUtil.parseList(json, MstWorksummaryInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstWorksummaryInfoDao, mstWorksummaryInfos);
					} else {
						updateData(mstWorksummaryInfoDao, mstWorksummaryInfos);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_WORKSUMMARY_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_WORKSUMMARY_INFO");
			}

			// 删除数据
			String strMST_WORKSUMMARY_INFOIDS = (String) responseHashMap.get("MST_WORKSUMMARY_INFO_DELETEFLAG");
			List<String> mstWorksummaryInfoids = JsonUtil.parseList(strMST_WORKSUMMARY_INFOIDS, String.class);
			deleteInids(database, "MST_WORKSUMMARY_INFO", "wskey", mstWorksummaryInfoids);
			connection.commit(null);

			Log.e(TAG, "createOrUpdate_MST_WORKSUMMARY_INFO commit transataion");
		} catch (Exception e) {
			try {
				Log.e(TAG, "createOrUpdate_MST_WORKSUMMARY_INFO rollback transataion", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新日工作总结出现错误：" + e.toString());
		}
	}

	/**
	 * CMM_AREA_M(省市县区域主表)
	 * 
	 */
	private void createOrUpdate_CMM_AREA_M(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新省市县区域数据中...", defaultDelayMillis);

		AndroidDatabaseConnection connection = null;
		try {

			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_CMM_AREA_M start transataion");

			String json = "";
			List<CmmAreaM> cmmAreaMs = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("CMM_AREA_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					cmmAreaMs = JsonUtil.parseList(json, CmmAreaM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(cmmAreaMDao, cmmAreaMs);
					} else {
						updateData(cmmAreaMDao, cmmAreaMs);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("CMM_AREA_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "CMM_AREA_M");
			}

			// 删除数据
			String strCMM_AREA_MIDS = (String) responseHashMap.get("CMM_AREA_M_DELETEFLAG");
			List<String> cmmAreaMids = JsonUtil.parseList(strCMM_AREA_MIDS, String.class);
			deleteIds(cmmAreaMDao, cmmAreaMids);
			connection.commit(null);
			Log.e(TAG, "createOrUpdate_CMM_AREA_M commit transataion");

		} catch (Exception e) {
			try {
				Log.e(TAG, "createOrUpdate_CMM_AREA_M rollback transataion", e);
				connection.rollback(null);

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新省市县区域主表出现错误：" + e.toString());
		}
	}

	/**
	 * MST_VISITAUTHORIZE_INFO(定格可拜访授权)  visitauthorizeInfos
	 * 
	 * @param
	 */
	private void createOrUpdate_MST_VISITAUTHORIZE_INFO(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新定格可拜访授权数据中...", defaultDelayMillis);

		AndroidDatabaseConnection connection = null;
		try {

			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			String json = "";
			List<MstVisitauthorizeInfo> visitauthorizeInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_VISITAUTHORIZE_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					visitauthorizeInfos = JsonUtil.parseList(json, MstVisitauthorizeInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstVisitauthorizeInfoDao, visitauthorizeInfos);
					} else {
						updateData(mstVisitauthorizeInfoDao, visitauthorizeInfos);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_VISITAUTHORIZE_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_VISITAUTHORIZE_INFO");
			}

			// 删除数据
			String mst_visitauthorize_infoids = (String) responseHashMap.get("MST_VISITAUTHORIZE_INFO_DELETEFLAG");
			List<String> visitauthorizeInfoids = JsonUtil.parseList(mst_visitauthorize_infoids, String.class);
			deleteIds(mstVisitauthorizeInfoDao, visitauthorizeInfoids);

			connection.commit(null);
			Log.e(TAG, "createOrUpdate_MST_VISITAUTHORIZE_INFO commit transataion");
		} catch (Exception e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdate_MST_VISITAUTHORIZE_INFO rollback transataion", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新定格可拜访授权表出现错误：" + e.toString());
		}
	}

	/**
	 * 更新终端档案-线路主表  mstTerminalinfoMs
	 * 
	 * @param
	 */
	private void createOrUpdateTerminal_Route(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新线路终端档案数据中...", defaultDelayMillis);
		Log.e(TAG, "createOrUpdateTerminal_Route");
		AndroidDatabaseConnection connection = null;
		try {

			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdateTerminal_Route start transataion");

			String json = "";
			List<MstTerminalinfoM> mstTerminalinfoMs = null;
			List<MstRouteM> mstRouteMs = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_TERMINALINFO_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					//FileUtil.writeTxt(json, FileUtil.getSDPath()+"/MST_TERMINALINFO_M_ZIPwww.txt");
					mstTerminalinfoMs = JsonUtil.parseList(json, MstTerminalinfoM.class);
					/*if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstTerminalinfoMDao, mstTerminalinfoMs);
					} else {
						updateData(mstTerminalinfoMDao, mstTerminalinfoMs);
					}*/
					StringBuffer buffer = new StringBuffer();
					buffer.append("DELETE FROM MST_TERMINALINFO_M WHERE padisconsistent =  ?  ");
					database.execSQL(buffer.toString(), new String[] { "1" });
					/*buffer.append("DELETE FROM MST_TERMINALINFO_M  ");
					database.execSQL(buffer.toString(), null);*/
					//updateData(mstTerminalinfoMDao, mstTerminalinfoMs);
					saveData(mstTerminalinfoMDao, mstTerminalinfoMs,true);

				} else if (key.toUpperCase().indexOf("MST_ROUTE_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					mstRouteMs = JsonUtil.parseList(json, MstRouteM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstRouteMDao, mstRouteMs);
					} else {
						updateData(mstRouteMDao, mstRouteMs);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_TERMINALINFO_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_TERMINALINFO_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_ROUTE_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_ROUTE_M");
			}

			// 删除数据
			String strMST_TERMINALINFO_MIDS = (String) responseHashMap.get("MST_TERMINALINFO_M_DELETEFLAG");
			List<String> mstTerminalinfoMids = JsonUtil.parseList(strMST_TERMINALINFO_MIDS, String.class);

			String strMST_ROUTE_MIDS = (String) responseHashMap.get("MST_ROUTE_M_DELETEFLAG");
			List<String> mstRouteMIDs = JsonUtil.parseList(strMST_ROUTE_MIDS, String.class);

			deleteIds(mstTerminalinfoMDao, mstTerminalinfoMids);
			deleteInids(database, "MST_TERMINALINFO_M", "routekey", mstRouteMIDs);// 线路不存在了
																					// 那么当前线路下所有终端都不存在
			deleteIds(mstRouteMDao, mstRouteMIDs);
			connection.commit(null);
			Log.e(TAG, "createOrUpdateTerminal_Route commit transataion");

		} catch (Exception e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdateTerminal_Route rollback transataion", e);

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新终端档案-线路主表出现错误" + e.toString());
		}
	}

	/**
	 * 更新巡店拜访信息
	 * 
	 */
	//int count = 0;
	private void createOrUpdateVisit(Map<String, Object> responseHashMap, String abc, DataSynStc adatasyn) {
		sendProgressMessage("更新巡店拜访数据中...", defaultDelayMillis);
		Log.e(TAG, "createOrUpdateVisit");
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdateVisit start transataion");

			String json = "";
			List<MstVisitM> visits = new ArrayList<MstVisitM>();
			List<MstAgencysupplyInfo> agencysupplyInfos;
			List<MstVistproductInfo> vistproductInfos;
			List<MstCheckexerecordInfo> checkexerecordInfos;
			List<MstCollectionexerecordInfo> collectionexerecordInfos;
			List<MstPromotermInfo> promotermInfos;
			List<MstCmpsupplyInfo> cmpsupplyInfos;
			boolean isDelCheckExrecord = true;
			for (String key : responseHashMap.keySet()) {

				// 拜访主表
				if (key.toUpperCase().indexOf("MST_VISIT_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					visits = JsonUtil.parseList(json, MstVisitM.class);
					//FileUtil.writeTxt(json, FileUtil.getSDPath()+"/MST_VISIT_M_ZIP_1.txt"); 
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstVisitMDao, visits);
					} else {
						updateData(mstVisitMDao, visits);
					}

					// 供货关系
				} else if (key.toUpperCase().indexOf("MST_AGENCYSUPPLY_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
						FileUtil.writeTxt(json,FileUtil.getSDPath()+"/MST_AGENCYSUPPLY_INFO_ZIP_ziputil.txt");
					} else {
						json = (String) responseHashMap.get(key);
						FileUtil.writeTxt(json,FileUtil.getSDPath()+"/MST_AGENCYSUPPLY_INFO_ZIP_string.txt");
					}
					// FileUtil.savFileFike(json, "MST_AGENCYSUPPLY_INFO_ZIP");
					// FileUtil.savFileFike(abc,
					// "abc_MST_AGENCYSUPPLY_INFO_ZIP");
					//count++;
					FileUtil.writeTxt(json,FileUtil.getSDPath()+"/MST_AGENCYSUPPLY_INFO_ZIP12323.txt");
					//FileUtil.writeTxt(count+"",FileUtil.getSDPath()+"/count_MST_AGENCYSUPPLY_INFO_ZIP2.txt");
					agencysupplyInfos = JsonUtil.parseList(json, MstAgencysupplyInfo.class);

					// if (database.isOpen()) {
					database.execSQL("DELETE FROM MST_AGENCYSUPPLY_INFO");// 清空数据
					// }
					saveData(mstAgencysupplyInfoDao, agencysupplyInfos, true);
					// 供货关系表的插入更新
					/*
					 * if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
					 * saveData(mstAgencysupplyInfoDao, agencysupplyInfos); }
					 * else { updateData(mstAgencysupplyInfoDao,
					 * agencysupplyInfos); }
					 */

					// 我品竞品记录
				} else if (key.toUpperCase().indexOf("MST_VISTPRODUCT_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					//FileUtil.writeTxt(json, FileUtil.getSDPath()+"/MST_VISTPRODUCT_INFO_ZIP_1.txt"); 
					vistproductInfos = JsonUtil.parseList(json, MstVistproductInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstVistproductInfoDao, vistproductInfos);
					} else {
						updateData(mstVistproductInfoDao, vistproductInfos);
					}

					// 拜访指标结果表
				} else if (key.toUpperCase().indexOf("MST_CHECKEXERECORD_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					//FileUtil.writeTxt(json, FileUtil.getSDPath()+"/MST_CHECKEXERECORD_INFO_ZIP_1.txt");
					Long time = new Date().getTime();
					checkexerecordInfos = JsonUtil.parseList(json, MstCheckexerecordInfo.class);
					// Date date= checkexerecordInfos.get(0).getCredate();
					// Log.e("updateData", "日期"+date);
					Long time1 = new Date().getTime();
					Log.e("updateData", "解析数据" + (time1 - time) + "数据长度：" + checkexerecordInfos.size());
					// // 如果非第一次更新，为满足Sieleb的要求应先删除更新到的终端、产品、指标对应的所有数据
					// if (!CheckUtil.isBlankOrNull(
					// ConstValues.kvMap.get("MST_CHECKEXERECORD_INFO").getUpdatetime()))
					// {
					// StringBuffer buffer = new StringBuffer();
					// buffer.append("delete from MST_CHECKEXERECORD_INFO ");
					// buffer.append("where visitkey = 'temp' and (1 != 1 ");
					// List<String> idLst = new ArrayList<String>();
					// String itemId = "";
					//
					// for (MstCheckexerecordInfo item : checkexerecordInfos) {
					// if (!(CheckUtil.isBlankOrNull(item.getTerminalkey()) ||
					// CheckUtil.isBlankOrNull(item.getProductkey()) ||
					// CheckUtil.isBlankOrNull(item.getCheckkey()))) {
					// itemId = item.getTerminalkey() + "|" +
					// item.getProductkey() + "|" + item.getCheckkey();
					// if (!idLst.contains(itemId)) {
					// idLst.add(itemId);
					// buffer.append(" or (terminalkey = '").append(item.getTerminalkey());
					// buffer.append("' and productkey = '").append(item.getProductkey());
					// buffer.append("' and checkkey = '").append(item.getCheckkey()).append("') ");
					// };
					// }
					// }
					// if (idLst.size() > 0) {
					// buffer.append(" )");
					// mstCheckexerecordInfoDao.executeRawNoArgs(buffer.toString());
					// }
					// }
					// 这儿1000条分次数删除，如果一起删除就会报异常 薛敬飞全部删除数据不要条件删除
					// if (!CheckUtil.isBlankOrNull(ConstValues.kvMap.get(
					// "MST_CHECKEXERECORD_INFO").getUpdatetime())) {
					// int end = 0;
					// for (int start = 0; start < checkexerecordInfos.size();
					// start = start + 500) {
					// end = start +500;
					// if (end >= checkexerecordInfos.size()) {
					// end = checkexerecordInfos.size();
					// }
					// StringBuffer buffer = new StringBuffer();
					// buffer.append("delete from MST_CHECKEXERECORD_INFO ");
					// buffer.append ("where visitkey = 'temp' and (1 != 1 ");
					// List<String> idLst = new ArrayList<String>();
					// String itemId = "";
					// for (MstCheckexerecordInfo item : checkexerecordInfos
					// .subList(start, end)) {
					// if (!(CheckUtil.isBlankOrNull(item
					// .getTerminalkey())
					// || CheckUtil.isBlankOrNull(item
					// .getProductkey()) || CheckUtil
					// .isBlankOrNull(item.getCheckkey()))) {
					// itemId = item.getTerminalkey() + "|"
					// + item.getProductkey() + "|"
					// + item.getCheckkey();
					// if (!idLst.contains(itemId)) {
					// idLst.add(itemId);
					// buffer.append(" or (terminalkey = '")
					// .append(item.getTerminalkey());
					// buffer.append("' and productkey = '")
					// .append(item.getProductkey());
					// buffer.append("' and checkkey = '")
					// .append(item.getCheckkey())
					// .append("') ");
					// }
					// ;
					// }
					//
					// }
					// if (idLst.size() > 0) {
					// buffer.append(" )");
					// int number= mstCheckexerecordInfoDao
					// .executeRawNoArgs(buffer.toString());
					// Log.e("updateData", "删除行数"+number);
					// }
					// }
					// };
					// 经过与姜世杰配合以后删除全部数据。。。
					if (isDelCheckExrecord) {
						mstCheckexerecordInfoDao.deleteBuilder().delete();
						isDelCheckExrecord = false;
					}
					List<MstCheckexerecordInfo> MstCheckexerecordInfos = mstCheckexerecordInfoDao.queryForAll();
					Log.e("updateData", "剩余数据" + MstCheckexerecordInfos.size());
					Long time2 = new Date().getTime();
					Log.e("updateData", "删除数据" + (time2 - time1));
					// 用基本sql
					// dBManager.addMstCheckexerecordInfo(checkexerecordInfos);

					saveData(mstCheckexerecordInfoDao, checkexerecordInfos, true);
					Long time3 = new Date().getTime();
					Log.e("updateData", "插入数据" + (time3 - time2));
					// 拜访采集项表
				} else if (key.toUpperCase().indexOf("MST_COLLECTIONEXERECORD_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					//FileUtil.writeTxt(json, FileUtil.getSDPath()+"/MST_COLLECTIONEXERECORD_INFO_ZIP_1.txt"); 
					collectionexerecordInfos = JsonUtil.parseList(json, MstCollectionexerecordInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstCollectionexerecordInfoDao, collectionexerecordInfos);
					} else {
						updateData(mstCollectionexerecordInfoDao, collectionexerecordInfos);
					}

					// 终端参与促销活动表
				} else if (key.toUpperCase().indexOf("MST_PROMOTERM_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					//FileUtil.writeTxt(json, FileUtil.getSDPath()+"/MST_PROMOTERM_INFO_ZIP_1.txt"); 
					promotermInfos = JsonUtil.parseList(json, MstPromotermInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstPromotermInfoDao, promotermInfos);
					} else {
						updateData(mstPromotermInfoDao, promotermInfos);
					}

					// 竞品供货关系
				} else if (key.toUpperCase().indexOf("MST_CMPSUPPLY_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					cmpsupplyInfos = JsonUtil.parseList(json, MstCmpsupplyInfo.class);
					// 更新数据
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstCmpsupplyInfoDao, cmpsupplyInfos);
					} else {
						updateData(mstCmpsupplyInfoDao, cmpsupplyInfos);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_VISIT_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_VISIT_M");
			}

			// 供货关系表 更新时间不做处理
			/*
			 * updatetime = FunUtil.isBlankOrNullTo(
			 * responseHashMap.get("MST_AGENCYSUPPLY_INFO_DATE"), ""); if
			 * (!CheckUtil.isBlankOrNull(updatetime)) { // 更新kv表
			 * updateKVData(database, updatetime, "MST_AGENCYSUPPLY_INFO"); }
			 */

			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_VISTPRODUCT_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_VISTPRODUCT_INFO");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_CHECKEXERECORD_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_CHECKEXERECORD_INFO");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_COLLECTIONEXERECORD_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_COLLECTIONEXERECORD_INFO");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PROMOTERM_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PROMOTERM_INFO");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_CMPSUPPLY_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_CMPSUPPLY_INFO");
			}

			// 删除数据
			String strMST_VISIT_MIDS = (String) responseHashMap.get("MST_VISIT_M_DELETEFLAG");
			List<String> visitids = JsonUtil.parseList(strMST_VISIT_MIDS, String.class);

			String strMST_AGENCYSUPPLY_INFOIDS = (String) responseHashMap.get("MST_AGENCYSUPPLY_INFO_DELETEFLAG");
			List<String> agencysupplyInfoids = JsonUtil.parseList(strMST_AGENCYSUPPLY_INFOIDS, String.class);

			String strMST_VISTPRODUCT_INFOIDS = (String) responseHashMap.get("MST_VISTPRODUCT_INFO_DELETEFLAG");
			List<String> vistproductInfoids = JsonUtil.parseList(strMST_VISTPRODUCT_INFOIDS, String.class);

			String strMST_CHECKEXERECORD_INFOIDS = (String) responseHashMap.get("MST_CHECKEXERECORD_INFO_DELETEFLAG");
			List<String> checkexerecordInfoids = JsonUtil.parseList(strMST_CHECKEXERECORD_INFOIDS, String.class);

			String strMST_COLLECTIONEXERECORD_INFOIDS = (String) responseHashMap
					.get("MST_COLLECTIONEXERECORD_INFO_DELETEFLAG");
			List<String> collectionexerecordInfoids = JsonUtil.parseList(strMST_COLLECTIONEXERECORD_INFOIDS,
					String.class);

			String strMST_PROMOTERM_INFOIDS = (String) responseHashMap.get("MST_PROMOTERM_INFO_DELETEFLAG");
			List<String> promotermInfoids = JsonUtil.parseList(strMST_PROMOTERM_INFOIDS, String.class);

			String strMST_CMPSUPPLY_INFOIDS = (String) responseHashMap.get("MST_CMPSUPPLY_INFO_DELETEFLAG");
			List<String> cmpsupplyInfoids = JsonUtil.parseList(strMST_CMPSUPPLY_INFOIDS, String.class);

			deleteIds(mstCmpsupplyInfoDao, cmpsupplyInfoids);

			deleteIds(mstPromotermInfoDao, promotermInfoids);
			deleteIds(mstCollectionexerecordInfoDao, collectionexerecordInfoids);
			deleteIds(mstCheckexerecordInfoDao, checkexerecordInfoids);
			deleteIds(mstVistproductInfoDao, vistproductInfoids);
			deleteIds(mstAgencysupplyInfoDao, agencysupplyInfoids);

			// 删除mst_Visit_M的字表
			deleteInids(database, "MST_VISTPRODUCT_INFO", "visitkey", visitids);
			deleteInids(database, "MST_CHECKEXERECORD_INFO", "visitkey", visitids);
			deleteInids(database, "MST_COLLECTIONEXERECORD_INFO", "visitkey", visitids);
			deleteInids(database, "MST_PROMOTERM_INFO", "visitkey", visitids);
			// 删除主表
			deleteIds(mstVisitMDao, visitids);

			connection.commit(null);
			Log.e(TAG, "createOrUpdateVisit commit transataion");
		} catch (Exception e) {
			try {
				Log.e(TAG, "createOrUpdateVisit rollback transataion", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新拜访信息表出现错误" + e.toString());
		}
	}

	/**
	 * 更新拜访主表
	 * 
	 * @param visits
	 *            <MstVisitM> visits
	 * @return List<String> ids
	 * @throws SQLException
	 */
	private void createOrUpdate_MST_VISIT_M(List<MstVisitM> visits) throws SQLException {

		if (visits == null || visits.size() == 0) {
			Log.i(TAG, "visits =null 或者 visits=0");
		} else {
			for (MstVisitM mstVisitM : visits) {
				mstVisitM.setPadisconsistent(ConstValues.FLAG_1);
				mstVisitM.setUploadFlag(ConstValues.FLAG_1);
				mstVisitMDao.createOrUpdate(mstVisitM);
			}
		}

	}

	/**
	 * 更新分经销商信息 MST_AGENCYINFO_M
	 */
	private void createOrUpdateAgencyInfo(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新分经销商信息数据中...", defaultDelayMillis);
		AndroidDatabaseConnection connection = null;// f
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdateAgencyInfo start transation");

			String json = "";
			List<MstAgencyinfoM> agencyinfoMs = null;
			List<MstProductM> products = null;
			List<MstProductareaInfo> productareaInfos = null;
			List<MstAgencygridInfo> agencygridInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_AGENCYINFO_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					agencyinfoMs = JsonUtil.parseList(json, MstAgencyinfoM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstAgencyinfoMDao, agencyinfoMs);
					} else {
						updateData(mstAgencyinfoMDao, agencyinfoMs);
					}

				} else if (key.toUpperCase().indexOf("MST_PRODUCT_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					// FileUtil.savFileFike(json, "MST_PRODUCT_M_ZIP");
					products = JsonUtil.parseList(json, MstProductM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstProductMDao, products);
					} else {
						updateData(mstProductMDao, products);
					}

				} else if (key.toUpperCase().indexOf("MST_PRODUCTAREA_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					productareaInfos = JsonUtil.parseList(json, MstProductareaInfo.class);
					if (database.isOpen()) {
						database.execSQL("DELETE FROM MST_PRODUCTAREA_INFO");// 清空数据
					}
					saveData(mstProductareaInfoDao, productareaInfos, true);

				} else if (key.toUpperCase().indexOf("MST_AGENCYGRID_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					agencygridInfos = JsonUtil.parseList(json, MstAgencygridInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstAgencygridInfoDao, agencygridInfos);
					} else {
						updateData(mstAgencygridInfoDao, agencygridInfos);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_AGENCYINFO_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_AGENCYINFO_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PRODUCT_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PRODUCT_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PRODUCTAREA_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PRODUCTAREA_INFO");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_AGENCYGRID_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_AGENCYGRID_INFO");
			}

			// 删除
			String strMST_AGENCYINFO_Mids = (String) responseHashMap.get("MST_AGENCYINFO_M_DELETEFLAG");
			List<String> agencyinfoMids = JsonUtil.parseList(strMST_AGENCYINFO_Mids, String.class);

			String strMST_PRODUCT_Mids = (String) responseHashMap.get("MST_PRODUCT_M_DELETEFLAG");
			List<String> productids = JsonUtil.parseList(strMST_PRODUCT_Mids, String.class);

			String strMST_PRODUCTAREA_INFOids = (String) responseHashMap.get("MST_PRODUCTAREA_INFO_DELETEFLAG");
			List<String> productareaInfoids = JsonUtil.parseList(strMST_PRODUCTAREA_INFOids, String.class);

			String strMST_AGENCYGRID_INFOids = (String) responseHashMap.get("MST_AGENCYGRID_INFO_DELETEFLAG");
			List<String> agencygridInfoids = JsonUtil.parseList(strMST_AGENCYGRID_INFOids, String.class);

			deleteIds(mstAgencygridInfoDao, agencygridInfoids);
			deleteIds(mstProductareaInfoDao, productareaInfoids);

			deleteInids(database, "MST_PRODUCTAREA_INFO", "productkey", productids);
			deleteIds(mstProductMDao, productids);

			deleteInids(database, "MST_AGENCYGRID_INFO", "agencykey", agencyinfoMids);
			deleteIds(mstAgencyinfoMDao, agencyinfoMids);

			connection.commit(null);
			Log.e(TAG, "createOrUpdateAgencyInfo commit transation");
		} catch (SQLException e) {
			try {
				Log.e(TAG, "createOrUpdateAgencyInfo rollback transation", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * 分经销商拜访
	 */
	private void createOrUpdateAgencyvisit(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新分经销商拜访数据中...", defaultDelayMillis);
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdateAgencyvisit 1 transation");

			String json = "";
			List<MstAgencyvisitM> agencyvisits = null;
			List<MstAgencytransferInfo> agencytransferInfos = null;
			List<MstInvoicingInfo> invoicingInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_AGENCYVISIT_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					agencyvisits = JsonUtil.parseList(json, MstAgencyvisitM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstAgencyvisitMDao, agencyvisits);
					} else {
						updateData(mstAgencyvisitMDao, agencyvisits);
					}

				} else if (key.toUpperCase().indexOf("MST_AGENCYTRANSFER_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					agencytransferInfos = JsonUtil.parseList(json, MstAgencytransferInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstAgencytransferInfoDao, agencytransferInfos);
					} else {
						updateData(mstAgencytransferInfoDao, agencytransferInfos);
					}

				} else if (key.toUpperCase().indexOf("MST_INVOICING_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					invoicingInfos = JsonUtil.parseList(json, MstInvoicingInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstInvoicingInfoDao, invoicingInfos);
					} else {
						updateData(mstInvoicingInfoDao, invoicingInfos);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_AGENCYVISIT_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_AGENCYVISIT_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_AGENCYTRANSFER_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_AGENCYTRANSFER_INFO");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_INVOICING_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_INVOICING_INFO");
			}

			// 删除
			String strMST_AGENCYVISIT_MIDS = (String) responseHashMap.get("MST_AGENCYVISIT_M_DELETEFLAG");
			List<String> agencyvisit_mids = JsonUtil.parseList(strMST_AGENCYVISIT_MIDS, String.class);

			String strMST_AGENCYTRANSFER_INFOIDS = (String) responseHashMap.get("MST_AGENCYTRANSFER_INFO_DELETEFLAG");
			List<String> agencytransfer_infoids = JsonUtil.parseList(strMST_AGENCYTRANSFER_INFOIDS, String.class);

			String strMST_INVOICING_INFOIDS = (String) responseHashMap.get("MST_INVOICING_INFO_DELETEFLAG");
			List<String> invoicing_infoids = JsonUtil.parseList(strMST_INVOICING_INFOIDS, String.class);

			deleteIds(mstInvoicingInfoDao, invoicing_infoids);
			deleteIds(mstAgencytransferInfoDao, agencytransfer_infoids);

			deleteInids(database, "MST_INVOICING_INFO", "agevisitkey", agencyvisit_mids);
			deleteInids(database, "MST_AGENCYTRANSFER_INFO", "agevisitkey", agencyvisit_mids);
			deleteIds(mstAgencyvisitMDao, agencyvisit_mids);

			connection.commit(null);
			Log.e(TAG, "createOrUpdateAgencyvisit 2 transation");

		} catch (SQLException e) {
			try {
				Log.e(TAG, "createOrUpdateAgencyvisit 3 transation", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("分经销商拜访表错误" + e.toString());
		}
	}

	/**
	 * 更新活动基础信息
	 */
	private void createOrUpdatePromotions(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新活动基础信息数据中...", defaultDelayMillis);
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdatePromotions 1 transation");

			String json = "";
			List<MstPromotionsM> promotionsMs = null;
			List<MstPromotionstypeM> promotionstypes = null;
			List<MstPromoproductInfo> promoproductInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_PROMOTIONS_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					promotionsMs = JsonUtil.parseList(json, MstPromotionsM.class);// 修改同步时间,每次都同步
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstPromotionsMDao, promotionsMs);
					} else {
						updateData(mstPromotionsMDao, promotionsMs);
					}

				} else if (key.toUpperCase().indexOf("MST_PROMOTIONSTYPE_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					promotionstypes = JsonUtil.parseList(json, MstPromotionstypeM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstPromotionstypeMDao, promotionstypes);
					} else {
						updateData(mstPromotionstypeMDao, promotionstypes);
					}

				} else if (key.toUpperCase().indexOf("MST_PROMOPRODUCT_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					promoproductInfos = JsonUtil.parseList(json, MstPromoproductInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstPromoproductInfoDao, promoproductInfos);
					} else {
						updateData(mstPromoproductInfoDao, promoproductInfos);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PROMOTIONS_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PROMOTIONS_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PROMOTIONSTYPE_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PROMOTIONSTYPE_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PROMOPRODUCT_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PROMOPRODUCT_INFO");
			}

			// 删除
			String strMST_PROMOTIONS_M_IDS = (String) responseHashMap.get("MST_PROMOTIONS_M_DELETEFLAG");
			List<String> promotionsmids = JsonUtil.parseList(strMST_PROMOTIONS_M_IDS, String.class);

			String strMST_PROMOTIONSTYPE_M_ids = (String) responseHashMap.get("MST_PROMOTIONSTYPE_M_DELETEFLAG");
			List<String> promotionstypemids = JsonUtil.parseList(strMST_PROMOTIONSTYPE_M_ids, String.class);

			String strMST_PROMOPRODUCT_INFO_ids = (String) responseHashMap.get("MST_PROMOPRODUCT_INFO_DELETEFLAG");
			List<String> promoproductinfoids = JsonUtil.parseList(strMST_PROMOPRODUCT_INFO_ids, String.class);

			deleteIds(mstPromoproductInfoDao, promoproductinfoids);//
			deleteIds(mstPromotionstypeMDao, promotionstypemids);
			deleteInids(database, "MST_PROMOPRODUCT_INFO", "promotkey", promotionsmids);
			deleteIds(mstPromotionsMDao, promotionsmids);

			connection.commit(null);
			Log.e(TAG, "createOrUpdatePromotions 2 transation");

		} catch (SQLException e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdatePromotions 3 transation", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(" 更新活动基础信息" + e.toString());
		}
	}

	/**
	 * 竞品基础表消息
	 */
	private void createOrUpdatecmpbrands(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新竞品数据中...", defaultDelayMillis);
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdatecmpbrands 1 transation");
			// 更新数据
			String json = "";
			List<MstCmpbrandsM> cmpbrandsMs = null;
			List<MstCmpcompanyM> cmpcompanyMs = null;
			List<MstCmproductinfoM> cmproductinfoMs = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_CMPBRANDS_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					cmpbrandsMs = JsonUtil.parseList(json, MstCmpbrandsM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstCmpbrandsMDao, cmpbrandsMs);
					} else {
						updateData(mstCmpbrandsMDao, cmpbrandsMs);
					}

				} else if (key.toUpperCase().indexOf("MST_CMPCOMPANY_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					cmpcompanyMs = JsonUtil.parseList(json, MstCmpcompanyM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstCmpcompanyMDao, cmpcompanyMs);
					} else {
						updateData(mstCmpcompanyMDao, cmpcompanyMs);
					}

				} else if (key.toUpperCase().indexOf("MST_CMPRODUCTINFO_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					cmproductinfoMs = JsonUtil.parseList(json, MstCmproductinfoM.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstCmproductinfoMDao, cmproductinfoMs);
					} else {
						updateData(mstCmproductinfoMDao, cmproductinfoMs);
					}
				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_CMPBRANDS_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_CMPBRANDS_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_CMPCOMPANY_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_CMPCOMPANY_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_CMPRODUCTINFO_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_CMPRODUCTINFO_M");
			}

			// 删除
			String strMST_CMPBRANDS_MIDS = (String) responseHashMap.get("MST_CMPBRANDS_M_DELETEFLAG");
			List<String> cmpbrands_mids = JsonUtil.parseList(strMST_CMPBRANDS_MIDS, String.class);

			String strMST_CMPCOMPANY_MIDS = (String) responseHashMap.get("MST_CMPCOMPANY_M_DELETEFLAG");
			List<String> cmpcompany_mids = JsonUtil.parseList(strMST_CMPCOMPANY_MIDS, String.class);

			String strMST_CMPRODUCTINFO_MIDS = (String) responseHashMap.get("MST_CMPRODUCTINFO_M_DELETEFLAG");
			List<String> cmproductinfo_mids = JsonUtil.parseList(strMST_CMPRODUCTINFO_MIDS, String.class);

			deleteIds(mstCmproductinfoMDao, cmproductinfo_mids);

			deleteInids(database, "MST_CMPRODUCTINFO_M", "cmpbrandkey", cmpcompany_mids);
			deleteIds(mstCmpbrandsMDao, cmpbrands_mids);

			// 删除
			deleteInids(database, "MST_CMPBRANDS_M", "cmpcomkey", cmpcompany_mids);
			deleteIds(mstCmpcompanyMDao, cmpcompany_mids);

			connection.commit(null);
			Log.e(TAG, "createOrUpdatecmpbrands 2 transation");

		} catch (SQLException e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdatecmpbrands 3 transation");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(" 竞品基础表消息表错误" + e.toString(), e);
		}
	}

	/**
	 * 工作计划计划指标状态基础信息
	 */
	private void createOrUpdatePlan(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新工作计划指标状态数据中...", defaultDelayMillis);
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdatePlan 1 transation");

			String json = "";
			List<MstPlanforuserM> planforuserMs = null;
			List<MstPlanTerminalM> MstPlanTerminalMs = null;
			List<MstPlanWeekforuserM> planWeekforuserMs = null;
			List<MstPlancheckInfo> plancheckInfos = null;
			List<MstCheckmiddleInfo> checkmiddleInfos = null;
			List<MstPlancollectionInfo> plancollectionInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_PLANFORUSER_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					planforuserMs = JsonUtil.parseList(json, MstPlanforuserM.class);
					createOrUpdate_MST_PLANFORUSER_M(planforuserMs);

				}

				if (key.toUpperCase().indexOf("MST_PLANTERMINAL_M") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					// FileUtil.savFileFike(json, "down_MST_PLANTERMINAL_M");
					MstPlanTerminalMs = JsonUtil.parseList(json, MstPlanTerminalM.class);
					// createOrUpdate_MST_PLANFORUSER_M(planforuserMs);
					updateData(MstPlanTerminalMDao, MstPlanTerminalMs);
				}

				if (key.toUpperCase().indexOf("MST_PLANWEEKFORUSER_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					// FileUtil.writeTxt(json,
					// FileUtil.getSDPath()+"/MST_PLANWEEKFORUSER_M_ZIP.txt");
					planWeekforuserMs = JsonUtil.parseList(json, MstPlanWeekforuserM.class);
					updateData(mstPlanWeekforuserMDao, planWeekforuserMs);
				} else if (key.toUpperCase().indexOf("MST_PLANCHECK_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					plancheckInfos = JsonUtil.parseList(json, MstPlancheckInfo.class);
					updateData(mstPlancheckInfoDao, plancheckInfos);

				} else if (key.toUpperCase().indexOf("MST_CHECKMIDDLE_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					checkmiddleInfos = JsonUtil.parseList(json, MstCheckmiddleInfo.class);
					updateData(mstCheckmiddleInfoDao, checkmiddleInfos);

				} else if (key.toUpperCase().indexOf("MST_PLANCOLLECTION_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					// FileUtil.savFileFike(json,
					// "MST_PLANCOLLECTION_INFO_ZIP");
					plancollectionInfos = JsonUtil.parseList(json, MstPlancollectionInfo.class);
					updateData(mstPlancollectionInfoDao, plancollectionInfos);
				}
			}

			// 周工作计划更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PLANWEEKFORUSER_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PLANWEEKFORUSER_M");
			}
			// 更新Kv表最后更新时间
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PLANFORUSER_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PLANFORUSER_M");
			}
			// 更新Kv表最后更新时间
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PLANTERMINAL_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PLANTERMINAL_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PLANCHECK_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PLANCHECK_INFO");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_CHECKMIDDLE_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_CHECKMIDDLE_INFO");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PLANCOLLECTION_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PLANCOLLECTION_INFO");
			}

			// 删除
			// 周计划
			String strMST_PLANWEEKFORUSER_MIDS = (String) responseHashMap.get("MST_PLANWEEKFORUSER_M_DELETEFLAG");
			List<String> planWeekforuser_mids = JsonUtil.parseList(strMST_PLANWEEKFORUSER_MIDS, String.class);
			// 日计划
			String strMST_PLANFORUSER_MIDS = (String) responseHashMap.get("MST_PLANFORUSER_M_DELETEFLAG");
			List<String> planforuser_mids = JsonUtil.parseList(strMST_PLANFORUSER_MIDS, String.class);

			String strMST_PLANCHECK_INFOIDS = (String) responseHashMap.get("MST_PLANCHECK_INFO_DELETEFLAG");
			List<String> plancheckInfoids = JsonUtil.parseList(strMST_PLANCHECK_INFOIDS, String.class);

			String strMST_CHECKMIDDLE_INFOIDS = (String) responseHashMap.get("MST_CHECKMIDDLE_INFO_DELETEFLAG");
			List<String> checkmiddle_infoids = JsonUtil.parseList(strMST_CHECKMIDDLE_INFOIDS, String.class);

			String strMST_PLANCOLLECTION_INFOIDS = (String) responseHashMap.get("MST_PLANCOLLECTION_INFO_DELETEFLAG");
			List<String> plancollection_infoids = JsonUtil.parseList(strMST_PLANCOLLECTION_INFOIDS, String.class);

			deleteIds(mstPlancollectionInfoDao, plancollection_infoids);
			deleteIds(mstCheckmiddleInfoDao, checkmiddle_infoids);
			deleteIds(mstPlancheckInfoDao, plancheckInfoids);
			// 删除mstPlanforuserM字表
			deleteInids(database, "MST_PLANROUTE_INFO", "plankey", planforuser_mids);
			deleteInids(database, "MST_PLANCHECK_INFO", "plankey", planforuser_mids);
			deleteIds(mstPlanforuserMDao, planforuser_mids);// plankey
			// 周工作计划
			deleteIds(mstPlanWeekforuserMDao, planWeekforuser_mids);// plankey

			connection.commit(null);
			Log.e(TAG, "createOrUpdatePlan 2 transation");

		} catch (SQLException e) {
			try {
				Log.e(TAG, "createOrUpdatePlan 3 transation", e);
				connection.rollback(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("计划指标状态基础信息表错误" + e.toString());
		}
	}

	/**
	 * MST_PLANFORUSER_M(人员计划主表)
	 * 
	 * @param planforuserMs
	 * @return
	 * @throws SQLException
	 */
	private void createOrUpdate_MST_PLANFORUSER_M(List<MstPlanforuserM> planforuserMs) throws SQLException {
		if (CheckUtil.IsEmpty(planforuserMs))
			return;

		for (MstPlanforuserM planforuserM : planforuserMs) {
			planforuserM.setPadisconsistent("1");
			String planstatus = planforuserM.getPlanstatus();
			if ("0".equals(planstatus)) {
				// 上传前为0，成功后服务器那边没改成4， 再次下载一定要改成4 要不又显示为未同步
				planforuserM.setPlanstatus("4");//
			}
			mstPlanforuserMDao.createOrUpdate(planforuserM);
		}

	}

	/**
	 * PAD_CHECKTYPE_M(PAD端采集用指标主表) 模板需要修改治无效
	 * PAD_CHECKACCOMPLISH_INFO(各类指标状态达成设置) PAD_CHECKPRO_INFO(PAD端指标关联产品表)
	 * 模板根据渠道号删除
	 * 
	 * PAD_CHECKSTATUS_INFO(指标选项值信息表) 全表
	 * 
	 * @param responseHashMap
	 */
	private void createOrUpdatePadChecktype(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新采集用指标数据中...", defaultDelayMillis);
		Log.e(TAG, "createOrUpdatePadChecktype");
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdatePadChecktype 1 transation");
			// 配合接口写成循环从map中取
			String strPAD_CHECKSTATUS_INFO = "";
			String strPAD_CHECKTYPE_M = "";
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("PAD_CHECKSTATUS_INFO_ZIP") != -1) {
					if (zipFlag) {
						strPAD_CHECKSTATUS_INFO = ZipUtil.readFile(zipPath + key + ".txt");
						// FileUtil.savFileFike("1"+strPAD_CHECKSTATUS_INFO,
						// "strPAD_CHECKSTATUS_INFO");
					} else {
						strPAD_CHECKSTATUS_INFO = (String) responseHashMap.get(key);
						// FileUtil.savFileFike("2"+strPAD_CHECKSTATUS_INFO,
						// "strPAD_CHECKSTATUS_INFO");
					}
					if (strPAD_CHECKSTATUS_INFO != null && !"".equals(strPAD_CHECKSTATUS_INFO.trim())) {
						List<PadCheckstatusInfo> padCheckstatusInfos = JsonUtil.parseList(strPAD_CHECKSTATUS_INFO,
								PadCheckstatusInfo.class);
						// PAD_CHECKSTATUS_INFO(指标选项值信息表)
						if (padCheckstatusInfos != null && !padCheckstatusInfos.isEmpty()) {
							// 清空数据
							if (database.isOpen()) {
								database.execSQL("DELETE FROM PAD_CHECKSTATUS_INFO");

							}
							saveData(padCheckstatusInfoDao, padCheckstatusInfos, true);
						}
					}

				} else if (key.toUpperCase().indexOf("PAD_CHECKTYPE_M_ZIP") != -1) {
					if (zipFlag) {
						strPAD_CHECKTYPE_M = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						strPAD_CHECKTYPE_M = (String) responseHashMap.get(key);
					}
					FileUtil.writeTxt(strPAD_CHECKTYPE_M, FileUtil.getSDPath()+"/strPAD_CHECKTYPE_M123.txt");  
					// FileUtil.savFileFike(strPAD_CHECKTYPE_M,
					// "strPAD_CHECKTYPE_M");
					if (strPAD_CHECKTYPE_M != null && !"".equals(strPAD_CHECKTYPE_M.trim())) {
						Map<String, Object> qudaoMap = JsonUtil.parseMap(strPAD_CHECKTYPE_M);// 有数

						Set<Entry<String, Object>> qudaoEntrySet = qudaoMap.entrySet();

						Iterator<Entry<String, Object>> iterator = qudaoEntrySet.iterator();

						while (iterator.hasNext()) {
							Entry<String, Object> next = iterator.next();
							String minorchannel = next.getKey();// 取出所有渠道号
							Log.i(TAG, "渠道号码" + minorchannel);
							// 取出表数据更新数据
							String childTableStr = (String) next.getValue();
							Map<String, Object> qudaoChilidTableMaps = JsonUtil.parseMap(childTableStr);
							String deleteFlag = (String) qudaoChilidTableMaps.get("DELETEFLAG");
							Log.e(TAG, "deleteFlag=" + deleteFlag);
							if ("1".equals(deleteFlag)) {// web端写死了恒等于1
								// 根据渠道号删除数据
								String sql_delete_PAD_CHECKPRO_INFO = "delete from PAD_CHECKPRO_INFO where minorchannel = ?";
								String sql_delete_PAD_CHECKACCOMPLISH_INFO = "delete from PAD_CHECKACCOMPLISH_INFO where minorchannel = ?";
								String sql_delete_PAD_CHECKTYPE_M = "delete from PAD_CHECKTYPE_M where minorchannel = ?";

								String deleteArgs[] = { minorchannel };
								if (database.isOpen()) {
									database.execSQL(sql_delete_PAD_CHECKPRO_INFO, deleteArgs);
									database.execSQL(sql_delete_PAD_CHECKACCOMPLISH_INFO, deleteArgs);
									database.execSQL(sql_delete_PAD_CHECKTYPE_M, deleteArgs);
								}

							}

							String childPAD_CHECKTYPE_M = (String) qudaoChilidTableMaps.get("PAD_CHECKTYPE_M");
							List<PadChecktypeM> padChecktypeMs = JsonUtil.parseList(childPAD_CHECKTYPE_M,
									PadChecktypeM.class);
							String strPAD_CHECKACCOMPLISH_INFO = (String) qudaoChilidTableMaps
									.get("PAD_CHECKACCOMPLISH_INFO");

							List<PadCheckaccomplishInfo> padCheckaccomplishInfos = JsonUtil.parseList(
									strPAD_CHECKACCOMPLISH_INFO, PadCheckaccomplishInfo.class);
							// FileUtil.savFileFike(strPAD_CHECKACCOMPLISH_INFO,
							// "strPAD_CHECKACCOMPLISH_INFO");

							String strPAD_CHECKPRO_INFO = (String) qudaoChilidTableMaps.get("PAD_CHECKPRO_INFO");
							List<PadCheckproInfo> padCheckproInfos = JsonUtil.parseList(strPAD_CHECKPRO_INFO,
									PadCheckproInfo.class);

							// 更新主表
							if (padChecktypeMs != null && !padChecktypeMs.isEmpty()) {
								// updateData(padChecktypeMDao, padChecktypeMs);
								saveData(padChecktypeMDao, padChecktypeMs, true);
							}

							// PAD_CHECKACCOMPLISH_INFO(各类指标状态达成设置)
							if (padCheckaccomplishInfos != null && !padCheckaccomplishInfos.isEmpty()) {
								// updateData(padCheckaccomplishInfoDao,
								// padCheckaccomplishInfos);
								saveData(padCheckaccomplishInfoDao, padCheckaccomplishInfos, true);
							}
							if (padCheckproInfos != null && !padCheckproInfos.isEmpty()) {
								// PAD_CHECKPRO_INFO(PAD端指标关联产品表)
								// updateData(padCheckproInfoDao,
								// padCheckproInfos);
								saveData(padCheckproInfoDao, padCheckproInfos, true);
							}

						}

					}

				}

			}
			String updateTime = (String) responseHashMap.get("PAD_CHECKTYPE_M_DATE");
			// 更新KV表
			List<PadChecktypeM> padChecktypeMAll = padChecktypeMDao.queryForAll();
			Map<String, String> map = new HashMap<String, String>();
			for (PadChecktypeM padChecktypeM : padChecktypeMAll) {
				String tempkey = padChecktypeM.getTempkey();
				String minorchannel = padChecktypeM.getMinorchannel();
				map.put(minorchannel, tempkey);
			}
			String sql = "UPDATE MST_SYNCKV_M SET updatetime=?, synctime= ? ,remarks = ? where tablename = ?";

			if (database.isOpen()) {
				database.execSQL(sql, new String[] { updateTime, synctime, JsonUtil.toJson(map), "PAD_CHECKTYPE_M" });
			}

			// if (zipFlag) {
			// strPAD_CHECKSTATUS_INFO = ZipUtil.readFile(zipPath +
			// "PAD_CHECKSTATUS_INFO_ZIP.txt");
			// } else {
			// strPAD_CHECKSTATUS_INFO = (String)
			// responseHashMap.get("PAD_CHECKSTATUS_INFO_ZIP");
			// }
			// if (strPAD_CHECKSTATUS_INFO != null &&
			// !"".equals(strPAD_CHECKSTATUS_INFO.trim())) {
			// List<PadCheckstatusInfo> padCheckstatusInfos =
			// JsonUtil.parseList(strPAD_CHECKSTATUS_INFO,
			// PadCheckstatusInfo.class);
			// // PAD_CHECKSTATUS_INFO(指标选项值信息表)
			// if (padCheckstatusInfos != null &&
			// !padCheckstatusInfos.isEmpty()) {
			// //清空数据
			// if (database.isOpen()) {
			// database.execSQL("DELETE FROM PAD_CHECKSTATUS_INFO");
			//
			// }
			// updateData(padCheckstatusInfoDao, padCheckstatusInfos);
			// }
			// }
			// String strPAD_CHECKTYPE_M = "";
			// if (zipFlag) {
			// strPAD_CHECKTYPE_M = ZipUtil.readFile(zipPath +
			// "PAD_CHECKTYPE_M_ZIP.txt");
			// } else {
			// strPAD_CHECKTYPE_M = (String)
			// responseHashMap.get("PAD_CHECKTYPE_M_ZIP");
			// }
			//
			// String updateTime = (String)
			// responseHashMap.get("PAD_CHECKTYPE_M_DATE");
			// Log.i(TAG, "updateTime =" + updateTime);
			// if (strPAD_CHECKTYPE_M != null &&
			// !"".equals(strPAD_CHECKTYPE_M.trim())) {
			//
			// Map<String, Object> qudaoMap =
			// JsonUtil.parseMap(strPAD_CHECKTYPE_M);//有数
			//
			// Set<Entry<String, Object>> qudaoEntrySet = qudaoMap.entrySet();
			//
			// Iterator<Entry<String, Object>> iterator =
			// qudaoEntrySet.iterator();
			//
			// while (iterator.hasNext()) {
			// Entry<String, Object> next = iterator.next();
			// String minorchannel = next.getKey();//取出所有渠道号
			// Log.i(TAG, "渠道号码" + minorchannel);
			// //取出表数据更新数据
			// String childTableStr = (String) next.getValue();
			// Map<String, Object> qudaoChilidTableMaps =
			// JsonUtil.parseMap(childTableStr);
			// String deleteFlag = (String)
			// qudaoChilidTableMaps.get("DELETEFLAG");
			// Log.e(TAG, "deleteFlag=" + deleteFlag);
			// if ("1".equals(deleteFlag)) {
			// //根据渠道号删除数据
			// String sql_delete_PAD_CHECKPRO_INFO =
			// "delete from PAD_CHECKPRO_INFO where minorchannel = ?";
			// String sql_delete_PAD_CHECKACCOMPLISH_INFO =
			// "delete from PAD_CHECKACCOMPLISH_INFO where minorchannel = ?";
			// String sql_delete_PAD_CHECKTYPE_M =
			// "delete from PAD_CHECKTYPE_M where minorchannel = ?";
			//
			// String deleteArgs[] = { minorchannel };
			// if (database.isOpen()) {
			// database.execSQL(sql_delete_PAD_CHECKPRO_INFO, deleteArgs);
			// database.execSQL(sql_delete_PAD_CHECKACCOMPLISH_INFO,
			// deleteArgs);
			// database.execSQL(sql_delete_PAD_CHECKTYPE_M, deleteArgs);
			// }
			//
			// }
			//
			// String childPAD_CHECKTYPE_M = (String)
			// qudaoChilidTableMaps.get("PAD_CHECKTYPE_M");
			// List<PadChecktypeM> padChecktypeMs =
			// JsonUtil.parseList(childPAD_CHECKTYPE_M, PadChecktypeM.class);
			// String strPAD_CHECKACCOMPLISH_INFO = (String)
			// qudaoChilidTableMaps.get("PAD_CHECKACCOMPLISH_INFO");
			//
			// List<PadCheckaccomplishInfo> padCheckaccomplishInfos =
			// JsonUtil.parseList(strPAD_CHECKACCOMPLISH_INFO,
			// PadCheckaccomplishInfo.class);
			//
			// String strPAD_CHECKPRO_INFO = (String)
			// qudaoChilidTableMaps.get("PAD_CHECKPRO_INFO");
			// List<PadCheckproInfo> padCheckproInfos =
			// JsonUtil.parseList(strPAD_CHECKPRO_INFO, PadCheckproInfo.class);
			//
			// //更新主表
			// if (padChecktypeMs != null && !padChecktypeMs.isEmpty()) {
			// updateData(padChecktypeMDao, padChecktypeMs);
			//
			// }
			//
			// // PAD_CHECKACCOMPLISH_INFO(各类指标状态达成设置)
			// if (padCheckaccomplishInfos != null &&
			// !padCheckaccomplishInfos.isEmpty()) {
			// updateData(padCheckaccomplishInfoDao, padCheckaccomplishInfos);
			//
			// }
			// if (padCheckproInfos != null && !padCheckproInfos.isEmpty()) {
			// // PAD_CHECKPRO_INFO(PAD端指标关联产品表)
			// updateData(padCheckproInfoDao, padCheckproInfos);
			// }
			//
			// }
			//
			// // 更新KV表
			// List<PadChecktypeM> padChecktypeMAll =
			// padChecktypeMDao.queryForAll();
			// Map<String, String> map = new HashMap<String, String>();
			// for (PadChecktypeM padChecktypeM : padChecktypeMAll) {
			// String tempkey = padChecktypeM.getTempkey();
			// String minorchannel = padChecktypeM.getMinorchannel();
			// map.put(minorchannel, tempkey);
			// }
			// String sql =
			// "UPDATE MST_SYNCKV_M SET updatetime=?, synctime= ? ,remarks = ? where tablename = ?";
			//
			// if (database.isOpen()) {
			// database.execSQL(sql, new String[] { updateTime, synctime,
			// JsonUtil.toJson(map), "PAD_CHECKTYPE_M" });
			// }
			// }
			connection.commit(null);
			Log.e(TAG, "createOrUpdatePadChecktype 2 transation");
		} catch (SQLException e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdatePadChecktype 3 transation", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("PAD端采集用表错误" + e.toString());
		}
	}

	/**
	 * PAD_PLANTEMPCHECK_M(PAD端计划模板指标主表)
	 * PAD_PLANTEMPCOLLECTION_INFO(PAD端计划模板指标对应采集项关联信息表)
	 * 
	 */
	private void createOrUpdatePadPlantempcheck(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新计划模板数据中...", defaultDelayMillis);
		AndroidDatabaseConnection connection = null;
		Log.e(TAG, "createOrUpdatePadPlantempcheck");
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdatePadPlantempcheck 1 transation");
			String strPAD_PLANTEMPCHECK_M = "";
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("PAD_PLANTEMPCHECK_M_ZIP") != -1) {
					if (zipFlag) {
						strPAD_PLANTEMPCHECK_M = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						strPAD_PLANTEMPCHECK_M = (String) responseHashMap.get(key);
					}
					if (strPAD_PLANTEMPCHECK_M != null && !"".equals(strPAD_PLANTEMPCHECK_M.trim())) {

						Map<String, Object> parentMap = JsonUtil.parseMap(strPAD_PLANTEMPCHECK_M);// 有数
						if (parentMap != null) {
							Log.i(TAG, "PAD_PLANTEMPCHECK_M无数据");

							String childPAD_PLANTEMPCHECK_M = (String) parentMap.get("PAD_PLANTEMPCHECK_M");
							String deleteflag = (String) parentMap.get("DELETEFLAG");
							// 12*7=84000
							Log.e(TAG, "PAD_PLANTEMPCOLLECTION_INFO DELETEFLAG" + deleteflag);

							List<PadPlantempcheckM> plantempcheckMs = JsonUtil.parseList(childPAD_PLANTEMPCHECK_M,
									PadPlantempcheckM.class);
							List<String> delFlagLst = JsonUtil.parseList(deleteflag, String.class);
							boolean isDelAllFlag = false;
							// 删除数据
							if (!CheckUtil.IsEmpty(delFlagLst)) {
								if (ConstValues.FLAG_1.equals(delFlagLst.get(0))) {
									database.execSQL("delete from PAD_PLANTEMPCHECK_M ");
									database.execSQL("delete from PAD_PLANTEMPCOLLECTION_INFO ");
									isDelAllFlag = true;
								}
							}

							if (plantempcheckMs != null && !plantempcheckMs.isEmpty()) {
								// 插入新数据
								// PAD_PLANTEMPCHECK_M(PAD端计划模板指标主表)
								if (!CheckUtil.IsEmpty(plantempcheckMs)) {
									if (isDelAllFlag) {
										saveData(padPlantempcheckMDao, plantempcheckMs, true);
									} else {
										updateData(padPlantempcheckMDao, plantempcheckMs);
									}
								}
								String updateTime = (String) responseHashMap.get("PAD_PLANTEMPCHECK_M_DATE");
								// 更新KV表
								// 多条数据 但是所有的数据 PLANTEMPKEY 是相同的
								String remarks = plantempcheckMs.get(0).getPlantempkey();
								Log.e(TAG, "PAD_PLANTEMPCHECK_M:" + "updatetime:" + updateTime + "remarks:" + remarks);
								String sql = "UPDATE MST_SYNCKV_M SET updatetime= ?,synctime= ?,remarks= ? where tablename= ?";

								if (database.isOpen()) {
									database.execSQL(sql, new String[] { updateTime, synctime, remarks,
											"PAD_PLANTEMPCHECK_M" });
								}
							}

							// PAD_PLANTEMPCOLLECTION_INFO(PAD端计划模板指标对应采集项关联信息表)
							String strPAD_PLANTEMPCOLLECTION_INFO = (String) parentMap
									.get("PAD_PLANTEMPCOLLECTION_INFO");
							List<PadPlantempcollectionInfo> plantempcollectionInfos = JsonUtil.parseList(
									strPAD_PLANTEMPCOLLECTION_INFO, PadPlantempcollectionInfo.class);
							if (plantempcollectionInfos != null && !plantempcollectionInfos.isEmpty()) {
								if (isDelAllFlag) {
									saveData(padPlantempcollectionInfoDao, plantempcollectionInfos, true);
								} else {
									updateData(padPlantempcollectionInfoDao, plantempcollectionInfos);
								}
							}

						}
					}

				}

				// String strPAD_PLANTEMPCHECK_M = "";
				// if (zipFlag) {
				// strPAD_PLANTEMPCHECK_M = ZipUtil.readFile(zipPath +
				// "PAD_PLANTEMPCHECK_M_ZIP.txt");
				// } else {
				// strPAD_PLANTEMPCHECK_M = (String)
				// responseHashMap.get("PAD_PLANTEMPCHECK_M_ZIP");
				// }
				// String updateTime = (String)
				// responseHashMap.get("PAD_PLANTEMPCHECK_M_DATE");
				//
				// if (strPAD_PLANTEMPCHECK_M != null &&
				// !"".equals(strPAD_PLANTEMPCHECK_M.trim())) {
				//
				// Map<String, Object> parentMap =
				// JsonUtil.parseMap(strPAD_PLANTEMPCHECK_M);//有数
				// if (parentMap != null) {
				// Log.i(TAG, "PAD_PLANTEMPCHECK_M无数据");
				//
				// String childPAD_PLANTEMPCHECK_M = (String)
				// parentMap.get("PAD_PLANTEMPCHECK_M");
				// String deleteflag = (String) parentMap.get("DELETEFLAG");
				// //12*7=84000
				// Log.e(TAG, "PAD_PLANTEMPCOLLECTION_INFO DELETEFLAG" +
				// deleteflag);
				//
				// List<PadPlantempcheckM> plantempcheckMs =
				// JsonUtil.parseList(childPAD_PLANTEMPCHECK_M,
				// PadPlantempcheckM.class);
				// List<String> delFlagLst = JsonUtil.parseList(deleteflag,
				// String.class);
				//
				// // 删除数据
				// if (!CheckUtil.IsEmpty(delFlagLst)) {
				// // 部分删除
				// if (ConstValues.FLAG_0.equals(delFlagLst.get(0))) {
				// StringBuffer buffer = new StringBuffer();
				// buffer.append("delete from PAD_PLANTEMPCHECK_M where 1 != 1");
				// for (int i = 0; i < delFlagLst.size(); i++) {
				// buffer.append(" or CHECKKEY = '").append(delFlagLst.get(i)).append("' ");
				// }
				// database.execSQL(buffer.toString());
				//
				// } else if (ConstValues.FLAG_1.equals(delFlagLst.get(0))) {
				// database.execSQL("delete from PAD_PLANTEMPCHECK_M ");
				// database.execSQL("delete from PAD_PLANTEMPCOLLECTION_INFO ");
				// }
				// }
				//
				// if (plantempcheckMs != null && !plantempcheckMs.isEmpty()) {
				// if ("1".equals(deleteflag)) {
				//
				// Map<String, String> checkKeyMap = new HashMap<String,
				// String>();
				// for (PadPlantempcheckM plantempcheckM : plantempcheckMs) {
				// String checkkey = plantempcheckM.getCheckkey();
				// checkKeyMap.put(checkkey, checkkey);
				// }
				//
				// String sql_delete_PAD_PLANTEMPCOLLECTION_INFO =
				// "delete from PAD_PLANTEMPCOLLECTION_INFO where checkkey = '";
				// String sql_delete_PAD_PLANTEMPCHECK_M =
				// "delete from PAD_PLANTEMPCHECK_M where checkkey = '";
				// String sql_or = "' OR checkkey = '";
				// String sql_del_or = "OR checkkey = '";
				// StringBuilder sb_PAD_PLANTEMPCOLLECTION_INFO = new
				// StringBuilder(sql_delete_PAD_PLANTEMPCOLLECTION_INFO);
				// StringBuilder sb_PAD_PLANTEMPCHECK_M = new
				// StringBuilder(sql_delete_PAD_PLANTEMPCHECK_M);
				// Set<Entry<String, String>> entrySet = checkKeyMap.entrySet();
				// Iterator<Entry<String, String>> iterator =
				// entrySet.iterator();
				// while (iterator.hasNext()) {
				// Entry<String, String> next = iterator.next();
				// String checkkey = next.getKey();
				// sb_PAD_PLANTEMPCOLLECTION_INFO.append(checkkey).append(sql_or);
				// sb_PAD_PLANTEMPCHECK_M.append(checkkey).append(sql_or);
				// }
				// // 删除最后" OR checkkey ="
				// sb_PAD_PLANTEMPCOLLECTION_INFO.delete(sb_PAD_PLANTEMPCOLLECTION_INFO.length()
				// - sql_del_or.length(),
				// sb_PAD_PLANTEMPCOLLECTION_INFO.length());
				// sb_PAD_PLANTEMPCHECK_M.delete(sb_PAD_PLANTEMPCHECK_M.length()
				// - sql_del_or.length(), sb_PAD_PLANTEMPCHECK_M.length());
				// // 删除旧数据
				// if (database.isOpen()) {
				// Log.i(TAG, "exec 删除 PAD_PLANTEMPCOLLECTION_INFO" +
				// sb_PAD_PLANTEMPCOLLECTION_INFO.toString());
				// Log.i(TAG, "exec 删除 PAD_PLANTEMPCHECK_M" +
				// sb_PAD_PLANTEMPCHECK_M.toString());
				// database.execSQL(sb_PAD_PLANTEMPCOLLECTION_INFO.toString());
				// database.execSQL(sb_PAD_PLANTEMPCHECK_M.toString());
				// }
				// }
				// 插入新数据
				// PAD_PLANTEMPCHECK_M(PAD端计划模板指标主表)
				// if (!CheckUtil.IsEmpty(plantempcheckMs)) {
				// updateData(padPlantempcheckMDao, plantempcheckMs);
				// }
				//
				// // 更新KV表
				// // 多条数据 但是所有的数据 PLANTEMPKEY 是相同的
				// String remarks = plantempcheckMs.get(0).getPlantempkey();
				// Log.e(TAG, "PAD_PLANTEMPCHECK_M:" + "updatetime:" +
				// updateTime + "remarks:" + remarks);
				// String sql =
				// "UPDATE MST_SYNCKV_M SET updatetime= ?,synctime= ?,remarks= ? where tablename= ?";
				//
				// if (database.isOpen()) {
				// database.execSQL(sql, new String[] { updateTime, synctime,
				// remarks, "PAD_PLANTEMPCHECK_M" });
				// }
				// }
				//
				// // PAD_PLANTEMPCOLLECTION_INFO(PAD端计划模板指标对应采集项关联信息表)
				// String strPAD_PLANTEMPCOLLECTION_INFO = (String)
				// parentMap.get("PAD_PLANTEMPCOLLECTION_INFO");
				// List<PadPlantempcollectionInfo> plantempcollectionInfos =
				// JsonUtil.parseList(strPAD_PLANTEMPCOLLECTION_INFO,
				// PadPlantempcollectionInfo.class);
				// if (plantempcollectionInfos != null &&
				// !plantempcollectionInfos.isEmpty()) {
				// updateData(padPlantempcollectionInfoDao,
				// plantempcollectionInfos);
				// }

				// }
			}
			connection.commit(null);
			Log.e(TAG, "createOrUpdatePadPlantempcheck 2 transation 最后一个表完成 数据同步成功");
		} catch (SQLException e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdatePadPlantempcheck 3 transation", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("PAD计划模板表错误" + e.toString());
		}
	}

	/**
	 * 万能铺货率终端类型 MST_POWERFULTERMINAL_INFO
	 * 
	 * @param
	 */
	private void createOrUpdate_MST_POWERFULTERMINAL_INFO(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新万能铺货率终端数据中...", defaultDelayMillis);
		String strMstPowerfulterminalInfo = (String) responseHashMap.get("MST_POWERFULTERMINAL_INFO");
		String strMstPowerfulterminalInfoIDS = (String) responseHashMap.get("MST_POWERFULTERMINAL_INFO_DELETEFLAG");
		List<MstPowerfulterminalInfo> powerfulterminalInfos = JsonUtil.parseList(strMstPowerfulterminalInfo,
				MstPowerfulterminalInfo.class);
		List<String> powerfulterminalInfoids = JsonUtil.parseList(strMstPowerfulterminalInfoIDS, String.class);
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_POWERFULTERMINAL_INFO 1 transation");
			// do中...

			if (!CheckUtil.IsEmpty(powerfulterminalInfos)) {
				updateData(mstPowerfulterminalInfoDao, powerfulterminalInfos);
				// 更新kv表
				if (powerfulterminalInfos.get(0).getUpdatetime() != null) {
					String updatetime = DateUtil.formatDate(powerfulterminalInfos.get(0).getUpdatetime(),
							"yyyy-MM-dd HH:mm:ss");
					updateKVData(database, updatetime, "MST_POWERFULTERMINAL_INFO");
				}
			}
			deleteIds(mstPowerfulterminalInfoDao, powerfulterminalInfoids);

			connection.commit(null);
			Log.e(TAG, "createOrUpdate_MST_POWERFULTERMINAL_INFO 2 transation");

		} catch (Exception e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdate_MST_POWERFULTERMINAL_INFO 3 transation", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新 终端万能铺货率表出现错误：" + e.toString());
		}
	}

	/**
	 * 万能铺货率渠道类型 MST_POWERFULCHANNEL_INFO
	 * 
	 */
	private void createOrUpdate_MST_POWERFULCHANNEL_INFO(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新万能铺货率渠道数据中...", defaultDelayMillis);
		String strMST_POWERFULCHANNEL_INFO = (String) responseHashMap.get("MST_POWERFULCHANNEL_INFO");
		String strMST_POWERFULCHANNEL_INFOIDS = (String) responseHashMap.get("MST_POWERFULCHANNEL_INFO_DELETEFLAG");
		List<MstPowerfulchannelInfo> powerfulchannelInfos = JsonUtil.parseList(strMST_POWERFULCHANNEL_INFO,
				MstPowerfulchannelInfo.class);
		List<String> powerfulchannelInfoids = JsonUtil.parseList(strMST_POWERFULCHANNEL_INFOIDS, String.class);

		Log.e(TAG, "createOrUpdate_MST_POWERFULCHANNEL_INFO");
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_POWERFULCHANNEL_INFO 1 transation");
			// do中...
			if (!CheckUtil.IsEmpty(powerfulchannelInfos)) {
				updateData(mstPowerfulchannelInfoDao, powerfulchannelInfos);
				// 更新kv表
				if (powerfulchannelInfos.get(0).getUpdatetime() != null) {
					String updatetime = DateUtil.formatDate(powerfulchannelInfos.get(0).getUpdatetime(),
							"yyyy-MM-dd HH:mm:ss");
					updateKVData(database, updatetime, "MST_POWERFULCHANNEL_INFO");
				}
			}
			deleteIds(mstPowerfulchannelInfoDao, powerfulchannelInfoids);

			connection.commit(null);
			Log.e(TAG, "createOrUpdate_MST_POWERFULCHANNEL_INFO 2 transation");

		} catch (Exception e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdate_MST_POWERFULCHANNEL_INFO 3 transation", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException("更新 渠道万能铺货率表出现错误：" + e.toString());
		}
	}

	/**
	 * 价格明细表 MST_PRICE_M MstPriceM MST_PRICEDETAILS_INFO MstPricedetailsInfo
	 * 
	 * @param responseHashMap
	 *            .
	 */
	private void createOrUpdatePriceDetails(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新价格明细数据中...", defaultDelayMillis);
		Log.e(TAG, "createOrUpdatePriceDetails");
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdatePriceDetails 1 transation");

			String json = "";
			List<MstPriceM> priceMs = null;
			List<MstPricedetailsInfo> priceDetailsInfos = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_PRICE_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					priceMs = JsonUtil.parseList(json, MstPriceM.class);
					updateData(mstPriceMDao, priceMs);

				} else if (key.toUpperCase().indexOf("MST_PRICEDETAILS_INFO_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					priceDetailsInfos = JsonUtil.parseList(json, MstPricedetailsInfo.class);
					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstPricedetailsInfoDao, priceDetailsInfos);
					} else {
						updateData(mstPricedetailsInfoDao, priceDetailsInfos);
					}

				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PRICE_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PRICE_M");
			}
			updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PRICEDETAILS_INFO_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PRICEDETAILS_INFO");
			}

			// 删除
			String strMST_PRICE_MIDS = (String) responseHashMap.get("MST_PRICE_M_DELETEFLAG");
			List<String> priceMids = JsonUtil.parseList(strMST_PRICE_MIDS, String.class);

			String strMST_MstPricedetailsInfoIDS = (String) responseHashMap.get("MST_PRICEDETAILS_INFO_DELETEFLAG");
			List<String> priceDetailsInfoids = JsonUtil.parseList(strMST_MstPricedetailsInfoIDS, String.class);

			deleteInids(database, "MST_PRICEDETAILS_INFO", "pricekey", priceMids);// 删除字表
			deleteIds(mstPriceMDao, priceMids);

			deleteIds(mstPricedetailsInfoDao, priceDetailsInfoids);

			connection.commit(null);
			Log.e(TAG, "createOrUpdatePriceDetails 2 transation");

		} catch (SQLException e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdatePriceDetails 3 transation", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(" 更新价格表出错" + e.toString());
		}
	}

	/**
	 * 图片类型表更新 注意本张表并不是差量插入,而是先全部删除,再全部插入 MST_PICTYPE_M 因为数量少
	 * 
	 * @param responseHashMap
	 *            .
	 */
	private void createOrUpdate_MST_PICTYPE_M(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新图片类型数据中...", defaultDelayMillis);
		Log.e(TAG, "createOrUpdateMST_PICTYPE_M");
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_PICTYPE_M 1 transation");

			String json = "";
			List<MstPictypeM> mstpictypeMs = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_PICTYPE_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					// 先将之前此表中的数据全部删除, 在做插入记录操作
					StringBuffer buffer = new StringBuffer();
					buffer.append("DELETE FROM MST_PICTYPE_M WHERE status= ? ");
					database.execSQL(buffer.toString(), new String[] { "0" });

					// 更新 插入
					mstpictypeMs = JsonUtil.parseList(json, MstPictypeM.class);
					updateData(mstpictypeMDao, mstpictypeMs);

				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_PICTYPE_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_PICTYPE_M");
			}

			connection.commit(null);
			Log.e(TAG, "createOrUpdatePriceDetails 2 transation");

		} catch (SQLException e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdatePriceDetails 3 transation", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(" 更新图片类型表出错" + e.toString());
		}
	}
	
	/**
	 * 产品组合是否达标 MST_GROUPPRODUCT_M
	 * 
	 * @param responseHashMap
	 * @param adatasyn
	 */
	private void createOrUpdate_MST_GROUPPRODUCT_M(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新产品组合是否达标数据中...", defaultDelayMillis);
		Log.e(TAG, "createOrUpdateMST_GROUPPRODUCT_M");
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_GROUPPRODUCT_M 1 transation");

			String json = "";
			List<MstGroupproductM> mstGroupproductM = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_GROUPPRODUCT_M2_ZIP1") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}
					//FileUtil.writeTxt(json, FileUtil.getSDPath()+"/MST_GROUPPRODUCT_M2_ZIP1108.txt");
					// 先将之前此表中的数据全部删除, 在做插入记录操作
					StringBuffer buffer = new StringBuffer();
					buffer.append("DELETE FROM MST_GROUPPRODUCT_M WHERE padisconsistent= ? ");// 把已上传的删掉
					database.execSQL(buffer.toString(), new String[] { "1" });

					// 更新 插入
					mstGroupproductM = JsonUtil.parseList(json, MstGroupproductM.class);
					updateData(mstGroupproductMDao, mstGroupproductM);

				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_GROUPPRODUCT_M2_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_GROUPPRODUCT_M");
			}

			connection.commit(null);
			Log.e(TAG, "createOrUpdate_MST_GROUPPRODUCT_M 2 transation");

		} catch (SQLException e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdate_MST_GROUPPRODUCT_M 3 transation", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(" 更新产品组合是否达标表出错" + e.toString());
		}
	}

	/**
	 * 经销商开发表更新 MST_AGENCYKF_M
	 * 
	 * @param responseHashMap
	 *            .
	 */
	private void createOrUpdate_MST_AGENCYKF_M(Map<String, Object> responseHashMap, DataSynStc adatasyn) {
		sendProgressMessage("更新经销商开发数据中...", defaultDelayMillis);
		Log.e(TAG, "createOrUpdate_MST_AGENCYKF_M");
		AndroidDatabaseConnection connection = null;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			connection = new AndroidDatabaseConnection(database, true);
			connection.setAutoCommit(false);
			Log.e(TAG, "createOrUpdate_MST_PICTYPE_M 1 transation");

			String json = "";
			List<MstAgencyKFM> mstAgencyKFMs = null;
			for (String key : responseHashMap.keySet()) {
				if (key.toUpperCase().indexOf("MST_AGENCYKF_M_ZIP") != -1) {
					if (zipFlag) {
						json = ZipUtil.readFile(zipPath + key + ".txt");
					} else {
						json = (String) responseHashMap.get(key);
					}

					// 更新 插入
					mstAgencyKFMs = JsonUtil.parseList(json, MstAgencyKFM.class);

					if (CheckUtil.isBlankOrNull(adatasyn.getStrLastTime())) {
						saveData(mstAgencyKFMDao, mstAgencyKFMs);
					} else {
						updateData(mstAgencyKFMDao, mstAgencyKFMs);
					}

				}
			}

			// 更新Kv表最后更新时间
			String updatetime = FunUtil.isBlankOrNullTo(responseHashMap.get("MST_AGENCYKF_M_DATE"), "");
			if (!CheckUtil.isBlankOrNull(updatetime)) {
				// 更新kv表
				updateKVData(database, updatetime, "MST_AGENCYKF_M");
			}

			// 删除已删除数据

			connection.commit(null);
			Log.e(TAG, "createOrUpdatePriceDetails 2 transation");

		} catch (SQLException e) {
			try {
				connection.rollback(null);
				Log.e(TAG, "createOrUpdatePriceDetails 3 transation", e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(" 更新图片类型表出错" + e.toString());
		}
	}

	// 下载Zip数据并解压
	private String downZip() {
		String zipFilePath = "";
		try {
			String filePath = PropertiesUtil.getProperties("platform_ip").replace("IntfServlet", "") + "syncDate/";
			//String fileName = ConstValues.loginSession.getUserCode() + ".zip";
			String fileName = PrefUtils.getString(context, "userCode", "") + ".zip";
			URL url = new URL(filePath + "/" + fileName);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			int length = conn.getContentLength();
			InputStream is = conn.getInputStream();

			File file = context.getFilesDir();
			if (!file.exists()) {
				file.mkdir();
			}
			zipFilePath = file.getPath() + "/" + fileName;
			File zipFile = new File(zipFilePath);
			FileOutputStream fos = new FileOutputStream(zipFile);

			byte buf[] = new byte[1024];
			int num = 0;
			while ((num = is.read(buf)) != -1) {
				fos.write(buf, 0, num);
			}

			fos.close();
			is.close();
		} catch (MalformedURLException e) {
			Log.e(TAG, "", e);
		} catch (IOException e) {
			Log.e(TAG, "", e);
		}

		try {
			ZipUtil.unzip(zipFilePath, zipFilePath.replace(".zip", ""));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return zipFilePath.replace(".zip", "") + "/";
	}

	/**
	 * 向界面发送失败信息
	 */
	private void sendFailureMessage() {
		Message message = new Message();
		message.obj = "网络信号有点差，是否继续下载？";
		message.what = DownLoadDataProgressActivity.SYNDATA_RESULT_Failure;
		handler.sendMessage(message);
	}

	/**
	 * 向界面发送进度条信息
	 */
	private synchronized void sendProgressMessage(String tableName) {
		Log.e("tablename", tableName);
		for (MstSynckvM mstSynckv : synTables) {
			if (mstSynckv.getTablename().equals(tableName)) {
				// 根据表名找出是什么表
				tableName = mstSynckv.getTableDesc();
			}
		}
		Log.d("DownLoadDataService", "progress:" + progress);

		DbtLog.write("progress:" + progress + ",synTables.size():" + synTables.size());
		if (progress == synTables.size()) {
			DownLoadDataService.saveDatasynTime(context);
		}
		Message msg = new Message();
		Bundle bundler = new Bundle();
		bundler.putInt("progress", progress++);
		FileUtil.writeTxt(tableName+"", FileUtil.getSDPath()+"/saveDatasynTime123456.txt");
		bundler.putInt("result", 0);
		bundler.putString("progressStr", tableName);

		msg.setData(bundler);
		msg.what = DownLoadDataProgressActivity.SYNDATA_PROGRESS;
		handler.sendMessageDelayed(msg, defaultDelayMillis);
	}

	/**
	 * 向界面发送手动同步成功的信息
	 */
	private void sendSuccessMessage() {
		Message message = new Message();
		message.what = DownLoadDataProgressActivity.SYNDATA_RESULT_Success;// 发送结束标志
		message.obj = "同步数据完成";
		handler.sendMessage(message);
	}

	/**
	 * 
	 * 项目名称：营销移动智能工作平台 </br> 文件名：DownLoadDataService.java</br> 作者：薛敬飞 </br>
	 * 创建时间：2014-8-26</br> 功能描述:多线程下载 </br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
	 * 修改人 修改版本</br>
	 */
	private class DownLoadPool {
		// 建立线程池。
		private ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		private List<DataSynStc> datasyns;
		private Handler handler;
		private Context context;
		long netSumTime = 0;
		long dbSumTime = 0;
		long jsonSumTime = 0;

		/**
		 * @param datasyns
		 * @param handler
		 * @param context
		 */
		public DownLoadPool(List<DataSynStc> datasyns, Handler handler, Context context) {
			// TODO Auto-generated constructor stub
			this.datasyns = datasyns;
			this.handler = handler;
			this.context = context;
		}

		public void downLoad() {
			// 更新前时间
			final Long beforeTime = new Date().getTime();
			for (final DataSynStc adatasyn : datasyns) {
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						short exceptionCount = 0; // 连接次数 连接失败后连接3次
						List<DataSynStc> datasyns = new ArrayList<DataSynStc>();
						datasyns.add(adatasyn);
						while (exceptionCount <= 2) {
							Map<String, String> requestHashMap = new HashMap<String, String>();
							LoginSession loginSession = ConstValues.loginSession;
							if (loginSession.getUserCode() == null || loginSession.getDisId() == null
									|| loginSession.getGridId() == null) {
								// 如果静态变量丢失重新从文件中获取（程序killed）
								loginSession = new LoginService().getLoginSession(context);
							}
							requestHashMap.put("userId", loginSession.getUserCode());
							requestHashMap.put("areaId", loginSession.getDisId());
							requestHashMap.put("gridKey", loginSession.getGridId());
							requestHashMap.put("DataSynStc", JsonUtil.toJson(datasyns));
							String json = JsonUtil.toJson(requestHashMap);
							FileUtil.writeTxt(json, FileUtil.getSDPath()+"/requestHashMap12132.txt"); 
							if (json.contains("PAD_CHECKTYPE_M")) {
								// FileUtil.savFileFike(json,
								// "PAD_CHECKTYPE_M");
							}
							// Log.i(TAG, "发送出去：" + json);
							sendProgressMessage("请求获取数据中...", 1300);
							HttpUtil httpUtil = new HttpUtil(30 * 60 * 1000);// 30分钟超时时间
							httpUtil.configResponseTextCharset("ISO-8859-1");
							final Message message = new Message();
							long startTime = new Date().getTime();
							DbtLog.write("==============================" + adatasyn.getStrTableName()
									+ "============================================");
							DbtLog.write("请求开始");
							Log.d(TAG, adatasyn.getStrTableName());
							ResponseInfo<String> responseInfo ;//= httpUtil.sendSynRequest("opt_get_dates2", json);
							if (json.contains("MST_CHECKEXERECORD_INFO")) {
								responseInfo = httpUtil.sendSynRequest("opt_get_dates2", json);
							}else{
								responseInfo = httpUtil.sendSynRequest("opt_get_dates2", json);
							}
							DbtLog.write("responseInfo==null:" + (responseInfo == null));
							DbtLog.write("请求结束");
							long subTime = new Date().getTime() - startTime;
							netSumTime += subTime;
							DbtLog.write("====请求耗时:" + subTime);
							if (responseInfo != null) {
								DbtLog.write(adatasyn.getStrTableName() + getMemoryInfo());
								// 连接成功
								// DbtLog.write(adatasyn.getStrTableName()+"====请求结束:responseInfo.result==null:"+(responseInfo.result==null));

								startTime = new Date().getTime();
								DbtLog.write("解析开始");
								ResponseStructBean resObj = null;
								if (adatasyn.getStrTableName().equals("MST_TERMINALINFO_M")) {//MST_CHECKEXERECORD_INFO //MST_VISTPRODUCT_INFO //MST_TERMINALINFO_M
									/*resObj =HttpUtil.parseRes(adatasyn.getStrTableName()+"*************",responseInfo.result);
									// 解压json -> 并转成对象  // 记得删除*/
									//resObj = HttpUtil.parseMstCheckexerecordInfo(responseInfo.result);
									
									
								} 
								resObj = HttpUtil.parseRes2(responseInfo.result);
								//resObj = HttpUtil.parseRes(responseInfo.result);
								
								DbtLog.write("resObj==null:" + (resObj == null));
								if (resObj == null) {
									DbtLog.write(adatasyn.getStrTableName() + "------------------------");
								}
								DbtLog.write("----------------status:" + (resObj.getResHead().getStatus()));
								DbtLog.write("解析结束");
								subTime = new Date().getTime() - startTime;
								jsonSumTime += subTime;
								DbtLog.write("====解析耗时:" + subTime);
								if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
									// 状态是"M"成功
									json = resObj.getResBody().getContent();
									// Log.i(TAG, "服务器得到的数据：" + json);
									// DbtLog.write(json);
									if ("{}".equals(json)) {
										message.what = DownLoadDataProgressActivity.SYNDATA_RESULT_Success;
										message.obj = "当前数据已经是最新的";
										handler.sendMessage(message);
										return;
									}

									final Map<String, Object> responseHashMap = JsonUtil.parseMap(json);
									// 如果数据据最较大，则下载并解压数据
									if (ConstValues.FLAG_1.equals(responseHashMap.get("zipFlag").toString())) {
										zipFlag = true;
										zipPath = downZip();
									}
									startTime = new Date().getTime();
									DbtLog.write("保存数据开始");
									Long afterTableTime = new Date().getTime();
									Log.e("updateTime", "表名：" + adatasyn.getStrTableName() + "更新时间"
											+ (afterTableTime - beforeTime));
									createOrUpdate_CMM_BOARD_M(responseHashMap, adatasyn);// 公告管理表
									createOrUpdate_CMM_DATADIC_M(responseHashMap, adatasyn);// 数据字典表
									createOrUpdate_MST_VISITMEMO_INFO(responseHashMap, adatasyn);// 客情备忘录
									createOrUpdate_MST_QUESTIONSANSWERS_INFO(responseHashMap, adatasyn);// 问题反馈
									createOrUpdate_MST_WORKSUMMARY_INFO(responseHashMap, adatasyn);// 日工作总结
									createOrUpdate_CMM_AREA_M(responseHashMap, adatasyn);// 省市县区域主表
									createOrUpdate_MST_VISITAUTHORIZE_INFO(responseHashMap, adatasyn);// 定格可拜访授权
									createOrUpdateTerminal_Route(responseHashMap, adatasyn);
									createOrUpdateVisit(responseHashMap, json, adatasyn);// 巡店拜访f
									createOrUpdateAgencyInfo(responseHashMap, adatasyn);// 经销商信息
									createOrUpdateAgencyvisit(responseHashMap, adatasyn);// 分经销商拜访f
									createOrUpdatePromotions(responseHashMap, adatasyn);// 活础动基信息
									createOrUpdatecmpbrands(responseHashMap, adatasyn);// 竞品基础信息
									createOrUpdatePlan(responseHashMap, adatasyn);// 工作计划指标基础
									createOrUpdate_MST_POWERFULTERMINAL_INFO(responseHashMap, adatasyn);
									createOrUpdate_MST_POWERFULCHANNEL_INFO(responseHashMap, adatasyn);
									createOrUpdatePriceDetails(responseHashMap, adatasyn);
									createOrUpdate_MST_MONTHTARGET_INFO(responseHashMap, adatasyn);
									createOrUpdate_MST_CMPAGENCY_INFO(responseHashMap, adatasyn);
									createOrUpdate_MST_PRODUCTSHOW_M(responseHashMap, adatasyn);
									createOrUpdatePadChecktype(responseHashMap, adatasyn);
									createOrUpdatePadPlantempcheck(responseHashMap, adatasyn);
									createOrUpdate_MST_PICTYPE_M(responseHashMap, adatasyn);
									createOrUpdate_MST_AGENCYKF_M(responseHashMap, adatasyn);
									createOrUpdate_MST_GROUPPRODUCT_M(responseHashMap, adatasyn);
									DbtLog.write("保存数据结束");
									subTime = new Date().getTime() - startTime;
									dbSumTime += subTime;
									DbtLog.write("====保存数据耗时:" + subTime);
									// 向界面发送
									sendProgressMessage(adatasyn.getStrTableName());
									break;
								} else {
									// 状态是"E" 失败
									// 立即关闭线程池
									exceptionCount++;
								}
							} else {
								// 连接失败
								// 立即关闭线程池
								exceptionCount++;
							}
							if (exceptionCount > 2) {
								// 三次连接失败或状态为E
								executorService.shutdownNow();
								sendFailureMessage();
							}
						}

					}
				});
			}

			// 等待所有线程执行完成
			executorService.shutdown();
			try {
				if (executorService.awaitTermination(10, TimeUnit.MINUTES)) {
					// 发送成功
					// 更新成功时间
					Long afterTime = new Date().getTime();
					Log.e("updateTime", "总体更新时间" + (afterTime - beforeTime));
					DbtLog.write("请求总耗时:" + netSumTime);
					DbtLog.write("json解析总耗时:" + jsonSumTime);
					DbtLog.write("保存数据总耗时:" + dbSumTime);
					DbtLog.write("同步总耗时:" + (new Date().getTime() - beforeTime));
					sendSuccessMessage();
				}
			} catch (InterruptedException e) {
				// 立即关闭线程池
				// 发送失败
				executorService.shutdownNow();
				sendFailureMessage();
			}
		}

	}

	/**
	 * 保存同步更新日期
	 * 
	 */
	public static void saveDatasynTime(Context context) {
		try {
			String dates = DateUtil.formatDate(new Date(), DateUtil.DEFAULT_DATE_FORMAT);
			FileUtil.writeTxt(dates, FileUtil.getSDPath()+"/saveDatasynTime123.txt"); 
			SharedPreferences sp = context.getSharedPreferences("datasynTime", 0);
			if (sp != null) {
				sp.edit().putString("dates", dates).commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 获取同步更新日期
	 * 
	 * @param context
	 * @return
	 */
	public static String getDatasynTime(Context context) {
		String dates = "";
		SharedPreferences sp = context.getSharedPreferences("datasynTime", 0);
		if (sp != null) {
			dates = sp.getString("dates", "");
		}
		return dates;
	}

	public static String getMemoryInfo() {
		// 应用程序最大可用内存
		// int maxMemory = ((int) Runtime.getRuntime().maxMemory())/1024/1024;
		// //应用程序已获得内存
		// long totalMemory = ((int)
		// Runtime.getRuntime().totalMemory())/1024/1024;
		// //应用程序已获得内存中未使用内存
		// long freeMemory = ((int)
		// Runtime.getRuntime().freeMemory())/1024/1024;
		// String
		// s="---> maxMemory="+maxMemory+"M,totalMemory="+totalMemory+"M,freeMemory="+freeMemory+"M"+",Memory="+getMemory(context);;
		// return s;
		return "";
	}

	private static String getMemory(Context context) {
		// ActivityManager manager = ((ActivityManager)
		// context.getSystemService(Context.ACTIVITY_SERVICE));
		// int mem = manager.getMemoryClass();
		// return mem+"";
		return "";
	}
}
