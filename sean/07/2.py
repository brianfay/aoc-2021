# Bahhh! My first intuition here was this: "Well, outliers seem significant now, so... Try the mean?"
# And I tried. And tried. The ordinary mean didn't work, so I tried a distance-weighted mean where I modified the
# weights to be proportional to the total distance to all other points... But at that point I was already most of the
# way to brute force--so I threw up my hands, put down my abacus, and picked up my club.
#
# Afterwards, I looked at the subreddit... AND THE MEAN WORKS.
# You just need to check both numbers around it because of math reasons.
# I KNEW IT! (Not really.) I HAD A VAGUE FEELING IT MIGHT BE TRUE! (Okay, sort of.) I'LL TAKE IT! (Fine, jeez.)
#
# For fuel cost, I used the formula for an arithmetic series.
# That's the sum of the first n terms of an arithmetic sequence.
# That's a sequence where the numbers increase by adding a constant value.
# Interestingly, the constant value doesn't matter for computing the sum! Hum dee dum!
# I would like to thank Google for interpreting my words, which were not as mathy as those, as those.
# How cool it would be to actually know math?! Approximatey 3 ↑↑↑↑ 3 cool, I reckon.

import math

with open('input.txt') as input_file:
    positions = [int(num) for num in input_file.readline().split(',')]

def fuel_distance(a, b):
    return abs(a - b) * (1 + abs(a - b)) / 2  # Arithmetic series

mean = sum(positions) / len(positions)
positions_to_check = (math.floor(mean), math.ceil(mean))
print(min(sum(fuel_distance(start, end) for start in positions) for end in positions_to_check))
