/* Advent of Code Day 7 Puzzle 1 */
OPTIONS NONUMBER NODATE;

DATA day7;
	infile "C:\Users\bridg\Documents\Bridget's Documents\2021 Advent of Code\bridget\day7.txt"
		DLM = ",";
	input crab @@;
RUN;

PROC UNIVARIATE data = day7;
	var crab;
	output out = median_set median = crab_median;
RUN;

DATA fuel;
	set median_set day7;
	retain moves 0 median;
	if _N_ = 1 then median = crab_median;
	if _N_ > 1 then moves = moves + abs(crab - median);
RUN;

PROC PRINT data = fuel;
RUN;
