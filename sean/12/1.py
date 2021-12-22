# ...Back to Python for now, in the interest of time and my fragile ego

from collections import defaultdict

connections = defaultdict(list)  # A dict of lists, mapping each cave to its neighbors
with open('input.txt') as input_file:
    for line in input_file:
        first, second = line.rstrip().split('-')
        connections[first].append(second)
        connections[second].append(first)

# Start at 'start' and perform a depth-first-search to find all the valid paths to 'end'
def spelunk(current_path: list):
    """Return the number of valid complete paths that are found by extending the given path."""
    global connections

    if current_path[-1] == 'end':
        return 1

    count = 0
    for cave in connections[current_path[-1]]:
        if cave == 'start' or cave.islower() and cave in current_path:
            continue
        count += spelunk(current_path + [cave])
    return count

print(spelunk(['start']))
