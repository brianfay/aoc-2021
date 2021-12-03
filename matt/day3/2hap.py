bits = []

with open('data.txt') as data:
    for line in data:
        line = line.strip()
        bits.append(line)

def oxy_finder():
    f = True
    oxy = bits
    result = bits
    while f:
        for i in range(12):
            x = 0
            y = 0
            for bit in result:
                if bit[i] == '1':
                    x += 1
                elif bit[i] == '0':
                    y += 1
                else:
                    print('cry1')

            oxy = result
            result = []

            for bit in oxy:
                if x >= y:
                    if bit[i] == '1':
                        result.append(bit)
                elif y > x:
                    if bit[i] == '0':
                        result.append(bit)
                else:
                    print('cry2')

            # print(result)
        
            if len(result) == 1:
                f = False
                print(int(result[0], 2))
                break

def co2_finder():
    f = True
    co2 = bits
    result = bits
    while f:
        for i in range(12):
            x = 0
            y = 0
            for bit in result:
                if bit[i] == '1':
                    x += 1
                elif bit[i] == '0':
                    y += 1
                else:
                    print('cry1')

            co2 = result
            result = []

            for bit in co2:
                if x < y:
                    if bit[i] == '1':
                        result.append(bit)
                elif y <= x:
                    if bit[i] == '0':
                        result.append(bit)
                else:
                    print('cry2')

            # print(result)
        
            if len(result) == 1:
                f = False
                print(int(result[0], 2))
                break

oxy_finder()
co2_finder()
