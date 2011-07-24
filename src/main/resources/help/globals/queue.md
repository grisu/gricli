GLobal: queue

The job queue.

The job queue will determine which resources and applications are available for your job.

To set the queue use the 'set' command.
To see a list of queues use the 'print queues command'.

You can only submit jobs to queues assigned to your group.
To view the available to groups use the 'print groups' command.
To view the queues available for a specific groups use the command 'print queues <group>'

To see which queues support a particular application use the command 'print applicaiton <application>'.
To see a list of applications use the command 'print applicaitons'.

If you have set the application, then the queue locaiton can be determined automatically.
Use the command 'set queue auto' to enable this option.

Example usage:

    set queue auto
    set queue gpu:gram5.ceres.auckland.ac.nz



