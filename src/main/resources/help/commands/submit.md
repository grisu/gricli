Command: submit <command> [&]

Submits a new job to execute the provided command

The job will be created on will wait on the queue until it is executed. If the submission is successful, the name of the job will be displayed.
The job parameters are set using the global variables. For more information on job properties type 'help globals'. To learn more about jobs in
general, see the help topic entry for Jobs: 'help topic Jobs'.

Jobs can also be submitted asynchronously using '&' and the end of the command. This will complete the operation
in the background and report back in the prompt with a '*'. To view pending messages, use the 'print messages' command.

Parameters:

    command    : The command to be executed.  


If & is specified the command will run in the background.     
    	
Example usage:

    submit echo "hello world"
    submit sleep 100 &
