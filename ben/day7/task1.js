// https://www.youtube.com/watch?v=LDU_Txk06tM&t=29s

let positions = require('fs').readFileSync('input', 'UTF-8')
  .split(',')
  .map(p => parseInt(p, 10));

// Any number between the min and max could be valid optimal positions
let [min, max] = [Math.min(...positions), Math.max(...positions)];
let fuelCosts = Array(max-min+1).fill(0)
  // Build up an object of fuel costs for each position (key = position, val = fuel cost)
  .reduce((posToFuelCost, _, index) => {
    let fuelCost = positions               // Total fuel equals
      .map(p => Math.abs(p - (min+index))) // Distance for each crab
      .reduce((a, b) => a + b);            // Added together
    return Object.assign(posToFuelCost, { [min+index]: fuelCost });
  }, {});

// Figure out the most fuel efficient option
console.log(Math.min(...Object.values(fuelCosts)));