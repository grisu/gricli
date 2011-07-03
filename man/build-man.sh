#! /bin/bash

# builds the gricli manpage, uses the help.properties 
# file to get descriptions for all commands
# needs pandoc installed (http://johnmacfarlane.net/pandoc/index.html)
# also sed, awk
# first and only argument is gricli parent dir

GRICLI_DIR=$1

HELPFILE=$GRICLI_DIR/src/main/resources/help.properties

USAGE_FILE=$GRICLI_DIR/USAGE.md

cat $GRICLI_DIR/man/manpage-template.md > $USAGE_FILE

while read line
do 
    COMMAND=${line%=*}
    DESC=${line#*=}
    CLEAN=`echo $COMMAND|sed 's/\./ /g'`
    STRIPPED=`echo $CLEAN| awk '{gsub(/^ +| +$/,"")}1'`
    if [  -n "$STRIPPED" ]
    then
	echo "\`$STRIPPED\`" >> $USAGE_FILE
	echo ":    $DESC" >> $USAGE_FILE
	echo >> $USAGE_FILE
    fi

done < $HELPFILE

echo 'The Gricli source code and all documentation may be downloaded from
<http://github.com/grisu/gricli>.' >> $USAGE_FILE


pandoc -s -w man $USAGE_FILE -o $GRICLI_DIR/man/gricli.1

