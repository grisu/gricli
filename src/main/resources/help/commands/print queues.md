Command: print queues [queue_properties]

Lists all queues that are available for the current environment.

The current environment is the group you set, the application package and version you choose (if any),
as well as the walltime, number of CPUs and memory (RAM).

Parameters:

	queue_properties : List of properties you want to have displayed per queue. (Optional) 

Allowed values: 

free_job_slots, gram_version, job_manager, queue_name, ramsize, rank, 
running_jobs, site, smp_size, total_jobs, virtualramsize, waiting_jobs
    
Example usage:

    print queues 
    print queues site job_manager total_jobs rank


