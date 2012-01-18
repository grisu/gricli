Command: download job <jobname> [target_dir]

Downloads the whole job directory to the specified locaiton.

The job directory which includes all the job input and output files will be downloaded to the location specified
in the global 'dir' or optionally, the 'target_dir' which can be specified after the 'jobname'.

If the 'target_dir' does not exist, it will be created.

Parameters:

    jobname	   : The name of the job to download.
    target_dir : The target dir to download the job directory to.

Example usage:

    download job myjob
    download job myjob /some/dir

