package et.tsingtaopad.syssetting.version;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.DeleteTools;
import et.tsingtaopad.R;
import et.tsingtaopad.db.tables.MstSoftupdateM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.FunUtil;
import et.tsingtaopad.tools.HttpUtil;
import et.tsingtaopad.tools.JsonUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.tools.PropertiesUtil;
import et.tsingtaopad.tools.ViewUtil;
import et.tsingtaopad.visit.syncdata.UploadDataService;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：VersionService.java</br>
 * 作者：hongen   </br>
 * 创建时间：2014-2-14</br>      
 * 功能描述: 程序版本更新的问题</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class VersionService {
    
    public static final String TAG = "VersionService";
    
    private Activity context;
    
    private String apkUrl;
    private String apkPath;
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    private ProgressBar versionPr;
    private int progress;
    private boolean interceptFlag = false;
    private boolean newestShowFlag;
    
    @SuppressLint("HandlerLeak") 
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ConstValues.WAIT1:
                versionPr.setProgress(progress);
                break;
                
            case ConstValues.WAIT2:
            	if (downloadDialog!=null && downloadDialog.isShowing()) {
            		downloadDialog.dismiss();
				}
            	showNoticeDialog();
                installApk();
                break;
                
            default:
                break;
            }
        };
    };
    
    
    public VersionService(Activity context, boolean fragment, boolean newestShowFlag) {
        if (fragment) {
            this.context = (Activity)context;
        } else {
            this.context = context;
        }
        this.newestShowFlag = newestShowFlag;
    }
    
    /**
     * 获取当前应用程序的版本号
     * @return
     */
    public String getVersion() {
        String versionName = "";
        try {
            PackageInfo pInfo = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (NameNotFoundException e) {
            Log.e(TAG, "获取程序版本号失败", e);
        }
        return versionName;
    }
    
    /**
     * 检查版本更新
     */
    public void checkVersion() {
        
        StringBuffer json = new StringBuffer();
        //json.append("{softversion:'").append(this.getVersion()).append("'}");
        // 添加定格id,经过后台判断,是否通知pad升级 // ywm 20160418
        json.append("{softversion:'").append(this.getVersion());
        //json.append("', gridid:'").append(ConstValues.loginSession.getGridId());
        json.append("', gridid:'").append(PrefUtils.getString(context, "gridId", ""));
        //json.append("', usercode:'").append(ConstValues.loginSession.getUserCode());
        json.append("', usercode:'").append(PrefUtils.getString(context, "userCode", ""));
        json.append("'}");
        
        // TODO hongen 网络获取最新版本
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.configResponseTextCharset("ISO-8859-1");
        httpUtil.send("opt_update_version", json.toString(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                ResponseStructBean resObj = 
                            HttpUtil.parseRes(responseInfo.result);
                if (ConstValues.SUCCESS.equals(
                              resObj.getResHead().getStatus())) {
                    MstSoftupdateM info = null;
                    if (!CheckUtil.isBlankOrNull(resObj.getResBody().getContent())) {
                        info = JsonUtil.parseJson(
                                resObj.getResBody().getContent(), MstSoftupdateM.class);
                    }
                    if (info != null) {
                        apkUrl = PropertiesUtil.getProperties("platform_web") + info.getSoftpath();
                        showNoticeDialog();
                    } else {
                        if (newestShowFlag) {
                            ViewUtil.sendMsg(context, R.string.version_msg_newest);
                        }
                    }
                    
                } else {
                    ViewUtil.sendMsg(context, FunUtil
                            .isNullSetSpace(resObj.getResBody().getContent()));
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Log.e(TAG, msg, error);
            }
        });
        
    }
    
    private void showNoticeDialog(){
        Builder builder = new Builder(context);
        builder.setTitle("软件版本更新");
        builder.setMessage(R.string.version_msg_prompt);
        builder.setPositiveButton("下载", new OnClickListener() {         
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
                showDownloadDialog();           
            }
        });
//        builder.setNegativeButton("以后再说", new OnClickListener() {           
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();               
//            }
//        });
        noticeDialog = builder.create();
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.show();
        noticeDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});
    }
    
    private void showDownloadDialog(){
        Builder builder = new Builder(context);
        builder.setTitle("软件版本更新");
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.version_download_progress, null);
        versionPr = (ProgressBar)v.findViewById(R.id.version_pr_progress);
        builder.setView(v);
//        builder.setNegativeButton("取消", new OnClickListener() { 
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                interceptFlag = true;
//            }
//        });
        downloadDialog = builder.create();
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();        
        downloadDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});
        
        downApk();
    }
    
    /**
     * 下载安装程序
     */
    public void downApk() {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    URL url = new URL(apkUrl);
                
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                    conn.setConnectTimeout(3000);
                    conn.connect();
                    if(conn.getResponseCode()==200){System.out.println("hjkkllll");}
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    
                    File file = context.getFilesDir();
                    if(!file.exists()){
                        file.mkdir();
                    }
                    apkPath = file.getPath() + "/DbtPlus.apk";
                    File ApkFile = new File(apkPath);
                    FileOutputStream fos = new FileOutputStream(ApkFile);
                    
                    int count = 0;
                    byte buf[] = new byte[1024];
                    do{                 
                        int numread = is.read(buf);
                        count += numread;
                        progress =(int)(((float)count / length) * 100);
                        //更新进度
                        handler.sendEmptyMessage(ConstValues.WAIT1);
                        if(numread <= 0){   
                            //下载完成通知安装
                            handler.sendEmptyMessage(ConstValues.WAIT2);
                            break;
                        }
                        fos.write(buf,0,numread);
                    }while(!interceptFlag);//点击取消就停止下载.
                    
                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    Log.e(TAG, "", e);
                } catch(IOException e){
                    Log.e(TAG, "", e);
                }
            }
        }).start();
    }
    
    /**
     * 安装apk之前先检是否有未上传的数据，如果有就上传数据并清空本地数据库，如果没有就直接删除本地数据
     */
    private void installApk(){
    	
    	UploadDataService uploadDataService = new UploadDataService(context, null);
    			try {
					if(uploadDataService.isAllEmpty()){
						DeleteTools.deleteDatabase(context);
					}else{
						uploadDataService.uploadTables(false);
						DeleteTools.deleteDatabase(context);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
			
		
    	
        this.exec(new String[] {"chmod", "604", apkPath});
        
        File apkfile = new File(apkPath);
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                                "application/vnd.android.package-archive"); 
        context.startActivity(i);
    }
    
    /**
     * 更改文件权限
     * 
     * @param args
     * @return
     */
    private String exec(String[] args) {
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }
}
