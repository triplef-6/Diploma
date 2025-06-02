import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("../data/data_final_F.csv")
plt.plot(df["Итерация"], df["Значение"], marker="o")
plt.xlabel("Итерация")
plt.ylabel("Значение")
plt.title("Изменение данных")
plt.grid(True)
plt.show()
