package et.tsingtaopad.visit.agencyvisit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencyinfoMDao;
import et.tsingtaopad.db.dao.MstAgencyvisitMDao;
import et.tsingtaopad.db.tables.MstAgencyinfoM;
import et.tsingtaopad.db.tables.MstAgencytransferInfo;
import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.db.tables.MstInvoicingInfo;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.PinYin4jUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.agencyvisit.domain.InOutSaveStc;
import et.tsingtaopad.visit.agencyvisit.domain.TransferStc;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：LedgerService.java</br> 作者：吴欣伟 </br>
 * 创建时间：2013-12-2</br> 功能描述: 经销商拜访进销存台账和调货台账业务逻辑</br> 版本 V 1.0</br> 修改履历</br> 日期
 * 原因 BUG号 修改人 修改版本</br>
 */
@SuppressLint("DefaultLocale")
public class LedgerService {

	private final String TAG = "ledgerService";

	private Context context;

	public LedgerService(Context context) {
		this.context = context;
	}

	/**
	 * 获取分经销拜访主表的结构体
	 * 
	 * @param agencyKey
	 *            经销商主键
	 * @param visitDate
	 *            拜访时间
	 * @return
	 */
	public Map<String, Object> getMstAgencyvisitM(String agencyKey, String visitDate) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 取该经销商最新一次拜访数据-> 作为本次拜访数据(下面修改下主键即可) -> 放进map中
		MstAgencyvisitM visitM = findNewest(agencyKey);
		// 取该经销商最新一次拜访数据-> 作为上次拜访数据 -> 放进map中
		MstAgencyvisitM visitM2 = findNewest(agencyKey);
		String prevVisitKey = "";
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			MstAgencyvisitMDao visitDao = (MstAgencyvisitMDao) helper.getMstAgencyvisitMDao();

			// MstAgencyvisitM表中有这个经销商的上次拜访
			if(visitM2!=null){
			    map.put("preDateVisit", visitM2);
			}
			
