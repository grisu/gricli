Command: add <list> <item>

Adds an item to a list.

Currently only a single item can be added per call. To add multiple items, use this command once for each item.

Parameters:

    list : The name of the list.
    item : The value to add. 

Currently available lists are:

    files : The files attached for a job.
    env   : The environment variables in the job execution environment

Example usage:

    add files ~/myfile.txt
    add files "~/my file.txt"
    add files grid://groups/nz/nesi/myfile.txt
    add env MY_VAR MY_VALUE
	
    


	
