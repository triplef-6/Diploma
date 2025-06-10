import pandas as pd
import matplotlib.pyplot as plt
import os

file_path = "../data/data_final_F.csv"

if not os.path.exists(file_path):
    print(f"Файл {file_path} не найден")
    exit()

df = pd.read_csv(file_path)

plt.style.use('bmh')
plt.figure(figsize=(12, 7))

# Получаем уникальные значения в столбце 'Исход' — это наши группы
unique_sources = df["Исход"].unique()

# Цвета для разных графиков — можно расширить или менять
colors = plt.cm.get_cmap('tab10', len(unique_sources))

for i, source in enumerate(unique_sources):
    group = df[df["Исход"] == source]
    label = "ПС" if source == 0 else f"s={source}"
    plt.plot(
        group["Итерация"],
        group["Значение"],
        color=colors(i),
        linewidth=2.5,
        marker='o',
        markersize=6,
        markerfacecolor='white',
        markeredgecolor=colors(i),
        markeredgewidth=1.5,
        label=label
    )

plt.title("Изменение целевой функции по исходам", fontsize=16, pad=20)
plt.xlabel("Итерация", fontsize=14)
plt.ylabel("Значение F", fontsize=14)
plt.xticks(fontsize=12)
plt.yticks(fontsize=12)

plt.grid(True, linestyle='--', alpha=0.7, color='#B0B0B0')

ax = plt.gca()
ax.spines['top'].set_visible(False)
ax.spines['right'].set_visible(False)
ax.spines['left'].set_color('#808080')
ax.spines['bottom'].set_color('#808080')

plt.legend(fontsize=12)
plt.tight_layout()

plt.show()
