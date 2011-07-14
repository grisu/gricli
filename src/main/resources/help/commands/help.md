Command:	help [keywords]

Prints this help message or a help message for a certain command, topic or global variable.

    keywords	: a whitespace seperated list of keywords

Usage:

    help			- prints this message

    help keyword		- prints a help message for a command, topic or global variable with this exact name or, if no such command, topic or global variable exists it lists all commands, topics or global variables that contain the keyword in the name or help message

    help commands		- lists all available commands
    help globals		- lists all available globals
    help topics			- lists all available topics
    
    help all			- lists all available commands, globals and topics

    help command <command>	- prints the help message for the specified command
    help global <global>	- prints the help message for the specified global variable
    help topic <topic>		- prints the help message for the specified topic

    help <keywords>		- prints the help message for the command that is called by this combination of keywords (if it exists)

    help search keyword		- prints a list of all commands, topics or global variables that contain the keyword in the name or help message

