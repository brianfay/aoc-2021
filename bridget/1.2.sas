/* Advent of Code Day 1 Puzzle 2 */

DATA day1;
	infile 'C:\Users\bridg\Desktop\2021 Advent of Code\day1.txt';
	retain count 0;
	input depth;
	previous = lag1(depth) + lag2(depth)+ lag3(depth);
	current = depth + lag1(depth) + lag2(depth);
	if previous = . then count = count;
	else if current > previous then count = count + 1;
RUN;

PROC PRINT data = day1 (OBS = 10);
RUN;

PROC PRINT data = day1 (FIRSTOBS = 1990);
RUN;
