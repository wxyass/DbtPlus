package et.tsingtaopad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;

import com.ibm.mqtt.IMqttClient;

import et.tsingtaopad.db.tables.MstRouteM;
import et.tsingtaopad.db.tables.MstSynckvM;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.login.domain.LoginSession;

/**
 * 系统常量配置
 */
public class ConstValues implements Serializable{

	// 版本序列号
	private static final long serialVersionUID = 3714072522003250341L;
	
	/***
	 * 是否上线版本
	 * ture：发布线上版本，不记录日志
	 * false：测试版本，记录日志
	 */
	public static boolean isOnline=true;
	/***
	 * 是否正在上传新增终端失败的终端
	 */
	public static boolean isUploadTermAdd=false;
	
	// 常用标识
	public static final String FLAG_0 = "0";
	public static final String FLAG_1 = "1";
	public static final String FLAG_2 = "2";
	public static final String FLAG_3 = "3";
	public static final String FLAG_4 = "4";
	
    // 成功、失败消息字符串常量
    public static final String SUCCESS = "M";
    public static final String ERROR = "E";

    // 同步数据
    public static final String SYNCDATA = "syncdata";
    
    // handler wait
    public static final int WAIT0 = 0;
    public static final int WAIT1 = 1;
    public static final int WAIT2 = 2;
    public static final int WAIT3 = 3;
    public static final int WAIT4 = 4;
    public static final int WAIT5 = 5;
    public static final int WAIT6 = 6;
    
    /***
     * 指标标删除状态（解决重复指标）
     */
    public static final String delFlag="8";
    
    // 登录信息缓存Key
    public static final String LOGINSESSIONKEY = "loginSesion";
    //今日要事是否允许提醒
    public static boolean isDayThingWarn=false;
    // 当前有效的Handler
    public static Handler msgHandler;
    public static Handler handler;
    
    // TODO hongen 删除new LoginSession()  登录Session 
    public static LoginSession loginSession = new LoginSession();
    
    
    // KV表, K:表名， V：kv对象
    public static Map<String, MstSynckvM> kvMap
                        = new HashMap<String, MstSynckvM>();
    
	// 数据字典
    public static Map<String, List<KvStc>> dataDicMap
                            = new HashMap<String, List<KvStc>>();
    
    // 省市县
    public static List<KvStc> provLst = new ArrayList<KvStc>();
    
    // 所属线路
    public static List<MstRouteM> lineLst = new ArrayList<MstRouteM>();
    
    // 我品经销商供货关系
    public static List<KvStc> agencyMineLst = new ArrayList<KvStc>();
    
    // 竞品经销商供货关系
    public static List<KvStc> agencyVieLst = new ArrayList<KvStc>();
    
    // 次渠道列表
    public static List<KvStc> secSellLst = new ArrayList<KvStc>();
    
    // 指标、指标值关联关系
    public static List<KvStc> indexLst = new ArrayList<KvStc>();
    
    // 可拜访经销
    public static List<KvStc> agencyVisitLst = new ArrayList<KvStc>();
    
    //连接mq服务器创建的连接
    public static IMqttClient mqttClient = null;
    
    //连接mq服务器创建的连接
    public static HashMap<String, Integer> AuthorityMap = new HashMap<String, Integer>();
    
}
