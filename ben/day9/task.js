
const lines = require('fs').readFileSync('input', 'UTF-8').split(/\r?\n/);

printBoardWithMinimasHighlighted();
runPart1();
runPart2();


// =================
// Utility Functions
// -----------------

function isLocalMinimum(data, location, lineLength) {
    const neighbors = [
        data[location-1] ?? 9,          // left
        data[location+1] ?? 9,          // right
        data[location-lineLength] ?? 9, // up
        data[location+lineLength] ?? 9  // down
    ];
    return data[location] < Math.min(...neighbors);
}

function printBoardWithMinimasHighlighted() {
    const result = lines
        .flatMap(l => l.split(''))
        .map(p => parseInt(p, 10))
        .reduce((prev, curr, loc, data) => {
            prev.push({num: curr, isMinimum: isLocalMinimum(data, loc, lines[0].length)})
            return prev;
        }, [])
        .map(({num, isMinimum}) => isMinimum ? `\x1b[31m${num}\x1b[0m` : num)
        .reduce((prev, curr) => {
            if (prev[prev.length-1].length === lines[0].length) {
                prev.push([]);
            }
            prev[prev.length-1].push(curr);
            return prev;
        }, [[]])
        .forEach((line) => console.log(line.join('')));
}


// ======
// Part 1
// ------

function runPart1() {
    const result = lines
        .flatMap(l => l.split(''))       // Combine into one mega array
        .map(p => parseInt(p, 10))       // Convert from strings to ints
        .filter((p,loc,data) => isLocalMinimum(data, loc, lines[0].length)) // Keep only the local minimas
        .reduce((a, b) => a + b + 1, 0); // Sum up the result

    // 564 is too low :\
    console.log("Part 1: " + result);
}


// ======
// Part 2
// ------

function runPart2() {
}