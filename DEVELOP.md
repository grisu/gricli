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


Running gricli using maven but debug via Eclipse
-------------------------------------------------

First, we export remote debug parameters via maven:

    export MAVEN_OPTS="-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044"

then we run gricli:

    mvn exec:java -Dexec.mainClass="grisu.gricli.Gricli" -Dexec.classpathScope=compile -o -Dexec.args="-b BeSTGRID-TEST"

Then we create -- if not already created -- an Eclipse "Remote Java Application" in the "Debug Configurations", set port to 1044. This we run via Eclipse debug options. Now we are able to debug stuff like tab-completion :-)


Including JRebel
-----------------

First, we need to export the jrebel javaagent to maven:

     mvn exec:exec -Dexec.executable="java" -Dexec.args="-javaagent:/opt/JRebel4/jrebel.jar -classpath %classpath grisu.gricli.Gricli" -o -Dexec.classpathScope=compile

Doesn't work for me though, since it seems gricli can't access the console anymore with this... :-(
