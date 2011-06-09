gricli shell allows commandline interaction with the grid.  

Latest version can be downloaded from ftp://ftp.bestgrid.org/pub/gricli-binary.jar

The latest development version can be downloaded from http://code.ceres.auckland.ac.nz/snapshot-downloads/gricli-dev.jar

Javadoc: http://grisu.github.com/gricli/javadoc/

Tutorial
======

Let's get going. Start gricli:

 java -jar gricli-binary.jar

If you are not logged in, select institutions login and use your
institutions credentials:

    Please select your preferred login method:
    [1]     Institutions login
    [2]     MyProxy login
    [3]     Certificate login
    [0]     Exit
    Login method: 1
    Loading list of institutions...
    ...


On successful login, gricli will create a proxy certificate valid for
10 days. For that period, gricli startup will not require user
interaction.


Finding Queues
======

This gives you a list of all the queues available in a particular group:

    gricli> print queues /nz/nesi
    ...
    gold@er171.ceres.auckland.ac.nz:ng2.auckland.ac.nz
    route@er171.ceres.auckland.ac.nz:ng2.auckland.ac.nz

If you don't care which queue to submit to, but want to run specific
application, queue needs to be set to null:

   gricli> set application R
   gricli> set version 2.10
   gricli> set queue null

'print application' command can be used to discover available applications
and versions:

    gricli> print application R

 
Various settings that make life easier
======

    gricli> set group /ARCS/BeSTGRID
    gricli> set queue route@er171.ceres.auckland.ac.nz:ng2.auckland.ac.nz
    
This queue name is from earlier when we listed the available
queues. It's a local queue, so we're going to use it to submit the
following job.

Submitting a job
======

    gricli> submit "echo hello world"
    job name is gricli_1285812002395
    gricli> print jobs
    gricli_1285812002395 : Done

This command takes a while, but when it returns, it will give you an
auto-generated job name. This job name can be used for further
commands (and supports globbing):

    gricli> print job gricli* 
    gricli> kill job gricli*
    gricli> download job gricli*
    

    gricli> attach something.txt
    
This will attach a file to every job submitted from now on. 'clear'
command can be used to reset the file list:

    gricli> clear files

'attach' command supports globs:

    gricli> attach *.xml

Jobs can be submitted asynchronously by adding & as second argument to submit command:
     
     gricli> submit "echo hello world" &

This command will complete much faster, but it will not report any submission problems. 

Help
======

'help' command will print the list of all possible commands and their
description. Any bug reports should be sent to
eresearch-admin@list.auckland.ac.nz Please send the commands, their
output and stack traces. In order to obtain stack traces, set 'debug'
to true:

    gricli> set debug true

grisu server caches faulty submission locations, and does not display
them for performance reasons. Sometimes cache must be reset when
submission location starts working again. It can be done by issuing
'clearCache' command and restarting gricli afterward:

    gricli> user clearCache 



