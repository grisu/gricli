Command: kill job <jobname>

Kills a job. 

This stops the remote execution of the job but leaves the job in the job database and also the job directory intact. To delete the job directory you need to clean the job. 

Note that a job cannot be resumed once it has been killed.

Parameters:

    jobname	: The name of the job to kill. Supports glob regular expressions.

Example usage:

    kill job myjob
    kill job myjob_1
    kill job myjob_2
    kill job myjob*
    kill job *



