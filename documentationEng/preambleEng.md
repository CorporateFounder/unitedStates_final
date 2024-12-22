# Instructions for Working with the Points System and Block Complexity

Instructions for Working with the Points System and Block Complexity
Reward Calculation
Reward Formula:
blocksSinceStart = current block index - 326840
year = blocksSinceStart / (432 × 360)
difficultyV2 = max(complexity - 22, 0)
Result = (3 + (coefficient / 4) + (difficultyV2 / 4)) × (1.005) ^ year
Reward = Result × Multiplier (where multiplier ≥ 1)
Coefficient:
Possible values: 3 or 0
Encourages transactional activity
3 if unique transactions and volume increase over the previous block
0 if they remain the same or decrease
Excludes BASE ADDRESS and duplicate addresses
Multiplier:
Initial value: 35, linearly decreases every 4 months.
Hybrid Consensus System
The system uses a combination of PoW and PoS to ensure security.

Player Points Formation:
Points = complexity points + random number + staking points + transaction points

Staking Points:
Exponential scale:
1st point: 1.1 coins
2nd point: 2.1 coins
3rd point: 4.1 coins
Maximum: 30 points.
Transaction Points:
Calculated based on the transaction amounts from unique senders:
1st point: 0.11 coins
2nd point: 0.21 coins
3rd point: 0.41 coins
Maximum: points cannot exceed staking points.
Complexity and Groups:
Every 100-230 seconds, 1 block is selected by nodes with the highest points.
Parameters of Complexity and Hash Goal
Each participant can send blocks with complexity from 17 to 100.

Valid Hash: A hash where the number of units in bits is less than or equal to the value:
Goal = 100 - Complexity.
Randomness Generation:
The random number is generated based on the block hash, where the hash serves as the seed for deterministic randomness. The range for the random number is fixed from 0 to 170.

New Scoring Model
The scoring model calculates points for players based on the following:

Difficulty Points:
Calculated as: difficulty * 15.
Transaction Points:
Determined by a pre-defined formula (already implemented).
Capped at the value of mineScore.
Hash Complexity Value (X):
Determined by the formula: 170.
Random Range:
Fixed range: 0 to 170.
A random number is selected within this range.
Total Points:
Final calculation: Total Points = Difficulty Points + Random Value + Transaction Points + mineScore.
Example Workflow
Retrieve the current hash complexity.
Calculate Difficulty Points: Multiply hash complexity by 15.
Use a fixed range of 0 to 170 for random number generation.
Select a random number within the fixed range.
Combine all components to determine the total points.
Key Notes
Transaction Points are pre-calculated and capped by mineScore.
The randomness range is always fixed between 0 and 170, regardless of complexity.
All calculations must use deterministic randomness for consistency.
Citu Supply Comparison
Conclusion
Each coin has only 2 decimal places.

Two Villages
In the EUR village:
They issued 1,000 banknotes.
The denomination of each banknote is 0.01 EUR.
Total currency volume: 1,000 × 0.01 = 10 EUR.
In the USD village:
They also issued 1,000 banknotes.
The denomination of each banknote is 1 USD.
Total currency volume: 1,000 × 1 = 1,000 USD.
At first glance, it seems that the currency volume differs: 10 EUR versus 1,000 USD. However, it is essential to consider the exchange rate.

Exchange and Parity
When the villagers decided to trade, they agreed that:

1 USD = 0.01 EUR.
Therefore, economic parity is fully preserved. For example:

A villager from the EUR village buys green sneakers from the USD village for 1 USD. To pay, they give 1 banknote of 0.01 EUR.
A villager from the USD village buys a red T-shirt for 0.01 EUR. To pay, they give 1 banknote of 1 USD.
Thus, despite different banknote denominations, the actual volume and purchasing power of the currencies remain equivalent since the exchange rate accounts for the nominal differences.

Economic Conclusion
The total number of banknotes (1,000) is the same.
The overall purchasing power of the currencies is equivalent when the exchange rate is applied.
Therefore, the economies of the two villages are symmetric, and the denomination of banknotes only affects perception.

Comparison with Bitcoin
Bitcoin has 8 decimal places, meaning it can be divided into 0.00000001 BTC, or 100,000,000 units per coin. Thus, the total number of units in the Bitcoin system equals:

2,100,000,000,000,000 satoshis. (calculated as 21,000,000 coins × 100,000,000 units per coin).
In comparison, Citu, even in 11 years, will reach a total of 22,600,000,000 units (calculated as 226,000,000 coins × 100 units per coin).

