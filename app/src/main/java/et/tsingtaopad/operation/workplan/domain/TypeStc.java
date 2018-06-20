package et.tsingtaopad.operation.workplan.domain;

import java.io.Serializable;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：TypeStc.java</br> 作者：@ray </br>
 * 创建时间：2013-12-13</br> 功能描述: 制定计划终端统计结构体</br> 版本 V 1.0</br> 修改履历</br> 日期 原因
 * BUG号 修改人 修改版本</br>
 */
public class TypeStc implements Serializable {

	private static final long serialVersionUID = 6183499605327605480L;

	private String type;
	private String num;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

}
