# Block Reward Distribution Mechanism and Mining Model of Cryptocurrency in the Context of Monetarism, Austrian School, and Libertarianism

## Introduction

This article examines the block reward calculation mechanism and the mining model of a cryptocurrency based on principles of self-regulating money supply. We will analyze how the use of economic theories from monetarism, the Austrian school, and libertarianism helps create a stable and effective economic system that supports the stable development of the cryptocurrency market. Special attention will be given to how these principles ensure the stability of the cryptocurrency's value and stimulate economic growth.

Currently, there are about 150 million coins in circulation, and the rate of increase is relatively small compared to other cryptocurrencies, such as Bitcoin and Dogecoin. While each Bitcoin can be divided into 100 million parts and each Dogecoin can also be divided into 100 million parts, our cryptocurrency can only be divided into 100 parts (2 decimal places). Lower divisibility means lower liquidity in the market, as overall liquidity is reduced. Each coin can only be divided into 100 pieces, which creates a scarcity in the market, whereas Dogecoin can be divided into 100 million parts, making it more flexible for trading. We assume that the issuance volume of our cryptocurrency is 100 times smaller, which will allow the value to increase. This makes one Dogecoin a million times more liquid compared to our coin. Moreover, Dogecoin releases 10,000 coins per block, while the current block reward in our network does not exceed 300 coins. A smaller reward means that less money enters circulation, making each coin more valuable. Dogecoin's reward is 10,000 coins per block, whereas ours is significantly smaller, which helps increase the value of our coins. This is due to the gradual reduction of the multiplier each year and the application of Milton Friedman's model to the additional 24 coins, increasing by 2%. This percentage is based on Milton Friedman's monetarist theory, which states that the money supply should grow between 2% and 5%, with the growth rate correlated with GDP. Thus, the overall volume of coin issuance remains stable and does not grow too quickly, contributing to maintaining a stable value and preventing inflation.

## Block Reward Calculation

The block reward is calculated in three stages, each of which considers different economic factors and encourages network participants.

### Stage 1

```
Reward = (5 + coefficient + (difficulty × 0.2) + additional) × multiplier
```

- **Coefficient**: Takes a value of 3 or 0. A value of 3 is assigned if the number and sum of transactions in the current block exceed those in the previous block. This encourages miners to include more transactions, increasing liquidity and reflecting the growing demand for cryptocurrency.
- **Difficulty**: Each participant can set a value between 17 and 100. Difficulty defines the target for the block hash:
    - **Target** = 100 - difficulty
    - The target represents the number of ones in the SHA-256 hash, where the number of ones must be less than or equal to the target. That is, if the difficulty is 20, the number of ones in the bits must be ≤ 100 - 20. When the target is 100 minus the difficulty, if the difficulty reaches 100, the hash must have 0 ones, which is impossible. This makes the mining process more predictable and transparent, as the difficulty limits the achievement of such a target.
- **Multiplier**: Initial value is 29, decreasing by one every one and a half years, but cannot be lower than 1. To maintain the same level of reward, participants need to increase the difficulty. The purpose of the multiplier is to reward early participants, gradually decreasing linearly, thus requiring constant difficulty increases to maintain the level of rewards. The time period is defined as the number of blocks 576 × 360 (days multiplied by the number of days in a year).

### Stage 2

An additional reward is added to compensate for increased difficulty:

```
Additional = (difficulty - 22) / 2
```

If the result is negative, it takes the value 0. This measure encourages miners to work with higher difficulty, compensating their costs and incentivizing maintaining higher difficulty, which helps prevent excessive growth in the money supply.

Updated reward:

```
Reward_2 = Reward + (additional × (coefficient + 6))
```

### Stage 3

Starting from index 326840, an adjustment based on Milton Friedman's model is introduced to ensure stable money supply growth:

```
blocksSinceStart = current index - 326840
year = blocksSinceStart / (432 × 360)
difficultyV2 = max(difficulty - 22, 0)

Result = (24 + (coefficient / 4) + (difficultyV2 / 4)) × (1.02) ^ year
Reward_3 = Reward_2 + Result
```

Rounded to two decimal places.

## Mechanism Explanation

### Coefficient

