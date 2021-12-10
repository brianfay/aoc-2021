# Prerequisite: PySpark and Java 8 must be installed

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
sc = SparkContext(conf=SparkConf().setAppName('aoc-2021-4.2').setMaster('local'))
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

# Play through the numbers and find the last winner (i.e. the highest local minimum of the winning number index)
winning_board_index, winning_number_index = rdd.map(play).reduceByKey(min).max(lambda pair: pair[1])

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
