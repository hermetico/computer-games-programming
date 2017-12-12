import sys
import pygame
import numpy as np
import random


REST_LENGTH = 50
TOTAL = 5
W = 640
H = 480

BLACK = 0, 0, 0
WHITE = 255, 255, 255
RED = 255, 0, 0
GRAVITY = np.array((0, -9.1)) * 10
TRANSFORM = np.array((1, -1))

X = 0
Y = 1

FPS = 30
pygame.init()
screen = pygame.display.set_mode((W, H))
#pygame.display.set_caption("Moving Box")

clock = pygame.time.Clock()

PICKING = False
picked = None

class Particle(object):
    def __init__(self):
        self.pos = np.zeros(2)
        self.old_pos = np.zeros(2)
        self.acc = np.zeros(2)
        self.mass = random.randint(2, 10)
        self.color = BLACK

    def collide(self, other_pos):
        return self.mass > np.linalg.norm(self.pos - other_pos)


particles = [Particle() for _ in xrange(TOTAL)]


class Cloth(object):
    def __init__(self):
        self.constraints = []
        for i in range(1, TOTAL):
                self.constraints.append((particles[i-1], particles[i]))


cloth = Cloth()


def setup():
    random.seed()
    for particle in particles:
        particle.pos[X] = random.randint(0, W)
        particle.old_pos[X] = particle.pos[X]
        particle.pos[Y] = random.randint(0, H)
        particle.old_pos[Y] = particle.pos[Y]


def accumulate_forces():
    for particle in particles:
        particle.acc = np.zeros(2)
        particle.acc += GRAVITY * TRANSFORM


def satisfy_constraints():
    global picked
    for _ in range(1):
        for a, b in cloth.constraints:
            a.pos[X] = min(W, max(a.pos[X], 0))
            a.pos[Y] = min(H, max(a.pos[Y], 0))
            b.pos[X] = min(W, max(b.pos[X], 0))
            b.pos[Y] = min(H, max(b.pos[Y], 0))

            x1 = a.pos
            x2 = b.pos
            delta = x2 - x1
            delta_length = np.sqrt(delta * delta)
            diff = (delta_length - REST_LENGTH) / delta_length * (1./a.mass + 1./b.mass)

            x1 += 1./a.mass * delta*0.5*diff
            x2 -= 1./b.mass * delta*0.5*diff

        if PICKING:
            a = picked

            x1 = a.pos
            x2 = pygame.mouse.get_pos()
            delta = x2 - x1
            delta_length = np.sqrt(delta * delta)
            diff = (delta_length - REST_LENGTH) / delta_length * (1./a.mass)

            x1 += 1./a.mass * delta*0.5*diff



def verlet():
    for particle in particles:
        x = particle.pos
        temp = np.array((x[X], x[Y]))
        old_x = particle.old_pos
        acc = particle.acc
        particle.pos = 1.99 * x - 0.99 * old_x + acc * (1./FPS) * (1./FPS)
        particle.old_pos = temp


def time_step():
    check_inputs()
    accumulate_forces()
    verlet()
    satisfy_constraints()


def draw():
    for particle in particles:
        pygame.draw.circle(screen, particle.color, np.array(particle.pos, dtype=np.int), particle.mass)

    for a, b in cloth.constraints:
        pygame.draw.aaline(screen, BLACK, a.pos, b.pos)


setup()

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

while True:

    clock.tick(FPS)

    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            sys.exit()

    screen.fill(WHITE)


    time_step()
    draw()
    pygame.display.flip()






