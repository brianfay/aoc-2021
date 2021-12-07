// https://www.youtube.com/watch?v=LDU_Txk06tM&t=29s

let positions = require('fs').readFileSync('input', 'UTF-8').split(',').map(p => parseInt(p, 10));

// Any number between the min and max could be valid optimal positions
let [min, max] = [Math.min(...positions), Math.max(...positions)];
let calculateFuel = (destination) => ((position) => (Math.pow(Math.abs(position - destination), 2) + Math.abs(position - destination))/2);

// Calculate an array of fuel costs for each position.
let fuelCosts = Array(max-min+1).fill(0).map((_, index) =>
  positions.map(calculateFuel(min+index)).reduce((a, b) => a + b));

// Figure out the most fuel efficient option
console.log(Math.min(...fuelCosts));