# Instructions for Working with the Points System and Block Complexity

## Parameters of Complexity and Hash Goal

Each participant can send blocks with complexity from **17 to 100**.

- **Valid Hash**: A hash where the number of units in bits is less than or equal to the value:

  **Goal = 100 - Complexity.**

---

## Reward Calculation

1. **Reward Formula:**

  - `blocksSinceStart` = current block index - 326840
  - `year` = `blocksSinceStart` / (432 × 360)
  - `difficultyV2` = max(complexity - 22, 0)

   **Result** = (3 + (coefficient / 4) + (difficultyV2 / 4)) × (1.005) ^ `year`

   **Reward** = `Result` × `Multiplier` (where multiplier ≥ 1)

2. **Coefficient:**
  - Possible values: 3 or 0, encourages transactional activity.

3. **Multiplier:**
  - Initial value: 65, linearly decreases every 4 months.

---

## Hybrid Consensus System

The system uses a combination of PoW and PoS to ensure security.

### Player Points Formation:

**Points** = complexity points + random number + staking points + transaction points

1. **Staking Points:**
  - Exponential scale:
    - 1st point: 1.1 coins
    - 2nd point: 2.1 coins
    - 3rd point: 4.1 coins
    - Maximum: 30 points.

2. **Transaction Points:**
  - Calculated based on the transaction amounts from unique senders:
    - 1st point: 0.11 coins
    - 2nd point: 0.21 coins
    - 3rd point: 0.41 coins
    - Maximum: points cannot exceed staking points.

3. **Complexity and Groups:**
  - Every 100-230 seconds, 1 block is selected by nodes with the highest points.

---

## Complexity and Parameter Table

| Group | Complexity | X   |
|-------|------------|-----|
| M+1   | 25         | 131 |
| M     | 20         | 126 |
| M-1   | 20         | 120 |
| M+2   | 15         | 115 |
| M-2   | 15         |  97 |
| M+3   | 10         | 110 |
| M-3   | 10         |  84 |
| M+4   | 5          | 100 |
| M-4   | 5          |  72 |
| M+5   | 0          |  64 |
| M>5   | 0          |  62 |
| M-5   | 0          |  64 |
| M<-5  | 0          |  62 |

### Player Total Points Calculation

1. Determine **Difficulty Point** and **X** from the table.
2. Calculate:
  - **Result** = `X` - (staking points + transaction points).
  - **Random Point** = random number from 0 to `Result`.
3. Total Points:
  - **Total Point** = Difficulty Point + Random Point + staking points + transaction points.

Example: Player in group M+1:

- **Difficulty Point** = 25
- **X** = 131
- **Staking Point** = 20
- **Transaction Point** = 10

Calculation:
- Result = 131 - (20 + 10) = 101
- Random Point — random number from 0 to 101
- Total Point = 25 + Random Point + 20 + 10

---

## Determining Mode M

**M** is calculated based on the mode of block complexity values:

1. Take the current block index. For example, 260.
2. Roll back 30 blocks (to 230) and select blocks from 200 to 230.
3. Calculate the mode (most frequent complexity value).
4. If there are multiple modes with equal frequency, choose the smallest.

---

Each coin has only 2 decimal places.

The lower boundary of the cryptocurrency price is determined by the electricity costs for its mining, reflecting the labor theory of value. The upper boundary is governed by the subjective theory of value, representing the value users attribute to the cryptocurrency. The average price will grow in accordance with the marginal utility theory since mining is inelastic, gradually decreasing, but never reaching zero. The annual coin supply increase is significantly less than 0.005% relative to the total issuance, offsetting lost coins and supporting miner expenses.

The system dynamically adapts: when demand decreases, mining complexity decreases, reducing emissions, and during a sharp price increase, complexity grows, stimulating coin issuance. This allows the system to regulate the exchange rate using market tools. Excess emissions are directed to staking, promoting long-term stability.

This cryptocurrency is especially suitable as a reserve currency. Its limited supply makes it attractive due to its scarcity, enhancing its market value.

This instruction describes the mechanisms of the system to ensure fairness and increase participant activity.



### contacts 
discord: https://discord.gg/MqkvC3SGHH
telegram: https://t.me/citu_coin
twitter: @citu4030
wallet: https://github.com/CorporateFounder/unitedStates_final
global server: https://github.com/CorporateFounder/unitedStates_storage

[Return to main page](./documentationEng.md)