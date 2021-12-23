# Prerequisite: PySpark 3 and Java 8 must be installed

# For today, I wanted to practice using RDDs (Resilient Distributed Datasets), which are a Spark data structure that's
# lower level than the Datasets (a.k.a. DataFrames) I used yesterday. (An RDD is basically a single stream of
# unstructured data, while a DataFrame is more like a table with named columns.) In this case, my RDDs are made up of
# 2-tuples in a (key, value) format, which allows for some special operations to be done "by key" (though I don't really
# use this until part 2).

# To this end, I wanted to use an approach that felt more RDD-ish. So instead of cleverly reducing the problem to an
# elegant bit of math like Ben did, I exploded it into a giant mess of brute force. I parsed the input data into a giant
# stream where rows and columns were stored side-by-side, each paired with their board index so I could figure out where
# they came from. Then I broadcast the list of drawn numbers to the entire cluster and played bingo until all 1000 rows
# and columns had won (yeah, way more information that I actually needed). Naturally, I then knew which row or column
# had won first, so then I just had to get all the numbers from that board and basically play through it again to figure
# out which ones were left unmarked. Altogether it was quite silly, but also quite parallelizable, and thus a good deal
# faster than yesterday's solution! The bingo part took about a second to run locally on my MacBook: still much slower
# than just using Python, but hey, it scales like a dragon! ...Theoretically.

#        )_    ,(
#        )_\  /_(    {
#         )_\,--_.V. }  ~~~ ~
#       oOo/  ,---- < ~~~~~~
#       8 |vv|         ~~ ~
#         \vv\
#    ,^^^_/  /
#    `--___-'
#       |_ |_

from pyspark.conf import SparkConf
from pyspark.context import SparkContext

BOARD_SIZE = 5

# Parse the input file into a list of 2-tuples, each containing a board index (int) and a row or column (list[int])
data = []
with open('input.txt') as input_file:
    drawn_numbers = [int(n) for n in input_file.readline().rstrip().split(',')]
    input_file.readline()  # Skip the first blank line

    board_index = 0
    # Columns need to be built up over several lines, so we keep a "working" (in-progress) list of them
    working_cols = None
    for line in input_file:
        if line.strip() == "":  # Blank line
            # Add the finished columns and reset the working list
            data.extend(working_cols)
            working_cols = None
            # On to the next board
            board_index += 1
            continue

        row_tuple = (board_index, [int(n) for n in line.split()])
        data.append(row_tuple)
        if working_cols is None:
            working_cols = [(board_index, []) for _ in range(BOARD_SIZE)]
        # Spread the row values out onto the working columns 
        for index, value in enumerate(row_tuple[1]):
            working_cols[index][1].append(value)

    if working_cols is not None:
        # Add the finished columns for the last board (phew, close one)
        data.extend(working_cols)

# Initialize Spark and load the data
sc = SparkContext(conf=SparkConf().setAppName('aoc-2021-4.1').setMaster('local'))
broadcast_numbers = sc.broadcast(drawn_numbers)
rdd = sc.parallelize(data).cache()

def play(data_tuple):
    """Play through the drawn numbers and return a tuple containing the board index and the index of the number that won
    or negative one if none won... honey bun. Format: (board_index, winning_number_index)"""
    board_index, data_row = data_tuple
    for number in broadcast_numbers.value:
        if number in data_row:
            data_row.remove(number)  # Assume there are no duplicates in a single row/col
            if len(data_row) == 0:
                return (board_index, broadcast_numbers.value.index(number))
    return (board_index, len(broadcast_numbers.value))  # Use an impossibly-high index to indicate a loss

# Play through the numbers and find the winner (i.e. the lowest winning number index)
winning_board_index, winning_number_index = rdd.map(play).min(lambda pair: pair[1])

# Select the winning board's data
winning_board_rdd = rdd.filter(lambda data_tuple: data_tuple[0] == winning_board_index)
# Flatten the rows and columns into one list
winning_board_rdd = winning_board_rdd.flatMap(lambda data_tuple: data_tuple[1])
# Keep only unique numbers (assuming no board has duplicates)
winning_board_rdd = winning_board_rdd.distinct()
# Remove the called numbers up to and including the winning one
winning_board_rdd = winning_board_rdd.filter(lambda num: num not in broadcast_numbers.value[:winning_number_index + 1])
# Sum up the rest
unmarked_sum = winning_board_rdd.sum()

print(f"{unmarked_sum} * {drawn_numbers[winning_number_index]} = {unmarked_sum * drawn_numbers[winning_number_index]}")
