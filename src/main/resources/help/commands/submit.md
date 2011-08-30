Command: submit <command> [&]

Submits a new job to execute the provided command

The job will be created on will wait on the queue until it is executed. If the submission is successful, the name of the job will be displayed.
The job parameters are set using the global variables. For more information on job properties type 'help globals'. To learn more about jobs in
general, see the help topic entry for Jobs: 'help topic Jobs'.

Parameters:

    command    : The command to be executed.
    &          : Specifies asynchronous execution.  


If & is specificed the command will run in the background.     
    	
Example usage:

    submit echo "hello world"
    submit sleep 100 &
