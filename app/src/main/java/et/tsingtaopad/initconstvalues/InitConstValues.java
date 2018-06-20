package et.tsingtaopad.initconstvalues;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencyinfoMDao;
import et.tsingtaopad.db.dao.MstCmpCompanyMDao;
import et.tsingtaopad.db.dao.MstRouteMDao;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.tables.CmmAreaM;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.db.tables.MstCmpcompanyM;
import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.db.tables.MstSynckvM;
import et.tsingtaopad.db.tables.MstVistproductInfo;
import et.tsingtaopad.db.tables.PadChecktypeM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.visit.shopvisit.line.domain.MstRouteMStc;
    
/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：InitConstValues.java</br>
 * 作者：hongen   </br>
 * 创建时间：2013-12-21</br>      
 * 功能描述: 静态变量初始化</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class InitConstValues extends Thread {
    private final String TAG = "InitConstValues";
    private Context context;
    public InitConstValues(Context context) {
        this.context = context;
    }
    
    @Override
    public void run() {
        
        // 初始化Kv表数据
        this.initSyncKv();
        
        // 初始化拜访路线
        this.initMstRoute();
        
        // 初始化区域信息
        this.initAreaInfo();
                                                
        // 初始化数据字典
        this.initDataDictionary();

        // 初始化指标、指标状态
        this.initCheckTypeStatus();  
        
        // 初始化经销商及经销商可销售产品的树级关系对象
        this.initAgencyMine();
        
        // 初始化竞品公司及产品关系
        this.initAgencyVie();
        
        // 初始化定格可拜访经销商及经销商可销售产品的树级关系对象
        this.initVisitAgencyPro();
        
        // 初始化权限图标
        this.initAuthority();
        
        // 初始化次渠道列表
        initSecondSell();
        
        Log.i(TAG, "初始化静态变量完成");
    }
    
    /**
	 * 初始化权限图标
	 */
	private void initAuthority() {
		// 初始化按钮图片对应
		HashMap<String, Integer> AuthorityMap = new HashMap<String, Integer>();
		AuthorityMap.put("001", R.drawable.bt_visit_shopvist);// 巡店拜访
		AuthorityMap.put("002", R.drawable.bt_visit_addterm);// 新增终端
		AuthorityMap.put("003", R.drawable.bt_visit_termdetail);// 终端进货明细
		AuthorityMap.put("004", R.drawable.bt_visit_agency);// 经销商拜访
		AuthorityMap.put("005", R.drawable.bt_visit_store);// 经销商库存
		AuthorityMap.put("006", R.drawable.bt_visit_agencykf);// 经销商开发
		AuthorityMap.put("007", R.drawable.bt_visit_sync);// 数据同步
		AuthorityMap.put("008", R.drawable.bt_visit_termtaizhang);// 终端进货台账
		AuthorityMap.put("009", R.drawable.bt_business_product);// 产品展示
		AuthorityMap.put("010", R.drawable.bt_visit_addterm_other);// 其它
		ConstValues.AuthorityMap = AuthorityMap;

	}

	/**
     * 获取并初始化拜访线路
     * @return
     */
    @SuppressWarnings("unchecked")
    public void initMstRoute(){
        
        List<MstRouteM> mstRouteList=new ArrayList<MstRouteM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);            
            Dao<MstRouteM, String> mstRouteMDao = helper.getMstRouteMDao();
            QueryBuilder<MstRouteM, String> qBuilder = mstRouteMDao.queryBuilder();
            Where<MstRouteM, String> where = qBuilder.where();
            where.and(where.or(where.isNull("deleteflag"), where.eq("deleteflag", "0")),
                    where.or(where.isNull("routestatus"), where.eq("routestatus", "0"))
                    );
            qBuilder.orderBy("orderbyno", true).orderBy("routename", true);
            mstRouteList=qBuilder.query();
        } catch (Exception e) {
            Log.e(TAG, "获取线路信息失败", e);
        }

        // 添加请选择
        MstRouteM mstRouteM = new MstRouteMStc();
        mstRouteM.setRoutekey("-1");
        mstRouteM.setRoutename("请选择");
        mstRouteList.add(0, mstRouteM);    					
        ConstValues.lineLst=mstRouteList;
    }
    
    /**
     * 获取省市县数据，并组建成相应的树级结构
     * @return
     */
    @SuppressWarnings("unchecked")
    public void initAreaInfo () {
        List<CmmAreaM> areaLst = new ArrayList<CmmAreaM>();
        MstAgencyinfoM agencyM = new MstAgencyinfoM();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            
            // 获取省市省
            Dao<CmmAreaM, String> cmmAreaMDao = helper.getCmmAreaMDao();
            QueryBuilder<CmmAreaM, String> qBuilder = cmmAreaMDao.queryBuilder();
            Where<CmmAreaM, String> where = qBuilder.where();
            where.or(where.isNull("deleteflag"), where.eq("deleteflag", "0"));
            qBuilder.orderBy("areacode", true).orderBy("orderbyno", true);
            areaLst = qBuilder.query();
            
            // 获取当前定格的主供货商
            MstAgencyinfoMDao agencyDao = helper.getDao(MstAgencyinfoM.class);
            //agencyM = agencyDao.queryMainAgency(helper, ConstValues.loginSession.getGridId());
            agencyM = agencyDao.queryMainAgency(helper, PrefUtils.getString(context, "gridId", ""));
        } catch (Exception e) {
             Log.e(TAG, "获取经销售商信息失败", e);
        }
        
        // 获取当前定格经销商
        ConstValues.provLst=  queryChildForArea(
                          areaLst, "-1", 1, agencyM.getProvince());    
    }
    
    /**
     *  递归遍历区域树
     *  
     * @param areaLst       数据源
     * @param parentId      父ID
     * @param level         当前等级
     * @param provinceId    当前等级
     * @return
     */
    private List<KvStc> queryChildForArea(List<CmmAreaM> areaLst,
                            String parentId, int level, String provinceId) {
        List<KvStc> kvLst = null;
        if (level <= 3) {
            kvLst = new ArrayList<KvStc>();
            KvStc kvItem = new KvStc();
            int nextLevel = level + 1;
            for (CmmAreaM areaItem : areaLst) {
                if (parentId.equals(FunUtil.isBlankOrNullTo(areaItem.getParentcode(), "-1"))) {
                    
                    // 省级限制
                    /*if (level == 1 && !areaItem.getAreacode().equals(provinceId)) {
                        continue;
                    }*/
                    
                    // 过滤 市的全省选项
                    if (level == 2 && areaItem.getAreaname().equals("全省")) {
                        continue;
                    }
                    
                    kvItem = new KvStc(areaItem.getAreacode(),
                            areaItem.getAreaname(), areaItem.getParentcode());
                    kvItem.setChildLst(queryChildForArea(areaLst, areaItem.getAreacode(), nextLevel, ""));
                    kvLst.add(kvItem);
                }
            }
            
            // 添加请选择
            if (level <= 3) {
                kvItem = new KvStc("-1", "请选择", "-1");
            }
            if (level <= 2) {
                kvItem.getChildLst().add(new KvStc("-1", "请选择", "-1"));
            }
            if (level <= 1) {
                kvItem.getChildLst().get(0).getChildLst().add(new KvStc("-1", "请选择", "-1"));
            }
            kvLst.add(0,kvItem);
        }
        return kvLst;
    }
    
    /**
     * 获取数据字典信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public void  initDataDictionary () {
        List<CmmDatadicM> dataDicLst = new ArrayList<CmmDatadicM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            QueryBuilder<CmmDatadicM, String> qBuilder = 
                                helper.getCmmDatadicMDao().queryBuilder();
            Where<CmmDatadicM, String> where = qBuilder.where();
            where.or(where.isNull("deleteflag"), where.eq("deleteflag", "0"));
            qBuilder.orderBy("parentcode", true).orderBy("orderbyno", true);
            dataDicLst = qBuilder.query();
        } catch (SQLException e) {
            Log.e(TAG, "初始化数据字典失败", e);
        }        
        
        this.initDataDicByAreaType(dataDicLst);
        this.initDataDicByAreaType2(dataDicLst);
        this.initDataDicByTermLevel(dataDicLst);
        this.initDataDicBySellChannel(dataDicLst);
        this.initDataDicByVisitPosition(dataDicLst);
    }    
    
    /**
     * 初始化终端区域类型
     */
    private void initDataDicByAreaType(List<CmmDatadicM> dataDicLst) {
        
        // 获取数据字典表中区域字典对应的父ID
        String areaType=PropertiesUtil.getProperties("datadic_areaType");
        
        List<KvStc> kvLst = new ArrayList<KvStc>();
        for (CmmDatadicM item : dataDicLst) {
            if (areaType.equals(item.getParentcode())) {
                kvLst.add(new KvStc(item.getDiccode()
                        , item.getDicname(), item.getParentcode()));
            } else if (kvLst.size() > 0) {
                break;
            }
        }
        kvLst.add(0, new KvStc("-1", "请选择", "-1"));
        ConstValues.dataDicMap.put("areaTypeLst", kvLst);
    }
    
    /**
     * 初始化终端区域类型(没有请选择)
     */
    private void initDataDicByAreaType2(List<CmmDatadicM> dataDicLst) {
        
        // 获取数据字典表中区域字典对应的父ID
        String areaType=PropertiesUtil.getProperties("datadic_areaType");
        
        List<KvStc> kvLst = new ArrayList<KvStc>();
        for (CmmDatadicM item : dataDicLst) {
            if (areaType.equals(item.getParentcode())) {
                kvLst.add(new KvStc(item.getDiccode()
                        , item.getDicname(), item.getParentcode()));
            } else if (kvLst.size() > 0) {
                break;
            }
        }
        ConstValues.dataDicMap.put("areaTypeLst2", kvLst);
    }
    /**
     * 初始化拜访对象职位(老板老板娘)
     */
    private void initDataDicByVisitPosition(List<CmmDatadicM> dataDicLst) {
    	
    	// 获取数据字典表中区域字典对应的父ID
    	String areaType=PropertiesUtil.getProperties("datadic_visitposition");
    	
    	List<KvStc> kvLst = new ArrayList<KvStc>();
    	for (CmmDatadicM item : dataDicLst) {
    		if (areaType.equals(item.getParentcode())) {
    			kvLst.add(new KvStc(item.getDiccode()
    					, item.getDicname(), item.getParentcode()));
    		} else if (kvLst.size() > 0) {
    			break;
    		}
    	}
    	kvLst.add(0, new KvStc("-1", "请选择", "-1"));
    	ConstValues.dataDicMap.put("visitPositionLst", kvLst);
    }
    
    /**
     * 初始化终端等级
     */
    private void initDataDicByTermLevel(List<CmmDatadicM> dataDicLst) {
        
        // 获取数据字典表中区域字典对应的父ID
        String termLevel = PropertiesUtil.getProperties("datadic_termLevel");
        
        List<KvStc> kvLst = new ArrayList<KvStc>();
        for (CmmDatadicM item : dataDicLst) {
            if (termLevel.equals(item.getParentcode())) {
                kvLst.add(new KvStc(item.getDiccode(),
                        item.getDicname(), item.getParentcode()));
            } else if (kvLst.size() > 0) {
                break;
            }
        }
        kvLst.add(0, new KvStc("-1", "请选择", "-1"));
        ConstValues.dataDicMap.put("levelLst", kvLst);
    }
    
    /**
     * 初始化销售渠道
     */
    private void initDataDicBySellChannel(List<CmmDatadicM> dataDicLst) {

        // 获取数据字典表中区域字典对应的父ID
        String sellChannel = PropertiesUtil.getProperties("datadic_sellChannel");
        
        ConstValues.dataDicMap.put("sellChannelLst", 
                    queryChildForDataDic(dataDicLst, sellChannel, 1));
    }
    
    /**
     *  递归遍历数据字典
     *  
     * @param dataDicLst    数据源
     * @param parentCode    父ID
     * @param level         当前等级  1 
     * @return
     */
    private List<KvStc> queryChildForDataDic(
                List<CmmDatadicM> dataDicLst, String parentCode, int level) {
        List<KvStc> kvLst = null;
        if (level <= 3) {
            kvLst = new ArrayList<KvStc>();
            KvStc kvItem = new KvStc();
            int nextLevel = level + 1;
            for (CmmDatadicM dataItem : dataDicLst) {
                if (parentCode.equals(dataItem.getParentcode())) {
                    kvItem = new KvStc(dataItem.getDiccode(),dataItem.getDicname(), dataItem.getParentcode());
                    kvItem.setChildLst(queryChildForDataDic( dataDicLst, dataItem.getDiccode(), nextLevel));
                    kvLst.add(kvItem);
                }
            }

            // 添加请选择
            if (level <= 3) {
                kvItem = new KvStc("-1", "请选择", "-1");
            }
            if (level <= 2) {
                kvItem.getChildLst().add(new KvStc("-1", "请选择", "-1"));
            }
            if (level <= 1) {
                kvItem.getChildLst().get(0).getChildLst().add(new KvStc("-1", "请选择", "-1"));
            }
            kvLst.add(0,kvItem);
        }
        return kvLst;
    }
    
    /**
     * 初始化经销商及经销商可销售产品的树级关系对象
     */
    public void initAgencyMine(){
        List<KvStc> agencySellProList=new ArrayList<KvStc>();        
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);        
            MstAgencyinfoMDao agencyInfoMDao = helper.getDao(MstAgencyinfoM.class);
            agencySellProList = agencyInfoMDao.agencySellProQuery(helper);
        } catch (Exception e) {
             Log.e(TAG, "初始化经销商及经销商可销售产品的树级关系对象失败", e);
        }
        ConstValues.agencyMineLst = agencySellProList;
    }
    
    /**
     * 初始化竞品公司及竞品的树级关系对象
     */
    public void initAgencyVie() {
        List<KvStc> agencySellProList=new ArrayList<KvStc>();        
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);        
            MstCmpCompanyMDao cmpCompanyMDao = helper.getDao(MstCmpcompanyM.class);
            agencySellProList = cmpCompanyMDao.agencySellProQuery(helper);
        } catch (Exception e) {
            Log.e(TAG, "初始化竞品公司及竞品的树级关系对象失败", e);
        }
        ConstValues.agencyVieLst = agencySellProList;
    }
        
    /**
     * 初始化定格可拜访经销商及产品的树级关系对象
     */
    public void initVisitAgencyPro() {

        List<KvStc> visitAgencyProList=new ArrayList<KvStc>();        
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);        
            MstAgencyinfoMDao agencyInfoMDao = helper.getDao(MstAgencyinfoM.class);
            visitAgencyProList = agencyInfoMDao.queryVisitAgencyPro(helper);
        } catch (Exception e) {
             Log.e(TAG, "初始化定格可拜访经销商及产品的树级关系对象失败", e);
        }
        ConstValues.agencyVisitLst = visitAgencyProList;
    }
    
    /**
     * 初始化指标、指标值树级关系 对象
     */
    public void initCheckTypeStatus() {        
        List<KvStc> checkTypeStatusList=new ArrayList<KvStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            PadChecktypeMDao dao = helper.getDao(PadChecktypeM.class);
            checkTypeStatusList= dao.queryCheckTypeStatus(helper, ConstValues.FLAG_0);            
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访指标失败", e);
            e.printStackTrace();
        }
        ConstValues.indexLst = checkTypeStatusList;
    }
    
    /**
     * 初始化Kv表数据
     */
    public void initSyncKv() {
        List<MstSynckvM> syncKvLst = new ArrayList<MstSynckvM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstSynckvM, String> dao = helper.getMstSynckvMDao();
            syncKvLst = dao.queryForAll();          
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访指标失败", e);
            e.printStackTrace();
        }
        
        Map<String, MstSynckvM> kvMap = new HashMap<String, MstSynckvM>();
        for (MstSynckvM item : syncKvLst) {
            kvMap.put(item.getTablename(), item);
        }
        ConstValues.kvMap = kvMap;
    }
    
    // 初始化次渠道
    public void initSecondSell(){
    	List<KvStc> dataDicLst = new ArrayList<KvStc>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstRouteMDao dao = helper.getDao(MstRouteM.class);
            
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
			dataDicLst = dao.querySecondSell(helper);
            
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }
        ConstValues.secSellLst =dataDicLst;
        
    }
    
}    
    