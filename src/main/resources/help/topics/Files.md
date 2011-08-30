Topic: Files
===========

The local and remote file resources used by jobs

Overview
--------

A job may request access to files, either as input or output parameters or as executable binaries.
To reference a file, you may specify the full path or URL in your application or you may simply 'attach' the file to the job.

Attaching Files
---------------

To attach a file use the 'attach <file_path>' command. This will add the file to the global property 'files'. The global 'files' is a list of all
additional files to be used by the job. An example of attaching a local file is shown below:

	attach /path/to/my/local/file.txt

The attached file will be uploaded to the job's working directory. This is a temporary directory that exists until the job is removed 
with the 'clean' command. The advantage of attaching files to the job is that the application you invoke may use a relative path to 
reference these files. For example, if your program requires input files then you may reference the file attached in the above example as follows:

    submit myprogram --input file.txt
	
Notice that now the file is in the working directory of the program and we do not need to specify the full path to the original file.

Removing Files
--------------

After a job has been submitted, the 'files' global will remain unchanged. If you would like to clear the list of attached files use the 
command 'unset files'. This will set the list to empty.
	
Shared Filesytems
-----------------
	
On shared filesystems, you may safely avoid attaching files as all hosts will be able to access your files. The equivalent command in this case is:
	
	submit myprogram --input /path/to/my/local/file.txt
	
This will behave in the same way as attaching the file and using a local reference (as shown above) except that some time is saved on 
file transfers. This becomes significant if you are dealing with large files.
	
Remote Files
------------

You may attach files from your cluster's GridFTP server by using grid:// prefix :

	attach grid://path/to/my/remote/file.txt
	submit myprogram --input file.txt
	
For other locations supporting GridFTP transfers use the gsiftp:// prefix :

	attach gsiftp://path/to/my/remote/file.txt
	submit myprogram --input file.txt
	
Listing Files
-------------

To see files in a directory use the 'ls' command. You can also navigate through a filesystem using the 'cd' (change directory) command.
If you need to know the directory you are currently in, use the 'pwd' (print working directory) command. The 'ls' command will show you files
in either local or remote locations:

	ls /my/local/directory
	ls grid://my/remote/directory

Note that the TAB key can be used to suggest names and values at each level in the file path. This makes typing long paths much more convenient
	
Viewing Files
-------------

Currently you may view local files using the command 'exec cat /path/to/local/file' .
Upcoming releases will include a command to let you view local as well as remote files easily.

Further Information
-------------------

For more information on any of the commands, globals or concepts presented here, please make use of the 'help' command.
	
	

	
	

	

