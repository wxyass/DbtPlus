package et.tsingtaopad.operation.workplan;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.CmmDatadicM;
import et.tsingtaopad.db.tables.MstProductM;
import et.tsingtaopad.db.tables.MstPromotionsM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.db.tables.PadPlantempcheckM;
import et.tsingtaopad.db.tables.PadPlantempcollectionInfo;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DateUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：MakePlanService.java</br> 作者：@ray </br>
 * 创建时间：2013-12-13</br> 功能描述: 制定工作计划业务处理</br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
@SuppressWarnings("unused")
public class MakePlanService
{
    private Context context;
    private Handler handler;
    private List<MstTerminalinfoM> mstTerminalList = new ArrayList<MstTerminalinfoM>();
    private List<PadPlantempcheckM> plantempList = new ArrayList<PadPlantempcheckM>();
    private List<PadPlantempcollectionInfo> colitemList = new ArrayList<PadPlantempcollectionInfo>();
    private DatabaseHelper helper;

    public MakePlanService(Context context, Handler handler)
    {
        this.context = context;
        this.handler = handler;
        helper = DatabaseHelper.getHelper(context);
    }

    /**
     * 查询终端类型数量
     * 
     * @param lineKey
     *            线路主键
     */
    public Map<String, Integer> queryTelvel(String lineKey)
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuilder sb = new StringBuilder("select tlevel,count(tlevel) from  MST_TERMINALINFO_M where routekey= '").append(lineKey).append("' ").append("group by tlevel");
        Map<String, Integer> telvelMap = new HashMap<String, Integer>();
        try
        {
            Dao<CmmDatadicM, String> cmmDatadicMDao = helper.getCmmDatadicMDao();
            if (db.isOpen())
            {
                Cursor cursor = db.rawQuery(sb.toString(), null);
                int count = 0;
                while (cursor.moveToNext())
                {
                    String tlevelID = cursor.getString(0);
                    //查询数据字典表
                    if (!CheckUtil.isBlankOrNull(tlevelID))
                    {
                        CmmDatadicM datadicM = cmmDatadicMDao.queryForId(tlevelID);
                        if (datadicM != null)
                        {
                            Integer tlevelCount = cursor.getInt(1);
                            count += tlevelCount;
                            if ("A".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("A", tlevelCount);
                            }
                            else if ("B".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("B", tlevelCount);
                            }
                            else if ("C".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("C", tlevelCount);
                            }
                            else if ("D".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("D", tlevelCount);
                            }
                        }
                    }

                }
                telvelMap.put("ABCD", count);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return telvelMap;

    }
    /**
     * 查询此渠道类型数量
     * @param lineKey
     * @return
     */
    public Map<String, Integer> queryMinorchannel(String lineKey){

        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuilder sb = new StringBuilder("select minorchannel,count(minorchannel) from  MST_TERMINALINFO_M where routekey= '").append(lineKey).append("' ").append("group by minorchannel");
        Map<String, Integer> telvelMap = new HashMap<String, Integer>();
        try
        {
            Dao<CmmDatadicM, String> cmmDatadicMDao = helper.getCmmDatadicMDao();
            if (db.isOpen())
            {
                Cursor cursor = db.rawQuery(sb.toString(), null);
                int count = 0;
                while (cursor.moveToNext())
                {
                    String tlevelID = cursor.getString(0);
                    //查询数据字典表
                    if (!CheckUtil.isBlankOrNull(tlevelID))
                    {
                        CmmDatadicM datadicM = cmmDatadicMDao.queryForId(tlevelID);
                        if (datadicM != null)
                        {
                            Integer tlevelCount = cursor.getInt(1);
                            count += tlevelCount;
                            if ("WR-西餐厅".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("WR-西餐厅", tlevelCount);
                            }
                            else if ("MOT1-演艺吧".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("MOT1-演艺吧", tlevelCount);
                            }
                            else if ("MOT6-MINI吧".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("MOT6-MINI吧", tlevelCount);
                            }
                            else if ("MOT4-静吧".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("MOT4-静吧", tlevelCount);
                            }
                            else if ("MOT2-慢摇吧".equals(datadicM.getDicname()))//5
                            {
                                telvelMap.put("MOT2-慢摇吧", tlevelCount);
                            }
                            else if ("MOT5-西餐吧与酒店吧".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("MOT5-西餐吧与酒店吧", tlevelCount);
                            }
                            else if ("MOT3-迪厅".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("MOT3-迪厅", tlevelCount);
                            }
                            else if ("D-速食餐饮".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("D-速食餐饮", tlevelCount);
                            }
                            else if ("YS-夜市排挡".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("YS-夜市排挡", tlevelCount);
                            }
                            else if ("C-小型餐饮".equals(datadicM.getDicname()))//10
                            {
                                telvelMap.put("C-小型餐饮", tlevelCount);
                            }
                            else if ("B-中型餐饮".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("B-中型餐饮", tlevelCount);
                            }
                            else if ("A-大型餐饮".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("A-大型餐饮", tlevelCount);
                            }
                            else if ("S3-其它单一专营店".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("S3-其它单一专营店", tlevelCount);
                            }
                            else if ("S2-名烟名酒专营店".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("S2-名烟名酒专营店", tlevelCount);
                            }
                            else if ("S1-食杂/杂货店".equals(datadicM.getDicname()))//15
                            {
                                telvelMap.put("S1-食杂/杂货店", tlevelCount);
                            }
                            else if ("KTV1-连锁量贩式KTV".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("KTV1-连锁量贩式KTV", tlevelCount);
                            }
                            else if ("KTV2-独立量贩式KTV".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("KTV2-独立量贩式KTV", tlevelCount);
                            }
                            else if ("NC1-夜总会及会所".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("NC1-夜总会及会所", tlevelCount);
                            }
                            else if ("NC2-商务KTV".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("NC2-商务KTV", tlevelCount);
                            }
                            else if ("NC3-歌厅".equals(datadicM.getDicname()))//20
                            {
                                telvelMap.put("NC3-歌厅", tlevelCount);
                            }
                            else if ("SP20-团购".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("SP20-团购", tlevelCount);
                            }
                            else if ("SP10-特渠".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("SP10-特渠", tlevelCount);
                            }
                            else if ("CVS1-连锁便利店".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("CVS1-连锁便利店", tlevelCount);
                            }
                            else if ("SPMS-独立超市/便利店".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("SPMS-独立超市/便利店", tlevelCount);
                            }
                            else if ("HPM-大卖场".equals(datadicM.getDicname()))//25
                            {
                                telvelMap.put("HPM-大卖场", tlevelCount);
                            }
                            else if ("WHC-仓储式卖场".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("WHC-仓储式卖场", tlevelCount);
                            }
                            else if ("LSPM-大型连锁超市".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("LSPM-大型连锁超市", tlevelCount);
                            }
                            else if ("MSPM-中型连锁超市".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("MSPM-中型连锁超市", tlevelCount);
                            }
                            else if ("SSPM-小型连锁超市".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("SSPM-小型连锁超市", tlevelCount);
                            }
                            else if ("C1-核心C类终端".equals(datadicM.getDicname()))//30
                            {
                                telvelMap.put("C1-核心C类终端", tlevelCount);
                            }
                            else if ("C2-优质C类终端".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("C2-优质C类终端", tlevelCount);
                            }
                            else if ("C3-一般C类终端".equals(datadicM.getDicname()))
                            {
                                telvelMap.put("C3-一般C类终端", tlevelCount);
                            }
                            else if ("C4-无纸箱C类终端".equals(datadicM.getDicname()))//33
                            {
                                telvelMap.put("C4-无纸箱C类终端", tlevelCount);
                            }
                            
                        }
                    }

                }
                telvelMap.put("ABCD", count);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return telvelMap;

    
    	
    }
    /**
     * 查询此渠道类型数量
     * @param lineKey
     * @return
     */
    public Map<String, Integer> queryMinorchannel1(String lineKey){
    	
    	SQLiteDatabase db = helper.getReadableDatabase();
    	StringBuilder sb = new StringBuilder("select minorchannel,count(minorchannel) from  MST_TERMINALINFO_M where routekey= '").append(lineKey).append("' ").append("group by minorchannel");
    	Map<String, Integer> telvelMap = new HashMap<String, Integer>();
    	try
    	{
    		Dao<CmmDatadicM, String> cmmDatadicMDao = helper.getCmmDatadicMDao();
    		if (db.isOpen())
    		{
    			Cursor cursor = db.rawQuery(sb.toString(), null);
    			int count = 0;
    			while (cursor.moveToNext())
    			{
    				String tlevelID = cursor.getString(0);
    				//查询数据字典表
    				if (!CheckUtil.isBlankOrNull(tlevelID))
    				{
    					CmmDatadicM datadicM = cmmDatadicMDao.queryForId(tlevelID);
    					if (datadicM != null)
    					{
    						Integer tlevelCount = cursor.getInt(1);
    						count += tlevelCount;
    						/*
    						if ("KTV-连锁量贩式KTV/独立量贩式KTV".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("KTV-连锁量贩式KTV/独立量贩式KTV", tlevelCount);
    						}
    						else if ("MOT-演艺吧/慢摇吧/迪厅/静吧/西式餐吧".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("MOT-演艺吧/慢摇吧/迪厅/静吧/西式餐吧", tlevelCount);
    						}
    						else if ("NC-夜总会及会所/商务KTV/歌厅".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("NC-夜总会及会所/商务KTV/歌厅", tlevelCount);
    						}
    						else if ("NKA-仓储式卖场/大卖场".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("NKA-仓储式卖场/大卖场", tlevelCount);
    						}
    						else if ("RKA-大型超市".equals(datadicM.getDicname()))//5
    						{
    							telvelMap.put("RKA-大型超市", tlevelCount);
    						}
    						else if ("LKA-中小型超市".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("LKA-中小型超市", tlevelCount);
    						}
    						else if ("CVS-便利店".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("CVS-便利店", tlevelCount);
    						}
    						else if ("YS-夜排档".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("YS-夜排档", tlevelCount);
    						}
    						else if ("D-速食餐饮".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("D-速食餐饮", tlevelCount);
    						}
    						else if ("C-小型餐饮".equals(datadicM.getDicname()))//10
    						{
    							telvelMap.put("C-小型餐饮", tlevelCount);
    						}
    						else if ("C1-核心C类终端".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("C1-核心C类终端", tlevelCount);
    						}
    						else if ("C2-优质C类终端".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("C2-优质C类终端", tlevelCount);
    						}
    						else if ("C3-一般C类终端".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("C3-一般C类终端", tlevelCount);
    						}
    						else if ("C4-无纸箱C类终端".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("C4-无纸箱C类终端", tlevelCount);
    						}
    						else if ("B-中型餐饮".equals(datadicM.getDicname()))//15
    						{
    							telvelMap.put("B-中型餐饮", tlevelCount);
    						}
    						else if ("A-大型餐饮".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("A-大型餐饮", tlevelCount);
    						}
    						else if ("SP1 -宾馆/旅社 /网吧/棋牌室".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("SP1 -宾馆/旅社 /网吧/棋牌室", tlevelCount);
    						}
    						else if ("SP2-飞机/火车/轮船".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("SP2-飞机/火车/轮船", tlevelCount);
    						}
    						else if ("S2-综合食杂".equals(datadicM.getDicname()))
    						{
    							telvelMap.put("S2-综合食杂", tlevelCount);
    						}
    						else if ("S1-名烟名酒店".equals(datadicM.getDicname()))//20
    						{
    							telvelMap.put("S1-名烟名酒店", tlevelCount);
    						}*/
    						
    						
    						if ("39DD41A3992D8C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("A-大型餐饮", tlevelCount);
    						}
    						else if ("39DD41A3992C8C68E05010ACE0016FCD".equals(datadicM.getDiccode()))//15
    						{
    							telvelMap.put("B-中型餐饮", tlevelCount);
    						}
    						else if ("39DD41A3992B8C68E05010ACE0016FCD".equals(datadicM.getDiccode()))//10
    						{
    							telvelMap.put("C-小型餐饮", tlevelCount);
    						}
    						else if ("39DD41A3992A8C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("C1-核心C类终端", tlevelCount);
    						}
    						else if ("39DD41A399298C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("C2-优质C类终端", tlevelCount);
    						}
    						else if ("39DD41A399288C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("C3-一般C类终端", tlevelCount);
    						}
    						else if ("39DD41A399278C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("C4-无纸箱C类终端", tlevelCount);
    						}
    						else if ("39DD41A399248C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("CVS-便利店", tlevelCount);
    						}
    						else if ("39DD41A399268C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("D-速食餐饮", tlevelCount);
    						}
    						else if ("39DD41A399308C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("KTV-连锁量贩式KTV/独立量贩式KTV", tlevelCount);
    						}
    						else if ("39DD41A399238C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("LKA-大型超市/中小型超市", tlevelCount);
    						}
    						else if ("39DD41A3992F8C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("MOT-演艺吧/慢摇吧/迪厅/静吧/西式餐吧", tlevelCount);
    						}
    						else if ("39DD41A3992E8C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("NC-夜总会及会所/商务KTV/歌厅", tlevelCount);
    						}
    						else if ("39DD41A399228C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("NKA-仓储式卖场/大卖场", tlevelCount);
    						}
    						else if ("39DD41A3991E8C68E05010ACE0016FCD".equals(datadicM.getDiccode()))//20
    						{
    							telvelMap.put("S1-类便利店", tlevelCount);
    						}
    						else if ("39DD41A3991D8C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("S2-综合食杂", tlevelCount);
    						}
    						else if ("39DD41A399208C68E05010ACE0016FCD".equals(datadicM.getDiccode()))//5
    						{
    							telvelMap.put("SC1-外向型", tlevelCount);
    						}
    						else if ("4CCEA64B239E6F05E05010ACE0010B86".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("SC2-内向型", tlevelCount);
    						}
    						else if ("4CCEA64B239F6F05E05010ACE0010B86".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("SC3-专向型", tlevelCount);
    						}
    						else if ("39DD41A399258C68E05010ACE0016FCD".equals(datadicM.getDiccode()))
    						{
    							telvelMap.put("YS-夜排档", tlevelCount);
    						}
    						
    					}
    				}
    				
    			}
    			telvelMap.put("ABCD", count);
    		}
    	}
    	catch (SQLException e)
    	{
    		e.printStackTrace();
    	}
    	return telvelMap;
    	
    	
    	
    }

    /**
     * 查询计划模版指标名称
     */
    public List<PadPlantempcheckM> queryCheckname(Context context)
    {
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from PAD_PLANTEMPCHECK_M ";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext())
        {
            PadPlantempcheckM plantemp = new PadPlantempcheckM();
            plantemp.setCheckkey(cursor.getString(cursor.getColumnIndex("checkkey")));
            plantemp.setCheckname(cursor.getString(cursor.getColumnIndex("checkname")));
            plantemp.setPlantempkey(cursor.getString(cursor.getColumnIndex("plantempkey")));
            plantempList.add(plantemp);
        }

        return plantempList;

    }

    /**
     * 查询计划模版采集项名称
     * 
     * @param checkkey
     *            指标主键
     */

    public List<PadPlantempcollectionInfo> queryColitemname(String checkkey)
    {
        try
        {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<PadPlantempcollectionInfo, String> collectionDao = helper.getPadPlantempcollectionInfoDao();
            colitemList = collectionDao.queryForEq("checkkey", checkkey);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return colitemList;

    }

    /**
    * 查询计划模版采集项名称
    * 
    * @param checkkey
    *            指标主键
    */

    public List<PadPlantempcollectionInfo> queryPromotionsColitemname(String checkkey, String planDate)
    {
        List<PadPlantempcollectionInfo> colitemList = new ArrayList<PadPlantempcollectionInfo>();
        try
        {
            SQLiteDatabase db = helper.getReadableDatabase();
            StringBuffer buffer = new StringBuffer();
            buffer.append("select colitemkey,colitemname from PAD_PLANTEMPCOLLECTION_INFO cim ");
            buffer.append("inner join MST_PROMOTIONS_M pm ");
            buffer.append("where pm.promotkey=cim.colitemkey ");
            buffer.append("and cim.checkkey=? ");
            buffer.append("and pm.enddate>=? ");
            buffer.append("and pm.startdate<=? ");
            Cursor cursor = db.rawQuery(buffer.toString(), new String[] { checkkey, planDate.replace("-", ""), planDate.replace("-", "") });
            while (cursor.moveToNext())
            {
                PadPlantempcollectionInfo info = new PadPlantempcollectionInfo();
                info.setColitemkey(cursor.getString(cursor.getColumnIndex("colitemkey")));
                info.setColitemname(cursor.getString(cursor.getColumnIndex("colitemname")));
                colitemList.add(info);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return colitemList;

    }
    public List<PadPlantempcollectionInfo> queryotherColitemname(String checkkey)
    {
    	List<PadPlantempcollectionInfo> colitemList = new ArrayList<PadPlantempcollectionInfo>();
    	try
    	{
    		SQLiteDatabase db = helper.getReadableDatabase();
    		StringBuffer buffer = new StringBuffer();
    		buffer.append("select colitemkey,colitemname from PAD_PLANTEMPCOLLECTION_INFO cim ");
    		buffer.append("where ");
    		buffer.append("cim.checkkey=? ");
    		Cursor cursor = db.rawQuery(buffer.toString(), new String[] { checkkey });
    		while (cursor.moveToNext())
    		{
    			PadPlantempcollectionInfo info = new PadPlantempcollectionInfo();
    			info.setColitemkey(cursor.getString(cursor.getColumnIndex("colitemkey")));
    			info.setColitemname(cursor.getString(cursor.getColumnIndex("colitemname")));
    			colitemList.add(info);
    		}
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return colitemList;
    	
    }

    /**
     * 
     * 查询空白终端添加产品list
     * 
     * @param gridkey
     *            定格主键
     * @param areaid
     *            营销区域主键
     * @return
     */
    public List<MstProductM> queryProduct(Context context, String gridkey, String areaid)
    {
        List<MstProductM> productList = new ArrayList<MstProductM>();
        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuffer buffer = new StringBuffer();
        buffer.append("select distinct ap.productkey, ap.proname ");
        buffer.append("from mst_agencygrid_info am, v_agencyselproduct_info ap ");
        buffer.append("where am.agencykey = ap.agencykey and am.gridkey = ? ");
        buffer.append("and ((ap.enddate is null and ap.startdate <= ?) or ( ? between ap.startdate and ap.enddate)) ");
        buffer.append("order by am.agencykey, ap.proname ");
        String currDay = DateUtil.formatDate(new Date(), "yyyyMMdd");
        Cursor cursor = db.rawQuery(buffer.toString(), new String[] { gridkey, currDay, currDay });
        while (cursor.moveToNext())
        {
            MstProductM mstProductM = new MstProductM();
            mstProductM.setProname(cursor.getString(cursor.getColumnIndex("proname")));
            mstProductM.setProductkey(cursor.getString(cursor.getColumnIndex("productkey")));
            productList.add(mstProductM);
        }
        return productList;
    }

}
