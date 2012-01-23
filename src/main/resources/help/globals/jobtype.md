Global: jobtype

The type of job to submit.

The job type determines how the job is configured for execution.

The current values are:

    single : A job that will use one CPU on one host.
    smp    : A job that will use one or more CPUs on one host.
    mpi    : A job that will use one or more CPUs across one or more hosts using the Open MPI framework.

Please note that a 'host' is a compute node within a queue. Since the hardware specifications may vary between hosts in a queue,
you are advised to check the properties of the queue to ensure you jobs run correctly. In particular, it is important that jobs do not request more resources than are available for a given job type.

By default, an mpi job may schedule CPUs on any nodes in the queue. You may use the hostcount global to force the CPUs to be scheduled on a 
specific number of nodes. To remove this restriction, use the unset command:

    set hostcount 2
    unset hostcount

If you have set the hostcount, you can check the value using the command 'print global hostcount' and after submission using the command 'print job <jobname> hostcount'.  

Example usage:

    set jobtype mpi
    print global jobtype
    print job myjob hostcount
