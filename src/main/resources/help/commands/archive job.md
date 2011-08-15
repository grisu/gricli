Command: archive job <jobname>

Downloads the job to the default archive location and then cleans the job.

Supports glob regular expressions. Note that if a job is still running it will be stopped.

Parameters:

    jobname    : The name of the job to archive. 

The default archive location is in the user's home directory on the Data Fabric:

    grid://groups/nz/nesi/archived-jobs/<jobname>

You can also access the Data Fabric via your browser at the following address:

    http://df.bestgrid.org/

Your files will be located in your home directory.

If the archiving was successful, the job will be deleted from the job database and the original job directory will be deleted.


Example usage:

    archive job myjob
    archive job myjob_1
    archive job myjob*

