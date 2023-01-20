from PIL import Image
import os

TEXSIZE = 4096


class Animation:  # contains images that may be swapped between frequently. this must be saved in one texture.
    def __init__(self):
        self.images = []
        self.size = 0

    def add(self, image):
        self.images.append(image)
        self.size += image.pixelCount


class TestSetup:
    def __init__(self):
        self.POIs = [(0, 0)]
        self.covered = []
        self.images = []
        self.imageLocations = []
        self.spaceLeft = TEXSIZE ** 2

    def become_copy(self, original):
        self.POIs = [e for e in original.POIs]
        self.covered = [e for e in original.covered]
        self.images = [e for e in original.images]
        self.imageLocations = [e for e in original.imageLocations]
        self.spaceLeft = original.spaceLeft
        return self

    def can_add(self, left, bottom, width, height):
        if bottom + height > TEXSIZE or left + width > TEXSIZE:
            if not self.images:
                print("Error: could not fit an image on a single texture.")
                os._exit(0)
            return False
        for e in self.covered:
            if left < e[0] + e[2] and left + width > e[0] and bottom < e[1] + e[3] and bottom + height > e[1]:
                return False
        return True

    def findLeftIntersect(self, right, y1, y2):
        r = 0
        for e in self.covered:
            if right >= e[0] + e[2] >r and y1 < e[1] + e[3] and y2> e[1]:
                r = e[0] + e[2]
        return r

    def findTopIntersect(self, bottom, x1, x2):
        r = 0
        for e in self.covered:
            if x1 < e[0] + e[2] and x2 > e[0] and bottom >= e[1] + e[3] > r:
                r = e[1] + e[3]
        return r

    def fallDown(self, x, y, w, h):
        rx, ry = 0,0
        for e in self.covered:
            if x >= e[0] + e[2] >rx and y < e[1] + e[3] and y+h> e[1]:
                rx = e[0] + e[2]
            if x < e[0] + e[2] and x+w > e[0] and y >= e[1] + e[3] > ry:
                ry = e[1] + e[3]
        return (rx, ry)

    def add(self, e, poi):
        loc = self.fallDown(*poi, *e.size)

        a = (loc[0], loc[1] + e.size[1])
        b = (loc[0] + e.size[0], loc[1])
        self.POIs.append(a)
        self.POIs.append(b)
        self.POIs.remove(poi)
        self.POIs.sort(key=lambda p: p[0] + p[1])
        self.covered.append((*loc, *e.size))
        self.images.append(e)
        self.imageLocations.append(loc)
        self.spaceLeft -= e.pixelCount

    def try_add_animation(self, anim):
        # if the entirety of anim fits in this, it is added and True is returned. else, False is returned.
        if anim.size > self.spaceLeft:
            return False  # no point in even trying
        new = TestSetup().become_copy(self)
        for img in anim.images:
            for poi in new.POIs:
                if new.can_add(*poi, *img.size):
                    new.add(img, poi)
                    break
            else:
                if not self.images:
                    print("Error: could not fit these images on a single texture.")
                    print([f"{e.name}, size {e.size}" for e in anim.images])
                    os._exit(0)
                return False
        self.become_copy(new)
        return True

    def save(self):
        new = Image.new('RGBA', (TEXSIZE, TEXSIZE), (255,255,255,255))
        text = ""

        for i in range(len(self.images)):
            poi = self.imageLocations[i]
            e = self.images[i]
            new.paste(e, poi)
            b, l, t, r = poi[1] / TEXSIZE, poi[0] / TEXSIZE, (poi[1] + e.size[1]) / TEXSIZE, (
                    poi[0] + e.size[0]) / TEXSIZE

            #b += 1 / TEXSIZE
            #l += 1 / TEXSIZE
            #t -= 1 / TEXSIZE
            #r -= 1 / TEXSIZE
            text += f"\n{e.name}|{r}|{b}|{l}|{t}|{r}|{t}|{l}|{b}"

        new.save("final images/T" + str(n_textures) + ".png")
        file = open(f"image coordinates/T{n_textures}.txt", "w")
        file.write(text[1::])


animations = {}
for e in os.listdir("final images"):
    os.remove(f"final images/{e}")
for e in os.listdir("image coordinates"):
    os.remove(f"image coordinates/{e}")
files = []

def add(fromFolder, e):
    if e.startswith("ANIM_"):
        animName = e.split("_")[1]
        img = Image.open(fromFolder + e)
        img.pixelCount = img.size[0] * img.size[1]
        img.name = e.split("_")[2].split(".")[0]
        if animName not in animations.keys():
            animations[animName] = Animation()
        animations[animName].add(img)
    elif os.path.isdir(fromFolder + e):
        animations[e] = Animation()
        for f in os.listdir(fromFolder + e):
            img = Image.open(fromFolder + e + "/" + f)
            img.name = e + "-" + f.split(".")[0]
            img.pixelCount = img.size[0] * img.size[1]
            animations[e].add(img)
    else:
        img = Image.open(fromFolder + e)
        img.name = e.split(".")[0]
        img.pixelCount = img.size[0] * img.size[1]
        files.append(img)
for e in os.listdir("rawImages"):
    add("rawImages/",e)
for e in os.listdir("fonts"):
    add("fonts/",e)

files.sort(key=lambda image: max(image.size[0], image.size[1]), reverse=True)
n_textures = 0
print(str([e.name for e in files]).replace("\'", "\""))

while files or animations:
    # create a new image
    setup = TestSetup()

    # loop through all standalone textures, see if they fit on the image
    i = 0
    while i < len(files):
        e = files[i]
        for poi in setup.POIs:
            if setup.can_add(*poi, *e.size):
                # it's fine to modify POIs here, because if we do, we break the loop iterating over them
                setup.add(e, poi)
                files.pop(i)
                i -= 1
                break
        i += 1

    # loop through all animations, see if they fit on the image
    for key, value in sorted(animations.items(), key=lambda a: a[1].size)[::-1]:
        if setup.try_add_animation(value):
            animations.pop(key)
    setup.save()
    n_textures += 1
