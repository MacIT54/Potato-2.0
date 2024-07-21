# Potato-2.0

В данном репозитории представлена улучшенная версия электронного кошелька "Картошка".
Проект написан в рамках летнего интенсива ЦФТ ШИФТ 2024.

“Картошка” - это отечественный электронный кошелек, который позволяет оплачивать услуги, делать денежные переводы в
рамках системы. 

Вклад в проект:
* проектирование и доработка API
* создание DTO-объектов
* реализация маппинга
* разработка бизнес-логики
* реализация взаимодействия с БД (PostgreSQL)
* настройка Spring Security, для авторизации используется JWT-токен
* внедрение логирования
* написание тестов
* составление json-запросов и работа с ними в Postman
* работа с Git

API и JSON для Postman находятся в корневой папке проекта

# Сертификат
<img align="left" alt="C" src="https://github.com/MacIT54/Potato-2.0/blob/main/docs/Certificate.png" />
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

# API
<img align="left" alt="C" src="https://github.com/MacIT54/Potato-2.0/blob/main/docs/api2.png" />
<img align="left" alt="C" src="https://github.com/MacIT54/Potato-2.0/blob/main/docs/api1.png" />

<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

# Требования к проекту

# Пользователь

## Описание

Основные поля пользователя:

- Фамилия
- Имя
- Отчество (опционально)
- Номер мобильного телефона
- Адрес электронной почты
- Дата рождения
- Пароль

ФИО допускается только буквы русского алфавита. Первые буквы - заглавные. Не более 50 символов по отдельности.
Номер мобильного телефона - Формат телефона: 11 цифр, начинается с '7'
К номеру телефона может быть привязан только один пользователь.
Адрес электронной почты - стандартная маска. К электронной почте может быть привязан только один пользователь.
Дата рождения - дата в ISO 8601
Особые требования к паролю:

- от 8 до 64 символов
- Только латинские символы, цифры, знаки только !?
- Обязательно наличие минимум 1 буквы верхнего и нижнего регистра, цифры и знака.
- Хранение исключительно в БД, в хешированном виде, алгоритм - на усмотрение разработки.

К требованиям хранения пользователей относится хранение временной метки (timestamp) создания и обновления полей
пользователя.
Пользователя запрещено удалять из БД

## Операции с пользователем

- Создание пользователя
- Получение пользователя
- Редактирование полей пользователя (только ФИО, дата рождения)

# Сессия

## Описание

Пользователь имеет неограниченное число сессий.
Сессия принадлежит только одному пользователю.
Сессии имеет ограниченный срок действия.
В БД необходимо хранить все сессии: активные и "протухшие" по времени или выходу из сессии

## Операции с сессией

- Создание сессии (вход в аккаунт)
- Получение информации о сессии
- Выход из сессии (выход из аккаунта)

# Кошелёк

## Описание

Кошельком владеет только один пользователь.
Изначально каждый пользователь имеет 100 д.е. в кошельке.
Основные поля кошелька:

- Номер - целое число.
- Баланс, в денежных единицах (далее д.е.). - целое число, меньше или равно нулю.

## Операции с кошельком

- Получение информации о кошельке
- HESOYAM: с 25% шансом пользователь получает на счёт 10 д.е.

# Счёт на оплату

## Описание

Выражает оплату за услугу между двумя пользователями: продавцом и потребителем.
Продавец выставляет потребителю счёт на оплату.
После выставления счёта потребитель может его оплатить по номеру.
Частичная оплата недопустима.
Пока счёт не оплачен, он может быть отменён получателем

Основные поля счёта на оплату (услуги):

- Номер счёта - UUID, идентифицирует счёт
- Стоимость услуги - целое число, больше нуля
- Идентификатор отправителя (продавца, тот кто выставил счёт)
- Идентификатор получателя (покупателя, тот кто оплачивает счёт)
- Комментарий (опционально)
- Статус - Оплачен/Неоплачен/Отменён
- Дата и время выставления счёта

Комментарий не более 250 символов, строка произвольная.
Дата выставления счёта в ISO 8601 формате.

## Операции со счётом на оплату

1. Создание счёта на оплату
2. Отмена счёта на оплату отправителем
3. Оплата счёта на оплату получателем
4. Получение информации о счёте на оплату
5. Получение всех выставленных счетов
6. Получение всех счетов к оплате
Фильтры к операциям 5-6:
- По статусам счёта (оплачен/неоплачен/отменён)
- По датам выставления счёта с/по
- По идентификатору
7. Получение наиболее давнего неоплаченного счёта к оплате
8. Получение общей задолженности по неоплаченным счетам к оплате

# Денежный перевод

## Описание

Основные поля счёта на денежного перевода:

- Дата и время перевода
- Сумма

Переводы могут быть двух типов:

- Перевод пользователю
- Оплата за услугу

Перевод совершается в д.е. Выполняется сразу после его создания. Пользователь может совершить перевод в д.е. не более,
чем есть у него в кошельке.
К требованиям хранения переводов относится хранение временной метки (timestamp) создания переводов.

### Перевод пользователю

Перевод пользователю можно осуществить:

- По номеру телефона
- По номеру кошелька

### Оплата за услугу

Оплатить услугу можно только по её номеру

## Операции с денежными переводами

- Получение информации о переводе
- Создание (оно же выполнение) денежного перевода
- Получение истории переводов: требуется возможность фильтрации по: типу (входящий/исходящий), статусу (оплачен/не
  оплачен), пользователю-получателю
