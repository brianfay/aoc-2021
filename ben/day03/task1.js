const fs = require('fs');

const lines = fs.readFileSync('input', 'UTF-8').split(/\r?\n/);

// Gamma rate is the most common binary number in each column.
// Calculate it by counting the number of 1s in each column, and
// if it's more than half the data, then the result is 1.
const gammaBinaryArray = lines.filter(n => n !== "")
  // Turn each line into an array of integer digits for easier reduction
  .map(n => n.split('').map(s => parseInt(s, 10)))
  // Reduce by summing up each column
  .reduce((prev, curr) => (prev.map((num, index) => num + curr[index])))
  // Convert to gamma value for each column based on how large the sum was
  .map(n => n >= lines.length/2 ? 1 : 0);

const gamma = parseInt(gammaBinaryArray.join(''), 2);

// Epsilon rate is the inversion of gamma. ...but javascript's binary
// "not" operator is painful to use because of two's complement, so
// doing this the manual way.
const epsilon = parseInt(gammaBinaryArray.map(d => d ? 0 : 1).join(''), 2);

console.log(gamma * epsilon);
