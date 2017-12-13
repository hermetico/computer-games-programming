import sys
import pygame
import numpy as np
import random
import math


RADIUS = 35
TOTAL = 50
W = 640
H = 480

BLACK = 0, 0, 0
WHITE = 255, 255, 255
RED = 255, 0, 0
GRAVITY = np.array((0, -9.1)) * 10
TRANSFORM = np.array((1, -1))

X = 0
Y = 1

FPS = 50
pygame.init()
screen = pygame.display.set_mode((W, H))
#pygame.display.set_caption("Moving Box")

clock = pygame.time.Clock()

PICKING = False
picked = None

circles = []


class Particle(object):
    def __init__(self):
        self.pos = np.zeros(2)
        self.old_pos = np.zeros(2)
        self.acc = np.zeros(2)
        self.radius = random.randint(5, RADIUS)
        self.color = BLACK

    def is_colliding(self, other_pos):
        return self.radius > np.linalg.norm(self.pos - other_pos)

    def accelerate(self, delta):
        self.pos += self.acc * delta * delta
        self.acc = np.zeros(2)

    def inertia(self, delta):
        new_pos = 2 * self.pos - self.old_pos
        self.old_pos = self.pos
        self.pos = new_pos

    def draw(self):
        rounded_pos = np.array(self.pos, dtype=np.int)
        pygame.draw.circle(screen, self.color, rounded_pos, self.radius)



def setup():
    global circles
    circles = [Particle() for _ in xrange(TOTAL)]

    random.seed()
    for circle in circles:

        circle.pos[X] = random.randint(0, W)
        circle.pos[Y] = random.randint(0, H)
        # circles are resting
        circle.old_pos[X] = circle.pos[X]
        circle.old_pos[Y] = circle.pos[Y]


def gravity():
    for circle in circles:
        circle.acc += GRAVITY * TRANSFORM


def accelerate(delta):
    for circle in circles:
        circle.accelerate(delta)


def world_constraints():
    for circle in circles:
        radius = circle.radius
        circle.pos[X] = min(W - radius, max(circle.pos[X] + radius, 2 * radius) - radius)
        circle.pos[Y] = min(H - radius, max(circle.pos[Y] + radius, radius) - radius)


def collide():
    trick =  0.0000001
    for i in xrange(TOTAL):
        a = circles[i]
        for j in xrange(i, TOTAL):
            b = circles[j]

            x = a.pos[X] - b.pos[X]
            y = a.pos[Y] - b.pos[Y]

            slength = (x * x + y * y)
            length = math.sqrt(slength) + trick

            target = a.radius + b.radius

            if length < target:
                factor = (length - target) / length

                a.pos[X] -= x * factor * 0.5
                a.pos[Y] -= y * factor * 0.5
                b.pos[X] += x * factor * 0.5
                b.pos[Y] += y * factor * 0.5


def inertia(delta):
    for circle in circles:
        circle.inertia(delta)


def step():
    iterations = 10
    delta = 1. / FPS / iterations

    for _ in xrange(iterations):
        gravity()
        accelerate(delta)
        collide()
        world_constraints()
        inertia(delta)


def draw():
    for circle in circles:
        circle.draw()






"""
def check_inputs():
    global PICKING
    global picked
    mouse_pos = pygame.mouse.get_pos()
    left_button, _, _ = pygame.mouse.get_pressed()

    # HOVER
    for i, particle in enumerate(particles):
        if particle.collide(mouse_pos):
            particle.color = RED
            if left_button and not PICKING:
                picked = particle
                PICKING = True

        else:
            particle.color = BLACK

    if not left_button:
        picked = None
        PICKING = False


"""




if __name__ == '__main__':
    setup()

    while True:

        clock.tick(FPS)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                sys.exit()

        screen.fill(WHITE)


        step()
        draw()
        pygame.display.flip()


