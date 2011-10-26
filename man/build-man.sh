#! /bin/bash

# builds the gricli manpage, uses the help.properties 
# file to get descriptions for all commands
# needs pandoc installed (http://johnmacfarlane.net/pandoc/index.html)
# also sed, awk
# first and only argument is gricli parent dir

GRICLI_DIR=$1

COMMANDS_DIR=$GRICLI_DIR/src/main/resources/help/commands
GLOBALS_DIR=$GRICLI_DIR/src/main/resources/help/globals
TOPICS_DIR=$GRICLI_DIR/src/main/resources/help/topics

USAGE_FILE=$GRICLI_DIR/USAGE.md

cat $GRICLI_DIR/man/manpage-template.md > $USAGE_FILE

echo "# GLOBALS" >> $USAGE_FILE
echo >> $USAGE_FILE

while read line
do
   if [[ $line != Topic:* ]] && [[ $line != =* ]]
   then 
       echo "$line" >> $USAGE_FILE
   fi
done < "$TOPICS_DIR/Globals.md"

echo "## List of globals:" >> $USAGE_FILE
echo >> $USAGE_FILE

IFS=$'\n'
for globalFile in $GLOBALS_DIR/*.md
do
   filename=${globalFile##*/}
   filename=${filename%.*}

   echo >> $USAGE_FILE
   echo "### $filename" >> $USAGE_FILE
   echo >> $USAGE_FILE

       while read line
       do
	   if [[ $line != Global:* ]]
	   then 
               echo "$line" >> $USAGE_FILE
	   fi
       done < $globalFile

done

echo "# COMMANDS" >> $USAGE_FILE
echo >> $USAGE_FILE

for commandFile in $COMMANDS_DIR/*.md
do
   filename=${commandFile##*/}
   filename=${filename%.*}

   echo >> $USAGE_FILE
   echo "## $filename" >> $USAGE_FILE
   echo >> $USAGE_FILE

       while read line
       do
	   if [[ $line != Command:* ]]
	   then 
               echo "$line" >> $USAGE_FILE
           fi
       done < $commandFile

done

echo >> $USAGE_FILE

for topicsFile in $TOPICS_DIR/*.md
do
   filename=${topicsFile##*/}
   filename=${filename%.*}

   if [[ $filename != Globals* ]] 
   then

      echo >> $USAGE_FILE
      echo "# $filename" >> $USAGE_FILE
      echo >> $USAGE_FILE

       while read line
       do
	   if [[ $line != Topic:* ]] && [[ $line != =* ]]
	   then 
               echo "$line" >> $USAGE_FILE
           fi
       done < $topicsFile
       fi
done

echo >> $USAGE_FILE
echo 'The Gricli source code and all documentation may be downloaded from
<http://github.com/grisu/gricli>.' >> $USAGE_FILE

pandoc -s -w man $USAGE_FILE -o $GRICLI_DIR/man/gricli.1

mkdir -p $GRICLI_DIR/target
cp $GRICLI_DIR/man/gricli.1 $GRICLI_DIR/target/gricli.1
