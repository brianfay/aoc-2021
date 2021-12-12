
const lines = require('fs').readFileSync('input', 'UTF-8').split(/\r?\n/);

runPart1();
runPart2();

// ======
// Part 1
// ------

function runPart1() {
    const caveSystem = lines.map(l => l.split('').map(p => parseInt(p, 10)));

    const result = caveSystem
        .flatMap((row, rowIndex) => row.map((height, colIndex) => ({row: rowIndex, col: colIndex, height})))
        .filter(location => isLocalMinimum(caveSystem, location)) // Keep only the local minimas
        .map(({height}) => height)
        .reduce((a, b) => a + b + 1, 0); // Sum up the result

    console.log("Part 1: " + result);
}

function isLocalMinimum(caveSystem, {row, col}) {
    // Won't you be my neighbor?
    const neighbors = [
        (caveSystem[row] ?? [])[col-1] ?? 9,
        (caveSystem[row] ?? [])[col+1] ?? 9,
        (caveSystem[row-1] ?? [])[col] ?? 9,
        (caveSystem[row+1] ?? [])[col] ?? 9,
    ];
    return caveSystem[row][col] < Math.min(...neighbors);
}


// ======
// Part 2
// ------

function runPart2() {
    const caveSystem = lines.map(l => l.split('').map(p => parseInt(p, 10)));

    // * UNDERWATER FLOOD! *
    // Water has started spewing out of the floor. It started simultaneously at all the lowest 
    // points in the cave (height 0), and once all those basins were full, it started spewing
    // out of non-flooded height 1 locations. This pattern continued until this water-filled
    // cave filled up with water! The horrors!
    let basins = [];
    let remainingPoints = caveSystem
        .flatMap((row, rowIndex) => row.map((height, colIndex) => ({row: rowIndex, col: colIndex, height})))
        .filter(({height}) => height !== 9);
    for (let basinLowPoint = 0; basinLowPoint < 9; basinLowPoint++) {
        // Find and record all the newly flooded basins
        let newBasins = remainingPoints.filter(({height}) => height === basinLowPoint)
            .map(({row, col}) => getBasinPoints(caveSystem, {row, col}, []));
        basins.push(...newBasins);

        // Update our understanding of the caves by removing all the flooded areas from existence.
        let usedLocations = basins.flatMap(i => i);
        remainingPoints = remainingPoints.filter(({row,col}) => !usedLocations.includes(`${row},${col}`));
    }

    const product = basins.map(b => b.length).sort((a,b) => b-a).slice(0,3).reduce((a,b) => a*b);
    console.log("Part 2: " + product);
}

function getBasinPoints(caveSystem, thisLocation, locationsSoFar) {
    let {row, col} = thisLocation;
    // If it's a "9" or edge of map, then exit.
    if (((caveSystem[row] ?? [])[col] ?? 9) === 9) return [];
    // If it's already been checked, then exit.
    if (locationsSoFar.includes(`${row},${col}`)) return [];

    // Otherwise, add it and check its neighbors!
    let newPoints = [];
    newPoints.push(`${row},${col}`);
    newPoints.push(...getBasinPoints(caveSystem, {row: row-1, col: col}, [...locationsSoFar, ...newPoints]));
    newPoints.push(...getBasinPoints(caveSystem, {row: row+1, col: col}, [...locationsSoFar, ...newPoints]));
    newPoints.push(...getBasinPoints(caveSystem, {row: row, col: col-1}, [...locationsSoFar, ...newPoints]));
    newPoints.push(...getBasinPoints(caveSystem, {row: row, col: col+1}, [...locationsSoFar, ...newPoints]));

    return newPoints;
}