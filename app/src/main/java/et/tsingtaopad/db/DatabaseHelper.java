package et.tsingtaopad.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.db.tables.CmmAreaM;
import et.tsingtaopad.db.tables.CmmBoardM;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstAgencyKFM;
import et.tsingtaopad.db.tables.MstAgencygridInfo;
import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.db.tables.MstAgencysupplyInfo;
import et.tsingtaopad.db.tables.MstAgencytransferInfo;
import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.db.tables.MstBrandsclassM;
import et.tsingtaopad.db.tables.MstBrandseriesM;
import et.tsingtaopad.db.tables.MstBsData;
import et.tsingtaopad.db.tables.MstCameraInfoM;
import et.tsingtaopad.db.tables.MstCenterM;
import et.tsingtaopad.db.tables.MstCenterdetailsM;
import et.tsingtaopad.db.tables.MstCheckaccomplishInfo;
import et.tsingtaopad.db.tables.MstCheckcollectionInfo;
import et.tsingtaopad.db.tables.MstCheckexerecordInfo;
import et.tsingtaopad.db.tables.MstCheckexerecordInfoTemp;
import et.tsingtaopad.db.tables.MstCheckmiddleInfo;
import et.tsingtaopad.db.tables.MstCheckstatusInfo;
import et.tsingtaopad.db.tables.MstChecktypeM;
import et.tsingtaopad.db.tables.MstCmpagencyInfo;
import et.tsingtaopad.db.tables.MstCmpareaInfo;
import et.tsingtaopad.db.tables.MstCmpbrandsM;
import et.tsingtaopad.db.tables.MstCmpcompanyM;
import et.tsingtaopad.db.tables.MstCmproductinfoM;
import et.tsingtaopad.db.tables.MstCmpsupplyInfo;
import et.tsingtaopad.db.tables.MstCollectionexerecordInfo;
import et.tsingtaopad.db.tables.MstCollectionitemM;
import et.tsingtaopad.db.tables.MstGridM;
import et.tsingtaopad.db.tables.MstGroupproductM;
import et.tsingtaopad.db.tables.MstInvalidapplayInfo;
import et.tsingtaopad.db.tables.MstInvoicingInfo;
import et.tsingtaopad.db.tables.MstMarketareaM;
import et.tsingtaopad.db.tables.MstMonthtargetInfo;
import et.tsingtaopad.db.tables.MstPictypeM;
import et.tsingtaopad.db.tables.MstPlanTerminalM;
import et.tsingtaopad.db.tables.MstPlanWeekforuserM;
import et.tsingtaopad.db.tables.MstPlancheckInfo;
import et.tsingtaopad.db.tables.MstPlancollectionInfo;
import et.tsingtaopad.db.tables.MstPlanforuserM;
import et.tsingtaopad.db.tables.MstPlanrouteInfo;
import et.tsingtaopad.db.tables.MstPlantempcheckInfo;
import et.tsingtaopad.db.tables.MstPlantempcollectionInfo;
import et.tsingtaopad.db.tables.MstPlantemplateM;
import et.tsingtaopad.db.tables.MstPowerfulchannelInfo;
import et.tsingtaopad.db.tables.MstPowerfulterminalInfo;
import et.tsingtaopad.db.tables.MstPriceM;
import et.tsingtaopad.db.tables.MstPricedetailsInfo;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.db.tables.MstProductareaInfo;
import et.tsingtaopad.db.tables.MstProductshowM;
import et.tsingtaopad.db.tables.MstPromomiddleInfo;
import et.tsingtaopad.db.tables.MstPromoproductInfo;
import et.tsingtaopad.db.tables.MstPromotermInfo;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstPromotionstypeM;
import et.tsingtaopad.db.tables.MstQuestionsanswersInfo;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.db.tables.MstShipmentledgerInfo;
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
import et.tsingtaopad.tools.PropertiesUtil;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{

    private static final String DATABASE_NAME = "FsaDBT.db";
    private static final int DATABASE_VERSION = 50;

    private Dao<CmmAreaM, String> cmmAreaMDao = null;
    private Dao<CmmDatadicM, String> cmmDatadicMDao = null;
    private Dao<MstAgencygridInfo, String> mstAgencygridInfoDao = null;
    private Dao<MstAgencyinfoM, String> mstAgencyinfoMDao = null;
    private Dao<MstAgencysupplyInfo, String> mstAgencysupplyInfoDao = null;
    private Dao<MstAgencytransferInfo, String> mstAgencytransferInfoDao = null;
    private Dao<MstAgencyvisitM, String> mstAgencyvisitMDao = null;
    private Dao<MstBrandsclassM, String> mstBrandsclassMDao = null;
    private Dao<MstBrandseriesM, String> mstBrandseriesMDao = null;
    private Dao<MstCenterdetailsM, String> mstCenterdetailsMDao = null;
    private Dao<MstCenterM, String> mstCenterMDao = null;
    private Dao<MstCheckaccomplishInfo, String> mstCheckaccomplishInfoDao = null;
    private Dao<MstCheckcollectionInfo, String> mstCheckcollectionInfoDao = null;
    private Dao<MstCheckexerecordInfo, String> mstCheckexerecordInfoDao = null;
    private Dao<MstCheckexerecordInfoTemp, String> mstCheckexerecordInfoTempDao = null;
    private Dao<MstCheckstatusInfo, String> mstCheckstatusInfoDao = null;
    private Dao<MstChecktypeM, String> mstChecktypeMDao = null;
    private Dao<MstCmpareaInfo, String> mstCmpareaInfoDao = null;
    private Dao<MstCmpbrandsM, String> mstCmpbrandsMDao = null;
    private Dao<MstCmpcompanyM, String> mstCmpcompanyMDao = null;
    private Dao<MstCmproductinfoM, String> mstCmproductinfoMDao = null;
    private Dao<MstCollectionitemM, String> mstCollectionitemMDao = null;
    private Dao<MstGridM, String> mstGridMDao = null;
    private Dao<MstInvalidapplayInfo, String> mstInvalidapplayInfoDao = null;
    private Dao<MstInvoicingInfo, String> mstInvoicingInfoDao = null;
    private Dao<MstMarketareaM, String> mstMarketareaMDao = null;
    private Dao<MstPricedetailsInfo, String> mstPricedetailsInfoDao = null;
    private Dao<MstPriceM, String> mstPriceMDao = null;
    private Dao<MstProductareaInfo, String> mstProductareaInfoDao = null;
    private Dao<MstProductM, String> mstProductMDao = null;
    private Dao<MstPromoproductInfo, String> mstPromoproductInfoDao = null;
    private Dao<MstPromotermInfo, String> mstPromotermInfoDao = null;
    private Dao<MstPromotionsM, String> mstPromotionsMDao = null;
    private Dao<MstPromotionstypeM, String> mstPromotionstypeMDao = null;
    private Dao<MstRouteM, String> mstRouteMDao = null;
    private Dao<MstShipmentledgerInfo, String> mstShipmentledgerInfoDao = null;
    private Dao<MstTerminalinfoM, String> mstTerminalinfoMDao = null;
    private Dao<MstVisitauthorizeInfo, String> mstVisitauthorizeInfoDao = null;
    private Dao<MstVisitM, String> mstVisitMDao = null;
    private Dao<MstVisitmemoInfo, String> mstVisitmemoInfoDao = null;
    private Dao<MstVistproductInfo, String> mstVistproductInfoDao = null;
    private Dao<MstWorksummaryInfo, String> mstWorksummaryInfoDao = null;
    private Dao<MstSynckvM, String> mstSynckvMDao = null;
    private Dao<MstQuestionsanswersInfo, String> mstQuestionsanswersInfoDao = null;
    private Dao<MstCollectionexerecordInfo, String> mstCollectionexerecordInfoDao = null;
    private Dao<MstPlanforuserM, String> mstPlanforuserMDao = null;
    private Dao<PadChecktypeM, String> padChecktypeMDao = null;
    private Dao<PadCheckaccomplishInfo, String> padCheckaccomplishInfoDao = null;
    private Dao<PadCheckstatusInfo, String> padCheckstatusInfoDao = null;
    private Dao<PadPlantempcheckM, String> padPlantempcheckMDao = null;
    private Dao<PadPlantempcollectionInfo, String> padPlantempcollectionInfoDao = null;
    private Dao<MstCheckmiddleInfo, String> mstCheckmiddleInfoDao = null;

    private Dao<MstPlantemplateM, String> mstPlantemplateMDao = null;
    private Dao<MstPlantempcheckInfo, String> mstPlantempcheckInfoDao = null;
    private Dao<MstPlantempcollectionInfo, String> mstPlantempcollectionInfoDao = null;
    private Dao<MstPlancheckInfo, String> mstPlancheckInfoDao = null;
    private Dao<MstPlancollectionInfo, String> mstPlancollectionInfoDao = null;
    private Dao<CmmBoardM, String> cmmBoardMDao = null;
    private Dao<PadCheckproInfo, String> padCheckproInfoDao = null;
    private Dao<MstPromomiddleInfo, String> mstPromomiddleInfoDao = null;
    private Dao<MstPowerfulchannelInfo, String> mstPowerfulchannelInfoDao = null;
    private Dao<MstPowerfulterminalInfo, String> mstPowerfulterminalInfoDao = null;
    private Dao<MstCmpsupplyInfo, String> mstCmpsupplyInfoDao = null;
    private Dao<MstPlanrouteInfo, String> mstPlanrouteInfoDao = null;
    private Dao<MstMonthtargetInfo, String> mstMonthtargetInfoDao = null;
    private Dao<MstCmpagencyInfo, String> mstCmpagencyInfo = null;
    private Dao<MstProductshowM, String> mstProductshowMDao = null;
    private Dao<MstShowpicInfo, String> mstShowpicInfoDao = null;
    private Dao<MstPlanWeekforuserM, String> mstPlanWeekforuserMDao = null;
    private Dao<MstCameraInfoM, String> mstCameraiInfoMDao = null;
    private  Dao<MstPictypeM, String>  mstpictypeMDao = null;
    private  Dao<MstAgencyKFM, String>  mstAgencyKFMDao = null;
    private  Dao<MstTermLedgerInfo, String>  mstTermLedgerInfoDao = null;
    private  Dao<MstPlanTerminalM, String>  mstPlanTerminalMDao = null;
    private Dao<MstBsData, String> mstBsDataDao = null;
    private Dao<MstGroupproductM, String> mstGroupproductMDao = null;

    public DatabaseHelper(Context context)
    {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static DatabaseHelper helper = null;

    public static synchronized DatabaseHelper getHelper(Context context)
    {

        if (helper == null)
        {
            helper = new DatabaseHelper(context);
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
    {

        try
        {
            long first = System.currentTimeMillis();
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, CmmAreaM.class);
            TableUtils.createTable(connectionSource, CmmDatadicM.class);
            TableUtils.createTable(connectionSource, MstAgencygridInfo.class);
            TableUtils.createTable(connectionSource, MstAgencyinfoM.class);
            TableUtils.createTable(connectionSource, MstAgencysupplyInfo.class);
            TableUtils.createTable(connectionSource, MstAgencytransferInfo.class);
            TableUtils.createTable(connectionSource, MstAgencyvisitM.class);
            TableUtils.createTable(connectionSource, MstBrandsclassM.class);
            TableUtils.createTable(connectionSource, MstBrandseriesM.class);
            TableUtils.createTable(connectionSource, MstCenterdetailsM.class);
            TableUtils.createTable(connectionSource, MstCenterM.class);
            TableUtils.createTable(connectionSource, MstCheckaccomplishInfo.class);
            TableUtils.createTable(connectionSource, MstCheckcollectionInfo.class);
            //			TableUtils.createTable(connectionSource, MstCheckexerecordInfo.class);
            CreateMstCheckexerecordInfoTable(db);
            TableUtils.createTable(connectionSource, MstCheckexerecordInfoTemp.class);
            TableUtils.createTable(connectionSource, MstCheckstatusInfo.class);
            TableUtils.createTable(connectionSource, MstChecktypeM.class);
            TableUtils.createTable(connectionSource, MstCmpareaInfo.class);
            TableUtils.createTable(connectionSource, MstCmpbrandsM.class);
            TableUtils.createTable(connectionSource, MstCmpcompanyM.class);
            TableUtils.createTable(connectionSource, MstCmproductinfoM.class);
            TableUtils.createTable(connectionSource, MstCollectionitemM.class);
            TableUtils.createTable(connectionSource, MstGridM.class);
            TableUtils.createTable(connectionSource, MstInvalidapplayInfo.class);
            TableUtils.createTable(connectionSource, MstInvoicingInfo.class);
            TableUtils.createTable(connectionSource, MstMarketareaM.class);
            TableUtils.createTable(connectionSource, MstPricedetailsInfo.class);
            TableUtils.createTable(connectionSource, MstPriceM.class);
            TableUtils.createTable(connectionSource, MstProductareaInfo.class);
            TableUtils.createTable(connectionSource, MstProductM.class);
            TableUtils.createTable(connectionSource, MstPromoproductInfo.class);
            TableUtils.createTable(connectionSource, MstPromotermInfo.class);
            TableUtils.createTable(connectionSource, MstPromotionsM.class);
            TableUtils.createTable(connectionSource, MstPromotionstypeM.class);
            TableUtils.createTable(connectionSource, MstRouteM.class);
            TableUtils.createTable(connectionSource, MstShipmentledgerInfo.class);
            TableUtils.createTable(connectionSource, MstTerminalinfoM.class);
            TableUtils.createTable(connectionSource, MstVisitauthorizeInfo.class);
            TableUtils.createTable(connectionSource, MstVisitM.class);
            TableUtils.createTable(connectionSource, MstVisitmemoInfo.class);
            TableUtils.createTable(connectionSource, MstVistproductInfo.class);
            TableUtils.createTable(connectionSource, MstWorksummaryInfo.class);
            TableUtils.createTable(connectionSource, MstSynckvM.class);
            TableUtils.createTable(connectionSource, MstQuestionsanswersInfo.class);
            TableUtils.createTable(connectionSource, MstCollectionexerecordInfo.class);
            TableUtils.createTable(connectionSource, MstPlanforuserM.class);
            TableUtils.createTable(connectionSource, PadCheckaccomplishInfo.class);
            TableUtils.createTable(connectionSource, PadCheckstatusInfo.class);
            TableUtils.createTable(connectionSource, PadChecktypeM.class);
            TableUtils.createTable(connectionSource, PadPlantempcheckM.class);
            TableUtils.createTable(connectionSource, PadPlantempcollectionInfo.class);
            TableUtils.createTable(connectionSource, MstPlantemplateM.class);
            TableUtils.createTable(connectionSource, MstPlantempcheckInfo.class);
            TableUtils.createTable(connectionSource, MstPlantempcollectionInfo.class);
            TableUtils.createTable(connectionSource, MstPlancheckInfo.class);
            TableUtils.createTable(connectionSource, MstPlancollectionInfo.class);
            TableUtils.createTable(connectionSource, CmmBoardM.class);
            TableUtils.createTable(connectionSource, MstCheckmiddleInfo.class);
            TableUtils.createTable(connectionSource, PadCheckproInfo.class);
            TableUtils.createTable(connectionSource, MstPromomiddleInfo.class);
            TableUtils.createTable(connectionSource, MstPowerfulchannelInfo.class);
            TableUtils.createTable(connectionSource, MstPowerfulterminalInfo.class);
            TableUtils.createTable(connectionSource, MstCmpsupplyInfo.class);
            TableUtils.createTable(connectionSource, MstPlanrouteInfo.class);
            TableUtils.createTable(connectionSource, MstMonthtargetInfo.class);
            TableUtils.createTable(connectionSource, MstCmpagencyInfo.class);
            TableUtils.createTable(connectionSource, MstProductshowM.class);
            TableUtils.createTable(connectionSource, MstShowpicInfo.class);
            TableUtils.createTable(connectionSource, MstPlanWeekforuserM.class);
            TableUtils.createTable(connectionSource, MstCameraInfoM.class);// 图片表
            TableUtils.createTable(connectionSource, MstPictypeM.class);// 拍照图片类型
            TableUtils.createTable(connectionSource, MstAgencyKFM.class);// 经销商开发
            TableUtils.createTable(connectionSource, MstTermLedgerInfo.class);// 终端台账
            TableUtils.createTable(connectionSource, MstPlanTerminalM.class);// 计划终端表
            TableUtils.createTable(connectionSource, MstBsData.class);// 计划终端表
            TableUtils.createTable(connectionSource, MstGroupproductM.class);// 计划终端表

            this.initView(db);
            this.initData(db);

            Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + (System.currentTimeMillis() - first));
        }
        catch (SQLException e)
        {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化视图
     */
    public void initView(SQLiteDatabase db)
    {

        // 获取各终端     每天    最新的拜访记录
        StringBuffer buffer = new StringBuffer();
        buffer.append("drop view if exists v_visit_m");
        db.execSQL(buffer.toString());
        buffer = new StringBuffer();
        buffer.append("create view IF NOT EXISTS v_visit_m as ");
        buffer.append("select m.* from mst_visit_m m  ");
        buffer.append("inner join (select max(visitdate) maxvisitdate, ");
        buffer.append("    max(terminalkey) maxterminalkey ");
        buffer.append("    from mst_visit_m where visitdate is not null group by terminalkey, substr(visitdate,1,8)) v ");
        buffer.append("  on m.terminalkey = v.maxterminalkey  ");
        buffer.append("    and m.visitdate = v.maxvisitdate");
        db.execSQL(buffer.toString());

        // 获取各终端    所有    拜访数据中最新拜访记录
        buffer = new StringBuffer();
        buffer.append("drop view if exists v_visit_m_newest");
        db.execSQL(buffer.toString());
        buffer = new StringBuffer();
        buffer.append("create view IF NOT EXISTS v_visit_m_newest as ");
        buffer.append("select m.* from mst_visit_m m  ");
        buffer.append("inner join (select max(visitdate) maxvisitdate, ");
        buffer.append("    max(terminalkey) maxterminalkey ");
        buffer.append("    from mst_visit_m where visitdate is not null group by terminalkey) v ");
        buffer.append("  on m.terminalkey = v.maxterminalkey  ");
        buffer.append("    and m.visitdate = v.maxvisitdate");
        db.execSQL(buffer.toString());

        // 获取经销商拜访每天最新的拜访记录
        buffer = new StringBuffer();
        buffer.append("drop view if exists v_agencyvisit_m");
        db.execSQL(buffer.toString());
        buffer = new StringBuffer();
        buffer.append("create view IF NOT EXISTS v_agencyvisit_m as ");
        buffer.append("select m.* from mst_agencyvisit_m m  ");
        buffer.append("inner join (select max(agevisitdate) maxagevisitdate, ");
        buffer.append("    max(agencykey) maxagencykey ");
        buffer.append("    from mst_agencyvisit_m group by agencykey, substr(agevisitdate,1,8)) v ");
        buffer.append("  on m.agencykey = v.maxagencykey  ");
        buffer.append("    and m.agevisitdate = v.maxagevisitdate");
        db.execSQL(buffer.toString());

        // 获取经销商拜访最新的拜访记录
        buffer = new StringBuffer();
        buffer.append("drop view if exists v_agencyvisit_m_newest");
        db.execSQL(buffer.toString());
        buffer = new StringBuffer();
        buffer.append("create view IF NOT EXISTS v_agencyvisit_m_newest as ");
        buffer.append("select m.* from mst_agencyvisit_m m  ");
        buffer.append("inner join (select max(agevisitdate) maxagevisitdate, ");
        buffer.append("    max(agencykey) maxagencykey ");
        buffer.append("    from mst_agencyvisit_m group by agencykey) v ");
        buffer.append("  on m.agencykey = v.maxagencykey  ");
        buffer.append("    and m.agevisitdate = v.maxagevisitdate");
        db.execSQL(buffer.toString());

        // V_AGENCYSELPRODUCT_INFO(经销商可以销售产品)
        buffer = new StringBuffer();
        buffer.append("drop view if exists v_agencyselproduct_info");
        db.execSQL(buffer.toString());
        buffer = new StringBuffer();
        buffer.append("create view IF NOT EXISTS v_agencyselproduct_info as ");
        buffer.append("select distinct am.agencykey, am.agencyname, pd.productkey, pdm.proname, pm.startdate, pm.enddate ");
        buffer.append("from mst_agencyinfo_m am, mst_price_m pm, mst_pricedetails_info pd, mst_productarea_info pa, mst_product_m pdm ");
        buffer.append("where am.pricekey = pm.pricekey and am.pricekey = pd.pricekey and pd.productkey = pa.productkey ");
        buffer.append("and pa.productkey = pdm.productkey and coalesce(pa.status, '0') != '1' and coalesce(am.agencystatus,'0') != '1' ");
        buffer.append("and coalesce(am.deleteflag,'0') != '1' and coalesce(pm.deleteflag,'0') != '1' and coalesce(pd.deleteflag,'0') != '1' ");
        buffer.append("and coalesce(pa.deleteflag,'0') != '1' and coalesce(pdm.deleteflag,'0') != '1' ");
        db.execSQL(buffer.toString());

        //v_pd_check_result (日工作明细查询指标  ---- 弃用20140428)
        buffer = new StringBuffer();
        buffer.append("drop view if exists v_pd_check_result");
        db.execSQL(buffer.toString());
        buffer = new StringBuffer();
        buffer.append("create view IF NOT EXISTS v_pd_check_result as select p.visitkey, p.productkey,");
        buffer.append("max(case when p.checkkey = '666b74b3-b221-4920-b549-d9ec39a463fd' then i.cstatusname else null end) hz,"); //合作执行是否到位
        buffer.append("max(case when p.checkkey = 'df2e88c9-246f-40e2-b6e5-08cdebf8c281' then i.cstatusname else null end) ps,"); //是否高质量配送
        buffer.append("max(case when p.checkkey = '59802090-02ac-4146-9cc3-f09570c36a26' then i.cstatusname else null end) zy,"); //单店占有率
        buffer.append("max(case when p.checkkey = 'ad3030fb-e42e-47f8-a3ec-4229089aab5d' then i.cstatusname else null end) ph,"); //铺货
        buffer.append("max(case when p.checkkey = 'ad3030fb-e42e-47f8-a3ec-4229089aab6d' then i.cstatusname else null end) dj, "); //道具生动化
        buffer.append("max(case when p.checkkey = 'ad3030fb-e42e-47f8-a3ec-4229089aab7d' then i.cstatusname else null end) cp  "); //产品生动化
        buffer.append("from MST_CHECKEXERECORD_INFO p, PAD_CHECKSTATUS_INFO i ");
        buffer.append("where p.checkkey = i.checkkey and p.acresult = i.cstatuskey ");
        buffer.append("   and p.deleteflag!='" + ConstValues.delFlag + "' ");
        buffer.append("group by p.visitkey,p.productkey");
        db.execSQL(buffer.toString());

        // pad端采集项
        buffer = new StringBuffer();
        buffer.append("drop view if exists v_pad_checkaccomplish_info");
        db.execSQL(buffer.toString());
        buffer = new StringBuffer();
        buffer.append("create view IF NOT EXISTS v_pad_checkaccomplish_info as ");
        buffer.append("select distinct p.colitemkey, p.colitemname from pad_checkaccomplish_info p ");
        db.execSQL(buffer.toString());
    }

    /**
    * 
    */
    public void CreateMstCheckexerecordInfoTable(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE  IF NOT EXISTS MST_CHECKEXERECORD_INFO " + "(recordkey  VARCHAR primary key not null,visitkey VARCHAR,productkey VARCHAR,checkkey VARCHAR,checktype VARCHAR,acresult VARCHAR,iscom VARCHAR,cstatuskey VARCHAR,isauto VARCHAR,exestatus VARCHAR,startdate VARCHAR,enddate VARCHAR,terminalkey VARCHAR,sisconsistent VARCHAR,scondate timestamp,siebelid VARCHAR,resultstatus VARCHAR,padisconsistent VARCHAR DEFAULT '0',padcondate VARCHAR,comid VARCHAR,remarks VARCHAR,orderbyno VARCHAR,version VARCHAR,credate timestamp,creuser VARCHAR,updatetime timestamp,updateuser VARCHAR,deleteflag VARCHAR  DEFAULT '0'   )";
        db.execSQL(sql);
    }

    /**
     * 初始化基础数据
     * 
     * @param db
     */
    public void initData(SQLiteDatabase db)
    {
        // 初始化KV表基础数据
        /* String synkvTablename[] = { "CMM_BOARD_M", "CMM_DATADIC_M", "CMM_AREA_M", "MST_ROUTE_M",
                 "MST_TERMINALINFO_M", "MST_AGENCYINFO_M", "MST_AGENCYGRID_INFO", 
                 "MST_AGENCYSUPPLY_INFO", "MST_AGENCYTRANSFER_INFO", 
                 "MST_INVOICING_INFO", "MST_PROMOTIONS_M", "MST_PROMOTIONSTYPE_M",
                 "MST_PROMOPRODUCT_INFO", "MST_PROMOTERM_INFO", "MST_VISIT_M", 
                 "MST_VISTPRODUCT_INFO", "MST_CHECKEXERECORD_INFO", "MST_COLLECTIONEXERECORD_INFO", 
                 "MST_CMPBRANDS_M", "MST_CMPCOMPANY_M", "MST_CMPRODUCTINFO_M", "MST_PRODUCT_M", 
                 "MST_VISITMEMO_INFO", "MST_VISITAUTHORIZE_INFO", "MST_AGENCYVISIT_M", "MST_QUESTIONSANSWERS_INFO", 
                 "MST_WORKSUMMARY_INFO", "PAD_CHECKTYPE_M", "MST_PLANFORUSER_M", "MST_PLANCOLLECTION_INFO", 
                 "PAD_PLANTEMPCHECK_M", "MST_CMPSUPPLY_INFO", "MST_PLANCHECK_INFO", 
                 "MST_PRICE_M", "MST_PRICEDETAILS_INFO", "MST_PRODUCTAREA_INFO", "MST_MONTHTARGET_INFO",
                 "MST_CMPAGENCY_INFO", "MST_PRODUCTSHOW_M","MST_SHOWPIC_INFO"};*/
        String synkvTabl[][] = {
        	
        		
               { "CMM_BOARD_M", "通知公告" }, 
                { "CMM_DATADIC_M", "数据字典" }, 
                { "CMM_AREA_M", "省市县区域" }, 
                { "MST_ROUTE_M", "线路" },
                { "MST_TERMINALINFO_M", "终端" }, 
                { "MST_AGENCYINFO_M", "分经销商" }, 
                { "MST_AGENCYGRID_INFO", "分经销商" }, 
                { "MST_AGENCYSUPPLY_INFO", "巡店拜访" }, 
                { "MST_AGENCYTRANSFER_INFO", "分经销商拜访" }, 
                { "MST_INVOICING_INFO", "分经销商拜访" }, 
                { "MST_PROMOTIONS_M", "活动基础信息" }, 
                { "MST_PROMOTIONSTYPE_M", "活动基础信息" }, 
                { "MST_PROMOPRODUCT_INFO", "活动基础信息" }, 
                { "MST_PROMOTERM_INFO", "巡店拜访1" }, 
                 { "MST_VISIT_M", "巡店拜访2" },
                { "MST_VISTPRODUCT_INFO", "巡店拜访3" }, 
                { "MST_CHECKEXERECORD_INFO", "巡店拜访4" },
                { "MST_COLLECTIONEXERECORD_INFO","巡店拜访5"},
                { "MST_CMPBRANDS_M", "竞品" }, 
                { "MST_CMPCOMPANY_M", "竞品" }, 
                { "MST_CMPRODUCTINFO_M", "竞品" }, 
                { "MST_PRODUCT_M", "分经销商" }, 
                { "MST_VISITMEMO_INFO", "客情备忘录" }, 
                { "MST_VISITAUTHORIZE_INFO", "定格可拜访授权" }, 
                { "MST_AGENCYVISIT_M", "分经销商拜访" }, 
                { "MST_QUESTIONSANSWERS_INFO", "问题反馈" }, 
                { "MST_WORKSUMMARY_INFO", "日工作总结" }, 
                { "PAD_CHECKTYPE_M", "采集用指标" }, 
                { "MST_PLANFORUSER_M", "工作计划指标状态" }, 
                { "MST_PLANCOLLECTION_INFO", "工作计划指标状态" }, 
                { "PAD_PLANTEMPCHECK_M", "计划模板" }, 
                { "MST_CMPSUPPLY_INFO", "巡店拜访" }, 
                { "MST_PLANCHECK_INFO", "工作计划指标状态" }, 
                { "MST_PRICE_M", "价格明细" }, 
                { "MST_PRICEDETAILS_INFO", "价格明细" }, 
                { "MST_PRODUCTAREA_INFO", "分经销商信息" }, 
                { "MST_MONTHTARGET_INFO", "月目标管理" }, 
                { "MST_CMPAGENCY_INFO", "竞品供应商管理" }, 
                { "MST_PRODUCTSHOW_M", "产品展示信息" }, 
                { "MST_SHOWPIC_INFO", "产品展示" }, 
                { "MST_PLANWEEKFORUSER_M", "周工作计划" },
        		 
                { "MST_PICTYPE_M", "图片类型" },
                
                { "MST_AGENCYKF_M", "经销商开发" },
                { "MST_PLANTERMINAL_M", "计划终端表" },
                { "MST_GROUPPRODUCT_M2", "产品组合是否达标" }
                
                
                

        };

        StringBuffer buffer;
        for (String[] item : synkvTabl)
        {
            buffer = new StringBuffer();
            buffer.append("insert into MST_SYNCKV_M (tablename, syncDay,tableDesc) ");
            buffer.append("values ('").append(item[0]).append("', '");
            buffer.append(PropertiesUtil.getProperties(item[0] + "_SYNCDAY", "0"));
            //buffer.append(PropertiesUtil.getProperties(item + "_SYNCDAY", "0"));
            buffer.append("','" + item[1] + "') ");
            db.execSQL(buffer.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {

        // C1： 添加表、视图
        if (oldVersion < 1)
        {
            db.execSQL("create table temp_hongen (usercode varchar2(20))");
            db.execSQL("create view v_hongen as select * from temp_hongen");
            db.execSQL("insert into temp_hongen(usercode) values('100001')");
        }

        // C2：添加字段
        if (oldVersion < 2)
        {
            db.execSQL("alter table temp_hongen add username varchar2(10)");
        }

        // C3：删除、修改字段
        if (oldVersion < 3)
        {
            db.execSQL("create table temp_hongen_temp as select * from temp_hongen");
            db.execSQL("drop table temp_hongen");
            db.execSQL("create table temp_hongen (usercode varchar2(20), username varchar2(20), deparment varchar2(10))");
            db.execSQL("insert into temp_hongen(usercode, username) select usercode,username from temp_hongen_temp");
            db.execSQL("drop table temp_hongen_temp");
        }

        // C4：修改视图
        if (oldVersion < 4)
        {
            db.execSQL("drop view if exists v_hongen");
            db.execSQL("create view v_hongen as select *, 'changeview' from temp_hongen");
        }

        // 28: 清除不需要下载数据的表
        if (oldVersion < 28)
        {
            db.execSQL("delete from MST_SYNCKV_M where tablename in ('MST_POWERFULCHANNEL_INFO','MST_POWERFULTERMINAL_INFO','MST_CHECKMIDDLE_INFO','MST_PLANROUTE_INFO')");
        }

        // 32: 拜访指标执行采集项记录表增加指标主键字段
        if (oldVersion < 32)
        {
            db.execSQL("alter table MST_COLLECTIONEXERECORD_INFO add checkkey varchar2");
        }

        // 33: 拜访指标执行采集项记录表增加指标主键字段
        if (oldVersion < 33)
        {
            //去除同步‘巡店拜访指标采集项’
            db.execSQL("delete from MST_SYNCKV_M  where tablename='MST_COLLECTIONEXERECORD_INFO'");
            //拜访终端是否店销和店销时间
            db.execSQL("alter table mst_visit_m add ifmine varchar2(10)");
            db.execSQL("alter table mst_visit_m add ifminedate varchar2(20)");
            //万能铺货率
            db.execSQL("alter table MST_POWERFULTERMINAL_INFO add prevdistribution integer");
            db.execSQL("alter table MST_POWERFULTERMINAL_INFO add preveffectdis integer");
            db.execSQL("alter table MST_POWERFULTERMINAL_INFO add preveffectsale integer");
            //
            db.execSQL("alter table MST_POWERFULCHANNEL_INFO add prevdistribution integer");
            db.execSQL("alter table MST_POWERFULCHANNEL_INFO add preveffectdis integer");
            db.execSQL("alter table MST_POWERFULCHANNEL_INFO add preveffectsale integer");
            //经销商拜访期初库存（上次库存）
            db.execSQL("alter table MST_INVOICING_INFO add prestorenum Double");
            //周工作计划
            db.execSQL("CREATE TABLE MST_PLANWEEKFORUSER_M (comid VARCHAR , credate VARCHAR , creuser VARCHAR , deleteflag VARCHAR DEFAULT '0' , enddate VARCHAR , gridkey VARCHAR , orderbyno VARCHAR , padcondate VARCHAR , padisconsistent VARCHAR DEFAULT '0' , plankey VARCHAR NOT NULL , planstatus VARCHAR , plantitle VARCHAR , remarks VARCHAR , scondate VARCHAR , sisconsistent VARCHAR , startdate VARCHAR , updatetime VARCHAR , updateuser VARCHAR , uploadFlag VARCHAR DEFAULT '0' , userid VARCHAR , version VARCHAR , PRIMARY KEY (plankey) );");
        }
        // 35: 创建 拜访拍照表
        if (oldVersion < 35)
        {
        	try {
        		/*//创建  本地拍照表,图片类型表
				TableUtils.createTable(connectionSource, MstCameraInfoM.class);
				TableUtils.createTable(connectionSource, MstPictypeM.class);
				// 创建经销商开发表
				TableUtils.createTable(connectionSource, MstAgencyKFM.class);
				// 创建终端台账表
				TableUtils.createTable(connectionSource, MstTermLedgerInfo.class);// 终端台账
				*/				
        		//创建  本地拍照表,图片类型表
				//TableUtils.createTable(connectionSource, MstCameraInfoM.class);
        		db.execSQL("CREATE TABLE MST_CAMERAINFO_M (camerakey VARCHAR(36) NOT NULL,terminalkey VARCHAR(36),visitkey VARCHAR(36),pictypekey VARCHAR(36),picname VARCHAR(150),localpath VARCHAR(150),netpath VARCHAR(150),cameradata VARCHAR(20),isupload VARCHAR2(36),istakecamera VARCHAR(36),picindex VARCHAR(16),pictypename VARCHAR(150),sureup VARCHAR(16),imagefileString VARCHAR(4000));");
        		//TableUtils.createTable(connectionSource, MstPictypeM.class);
        		db.execSQL("CREATE TABLE MST_PICTYPE_M ( pictypekey VARCHAR(36) NOT NULL,pictypename VARCHAR(36),areaid VARCHAR(36),orderno VARCHAR(36),status VARCHAR(150),focus VARCHAR(150),createuser VARCHAR(36),updateuser VARCHAR(36),createdate VARCHAR2(36),updatedate VARCHAR(36));");
				// 创建经销商开发表
				//TableUtils.createTable(connectionSource, MstAgencyKFM.class);
				db.execSQL("CREATE TABLE MST_AGENCYKF_M ( agencykfkey VARCHAR(36) NOT NULL,gridkey VARCHAR(36),agencyname VARCHAR(36),contact VARCHAR(36),mobile VARCHAR(150),address VARCHAR(150),area VARCHAR(150),money VARCHAR(20),carnum VARCHAR2(5),productname VARCHAR(36),kfdate VARCHAR(20),status VARCHAR(5),createdate VARCHAR(20),createuser VARCHAR(20),updatedate VARCHAR(20),updateuser VARCHAR2(20),upload VARCHAR(5));");
				// 创建终端台账表
				//TableUtils.createTable(connectionSource, MstTermLedgerInfo.class);// 终端台账
				db.execSQL("CREATE TABLE MST_TERMLEDGER_INFO ( termledgerkey VARCHAR(36) NOT NULL,terminalkey VARCHAR(36),terminalcode VARCHAR(36),terminalname VARCHAR(36),gridname VARCHAR(15),gridkey VARCHAR(10),agencykey VARCHAR(36),agencycode VARCHAR(20),agencyname VARCHAR2(36),productkey VARCHAR(36),procode VARCHAR(16),proname VARCHAR(30),padisconsistent VARCHAR(2),orderbyno VARCHAR(1),deleteflag VARCHAR(1),remarks VARCHAR(36),yesup VARCHAR(1),purchase VARCHAR(5),sequence VARCHAR(5),routename VARCHAR(36),address VARCHAR(36),contact VARCHAR2(36),mobile VARCHAR(36),areaid VARCHAR(16),areaname VARCHAR(36),downdate VARCHAR(10),firstzm VARCHAR(10),purchasetime VARCHAR(10));");
        		// 在同步kv表中插入图片类型一条记录
				StringBuffer buffer = new StringBuffer();
	            buffer.append("insert into MST_SYNCKV_M (tablename, syncDay,tableDesc) ");
	            buffer.append("values ('").append("MST_PICTYPE_M").append("', '");
	            buffer.append(PropertiesUtil.getProperties("MST_PICTYPE_M" + "_SYNCDAY", "0"));
	            buffer.append("','" + "图片类型" + "') ");
	            db.execSQL(buffer.toString());
	            // 在同步kv表中插入经销商开发一条记录 
	            StringBuffer buffer1 = new StringBuffer();
	            buffer1.append("insert into MST_SYNCKV_M (tablename, syncDay,tableDesc) ");
	            buffer1.append("values ('").append("MST_AGENCYKF_M").append("', '");
	            buffer1.append(PropertiesUtil.getProperties("MST_PICTYPE_M" + "_SYNCDAY", "0"));
	            buffer1.append("','" + "经销商开发" + "') ");
	            db.execSQL(buffer1.toString());
	            // copy上面
	            // 在MST_VISTPRODUCT_INFO表中添加agencyname字段
	            //alter table 表名 add 字段名 数据类型 default 默认值 
	            db.execSQL("alter table MST_VISTPRODUCT_INFO add agencyname varchar(100)");
	            
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        // 36 创建计划终端表
        if(oldVersion < 36){

        	try {
        		//创建  本地拍照表,图片类型表
				//TableUtils.createTable(connectionSource, MstPlanTerminalM.class);
        		db.execSQL("CREATE TABLE MST_PLANTERMINAL_M ( planterminalkey VARCHAR(36) NOT NULL,pcolitemkey VARCHAR(36),terminalkey VARCHAR(36),terminalname VARCHAR(36),tlevel VARCHAR(150),padisconsistent VARCHAR(150),creuser VARCHAR(150),updateuser VARCHAR(20),cretime DATE,updatetime DATE);");
				
				// 在同步kv表中插入图片类型一条记录
				StringBuffer buffer = new StringBuffer();
	            buffer.append("insert into MST_SYNCKV_M (tablename, syncDay,tableDesc) ");
	            buffer.append("values ('").append("MST_PLANTERMINAL_M").append("', '");
	            buffer.append(PropertiesUtil.getProperties("MST_PICTYPE_M" + "_SYNCDAY", "0"));
	            buffer.append("','" + "计划终端表" + "') ");
	            db.execSQL(buffer.toString());
	            
	            // 经销商开发 新加一些字段
	            db.execSQL("alter table MST_AGENCYKF_M add persion varchar(100)");
	            db.execSQL("alter table MST_AGENCYKF_M add business varchar(100)");
	            db.execSQL("alter table MST_AGENCYKF_M add isone integer");
	            db.execSQL("alter table MST_AGENCYKF_M add coverterms varchar(1000)");
	            db.execSQL("alter table MST_AGENCYKF_M add supplyterms varchar(1000)");
	            db.execSQL("alter table MST_AGENCYKF_M add passdate varchar(1000)");
	            
	            
			} catch (Exception e) {
				e.printStackTrace();
			}
        
        }
        // 37 新增字段
        if(oldVersion < 37){
        	
        	try {
        		// MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表) 新加一些字段
        		db.execSQL("alter table MST_VISTPRODUCT_INFO add fristdate varchar(100)");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        }
        // 38 新增字段
        if(oldVersion < 38){
        	
        	try {
        		db.execSQL("alter table MST_TERMINALINFO_M add selftreaty varchar(100)");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        }
        if(oldVersion < 39){
        	
        	try {
        		db.execSQL("alter table MST_TERMINALINFO_M add cmpselftreaty varchar(100)");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        }
        if(oldVersion < 40){
        	
        	try {
        		db.execSQL("alter table MST_VISIT_M add visitposition varchar2(36)");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        }
        if(oldVersion < 41){
        	
        	try {
        		db.execSQL("alter table MST_PLANCOLLECTION_INFO add ordernum varchar2(36)");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        }
        if(oldVersion < 42){
        	
        	try {
        		db.execSQL("alter table MST_PROMOTIONS_M add ispictype varchar2(36)");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        }
        if(oldVersion < 43){
        	
        	try {
        		db.execSQL("alter table MST_COLLECTIONEXERECORD_INFO add freshness varchar2(36)");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        if(oldVersion < 44){
        	
        	try {
        		// 新增累计卡字段
        		db.execSQL("alter table MST_VISTPRODUCT_INFO add addcard varchar2(36)");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        if(oldVersion < 45){
        	
        	try {
        		
        		//创建  本地流量统计表
        		db.execSQL("CREATE TABLE MST_BS_DATA (bsdatakey VARCHAR(36) NOT NULL,title VARCHAR(36),sizes VARCHAR(36),credate VARCHAR(36),flag VARCHAR(150),content VARCHAR(150),remark VARCHAR(150));");
        		
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        if(oldVersion < 46){
        	
        	try {
        		
        		// 
        		db.execSQL("alter table MST_INVOICING_INFO add selfsales varchar2(36)");
        		db.execSQL("alter table MST_INVOICING_INFO add unselfsales varchar2(36)");
        		db.execSQL("alter table MST_INVOICING_INFO add othersales varchar2(36)");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        if(oldVersion < 47){
        	
        	try {
        		
        		// 
				db.execSQL("alter table MST_TERMINALINFO_M add ifmine varchar2(10)");
				db.execSQL("alter table MST_TERMINALINFO_M add ifminedate varchar2(20)"); 
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        if(oldVersion < 48){
        	
        	try {
        		
        		// 新加MST_GROUPPRODUCT_M表
        		db.execSQL("CREATE TABLE MST_GROUPPRODUCT_M (gproductid VARCHAR2(36), terminalcode VARCHAR2(20), terminalname VARCHAR2(500), ifrecstand CHAR(1), startdate DATE, enddate DATE, createusereng VARCHAR2(30), createdate DATE , updateusereng VARCHAR2(30), updatetime DATE, uploadflag VARCHAR(30), padisconsistent VARCHAR(30))");
        		
        		
        		// 在同步kv表中插入产品组合是否达标一条记录
				StringBuffer buffer = new StringBuffer();
	            buffer.append("insert into MST_SYNCKV_M (tablename, syncDay,tableDesc) ");
	            buffer.append("values ('").append("MST_GROUPPRODUCT_M2").append("', '");
	            buffer.append(PropertiesUtil.getProperties("MST_PICTYPE_M" + "_SYNCDAY", "0"));
	            buffer.append("','" + "产品组合是否达标" + "') ");
	            db.execSQL(buffer.toString());
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        if(oldVersion < 49){
        	
        	try {
        		
        		// 
				db.execSQL("alter table MST_COLLECTIONEXERECORD_INFO add bianhualiang varchar2(20)");
				db.execSQL("alter table MST_COLLECTIONEXERECORD_INFO add xianyouliang varchar2(20)"); 
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        if(oldVersion < 50){

            try {

                // 拜访主表
                db.execSQL("alter table MST_VISIT_M add address varchar2(200)");

                // 获取各终端     每天    最新的拜访记录
                StringBuffer buffer = new StringBuffer();
                buffer.append("drop view if exists v_visit_m");
                db.execSQL(buffer.toString());
                buffer = new StringBuffer();
                buffer.append("create view IF NOT EXISTS v_visit_m as ");
                buffer.append("select m.* from mst_visit_m m  ");
                buffer.append("inner join (select max(visitdate) maxvisitdate, ");
                buffer.append("    max(terminalkey) maxterminalkey ");
                buffer.append("    from mst_visit_m where visitdate is not null group by terminalkey, substr(visitdate,1,8)) v ");
                buffer.append("  on m.terminalkey = v.maxterminalkey  ");
                buffer.append("    and m.visitdate = v.maxvisitdate");
                db.execSQL(buffer.toString());

                // 获取各终端    所有    拜访数据中最新拜访记录
                buffer = new StringBuffer();
                buffer.append("drop view if exists v_visit_m_newest");
                db.execSQL(buffer.toString());
                buffer = new StringBuffer();
                buffer.append("create view IF NOT EXISTS v_visit_m_newest as ");
                buffer.append("select m.* from mst_visit_m m  ");
                buffer.append("inner join (select max(visitdate) maxvisitdate, ");
                buffer.append("    max(terminalkey) maxterminalkey ");
                buffer.append("    from mst_visit_m where visitdate is not null group by terminalkey) v ");
                buffer.append("  on m.terminalkey = v.maxterminalkey  ");
                buffer.append("    and m.visitdate = v.maxvisitdate");
                db.execSQL(buffer.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //		try {
        //			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
        //			TableUtils.dropTable(connectionSource, CmmAreaM.class, true);
        //			TableUtils.dropTable(connectionSource, CmmDatadicM.class, true);
        //			TableUtils.dropTable(connectionSource, MstAgencygridInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstAgencyinfoM.class, true);
        //			TableUtils.dropTable(connectionSource, MstAgencysupplyInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstAgencytransferInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstAgencyvisitM.class, true);
        //			TableUtils.dropTable(connectionSource, MstBrandsclassM.class, true);
        //			TableUtils.dropTable(connectionSource, MstBrandseriesM.class, true);
        //			TableUtils.dropTable(connectionSource, MstCenterdetailsM.class, true);
        //			TableUtils.dropTable(connectionSource, MstCenterM.class, true);
        //			TableUtils.dropTable(connectionSource, MstCheckaccomplishInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstCheckcollectionInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstCheckexerecordInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstCheckstatusInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstChecktypeM.class, true);
        //			TableUtils.dropTable(connectionSource, MstCmpareaInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstCmpbrandsM.class, true);
        //			TableUtils.dropTable(connectionSource, MstCmpcompanyM.class, true);
        //			TableUtils.dropTable(connectionSource, MstCmproductinfoM.class, true);
        //			TableUtils.dropTable(connectionSource, MstCollectionitemM.class, true);
        //			TableUtils.dropTable(connectionSource, MstGridM.class, true);
        //			TableUtils.dropTable(connectionSource, MstInvalidapplayInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstInvoicingInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstMarketareaM.class, true);
        //			TableUtils.dropTable(connectionSource, MstPricedetailsInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPriceM.class, true);
        //			TableUtils.dropTable(connectionSource, MstProductareaInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstProductM.class, true);
        //			TableUtils.dropTable(connectionSource, MstPromoproductInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPromotermInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPromotionsM.class, true);
        //			TableUtils.dropTable(connectionSource, MstPromotionstypeM.class, true);
        //			TableUtils.dropTable(connectionSource, MstRouteM.class, true);
        //			TableUtils.dropTable(connectionSource, MstShipmentledgerInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstTerminalinfoM.class, true);
        //			TableUtils.dropTable(connectionSource, MstVisitauthorizeInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstVisitM.class, true);
        //			TableUtils.dropTable(connectionSource, MstVisitmemoInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstVistproductInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstWorksummaryInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstSynckvM.class, true);
        //			TableUtils.dropTable(connectionSource, MstQuestionsanswersInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstCollectionexerecordInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPlanforuserM.class, true);
        //			TableUtils.dropTable(connectionSource, PadCheckaccomplishInfo.class, true);
        //			TableUtils.dropTable(connectionSource, PadCheckstatusInfo.class, true);
        //			TableUtils.dropTable(connectionSource, PadChecktypeM.class, true);
        //			TableUtils.dropTable(connectionSource, PadPlantempcheckM.class, true);
        //			TableUtils.dropTable(connectionSource, PadPlantempcollectionInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPlantemplateM.class, true);
        //			TableUtils.dropTable(connectionSource, MstPlantempcheckInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPlantempcollectionInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPlancheckInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPlancollectionInfo.class, true);
        //			TableUtils.dropTable(connectionSource, CmmBoardM.class, true);
        //			TableUtils.dropTable(connectionSource, MstCheckmiddleInfo.class, true);
        //			TableUtils.dropTable(connectionSource, PadCheckproInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPromomiddleInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPowerfulchannelInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPowerfulterminalInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstCmpsupplyInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstPlanrouteInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstMonthtargetInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstCmpagencyInfo.class, true);
        //			TableUtils.dropTable(connectionSource, MstProductshowM.class, true);
        //			TableUtils.dropTable(connectionSource, MstShowpicInfo.class, true);
        //			onCreate(db, connectionSource);
        //		} catch (SQLException e) {
        //			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
        //			throw new RuntimeException(e);
        //		}
    }

    @Override
    public void close()
    {
        super.close();
    }
    
    public boolean deleteDataBase(Context ctx) {
        return ctx.deleteDatabase(DATABASE_NAME);
    }

    public Dao<CmmAreaM, String> getCmmAreaMDao() throws SQLException
    {

        if (cmmAreaMDao == null)
        {
            cmmAreaMDao = getDao(CmmAreaM.class);
        }
        return cmmAreaMDao;
    }

    public Dao<CmmDatadicM, String> getCmmDatadicMDao() throws SQLException
    {

        if (cmmDatadicMDao == null)
        {
            cmmDatadicMDao = getDao(CmmDatadicM.class);
        }
        return cmmDatadicMDao;
    }

    public Dao<MstAgencygridInfo, String> getMstAgencygridInfoDao() throws SQLException
    {

        if (mstAgencygridInfoDao == null)
        {
            mstAgencygridInfoDao = getDao(MstAgencygridInfo.class);
        }
        return mstAgencygridInfoDao;
    }

    public Dao<MstAgencyinfoM, String> getMstAgencyinfoMDao() throws SQLException
    {

        if (mstAgencyinfoMDao == null)
        {
            mstAgencyinfoMDao = getDao(MstAgencyinfoM.class);
        }
        return mstAgencyinfoMDao;
    }

    public Dao<MstAgencysupplyInfo, String> getMstAgencysupplyInfoDao() throws SQLException
    {

        if (mstAgencysupplyInfoDao == null)
        {
            mstAgencysupplyInfoDao = getDao(MstAgencysupplyInfo.class);
        }
        return mstAgencysupplyInfoDao;
    }

    public Dao<MstAgencytransferInfo, String> getMstAgencytransferInfoDao() throws SQLException
    {

        if (mstAgencytransferInfoDao == null)
        {
            mstAgencytransferInfoDao = getDao(MstAgencytransferInfo.class);
        }
        return mstAgencytransferInfoDao;
    }

    public Dao<MstAgencyvisitM, String> getMstAgencyvisitMDao() throws SQLException
    {

        if (mstAgencyvisitMDao == null)
        {
            mstAgencyvisitMDao = getDao(MstAgencyvisitM.class);
        }
        return mstAgencyvisitMDao;
    }

    public Dao<MstBrandsclassM, String> getMstBrandsclassMDao() throws SQLException
    {

        if (mstBrandsclassMDao == null)
        {
            mstBrandsclassMDao = getDao(MstBrandsclassM.class);
        }
        return mstBrandsclassMDao;
    }

    public Dao<MstBrandseriesM, String> getMstBrandseriesMDao() throws SQLException
    {

        if (mstBrandseriesMDao == null)
        {
            mstBrandseriesMDao = getDao(MstBrandseriesM.class);
        }
        return mstBrandseriesMDao;
    }

    public Dao<MstCenterdetailsM, String> getMstCenterdetailsMDao() throws SQLException
    {

        if (mstCenterdetailsMDao == null)
        {
            mstCenterdetailsMDao = getDao(MstCenterdetailsM.class);
        }
        return mstCenterdetailsMDao;
    }

    public Dao<MstCenterM, String> getMstCenterMDao() throws SQLException
    {

        if (mstCenterMDao == null)
        {
            mstCenterMDao = getDao(MstCenterM.class);
        }
        return mstCenterMDao;
    }

    public Dao<MstCheckaccomplishInfo, String> getMstCheckaccomplishInfoDao() throws SQLException
    {

        if (mstCheckaccomplishInfoDao == null)
        {
            mstCheckaccomplishInfoDao = getDao(MstCheckaccomplishInfo.class);
        }
        return mstCheckaccomplishInfoDao;
    }

    public Dao<MstCheckcollectionInfo, String> getMstCheckcollectionInfoDao() throws SQLException
    {

        if (mstCheckcollectionInfoDao == null)
        {
            mstCheckcollectionInfoDao = getDao(MstCheckcollectionInfo.class);
        }
        return mstCheckcollectionInfoDao;
    }

    public Dao<MstCheckexerecordInfo, String> getMstCheckexerecordInfoDao() throws SQLException
    {

        if (mstCheckexerecordInfoDao == null)
        {
            mstCheckexerecordInfoDao = getDao(MstCheckexerecordInfo.class);
        }
        return mstCheckexerecordInfoDao;
    }

    public Dao<MstCheckexerecordInfoTemp, String> getMstCheckexerecordInfoTempDao() throws SQLException
    {

        if (mstCheckexerecordInfoTempDao == null)
        {
            mstCheckexerecordInfoTempDao = getDao(MstCheckexerecordInfoTemp.class);
        }
        return mstCheckexerecordInfoTempDao;
    }

    public Dao<MstCheckstatusInfo, String> getMstCheckstatusInfoDao() throws SQLException
    {

        if (mstCheckstatusInfoDao == null)
        {
            mstCheckstatusInfoDao = getDao(MstCheckstatusInfo.class);
        }
        return mstCheckstatusInfoDao;
    }

    public Dao<MstChecktypeM, String> getMstChecktypeMDao() throws SQLException
    {

        if (mstChecktypeMDao == null)
        {
            mstChecktypeMDao = getDao(MstChecktypeM.class);
        }
        return mstChecktypeMDao;
    }

    public Dao<MstCmpareaInfo, String> getMstCmpareaInfoDao() throws SQLException
    {

        if (mstCmpareaInfoDao == null)
        {
            mstCmpareaInfoDao = getDao(MstCmpareaInfo.class);
        }
        return mstCmpareaInfoDao;
    }

    public Dao<MstCmpbrandsM, String> getMstCmpbrandsMDao() throws SQLException
    {

        if (mstCmpbrandsMDao == null)
        {
            mstCmpbrandsMDao = getDao(MstCmpbrandsM.class);
        }
        return mstCmpbrandsMDao;
    }

    public Dao<MstCmpcompanyM, String> getMstCmpcompanyMDao() throws SQLException
    {

        if (mstCmpcompanyMDao == null)
        {
            mstCmpcompanyMDao = getDao(MstCmpcompanyM.class);
        }
        return mstCmpcompanyMDao;
    }

    public Dao<MstCmproductinfoM, String> getMstCmproductinfoMDao() throws SQLException
    {

        if (mstCmproductinfoMDao == null)
        {
            mstCmproductinfoMDao = getDao(MstCmproductinfoM.class);
        }
        return mstCmproductinfoMDao;
    }

    public Dao<MstCollectionitemM, String> getMstCollectionitemMDao() throws SQLException
    {

        if (mstCollectionitemMDao == null)
        {
            mstCollectionitemMDao = getDao(MstCollectionitemM.class);
        }
        return mstCollectionitemMDao;
    }

    public Dao<MstGridM, String> getMstGridMDao() throws SQLException
    {

        if (mstGridMDao == null)
        {
            mstGridMDao = getDao(MstGridM.class);
        }
        return mstGridMDao;
    }

    public Dao<MstInvalidapplayInfo, String> getMstInvalidapplayInfoDao() throws SQLException
    {

        if (mstInvalidapplayInfoDao == null)
        {
            mstInvalidapplayInfoDao = getDao(MstInvalidapplayInfo.class);
        }
        return mstInvalidapplayInfoDao;
    }

    public Dao<MstInvoicingInfo, String> getMstInvoicingInfoDao() throws SQLException
    {

        if (mstInvoicingInfoDao == null)
        {
            mstInvoicingInfoDao = getDao(MstInvoicingInfo.class);
        }
        return mstInvoicingInfoDao;
    }

    public Dao<MstMarketareaM, String> getMstMarketareaMDao() throws SQLException
    {

        if (mstMarketareaMDao == null)
        {
            mstMarketareaMDao = getDao(MstMarketareaM.class);
        }
        return mstMarketareaMDao;
    }

    public Dao<MstPricedetailsInfo, String> getMstPricedetailsInfoDao() throws SQLException
    {

        if (mstPricedetailsInfoDao == null)
        {
            mstPricedetailsInfoDao = getDao(MstPricedetailsInfo.class);
        }
        return mstPricedetailsInfoDao;
    }

    public Dao<MstPriceM, String> getMstPriceMDao() throws SQLException
    {

        if (mstPriceMDao == null)
        {
            mstPriceMDao = getDao(MstPriceM.class);
        }
        return mstPriceMDao;
    }
    
    public Dao<MstPictypeM, String> getMstpictypeMDao() throws SQLException
    {
    	
    	if (mstpictypeMDao == null)
    	{
    		mstpictypeMDao = getDao(MstPictypeM.class);
    	}
    	return mstpictypeMDao;
    }

    public Dao<MstProductareaInfo, String> getMstProductareaInfoDao() throws SQLException
    {

        if (mstProductareaInfoDao == null)
        {
            mstProductareaInfoDao = getDao(MstProductareaInfo.class);
        }
        return mstProductareaInfoDao;
    }

    public Dao<MstProductM, String> getMstProductMDao() throws SQLException
    {

        if (mstProductMDao == null)
        {
            mstProductMDao = getDao(MstProductM.class);
        }
        return mstProductMDao;
    }

    public Dao<MstPromoproductInfo, String> getMstPromoproductInfoDao() throws SQLException
    {

        if (mstPromoproductInfoDao == null)
        {
            mstPromoproductInfoDao = getDao(MstPromoproductInfo.class);
        }
        return mstPromoproductInfoDao;
    }

    public Dao<MstPromotermInfo, String> getMstPromotermInfoDao() throws SQLException
    {

        if (mstPromotermInfoDao == null)
        {
            mstPromotermInfoDao = getDao(MstPromotermInfo.class);
        }
        return mstPromotermInfoDao;
    }

    public Dao<MstPromotionsM, String> getMstPromotionsMDao() throws SQLException
    {

        if (mstPromotionsMDao == null)
        {
            mstPromotionsMDao = getDao(MstPromotionsM.class);
        }
        return mstPromotionsMDao;
    }

    public Dao<MstPromotionstypeM, String> getMstPromotionstypeMDao() throws SQLException
    {

        if (mstPromotionstypeMDao == null)
        {
            mstPromotionstypeMDao = getDao(MstPromotionstypeM.class);
        }
        return mstPromotionstypeMDao;
    }

    public Dao<MstRouteM, String> getMstRouteMDao() throws SQLException
    {

        if (mstRouteMDao == null)
        {
            mstRouteMDao = getDao(MstRouteM.class);
        }
        return mstRouteMDao;
    }

    public Dao<MstShipmentledgerInfo, String> getMstShipmentledgerInfoDao() throws SQLException
    {

        if (mstShipmentledgerInfoDao == null)
        {
            mstShipmentledgerInfoDao = getDao(MstShipmentledgerInfo.class);
        }
        return mstShipmentledgerInfoDao;
    }

    public Dao<MstTerminalinfoM, String> getMstTerminalinfoMDao() throws SQLException
    {

        if (mstTerminalinfoMDao == null)
        {
            mstTerminalinfoMDao = getDao(MstTerminalinfoM.class);
        }
        return mstTerminalinfoMDao;
    }

    public Dao<MstVisitauthorizeInfo, String> getMstVisitauthorizeInfoDao() throws SQLException
    {

        if (mstVisitauthorizeInfoDao == null)
        {
            mstVisitauthorizeInfoDao = getDao(MstVisitauthorizeInfo.class);
        }
        return mstVisitauthorizeInfoDao;
    }

    public Dao<MstVisitM, String> getMstVisitMDao() throws SQLException
    {

        if (mstVisitMDao == null)
        {
            mstVisitMDao = getDao(MstVisitM.class);
        }
        return mstVisitMDao;
    }

    public Dao<MstVisitmemoInfo, String> getMstVisitmemoInfoDao() throws SQLException
    {

        if (mstVisitmemoInfoDao == null)
        {
            mstVisitmemoInfoDao = getDao(MstVisitmemoInfo.class);
        }
        return mstVisitmemoInfoDao;
    }

    public Dao<MstVistproductInfo, String> getMstVistproductInfoDao() throws SQLException
    {

        if (mstVistproductInfoDao == null)
        {
            mstVistproductInfoDao = getDao(MstVistproductInfo.class);
        }
        return mstVistproductInfoDao;
    }

    public Dao<MstWorksummaryInfo, String> getMstWorksummaryInfoDao() throws SQLException
    {

        if (mstWorksummaryInfoDao == null)
        {
            mstWorksummaryInfoDao = getDao(MstWorksummaryInfo.class);
        }
        return mstWorksummaryInfoDao;
    }

    public Dao<MstSynckvM, String> getMstSynckvMDao() throws SQLException
    {

        if (mstSynckvMDao == null)
        {
            mstSynckvMDao = getDao(MstSynckvM.class);
        }
        return mstSynckvMDao;
    }

    public Dao<MstQuestionsanswersInfo, String> getMstQuestionsanswersInfoDao() throws SQLException
    {

        if (mstQuestionsanswersInfoDao == null)
        {
            mstQuestionsanswersInfoDao = getDao(MstQuestionsanswersInfo.class);
        }
        return mstQuestionsanswersInfoDao;
    }

    public Dao<MstCollectionexerecordInfo, String> getMstCollectionexerecordInfoDao() throws SQLException
    {

        if (mstCollectionexerecordInfoDao == null)
        {
            mstCollectionexerecordInfoDao = getDao(MstCollectionexerecordInfo.class);
        }
        return mstCollectionexerecordInfoDao;
    }

    public Dao<MstPlanforuserM, String> getMstPlanforuserMDao() throws SQLException
    {

        if (mstPlanforuserMDao == null)
        {
            mstPlanforuserMDao = getDao(MstPlanforuserM.class);
        }
        return mstPlanforuserMDao;
    }

    public Dao<PadCheckaccomplishInfo, String> getPadCheckaccomplishInfoDao() throws SQLException
    {

        if (padCheckaccomplishInfoDao == null)
        {
            padCheckaccomplishInfoDao = getDao(PadCheckaccomplishInfo.class);
        }
        return padCheckaccomplishInfoDao;
    }

    public Dao<PadCheckstatusInfo, String> getPadCheckstatusInfoDao() throws SQLException
    {

        if (padCheckstatusInfoDao == null)
        {
            padCheckstatusInfoDao = getDao(PadCheckstatusInfo.class);
        }
        return padCheckstatusInfoDao;
    }

    public Dao<PadChecktypeM, String> getPadChecktypeMDao() throws SQLException
    {

        if (padChecktypeMDao == null)
        {
            padChecktypeMDao = getDao(PadChecktypeM.class);
        }
        return padChecktypeMDao;
    }

    public Dao<PadPlantempcheckM, String> getPadPlantempcheckMDao() throws SQLException
    {

        if (padPlantempcheckMDao == null)
        {
            padPlantempcheckMDao = getDao(PadPlantempcheckM.class);
        }
        return padPlantempcheckMDao;
    }

    public Dao<PadPlantempcollectionInfo, String> getPadPlantempcollectionInfoDao() throws SQLException
    {

        if (padPlantempcollectionInfoDao == null)
        {
            padPlantempcollectionInfoDao = getDao(PadPlantempcollectionInfo.class);
        }
        return padPlantempcollectionInfoDao;
    }

    public Dao<MstPlantemplateM, String> getMstPlantemplateMDao() throws SQLException
    {

        if (mstPlantemplateMDao == null)
        {
            mstPlantemplateMDao = getDao(MstPlantemplateM.class);
        }
        return mstPlantemplateMDao;
    }

    public Dao<MstPlantempcheckInfo, String> getMstPlantempcheckInfoDao() throws SQLException
    {
        if (mstPlantempcheckInfoDao == null)
        {
            mstPlantempcheckInfoDao = getDao(MstPlantempcheckInfo.class);
        }
        return mstPlantempcheckInfoDao;
    }

    public Dao<MstPlantempcollectionInfo, String> getMstPlantempcollectionInfoDao() throws SQLException
    {
        if (mstPlantempcollectionInfoDao == null)
        {
            mstPlantempcollectionInfoDao = getDao(MstPlantempcollectionInfo.class);
        }
        return mstPlantempcollectionInfoDao;
    }

    public Dao<MstPlancheckInfo, String> getMstPlancheckInfoDao() throws SQLException
    {
        if (mstPlancheckInfoDao == null)
        {
            mstPlancheckInfoDao = getDao(MstPlancheckInfo.class);
        }
        return mstPlancheckInfoDao;
    }

    public Dao<MstPlancollectionInfo, String> getMstPlancollectionInfoDao() throws SQLException
    {
        if (mstPlancollectionInfoDao == null)
        {
            mstPlancollectionInfoDao = getDao(MstPlancollectionInfo.class);
        }
        return mstPlancollectionInfoDao;
    }

    public Dao<CmmBoardM, String> getCmmBoardMDao() throws SQLException
    {
        if (cmmBoardMDao == null)
        {
            cmmBoardMDao = getDao(CmmBoardM.class);
        }
        return cmmBoardMDao;
    }

    public Dao<MstCheckmiddleInfo, String> getMstCheckmiddleInfoDao() throws SQLException
    {
        if (mstCheckmiddleInfoDao == null)
        {
            mstCheckmiddleInfoDao = getDao(MstCheckmiddleInfo.class);
        }
        return mstCheckmiddleInfoDao;
    }

    public Dao<PadCheckproInfo, String> getPadCheckproInfoDao() throws SQLException
    {
        if (padCheckproInfoDao == null)
        {
            padCheckproInfoDao = getDao(PadCheckproInfo.class);
        }
        return padCheckproInfoDao;
    }

    public Dao<MstPromomiddleInfo, String> getMstPromomiddleInfoDao() throws SQLException
    {
        if (mstPromomiddleInfoDao == null)
        {
            mstPromomiddleInfoDao = getDao(MstPromomiddleInfo.class);
        }
        return mstPromomiddleInfoDao;
    }

    public Dao<MstPowerfulchannelInfo, String> getMstPowerfulchannelInfoDao() throws SQLException
    {
        if (mstPowerfulchannelInfoDao == null)
        {
            mstPowerfulchannelInfoDao = getDao(MstPowerfulchannelInfo.class);
        }
        return mstPowerfulchannelInfoDao;
    }

    public Dao<MstPowerfulterminalInfo, String> getMstPowerfulterminalInfoDao() throws SQLException
    {
        if (mstPowerfulterminalInfoDao == null)
        {
            mstPowerfulterminalInfoDao = getDao(MstPowerfulterminalInfo.class);
        }
        return mstPowerfulterminalInfoDao;
    }

    public Dao<MstCmpsupplyInfo, String> getMstCmpsupplyInfoDao() throws SQLException
    {
        if (mstCmpsupplyInfoDao == null)
        {
            mstCmpsupplyInfoDao = getDao(MstCmpsupplyInfo.class);
        }
        return mstCmpsupplyInfoDao;
    }

    public Dao<MstPlanrouteInfo, String> getMstPlanrouteInfoDao() throws SQLException
    {
        if (mstPlanrouteInfoDao == null)
        {
            mstPlanrouteInfoDao = getDao(MstPlanrouteInfo.class);
        }
        return mstPlanrouteInfoDao;
    }

    public Dao<MstMonthtargetInfo, String> getMstMonthtargetInfoDao() throws SQLException
    {
        if (mstMonthtargetInfoDao == null)
        {
            mstMonthtargetInfoDao = getDao(MstMonthtargetInfo.class);
        }
        return mstMonthtargetInfoDao;
    }

    public Dao<MstCmpagencyInfo, String> getMstCmpagencyInfoDao() throws SQLException
    {
        if (mstCmpagencyInfo == null)
        {
            mstCmpagencyInfo = getDao(MstCmpagencyInfo.class);
        }
        return mstCmpagencyInfo;
    }

    public Dao<MstProductshowM, String> getMstProductshowMDao() throws SQLException
    {
        if (mstProductshowMDao == null)
        {
            mstProductshowMDao = getDao(MstProductshowM.class);
        }
        return mstProductshowMDao;
    }

    public Dao<MstShowpicInfo, String> getMstShowpicInfoDao() throws SQLException
    {
        if (mstShowpicInfoDao == null)
        {
            mstShowpicInfoDao = getDao(MstShowpicInfo.class);
        }
        return mstShowpicInfoDao;
    }

    public Dao<MstPlanWeekforuserM, String> getMstPlanWeekforuserMDao() throws SQLException
    {
        if (mstPlanWeekforuserMDao == null)
        {
            mstPlanWeekforuserMDao = getDao(MstPlanWeekforuserM.class);
        }
        return mstPlanWeekforuserMDao;
    }
    public Dao<MstCameraInfoM, String> getMstCameraiInfoMDao() throws SQLException
    {
    	if (mstCameraiInfoMDao == null)
    	{
    		mstCameraiInfoMDao = getDao(MstCameraInfoM.class);
    	}
    	return mstCameraiInfoMDao;
    }
    
    
    public Dao<MstAgencyKFM, String> getMstAgencyKFMDao() throws SQLException
    {
    	if (mstAgencyKFMDao == null)
    	{
    		mstAgencyKFMDao = getDao(MstAgencyKFM.class);
    	}
    	return mstAgencyKFMDao;
    }
    
    
    public Dao<MstTermLedgerInfo, String> getTermLedgerInfoDao() throws SQLException
    {
    	if (mstTermLedgerInfoDao == null)
    	{
    		mstTermLedgerInfoDao = getDao(MstTermLedgerInfo.class);
    	}
    	return mstTermLedgerInfoDao;
    }
    
    public Dao<MstPlanTerminalM, String> getMstPlanTerminalM() throws SQLException
    {
    	if (mstPlanTerminalMDao == null)
    	{
    		mstPlanTerminalMDao = getDao(MstPlanTerminalM.class);
    	}
    	return mstPlanTerminalMDao;
    }
    
    public Dao<MstBsData, String> getMstBsDataDao() throws SQLException
    {

        if (mstBsDataDao == null)
        {
        	mstBsDataDao = getDao(MstBsData.class);
        }
        return mstBsDataDao;
    }
    
    public Dao<MstGroupproductM, String> getMstGroupproductMDao() throws SQLException
    {

        if (mstGroupproductMDao == null)
        {
        	mstGroupproductMDao = getDao(MstGroupproductM.class);
        }
        return mstGroupproductMDao;
    }
    
    /**
     * 删除数据库
     * @param context
     * @return
     */
    public boolean deleteDatabase(Context context) {  
    	return context.deleteDatabase(DATABASE_NAME);  
    } 
    
}
