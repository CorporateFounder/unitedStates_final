# Инструкция по работе с системой баллов и сложностью блоков

Инструкции по работе с системой баллов и сложностью блока
Расчет вознаграждения
Формула вознаграждения:
blocksSinceStart = текущий индекс блока - 326840
год = blocksSinceStart / (432 × 360)
сложностьV2 = max(сложность - 22, 0)
Результат = (3 + (коэффициент / 4) + (сложностьV2 / 4)) × (1,005) ^ год
Награда = Результат × Множитель (где множитель ≥ 1)
Коэффициент:
Возможные значения: 3 или 0
Поощряет транзакционную активность
3, если уникальные транзакции и объем увеличиваются по сравнению с предыдущим блоком
0, если они остаются прежними или уменьшаются
Исключает БАЗОВЫЙ АДРЕС и дублирующиеся адреса
Множитель:
Начальное значение: 35, линейно уменьшается каждые 4 месяца.
Гибридная система консенсуса
Система использует комбинацию PoW и PoS для обеспечения безопасности.

Формирование очков игрока:
Очки = очки сложности + случайное число + очки стейкинга + очки транзакций

Очки стейкинга:
Экспоненциальная шкала:
1-й балл: 1,1 монеты
2-й балл: 2,1 монеты
3-й балл: 4,1 монеты
Максимум: 30 баллов.
Очки транзакций:
Рассчитываются на основе сумм транзакций от уникальных отправителей:
1-й балл: 0,11 монеты
2-й балл: 0,21 монеты
3-й балл: 0,41 монеты
Максимум: очки не могут превышать очки стейкинга.
Сложность и группы:
Каждые 100–230 секунд 1 блок выбирается узлами с наивысшими баллами.
Параметры сложности и цели хеша
Каждый участник может отправлять блоки со сложностью от 17 до 100.

Действительный хеш: хеш, в котором количество единиц в битах меньше или равно значению:
Цель = 100 - Сложность.
Генерация случайности:
Случайное число генерируется на основе хеша блока, где хеш служит начальным числом для детерминированной случайности. Диапазон случайного числа фиксирован от 0 до 170.

Новая модель подсчета очков
Модель подсчета очков рассчитывает очки для игроков на основе следующего:

Очки сложности:
Рассчитываются как: сложность * 15.
Очки транзакций:
Определяются по предопределенной формуле (уже реализованной).
Ограничено значением mineScore.
Значение сложности хеша (X):
Определяется по формуле: 170.
Случайный диапазон:
Фиксированный диапазон: от 0 до 170.
В этом диапазоне выбирается случайное число.
Общее количество баллов:
Окончательный расчет: Общее количество баллов = Очки сложности + Случайное значение + Очки транзакции + mineScore.
Пример рабочего процесса
Извлеките текущую сложность хеша.
Рассчитайте Очки сложности: Умножьте сложность хеша на 15.
Используйте фиксированный диапазон от 0 до 170 для генерации случайных чисел.
Выберите случайное число в фиксированном диапазоне.
Объедините все компоненты, чтобы определить общее количество баллов.
Важные примечания
Очки транзакции предварительно вычисляются и ограничиваются mineScore.
Диапазон случайности всегда фиксирован от 0 до 170, независимо от сложности.
Все вычисления должны использовать детерминированную случайность для согласованности.
Сравнение поставок Citu
Вывод
Каждая монета имеет только 2 десятичных знака.

Две деревни
В деревне EUR:
Они выпустили 1000 банкнот.
Номинал каждой банкноты составляет 0,01 EUR.
Общий объем валюты: 1000 × 0,01 = 10 EUR.
В деревне USD:
Они также выпустили 1000 банкнот.
Номинал каждой банкноты составляет 1 USD.
Общий объем валюты: 1000 × 1 = 1000 USD.
На первый взгляд кажется, что объем валюты отличается: 10 EUR против 1000 USD. Однако важно учитывать обменный курс.

Обмен и паритет
Когда жители деревни решили торговать, они договорились, что:

1 USD = 0,01 EUR.
Таким образом, экономический паритет полностью сохраняется. Например:

Житель деревни EUR покупает зеленые кроссовки в деревне USD за 1 USD. Для оплаты он дает 1 банкноту достоинством 0,01 EUR.
Житель деревни USD покупает красную футболку за 0,01 EUR. Для оплаты он дает 1 банкноту достоинством 1 USD.
Таким образом, несмотря на разный номинал банкнот, фактический объем и покупательная способность валют остаются эквивалентными, поскольку обменный курс учитывает номинальные различия.

Экономический вывод
Общее количество банкнот (1000) одинаково.
Общая покупательная способность валют эквивалентна при применении обменного курса.
Таким образом, экономики двух деревень симметричны, а номинал банкнот влияет только на восприятие.

Сравнение с Bitcoin
Биткойн имеет 8 знаков после запятой, то есть его можно разделить на 0,00000001 BTC или 100 000 000 единиц на монету. Таким образом, общее количество единиц в системе Bitcoin равно:

2 100 000 000 000 000 сатоши. (рассчитывается как 21 000 000 монет × 100 000 000 единиц на монету).
Для сравнения, Citu, даже через 11 лет, достигнет общего количества в 22 600 000 000 единиц (рассчитывается как 226 000 000 монет × 100 единиц на монету).

Когда люди говорят, что на блок добывается всего 3 биткойна, это эквивалентно 3 «коробкам», каждая из которых содержит 100 000 000 банкнот. Таким образом, одна монета Citu по сути эквивалентна «коробке», содержащей 100 банкнот, в то время как каждый биткойн генерирует в 1 000 000 раз больше банкнот.

