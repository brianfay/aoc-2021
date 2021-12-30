# I'm going for speed here, so it's full of icky assumptions and brutishness.
# Perhaps one day I'll return with my high school math notes and do it properly.

with open("input.txt") as input_file:
    (x_min, x_max), (y_min, y_max) = [
        [int(num) for num in coords[2:].split('..')]
        for coords in input_file.readline().rstrip().split(': ')[-1].split(', ')
    ]

# Assuming a sharp upward angle is the way to go, let's try all x values between 1 and x_max.
# For each of those, we'll try y values until it passes x_max without dropping below y_max.

def fire(v_x, v_y):
    """Given initial x and y velocities, return the highest height reached or negative infinity for a miss."""
    x = y = 0
    max_height = float('-inf')
    while True:
        # "On each step, these changes occur in the following order:"
        # "The probe's x position increases by its x velocity."
        x += v_x
        # "The probe's y position increases by its y velocity."
        y += v_y
        # "Due to drag, the probe's x velocity changes by 1 toward the value 0."
        if v_x > 0:
            v_x -= 1
        # "Due to gravity, the probe's y velocity decreases by 1."
        v_y -= 1

        max_height = max(y, max_height)

        if x_min <= x <= x_max and y_min <= y <= y_max:
            # We've hit the target area!
            return max_height

        if v_x == 0 and y < y_min:
            # We've missed the target area
            return float('-inf')

max_height = float('-inf')
working_velocities = []
for init_v_x in range(1, x_max + 1):
    # y always seems to come back to 0 just before v_y hits -init_v_y, so use this to set bounds
    # (assuming both min_y and max_y are negative)
    for init_v_y in range(min(y_min, 0), abs(y_min) + 1):
        height = fire(init_v_x, init_v_y)
        if height > float('-inf'):
            working_velocities.append((init_v_x, init_v_y))
        max_height = max(height, max_height)  # max height max height max height

print(f"Max height: {max_height}")
print(f"Number of valid initial velocity values: {len(working_velocities)}")
