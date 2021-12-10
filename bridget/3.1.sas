/* Advent of Code Day 3 Puzzle 1 */
OPTIONS NONUMBER NODATE;

DATA day3;
	infile "C:\Users\bridg\Documents\Bridget's Documents\2021 Advent of Code\bridget\day3.txt" truncover;
	input (x1-x12) (1.) ;
	call symputx ('n', _N_);
RUN;

/*Overly complicated way to get the mode*/
ODS OUTPUT Modes = mode_data;
PROC UNIVARIATE MODE data = day3;
RUN;

/*Gamma and Epsilon rates - NO LONGER NEEDED*/
DATA report_values (drop = VarName Mode Count);
	set mode_data end = last;
	length gamma $12. epsilon $12.;
	gamma = cats(gamma,mode);
	if mode = '1' then epsilon = cats(epsilon,0);
	else if mode = '0' then epsilon = cats(epsilon,1);  
	retain gamma epsilon;
	if last then output;
RUN;

DATA SAS_is_Dumb; *SAS does not like binary numbers, so doing it myself!;
	set mode_data end = last;
	retain power 12 gamma_total 0 epsilon_total 0;
	power = power -1;
	gamma_total = gamma_total + (2**power) * mode;
	epsilon_total = epsilon_total + 2**power * (1-mode);
	final = gamma_total * epsilon_total;
	if last then output;
RUN;

PROC PRINT data = mode_data;
RUN;

PROC PRINT data = SAS_is_Dumb;
RUN;
