Command: print queues [queue_properties]

Lists all queues that are available for the current environment.

The current environment is the group you set, the application package and version you choose (if any).
The order that these variables are set is important and they should be set in the following order:

     group
     package (optional)
     version (optional)
     
Once the environment is defined, the print queues command will list the available queues.

Parameters:

	queue_properties : List of properties you want to have displayed per queue. (Optional) 

Allowed values: 

    free_job_slots : The number of free CPUs on the queue.
    gram_version   : The job monitor version.
    job_manager    : The job scheduling framework.
    queue_name     : The name of the queue.
    rank           : The number of free CPUs on the queue.
    running_jobs   : The number of currently running jobs.
    site           : The institution managing the queue.
    total_jobs     : The total number of jobs, both running and queued.
    waiting_jobs   : The number of jobs that are waiting on the queue.
    
Example usage:

    print queues 
    print queues site
    print queues site job_manager


