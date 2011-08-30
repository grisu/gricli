Command:	login <backend>

Logs in to a grisu backend with existing certificate proxy. 

Reports an error if there is no proxy.

    backend	: The Grisu backend to login to.

The choice of backend is one of:

    BeSTGRID        : The default backend.
    DEV    : The development backend.     

Example usage:

    login BeSTGRID
    login DEV 
