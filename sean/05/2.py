# Prerequisite: PySpark and Java 8 must be installed

from pyspark.conf import SparkConf
from pyspark.context import SparkContext

# Parse the input file into a list of pairs of pairs of coordinates, like this: [[[x1, y1], [x2, y2]], ...]
with open('input.txt') as input_file:
    # Not taking up half my code THIS time!
    input_coords = [[map(int, coord_pair.split(',')) for coord_pair in line.split(' -> ')] for line in input_file]

# Initialize Spark and load the data
sc = SparkContext(conf=SparkConf().setAppName('aoc-2021-5.2').setMaster('local'))
rdd = sc.parallelize(input_coords)

def get_step(a, b):
    if b > a:
        return 1
    elif b < a:
        return -1
    return 0

def plot(coord_quadruplet):
    """Convert a line represented by two pairs of coordinates into a list of key-value tuples where the key is a
    pair of coordinates crossed by the line and the value is the number 1. Like this: [((x, y), 1), ...]"""
    [x1, y1], [x2, y2] = coord_quadruplet
    result = [((x1, y1), 1)]
    x_step = get_step(x1, x2)
    y_step = get_step(y1, y2)
    x = x1
    y = y1
    while x != x2 or y != y2:
        x += x_step
        y += y_step
        result.append(((x, y), 1))
    return result

# Execute plot(), combine the results by adding the values, and print the number of keys with a value greater than 1
print(rdd.flatMap(plot).reduceByKey(lambda a, b: a + b).filter(lambda pair: pair[1] > 1).count())
