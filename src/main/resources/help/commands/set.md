Command: set <var> <value>

Sets a value for a variable.

Parameters:

    var	  : The name of the variable.
    value : The value.

Currently only the global variables for a job (such as the amount of memory to be used) can be set.

To the set the value of list-type variables such as 'files' and 'env' use the 'add' command.
See the help file for the 'add' command for examples of setting and unsetting lists.

Example usage:

    set memory 1g
    set set cpus 10
    set walltime 3d

