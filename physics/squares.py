import sys
import pygame
import numpy as np
import random
import math


SIZE = 35
TOTAL = 10
W = 640
H = 480

BLACK = 0, 0, 0
WHITE = 255, 255, 255
RED = 255, 0, 0
GREEN = 0, 255, 0
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

squares = []
collisions = []

class Particle(object):
    def __init__(self):
        self.pos = np.zeros(2)
        self.old_pos = np.zeros(2)
        self.acc = np.zeros(2)

    def accelerate(self, delta):
        self.pos += self.acc * delta * delta
        self.acc = np.zeros(2)

    def inertia(self, delta):
        new_pos = 2 * self.pos - self.old_pos
        self.old_pos = self.pos
        self.pos = new_pos

    def rest(self):
        self.old_pos = np.array(self.pos)


class Constraint(object):
    def __init__(self, a, b, spring):
        self.a = a
        self.b = b
        self.spring = spring


class Square(object):
    def __init__(self):
        self.size = SIZE #random.randint(5, size)
        self.corners = [ Particle() for _ in xrange(4) ]
        self.color = BLACK
        self.center = np.zeros(2)
        self.constraints = []
        self.set_constraints()

    def set(self, x, y):
        self.center = np.array([x, y])
        self.build()
        self.rest()

    def set_constraints(self):
        # create constraints
        for i in range(4):
            self.constraints.append(
                Constraint(self.corners[i], self.corners[(i + 1) % 4], self.size))

        # diagonals
        self.constraints.append(
            Constraint(self.corners[0], self.corners[2], math.sqrt(self.size ** 2 + self.size ** 2)))

        self.constraints.append(
            Constraint(self.corners[1], self.corners[3], math.sqrt(self.size ** 2 + self.size ** 2)))

    def build(self):
        #top left
        self.corners[0].pos[X] = self.center[X] - self.size / 2.
        self.corners[0].pos[Y] = self.center[Y] + self.size / 2. #* -1 # to adapt pygame coordinates
        #top right
        self.corners[1].pos[X] = self.center[X] + self.size / 2.
        self.corners[1].pos[Y] = self.center[Y] + self.size / 2. #* -1
        #bottom right
        self.corners[2].pos[X] = self.center[X] + self.size / 2.
        self.corners[2].pos[Y] = self.center[Y] - self.size / 2. #* -1
        #bottom left
        self.corners[3].pos[X] = self.center[X] - self.size / 2.
        self.corners[3].pos[Y] = self.center[Y] - self.size / 2. #* -1

    def gravity(self, force):
        for corner in self.corners:
            corner.acc += force

    def is_colliding(self, other):
        s_max_x = self.max_axis(X)
        s_max_y = self.max_axis(Y)

        s_min_x = self.min_axis(X)
        s_min_y = self.min_axis(Y)

        o_max_x = other.max_axis(X)
        o_max_y = other.max_axis(Y)

        o_min_x = other.min_axis(X)
        o_min_y = other.min_axis(Y)

        candidate = False
        if s_min_x <= o_min_x and o_min_x <= s_max_x:
            candidate = True

        if o_min_x <= s_min_x and s_min_x <= o_max_x:
            candidate = True

        if candidate:
            if s_min_y <= o_min_y and o_min_y <= s_max_y:
                return True

            if o_min_y <= s_min_y and s_min_y <= o_max_y:
                return True

        return False

    def make_constraint(self, other):
        ba, bb = self.corners[0], other.corners[0]
        closest_distance = np.linalg.norm(ba.pos-bb.pos)
        for acorner in self.corners:
            for bcorner in other.corners:
                a = acorner.pos
                b = bcorner.pos
                distance = np.linalg.norm(a-b)
                if distance < closest_distance:
                    closest_distance = distance
                    ba, bb = acorner, bcorner

        return Constraint(ba, bb, closest_distance)

    def accelerate(self, delta):
        for corner in self.corners:
            corner.accelerate(delta)

    def inertia(self, delta):
        for corner in self.corners:
            corner.inertia(delta)

    def max_axis(self, axis):
        result = self.corners[0].pos[axis]
        for corner in self.corners:
            if corner.pos[axis] > result:
                result = corner.pos[axis]
        return result

    def min_axis(self, axis):
        result = self.corners[0].pos[axis]
        for corner in self.corners:
            if corner.pos[axis] < result:
                result = corner.pos[axis]
        return result

    def draw(self):
        #rect = pygame.Rect(self.bottom_left(), self.top_right())
        #pygame.draw.rect(screen, self.color, rect, self.size)
        pygame.draw.polygon(screen, self.color, [ np.array(corner.pos , dtype=np.int) for corner in self.corners])

    def rest(self):
        for corner in self.corners:
            corner.rest()

    def world_constraints(self):
        for corner in self.corners:
            corner.pos[X] = min(W, max(corner.pos[X], 0))
            corner.pos[Y] = min(H, max(corner.pos[Y], 0))

    def internal_constraints(self):
        trick =  0.0000001
        for constraint in self.constraints:

            a = constraint.a
            b = constraint.b
            spring = constraint.spring
            x = a.pos[X] - b.pos[X]
            y = a.pos[Y] - b.pos[Y]

            slength = (x * x + y * y)
            length = math.sqrt(slength) + trick

            if length != spring:
                factor = (length - spring) / length

                a.pos[X] -= x * factor * 0.5
                a.pos[Y] -= y * factor * 0.5
                b.pos[X] += x * factor * 0.5
                b.pos[Y] += y * factor * 0.5


def external_constraint(constraint):
    trick =  0.0000001
    a = constraint.a
    b = constraint.b
    spring = constraint.spring
    x = a.pos[X] - b.pos[X]
    y = a.pos[Y] - b.pos[Y]



    factor = spring / 2.

    a.pos[X] -= factor * 0.05
    a.pos[Y] -= factor * 0.05
    b.pos[X] += factor * 0.05
    b.pos[Y] += factor * 0.05


def setup():
    global squares
    squares = [Square() for _ in xrange(TOTAL)]

    random.seed()
    for i, square in enumerate(squares):
        #square.set(random.randint(0, W), random.randint(0, H))
        square.set(100 + 15 * i, 50 * i)



def gravity():
    for square in squares:
        square.gravity(GRAVITY * TRANSFORM)


def accelerate(delta):
    for square in squares:
        square.accelerate(delta)


def world_constraints():
    for square in squares:
        square.world_constraints()


def internal_constraints():
    for square in squares:
        square.internal_constraints()


def collide():
    global collisions
    trick =  0.0000001
    for i in xrange(TOTAL):
        a = squares[i]
        for j in xrange(i + 1, TOTAL):
            b = squares[j]

            # coarse grain

            if a.is_colliding(b):
                collisions.append(a.make_constraint(b))
                a.color = RED
                b.color = RED
            else:
                a.color = BLACK
                b.color = BLACK
    # fix constraints
    for constraint in collisions:
        external_constraint(constraint)

    collisions = []

def inertia(delta):
    for square in squares:
        square.inertia(delta)


def step():
    iterations = 10
    delta = 1. / FPS / iterations

    for _ in xrange(iterations):
        gravity()
        accelerate(delta)


        world_constraints()
        internal_constraints()
        collide()

        inertia(delta)
        pass


def draw():
    global collisions
    for square in squares:
        square.draw()

    for collision in collisions:
        pygame.draw.circle(screen, GREEN, collision, 2)

    collisions = []


if __name__ == '__main__':
    setup()

    while True:

        clock.tick(FPS)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                sys.exit()

        screen.fill(WHITE)

        draw()
        pygame.display.flip()
        step()




