Command: ilogin <backend>

Logs in to a Grisu backend. 

Parameters:

    backend     : The Grisu backend. 

The choice of backend is one of:

    BeSTGRID        : The default backend.
    BeSTGRID-DEV    : The development backend.     

If there is no certificate proxy the user is asked details to create one.

Example usage:

    ilogin BeSTGRID
    ilogin BeSTGRID-DEV 
