package grisu.gricli;

public class GricliException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public GricliException(String msg,Exception ex){
		super(msg,ex);
	}

	public GricliException(Exception ex) {
		super(ex);
	}

	public GricliException() {
		super();
	}

	public GricliException(String message) {
		super(message);
	}

}
