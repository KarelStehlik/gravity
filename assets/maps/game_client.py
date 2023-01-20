from imports import *
import groups
import client_utility
import images
import constants


class Game:

    def __init__(self, mapname, batch):
        self.mapname = mapname
        self.batch = batch
        self.background = pyglet.sprite.Sprite(images.data[mapname], x=constants.SCREEN_WIDTH / 2,
                                               y=constants.SCREEN_HEIGHT / 2, batch=batch, group=groups.g[0])
        self.background.scale_y = constants.SCREEN_HEIGHT / self.background.height
        self.background.scale_x = constants.SCREEN_WIDTH / self.background.width
        self.UI_toolbars = []
        self.mousex, self.mousey = 0, 0
        self.key_press_detectors = []
        self.mouse_click_detectors = []
        self.mouse_move_detectors = []
        self.drawables = []
        self.nodes = []

    def tick(self):
        [e.graphics_update(1 / 60) for e in self.drawables]
        self.batch.draw()

    def mouse_move(self, x, y, dx, dy):
        [e.mouse_move(x, y) for e in self.UI_toolbars]
        [e.mouse_move(x, y) for e in self.mouse_move_detectors]
        self.mousex, self.mousey = x, y

    def mouse_drag(self, x, y, dx, dy, button, modifiers):
        [e.mouse_drag(x, y) for e in self.UI_toolbars]
        [e.mouse_drag(x, y) for e in self.mouse_move_detectors]

    def key_press(self, symbol, modifiers):
        [e.key_press(symbol, modifiers) for e in self.key_press_detectors]
        if symbol == 65293:
            self.save()
        if symbol == key.DELETE:
            self.nodes[-1].delete()

    def key_release(self, symbol, modifiers):
        [e.key_release(symbol, modifiers) for e in self.key_press_detectors]

    def mouse_press(self, x, y, button, modifiers):
        if True in (e.mouse_click(x, y, button, modifiers) for e in self.mouse_click_detectors):
            return
        TrackNode(x, y, len(self.nodes), self)

    def mouse_release(self, x, y, button, modifiers):
        [e.mouse_release(x, y, button, modifiers) for e in self.mouse_click_detectors]

    def mouse_scroll(self, x, y, scroll_x, scroll_y):
        pass

    def save(self):
        text = self.mapname
        for n in self.nodes:
            text += f" {n.x},{n.y}"
        with open("output.txt", "a") as file:
            file.write(text + "\n")

        from PIL import Image
        import math
        bg = Image.open(f"imageFiles/{self.mapname}.png").resize((1920, 1080))
        width = 64
        track = Image.open("imageFiles/Track.png").resize((width, width))
        result = Image.new("RGB", (1920, 1080), (255,) * 3)
        result.paste(bg, (0, 0))
        point = [self.nodes[0].x, self.nodes[0].y]
        for n in self.nodes[1::]:
            direction = get_rotation(n.x - point[0], n.y - point[1])
            speed = 5
            direction = (speed * math.cos(direction), speed * math.sin(direction))
            for _ in range(int(distance_squared(*point, n.x, n.y)**.5 // speed)):
                point[0] += direction[0]
                point[1] += direction[1]
                result.paste(track, (int(point[0]-width//2), 1080 - int(point[1]+width//2)), track)
            point = [n.x, n.y]
        result.save(f"outputImageFiles/{self.mapname}.png")


class TrackNode:
    size = 100

    def __init__(self, x, y, order, game):
        self.x = x
        self.y = y
        self.sprite = pyglet.sprite.Sprite(images.images.Waypoint, x, y, batch=game.batch, group=groups.g[7])
        self.sprite.scale = self.size / self.sprite.width
        self.id = order
        self.game = game
        game.nodes.append(self)
        game.mouse_move_detectors.append(self)
        game.mouse_click_detectors.append(self)
        game.drawables.append(self)
        self.text = pyglet.text.Label(str(order), x=self.x,
                                      y=self.y, color=(255, 255, 0, 255),
                                      batch=game.batch, group=groups.g[8], font_size=self.size // 2,
                                      anchor_x="center", align="center", anchor_y="center")
        self.selected = 0
        self.selX, self.selY = 0, 0

    def mouse_release(self, x, y, button, modifiers):
        if button == 1:
            self.selected = 0

    def setIndex(self, i):
        self.id = i
        self.text.text = str(i)

    def mouse_click(self, x, y, button, modifiers):
        if distance_squared(x, y, self.x, self.y) < self.size ** 2 / 4:
            if modifiers == 16:  # none
                if button == 1:  # lmb
                    self.selected = 1
                elif button == 4:
                    self.delete()
                    for i in range(len(self.game.nodes)):
                        self.game.nodes[i].setIndex(i)
            elif modifiers == 17:  # shift
                if button == 1:
                    self.delete()
                    for i in range(len(self.game.nodes)):
                        self.game.nodes[i].setIndex(i)
                    TrackNode(self.x, self.y, len(self.game.nodes), self.game)
            elif modifiers == 18:
                if button == 1:
                    self.selected = 2
                    self.selX, self.selY = self.x, self.y
            return True
        return False

    def mouse_drag(self, x, y):
        if self.selected == 1:
            self.x = x
            self.y = y
        elif self.selected == 2:
            self.x = (x - self.selX) * 2 + self.selX
            self.y = (y - self.selY) * 2 + self.selY

    def mouse_move(self, x, y):
        if self.selected == 1:
            self.x = x
            self.y = y
        elif self.selected == 2:
            self.x = (x - self.selX) * 2 + self.selX
            self.y = (y - self.selY) * 2 + self.selY

    def graphics_update(self, dt):
        self.sprite.x, self.sprite.y = self.text.x, self.text.y = self.x, self.y

    def delete(self):
        self.sprite.delete()
        self.text.delete()
        self.game.nodes.remove(self)
        self.game.mouse_move_detectors.remove(self)
        self.game.mouse_click_detectors.remove(self)
        self.game.drawables.remove(self)
