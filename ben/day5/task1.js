const fs = require('fs');

const lines = fs.readFileSync('input', 'UTF-8').split(/\r?\n/);

const board = lines
  // Parse each line ('0,9 -> 5,9') into an object ({ start: {x, y}, stop: {x, y} })
  .map(line => {
    const [start, stop]    = line.split(' -> ');
    const [startx, starty] = start.split(',');
    const [stopx, stopy]   = stop.split(',');
    return {
      start: { x: parseInt(startx, 10), y: parseInt(starty, 10) },
      stop:  { x: parseInt(stopx, 10),  y: parseInt(stopy, 10)  }
    };
  })

  // Consider horizontal and vertical only
  .filter(({start, stop}) => start.x === stop.x || start.y === stop.y)

  // Flatten each line to an array of points
  .reduce((points, {start, stop}) => {
    for (let x = Math.min(start.x, stop.x); x <= Math.max(start.x, stop.x); x++) {
      for (let y = Math.min(start.y, stop.y); y <= Math.max(start.y, stop.y); y++) {
        points.push({x, y});
      }
    }
    return points;
  }, [])

  // Count each point on a "board". The board is a coordinate system lookup for the
  // counts to avoid having a billion empty array cells: { 'x,y': count, ... }
  .reduce((board, {x, y}) => {
    const key = `${x},${y}`;
    board[key] = board[key] ? board[key]+1 : 1;
    return board;
  }, {});

console.log(Object.values(board).filter(count => count >= 2).length);
