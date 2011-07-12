Command:	wait job [jobname]

Waits for a job to finish on the remote compute resource. 

Useful for use within scripts where you want to automatically submit and download/archive jobs. At the moment allows to wait for single job only.

    jobname	: the name of the job on which to wait. Regular expressions are not supported.
