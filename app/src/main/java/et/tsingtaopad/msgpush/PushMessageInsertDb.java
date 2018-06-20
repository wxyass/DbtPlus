package et.tsingtaopad.msgpush;

import java.sql.SQLException;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.tables.CmmBoardM;
import et.tsingtaopad.db.tables.MstPlanWeekforuserM;
import et.tsingtaopad.db.tables.MstPlanforuserM;
import et.tsingtaopad.db.tables.MstTerminalinfoM;
import et.tsingtaopad.tools.CheckUtil;
import et.tsingtaopad.tools.DbtLog;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：PushMessageInsertDb.java</br>
 * 作者：dbt   </br>
 * 创建时间：2013-12-28</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class PushMessageInsertDb
{

    private Context context;

    public PushMessageInsertDb(Context context)
    {
        this.context = context;
    }

    public void insertDb(PushMessageEntity pushMessage)
    {
        switch (pushMessage.getMessageType())
        {
            case 0://通知公告
            	DbtLog.logUtils("PushMessageInsertDb", "insertNotice");
                insertNotice(pushMessage);
                break;
            case 1://新增终端审核
            	DbtLog.logUtils("PushMessageInsertDb", "updateNewTerminal");
                updateNewTerminal(pushMessage);
                break;
            case 2://无效终端审核
            	DbtLog.logUtils("PushMessageInsertDb", "updateNotEffectTerminal");
                updateNotEffectTerminal(pushMessage);
                break;
            case 3://日工作计划审核
            	DbtLog.logUtils("PushMessageInsertDb", "updatePlanCheck");
                updatePlanCheck(pushMessage);
                break;
            case 4://问题反馈
            	DbtLog.logUtils("PushMessageInsertDb", "updateQueryFadeBack");
                updateQueryFadeBack(pushMessage);
                break;
            case 5://周工作计划审核
            	DbtLog.logUtils("PushMessageInsertDb", "updateWeekPlanCheck");
                updateWeekPlanCheck(pushMessage);
                break;
        }
    }

    private void insertNotice(PushMessageEntity pushMessage)
    {
        CmmBoardM cmmboard = new CmmBoardM();

        try
        {
            if (!CheckUtil.IsEmpty(pushMessage.getMessageMainKey()))
            {
                cmmboard.setMessageid(pushMessage.getMessageMainKey().get(0));
            }
            cmmboard.setMesstitle(pushMessage.getMessageTitle());
            cmmboard.setMessagedesc(pushMessage.getMessageContent());
            cmmboard.setStartdate(pushMessage.getStartDate().replace("-", ""));
            cmmboard.setEnddate(pushMessage.getEndDate().replace("-", ""));
            cmmboard.setUsername(pushMessage.getCreUser());
            cmmboard.setCredate(pushMessage.getCreDate());
            cmmboard.setUpdatetime(pushMessage.getCreDate());
            int flag = DatabaseHelper.getHelper(context).getCmmBoardMDao().create(cmmboard);
            if (flag > 0)
            {
                Log.i("infor", "添加成功");

            }
            else
            {
                Log.i("infor", "添加失败");
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 新增终端
     * @param pushMessage
     */
    private void updateNewTerminal(PushMessageEntity pushMessage)
    {
        String[] arguments = new String[pushMessage.getMessageMainKey().size() + 1];
        arguments[0] = String.valueOf(pushMessage.getMessageState());
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < pushMessage.getMessageMainKey().size(); i++)
        {
            arguments[i + 1] = pushMessage.getMessageMainKey().get(i);
            temp.append("?,");
        }
        String terminalcode = pushMessage.getStartDate();

        StringBuffer revertSql = new StringBuffer("update MST_TERMINALINFO_M  set STATUS = ?,");
        revertSql.append("terminalcode='");
        revertSql.append(terminalcode).append("'").append(" where terminalkey in (");
        revertSql.append(temp.substring(0, temp.length() - 1)).append(")");

        StringBuffer sql = new StringBuffer("update MST_INVALIDAPPLAY_INFO  set STATUS = ?  where terminalkey in (").append(temp.substring(0, temp.length() - 1)).append(")");
        try
        {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstTerminalinfoM, String> mstInvalidapplayInforDao = helper.getMstTerminalinfoMDao();
            mstInvalidapplayInforDao.executeRaw(sql.toString(), arguments);
            Dao<MstTerminalinfoM, String> mstTerminalinfoMDao = helper.getMstTerminalinfoMDao();
            mstTerminalinfoMDao.executeRaw(revertSql.toString(), arguments);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 无效终端审核
     * @param pushMessage
     */
    private void updateNotEffectTerminal(PushMessageEntity pushMessage)
    {

        String[] arguments = new String[pushMessage.getMessageMainKey().size() + 1];
        arguments[0] = String.valueOf(pushMessage.getMessageState());
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < pushMessage.getMessageMainKey().size(); i++)
        {
            arguments[i + 1] = pushMessage.getMessageMainKey().get(i);
            temp.append("?,");
        }
        //无效终端申请 当申请失败的时候终端表还原成有效
        StringBuffer revertSql = new StringBuffer("update MST_TERMINALINFO_M  set STATUS = ? where terminalkey in (").append(temp.substring(0, temp.length() - 1)).append(")");
        StringBuffer sql = new StringBuffer("update MST_INVALIDAPPLAY_INFO  set STATUS = ? where terminalkey in (").append(temp.substring(0, temp.length() - 1)).append(")");
        try
        {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstTerminalinfoM, String> mstInvalidapplayInforDao = helper.getMstTerminalinfoMDao();
            int result = mstInvalidapplayInforDao.executeRaw(sql.toString(), arguments);
            System.out.println("result->" + result);
            //无效终端申请 当申请失败的时候终端表还原成有效
            if (arguments[0].equals("2"))
            {
                arguments[0] = "1";
                Dao<MstTerminalinfoM, String> mstTerminalinfoMDao = helper.getMstTerminalinfoMDao();
                mstTerminalinfoMDao.executeRaw(revertSql.toString(), arguments);
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 日计划审核
     */
    private void updatePlanCheck(PushMessageEntity pushMessage)
    {

        //
        String[] arguments = new String[pushMessage.getMessageMainKey().size() + 1];
        int tempInt = pushMessage.getMessageState() == 0 ? 4 : pushMessage.getMessageState();
        arguments[0] = String.valueOf(tempInt);
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < pushMessage.getMessageMainKey().size(); i++)
        {
            arguments[i + 1] = pushMessage.getMessageMainKey().get(i);
            temp.append("?,");
        }

        StringBuffer sql = new StringBuffer("update MST_PLANFORUSER_M  set PLANSTATUS = ? where PLANKEY in (").append(temp.substring(0, temp.length() - 1)).append(")");
        try
        {
            Dao<MstPlanforuserM, String> mstInvalidapplayInforDao = DatabaseHelper.getHelper(context).getMstPlanforuserMDao();
            int result = mstInvalidapplayInforDao.executeRaw(sql.toString(), arguments);
            System.out.println("result->" + result);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    /**
    * 周计划审核
    */
    private void updateWeekPlanCheck(PushMessageEntity pushMessage)
    {

        //
        String[] arguments = new String[pushMessage.getMessageMainKey().size() + 1];
        int tempInt = pushMessage.getMessageState() == 0 ? 4 : pushMessage.getMessageState();
        arguments[0] = String.valueOf(tempInt);
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < pushMessage.getMessageMainKey().size(); i++)
        {
            arguments[i + 1] = pushMessage.getMessageMainKey().get(i);
            temp.append("?,");
        }

        StringBuffer sql = new StringBuffer("update MST_PLANWEEKFORUSER_M  set PLANSTATUS = ? where PLANKEY in (").append(temp.substring(0, temp.length() - 1)).append(")");
        try
        {
            Dao<MstPlanWeekforuserM, String> mstInvalidapplayInforDao = DatabaseHelper.getHelper(context).getMstPlanWeekforuserMDao();
            int result = mstInvalidapplayInforDao.executeRaw(sql.toString(), arguments);
            System.out.println("result->" + result);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    private void updateQueryFadeBack(PushMessageEntity pushMessage)
    {
        String[] arguments = { pushMessage.getMessageContent(), pushMessage.getStartDate(), pushMessage.getMessageMainKey().get(0) };
        StringBuffer sql = new StringBuffer("update MST_QUESTIONSANSWERS_INFO  set QAACONTENT = ?, QAADATE = ? where QAKEY = ?");
        try
        {
            Dao<MstTerminalinfoM, String> mstInvalidapplayInforDao = DatabaseHelper.getHelper(context).getMstTerminalinfoMDao();
            mstInvalidapplayInforDao.executeRaw(sql.toString(), arguments);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

}
