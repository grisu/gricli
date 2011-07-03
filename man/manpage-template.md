<!-- don't edit the USAGE.md file directly since it'll be overwritten with regularly. Edit man/manpage-template.md instead -->

% GRICLI(1) Gricli user manual
% Yuriy Halytskyy, Markus Binsteiner
% July 1, 2011

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
     
 The above command would connect to the default *BeSTGRID* backend that publishes the *Grisu* API via SOAP. The latter would connect to a local *Grisu* backend which sits on the same computer as *gricli*, as long as the local backend jar (http://code.ceres.auckland.ac.nz/downloads/local-backend.jar) is in the classpath (either in the same folder as gricli or in %HOME/.grisu.beta/lib/).

# OPTIONS

-b *BACKEND*, \--backend=*BACKEND*
:    the Grisu backend to connect to, default is *BeSTGRID*, other possible backends are *BeSTGRID-TEST*, *BeSTGRID-DEV*, *Local*.

-f  *SCRIPT*, \--file=*SCRIPT*
:    Executes a gricli script

-n, \--nologin
:    Disables login at gricli startup

# COMMANDS


