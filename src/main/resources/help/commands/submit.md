Command: submit <command> [&]

Submits a new job to execute a command

The job will be created and will wait on a queue until it is executed. 

The job properties (such as associated files and memory) are set using 'global variables'. 
For more information on global varaibles type 'help globals'. To learn more about jobs in general, see the help topic entry for Jobs: 'help topic Jobs'.

Jobs can also be submitted asynchronously using '&' and the end of the command. 
This will complete the operation in the background and report back in the prompt with a '*'.
To view pending messages, use the 'print messages' command.

Parameters:

    command : The command to be executed.  
 	
Example usage:

    submit echo Hello World
    submit sleep 100 &
