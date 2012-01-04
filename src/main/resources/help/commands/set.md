Command:	set <var> <value>

Sets a value for a variable.

Parameters:

    var		: The name of the variable.
    value	: The value.

Currently only the global variables for a job can be set.
To reset a global to a default value use the 'unset <global>' command.

Example usage:

    set memory 1g
    set set cpus 10
    set walltime 3d

