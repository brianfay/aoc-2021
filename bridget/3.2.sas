/* Advent of Code Day 3 Puzzle 1 */
OPTIONS NONUMBER NODATE;

DATA day3;
	infile "C:\Users\bridg\Documents\Bridget's Documents\2021 Advent of Code\bridget\day3.txt" truncover;
	input (x1-x12) (1.) ;
	call symputx ('n1', _N_);
RUN;

/*Overly complicated way to get the mode*/
/*step 1*/
ODS OUTPUT Modes = mode1;
PROC UNIVARIATE MODE data = day3;
	var x1;
RUN;

DATA step2 (drop = VarName Mode Count criteria);
	merge day3 mode1;
	if mode ne . then criteria = mode;
	retain criteria;
	if &n1 = 1 then output;
	else if x1 = criteria then output;
RUN;

DATA check2;
	set step2;
	call symputx ('n2', _N_);
RUN;

/*step 2*/
ODS OUTPUT Modes = mode2;
PROC UNIVARIATE MODE data = step2;
	var x2;
RUN;

DATA step3(drop = VarName Mode Count criteria);
	merge step2 mode2;
	if mode ne . then criteria = mode;
	retain criteria;
	if &n2 = 1 then output;
	else if x2 = criteria then output;
RUN;

DATA check3;
	set step3;
	call symputx ('n3', _N_);
RUN;

/*step 3*/
ODS OUTPUT Modes = mode3;
PROC UNIVARIATE MODE data = step3;
	var x3;
RUN;

DATA step4 (drop = VarName Mode Count criteria);
	merge step3 mode3;
	if mode ne . then criteria = mode;
	retain criteria;
	if &n3 = 1 then output;
	else if x3 = criteria then output;
RUN;

DATA check4;
	set step4;
	call symputx ('n4', _N_);
RUN;

/*step 4*/
ODS OUTPUT Modes = mode4;
PROC UNIVARIATE MODE data = step4;
	var x4;
RUN;

DATA step5 (drop = VarName Mode Count criteria);
	merge step4 mode4;
	if mode ne . then criteria = mode;
	retain criteria;
	if &n4 = 1 then output;
	else if x4 = criteria then output;
RUN;

DATA check5;
	set step5;
	call symputx ('n5', _N_);
RUN;

/*step 5*/
ODS OUTPUT Modes = mode5;
PROC UNIVARIATE MODE data = step5;
	var x5;
RUN;

DATA step6 (drop = VarName Mode Count criteria);
	merge step5 mode5;
	if mode ne . then criteria = mode;
	retain criteria;
	if &n5 = 1 then output;
	if x5 = 1 then output; 
RUN;

DATA check6;
	set step6;
	call symputx ('n6', _N_);
RUN;

/*step 6*/
ODS OUTPUT Modes = mode6;
PROC UNIVARIATE MODE data = step6;
	var x6;
RUN;

DATA step7 (drop = VarName Mode Count criteria);
	merge step6 mode6;
	if mode ne . then criteria = mode;
	retain criteria;
	if &n6 = 1 then output;
	else if x6 = criteria then output;
RUN;

DATA check7;
	set step7;
	call symputx ('n7', _N_);
RUN;

/*step 7*/
ODS OUTPUT Modes = mode7;
PROC UNIVARIATE MODE data = step7;
	var x7;
RUN;

DATA step8 (drop = VarName Count criteria);
	merge step7 mode7;
	if mode ne . then criteria = mode;
	*if mode = . then criteria = 1;
	retain criteria;
	if &n7 = 1 then output;
	else if x7 = criteria then output;
RUN;

DATA check8;
	set step8;
	call symputx ('n8', _N_);
RUN;

/*step 8*/
ODS OUTPUT Modes = mode8;
PROC UNIVARIATE MODE data = step8;
	var x8;
RUN;

DATA step9 (drop = VarName Count criteria);
	merge step8 mode8;
	if mode ne . then criteria = mode;
	*if mode = . then criteria = 1;
	retain criteria;
	if &n8 = 1 then output;
	else if x8 = criteria then output;
RUN;

DATA check9;
	set step9;
	call symputx ('n9', _N_);
RUN;

/*step 9*/
ODS OUTPUT Modes = mode9;
PROC UNIVARIATE MODE data = step9;
	var x9;
