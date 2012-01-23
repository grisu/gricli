The command syntax presented in the help files has the following format:

    command_name <required_argument> [optional_argument]

A command may have multiple required and optional arguments.

Command: help [keywords]

Prints this help message or a help message for a certain command, topic or global variable.

Parameters:

    keywords : A whitespace separated list of keywords.
	
Usage:

    help			

        Prints this message.

    help <keyword>		

        Prints a help message for a command, topic or global variable with this exact name or, if no such command, topic
        or global variable exists it lists all commands, topics or global variables that contain the keyword in the name
        or help message.

    help commands		

        Lists all available commands.

    help globals	

	    Lists all available globals.

    help topics			
        
        Lists all available topics.
    
    help all			
    
        Lists all available commands, globals and topics.

    help command <command>	
     
        Prints the help message for the specified command.

    help global <global>	

        Prints the help message for the specified global variable.
    
    help topic <topic>		

        Prints the help message for the specified topic.

    help <keywords>		

        Prints the help message for the command that is called by this combination of keywords (if it exists).

    help search <keyword>	
  
       Prints a list of all commands, topics or global variables that contain the keyword in the name or help message

Example usage:

    help
    help all

    help commands
    help command print jobs
    help print jobs
    help jobs

    help globals
    help global memory
    help memory

    help topics
    help topic Jobs
    help Jobs

    help search batch

   


