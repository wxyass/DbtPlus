package et.tsingtaopad.guide;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：GuideService.java</br>
 * 作者：wangfei   </br>
 * 创建时间：2014-9-25</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class GuideService {
    private Context context;

    /**
     * @param context
     */
    public GuideService(Context context) {
        super();
        this.context = context;
    }

    /***
     * 是否展示导引界
     * @param context
     * @return
     */
    public boolean isShowGuide(Context context) {
        boolean isShow = true;
        SharedPreferences sp = context.getSharedPreferences("guide", 0);
        if (sp != null) {
            isShow = sp.getBoolean("isShow", true);
        }
        isShow=false;
        return isShow;
    }

    /***
     * 设置下次是否展示导引界面
     * @param isShow true:展示  false：不展示
     */
    public void setShowGuide(boolean isShow) {
        SharedPreferences sp = context.getSharedPreferences("guide", 0);
        if (sp != null) {
            sp.edit().putBoolean("isShow", false).commit();
        }
    }
}
