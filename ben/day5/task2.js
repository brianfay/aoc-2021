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

  // Flatten each line to an array of points (don't judge - you know you love this function)
  .reduce((points, {start, stop}) => {
    if      (start.x < stop.x) var [xOp, xCondition] = [x => x+1, x => x <= stop.x];
    else if (start.x > stop.x) var [xOp, xCondition] = [x => x-1, x => x >= stop.x];
    else                       var [xOp, xCondition] = [x => x,   x => true];
    if      (start.y < stop.y) var [yOp, yCondition] = [y => y+1, y => y <= stop.y];
    else if (start.y > stop.y) var [yOp, yCondition] = [y => y-1, y => y >= stop.y];
    else                       var [yOp, yCondition] = [y => y,   y => true];

    for (let [x,y] = [start.x,start.y];
         xCondition(x) && yCondition(y);
         [x,y] = [xOp(x), yOp(y)]) {
      points.push({x, y});
    }
    return points;
  }, [])

  // Record each point on a "board". The board is a coordinate system lookup for the
  // counts to avoid having a billion empty array cells: { 'x,y': count, ... }
  .reduce((board, {x, y}) => {
    const key = `${x},${y}`;
    board[key] = board[key] ? board[key]+1 : 1;
    return board;
  }, {});

console.log(Object.values(board).filter(count => count >= 2).length);
