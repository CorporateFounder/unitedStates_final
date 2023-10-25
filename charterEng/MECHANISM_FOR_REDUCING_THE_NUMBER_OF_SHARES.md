# Penalty mechanism

You make a transaction in which you lose this amount of shares, but
and the account to which the penalty is directed loses such an amount of shares.

Valid for digital dollars only.
![Lead fine](../screenshots/lead_a_fine_eng.png)
______

## MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES MECHANISM FOR REDUCING THE NUMBER OF SHARES. Entering fines.
Every time one account sends a digital share to another account but uses VoteEnum.NO, the account
The recipient's digital shares are reduced by the number sent by the sender of the shares.
Example account A sent 100 digital shares to account B with VoteEnum.NO, then account A and account B both lose 100
digital shares. This measure is needed to provide a mechanism for removing the Board of Shareholders from office and also allows for votes to be reduced
destructive accounts, since the number of votes is equal to the number of shares,
when electing CORPORATE_COUNCIL_OF_REFEREES, and other positions that are elected by shares.
This mechanism only applies to digital shares and only if the sender has completed a transaction with
VoteEnum.NO.

[exit to home](../documentationEng/documentationEng.md)