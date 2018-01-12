import networkx as nx
import random as rand
from networkx.algorithms import approximation as approx
import matplotlib.pyplot as plt


def graph_creation(prob, grid_size):
    G = nx.Graph()
    row = grid_size
    col = grid_size
    empty = True
    for i in range(row):
        for j in range(col):
            if rand.random() < prob:
                empty = False
                G.add_node(i * col + j)
                if i > 0 and (i - 1) * col + j in G.nodes():
                    G.add_edge(i * col + j, (i - 1) * col + j)
                if j >0 and i * col + (j - 1) in G.nodes():
                    G.add_edge(i * col + j, i * col + (j - 1))

    if empty:
        return None
    else:
        return G

def c_calculate(density, grid_size):
    conn = {}
    iterations = 10
    for prob in density:
        if .4 <= prob < .8:
            iterations = 100
        else:
            iterations = 10
        total = 0
        for i in range(iterations):
            graph = graph_creation(prob, grid_size)
            if graph == None:
                continue
            total += nx.diameter(max(nx.connected_component_subgraphs(graph), key=len))
            graph.clear()
        conn[prob] = 1.0 * total / iterations
    return conn

def routine(x, grid, file):
    conn = c_calculate(x, grid)
    plt.plot(list(conn.keys()), list(conn.values()), 'ro')
    plt.xlabel('density')
    plt.ylabel('largest-diameter')
    plt.savefig("DensityVsComponent-{}.png".format(grid), dpi=1000)
    print("plot saved b")
    plt.gcf().clear()
    file.write("{}".format(conn))

if __name__ == '__main__':
    x = [x / 10.0 for x in range(1, 5)]
    x.extend([x / 100.0 for x in range(50, 70)])
    x.extend([x / 10.0 for x in range(7, 11)])
    file = open("diameter_critical.txt", "w")
    for value in [16, 32, 64]:
        routine(x, value, file)
    file.close()
