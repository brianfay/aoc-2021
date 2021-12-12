const fs = require('fs');

// Index represents the day. Value represents the number of fish.
let fishCounts = Array(9).fill(0);
fs.readFileSync('input', 'UTF-8').split(',').forEach(day => fishCounts[day]++);

// "Time passes for fish
//  One day after another
//  They make fish babies"
for (let day = 0; day < 256; day++) {
  // Rotate the array, inserting the first element at index 6
  let firstItem = fishCounts.shift();
  fishCounts[6] += firstItem;
  // Make fish babies!
  fishCounts.push(firstItem);
}

console.log(fishCounts.reduce((a, b) => a + b));