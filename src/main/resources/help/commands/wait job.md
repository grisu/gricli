Command: wait job <jobname>

Waits for a job to finish on the remote compute resource. 

This is useful in Grilci scripts where the execution will block until the job has finished. It allows for actions to be taken automatically when a job has finished. 

Parameters:

    jobname	: the name of the job on which to wait. Regular expressions are not supported.

Currently only a single job can be waited on.

Example usage:

    wait job myjob