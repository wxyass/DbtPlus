package et.tsingtaopad.tools;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import android.content.Context;
import android.util.Log;
import cn.com.benyoyo.manage.Struct.RequestStructBean;
import cn.com.benyoyo.manage.Struct.ResponseStructBean;

import com.alibaba.fastjson.JSONReader;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.MyApplication;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：HttpUtil.java</br>
 * 作者：@吴承磊    </br>
 * 创建时间：2013/11/26</br>      
 * 功能描述: 网络工具类</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class HttpUtil extends HttpUtils {
    
    private static final String TAG = "HttpUtil";
    private static BASE64Decoder base;
    private static BASE64Encoder encoder;
    private static Context mContext;
    
    public HttpUtil() {
        super();
      
		base = new BASE64Decoder();
		encoder = new BASE64Encoder();
		mContext = MyApplication.getContext();
    }
    
    public HttpUtil(int connTimeout) {
        super(connTimeout);
//        connTimeout = connTimeout > 3 * 60000 ? connTimeout : 3 * 60000;
//        super.configTimeout(connTimeout);
      
		base = new BASE64Decoder();
		encoder = new BASE64Encoder();
		mContext = MyApplication.getContext();
    }

    /**
     * 异步发送网络请求
     *  
     * @param optCode   操作码，如登录：opt_get_login
     * @param json      请求参数的JSON形式
     * @param callBack  回调方法
     */
	public <T> HttpHandler<T> send(String optCode,
                    String json, RequestCallBack<T> callBack) {
	    
        // 组建请求Json
        RequestStructBean reqObj = new RequestStructBean();
        reqObj.getReqHead().setOptcode(PropertiesUtil.getProperties(optCode));
        reqObj.getReqBody().setContent(json);
        RequestParams params = new RequestParams();
        
        // 压缩请求数据
        String jsonZip = "";
        try {
        	DbtLog.logUtils(TAG, "压缩前数据");
        	DbtLog.logUtils(TAG, JsonUtil.toJson(reqObj));
            //jsonZip = new String(ZipHelper.zipString(JsonUtil.toJson(reqObj)), "ISO-8859-1");
            
        	//jsonZip = GZIP.compress(JsonUtil.toJson(reqObj));
            
            // 先压缩,在BASE64加密
            jsonZip = encoder.encode(GZIP.compress(JsonUtil.toJson(reqObj)).getBytes("UTF-8"));
	    	
            
            //配合朱志凯测试给他提取json语句进行压力测试
            DbtLog.logUtils(TAG, "压缩后数据");
            DbtLog.logUtils(TAG, jsonZip);
            //FileUtil.writeTxt(JsonUtil.toJson(reqObj),FileUtil.getSDPath()+"/ceshi.txt");
        } catch (Exception e1) {
            Log.e(TAG, "压缩上传数据JSON失败", e1);
        }
        
        // 建立请求对象
        //params.addBodyParameter("data", jsonZip);
        // 测试区域
        params.addBodyParameter("datatest", jsonZip);
        
        saveMstBsData(PropertiesUtil.getProperties(optCode), ""+jsonZip.getBytes().length, "0", "", "异步发送网络请求send");
        
        // 获取平台服务接口
        String url = PropertiesUtil.getProperties("platform_ip");
        
        return send(HttpRequest.HttpMethod.POST, url, params, callBack);
    }
    
    /**
     * 同步发送网络请求
     *  
     * @param optCode   操作码，如登录：opt_get_login
     * @param json      请求参数的JSON形式
     */
    public  ResponseInfo<String>  sendSynRequest(String optCode,
                    String json) {
        
        // 组建请求Json
        RequestStructBean reqObj = new RequestStructBean();
        reqObj.getReqHead().setOptcode(PropertiesUtil.getProperties(optCode));
        reqObj.getReqBody().setContent(json);
        RequestParams params = new RequestParams();
        
        // 压缩请求数据
        String jsonZip = "";
        try {
            //jsonZip = new String(ZipHelper.zipString(JsonUtil.toJson(reqObj)), "ISO-8859-1");
            //jsonZip = GZIP.compress(JsonUtil.toJson(reqObj));
        	
        	// 先压缩,在BASE64加密
            jsonZip = encoder.encode(GZIP.compress(JsonUtil.toJson(reqObj)).getBytes("UTF-8"));
            
        } catch (Exception e1) {
            Log.e(TAG, "压缩上传数据JSON失败", e1);
        }
        
        // 建立请求对象
        //params.addBodyParameter("data", jsonZip);
        // 测试区域
        params.addBodyParameter("datatest", jsonZip);
        
        saveMstBsData(PropertiesUtil.getProperties(optCode), ""+jsonZip.getBytes().length, "0", "", "同步发送网络请求sendSynRequest");
        // 获取平台服务接口
        String url = PropertiesUtil.getProperties("platform_ip");        
        ResponseStream sendSync=null;
        ResponseInfo<String> responseInfo=null;
        try {
            sendSync = sendSync(HttpRequest.HttpMethod.POST, url, params);
            
            if (200==sendSync.getStatusCode()) {
            	DbtLog.write("同步请求成功0");
            }
            
            String readString = sendSync.readString(); 
            DbtLog.write("读取数据成功");
            responseInfo=new ResponseInfo<String>(null,readString,true);
        } catch (Exception e) {
            
        }
        return responseInfo;
       
    }
	
	
    /**
     * 解压、解析网络请求返回结果
     * 
     * 返回结果 用新的解压方式解压(161127)
     * 
     * 除了同步请求 解压其他的异步请求(2018/02/23) 原名parseRes(String resContent)
     * 
     * @param resContent    网络请求返回结果
     * @return
     */
    public static ResponseStructBean parseRes(String resContent) {
//      //配合朱志凯测试给他提取json语句进行压力测试
//      FileUtil.writeTxt(JsonUtil.toJson(resContent),FileUtil.getSDPath()+"/ceshi.txt");
      ResponseStructBean resObj = new ResponseStructBean();
      //判断字符串是否为空或者null
      if (!CheckUtil.isBlankOrNull(resContent)) {
    	  
    	  saveMstBsData("", ""+resContent.getBytes().length, "1", "", "解压、解析网络请求返回结果parseRes");
          
    	  String json;
    	  try {
    		  //json = GZIP.uncompress2(base.decodeBuffer(resContent));
    		  
    		  // 先BASE64解密,再解压
    		  json =GZIP.unCompress(new String(base.decodeBuffer(resContent), "UTF-8"));
				
		        resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
		        // 促使内存释放
		        json = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          
    	  /*try {
              
        	  // 解密
        	  //String json3 = URLDecoder.decode(resContent, "GBK");
        	  // 解压
        	  byte[] buffer = new byte[0];
              buffer = resContent.getBytes("ISO-8859-1");
              json = ZipHelper.unZipByteToString(buffer);
              
              // 先base64解密 再解压成json
  			  //json =GZIP.uncompress2(base.decodeBuffer(resContent));
        
              FileUtil.writeTxt(json, FileUtil.getSDPath()+"/login4.txt");
              resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
              // 促使内存释放
              json = null;
              
          } catch (UnsupportedEncodingException e) {
              Log.e(TAG, "处理网络返回结果失败", e);
              if(resObj.getResHead().getStatus().equals("N")){
              	
              }else{
              	resObj.getResHead().setStatus(ConstValues.ERROR);
                  resObj.getResBody().setContent(
                          PropertiesUtil.getProperties("msg_err_netfail"));
              }
              
          }*/
          
      } else {
          resObj = new ResponseStructBean();
          resObj.getResHead().setStatus(ConstValues.ERROR);
          resObj.getResBody().setContent(
                  PropertiesUtil.getProperties("msg_err_netfail"));
      }
      return resObj;
  }
    
    /**
     * 解压、解析网络请求返回结果2(此方法只在同步时使用,)
     * 
     * @param resContent    网络请求返回结果
     * @return
     */
	public static ResponseStructBean parseRes2(String resContent) {
		// //配合朱志凯测试给他提取json语句进行压力测试
		// FileUtil.writeTxt(JsonUtil.toJson(resContent),FileUtil.getSDPath()+"/ceshi.txt");
		ResponseStructBean resObj = new ResponseStructBean();
		// 判断字符串是否为空或者null
		if (!CheckUtil.isBlankOrNull(resContent)) {

			saveMstBsData("", "" + resContent.getBytes().length, "1", "", "解压、解析网络请求返回结果2(此方法只在同步时使用,)parseRes2");

			String json;
			try {
				// 先base64解密 再解压成json
				//json = GZIP.uncompress2(base.decodeBuffer(resContent));
				
				//FileUtil.writeTxt(resContent, FileUtil.getSDPath()+"/MST_CHECKEXERECORD_INFO_ZIP1040.txt");  
				
				// 先BASE64解密,再解压
				String str = new String(base.decodeBuffer(resContent), "UTF-8");
				json =GZIP.unCompress(str);

				// FileUtil.writeTxt(json, FileUtil.getSDPath()+"/login4.txt");
				// 业代 2035419 1234567a 业代同步数据后,终端数量为0 改为这种解析->更改jar包fastjson-1.1.33到1.2.36
				//resObj = JsonUtil.parseJson(json, ResponseStructBean.class);

				// 使用Gson将json转成对象
				/*
				 * Gson gson = new Gson(); 
				 resObj = gson.fromJson(json,ResponseStructBean.class);
				 */

				JSONReader reader = new JSONReader(new StringReader(json));// 已流的方式处理，这里很快
				resObj = reader.readObject(ResponseStructBean.class);
				reader.close();
				
				 /*StringReader stringReader = new StringReader(json);
				 JSONReader jsonReader = new JSONReader(stringReader); 
				 resObj= jsonReader.readObject(ResponseStructBean.class);
				 jsonReader.close();*/
				 

				// 促使内存释放
				json = null;
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			resObj = new ResponseStructBean();
			resObj.getResHead().setStatus(ConstValues.ERROR);
			resObj.getResBody().setContent(PropertiesUtil.getProperties("msg_err_netfail"));
		}
		return resObj;
	}
    
    /**
     * 解压、解析网络请求返回结果--  (似乎废弃了2018-2-23)
     * 
     * @param resContent    网络请求返回结果
     * @return
     */
    public static ResponseStructBean parseRes(String tabName,String resContent) {
//        //配合朱志凯测试给他提取json语句进行压力测试
        //FileUtil.writeTxt(JsonUtil.toJson(resContent),FileUtil.getSDPath()+"/ceshi072758.txt");
        ResponseStructBean resObj = new ResponseStructBean();
        
        if (!CheckUtil.isBlankOrNull(resContent)) {
        	DbtLog.write(tabName+":resContent:is not null");
        	
        	saveMstBsData("", ""+resContent.getBytes().length, "1", "", "解压、解析网络请求返回结果--parseRes");
        	
            try {
                byte[] buffer = resContent.getBytes("ISO-8859-1");
                Log.e(TAG, "BUFFER LENGTH:"+buffer.length);
                DbtLog.write(tabName+"BUFFER LENGTH:"+buffer.length);
            	DbtLog.write(tabName+"BUFFER LENGTH:"+buffer.length/1024d/2024d);
            	
                String json = ZipHelper.unZipByteToString(tabName,buffer);
                DbtLog.write("log1.txt", json);
            	DbtLog.write(tabName+"json 结束:");
                resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
            	DbtLog.write(tabName+"resObj==null:"+(resObj==null));
                // 促使内存释放
                json = null;
                
            } catch (UnsupportedEncodingException e) {
            	DbtLog.write(tabName+":"+e.getMessage());
                Log.e(TAG, "处理网络返回结果失败", e);
                resObj.getResHead().setStatus(ConstValues.ERROR);
                resObj.getResBody().setContent(
                        PropertiesUtil.getProperties("msg_err_netfail"));
            }
            
        } else {
        	DbtLog.write(tabName+":resContent:is null");
            resObj = new ResponseStructBean();
            resObj.getResHead().setStatus(ConstValues.ERROR);
            resObj.getResBody().setContent(
                    PropertiesUtil.getProperties("msg_err_netfail"));
        }
        return resObj;
    }
    
    // 流量计算 (为了减少性能损耗 注释掉)
    public static void saveMstBsData(String title, String sizes, String flag, String content, String remarks) {
    	/*AndroidDatabaseConnection connection = null;
	    try {
	        DatabaseHelper helper = DatabaseHelper.getHelper(mContext);
	        Dao<MstBsData, String> dao = helper.getMstBsDataDao();
	        connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
			connection.setAutoCommit(false);
	        MstBsData info;
	        String currDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
	        info = new MstBsData();
	        info.setBsdatakey(FunUtil.getUUID());
	        info.setTitle(title);
	        info.setSizes(sizes);
	        info.setCredate(currDate);
	        info.setFlag(flag);
	        info.setContent(PrefUtils.getString(mContext, "userGongHao", "1234567"));
	        info.setRemark(remarks);
	        dao.create(info);
	        
			connection.commit(null);
	            
	    } catch (SQLException e) {
	        Log.e(TAG, "获取流量保存表DAO对象失败", e);
	    }*/
    }
    
}
