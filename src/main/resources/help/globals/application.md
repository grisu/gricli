Global: package

The is the applicaiton package used by the job.

To set the package use the 'set' command.
To see a list of available packages use the 'print packages' command.
To see the which package is set for a job before it is submitted, use the command 'print global package'.
After a job has been submitted you can check the package with 'print job <jobname> package'

Note that the package is not set by default and is required to submit a job.

Example usage:

    print packages
    set package R
    print global package
    print job myjob package
    
