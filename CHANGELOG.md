Changelog
=========

0.7
---

* "kill/clean jobs" commands now deprecated. "kill/clean job [list of jobnames/globs]" is used insted. Supports globs completion.
* "download" and "downloadclean" commands now support optional target directory.
* "view" command can now list jobdirectories directly ("view job <jobname> <file_relative_to_jobdir>").
* Changes to login-component, makes renewal of proxy within gricli possible via 'renew session'.
* Destroy proxy renamed to 'close session'.
* Notification when user proxy reaches configured minimum
* man & markdown USAGE.md pages are created automatically via maven
* Debian and RedHat packages
* "kill/clean", "archive" and "submit" commands can now be executed in background using "&" operator
* Custom job type removed from docs and replaced by single
* hostCount renamed to hostcount
* host is no longer a global
* print queue [propeties] doesn't list memory properties

0.6
----

* renamed global "application" to "package"
* fixed job download where directories within job directories weren't downloaded
* updated Grisu dependency which brings lots of improvements to scalability and stability
* "archive job" blocks now until archiving is finished
* progress bar for killing of multiple jobs
* basic "view" command to pre-view files. mostly useful for viewing stdout/stderr while job is running

0.5.2
-----

* updated Grisu dependency with shib login fix

0.5.1
-----

* updated Grisu dependency with more logging statements

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
* new command: "status" -- prints out a summary of how many jobs are active/finished/failed
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
