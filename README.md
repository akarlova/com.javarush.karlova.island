_**Island Simulation Project**_

Многопоточная симуляция жизни животных на острове, где они перемещаются, едят и размножаются.

**ТЗ проекта, обязательное к соблюдению:**

Обязательная часть задания:
- Иерархия животных (ООП)
- Поведение животных
- Многопоточность
- Статистика по состоянию острова на каждом такте (в консоль)

Животные, обязательные к созданию (Должно быть создано минимум 10 видов травоядных и 5 видов хищников (описаны в п. 1)):
- Хищник: Волк, Удав, Лиса, Медведь, Орел
- Травоядные: Лошадь, Олень, Кролик, Мышь, Коза, Овца, Кабан, Буйвол, Утка, Гусеница

Для этих животных в ТЗ указаны готовые параметры:
- Вес одного животного, кг
- Максимальное количество животных этого вида на одной клетке
- Скорость перемещения, не более чем, клеток за ход
- Сколько килограммов пищи нужно животному для полного насыщения

Данные параметры отражены в загрузочном файле AnimalStats.yaml
В классе Grass отдельно учтены требования по характеристикам травы.

Также для данных животных поданы данные по вероятности поедания друг друга или травы. 
Данные отражены в загрузочном файле eatingProbabilities.yaml

У животного должны быть методы: покушать, размножиться, выбрать направление передвижения -
реализовано через соответствующие интерфейсы.

**_ЧТО НОВОГО?_**

Мною добавлены следующие вещи для оживления обстановки на Острове:

1) Добавлены структуры клеток острова:
- Land - клетки, покрытые землей
- Water - клетки, на которых есть вода
- Sand - структура клетки, которую та приобретает, если имеет наибольшую смертность за такт,
наименьшую рождаемость за такт и рандомное распределение с вероятностью 15% за такт (дует ветер)

2) Цель симуляции определена как: отобразить превращение острова в Дюну (Арракис)
  Цель считается достигнутой, когда более 80% клеток на острове заполняются песком.

3) Добавлен обитатель Дюны - Червь (Шаи-Хулуд). 
Появление: Как только клетка приобретает структуру "Песок", трава на ней перестаёт расти
и рождается Червь.
Червь не относится к животным, наследование происходит напрямую от Creature, 
он имеет свой личный конструктор в Worm.java. Также Червь обладает имбовыми способностями.
Убить его невозможно, еда ему не нужна (по умолчанию, он питается песком). 
Однако его размножение ограничено количеством клеток на Острове. 
Перемещаться он может лишь по клеткам со структурой Песок.

4) Чтобы жизнь на острове протекала веселее, добавлен хищник Тираннозавр. Тираннозавр питается
всеми крупными животными, его пищевая карта добавлена в eatingprobabilities. 
Вероятность поражения добычи 100%. Ест Тираннозавр до полного насыщения (если это позволяет население острова).
Убить его может лишь Червь, находясь с ним на одной клетке. И убьет обязательно, т.к. Червя приманивает топот Тираннозавра по песку.
Информация о бое выводится в консоль:
![Fighting](https://github.com/akarlova/com.javarush.karlova.island/blob/master/images/screen1.png?raw=true)


5) В AnimalStats добавлены параметры по размножению: необходима ли пара для размножения, вероятность
размножения и количество детенышей в случае успеха. Для Тираннозавра, например, вероятность составляет 0.1%,
причем за счет самооплодотворения (как в фильме "Парк Юрского периода"). Что интересно, при определенном везении,
иногда он даже успевает размножиться, избежав встречи с Червем.
Также на остров добавлены бабочки, размножение которых описано в LifeCycle путем трансформации из гусениц.

6) На острове, где есть пресная вода (клетки Water), животные могут умереть не только от голода, но и от жажды.

Ожидаемое окончание симуляции представлено на скрине:
![DUNE](https://github.com/akarlova/com.javarush.karlova.island/blob/master/images/screen2.png?raw=true)

 ИСХОДНЫЕ ПАРАМЕТРЫ ОСТРОВА ПРИ ЗАПУСКЕ СИМУЛЯЦИИ:
2000 клеток
Максимальный период симуляции: 50 тактов 

Запуск симуляции расположен в классе AppGo

**Поехали!**

