import pulp as pl


def parse_augmented_from_line(line):
    rows = [r.strip() for r in line.split(";") if r.strip()]
    matrix = []

    for r in rows:
        values = [int(v.strip()) for v in r.split(",")]
        matrix.append(values)

    if not matrix:
        raise ValueError("Empty problem")

    width = len(matrix[0])
    if width < 2:
        raise ValueError("Augmented matrix must have at least 2 columns")

    for row in matrix:
        if len(row) != width:
            raise ValueError("Non-rectangular augmented matrix")

    A = [row[:-1] for row in matrix]
    b = [row[-1] for row in matrix]

    return A, b


def solve_min_sum_ilp(A, b):
    n = len(A[0])
    m = len(A)

    prob = pl.LpProblem("min_sum_x", pl.LpMinimize)

    x = [pl.LpVariable(f"x{i+1}", lowBound=0, cat=pl.LpInteger)
         for i in range(n)]

    prob += pl.lpSum(x)

    for r in range(m):
        prob += pl.lpSum(A[r][c] * x[c] for c in range(n)) == b[r]

    prob.solve(pl.PULP_CBC_CMD(msg=False))

    if prob.status != pl.LpStatusOptimal:
        raise RuntimeError("No optimal solution found")

    return int(pl.value(prob.objective))


def solve_file(path):
   
    total_objective = 0

    with open(path, "r") as f:
        for line_no, line in enumerate(f, start=1):
            line = line.strip()
            if not line:
                continue

            A, b = parse_augmented_from_line(line)
            obj = solve_min_sum_ilp(A, b)
            total_objective += obj

    return total_objective


total_objective = solve_file("input.txt")

print("total objective:", total_objective)
