Command:	attach <files>

Attaches a file to the file list of the current job.

Supports multiple arguments and glob regular expressions.

Parameters

    files	: Whitespace seperated list of files

Example usage:

    attach ~/myfile.txt
    attach "~/my file.txt"
    attach ~/myfile_1.txt /~myfile_2.txt
    attach ~/*.txt