# Copyright 2019 Vulcalien

# Create a font file (v4) using an image.
# Font type: boolean

from os import path
from PIL import Image

def ask_number(prompt):
    while True:
        try:
            return int(input(prompt))
        except ValueError:
            print('Error: Insert a number\n')

def to1byte(num):
    return num.to_bytes(1, 'big')

def to4bytes(num):
    return num.to_bytes(4, 'big')



print('*** Image to Font ***')

# ask for source
while True:
    src_path = input('Insert Source Image:\n>')

    if(path.isfile(src_path)):
        break
    else:
        print('Error: file does not exist\n')

# ask for destination
while True:
    dest_path = input('Insert Destination File:\n>')

    if(path.exists(dest_path)):
        print('Error: destination file already exists\n')
    else:
        break

while True:
    font_type = ask_number('Insert the type of the font (0 for boolean, 1 for byte)\n')
    if(font_type in (0, 1)):
        break


n_chars = ask_number('Insert number of characters (Default ASCII has 95):\n>')

letter_spacing = ask_number('Insert Letter-Spacing:\n>') & 0xff
line_spacing = ask_number('Insert Line-Spacing:\n>') & 0xff

img = Image.open(src_path).convert('RGB')
font_height = img.height & 0xff

#---WRITE INTO FILE---#
out = open(dest_path, 'wb')

out.write(to1byte(font_type))       # font type - byte

out.write(to4bytes(n_chars))        # chars - int
out.write(to1byte(font_height))     # height - byte

out.write(to1byte(letter_spacing))  # letter-spacing - byte
out.write(to1byte(line_spacing))    # line-spacing - byte

# calculate widths
char_widths = []
last_red = -1

for i in range(img.width):
    pix = img.getpixel((i, 0))
    if(pix == (0xff, 0x00, 0x00)):
        char_widths.append(i - last_red - 1)
        last_red = i

char_widths.append(i - last_red)
del last_red

xOffset = 0
for i in range(n_chars):
    wc = char_widths[i]

    out.write(to1byte(wc))

    # write image pixels
    byteBuffer = 0
    usedBits = 0

    for y in range(font_height):
        for x in range(wc):
            pix = img.getpixel((xOffset + x, y))

            if(font_type == 0): # if boolean
                if(pix == (0x00, 0x00, 0x00)):
                    byteBuffer = byteBuffer | (1 << (7 - usedBits))

                usedBits += 1

                if(usedBits == 8):
                    out.write(to1byte(byteBuffer))

                    byteBuffer = 0
                    usedBits = 0
            elif(font_type == 1): # if byte
                out.write(to1byte(0xff - pix[2]))

    if(font_type == 0 and usedBits != 0): # if boolean, write the last byte with padding bits
        out.write(to1byte(byteBuffer))

    xOffset += wc + 1
