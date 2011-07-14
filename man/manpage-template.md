% GRICLI(1) Gricli user manual
% Yuriy Halytskyy, Markus Binsteiner
% July 1, 2011

<!-- 

Don't edit the USAGE.md file directly since it'll be overwritten with regularly. Edit man/manpage-template.md instead

 -->

# NAME

gricli - grid commandline interface

# SYNOPSIS

gricli [*options*] 

# DESCRIPTION

Gricli is a shell that allows commandline interaction with the grid. You can use it to submit, control and monitor jobs. It also supports easy access to grid-filesystems and resource information.

Gricli is based on the *Grisu* framework and can connect to different *Grisu backends* by selecting the appropriate one as a commandline parameter, e. g.:

    gricli -b BeSTGRID
    
 or 
 
     gricli -b Local
     
The above first would connect to the default *BeSTGRID* backend that publishes the *Grisu* API via SOAP. The latter would connect to a local *Grisu* backend which sits on the same computer as *gricli*, as long as the local backend jar (http://code.ceres.auckland.ac.nz/downloads/local-backend.jar) is in the classpath (either in the same folder as gricli or in %$HOME/.grisu.beta/lib/).

# OPTIONS

-b *BACKEND* or \--backend=*BACKEND*

The Grisu backend to connect to. The default is *BeSTGRID* abd other possible backends are *BeSTGRID-TEST*, *BeSTGRID-DEV* and *Local*.

Examples:

gricli -b BeSTGRID
gricli --backend=BeSTGRID

-f  *SCRIPT* or \--file=*SCRIPT*

Executes a gricli script.

Examples:

gricli -f myexp.gs
gricli --file=myexp.gs

-n, \--nologin

Disables login at gricli startup.

Example:

gricli -n

# COMMANDS


