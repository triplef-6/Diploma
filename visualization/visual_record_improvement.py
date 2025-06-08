import pandas as pd
import matplotlib.pyplot as plt
import os

# путь к файлу
file_path = "../data/data_final_improvement.csv"

# проверка наличия файла
if not os.path.exists(file_path):
    print(f"Файл {file_path} не найден")
    exit()

# чтение данных
df = pd.read_csv(file_path)

plt.style.use('bmh')  # стиль
plt.figure(figsize=(12, 7)) # размер 

# построение графика
plt.plot(
    df["Итерация"], 
    df["Значение"], 
    color='#6A0DAD',
    linewidth=2.5,    
    marker='o',       
    markersize=8,     
    markerfacecolor='white',  
    markeredgecolor='#6A0DAD',
    markeredgewidth=1.5,
    label='Изменение данных'
)

# улучшение подписей и шрифтов
plt.title("Улучшение по итерациям", fontsize=16, pad=20)
plt.xlabel("Итерация", fontsize=14)
plt.ylabel("Улучшение на итерации", fontsize=14)
plt.xticks(fontsize=12)
plt.yticks(fontsize=12)

# улучшение сетки
plt.grid(True, linestyle='--', alpha=0.7, color='#B0B0B0')

# улучшение внешнего вида осей
ax = plt.gca()
ax.spines['top'].set_visible(False)
ax.spines['right'].set_visible(False)
ax.spines['left'].set_color('#808080')
ax.spines['bottom'].set_color('#808080')

# добавление отступов
plt.tight_layout()

# сохранение графика в файл 
# plt.savefig('data_plot.png', dpi=300, bbox_inches='tight')

plt.show()
