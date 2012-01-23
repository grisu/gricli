Command: wait job <jobname>

Waits for a job to finish. 

This is useful in scripts where the execution will block until the job has finished.
It allows for actions to be taken automatically when a job has finished e.g:

    set jobname myjob
    submit echo Hello
    wait job myjob
    download job myjob

Parameters:

    jobname	: The name of the job on which to wait. 

Note: Regular expressions are not supported and only a single job can be waited on.

Example usage:

    wait job myjob