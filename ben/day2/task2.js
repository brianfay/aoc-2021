const fs = require('fs');

const data = fs.readFileSync('input', 'UTF-8');
const lines = data.split(/\r?\n/);

let { horizontal, depth, aim } = lines.reduce((prev, curr) => {
  let words = curr.split(' ');
  if (words[0] === 'forward') {
    prev.horizontal += parseInt(words[1], 10);
    prev.depth += (prev.aim * parseInt(words[1], 10));
  } else if (words[0] === 'up') {
    prev.aim -= parseInt(words[1], 10);
  } else if (words[0] === 'down') {
    prev.aim += parseInt(words[1], 10);
  }
  return prev;
}, { horizontal: 0, depth: 0, aim: 0 });

console.log(horizontal * depth);
