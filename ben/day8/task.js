
const lines = require('fs').readFileSync('input', 'UTF-8').split(/\r?\n/);

runPart1();
runPart2();


// ======
// Part 1
// ------

function runPart1() {
    // Flattened array containing all the output digits.
    const outputDigits = lines.map(l => l.split(' | ')[1]).flatMap(l => l.split(' '));

    // Count up our favorite numbers. The rest are yoosless!
    const countOf1478 = outputDigits.filter(digit => [2, 4, 3, 7].includes(digit.length)).length;

    console.log("Part 1: " + countOf1478);
}


// ======
// Part 2
// ------

function runPart2() {

    const outputs = lines.map(line => {
        // Line segment to possible inbound wire values
        let possibleMappings = 'abcdefg'.split('').reduce((lookupMap, key) => Object.assign(lookupMap, { [key]: 'abcdefg'.split('') }), {});

        const [decoderString, outputString] = line.split(' | ');
        const [decoderArr, outputArr] = [decoderString.split(' '), outputString.split(' ')];

        // Initial pass - trim down the possible mappings using the unique-segment-count digits "1478"
        [...decoderArr, ...outputArr].forEach(digit => {
            if      (digit.length === 2) intersect(possibleMappings, 'cf'.split(''),   digit.split(''));
            else if (digit.length === 4) intersect(possibleMappings, 'bcdf'.split(''), digit.split(''));
            else if (digit.length === 3) intersect(possibleMappings, 'acf'.split(''),  digit.split(''));
            // 8 is useless to check since it's all the segments. It doesn't eliminate anything.
        });

        let changesHappened = true;
        while (changesHappened) {
            const original = JSON.stringify(possibleMappings);

            // If exactly 2 possible mappings have the same 2 possibilities, or
            // if exactly 3 possible mappings have the same 3 possibilities, etc...
            // Then we can eliminate those as possibilities from the others.
            'abcdefg'.split('')
                .map(segment => getMatchingKeys(possibleMappings, segment))
                .filter((val, index, parentArr) => -1 !== parentArr.slice(index+1).findIndex(arr => arraysEqual(arr, val)))
                .forEach(matchingGroup => {
                    if (matchingGroup.length === possibleMappings[matchingGroup[0]].length) {
                        // Eliminate these as possibilities from the rest of the options
                        eliminate(possibleMappings, matchingGroup, possibleMappings[matchingGroup[0]]);
                    }
                });

            // If any segment has an input that appears in no others, it must be the correct value.
            'abcdefg'.split('')
                .map(segment => ({ segment, count: countOccurrences(possibleMappings, segment) }))
                .filter(({segment,count}) => count === 1)
                .forEach(({segment,count}) => eliminate(
                    possibleMappings,
                    Object.keys(possibleMappings).filter(k => arraysEqual(possibleMappings[k], [segment])),
                    [segment]));

            // If any segment has just one value, eliminate that value from the others
            'abcdefg'.split('')
                .filter(segment => possibleMappings[segment].length === 1)
                .forEach(segment => eliminate(possibleMappings, [segment], possibleMappings[segment]));

            // Determine if we need to loop again
            changesHappened = original !== JSON.stringify(possibleMappings);
        }

        // At this point, we need to convert our map to a list of all the possible translations.
        // Then, comparing those possibilities to a list of valid combinations, we should be able
        // to determine the correct mapping.
        function generatePossibilityTree(remainingSegments, prefix) {
            let thisSegment = remainingSegments[0];
            // For all letters in this segment, keep traversing deeper.
            return thisSegment.reduce((results, possibleSegment) => {
                let translations = remainingSegments.length === 1
                    ? [[...prefix, possibleSegment]]
                    : generatePossibilityTree(remainingSegments.slice(1), [...prefix, possibleSegment])
                return [...results, ...translations];
            }, []);
        }
        let allPossibilities = generatePossibilityTree(Object.values(possibleMappings), [])
            // Cut out any that are impossible (ie: multiple wires both mapped to segment 'g')
            .filter(combos => !combos.some((val, index, arr) => -1 !== arr.indexOf(val, index+1)));

        // Translate our mappings to the "normal" mappings to see if any light up the segments in
        // a nonsensical way. (ex: a backwards 7 isn't a valid number.)
        const validNumberSegments = ['abcefg','cf','acdeg','acdfg','bcdf','abdfg','abdefg','acf','abcdefg','abcdfg'];
        let translate = allPossibilities.filter(possibleMapping => {
            // If one of our provided numbers translate to something impossible with this mapping, filter it out.
            return ![...decoderArr, ...outputArr].some(word => {
                const translatedWord = word.split('').map(letter => 'abcdefg'.split('')[possibleMapping.indexOf(letter)])
                return !validNumberSegments.includes(translatedWord.sort().join(''));
            });
        }).map((translationArr) => {
            // Create a translation function that takes a word, and "re-aligns" the wires, returning a string of (sorted) segments.
            // ex: 'acd' (nonsense) => 'bef' (rainbowy goodness)
            return word => word.split('').map(letter => 'abcdefg'.split('')[translationArr.indexOf(letter)]).sort().join('');
        })[0];

        return parseInt(outputArr
            .map(w => translate(w))
            .map(w => validNumberSegments.indexOf(w))
            .join(''), 10);
    });

    let sum = outputs.reduce((a, b) => a + b);
    console.log("Part 2: " + sum);
}

/**
 * For each provided key ('a', 'b', ...), removes any letters from mappings[key]
 * that aren't contained in digitLetters.
 */
function intersect(mappings, keys, digitLetters) {
    keys.forEach(key => {
        mappings[key] = mappings[key].filter(val => digitLetters.includes(val));
    });
}

/**
 * Removes 'keysToEliminate' from all values inside 'mappings' that aren't one of
 * the provided 'safeKeys'.
 */
function eliminate(mappings, safeKeys, keysToEliminate) {
    'abcdefg'.split('')
        .filter(k => !safeKeys.includes(k))
        .forEach(key => {
            mappings[key] = mappings[key].filter(val => !keysToEliminate.includes(val));
        });
}

/**
 * Counts the number of times the given segment letter appears within the provided
 * lookup map.
 */
function countOccurrences(mappings, segment) {
    return Object.values(mappings).flatMap(a => a).filter(a => a === segment).length;
}

/**
 * Returns a list of keys which have the same possible mappings as 'segment' based
 * on the data inside 'mappings'.
 */
function getMatchingKeys(mappings, segment) {
    const possibleMappings = mappings[segment];
    return 'abcdefg'.split('').filter(s => arraysEqual(mappings[s], possibleMappings));
}

/**
 * Returns true if arr1 and arr2 contain the same items. Duh.
 */
function arraysEqual(arr1, arr2) {
    if (arr1.length !== arr2.length)
        return false;
    return !arr1.some(v => !arr2.includes(v));
}

/**
 * Takes a given segment (ex: 'acf' or ['a', 'c', 'f']) and prints it out in a 7-segment display.
 * Amazing.
 */
function printSegment(segmentString) {
    function l(segment) {
        return segmentString.includes(segment) ? segment : '.';
    }
    console.log(' ' + l('a') + l('a') + l('a') + l('a') + ' ');
    console.log(l('b') + '    ' + l('c'));
    console.log(l('b') + '    ' + l('c'));
    console.log(' ' + l('d') + l('d') + l('d') + l('d') + ' ');
    console.log(l('e') + '    ' + l('f'));
    console.log(l('e') + '    ' + l('f'));
    console.log(' ' + l('g') + l('g') + l('g') + l('g') + ' ');
}