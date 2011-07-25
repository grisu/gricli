Global: jobname

The job name.

This will be the name of the submitted job.
If a job with this name already exists, an integer will be appended to make sure it is unique.

To set the job name use the 'set' command.
To view the job name before submission use the command 'print global jobname'.
To view the job name after a job has been submitted use the command 'print jobs'.

Example usage:

    set jobname myjob
    print global jobname
    print jobs