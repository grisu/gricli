Command: cd <dir>

Changes the current job directory.

Can be used in conjunction with the 'pwd' and 'ls' commands to explore the file system.
The command also sets the job global 'dir' which determines where relative paths start from.

Parameters:

    dir    : The path to the new current directory.

Example usage:

    cd /home/whoami/myfolder

    attach ~/myfolder/myfile_1 ~/myfolder/myfile_2
    cd ~/myfolder
    attach myfile_1 myfile_2

