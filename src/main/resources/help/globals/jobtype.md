Global: jobtype

The type of job to submit.

The job type determines how the job is configured for execution.

The current values are:

    smp          : A job that will use one or more CPUs on a single host.
    mpi          : A job that will use one or more CPUs across one or more hosts.
    single       : A job that will use one or more CPUs across one or more hosts using a custom configuration.

The number of hosts used for an mpi job can be checked after submission using the command 'print job <jobname> hostCount'.

Please note that a 'host' is a compute node within a queue. Since the hardware specificaitons may vary between hosts in a queue, you are advised to check the properties of your queues to ensure you jobs run correctly. In particular, it is important that jobs do not request more resources than are available for a given job type. Some tips are provided below:

SMP

When you select a job of this type, please ensure that the at least one host in the queue can meet the job requirements.

MPI

When you select a job of this type, please ensure that the requested resources do not exceed the maximum capacity of the queue.

Single

Please note that is is up to you to ensure your job is scheduled correctly as this job type implies you may not be relying on MPI to coordinate your processes.
    

Example usage:

    set jobtype mpi
    print global jobtype
    print job myjob hostCount
