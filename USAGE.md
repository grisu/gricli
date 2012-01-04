% GRICLI(1) Gricli user manual
% Yuriy Halytskyy, Markus Binsteiner
% July 1, 2011

<!-- 

Don't edit the USAGE.md file directly since it'll be overwritten with regularly. Edit man/manpage-template.md instead

 -->

# NAME

gricli - grid commandline interface

# SYNOPSIS

gricli [*options*] 

# DESCRIPTION

Gricli is a shell that allows commandline interaction with the grid. You can use it to submit, control and monitor jobs. It also supports easy access to grid-filesystems and resource information.

Gricli is based on the *Grisu* framework and can connect to different *Grisu backends* by selecting the appropriate one as a commandline parameter, e. g.:

    gricli -b BeSTGRID
    
 or 
 
    gricli -b Local
     
The above first would connect to the default *BeSTGRID* backend that publishes the *Grisu* API via SOAP. The latter would connect to a local *Grisu* backend which sits on the same computer as *gricli*, as long as the local backend jar (http://code.ceres.auckland.ac.nz/downloads/local-backend.jar) is in the classpath (either in the same folder as gricli or in %$HOME/.grisu.beta/lib/).

# OPTIONS

-b *BACKEND* or \--backend=*BACKEND*

  The Grisu backend to connect to. The default is *BeSTGRID* and other possible backends are
  *BeSTGRID-TEST*,*BeSTGRID-DEV* and *Local*.

  Examples:

    gricli -b BeSTGRID
    gricli --backend=BeSTGRID

-f  *SCRIPT* or \--file=*SCRIPT*

  Executes a gricli script.

  Examples:

    gricli -f myexp.gs
    gricli --file=myexp.gs

-n or \--nologin

  Disables login at gricli startup.

  Example:

    gricli -n

# GLOBALS


Globals define the properties of a job.

Overview
---------

Globals are the properties for a job. They let you determine how and where your job will be executed.
To see the list of globals use the command 'print globals'.
You may use the 'set' command to set any of the global properties.
To view an individual property use the command 'print global <global>'.

To view information and examples on each global use the command 'help <global>'.
Note that you may use a plain text script to automate the task of setting globals.
For more information see use the command 'help run'.

Example usage:

print globals
print global memory
set memory 1g
help memory
help global memory


## List of globals:


### cpus


The number of cpus to be used by a job.

To set the number of cpus use the 'set' command.
To view the number of cpus used in a job use the command 'print global cpus'.
After a job has been submitted you can check the cpus used with the command 'print job <jobname> cpus'

Example usage:

    set cpus 10
    print global cpus
    print job myjob cpus



### debug


Boolean to show debug output from exceptions.

This value can be set using the 'set' command.

Example usage:

    set debug true
    set debug false

### description


The description of a job.

To set the job description use the 'set' command.
To view the description before the job has been submitted use the command 'print global description'.
To view the description of a submitted job use the command 'print job <jobname> description'.

Example usage:

    set description "my job description"
    print global description
    print job myjob description

### dir


The job directory.

This is the directory the job will be downloaded to after a 'download' or 'downloadclean' command.

It is also used as the starting point where relative paths are applicable. 
For example if a file is located at /home/myfolder/myfile and the dir variable is /home then 
myfile can be attached with using the relative path: attach myfolder/myfile

To set the dir use the 'set' command or the 'cd' command.
To view the dir before a job has been submitted use the command 'print global dir'.
To view the dir after a job as been submitted use the command 'print job <jobname> jobDirectory'.

Grid locations (starting with prefix grid://) are currently not supported for this command.

Example usage:

    set dir ~
    set dir /home/myfolder
    cd ~/myfolder

### email


The email address to send notifications to.

The email address can be set using the 'set' command.
To view the email address of a job before submission use the command 'print global email'.
To view the email address of a job after submission use the command 'print job <jobname> email_address'.

Example usage:

    set email myemail@myhost.x
    print global email

### email_on_finish


Boolean to send an email notification when a job has finished.

To set the variable use the 'set' command.
To view the setting before submission use the command 'print global email_on_finish'.
To view setting after submission use the command 'print job <jobname> email_on_finish'.

Example usage:

    set email_on_finish true
    set email_on_finish false

### email_on_start


Boolean to send an email notification when a job has started executing.

To set the variable use the 'set' command.
To view the setting before submission use the command 'print global email_on_start'.
To view setting after submission use the command 'print job <jobname> email_on_start'.

Example usage:

    set email_on_start true
    set email_on_start false

### environment


The execution evironment variables of a job.

To add an environment variable and value use the 'add env <var> <value>' command.

Note that you do not need '$' as part of the variable name.

To view the environment variables and their values before submission use the command 'print global environment'.
To view the environment variables after submission use the command 'print job <jobname> environmentVariables'.

Example usage:

    add environment MY_VAR MY_VALUE
    print global environment
    print job myjob environmentVariables


### gdir


The grid directory.

This is for use with the filemanager command which has yet to be implemented.

### group


The group used to send jobs.

The group determines which queues you will have access to and consequently which application package you can use.

To set the group use the 'set' command. Note that the group must be set before a job can be submitted.
To view the group before a job has been submitted use the command 'print global group'.
To view the group after a job has been submitted use the command 'print job <jobname> group'.

Example usage:

    set group /nz/nesi
    print global group
    print job myjob group




### host


The hostname.

To set the hostname use the 'set' command.
The available hostnames can be seen using the command 'print hosts'.
To view the host before a job has been submitted use the command 'print global host'.
To view the host after a job has been sumitted ise the command 'print job <jobname> submissionHost'.

Example usage:

    set host ng2.canterbury.ac.nz
    print global host
    print job myjob submissionHost

### jobname


The job name.

This will be the name of the submitted job.
If a job with this name already exists, an integer will be appended to make sure it is unique.

To set the job name use the 'set' command.
To view the job name before submission use the command 'print global jobname'.
To view the job name after a job has been submitted use the command 'print jobs'.

Example usage:

    set jobname myjob
    print global jobname

### jobtype


The type of job to submit.

The job type determines how the job is configured for execution.

The current values are:

    smp          : A job that will use one or more CPUs on a single host.
    mpi          : A job that will use one or more CPUs across one or more hosts using the Open MPI framework.
    custom       : A job that will use one or more CPUs across one or more hosts using a custom configuration.

The number of hosts used for an mpi job can be checked after submission using the command 'print job <jobname> hostCount'.

Please note that a 'host' is a compute node within a queue. Since the hardware specifications may vary between hosts in a queue, you are advised to check the properties of your queues to ensure you jobs run correctly. In particular, it is important that jobs do not request more resources than are available for a given job type. Some tips are provided below:

SMP

When you select a job of this type, please ensure that the at least one host in the queue can meet the job requirements.

MPI

When you select a job of this type, please ensure that the requested resources do not exceed the maximum capacity of the queue.

Custom

Please note that is up to you to ensure your job is scheduled correctly as this job type implies you may not be relying on Open MPI to coordinate your processes.
    

Example usage:

    set jobtype mpi
    print global jobtype
    print job myjob hostCount

### memory


The total memory (in MB) to be used by the job.

The total memory is the divided amongst the cpus.
To set the memory for the job, use the 'set' command. The command accepts values in the following formats:

    set memory 200       : sets memory to 200 MB
    set memory 200m      : sets memory to 200 MB
    set memory 1g        : sets memory to 1 GB (1024 MB)
    set memory 1g200m    : sets memory to 1224 MB

To view the memory of a job before submission use the command 'print global memory'.
To view the memory of a job after submission use the command 'print job <jobname> memory.

Example usage:

   set memory 1224
   set memory 1g200m
   print global memory
   print job myjob memory
   


### outputfile


The path to a file where Gricli output is redirected to.

Some Gricli commands will print messages for the user. This output can be redirected to a file for processing.

Example usage:

    set outputfile /home/myfolder/gricli_output.txt

### package


This is the application package used by the job.

To set the package use the 'set' command.
To see a list of available packages use the 'print packages' command.
To see which package is set for a job before it is submitted, use the command 'print global package'.
After a job has been submitted you can check the package with 'print job <jobname> package'

Note that the package is not set by default and is required to submit a job.

Example usage:

    print package
    set package R
    print global package
    print job myjob package
    

### prompt


The prompt message.

This is can changed using the 'set' command.
The command can accept macros, substituting the values of other globals into the prompt.

Example usage:

   set prompt "myprompt> "
   set prompt "${dir}> "
   set prompt "${jobname}> "


### queue


The job queue.

The job queue will determine which resources and application packages are available for your job.

To set the queue use the 'set' command.
To see a list of queues use the 'print queues command'.

You can only submit jobs to queues assigned to your group.
To view the available to groups use the 'print groups' command.
To view the queues available for a specific groups use the command 'print queues <group>'

To see which queues support a particular application package use the command 'print package <application_package>'.
To see a list of application packages use the command 'print packages'.

If you have set the application package, then the queue location can be determined automatically.
Use the command 'set queue auto' to enable this option.

To see the queue before a job is submitted use the command 'print global queue'.
To see the queue after a job has been submitted use the command 'print job <jobname> submissionLocation'.

Example usage:

    set queue auto
    set queue gpu:gram5.ceres.auckland.ac.nz
    print global queue
    print job myjob submissionLocation




### version


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


### walltime


The walltime for the job measured in minutes.

The walltime determines the upper limit on how long a job will execute for.
If a job has not finished after the allocated walltime, the job will be killed.

Walltime can be set with strings as follows:

   set walltime 120            : sets the walltime for 120 minutes.
   set walltime 1d2h3m         : sets the walltime for 1 day 2 hours and 3 minutes.

To view the walltime before a job has been submitted, use the command 'print global walltime'.
To view the walltime after a job has been submitted, use the command 'print job <jobname> walltime'.

Example usage:

    set walltime 240
    set walltime 240m
    set walltime 4h
    set walltime 30d4h12m
    print global walltime
    print job myjob walltime

# COMMANDS


## about


Displays the following information about the Gricli shell:

version:                     This is the software version you are using.
grisu frontend version:      The interface used to communicate with Grisu.
grisu backend:               The Grisu backend (BeSTGRID or DEV) 
grisu backend host:          The Grisu host.
grisu backend version:       The version of Grisu used.
documentation:               Where you may find further help and information.
contact:                     Who to contact in case you have problems or questions.

Example usage:

    about



## add


Adds an item to a list.

Currently only a single item can be added per call. To add multiple items, use this command once for each item.

Parameters:

    list	: The name of the list.
    item	: The value to add. 

Currently available lists are:

    files       : The files attached for a job.
    environment : The environment variables in the job execution environment

Example usage:

    add files ~/myfile.txt
    add files "~/my file.txt"
    add files grid://groups/nz/nesi/myfile.txt
    add environment MY_VAR MY_VALUE
	
    


	

## apropos


Lists help entries that are associated with a keyword. 

The command displays the entry type (command, global or topic) and the entry name.
To find out more use the help command on the command,  global or topic of interest.

Parameters:

    keyword       : The keyword to search for.

Example usage

  apropos queues

## archive job


Downloads the job to the default archive location and then cleans the job.

Supports glob regular expressions. Note that if a job is still running it will be stopped. 
The archive process may take a while depending on how large the files are. 

Parameters:

    jobname    : The name of the job to archive. 

The default archive location is in the user's home directory on the Data Fabric:

    grid://groups/nz/nesi/archived-jobs/<jobname>

You can also access the Data Fabric via your browser at the following address:

    http://df.bestgrid.org/

Your files will be located in your home directory.

If the archiving was successful, the job will be deleted from the job database and the original job directory will be deleted.


Example usage:

    archive job myjob
    archive job myjob_1
    archive job myjob*


## attach


Attaches a file to the file list of the current job.

Supports multiple arguments and glob regular expressions.

Parameters

    files	: Whitespace separated list of files

Example usage:

    attach ~/myfile.txt
    attach "~/my file.txt"
    attach ~/myfile_1.txt ~/myfile_2.txt
    attach ~/*.txt

## batch add


Add a new command to a batch job container.

Parameters:

    name	: The name of the batchjob.
    command	: The new command string to add.

Example usage:


## batch attach


Attach a list of files to a batchjob container

Supports multiple arguments and glob regular expressions.

Parameters

    bactchjob   : The name of the batchjob
    files       : Whitespace separated list of files

Example usage:

    batch attach ~/myfile.txt
    batch attach "~/my file.txt"
    batch attach ~/myfile_1.txt ~/myfile_2.txt
    batch attach ~/*.txt
    batch attach grid://groups/nz/nesi/myfile.txt
 

## batch create


Creates a new batch job object. 

Batch job objects act as containers for jobs.

Parameters:

    name	: The name of the new batch job. 

Choose a meaningful name and make sure it is unique with respect to other job names.

Example usage:

    batch create mybatch


## batch submit


Submits a batch job for execution.

The batch job should created beforehand using the 'batch create' command.

Parameters:

    name    : The name of the batch job to submit.

Example usage:

    batch submit mybatch


## cd


Changes the current job directory.

Can be used in conjunction with the 'pwd' and 'ls' commands to explore the file system.
The command also sets the job global 'dir' which determines where relative paths start from.
Grid locations (starting with prefix grid://) are currently not supported.

Parameters:

    dir    : The path to the new current directory.

Example usage:

    cd /home/whoami/myfolder

    attach ~/myfolder/myfile_1 ~/myfolder/myfile_2
    cd ~/myfolder
    attach myfile_1 myfile_2


## clean job


Kills a job if it still running and then removes it from the database and deletes the job directory.

Supports glob regular expressions.

Parameters:

    jobname : The name of the job to clean

Example usage:

    clean myjob
    clean myjob_1
    clean myjob_2
    clean myjob*
    clean *
    clean jobs

## destroy proxy


Deletes your login information.

You will have to enter your login information again on your next login.

This can be used if you would like to login with another profile.

Example usage:

    destroy proxy


## downloadclean job


Downloads the job and cleans the job upon success.

Parameters:

    jobname    : The name of the job to download and clean.


If the download is not successful the job will not be cleaned.

Note that once a job has been cleaned it is no longer accessible via job related commands.


Example usage:


## download job


Downloads the whole job directory to the location specified in the global 'dir'.

Parameters:

    jobname	: The name of the job to download.

Example usage:

    download job myjob


## exec


Executes a command from the underlying shell.

Parameters:

    commandline        : The command string to execute. 

Be aware, you can't use commands with remote files (yet).

Example usage:

    exec ls -lah
    exec javac -version
    exec cat myscript.gs



## filemanager


Not yet implemented.


## help

The command syntax presented in the help files has the following format:

    command_name <required_argument> [optional_argument] 

A command may have multiple required and optional arguments.


Prints this help message or a help message for a certain command, topic or global variable.

Parameters:

    keywords	: A whitespace separated list of keywords.
	
Usage:

    help			

        Prints this message.

    help <keyword>		

        Prints a help message for a command, topic or global variable with this exact name or, if no such command, topic
        or global variable exists it lists all commands, topics or global variables that contain the keyword in the name
        or help message.

    help commands		

        Lists all available commands.

    help globals	

	Lists all available globals.

    help topics			
        
        Lists all available topics.
    
    help all			
    
        Lists all available commands, globals and topics.

    help command <command>	
     
        Prints the help message for the specified command.

    help global <global>	

        Prints the help message for the specified global variable.
    
    help topic <topic>		

        Prints the help message for the specified topic.

    help <keywords>		

        Prints the help message for the command that is called by this combination of keywords (if it exists)

    help search <keyword>	
  
       Prints a list of all commands, topics or global variables that contain the keyword in the name or help message

Example usage:

    help
    help all

    help commands
    help command print jobs
    help print jobs
    help jobs

    help globals
    help global memory
    help memory

    help topics
    help topic Jobs
    help Jobs

    help search batch

   



## ilogin


Logs in to a Grisu backend. 

Parameters:

    backend     : The Grisu backend. 

The choice of backend is one of:

    BeSTGRID        : The default backend.
    DEV    : The development backend.     

If there is no certificate proxy the user is asked details to create one.

Example usage:

    ilogin BeSTGRID
    ilogin DEV 

## kill job


Kills a job by stopping its execution.

This stops the remote execution of the job but leaves the job in the job database and also the job directory intact. To delete the job directory you need to clean the job. 

Note that a job cannot be resumed once it has been killed.

Parameters:

    jobname	: The name of the job to kill. Supports glob regular expressions.

Example usage:

    kill job myjob
    kill job myjob_1
    kill job myjob_2
    kill job myjob*
    kill job *
    kill jobs




## login


Logs in to a Grisu backend with existing certificate proxy. 

Reports an error if there is no proxy.

    backend	: The Grisu backend to login to.

The choice of backend is one of:

    BeSTGRID        : The default backend.
    DEV    			: The development backend.     

Example usage:

    login BeSTGRID
    login DEV 

## ls


Lists the current directory or the directory that is specified by the path.

Parameters:

    path	: The directory to list.

Example usage:

    ls ~
    ls /home/whoami
    ls grid://groups/nz/nesi




## print global


Prints the value of the specified global variable.

    varname	: The name of the global variable.

Use 'print globals' to see the list of global variables.

Example usage:

    print global memory
    print global walltime
    print global cpus





## print globals


Lists all global variables.

Example usage:


## print groups


Lists all groups that are available to you.

Example usage:

    print groups

## print hosts


Lists all submission gateways.

Example usage:


## print job


Prints either all or a specific property of a job.

    jobname     : The name of the job. Supports glob regular expressions.
    property	: The property.

To see the available job properties use:

    print job <jobname>

Example usage:

    print job myjob
    print job myjob memory
    print job myjob jobDirectory
    print job * jobDirectory

## print jobs


Lists all jobs in the job database.

The job database will store information on jobs that are currently running or have finished / stopped. Once a job has been cleaned or archived, the job will be removed from the database and can no longer be queried.

Example usage:

    print jobs



## print package


Prints the available versions and queue locations for the specified application package.

Parameters:

    application_package    : The application package. Supports glob regular expressions.

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



## print packages


List all application packages available to you.

Note that application packages are bound to queues so you must ensure the queue you submit to can support the
package you would like to use. This will be taken care of when you set the queue to 'auto'.

If you set the queue manually, use the 'print package <application_package>' command to check that the application and the version you would
like to use is supported by the queue.

Example usage:

    print packages


## print queue


Displays all details about a queue.

Beware that the queue you are querying about needs to be available for your currently setup environment (package, group,...).

Parameters:

	queue: the name of the queue
	
Fields:

	Site			: The location of the hosts represented by the queue.
	Queue name		: The name of the queue.
	Job manager		: The type of job scheduler used.
	GRAM version	: GRAM is a submission system. More recent versions provide better performance.
	
	Total jobs		: The total number of jobs in the queue.
	Running jobs	: The number of active jobs in the queue.
	Waiting jobs	: The number of jobs waiting to run.
	
    
Example usage:

    print queue default:gram5.ceres.auckland.ac.nz

## print queues


Lists all queues that are available for the current environment.

The current environment is the group you set, the application package and version you choose (if any),
also walltime, number of CPUs and memory (RAM).

Parameters:

	queue properties : (optional) properties you want to have displayed, per queue. 

Allowed values: 

free_job_slots, gram_version, job_manager, queue_name, ramsize, rank, 
running_jobs, site, smp_size, total_jobs, virtualramsize, waiting_jobs
    
Example usage:

    print queues 
    print queues site job_manager total_jobs rank



## pwd


Prints the current working directory.

Used in conjunction with 'ls' and 'cd' to navigate the filesystem.

Example usage:

    pwd

## quit


Logs out of this Gricli session.

Login information is left intact so you don't need to enter those on your next login.
 
If the information is no longer valid you will need to provide the information again.

Example usage:

    quit



## run


Runs a set of commands from a plain text file.

Using a script can automate common tasks such as configuring your job environment.

Parameters:

    script	: The plain text file containing commands.

A specific file extension (.txt, .xyz) is not required for the filename and you may use the '#' character 
to ignore lines in the script.

Example script:

#Name: myscript
#Script to setup and run a job
set group /my/group
set package UnixCommands
set jobname myjob
set jobtype single
set memory 1g
set cpus 1
set walltime 10m
set description "a test job"
submit echo "Hello World"

Example usage:

    run myscript
    run myscript.txt
    run myscript.xyz



## set


Sets a value for a variable.

    var		: The name of the variable.
    value	: The value.

Currently only the global variables for a job can be set.
To reset a global to a default value use the 'unset <global>' command.

Example usage:

    set memory 1g
    set set cpus 10
    set walltime 3d


## status


Displays a summary of current jobs.

Fields are defined as follows:

	Active				: The number of jobs that are running or waiting to run.
	Finished			: The number of jobs that have stopped running.
						  Successful jobs finished within their walltime limit.
						  Failed jobs were killed.
	Broken/Not found 	        : These jobs have had an error before starting.

Example usage:

    status



## submit


Submits a new job to execute the provided command

The job will be created on will wait on the queue until it is executed. If the submission is successful, the name of the job will be displayed.
The job parameters are set using the global variables. For more information on job properties type 'help globals'. To learn more about jobs in
general, see the help topic entry for Jobs: 'help topic Jobs'.

Parameters:

    command    : The command to be executed.
    &          : Specifies asynchronous execution.  


If & is specified the command will run in the background.     
    	
Example usage:

    submit echo "hello world"
    submit sleep 100 &

## unset


Resets an optional variable to its default value.

    var		: The name of the optional variable.

Currently only the non-essential global variables for a job can be unset. 
To set a global variable use the 'set <var> <value>' command.

Example usage:

	unset files
	unset email
	


## user clearCache


Clears the Grisu file system cache. 

You need to logout and login again to see the effects of this command. Be aware that the next login will take longer than usual because the filesystem cache is rebuilt at that stage.

## wait job


Waits for a job to finish on the remote compute resource. 

This is useful in Grilci scripts where the execution will block until the job has finished. It allows for actions to be taken automatically when a job has finished. 

Parameters:

    jobname	: the name of the job on which to wait. Regular expressions are not supported.

Currently only a single job can be waited on.

Example usage:



# Files


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
	
	

	
	

	


# Jobs


An executable command and the environment properties.

Overview
---------

A job is the configuration for the program you would like to execute on the cluster.
Job properties are set through 'globals'. These allow you to set the application package and version to run
as well as the resources that the application needs such as the memory (RAM) and the number of CPUs.

To see a list of available job properties use the command 'print globals'.
To set a job property use the command 'set <variable>'.
To see the value for a specific job property use the command 'print global <property>'.

Job Requirements
-----------------

A typical job requires that that an application package be set and that the version be specific or 'any'.
To view the available packages, use the command 'print packages'.

Note that application packages are bound to specific queues and to use applications in the package you must have access
to the appropriate queue. To see the versions and queues for an application package use the command 'print package <application_package>'.

To set the queue, use the 'set' command. You can view all available queues with the 'print queues' command or 
you can filter by package using the command 'print package <application_package>'. You may also filter by group using the 
command 'print queues <group>'.
  
If you do not mind which queue your job is submitted to, you can use 'set queue auto' to let the system determine
the appropriate queue. 

Note that regardless of your queue choice, you must choose a group. You can view the available groups using the command
'print groups'. To set the group use the command 'set group <group>'.

Job memory and CPU count will depend on your application. By default a job has 2 GB of memory and 1 CPU. This is the
default configuration for a 'single' jobtype. To use multiple CPUs you will need to set the jobtype to 'smp' or 'mpi' and increase the number of cpus. You may also use the 'custom' jobtype but here it is up to you to ensure correct parallelism.
For more information on these job types use the command 'help jobtype'.

If your job requires any files to run you can use the 'attach' command to set them. The files can include input files
and compiled binaries. They are stored as a list called 'files'. Use 'help attach' for more information on attaching files.
To view the files attached for a job, use the command 'print global files'.

A job also requires that the walltime be set. This is length of time in minutes that the job will run for.
For more information on walltimes use the command 'help walltime'.

Finally a job must have a job name. If you do not specify the job name, one is created for you. Job names must be unique
with respect to existing jobs. If you submit two or more jobs with the same name, they will have a number appended to 
distinguish them. To set the job name use the command 'set' command. To view the job name use the command
'print global jobname'.

Optional Properties
--------------------

Jobs may have optional properties to inform you of changes or to help manage your jobs. You may request email notification
when a job has started and when it has finished. Use the command 'set email <email>' to  set the email address.
To receive emails you must set the globals 'email_on_start' and 'email_on_finish'. These take the values 'true'
or 'false'.

Jobs may also have a description. This helps identify the job after it has been submitted. For more information, use
the command 'help description'.

An output file may also be specified to redirect messages from Gricli to a file. For more information see the help
entry on 'outputfile'.

The 'debug' property accepts a boolean (true or false) and will display errors in full. This is useful if you are having
problems and you need to report an error. The contact details in this case can be found using the 'about' command.

Job Submission
---------------

Once you have set the properties for a job you can submit using the 'submit' command. The command takes 
a string in which is the command to be executed by the remote hosts. See 'help submit' for more information.

While a job is running, a directory with the job name is created in your home directory, under the directory
'active-jobs' (~/active-jobs). The job directory is temporary and is removed when the job is cleaned.


Checking Job Progress
---------------------

You can check the details of your job using the 'print job <jobname>' command.
To see the job status use the command 'print job <jobname> status'. If you do not know the name of your job, 
you can check all current jobs using the command 'print jobs'. See the associated help for each of these commands
for more information.

To see the output of a job at any time, use the 'view' command. This will let you monitor your job progression if it is
writing to output files. For examples, type 'help view'.

Downloading Job Results
-----------------------

When a job is complete you can download the job to the location defined in the global 'dir'.
To ensure your job is downloaded to an appropriate directory, please check that the 'dir' global is correct. 

You can set the dir global with the command 'set dir <path>' and view it with the command 'print global dir'. Alternatively, 
you may use the 'cd' and 'ls' commands to navigate to the appropriate directory and the global 'dir' will match the 
current working directory. To see the current working directory use the 'pwd' command.

The job files can also be archived to your home directory on the Data Fabric. This will be grid://groups/nz/nesi
You can do this using the 'archive job' command. Note that this command cleans the job upon success.

When you are finished with a job you can use the 'clean job' command. This will remove the job directory in ~/active-jobs
and remove the job entry from the job database.

Stopping a Job
---------------

If you need to stop a job for any reason, use the 'kill job' command. Note that once a job has been stopped it cannot be resumed.

Example
--------

Here is an example of how you can setup, submit and download a job:

    > set group /nz/nesi
    > print queues /nz/nesi
    > set queue demo:gram5.ceres.auckland.ac.nz
    > set package UnixCommands
    > set jobtype single
    > set cpus 1
    > set memory 100
    > set walltime 10
    > set jobname echoJob
    > set description "Job to test echo command"
    > submit echo "Hello World"
    > print job echoJob status
    > download job echoJob
    > clean job echoJob 


Further Information
--------------------

For more information use the 'help' command to learn more about the commands, globals and topics mentioned here.



The Gricli source code and all documentation may be downloaded from
<http://github.com/grisu/gricli>.
