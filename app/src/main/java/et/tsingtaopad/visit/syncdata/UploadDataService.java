package et.tsingtaopad.visit.syncdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.TitleLayout;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencyKFMDao;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.tables.MstAgencyKFM;
import et.tsingtaopad.db.tables.MstAgencysupplyInfo;
import et.tsingtaopad.db.tables.MstAgencytransferInfo;
import et.tsingtaopad.db.tables.MstAgencyvisitM;
import et.tsingtaopad.db.tables.MstCheckexerecordInfo;
import et.tsingtaopad.db.tables.MstCmpsupplyInfo;
import et.tsingtaopad.db.tables.MstCollectionexerecordInfo;
import et.tsingtaopad.db.tables.MstGroupproductM;
import et.tsingtaopad.db.tables.MstInvalidapplayInfo;
import et.tsingtaopad.db.tables.MstInvoicingInfo;
import et.tsingtaopad.db.tables.MstPlanTerminalM;
import et.tsingtaopad.db.tables.MstPlanWeekforuserM;
import et.tsingtaopad.db.tables.MstPlancheckInfo;
import et.tsingtaopad.db.tables.MstPlancollectionInfo;
import et.tsingtaopad.db.tables.MstPlanforuserM;
import et.tsingtaopad.db.tables.MstPlanrouteInfo;
import et.tsingtaopad.db.tables.MstPromotermInfo;
import et.tsingtaopad.db.tables.MstQuestionsanswersInfo;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.MstVisitM;
import et.tsingtaopad.db.tables.MstVisitmemoInfo;
import et.tsingtaopad.db.tables.MstVistproductInfo;
import et.tsingtaopad.db.tables.MstWorksummaryInfo;
import et.tsingtaopad.operation.workplan.WorkPlanFragment1;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FileUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.agencykf.AgencyKFService;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.shopvisit.camera.CameraService;
import et.tsingtaopad.visit.shopvisit.camera.domain.MstCameraListMStc;
import et.tsingtaopad.visit.shopvisit.camera.domain.UpCameraListMStc;
import et.tsingtaopad.visit.shopvisit.sayhi.domain.MstTerminalInfoMStc;
import et.tsingtaopad.visit.termadd.TermAddService;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：UploadDataService.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2013年12月20日</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class UploadDataService
{

    public DatabaseHelper helper;
    private final String TAG = "UploadDataService";
    private Context context;
    private Handler handler;
    private Dao<MstVisitmemoInfo, String> mstVisitmemoInfoDao = null;
    private Dao<MstVisitM, String> mstVisitMDao = null;
    private Dao<MstQuestionsanswersInfo, String> mstQuestionsanswersInfoDao = null;
    private Dao<MstWorksummaryInfo, String> mstWorksummaryInfoDao = null;
    private Dao<MstTerminalinfoM, String> mstTerminalinfoMDao = null;
    private Dao<MstInvoicingInfo, String> mstInvoicingInfoDao = null;
    private Dao<MstAgencytransferInfo, String> mstAgencytransferInfoDao = null;
    private Dao<MstVistproductInfo, String> mstVistproductInfoDao = null;
    private Dao<MstCheckexerecordInfo, String> mstCheckexerecordInfoDao = null;
    private Dao<MstInvalidapplayInfo, String> mstInvalidapplayInfoDao = null;
    private Dao<MstPromotermInfo, String> mstPromotermInfoDao = null;
    private Dao<MstCollectionexerecordInfo, String> mstCollectionexerecordInfoDao = null;
    private Dao<MstPlancheckInfo, String> mstPlancheckInfoDao = null;
    private Dao<MstPlancollectionInfo, String> mstPlancollectionInfoDao = null;
    private Dao<MstPlanforuserM, String> mstPlanforuserMDao = null;
    private Dao<MstPlanWeekforuserM, String> mstPlanWeekforuserMDao = null;
    private Dao<MstAgencysupplyInfo, String> mstAgencysupplyInfoDao = null;
    private Dao<MstAgencyvisitM, String> mstAgencyvisitMDao = null;
    private Dao<MstCmpsupplyInfo, String> mstCmpsupplyInfoDao = null;
    private Dao<MstPlanrouteInfo, String> mstPlanrouteInfodDao = null;
    private MstCameraiInfoMDao mstCameraiInfoMDao = null;
    private MstAgencyKFMDao mstAgencyKFMDao = null;
    private Dao<MstPlanTerminalM, String> mstPlanTerminalMDao = null;
    private Dao<MstGroupproductM, String> mstGroupproductMDao = null;

    

    public UploadDataService(Context context, Handler handler)
    {
        this.handler = handler;
        this.context = context;
        helper = DatabaseHelper.getHelper(context);
        try
        {
            mstVisitmemoInfoDao = helper.getMstVisitmemoInfoDao();
            mstQuestionsanswersInfoDao = helper.getMstQuestionsanswersInfoDao();
            mstVisitMDao = helper.getMstVisitMDao();
            mstWorksummaryInfoDao = helper.getMstWorksummaryInfoDao();
            mstTerminalinfoMDao = helper.getMstTerminalinfoMDao();
            mstInvoicingInfoDao = helper.getMstInvoicingInfoDao();
            mstAgencytransferInfoDao = helper.getMstAgencytransferInfoDao();
            mstVistproductInfoDao = helper.getMstVistproductInfoDao();
            mstCheckexerecordInfoDao = helper.getMstCheckexerecordInfoDao();
            mstInvalidapplayInfoDao = helper.getMstInvalidapplayInfoDao();
            mstCollectionexerecordInfoDao = helper.getMstCollectionexerecordInfoDao();
            mstPromotermInfoDao = helper.getMstPromotermInfoDao();
            mstPlancheckInfoDao = helper.getMstPlancheckInfoDao();
            mstPlanTerminalMDao = helper.getMstPlanTerminalM();
            
            mstPlancollectionInfoDao = helper.getMstPlancollectionInfoDao();
            mstPlanforuserMDao = helper.getMstPlanforuserMDao();
            mstPlanWeekforuserMDao = helper.getMstPlanWeekforuserMDao();
            mstAgencysupplyInfoDao = helper.getMstAgencysupplyInfoDao();
            mstAgencyvisitMDao = helper.getMstAgencyvisitMDao();
            mstCmpsupplyInfoDao = helper.getMstCmpsupplyInfoDao();
            mstPlanrouteInfodDao = helper.getMstPlanrouteInfoDao();
            mstCameraiInfoMDao = (MstCameraiInfoMDao)helper.getMstCameraiInfoMDao();
            mstAgencyKFMDao = (MstAgencyKFMDao)helper.getMstAgencyKFMDao();
            mstGroupproductMDao = helper.getMstGroupproductMDao();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 上传数据
     * @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
     */
    public void uploadTables(boolean isNeedExit)
    {

        upload_memos(isNeedExit, null);//客情备忘录
        upload_question_feedback_infos(isNeedExit, null);//问题反馈f
        upload_worksummary_infos(isNeedExit, null);//日工作总结
        upload_terminal(isNeedExit, null, -1);//终端
        upload_terminalAdd(isNeedExit, null, -1);
        upload_work_plans(isNeedExit, null);//工作计划
        upload_agency_visit(isNeedExit, null);//经销商拜访
        upload_visit(isNeedExit, null, -1);//巡店拜访
        
        // 巡店拜访拍照上传,上传所有标识为"0"的记录  已经添加到巡店拜访数据上传
        upload_visit_camera(isNeedExit,"0",null);
        // 上传经销商记录
        upload_agencykf(isNeedExit,null); 
        // 上传周计划
        upload_weekwork_plans(isNeedExit, null);
        
    }

    /**
     * 所有经销商开发记录上传
     * 
	 * @param isNeedExit
	 * @param object
	 */
	private void upload_agencykf(final boolean isNeedExit, Object object) {
		List<MstAgencyKFM> mstAgencyKFMs = new ArrayList<MstAgencyKFM>();
		final AgencyKFService service = new AgencyKFService(context, null);
		mstAgencyKFMs = mstAgencyKFMDao.queryMstAgencyKFMLstbynotupload(helper, "0");
		
		MstAgencyKFM agencyKFInfo = null;
		if(!mstAgencyKFMs.isEmpty()){
			for (MstAgencyKFM mstAgencyKFM : mstAgencyKFMs) {
				agencyKFInfo = new MstAgencyKFM();
				agencyKFInfo.setAgencykfkey(mstAgencyKFM.getAgencykfkey());
				agencyKFInfo.setAgencyname(mstAgencyKFM.getAgencyname());
				agencyKFInfo.setContact(mstAgencyKFM.getContact());
				agencyKFInfo.setMobile(mstAgencyKFM.getMobile());
				agencyKFInfo.setAddress(mstAgencyKFM.getAddress());
				agencyKFInfo.setArea(mstAgencyKFM.getArea());
				agencyKFInfo.setMoney(mstAgencyKFM.getMoney());
				agencyKFInfo.setCarnum(mstAgencyKFM.getCarnum());
				agencyKFInfo.setProductname(mstAgencyKFM.getProductname());
				agencyKFInfo.setKfdate(mstAgencyKFM.getKfdate());
				//agencyKFInfo.setGridkey(ConstValues.loginSession.getGridId());
				agencyKFInfo.setGridkey(PrefUtils.getString(context, "gridId", ""));
				agencyKFInfo.setStatus("0");
				agencyKFInfo.setCreateuser(mstAgencyKFM.getCreateuser());
				agencyKFInfo.setCreatedate(mstAgencyKFM.getCreatedate());
				agencyKFInfo.setUpdateuser(mstAgencyKFM.getUpdateuser());
				agencyKFInfo.setUpdatedate(mstAgencyKFM.getUpdatedate());
				agencyKFInfo.setUpload("0");
				
				// ---经销商开发 新加字段 离线上传---------------------------------------------
				agencyKFInfo.setPersion(mstAgencyKFM.getPersion());
				agencyKFInfo.setBusiness(mstAgencyKFM.getBusiness());
				agencyKFInfo.setIsone(mstAgencyKFM.getIsone());
				agencyKFInfo.setCoverterms(mstAgencyKFM.getCoverterms());
				agencyKFInfo.setSupplyterms(mstAgencyKFM.getSupplyterms());
				agencyKFInfo.setPassdate(mstAgencyKFM.getPassdate());
				// ---经销商开发 新加字段 离线上传---------------------------------------------
				
				// 将对象转成json
				String json = JSON.toJSONString(agencyKFInfo);

				// 请求网络 上传开发商信息 并保存到本地
				HttpUtil httpUtil = new HttpUtil(60 * 1000);
				httpUtil.configResponseTextCharset("ISO-8859-1");
				httpUtil.send("opt_save_agencykf", json, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						ResponseStructBean resObj = HttpUtil
								.parseRes(responseInfo.result);
						if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
							String agencykfkey = resObj
									.getResBody()
									.getContent()
									.substring(
											2,
											resObj.getResBody().getContent().length() - 2);
							// 更改经销商上传标记为1(已上传)
							service.updataagencyKfupstatusbyAgencykfkey(agencykfkey);
							// Toast.makeText(context, "上传成功", 0).show();
							
							DbtLog.logUtils("退出上传", "经销商开发上传成功Success");
							onSuccessSendMessage(isNeedExit, null);

						} else {

						}
					}

					@Override
					public void onFailure(HttpException error, String errMsg) {
						Log.e(TAG, errMsg, error);
						DbtLog.logUtils("退出上传", "经销商开发失败Fail");
						onFailureSendMessage(isNeedExit, null);
					}
				});
			}
		}else{
			if (isNeedExit)
            {
				// 所有经销商开发记录上传
                handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
            }
		}
		
	}

	/**
     * 拍照上传
     * 
	 * @param isNeedExit  true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
	 * @param isupload "0"未上传  "1" 已上传
	 */
	public void upload_visit_camera(final boolean isNeedExit, final String isupload, final String visitKey) {
		
		// 有拜访主键时()
		if (visitKey != null && !visitKey.trim().equals("")) {
			
			new Thread(){
				@Override
				public void run() {
					super.run();
					List<MstCameraListMStc> cameraListMStcs = new ArrayList<MstCameraListMStc>();
					cameraListMStcs = mstCameraiInfoMDao.getCameraListNoIsuploadByVisitkey(helper,isupload,visitKey);
					//List<UpCameraListMStc> upCameraListMStcs = new ArrayList<UpCameraListMStc>();

					if(!cameraListMStcs.isEmpty()){
						for (MstCameraListMStc mstCameraListMStc : cameraListMStcs) {

							//UpCameraListMStc upCameraListMStc = new UpCameraListMStc();
							UpCameraListMStc upCameraListMStc = new UpCameraListMStc();
							
							upCameraListMStc.setImagefile(mstCameraListMStc.getImagefileString());
							upCameraListMStc.setRecordkey(mstCameraListMStc.getCamerakey());
							upCameraListMStc.setVisitkey(mstCameraListMStc.getVisitkey());
							upCameraListMStc.setCameradata(mstCameraListMStc.getCameradata());
							upCameraListMStc.setPicname(mstCameraListMStc.getPicname());
							upCameraListMStc.setLocalpath(mstCameraListMStc.getLocalpath());
							upCameraListMStc.setPictypekey(mstCameraListMStc.getPictypekey());
							String usercode = PrefUtils.getString(context, "userCode", "");
							String gridId = PrefUtils.getString(context, "gridId", "");
							String disId = PrefUtils.getString(context, "disId", "");
							upCameraListMStc.setCreateuser(usercode);
							upCameraListMStc.setUpdateuser(usercode);
							upCameraListMStc.setGridid(gridId);
							upCameraListMStc.setDisid(disId);
							upCameraListMStc.setRouteid(findTermByTermkey(mstCameraListMStc.getTerminalkey()).getRoutekey());
							upCameraListMStc.setTerminalkey(mstCameraListMStc.getTerminalkey());
							
							// ↓---添加visidate enddate--// ywm 20160412-----------------------------------
							try {
								MstVisitM mstvisitm = mstVisitMDao.queryForId(mstCameraListMStc.getVisitkey());
								upCameraListMStc.setEnddate(mstvisitm.getEnddate());
								upCameraListMStc.setVisitdate(mstvisitm.getVisitdate());
								upCameraListMStc.setVisituser(mstvisitm.getVisituser());
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// ↑---添加visidate enddate--// ywm 20160412-----------------------------------
							
							upCameraListMStcs.add(upCameraListMStc);
						}
							String json = JSON.toJSONString(upCameraListMStcs);
							//FileUtil.writeTxt(json, FileUtil.getSDPath()+"/upCameraListMStcs12.txt");
							//savFile(json, "picjson");// 上传图片json
							// 发送请求,上传图片字符串
							HttpUtil httpUtil = new HttpUtil(60 * 1000);
							httpUtil.configResponseTextCharset("ISO-8859-1");
							httpUtil.send("opt_save_visitpic", json, new RequestCallBack<String>() {

								@Override
								public void onSuccess(ResponseInfo<String> responseInfo) {

									ResponseStructBean resObj = HttpUtil
											.parseRes(responseInfo.result);
									if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
										
										//Toast.makeText(context, "拜访拍照上传成功", 0).show();
										
										// 获取上传成功的图片主键数组
										String json = resObj.getResBody().getContent();
										if(json !=null && json.length()>0){
											JSONArray numberList = JSON.parseArray(json);
											for(int i=0; i< numberList.size(); i++){
												String camerakey = numberList.getString(i);
												// 根据图片主键获取本地路径
												String path = new CameraService(context, handler).getPathbyCameraKey(camerakey);
												// 删除图片文件
												FileUtil.deleteFile(new File(path));
												// 图片表删除此记录
												new CameraService(context, handler).deletePicByCamerakey(camerakey);
											}
									        
										}
										
										// 更新所有上传图片状态  
										//new CameraService(context, handler).updataUpstatus();
										// 删除本地文件
										//String path = context.getFilesDir().getAbsolutePath() + File.separator + "photo";
										//String path = Environment.getExternalStorageDirectory()+ ""+ "/dbt/et.tsingtaopad"+ "/photo";
										//FileUtil.deleteFile(new File(path));
									
										// 上传成功发送信息
										DbtLog.logUtils("退出上传", "拍照上传成功Success1");
										onSuccessSendMessage(isNeedExit, null);
										
									} else {
										//Toast.makeText(context, "后台返回E 拜访拍照上传失败", 0).show();
									}
								}

								@Override
								public void onFailure(HttpException error, String errMsg) {
									Log.e(TAG, errMsg, error);
									//Toast.makeText(context, "网络连接失败 图片上传失败", 0).show();
									DbtLog.logUtils("退出上传", "拍照上传失败Fail1");
				                    onFailureSendMessage(isNeedExit, null);
								}
							});
						
						
					}else{
						if (isNeedExit)
			            {
							// 拍照上传
			                handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
			            }
					}
				}
			}.start();
			
			
		}else{// 没有拜访主键时
			
			// 开启子线程
			new Thread(){
				@Override
				public void run() {
					super.run();
					
					// 查询 未上传图片的 visitkey 
					List<String> visitKeyLst = new ArrayList<String>();
					visitKeyLst = mstCameraiInfoMDao.getVisitkeyByisupload(helper,"0");
					
					if(!visitKeyLst.isEmpty()){
						for (String visitKey : visitKeyLst) {
							
							// 根据visitkey 查询所有未上传的图片
							List<MstCameraListMStc> cameraListMStcs = new ArrayList<MstCameraListMStc>();
							cameraListMStcs = mstCameraiInfoMDao.getCameraListNoIsuploadByVisitkey(helper,"0",visitKey);
							
							// 上传图片列表
							List<UpCameraListMStc> picListMStcs = new ArrayList<UpCameraListMStc>();
							
							for (MstCameraListMStc mstCameraListMStc : cameraListMStcs) {

								//UpCameraListMStc upCameraListMStc = new UpCameraListMStc();
								UpCameraListMStc upCameraListMStc = new UpCameraListMStc();
								
								upCameraListMStc.setImagefile(mstCameraListMStc.getImagefileString());
								upCameraListMStc.setRecordkey(mstCameraListMStc.getCamerakey());
								upCameraListMStc.setVisitkey(mstCameraListMStc.getVisitkey());
								upCameraListMStc.setCameradata(mstCameraListMStc.getCameradata());
								upCameraListMStc.setPicname(mstCameraListMStc.getPicname());
								upCameraListMStc.setLocalpath(mstCameraListMStc.getLocalpath());
								upCameraListMStc.setPictypekey(mstCameraListMStc.getPictypekey());
								String usercode = PrefUtils.getString(context, "userCode", "");
								String gridId = PrefUtils.getString(context, "gridId", "");
								String disId = PrefUtils.getString(context, "disId", "");
								upCameraListMStc.setCreateuser(usercode);
								upCameraListMStc.setUpdateuser(usercode);
								upCameraListMStc.setGridid(gridId);
								upCameraListMStc.setDisid(disId);
								upCameraListMStc.setRouteid(findTermByTermkey(mstCameraListMStc.getTerminalkey()).getRoutekey());
								upCameraListMStc.setTerminalkey(mstCameraListMStc.getTerminalkey());
								
								// ↓---添加visidate enddate--// ywm 20160412-----------------------------------
								try {
									MstVisitM mstvisitm = mstVisitMDao.queryForId(mstCameraListMStc.getVisitkey());
									upCameraListMStc.setEnddate(mstvisitm.getEnddate());
									upCameraListMStc.setVisitdate(mstvisitm.getVisitdate());
									upCameraListMStc.setVisituser(mstvisitm.getVisituser());
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// ↑---添加visidate enddate--// ywm 20160412-----------------------------------
								
								picListMStcs.add(upCameraListMStc);
							}
								String json = JSON.toJSONString(picListMStcs);
								//FileUtil.writeTxt(json, FileUtil.getSDPath()+"/upCameraListMStcs12.txt");
								//savFile(json, "picjson");// 上传图片json
								// 发送请求,上传图片字符串
								HttpUtil httpUtil = new HttpUtil(60 * 1000);
								httpUtil.configResponseTextCharset("ISO-8859-1");
								httpUtil.send("opt_save_visitpic", json, new RequestCallBack<String>() {

									@Override
									public void onSuccess(ResponseInfo<String> responseInfo) {

										ResponseStructBean resObj = HttpUtil
												.parseRes(responseInfo.result);
										if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
											
											//Toast.makeText(context, "拜访拍照上传成功", 0).show();
											
											// 获取上传成功的图片主键数组
											String json = resObj.getResBody().getContent();
											if(json !=null && json.length()>0){
												JSONArray numberList = JSON.parseArray(json);
												for(int i=0; i< numberList.size(); i++){
													String camerakey = numberList.getString(i);
													// 根据图片主键获取本地路径
													String path = new CameraService(context, handler).getPathbyCameraKey(camerakey);
													// 删除图片文件
													FileUtil.deleteFile(new File(path));
													// 图片表删除此记录
													new CameraService(context, handler).deletePicByCamerakey(camerakey);
												}
										        
											}
											
											// 更新所有上传图片状态  
											//new CameraService(context, handler).updataUpstatus();
											// 删除本地文件
											//String path = context.getFilesDir().getAbsolutePath() + File.separator + "photo";
											//String path = Environment.getExternalStorageDirectory()+ ""+ "/dbt/et.tsingtaopad"+ "/photo";
											//FileUtil.deleteFile(new File(path));
										
											// 上传成功发送信息
											/*DbtLog.logUtils("退出上传", "拍照上传成功Success1");
											onSuccessSendMessage(isNeedExit, null);*/
											
										} else {
											//Toast.makeText(context, "后台返回E 拜访拍照上传失败", 0).show();
										}
									}

									@Override
									public void onFailure(HttpException error, String errMsg) {
										Log.e(TAG, errMsg, error);
										//Toast.makeText(context, "网络连接失败 图片上传失败", 0).show();
										DbtLog.logUtils("退出上传", "拍照上传失败Fail1");
					                    onFailureSendMessage(isNeedExit, null);
									}
								});
							
						}
						
						
					}else{// 图片全部上传成功,发送成功biaoji
						if (isNeedExit)
			            {
							// 拍照上传
			                handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
			            }
					}
					
					/**
					
					// 查询所有未上传的图片
					List<MstCameraListMStc> cameraListMStcs = new ArrayList<MstCameraListMStc>();
					cameraListMStcs = mstCameraiInfoMDao.getCameraListNoIsuploadNoByVisitkey(helper,"0");
					
					if(!cameraListMStcs.isEmpty()){
						for (MstCameraListMStc mstCameraListMStc : cameraListMStcs) {

							UpCameraListMStc upCameraListMStc = new UpCameraListMStc();
							upCameraListMStc.setImagefile(mstCameraListMStc.getImagefileString());
							upCameraListMStc.setRecordkey(mstCameraListMStc.getCamerakey());
							upCameraListMStc.setVisitkey(mstCameraListMStc.getVisitkey());
							upCameraListMStc.setCameradata(mstCameraListMStc.getCameradata());
							upCameraListMStc.setPicname(mstCameraListMStc.getPicname());
							upCameraListMStc.setLocalpath(mstCameraListMStc.getLocalpath());
							upCameraListMStc.setPictypekey(mstCameraListMStc.getPictypekey());
							String usercode = PrefUtils.getString(context, "userCode", "");
							String gridId = PrefUtils.getString(context, "gridId", "");
							String disId = PrefUtils.getString(context, "disId", "");
							upCameraListMStc.setCreateuser(usercode);
							upCameraListMStc.setUpdateuser(usercode);
							upCameraListMStc.setGridid(gridId);
							upCameraListMStc.setDisid(disId);
							upCameraListMStc.setRouteid(findTermByTermkey(mstCameraListMStc.getTerminalkey()).getRoutekey());
							upCameraListMStc.setTerminalkey(mstCameraListMStc.getTerminalkey());
							// ↓---添加visidate enddate--// ywm 20160412-----------------------------------
							try {
								MstVisitM mstvisitm = mstVisitMDao.queryForId(mstCameraListMStc.getVisitkey());
								upCameraListMStc.setEnddate(mstvisitm.getEnddate());
								upCameraListMStc.setVisitdate(mstvisitm.getVisitdate());
								upCameraListMStc.setVisituser(mstvisitm.getVisituser());
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// ↑---添加visidate enddate--// ywm 20160412-----------------------------------
							
							upCameraListMStcs = new ArrayList<UpCameraListMStc>();
							upCameraListMStcs.add(upCameraListMStc);
							
							String json = JSON.toJSONString(upCameraListMStcs);
							// 发送请求,上传图片字符串
							HttpUtil httpUtil = new HttpUtil(60 * 1000);
							httpUtil.configResponseTextCharset("ISO-8859-1");
							httpUtil.send("opt_save_visitpic", json, new RequestCallBack<String>() {

								@Override
								public void onSuccess(ResponseInfo<String> responseInfo) {

									ResponseStructBean resObj = HttpUtil
											.parseRes(responseInfo.result);
									if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
										
										//Toast.makeText(context, "拜访拍照上传成功", 0).show();
										
										// 获取上传成功的图片主键数组
										String json = resObj.getResBody().getContent();
										if(json !=null && json.length()>0){
											JSONArray numberList = JSON.parseArray(json);
											for(int i=0; i< numberList.size(); i++){
												String camerakey = numberList.getString(i);
												// 根据图片主键获取本地路径
												String path = new CameraService(context, handler).getPathbyCameraKey(camerakey);
												// 删除图片文件
												FileUtil.deleteFile(new File(path));
												// 图片表删除此记录
												new CameraService(context, handler).deletePicByCamerakey(camerakey);
											}
										}
										
										// 更新所有上传图片状态  
										//new CameraService(context, handler).updataUpstatus();
										// 删除本地文件
										//String path = context.getFilesDir().getAbsolutePath() + File.separator + "photo";
										//String path = Environment.getExternalStorageDirectory()+ ""+ "/dbt/et.tsingtaopad"+ "/photo";
										//FileUtil.deleteFile(new File(path));
										
										// 上传完成后,查询数据库,是否还有未上传的记录
										// 每次图片上传成功 检测一下本地数据库 查询所有未上传的图片 若没有要上传的图片则发送消息
										List<MstCameraListMStc> cameraListMStcs = new ArrayList<MstCameraListMStc>();
										cameraListMStcs = mstCameraiInfoMDao.getCameraListNoIsuploadNoByVisitkey(helper,"0");
										if(cameraListMStcs.size()==0){
											DbtLog.logUtils("退出上传", "拍照上传成功Success2");
											onSuccessSendMessage(isNeedExit, null);
										}
									
									} else {
										//Toast.makeText(context, "后台返回E 拜访拍照上传失败", 0).show();
									}
								}

								@Override
								public void onFailure(HttpException error, String errMsg) {
									Log.e(TAG, errMsg, error);
									//Toast.makeText(context, "网络连接失败 图片上传失败", 0).show();
									DbtLog.logUtils("退出上传", "拍照上传Fail2");
				                    onFailureSendMessage(isNeedExit, null);
								}
							});
						}
						
						
					} else{// 图片全部上传成功,发送成功biaoji
						if (isNeedExit)
			            {
							// 拍照上传
			                handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
			            }
					}
					*/
					
					/*for (String vistkey : vistkeys) {
						
						List<MstCameraListMStc> cameraListMStcs = new ArrayList<MstCameraListMStc>();
						cameraListMStcs = mstCameraiInfoMDao.getCameraListNoIsuploadByVisitkey(helper,"0",vistkey);
						//List<UpCameraListMStc> upCameraListMStcs = new ArrayList<UpCameraListMStc>();
						List<UpCameraListMStc> upCameraListMStcs;

						
					}*/
					
					/*List<String> vistkeys = new ArrayList<String>();
					vistkeys = mstCameraiInfoMDao.queryVisitKey(helper, "0");*/
					
					// 根据拜访主键 将图片分多次上传
				}
			}.start();
			
		}
		
	}

	/**
     * 
     * @return true 表示都上全完了，false 表示还有未上传数据
     * @throws SQLException
     */
    public boolean isAllEmpty() throws SQLException
    {
        //备忘录检查
        List<MstVisitmemoInfo> visitmemoInfos = mstVisitmemoInfoDao.queryForEq("padisconsistent", "0");
        if (!visitmemoInfos.isEmpty())
        {
            return false;
        }
        //问题反馈检查
        List<MstQuestionsanswersInfo> questionsanswersInfos = mstQuestionsanswersInfoDao.queryForEq("padisconsistent", "0");
        if (!questionsanswersInfos.isEmpty())
        {
            return false;
        }
        //工作总结检查
        List<MstWorksummaryInfo> worksummaryinfos = mstWorksummaryInfoDao.queryForEq("padisconsistent", "0");
        if (!worksummaryinfos.isEmpty())
        {
            return false;
        }

        //终端失效检查
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("padisconsistent", "0");
        map.put("status", ConstValues.FLAG_3);
        //		map.put("terminalcode", "temp");
        List<MstTerminalinfoM> terminalinfos = mstTerminalinfoMDao.queryForFieldValues(map);
        if (!terminalinfos.isEmpty())
        {
            return false;
        }

        //新增终端检查
        List<MstTerminalinfoM> termAddList = TermAddService.getTermAddListFromShared(context);
        if (!termAddList.isEmpty())
        {
            return false;
        }

        // MST_PLANFORUSER_M(人员计划主表) 工作计划检查
        List<MstPlanforuserM> mPlanforusers = mstPlanforuserMDao.queryForEq("padisconsistent", "2");// 2 确定上传
        if (!mPlanforusers.isEmpty())
        {
            return false;
        }
        
        // 周计划检查
        List<MstPlanWeekforuserM> mPlanforuserms4 = mstPlanWeekforuserMDao.queryForEq("padisconsistent", "0");
        if (!mPlanforuserms4.isEmpty())
        {
            return false;
        }

        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("uploadFlag", "1");
        fieldValues.put("padisconsistent", "0");
        //经销商拜访检查
        List<MstAgencyvisitM> agencyvisitMs = mstAgencyvisitMDao.queryForFieldValues(fieldValues);
        if (!agencyvisitMs.isEmpty())
        {
            return false;
        }
        //巡店拜访检查
        List<MstVisitM> visits = mstVisitMDao.queryForFieldValuesArgs(fieldValues);
        if (!visits.isEmpty())
        {
            return false;
        }
        
        //经销商开发
		List<MstAgencyKFM> mstAgencyKFMs = new ArrayList<MstAgencyKFM>();
		mstAgencyKFMs = mstAgencyKFMDao.queryMstAgencyKFMLstbynotupload(helper, "0");
		
        if (!mstAgencyKFMs.isEmpty())
        {
        	return false;
        }
        
        //拍照上传
        List<MstCameraListMStc> cameraList = new ArrayList<MstCameraListMStc>();
		cameraList = mstCameraiInfoMDao.getCameraListNoIsupload(helper,"0");
        if (!cameraList.isEmpty())
        {
        	return false;
        }

        return true;
    }

    List<MstVisitmemoInfo> memoInfos = new ArrayList<MstVisitmemoInfo>();

    /**
     * 上传客情备忘录
     * @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
     * @param memo
     */
    public void upload_memos(final boolean isNeedExit, final MstVisitmemoInfo memo)
    {
        try
        {
            if (memo != null)
            {
                memoInfos.add(memo);
            }
            else
            {
                memoInfos = mstVisitmemoInfoDao.queryForEq("padisconsistent", "0");
            }

            if (memoInfos.size() != 0)
            {
                for (MstVisitmemoInfo memoinfo : memoInfos)
                {
                    memoinfo.setPadisconsistent("1");
                }
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.configResponseTextCharset("ISO-8859-1");
                String json = JsonUtil.toJson(memoInfos);

                Log.e(TAG, " 客情备忘录 send:" + json);
                httpUtil.send("opt_save_visitmemo", json, new RequestCallBack<String>()
                {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {
                        ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                        Log.i(TAG, "客情备忘录" + resObj.getResBody().getContent());
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus()))
                        {
                            for (MstVisitmemoInfo memoInfo : memoInfos)
                            {
                                memoInfo.setPadisconsistent("1");
                                try
                                {
                                    mstVisitmemoInfoDao.update(memoInfo);
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                            DbtLog.logUtils("退出上传", "上传客情备忘录成功Success");
                            onSuccessSendMessage(isNeedExit, memo);
                        }
                        else
                        {
                            String content = resObj.getResBody().getContent();
                            List<String> ids = JsonUtil.parseList(content, String.class);
                            updateInids("MST_VISITMEMO_INFO", "memokey", ids);
                            DbtLog.logUtils("退出上传", "上传客情备忘录E");
                            onFailureSendMessage(isNeedExit, memo);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg)
                    {
                        Log.e(TAG, msg, error);
                        DbtLog.logUtils("退出上传", "上传客情备忘录Fail");
                        onFailureSendMessage(isNeedExit, memo);
                    }
                });
            }
            else
            {
                if (isNeedExit)
                {
                	// 上传客情备忘录
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    List<MstQuestionsanswersInfo> questionInfos = new ArrayList<MstQuestionsanswersInfo>();

    /**
     * 上传所有问题反馈
     * @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
     * @param questionsAnswer
     */
    public void upload_question_feedback_infos(final boolean isNeedExit, final MstQuestionsanswersInfo questionsAnswer)
    {

        try
        {
            if (questionsAnswer != null)
            {
                questionInfos.add(questionsAnswer);

            }
            else
            {
                questionInfos = mstQuestionsanswersInfoDao.queryForEq("padisconsistent", "0");

            }
            if (questionInfos.size() != 0)
            {
                for (MstQuestionsanswersInfo questionInfo : questionInfos)
                {
                    questionInfo.setPadisconsistent("1");
                }
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.configResponseTextCharset("ISO-8859-1");
                String json = JsonUtil.toJson(questionInfos);
                Log.e(TAG, "问题反馈 send:" + json);

                httpUtil.send("opt_save_questionsanswers", json, new RequestCallBack<String>()
                {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {
                        ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                        Log.i(TAG, "问题反馈" + resObj.getResBody().getContent());
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus()))
                        {
                            for (MstQuestionsanswersInfo questionInfo : questionInfos)
                            {
                                questionInfo.setPadisconsistent("1");
                                try
                                {
                                    mstQuestionsanswersInfoDao.update(questionInfo);
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            DbtLog.logUtils("退出上传", "上传所有问题反馈成功Success");
                            onSuccessSendMessage(isNeedExit, questionsAnswer);
                        }
                        else
                        {
                            String content = resObj.getResBody().getContent();
                            List<String> ids = JsonUtil.parseList(content, String.class);
                            updateInids("MST_QUESTIONSANSWERS_INFO", "qakey", ids);
                            DbtLog.logUtils("退出上传", "上传所有问题反馈E");
                            onFailureSendMessage(isNeedExit, questionsAnswer);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg)
                    {
                        Log.e(TAG, msg, error);
                        DbtLog.logUtils("退出上传", "上传所有问题反馈Fail");
                        onFailureSendMessage(isNeedExit, questionsAnswer);
                    }
                });
            }
            else
            {
                if (isNeedExit)
                {
                	// 上传所有问题反馈
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    List<MstWorksummaryInfo> worksummaryinfos = new ArrayList<MstWorksummaryInfo>();

    /**
     * 上传所有日工作总结
     * @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
     * @param worksummaryinfo
     */
    public void upload_worksummary_infos(final boolean isNeedExit, final MstWorksummaryInfo worksummaryinfo)
    {
        try
        {
            if (worksummaryinfo != null)
            {
                worksummaryinfos.add(worksummaryinfo);
            }
            else
            {
                worksummaryinfos = mstWorksummaryInfoDao.queryForEq("padisconsistent", "0");
            }
            if (!worksummaryinfos.isEmpty())
            {
                for (MstWorksummaryInfo worksummary : worksummaryinfos)
                {
                    worksummary.setPadisconsistent("1");//上传标示设置为1  上传到服务器的状态也是1 当下次同步下来的数据状态还是1  在次上传时此次同步的数据就不会再上传因为服务器有这份数据
                }
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.configResponseTextCharset("ISO-8859-1");
                String json = JsonUtil.toJson(worksummaryinfos);
                Log.e(TAG, "日工作总结 send:" + json);
                httpUtil.send("opt_save_worksummary", json, new RequestCallBack<String>()
                {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {
                        ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                        Log.i(TAG, "日工作总结" + resObj.getResBody().getContent());
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus()))
                        {
                            for (MstWorksummaryInfo worksummaryinfo : worksummaryinfos)
                            {
                                worksummaryinfo.setPadisconsistent("1");
                                try
                                {
                                    mstWorksummaryInfoDao.update(worksummaryinfo);
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            DbtLog.logUtils("退出上传", "上传所有日工作总结Success");
                            onSuccessSendMessage(isNeedExit, worksummaryinfo);
                        }
                        else
                        {

                        	DbtLog.logUtils("退出上传", "上传所有日工作总结E");
                            onFailureSendMessage(isNeedExit, worksummaryinfo);
                            String content = resObj.getResBody().getContent();
                            List<String> ids = JsonUtil.parseList(content, String.class);
                            updateInids("MST_WORKSUMMARY_INFO", "wskey", ids);//部分上传成功时   padisconsistent设置为0 下次上传时继续上传
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg)
                    {
                        Log.e(TAG, msg, error);
                        DbtLog.logUtils("退出上传", "上传所有日工作总结Fail");
                        onFailureSendMessage(isNeedExit, worksummaryinfo);
                    }
                });
            }
            else
            {
                if (isNeedExit)
                {
                	// 上传所有日工作总结
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    List<MstTerminalinfoM> terminalinfoms = new ArrayList<MstTerminalinfoM>();
    List<MstInvalidapplayInfo> mInvalidapplayInfos_terminal = new ArrayList<MstInvalidapplayInfo>();

    /**
     * 上传所有temp终端 {@link MstTerminalinfoM}
     * @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
     * @param terminalkey2 单个数据上传的id
     */
    public void upload_terminal(final boolean isNeedExit, final String terminalkey2, final int whatId)
    {
        try
        {

            if (terminalkey2 != null)
            {
                MstTerminalinfoM terminalinfoM = mstTerminalinfoMDao.queryForId(terminalkey2);
                if (terminalinfoM != null)
                {
                    terminalinfoms.add(terminalinfoM);
                    // 	Map<String, Object> fieldValues = new HashMap<String, Object>();
                    // 	fieldValues.put("terminalkey", terminalkey2);
                    // 	fieldValues.put("padisconsistent", "0");
                    // 	mInvalidapplayInfos_terminal = mstInvalidapplayInfoDao.queryForFieldValues(fieldValues);
                }
            }
            else
            {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("padisconsistent", "0");
                map.put("status", ConstValues.FLAG_3);
                //				map.put("terminalcode", "temp");
                terminalinfoms = mstTerminalinfoMDao.queryForFieldValues(map);
            }

            mInvalidapplayInfos_terminal = mstInvalidapplayInfoDao.queryForEq("padisconsistent", "0");
            List<Map<String, String>> mainDatas = new ArrayList<Map<String, String>>();
            if (!terminalinfoms.isEmpty())
            {
                for (MstTerminalinfoM terminalinfom : terminalinfoms)
                {
                    Map<String, String> childDatas = new HashMap<String, String>();
                    String terminalkey = terminalinfom.getTerminalkey();
                    terminalinfom.setPadisconsistent("1");
                    childDatas.put("MST_TERMINALINFO_M", JsonUtil.toJson(terminalinfom));
                    // 终端档案申请表
                    List<MstInvalidapplayInfo> childInvalidapplayInfos = new ArrayList<MstInvalidapplayInfo>();
                    for (MstInvalidapplayInfo mInvalidapplayInfo : mInvalidapplayInfos_terminal)
                    {
                        if (terminalkey.equals(mInvalidapplayInfo.getTerminalkey()) && "0".equals(mInvalidapplayInfo.getApplaytype()))
                        {
                            mInvalidapplayInfo.setPadisconsistent("1");
                            childInvalidapplayInfos.add(mInvalidapplayInfo);
                        }
                    }
                    childDatas.put("MST_INVALIDAPPLAY_INFO", JsonUtil.toJson(childInvalidapplayInfos));
                    mainDatas.add(childDatas);
                }
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.configTimeout(5 * 60000);
                httpUtil.configResponseTextCharset("ISO-8859-1");
                String json = JsonUtil.toJson(mainDatas);
                Log.e(TAG, "终端上传send:" + mainDatas.size() + json);
                httpUtil.send("opt_save_terminal", json, new RequestCallBack<String>()
                {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {

                        ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                        Log.i(TAG, "终端上传" + resObj.getResBody().getContent());
                        Map<String, Object> termIdMap = JsonUtil.parseMap(resObj.getResBody().getContent());
                        String termSql = "update mst_terminalinfo_m set terminalkey=?, terminalcode=?, Padisconsistent=? where terminalkey=? ";
                        String[] val;
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus()))
                        {
                            for (MstTerminalinfoM terminalinfo : terminalinfoms)
                            {
                                //termIdMap.put(termId, "1," + terminal.getTerminalkey() + "," + terminal.getTerminalcode());接口返回字段
                                val = String.valueOf(termIdMap.get(terminalinfo.getTerminalkey())).split(",");
                                try
                                {
                                    if (val.length == 3)
                                    {
                                        mstTerminalinfoMDao.executeRaw(termSql, new String[] { val[1], val[2], "1", terminalinfo.getTerminalkey() });
                                    }
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                            for (MstInvalidapplayInfo mInvalidapplayInfo : mInvalidapplayInfos_terminal)
                            {
                                mInvalidapplayInfo.setPadisconsistent("1");
                                try
                                {
                                    mstInvalidapplayInfoDao.update(mInvalidapplayInfo);
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            DbtLog.logUtils("退出上传", "上传所有temp终端成功Success");
                            onSuccessSendMessage(whatId, isNeedExit);
                        }
                        else
                        {

                        	DbtLog.logUtils("退出上传", "上传所有temp终端E");
                            onFailureSendMessage(isNeedExit, terminalkey2);
                            for (String termId : termIdMap.keySet())
                            {
                                val = String.valueOf(termIdMap.get(termId)).split(",");
                                if (val.length == 3)
                                {
                                    try
                                    {
                                        if (ConstValues.FLAG_1.equals(val[0]))
                                        {
                                            mstTerminalinfoMDao.executeRaw(termSql, new String[] { val[1], val[2], "1", termId });
                                        }
                                        else
                                        {
                                            mstTerminalinfoMDao.executeRaw(termSql, new String[] { val[1], val[2], "0", termId });
                                        }
                                    }
                                    catch (SQLException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg)
                    {
                        Log.e(TAG, msg, error);
                        DbtLog.logUtils("退出上传", "上传所有temp终端Fail");
                        onFailureSendMessage(isNeedExit, terminalkey2);
                    }
                });
            }
            else
            {
                if (isNeedExit)
                {
                	// 上传所有temp终端
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
    * 上传所有新增终端
    * @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
    */
    public void upload_terminalAdd(final boolean isNeedExit, final String terminalkey2, final int whatId)
    {
        final List<MstTerminalinfoM> terminalinfoms = new ArrayList<MstTerminalinfoM>();
        final List<MstInvalidapplayInfo> mInvalidapplayInfos_terminal = new ArrayList<MstInvalidapplayInfo>();
        try
        {
            terminalinfoms.addAll(TermAddService.getTermAddListFromShared(context));

            List<Map<String, String>> mainDatas = new ArrayList<Map<String, String>>();
            if (!terminalinfoms.isEmpty())
            {
                if (ConstValues.isUploadTermAdd)
                {
                    return;
                }
                else
                {
                    ConstValues.isUploadTermAdd = true;
                }
                for (MstTerminalinfoM terminalinfom : terminalinfoms)
                {
                    Map<String, String> childDatas = new HashMap<String, String>();
                    terminalinfom.setPadisconsistent("1");
                    childDatas.put("MST_TERMINALINFO_M", JsonUtil.toJson(terminalinfom));
                    // 终端档案申请表
                    MstInvalidapplayInfo childInvalidapplayInfos = new MstInvalidapplayInfo();
                    childInvalidapplayInfos.setApplaykey(FunUtil.getUUID());
                    childInvalidapplayInfos.setStatus("1");
                    childInvalidapplayInfos.setApplaycause("1");
                    childInvalidapplayInfos.setDeleteflag("0");
                    mInvalidapplayInfos_terminal.add(childInvalidapplayInfos);
                    childDatas.put("MST_INVALIDAPPLAY_INFO", JsonUtil.toJson(mInvalidapplayInfos_terminal));
                    mainDatas.add(childDatas);
                }

                HttpUtil httpUtil = new HttpUtil();
                httpUtil.configTimeout(5 * 60000);
                httpUtil.configResponseTextCharset("ISO-8859-1");
                String json = JsonUtil.toJson(mainDatas);
                Log.e(TAG, "终端上传send:" + mainDatas.size() + json);
                httpUtil.send("opt_save_terminal", json, new RequestCallBack<String>()
                {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {
                        ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                        Log.i(TAG, "新增终端上传" + resObj.getResBody().getContent());
                        Map<String, Object> termIdMap = JsonUtil.parseMap(resObj.getResBody().getContent());
                        String[] val;
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus()))
                        {
                            for (MstTerminalinfoM terminalinfo : terminalinfoms)
                            {
                                //termIdMap.put(termId, "1," + terminal.getTerminalkey() + "," + terminal.getTerminalcode());接口返回字段
                                val = String.valueOf(termIdMap.get(terminalinfo.getTerminalkey())).split(",");
                                try
                                {
                                    if (val.length == 3)
                                    {
                                        TermAddService.delTermAddFromShared(context, terminalinfo);
                                        terminalinfo.setTerminalkey(val[1]);
                                        terminalinfo.setTerminalcode(val[2]);
                                        terminalinfo.setPadisconsistent("1");
                                        mstTerminalinfoMDao.createOrUpdate(terminalinfo);
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            DbtLog.logUtils("退出上传", "上传所有新增终端成功Success");
                            onSuccessSendMessage(whatId, isNeedExit);
                        }
                        else
                        {

                        	DbtLog.logUtils("退出上传", "上传所有新增终端E");
                            onFailureSendMessage(isNeedExit, terminalkey2);
                            for (MstTerminalinfoM terminalinfo : terminalinfoms)
                            {
                                //termIdMap.put(termId, "1," + terminal.getTerminalkey() + "," + terminal.getTerminalcode());接口返回字段
                                val = String.valueOf(termIdMap.get(terminalinfo.getTerminalkey())).split(",");
                                try
                                {
                                    if (val.length == 3)
                                    {
                                        if (ConstValues.FLAG_1.equals(val[1]))
                                        {
                                            TermAddService.delTermAddFromShared(context, terminalinfo);
                                            terminalinfo.setTerminalkey(val[1]);
                                            terminalinfo.setTerminalcode(val[2]);
                                            terminalinfo.setPadisconsistent("1");
                                            mstTerminalinfoMDao.createOrUpdate(terminalinfo);

                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                        ConstValues.isUploadTermAdd = false;
                    }

                    @Override
                    public void onFailure(HttpException error, String msg)
                    {
                        Log.e(TAG, msg, error);
                        ConstValues.isUploadTermAdd = false;
                        DbtLog.logUtils("退出上传", "上传所有新增终端Fail");
                        onFailureSendMessage(isNeedExit, terminalkey2);
                    }
                });
            }
            else
            {
                if (isNeedExit)
                {
                	// 上传所有新增终端
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    List<MstPlanforuserM> mPlanforuserms = new ArrayList<MstPlanforuserM>();
    List<MstPlancheckInfo> mPlancheckInfos = new ArrayList<MstPlancheckInfo>();
    List<MstPlancollectionInfo> mPlancollectionInfos = new ArrayList<MstPlancollectionInfo>();
    List<MstPlanrouteInfo> mplanRouteInfos = new ArrayList<MstPlanrouteInfo>();
    List<MstPlancollectionInfo> plancollectionInfos = new ArrayList<MstPlancollectionInfo>();
    private List<MstPlanTerminalM> planTerminalMs = new ArrayList<MstPlanTerminalM>();

    /**
     * 上传工作计划 workplan
     * @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
     */

    public void upload_work_plans(final boolean isNeedExit, final String planKey)
    {
        try
        {
            if (planKey != null)
            {
                MstPlanforuserM mPlanforuserm = mstPlanforuserMDao.queryForId(planKey);
                if (mPlanforuserm != null)
                {
                    mPlanforuserms.add(mPlanforuserm);
                    Map<String, Object> fieldValues = new HashMap<String, Object>();
                    fieldValues.put("plankey", planKey);
                    fieldValues.put("padisconsistent", "0");
                    mPlancheckInfos = mstPlancheckInfoDao.queryForFieldValues(fieldValues);
                    mplanRouteInfos = mstPlanrouteInfodDao.queryForFieldValues(fieldValues);
                }
            }
            else
            {
                // MST_PLANFORUSER_M(人员计划主表) MstPlanforuserM
                mPlanforuserms = mstPlanforuserMDao.queryForEq("padisconsistent", "2");//为什么是2 看本上的记录 //0 记录创建时为0 ,
                																	   // 2 确定上传 ,1 上传成功
                // MST_PLANCHECK_INFO(计划指标信息) MstPlancheckInfo
                mPlancheckInfos = mstPlancheckInfoDao.queryForEq("padisconsistent", "0");
                mstPlanrouteInfodDao.queryForEq("padisconsistent", "0");
            }
            // MST_PLANCOLLECTION_INFO(计划指标与采集项信息)MstPlancollectionInfo
            mPlancollectionInfos = mstPlancollectionInfoDao.queryForEq("padisconsistent", "0");

            // 根据表结果组织数据关系
            if (!mPlanforuserms.isEmpty())
            {
                List<HashMap<String, String>> mainList = new ArrayList<HashMap<String, String>>();
                for (MstPlanforuserM mPlanforuser : mPlanforuserms)
                {
                    HashMap<String, String> mainMap = new HashMap<String, String>();
                    // ↓直接上传Planstatus为5(自动通过) 原来是4//ywm 20160413--------------------------
                    mPlanforuser.setPlanstatus("5");//传到服务器如果服务器保存成功 web端就是已提交 保存失败本地不修改 成功本地数据改成4
                    // ↑直接上传Planstatus为5(自动通过) //ywm 20160413--------------------------
                    mPlanforuser.setPadisconsistent("1");
                    mainMap.put("MST_PLANFORUSER_M", JsonUtil.toJson(mPlanforuser));
                    String plankey = mPlanforuser.getPlankey();
                    // MST_PLANCHECK_INFO(计划指标信息) MstPlancheckInfo
                    List<MstPlancheckInfo> plancheckInfos = new ArrayList<MstPlancheckInfo>();
                    plancollectionInfos = new ArrayList<MstPlancollectionInfo>();
                    List<MstPlanrouteInfo> planRouteInfos = new ArrayList<MstPlanrouteInfo>();
                    planTerminalMs = new ArrayList<MstPlanTerminalM>();

                    for (MstPlancheckInfo mPlancheckInfo : mPlancheckInfos)
                    {
                        if (plankey.equals(mPlancheckInfo.getPlankey()))
                        {
                            mPlancheckInfo.setPadisconsistent("1");
                            plancheckInfos.add(mPlancheckInfo);
                            String pcheckkey = mPlancheckInfo.getPcheckkey();
                            for (MstPlancollectionInfo mPlancollectionInfo : mPlancollectionInfos)
                            {
                                if (pcheckkey.equals(mPlancollectionInfo.getPcheckkey()))
                                {
                                    mPlancollectionInfo.setPadisconsistent("1");// 之所以设置Padisconsistent为1 是为了上传后台Padisconsistent为1 同步下来也是1
                                    plancollectionInfos.add(mPlancollectionInfo);
                                    // 根据mPlancollectionInfo的pcolitemkey查询mst_planterminal_m表
                                    List<MstPlanTerminalM> MstPlanTerminalMs = mstPlanTerminalMDao.queryForEq("pcolitemkey", mPlancollectionInfo.getPcolitemkey());
                                    for (MstPlanTerminalM mstPlanTerminalM : MstPlanTerminalMs) {
                                    	mstPlanTerminalM.setPadisconsistent("1");
                                    	planTerminalMs.add(mstPlanTerminalM);// 
									}
                                }
                            }

                        }
                    }
                    for (MstPlanrouteInfo planRouteInfo : mplanRouteInfos)
                    {

                        if (plankey.equals(planRouteInfo.getPlankey()))
                        {
                            planRouteInfo.setPadisconsistent("1");
                            planRouteInfos.add(planRouteInfo);
                        }
                    }
                    mainMap.put("MST_PLANCHECK_INFO", JsonUtil.toJson(plancheckInfos));
                    mainMap.put("MST_PLANCOLLECTION_INFO", JsonUtil.toJson(plancollectionInfos));
                    mainMap.put("MST_PLANROUTE_INFO", JsonUtil.toJson(planRouteInfos));
                    mainMap.put("MST_PLANTERMINAL_M", JsonUtil.toJson(planTerminalMs));
                    mainList.add(mainMap);
                }
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.configResponseTextCharset("ISO-8859-1");
                String json = JsonUtil.toJson(mainList);
                Log.e(TAG, "工作计划 send:" + json);
                httpUtil.send("opt_save_workplant", json, new RequestCallBack<String>()
                {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {
                        ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                        Log.i(TAG, "工作计划:" + resObj.getResBody().getContent());
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus()))
                        {
                            try
                            {

                                for (MstPlanforuserM mPlanforuserm : mPlanforuserms)
                                {
                                    mPlanforuserm.setPadisconsistent("1");
                                    //状态4 显示上传成功标志
                                    // ↓修改Planstatus为5(自动通过) 原来是4//ywm 20160413----
                                    mPlanforuserm.setPlanstatus("5");
                                    // ↑修改Planstatus为5(自动通过) 原来是4//ywm 20160413----
                                    mstPlanforuserMDao.createOrUpdate(mPlanforuserm);
                                }

                                for (MstPlancheckInfo mPlancheckInfo : mPlancheckInfos)
                                {
                                    mPlancheckInfo.setPadisconsistent("1");
                                    mstPlancheckInfoDao.createOrUpdate(mPlancheckInfo);
                                }

                                for (MstPlancollectionInfo mPlancollectionInfo : plancollectionInfos)
                                {

                                    mPlancollectionInfo.setPadisconsistent("1");
                                    mstPlancollectionInfoDao.createOrUpdate(mPlancollectionInfo);
                                }
                                
                                for (MstPlanTerminalM planTerminalM : planTerminalMs) {
                                	planTerminalM.setPadisconsistent("1");
                                	mstPlanTerminalMDao.createOrUpdate(planTerminalM);
								}

                                for (MstPlanrouteInfo plancollectionInfo : mplanRouteInfos)
                                {
                                    plancollectionInfo.setPadisconsistent("1");
                                    mstPlanrouteInfodDao.createOrUpdate(plancollectionInfo);
                                }
                            }
                            catch (SQLException e)
                            {
                                e.printStackTrace();
                            }
                            if (planKey != null)
                            {
                                handler.sendEmptyMessage(WorkPlanFragment1.UPLOADSUCCESS);//上传成功刷新界面
                            }else if(planKey == null){
                            	//handler.sendEmptyMessage(WorkPlanFragment1.ALLDAYUPLOADSUCCESS);//
                            	//handler.sendEmptyMessage(WorkPlanFragment1.UPLOADSUCCESS);//
                            	//WorkPlanFragment1.initworkplan();
                            }
                            DbtLog.logUtils("退出上传", "上传工作计划成功Success");
                            onSuccessSendMessage(isNeedExit, planKey);
                        }
                        else
                        {

                        	DbtLog.logUtils("退出上传", "上传工作计划E");
                            onFailureSendMessage(isNeedExit, planKey);
                            String content = resObj.getResBody().getContent();
                            List<String> ids = JsonUtil.parseList(content, String.class);
                            updateInids("MST_PLANFORUSER_M", "plankey", ids);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg)
                    {
                        Log.e(TAG, msg, error);
                        DbtLog.logUtils("退出上传", "上传工作计划Fail");
                        onFailureSendMessage(isNeedExit, planKey);
                    }
                });
            }
            else
            {
                if (isNeedExit)
                {
                	// 上传工作计划
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            if (planKey != null)
            {
                ViewUtil.sendMsg(context, "上传失败");
            }
        }

    }

    /***
     * 上传周工作计划
     * @param isNeedExit
     * @param planKey
     */
    public void upload_weekwork_plans(final boolean isNeedExit, final String planKey)
    {
        final List<MstPlanWeekforuserM> mPlanforuserms = new ArrayList<MstPlanWeekforuserM>();
        try
        {
            if (planKey != null)
            {
                MstPlanWeekforuserM mPlanforuserm = mstPlanWeekforuserMDao.queryForId(planKey);
                if (mPlanforuserm != null)
                {
                    mPlanforuserms.add(mPlanforuserm);
                }
            }
            else
            {
                List<MstPlanWeekforuserM> mPlanforuserms2 = mstPlanWeekforuserMDao.queryForEq("padisconsistent", "0");
                mPlanforuserms.addAll(mPlanforuserms2);
            }

            // 根据表结果组织数据关系
            if (!mPlanforuserms.isEmpty())
            {
                for (MstPlanWeekforuserM mPlanforuser : mPlanforuserms)
                {
                    mPlanforuser.setPlanstatus("4");//传到服务器如果服务器保存成功 web端就是已提交 保存失败本地不修改 成功本地数据改成4
                    mPlanforuser.setPadisconsistent("1");
                }
                HttpUtil httpUtil = new HttpUtil();
                httpUtil.configResponseTextCharset("ISO-8859-1");
                String json = JsonUtil.toJson(mPlanforuserms);
                Log.e(TAG, "周工作计划 send:" + json);
                httpUtil.send("opt_save_planweek", json, new RequestCallBack<String>()
                {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {
                        ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                        Log.i(TAG, "工作计划:" + resObj.getResBody().getContent());
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus()))
                        {
                            try
                            {
                                for (MstPlanWeekforuserM mPlanforuserm : mPlanforuserms)
                                {
                                    mPlanforuserm.setPadisconsistent("1");
                                    //状态4 显示上传成功标志
                                    mPlanforuserm.setPlanstatus("4");
                                    mstPlanWeekforuserMDao.createOrUpdate(mPlanforuserm);
                                }
                                //ViewUtil.sendMsg(context, "周计划保存成功");
                            }
                            catch (SQLException e)
                            {
                                e.printStackTrace();
                            }
                            if (planKey != null)
                            {
                                handler.sendEmptyMessage(WorkPlanFragment1.WeekUPLOADSUCCESS);//上传成功刷新界面
                            }
                            DbtLog.logUtils("退出上传", "上传周工作计划成功Success");
                            onSuccessSendMessage(isNeedExit, planKey);
                        }
                        else
                        {
                        	DbtLog.logUtils("退出上传", "上传周工作计划E");
                            onFailureSendMessage(isNeedExit, planKey);
                            String content = resObj.getResBody().getContent();
                            List<String> ids = JsonUtil.parseList(content, String.class);
                            updateInids("MST_PLANWEEKFORUSER_M", "plankey", ids);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg)
                    {
                        Log.e(TAG, msg, error);
                        DbtLog.logUtils("退出上传", "上传周工作计划Fail");
                        onFailureSendMessage(isNeedExit, planKey);
                        //ViewUtil.sendMsg(context, "网络异常E 周计划保存失败");
                    }
                });
            }
            else
            {
                if (isNeedExit)
                {
                	// 上传周工作计划
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            if (planKey != null)
            {
                ViewUtil.sendMsg(context, "上传失败");
            }
        }

    }

    private List<MstAgencyvisitM> agencyvisitMs = new ArrayList<MstAgencyvisitM>();
    private List<MstAgencytransferInfo> agencytransferInfos = new ArrayList<MstAgencytransferInfo>();
    private List<MstInvoicingInfo> invoicingInfos = new ArrayList<MstInvoicingInfo>();

    /**
     * 经销商拜访
     * @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
     * 
     */
    public void upload_agency_visit(final boolean isNeedExit, final String agevisitKey)
    {
        try
        {

            if (agevisitKey != null)
            {
                MstAgencyvisitM agencyvisitM = mstAgencyvisitMDao.queryForId(agevisitKey);
                if (agencyvisitM != null)
                {
                    agencyvisitMs.add(agencyvisitM);
                    Map<String, Object> fieldValues = new HashMap<String, Object>();
                    fieldValues.put("agevisitkey", agevisitKey);
                    fieldValues.put("padisconsistent", "0");
                    invoicingInfos = mstInvoicingInfoDao.queryForFieldValues(fieldValues);
                    agencytransferInfos = mstAgencytransferInfoDao.queryForFieldValues(fieldValues);

                }

            }
            else
            {
                Map<String, Object> fieldValues = new HashMap<String, Object>();
                fieldValues.put("uploadFlag", "1");
                fieldValues.put("padisconsistent", "0");
                agencyvisitMs = mstAgencyvisitMDao.queryForFieldValues(fieldValues);
                invoicingInfos = mstInvoicingInfoDao.queryForEq("padisconsistent", "0");
                agencytransferInfos = mstAgencytransferInfoDao.queryForEq("padisconsistent", "0");
            }

            if (!agencyvisitMs.isEmpty())
            {

                //			MST_AGENCYVISIT_M(分经销商拜访主表)
                List<Map<String, String>> mainDatas = new ArrayList<Map<String, String>>();
                for (MstAgencyvisitM agencyvisitM : agencyvisitMs)
                {
                    agencyvisitM.setPadisconsistent("1");
                    Map<String, String> chilidMap = new HashMap<String, String>();
                    String agevisitkey = agencyvisitM.getAgevisitkey();
                    chilidMap.put("MST_AGENCYVISIT_M", JsonUtil.toJson(agencyvisitM));
                    List<MstInvoicingInfo> childMstInvoicingInfos = new ArrayList<MstInvoicingInfo>();
                    for (MstInvoicingInfo invoicingInfo : invoicingInfos)
                    {
                        if (agevisitkey.equals(invoicingInfo.getAgevisitkey()))
                        {
                            invoicingInfo.setPadisconsistent("1");
                            childMstInvoicingInfos.add(invoicingInfo);
                        }
                    }
                    chilidMap.put("MST_INVOICING_INFO", JsonUtil.toJson(childMstInvoicingInfos));
                    List<MstAgencytransferInfo> childMstAgencytransferInfos = new ArrayList<MstAgencytransferInfo>();
                    for (MstAgencytransferInfo agencytransferInfo : agencytransferInfos)
                    {
                        if (agevisitkey.equals(agencytransferInfo.getAgevisitkey()))
                        {
                            agencytransferInfo.setPadisconsistent("1");
                            childMstAgencytransferInfos.add(agencytransferInfo);
                        }
                    }
                    chilidMap.put("MST_AGENCYTRANSFER_INFO", JsonUtil.toJson(childMstAgencytransferInfos));
                    mainDatas.add(chilidMap);
                }

                HttpUtil httpUtil = new HttpUtil();
                httpUtil.configResponseTextCharset("ISO-8859-1");
                String json = JsonUtil.toJson(mainDatas);
                Log.e(TAG, " 经销商拜访 send:" + json);
                httpUtil.send("opt_save_agency", json, new RequestCallBack<String>()
                {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo)
                    {
                        ResponseStructBean resObj = HttpUtil.parseRes(responseInfo.result);
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus()))
                        {
                            Log.i(TAG, "经销商拜访" + resObj.getResBody().getContent());
                            for (MstAgencyvisitM agencyvisitM : agencyvisitMs)
                            {
                                try
                                {
                                    agencyvisitM.setPadisconsistent("1");
                                    mstAgencyvisitMDao.createOrUpdate(agencyvisitM);
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            for (MstInvoicingInfo invoicingInfo : invoicingInfos)
                            {
                                invoicingInfo.setPadisconsistent("1");
                                try
                                {
                                    mstInvoicingInfoDao.update(invoicingInfo);
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            for (MstAgencytransferInfo agencytransferInfo : agencytransferInfos)
                            {
                                agencytransferInfo.setPadisconsistent("1");
                                try
                                {
                                    mstAgencytransferInfoDao.update(agencytransferInfo);
                                }
                                catch (SQLException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            DbtLog.logUtils("退出上传", "经销商拜访Success");
                            onSuccessSendMessage(isNeedExit, agevisitKey);
                        }
                        else
                        {

                        	DbtLog.logUtils("退出上传", "经销商拜访E");
                            onFailureSendMessage(isNeedExit, agevisitKey);
                            String content = resObj.getResBody().getContent();
                            List<String> ids = JsonUtil.parseList(content, String.class);
                            updateInids("MST_AGENCYVISIT_M", "agevisitkey", ids);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg)
                    {
                        Log.e(TAG, msg, error);
                        DbtLog.logUtils("退出上传", "经销商拜访Fail");
                        onFailureSendMessage(isNeedExit, agevisitKey);
                    }
                });
            }
            else
            {
                if (isNeedExit)
                {
                	// 经销商拜访
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    List<MstVisitM> visits = new ArrayList<MstVisitM>();
    List<MstVisitM> visitsall = new ArrayList<MstVisitM>();
    List<MstVistproductInfo> mVistproductInfos = new ArrayList<MstVistproductInfo>();
    List<MstInvalidapplayInfo> mInvalidapplayInfos_visit = new ArrayList<MstInvalidapplayInfo>();
    List<MstPromotermInfo> mPromotermInfos = new ArrayList<MstPromotermInfo>();
    List<MstTerminalinfoM> mTerminalinfoMs_visit = new ArrayList<MstTerminalinfoM>();
    List<MstCheckexerecordInfo> mCheckexerecordInfos = new ArrayList<MstCheckexerecordInfo>();
    List<MstCollectionexerecordInfo> mCollectionexerecordInfos = new ArrayList<MstCollectionexerecordInfo>();
    List<MstAgencysupplyInfo> mAgencysupplyInfos = new ArrayList<MstAgencysupplyInfo>();
    List<MstCmpsupplyInfo> cmpsupplyInfos = new ArrayList<MstCmpsupplyInfo>();
    
    // 上传图片列表
	List<UpCameraListMStc> upCameraListMStcs = new ArrayList<UpCameraListMStc>();
	// 上传产品组合是否达标
	List<MstGroupproductM> mMstGroupproductMs = new ArrayList<MstGroupproductM>();
	
	

    /**
     * 上传所有的巡店拜访
     *  @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
     *  
     *  离线拜访修改: 
     *  	以前是针对一家终端多次离线拜访,所有边防数据都上传,但是数据都跟最后一次的数据相同
     *  	现在 针对同一家终端,一天内进行多次离线拜访,要求只上传最后一次数据
     *  	1 上传时,查询拜访主表,获取两个集合,
     *  	2 一个存放今天所有终端所有次数的拜访记录,另一个只存放所有终端的最新一次拜访记录(分组)
     *  	3 上传第二个集合对应的拜访记录数据,
     *  	4 成功上传后,依据第一个集合更改所有拜访记录的上传标记为已上传
     */
	public void upload_visit(final boolean isNeedExit, final String visitKey,
            final int whatId) {
        try {
            MstVisitM visit = null;
            if (visitKey != null && !visitKey.trim().equals("")) {
                visit = mstVisitMDao.queryForId(visitKey);
                if (visit != null) {
                    // String visitDate = DateUtil.formatDate(new Date(),
                    // "yyyyMMddHHmmss");
                    // visit.setEnddate(visitDate);
                    visits.add(visit);
                    visitsall.add(visit);
                    String terminalkey = visit.getTerminalkey();
                    Map<String, Object> visitKeyMap = new HashMap<String, Object>();
                    visitKeyMap.put("visitkey", visitKey);
                    visitKeyMap.put("padisconsistent", "0");
                    Map<String, Object> terminalKeyMap = new HashMap<String, Object>();
                    terminalKeyMap.put("terminalkey", terminalkey);
                    terminalKeyMap.put("padisconsistent", "0");
                    // MST_INVALIDAPPLAY_INFO(终端申请表)
                    mInvalidapplayInfos_visit = mstInvalidapplayInfoDao
                            .queryForFieldValues(visitKeyMap);
                    // MST_PROMOTERM_INFO(终端参加活动信息表)MstPromotermInfo
                    mPromotermInfos = mstPromotermInfoDao
                            .queryForFieldValues(visitKeyMap);
                    // MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)MstVistproductInfo
                    mVistproductInfos = mstVistproductInfoDao
                            .queryForFieldValues(visitKeyMap);
                    // MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)MstCollectionexerecordInfo
                    mCollectionexerecordInfos = mstCollectionexerecordInfoDao
                            .queryForFieldValues(visitKeyMap);
                    for (int i = 0; i < mCollectionexerecordInfos.size(); i++) {
                        if (null != mCollectionexerecordInfos.get(i)
                                .getUpdateuser()) {
                            if ("0-1".equals(mCollectionexerecordInfos.get(i)
                                    .getUpdateuser().trim())) {
                                mCollectionexerecordInfos.get(i).setUpdateuser(
                                        visit.getUserid());
                            }
                        }

                    }
                    // MST_TERMINALINFO_M(终端档案主表)MstTerminalinfoM
                    mTerminalinfoMs_visit = mstTerminalinfoMDao.queryForEq(
                            "padisconsistent", "0");
                    // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                    mCheckexerecordInfos = mstCheckexerecordInfoDao
                            .queryForFieldValues(terminalKeyMap);
                    // mTerminalinfoMs_visit =
                    // mstTerminalinfoMDao.queryForFieldValues(terminalKeyMap);
                    
                    // MST_CMPSUPPLY_INFO(竞品供货关系表)
                    cmpsupplyInfos = mstCmpsupplyInfoDao
                            .queryForFieldValues(terminalKeyMap);
                    
                    // MstGroupproductM (产品组合是否达标)
                    //mMstGroupproductMs = mstGroupproductMDao.queryForFieldValues(terminalKeyMap);
                    // 存放所有MstGroupproductM表的未上传记录
                    QueryBuilder<MstGroupproductM, String> MstGroupproductMQB1 = mstGroupproductMDao.queryBuilder();
                    Where<MstGroupproductM, String> mstGroupproductMWhere1 = MstGroupproductMQB1.where();
                    mstGroupproductMWhere1.eq("uploadflag", "1");
                    mstGroupproductMWhere1.and();
                    mstGroupproductMWhere1.eq("padisconsistent", "0");
                    MstGroupproductMQB1.orderBy("createdate", true);
                    mMstGroupproductMs = MstGroupproductMQB1.query();

                    Map<String, Object> lowerkeyMap = new HashMap<String, Object>();
                    lowerkeyMap.put("lowerkey", terminalkey);
                    lowerkeyMap.put("padisconsistent", "0");
                    // MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)MstAgencysupplyInfo
                    mAgencysupplyInfos = mstAgencysupplyInfoDao
                            .queryForFieldValues(lowerkeyMap);

                }

            } else {
                // Map<String, Object> hashMap = new HashMap<String, Object>();
                // hashMap.put("uploadFlag", "1");
                // hashMap.put("padisconsistent", "0");
                // visits = mstVisitMDao.queryForFieldValues(hashMap);
            	
            	// 只存放所有终端的最新一次拜访记录(分组)
                QueryBuilder<MstVisitM, String> visitQB = mstVisitMDao
                        .queryBuilder();
                Where<MstVisitM, String> visitWhere = visitQB.where();
                visitWhere.eq("uploadFlag", "1");
                visitWhere.and();
                visitWhere.eq("padisconsistent", "0");
                //productQb.groupBy(DBConst.PROD_PARENT_PRODVAR_ID); 
                // 离线拜访时,针对同一家终端多次拜访,只上传最后一次拜访数据
                visitQB.groupBy("terminalkey"); 
                
                visitQB.orderBy("terminalkey", true);
                visitQB.orderBy("visitdate", false);
                
                visits = visitQB.query();
                
                
                // 存放今天所有终端所有次数的拜访记录
                QueryBuilder<MstVisitM, String> visitQB1 = mstVisitMDao
                        .queryBuilder();
                Where<MstVisitM, String> visitWhere1 = visitQB1.where();
                visitWhere1.eq("uploadFlag", "1");
                visitWhere1.and();
                visitWhere1.eq("padisconsistent", "0");
                //productQb.groupBy(DBConst.PROD_PARENT_PRODVAR_ID); 
                visitQB1.orderBy("terminalkey", true);
                visitQB1.orderBy("visitdate", false);
                visitsall = visitQB1.query();
                
                
                
                if (visits != null && !visits.isEmpty()) {
                    // MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)MstVistproductInfo
                    mVistproductInfos = mstVistproductInfoDao.queryForEq(
                            "padisconsistent", "0");
                    // MST_INVALIDAPPLAY_INFO(终端申请表)MstInvalidapplayInfo
                    // 此处不上传终端申请失效数据
                    // mInvalidapplayInfos_visit =
                    // mstInvalidapplayInfoDao.queryForEq("padisconsistent",
                    // "0");
                    // MST_TERMINALINFO_M(终端档案主表)MstTerminalinfoM
                    // 只上传非终端申请失效数据
                    // mTerminalinfoMs_visit =
                    // mstTerminalinfoMDao.queryForEq("padisconsistent", "0");
                    QueryBuilder<MstTerminalinfoM, String> termQb = mstTerminalinfoMDao
                            .queryBuilder();
                    Where<MstTerminalinfoM, String> termWhere = termQb.where();
                    termWhere.eq("padisconsistent", "0");
                    termWhere.and();
                    termWhere.ne("status", ConstValues.FLAG_3);
                    mTerminalinfoMs_visit = termQb.query();
                    // MST_PROMOTERM_INFO(终端参加活动信息表)MstPromotermInfo
                    mPromotermInfos = mstPromotermInfoDao.queryForEq(
                            "padisconsistent", "0");
                    // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                    mCheckexerecordInfos = mstCheckexerecordInfoDao.queryForEq(
                            "padisconsistent", "0");
                    if (null != visit) {
                        for (int i = 0; i < mCheckexerecordInfos.size(); i++) {
                            if (null != mCheckexerecordInfos.get(i).getUpdateuser()) {
                                if ("0-1".equals(mCheckexerecordInfos.get(i).getUpdateuser().trim())) {
                                    mCheckexerecordInfos.get(i).setUpdateuser(visit.getUserid());
                                }
                            }
                        }
                    }
                    // MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)MstCollectionexerecordInfo
                    mCollectionexerecordInfos = mstCollectionexerecordInfoDao
                            .queryForEq("padisconsistent", "0");
                    // MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)MstAgencysupplyInfo
                    mAgencysupplyInfos = mstAgencysupplyInfoDao.queryForEq(
                            "padisconsistent", "0");
                    // MST_CMPSUPPLY_INFO(竞品供货关系表)
                    cmpsupplyInfos = mstCmpsupplyInfoDao.queryForEq(
                            "padisconsistent", "0");
                    
                    // 存放所有MstGroupproductM表的未上传记录
                    QueryBuilder<MstGroupproductM, String> MstGroupproductMQB1 = mstGroupproductMDao.queryBuilder();
                    Where<MstGroupproductM, String> mstGroupproductMWhere1 = MstGroupproductMQB1.where();
                    mstGroupproductMWhere1.eq("uploadflag", "1");
                    mstGroupproductMWhere1.and();
                    mstGroupproductMWhere1.eq("padisconsistent", "0");
                    MstGroupproductMQB1.orderBy("createdate", true);
                    mMstGroupproductMs = MstGroupproductMQB1.query();

                }
            }
            if (visits != null && !visits.isEmpty()) {
                List<Map<String, String>> mainDatas = new ArrayList<Map<String, String>>();
                // 根据表结果组织数据关系
                for (MstVisitM mstVisitm : visits) {
                    Map<String, String> childDatas = new HashMap<String, String>();
                    mstVisitm.setPadisconsistent("1");
                    
                    String visitsss = JsonUtil.toJson(mstVisitm);
                    //savFile(visitsss, "MstVisits");// 采集项记录表
                    //FileUtil.writeTxt(visitsss,FileUtil.getSDPath()+"/MstVisits2.txt");//上传巡店拜访的json
                    childDatas.put("MST_VISIT_M", JsonUtil.toJson(mstVisitm));
                    
                    String visitkey = mstVisitm.getVisitkey();
                    String terminalkey = mstVisitm.getTerminalkey();
                    String terminalcode = "";
                    List<MstTerminalinfoM> childTerminalinfoMs = new ArrayList<MstTerminalinfoM>();
                    for (MstTerminalinfoM mTerminalinfoM : mTerminalinfoMs_visit) {
                        if (terminalkey.equals(mTerminalinfoM.getTerminalkey())) {
                        	terminalcode = mTerminalinfoM.getTerminalcode();
                            mTerminalinfoM.setPadisconsistent("1");
                            childTerminalinfoMs.add(mTerminalinfoM);
                        }
                    }
                    childDatas.put("MST_TERMINALINFO_M",JsonUtil.toJson(childTerminalinfoMs));
                    /*String childTerminalinfoMsqwe = JsonUtil.toJson(childTerminalinfoMs);
                    FileUtil.writeTxt(childTerminalinfoMsqwe, FileUtil.getSDPath()+"/childTerminalinfoMsqwe.txt");*/
                    
                    List<MstGroupproductM> childMstGroupproductMs = new ArrayList<MstGroupproductM>();
                    for (MstGroupproductM mstgroupproductm : mMstGroupproductMs) {
                        if (terminalcode.equals(mstgroupproductm.getTerminalcode())) {
                        	mstgroupproductm.setPadisconsistent("1");
                            childMstGroupproductMs.add(mstgroupproductm);
                        }
                    }
                    childDatas.put("MST_GROUPPRODUCT_M",JsonUtil.toJson(childMstGroupproductMs));
                    
                    
                    List<MstCmpsupplyInfo> childCmpsupplyInfos = new ArrayList<MstCmpsupplyInfo>();
                    for (MstCmpsupplyInfo childCmpsupplyInfo : cmpsupplyInfos) {
                        if (terminalkey.equals(childCmpsupplyInfo
                                .getTerminalkey())) {
                            childCmpsupplyInfo.setPadisconsistent("1");
                            childCmpsupplyInfos.add(childCmpsupplyInfo);
                        }
                    }
                    childDatas.put("MST_CMPSUPPLY_INFO",
                            JsonUtil.toJson(childCmpsupplyInfos));

                    // 终端档案申请表
                    List<MstInvalidapplayInfo> childInvalidapplayInfos = new ArrayList<MstInvalidapplayInfo>();
                    for (MstInvalidapplayInfo mInvalidapplayInfo : mInvalidapplayInfos_visit) {
                        if (visitkey.equals(mInvalidapplayInfo.getVisitkey())) {
                            mInvalidapplayInfo.setPadisconsistent("1");
                            childInvalidapplayInfos.add(mInvalidapplayInfo);
                        }
                    }
                    childDatas.put("MST_INVALIDAPPLAY_INFO",
                            JsonUtil.toJson(childInvalidapplayInfos));
                    // MST_PROMOTERM_INFO(终端参加活动信息表)
                    List<MstPromotermInfo> childPromotermInfos = new ArrayList<MstPromotermInfo>();
                    for (MstPromotermInfo mPromotermInfo : mPromotermInfos) {
                        if (visitkey.equals(mPromotermInfo.getVisitkey())) {
                            mPromotermInfo.setPadisconsistent("1");
                            childPromotermInfos.add(mPromotermInfo);
                        }
                    }

                    childDatas.put("MST_PROMOTERM_INFO",
                            JsonUtil.toJson(childPromotermInfos));
                    // MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)
                    List<MstVistproductInfo> childMstVistproductInfos = new ArrayList<MstVistproductInfo>();
                    for (MstVistproductInfo mVistproductInfo : mVistproductInfos) {
                        if (visitkey.equals(mVistproductInfo.getVisitkey())) {
                            mVistproductInfo.setPadisconsistent("1");
                            childMstVistproductInfos.add(mVistproductInfo);
                        }
                    }
                    String json = JsonUtil.toJson(childMstVistproductInfos);
                    //FileUtil.writeTxt(json, FileUtil.getSDPath()+"/mVistproductInfos0808.txt");
                    childDatas.put("MST_VISTPRODUCT_INFO",
                            JsonUtil.toJson(childMstVistproductInfos));

                    // MstAgencysupplyInfo MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)
                    List<MstAgencysupplyInfo> childMstAgencysupplyInfos = new ArrayList<MstAgencysupplyInfo>();
                    for (MstAgencysupplyInfo mAgencysupplyInfo : mAgencysupplyInfos) {
                        if (terminalkey.equals(mAgencysupplyInfo.getLowerkey())) {
                            mAgencysupplyInfo.setPadisconsistent("1");
                            mAgencysupplyInfo.setOrderbyno("1");// 上传时将今天新增的供货关系标记改为1
                            childMstAgencysupplyInfos.add(mAgencysupplyInfo);
                        }
                    }
                    childDatas.put("MST_AGENCYSUPPLY_INFO",
                            JsonUtil.toJson(childMstAgencysupplyInfos));

                    // MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)
                    List<MstCollectionexerecordInfo> childMstCollectionexerecordInfos = new ArrayList<MstCollectionexerecordInfo>();

                    for (MstCollectionexerecordInfo mCollectionexerecordInfo : mCollectionexerecordInfos) {
                        if (visitkey.equals(mCollectionexerecordInfo
                                .getVisitkey())) {
                            mCollectionexerecordInfo.setPadisconsistent("1");
                            childMstCollectionexerecordInfos
                                    .add(mCollectionexerecordInfo);
                        }
                    }
                    String collectionexerecord = JsonUtil.toJson(childMstCollectionexerecordInfos);
                    //FileUtil.writeTxt(collectionexerecord, FileUtil.getSDPath()+"/collectionexerecord1.txt");
                    childDatas.put("MST_COLLECTIONEXERECORD_INFO",
                            JsonUtil.toJson(childMstCollectionexerecordInfos));

                    // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                    List<MstCheckexerecordInfo> childMstCheckexerecordInfos = new ArrayList<MstCheckexerecordInfo>();
                    for (MstCheckexerecordInfo mCheckexerecordInfo : mCheckexerecordInfos) {
                    	if(null != visit){
                    		mCheckexerecordInfo.setUpdateuser(visit.getUserid());
                    	}
                    	// 修改多家离线上传数据错误bug
                    	if(terminalkey.equals(mCheckexerecordInfo.getTerminalkey())){
                    		
                    		// 只上传离线拜访的最后一次
                            if("temp".equals(mCheckexerecordInfo.getVisitkey())){
                            	mCheckexerecordInfo.setPadisconsistent("1");
                                childMstCheckexerecordInfos.add(mCheckexerecordInfo);
                            }
                            if(visitkey.equals(mCheckexerecordInfo.getVisitkey())){
                            	mCheckexerecordInfo.setPadisconsistent("1");
                                childMstCheckexerecordInfos.add(mCheckexerecordInfo);
                            }
                            
                            
                    	}
                    }

                    //String checkexerecord = JsonUtil.toJson(childMstCheckexerecordInfos);
                    //savFile(checkexerecord, "MST_CHECKEXERECORD_INFO");// 拉链表
                    //FileUtil.writeTxt(checkexerecord, FileUtil.getSDPath()+"/checkexerecord3213.txt");
                    childDatas.put("MST_CHECKEXERECORD_INFO",
                            JsonUtil.toJson(childMstCheckexerecordInfos));

                    mainDatas.add(childDatas);
                }
                
                // 添加
                HttpUtil httpUtil = new HttpUtil(10 * 60000);// 10分钟超时
                httpUtil.configResponseTextCharset("ISO-8859-1");
                String json = JsonUtil.toJson(mainDatas);
                FileUtil.writeTxt(json,FileUtil.getSDPath()+"/shopvisit1016.txt");//上传巡店拜访的json
                // System.out.println("巡店拜访"+json);
                Log.i(TAG, "巡店拜访send list size" + mainDatas.size() + json);
                httpUtil.send("opt_save_visit", json,
                        new RequestCallBack<String>() {
 
                            @Override
                            public void onSuccess(
                                    ResponseInfo<String> responseInfo) {
                                ResponseStructBean resObj = HttpUtil
                                        .parseRes(responseInfo.result);
                                Log.e(TAG, "巡店拜访"
                                        + resObj.getResBody().getContent());
                                // 获取请求服务器返回状态
                                if (ConstValues.SUCCESS.equals(resObj
                                        .getResHead().getStatus())) {

                                    // 更新表数据
                                    try {
                                        for (MstVisitM visit : visitsall) {
                                            visit.setPadisconsistent("1");
                                            mstVisitMDao.createOrUpdate(visit);

                                        }
                                        // 更新MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)MstVistproductInfo
                                        for (MstVistproductInfo mVistproductInfo : mVistproductInfos) {
                                            mVistproductInfo
                                                    .setPadisconsistent("1");
                                            mstVistproductInfoDao
                                                    .createOrUpdate(mVistproductInfo);

                                        }
                                        // 更新MST_INVALIDAPPLAY_INFO(终端申请表)MstInvalidapplayInfo
                                        for (MstInvalidapplayInfo mInvalidapplayInfo : mInvalidapplayInfos_visit) {
                                            mInvalidapplayInfo
                                                    .setPadisconsistent("1");
                                            mstInvalidapplayInfoDao
                                                    .createOrUpdate(mInvalidapplayInfo);

                                        }
                                       // 更新MST_INVALIDAPPLAY_INFO(终端申请表)MstInvalidapplayInfo
                                        for (MstGroupproductM mMstGroupproductM : mMstGroupproductMs) {
                                        	mMstGroupproductM
                                                    .setPadisconsistent("1");
                                            mstGroupproductMDao
                                                    .createOrUpdate(mMstGroupproductM);

                                        }
                                        // 更新MST_PROMOTERM_INFO(终端参加活动信息表)MstPromotermInfo
                                        for (MstPromotermInfo mPromotermInfo : mPromotermInfos) {
                                            mPromotermInfo
                                                    .setPadisconsistent("1");
                                            mstPromotermInfoDao
                                                    .createOrUpdate(mPromotermInfo);

                                        }// 更新MST_TERMINALINFO_M(终端档案主表)MstTerminalinfoM
                                        for (MstTerminalinfoM mTerminalinfoM : mTerminalinfoMs_visit) {
                                            mTerminalinfoM
                                                    .setPadisconsistent("1");
                                            mstTerminalinfoMDao
                                                    .createOrUpdate(mTerminalinfoM);

                                        }
                                        // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                                        Map<String, Object> siebelMap = JsonUtil
                                                .parseMap(resObj.getResBody()
                                                        .getContent());
                                        for (MstCheckexerecordInfo mCheckexerecordInfo : mCheckexerecordInfos) {
                                            if (siebelMap != null
                                                    && CheckUtil
                                                            .isBlankOrNull(mCheckexerecordInfo
                                                                    .getSiebelid())) {
                                                mCheckexerecordInfo.setSiebelid(FunUtil.isBlankOrNullTo(
                                                        siebelMap
                                                                .get(mCheckexerecordInfo
                                                                        .getRecordkey()),
                                                        null));
                                            }
                                            if (null != visits) {
                                                if (visits.size() > 0) {
                                                    mCheckexerecordInfo
                                                            .setUpdateuser(visits
                                                                    .get(0)
                                                                    .getUserid());
                                                }
                                            }
                                            mCheckexerecordInfo
                                                    .setPadisconsistent("1");
                                            mstCheckexerecordInfoDao
                                                    .createOrUpdate(mCheckexerecordInfo);

                                        }
                                        // 更新MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)MstCollectionexerecordInfo
                                        for (MstCollectionexerecordInfo mCollectionexerecordInfo : mCollectionexerecordInfos) {
                                            mCollectionexerecordInfo
                                                    .setPadisconsistent("1");
                                            mstCollectionexerecordInfoDao
                                                    .createOrUpdate(mCollectionexerecordInfo);

                                        }
                                        // 更新MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)MstAgencysupplyInfo
                                        for (MstAgencysupplyInfo mAgencysupplyInfo : mAgencysupplyInfos) {
                                            if (siebelMap != null
                                                    && CheckUtil
                                                            .isBlankOrNull(mAgencysupplyInfo
                                                                    .getSiebelkey())) {
                                                mAgencysupplyInfo.setSiebelkey(FunUtil.isBlankOrNullTo(
                                                        siebelMap
                                                                .get(mAgencysupplyInfo
                                                                        .getAsupplykey()),
                                                        null));
                                            }
                                            mAgencysupplyInfo.setPadisconsistent("1");
                                            mAgencysupplyInfo.setOrderbyno("1");// 上传成功将今天新增的供货关系标记改为1
                                            mstAgencysupplyInfoDao.createOrUpdate(mAgencysupplyInfo);

                                        }
                                        // 上传客情备忘录
                                        upload_memos(false, null);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    // 上传所以巡店拜访数据
                                    DbtLog.logUtils("退出上传", "上传所有的巡店拜访成功Success");
                                    onSuccessSendMessage(whatId, isNeedExit);
                                } else {
                                    String msg_timeoutString = resObj
                                            .getResBody().getContent();
                                    if (resObj.getResHead().getStatus()
                                            .equals("N")) {
                                        onFailureSendMessage_timeOut(
                                                isNeedExit, visitKey,
                                                msg_timeoutString.substring(0,
                                                        msg_timeoutString
                                                                .indexOf("[")));
                                    } else {
                                    	DbtLog.logUtils("退出上传", "上传所有的巡店拜访E");
                                        onFailureSendMessage(isNeedExit,
                                                visitKey);
                                    }
                                    // String content =
                                    // resObj.getResBody().getContent();
                                    List<String> ids = JsonUtil.parseList(
                                            msg_timeoutString
                                                    .substring(msg_timeoutString
                                                            .indexOf("[")),
                                            String.class);
                                    // 部分更新成功的时候,根据主/外 键更新数据
                                    updateInids("MST_VISIT_M", "visitkey", ids);
                                }
                            }

                            @Override
                            public void onFailure(HttpException error,
                                    String msg) {
                                Log.e(TAG, msg, error);
                                DbtLog.logUtils("退出上传", "上传所有的巡店拜访Fail");
                                onFailureSendMessage(isNeedExit, visitKey);
                            }
                        });
            } else {
                if (isNeedExit) {
                	//上传所有的巡店拜访
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成文件
     * 
	 * @param json
	 */
	private void savFile(String json,String name) {
		
		String sdcardPath = Environment.getExternalStorageDirectory() + "";
        String DbtPATH = sdcardPath + "/dbt/et.tsingtaopad";
        String BUGPATH = DbtPATH + "/log/";

		File txt = new File(BUGPATH+name+".txt");
		if (!txt.exists()) {
			try {
				txt.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		byte bytes[] = new byte[2048];
		bytes = json.getBytes(); // 新加的
		int b = json.length(); // 改
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(txt);
			fos.write(bytes, 0, b);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
     * 部分更新成功的时候
     * 根据主/外 键更新数据
     * @param tableName
     * @param keyName
     * @param ids
     * @throws Exception 
     * @throws SQLException
     */
    private void updateInids(String tableName, String keyName, List<String> ids)
    {

        //String sql = update + tableName + set padisconsistent='1'  where + keyName + "in (" + " 'id1','id2','id3' " + " ) set ";
        SQLiteDatabase database = helper.getWritableDatabase();
        if (!CheckUtil.IsEmpty(ids))
        {
            String idIn = FunUtil.brackReplace(ids);
            StringBuilder sqlBuilder = new StringBuilder();
            if("MST_VISIT_M".equals(tableName)){
            	sqlBuilder.append("delete from ");
            	sqlBuilder.append(tableName+" ");
            	//sqlBuilder.append(" set padisconsistent='0',comid='TIME_OUT' ");
            }else{
            	sqlBuilder.append("update ");
            	sqlBuilder.append(tableName);
            	sqlBuilder.append(" set padisconsistent='0' ");
            }
            sqlBuilder.append("where ");
            sqlBuilder.append(keyName);
            sqlBuilder.append(" in ( ");
            sqlBuilder.append(idIn);
            sqlBuilder.append(" )");

            Log.i(TAG, "上传部分更新成功 ：" + tableName + "表为： key 为 " + keyName + ids.size() + sqlBuilder.toString());
            if (database.isOpen())
            {
                database.execSQL(sqlBuilder.toString());

            }
        }
    }

    /**
     * 
     * @param obj 单个数据id或者对象
     * @param isNeedExit true:上传后退出系统
     */
    private void onFailureSendMessage(boolean isNeedExit, Object obj)
    {
        ViewUtil.sendMsg(context, "上传失败");
        //		if (obj != null) {
        //			ViewUtil.sendMsg(context, "上传失败");
        //		}else{
        //		    ViewUtil.sendMsg(context, "添加终端到Siebel系统失败");
        //		}
        //上传出错的情况下，发送消息阻止退出
        if (isNeedExit)
        {
            Message errmsg = new Message();
            errmsg.what = TitleLayout.UPLOAD_DATA;
            errmsg.obj = "error,上传出错了不允许退出";
            handler.sendMessage(errmsg);
        }
    }
    /**
     * 
     * @param obj 单个数据id或者对象
     * @param isNeedExit
     */
    private void onFailureSendMessage_timeOut(boolean isNeedExit, Object obj,String contents)
    {
        ViewUtil.sendMsg(context, contents);
        //		if (obj != null) {
        //			ViewUtil.sendMsg(context, "上传失败");
        //		}else{
        //		    ViewUtil.sendMsg(context, "添加终端到Siebel系统失败");
        //		}
        //上传出错的情况下，发送消息阻止退出
        if (isNeedExit)
        {
            Message errmsg = new Message();
            errmsg.what = TitleLayout.UPLOAD_DATA;
            errmsg.obj = "error,上传出错了不允许退出";
            handler.sendMessage(errmsg);
        }
    }
    /**
     * 上传成功后提示（待删除）
     * 
     * @param obj 单个数据id或者对象
     * @param isNeedExit
     */
    @Deprecated
    private void onSuccessSendMessage(boolean isNeedExit, Object obj)
    {
        if (isNeedExit)
        {
            handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
        }
        //单个数据 上传 结果发送出去
        if (obj != null)
        {
            ViewUtil.sendMsg(context, "上传成功");
            if (ConstValues.handler != null)
            {
                ConstValues.handler.sendEmptyMessage(ConstValues.WAIT2);//主要更新界面的状态位刷新listvie标志
            }
        }
    }

    /**
     * 用于网络上传成功后，发出提示信息
     * 
     * @param isNeedExit   
     * @param whatId       大于0的  
     */
    private void onSuccessSendMessage(int whatId, boolean isNeedExit)
    {
        if (isNeedExit)
        {
            handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
        }
        //单个数据 上传 结果发送出去
        if (whatId > 0)
        {
            ViewUtil.sendMsg(context, "上传成功");
            if (ConstValues.handler != null)
            {
                ConstValues.handler.sendEmptyMessage(whatId);
            }
        }
    }
    
    
    /**
     * 获取拍照上传json
     */
    private String getUpImageJson(String isupload,String visitKey){

		
		List<MstCameraListMStc> cameraListMStcs = new ArrayList<MstCameraListMStc>();
		cameraListMStcs = mstCameraiInfoMDao.getCameraListNoIsuploadByVisitkey(helper,isupload,visitKey);
		//List<UpCameraListMStc> upCameraListMStcs = new ArrayList<UpCameraListMStc>();

		if(!cameraListMStcs.isEmpty()){
			for (MstCameraListMStc mstCameraListMStc : cameraListMStcs) {

				UpCameraListMStc upCameraListMStc = new UpCameraListMStc();
				upCameraListMStc.setImagefile(mstCameraListMStc.getImagefileString());
				upCameraListMStc.setRecordkey(mstCameraListMStc.getCamerakey());
				upCameraListMStc.setVisitkey(mstCameraListMStc.getVisitkey());
				upCameraListMStc.setPicname(mstCameraListMStc.getPicname());
				upCameraListMStc.setLocalpath(mstCameraListMStc.getLocalpath());
				upCameraListMStc.setPictypekey(mstCameraListMStc.getPictypekey());
				//upCameraListMStc.setCreateuser(ConstValues.loginSession.getUserCode());
				upCameraListMStc.setCreateuser(PrefUtils.getString(context, "userCode", ""));
				//upCameraListMStc.setUpdateuser(ConstValues.loginSession.getUserCode());
				upCameraListMStc.setUpdateuser(PrefUtils.getString(context, "userCode", ""));
				//upCameraListMStc.setGridid(ConstValues.loginSession.getGridId());
				upCameraListMStc.setGridid(PrefUtils.getString(context, "gridId", ""));
				//upCameraListMStc.setDisid(ConstValues.loginSession.getDisId());
				upCameraListMStc.setDisid(PrefUtils.getString(context, "disId", ""));
				upCameraListMStc.setRouteid(new ShopVisitService(context, null)
						.findTermById(mstCameraListMStc.getTerminalkey())
						.getRoutekey());
				upCameraListMStcs.add(upCameraListMStc);
			}
			
			String json = JSON.toJSONString(upCameraListMStcs);
			return json;
		}else{
			return null;
		}
    }
    
    /**
     * 获取终端信息
     * 
     * @param termId    终端ID
     * @return
     */
    public MstTerminalInfoMStc findTermByTermkey(String termId) {
        
        MstTerminalInfoMStc termInfo = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstTerminalinfoMDao dao = helper.getDao(MstTerminalinfoM.class);
            termInfo = dao.findByTermId(helper, termId);
            
        } catch (SQLException e) {
            Log.e(TAG, "获取终端表DAO对象失败", e);
        }
        
        return termInfo;
    }
}
