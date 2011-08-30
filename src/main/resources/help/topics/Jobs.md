Topic: Jobs
===========

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

A typical job requires that that an application be set and that the version be specific or 'any'.
To view the available applications, use the command 'print applications'.

Note that application packages are bound to specific queues and to use applications in the package you must have access
to the appropriate queue. To see the versions and queues for an application use the command 'print application <application_package>'.

To set the queue, use the 'set' command. You can view all available queues with the 'print queues' command or 
you can filter by package using the command 'print application <application_package>'. You may also filter by group using the 
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
    > set application UnixCommands
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


