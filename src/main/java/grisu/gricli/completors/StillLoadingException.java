package grisu.gricli.completors;

public class StillLoadingException extends Exception {

	public StillLoadingException(String url) {
		super(url);
	}

}
