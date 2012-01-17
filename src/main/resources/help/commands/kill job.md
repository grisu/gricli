Command: kill job <jobname> [&]

Kills a job by stopping its execution.

This stops the remote execution of the job but leaves the job in the job database and also the job directory intact.
To delete the job directory you need to clean the job. 

Note that a job cannot be resumed once it has been killed. To kill all jobs use 'kill job *'.

Jobs can also be killed asynchronously using '&' and the end of the command. This will complete the operation
in the background and report back in the prompt with a '*'. To view pending messages, use the 'print messages' command.

Parameters:

    jobname	: The name of the job to kill. Supports glob regular expressions.

Example usage:

    kill job myjob
    kill job myjob_1
    kill job myjob_2
    kill job myjob*
    kill job *
    kill job myjob &




