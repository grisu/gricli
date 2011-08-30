Command: run <script>

Runs a set of commands from a plain text file.

Using a script can automate common tasks such as configuring your job environment.

Parameters:

    script	: The plain text file containing commands.

A specific file extension (.txt, .xyz) is not required for the filename and you may use the '#' character 
to ignore lines in the script.

Example script:

#Name: myscript
#Script to setup and run a job
set group /my/group
set application UnixCommands
set jobname myjob
set jobtype single
set memory 1g
set cpus 1
set walltime 10m
set description "a test job"
submit echo "Hello World"

Example usage:

    run myscript
    run myscript.txt
    run myscript.xyz


