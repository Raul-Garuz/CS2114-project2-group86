import os
import numpy as np

np.set_printoptions(precision=6, suppress=True)

path = os.path.expanduser("~/Downloads/blacksburg_normals.csv")
data = np.loadtxt(path, delimiter=",", skiprows=2)
T = data[:, 1:4]          # columns tavg, tmax, tmin  (12 x 3)

U, s, Vt = np.linalg.svd(T, full_matrices=False)

# (a) singular values
print("(a) singular values of T:", s)

normT = np.linalg.norm(T, 2)
print("||T|| =", normT, "(= sigma_1)")

# (b) rank-1 approximation
T1 = s[0] * np.outer(U[:, 0], Vt[0])
err1 = np.linalg.norm(T - T1, 2)
print("||T - T1||=", err1)
print(" relative error    =", err1 / normT)
print("    sigma_2           =", s[1])
print("    ||T - T1|| == sigma_2 ?", np.isclose(err1, s[1]))

# (c) rank-2 approximation
T2 = T1 + s[1] * np.outer(U[:, 1], Vt[1])
err2 = np.linalg.norm(T - T2, 2)
print("||T - T2||=", err2)
print("relative error=", err2 / normT)
print("sigma_3=", s[2])
print("||T - T2|| == sigma_3 ?", np.isclose(err2, s[2]))

# (d) the near-null direction
w = np.array([2.0, -1.0, -1.0])
Tw = T @ w
print("\n(d) Tw =", Tw)
print("||Tw||=", np.linalg.norm(Tw))
print("w/||w||=", w / np.linalg.norm(w))
print("v3 (3rd right sv) =", Vt[2])
print("|cos angle(w, v3)|=", abs(w @ Vt[2]) / np.linalg.norm(w))
print("||T v3|| = sigma_3 =", np.linalg.norm(T @ Vt[2]))