- **Encouraging activity**: Encouraging miners to include more transactions increases network efficiency.
- **Regulating liquidity**: As the number of transactions grows, the money supply increases, preventing excessive price growth and providing stability, which aligns with the monetarist principles of controlling the money supply.

### Difficulty

- **Demand indicator**: Increasing difficulty signals a rise in demand for cryptocurrency, enabling the network to adapt to changes in market activity.
- **Self-regulation**: This approach aligns with the Austrian school, which advocates for the market to find equilibrium independently, without external interference.

### Multiplier

- **Emission control**: The gradual reduction of the multiplier helps prevent hyperinflation, ensuring stable and controlled growth in the money supply, which reflects monetarist principles.

## Mining Model

A hybrid Proof-of-Work (PoW) and Proof-of-Stake (PoS) model with an element of randomness is used.

### Block selection process

Every 100 seconds, nodes exchange the best blocks. The block with the highest score is selected.

### Score calculation

```
Score = (difficulty × 25) + (random number from 0 to 150) + staking points + transaction points
```

- **Staking points**: An exponential increase in coins is required for staking to gain additional points.
    - 1st point: 1.1 coins
    - 2nd point: 2.1 coins
    - 3rd point: 4.1 coins
    - And so on.

This encourages long-term holding of coins, which aligns with libertarian ideas of freedom and personal responsibility.

### Transaction points

- The sum of transactions from unique senders is considered.
- **Point cost**:
    - 1st point: 0.11 coins
    - 2nd point: 0.21 coins
    - 3rd point: 0.41 coins
    - And so on.

The total number of points for transactions cannot exceed the sum of staking points + diffLimit × 3, where:
- **diffLimit** = difficulty - 19

If diffLimit is less than 0, it takes the value 0.

## Reasons for Using a Complex Mechanism

### Self-regulation and Maintaining Liquidity

The reward mechanism described above is based on the idea that the market should self-regulate. This idea is consistent with the theoretical foundations of the Austrian school of economics. According to the Austrian school, the best way to ensure economic growth and stability is to allow the free market to determine the value of goods and services. Maintaining liquidity and the flexibility of new coin issuance help avoid large fluctuations in value.

This approach also resonates with the monetarist principles of Milton Friedman. The 2% increase in coin issuance annually as the number of blocks grows is a classic example of adaptive monetary policy. This percentage is chosen in accordance with Milton Friedman's monetarist theory, where the money supply should grow between 2% and 5%, correlated with GDP growth, which matches the average rate of economic growth. In real economies, Friedman emphasized the importance of predictable money supply growth, which helps avoid inflationary spikes and supports sustainable economic growth.

### Balance Between Mining and Staking

Mining and staking in this system are closely related and provide a natural balance between liquidity and stability. The PoW model ensures network security and stability, while PoS elements encourage long-term coin holding. This approach is similar to the gold standard, where gold reserves incentivized countries to maintain certain reserves, providing currency stability. Here, staking serves as an analog to holding gold reserves, preventing excessive liquidity in the market.

## Conclusion

Integrating the economic theories of monetarism, the Austrian school, and libertarianism into the technical basis of a cryptocurrency helps create a sustainable and self-regulating economic system. The considered block reward mechanism and hybrid mining model contribute to value stabilization, encourage participant activity, and reflect fundamental economic principles. Sustainable money supply management, balance between liquidity and stability, and limited divisibility make this cryptocurrency attractive to both miners and investors. All of this ensures the efficient functioning of the cryptocurrency and contributes to long-term market development, creating conditions for sustainable economic growth and increased prosperity of network participants.

This model can serve as an example of how modern technology and economic theories can work together to create innovative solutions that not only provide financial stability but also promote economic freedom. Ultimately, the proposed cryptocurrency is capable of becoming a reliable financial instrument that provides decentralization, protects against inflation, and stimulates economic activity, which distinguishes it favorably from traditional monetary systems.


### contacts 
discord: https://discord.gg/MqkvC3SGHH
telegram: https://t.me/citu_coin
twitter: @citu4030
wallet: https://github.com/CorporateFounder/unitedStates_final
global server: https://github.com/CorporateFounder/unitedStates_storage

[Return to main page](./documentationEng.md)