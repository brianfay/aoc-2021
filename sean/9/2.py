# Feelin' laaazyyyyy

import functools  # Bring on the func ♬♬♪♬

with open('input.txt') as input_file:
    heightmap = [[int(num_str) for num_str in line.rstrip()] for line in input_file]

def is_low_point(x, y):
    global heightmap
    if x > 0                     and heightmap[x - 1][y] <= heightmap[x][y]: return False
    if x < len(heightmap) - 1    and heightmap[x + 1][y] <= heightmap[x][y]: return False
    if y > 0                     and heightmap[x][y - 1] <= heightmap[x][y]: return False
    if y < len(heightmap[x]) - 1 and heightmap[x][y + 1] <= heightmap[x][y]: return False
    return True

def survey_basin(x, y):
    """Add all the coordinates of the basin containing the given low point to the global coordinates set."""
    global heightmap, coordinates
    coordinates.add((x, y))
    if x > 0 and heightmap[x - 1][y] < 9 and (x - 1, y) not in coordinates: survey_basin(x - 1, y)
    if x < len(heightmap) - 1 and heightmap[x + 1][y] < 9 and (x + 1, y) not in coordinates: survey_basin(x + 1, y)
    if y > 0 and heightmap[x][y - 1] < 9 and (x, y - 1) not in coordinates: survey_basin(x, y - 1)
    if y < len(heightmap[x]) - 1 and heightmap[x][y + 1] < 9 and (x, y + 1) not in coordinates: survey_basin(x, y + 1)
    return coordinates

basin_sizes = []
for x in range(len(heightmap)):
    for y in range(len(heightmap[x])):
       if is_low_point(x, y):
           coordinates = set()
           basin_sizes.append(len(survey_basin(x, y)))

print(functools.reduce(lambda a, b: a * b, sorted(basin_sizes)[-3:]))
