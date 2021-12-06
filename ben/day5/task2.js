
// =================
// Utility Functions
// -----------------

/**
 * Generates and returns an incrementing or decrementing array from 'begin' to 'end'.
 * If 'begin' equals 'end', then generates a repeating array for the given length.
 */
function range(begin, end, length) {
  if      (begin < end) return Array(end - begin + 1).fill(0).map((val, i) => i + begin);
  else if (begin > end) return Array(begin - end + 1).fill(0).map((val, i) => i + end).reverse();
  else                  return Array(length).fill(begin);
}


// =================
// Program Execution
// -----------------

const fs = require('fs');

const lines = fs.readFileSync('input', 'UTF-8').split(/\r?\n/);

const board = lines
  // Parse each line ('0,9 -> 5,9') into an object ({ start: {x, y}, stop: {x, y} })
  .map(line => {
    let [start, stop]    = line.split(' -> ');
    let [startx, starty] = start.split(',');
    let [stopx, stopy]   = stop.split(',');
    return {
      start: { x: parseInt(startx, 10), y: parseInt(starty, 10) },
      stop:  { x: parseInt(stopx, 10),  y: parseInt(stopy, 10)  }
    };
  })

  // Flatten each line to an array of points, represented by a string ('x,y')
  .reduce((points, {start, stop}) => {
    // Generate range arrays for the X's and Y's. (ex: [3, 4, 5] or [8, 7, 6, 5, 4])
    const xs = range(start.x, stop.x, Math.abs(start.y - stop.y)+1);
    const ys = range(start.y, stop.y, Math.abs(start.x - stop.x)+1);

    // "Zip" xs and ys together into a single string ('x,y')
    return xs.map((val, i) => `${val},${ys[i]}`).concat(points);
  }, [])

  // Record each point on a "board". The board is a coordinate system lookup for the
  // counts to avoid having a billion empty array cells: { 'x,y': count, ... }
  .reduce((board, key) =>
    Object.assign(board, { [key]: (board[key] ? board[key]+1 : 1) }),
    {});

const count = Object.values(board).filter(count => count >= 2).length;
console.log(count);
