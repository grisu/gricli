Command:	add <list> <item>

Adds an item to a list.

Currently only a single item can be added per call. To add multiple items, use this command once for each item.

Parameters:

    list	: The name of the list.
    item	: The value to add. 

Currently available lists are:

    files       : The files attached for a job.

Example usage:

    add files ~/myfile.txt
    add files "~/my file.txt"
    add files grid://Groups/nz/nesi/myfile.txt
    


	
