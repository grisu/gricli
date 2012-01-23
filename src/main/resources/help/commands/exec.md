Command: exec <command>

Executes a command from the underlying shell.

Parameters:

    command : The command to execute. 

Please note that you can not use commands with remote files (yet).

Example usage:

    exec ls -lah
    exec javac -version
    exec cat myscript.gs


