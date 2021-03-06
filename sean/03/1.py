# Prerequisite: PySpark 3 and Java 8 must be installed

# Today I decided to use Spark, both for over-engineering purposes and to help me learn it.
# It's a completely silly solution, breaking up a simple data set into a super-complicated Spark Dataset just to find
# the most common bit in each column, using cluster-computing concepts on a single machine and a 1000-line text file.
# It takes ~11 seconds on my MacBook, which is horrendously slow even accounting for ~3 seconds for Spark to start up.
# But as Ben would say, Spark SQL: it Sparkles! And Spark's cool. Especially Sparksicles.
# This was basically Spark school.

from pyspark.sql import SparkSession
from pyspark.sql.functions import *

# Initialize SparkContext and read input file
spark = SparkSession.builder.appName('aoc-2021-3.1').getOrCreate()
data = spark.read.text('input.txt').cache()

# Split data into one column per bit
data_as_arrays = data.select(split(data.value, ''))
array_len = len(data_as_arrays.first()[0])  # Assume they're all the same length
data = data_as_arrays.select(*(data_as_arrays[0].getItem(i) for i in range(array_len)))

gamma_string = ''
# Append the most common bit from each column, using 1 in case of a tie
for col_name in data.columns:
    gamma_string += data.groupBy(col_name).count().orderBy(desc('count'), desc(col_name)).first()[0]

# Invert the bits
epsilon_string = ''.join('1' if bit == '0' else '0' for bit in gamma_string)

# Convert from binary strings to ints
gamma_rate = int(gamma_string, 2)
epsilon_rate = int(epsilon_string, 2)

print(f"{gamma_rate} * {epsilon_rate} = {gamma_rate * epsilon_rate}")
