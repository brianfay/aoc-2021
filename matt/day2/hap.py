fwd = 0
depth = 0
aim = 0

with open('data.txt') as data:
    for line in data:
        move, amt = line.split()
        
        if move == 'forward':
            fwd += int(amt)
            depth += aim * int(amt)
        elif move == 'up':
            aim -= int(amt)
        else:
            aim += int(amt)

print(fwd * depth)