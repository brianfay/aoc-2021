# Prerequisite: PySpark 3 and Java 8 must be installed

import time

from pyspark.sql import SparkSession
from pyspark.sql.functions import *

# For measuring performance
start_time = time.perf_counter()
last_time = start_time
def time_log(message):
    """Print the given message with the elapsed time since the last call appended."""
    global last_time
    print(f"{message} in {time.perf_counter() - last_time:.6f} s")
    last_time = time.perf_counter()

# Initialize SparkContext and read input file
spark = SparkSession.builder.appName('aoc-2021-3.2t').getOrCreate()
time_log("Initialized SparkContext")
data = spark.read.text('input.txt')
time_log("Read in data")

# Split data into one column per bit
data_as_arrays = data.select(split(data.value, ''))
array_len = len(data_as_arrays.first()[0])  # Assume they're all the same length
data = data_as_arrays.select(*(data_as_arrays[0].getItem(i) for i in range(array_len)))
time_log("Split data")
data.cache()
time_log("Cached split data")

o2_data = co2_data = data
o2_count = co2_count = data.count()
for col_name in data.columns:
    # Get the most common bit from this column, using 1 in case of a tie
    most_common_o2_bit = o2_data.groupBy(col_name).count().orderBy(desc('count'), desc(col_name)).first()[0]
    most_common_co2_bit = co2_data.groupBy(col_name).count().orderBy(desc('count'), desc(col_name)).first()[0]
    time_log("Found most common bit")

    # Filter rows accordingly
    if o2_count > 1:
        o2_data = o2_data.filter(data[col_name] == most_common_o2_bit)
        o2_count = o2_data.count()
        time_log("Filtered and counted o2 rows")
    if co2_count > 1:
        co2_data = co2_data.filter(data[col_name] != most_common_co2_bit)
        co2_count = co2_data.count()
        time_log("Filtered and counted co2 rows")

    if o2_count == co2_count == 1:
        break

# Concatenate columns to get binary strings
o2_string = o2_data.select(concat(*(col(col_name) for col_name in o2_data.columns))).first()[0]
co2_string = co2_data.select(concat(*(col(col_name) for col_name in co2_data.columns))).first()[0]

time_log("Concatenated columns")

# Convert from binary strings to ints
o2_rating = int(o2_string, 2)
co2_rating = int(co2_string, 2)

print(f"Total elapsed time: {time.perf_counter() - start_time} s")

print(f"{o2_rating} * {co2_rating} = {o2_rating * co2_rating}")
