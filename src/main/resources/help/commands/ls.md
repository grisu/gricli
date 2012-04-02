Command: ls [jobname_or_path]

Lists a jobdirectory, the current directory or the directory/file that is specified by the path.

If executed without parameters, this lists the current (local) directory. 

If executed with one parameter gricli checks whether the parameter is the name of a currently active job. If that's the case, the jobdirectory of this job is listed, otherwise the parameter is treated as url or path.

If executed with two parameters, the first parameter needs to be a jobname of a currently active job and the 2nd parameter needs to be a file in the jobdirectory of this job.

Parameters:

    jobname_or_path : a jobname or a directory/file to list. (Optional)

Example usage:

	ls
    ls ~
    ls job_name
    ls jobname stdout.txt
    ls /home/whoami
    ls grid://groups/nz/nesi
    ls grid://sites/Auckland/gram5.ceres.auckland.ac.nz/home/mbin029
    



