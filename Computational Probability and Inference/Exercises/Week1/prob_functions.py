import numpy as np
import matplotlib.pyplot as plt
import pandas as pd

def prob_of_event(event, prob_space):
    total = 0
    for outcome in event:
        total += prob_space[outcome]
    return total