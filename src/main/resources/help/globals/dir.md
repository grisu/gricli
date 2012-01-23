Global: dir

The job directory.

This is the directory the job will be downloaded to after a 'download' or 'downloadclean' command if a target directory is not specified.

It is also used as the starting point where relative paths are applicable. 
For example if a file is located at /home/myfolder/myfile and the dir variable is /home then 
myfile can be attached with using the relative path: attach myfolder/myfile

To set the dir use the 'set' command or the 'cd' command.
To view the dir before a job has been submitted use the command 'print global dir'.
To view the dir after a job as been submitted use the command 'print job <jobname> jobDirectory'.

Grid locations (starting with prefix grid://) are currently not supported for this command.

Example usage:

    set dir ~
    set dir /home/myfolder
    cd ~/myfolder
