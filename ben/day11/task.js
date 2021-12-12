
const splitInput = require('fs').readFileSync('input', 'UTF-8')
    .split(/\r?\n/)
    .map(line => line
        .split('')
        .map(octo => parseInt(octo, 10)));

runPart1();
runPart2();


// ======
// Part 1
// ------

function runPart1() {
    let octoGrid = JSON.parse(JSON.stringify(splitInput)); // "I'm feeling lazy" copy
    let zipzapCount = 0;

    for (let day = 0; day < 100; day++) {
        let stillToFlash = photosynthesize(octoGrid);
        zipzapCount += wakeUpNeighbors(octoGrid, stillToFlash);
        octoGrid = goToSleep(octoGrid);
    }

    console.log("Part 1: " + zipzapCount);
}


// ======
// Part 2
// ------

function runPart2() {
    let octoGrid = JSON.parse(JSON.stringify(splitInput)); // "I'm feeling lazy" copy
    let zipzapCount = 0;

    for (var day = 0; zipzapCount < (octoGrid.length * octoGrid[0].length); day++) {
        let stillToFlash = photosynthesize(octoGrid);
        zipzapCount = wakeUpNeighbors(octoGrid, stillToFlash);
        octoGrid = goToSleep(octoGrid);
    }

    console.log("Part 2: " + day);
}


// =================
// Utility Functions
// -----------------

/**
 * Increments the entire grid by 1, returning an array of any octobuddy
 * coordinates that surpass energy level 9.
 */
function photosynthesize(grid) {
    let flashyBuds = [];
    for (let row = 0; row < grid.length; row++) {
        for (let col = 0; col < grid[row].length; col++) {
            if ((++grid[row][col])*1000 > 9000) // (It's over 9000!)
                flashyBuds.push({row, col});
        }
    }
    return flashyBuds;
}

/**
 * Iterates through the 'stillToFlash' array of octobuddy coordinates,
 * incrementing each of that location's 8 neighbors, and repeats the
 * process for any of those that increment up to energy level 10. Returns
 * the total number of octobuddies that flashed.
 */
function wakeUpNeighbors(grid, stillToFlash) {
    let flashCount = 0;
    while (stillToFlash.length > 0) {
        flashCount++;
        let {row, col} = stillToFlash.shift();
        stillToFlash.push(...incrementNeighbors(grid, row, col));
    }
    return flashCount;
}

/**
 * Resets any flashed octobuddies back down to 0.
 */
function goToSleep(grid) {
    return grid.map(octoRow => (octoRow.map(octoBuddy => octoBuddy > 9 ? 0 : octoBuddy)));
}

/**
 * If the given row/col exists, it gets incremented. If it changes to 10,
 * it gets returned as a 1-element array. If it doesn't change to 10, then
 * an empty array is returned. (I chose an array to make syntax cleaner
 * for the calling function.)
 */
function tryIncrementing(grid, row, col) {
    if (grid[row] && grid[row][col]) {
        grid[row][col]++;
        if (grid[row][col] === 10) {
            return [{row, col}];
        }
    }
    return [];
}

/**
 * Increments the energy level of the 8 neighbors to the provided "row, col".
 * Returns an array of all incremented locations [{row, col}, ...]
 */
function incrementNeighbors(grid, row, col) {
    return [
        ...tryIncrementing(grid, row-1, col-1),
        ...tryIncrementing(grid, row-1, col),
        ...tryIncrementing(grid, row-1, col+1),
        ...tryIncrementing(grid, row,   col-1),
        ...tryIncrementing(grid, row,   col+1),
        ...tryIncrementing(grid, row+1, col-1),
        ...tryIncrementing(grid, row+1, col),
        ...tryIncrementing(grid, row+1, col+1),
    ];
}