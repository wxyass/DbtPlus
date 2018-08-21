package et.tsingtaopad.tools;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import et.tsingtaopad.MyApplication;


/**
 * Created by yangwenmin on 2017/10/16.
 */

public class DimenUtil {

    // 获取屏幕的宽度
    public  static int getScreenWidth(){
        final Resources resources = MyApplication.getContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    // 获取屏幕的高度
    public  static int getScreenHeight(){
        final Resources resources = MyApplication.getContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
