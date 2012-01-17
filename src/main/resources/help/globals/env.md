Global: env

The execution environment variables of a job.

To add an environment variable and value use the 'add env <var> <value>' command.

Note that you do not need '$' as part of the variable name.

To view the environment variables and their values before submission use the command 'print global environment'.
To view the environment variables after submission use the command 'print job <jobname> environmentVariables'.

Example usage:

    add env MY_VAR MY_VALUE
    print global env
    print job myjob environmentVariables

For MPI jobs using multiple hosts, the environment variables must be explicitly exported using the -x option in mpirun e.g:

    submit -x MY_VAR /home/me001/my_application arg0 arg1
    

    
    