RUN;

DATA step10 (drop = VarName Count criteria);
	merge step9 mode9;
	if mode ne . then criteria = mode;
	*if mode = . then criteria = 1;
	retain criteria;
	if &n9 = 1 then output;
	else if x9 = criteria then output;
RUN;

DATA check10;
	set step10;
	call symputx ('n10', _N_);
RUN;

/*step 10*/
ODS OUTPUT Modes = mode10;
PROC UNIVARIATE MODE data = step10;
	var x10;
RUN;

DATA step11 (drop = VarName Count criteria);
	merge step10 mode10;
	if mode ne . then criteria = mode;
	*if mode = . then criteria = 1;
	retain criteria;
	if &n10 = 1 then output;
	else if x10 = criteria then output;
RUN;

DATA check11;
	set step11;
	call symputx ('n11', _N_);
RUN;

/*step 11*/
ODS OUTPUT Modes = mode11;
PROC UNIVARIATE MODE data = step11;
	var x11;
RUN;


DATA step12 (drop = VarName Count criteria);
	merge step11 mode11;
	if mode ne . then criteria = mode;
	if mode = . then criteria = 1;
	retain criteria;
	if &n11 = 1 then output;
	else if x11 = criteria then output;
RUN;

DATA check12;
	set step12;
	call symputx ('n12', _N_);
RUN;

/*step 12*/
ODS OUTPUT Modes = mode12;
PROC UNIVARIATE MODE data = step12;
	var x12;
RUN;

DATA final (drop = VarName Mode Count criteria);
	merge step12 mode12;
	if mode ne . then criteria = mode;
	if mode = . then criteria = 1;
	retain criteria;
	if &n12 = 1 then output;
	if x12 = 1 then output; 
RUN;


/********************************************************************************************/


/*Reverse - step 1*/
ODS OUTPUT Modes = mode1;
PROC UNIVARIATE MODE data = day3;
	var x1;
RUN;

DATA step2R (drop = VarName Mode Count criteria);
	merge day3 mode1;
	if mode ne . then criteria = 1-mode;
	retain criteria;
	if &n1 = 1 then output;
	else if x1 = criteria then output;
RUN;

DATA check2R;
	set step2R;
	call symputx ('n2R', _N_);
RUN;

/*step 2*/
ODS OUTPUT Modes = mode2R;
PROC UNIVARIATE MODE data = step2R;
	var x2;
RUN;

DATA step3R(drop = VarName Mode Count criteria);
	merge step2R mode2R;
	if mode ne . then criteria = 1-mode;
	retain criteria;
	if &n2R = 1 then output;
	else if x2 = criteria then output;
RUN;

DATA check3R;
	set step3R;
	call symputx ('n3R', _N_);

/*step 3*/
ODS OUTPUT Modes = mode3R;
PROC UNIVARIATE MODE data = step3R;
	var x3;
RUN;

DATA step4R (drop = VarName Mode Count criteria);
	merge step3R mode3R;
	if mode ne . then criteria = 1-mode;
	retain criteria;
	if &n3R = 1 then output;
	else if x3 = criteria then output;
RUN;

DATA check4R;
	set step4R;
	call symputx ('n4R', _N_);

/*step 4*/
ODS OUTPUT Modes = mode4R;
PROC UNIVARIATE MODE data = step4R;
	var x4;
RUN;

DATA step5R (drop = VarName Mode Count criteria);
	merge step4R mode4R;
	if mode ne . then criteria = 1-mode;
	retain criteria;
	if &n4R = 1 then output;
	else if x4 = criteria then output;
RUN;

DATA check5R;
	set step5R;
	call symputx ('n5R', _N_);

/*step 5*/
ODS OUTPUT Modes = mode5R;
PROC UNIVARIATE MODE data = step5R;
	var x5;
RUN;

DATA step6R (drop = VarName Mode Count criteria);
	merge step5R mode5R;
	if mode ne . then criteria = 1-mode;
	retain criteria;
	if &n5R = 1 then output;
	else if x5 = criteria then output;
RUN;

DATA check6R;
	set step6R;
	call symputx ('n6R', _N_);

/*step 6*/
ODS OUTPUT Modes = mode6R;
PROC UNIVARIATE MODE data = step6R;
	var x6;
RUN;

