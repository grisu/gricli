Global: version

The application package version.

This is the package version to be used. 
By default the value is 'any'.

If a package is specified and the queue is set to auto then the job will be submitted
to valid queue locaiton that supports a version of the chosen package.

To ensure a specific version of the package is used, use the 'set' command to choose the version.
To see the list of versions available for a package use the command 'print package <package>'.

Example usage:

    set package R
    set version any

    set package R
    set version  2.11.1

    