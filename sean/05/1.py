# Prerequisite: PySpark and Java 8 must be installed

# It's RDDs again for another quick-and-dirty Spark solution.

from pyspark.conf import SparkConf
from pyspark.context import SparkContext

# Parse the input file into a list of coordinate 4-tuples, like this: [(x1, y1, x2, y2), ...]
data = []
with open('input.txt') as input_file:
    for line in input_file:
        [x1, y1], [x2, y2] = [map(int, coord_pair.split(',')) for coord_pair in line.split(' -> ')]
        if x1 == x2 or y1 == y2:  # "Consider only horizontal and vertical lines"
            data.append((x1, y1, x2, y2))

# Initialize Spark and load the data
sc = SparkContext(conf=SparkConf().setAppName('aoc-2021-5.1').setMaster('local'))
rdd = sc.parallelize(data)

def get_step(a, b):
    """Fun fact: This is called a 'sign function' or 'signum function' in mathematics and 'cmp' in Python 2."""
    if b > a:
        return 1
    elif b < a:
        return -1
    return 0

def plot(coord_quadruplet):
    """Convert a line represented by two pairs of coordinates into a list of key-value tuples where the key is a
    pair of coordinates crossed by the line and the value is the number 1. Like this: [((x, y), 1), ...]"""
    x1, y1, x2, y2 = coord_quadruplet
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