DATA step7R (drop = VarName Mode Count criteria);
	merge step6R mode6R;
	if mode ne . then criteria = 1-mode;
	retain criteria;
	if &n6R = 1 then output;
	else if x6 = criteria then output;
RUN;

DATA check7R;
	set step7R;
	call symputx ('n7R', _N_);

/*step 7*/
ODS OUTPUT Modes = mode7R;
PROC UNIVARIATE MODE data = step7R;
	var x7;
RUN;

DATA step8R (drop = VarName Mode Count criteria);
	merge step7R mode7R;
	if mode ne . then criteria = 1-mode;
	retain criteria;
	if &n7R = 1 then output;
	else if x7 = criteria then output;
RUN;

DATA check8R;
	set step8R;
	call symputx ('n8R', _N_);

/*step 8*/
ODS OUTPUT Modes = mode8R;
PROC UNIVARIATE MODE data = step8R;
	var x8;
RUN;

DATA step9R (drop = VarName Mode Count criteria);
	merge step8R mode8R;
	if mode ne . then criteria = 1-mode;
	else if mode = . then criteria = 0;
	if &n8R = 1 then output;
	else if x8 = criteria then output;
RUN;

DATA check9R;
	set step9R;
	call symputx ('n9R', _N_);

/*step 9*/
ODS OUTPUT Modes = mode9R;
PROC UNIVARIATE MODE data = step9R;
	var x9;
RUN;

DATA step10R (drop = VarName Mode Count criteria);
	merge step9R mode9R;
	if mode ne . then criteria = 1-mode;
	else if mode = . then criteria = 0;
	retain criteria;
	if &n9R = 1 then output;
	else if x9 = criteria then output;
RUN;

DATA check10R;
	set step10R;
	call symputx ('n10R', _N_);

/*step 10*/
ODS OUTPUT Modes = mode10R;
PROC UNIVARIATE MODE data = step10R;
	var x10;
RUN;

DATA step11R (drop = VarName Mode Count criteria);
	merge step10R mode10R;
	if mode ne . then criteria = 1-mode;
	else if mode = . then criteria = 0;
	retain criteria;
	if &n10R = 1 then output;
	else if x10 = criteria then output;
RUN;

DATA check11R;
	set step11R;
	call symputx ('n11R', _N_);

/*step 11*/
ODS OUTPUT Modes = mode11R;
PROC UNIVARIATE MODE data = step11R;
	var x11;
RUN;

DATA step12R (drop = VarName Mode Count criteria);
	merge step11R mode11R;
	if mode ne . then criteria = 1-mode;
	else if mode = . then criteria = 0;
	retain criteria;
	if &n11R = 1 then output;
	else if x11 = criteria then output;
RUN;

DATA check12R;
	set step12R;
	call symputx ('n12R', _N_);

/*step 12*/
ODS OUTPUT Modes = mode12R;
PROC UNIVARIATE MODE data = step12R;
	var x12;
RUN;

DATA finalR (drop = VarName Mode Count criteria);
	merge step12R mode12R;
	if mode ne . then criteria = 1-mode;
	else if mode = . then criteria = 0;
	retain criteria;
	if &n12R = 1 then output;
	else if x12 = criteria then output;
	call symputx ('nfinalR', _N_);
RUN;

PROC PRINT data = final;
RUN;
PROC PRINT data = finalR;
RUN;


DATA ox_value (keep = ox_total);
	set final end = last;
	ox_total = (2**11) * x1 + (2**10) * x2 +(2**9) * x3 +(2**8) * x4 +(2**7) * x5 +(2**6) * x6 +
(2**5) * x7 +(2**4) * x8 +(2**3) * x9 +(2**2) * x10 +(2**1) * x11 + (2**0) * x12;
	if last then output;
RUN;

DATA co2_value (keep = co2_total);
	set finalR end = last;
	co2_total = (2**11) * x1 + (2**10) * x2 +(2**9) * x3 +(2**8) * x4 +(2**7) * x5 +(2**6) * x6 +
(2**5) * x7 +(2**4) * x8 +(2**3) * x9 +(2**2) * x10 +(2**1) * x11 + (2**0) * x12;
	if last then output;
RUN;

DATA DONE;
	merge ox_value co2_value;
	answer = ox_total * co2_total;
RUN;

PROC PRINT data = DONE;
RUN;
