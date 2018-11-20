package et.tsingtaopad.visit.shopvisit.checkindex;

import android.os.AsyncTask;
import android.os.Message;

import java.io.File;
import java.util.List;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.ui.loader.LatteLoader;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.visit.shopvisit.checkindex.domain.ProItem;

/**
 * Created by wxyass on 2018/8/23.
 */

public class AutoSumTask extends AsyncTask<Object, Void, Void> {

    // 异步执行前
    protected void onPreExecute() {
    }


    // channelId, proItemLst, calculateLst, proId, indexId
    @Override
    protected Void doInBackground(Object... objs) {

        String channelId = (String) objs[0];
        List<ProItem> proItemLst = (List<ProItem>) objs[1];
        List<ProIndex> calculateLst = (List<ProIndex>) objs[2];
        String proId = (String) objs[3];
        String indexId = (String) objs[4];
        CheckIndexService service = (CheckIndexService) objs[5];
        service.calculateIndex(channelId, proItemLst, calculateLst, proId, indexId);

        return null;
    }


    // 异步执行后
    protected void onPostExecute(Void result) {
        // initData();
        ConstValues.handler.sendEmptyMessage(ConstValues.WAIT8);
        // ConstValues.handler.sendEmptyMessageDelayed(ConstValues.WAIT8, 2000);
    }
}