Другими словами, 226 биткойнов имеют то же количество банкнот, которое Citu получит через 11 лет.

Текущее предложение (150 миллионов монет Citu):
Показаны самые маленькие единицы: Citu: 15 000 000 000 единиц. (рассчитывается как 150 000 000 монет × 100 униts за монету).

Будущее предложение через 11 лет (226 миллионов монет Citu):
Показаны самые маленькие единицы: Citu: 22 600 000 000 единиц. (рассчитывается как 226 000 000 монет × 100 единиц за монету).

Bitcoin:
Показаны самые маленькие единицы: Bitcoin: 2 100 000 000 000 000 000 сатоши.

Ethereum:
Показаны самые маленькие единицы: Ethereum: 120 000 000 000 000 000 000 000 вэй (рассчитывается как 120 000 000 монет × 1 000 000 000 000 000 000 единиц за монету).

Золото:
Общие золотые резервы: 205 000 000 000 граммов.

Если бы золото было распределено:
Распределение золота
Биткойн (сатоши): 0,0000976 граммов за сатоши
Эфириум (wei): 0,00000000000000000017 граммов за wei
Citu (текущий): 13,67 граммов за единицу
Citu (будущий): 9,07 граммов за единицу
Логарифмическая шкала показывает разрыв между валютой Citu и остальными активами, подчеркивая дефицит Citu как сейчас, так и в долгосрочной перспективе.

Сравнение с глобальной ликвидностью
Глобальная ликвидность охватывает общую стоимость всех денег, активов и богатств в мире, оцениваемую в 100 000 000 000 000 долларов США (100 триллионов долларов). Сюда входят:

Наличные и физическая валюта в обращении
Банковские депозиты
Недвижимость, акции и облигации
Другие ликвидные и полуликвидные активы
Сравнение общего количества минимальных единиц для каждой валюты с глобальной ликвидностью:

Биткойн: Общее количество единиц: 2 100 000 000 000 000. Глобальная ликвидность в 21 000 раз меньше общего количества сатоши.
Эфириум: Общее количество единиц: 120 000 000 000 000 000 000 000. Глобальная ликвидность в 1 триллион раз меньше общего количества wei.
Citu (текущий): Общее количество единиц: 15 000 000 000. Глобальная ликвидность в 6 667 раз больше общего количества минимальных единиц Citu.
Citu (будущий): Общее количество единиц: 22 600 000 000. Глобальная ликвидность в 4425 раз больше, чем общее количество минимальных единиц Citu.
Citu
Объяснение глобальной ликвидности
Глобальная ликвидность относится к общей денежной стоимости всех легкодоступных финансовых активов во всем мире, которая оценивается в 100 триллионов долларов США. Эта цифра включает:

Физические наличные: Банкноты и монеты в обращении.
Банковские депозиты: Средства, хранящиеся на текущих, сберегательных и аналогичных счетах.
Финансовые активы: Акции, облигации и другие легко торгуемые ценные бумаги.
Недвижимость: Жилая, коммерческая и промышленная недвижимость, вносящая вклад в глобальную ликвидность.
Товары: Золото, нефть и другие торгуемые на рынке физические активы.
Криптовалюты: Цифровые валюты, такие как Bitcoin, Ethereum и другие, включенные в состав ликвидных цифровых активов.
Глобальная ликвидность является ключевым показателем финансового здоровья мира, отражающим, сколько капитала легко доступно для инвестиций, потребления и торговли.

Состав глобальной ликвидности
Резюме
Номинал валют (например, 0,01 евро или 1 доллар США) влияет только на восприятие, но не на фактическую покупательную способность, при условии применения правильного обменного курса. Для экономики важны общее количество банкнот и их делимость.

Биткоин имеет высокую делимость (8 знаков после запятой), создавая огромное количество единиц (2,1 квадриллиона). Для сравнения, Citu достигнет всего 22,6 миллиарда единиц за 11 лет из-за своей делимости на 2 знака после запятой. Таким образом, каждый биткоин эквивалентен миллиону банкнот Citu, а 226 биткоинов имеют такое же количество банкнот, сколько Citu достигнет за 11 лет.

Нижняя граница цены криптовалюты определяется затратами на электроэнергию для ее майнинга, что отражает трудовую теорию стоимости. Верхняя граница регулируется субъективной теорией стоимости, представляющей ценность, которую пользователи приписывают криптовалюте. Средняя цена будет расти в соответствии с теорией предельной полезности, поскольку майнинг неэластичен, постепенно уменьшаясь, но никогда не достигая нуля. Ежегодный прирост предложения монет значительно меньше 0,005% относительно общего выпуска, компенсируя потерянные монеты и поддерживая расходы майнеров.

Система динамически адаптируется: когда спрос падает, сложность майнинга падает, сокращая выбросы, а во время резкого роста цен сложность растет, стимулируя выпуск монет. Это позволяет системе регулировать обменный курс с помощью рыночных инструментов. Избыточные выбросы направляются на стейкинг, способствуя долгосрочной стабильности.

Эта криптовалюта особенно подходит в качестве резервной валюты. Ее ограниченное предложение делает ее привлекательной из-за ее редкости, повышая ее рыночную стоимость.

### Контакты
discord: https://discord.gg/MqkvC3SGHH
telegram: https://t.me/citu_coin
twitter: @citu4030
wallet: https://github.com/CorporateFounder/unitedStates_final
global server: https://github.com/CorporateFounder/unitedStates_storage

[Возврат на главную](./documentationRus.md)