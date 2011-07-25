Command: exec <commandline>

Executes a command from the underlying shell.

Parameters:

    commandline        : The command string to execute. 

Be aware, for now you have to escape the commandline with "'s. Also, you can't use commands with remote files (yet).

Example usage:

    exec "ls -lah"

