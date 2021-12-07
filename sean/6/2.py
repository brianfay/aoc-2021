# Concise-ified a bit from part 1. Thatsalottafishies.

fish_counts = [0] * 9
with open('input.txt') as input_file:
    for fish_age in map(int, input_file.readline().split(',')):
        fish_counts[fish_age] += 1

for _ in range(256):
    fish_counts.append(fish_counts.pop(0)) # New babies to the back
    fish_counts[6] += fish_counts[-1]  # Fish-mommies beginning maternity leave

print(sum(fish_counts))
