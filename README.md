Tutorial
======

Let's get going. Start gricli:

 java -jar gricli-binary.jar

This gives you a command shell. Are you already logged in with
[MyProxy]? If you are, you can use 'login'. If you aren't, login now
and create a proxy:

    gricli> ilogin BeSTGRID_TEST
    Please select your preferred login method:
    [1]     Institutions login
    [2]     MyProxy login
    [3]     Certificate login
    [0]     Exit
    Login method: 1
    Loading list of institutions...
    ...

All sorted. Now I'm logged in.

Finding Queues
======

This gives you a list of all the queues available in a particular VO:

    gricli> print queues /ARCS/BeSTGRID
    ...
    gold@er171.ceres.auckland.ac.nz:ng2.auckland.ac.nz
    route@er171.ceres.auckland.ac.nz:ng2.auckland.ac.nz
 
Various settings that make life easier
======

    gricli> set global fqan /ARCS/BeSTGRID
    gricli> set global queue route@er171.ceres.auckland.ac.nz:ng2.auckland.ac.nz
    
This queue name is from earlier when we listed the available
queues. It's a local queue, so we're going to use it to submit the
following job.

Submitting a job
======

    gricli> submit cmd "echo hello world"
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
    
This will attach a file to a job. It supports globs.
