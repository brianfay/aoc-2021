# ♪ deque the halls with needless imports, ', '.join([' '.join(['fa'] + ['la'] * 4)] + [' '.join(['la'] * 4)]) ♪
from collections import deque

# "Bracket" is such an ugly word...
OPEN_SQUIGGLES = ('(', '[', '{', '<')
SQUIGGLE_MAP = {'(': ')', '[': ']', '{': '}', '<': '>'}
SCORE_MAP = {')': 1, ']': 2, '}': 3, '>': 4}

scores = []
squiggle_stack = deque()
with open('input.txt') as input_file:
    for line in input_file:
        skip_line = False
        squiggle_stack.clear()
        for squiggle in line.rstrip():
            if squiggle in OPEN_SQUIGGLES:
                squiggle_stack.append(squiggle)
            elif SQUIGGLE_MAP[squiggle_stack.pop()] != squiggle:
                skip_line = True
                break
        if skip_line: continue
        score = 0
        for open_squiggle in reversed(squiggle_stack):
            score *= 5
            score += SCORE_MAP[SQUIGGLE_MAP[open_squiggle]]
        scores.append(score)

print(sorted(scores)[len(scores) // 2])
