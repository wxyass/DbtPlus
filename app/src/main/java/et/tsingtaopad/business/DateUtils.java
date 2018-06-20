package et.tsingtaopad.business;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DateUtils.java</br>
 * 作者：dbt   </br>
 * 创建时间：2014-1-23</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class DateUtils {
	
	public static String divide(String str){
		if(str==null || "".equals(str)){
			return "";
		}
		return str.substring(0, 4)+"-"+str.substring(4, 6)+"-"+str.substring(6,8);
	}
	
}
