package et.tsingtaopad.home;

public class HomeListViewStc {

	private String key;
	private String title;
	private String content;
	private String updateTime;
	private String startDate;
	private String endDate;
	private String terminalKey;

	public HomeListViewStc() {
		super();
	}

	/**
	 * @param key
	 * @param title
	 * @param content
	 * @param updateTime
	 * @param startDate
	 * @param endDate
	 */
	public HomeListViewStc(String key, String title, String content, String updateTime, String startDate, String endDate,String terminalKey) {
		super();
		this.key = key;
		this.title = title;
		this.content = content;
		this.updateTime = updateTime;
		this.startDate = startDate;
		this.endDate = endDate;
		this.terminalKey = terminalKey;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTerminalKey() {
		return terminalKey;
	}

	public void setTerminalKey(String terminalKey) {
		this.terminalKey = terminalKey;
	}
    
}
