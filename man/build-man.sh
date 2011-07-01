#! /bin/bash

# builds the gricli manpage, uses the help.properties 
# file to get descriptions for all commands
# needs pandoc installed (http://johnmacfarlane.net/pandoc/index.html)
# also sed, awk
# first and only argument is gricli parent dir

GRICLI_DIR=$1

HELPFILE=$GRICLI_DIR/src/main/resources/help.properties

TEMPFILE=/tmp/gricli.md

cat $GRICLI_DIR/man/manpage.md > $TEMPFILE

while read line
do 
    COMMAND=${line%=*}
    DESC=${line#*=}
    CLEAN=`echo $COMMAND|sed 's/\./ /g'`
    STRIPPED=`echo $CLEAN| awk '{gsub(/^ +| +$/,"")}1'`
    if [  -n "$STRIPPED" ]
    then
	echo "\`$STRIPPED\`" >> $TEMPFILE
	echo ":    $DESC" >> $TEMPFILE
	echo >> $TEMPFILE
    fi

done < $HELPFILE

echo 'The Gricli source code and all documentation may be downloaded from
<http://github.com/grisu/gricli>.' >> $TEMPFILE


pandoc -s -w man $TEMPFILE -o $GRICLI_DIR/man/gricli.1

