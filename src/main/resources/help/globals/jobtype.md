Global: jobtype

The type of job to submit.

The job type determines how the job is configured for execution.

The current values are:

    single       : A job that can be run on a single cpu
    smp          : A job that will use multiple cpus on a single host.
    mpi          : A job that will use multiple cpus across multiple hosts.

The number of hosts used for an mpi job can be checked after submission using the command 'print job <jobname> hostCount'.

Example usage:

    set jobtype mpi
    print global jobtype
    print job myjob hostCount
