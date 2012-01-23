Command: login <backend>

Logs in to a Grisu backend with existing proxy certificate. 

The command will report an error if there is no proxy certificate.

    backend	: The Grisu backend to login to.

The choice of backend is one of:

    BeSTGRID : The default backend.
    DEV : The development backend.     

Example usage:

    login BeSTGRID
    login DEV 
