Command: batch attach <batchjob> <files>

Attach a list of files to a batchjob container

Supports multiple arguments and glob regular expressions.

Parameters

    bactchjob   : The name of the batchjob
    files	: Whitespace seperated list of files

Example usage:

    batch attach ~/myfile.txt
    batch attach "~/my file.txt"
    batch attach ~/myfile_1.txt ~/myfile_2.txt
    batch attach ~/*.txt
    batch attach grid://Groups/nz/nesi/myfile.txt
 
