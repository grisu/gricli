Global: host

The hostname.

To set the hostname use the 'set' command.
The available hostnames can be seen using the command 'print hosts'.
To view the host before a job has been submitted use the command 'print global host'.
To view the host after a job has been sumitted ise the command 'print job <jobname> submissionHost'.

Example usage:

    set host ng2.canterbury.ac.nz
    print global host
    print job myjob submissionHost
