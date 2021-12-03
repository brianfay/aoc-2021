
// =================
// Utility Functions
// -----------------

/**
  * Given an array of arrays where each column of the inner array is a 0 or 1
  * digit, returns the number of ones in the requested column across the arrays.
  */
function countOnesInColumn(arrays, colIndex) {
  return arrays.reduce((prev, curr) => prev + curr[colIndex], 0);
}

/**
  * Given an array of arrays where each column of the inner array is a 0 or 1
  * digit, returns the integer rating gathered by applying the given
  * "getBitToKeep()" bit criteria function.
  */
function getRating(linesArray, getBitToKeep) {
  // Making a copy to keep this function pure
  let remainingLines = [...linesArray];

  // Loop through our lines, each iteration removing lines from "remainingLines",
  // until we're left with only one.
  for (let inspectionCol = 0; remainingLines.length > 1; inspectionCol++) {
    const onesCount = countOnesInColumn(remainingLines, inspectionCol);
    const mostCommonValue = getBitToKeep(onesCount, remainingLines);

    // Using "==" instead of "===" because "getBitToKeep" returns "true/false",
    // and we need to compare those to the "0/1" in the line.
    remainingLines = remainingLines.filter(line => line[inspectionCol] == mostCommonValue);
  }

  // Parse the last remaining line into a base 10 integer.
  return parseInt(remainingLines[0].join(''), 2);
}


// ===================
// Actual Calculations
// -------------------

const fs = require('fs');

// Parse each line into an array of integer digits for easier manipulation.
const oxygenLines = fs.readFileSync('input', 'UTF-8')
      .split(/\r?\n/)
      .filter(n => n !== "")
      .map(n => n.split('').map(s => parseInt(s, 10)));
const co2Lines = [...oxygenLines];

// Calculate the two ratings.
const oxygen = getRating(oxygenLines,
  (onesCount, remainingLines) => onesCount >= remainingLines.length/2);
const co2 = getRating(co2Lines,
  (onesCount, remainingLines) => onesCount < remainingLines.length/2);

// Will Christmas be saved?
console.log(oxygen * co2);
