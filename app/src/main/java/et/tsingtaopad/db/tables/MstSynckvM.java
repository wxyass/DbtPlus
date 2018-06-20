package et.tsingtaopad.db.tables;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：MstSynckvM.java</br> 作者：吴承磊 </br>
 * 创建时间：2013-11-29</br> 功能描述: 记录每张表的工更新情况的KV表</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
@DatabaseTable(tableName = "MST_SYNCKV_M")
public class MstSynckvM implements Serializable {

	private static final long serialVersionUID = 1L;

	// 表名
	@DatabaseField(canBeNull = false, id = true)
	private String tablename;

	// 表中最新数据的updatetime，格式：yyyy-MM-dd HH:mm:ss
	@DatabaseField
	private String updatetime;
	// 表最新一次单击更新按钮并成功更新的时间,格式：yyyy-MM-dd HH:mm:ss
	@DatabaseField
	private String synctime;
	// 要更新的数据时间埃范围(天)
	@DatabaseField
	private String syncDay;
	@DatabaseField
	private String remarks;

	@DatabaseField
	private String tableDesc;
	/**
	 * 记录同步失败次数
	 */
	private int  synFailtimes=0;
	
	public MstSynckvM() {
	}

	public MstSynckvM(String tablename, String updatetime, String synctime, String syncDay, String remarks) {
		super();
		this.tablename = tablename;
		this.updatetime = updatetime;
		this.synctime = synctime;
		this.syncDay = syncDay;
		this.remarks = remarks;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getSynctime() {
		return synctime;
	}

	public void setSynctime(String synctime) {
		this.synctime = synctime;
	}

	public String getSyncDay() {
		return syncDay;
	}

	public void setSyncDay(String syncDay) {
		this.syncDay = syncDay;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

    public int getSynFailtimes() {
        return synFailtimes;
    }

    public void setSynFailtimes(int synFailtimes) {
        this.synFailtimes = synFailtimes;
    }

    public String getTableDesc() {
        return tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }
	
    
	
}
