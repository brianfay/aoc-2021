import itertools

from snailfish import Pair

input_pairs = []
with open('input.txt') as input_file:
    for line in input_file:
        input_pairs.append(Pair.from_string(line.rstrip()))

max_magnitude = max((x + y).clone().reduce().magnitude() for x, y in itertools.permutations(input_pairs, 2))

print("Largest magnitude from adding any two numbers:", max_magnitude)
