# API Documentation

## Node API

### GET /datashort
Returns blockchain meta data including height, hashCount, staking, transactions, bigRandomNumber, and validation.

`http://{{host}}:{{port}}/datashort`

### GET /prevBlock
Returns the last block.

`http://{{host}}:{{port}}/prevBlock`

### GET /conductorBlock
Returns a block at the specified index. Note: Genesis block has index 1.

`http://{{host}}:{{port}}/conductorBlock?index=263899`

### GET /status
Returns the current status of the node.

`http://{{host}}:{{port}}/status`

### GET /addresses
Returns a list of all addresses with their accounts. Not recommended due to large size.

`http://{{host}}:{{port}}/addresses`

### GET /totalDollars
Returns the sum of all dollars in the system.

`http://{{host}}:{{port}}/totalDollars`

### GET /size
Returns the height of the blockchain.

`http://{{host}}:{{port}}/size`

### GET /version
Returns the special version that differs from Github.

`http://{{host}}:{{port}}/version`

### GET /difficultyBlockchain
Returns the difficulty of the last block and the sum of all difficulties in this blockchain.

`http://{{host}}:{{port}}/difficultyBlockchain`

### POST /sub-blocks
Returns a list of blocks from start to finish index, inclusive.

`http://{{host}}:{{port}}/sub-blocks`

Payload:
```json
{
    "start": 0,
    "finish": 10
}
```

### GET /getTransactions
Returns a list of transactions that have not yet been added to the blockchain.

`http://{{host}}:{{port}}/getTransactions`

### GET /senderTransactions
Returns all sent transactions of a given address from the specified block range.

`http://{{host}}:{{port}}/senderTransactions?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43&from=0&to=10`

### GET /customerTransactions
Returns all received transactions of a given address from the specified block range.

`http://{{host}}:{{port}}/customerTransactions?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43&from=0&to=10`

### GET /senderCountDto
Returns the sum of all transactions sent by this address.

`http://{{host}}:{{port}}/senderCountDto?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43`

### GET /isSaveFile
Checks if the file is saved.

`http://{{host}}:{{port}}/isSaveFile`

### POST /isTransactionAddBase64
Determines if a given transaction is added to the blockchain based on the base64 signature.

`http://{{host}}:{{port}}/isTransactionAddBase64`

Payload:
```json
{
    "sign": "MEUCIQCikyqSQ+/chwaQlqx0MAU6yKM9hBqDIKsn9Dudg/F37AIgUpbfRPt+nGlHLYuL0FStgsm5epJ8Jr1TsIeYKxXEkMg="
}
```

### POST /TransactionAddBase64
Returns the transaction based on its base64 signature if it's in the blockchain.

`http://{{host}}:{{port}}/TransactionAddBase64`

Payload:
```json
{
    "sign": "MEUCIQCikyqSQ+/chwaQlqx0MAU6yKM9hBqDIKsn9Dudg/F37AIgUpbfRPt+nGlHLYuL0FStgsm5epJ8Jr1TsIeYKxXEkMg="
}
```

### GET /account
Returns the balance of an account (Dollar, Promotions, Staking).

`http://{{host}}:{{port}}/account?address=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg`

### GET /customerCountDto
Returns the sum of all transactions received by this address.

`http://{{host}}:{{port}}/customerCountDto?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43`

### GET /allwinners
Returns a list of block candidates that participated in the last tournament.

`http://{{host}}:{{port}}/allwinners`

### GET /bigRandomWiner
Returns the winner from the last tournament.

`http://{{host}}:{{port}}/bigRandomWiner`

### GET /winnerList
Returns the strongest candidate from the list of blocks that have not yet entered the tournament.

`http://{{host}}:{{port}}/winnerList`

### POST /isTransactionAdd
Returns true or false if the transaction is added to the blockchain, takes base58 as input.

`http://{{host}}:{{port}}/isTransactionAdd`

Payload:
```json
{
    "sign": "381yXZJ8ERboZbDj9nf2pZkvjLeMTZAeuhzMgfipCP9minvaLfdbBy6LWr8X7wpKPy6pBCUH2HUGu2TeK9hqGs5ZiVcFhKSo"
}
```

### POST /TransactionAddBase
Returns a transaction object if the transaction has been added to the blockchain, if not null, takes base58 as input.

`http://{{host}}:{{port}}/TransactionAddBase`

Payload:
```json
{
    "sign": "381yXZJ8ERboZbDj9nf2pZkvjLeMTZAeuhzMgfipCP9minvaLfdbBy6LWr8X7wpKPy6pBCUH2HUGu2TeK9hqGs5ZiVcFhKSo"
}
```

