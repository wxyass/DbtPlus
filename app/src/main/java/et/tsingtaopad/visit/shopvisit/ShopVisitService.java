package et.tsingtaopad.visit.shopvisit;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import cn.com.benyoyo.manage.bs.IntStc.BsVisitEmpolyeeStc;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencysupplyInfoDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.tables.MstAgencysupplyInfo;
import et.tsingtaopad.db.tables.MstCheckexerecordInfo;
import et.tsingtaopad.db.tables.MstCheckexerecordInfoTemp;
import et.tsingtaopad.db.tables.MstCollectionexerecordInfo;
import et.tsingtaopad.db.tables.MstPromotermInfo;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.db.tables.MstVistproductInfo;
import et.tsingtaopad.db.tables.PadCheckaccomplishInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.sayhi.domain.MstTerminalInfoMStc;
import et.tsingtaopad.visit.shopvisit.term.domain.MstTermListMStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：ShopVisitService.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-5</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class ShopVisitService {
    
    private final String TAG = "ShopVisitService";
    
    protected Context context;
    protected Handler handler;

	private String prevVisitId;// 获取上次拜访主键

	private String prevVisitDate; // 上次拜访日期
    
    public ShopVisitService(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }
    
    /**
     * 获取拜访主表信息
     * 
     * @param visitId    拜访请表信息ID
     * @return
     */
    public MstVisitM findVisitById(String visitId) {
        
        MstVisitM visitInfo = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVisitMDao dao = helper.getDao(MstVisitM.class);
            visitInfo = dao.queryForId(visitId);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访主表DAO对象失败", e);
        }
        
        return visitInfo;
    }
    
    /**
     * 获取某终端的最新拜访记录信息
     * 
     * @param termId    终端ID
     * @param isEndVisit    是否结束拜访-true：上次结束拜访，false：所有拜访
     * @return
     */
    public MstVisitM findNewVisit(String termId,boolean isEndVisit) {
        
        MstVisitM visitM = null;
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        MstVisitMDao visitDao;
        try {
            visitDao = (MstVisitMDao)helper.getMstVisitMDao();
            QueryBuilder<MstVisitM, String> qBuilder = visitDao.queryBuilder();
            if(isEndVisit){
                qBuilder.where().eq("terminalkey", termId).and().eq("padisconsistent", "1");
            }else{
                qBuilder.where().eq("terminalkey", termId);
            }
            qBuilder.orderBy("visitdate", false);
            visitM = qBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访请表DAO异常", e);
        }
        return visitM;
    }
    
    /**
     * 获取某终端的最新拜访记录信息
     * 
     * @param termId    终端ID
     * @return
     */
    public MstVisitM findNewVst(String termId) {
        
        MstVisitM visitM = null;
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        MstVisitMDao visitDao;
        try {
            visitDao = (MstVisitMDao)helper.getMstVisitMDao();
            QueryBuilder<MstVisitM, String> qBuilder = visitDao.queryBuilder();
            qBuilder.where().eq("terminalkey", termId).and().eq("padisconsistent", "1");
            qBuilder.orderBy("visitdate", false);
            visitM = qBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访请表DAO异常", e);
        }
        return visitM;
    }
    
    /**
     * 获取终端信息
     * 
     * @param termId    终端ID
     * @return
     */
    public MstTerminalinfoM findTermById(String termId) {
        
        MstTerminalinfoM termInfo = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            termInfo = dao.queryForId(termId);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
        
        return termInfo;
    }
    
    /**
     * 获取终端信息 
     * 有错误
     * @param termId    终端ID
     * @return
     */
    public MstTerminalInfoMStc findTermStcById(String termId) {
        
        MstTerminalInfoMStc termInfo = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            termInfo = dao.findById(helper, termId);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
        
        return termInfo;
    }

    /**
     * 更新拜访离店时间及是否要上传标志
     * 
     * @param visitId       拜访主键
     * @param termId        终端主键
     * @param endDate       离店时间
     * @param uploadFlag    是否要上传标志
     */
    public void confirmUpload(String visitId, String termId, String endDate, String uploadFlag) {
        // 事务控制
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVisitMDao dao = helper.getDao(MstVisitM.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            
            // 根据拜访主键,更新拜访主表的结束时间,上传标记"1"
            StringBuffer buffer = new StringBuffer();
            buffer.append("update mst_visit_m set ");
            buffer.append("enddate=?, uploadflag=? ");
            buffer.append("where visitkey=? ");
            dao.executeRaw(buffer.toString(),
                    new String[]{endDate, uploadFlag, visitId});
            
            // 如果结束拜访, 则生成最新的终端指标结果记录表
            if (ConstValues.FLAG_1.equals(uploadFlag)) {
                
                // 只留年月日
                endDate = endDate.substring(0, 8);
                
                // 获取当前拜访的指标结果记录情况
                Dao<MstCheckexerecordInfoTemp, String> tempDao = helper.getMstCheckexerecordInfoTempDao();
                Dao<MstCheckexerecordInfo, String> checkInfoDao = helper.getMstCheckexerecordInfoDao();
                
                // 临时表 所有记录(根据visitkey)
                List<MstCheckexerecordInfoTemp> tempAllList = getCheckExerecordTempListByVisitid(tempDao, visitId);
                
                if (!CheckUtil.IsEmpty(tempAllList)) {
                	//非关联产品指标（临时） false表示没有productkey
                	List<MstCheckexerecordInfoTemp> noProTempList=getCheckExerecordTemp(tempAllList, false);
                	for (MstCheckexerecordInfoTemp tempItem:noProTempList) {
                		MstCheckexerecordInfo item = new MstCheckexerecordInfo();
                		item.setRecordkey(tempItem.getRecordkey());
                		item.setVisitkey(tempItem.getVisitkey());
                		item.setCheckkey(tempItem.getCheckkey());
                		item.setChecktype(tempItem.getChecktype());
                		item.setAcresult(tempItem.getAcresult());
                		item.setIscom(tempItem.getIscom());
                		item.setStartdate(endDate);
                		item.setEnddate(endDate);
                		item.setTerminalkey(tempItem.getTerminalkey());
                		item.setPadisconsistent(ConstValues.FLAG_0);
                		item.setPadcondate(null);
                		//item.setCreuser(ConstValues.loginSession.getUserCode());
                		item.setCreuser(PrefUtils.getString(context, "userCode", ""));
                		item.setUpdateuser(item.getCreuser());
                		checkInfoDao.create(item);
					}
                	//关联产品指标（临时） true表示有productkey
                	List<MstCheckexerecordInfoTemp> proTempList=getCheckExerecordTemp(tempAllList, true);

                	if(proTempList!=null && proTempList.size()>0){
                		//关联产品指标（临时）key：终端-指标-产品,value;指标集合
                		Map<String, MstCheckexerecordInfoTemp> proTempMap=new HashMap<String, MstCheckexerecordInfoTemp>();
                		for(MstCheckexerecordInfoTemp temp:proTempList){
                			String key=temp.getTerminalkey()+"-"+temp.getCheckkey()+"-"+temp.getProductkey();
                			if(!proTempMap.containsKey(key)){
                				proTempMap.put(key, temp);
                			}
                		}
                		proTempList.clear();
                		// 去重
                		for(String key:proTempMap.keySet()){
                			MstCheckexerecordInfoTemp temp=proTempMap.get(key);
                			proTempList.add(temp);
                		}
                		//获取当前终端各指标的最新指标结果状态（产品指标）
                		List<MstCheckexerecordInfo> proList=getCheckExerecordList(checkInfoDao, termId);
                		
                		// 实例化要最终指标结果集对象
                		boolean addFlag = false;
                        MstCheckexerecordInfo item;
                		for (MstCheckexerecordInfoTemp tempItem : proTempList) {// 遍历临时表的指标 MstCheckexerecordInfoTemp
                            	addFlag = false;
                                String acresultTemp=tempItem.getAcresult();
                                String checkKeyTemp=tempItem.getCheckkey();
                                if(CheckUtil.isBlankOrNull(acresultTemp) || "-1".equals(acresultTemp)){
                                    if(PropertiesUtil.getProperties("check_puhuo").equals(checkKeyTemp)){//#铺货状态
                                        acresultTemp="301";
                                    }else if(PropertiesUtil.getProperties("check_daoju").equals(checkKeyTemp)){//#道具生动化
                                        acresultTemp="307";
                                    }else if(PropertiesUtil.getProperties("check_chanpin").equals(checkKeyTemp)){//#产品生动化
                                        acresultTemp="309";
                                    }else if(PropertiesUtil.getProperties("check_bingdong").equals(checkKeyTemp)){//#冰冻化
                                        acresultTemp="311";
                                    }
                                }
							for (MstCheckexerecordInfo valueItem : proList) {// 遍历正式表的指标
																				// MstCheckexerecordInfo
								// 如果是同一产品的相同指标
								if (tempItem.getProductkey().equals(valueItem.getProductkey()) && tempItem.getCheckkey().equals(valueItem.getCheckkey())) {

									addFlag = true;
									// 删除产品的指标
									if (ConstValues.FLAG_1.equals(tempItem.getDeleteflag())) {
										// 如果结束日期与开始日期相同，则只更新不做插入数据
										if (endDate.equals(valueItem.getStartdate())) {
											valueItem.setAcresult(getDefaultAcresult(valueItem.getCheckkey()));// 默认指标
											valueItem.setStartdate(endDate);
											valueItem.setEnddate("30001201");
											valueItem.setSisconsistent(ConstValues.FLAG_0);
											valueItem.setScondate(null);
											valueItem.setPadisconsistent(ConstValues.FLAG_0);
											valueItem.setPadcondate(null);
											// valueItem.setCreuser(ConstValues.loginSession.getUserCode());
											valueItem.setCreuser(PrefUtils.getString(context, "userCode", ""));
											valueItem.setUpdateuser(valueItem.getCreuser());
											valueItem.setResultstatus(new BigDecimal(0));
											checkInfoDao.update(valueItem);
										} else {
											// 更新前一个值的结束时间
											valueItem.setEnddate(endDate);
											valueItem.setResultstatus(new BigDecimal(1));
											valueItem.setSisconsistent(ConstValues.FLAG_0);
											valueItem.setScondate(null);
											valueItem.setPadisconsistent(ConstValues.FLAG_0);
											valueItem.setPadcondate(null);
											// valueItem.setCreuser(ConstValues.loginSession.getUserCode());
											valueItem.setCreuser(PrefUtils.getString(context, "userCode", ""));
											valueItem.setUpdateuser(valueItem.getCreuser());
											valueItem.setResultstatus(new BigDecimal(1));
											checkInfoDao.update(valueItem);

											// 插入一新指标结果
											item = new MstCheckexerecordInfo();
											item.setRecordkey(FunUtil.getUUID());
											item.setVisitkey("temp");
											item.setProductkey(valueItem.getProductkey());
											item.setCheckkey(valueItem.getCheckkey());
											item.setChecktype(valueItem.getChecktype());
											item.setAcresult(getDefaultAcresult(valueItem.getCheckkey()));// 默认指标
											item.setIscom(valueItem.getIscom());
											item.setStartdate(endDate);
											item.setEnddate("30001201");
											item.setTerminalkey(valueItem.getTerminalkey());
											item.setSisconsistent(ConstValues.FLAG_0);
											item.setPadisconsistent(ConstValues.FLAG_0);
											// item.setCreuser(ConstValues.loginSession.getUserCode());
											item.setCreuser(PrefUtils.getString(context, "userCode", ""));
											item.setUpdateuser(item.getCreuser());
											item.setResultstatus(new BigDecimal(0));
											checkInfoDao.create(item);
										}
									} else {
										// 如果指标结果不同
										if (!acresultTemp.equals(valueItem.getAcresult())) {

											// 如果结束日期与开始日期相同，则只更新不做插入数据
											if (endDate.equals(valueItem.getStartdate())) {
												// 更新前一个值的结束时间
												valueItem.setAcresult(acresultTemp);
												valueItem.setSisconsistent(ConstValues.FLAG_0);
												valueItem.setScondate(null);
												valueItem.setPadisconsistent(ConstValues.FLAG_0);
												valueItem.setPadcondate(null);
												// valueItem.setCreuser(ConstValues.loginSession.getUserCode());
												valueItem.setCreuser(PrefUtils.getString(context, "userCode", ""));
												valueItem.setUpdateuser(valueItem.getCreuser());
												valueItem.setResultstatus(new BigDecimal(0));
												checkInfoDao.update(valueItem);
											} else {
												// 更新前一个值的结束时间
												valueItem.setEnddate(endDate);
												valueItem.setResultstatus(new BigDecimal(1));
												valueItem.setSisconsistent(ConstValues.FLAG_0);
												valueItem.setScondate(null);
												valueItem.setPadisconsistent(ConstValues.FLAG_0);
												valueItem.setPadcondate(null);
												// valueItem.setCreuser(ConstValues.loginSession.getUserCode());
												valueItem.setCreuser(PrefUtils.getString(context, "userCode", ""));
												valueItem.setUpdateuser(valueItem.getCreuser());
												valueItem.setResultstatus(new BigDecimal(1));
												checkInfoDao.update(valueItem);

												// 插入一新指标结果
												item = new MstCheckexerecordInfo();
												item.setRecordkey(FunUtil.getUUID());
												item.setVisitkey("temp");
												item.setProductkey(valueItem.getProductkey());
												item.setCheckkey(valueItem.getCheckkey());
												item.setChecktype(valueItem.getChecktype());
												item.setAcresult(acresultTemp);
												item.setIscom(valueItem.getIscom());
												item.setStartdate(endDate);
												item.setEnddate("30001201");
												item.setTerminalkey(valueItem.getTerminalkey());
												item.setSisconsistent(ConstValues.FLAG_0);
												item.setPadisconsistent(ConstValues.FLAG_0);
												// item.setCreuser(ConstValues.loginSession.getUserCode());
												item.setCreuser(PrefUtils.getString(context, "userCode", ""));
												item.setUpdateuser(item.getCreuser());
												item.setResultstatus(new BigDecimal(0));
												checkInfoDao.create(item);
											}
										}
									}
									break;
								}
							}
                                
                                // 如果之前没有，则为新境
                                if (!addFlag) {
                                    item = new MstCheckexerecordInfo();
                                    item.setRecordkey(FunUtil.getUUID());
                                    item.setVisitkey("temp");
                                    item.setProductkey(tempItem.getProductkey());
                                    item.setCheckkey(tempItem.getCheckkey());
                                    item.setChecktype(tempItem.getChecktype());
                                    item.setAcresult(acresultTemp);
                                    item.setIscom(tempItem.getIscom());
                                    item.setStartdate(endDate);
                                    item.setEnddate("30001201");
                                    item.setTerminalkey(tempItem.getTerminalkey());
                                    //item.setCreuser(ConstValues.loginSession.getUserCode());
                                    item.setCreuser(PrefUtils.getString(context, "userCode", ""));
                                    item.setUpdateuser(item.getCreuser());
                                    item.setResultstatus(new BigDecimal(0));
                                    checkInfoDao.create(item);
                                }
						}
                	}
                    // 删除临时数据
                    tempDao.delete(tempAllList);
				}
                delRepeatMstCollectionexerecordInfo(visitId);
            }
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "更新拜访离店时间及是否要上传标志失败", e);
            try {
                connection.rollback(null);
                ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    /***
     * 获取指标正式表集合
 	        获取当前终端各指标的最新指标结果状态
     * @return
     * @throws SQLException
     */
	@SuppressWarnings("unused")
	private List<MstCheckexerecordInfo> getCheckExerecordList(Dao<MstCheckexerecordInfo, String> valueDao,
    		String termId) throws SQLException{
        QueryBuilder<MstCheckexerecordInfo, String> valueQb = valueDao.queryBuilder();
        Where<MstCheckexerecordInfo, String> valueWr = valueQb.where();
        valueWr.eq("terminalkey", termId);
        valueWr.and();
        valueWr.eq("enddate", "30001201");
        valueWr.and();
        valueWr.ne("deleteflag", ConstValues.delFlag);
        valueWr.and();
        valueWr.isNotNull("productkey");
        List<MstCheckexerecordInfo> valueLst = valueQb.query();
        return valueLst;
    }
    
    /***
     * 获取当前指标集合
     * @param tempDao
     * @param visitId
     * @return
     * @throws SQLException
     */
	@SuppressWarnings("unused")
	private List<MstCheckexerecordInfoTemp> getCheckExerecordTempListByVisitid(Dao<MstCheckexerecordInfoTemp, String> tempDao,
    		String visitId) throws SQLException{
        QueryBuilder<MstCheckexerecordInfoTemp, String> tempValueQb = tempDao.queryBuilder();
        Where<MstCheckexerecordInfoTemp, String> tempValueWr = tempValueQb.where();
        tempValueWr.eq("visitkey", visitId);                
        tempValueQb.orderBy("terminalkey", true);
        tempValueQb.orderBy("checkkey", true);
        tempValueQb.orderBy("productkey", true);
        tempValueQb.orderBy("enddate", false);
        tempValueQb.orderBy("startdate", false);
        tempValueQb.orderBy("acresult", false);
        List<MstCheckexerecordInfoTemp> tempList = tempValueQb.query();
        return tempList;
    }
    

	/***
	 * 获取指标临时表集合
	 * @param tempList   指标临时表集合
	 * @param isPro      是否关联产品
	 */
    @SuppressWarnings({ "unchecked", "unused" })
	private List<MstCheckexerecordInfoTemp> getCheckExerecordTemp(List<MstCheckexerecordInfoTemp> tempList,boolean isPro){
    	List<MstCheckexerecordInfoTemp> temps=new ArrayList<MstCheckexerecordInfoTemp>();
    	for(MstCheckexerecordInfoTemp temp:tempList){
    		
    		if(isPro){// 关联产品
    			if (!CheckUtil.isBlankOrNull(temp.getProductkey())) {
    				temps.add(temp);
    			}
    		}else{// 非关联产品
    			if (CheckUtil.isBlankOrNull(temp.getProductkey())) {
    				temps.add(temp);
    			}
    		}
    	}
    	return temps;
    }
    
    
    /**
     * 配置本次拜访的相关表的数据
     * 
     * @param bundle    跳转到本页面时的传入参数
     * @return 
     */
    @SuppressWarnings("unchecked")
    public String configVisitData(Bundle bundle) {
        
        // 记录拜访开始日期
        String visitDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
        
        // 获取参数:终端信息、上次拜访主表对象  <termStc--终端信息> <visitM--上次拜访主表对象>
        MstTermListMStc termStc = (MstTermListMStc)bundle.getSerializable("termStc");
        //MstVisitM mstVisitM = (MstVisitM) bundle.getSerializable("visitM");
        // 获取最后一次拜访的信息(解决拜访时间为s,和拜访拍照后不能上传的问题)
        MstVisitM mstVisitM = findNewVisit(termStc.getTerminalkey(),false);
        
        // 事务控制
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstVisitM, String> visitDao = helper.getMstVisitMDao();
            Dao<MstVistproductInfo, String> proDao = helper.getMstVistproductInfoDao();
            Dao<MstCheckexerecordInfo, String> valueDao = helper.getMstCheckexerecordInfoDao();
            Dao<MstCheckexerecordInfoTemp, String> valueTempDao = helper.getMstCheckexerecordInfoTempDao();
            Dao<MstCollectionexerecordInfo, String> collectionDao = helper.getMstCollectionexerecordInfoDao();
            Dao<MstPromotermInfo, String> promDao = helper.getMstPromotermInfoDao();
            MstAgencysupplyInfoDao asupplyDao = helper.getDao(MstAgencysupplyInfo.class);
            Dao<MstAgencysupplyInfo, String> agencyDao = helper.getDao(MstAgencysupplyInfo.class);
            Dao<PadCheckaccomplishInfo,String> padCheckaccomplishInfoDao = helper.getPadCheckaccomplishInfoDao();
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            // 如果上次拜访为空，则是本终端的第一次拜访
            if (mstVisitM == null ) {
            	//MST_VISIT_M(拜访主表)
                mstVisitM = new MstVisitM();
                mstVisitM.setVisitkey(FunUtil.getUUID());
                mstVisitM.setTerminalkey(termStc.getTerminalkey());
                mstVisitM.setRoutekey(termStc.getRoutekey());
                //mstVisitM.setGridkey(ConstValues.loginSession.getGridId());
                mstVisitM.setGridkey(PrefUtils.getString(context, "gridId", ""));
                //mstVisitM.setAreaid(ConstValues.loginSession.getDisId());
                mstVisitM.setAreaid(PrefUtils.getString(context, "disId", ""));
                mstVisitM.setVisitdate(visitDate);
                //mstVisitM.setUserid(ConstValues.loginSession.getUserCode());
                mstVisitM.setUserid(PrefUtils.getString(context, "userCode", ""));
                mstVisitM.setStatus(ConstValues.FLAG_1);
                mstVisitM.setSisconsistent(ConstValues.FLAG_0);
                mstVisitM.setPadisconsistent(ConstValues.FLAG_0);
                mstVisitM.setOrderbyno(ConstValues.FLAG_0);// 同一次是0 返回时会改为1
                //mstVisitM.setCreuser(ConstValues.loginSession.getUserCode());
                mstVisitM.setCreuser(PrefUtils.getString(context, "userCode", ""));
                //mstVisitM.setUpdateuser(ConstValues.loginSession.getUserCode());
                mstVisitM.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                mstVisitM.setUploadFlag(ConstValues.FLAG_0);
                visitDao.create(mstVisitM);
                // 终端返回时将终端拜访状态置为1 进入拜访时终端拜访状态置为0(默认为0) 确定上传时移除终端拜访状态
				PrefUtils.putString(context, "back"+termStc.getTerminalkey(), "0");
                //初始化拜访产品表
                addProductInfo(mstVisitM.getTerminalkey(),mstVisitM.getVisitkey(),agencyDao,proDao);
                createMstCheckexerecordInfoTemp(mstVisitM.getVisitkey(),visitDate, mstVisitM, valueDao,
                        valueTempDao); 
            // 如果上次是“结束拜访”的数据，则本次拜访用上次拜访的数据为基础数据
            } else if (ConstValues.FLAG_1.equals(mstVisitM.getUploadFlag())) {
            	
            	BsVisitEmpolyeeStc emp = new BsVisitEmpolyeeStc();
                // 获取上次拜访主键
                prevVisitId = mstVisitM.getVisitkey();
                
                // 上次拜访日期
                prevVisitDate = mstVisitM.getVisitdate();
                
                // 如果上次拜访日期与本次拜访不是同一天(竞品拜访记录清空)
                if (!visitDate.substring(0, 8).equals(prevVisitDate.substring(0, 8))) {
                    mstVisitM.setRemarks("");
                }
                
                // 复制拜访主表数据，MST_VISIT_M(拜访主表)
                mstVisitM.setVisitkey(FunUtil.getUUID());
                mstVisitM.setLongitude("");
                mstVisitM.setLatitude("");
                mstVisitM.setVisitdate(visitDate);
                mstVisitM.setEnddate(null);
                //mstVisitM.setUserid(ConstValues.loginSession.getUserCode());
                mstVisitM.setUserid(PrefUtils.getString(context, "userCode", ""));
                mstVisitM.setStatus(ConstValues.FLAG_1);
                mstVisitM.setSisconsistent(ConstValues.FLAG_0);
                mstVisitM.setScondate(null);
                mstVisitM.setPadisconsistent(ConstValues.FLAG_0);
                mstVisitM.setOrderbyno(ConstValues.FLAG_0);// 同一次是0 返回时会改为1
                mstVisitM.setPadcondate(null);
                mstVisitM.setUploadFlag(ConstValues.FLAG_0);
                mstVisitM.setCredate(null);
                mstVisitM.setUpdatetime(null);
                //mstVisitM.setGridkey(ConstValues.loginSession.getGridId());
                mstVisitM.setGridkey(PrefUtils.getString(context, "gridId", ""));
                //mstVisitM.setAreaid(ConstValues.loginSession.getDisId());
                mstVisitM.setAreaid(PrefUtils.getString(context, "disId", ""));
                visitDao.create(mstVisitM);
                // 终端返回时将终端拜访状态置为1 进入拜访时终端拜访状态置为0(默认为0) 确定上传时移除终端拜访状态
				PrefUtils.putString(context, "back"+termStc.getTerminalkey(), "0");
                
                // 复制进销存及聊竞品数据。MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)   
                List<MstVistproductInfo> proLst = proDao.queryForEq("visitkey", prevVisitId);
                if (!CheckUtil.IsEmpty(proLst)) {
                	//修复进销存界面因经销商不给该定格供货所以产品不显示 ,但是查指标界面还显示此商品的BUG
                	List<MstAgencysupplyInfo> agencysupply = asupplyDao.agencysupply(helper,mstVisitM.getTerminalkey());
                	// 通过终端获取此终端供货关系    
//                	List<MstAgencysupplyInfo> agencysupplyList=getAgencySupplyInfoList(mstVisitM.getTerminalkey(), agencyDao);
                    Set<String> agencysupplySet=new HashSet<String>();
                    for(MstAgencysupplyInfo info:agencysupply){
                    	String key=info.getUpperkey()+info.getProductkey();
                    	agencysupplySet.add(key);
                    }
                    
                	for (MstVistproductInfo product : proLst) {
                        if (!ConstValues.FLAG_1.equals(product.getDeleteflag())) {
                        	String key=product.getAgencykey()+product.getProductkey();
                        	if(CheckUtil.isBlankOrNull(product.getProductkey()) || agencysupplySet.contains(key)){
                        		product.setRecordkey(FunUtil.getUUID());
                        		product.setVisitkey(mstVisitM.getVisitkey());
                        		
                        		// 如果上次拜访日期与本次拜访不是同一天
                        		if (!visitDate.substring(0, 8)
                        				.equals(prevVisitDate.substring(0, 8))) {
                        			//上周期进货总量(改为订单量)
                        			product.setPurcnum(null);
                        			//上次库存
                        			product.setPronum(product.getCurrnum());
                        			//本次库存(当前库存)
                        			product.setCurrnum(null);
                        			//日销量
                        			product.setSalenum(null);
                        			//描述（竞品描述）// 竞品描述不再隔天清零 ywm 20160407
                        			//product.setRemarks("");
                        		}
                        		product.setSisconsistent(ConstValues.FLAG_0);
                        		product.setScondate(null);
                        		product.setPadisconsistent(ConstValues.FLAG_0);
                        		product.setPadcondate(null);
                        		product.setDeleteflag(ConstValues.FLAG_0);
                        		product.setCredate(null);
                        		product.setUpdatetime(null);
                        		proDao.create(product);
                        	}
                        }
                    }
                }
                
                
                
                // 如果上次拜访日期与本次拜访是同一天
                if (visitDate.substring(0, 8).equals(prevVisitDate.substring(0, 8))) {
                	
                	// 复制指标结果表，MST_CHECKEXERECORD_INFO(拜访指标执行记录表)
                    createMstCheckexerecordInfoTemp(prevVisitId,visitDate, mstVisitM, valueDao,valueTempDao);
                	
                	
                    // 复制指标结果表，MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)
                    QueryBuilder<MstCollectionexerecordInfo, String> collectionQB = collectionDao.queryBuilder();
                    Where<MstCollectionexerecordInfo, String> collectionWhere=collectionQB.where();
                    collectionWhere.eq("visitkey", prevVisitId);
                    collectionWhere.and();
                    collectionWhere.ne("deleteflag", ConstValues.FLAG_1);
                    collectionWhere.and();
                    collectionWhere.isNotNull("checkkey");
                    collectionWhere.and();
                    collectionWhere.ne("checkkey", "");
                    collectionQB.orderBy("productkey", true);
                    collectionQB.orderBy("colitemkey", true);
                    collectionQB.orderBy("checkkey", false);
                    collectionQB.orderBy("updatetime", false);
                    List<MstCollectionexerecordInfo> collectionList =collectionQB.query();
                    
                    if (!CheckUtil.IsEmpty(collectionList)) {
                        //数据库查询
                        //根据渠道查询拜访指标执行采集项记录表,按采集项+产品放入map
                        QueryBuilder<PadCheckaccomplishInfo, String> valueQb = padCheckaccomplishInfoDao.queryBuilder();
                        Where<PadCheckaccomplishInfo, String> valueWr = valueQb.where();
                        valueWr.eq("minorchannel",termStc.getMinorchannel());
                        List<PadCheckaccomplishInfo> valueLst = valueQb.query();
                        Map<String,String> colitemProductkeys =new HashMap<String,String>();
                        if(valueLst!=null){
                            for(int i=0;i<valueLst.size();i++){
                                colitemProductkeys.put(valueLst.get(i).getColitemkey()+valueLst.get(i).getProductkey(), "");
                            }
                        }
                        
                        List<String> list=new ArrayList<String>();
                        //根据有效的采集项和产品进行复制
                        for (MstCollectionexerecordInfo item : collectionList) {
                            String key=item.getColitemkey()+item.getProductkey();
                            if(colitemProductkeys.containsKey(key)){//此产品存在于指标采集项模板
                                if(!list.contains(key)){//防止重复去重
                                    list.add(key);
                                    item.setColrecordkey(FunUtil.getUUID());
                                    item.setVisitkey(mstVisitM.getVisitkey());
                                    item.setSisconsistent(ConstValues.FLAG_0);
                                    item.setScondate(null);
                                    item.setPadisconsistent(ConstValues.FLAG_0);
                                    item.setPadcondate(null);
                                    item.setDeleteflag(ConstValues.FLAG_0);
                                    item.setCredate(null);
                                    item.setUpdatetime(null);
                                    collectionDao.create(item);
                                }
                            }
                        }
                    }
                }else{
                	// 当天第一次拜访  只复制非关联产品的指标
                	createMstCheckexerecordInfoTemp1(prevVisitId, visitDate, mstVisitM, valueDao, valueTempDao);
                }
                
                // 复制终端参加活动状态表，MST_PROMOTERM_INFO(终端参加活动信息表)
                String currDay = DateUtil.formatDate(new Date(), "yyyyMMdd");
                QueryBuilder<MstPromotionsM, String> promoQb = helper.getMstPromotionsMDao().queryBuilder();
                Where<MstPromotionsM, String> promoWr = promoQb.where();
                promoWr.and(promoWr.ge("enddate", currDay), promoWr.le("startdate", currDay));
                List<MstPromotionsM> promoLst = promoQb.query();
                List<String> promoIdLst = FunUtil.getPropertyByName(promoLst, "promotkey", String.class);
                List<MstPromotermInfo> promTermLst = promDao.queryForEq("visitkey", prevVisitId);
                if (!CheckUtil.IsEmpty(promTermLst)) {
                    for (MstPromotermInfo item : promTermLst) {
                        if (!ConstValues.FLAG_1.equals(item.getDeleteflag()) 
                                        && promoIdLst.contains(item.getPtypekey())) {
                            item.setRecordkey(FunUtil.getUUID());
                            item.setVisitkey(mstVisitM.getVisitkey());
                            item.setStartdate(visitDate);
                            item.setSisconsistent(ConstValues.FLAG_0);
                            item.setScondate(null);
                            item.setPadisconsistent(ConstValues.FLAG_0);
                            item.setPadcondate(null);
                            item.setDeleteflag(ConstValues.FLAG_0);
                            item.setCredate(null);
                            item.setUpdatetime(null);
                            promDao.create(item);
                        }
                    }
                }
                
            // 如果上次是没结束的拜访，则只需要修改本次拜访的开始时间
            } else {
            	
            	//if("1".equals(mstVisitM.getOrderbyno())){// 返回了,需更新时间
            	if("1".equals(PrefUtils.getString(context, "back"+termStc.getTerminalkey(), "1"))){// 返回了,需更新时间 (默认需要改时间)
            		mstVisitM.setVisitdate(visitDate);
            		mstVisitM.setOrderbyno("0");
                    visitDao.createOrUpdate(mstVisitM);
                    
                    // 终端返回时将终端拜访状态置为1 进入拜访时终端拜访状态置为0(默认为0) 确定上传时移除终端拜访状态
    				PrefUtils.putString(context, "back"+termStc.getTerminalkey(), "0");
                    
                    /**
                     * 删除供货关系表中的 失效产品(原因: 修改后台失效一个供货关系,查指标页面仍旧显示的问题)
                     * 
                     * 1 查询供货关系表(用户巡店时,失效一个供货关系)
                     * 2 查询产品表
                     * 3 若两者表记录不一致 做删除(产品表删除)
                     */
                    
                    List<MstAgencysupplyInfo> agencysupply = asupplyDao.agencysupply(helper,mstVisitM.getTerminalkey());
                    // 此处添加productkey不为null的条件
                   
                    /*
                    Map<String, Object> KeyMap = new HashMap<String, Object>();
                    KeyMap.put("visitkey", mstVisitM.getVisitkey());
                    KeyMap.put("cmpproductkey", "");
                    List<MstVistproductInfo> proLst = proDao.queryForFieldValues(KeyMap);
                    List<MstVistproductInfo> proLst2 = proDao.queryForFieldValues(KeyMap);
                    */
                    //--------------------------------------------
                    QueryBuilder<MstVistproductInfo, String> collectionQB = proDao.queryBuilder();
                    Where<MstVistproductInfo, String> collectionWhere=collectionQB.where();
                    collectionWhere.eq("visitkey", mstVisitM.getVisitkey());
                    collectionWhere.and();
                    collectionWhere.isNotNull("productkey");
                    List<MstVistproductInfo> proLst =collectionQB.query();
                    List<MstVistproductInfo> proLst2 =collectionQB.query();
                    //--------------------------------------------
                    
                    
                    
                    
                    
                    
                    List<MstVistproductInfo> proLst3 = proDao.queryForEq("visitkey", mstVisitM.getVisitkey());
                    //List<MstVistproductInfo> proLst2 = proDao.queryForEq("visitkey", mstVisitM.getVisitkey());
                    if(proLst.size()>agencysupply.size()){
                    	for (MstVistproductInfo mstVistproductInfo : proLst) { 
                    		for (MstAgencysupplyInfo mstAgencysupplyInfo : agencysupply) {
                    			if(mstVistproductInfo.getProductkey()!=null&&mstAgencysupplyInfo.getProductkey()!=null){
                    				if(mstVistproductInfo.getProductkey().equals(mstAgencysupplyInfo.getProductkey())){
                        				proLst2.remove(mstVistproductInfo);
                        			}
                    			}
    						}
    					}
                    	for (MstVistproductInfo mstVistproductInfo : proLst2) {
                    		proDao.deleteById(mstVistproductInfo.getRecordkey());
    					}
                    }
                
            	}else{// shopvisitactivity页面被回收 时间不变
            		// 返回了
            		//mstVisitM.setVisitdate(visitDate);
                    //visitDao.createOrUpdate(mstVisitM);
            		
            		// 终端返回时将终端拜访状态置为1 进入拜访时终端拜访状态置为0(默认为0) 确定上传时移除终端拜访状态
    				PrefUtils.putString(context, "back"+termStc.getTerminalkey(), "0");
                    
                    /**
                     * 删除供货关系表中的 失效产品(原因: 修改后台失效一个供货关系,查指标页面仍旧显示的问题)
                     * 
                     * 1 查询供货关系表(用户巡店时,失效一个供货关系)
                     * 2 查询产品表
                     * 3 若两者表记录不一致 做删除(产品表删除)
                     */
                    
                    List<MstAgencysupplyInfo> agencysupply = asupplyDao.agencysupply(helper,mstVisitM.getTerminalkey());
                    // 此处添加productkey不为null的条件
                   
                    /*
                    Map<String, Object> KeyMap = new HashMap<String, Object>();
                    KeyMap.put("visitkey", mstVisitM.getVisitkey());
                    KeyMap.put("cmpproductkey", "");
                    List<MstVistproductInfo> proLst = proDao.queryForFieldValues(KeyMap);
                    List<MstVistproductInfo> proLst2 = proDao.queryForFieldValues(KeyMap);
                    */
                    //--------------------------------------------
                    QueryBuilder<MstVistproductInfo, String> collectionQB = proDao.queryBuilder();
                    Where<MstVistproductInfo, String> collectionWhere=collectionQB.where();
                    collectionWhere.eq("visitkey", mstVisitM.getVisitkey());
                    collectionWhere.and();
                    collectionWhere.isNotNull("productkey");
                    List<MstVistproductInfo> proLst =collectionQB.query();
                    List<MstVistproductInfo> proLst2 =collectionQB.query();
                    //--------------------------------------------
                    
                    
                    
                    
                    
                    
                    List<MstVistproductInfo> proLst3 = proDao.queryForEq("visitkey", mstVisitM.getVisitkey());
                    //List<MstVistproductInfo> proLst2 = proDao.queryForEq("visitkey", mstVisitM.getVisitkey());
                    if(proLst.size()>agencysupply.size()){
                    	for (MstVistproductInfo mstVistproductInfo : proLst) { 
                    		for (MstAgencysupplyInfo mstAgencysupplyInfo : agencysupply) {
                    			if(mstVistproductInfo.getProductkey()!=null&&mstAgencysupplyInfo.getProductkey()!=null){
                    				if(mstVistproductInfo.getProductkey().equals(mstAgencysupplyInfo.getProductkey())){
                        				proLst2.remove(mstVistproductInfo);
                        			}
                    			}
    						}
    					}
                    	for (MstVistproductInfo mstVistproductInfo : proLst2) {
                    		proDao.deleteById(mstVistproductInfo.getRecordkey());
    					}
                    }
                
            	
            	}
            }
            
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "重复拜访配置数据出错", e);
            try {
                connection.rollback(null);
                ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        
        return mstVisitM.getVisitkey();
    }

    /**
     * @param prevVisitId 上次拜访主键key
     * @param visitDate
     * @param mstVisitM
     * @param valueDao
     * @param valueTempDao
     * @throws SQLException
     */
    private void createMstCheckexerecordInfoTemp(String prevVisitId,String visitDate,
            MstVisitM mstVisitM, Dao<MstCheckexerecordInfo, String> valueDao,
            Dao<MstCheckexerecordInfoTemp, String> valueTempDao)
            throws SQLException {
    	createMstCheckexerecordInfoTemp1(prevVisitId, visitDate, mstVisitM, valueDao, valueTempDao);
    	createMstCheckexerecordInfoTemp2(prevVisitId, visitDate, mstVisitM, valueDao, valueTempDao);
    }
    
    /**
     * 插入非关联产品指标表
     * @param prevVisitId 上次拜访主键key
     * @param valueDao
     * @param valueTempDao
     * @throws SQLException
     */
    private void createMstCheckexerecordInfoTemp1(String prevVisitId,String visitDate,
            MstVisitM mstVisitM, Dao<MstCheckexerecordInfo, String> valueDao,
            Dao<MstCheckexerecordInfoTemp, String> valueTempDao)
            throws SQLException {
        // 复制指标结果表，MST_CHECKEXERECORD_INFO(拜访指标执行记录表)
        QueryBuilder<MstCheckexerecordInfo, String> valueQb = valueDao.queryBuilder();
        Where<MstCheckexerecordInfo, String> valueWr = valueQb.where();
        valueWr.eq("visitkey", prevVisitId);
        valueWr.and();
        valueWr.isNull("productkey");
        List<MstCheckexerecordInfo> valueLst = valueQb.query();
        if (!CheckUtil.IsEmpty(valueLst)) {
            MstCheckexerecordInfoTemp itemTemp;
            for (MstCheckexerecordInfo item : valueLst) {
                itemTemp = new MstCheckexerecordInfoTemp();
                itemTemp.setRecordkey(FunUtil.getUUID());
                itemTemp.setVisitkey(mstVisitM.getVisitkey());
                itemTemp.setProductkey(item.getProductkey());
                itemTemp.setCheckkey(item.getCheckkey());
                itemTemp.setAcresult(item.getAcresult());
                itemTemp.setStartdate(item.getStartdate());
                itemTemp.setEnddate(visitDate);
                itemTemp.setTerminalkey(item.getTerminalkey());
                valueTempDao.create(itemTemp);
            }
        }
    }
    

    /**
     * 插入产品指标表
     * @param prevVisitId 上次拜访主键key
     * @param visitDate
     * @param mstVisitM
     * @param valueDao
     * @param valueTempDao
     * @throws SQLException
     */
    private void createMstCheckexerecordInfoTemp2(String prevVisitId,String visitDate,
            MstVisitM mstVisitM, Dao<MstCheckexerecordInfo, String> valueDao,
            Dao<MstCheckexerecordInfoTemp, String> valueTempDao)
            throws SQLException {
        // 复制指标结果表，MST_CHECKEXERECORD_INFO(拜访指标执行记录表)
        QueryBuilder<MstCheckexerecordInfo, String> valueQb = valueDao.queryBuilder();
        Where<MstCheckexerecordInfo, String> valueWr = valueQb.where();
        valueWr.eq("terminalkey", mstVisitM.getTerminalkey());
        valueWr.and();
        valueWr.eq("enddate", "30001201");
        valueWr.and();
        valueWr.isNotNull("productkey");
        valueWr.and();
        valueWr.ne("deleteflag", ConstValues.delFlag);
 
        valueQb.orderBy("terminalkey", true);
        valueQb.orderBy("checkkey", true);
        valueQb.orderBy("productkey", true);
        valueQb.orderBy("updatetime", false);
        valueQb.orderBy("siebelid", false);
        valueQb.orderBy("acresult", false);
        List<MstCheckexerecordInfo> valueLst = valueQb.query();
        Map<String, MstCheckexerecordInfo> map=new HashMap<String, MstCheckexerecordInfo>();
        if (!CheckUtil.IsEmpty(valueLst)) {
            MstCheckexerecordInfoTemp itemTemp;
            for (MstCheckexerecordInfo item : valueLst) {
            	String key=item.getTerminalkey()+"-"+item.getCheckkey()+"-"+item.getProductkey();
            	if(!map.containsKey(key)){
            		map.put(key, item);
            		itemTemp = new MstCheckexerecordInfoTemp();
            		itemTemp.setRecordkey(FunUtil.getUUID());
            		itemTemp.setVisitkey(mstVisitM.getVisitkey());
            		itemTemp.setProductkey(item.getProductkey());
            		itemTemp.setCheckkey(item.getCheckkey());
            		itemTemp.setAcresult(item.getAcresult());
            		itemTemp.setStartdate(item.getStartdate());
            		itemTemp.setEnddate(visitDate);
            		itemTemp.setTerminalkey(item.getTerminalkey());
            		valueTempDao.create(itemTemp);
            	}else{
            		item.setDeleteflag(ConstValues.delFlag);
            		item.setSisconsistent(ConstValues.FLAG_0);
            		item.setPadisconsistent(ConstValues.FLAG_0);
            		item.setResultstatus(new BigDecimal(1));
            		valueDao.update(item);
            	}
            }
        }
    }
    
    /***
     * 去除拜访指标采集项重复
     * @param visitkey
     */
    public void delRepeatMstCollectionexerecordInfo(String visitkey){
    	try{
            Dao<MstCollectionexerecordInfo, String> collectionDao = DatabaseHelper.getHelper(context).getMstCollectionexerecordInfoDao();
            QueryBuilder<MstCollectionexerecordInfo, String> collectionQB = collectionDao.queryBuilder();
            Where<MstCollectionexerecordInfo, String> collectionWhere=collectionQB.where();
            collectionWhere.eq("visitkey", visitkey);
            collectionWhere.and();
            collectionWhere.ne("deleteflag", ConstValues.FLAG_1);
            collectionQB.orderBy("productkey", true);
            collectionQB.orderBy("colitemkey", true);
            collectionQB.orderBy("checkkey", false);
            collectionQB.orderBy("updatetime", false);
            List<MstCollectionexerecordInfo> collectionList =collectionQB.query();
            List<String> list=new ArrayList<String>();
            for(MstCollectionexerecordInfo collection:collectionList){
            	if(CheckUtil.isBlankOrNull(collection.getCheckkey())){
            		collectionDao.delete(collection);
            	}else{
            		String key=collection.getColitemkey()+collection.getProductkey()+collection.getCheckkey();
            		if(!list.contains(key)){
            			list.add(key);
            		}else{
            			collectionDao.delete(collection);
            		}
            	}
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * 去除重复产品指标表
     * @param mstVisitM
     * @throws SQLException
     */
    public void delRepeatMstCheckexerecordInfo(MstVisitM mstVisitM){
    	 AndroidDatabaseConnection connection = null;
         try {
        	 DatabaseHelper helper = DatabaseHelper.getHelper(context);
             Dao<MstCheckexerecordInfo, String> valueDao = helper.getMstCheckexerecordInfoDao();
             connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
             connection.setAutoCommit(false);
             
             QueryBuilder<MstCheckexerecordInfo, String> valueQb = valueDao.queryBuilder();
             Where<MstCheckexerecordInfo, String> valueWr = valueQb.where();
             valueWr.eq("terminalkey", mstVisitM.getTerminalkey());
             valueWr.and();
             valueWr.eq("enddate", "30001201");
             valueWr.and();
             valueWr.isNotNull("productkey");
             valueWr.and();
             valueWr.ne("deleteflag", ConstValues.delFlag);
      
             valueQb.orderBy("terminalkey", true);
             valueQb.orderBy("checkkey", true);
             valueQb.orderBy("productkey", true);
             valueQb.orderBy("updatetime", false);
             valueQb.orderBy("siebelid", false);
             valueQb.orderBy("acresult", false);
             List<MstCheckexerecordInfo> valueLst = valueQb.query();
             Map<String, MstCheckexerecordInfo> map=new HashMap<String, MstCheckexerecordInfo>();
             if (!CheckUtil.IsEmpty(valueLst)) {
                 for (MstCheckexerecordInfo item : valueLst) {
                 	String key=item.getTerminalkey()+"-"+item.getCheckkey()+"-"+item.getProductkey();
                 	if(!map.containsKey(key)){
                 		map.put(key, item);
                 	}else{
                 		item.setDeleteflag(ConstValues.delFlag);
                 		item.setSisconsistent(ConstValues.FLAG_0);
                 		item.setPadisconsistent(ConstValues.FLAG_0);
                 		item.setResultstatus(new BigDecimal(1));
                 		valueDao.update(item);
                 	}
                 }
             }
             connection.commit(null);
         }  catch (Exception e) {
             Log.e(TAG, "查看拜访记录去除重复指标出错", e);
             try {
                 connection.rollback(null);
                 ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
             } catch (SQLException e1) {
                 e1.printStackTrace();
             }
         }
        
    }
    
    /**
     * 获取当天计划拜访终端个数
     * @return
     */
    public int queryPlanTermNum() {
        int termNum = 0;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVisitMDao dao = helper.getDao(MstVisitM.class);
            termNum = dao.queryPlanTermNum(helper, 
                    DateUtil.formatDate(new Date(), "yyyyMMdd"));
            
        } catch (SQLException e) {
            Log.e(TAG, "获取当天计划拜访终端个数失败", e);
        }
        return termNum;
    }
    
    /**
     * 根据拜访日期获取当天结束且有效的拜访的终端 个数
     */
    public int queryVisitTermNum() {
        int termNum = 0;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVisitMDao dao = helper.getDao(MstVisitM.class);
            termNum = dao.queryVisitTermNum(helper, 
                    DateUtil.formatDate(new Date(), "yyyyMMdd"));
            
        } catch (SQLException e) {
            Log.e(TAG, "获取当天结束且有效的拜访的终端 个数", e);
        }
        return termNum;
    }

	/***
	 * 更新拜访离店时间及是否要上传标志
	 * @param visitId
	 * @param longitude 经度
	 * @param latitude  维度
	 * @param gpsStatus
	 */
    public void updateGps(String visitId,double longitude, double latitude, String gpsStatus) {
        try {
        	if(0!=latitude && 0!=longitude){
        		DatabaseHelper helper = DatabaseHelper.getHelper(context);
        		MstVisitMDao dao = helper.getDao(MstVisitM.class);
        		StringBuffer buffer = new StringBuffer();
        		buffer.append("update mst_visit_m set longitude=?, ");
        		buffer.append("latitude=?, gpsstatus=? ");
        		buffer.append("where visitkey=? ");
        		String[] args = new String[4];
        		args[0] = String.valueOf(longitude);
        		args[1] = String.valueOf(latitude);
        		args[2] = gpsStatus;
        		args[3] = visitId;
        		dao.executeRaw(buffer.toString(), args);
        	}
            
        } catch (SQLException e) {
            Log.e(TAG, "更新拜访GPS信息失败", e);
        } 
    }

    /**
     * 更新拜访离店时间及是否要上传标志
     * 
     * @param visitId       拜访主键
     * @param location       离店时间
     * @param gpsStatus    是否要上传标志
     */
    public void updateGps(String visitId, Location location, String gpsStatus) {
        if (location != null || !CheckUtil.isBlankOrNull(gpsStatus)) {
            try {
                DatabaseHelper helper = DatabaseHelper.getHelper(context);
                MstVisitMDao dao = helper.getDao(MstVisitM.class);
                StringBuffer buffer = new StringBuffer();
                buffer.append("update mst_visit_m set longitude=?, ");
                buffer.append("latitude=?, gpsstatus=? ");
                buffer.append("where visitkey=? ");
                String[] args = new String[4];
                if (location != null) {
                    args[0] = String.valueOf(location.getLongitude());
                    args[1] = String.valueOf(location.getLatitude());
                }
                args[2] = gpsStatus;
                args[3] = visitId;
                dao.executeRaw(buffer.toString(), args);
                
            } catch (SQLException e) {
                Log.e(TAG, "更新拜访GPS信息失败", e);
            } 
        }
    }
    
    /***
     * 初始化拜访产品表
     * @param termid
     * @param Vistid
     * @param agencyDao
     * @param proDao
     */
    private  void addProductInfo(String termid ,String Vistid, Dao<MstAgencysupplyInfo, String> agencyDao,Dao<MstVistproductInfo, String> proDao ){
            try {
                List<MstAgencysupplyInfo> valueLst = getAgencySupplyInfoList(termid, agencyDao);
                if (!CheckUtil.IsEmpty(valueLst)) {
                   MstVistproductInfo  itemTemp;
                   for (MstAgencysupplyInfo item : valueLst) {
                       itemTemp =  new MstVistproductInfo();
                       itemTemp.setRecordkey(FunUtil.getUUID());
                       itemTemp.setVisitkey(Vistid);
                       itemTemp.setProductkey(item.getProductkey());
                       itemTemp.setAgencykey(item.getUpperkey());
                       itemTemp.setCredate(DateUtil.getNow());
                       itemTemp.setUpdatetime(DateUtil.getNow());
                       //itemTemp.setCreuser(ConstValues.loginSession.getUserCode());
                       itemTemp.setCreuser(PrefUtils.getString(context, "userCode", ""));
                       //itemTemp.setUpdateuser(ConstValues.loginSession.getUserCode());
                       itemTemp.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
                       itemTemp.setPurcprice(0.00);
                       itemTemp.setRetailprice(0.00);
                       itemTemp.setPronum(0.00);
                       itemTemp.setCurrnum(0.00);
                       itemTemp.setSalenum(0.00);
                       proDao.create(itemTemp);
                   }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    /***
     * 通过终端获取此终端供货关系
     * @param termid
     * @param agencyDao
     * @return
     */
    private List<MstAgencysupplyInfo> getAgencySupplyInfoList(String termid,Dao<MstAgencysupplyInfo, String> agencyDao){
    	List<MstAgencysupplyInfo> list=new ArrayList<MstAgencysupplyInfo>();
    	try {
        	QueryBuilder<MstAgencysupplyInfo, String> qb = agencyDao.queryBuilder();
        	Where<MstAgencysupplyInfo, String> where = qb.where();
        	where.eq("lowerkey",termid);
        	where.and();
        	where.eq("status","0");
        	where.and();
        	where.eq("lowertype","2");
            list = qb.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	return list;
    }
    
    /***
     * 获取指标值得默认值
     * @param checkKey
     * @return
     */
    private String getDefaultAcresult(String checkKey){
        String acresult="";
        if(PropertiesUtil.getProperties("check_puhuo").equals(checkKey)){//#铺货状态
            acresult="301";
        }else if(PropertiesUtil.getProperties("check_daoju").equals(checkKey)){//#道具生动化
            acresult="307";
        }else if(PropertiesUtil.getProperties("check_chanpin").equals(checkKey)){//#产品生动化
            acresult="309";
        }else if(PropertiesUtil.getProperties("check_bingdong").equals(checkKey)){//#冰冻化
            acresult="311";
        }
        return acresult;
    }
    
    public void saveorUpdate(MstVisitM visitM){
    	// 事务控制
    	
    	try {
    		/*DatabaseHelper helper = DatabaseHelper.getHelper(context);
    		Dao<MstVisitM, String> visitDao = helper.getMstVisitMDao();
            visitDao.createOrUpdate(visitM);*/
            
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVisitMDao dao = helper.getDao(MstVisitM.class);
            
            dao.createOrUpdate(visitM);
            MstVisitM termInfo = dao.queryForId(visitM.getVisitkey());
            String sd = "baocunchucuo";
		} catch (Exception e) {
			// TODO: handle exception
			String sd = "baocunchucuo";
		}
        
    }
}
