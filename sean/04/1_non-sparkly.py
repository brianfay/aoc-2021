# My part 1 solution without Spark(les), to show how Python can use the same approach,
# though a different approach could be much more memory-efficient and sensible.

import copy

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

def play(data_tuple):
    """Play through the drawn numbers and return a tuple containing the board index and the index of the number that won
    or negative one if none won... honey bun. Format: (board_index, winning_number_index)"""
    board_index, data_row = data_tuple
    for number in drawn_numbers:
        if number in data_row:
            data_row.remove(number)  # Assume there are no duplicates in a single row/col
            if len(data_row) == 0:
                return (board_index, drawn_numbers.index(number))
    return (board_index, len(drawn_numbers))  # Use an impossibly-high index to indicate a loss

# Play through the numbers and find the winner (i.e. the lowest winning number index)
result_pairs = map(play, copy.deepcopy(data))
winning_board_index, winning_number_index = min(result_pairs, key=lambda pair: pair[1])

# Select the winning board's data
winning_board_data = filter(lambda data_tuple: data_tuple[0] == winning_board_index, data)
# Combine the rows and columns into a set (to remove duplicates)
winning_board_numbers = set()
for data_tuple in winning_board_data:
    for number in data_tuple[1]:
        # Only add the numbers that weren't drawn (unmarked numbers)
        if number not in drawn_numbers[:winning_number_index + 1]:
            winning_board_numbers.add(number)
# Sum up the unmarked numbers
unmarked_sum = sum(winning_board_numbers)

print(f"{unmarked_sum} * {drawn_numbers[winning_number_index]} = {unmarked_sum * drawn_numbers[winning_number_index]}")
