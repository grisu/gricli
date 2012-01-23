Command: status

Displays a summary of current jobs.

Fields are defined as follows:

	Active			 : The number of jobs that are running or waiting to run.
	Finished		 : The number of jobs that have stopped running.						  
						 - Successful jobs finished within their walltime limit.
						 - Failed jobs were stopped for some reason.
						  
	Broken/Not found : These jobs have had an error before starting.

To see which jobs have failed try looking at the output of the stderr.txt file:

    view myjob stderr.txt

Example usage:

    status


