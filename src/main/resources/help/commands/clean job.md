Command: clean job <jobname> [&]

Kills a job if it still running and then removes it from the database and deletes the job directory.

To clean all jobs use 'clean job *'.

Jobs can also be cleaned asynchronously using '&' and the end of the command. This will complete the operation
in the background and report back in the prompt with a '*'. To view pending messages, use the 'print messages' command.

Parameters:

    jobname : The name of the job to clean. Supports glob regular expressions.

Example usage:

    clean job myjob
    clean job myjob_1
    clean job myjob_2
    clean job myjob*
    clean job *
    clean job myjob &
    
