if __name__ == "__main__":
    model = {}
    x = 6
    y = 6
    z = 1/36
    for i in range(1, x + 1):
        for j in range(1, y + 1):
            model[(i, j)] = z
    print(model)