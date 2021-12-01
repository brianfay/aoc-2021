const fs = require('fs')

try {
  const data = fs.readFileSync('../input.txt', 'utf8')
  const arr = data.split('\n')
  let numIncreases = 0

  const windows = []
  for (let idx = 0; idx <= arr.length; idx++) {
    const newVal = parseInt(arr[idx])
    const nextVal = parseInt(arr[idx+1])
    const nextNextVal = parseInt(arr[idx+2])
    windows.push(newVal + nextVal + nextNextVal)
  }

  let prevVal
  for (w of windows) {
    if (w > prevVal) {
      numIncreases += 1;
    }
    prevVal = w;
  }
  console.log(`number of increases: ${numIncreases}`)
} catch (err) {
  console.error(err)
}
