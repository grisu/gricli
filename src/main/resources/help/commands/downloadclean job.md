Command: downloadclean job <jobname> [target_dir]

Downloads the job to the specified directory and cleans the job upon success.

Parameters:

    jobname    : The name of the job to download and clean.
    target_dir : The target dir to download the job directory to.

The job directory includes all the job input and output files and will be downloaded to the location specified
in the global 'dir' or optionally, the 'target_dir' which can be specified after the 'jobname'. The 'target_dir'
will be created if it does not exist.

If the download is not successful the job will not be cleaned.

Note that once a job has been cleaned it is no longer accessible via job related commands.

Example usage:

    downloadclean myjob
    downloadclean myjob /some/dir