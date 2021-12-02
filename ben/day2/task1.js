const fs = require('fs');

const data = fs.readFileSync('input', 'UTF-8');
const lines = data.split(/\r?\n/);

let { horizontal, depth } = lines.reduce((prev, curr) => {
  let words = curr.split(' ');
  if (words[0] === 'forward') {
    prev.horizontal += parseInt(words[1], 10);
  } else if (words[0] === 'up') {
    prev.depth -= parseInt(words[1], 10);
  } else if (words[0] === 'down') {
    prev.depth += parseInt(words[1], 10);
  }
  return prev;
}, { horizontal: 0, depth: 0 });

console.log(horizontal * depth);
