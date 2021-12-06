# No Spark this time! Just a counter and some dirty loops. https://youtu.be/e19f0cWBzdY

fish_counts = {0: 0, 1: 0, 2: 0, 3: 0, 4: 0, 5: 0, 6: 0, 7: 0, 8: 0}  # int keys feel kinda weird but they work here
with open('input.txt') as input_file:
    for fish_age in input_file.readline().split(','):
        fish_counts[int(fish_age)] += 1

for _ in range(80):
    expecting_count = fish_counts[0]
    for i in range(1, 8 + 1):  # Skip 0 and include 8
        fish_counts[i - 1] = fish_counts[i]  # Move all the counts down one day
    fish_counts[6] += expecting_count  # Fish-mommies beginning maternity leave
    fish_counts[8] = expecting_count  # New babies

print(sum(fish_counts.values()))
