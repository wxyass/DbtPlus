package et.tsingtaopad.visit.shopvisit.camera;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;


import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.GlobalValues;
import et.tsingtaopad.MyApplication;
import et.tsingtaopad.R;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.tables.MstCameraInfoM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DbtLog;
import et.tsingtaopad.tools.FileTool;
import et.tsingtaopad.tools.FileUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.ImageUtil;
import et.tsingtaopad.tools.PhotoUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.shopvisit.ShopVisitService;
import et.tsingtaopad.visit.shopvisit.camera.container.TakeCameraAty;
import et.tsingtaopad.visit.shopvisit.camera.domain.CameraDataStc;
import et.tsingtaopad.visit.shopvisit.camera.domain.CameraImageBean;
import et.tsingtaopad.visit.shopvisit.camera.domain.PictypeDataStc;
import et.tsingtaopad.visit.shopvisit.checkindex.CheckIndexService;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.visit.shopvisit.invoicing.InvoicingFragment;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：CameraFragment.java</br> 作者：ywm </br>
 * 创建时间：2015-11-10</br> 功能描述: 拍照Fragment</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
public class CameraFragment extends BaseFragmentSupport {

	private final String TAG = "CameraFragment";
	private et.tsingtaopad.view.MyGridView picGv;
	private Bitmap photo;
	private Bitmap picphoto;

	// 后台配置拍照类型集合
	// private List<String> mPicDescLst;
	// 标记点击时条目位置
	private int positionTag;
	// 适配器
	private GridAdapter gridAdapter;
	// 拍照得到的图片集合(初始长度为0)
	Map<Integer, Bitmap> cameraPicMap;
	// 拍过照的条目设置标记 0:该位置未拍过照 1:该位置拍过照
	Map<Integer, String> isCameraMap;
	// 本地图片路径
	private String filePath;
	// 终端主键,
	private String termId;
	// 终端名称
	private String termname;
	// 拜访主键
	private String visitKey;
	// 读取数据库图片表已拍照记录
	private List<CameraDataStc> piclst = new ArrayList<CameraDataStc>();
	// 当前时间 如:20110411
	private String currentdata;
	// 查询数据库 要拍几张照片
	private List<PictypeDataStc> valueLst;
	// 拜访0 查看1
	private String seeFlag;
	// 查看时的提示
	TextView descTv;
	ScrollView descGv;

	private int idsuccess = -1; // 0在本地保存成功 1在本地还没保存成功
	private Bitmap bitmap;// 拍照前默认图片
	private CameraService cameraService;

	// 图片保存路径
	private String path;
	private String name;

	AlertDialog dialog;

	MyHandler handler;

	//是否在加载数据
	private boolean isLoadingData = true;

	/*final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {// 拍照完成,刷新页面
				//
				if (dialog != null) {
					dialog.dismiss();
				}
				gridAdapter.notifyDataSetChanged();
			} 
			else if (msg.what == 2) {// 图片正在保存,请稍后
				Toast.makeText(getActivity(), "图片正在保存,请稍后", Toast.LENGTH_SHORT).show();
			} 
			else if (msg.what == 3) {// 弹出滚动条, 替换2
				// 弹出进度框
				dialog = new Builder(getActivity()).setCancelable(false).create();
				dialog.setView(getActivity().getLayoutInflater().inflate(R.layout.camera_progress, null), 0, 0, 0, 0);
				dialog.setCancelable(false); // 是否可以通过返回键 关闭
				dialog.show();
			} 
			else if (msg.what == 4) {// 拍照处理成功 取消滚动条, 拍照完成,刷新页面
				if (dialog != null) {
					dialog.dismiss();
				}
				gridAdapter.notifyDataSetChanged();
			}
			else if (msg.what == 6) {// 拍照处理失败 取消滚动条, 拍照完成,刷新页面
				if (dialog != null) {
					dialog.dismiss();
				}
				gridAdapter.notifyDataSetChanged();
				Toast.makeText(getActivity(), "图片保存失败,请重新拍照", Toast.LENGTH_SHORT).show();
				DbtLog.logUtils(TAG, "onActivityResult()-图片保存失败,请重新拍照");
			}
		}
	};*/


	/**
	 * 接收子线程消息的 Handler
	 */
	public static class MyHandler extends Handler {

		// 软引用
		SoftReference<CameraFragment> fragmentRef;

		public MyHandler(CameraFragment fragment) {
			fragmentRef = new SoftReference<CameraFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			CameraFragment fragment = fragmentRef.get();
			if (fragment == null) {
				return;
			}
			// 处理UI 变化
			super.handleMessage(msg);
			if (msg.what == 1) {// 拍照完成,刷新页面
				fragment.notifyAdapterData();
			}
			else if (msg.what == 2) {// 图片正在保存,请稍后
				// Toast.makeText(getActivity(), "图片正在保存,请稍后", Toast.LENGTH_SHORT).show();
			}
			else if (msg.what == 3) {// 弹出滚动条, 替换2
				// 拍照  弹出进度框
				fragment.showDialogBuilder();
			}
			else if (msg.what == 4) {// 拍照处理成功 取消滚动条, 拍照完成,刷新页面
				fragment.closeDialogBuilder(false);// false:不吐司
			}
			else if (msg.what == 6) {// 拍照处理失败 取消滚动条, 拍照完成,刷新页面
				fragment.closeDialogBuilder(true); // true:吐司 解释为什么失败
			}
		}




	}



	// 拍照完成,刷新页面
	private void notifyAdapterData() {
		if (dialog != null) {
			dialog.dismiss();
		}
		gridAdapter.notifyDataSetChanged();
	}

	// 拍照 弹出进度框
	private void showDialogBuilder() {
		dialog = new Builder(getActivity()).setCancelable(false).create();
		dialog.setView(getActivity().getLayoutInflater().inflate(R.layout.camera_progress, null), 0, 0, 0, 0);
		dialog.setCancelable(false); // 是否可以通过返回键 关闭
		dialog.show();
	}

