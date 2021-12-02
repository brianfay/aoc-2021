import queue

# Use a Queue with maxlen 3 and just compare the newest number to the oldest (since the middle two are always the same)

with open('input.txt') as input_file:
    queue = queue.Queue(3)
    increase_count = 0

    for line in input_file:
        newest_num = float(line)
        if queue.full():
            oldest_num = queue.get()
            if newest_num > oldest_num:
                increase_count += 1
        queue.put(newest_num)

    print(increase_count)
