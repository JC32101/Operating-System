#!/bin/bash
set -x
rm res/cybersla.0*
rm res/cybersla_your_output.txt
rm res/out.txt
rm res/out_*
rm res/cybersla
for i in {1..300}; do cat res/cybersla_old >> res/cybersla; done
line=`wc -l $1 | cut -d" " -f1`
echo $line
filesize=$[ $line/$2+1 ]
split -l $filesize -d $1 $1.
