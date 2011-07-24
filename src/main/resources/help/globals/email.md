Global: email

The email address to send notificaitons to.

The email address can be set using the 'set' command.
To view the email address of a job before submission use the command 'print global email'.
To view the email address of a job after submission use the command 'print job <jobname> email_address'.

Example usage:

    set email myemail@myhost.x
    print global email
    print job myjob email_address