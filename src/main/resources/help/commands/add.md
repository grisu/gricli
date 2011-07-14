Command:	add <list> <item>

Adds an item to a list.

Currently only a single item can be added per call. To add multiple items, use this command once for each item.

Parameters:

    list	: The name of the list.
    item	: The value to add. Currently only a single value can be added per call.

Currently available lists are:

    files       : The files attached for a job.

Example usage:

    add files myinput.txt
    add files "~/Desktop/my input.txt"
    


	
