Global: application

This is the applicaiton package used by the job.

To set the application use the 'set' command.
To see a list of available applications use the 'print applications' command.
To see which application is set for a job before it is submitted, use the command 'print global application'.
After a job has been submitted you can check the package with 'print job <jobname> application'

Note that the application is not set by default and is required to submit a job.

Example usage:

    print application
    set application R
    print global application
    print job myjob application
    
