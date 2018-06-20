package et.tsingtaopad.db.tables;

import java.math.BigDecimal;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import et.tsingtaopad.db.dao.impl.MstCheckexerecordInfoDaoImpl;

/**
 * MstCheckaccomplishInfo entity. @author MyEclipse Persistence Tools
 */
@DatabaseTable(tableName = "MST_CHECKACCOMPLISH_INFO" )
public class MstCheckaccomplishInfo implements java.io.Serializable {

	// Fields
	@DatabaseField(canBeNull = false, id = true)
	private String accomplishkey;
	@DatabaseField
	private String checkkey;
	@DatabaseField
	private String cstatuskey;
	@DatabaseField
	private String colitemkey;
	@DatabaseField
	private String areaid;
	@DatabaseField
	private String productkey;
	@DatabaseField
	private Double addcount;
	@DatabaseField
	private Double totalcount;
	@DatabaseField
	private String minorchannel;
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
	@DatabaseField
	private BigDecimal version;
	@DatabaseField
	private Date credate;
	@DatabaseField
	private String creuser;
	@DatabaseField
	private Date updatetime;
	@DatabaseField()
	private String updateuser;
	@DatabaseField(defaultValue = "0")
	private String deleteflag;

	// Constructors

	/** default constructor */
	public MstCheckaccomplishInfo() {
	}

	/** minimal constructor */
	public MstCheckaccomplishInfo(String accomplishkey) {
		this.accomplishkey = accomplishkey;
	}

	/** full constructor */
	public MstCheckaccomplishInfo(String accomplishkey, String checkkey, String cstatuskey, String colitemkey, String areaid, String productkey,
			Double addcount, Double totalcount, String minorchannel, String sisconsistent, Date scondate, String padisconsistent, Date padcondate,
			String comid, String remarks, String orderbyno, String deleteflag, BigDecimal version, Date credate, String creuser, Date updatetime,
			String updateuser) {
		this.accomplishkey = accomplishkey;
		this.checkkey = checkkey;
		this.cstatuskey = cstatuskey;
		this.colitemkey = colitemkey;
		this.areaid = areaid;
		this.productkey = productkey;
		this.addcount = addcount;
		this.totalcount = totalcount;
		this.minorchannel = minorchannel;
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

	public String getAccomplishkey() {
		return this.accomplishkey;
	}

	public void setAccomplishkey(String accomplishkey) {
		this.accomplishkey = accomplishkey;
	}

	public String getCheckkey() {
		return this.checkkey;
	}

	public void setCheckkey(String checkkey) {
		this.checkkey = checkkey;
	}

	public String getCstatuskey() {
		return this.cstatuskey;
	}

	public void setCstatuskey(String cstatuskey) {
		this.cstatuskey = cstatuskey;
	}

	public String getColitemkey() {
		return this.colitemkey;
	}

	public void setColitemkey(String colitemkey) {
		this.colitemkey = colitemkey;
	}

	public String getAreaid() {
		return this.areaid;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}

	public String getProductkey() {
		return this.productkey;
	}

	public void setProductkey(String productkey) {
		this.productkey = productkey;
	}

	public Double getAddcount() {
		return this.addcount;
	}

	public void setAddcount(Double addcount) {
		this.addcount = addcount;
	}

	public Double getTotalcount() {
		return this.totalcount;
	}

	public void setTotalcount(Double totalcount) {
		this.totalcount = totalcount;
	}

	public String getMinorchannel() {
		return this.minorchannel;
	}

	public void setMinorchannel(String minorchannel) {
		this.minorchannel = minorchannel;
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