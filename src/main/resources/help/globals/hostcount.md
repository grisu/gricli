Global: hostcount

The number of compute hosts to be used

The hostcount is important for jobs where processes communicate across a number of physical machines or hosts e.g. MPI jobs.
Setting the hostcount will force the job to use the set number of hosts. This can improve efficiency as the communications
overhead is less between processes running on the same host. However the job may take longer to be dequeued as the requirements 
are more restrictive.

The hostcount is unset by default and will not show in the list of globals. Once set, it will be visible in the
list of globals. Note that when setting the hostcount, you must use a positive integer. To disable the hostcount 
restriction use the command 'unset hostcount'.

Example usage:

    set hostcount 2
    unset hostcount
    print global hostcount
    print job myjob hostcount

