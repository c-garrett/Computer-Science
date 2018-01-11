#!/usr/bin/env python
# python q2.py <input file>
import os
import sys
import math
import matplotlib.pyplot as plt
import numpy as np
from numpy import linalg as LA


def file_cleaning(lines):
    for i in range(len(lines)):
        lines[i] = [float(p) for p in lines[i].strip().split()]
    return lines

def calculate(points):
    log_distance = dict()
    for i in range(len(points)):
        for j in range(len(points)):
            if i == j: 
                continue
            e_d = LA.norm(np.array(points[i]) - np.array(points[j]))
            if e_d != 0:
                if math.log(e_d) in log_distance:
                    log_distance[math.log(e_d)] += 1
                else:
                    log_distance[math.log(e_d)] = 1

    prev = 1000
    for key in sorted(log_distance.keys()):
        log_distance[key] += prev
        prev = log_distance[key]
        log_distance[key] = math.log(log_distance[key])

    return log_distance

def plot(distance):
    plt.plot(list(distance.keys()), list(distance.values()), 'ro')
    plt.xlabel('log(r)')
    plt.ylabel('log(count)')
    plt.show()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit()
    path = os.getcwd()
    filename = sys.argv[1]
    with open(path+'/'+filename,'r') as file:
        lines = file.readlines()
    
    plot(calculate(file_cleaning(lines)))