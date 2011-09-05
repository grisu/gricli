Global: prompt

The prompt message.

This is can changed using the 'set' command.
The command can accept macros, substituting the values of other globals into the prompt.

Example usage:

   set prompt "myprompt> "
   set prompt "${dir}> "
   set prompt "${jobname}> "

