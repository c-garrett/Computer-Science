from fractions import Fraction
from Exercises import comp_prob_inference
from Exercises.Week1 import prob_functions


def to_fraction(n):
    return Fraction(n).limit_denominator(1000)

def coin_simulation():
    n = 100000
    heads_so_far = 0
    fraction_of_heads = []
    for i in range(n):
        if comp_prob_inference.flip_fair_coin() == 'heads':
            heads_so_far += 1
        fraction_of_heads.append(heads_so_far / (i + 1))

    import matplotlib.pyplot as plt

    plt.figure(figsize=(8, 4))
    plt.plot(range(1, n + 1), fraction_of_heads)
    plt.xlabel('Number of flips')
    plt.ylabel('Fraction of heads')
    plt.show()

def event_simulation():
    prob_space = {'sunny': 1 / 2, 'rainy': 1 / 6, 'snowy': 1 / 3}
    rainy_or_snowy_event = {'rainy', 'snowy'}
    print(prob_functions.prob_of_event(rainy_or_snowy_event, prob_space))


def faces_that_add_prob(n):
    faces = set()
    count = 0
    for x in range(1, 7):
        for y in range(1, 7):
            if x + y == n:
                count += 1
                faces.add((x, y))
    print(faces)
    print(Fraction(count / 36).limit_denominator(1000))

def random_variable():
    prob_space = {'sunny': 1 / 2, 'rainy': 1 / 6, 'snowy': 1 / 3}
    W_mapping = {'sunny': 'sunny', 'rainy': 'rainy', 'snowy': 'snowy'}
    I_mapping = {'sunny': 1, 'rainy': 0, 'snowy': 0}
    random_outcome = comp_prob_inference.sample_from_finite_probability_space(prob_space)
    W = W_mapping[random_outcome]
    I = I_mapping[random_outcome]
    print(W)
    print(I)
    W_table = {'sunny': 1 / 2, 'rainy': 1 / 6, 'snowy': 1 / 3}
    I_table = {0: 1 / 2, 1: 1 / 2}
    W = comp_prob_inference.sample_from_finite_probability_space(W_table)
    I = comp_prob_inference.sample_from_finite_probability_space(I_table)
    print(W)
    print(I)
    return

def two_dice_pmf():
    pmf = {}  # start with empty pmf
    for x in range(1, 7):
        for y in range(1, 7):
            if (x + y) in pmf:
                pmf[x + y] += to_fraction(1/36)
            else:
                pmf[x + y] = to_fraction(1/36)
    print(pmf)

if __name__ == "__main__":
    pass
