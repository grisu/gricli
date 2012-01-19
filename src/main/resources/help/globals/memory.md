Global: memory

The total memory (in MB) to be used by the job.

The value of this global represents the amount of physical memory (RAM) to be allocated as well as the amount of
virtual memory to be allocated. This means that if you enter the following command:

    set memory 1024
    
Your job will have 1024 MB (or 1 GB) of RAM and 1 GB of virtual memory

The way memory is used depends on the jobtype.

    single : All memory is used by one CPU.
    smp    : The memory is shared between one or more CPUs on a single host.
    mpi    : The memory is divided between the CPUs which may be on one or more hosts.
    
To set the memory for the job, use the 'set' command. The command accepts values in the following formats:

    set memory 200    : sets memory to 200 MB
    set memory 200m   : sets memory to 200 MB
    set memory 1g     : sets memory to 1 GB (1024 MB)
    set memory 1g200m : sets memory to 1224 MB

To view the memory of a job before submission use the command 'print global memory'.
To view the memory of a job after submission use the command 'print job <jobname> memory.

Please note that if you request more memory than is available for your jobtype on a given queue, the job may
stay on the queue because the scheduler cannot find the appropriate resources to start the job.

Example usage:

   set memory 1224
   set memory 1g200m
   print global memory
   print job myjob memory
   

