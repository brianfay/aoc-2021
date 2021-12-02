const fs = require('fs');

const data = fs.readFileSync('input', 'UTF-8');
const lines = data.split(/\r?\n/).map(line => parseInt(line, 10));

// Not memory performant, but I'm lazy.
let windows = [];
for (let x = 0; x < lines.length-2; x++) {
  windows.push(lines[x] + lines[x+1] + lines[x+2]);
}

let prev = windows[0];
let increases = 0;
for (let x = 1; x < windows.length; x++) {
  if (windows[x] > prev) increases++;
  prev = windows[x];
}

console.log(increases);
