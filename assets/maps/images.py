import pyglet
import os

data = {}


class im:
    def __getattr__(self, item):
        if item in data:
            return data[item]
        else:
            print(f"no image \"{item}\"")
            return data["Background"]


images = im()

def centre(image):
    image.anchor_x = image.width // 2
    image.anchor_y = image.height // 2

for e in os.listdir("imageFiles"):
    img = pyglet.image.load("imageFiles/" + e)
    centre(img)
    data[e[:-4]] = img
