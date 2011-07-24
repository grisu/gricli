Global: memory

The total memory (in MB) to be used by the job.

The total memory is the divided amongst the cpus.
To set the memory for the job, use the 'set' command. The command accepts values in the following formats:

    set memory 200       : sets memory to 200 MB
    set memory 200m      : sets memory to 200 MB
    set memory 1g        : sets memory to 1 GB (1024 MB)
    set memory 1g200m    : sets memory to 1224 MB

To view the memory of a job before sumbmission use the command 'print global memory'.
To view the memory of a job after submission use the command 'print job <jobname> memory.

Example usage:

   set memory 1224
   set memory 1g200m
   print global memory
   print job myjob memory
   

