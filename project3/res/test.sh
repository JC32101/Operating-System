#!/bin/bash
for i in res/out_*; do
    cat $i >> res/out.txt
done
sort -k 1 -u res/out.txt > $1_your_output.txt
diff $1_expect.txt $1_your_output.txt
exit $?
