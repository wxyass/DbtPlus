package et.tsingtaopad.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;


/**
 * 常用上传工具类
 *
 */
public class UpImageUtil {
    
	/**
     * 发送POST请求，携带json和多个文件参数得到服务器返回的结果（json格式）
     * 
     * @param url 请求地址
     * @param json 请求体中封装的json字符串
     * @param files 上传多个文件
     * @return String 服务器返回的结果
     */
    public static String sendListPost(String url, String json, List<File> files) {
        HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("json", json);
        if (files.size() == 1) {
            try {
                params.addBodyParameter("file",
                        new FileInputStream(files.get(0)), files.get(0)
                                .length(), files.get(0).getName(),
                        "application/octet-stream");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            for (int i = 0; i < files.size(); i++) {
                if (files.get(i) != null) {
                    try {
                        params.addBodyParameter("file" + i,
                                new FileInputStream(files.get(i)), files.get(i)
                                        .length(), files.get(i).getName(),
                                "application/octet-stream");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String s = null;
        try {
            // 同步方法，获取服务器端返回的流
            ResponseStream responseStream = http.sendSync(HttpMethod.POST, url,
                    params);
            s = responseStream.readString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    
    /**
     * 发送POST请求，携带json和单个文件参数得到服务器返回的结果（json格式）
     * 
     * @param url 请求地址
     * @param json 请求体中封装的json字符串
     * @param file 上传的文件
     * @return String 服务器返回的结果
     */
    public static String sendSinglePost(String url, String json, File file) {
    	HttpUtils http = new HttpUtils();
    	RequestParams params = new RequestParams();
    	params.addBodyParameter("json", json);
    	if (file!= null) {
    		try {
    			params.addBodyParameter("file",
    					new FileInputStream(file), file
    					.length(), file.getName(),
    					"application/octet-stream");
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}
    	} 
    	String s = null;
    	try {
    		// 同步方法，获取服务器端返回的流
    		ResponseStream responseStream = http.sendSync(HttpMethod.POST, url,
    				params);
    		s = responseStream.readString();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return s;
    }
}
