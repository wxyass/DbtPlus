package et.tsingtaopad.db.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import et.tsingtaopad.ConstValues;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCheckexerecordInfoDao;
import et.tsingtaopad.db.tables.MstCheckexerecordInfo;
import et.tsingtaopad.visit.shopvisit.termindex.domain.TermIndexStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstCheckexerecordInfoDaoImpl.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-4</br>      
 * 功能描述: 拜访指标执行记录表Dao层实现</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class MstCheckexerecordInfoDaoImpl extends BaseDaoImpl<
        MstCheckexerecordInfo, String> implements MstCheckexerecordInfoDao {
    
    public MstCheckexerecordInfoDaoImpl(ConnectionSource 
                                connectionSource) throws SQLException {
        super(connectionSource, MstCheckexerecordInfo.class);
    }
    
    /**
     * 获取终端某次拜访下产品对应的各指标的采集项目状态
     * 
     * 用于：巡店拜访 -- 终端指标状态
     * @param helper
     * @param visitId       拜访主键
     * @param uploadFlag    结束拜访标识，1：结束拜访，0：未结束拜访
     * @param termId        终端主键
     * @param channelId     终端次渠道ID
     * @return
     */
    @Override
    public List<TermIndexStc> queryTermIndex(
            DatabaseHelper helper, String visitId, String uploadFlag, String termId, String channelId) {

        List<TermIndexStc> lst = new ArrayList<TermIndexStc>();
        
        StringBuffer buffer = new StringBuffer();
//        buffer.append("select distinct p.proname,r.checkkey,r.acresult,ci.colitemkey, ");
//        buffer.append("ci.colitemname,ci.addcount,ci.totalcount, ");
//        buffer.append("rd.addcount prevaddcount,rd.totalcount prevtotalcount ");
//        buffer.append("from pad_checkstatus_info pv  ");
//        buffer.append("left join  mst_checkexerecord_info r ");
//        buffer.append(" on r.acresult = pv.cstatuskey ");
//        buffer.append("left join mst_product_m p on r.productkey = p.productkey ");
//        buffer.append("left join pad_checkaccomplish_info ci ");
//        buffer.append(" on r.checkkey = ci.checkkey and r.acresult = ci.cstatuskey ");
//        buffer.append(" and r.productkey = ci.productkey and ci.minorchannel=? ");
//        buffer.append("left join mst_collectionexerecord_info rd ");
//        buffer.append(" on r.visitkey = rd.visitkey and r.productkey = rd.productkey ");
//        buffer.append("     and ci.colitemkey = rd.colitemkey ");
//        buffer.append("where pv.ispadshow = '1' and r.visitkey = ? and p.proname is not null ");
//        buffer.append("order by r.checkkey,r.acresult,r.productkey ");
        
        buffer.append("select distinct vp.productkey, p.proname, pv.checkkey, pv.cstatuskey, ");
        buffer.append("ci.colitemkey,ci.colitemname,ci.addcount,ci.totalcount, ");
        buffer.append("rd.addcount prevaddcount,rd.totalcount prevtotalcount ");
        buffer.append("from pad_checkstatus_info pv ");
        buffer.append("inner join pad_checkpro_info cp on pv.checkkey = cp.checkkey ");
        buffer.append("and cp.minorchannel like '%").append(channelId).append("%' ");
        buffer.append("inner join mst_vistproduct_info vp ");
        buffer.append("  on cp.productkey = vp.productkey and vp.visitkey= ? ");
        buffer.append("left join mst_product_m p on vp.productkey = p.productkey ");
        buffer.append("left join pad_checkaccomplish_info ci ");// 
        buffer.append("  on pv.checkkey = ci.checkkey and pv.CSTATUSKEY = ci.cstatuskey ");
        buffer.append("  and vp.productkey = ci.productkey ");
        buffer.append("and ci.minorchannel like '%").append(channelId).append("%' ");
        
        // 如果上次拜访结束测从临时表的查找, 否则从指标结果表里查找
        if (ConstValues.FLAG_0.equals(uploadFlag)) {
            buffer.append("left join  mst_checkexerecord_info_temp r ");
            buffer.append("  on pv.checkkey = r.checkkey and r.acresult = pv.cstatuskey ");
            buffer.append("  and r.productkey = vp.productkey ");
            buffer.append("  and r.visitkey='").append(visitId).append("' ");
        
        } else {
            buffer.append("left join  mst_checkexerecord_info r ");
            buffer.append("  on pv.checkkey = r.checkkey and r.acresult = pv.cstatuskey ");
            buffer.append(" and r.productkey = vp.productkey ");
            buffer.append(" and r.terminalkey='").append(termId).append("' ");
            buffer.append(" and r.deleteflag!='"+ConstValues.delFlag+"' ");
            buffer.append(" and r.enddate = '30001201' ");
        }
        buffer.append("left join mst_collectionexerecord_info rd ");// 
        buffer.append("  on rd.visitkey = '").append(visitId).append("' ");
        buffer.append("  and r.productkey = rd.productkey and ci.colitemkey = rd.colitemkey ");
        buffer.append("where pv.ispadshow = '1' ");
        buffer.append("order by pv.checkkey,pv.cstatuskey,cp.checkkey ");
        
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), new String[]{visitId});
        TermIndexStc item;
        while (cursor.moveToNext()) {
            item = new TermIndexStc();
            item.setProKey(cursor.getString(cursor.getColumnIndex("productkey")));
            item.setProName(cursor.getString(cursor.getColumnIndex("proname")));
            item.setIndexKey(cursor.getString(cursor.getColumnIndex("checkkey")));
            item.setIndexValueKey(cursor.getString(cursor.getColumnIndex("cstatuskey")));
            item.setItemKey(cursor.getString(cursor.getColumnIndex("colitemkey")));
            item.setItemName(cursor.getString(cursor.getColumnIndex("colitemname")));
            item.setAddCount((long)cursor.getDouble(cursor.getColumnIndex("addcount"))+"");
            item.setTotalCount((long)cursor.getDouble(cursor.getColumnIndex("totalcount"))+"");
            item.setPrevAddCount((long)cursor.getDouble(cursor.getColumnIndex("prevaddcount"))+"");
            item.setPrevTotalCount((long)cursor.getDouble(cursor.getColumnIndex("prevtotalcount"))+"");
            lst.add(item);
        }
        return lst;
    }

    
    // 从checkexerecord表中删除没有供货关系,却有铺货指标的记录
	@Override
	public List<MstCheckexerecordInfo> queryFromcheckexerecord(DatabaseHelper helper) {
		List<MstCheckexerecordInfo> lst = new ArrayList<MstCheckexerecordInfo>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" select a.* from mst_checkexerecord_info a ");
        buffer.append(" left join mst_agencysupply_info b ");
        buffer.append(" on a.terminalkey=b.lowerkey ");
        buffer.append(" and a.productkey=b.productkey ");
        buffer.append(" where b.asupplykey is null and a.checkkey = 'ad3030fb-e42e-47f8-a3ec-4229089aab5d' ");
        
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), new String[]{});
        MstCheckexerecordInfo item;
        while (cursor.moveToNext()) {
            item = new MstCheckexerecordInfo();
            item.setRecordkey(cursor.getString(cursor.getColumnIndex("recordkey")));
            
            lst.add(item);
        }
        return lst;
	}

	@Override
	public void deletecheckexerecord(DatabaseHelper helper, String recordkey) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DELETE FROM MST_CHECKEXERECORD_INFO WHERE recordkey = ?");
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(buffer.toString(), new String[] { recordkey });
	}

}
