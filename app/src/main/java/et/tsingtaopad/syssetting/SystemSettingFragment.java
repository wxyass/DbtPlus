package et.tsingtaopad.syssetting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import et.tsingtaopad.BaseFragmentSupport;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.DeleteTools;
import et.tsingtaopad.MyApplication;
import et.tsingtaopad.R;
import et.tsingtaopad.syssetting.notice.NoticeFragmentTest;
import et.tsingtaopad.syssetting.queryfeedback.QueryFeedbackFragment;
import et.tsingtaopad.syssetting.version.SystemSettingUpdateFragment;
import et.tsingtaopad.tools.FunUtil;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：SystemSettingFragment.java</br>
 * 作者：dbt   </br>
 * 创建时间：2014-2-10</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class SystemSettingFragment extends BaseFragmentSupport implements OnClickListener
{

    private Button noticeBt;
    private Button questionBt;
    private Button checkVersionBt;
    private Button changePwdBt;
    private Button sysInfoBt;
    private Button exportDbBt;
    private Button delVisitBt;
	private Button deletebutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.platform_syssetting, null);
        view.setOnClickListener(null);
        this.initView(view);
        this.initDate();
        return view;
    }

    private void initView(View view)
    {

        noticeBt = (Button) view.findViewById(R.id.syssetting_bt_notice);
        questionBt = (Button) view.findViewById(R.id.syssetting_bt_question);
        checkVersionBt = (Button) view.findViewById(R.id.syssetting_bt_update);
        changePwdBt = (Button) view.findViewById(R.id.syssetting_bt_psw);
        sysInfoBt = (Button) view.findViewById(R.id.syssetting_bt_info);
        exportDbBt = (Button) view.findViewById(R.id.exportDB);
        delVisitBt = (Button) view.findViewById(R.id.delVisit);
        deletebutton = (Button) view.findViewById(R.id.deletedatabase);
        if (!ConstValues.isOnline)
        {
            exportDbBt.setVisibility(View.VISIBLE);
            //	        delVisitBt.setVisibility(View.VISIBLE);
        }

        noticeBt.setOnClickListener(this);
        questionBt.setOnClickListener(this);
        checkVersionBt.setOnClickListener(this);
        changePwdBt.setOnClickListener(this);
        sysInfoBt.setOnClickListener(this);
        exportDbBt.setOnClickListener(this);
        delVisitBt.setOnClickListener(this);
        deletebutton.setOnClickListener(this);
    }

    private void initDate()
    {

        // 获取tabIndex、funId
        Bundle bundle = getArguments();
        int tabIndex = -1;
        String funId = "";
        if (bundle != null)
        {
            tabIndex = bundle.getInt("tabIndex");
            funId = FunUtil.isBlankOrNullTo(bundle.getString("funId"), "");

        }

        if (tabIndex == 3)
        {
            bundle.remove("funId");
            if ("question".equals(funId))
            {
                questionBt.performClick();
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        Fragment fragment = null;
        switch (v.getId())
        {

        // 通知公告
            case R.id.syssetting_bt_notice:
                fragment = new NoticeFragmentTest();
                break;

            // 问题反馈
            case R.id.syssetting_bt_question:
                fragment = new QueryFeedbackFragment();
                break;

            // 检查更新
            case R.id.syssetting_bt_update:
                fragment = new SystemSettingUpdateFragment();
                break;

            // 改密码
            case R.id.syssetting_bt_psw:
                fragment = new SystemSettingModifyPwdFragment();
                break;

            // 系统关于
            case R.id.syssetting_bt_info:
                fragment = new SystemSettingInfor();
                break;

            // 导出数据库到sdcard
            case R.id.exportDB:
                fragment = null;
                copyDBtoSD();
                break;
                
            case R.id.deletedatabase:
            	DeleteTools.deleteDatabase(getActivity());
            	break;
            //删除拜访数据
            case R.id.delVisit:
                fragment = null;
                SyssettingService service = new SyssettingService(getActivity(), null);
                if (service.delVisit())
                    Toast.makeText(getActivity(), "处理成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "处理失败", Toast.LENGTH_SHORT).show();

                break;
        }

        if (fragment != null)
        {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.syssetting_container, fragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /***
     * 拷贝数据库到SD卡
     */
    private void copyDBtoSD()
    {
        new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... params)
            {
                String sdcardPath = Environment.getExternalStorageDirectory() + "/";
                String dbPath = sdcardPath + "dbt/et.tsingtaopad/db/";
                File DBPath = new File(dbPath);
                if (!DBPath.exists())
                {
                    DBPath.mkdirs();
                }
                boolean copyGooagooDb = copyDBtoSD(dbPath, "FsaDBT.db");
                return copyGooagooDb;

            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                if (result)
                    Toast.makeText(getActivity(), "复制文件成功!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "复制文件失败!", Toast.LENGTH_SHORT).show();
            }

        }.execute();
    }

    /**
     * 复制数据库文件到sd卡目录中
     * @return  true 复制成功，false 复制失败
     */
    public static boolean copyDBtoSD(String dbPath, String dbName)
    {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try
        {
            File srcFile = MyApplication.getInstance().getDatabasePath(dbName);
            File desFile = new File(dbPath + dbName);
            if (!desFile.exists())
            {
                desFile.createNewFile();
            }
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(desFile);
            byte[] buf = new byte[1024 * 4];
            int len = 0;
            while ((len = fis.read(buf)) != -1)
            {
                fos.write(buf, 0, len);
                fos.flush();
            }
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try
            {
                if (fis != null)
                    fis.close();
                if (fos != null)
                    fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
