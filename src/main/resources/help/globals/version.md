Global: version

The application package version.

This is the application package version to be used. 
By default the value is 'any'.

If a package is specified and the queue is set to auto, the job will be submitted
to a queue location that supports a version of the chosen application package.

To ensure a specific version of the package is used, use the 'set' command to choose the version.
To see the list of versions available for an application package use the command 'print package <application_package>'.

Example usage:

    set package R
    set version any

    set package R
    set version  2.11.1

    