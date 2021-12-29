# Let's make a class, just for funsies

import math

class SillyString:  # Hey, I get to name it
    """Provides streaming read operations on a string of bits converted from a hex string from the given input file."""
    def __init__(self, file_name, part):
        with open(file_name) as input_file:
            self.content = format(int(input_file.readline().rstrip(), 16), 'b')
        self.part = part
        self.seek_pos = 0

    def read_str(self, n):
        """Read the next n bits into a string."""
        self.seek_pos += n
        return self.content[self.seek_pos - n:self.seek_pos]

    def read_int(self, n):
        """Read the next n bits into an int."""
        return int(self.read_str(n), 2)

    def read_literal_value(self):
        """Read a literal value (one or more groups of four bits, each prefixed with 1 for not-last or 0 for last)."""
        literal_str = ''
        while self.read_str(1) == '1':
            literal_str += self.read_str(4)
        literal_str += self.read_str(4)
        return int(literal_str, 2)

    def read_packet(self):
        """Read a packet and return its value."""
        # Header (VVVTTT: V = version, T = type)
        version_number = self.read_int(3)
        type_id = self.read_int(3)

        if type_id == 4:
            literal_value = self.read_literal_value()
            if   self.part == 1: packet_value = version_number
            elif self.part == 2: packet_value = literal_value
        else:  # Operator
            subpacket_values = []
            length_type_id = self.read_int(1)
            if length_type_id == 0:
                total_length_of_subpackets = self.read_int(15)
                end_of_subpackets = self.seek_pos + total_length_of_subpackets
                while self.seek_pos < end_of_subpackets:
                    subpacket_values.append(self.read_packet())
            else:
                number_of_subpackets = self.read_int(11)
                for _ in range(number_of_subpackets):
                    subpacket_values.append(self.read_packet())

            if self.part == 1:
                packet_value = version_number + sum(subpacket_values)
            elif self.part == 2:
                if   type_id == 0: packet_value = sum(subpacket_values)
                elif type_id == 1: packet_value = math.prod(subpacket_values)
                elif type_id == 2: packet_value = min(subpacket_values)
                elif type_id == 3: packet_value = max(subpacket_values)
                elif type_id == 5: packet_value = int(subpacket_values[0] > subpacket_values[1])
                elif type_id == 6: packet_value = int(subpacket_values[0] < subpacket_values[1])
                elif type_id == 7: packet_value = int(subpacket_values[0] == subpacket_values[1])

        return packet_value
