
console.log(Object.values(
  require('fs').readFileSync('input', 'UTF-8').split(/\r?\n/)
    // Parse each line ('0,9 -> 5,9') into an object ({ start: {x, y}, stop: {x, y} })
    .map(line => ({
      start: {
        x: parseInt(line.split(' -> ')[0].split(',')[0], 10),
        y: parseInt(line.split(' -> ')[0].split(',')[1], 10)
      }, stop: {
        x: parseInt(line.split(' -> ')[1].split(',')[0], 10, 10),
        y: parseInt(line.split(' -> ')[1].split(',')[1], 10)
      }
    })
    )

    // Flatten each line to an array of points (don't judge - you know you love this function)
    .reduce((points, { start, stop }) => [
      ...points,
      ...(
        // Generate array of digits from start.x to stop.x
        ((start.x < stop.x)
          ? [...Array(stop.x - start.x + 1).keys()].map(i => i + start.x)
          : ((start.x > stop.x)
            ? [...Array(start.x - stop.x + 1).keys()].reverse().map(i => i + stop.x)
            : Array(Math.abs(start.y - stop.y) + 1).fill(start.x)))
          // Zip them up with a similar array for the "y"s using reduce trickery
          .reduce(({ result, remainingYs }, curr) => ({
            result: [...result, { key: `${curr},${remainingYs[0]}`, x: curr, y: remainingYs[0] }],
            remainingYs: remainingYs.slice(1)
          }), {
            result: [],
            remainingYs: ((start.y < stop.y)
              ? [...Array(stop.y - start.y + 1).keys()].map(i => i + start.y)
              : ((start.y > stop.y)
                ? [...Array(start.y - stop.y + 1).keys()].reverse().map(i => i + stop.y)
                : Array(Math.abs(start.x - stop.x) + 1).fill(start.y)))
          }).result
      )], [])

    // Record each point on a "board". The board is a coordinate system lookup for the
    // counts to avoid having a billion empty array cells: { 'x,y': count, ... }
    .reduce((board, { key, x, y }) =>
      Object.assign(board, { [key]: (board[key] ? board[key] + 1 : 1) }),
      {})

).filter(count => count >= 2).length);
