# "Bracket" is such an ugly word...
OPEN_SQUIGGLES = ('(', '[', '{', '<')
SQUIGGLE_MAP = {'(': ')', '[': ']', '{': '}', '<': '>'}
SCORE_MAP = {')': 3, ']': 57, '}': 1197, '>': 25137}

score = 0
squiggle_stack = []
with open('input.txt') as input_file:
    for line in input_file:
        for squiggle in line.rstrip():
            if squiggle in OPEN_SQUIGGLES:
                squiggle_stack.append(squiggle)
            elif SQUIGGLE_MAP[squiggle_stack.pop()] != squiggle:
                score += SCORE_MAP[squiggle]
                break

print(score)
