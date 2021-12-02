/* Advent of Code Day 2 Puzzle 2 */

DATA day2;
	infile 'C:\Users\bridg\Desktop\2021 Advent of Code\day2.txt' end = last;
	retain x 0 y 0 aim 0;
	input direction $ value;
	if direction = 'up' then aim = aim - value;
	else if direction = 'down' then aim = aim + value;
	else if direction = 'forward' then do
		x = x + value;
		y = y + aim * value;
		END;
	call symputx ('n', _N_);
	if last then final = x * y;
RUN;

PROC PRINT data = day2 (OBS = 10);
RUN;

PROC PRINT data = day2 (FIRSTOBS = &n);
RUN;
