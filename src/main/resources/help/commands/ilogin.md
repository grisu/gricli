Command: ilogin <backend>

Logs in to a Grisu backend. 

Parameters:

    backend : The Grisu backend. 

The choice of backend is one of:

    BeSTGRID : The default backend.
    DEV      : The development backend.     

If there is no proxy certificate the user is asked to create one.

Example usage:

    ilogin BeSTGRID
    ilogin DEV 
