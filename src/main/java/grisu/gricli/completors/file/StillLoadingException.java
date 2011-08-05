package grisu.gricli.completors.file;

public class StillLoadingException extends Exception {

	public StillLoadingException(String url) {
		super(url);
	}

}
