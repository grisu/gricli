Global: jobtype

The type of job to submit.

The job type determines how the job is configured for execution.

The current values are:

    smp          : A job that will use one or more CPUs on a single host.
    mpi          : A job that will use one or more CPUs across one or more hosts.
    single       : A job that will use one or more CPUs across one or more hosts using a custom configuration.

The number of hosts used for an mpi job can be checked after submission using the command 'print job <jobname> hostCount'.

Example usage:

    set jobtype mpi
    print global jobtype
    print job myjob hostCount
