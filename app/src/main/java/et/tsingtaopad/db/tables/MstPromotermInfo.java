package et.tsingtaopad.db.tables;

import java.math.BigDecimal;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * MstPromotermInfo entity. @author MyEclipse Persistence Tools
 */
@DatabaseTable(tableName = "MST_PROMOTERM_INFO")
public class MstPromotermInfo implements java.io.Serializable {

	// Fields
	@DatabaseField(canBeNull = false, id = true)
	
	private String recordkey;
	@DatabaseField
	private String ptypekey;
	@DatabaseField
	private String terminalkey;
	@DatabaseField
	private String visitkey;
	@DatabaseField
	private String startdate;
	@DatabaseField
	private String isaccomplish;
	@DatabaseField
	private String sisconsistent;
	@DatabaseField
	private Date scondate;
	@DatabaseField(defaultValue = "0")
	private String padisconsistent;
	@DatabaseField
	private Date padcondate;
	@DatabaseField
	private String comid;
	@DatabaseField
	private String remarks;
	@DatabaseField
	private String orderbyno;
	@DatabaseField(defaultValue = "0")
	private String deleteflag;
	@DatabaseField
	private BigDecimal version;
	@DatabaseField
	private Date credate;
	@DatabaseField
	private String creuser;
	@DatabaseField
	private Date updatetime;
	@DatabaseField
	private String updateuser;

	// Constructors

	/** default constructor */
	public MstPromotermInfo() {
	}

	/** minimal constructor */
	public MstPromotermInfo(String recordkey) {
		this.recordkey = recordkey;
	}

	/** full constructor */
	public MstPromotermInfo(String recordkey, String ptypekey, String terminalkey, String visitkey, String startdate, String isaccomplish,
			String sisconsistent, Date scondate, String padisconsistent, Date padcondate, String comid, String remarks, String orderbyno,
			String deleteflag, BigDecimal version, Date credate, String creuser, Date updatetime, String updateuser) {
		this.recordkey = recordkey;
		this.ptypekey = ptypekey;
		this.terminalkey = terminalkey;
		this.visitkey = visitkey;
		this.startdate = startdate;
		this.isaccomplish = isaccomplish;
		this.sisconsistent = sisconsistent;
		this.scondate = scondate;
		this.padisconsistent = padisconsistent;
		this.padcondate = padcondate;
		this.comid = comid;
		this.remarks = remarks;
		this.orderbyno = orderbyno;
		this.deleteflag = deleteflag;
		this.version = version;
		this.credate = credate;
		this.creuser = creuser;
		this.updatetime = updatetime;
		this.updateuser = updateuser;
	}

	// Property accessors

	public String getRecordkey() {
		return this.recordkey;
	}

	public void setRecordkey(String recordkey) {
		this.recordkey = recordkey;
	}

	public String getPtypekey() {
		return this.ptypekey;
	}

	public void setPtypekey(String ptypekey) {
		this.ptypekey = ptypekey;
	}

	public String getTerminalkey() {
		return this.terminalkey;
	}

	public void setTerminalkey(String terminalkey) {
		this.terminalkey = terminalkey;
	}

	public String getVisitkey() {
		return this.visitkey;
	}

	public void setVisitkey(String visitkey) {
		this.visitkey = visitkey;
	}

	public String getStartdate() {
		return this.startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getIsaccomplish() {
		return this.isaccomplish;
	}

	public void setIsaccomplish(String isaccomplish) {
		this.isaccomplish = isaccomplish;
	}

	public String getSisconsistent() {
		return this.sisconsistent;
	}

	public void setSisconsistent(String sisconsistent) {
		this.sisconsistent = sisconsistent;
	}

	public Date getScondate() {
		return this.scondate;
	}

	public void setScondate(Date scondate) {
		this.scondate = scondate;
	}

	public String getPadisconsistent() {
		return this.padisconsistent;
	}

	public void setPadisconsistent(String padisconsistent) {
		this.padisconsistent = padisconsistent;
	}

	public Date getPadcondate() {
		return this.padcondate;
	}

	public void setPadcondate(Date padcondate) {
		this.padcondate = padcondate;
	}

	public String getComid() {
		return this.comid;
	}

	public void setComid(String comid) {
		this.comid = comid;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getOrderbyno() {
		return this.orderbyno;
	}

	public void setOrderbyno(String orderbyno) {
		this.orderbyno = orderbyno;
	}

	public String getDeleteflag() {
		return this.deleteflag;
	}

	public void setDeleteflag(String deleteflag) {
		this.deleteflag = deleteflag;
	}

	public BigDecimal getVersion() {
		return this.version;
	}

	public void setVersion(BigDecimal version) {
		this.version = version;
	}

	public Date getCredate() {
		return this.credate;
	}

	public void setCredate(Date credate) {
		this.credate = credate;
	}

	public String getCreuser() {
		return this.creuser;
	}

	public void setCreuser(String creuser) {
		this.creuser = creuser;
	}

	public Date getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdateuser() {
		return this.updateuser;
	}

	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}

}