When people say that only 3 bitcoins are mined per block, this is equivalent to 3 "boxes," each containing 100,000,000 banknotes. Thus, one Citu coin is essentially equivalent to a "box" containing 100 banknotes, while each bitcoin generates 1,000,000 times more banknotes.

In other words, 226 bitcoins have the same number of banknotes as Citu will achieve in 11 years.

Current Supply (150 Million Citu Coins):
The smallest units are shown: Citu: 15,000,000,000 units. (calculated as 150,000,000 coins × 100 units per coin).

Future Supply in 11 Years (226 Million Citu Coins):
The smallest units are shown: Citu: 22,600,000,000 units. (calculated as 226,000,000 coins × 100 units per coin).

Bitcoin:
The smallest units are shown: Bitcoin: 2,100,000,000,000,000 satoshis.

Ethereum:
The smallest units are shown: Ethereum: 120,000,000,000,000,000,000,000 wei (calculated as 120,000,000 coins × 1,000,000,000,000,000,000 units per coin).

Gold:
Total gold reserves: 205,000,000,000 grams.

If Gold Were Distributed:
Gold Distribution
Bitcoin (satoshis): 0.0000976 grams per satoshi
Ethereum (wei): 0.0000000000000000017 grams per wei
Citu (current): 13.67 grams per unit
Citu (future): 9.07 grams per unit
The logarithmic scale shows the gap between the Citu currency and the rest of the assets, highlighting the scarcity of Citu both now and in the long term.

Comparison with Global Liquidity
Global liquidity encompasses the total value of all money, assets, and wealth in the world, estimated at 100,000,000,000,000 USD (100 trillion dollars). This includes:

Cash and physical currency in circulation
Bank deposits
Real estate, stocks, and bonds
Other liquid and semi-liquid assets
Comparing the total number of minimal units for each currency to global liquidity:

Bitcoin: Total units: 2,100,000,000,000,000. Global liquidity is 21,000 times smaller than the total satoshis.
Ethereum: Total units: 120,000,000,000,000,000,000,000. Global liquidity is 1 trillion times smaller than the total wei.
Citu (current): Total units: 15,000,000,000. Global liquidity is 6,667 times larger than the total minimal units of Citu.
Citu (future): Total units: 22,600,000,000. Global liquidity is 4,425 times larger than the total minimal units of Citu.
Citu
Explanation of Global Liquidity
Global liquidity refers to the total monetary value of all easily accessible financial assets worldwide, which is estimated at 100 trillion USD. This figure includes:

Physical cash: Banknotes and coins in circulation.
Bank deposits: Funds held in checking, savings, and similar accounts.
Financial assets: Stocks, bonds, and other easily tradeable securities.
Real estate: Residential, commercial, and industrial properties contributing to global liquidity.
Commodities: Gold, oil, and other market-traded physical assets.
Cryptocurrencies: Digital currencies like Bitcoin, Ethereum, and others included as part of liquid digital assets.
Global liquidity is a key measure of the world's financial health, reflecting how much capital is readily available for investment, consumption, and trade.

Global Liquidity Composition
Summary
The denomination of currencies (e.g., 0.01 EUR or 1 USD) affects only perception but not actual purchasing power, provided the correct exchange rate is applied. For an economy, the total number of banknotes and their divisibility matter.

Bitcoin has high divisibility (8 decimal places), creating a massive number of units (2.1 quadrillion). In comparison, Citu will reach only 22.6 billion units in 11 years due to its 2 decimal place divisibility. Thus, each bitcoin is equivalent to a million Citu banknotes, and 226 bitcoins have the same number of banknotes as Citu will achieve in 11 years.

The lower boundary of the cryptocurrency price is determined by the electricity costs for its mining, reflecting the labor theory of value. The upper boundary is governed by the subjective theory of value, representing the value users attribute to the cryptocurrency. The average price will grow in accordance with the marginal utility theory since mining is inelastic, gradually decreasing, but never reaching zero. The annual coin supply increase is significantly less than 0.005% relative to the total issuance, offsetting lost coins and supporting miner expenses.

The system dynamically adapts: when demand decreases, mining complexity decreases, reducing emissions, and during a sharp price increase, complexity grows, stimulating coin issuance. This allows the system to regulate the exchange rate using market tools. Excess emissions are directed to staking, promoting long-term stability.

This cryptocurrency is especially suitable as a reserve currency. Its limited supply makes it attractive due to its scarcity, enhancing its market value.

### contacts 
discord: https://discord.gg/MqkvC3SGHH
telegram: https://t.me/citu_coin
twitter: @citu4030
wallet: https://github.com/CorporateFounder/unitedStates_final
global server: https://github.com/CorporateFounder/unitedStates_storage

[Return to main page](./documentationEng.md)