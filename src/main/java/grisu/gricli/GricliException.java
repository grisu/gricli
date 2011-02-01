package grisu.gricli;

public class GricliException extends Exception {

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
