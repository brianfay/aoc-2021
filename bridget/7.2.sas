/* Advent of Code Day 7 Puzzle 1 */
OPTIONS NONUMBER NODATE;

DATA day7;
	infile "C:\Users\bridg\Documents\Bridget's Documents\2021 Advent of Code\bridget\day7.txt"
		DLM = ",";
	input crab @@;
RUN;

PROC UNIVARIATE data = day7;
	var crab;
	output out = mean_set mean = crab_mean;
RUN;

DATA fuel;
	set mean_set day7 end = last;
	retain cost_low 0 cost_high 0 mean_low mean_high;
	move_single_crab_low = int(abs(crab - mean_low));
	move_single_crab_high = int(abs(crab - mean_high));
	cost_single_crab_low = (move_single_crab_low * (move_single_crab_low + 1)) / 2;
	cost_single_crab_high = (move_single_crab_high * (move_single_crab_high + 1)) / 2;
	if _N_ = 1 then DO;
		mean_low = int(crab_mean);
		mean_high = int(crab_mean) + 1;
		END;
	if _N_ > 1 then DO;
		cost_low = cost_low + cost_single_crab_low;
		cost_high = cost_high + cost_single_crab_high;
		END;
	if last then final = min(cost_low, cost_high);
RUN;

PROC PRINT data = fuel;
RUN;
