How to develop on **gricli**
==========================

Getting the souce code
-------------------------------------

Recommended way to contribute is to fork the gricli repo (https://github.com/grisu/gricli) and then clone your remote repo, something like:

    git clone git@github.com:makkus/gricli.git
    
If you have Eclipse setup with the m2eclipse plugin, you can just import gricli as maven project via:

    File -> Import... -> Maven -> Existing Maven Projects 
    
and point Eclipse to the root gricli directory.

Once you made changes and want those to be included into the gricli project, just send a pull request via github.

Building 
--------------

In order to build gricli you need to have Java and Maven installed on your development machine. Once that is ready, just

    mvn clean install
    
and you have a ready-to-use **gricli-binary.jar** in the target/ directory.


Executing directly
---------------------------

The gricli main class is:

    grisu.gricli.Gricli
    
You can either start that with in your IDE (although that sometimes causes issues since the Console that comes for example with Eclipse does not support advanced features like tab-completion) or execute it via maven:

    mvn exec:java -Dexec.mainClass="grisu.gricli.Gricli" -Dexec.classpathScope=compile 

or

    mvn exec:java -Dexec.mainClass="grisu.gricli.Gricli" -Dexec.classpathScope=compile -o -Dexec.args="-b Local"

to run it with a local backend and set maven to be offline (which means it doesn't try to download dependency updates).
