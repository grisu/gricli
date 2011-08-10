Changelog
=========

0.5
---

* tab-completion for remote file systems
* BeSTGRID-DEV renamed to **dev** case insensitive
* "print queues" now displays only queues that are available for the currently setup environment
* "print queues" can now display more information about queues, like free job slots, total running jobs
* new command: "print queue [queue]" -- displays all information that is known for a queue (which needs to be available for the current environment).
* tab-completion now also works on "submit" command for executables and input files

0.4
---

* improved internal help system
  - Added commands descriptions and usage examples
  - help can now be requested for the use of a particular command (e.g. help cd)
* overall improvement of ease of use
  - added print groups command
  - added block size support for memory value set (e.g. set memory 3g)
  - added walltime set through formatted time value (e.g. set walltime 1h20m)
* New job type smp added to improve scheduling performance
* Added support for staging in data from remote gridftp servers
   - data can be staged in directly from remote gridftp servers (e.g. to stage in a file from the home directory on data fabric, use command attach grid://Groups/nz/nesi/my_file_here.dat
* jobs can have description property now
* added wait job command to pause gricli and wait until the specified job terminates
* added commands: about, cd, exec, ls, pwd
* improved auto-completion
* patches that fix bugs on Windows

Related Issues
---

Full details of all issues resolved in this release are available here:

July 2011 Milestone: https://github.com/grisu/gricli/issues?state=closed&milestone=1

June 2011 Milestone: https://github.com/grisu/gricli/issues?state=closed&milestone=5