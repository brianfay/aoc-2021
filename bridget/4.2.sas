/* Advent of Code Day 4 Puzzle 2 */
OPTIONS NONUMBER NODATE;

DATA lotto;
	infile "C:\Users\bridg\Documents\Bridget's Documents\2021 Advent of Code\bridget\day4.txt" OBS = 1 DSD ;
	input (x1-x100)($);
	call symputx('n_lotto',_N_);
	output;
RUN;

DATA boards;
	infile "C:\Users\bridg\Documents\Bridget's Documents\2021 Advent of Code\bridget\day4.txt" FIRSTOBS = 3;
	input (a1-a5) ($) /(b1-b5) ($)/(c1-c5)($)/ (d1-d5)($) /(e1-e5)($);
	call symputx('n_boards',_N_);
RUN;

DATA game ;
	merge lotto boards;
	array lotto_raw_order {100} x1-x100;
	array lotto_order {100} lot1-lot100;
	array board_value {25} a1-a5 b1-b5 c1-c5 d1-d5 e1-e5;
	array board_order {25} o1-o25;
	retain board 0;
	board = board +1;
	if _N_ = 1 then 
		DO index = 1 to 100;
			lotto_order{index} = lotto_raw_order{index};
		END;
	DO i = 1 to 100;
		lotto_num = lotto_order{i};
		DO c = 1 to 25;
			if (lotto_num = board_value{c}) then board_order {c} = i; 
		END;
		lotto_num = .;
	END;
	retain lot1-lot100;
	
RUN; 

DATA sums;
	set game (keep = board o1-o25 a1-a5 b1-b5 c1-c5 d1-d5 e1-e5);
	row1 = max (o1, o2, o3, o4, o5);
	row2 = max (o6, o7, o8, o9, o10);
	row3 = max (o11, o12, o13, o14, o15);
	row4 = max (o16, o17, o18, o19, o20);
	row5 = max (o21, o22, o23, o24, o25);
	col1 = max (o1, o6, o11, o16, o21);
	col2 = max (o2, o7, o12, o17, o22);
	col3 = max (o3, o8, o13, o18, o23);
	col4 = max (o4, o9, o14, o19, o24);
	col5 = max (o5, o10, o15, o20, o25);
	min_value = min(row1, row2, row3, row4, row5, col1, col2, col3, col4, col5);
RUN;

DATA winner; *find highest min for part 2;
	set sums;
	retain current_min 0;
	if min_value > current_min then current_min = min_value;
	else delete;
RUN;

DATA final_winner;
	set winner end = last;
	if last then output;
RUN;

*Sum of unmarked X last number called;

DATA answer;
	merge final_winner lotto;
	array lotto_order {100} x1-x100;
	array board_final {25} a1-a5 b1-b5 c1-c5 d1-d5 e1-e5;
	DO i = 1 to 100;
		DO c = 1 to 25;
			if lotto_order{i} = board_final{c} then board_final{c} = 0; 
			lotto_final = lotto_order{i};
		END;
		row_1 = a1 + a2 + a3 + a4 + a5;
		row_2 = b1 + b2 + b3 + b4 + b5;
		row_3 = c1 + c2 + c3 + c4 + c5;
		row_4 = d1 + d2 + d3 + d4 + d5;
		row_5 = e1 + e2 + e3 + e4 + e5;
		col_1 = a1 + b1 + c1 + d1 + e1;
		col_2 = a2 + b2 + c2 + d2 + e2;
		col_3 = a3 + b3 + c3 + d3 + e3;
		col_4 = a4 + b4 + c4 + d4 + e4;
		col_5 = a5 + b5 + c5 + d5 + e5;
		if row_1 = 0 or row_2 = 0 or row_3 = 0 or row_4 = 0 or row_5 = 0 or col_1 = 0 or col_2 = 0 or col_3 = 0 or col_4 = 0 or col_5 = 0 
		then output;
	END;
RUN;

DATA answer_only;
	set answer (OBS = 1);
	total_final = sum(a1,a2,a3,a4,a5,b1,b2,b3,b4,b5,c1,c2,c3,c4,c5,d1,d2,d3,d4,d5,e1,e2,e3,e4,e5);
	answer = total_final * lotto_final;
RUN;

PROC PRINT data = answer_only;
	title 'answer';
RUN;
	
