import sys
import pygame
import numpy as np
import random
import math


SIZE = 35
TOTAL = 20
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

FPS = 25
pygame.init()
screen = pygame.display.set_mode((W, H))
#pygame.display.set_caption("Moving Box")

clock = pygame.time.Clock()

PICKING = False
picked = None

squares = []
collisions = []

class Particle(object):
    def __init__(self, owner):
        self.pos = np.zeros(2)
        self.old_pos = np.zeros(2)
        self.acc = np.zeros(2)
        self.owner = owner

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


class Collision(object):
    def __init__(self, normal=None, depth=None, edge=None, vertex=None):
        self.normal = normal
        self.depth = depth
        self.edge = edge
        self.vertex = vertex


class Edge(object):
    def __init__(self, a, b, owner):
        self.a = a
        self.b = b
        self.owner = owner

class Square(object):
    def __init__(self):
        self.size = random.randint(5, SIZE)
        self.corners = [ Particle(self) for _ in xrange(4) ]
        self.color = BLACK
        self.center = np.zeros(2)
        self.constraints = []
        self.edges = []
        self.set_constraints()
        self.mass = self.size

    def set(self, x, y):
        self.center = np.array([x, y])
        self.build()
        self.rest()

    def set_constraints(self):
        # create constraints
        for i in range(4):
            self.constraints.append(
                Constraint(self.corners[i], self.corners[(i + 1) % 4], self.size))
            self.edges.append(Edge(self.corners[i], self.corners[(i + 1) % 4], self))

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

    def project_along(self, axis):
        dot_p = np.dot(self.corners[0].pos, axis)

        min_ = max_ = dot_p
        for i in range(1, len(self.corners)):
            dot_p = np.dot(self.corners[i].pos, axis)
            min_ = min(dot_p, min_)
            max_ = max(dot_p, max_)

        return min_, max_

    def update_center(self):
        self.center = np.array(
            ((self.min_axis(X) + self.max_axis(X)) * 0.5,
             (self.min_axis(Y) + self.max_axis(Y) * 0.5)))

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


def SAT_collision(a, b):
    depth_ = float("inf")
    normal_ = None
    edge_ = None

    for edge in a.edges + b.edges:

        p0, p1 = edge.a, edge.b
        # normal of the projections
        perp = np.array([p0.pos[Y] - p1.pos[Y], p1.pos[X] - p0.pos[X]])
        # do the normal
        axis = perp * 1.0 / math.sqrt(perp[0] ** 2 + perp[1] ** 2)

        a_min, a_max = a.project_along(axis)
        b_min, b_max = b.project_along(axis)

        if a_min < b_min:
            distance = b_min - a_max
        else:
            distance = a_min - b_max

        if distance > 0.0:
            return None

        elif math.fabs(distance) < depth_:
            depth_ = math.fabs(distance)
            normal_ = axis
            edge_ = edge

    col = Collision(normal_, depth_, edge_)

    # swap bodies
    if edge.owner != b:
        temp = b
        b = a
        a = temp

    n = np.dot(col.normal, a.center - b.center)
    if n < 0:
        # negate the axis
        col.normal *= -1

    smallestD = float("inf")
    for corner in a.corners:
        distance2 = np.dot(col.normal, corner.pos - b.center)
        if distance2 < smallestD:
            smallestD = distance2
            col.vertex = corner

    return col

def resolve_collision( collision ):
    response = np.array(collision.normal * collision.depth)
    e1, e2 = collision.edge.a, collision.edge.b
    if  math.fabs(e1.pos[X] - e2.pos[X]) > math.fabs(e1.pos[Y] - e2.pos[Y]):
        T = (collision.vertex.pos[X] -  response[X] - e1.pos[X]) / e2.pos[X] - e1.pos[X]
    else:
        T = (collision.vertex.pos[Y] -  response[Y] - e1.pos[Y]) / e2.pos[Y] - e1.pos[Y]

    factor = 1.0 / (T*T + ( 1 - T) * (1 - T))
    e1.pos -= response * (1 - T ) * 0.5 * factor #* 1. / e1.owner.mass # adds the mass of the owner
    e2.pos -= response * T  * 0.5 * factor
    collision.vertex.pos += response * 0.5 #* 1. / collision.vertex.owner.mass # here too


def setup():
    global squares
    squares = [Square() for _ in xrange(TOTAL)]

    random.seed()
    for i, square in enumerate(squares):
        square.set(random.randint(0, W), random.randint(0, H))
        #square.set(100 + 15 * i, 50 * i)



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
    for square in squares:
        square.color = BLACK
        square.update_center()

    for i in xrange(TOTAL):
        a = squares[i]
        for j in xrange(TOTAL):
            b = squares[j]

            if a == b: continue

            # coarse grain
            collision = SAT_collision(a, b)
            if collision is not None:
                resolve_collision(collision)


def inertia(delta):
    for square in squares:
        square.inertia(delta)


def step():
    iterations = 2
    delta = 1. / FPS / iterations

    for _ in xrange(iterations):
        gravity()
        accelerate(delta)

        world_constraints()
        internal_constraints()
        inertia(delta)

        collide()


        pass


def draw():
    global collisions
    for square in squares:
        square.draw()

    #for collision in collisions:
    #    pygame.draw.circle(screen, GREEN, np.array(collision.vertex.pos, dtype=np.int), 2)

    #collisions = []


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