### POST /findBlocksFromSign58
Returns a list of blocks if this transaction is present in the blockchain, otherwise an empty list. Takes base58 as input.

`http://{{host}}:{{port}}/findBlocksFromSign58`

Payload:
```json
{
    "sign": "381yXZJ8ERboZbDj9nf2pZkvjLeMTZAeuhzMgfipCP9minvaLfdbBy6LWr8X7wpKPy6pBCUH2HUGu2TeK9hqGs5ZiVcFhKSo"
}
```

### POST /findBlocksFromSign64
Returns a list of blocks if this transaction is present in the blockchain, otherwise an empty list. Takes base64 as input.

`http://{{host}}:{{port}}/findBlocksFromSign64`

Payload:
```json
{
    "sign": "MEYCIQCFc+Jzdf/qDO43BefYb4TbMmQk3MpwEH4y14ScL8M4GQIhALse6+GyeG9YnHmihIUgJI3Z4L6S9syFL4Bxyu1hDrjT"
}
```

### POST /statusTransaction58
Returns the status of a transaction based on base58 signature.

`http://{{host}}:{{port}}/statusTransaction58`

Payload:
```json
{
    "sign": "381yXZJ8ERboZbDj9nf2pZkvjLeMTZAeuhzMgfipCP9minvaLfdbBy6LWr8X7wpKPy6pBCUH2HUGu2TeK9hqGs5ZiVcFhKSo"
}
```
Status values:
1. Updating information
2. Incorrect signature
3. Success
4. Pending
5. Absent

### POST /statusTransaction64
Returns the status of a transaction based on base64 signature.

`http://{{host}}:{{port}}/statusTransaction64`

Payload:
```json
{
    "sign": "MEYCIQCFc+Jzdf/qDO43BefYb4TbMmQk3MpwEH4y14ScL8M4GQIhALse6+GyeG9YnHmihIUgJI3Z4L6S9syFL4Bxyu1hDrjT"
}
```
Status values:
1. Updating information
2. Incorrect signature
3. Success
4. Pending
5. Absent

## Wallet API

### GET /keys
Returns the public key and private key (password).

`localhost:8082/keys`

### GET /account
Returns the balance of an account (Dollar, Promotions, Staking).

`localhost:8082/account?address=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg`

### GET /dollar
Returns the dollar balance only.

`localhost:8082/dollar?address=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg`

### GET /sendCoin
Sends coins from one address to another.

`localhost:8082/sendCoin?sender=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg&recipient=rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM&dollar=1&stock=2.0&reward=0.0&password=`

### GET /stock
Returns the stock balance.

`localhost:8082/stock?address=tiwnwfxx3JjKaDi6ZmRihH9PNE5GZXcLNYqGrm7Rwxjg`

### POST /isTransactionAddBase64
Determines if a given transaction is added to the blockchain based on the base64 signature.

`localhost:8082/isTransactionAddBase64`

Payload:
```json
{
    "sign": "MEUCIQCikyqSQ+/chwaQlqx0MAU6yKM9hBqDIKsn9Dudg/F37AIgUpbfRPt+nGlHLYuL0FStgsm5epJ8Jr1TsIeYKxXEkMg="
}
```

### POST /TransactionAddBase64
Returns the transaction based on its base64 signature if it's in the blockchain.

`localhost:8082/TransactionAddBase64`

Payload:
```json
{
    "sign": "MEUCIQCikyqSQ+/chwaQlqx0MAU6yKM9hBqDIKsn9Dudg/F37AIgUpbfRPt+nGlHLYuL0FStgsm5epJ8Jr1TsIeYKxXEkMg="
}
```

### GET /senderCountDto
Returns the sum of all transactions sent by this address.

`localhost:8082/senderCountDto?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43`

### GET /senderTransactions
Returns all sent transactions of a given address from the specified block range. Limitation: the `to` parameter cannot be more than 500 blocks larger than the `from` parameter.

`localhost:8082/senderTransactions?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43&from=0&to=10`

### GET /customerTransactions
Returns all received transactions of a given address from the specified block range. Limitation: the `to` parameter cannot be more than 500 blocks larger than the `from` parameter.

`localhost:8082/customerTransactions?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43&from=0&to=10`

### GET /customerCountDto
Returns the sum of all transactions received by this address.

`localhost:8082/customerCountDto?address=nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43`


[Return to main page](./documentationEng.md)