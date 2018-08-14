///////////////////////////////////////////////////////////////////////////////
//Copyright ownership belongs to SINOSOFT CO.,LTD.shall not be reproduced ,  //
//copied,or used in other ways without permission.Otherwise SINOSOFT CO.,LTD.//
//will have the right to pursue legal responsibilities.                      //
//                                                                           //
//Copyright &copy; 2011 SINOSOFT CO., LTD. All rights reserved.              //
///////////////////////////////////////////////////////////////////////////////
package et.tsingtaopad.operation.zhuguan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import et.tsingtaopad.BaseActivity;
import et.tsingtaopad.ConstValues;
import et.tsingtaopad.R;
import et.tsingtaopad.tools.DateUtil;
import et.tsingtaopad.tools.DialogUtil;
import et.tsingtaopad.tools.PrefUtils;
import et.tsingtaopad.visit.tomorrowworkrecord.TomorrowWorkRecordService;

/**
 * 项目名称：营销移动智能工作平台<br>
 * 文件名：TomorrowWorkRecordFragment.java<br>
 * 作者：@沈潇   <br>
 * 创建时间：2013/11/24  <br>
 * 功能描述: 每日工作<br>
 * 版本 V 1.0<br>
 * 修改履历<br>
 * 日期      原因  BUG号    修改人 修改版本<br>
 */
@SuppressLint("NewApi")
public class ZgFragment extends BaseActivity {

    private final String TAG = "ZgFragment";

    private WebView web_zhuguan;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.operation_zhuguan);
        this.initView();
        this.initData();
    }

    /**
     * 初始化界面组件
     */
    private void initView() {
        // 绑定界面组件
        /*btn_back = (Button) findViewById(R.id.banner_navigation_bt_back);
        tv_title = (TextView) findViewById(R.id.banner_navigation_tv_title);

        RelativeLayout backRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_back);
        RelativeLayout confirmRl = (RelativeLayout) findViewById(R.id.banner_navigation_rl_confirm);
        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
        btn_back.setOnClickListener(this);*/

        web_zhuguan = (WebView) findViewById(R.id.weekwork_web_zhuguan);

    }

    /**
     * 运用管理模块工作计划-->本周计划完成目标汇总预览
     */
    private void showWebView() {

        web_zhuguan.getSettings().setJavaScriptEnabled(true);
        web_zhuguan.getSettings().setDomStorageEnabled(true);


        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append("http://172.16.0.229:8001/da/zg/index.html");
        /*urlBuffer.append("?areaId=").append(PrefUtils.getString(getActivity(), "disId", ""));
        urlBuffer.append("&userId=").append(PrefUtils.getString(getActivity(), "userCode", ""));*/
        urlBuffer.append("?areaId=").append("1-4HKF");
        urlBuffer.append("&userId=").append("4819b8b2-182c-463b-b2a6-a408e91b32b4");
        web_zhuguan.loadUrl(urlBuffer.toString());
    }

    /**
     * 初始化数据
     */
    private void initData() {
        showWebView();
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        //清空所有Cookie
        CookieSyncManager.createInstance(ZgFragment.this);  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

        web_zhuguan.setWebChromeClient(null);
        web_zhuguan.setWebViewClient(null);
        web_zhuguan.getSettings().setJavaScriptEnabled(false);
        web_zhuguan.clearCache(true);
    }*/

}
