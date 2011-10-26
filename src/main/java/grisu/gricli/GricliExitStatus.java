package grisu.gricli;

public enum GricliExitStatus {
	SUCCESS(0),
	RUNTIME(-1),
	SYNTAX(-2),
	LOGIN(-3);

	private int status;

	private GricliExitStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
