from snailfish import Pair

input_pairs = []
with open('input.txt') as input_file:
    for line in input_file:
        input_pairs.append(Pair.from_string(line.rstrip()))

total_sum = input_pairs[0]
for i in range(1, len(input_pairs)):
    total_sum += input_pairs[i]
    total_sum.reduce()

print("Magnitude of total sum:", total_sum.magnitude())
