with open('input.txt') as input_file:
    h_pos = 0
    depth = 0

    for line in input_file:
        amount = int(line.rstrip()[-1])  # Assume it's always a single-digit integer

        if line[0] == 'f':
            h_pos += amount
        elif line[0] == 'd':
            depth += amount
        elif line[0] == 'u':
            depth -= amount
        else:
            print("wat")

    print(f"{h_pos} * {depth} = {h_pos * depth}")
