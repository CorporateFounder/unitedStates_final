# Документация API

## Node API

### GET /datashort
Возвращает метаданные блокчейна, включая высоту, hashCount, стейкинг, транзакции, bigRandomNumber и валидацию.

`http://{{host}}:{{port}}/datashort`

### GET /prevBlock
Возвращает последний блок.

`http://{{host}}:{{port}}/prevBlock`

### GET /conductorBlock
Возвращает блок с указанным индексом. Обратите внимание: генезис-блок имеет индекс 1.

`http://{{host}}:{{port}}/conductorBlock?index=263899`

### GET /status
Возвращает текущий статус узла.

`http://{{host}}:{{port}}/status`

### GET /addresses
Возвращает список всех адресов с их учетными записями. Не рекомендуется из-за большого размера.

`http://{{host}}:{{port}}/addresses`

### GET /totalDollars
Возвращает сумму всех долларов в системе.

`http://{{host}}:{{port}}/totalDollars`

### GET /size
Возвращает высоту блокчейна.

`http://{{host}}:{{port}}/size`

### GET /version
Возвращает специальную версию, отличающуюся от Github.

`http://{{host}}:{{port}}/version`

### GET /difficultyBlockchain
Возвращает сложность последнего блока и сумму всех сложностей в этом блокчейне.

`http://{{host}}:{{port}}/difficultyBlockchain`

### POST /sub-blocks
Возвращает список блоков от начального до конечного индекса включительно.

`http://{{host}}:{{port}}/sub-blocks`

Тело запроса:
```json
{
    "start": 0,
    "finish": 10
}
```

### GET /getTransactions
Возвращает список транзакций, которые еще не добавлены в блокчейн.

`http://{{host}}:{{port}}/getTransactions`

### GET /senderTransactions
Возвращает все отправленные транзакции указанного адреса в заданном диапазоне блоков.

`http://{{host}}:{{port}}/senderTransactions?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43&from=0&to=10`

### GET /customerTransactions
Возвращает все полученные транзакции указанного адреса в заданном диапазоне блоков.

`http://{{host}}:{{port}}/customerTransactions?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43&from=0&to=10`

### GET /senderCountDto
Возвращает сумму всех транзакций, отправленных с данного адреса.

`http://{{host}}:{{port}}/senderCountDto?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43`

### GET /isSaveFile
Проверяет, сохранен ли файл.

`http://{{host}}:{{port}}/isSaveFile`

### POST /isTransactionAddBase64
Определяет, добавлена ли данная транзакция в блокчейн, используя base64-подпись.

`http://{{host}}:{{port}}/isTransactionAddBase64`

Тело запроса:
```json
{
    "sign": "MEUCIQCikyqSQ+/chwaQlqx0MAU6yKM9hBqDIKsn9Dudg/F37AIgUpbfRPt+nGlHLYuL0FStgsm5epJ8Jr1TsIeYKxXEkMg="
}
```

### POST /TransactionAddBase64
Возвращает транзакцию по ее base64-подписи, если она находится в блокчейне.

`http://{{host}}:{{port}}/TransactionAddBase64`

Тело запроса:
```json
{
    "sign": "MEUCIQCikyqSQ+/chwaQlqx0MAU6yKM9hBqDIKsn9Dudg/F37AIgUpbfRPt+nGlHLYuL0FStgsm5epJ8Jr1TsIeYKxXEkMg="
}
```

### GET /account
Возвращает баланс учетной записи (доллары, промоакции, стейкинг).

`http://{{host}}:{{port}}/account?address=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg`

### GET /customerCountDto
Возвращает сумму всех транзакций, полученных этим адресом.

`http://{{host}}:{{port}}/customerCountDto?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43`

### GET /allwinners
Возвращает список кандидатов на блок, участвовавших в последнем турнире.

`http://{{host}}:{{port}}/allwinners`

### GET /bigRandomWiner
Возвращает победителя последнего турнира.

`http://{{host}}:{{port}}/bigRandomWiner`

### GET /winnerList
Возвращает самого сильного кандидата из списка блоков, которые еще не вошли в турнир.

`http://{{host}}:{{port}}/winnerList`

### POST /isTransactionAdd
Возвращает true или false, если транзакция добавлена в блокчейн. Принимает base58 в качестве входных данных.

`http://{{host}}:{{port}}/isTransactionAdd`

Тело запроса:
```json
{
    "sign": "381yXZJ8ERboZbDj9nf2pZkvjLeMTZAeuhzMgfipCP9minvaLfdbBy6LWr8X7wpKPy6pBCUH2HUGu2TeK9hqGs5ZiVcFhKSo"
}
```

### POST /TransactionAddBase
Возвращает объект транзакции, если транзакция была добавлена в блокчейн, иначе null. Принимает base58 в качестве входных данных.

