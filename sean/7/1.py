# On a hunch, I used the median to get a "middle" position that isn't skewed by outliers.
# To my surprise, it actually worked! Full disclosure: I tried mean first...

import statistics

with open('input.txt') as input_file:
    positions = list(map(int, input_file.readline().split(',')))

median_position = round(statistics.median(positions))
print(sum(abs(position - median_position) for position in positions))
