Global: cpus

The number of CPUs to be used by a job.

To set the number of CPUs use the 'set' command.
To view the number of CPUs used in a job use the command 'print global cpus'.
After a job has been submitted you can check the CPUs used with the command 'print job <jobname> cpus'

Example usage:

    set cpus 10
    print global cpus
    print job myjob cpus


