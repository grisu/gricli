Global: queue

The job queue.

The job queue will determine which resources and application packages are available for your job.

To set the queue use the 'set' command.
To see a list of queues use the 'print queues command'.

You can only submit jobs to queues assigned to your group.
To view the available groups use the 'print groups' command.
To view the queues available for a specific groups use the command 'print queues <group>'

To see which queues support a particular application package use the command 'print package <application_package>'.
To see a list of application packages use the command 'print packages'.

If you have set the application package, then the queue location can be determined automatically.
Use the command 'set queue auto' to enable this option.

To see the queue before a job is submitted use the command 'print global queue'.
To see the queue after a job has been submitted use the command 'print job <jobname> submissionLocation'.

Example usage:

    set queue auto
    set queue gpu:gram5.ceres.auckland.ac.nz
    print global queue
    print job myjob submissionLocation



