Changelog
=========

0.5
---

* walltime global display has changed to human readable format
* clear files has been replaced by unset files, unset is a new command
* new command: unset - can be used to set optional globals to their null values. unset on required globals will throw error 
* script comments #
* glob support for print package
* new commands clean/kill jobs
* persistent user session; globals except attached files and env vars saved
* last used group remembered
* tab-completion for remote file systems
* tab-completion now also works on "submit" command for executables and input files
* submit command no longer needs quotes
* BeSTGRID-DEV renamed to **dev** case insensitive
* "print queues" now displays only queues that are available for the currently setup environment
* "print queues" can now display more information about queues, like free job slots, total running jobs
* new command: "print queue [queue]" -- displays all information that is known for a queue (which needs to be available for the current environment).
* fixed job status problem where they are incorrectly marked Done.
* renamed application to package
* pwd and exec pwd are the same now
* gls command replaced by ls

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