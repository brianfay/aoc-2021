
const lines = require('fs').readFileSync('input', 'UTF-8').split(/\r?\n/);

const closer = { '(':')', '{':'}', '[':']', '<':'>' };

runPart1();
runPart2();

/**
 * Inefficiently eliminates adjacent open/close pairs from 'symbols' until no
 * more adjacent pairs exist, and then returns the remaining symbols.
 */
function eliminatePairs(symbols) {
    let changeHappened = true;
    while (changeHappened) {
        changeHappened = false;
        for (let x = 0; x < symbols.length-1; x++) {
            if (closer[symbols[x]] === symbols[x+1]) {
                changeHappened = true;
                symbols.splice(x, 2);
                break;
            }
        }
    }
    return symbols;
}

// ======
// Part 1
// ------

function runPart1() {
    const points = { ')':3, '}':1197, ']':57, '>':25137 };

    const score = lines
        .map(line => eliminatePairs(line.split(''))
            .find(symbol => [')', '}', ']', '>'].includes(symbol)))
        .filter(symbol => symbol !== undefined)
        .map(symbol => points[symbol])
        .reduce((a,b) => a+b);

    console.log(score);
}


// ======
// Part 2
// ------

function runPart2() {
    const points = { ')':1, '}':3, ']':2, '>':4 };

    const scores = lines
        .map(line => eliminatePairs(line.split('')))
        .filter(lineOpening => lineOpening.find(symbol => [')', '}', ']', '>'].includes(symbol)) === undefined)
        .map(lineOpening => lineOpening
            .map(char => points[closer[char]])
            .reverse()
            .reduce((score, curr) => score * 5 + curr, 0))
        .sort((a,b) => a-b);

    console.log(scores[(scores.length-1)/2]);
}
