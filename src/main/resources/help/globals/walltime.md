Global: walltime

The walltime for the job measured in minutes.

The walltime determines the upper limit on how long a job will execute for.
If a job has not finished after the allocated walltime, the job will be killed.

Walltime can be set with strings as follows:

   set walltime 120            : sets the walltime for 120 minutes.
   set walltime 1d2h3m         : sets the walltime for 1 day 2 hours and 3 minutes.

To view the walltime before a job has been submitted, use the command 'print global walltime'.
To view the walltime after a job has been submitted, use the command 'print job <jobname> walltime'.

You can also see the remaining walltime for a job that has started here:

    http://cluster.ceres.auckland.ac.nz/cgi-bin/showq.cgi

Example usage:

    set walltime 1000
    set walltime 30d4h12m
    print global walltime
    print job myjob walltime

