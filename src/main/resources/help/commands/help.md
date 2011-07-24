Command: help

    Prints this help message.


Command: help <keywords>

    Prints a help message for a command, topic or global variable with this exact name. If no such command, topic or global variable exists it lists all commands, topics or global variables that contain the keyword in the name or help message.

Parameters:

    keywords	: A whitespace seperated list of keywords.

Command:

    help			

Prints this message.

    help <keyword>


    help commands

Lists all available commands

    help globals		- Lists all available globals
    help topics			- Lists all available topics
    
    help all			- Lists all available commands, globals and topics

    help command <command>	- Prints the help message for the specified command
    help global <global>	- Prints the help message for the specified global variable
    help topic <topic>		- Prints the help message for the specified topic

    help <keywords>		- Prints the help message for the command that is called by this combination of keywords (if it exists)

    help search keyword		- Prints a list of all commands, topics or global variables that contain the keyword in the name or help message



