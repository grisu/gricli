Global: environment

The execution evironment variables of a job.

To add an environment vaiable and value use the 'add env <var> <value>' command.

Note that you do not need '$' as part of the variable name.

To view the environment vaiables and their values before submission use the command 'print global environment'.
To view the environment variables after submission use the command 'print job <jobname> environmentVariables'.

Example usage:

	add environment MY_VAR MY_VALUE
    print global environment
    print job myjob environmentVariables

