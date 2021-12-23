# Prerequisite: matplotlib 3 needs to be installed
from matplotlib import pyplot

def solve(part):
    with open('input.txt') as input_file:
        # Store coordinates as a set of (x, y) tuples so duplicates aren't kept
        coords = set()
        for line in input_file:
            if line == '\n': break  # Stop upon reaching a blank line
            x, y = [int(num) for num in line.rstrip().split(',')]
            coords.add((x, y))

        # Perform the fold(s) by replacing coordinates with their folded-over counterparts
        for line in input_file:
            axis, value = line.rstrip().rsplit(' ', 1)[-1].split('=')
            value = int(value)

            for x, y in list(coords):
                if axis == 'x' and x > value:
                    coords.remove((x, y))
                    coords.add((value - (x - value), y))
                elif axis == 'y' and y > value:
                    coords.remove((x, y))
                    coords.add((x, value - (y - value)))

            if part == 1:
                return len(coords)

    if part == 2:
        # Idea from Bee: Just plot 'em!
        x_values, y_values = zip(*coords)
        _, axes = pyplot.subplots(figsize=(5, 0.6))
        axes.scatter(x_values, y_values)
        axes.set_ylim(max(y_values) + 1, 0 - 1)  # Reverse the y-axis and add some padding
        pyplot.show()

if __name__ == "__main__":
    print(solve(1))
