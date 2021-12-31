# This smells like a binary tree kind of situation... and snailfish. 🎄🐌🐟🎄
# It's also my first use of the walrus operator! :=
# It's also my first solution to break 100 lines of code... >:=
# The explode method in particular really exploded.

import re, copy

class Pair:
    """A snailfish number. Contains two items, each of which can be a regular number (int) or another Pair."""
    @staticmethod
    def from_string(pair_string):
        """Return a Pair representing the snailfish number represented by the given string."""
        pair_string = pair_string[1:-1]  # Remove surrounding brackets

        for position in ['left', 'right']:
            if pair_string[0] == '[':
                # Found an inner pair, so find the end and parse it
                bracket_stack = []
                for index, character in enumerate(pair_string):
                    if character == '[':
                        bracket_stack.append(character) # Bracket? Stack it.
                    elif character == ']':
                        bracket_stack.pop()
                        if len(bracket_stack) == 0:
                            # We found the end of this pair, so parse it
                            if position == 'left':
                                left = Pair.from_string(pair_string[:index + 1])
                                # Skip over this pair and the comma so we're ready to parse the right item
                                pair_string = pair_string[index + 2:]
                                break
                            else:  # position == 'right'
                                right = Pair.from_string(pair_string[:index + 1])
            else:
                # Found a regular number, so parse it
                match = re.search("\d+", pair_string)
                if position == 'left':
                    left = int(match[0])
                    # Skip over the number and the comma so we're ready to parse the right item
                    pair_string = pair_string[match.end() + 1:]
                else:  # position == 'right'
                    right = int(match[0])

        return Pair(left, right)

    def __init__(self, left, right):
        self.up = None  # To be changed later if added to a parent Pair
        self.left = left
        self.right = right

        if isinstance(left, Pair):
            left.up = self
        if isinstance(right, Pair):
            right.up = self

    def __add__(self, other):
        """Override the + operator to do addition the snailfish way."""
        return Pair(self, other)

    def __str__(self):  # This one's for debugging purposes
        """Return a string representation of this Pair (e.g. [[1,2],3])."""
        return f"[{str(self.left)},{str(self.right)}]"

    def __repr__(self):  # This one's just for fun
        """Return a valid Python expression that could be used to recreate this Pair."""
        return f"Pair({repr(self.left)}, {repr(self.right)})"

    def clone(self):
        """Return a deep copy of this Pair."""
        return copy.deepcopy(self)

    def reduce(self):
        """Reduce this Pair in place, as must always be done with snailfish numbers, and also return it."""
        while (pair_to_explode := self.find_pair_to_explode()) is not None \
                or (pair_to_split := self.find_pair_to_split()) is not None:
            if pair_to_explode is not None:
               pair_to_explode.explode()
            elif pair_to_split is not None:
               pair_to_split.split()
        return self

    def find_pair_to_explode(self, level=0):
        """Find and return the leftmost Pair within this one that is ready to explode, or None if none are."""
        if level == 4:
            return self
        for child in [self.left, self.right]:
            if isinstance(child, Pair) and (pair_to_explode := child.find_pair_to_explode(level + 1)) is not None:
                return pair_to_explode
        return None

    def find_pair_to_split(self):
        """Find and return the Pair containing the leftmost regular number within this Pair that is ready to split,
        or None if none are."""
        for child in [self.left, self.right]:
            if isinstance(child, Pair):
                if (pair_to_split := child.find_pair_to_split()) is not None:
                    return pair_to_split
            elif child >= 10:
                return self
        return None

    def explode(self):
        # "To explode a pair..."
        # "...the pair's left value is added to the first regular number to the left of the exploding pair (if any)..."
        if self.left > 0:
            if not isinstance(self.up.left, Pair):
                # Our immediate neighbor is a regular number, so add to that one
                self.up.left += self.left
            else:
                # Otherwise, we search for more distant neighbors
                pair = self
                parent = pair.up

                # Climb up until we find a neighbor to the left, then add our left number to its rightmost number
                while True:
                    if parent.left != pair:
                        # Found a neighbor to the left
                        if not isinstance(parent.left, Pair):
                            # It's a regular number, so add to that one
                            parent.left += self.left
                            break
                        else:
                            # It's a pair, so find its rightmost number and add to that one
                            pair = parent.left
                            while isinstance(pair.right, Pair):
                                pair = pair.right
                            pair.right += self.left
                            break
                    else:
                        if parent.up is None:
                            break  # We hit the top, so stop searching
                        # Keep going up
                        pair = parent
                        parent = pair.up

        # "...the pair's left value is added to the first regular number to the left of the exploding pair (if any)..."
        # Basically, reverse the lefts and rights in the above code
        if self.right > 0:
            if not isinstance(self.up.right, Pair):
                self.up.right += self.right
            else:
                pair = self
                parent = pair.up
                while True:
                    if parent.right != pair:
                        if not isinstance(parent.right, Pair):
                            parent.right += self.right
                            break
                        else:
                            pair = parent.right
                            while isinstance(pair.left, Pair):
                                pair = pair.left
                            pair.left += self.right
                            break
                    else:
                        if parent.up is None:
                            break
                        pair = parent
                        parent = pair.up

        # "Then, the entire exploding pair is replaced with the regular number 0."
        if self.up.left == self:
            self.up.left = 0
        else:
            self.up.right = 0

    def split(self):
        """Split the leftmost number in this Pair that is ready to split. Assume one of this Pair's items is a
        regular number ready to split."""
        # "To split a regular number, replace it with a pair;
        # the left element of the pair should be the regular number divided by two and rounded down,
        # while the right element of the pair should be the regular number divided by two and rounded up."
        if not isinstance(self.left, Pair) and self.left >= 10:
            self.left = Pair(self.left // 2, -(self.left // -2))
            self.left.up = self
        elif not isinstance(self.right, Pair) and self.right >= 10:
            self.right = Pair(self.right // 2, -(self.right // -2))
            self.right.up = self

    def magnitude(self):
        """Compute and return the magnitude of this pair."""
        if isinstance(self.left, Pair):
            left_magnitude = self.left.magnitude()
        else:
            left_magnitude = self.left

        if isinstance(self.right, Pair):
            right_magnitude = self.right.magnitude()
        else:
            right_magnitude = self.right

        return 3 * left_magnitude + 2 * right_magnitude
