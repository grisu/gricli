Command: clean job <jobname>

Kills a job if it still running and then removes it from the database and deletes the job directory.

Supports glob regular expressions.

Parameters:

    jobname : The name of the job to clean

Example usage:

    clean myjob
    clean myjob_1
    clean myjob_2
    clean myjob*
    clean *
    clean jobs
