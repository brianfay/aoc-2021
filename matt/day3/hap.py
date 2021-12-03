g = ""
e = ""

bits = {0:[], 1:[], 2:[], 3:[], 4:[], 5:[], 6:[], 7:[], 8:[], 9:[], 10:[], 11:[]}

with open('data.txt') as data:
    for line in data:
        line = line.strip()
        key = 0    
        while key < 12:
            bits[key].append(line[key])
            key += 1
            
for i in bits:
    x = (bits[i].count('1'))
    y = (bits[i].count('0'))
            
    if x > y:
        g = g + "1"
        e = e + "0"
    else:
        g = g + "0"
        e = e + "1"

    

gdec = (int(g, 2))
edec = (int(e, 2))

print(gdec * edec)