`http://{{host}}:{{port}}/TransactionAddBase`

Тело запроса:
```json
{
    "sign": "381yXZJ8ERboZbDj9nf2pZkvjLeMTZAeuhzMgfipCP9minvaLfdbBy6LWr8X7wpKPy6pBCUH2HUGu2TeK9hqGs5ZiVcFhKSo"
}
```

### POST /findBlocksFromSign58
Возвращает список блоков, если эта транзакция присутствует в блокчейне, иначе пустой список. Принимает base58 в качестве входных данных.

`http://{{host}}:{{port}}/findBlocksFromSign58`

Тело запроса:
```json
{
    "sign": "381yXZJ8ERboZbDj9nf2pZkvjLeMTZAeuhzMgfipCP9minvaLfdbBy6LWr8X7wpKPy6pBCUH2HUGu2TeK9hqGs5ZiVcFhKSo"
}
```

### POST /findBlocksFromSign64
Возвращает список блоков, если эта транзакция присутствует в блокчейне, иначе пустой список. Принимает base64 в качестве входных данных.

`http://{{host}}:{{port}}/findBlocksFromSign64`

Тело запроса:
```json
{
    "sign": "MEYCIQCFc+Jzdf/qDO43BefYb4TbMmQk3MpwEH4y14ScL8M4GQIhALse6+GyeG9YnHmihIUgJI3Z4L6S9syFL4Bxyu1hDrjT"
}
```

### POST /statusTransaction58
Возвращает статус транзакции по base58-подписи.

`http://{{host}}:{{port}}/statusTransaction58`

Тело запроса:
```json
{
    "sign": "381yXZJ8ERboZbDj9nf2pZkvjLeMTZAeuhzMgfipCP9minvaLfdbBy6LWr8X7wpKPy6pBCUH2HUGu2TeK9hqGs5ZiVcFhKSo"
}
```
Значения статуса:
1. Обновление информации
2. Неверная подпись
3. Успешно добавлено
4. Ожидает добавления
5. Отсутствует

### POST /statusTransaction64
Возвращает статус транзакции по base64-подписи.

`http://{{host}}:{{port}}/statusTransaction64`

Тело запроса:
```json
{
    "sign": "MEYCIQCFc+Jzdf/qDO43BefYb4TbMmQk3MpwEH4y14ScL8M4GQIhALse6+GyeG9YnHmihIUgJI3Z4L6S9syFL4Bxyu1hDrjT"
}
```
Значения статуса:
1. Обновление информации
2. Неверная подпись
3. Успешно добавлено
4. Ожидает добавления
5. Отсутствует

## Wallet API

### GET /keys
Возвращает открытый ключ и закрытый ключ (пароль).

`localhost:8082/keys`

### GET /account
Возвращает баланс учетной записи (доллары, промоакции, стейкинг).

`localhost:8082/account?address=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg`

### GET /dollar
Возвращает только баланс в долларах.

`localhost:8082/dollar?address=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg`

### GET /sendCoin
Отправляет монеты с одного адреса на другой.

`localhost:8082/sendCoin?sender=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg&recipient=rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM&dollar=1&stock=2.0&reward=0.0&password=`

### GET /stock
Возвращает баланс акций.

`localhost:8082/stock?address=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg`

### POST /isTransactionAddBase64
Определяет, добавлена ли данная транзакция в блокчейн по base64-подписи.

`localhost:8082/isTransactionAddBase64`

Тело запроса:
```json
{
    "sign": "MEUCIQCikyqSQ+/chwaQlqx0MAU6yKM9hBqDIKsn9Dudg/F37AIgUpbfRPt+nGlHLYuL0FStgsm5epJ8Jr1TsIeYKxXEkMg="
}
```

### POST /TransactionAddBase64
Возвращает транзакцию по ее base64-подписи, если она находится в блокчейне.

`localhost:8082/TransactionAddBase64`

Тело запроса:
```json
{
    "sign": "MEUCIQCikyqSQ+/chwaQlqx0MAU6yKM9hBqDIKsn9Dudg/F37AIgUpbfRPt+nGlHLYuL0FStgsm5epJ8Jr1TsIeYKxXEkMg="
}
```

### GET /senderCountDto
Возвращает сумму всех транзакций, отправленных с данного адреса.

`localhost:8082/senderCountDto?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43`

### GET /senderTransactions
Возвращает все отправленные транзакции указанного адреса в заданном диапазоне блоков. Ограничение: параметр `to` не может быть больше `from` более чем на 500 блоков.

`localhost:8082/senderTransactions?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43&from=0&to=10`

### GET /customerTransactions
Возвращает все полученные транзакции указанного адреса в заданном диапазоне блоков. Ограничение: параметр `to` не может быть больше `from` более чем на 500 блоков.

`localhost:8082/customerTransactio
[Возврат на главную](./documentationRus.md)