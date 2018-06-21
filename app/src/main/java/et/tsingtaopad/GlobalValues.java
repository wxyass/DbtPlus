package et.tsingtaopad;

import android.Manifest;


/**
 * Created by yangwenmin on 2017/12/25.
 */

public class GlobalValues {

    // 权限相关 ↓--------------------------------------------------------------------------
    /**
     * 权限常量相关
     */
    public static final int WRITE_READ_EXTERNAL_CODE = 0x01;// 读写内存卡权限请求码
    public static final int HARDWEAR_CAMERA_CODE = 0x02;// 相机权限请求码
    public static final int LOCAL_CODE = 0x03;// 定位请求码
    public static final int WRITE_LOCAL_CODE = 0x04;// 拍照+ 读写

    // 读写内存卡权限
    public static final String[] WRITE_READ_EXTERNAL_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

    // 相机权限
    public static final String[] HARDWEAR_CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    // 定位权限
    public static final String[] LOCAL_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    public static final String[] WRITE_EXTERNAL_CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

    // 权限相关 ↑--------------------------------------------------------------------------
}
