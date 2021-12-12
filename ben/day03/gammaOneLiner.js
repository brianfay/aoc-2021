
const lines = require('fs').readFileSync('testinput', 'UTF-8').split(/\r?\n/);

// JavaScript is super clean and easy to read - see?
const gamma = parseInt(lines.filter(n => n !== "").map(n => n.split('').map(s => parseInt(s, 10))).reduce((prev, curr) => (prev.map((num, index) => num + curr[index]))).map(n => n >= lines.length/2 ? 1 : 0).join(''), 2);

console.log(gamma);
