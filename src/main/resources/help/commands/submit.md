Command: submit <command> [&]

Submits a new job.

The job parameters are set using the global variables and the command returns the name of the job on success.

Parameters:

    command    : The command to be executed.
    &          : Specifies asynchronous execution.  


If & is specificed the command will run in the background.     
    	
Example usage:

    submit echo "hello world"
    submit "echo \"hello world\""
    submit "sleep 100" &
