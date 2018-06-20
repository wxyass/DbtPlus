package et.tsingtaopad.operation.weekworkplan;

import java.util.Date;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：WorkPlanStc.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2014年1月11日</br>      
 * 功能描述:工作计划页面的结构体 </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class WorkPlanStc {

	private String visitTime;//预计拜访时间
	private String operation;//操作
	private int state = 2;//状态
	private String planKey;//绑定的MstPlanforuserM 的plankey
	private Date date;

	public WorkPlanStc() {
	}

	/**
	 * @param visitTime
	 * @param operation
	 * @param state
	 * @param planKey
	 */
	public WorkPlanStc(String visitTime, String operation, int state, String planKey) {
		super();
		this.visitTime = visitTime;
		this.operation = operation;
		this.state = state;
		this.planKey = planKey;
	}

	public String getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(String visitTime) {
		this.visitTime = visitTime;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getPlanKey() {
		return planKey;
	}

	public void setPlanKey(String planKey) {
		this.planKey = planKey;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
