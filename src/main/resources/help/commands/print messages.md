Command: print messages

Prints pending messages from asynchronous operations

Commands can be issued to run in the background using the ampersand '&' e.g:

    submit echo hello &
    kill job myjob &
    clean job myjob &
    archive job myjob &
    
The commands will then be executed asynchronously and when they have completed an asterisk '*' will be shown in the shell prompt e.g:

    jobs> submit echo hello &
    ...
    (1*) jobs> 

This command will show the messages produced by these background opertations, informing you of their success or failure.
Once the messages have been printed, they are cleared from memory.

Example usage:

    print messages
    
    
    