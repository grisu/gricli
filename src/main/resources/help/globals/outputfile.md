Global: outputfile

The path to a file where command output is redirected to.

Some commands will print messages for the user. This output can be redirected to a file for processing.

Note that this option does not redirect job output. They will use the standard output files stdout.txt and stderr.txt
You can see the contents of these files using the 'view' command e.g:

    view myjob stdout.txt
    view myjob stderr.txt

Example usage:

    set outputfile /home/myfolder/output.txt
