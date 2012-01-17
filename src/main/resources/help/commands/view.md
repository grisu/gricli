Command: view [jobname] <filename> 

Prints the contents of a file.

Once a job is submitted, a job directory is created which contains all the files associated with that job.
The view command will print the contents of a specified text file in that job directory.

The command can also print the contents of a remote file without reference to a jobname.
In this case, a full path name is required.

Note: Local file paths (e.g ~/myfile) are not currently supported. 

Parameters:

    jobname:	The name of the job which the file is associated with (optional).
    filename:	The relative or full path of the file.
    
Example usage:

    view myjob stdout.txt
    view myjob input/first.txt
    view grid://groups/nz/nesi/myfile.xyz
    view grid://jobs/myjob/myfile.xyz
    view gsiftp://some.example.server/home/myfile.xyz
    

