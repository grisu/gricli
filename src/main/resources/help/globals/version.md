Global: version

The application version.

The is the application version to be used. 
By default the value is 'any'.

If an application is specified and the queue is set to auto then the job will be submitted
to valid queue locaiton that supports a version of the chosen applicaiton.

To ensure a specific version of the applicaiton is used, use the 'set' command to choose the version.
To see the list of versions available for an applicaiton use the command 'print application <applicaiton'.

Example usage:

    set application R
    set version any

    set application R
    set version  2.11.1