Command: print package <application_package>

Prints the available versions and queue locations for the specified application package.

Parameters:

    application_package : The application package. Supports glob regular expressions.

To see a list of available applications use:

    print packages
	
Note that application packages are bound to queues so you must ensure the queue you submit to can support the
application you would like to use. This will be taken care of when you set the queue to 'auto'.

If you set the queue manually, use the this command to check that the application and the version you would
like to use is supported by the queue.	

Example usage:

    print package R
    print package BEAST
    print package UnixCommands
    print package *
    print package B*


