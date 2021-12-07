# "Bah! There's only 2000 or so--let's just check 'em all."--me after failing to find an elegant mathematical solution
#
# So I just checked every possible position between the min and max to see which one had the lowest fuel cost...
#
# At least I got to use the formula for an arithmetic series!
# That's the sum of the first n terms of an arithmetic sequence.
# That's a sequence where the numbers increase by adding a constant value.
# Interestingly, the constant value doesn't matter for computing the sum! Hum dee dum!
# I would like to thank Google for interpreting my words, which were not as mathy as those, as those.
# How cool it would be to actually know math?! Approximatey 3 ↑↑↑↑ 3 cool, I reckon.

with open('input.txt') as input_file:
    positions = [int(num) for num in input_file.readline().split(',')]

def fuel_distance(a, b):
    return abs(a - b) * (1 + abs(a - b)) / 2  # Arithmetic series

print(min(sum(fuel_distance(start, end) for start in positions) for end in range(min(positions), max(positions))))
