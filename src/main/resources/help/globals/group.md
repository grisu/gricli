Global: group

The group used to send jobs.

The group determines which queues you will have access to and consequently which application you can use.

To set the group use the 'set' command. Note that the group must be set before a job can be submitted.
To view the group before a job has been submitted use the command 'print global group'.
To view the group after a job has been submitted use the command 'print job <jobname> fqan'.

Example usage:

    set group /nz/nesi
    print global group
    print job myjob fqan



