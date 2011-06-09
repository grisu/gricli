package grisu.gricli.command;

import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.JobSubmissionException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

public class SubmitBatchCommand implements GricliCommand {
	
	private String batchname;

	@SyntaxDescription(command={"batch","submit"},
			arguments={"name"},
			help="submits batch job (which should be created beforehand with 'batch create [name]' command")
	public SubmitBatchCommand(String batchname){
		this.batchname = batchname;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		
		BatchJobObject obj;
		try {
			obj = new BatchJobObject(env.getServiceInterface(),this.batchname,false);
		} catch (BatchJobException e) {
			throw new GricliRuntimeException(e);
		} catch (NoSuchJobException e) {
			throw new GricliRuntimeException("batch job container " + this.batchname + 
			" does not exist. Use 'create batch [containername]' command");
		}
		try {
			obj.submit();
		} catch (JobSubmissionException e) {
			throw new GricliRuntimeException(e);
		} catch (NoSuchJobException e) {
			throw new GricliRuntimeException("one of the subjobs cannot be created: " + e.getMessage());
		} catch (InterruptedException e) {
			throw new GricliRuntimeException(e);
		}
		return env;
	}

}
