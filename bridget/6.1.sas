/* Advent of Code Day 6 Puzzle 1 */
OPTIONS NONUMBER NODATE;

/********************************************************************/
*USER INPUT - Number of Days;
%let days = 80;
/*********************************************************************/

DATA day6;
	infile "C:\Users\bridg\Documents\Bridget's Documents\2021 Advent of Code\bridget\day6.txt" 
		DLM = ",";
	input fish @@;
	output;
RUN;

DATA fishies;
	set day6 nobs = n end = last;
	*add up total fishies at each age;
	array num [0:8] num0-num8 (0 0 0 0 0 0 0 0 0);
	num(fish) = num(fish) + 1;
	*on last line, find total number of fishies (adults + babies!);
	if last then DO;
		DO i = 1 to &days;
			number0 = num(0);
			DO j = 0 to 8;
				if j = 6 then num(j) = number0 + num(7); *adults back to 6 + fish at 7;
				else if j = 8 then num(j) = number0; *babies to 8;
				else num(j) = num(j+1);
			END;
			j = 0;
		END;
		all_fishies = sum(of num0-num8);
	END;
RUN;

PROC PRINT data = fishies;
RUN;
	
