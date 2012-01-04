Command: clean job <jobname>

Kills a job if it still running and then removes it from the database and deletes the job directory.

To clean all jobs use 'clean job *'.

Parameters:

    jobname : The name of the job to clean. Supports glob regular expressions.

Example usage:

    clean myjob
    clean myjob_1
    clean myjob_2
    clean myjob*
    clean *
