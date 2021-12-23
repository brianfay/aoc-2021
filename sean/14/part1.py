from collections import defaultdict

def solve(steps):
    pair_map = {}
    with open('input.txt') as input_file:
        starting_string = input_file.readline().rstrip()
        input_file.readline()  # Skip the blank line

        # Build a mapping from each pair (string) to the two pairs that replace it (tuple[string])
        for line in input_file:
            pair, insert = line.rstrip().split(' -> ')
            pair_map[pair] = (pair[0] + insert, insert + pair[1])

    # Store the number of occurrences of each pair
    pair_counts = defaultdict(int)
    for i in range(1, len(starting_string)):
        pair = starting_string[i - 1] + starting_string[i]
        pair_counts[pair] += 1

    # Grow the polymer by increasing the pair counts
    for _ in range(steps):
        for pair, count in list(pair_counts.items()):
            if count > 0:
                pair_counts[pair] -= count
                for result_pair in pair_map[pair]:
                    pair_counts[result_pair] += count

    # Extract the letter counts from the pair counts
    letter_counts = defaultdict(int)
    for pair, count in pair_counts.items():
        for letter in pair:
            letter_counts[letter] += count

    # Insight from Bee: All letters are double-counted except the first and last
    letter_counts[starting_string[0]] += 1
    letter_counts[starting_string[-1]] += 1
    for letter in letter_counts.keys():
        letter_counts[letter] //= 2

    print(max(letter_counts.values()) - min(letter_counts.values()))

if __name__ == '__main__':
    solve(10)