	// 拍照处理成功 取消滚动条, 拍照完成,刷新页面
	private void closeDialogBuilder(boolean type) {
		if (dialog != null) {
			dialog.dismiss();
		}
		gridAdapter.notifyDataSetChanged();

		if(type){
			Toast.makeText(getActivity(), "图片保存失败,请重新拍照", Toast.LENGTH_SHORT).show();
			DbtLog.logUtils(TAG, "onActivityResult()-图片保存失败,请重新拍照");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//View view = inflater.inflate(R.layout.shopvisit_camera, null);
		View view = inflater.inflate(R.layout.shopvisit_camera, container, false);
		DbtLog.logUtils(TAG, "onCreateView()-打开拍照模块");
		createphotoFile();// 创建拍照文件夹
		this.initView(view);
		// this.initData();
		this.asynch();
		return view;
	}

	/**
	 * 异步加载
	 */
	public void asynch() {
		DbtLog.logUtils(TAG, "asynch()");
		new AsyncTask<Void, Void, Void>() {
			protected void onPreExecute() {
				isLoadingData = true;
			}

			@Override
			protected Void doInBackground(Void... params) {
				return null;
			}

			protected void onPostExecute(Void result) {
				initData();
				isLoadingData = false;
			}

			;

		}.execute();
	}


	private void initView(View view) {
		DbtLog.logUtils(TAG, "initView()-初始化拍照模块界面");
		descGv = (ScrollView) view.findViewById(R.id.visitshop_sv_desc);
		picGv = (et.tsingtaopad.view.MyGridView) view.findViewById(R.id.gv_camera);
		descTv = (TextView) view.findViewById(R.id.visitshop_tv_desc);

	}

	private void initData() {
		DbtLog.logUtils(TAG, "initData()-初始化拍照模块界面数据");
		handler = new MyHandler(this);
		// 获取参数
		Bundle bundle = getArguments();
		termId = bundle.getString("termId");
		visitKey = bundle.getString("visitKey");
		seeFlag = bundle.getString("seeFlag");
		//termname = bundle.getString("termname");

		// 如果不是查看,图片列表展示 提示消失
		if (!ConstValues.FLAG_1.equals(seeFlag)) {
			descGv.setVisibility(View.VISIBLE);
			descTv.setVisibility(View.GONE);
		} else { // 如果是查看,图片列表消失 提示展示
			descGv.setVisibility(View.GONE);
			descTv.setVisibility(View.VISIBLE);
			descTv.setText("图片上传后本地会自动删除,查看模式下不能显示,只能通过服务器后台查看");
		}

		cameraService = new CameraService(getActivity(), null);
		//cameraService = new CameraService(MyApplication.getContext(), null);

		//ShopVisitService shopVisitService = new ShopVisitService(getActivity(), null);
		//String Routekey = new ShopVisitService(getActivity(), null).findTermById(termId).getRoutekey();
		
		termname = cameraService.findTermStcBytermId(termId).getTerminalname();

		// 初始化照片 及已拍照片
		initpic();
		// 初始化拍照文件夹
		createphotoFile();
	}

	@Override
	public void onResume() {
		DbtLog.logUtils(TAG, "onResume()-创建拍照存储文件夹");
		super.onResume();
		// 初始化拍照文件夹
		//createphotoFile();

		//initpic();
	}

	// 初始化照片 及已拍照片
	private void initpic() {
		
		// 拍照前默认图片
		bitmap = null;
		bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_camera);

		// 获取当前时间 如:20110411
		currentdata = DateUtil.getDateTimeStr(0);

		// 初始化需要拍几张图
		// 查询图片类型表,获取需要拍几张图片
		valueLst = new ArrayList<PictypeDataStc>();
		// CameraService cameraService = new CameraService(getActivity(), null);
		valueLst = cameraService.queryPictypeMAll();
		CheckIndexService checkindexservice = new CheckIndexService(getActivity(), null);
		MstTerminalinfoM term = checkindexservice.findTermById(termId);

		// 根据促销活动,添加促销活动照片类型,注意initData()中先走了一遍(?)--160927----
		List<CheckIndexPromotionStc> promotionLst = checkindexservice.queryPromotion(visitKey, term.getSellchannel(), term.getTlevel());
		/*
		 * int IsAccomplishcount1 = 0; for (int i = 0; i < promotionLst.size();
		 * i++) {
		 * 
		 * if ("1".equals(promotionLst.get(i).getIsAccomplish())) {// 有达成的活动 //
		 * 则+1; IsAccomplishcount1++; } } if (promotionLst.size() > 0 &&
		 * IsAccomplishcount1 > 0 && valueLst.size() < 3) { // 如果有促销活动
		 * valueLst.add(new
		 * PictypeDataStc("42f44fg3-42s5-458d-a32e-622e393o76d6", "1", "100",
		 * "促销活动")); }
		 */
		
		// 根据图片类型表Mst_pictype_M  判断普通图片 是不是大区配置的  还是二级区域配置的
        String isbigerae = "0";  // 默认大区配置  0:大区配置   1:二级区域配置
        if(valueLst.size()>0){// 有普通照片
        	// 查看普通照片中,是否有二级区域的记录
        	if(PrefUtils.getString(getActivity(), "disId", "").equals(valueLst.get(0).getAreaid())){
        		isbigerae = "1";// 1:二级区域配置
        	}
        }else{// 没有普通照片,查看促销活动,
        	for (CheckIndexPromotionStc pictypeDataStc : promotionLst) {
        		// 活动表记录 是二级区域的且是拍照类型
				if(PrefUtils.getString(getActivity(), "disId", "").equals(pictypeDataStc.getAreaid())&&"1".equals(pictypeDataStc.getIspictype())){
					isbigerae = "1";// 1:二级区域配置
				}
			}
        }
        
        String twoareaid = PrefUtils.getString(getActivity(), "disId", "");
        // 根据是不是大区或者二级区域 显示不同的促销活动拍照
        if("1".equals(isbigerae)){// 二级配置  0:大区  1:二级区域
        	// 1:二级区域
        	String picname = "";
    		for (int i = 0; i < promotionLst.size(); i++) {

    			if ("1".equals(promotionLst.get(i).getIspictype()) 
    					&& "1".equals(promotionLst.get(i).getIsAccomplish()) 
    					&& twoareaid.equals(promotionLst.get(i).getAreaid())) {// 有达成的活动

    				// 截取促销活动名称前7位
    				picname = promotionLst.get(i).getPromotName();
    				if(promotionLst.get(i).getPromotName().length()>7){
    					picname = promotionLst.get(i).getPromotName().substring(0, 7);
    				}
    				// 参数: 图片类型主键(促销活动主键),清晰度,排序orderno,图片类型名称(促销活动名称)
    				valueLst.add(new PictypeDataStc(promotionLst.get(i).getPromotKey(), "1", (i + 4) + "", picname));
    			}
    		}
        	
        }else{
        	// 0:大区
        	String picname = "";
    		for (int i = 0; i < promotionLst.size(); i++) {

    			if ("1".equals(promotionLst.get(i).getIspictype()) 
    					&& "1".equals(promotionLst.get(i).getIsAccomplish()) 
    					&& (!twoareaid.equals(promotionLst.get(i).getAreaid()))) {// 有达成的活动

    				// 截取促销活动名称前7位
    				picname = promotionLst.get(i).getPromotName();
    				if(promotionLst.get(i).getPromotName().length()>7){
    					picname = promotionLst.get(i).getPromotName().substring(0, 7);
    				}
    				// 参数: 图片类型主键(促销活动主键),清晰度,排序orderno,图片类型名称(促销活动名称)
    				valueLst.add(new PictypeDataStc(promotionLst.get(i).getPromotKey(), "1", (i + 4) + "", picname));
    			}
    		}
        }
        
        /*String picname = "";
		for (int i = 0; i < promotionLst.size(); i++) {

			if ("1".equals(promotionLst.get(i).getIspictype()) 
					&& "1".equals(promotionLst.get(i).getIsAccomplish()) ) {// 有达成的活动

				// 截取促销活动名称前7位
				picname = promotionLst.get(i).getPromotName();
				if(promotionLst.get(i).getPromotName().length()>7){
					picname = promotionLst.get(i).getPromotName().substring(0, 7);
				}
				// 参数: 图片类型主键(促销活动主键),清晰度,排序orderno,图片类型名称(促销活动名称)
				valueLst.add(new PictypeDataStc(promotionLst.get(i).getPromotKey(), "1", (i + 4) + "", picname));
			}
		}*/

		// 没有图片类型时,给与提示
		if (valueLst == null || valueLst.size() <= 0) {
			descGv.setVisibility(View.GONE);
			descTv.setVisibility(View.VISIBLE);
			descTv.setText("普通图片类型后台未配置或促销活动未达成,不予显示");
		} else if (ConstValues.FLAG_0.equals(seeFlag) && valueLst != null && valueLst.size() > 0) {
			descGv.setVisibility(View.VISIBLE);
			descTv.setVisibility(View.GONE);
		}
		// 根据促销活动,添加促销活动照片类型,注意initData()中先走了一遍(?)--160927----

		// 获取当天已保存的图片 // 每次切换标签都会重新查询
		piclst = queryCurrentPicRecord(termId, currentdata, "1", "0", visitKey);

		// 初始化所有条目都没拍过照
		isCameraMap = new HashMap<Integer, String>();
		
		// 初始化所有条目都设置默认图片
		cameraPicMap = new HashMap<Integer, Bitmap>();
		for (PictypeDataStc pictypedatastc : valueLst) {
			cameraPicMap.put(Integer.parseInt(pictypedatastc.getOrderno()), bitmap);
		}
		
		/*
		 * for (int i = 0; i < valueLst.size(); i++) { cameraPicMap.put(i,
		 * bitmap); }
		 */

		// 根据拜访拍照表当天记录,初始化界面显示
		if (piclst != null) {
			
			Bitmap picbm = null;
			for (CameraDataStc cameraDataStc : piclst) {
				int n = 0;
				try {
					n = Integer.parseInt(cameraDataStc.getPicindex());
				} catch (NumberFormatException ex) {

					DbtLog.logUtils(TAG, "initpic()-读取已拍角标出错");
				}
				picbm = BitmapFactory.decodeFile(cameraDataStc.getLocalpath());
				isCameraMap.put(n, "1");
				cameraPicMap.put(n, picbm);
			}
		}

		// 设置适配器
		gridAdapter = new GridAdapter(getActivity(), valueLst, cameraPicMap);
		picGv.setAdapter(gridAdapter);

		// 设置item的点击监听
		picGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// positionTag = position;

				// 获取该位置 图片的角标
				int abc = Integer.parseInt(valueLst.get(position).getOrderno());
				String name = valueLst.get(position).getPictypename();
				positionTag = abc;

				// 根据是否拍过照 进行弹窗
				if ("1".equals(isCameraMap.get(abc))) {// 该角标是否拍照
					// if ("1".equals(isCameraMap.get(position))) {
					// 该条目拍过照 设置弹窗
					// showNotifyDialog(position);
					showNotifyDialog(abc,name);// 参数没用,name有用
					// showNotifyDialog(positionTag);
				} else {
					// 去拍照
					// toCamera1(position);
					toCamera2(position);// 参数没用
					// toCamera2(positionTag);
					// toCamera3(position);
				}
			}
		});

	}

	/**
	 * 创建拍照文件夹
	 */
	private void createphotoFile() {
		DbtLog.logUtils(TAG, "createphotoFile()-创建拍照存储文件夹");

		// 创建拍照文件夹(在ShopVisitActivity界面也创建了一遍)
		String path1 = getActivity().getFilesDir().getAbsolutePath() + File.separator + "photo" + File.separator;
		String path2 = Environment.getExternalStorageDirectory() + "/dbt/et.tsingtaopad" + "/photo" + File.separator;
		// 调用自定义相机,图片保存路径 /data/data/et.tsingtaopad/files/photo/
		FileUtil.createphotoFile(new File(path1));
		// 调用系统相机,图片保存路径 /storage/emulated/0/dbt/et.tsingtaopad/photo/
		FileUtil.createphotoFile(new File(path2));

	}

	/**
	 * 打开自定义照相机,去拍照
	 * 
	 * (废弃)
	 */
	private void toCamera(int position) {

		String pictypekey = "";// 图片类型
		String pictypename = "";// 图片类型描述
		String focus = "";// 清晰度
		for (PictypeDataStc item : valueLst) {
			if (String.valueOf(position + 1).equals(item.getOrderno())) {
				pictypekey = item.getPictypekey();
				pictypename = item.getPictypename();
				focus = item.getFocus();
			}
		}
		Intent intent = new Intent(getActivity(), TakeCameraActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("pictypekey", pictypekey);
		intent.putExtra("pictypename", pictypename);
		intent.putExtra("focus", focus);
		intent.putExtra("termname", termname);
		startActivityForResult(intent, 3);
	}

	/**
	 * 打开自定义照相机,去拍照
	 */
	private void toCamera1(int position) {
		DbtLog.logUtils(TAG, "toCamera1()-跳到摄像头界面");
		/*
		 * String pictypekey = "";// 图片类型 String pictypename = "";// 图片类型描述
		 * String focus = "";// 清晰度 for (PictypeDataStc item : valueLst) {
		 * if(String.valueOf(position+1).equals(item.getOrderno())){ pictypekey
		 * = item.getPictypekey(); pictypename = item.getPictypename(); focus =
		 * item.getFocus(); } }
		 */
		ViewUtil.clearDoubleClick();
		if (!FunUtil.cameraIsCanUse()) {// true
			// cameraIsCanUse()
			Toast.makeText(getActivity(), "请先开启拍照权限", Toast.LENGTH_SHORT).show();
			return;
		}

		// Intent intent = new Intent(getActivity(),TakeCameraActivity.class);
		Intent intent = new Intent(getActivity(), TakeCameraAty.class);
		intent.putExtra("position", position);
		intent.putExtra("pictypekey", valueLst.get(position).getPictypekey());
		intent.putExtra("pictypename", valueLst.get(position).getPictypename());
		intent.putExtra("focus", valueLst.get(position).getFocus());
		intent.putExtra("termname", termname);
		startActivityForResult(intent, 3);
	}

	/**
	 * 打开系统相机,去拍照
	 */
	private void toCamera2(int position) {
		DbtLog.logUtils(TAG, "toCamera2()-跳到系统摄像头界面");

		if (hasPermission(GlobalValues.HARDWEAR_CAMERA_PERMISSION)) {
			// 拥有了此权限,那么直接执行业务逻辑
			startToCamera();
		} else {
			// 还没有对一个权限(请求码,权限数组)这两个参数都事先定义好
			requestPermission(GlobalValues.HARDWEAR_CAMERA_CODE, GlobalValues.HARDWEAR_CAMERA_PERMISSION);
		}

	}

	@Override
	public void doOpenCamera() {
		startToCamera();
	}

	/**
	 * 打开系统相机,去拍照
	 */
	private void startToCamera() {
		DbtLog.logUtils(TAG, "toCamera2()-跳到系统摄像头界面");

		/*
		 * Intent intent = null; intent = new Intent(); // 指定开启系统相机的Action
		 * intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		 * intent.addCategory(Intent.CATEGORY_DEFAULT);
		 */

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 根据文件地址创建文件

		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		path = sdcardPath + "/dbt/et.tsingtaopad" + "/photo/";
		name = DateUtil.formatDate(new Date(), null) + ".jpg";

		File file = new File(path, name);
		DbtLog.logUtils(TAG, "创建File: "+path + name);


		Uri fileuri;
		// 兼容7.0及以上的写法
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			//intent = toCameraByFileProvider(intent,tempFile);
			//intent = toCameraByContentResolver(intent,tempFile,currentPhotoName);

            /*fileuri = toCameraByContentResolverUri(tempFile,currentPhotoName);
            CameraImageBean cameraImageBean = CameraImageBean.getInstance();
            cameraImageBean.setmPath(fileuri);
            cameraImageBean.setPicname(currentPhotoName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri);
            DELEGATE.startActivityForResult(intent, RequestCodes.TAKE_PHONE);*/

			intent = toCameraByContentResolver(intent,file,name);
			startActivityForResult(intent, 100);
		} else {
			fileuri = Uri.fromFile(file);// 将File转为Uri
			//CameraImageBean.getInstance().setmPath(fileUri);
			CameraImageBean cameraImageBean = CameraImageBean.getInstance();
			cameraImageBean.setmPath(fileuri);
			cameraImageBean.setPicname(name);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri);
			startActivityForResult(intent, 100);
		}

		/*try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 把文件地址转换成Uri格式
		Uri uri = Uri.fromFile(file);
		// 设置系统相机拍摄照片完成后图片文件的存放地址
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		// intent.putExtra("return-data", true);
		startActivityForResult(intent, 100);*/

	}


	/**
	 * 打开系统相机,去拍照
	 */
	private void toCamera3(int position) {
		DbtLog.logUtils(TAG, "toCamera2()-跳到系统摄像头界面");

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		startActivityForResult(intent, 200);
	}

	/**
	 * 再次拍照显示弹框
	 * 
	 * @param position
	 */
	public void showNotifyDialog(final int position,String name) {
		DbtLog.logUtils(TAG, "showNotifyDialog()-再次拍照提示弹窗");
		// 提示删除数据
		Builder builder = new Builder(getActivity());
		builder.setTitle("图片操作");
		builder.setMessage("对图片 " + name + " 进行操作");
		builder.setPositiveButton("重拍", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 去拍照
				// toCamera1(position);
				toCamera2(position);
				// toCamera3(position);
			}
		});
		builder.setNegativeButton("查看", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 通过相册打开图片
				/*
				 * File file = new File(getPicLocalPathByposition(position)); if
				 * (file != null && file.isFile() == true) { Intent intent = new
				 * Intent();
				 * intent.setAction(android.content.Intent.ACTION_VIEW);
				 * intent.setDataAndType(Uri.fromFile(file), "image/*");
				 * startActivity(intent); }
				 */

				// 跳转到 图片展示页面
				Bitmap bm = cameraPicMap.get(position);
				Intent intent = new Intent(getActivity(), ShowPicActivity.class);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[] bitmapByte = baos.toByteArray();
				intent.putExtra("bitmap", bitmapByte);
				startActivity(intent);
			}
		});
		builder.create().show();
	}

	/**
	 * 拍照返回处理
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		DbtLog.logUtils(TAG, "onActivityResult()-拍照后照片处理");
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case 100:// 系统相机

			if (resultCode == getActivity().RESULT_OK) {

				// Uri xiangji_uri =
				// Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
				// sdcardTempFile.getAbsolutePath(), null, null));
				DbtLog.logUtils(TAG, "onActivityResult()-case 100");

				idsuccess = -1;// -1 表示本次拍照图片还未保存到本地
				Message message = new Message();
				message.what = 3;
				handler.sendMessage(message);// 提示:图片正在保存,请稍后

				new Thread() {
					@Override
					public void run() {
						super.run();
						// ----
						try {

							/*
							 * Bundle extras = data.getExtras(); Bitmap
							 * mImageBitmap = (Bitmap) extras.get("data");
							 * Bitmap bmp = FunUtil.zoomImg(mImageBitmap, 480,
							 * 640);
							 */

							// startPhotoZoom(data.getData());
							// Bitmap mImageBitmap =
							// PhotoUtil.readBitmapFromPath(getActivity(),path+name);
							
							/*String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
					        String path2 = sdcardPath + "/dbt/et.tsingtaopad" + "/zip/";
							//String name2 = "erlabaji.jpg";
							String name2 = "234.jpg";*/
							
							// 定义一个读取bm的方法
							//DbtLog.logUtils(TAG, "读取bm: "+path + name);
							//picphoto = PhotoUtil.readBitmapFromPath(getActivity(), path2 + name2);
							//picphoto = PhotoUtil.readBitmapFromPath(getActivity(), path + name);
							// 定义一个读取bm的方法
							//Bitmap bm = BitmapFactory.decodeFile(path + name);
							// 定义一个读取bm的方法
							//Bitmap bm = readFileToBm(path + name);
							// 定义一个读取bm的方法
							//Bitmap bm = toBm(path + name);
							//DbtLog.logUtils(TAG, "onActivityResult()-读取照片成功");
							
							//int width = picphoto.getWidth(); 
							/*if(picphoto==null){
								picphoto = PhotoUtil.readBitmapFromPath(getActivity(), path + name);
								DbtLog.logUtils(TAG, "onActivityResult()-再次读取照片成功");
							}*/
							
							// 自动裁剪
							//picphoto = FunUtil.zoomImg(picphoto, 480, 640);
							//picphoto = FunUtil.scaleBitmap(picphoto, 480, 640);

							// 获取照片文件路径
							CameraImageBean cameraImageBean = CameraImageBean.getInstance();
							String picname = cameraImageBean.getPicname();
							Uri uri = cameraImageBean.getmPath();


							// 裁剪后台所需大小  本地读取照片并裁剪
							// picphoto = FunUtil.convertToBitmap(path+name,480,640);

							picphoto = getBitmapFormUri(getContext(), uri);

							DbtLog.logUtils(TAG, "onActivityResult()-裁剪成功");
							//int a = 1/0;

							// path = sdcardPath + "/dbt/et.tsingtaopad/photo/";
							// 删除图片(因为图片太大,物理删除)
							if(picphoto!=null){
								deleteFile(new File(path, name));
							}
							/*
							 * File pic =new File(path , name); try { if
							 * (pic.exists()) { pic.delete(); }
							 * //pic.createNewFile(); } catch (Exception e) {
							 * e.printStackTrace(); }
							 */
							// deleteFile(new File(path+name));
							// sendBroadcast(new
							// Intent(Intent.ACTION_MEDIA_MOUNTED,
							// Uri.parse("file://"+Environment.getExternalStorageDirectory())));
							// 压缩
							// 设置清晰度
							DbtLog.logUtils(TAG, "onActivityResult()-删除图片成功(因为图片太大,物理删除)");
							int definition = 25;

							String Pictypekey = "";
							String Pictypename = "";
							for (PictypeDataStc pictypedatastc : valueLst) {
								if ((positionTag + "").equals(pictypedatastc.getOrderno())) {
									Pictypekey = pictypedatastc.getPictypekey();
									Pictypename = pictypedatastc.getPictypename();
								}
							}

							/*
							 * switch
							 * (Integer.parseInt(valueLst.get(positionTag)
							 * .getFocus())) { case 1:// 勉强清晰 // definition =
							 * 100; definition = 25; break; case 2:// 清晰
							 * definition = 20; break; case 3:// 不清晰 definition
							 * = 15; break; default: break; }
							 */

							picphoto = FunUtil.compressBitmap(picphoto, definition);// 压缩
							DbtLog.logUtils(TAG, "onActivityResult()-压缩成功");
							
							// 加水印
							String gonghao = PrefUtils.getString(getActivity(), "userGongHao", "");

							Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_picc);
							waterBitmap = FunUtil.zoomImg(waterBitmap, ViewUtil.dip2px(getActivity(), 640), ViewUtil.dip2px(getActivity(), 30));
							picphoto = ImageUtil.createWaterMaskLeftBottom(getActivity(), picphoto, waterBitmap, 0, 0);
							// (上下文,bm,文字内容,字体大小,字体颜色,距左距离,距下距离)
							picphoto = ImageUtil.drawTextToLeftBottom(getActivity(), picphoto, "日期: " + DateUtil.getDateTimeStr(3), 10, Color.WHITE, ViewUtil.dip2px(getActivity(), 5), ViewUtil.dip2px(getActivity(), 14));
							picphoto = ImageUtil.drawTextToLeftBottom(getActivity(), picphoto, "业代: " + gonghao, 10, Color.WHITE, ViewUtil.px2dip(getActivity(), 288), ViewUtil.dip2px(getActivity(), 14));
							picphoto = ImageUtil.drawTextToLeftBottom(getActivity(), picphoto, "终端: " + termname, 10, Color.WHITE, ViewUtil.dip2px(getActivity(), 5), ViewUtil.dip2px(getActivity(), 3));
							picphoto = ImageUtil.drawTextToLeftBottom(getActivity(), picphoto, "照片: " + Pictypename, 10, Color.WHITE, ViewUtil.px2dip(getActivity(), 288), ViewUtil.dip2px(getActivity(), 3));
							/*
							 * bmp =
							 * ImageUtil.drawTextToLeftBottom(getActivity(),
							 * bmp, DateUtil.getDateTimeStr(3)
							 * +" "+ConstValues.loginSession.getUserGongHao()
							 * +" "+termname
							 * +" "+valueLst.get(positionTag).getPictypename(),
							 * 10, Color.WHITE,
							 * ViewUtil.dip2px(getActivity(),5),
							 * ViewUtil.dip2px(getActivity(),3));
							 * 
							 * bmp = FunUtil.createWateBitmap1(bmp, termname,
							 * 250, 570,
							 * ConstValues.loginSession.getUserGongHao(),
							 * 250,590,
							 * valueLst.get(positionTag).getPictypename(), 250,
							 * 610, DateUtil.getDateTimeStr(1), 250, 630);
							 */
							DbtLog.logUtils(TAG, "onActivityResult()-加水印成功");
							// 保存压缩
							FunUtil.saveHeadImg(path, picphoto, name, 100, definition);

							// 添加到集合中
							cameraPicMap.put(positionTag, picphoto);
							isCameraMap.put(positionTag, "1");
							// gridAdapter.notifyDataSetChanged();
							// 保存图片记录到到本地数据库表
							MstCameraInfoM cameraListMStc = new MstCameraInfoM();
							// PicFileUpM cameraListMStc = new PicFileUpM();

							// 保存本地时 拍照图片主键不能相同
							cameraListMStc.setCamerakey(FunUtil.getUUID());
							
							
							cameraListMStc.setTerminalkey(termId);// #
							cameraListMStc.setVisitkey(visitKey);// #
							cameraListMStc.setPicname(gonghao + "_" + name);// #
							cameraListMStc.setLocalpath(path + name);// #
							cameraListMStc.setNetpath(path + name);// 该字段 无用
							cameraListMStc.setCameradata(currentdata);// #
							cameraListMStc.setIsupload("0");
							cameraListMStc.setIstakecamera("1");
							cameraListMStc.setPicindex(String.valueOf(positionTag));
							
							
							

							/*
							 * cameraListMStc.setPictypekey(valueLst.get(positionTag
							 * ).getPictypekey());
							 * cameraListMStc.setPictypename(
							 * valueLst.get(positionTag).getPictypename());
							 */
							
							
							cameraListMStc.setPictypekey(Pictypekey);
							cameraListMStc.setPictypename(Pictypename);
							
							

							// 根据角标设置key
							/*
							 * for (PictypeDataStc pictypedatastc : valueLst) {
							 * if
							 * ((positionTag+"").equals(pictypedatastc.getOrderno
							 * ())){
							 * cameraListMStc.setPictypekey(pictypedatastc.
							 * getPictypekey());
							 * cameraListMStc.setPictypename(pictypedatastc
							 * .getPictypename()); } }
							 */

							cameraListMStc.setSureup("1");// 确定上传    0确定上传       1未确定上传
															// 会在点击确定时更改为0

							// 将图片转成字符串
							// String imagefileString = FileUtil.file2String(filePath);
							// cameraListMStc.setImagefileString(imagefileString);
							
							// 将图片转成字符串
							String imagefileString;
							try {
								imagefileString = FileUtil.fileToString(path + name);
								cameraListMStc.setImagefileString(imagefileString);
							} catch (Exception e) {
								DbtLog.logUtils(TAG, "onActivityResult()-将图片转成字符串出错");
								e.printStackTrace();
							}
							DbtLog.logUtils(TAG, "onActivityResult()-将图片转成字符串成功");

							// cameraListMStc.setImagefileString(new
							// File(filePath));

							CameraService cameraService = new CameraService(getActivity(), null);
							// 插入图片表一条图片记录
							cameraService.saveCamera1(cameraListMStc);
							// saveCamera(cameraListMStc);

							// 若是重拍的,要删除该位置上一张图片及记录,因为piclst是一进来就读取数据,所以这列表中的数据是没变的,
							DbtLog.logUtils(TAG, "onActivityResult()-删除重拍");
							String filepath = getPicLocalPathByposition(positionTag);
							if (!TextUtils.isEmpty(filepath)) {
								// 若是重拍的,要删除该位置上一张图片及记录
								deleteFile(new File(filepath));
								// 若是重拍的,要删除该位置的上一条记录
								deleteLastRecord(positionTag, currentdata, termId);
							}
							// 重新读取当天已保存的图片记录
							DbtLog.logUtils(TAG, "onActivityResult()-重新读取");
							piclst = cameraService.queryCurrentPicRecord1(termId, currentdata, "1", "0", visitKey);
							PrefUtils.putInt(getActivity(), "idsavesuccess", 1);// 2:开始保存照片,
																				// 1:保存成功

							// 删除DCIM文件夹,因为Android5.0 之前的版本会在DCIM下生成照片
							String DCIMPath = Environment.getExternalStorageDirectory() + "" + "/DCIM/";
							deleteFile(new File(DCIMPath));

							// 删除照片缓存 /DCIM/.thumbnails
							// String DCIMPath1 =
							// Environment.getExternalStorageDirectory() + "" +
							// "/DCIM/";
							deleteFile(new File(DCIMPath, ".thumbnails"));
							File file = new File(DCIMPath, ".thumbnails");
							try {
								if (file.exists()) {
									file.delete();
								}
								file.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}

							// 重新扫描磁盘
							/*Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); // MediaStore.Images.Media.EXTERNAL_CONTENT_URI // //ACTION_MEDIA_SCANNER_SCAN_FILE
							String path = Environment.getExternalStorageDirectory() + "";
							Uri newuri = Uri.fromFile(new File(path));
							// Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
							intent.setData(newuri);
							getActivity().sendBroadcast(intent);*/

							// 重新读取照片数据
							//initpic();
							
							// 更新UI界面 刷新适配器
							Message message1 = new Message();
							message1.what = 4;
							handler.sendMessage(message1);// 刷新UI
							
						} catch (Exception e) {
							e.printStackTrace();
							DbtLog.logUtils(TAG, "错误信息: "+e.toString());
							
							// 更新UI界面 刷新适配器
							Message message1 = new Message();
							message1.what = 6;
							handler.sendMessage(message1);// 刷新UI
						}
					}
				}.start();
			} else {
				Toast.makeText(getActivity(), "拍照失败或初始化相机失败,请重新拍照", Toast.LENGTH_SHORT).show();
			}

			break;

		case 200:// 系统相机 失败

			if (resultCode == getActivity().RESULT_OK) {

				DbtLog.logUtils(TAG, "onActivityResult()-case 100");
				try {

					Bundle bundle = data.getExtras();
					Bitmap bmp = (Bitmap) bundle.get("data");
					bmp = FunUtil.zoomImg(bmp, 480, 640);

					// 压缩
					// 设置清晰度
					DbtLog.logUtils(TAG, "onActivityResult()-设置清晰度");
					int definition = 25;
					switch (Integer.parseInt(valueLst.get(positionTag).getFocus())) {
					case 1:// 勉强清晰
							// definition = 100;
						definition = 25;
						break;
					case 2:// 清晰
						definition = 20;
						break;
					case 3:// 不清晰
						definition = 15;
						break;
					default:
						break;
					}
					bmp = FunUtil.compressBitmap(bmp, definition);// 压缩

					// 加水印
					DbtLog.logUtils(TAG, "onActivityResult()-加水印");
					// bmp = FunUtil.createWateBitmap1(bmp, termname, 275, 570,
					// ConstValues.loginSession.getUserGongHao(), 275, 590,
					// valueLst.get(positionTag).getPictypename(), 275, 610,
					// DateUtil.getDateTimeStr(1), 275, 630);
					bmp = FunUtil.createWateBitmap1(bmp, termname, 275, 570, PrefUtils.getString(getActivity(), "userGongHao", ""), 275, 590, valueLst.get(positionTag).getPictypename(), 275, 610, DateUtil.getDateTimeStr(1), 275, 630);
					// 保存压缩
					DbtLog.logUtils(TAG, "onActivityResult()-保存压缩");
					FunUtil.saveHeadImg(path, bmp, name, 100, definition);

					// 添加到集合中
					cameraPicMap.put(positionTag, bmp);
					isCameraMap.put(positionTag, "1");
					// gridAdapter.notifyDataSetChanged();
					// 保存图片记录到到本地数据库表
					MstCameraInfoM cameraListMStc = new MstCameraInfoM();
					// PicFileUpM cameraListMStc = new PicFileUpM();

					// 保存本地时 拍照图片主键不能相同
					cameraListMStc.setCamerakey(FunUtil.getUUID());
					cameraListMStc.setTerminalkey(termId);// #
					cameraListMStc.setVisitkey(visitKey);// #
					// cameraListMStc.setPicname(ConstValues.loginSession.getUserGongHao()
					// + "_" + name);// #
					cameraListMStc.setPicname(PrefUtils.getString(getActivity(), "userGongHao", "") + "_" + name);// #
					cameraListMStc.setLocalpath(path + name);// #
					cameraListMStc.setNetpath(path + name);// 该字段 无用
					cameraListMStc.setCameradata(currentdata);// #
					cameraListMStc.setIsupload("0");
					cameraListMStc.setIstakecamera("1");
					cameraListMStc.setPicindex(String.valueOf(positionTag));
					cameraListMStc.setPictypekey(valueLst.get(positionTag).getPictypekey());
					cameraListMStc.setPictypename(valueLst.get(positionTag).getPictypename());
					cameraListMStc.setSureup("1");// 确定上传 0确定上传 1未确定上传
													// 会在点击确定时更改为0

					// 将图片转成字符串
					// String imagefileString = FileUtil.file2String(filePath);
					// cameraListMStc.setImagefileString(imagefileString);
					DbtLog.logUtils(TAG, "onActivityResult()-将图片转成字符串");
					String imagefileString;
					try {
						imagefileString = FileUtil.fileToString(path + name);
						cameraListMStc.setImagefileString(imagefileString);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// cameraListMStc.setImagefileString(new File(filePath));

					// 插入图片表一条图片记录
					cameraService.saveCamera1(cameraListMStc);
					// saveCamera(cameraListMStc);

					// 若是重拍的,要删除该位置上一张图片及记录
					DbtLog.logUtils(TAG, "onActivityResult()-删除重拍");
					String filepath = getPicLocalPathByposition(positionTag);
					if (!TextUtils.isEmpty(filepath)) {
						// 若是重拍的,要删除该位置上一张图片及记录
						deleteFile(new File(filepath));
						// 若是重拍的,要删除该位置的上一条记录
						deleteLastRecord(positionTag, currentdata, termId);
					}
					// 重新读取当天已保存的图片记录
					DbtLog.logUtils(TAG, "onActivityResult()-重新读取");
					piclst = cameraService.queryCurrentPicRecord1(termId, currentdata, "1", "0", visitKey);
					PrefUtils.putInt(getActivity(), "idsavesuccess", 1);// 2:开始保存照片,
																		// 1:保存成功

					// 更新UI界面 刷新适配器
					Message message1 = new Message();
					message1.what = 1;
					handler.sendMessage(message1);// 刷新UI

					String DCIMPath = Environment.getExternalStorageDirectory() + "" + "/DCIM/";
					deleteFile(new File(DCIMPath));
					Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); // ,
																						// MediaStore.Images.Media.EXTERNAL_CONTENT_URI
																						// //ACTION_MEDIA_SCANNER_SCAN_FILE
					String path = Environment.getExternalStorageDirectory() + "";
					Uri uri = Uri.fromFile(new File(path));
					// Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					intent.setData(uri);
					getActivity().sendBroadcast(intent);

				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "图片保存失败,请重新拍照", Toast.LENGTH_SHORT).show();
					DbtLog.logUtils(TAG, "onActivityResult()-图片保存失败,请重新拍照");
				}
			} else {
				Toast.makeText(getActivity(), "初始化相机失败,请重新拍照", Toast.LENGTH_SHORT).show();
			}

			break;
		// 自定义相机跳回执行
		case 3:// 自定义
				// 在子线程中 进行图片压缩,水印,保存本地,插入记录
			new Thread() {
				@Override
				public void run() {

					if (data != null) {
						idsuccess = -1;// -1 表示本次拍照图片还未保存到本地
						Message message = new Message();
						message.what = 2;
						handler.sendMessage(message);// 提示:图片正在保存,请稍后
						// 获取数据
						String path = data.getStringExtra("path");// sdcard/
						String name = data.getStringExtra("name");// 图片名称
																	// 20151118143047.jpg
						String pictypekey = data.getStringExtra("pictypekey");// 图片类型主键
						int definition = data.getIntExtra("focus", 25);// 清晰度
						int cameratype = data.getIntExtra("cameratype", 0);// 清晰度
						String pictypename = data.getStringExtra("pictypename");//
						filePath = path + name; // sdcard/20151118143047.jpg
						int position = data.getIntExtra("position", 1000);
						byte[] bis = data.getByteArrayExtra("bitmap");
						Bitmap bm = BitmapFactory.decodeByteArray(bis, 0, bis.length); // 获取bitmap
						if (cameratype == 1 && !android.os.Build.MODEL.contains("A3000")) {// 需处理(前置而且不是A3000)
																							// A3000拍的不需要旋转
							bm = FunUtil.convert(bm);
						}

						// 压缩
						bm = FunUtil.compressBitmap(bm, definition);// 压缩
						// 加水印
						Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_picc);
						waterBitmap = FunUtil.zoomImg(waterBitmap, ViewUtil.dip2px(getActivity(), 640), ViewUtil.dip2px(getActivity(), 30));
						bm = ImageUtil.createWaterMaskLeftBottom(getActivity(), bm, waterBitmap, 0, 0);
						bm = ImageUtil.drawTextToLeftBottom(getActivity(), bm, "日期: " + DateUtil.getDateTimeStr(3), 10, Color.WHITE, ViewUtil.dip2px(getActivity(), 5), ViewUtil.dip2px(getActivity(), 12));
						// bm = ImageUtil.drawTextToLeftBottom(getActivity(),
						// bm, "业代: " +
						// ConstValues.loginSession.getUserGongHao(), 10,
						// Color.WHITE, ViewUtil.px2dip(getActivity(), 288),
						// ViewUtil.dip2px(getActivity(), 12));
						bm = ImageUtil.drawTextToLeftBottom(getActivity(), bm, "业代: " + PrefUtils.getString(getActivity(), "userGongHao", ""), 10, Color.WHITE, ViewUtil.px2dip(getActivity(), 288), ViewUtil.dip2px(getActivity(), 12));
						bm = ImageUtil.drawTextToLeftBottom(getActivity(), bm, "终端: " + termname, 10, Color.WHITE, ViewUtil.dip2px(getActivity(), 5), ViewUtil.dip2px(getActivity(), 3));
						bm = ImageUtil.drawTextToLeftBottom(getActivity(), bm, "照片: " + valueLst.get(positionTag).getPictypename(), 10, Color.WHITE, ViewUtil.px2dip(getActivity(), 288), ViewUtil.dip2px(getActivity(), 3));
						// bm = FunUtil.createWateBitmap1(bm, termname, 275,
						// 570, ConstValues.loginSession.getUserGongHao(),
						// 275,590, pictypename, 275, 610,
						// DateUtil.getDateTimeStr(1), 275, 630);

						// 保存本地
						FunUtil.saveHeadImg(path, bm, name, 100, definition);

						// 读取刚拍的图片
						// Bitmap bmp = BitmapFactory.decodeFile(filePath);
						// 添加到集合中
						cameraPicMap.put(position, bm);
						isCameraMap.put(position, "1");
						// gridAdapter.notifyDataSetChanged();
						// 保存图片记录到到本地数据库表
						MstCameraInfoM cameraListMStc = new MstCameraInfoM();
						// PicFileUpM cameraListMStc = new PicFileUpM();

						// 保存本地时 拍照图片主键不能相同
						cameraListMStc.setCamerakey(FunUtil.getUUID());
						cameraListMStc.setTerminalkey(termId);
						cameraListMStc.setVisitkey(visitKey);
						// cameraListMStc.setPicname(ConstValues.loginSession.getUserGongHao()
						// + "_" + name);
						cameraListMStc.setPicname(PrefUtils.getString(getActivity(), "userGongHao", "") + "_" + name);
						cameraListMStc.setLocalpath(filePath);
						cameraListMStc.setNetpath(filePath);// 该字段 无用
						cameraListMStc.setCameradata(currentdata);
						cameraListMStc.setIsupload("0");
						cameraListMStc.setIstakecamera("1");
						cameraListMStc.setPicindex(String.valueOf(position));
						cameraListMStc.setPictypekey(pictypekey);
						cameraListMStc.setPictypename(pictypename);
						cameraListMStc.setSureup("1");// 确定上传 0确定上传 1未确定上传
														// 会在点击确定时更改为0

						// 将图片转成字符串
						// String imagefileString =
						// FileUtil.file2String(filePath);
						// cameraListMStc.setImagefileString(imagefileString);
						String imagefileString;
						try {
							imagefileString = FileUtil.fileToString(filePath);
							cameraListMStc.setImagefileString(imagefileString);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// cameraListMStc.setImagefileString(new
						// File(filePath));

						// 插入图片表一条图片记录
						cameraService.saveCamera1(cameraListMStc);
						// saveCamera(cameraListMStc);

						// 若是重拍的,要删除该位置上一张图片及记录
						String filepath = getPicLocalPathByposition(position);
						if (!TextUtils.isEmpty(filepath)) {
							// 若是重拍的,要删除该位置上一张图片及记录
							deleteFile(new File(filepath));
							// 若是重拍的,要删除该位置的上一条记录
							deleteLastRecord(position, currentdata, termId);
						}
						// 重新读取当天已保存的图片记录
						piclst = cameraService.queryCurrentPicRecord1(termId, currentdata, "1", "0", visitKey);
						PrefUtils.putInt(getActivity(), "idsavesuccess", 1);// 2:开始保存照片,
																			// 1:保存成功

						// 更新UI界面 刷新适配器
						Message message1 = new Message();
						message1.what = 1;
						handler.sendMessage(message1);// 刷新UI
					}
				}
			}.start();
			break;
		}
	}

	/**
	 * 删除图片表一条记录
	 * 
	 * @param position
	 *            位置
	 * @param current
	 *            拍照时间
	 * @param termkey
	 *            终端key
	 */
	private void deleteLastRecord(int position, String current, String termkey) {
		DbtLog.logUtils(TAG, "deleteLastRecord()-删除图片表一条记录");
		if (piclst != null) {
			for (CameraDataStc cameraDataStc : piclst) {
				int n = 0;
				try {
					n = Integer.parseInt(cameraDataStc.getPicindex());
				} catch (NumberFormatException ex) {

				}
				if (n == position) {
					String localpath = cameraDataStc.getLocalpath();
					String camerakey = cameraDataStc.getCamerakey();

					AndroidDatabaseConnection connection = null;
					try {
						DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
						MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
						connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
						connection.setAutoCommit(false);
						// 删除该角标的上一条记录
						dao.deleteCameraRecord(helper, localpath, camerakey);
						connection.commit(null);

					} catch (Exception e) {
						Log.e(TAG, "删除图片表记录失败", e);
						try {
							connection.rollback(null);
						} catch (SQLException e1) {
							Log.e(TAG, "删除图片表记录失败", e1);
						}
					}
				}
			}
		}
	}

	/**
	 * 删除图片文件
	 * 
	 * @param file
	 */
	public void deleteFile(File file) {
		//DbtLog.logUtils(TAG, "deleteFile()-删除拍照文件夹下的一张图片");
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			Log.e(TAG, "要删除的图片文件不存在！");
		}
	}

	/**
	 * 通过角标获取数据表中该位置图片路径
	 */
	private String getPicLocalPathByposition(int position) {
		DbtLog.logUtils(TAG, "getPicLocalPathByposition()-获取图片路径删除该图片");
		// 若已拍过, 获取当前位置下图片路径
		String localpath = "";
		// piclst是拍照前去数据库读取的集合,
		if (piclst != null) {
			for (CameraDataStc cameraDataStc : piclst) {
				int n = 0;
				try {
					n = Integer.parseInt(cameraDataStc.getPicindex());
				} catch (NumberFormatException ex) {

				}
				if (n == position) {
					localpath = cameraDataStc.getLocalpath();
					break;
				}
			}
		}
		return localpath;// 有值返回路径,无值返回空""
	}

	/**
	 * 添加图片记录到到图片表 (废弃20160912)
	 * 
	 * @param info
	 */
	private void saveCamera(MstCameraInfoM info) {
		DbtLog.logUtils(TAG, "saveCamera()-保存一条图片表记录");
		// 保存
		AndroidDatabaseConnection connection = null;
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
			Dao<MstCameraInfoM, String> mstCameraiInfoMDao = helper.getMstCameraiInfoMDao();
			connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
			// 保存图片记录
			mstCameraiInfoMDao.create(info);
			connection.commit(null);

		} catch (Exception e) {
			Log.e(TAG, "新增图片记录失败", e);
			try {
				connection.rollback(null);
			} catch (SQLException e1) {
				Log.e(TAG, "新增图片记录失败后回滚失败", e1);
			}
		}
	}

	/**
	 * 获取当天拍照记录
	 * 
	 * @param terminalkey
	 *            终端主键
	 * @param cameradata
	 *            拍照时间
	 * @param istakecamera
	 *            无用的一个字段
	 * @param isupload
	 *            是否上传成功 0未上传 1已上传
	 * @return
	 */
	public List<CameraDataStc> queryCurrentPicRecord(String terminalkey, String cameradata, String istakecamera, String isupload, String visitKey) {
		DbtLog.logUtils(TAG, "queryCurrentPicRecord()-获取当天拍照记录");
		List<CameraDataStc> lst = new ArrayList<CameraDataStc>();
		try {
			DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
			MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
			lst = dao.queryCurrentCameraLst(helper, terminalkey, cameradata, istakecamera, isupload, visitKey);

		} catch (SQLException e) {
			Log.e(TAG, "获取拜访拍照表DAO对象失败", e);
		}
		return lst;
	}

	/**
	 * 通过尝试打开相机的方式判断有无拍照权限（在6.0以下使用拥有root权限的管理软件可以管理权限） 废弃
	 * 
	 * @return
	 */
	/*
	 * public static boolean cameraIsCanUse1() { boolean isCanUse = true; Camera
	 * mCamera = null; try { mCamera = Camera.open(); Camera.Parameters
	 * mParameters = mCamera.getParameters();
	 * mCamera.setParameters(mParameters); } catch (Exception e) { isCanUse =
	 * false; }
	 * 
	 * if (mCamera != null) { try { mCamera.release(); } catch (Exception e) {
	 * e.printStackTrace(); return isCanUse; } } return isCanUse; }
	 */

	/**
	 * 剪切图片
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		File yunDir = new File(path + name);
		if (!yunDir.exists()) {
			yunDir.mkdirs();
		}
		Uri imageUri = Uri.fromFile(new File(path + name)); // String imageUri =

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("circleCrop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 480);
		intent.putExtra("outputY", 640);
		// TODO 剪切图 结果存放到文件中
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);// 图像输出
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		// TODO 回调方法data.getExtras().getParcelable("data") 返回数据为空
		// TODO 如果只取缩略图 置为true
		intent.putExtra("return-data", false);
		startActivityForResult(intent, 400);

	}
	
	// 把File读取成Bitmap
	private Bitmap readFileToBm(String path) {
		BitmapFactory.Options options;

		Bitmap bitmap = null;
		try {
		  bitmap = BitmapFactory.decodeFile(path);
		  return bitmap;
		} catch (OutOfMemoryError e) {
			
			
		  try {
		    options = new BitmapFactory.Options();
		    options.inSampleSize = 2;
		    //Bitmap bitmap = BitmapFactory.decodeFile(path, null, options);
		    bitmap = BitmapFactory.decodeFile(path,options);
		    return bitmap;
		  } catch(Exception e1) {
			  Log.e(TAG, "删除图片表记录失败", e1);
		  }
		  
		  
		}
		return bitmap;
	}
	
	// 把File读取成Bitmap
	private Bitmap toBm(String path){
		Bitmap bitmap = null;
		try {
			File dest = new File(path);
			FileInputStream fis;
			fis = new FileInputStream(dest);
			
			//bitmap = BitmapFactory.decodeStream(fis);
			
			//然后使用方法decodeByteArray（）方法解析编码，生成Bitmap对象。 
			InputStream is = getInputStream(fis);
		    byte[] data = getBytes(is);
		    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); 
		    
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	//定义一个根据图片url获取InputStream的方法  
    public static byte[] getBytes(InputStream is) throws IOException {  
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024]; // 用数据装  
        int len = -1;  
        while ((len = is.read(buffer)) != -1) {  
            outstream.write(buffer, 0, len);  
        }  
        outstream.close();  
        // 关闭流一定要记得。  
        return outstream.toByteArray();  
    }
    
    public InputStream getInputStream(FileInputStream fileInput) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024*4];  
        int n = -1;  
        InputStream inputStream = null;  
        try {  
            while ((n=fileInput.read(buffer)) != -1) {  
                baos.write(buffer, 0, n);  
                  
            }  
            byte[] byteArray = baos.toByteArray();  
            inputStream = new ByteArrayInputStream(byteArray);  
            return inputStream;  
              
              
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return null;  
        } catch (IOException e) {  
            e.printStackTrace();  
            return null;  
        } finally {  
            if (inputStream != null) {  
                try {  
                    inputStream.close();  
                } catch (IOException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
            }  
        }  
    }

}
