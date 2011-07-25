Global: application

The is the application used by the job.

To set the application use the 'set' command.
To see a list of available applications use the 'print applications' command.
To see the application set for a job before it is submitted, use the command 'print global application'.
After a job has been submitted you can check the application with 'print job <jobname> applicaiton'

Note that the applicaiton is not set by default and is required to submit a job.

Example usage:

    print applicaitons
    set application R
    print global application
    print job myjob application
    
