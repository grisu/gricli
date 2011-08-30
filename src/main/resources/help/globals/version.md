Global: version

The application package version.

This is the application version to be used. 
By default the value is 'any'.

If an application is specified and the queue is set to auto then the job will be submitted
to valid queue locaiton that supports a version of the chosen application package.

To ensure a specific version of the application is used, use the 'set' command to choose the version.
To see the list of versions available for an application package use the command 'print application <application_package>'.

Example usage:

    set application R
    set version any

    set application R
    set version  2.11.1

    