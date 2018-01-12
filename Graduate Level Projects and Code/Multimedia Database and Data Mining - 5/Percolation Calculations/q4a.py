import networkx as nx
import random as rand
from networkx.algorithms import approximation as approx
from networkx.algorithms import shortest_paths as paths
import matplotlib.pyplot as plt


def percolated_graph(prob, grid_size):
    G = nx.Graph()
    row = grid_size
    col = grid_size
    for i in range(row):
        for j in range(col):
            if rand.random() < prob:
                G.add_node(i * col + j)
                if (i - 1) * col + j in G.nodes():
                    G.add_edge(i * col + j, (i - 1) * col + j)
                if i * col + (j - 1) in G.nodes():
                    G.add_edge(i * col + j, i * col + (j - 1))

    for i in range(row):
        G.add_edge(i, -1)
        G.add_edge((row ** 2 - 1) - i, row ** 2)

    return paths.has_path(G, -1, row ** 2)


# def valid_edge(G, src, dest, grid_size):
#     if dest > ((grid_size ** 2) - 1):
#         return False
#     if src % grid_size != 0 and dest % grid_size == 0:
#         return False
#     if G.nodes[src]['o'] != 'T':
#         return False
#     if G.nodes[dest]['o'] != 'T':
#         return False
#     return True
#
#
# def generate_graph(prob, grid_size):
#     G = nx.Graph()
#     for node in range(grid_size ** 2):
#         if rand.random() < prob:
#             G.add_node(node, o='T')
#         else:
#             G.add_node(node, o='F')
#
#     for node in range(grid_size ** 2):
#         if valid_edge(G, node, node + grid_size, grid_size):
#             G.add_edge(node, node + grid_size)
#         if valid_edge(G, node, node + 1, grid_size):
#             G.add_edge(node, node + 1)
#
#     percolated = False
#
#     dest_nodes = []
#     for i in range(grid_size):
#         dest_nodes.append((grid_size ** 2 - 1) - i)
#
#     for i in range(grid_size):
#         for j in range(grid_size):
#             if approx.local_node_connectivity(G, i, dest_nodes[j]) > 0:
#                 percolated = True
#
#     return percolated


def p_calculate(density, grid_size):
    conn = {}
    for prob in density:
        if .4 <= prob < .8:
            iterations = 100
        else:
            iterations = 10
        total = 0
        for i in range(iterations):
            if percolated_graph(prob, grid_size):
                total += 1
        conn[prob] = 1.0 * total / iterations
    return conn


def routine(x, grid, file):
    conn = p_calculate(x, grid)
    print("Number of data points {}".format(len(conn)))
    plt.plot(list(conn.keys()), list(conn.values()), 'ro')
    plt.xlabel('density')
    plt.ylabel('prob percolation')
    plt.xlim(.1,1)
    plt.ylim(0,1)
    plt.savefig("DensityVsProb-{}.png".format(grid), dpi = 1000)
    print("plot saved a")
    plt.gcf().clear()
    file.write("{}\n".format(conn))


if __name__ == '__main__':
    x = [float(format(i * 0.1, '.1f')) for i in range(1, 4)]
    x.extend([float(format(i / 500, '.3f')) for i in range(200, 400)])
    x.extend([float(format(i * 0.1, '.1f')) for i in range(8, 11)])
    file = open("percolation_prob_critical.txt", "w")
    for value in [16, 32, 64]:
        routine(x, value, file)
    file.close()
