# Parse input:
# line 1 = call_numbers separated by ,
# Matrices separated by blank row

# Store input:
# Bingo Cards: Dictionary of Dictionaires of lists? (card - column - numbers)
# call_numbers: list

# function to x numbers as called

# function to check if card(s) won

# function to add all unmarked numbers on winning board (if card won)
# store unmarked numbers as dictionary of lists (card - numbers)
# sum unmarked numbers in each list
# multiply sum of each list by called number and determine highest score


import collections
            
number_list = []
cards = {}
numbers_called = 0

with open('data.txt') as data:
    # numbers to call
    numbers = data.readline().strip().split(',')
    for number in numbers:
        number = int(number)
        number_list.append(number)

    # bingo cards
    card_number = 0

    for line in data:
        line.strip()
        if line == "\n":
            print('space')
            card_number += 1
        elif line != "\n":
            # parse line
            y = line.rstrip().split(" ")
            while("" in y):
               y.remove("")

            if card_number not in cards:
                cards[card_number] = {}
                cards[card_number]['B'] = [int(y[0])]
                cards[card_number]['I'] = [int(y[1])]
                cards[card_number]['N'] = [int(y[2])]
                cards[card_number]['G'] = [int(y[3])]
                cards[card_number]['O'] = [int(y[4])]

            elif card_number in cards:
                    cards[card_number]['B'].append(int(y[0]))
                    cards[card_number]['I'].append(int(y[1]))
                    cards[card_number]['N'].append(int(y[2]))
                    cards[card_number]['G'].append(int(y[3]))
                    cards[card_number]['O'].append(int(y[4]))
                
        else:
            "crymost"

# print(f'{cards[1]}\n{cards[2]}\n{cards[3]}')
# sample data testing
# number_list = [7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1]

# cardz = {
#     1: {
#         'B': [22,8,21,6,1],
#         'I': [13,2,9,10,12],
#         'N': [17,23,14,3,20],
#         'G': [11,4,16,18,15],
#         'O': [0,24,7,5,19]
#     },
#     2: {
#         'B': [3,9,19,20,14],
#         'I': [15,18,8,11,21],
#         'N': [0,13,7,10,16],
#         'G': [2,17,25,24,12],
#         'O': [22,5,23,4,6]
#     },
#     3: {
#         'B': [14,10,18,22,2],
#         'I': [21,16,8,11,0],
#         'N': [17,15,23,13,12],
#         'G': [24,9,26,6,3],
#         'O': [4,19,20,5,7]
#     }
# }

# print(f'{cardz[1]}\n{cardz[2]}\n{cardz[3]}')
# exit()

def check_number(last_called):
    for card in cards:
        for x in range(5):
            if cards[card]['B'][x] == last_called:
                cards[card]['B'][x] = 'x'
            elif cards[card]['I'][x] == last_called:
                cards[card]['I'][x] = 'x'
            elif cards[card]['N'][x] == last_called:
                cards[card]['N'][x] = 'x'
            elif cards[card]['G'][x] == last_called:
                cards[card]['G'][x] = 'x'
            elif cards[card]['O'][x] == last_called:
                cards[card]['O'][x] = 'x'
            else:
                print('crymore')


def check_winner(last_called):
    winning_scores = []
    winners = False

    for card in cards:
        for y in range(5):

            # check horizontal & vertical win for all cards
            if ((cards[card]['B'][y] == "x" and cards[card]['I'][y] == 'x' and cards[card]['N'][y] == 'x' and cards[card]['G'][y] == 'x' and  cards[card]['O'][y] == 'x')
                or cards[card]['B'][0] == 'x' and cards[card]['B'][1] == 'x' and cards[card]['B'][2] == 'x' and cards[card]['B'][3] == 'x' and cards[card]['B'][4] == 'x'
                or cards[card]['I'][0] == 'x' and cards[card]['I'][1] == 'x' and cards[card]['I'][2] == 'x' and cards[card]['I'][3] == 'x' and cards[card]['I'][4] == 'x'
                or cards[card]['N'][0] == 'x' and cards[card]['N'][1] == 'x' and cards[card]['N'][2] == 'x' and cards[card]['N'][3] == 'x' and cards[card]['N'][4] == 'x'
                or cards[card]['G'][0] == 'x' and cards[card]['G'][1] == 'x' and cards[card]['G'][2] == 'x' and cards[card]['G'][3] == 'x' and cards[card]['G'][4] == 'x'
                or cards[card]['O'][0] == 'x' and cards[card]['O'][1] == 'x' and cards[card]['O'][2] == 'x' and cards[card]['O'][3] == 'x' and cards[card]['O'][4] == 'x'):

                winners = True

                # add all unmarked numbers for this card
                add_number = 0
                for z in range(5):
                    print(cards[card]['O'][z])
                    if cards[card]['B'][z] != 'x':
                        add_number += cards[card]['B'][z]
                    if cards[card]['I'][z] != 'x':
                        add_number += cards[card]['I'][z]
                    if cards[card]['N'][z] != 'x':
                        add_number += cards[card]['N'][z]
                    if cards[card]['G'][z] != 'x':
                        add_number += cards[card]['G'][z]
                    if cards[card]['O'][z] != 'x':
                        add_number += cards[card]['O'][z]
                    
                winning_scores.append(add_number)
                

    # After all cards checked
    if winners:
        print(f'winning scores: {winning_scores} last called: {last_called}')
        final = compare_scores(winning_scores, last_called)
        return final


def compare_scores(winning_scores, last_called):
    final_winning_number = 0

    for score in winning_scores:
        last_score = score
        if last_score:
            if score >= last_score:
                final_winning_number = score
            elif last_score > score:
                final_winning_number = last_score

    return final_winning_number * last_called


for last_called in number_list:
    numbers_called += 1

    check_number(last_called)

    if numbers_called > 4:
        winning = check_winner(last_called)

        if winning:
            print(winning)
            break