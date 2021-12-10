# So... just count the letters?
LENGTHS_TO_COUNT = (2, 3, 4, 7)  # 2 -> 1, 3 -> 7, 4 -> 4, 7 -> 8
count = 0
with open('input.txt') as input_file:
    for line in input_file:
        for word in line.split('|')[1].split():
            if len(word) in LENGTHS_TO_COUNT:
                count += 1
print(count)
