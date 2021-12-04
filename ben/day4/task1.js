const fs = require('fs');

const lines = fs.readFileSync('input', 'UTF-8').split(/\r?\n/);

const calledNumbers = lines[0].split(',').map(n => parseInt(n, 10));

function invert(matrix) {
  let retVal = [];
  for (let x = 0; x < matrix.length; x++) {
    for (let y = 0; y < matrix.length; y++) {
      if (!retVal[y]) retVal[y] = [];
      retVal[y][x] = matrix[x][y];
    }
  }
  return retVal;
}

// Parse the boards
const originalBoards = [];
let boardIndex = -1;
lines.slice(1).forEach(line => {
  if (line === '') originalBoards[++boardIndex] = [];
  else             originalBoards[boardIndex].push(line.trim().split(/\s+/).map(n => parseInt(n, 10)));
});

// Yo, I heard you like maps, so I mapped a mapped map in your map.
let indexedBoards = originalBoards.map(board => board.map(row => row.map(num => calledNumbers.indexOf(num))));

// Determine the biggest number in each row and column, then the smallest of those.
let winningSolutions = indexedBoards.map(board => {
    let rowSolution = Math.min(...board.map(row => Math.max(...row)));
    let colSolution = Math.min(...invert(board).map(row => Math.max(...row)));
    return Math.min(rowSolution, colSolution);
});

// Which board won?
let minSolution = Math.min(...winningSolutions);
let winningBoard = winningSolutions.indexOf(minSolution);

// Ok, calculate the score. Bleh.
let sumOfUnmarked = 0;
for (let row = 0; row < indexedBoards[winningBoard].length; row++) {
  for (let col = 0; col < indexedBoards[winningBoard][row].length; col++) {
    if (indexedBoards[winningBoard][row][col] > minSolution) {
      sumOfUnmarked += originalBoards[winningBoard][row][col];
    }
  }
}
console.log(sumOfUnmarked * calledNumbers[minSolution]);
