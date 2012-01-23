Global: package

This is the application package used by the job.

To set the package use the 'set' command.
To see a list of available packages use the 'print packages' command.

To see which package is set for a job before it is submitted, use the command 'print global package'.
After a job has been submitted you can check the package with 'print job <jobname> package'

Note that the package is set to generic by default. If you want the queue to be determined automatically, 
then it is best to set the package to ensure that the selected queue can support your job. Otherwise you would 
need to set the queue manually and check that it supports the application run by your job.
If you would like to set the queue manually, use the command 'print package <package>' to see the available queues for your application.

Example usage:

    print package
    set package R
    print global package
    print job myjob package
    
