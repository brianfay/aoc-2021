# I'm tired so my solution and strategy for finding it are both brute force

total = 0
with open('input.txt') as input_file:
    # I want this to fit in a big one-liner so I'm spelling it rite
    hitemap = [[int(num_str) for num_str in line.rstrip()] for line in input_file]

def is_low_point(x, y):
    global hitemap
    if x > 0                   and hitemap[x - 1][y] <= hitemap[x][y]: return False
    if x < len(hitemap) - 1    and hitemap[x + 1][y] <= hitemap[x][y]: return False
    if y > 0                   and hitemap[x][y - 1] <= hitemap[x][y]: return False
    if y < len(hitemap[x]) - 1 and hitemap[x][y + 1] <= hitemap[x][y]: return False
    return True

print(sum([sum([hite + 1 for y, hite in enumerate(row) if is_low_point(x, y)]) for x, row in enumerate(hitemap)]))