			// 没有上次拜访,或者今天没拜访
            if (visitM == null || !visitM.getAgevisitdate().substring(0, 8).equals(visitDate.substring(0, 8))) {
				visitM = new MstAgencyvisitM();
				visitM.setAgevisitkey(FunUtil.getUUID());
				visitM.setAgencykey(agencyKey);
				//visitM.setGridkey(ConstValues.loginSession.getGridId());
				visitM.setGridkey(PrefUtils.getString(context, "gridId", ""));
				visitM.setAgevisitdate(visitDate);
				visitM.setPadisconsistent(ConstValues.FLAG_0);
				visitM.setUploadFlag(ConstValues.FLAG_0);
				//visitM.setCreuser(ConstValues.loginSession.getUserCode());
				visitM.setCreuser(PrefUtils.getString(context, "userCode", ""));
				//visitM.setUpdateuser(ConstValues.loginSession.getUserCode());
				visitM.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
				prevVisitKey = visitM.getAgevisitkey();
				visitDao.create(visitM);
			} else {
				prevVisitKey = visitM.getAgevisitkey();
				if (ConstValues.FLAG_1.equals(visitM.getPadisconsistent())) {
					visitM.setAgevisitkey(FunUtil.getUUID());
					visitM.setUploadFlag(ConstValues.FLAG_0);
					visitM.setPadisconsistent(ConstValues.FLAG_0);
				}
				visitM.setAgevisitdate(visitDate);
				visitDao.createOrUpdate(visitM);
			}
			map.put("LsMstAgencyvisitM", visitM);
			map.put("LsprevVisitKey", prevVisitKey);
		} catch (SQLException e) {
			Log.e(TAG, "获取MstAgencyvisitMDao异常", e);
		}
		return map;
	}

	/**
	 * 点击确定结束拜访更新分经销拜访主表的一条数据
	 * 
	 * @param visitM
	 *            分经销拜访的一条数据
	 * @return
	 */
	public void saveMstAgencyvisitM(MstAgencyvisitM visitM) {
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			MstAgencyvisitMDao visitDao = (MstAgencyvisitMDao) helper.getMstAgencyvisitMDao();
			visitDao.createOrUpdate(visitM);
		} catch (SQLException e) {
			Log.e(TAG, "获取MstAgencyvisitMDao异常", e);
		}
	}

	/**
	 * 获取某经销商拜访的最新拜访记录信息
	 * 
	 * @param agencykey
	 *            经销商主键
	 * @return
	 */
	private MstAgencyvisitM findNewest(String agencykey) {

		MstAgencyvisitM mstAgencyvisitM = null;
		DatabaseHelper helper = DatabaseHelper.getHelper(context);
		MstAgencyvisitMDao visitDao;
		try {
			visitDao = (MstAgencyvisitMDao) helper.getMstAgencyvisitMDao();
			QueryBuilder<MstAgencyvisitM, String> qBuilder = visitDao.queryBuilder();
			qBuilder.where().eq("agencykey", agencykey);
			qBuilder.orderBy("agevisitdate", false);
			mstAgencyvisitM = qBuilder.queryForFirst();
		} catch (SQLException e) {
			Log.e(TAG, "获取分经销商拜访主表DAO异常", e);
		}
		return mstAgencyvisitM;
	}

	/**
	 * 查询调货台账中可以选择调货的经销商数据
	 * 
	 * @param agencyKey 经销商ID
	 * @return
	 */
	public List<MstAgencyinfoM> tagAgencyQuery(String agencyKey) {
		List<MstAgencyinfoM> tagAgencyStcLst = new ArrayList<MstAgencyinfoM>();
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(context);
			MstAgencyinfoMDao dao = helper.getDao(MstAgencyinfoM.class);
			tagAgencyStcLst = dao.queryBuilder()
			        .where().ne("agencykey", agencyKey).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tagAgencyStcLst;
	}

	/**
	 * 查询进销存台账和调货台账中的当然经销商的产品数据
	 * 
	 * @param asupplykey
	 *            经销商ID
	 * @return
	 */
	public List<KvStc> proQuery(String asupplykey) {

		List<KvStc> proStcLst = new ArrayList<KvStc>();
		if (!CheckUtil.IsEmpty(ConstValues.agencyVisitLst)) {
			for (KvStc kvStc : ConstValues.agencyVisitLst) {
				if (kvStc.getKey().equals(asupplykey)) {
					proStcLst = kvStc.getChildLst();
					break;
				}
			}
		}

		return proStcLst;
	}

    /**
     * 进销存台账页面的展示数据
     * 
     * @param agencykey
     *            经销商ID
     * @param agevisitkey
     *            经销商拜访主键
     * @param visitM
     *            分经销商拜访记录
     * @return
     */
    public List<InOutSaveStc> getInOutSave(String agencykey,
            String agevisitkey, MstAgencyvisitM visitM,Map<String, Object> mapLedger) {
        MstAgencyvisitM preDateVisit=null;
        if(mapLedger.get("preDateVisit")!=null){
            preDateVisit=(MstAgencyvisitM) mapLedger.get("preDateVisit");
        }
        //获取上次拜访数据
        List<MstInvoicingInfo> LastMstInfoLst= getLastInOutSaveList(agencykey);
        Map<String, Double> map=new HashMap<String, Double>();
        for(MstInvoicingInfo info:LastMstInfoLst){
            map.put(info.getProductkey(), info.getStorenum());
        }
        List<InOutSaveStc> iosLst = inOutSaveProQuery(agencykey);
        
        // 获取数据,不管是上次成功上传的,还是上次没上传就返回的
        List<MstInvoicingInfo> mstInfoLst = inOutSaveQueryDB(agevisitkey, visitM);

        String visitDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
        for (InOutSaveStc item : iosLst) {
            for (MstInvoicingInfo itemJ : mstInfoLst) {
                if (item.getProductkey().equals(itemJ.getProductkey())) {
                    item.setInvoicingkey(itemJ.getInvoicingkey());
                    item.setAgevisitkey(itemJ.getAgevisitkey());
                    item.setAgencykey(itemJ.getAgencykey());
                    item.setProductkey(itemJ.getProductkey());
                    item.setInnum(itemJ.getInnum());
                    item.setDirecout(itemJ.getDirecout());
                    item.setIndirecout(itemJ.getIndirecout());
                    item.setStorenum(itemJ.getStorenum());
                    item.setPrestorenum(itemJ.getPrestorenum());
                    
                    item.setSelfsales(itemJ.getSelfsales());
                    item.setUnselfsales(itemJ.getUnselfsales());
                    item.setOthersales(itemJ.getOthersales());
                    
                    if(preDateVisit!=null){
                        if(preDateVisit.getAgevisitdate().substring(0, 8).equals(visitDate.substring(0, 8))){//上次拜访与本次拜访不是同一天
//                            item.setStorenumTemp(itemJ.getStorenum()); 
                            if(map.containsKey(item.getProductkey())){
                                item.setStorenumTemp(map.get(item.getProductkey()));
                            }
//                            if(ConstValues.FLAG_1.equals(preDateVisit.getPadisconsistent())){
//                                item.setStorenumTemp(itemJ.getStorenum()); 
//                            }else{
//                                item.setStorenumTemp(itemJ.getPrestorenum()); 
//                            }
                        }
                    }
                    break;
                }
            }
            if(preDateVisit!=null){
                if(!preDateVisit.getAgevisitdate().substring(0, 8).equals(visitDate.substring(0, 8))){//上次拜访与本次拜访不是同一天
                    if(map.containsKey(item.getProductkey())){
                        item.setStorenumTemp(map.get(item.getProductkey()));
                        item.setPrestorenum(map.get(item.getProductkey()));
                    }
                }else{
                    if(map.containsKey(item.getProductkey())){
//                        item.setStorenumTemp(map.get(item.getProductkey()));
//                        item.setPrestorenum(map.get(item.getProductkey()));
                    }
                }
            }
        }
        return iosLst;
    }
    
    /**
  * 查询上次经销商进销存信息列表
  * 
  * @param agencykey
  *            经销商拜访主键
  * @return
  */
 public List<MstInvoicingInfo> getLastInOutSaveList(String agencykey) {
     List<MstInvoicingInfo> list = new ArrayList<MstInvoicingInfo>();
     try {
         Dao<MstAgencyvisitM, String> mstAgencyvisitMDao= DatabaseHelper
                 .getHelper(context).getMstAgencyvisitMDao();
         QueryBuilder<MstAgencyvisitM, String> qb = mstAgencyvisitMDao
                 .queryBuilder();
         Where<MstAgencyvisitM, String> where=qb.where();
         where.eq("agencykey", agencykey);
         where.and();
         where.eq("padisconsistent", ConstValues.FLAG_1);
         qb.orderBy("agevisitdate", false);
         MstAgencyvisitM visit=qb.queryForFirst();
         if(visit!=null){
             Dao<MstInvoicingInfo, String> mstInvoicingInfoDao = DatabaseHelper
                     .getHelper(context).getMstInvoicingInfoDao();
             QueryBuilder<MstInvoicingInfo, String> queryBuilder = mstInvoicingInfoDao
                     .queryBuilder();
             queryBuilder.where().eq("agevisitkey", visit.getAgevisitkey());
             list = queryBuilder.query();
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return list;
 }

	/**
	 * 查询经销商进销存信息表没数据时显示此数据
	 * 
	 * @param agencykey
	 *            经销商ID
	 * @return
	 */
	public List<InOutSaveStc> inOutSaveProQuery(String agencykey) {
		List<InOutSaveStc> iosLst = new ArrayList<InOutSaveStc>();
		List<KvStc> proStcLst = proQuery(agencykey);
		for (KvStc proStc : proStcLst) {
		    InOutSaveStc iosStc = new InOutSaveStc();
		    iosStc.setProductkey(proStc.getKey());
		    iosStc.setProName(proStc.getValue());
		    iosLst.add(iosStc);
		}
		return iosLst;
	}

	/**
	 * 查询数据库获得的经销商进销存信息列表
	 * 
	 * @param agevisitkey
	 *            经销商拜访主键
	 * @return
	 */
	public List<MstInvoicingInfo> inOutSaveQueryDB(String agevisitkey, MstAgencyvisitM visitM) {
		List<MstInvoicingInfo> query = new ArrayList<MstInvoicingInfo>();

        try {
            Dao<MstInvoicingInfo, String> mstInvoicingInfoDao = DatabaseHelper
                    .getHelper(context).getMstInvoicingInfoDao();
            QueryBuilder<MstInvoicingInfo, String> queryBuilder = mstInvoicingInfoDao
                    .queryBuilder();
            queryBuilder.where().eq("agevisitkey", agevisitkey);
            query = queryBuilder.query();
            if (!agevisitkey.equals(visitM.getAgevisitkey())) {
                for (MstInvoicingInfo info : query) {
                    info.setInvoicingkey(FunUtil.getUUID());
                    info.setAgevisitkey(visitM.getAgevisitkey());
//                    mstInvoicingInfoDao.createOrUpdate(info);
                }
            }
        } catch (SQLException e) {

			e.printStackTrace();
		}
		return query;
	}

	/**
	 * 进销存台账数据保存
	 * 
	 * @param iosStcLst
	 *            要保存的进销存数据
	 * @param visitM
	 *            分经销商拜访主表结构体
	 */
	public void saveIOSData(List<InOutSaveStc> iosStcLst, MstAgencyvisitM visitM, String prevAgevisitkey,Map<String, InOutSaveStc> iosStcMap,boolean isOk) {
		try {
			Dao<MstInvoicingInfo, String> dao = DatabaseHelper.getHelper(context).getMstInvoicingInfoDao();
			for (InOutSaveStc iosStc : iosStcLst) {
				MstInvoicingInfo mstInfo = new MstInvoicingInfo();
				if (CheckUtil.isBlankOrNull(iosStc.getInvoicingkey())) {
					iosStc.setInvoicingkey(FunUtil.getUUID());
				}
				mstInfo.setInvoicingkey(iosStc.getInvoicingkey());
				mstInfo.setAgevisitkey(visitM.getAgevisitkey());
				mstInfo.setAgencykey(visitM.getAgencykey());
				mstInfo.setProductkey(iosStc.getProductkey());
				mstInfo.setRecorddate(visitM.getAgevisitdate().substring(0, 8));
				if (iosStc.getInnum() == null) {
					iosStc.setInnum(0.0);
				}
				if (iosStc.getDirecout() == null) {
					iosStc.setDirecout(0.0);
				}
				if (iosStc.getIndirecout() == null) {
					iosStc.setIndirecout(0.0);
				}
				
				if (iosStc.getSelfsales() == null) {
					iosStc.setSelfsales(0.0);
				}
				if (iosStc.getUnselfsales() == null) {
					iosStc.setUnselfsales(0.0);
				}
				if (iosStc.getOthersales() == null) {
					iosStc.setOthersales(0.0);
				}
				
				String key=visitM.getAgencykey()+"_"+iosStc.getProductkey();
				
				// 处理初期库存,期末库存
				if(!isOk){
				    if(iosStcMap.containsKey(key)){
				        InOutSaveStc inOut=iosStcMap.get(key);
				        iosStc.setStorenum(inOut.getStorenum());
				        iosStc.setPrestorenum(inOut.getPrestorenum());
				    }
				}
				if (iosStc.getStorenum() == null) {
					iosStc.setStorenum(0.0);
				}
				mstInfo.setStorenum(iosStc.getStorenum());
				mstInfo.setPrestorenum(iosStc.getPrestorenum());
				mstInfo.setInnum(iosStc.getInnum());
				mstInfo.setDirecout(iosStc.getDirecout());
				mstInfo.setIndirecout(iosStc.getIndirecout());
				
				// 保存协议店销量-非协议店销量-其他销量
				mstInfo.setSelfsales(iosStc.getSelfsales());
				mstInfo.setUnselfsales(iosStc.getUnselfsales());
				mstInfo.setOthersales(iosStc.getOthersales());
				
				mstInfo.setCredate(new Date());
				//mstInfo.setCreuser(ConstValues.loginSession.getUserCode());
				mstInfo.setCreuser(PrefUtils.getString(context, "userCode", ""));
				mstInfo.setUpdatetime(new Date());
				//mstInfo.setUpdateuser(ConstValues.loginSession.getUserCode());
				mstInfo.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
				dao.createOrUpdate(mstInfo);
			}
			// ViewUtil.sendMsg(context, R.string.agencyvisit_msg_oksave);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询数据库获得的经销商调货记录表
	 * 
	 * @param agevisitkey
	 *            经销商拜访主键
	 * @return
	 */
	public List<TransferStc> getTransfer(MstAgencyvisitM visitM, String agevisitkey) {
		List<TransferStc> transStcLst = new ArrayList<TransferStc>();
		try {
		    DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyvisitMDao dao = helper.getDao(MstAgencyvisitM.class);
            transStcLst = dao.queryTransByVisitId(helper, agevisitkey);
            if (!CheckUtil.IsEmpty(transStcLst)) {
                for (TransferStc stc : transStcLst) {
                    if (!agevisitkey.equals(visitM.getAgevisitkey())) {
                        stc.setTrankey(FunUtil.getUUID());
                        stc.setAgencykey(visitM.getAgevisitkey());
                    }
                }
            }
        } catch (SQLException e1) {
            Log.e(TAG, "查询经销商调货记录失败", e1);
        } 
//		try {
//			Dao<MstAgencytransferInfo, String> mstAgencytransferInfoDao = DatabaseHelper.getHelper(context).getMstAgencytransferInfoDao();
//			QueryBuilder<MstAgencytransferInfo, String> queryBuilder = mstAgencytransferInfoDao.queryBuilder();
//			queryBuilder.where().eq("agevisitkey", agevisitkey);
//			List<MstAgencytransferInfo> mstTransLst = queryBuilder.query();
//			for (MstAgencytransferInfo mstInfo : mstTransLst) {
//				if (!agevisitkey.equals(visitM.getAgevisitkey())) {
//					mstInfo.setTrankey(FunUtil.getUUID());
//					mstInfo.setAgencykey(visitM.getAgevisitkey());
//					// mstAgencytransferInfoDao.createOrUpdate(mstInfo);
//				}
//
//				TransferStc transStc = new TransferStc();
//				transStc.setTrankey(mstInfo.getTrankey());
//				transStc.setAgevisitkey(visitM.getAgevisitkey());
//				transStc.setAgencykey(mstInfo.getAgencykey());
//				transStc.setTagencykey(mstInfo.getTagencykey());
//
//				Dao<MstAgencyinfoM, String> mstAgencyInfoMDao = DatabaseHelper.getHelper(context).getMstAgencyinfoMDao();
//				MstAgencyinfoM mstAgency = mstAgencyInfoMDao.queryForId(mstInfo.getTagencykey());
//				transStc.setTagAgencyName(mstAgency.getAgencyname());
//
//				transStc.setProductkey(mstInfo.getProductkey());
//
//				Dao<MstProductM, String> mstProductMDao = DatabaseHelper.getHelper(context).getMstProductMDao();
//				MstProductM mstProM = mstProductMDao.queryForId(mstInfo.getProductkey());
//				transStc.setProName(mstProM.getProname());
//				transStc.setTrandate(mstInfo.getTrandate());
//				transStc.setTranin(mstInfo.getTranin());
//				transStc.setTranout(mstInfo.getTranout());
//				transStcLst.add(transStc);
//			}
//		} catch (SQLException e) {
//
//			e.printStackTrace();
//		}
		return transStcLst;
	}

	/**
	 * 保存经销商调货记录表
	 * 
	 * @param visitM
	 *            经销商拜访主键
	 * @return
	 */
	public void saveTransferData(MstAgencyvisitM visitM, List<TransferStc> transferStcLst) {

		try {
			Dao<MstAgencytransferInfo, String> mstAgencytransferInfoDao = DatabaseHelper.getHelper(context).getMstAgencytransferInfoDao();
			for (int i = 0; i < transferStcLst.size(); i++) {
				MstAgencytransferInfo info = transferStcLst.get(i);
				if (info.getProductkey() != null && info.getTagencykey() != null && !(info.getTranin().intValue() == 0 && info.getTranout().intValue() == 0)) {
					MstAgencytransferInfo mstTransfer = new MstAgencytransferInfo();
					if (CheckUtil.isBlankOrNull(info.getTrankey())) {
						info.setTrankey(FunUtil.getUUID());
					}

					mstTransfer.setTrankey(info.getTrankey());
					mstTransfer.setAgevisitkey(visitM.getAgevisitkey());
					mstTransfer.setAgencykey(visitM.getAgencykey());
					mstTransfer.setTagencykey(info.getTagencykey());
					mstTransfer.setProductkey(info.getProductkey());
					mstTransfer.setTrandate(visitM.getAgevisitdate().substring(0, 8));
					mstTransfer.setTranin(info.getTranin());
					mstTransfer.setTranout(info.getTranout());
					mstTransfer.setCredate(new Date());
					//mstTransfer.setCreuser(ConstValues.loginSession.getUserCode());
					mstTransfer.setCreuser(PrefUtils.getString(context, "userCode", ""));
					mstTransfer.setUpdatetime(new Date());
					//mstTransfer.setUpdateuser(ConstValues.loginSession.getUserCode());
					mstTransfer.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
					mstAgencytransferInfoDao.createOrUpdate(mstTransfer);
					for (int j = i + 1; j < transferStcLst.size(); j++) {
						MstAgencytransferInfo info1 = transferStcLst.get(j);
						if (info1.getProductkey() != null && info1.getTagencykey() != null && !(info1.getTranin().intValue() == 0 && info1.getTranout().intValue() == 0) && (info.getProductkey().equals(info1.getProductkey()) && info.getTagencykey().equals(info1.getTagencykey()))) {
							mstAgencytransferInfoDao.delete(info);
						}
					}
				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 调货台账数据删除
	 * 
	 * @param transferStc
	 *            要删除某一条数据
	 */
	public void deleteTransfer(TransferStc transferStc) {
		try {
			Dao<MstAgencytransferInfo, String> mstAgencgridInfoDao = DatabaseHelper.getHelper(context).getMstAgencytransferInfoDao();

			mstAgencgridInfoDao.deleteById(transferStc.getTrankey());
			ViewUtil.sendMsg(context, R.string.transfer_msg_delete);
		} catch (SQLException e) {
			ViewUtil.sendMsg(context, R.string.transfer_msg_faildelete);
			e.printStackTrace();
		}
	}

	/**
	 * 调货台账中品种列表的dialog展示
	 * 
	 * @param dataLst
	 *            数据集合
	 * @param text
	 *            所点击的当然文本框
	 * @param asupplykey
	 *            经销商主键
	 */
	public void showProDialog(final List<TransferStc> dataLst, final EditText text, String asupplykey) {
		final List<KvStc> proStcLst = proQuery(asupplykey);

		if (proStcLst.size() == 0) {
			Toast.makeText(context, "没有可选择产品", Toast.LENGTH_SHORT).show();
			return;
		}
		View proView = LayoutInflater.from(context).inflate(R.layout.agencyvisit_transfer_searchpro, null);
		final ListView proLv = (ListView) proView.findViewById(R.id.searchpro_lv_pro);
		SearchProAdapter proAdapter = new SearchProAdapter(context, proStcLst);
		proLv.setAdapter(proAdapter);
		//        List<String> proNameLst = new ArrayList<String>();
		//        List<String> proIdLst = new ArrayList<String>();
		//        for (KvStc proStc : proStcLst) {
		//            String id = proStc.getKey();
		//            String name = proStc.getValue();
		//            proIdLst.add(id);
		//            proNameLst.add(name);
		//        }
		//        int size = proNameLst.size();
		//        final String[] itemsId = (String[]) proIdLst.toArray(new String[size]);
		//        final String[] itemsName = (String[]) proNameLst
		//                .toArray(new String[size]);
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		//        .setItems(
		//                itemsName, new DialogInterface.OnClickListener() {
		//
		//                    @Override
		//                    public void onClick(DialogInterface dialog, int which) {
		//                        text.setText(itemsName[which]);
		//                        dataLst.get((Integer) text.getTag()).setProductkey(
		//                                itemsId[which]);
		//                        dataLst.get((Integer) text.getTag()).setProName(
		//                                itemsName[which]);
		//                    }
		//                })
		alertDialog.setView(proView, 0, 0, 0, 0);
		alertDialog.show();
		proLv.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg1.setBackgroundResource(R.color.bg_content_color_orange);
				text.setText(proStcLst.get(arg2).getValue());
				dataLst.get((Integer) text.getTag()).setProductkey(proStcLst.get(arg2).getKey());
				dataLst.get((Integer) text.getTag()).setProName(proStcLst.get(arg2).getValue());
				alertDialog.dismiss();
			}
		});
	}

	/**
	 * 获取个可调货经销商名称的拼音
	 * 
	 * @param tagAgencyLst
	 *            终端列表
	 */
	public Map<String, String> getTagAgencyPinyin(List<MstAgencyinfoM> tagAgencyLst) {
		Map<String, String> tagAgencyPinyinMap = new HashMap<String, String>();
		for (MstAgencyinfoM item : tagAgencyLst) {
			tagAgencyPinyinMap.put(item.getAgencykey(), "," + PinYin4jUtil.converterToFirstSpell(item.getAgencyname()).toLowerCase());
		}
		return tagAgencyPinyinMap;
	}

	/**
	 * 按条件查询终端列表
	 * 
	 * @param tagAgencyLst
	 *            线路下所有终端
	 * @param searchStr
	 *            查询条件
	 * @param tagAgencyPinyinMap
	 *            各终端名称的拼音
	 */
	public List<MstAgencyinfoM> searchTagAgency(List<MstAgencyinfoM> tagAgencyLst, String searchStr, Map<String, String> tagAgencyPinyinMap) {

		List<MstAgencyinfoM> tagAgencySearchLst = new ArrayList<MstAgencyinfoM>();
		if (!CheckUtil.IsEmpty(tagAgencyLst)) {
			if (CheckUtil.isBlankOrNull(searchStr)) {
				tagAgencySearchLst = tagAgencyLst;
			} else {
				searchStr = searchStr.toLowerCase();
				for (MstAgencyinfoM item : tagAgencyLst) {
					Pattern pattern = Pattern.compile("[a-z]*");
					if (pattern.matcher(searchStr).matches()) {
						String pinyin = tagAgencyPinyinMap.get(item.getAgencykey());
						if (pinyin.indexOf("," + searchStr) > -1 || pinyin.contains(searchStr)) {
							tagAgencySearchLst.add(item);
						}
					} else {
						if (item.getAgencyname().contains(searchStr)) {
							tagAgencySearchLst.add(item);
						}
					}
				}
			}
		}
		return tagAgencySearchLst;
	}
}
