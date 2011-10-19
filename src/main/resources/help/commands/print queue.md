Command: print queue [queue]

Displays all details about a queue.

Beware that the queue you are querying about needs to be available for your currently setup environment (package, group,...).

Parameters:

	queue: the name of the queue
	
Fields:

	Site			: The location of the hosts represented by the queue.
	Queue name		: The name of the queue.
	Job manager		: The type of job scheduler used.
	GRAM version	: GRAM is a submission system. More recent versions provide better performance.
	
	Total jobs		: The total number of jobs in the queue.
	Running jobs	: The number of active jobs in the queue.
	Waiting jobs	: The number of jobs waiting to run.
	
    
Example usage:

    print queue default:gram5.ceres.auckland.ac.nz
