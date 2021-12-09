# Wires are identified by letters (a-g). Segments are numbers, like this:
#  0000
# 1    2
# 1    2
#  3333
# 4    5
# 4    5
#  6666

# Unique amount of segments:
# 2 segs -> 1: (_, _, 2, _, _, 5, _)
# 3 segs -> 7: (0, _, 2, _, _, 5, _)
# 4 segs -> 4: (_, 1, 2, 3, _, 5, _)
# 7 segs -> 8: (0, 1, 2, 3, 4, 5, 6)

# 5 segments:
# 2: (0, _, 2, 3, 4, _, 6)
# 3: (0, _, 2, 3, _, 5, 6)
# 5: (0, 1, _, 3, _, 5, 6)

# 6 segments:
# 0: (0, 1, 2, _, 4, 5, 6)
# 6: (0, 1, _, 3, 4, 5, 6)
# 9: (0, 1, 2, 3, _, 5, 6)

# First we identify the numbers with a unique amount of segments, then we use those to narrow down which letters could
# correspond to which segments, and then we deduce the rest from there.

ALL_WIRES = {'a', 'b', 'c', 'd', 'e', 'f', 'g'}
NUMBER_TO_SEGMENTS = [
    {0, 1, 2, 4, 5, 6},     # 0
    {2, 5},                 # 1
    {0, 2, 3, 4, 6},        # 2
    {0, 2, 3, 5, 6},        # 3
    {1, 2, 3, 5},           # 4
    {0, 1, 3, 5, 6},        # 5
    {0, 1, 3, 4, 5, 6},     # 6
    {0, 2, 5},              # 7
    {0, 1, 2, 3, 4, 5, 6},  # 8
    {0, 1, 2, 3, 5, 6}      # 9
]
total_value = 0

# While I'm often on board with prescriptivist pedantry, I gotta say indexes seems like a perfectly fine word to me :)
def update_wire_possibilities(segment_to_wires, pattern_letters, segment_indexes):
    """Update the segments sets knowing only the specified segments can have the letters in the given pattern."""
    non_pattern_letters = ALL_WIRES - pattern_letters

    # Remove the non-pattern letters from the specified segment sets
    for index in segment_indexes:
        segment_to_wires[index] -= non_pattern_letters

    # Remove the pattern letters from other segment sets
    for index in range(len(segment_to_wires)):
        if index not in segment_indexes:
            segment_to_wires[index] -= pattern_letters

with open('input.txt') as input_file:
    for line in input_file:
        # Use sets so we get neato operations like differences and unions
        input_patterns, output_patterns = [[set(pattern) for pattern in group.split()] for group in line.split('|')]
        # This one is a mapping from segment index to a set of wire letters that could correspond to that segment
        segment_to_wires = [ALL_WIRES.copy() for _ in range(7)]  # At first, any wire could correspond to any segment

        for pattern in input_patterns:
            # Python's too cool for switch statements
            if len(pattern) == 2:
                update_wire_possibilities(segment_to_wires, pattern, NUMBER_TO_SEGMENTS[1])
            elif len(pattern) == 3:
                update_wire_possibilities(segment_to_wires, pattern, NUMBER_TO_SEGMENTS[7])
            elif len(pattern) == 4:
                update_wire_possibilities(segment_to_wires, pattern, NUMBER_TO_SEGMENTS[4])
            elif len(pattern) == 7:
                pass # 8 doesn't help us narrow down wires

        # At this point, we've narrowed down the possible-wires-per-segment mapping like this:
        # [{'f'}, {'c', 'b'}, {'g', 'e'}, {'c', 'b'}, {'a', 'd'}, {'g', 'e'}, {'a', 'd'}]
        for pattern in input_patterns:
            if len(pattern) == 5 and segment_to_wires[2] <= pattern:
                # It has both of the wires that could correspond to segments 2 or 5, so it must be 3
                update_wire_possibilities(segment_to_wires, pattern, NUMBER_TO_SEGMENTS[3])
                break

        # Now it looks like this:
        # [{'d'}, {'g'}, {'b', 'e'}, {'c'}, {'a'}, {'b', 'e'}, {'f'}]
        for pattern in input_patterns:
            if len(pattern) == 5 and segment_to_wires[1] <= pattern:
                # It has segment 1 lit, so it must be 5
                update_wire_possibilities(segment_to_wires, pattern, NUMBER_TO_SEGMENTS[5])
                break

        # Now we know the full mapping:
        # [{'d'}, {'g'}, {'b'}, {'c'}, {'a'}, {'e'}, {'f'}]
        output_string = ''  # Use a string to easily concatenate the output numbers
        for pattern in output_patterns:
            # Get this pattern's segments by finding the index of each wire in the segment_to_wires mapping
            segs = set(next(seg for seg, wires in enumerate(segment_to_wires) if letter in wires) for letter in pattern)
            # Get the corresponding number by finding the matching set of segments in the NUMBER_TO_SEGMENTS mapping
            output_string += str(NUMBER_TO_SEGMENTS.index(segs))

        total_value += int(output_string)

print(total_value)

