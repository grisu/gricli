Command: print job <jobname> [property]

Prints either all or a specific property of a job.

    jobname  : The name of the job. Supports glob regular expressions.
    property : The job property. (Optional)

To see the available job properties use:

    print job <jobname>

Example usage:

    print job myjob
    print job myjob memory
    print job myjob jobDirectory
    print job * jobDirectory
