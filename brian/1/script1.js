const fs = require('fs')

try {
  const data = fs.readFileSync('../input.txt', 'utf8')
  const arr = data.split('\n')
  let numIncreases = 0;
  let prevVal;
  for (e of arr) {
    const newVal = parseInt(e);
    if (newVal > prevVal) {
      numIncreases += 1;
    }
    prevVal = newVal;
  }
  console.log(`number of increases: ${numIncreases}`)
} catch (err) {
  console.error(err)
}
