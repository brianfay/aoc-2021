/* Advent of Code Day 5 Puzzle 2 */
OPTIONS NONUMBER NODATE;

DATA day5;
	infile "C:\Users\bridg\Documents\Bridget's Documents\2021 Advent of Code\bridget\day5.txt" 
		DLM = ", ->";
	input x1 y1 x2 y2;
	if max(x1,x2) > max_x then max_x = max(x1,x2);
	else max_x = max_x;
	if max(y1,y2) > max_y then max_y = max(y1,y2);
	else max_y = max_y;
	retain max_x max_y;
	call symputx('n_imputs',_N_);
	call symputx('max_x_overall',max_x);
	call symputx('max_y_overall',max_y);
RUN;

DATA lines;
	set day5 end = last;
	array danger {0:&max_x_overall,0:&max_y_overall} _TEMPORARY_;
	danger_spots = 0;
	*Setting up moves for lines;
	moves = max(abs(x2-x1),abs(y2-y1));
	if (x2-x1) ne 0 then move_x = ((x2-x1)/abs(x2-x1)); *+/-1;
	else move_x = 0;
	if (y2-y1) ne 0 then move_y = ((y2-y1)/abs(y2-y1)); *+/-1;
	else move_y = 0;
	*Finding totals per x,y position;
	DO i = 0 to moves;
		position_x = x1 + move_x*i;
		position_y = y1 + move_y*i;
		if danger(position_y,position_x) = . then danger(position_y,position_x) = 1;
		else danger(position_y,position_x) = danger(position_y,position_x) + 1;
	END;
	*Finding scary spots;
	if last then 
		DO i = 0 to &max_x_overall;
			DO j = 0 to &max_y_overall;
				if danger(j,i) => 2 then danger_spots = danger_spots + 1;
			END;
			j = 0;
		END;
	RUN;

PROC PRINT data = lines;
RUN;
