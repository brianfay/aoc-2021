# On a hunch, I used the median to get a "middle" position that isn't skewed by outliers.
# To my surprise, it actually worked! Full disclosure: I tried mean first...

# But the median can be halfway between two numbers, and what do you do then? Is it really enough just to round?
# This niggling doubt seems to have some truth to it. Reading the mathy comments on the subreddit made me realize I had
# to check both positions around the mean for part 2, which I'm guessing also applies to the median for part 1.
# So hopefully this now works for every input! (But it's still just a guess, to be honest.)

import math, statistics  # WOOoOo gettin' mathy!

with open('input.txt') as input_file:
    positions = [int(num) for num in input_file.readline().split(',')]

median = statistics.median(positions)
positions_to_check = (math.floor(median), math.ceil(median))
print(min(sum(abs(position - position_to_check) for position in positions) for position_to_check in positions_to_check))
