with open('inputs/1.txt') as input_file:
    prev_num = float('inf')
    increase_count = 0

    for line in input_file:
        current_num = float(line)
        if current_num > prev_num:
            increase_count += 1
        prev_num = current_num

    print(increase_count)

