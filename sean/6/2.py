# Thatsalottafishies.

fish_counts = [0] * 9
with open('input.txt') as input_file:
    for fish_age in input_file.readline().split(','):
        fish_counts[int(fish_age)] += 1

for _ in range(256):
    expecting_count = fish_counts.pop(0)
    fish_counts[6] += expecting_count  # Fish-mommies beginning maternity leave
    fish_counts.append(expecting_count)  # New babies

print(sum(fish_counts))
