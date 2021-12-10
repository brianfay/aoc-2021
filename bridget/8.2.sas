/* Advent of Code Day 7 Puzzle 1 */
OPTIONS NONUMBER NODATE;

DATA day8;
	infile "C:\Users\bridg\Documents\Bridget's Documents\2021 Advent of Code\bridget\day8.txt"
		DLM = " |";
	input (in1-in10) ($) (out1-out4) ($) @@;
RUN;

DATA lights;
	set day8;
	array inputs [*] in1 - in10;
	array outputs [*] out1 - out4;
	array result [4] (0 0 0 0);
	retain sum 0;
	*Finding one, four, and four-one subsets to use as indicators;
	DO i = 1 to 10;
		if length(inputs(i)) = 4 then DO; *four subset;
			four = inputs(i);
			four1 = substr(four,1,1);
			four2 = substr(four,2,1);
			four3 = substr(four,3,1);
			four4 = substr(four,4,1);
			END;
		if length(inputs(i)) = 2 then DO; *1 subset;
			one = inputs(i);
			one1 = substr(one,1,1);
			one2 = substr(one,2,1);
			END;	
		if four ne '' and one ne '' then DO; *4-1 subset;
			check = tranwrd((tranwrd(four,trim(one1),'')),trim(one2),'');
			check1 = substr(compress(check),1,1);
			check2 = substr(compress(check),2,1);
			END;
		END;
	*Converting output strings to numbers;
	DO i = 1 to 4;
		if length(outputs(i)) = 2 then result(i) = 1;
		else if length(outputs(i)) = 3 then result(i) = 7;
		else if length(outputs(i)) = 4 then result(i) = 4;
		else if length(outputs(i)) = 7 then result(i) = 8;
		else if length(outputs(i)) = 5 then
			if findc(outputs(i),trim(one1)) > 0 and findc(outputs(i),trim(one2)) > 0 then result(i) = 3; *length=5, contains 1 as subset;
			else if findc(outputs(i),trim(check1)) > 0 and findc(outputs(i),trim(check2)) > 0 then result(i) = 5; *length=5, contains 4-1 as subset;
			else result(i) = 2;
		else if length(outputs(i)) = 6 then 
			if findc(outputs(i),trim(four1))>0 and findc(outputs(i),trim(four2))>0 
				and findc(outputs(i),trim(four3))>0 and findc(outputs(i),trim(four4))>0 then result(i) = 9; *length=6, contains 4 as subset;
			else if findc(outputs(i),trim(check1))>0 and findc(outputs(i),trim(check2))>0 then result(i) = 6; *length=6, contains 4-1 as subset;
			else result(i) = 0;
		else result(i) = .;
	END;
	*final sums;
	final = cats(result1,result2,result3,result4);
	final_num = input(final,4.);
	sum = sum + final_num;
RUN; 

PROC PRINT data = lights;
RUN;
