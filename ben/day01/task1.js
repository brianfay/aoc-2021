const fs = require('fs');

const data = fs.readFileSync('input', 'UTF-8');
const lines = data.split(/\r?\n/).map(line => parseInt(line, 10));

let prev = lines[0];
let increases = 0;
for (let x = 1; x < lines.length; x++) {
  if (lines[x] > prev) increases++;
  prev = lines[x];
}

console.log(increases);
