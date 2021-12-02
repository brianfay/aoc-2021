/* Advent of Code Day 1 Puzzle 1 */

DATA day1;
	infile 'C:\Users\bridg\Desktop\2021 Advent of Code\day1.txt';
	retain count 0;
	input depth;
	previous = lag1(depth);
	if previous = . then count = count;
	else if depth > previous then count = count + 1;
RUN;

PROC PRINT data = day1 (OBS = 10);
RUN;

PROC PRINT data = day1 (FIRSTOBS = 1990);
RUN;
