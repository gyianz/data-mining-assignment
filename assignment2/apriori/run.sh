#!/bin/bash
FILES=./data/*
for f in $FILES
do
	for a in 0.9 0.8 0.7 0.6 0.5 0.4 0.3 0.2 0.1
	do
		for x in 1 2 3 4 5
		do
				b=$(basename $f)
				echo "Doing $f $a $x";
  				java miningRule $f $a $x > output/$x/$b$a.log
		done
	done
done
