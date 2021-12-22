from collections import defaultdict

connections = defaultdict(list)  # A dict of lists, mapping each cave to its neighbors
with open('input.txt') as input_file:
    for line in input_file:
        first, second = line.rstrip().split('-')
        connections[first].append(second)
        connections[second].append(first)

# Start at 'start' and perform a depth-first-search to find all the valid paths to 'end'
def spelunk(current_path: list, visited_a_small_cave_twice: bool):
    """Return the number of valid complete paths that are found by extending the given path."""
    global connections

    if current_path[-1] == 'end':
        return 1

    count = 0
    for cave in connections[current_path[-1]]:
        if cave == 'start':
            continue
        elif cave.islower() and cave in current_path:
            if visited_a_small_cave_twice:
                continue
            else:
                count += spelunk(current_path + [cave], True)
        else:
            count += spelunk(current_path + [cave], visited_a_small_cave_twice)
    return count

print(spelunk(['start'], False))
