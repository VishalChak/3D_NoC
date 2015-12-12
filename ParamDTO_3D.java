
public class ParamDTO_3D {
	
	private String param;

	
	private String val;

	
	public ParamDTO_3D(String parameter, String value) {
		this.param = parameter;
		this.val = value;
	}

	
	public void setParam(String str) {
		this.param = str;
	}

	
	public void setVal(String val) {
		this.val = val;
	}

	
	public String getParam() {
		return this.param;
	}

	
	public String getVal() {
		return this.val;
	}

}