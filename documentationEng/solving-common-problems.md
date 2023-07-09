# SOLUTIONS TO SPECIAL PROBLEMS

## 1. Problem with the port.
If you see this error on the command line, then take this port.
This problem occurs if you are on the same computer twice without closing the previous
command line trying to run the program. You need to restart your computer.
***************************
APPLICATION FAILED TO START
***************************
Description:
Web server failed to start. Port 8082 was already in use.
action:
Identify and stop the process that's listening on port 8082 or configure this application to listen on another port.

## 2. Sometimes the balance display disappears.
This occurs when, for some reason, the blockchain is incorrectly recorded.
The actual blockchain is always stored in the global server.
And to restore your balance, it is enough to update the blockchain on the main page.

## 3. Your local blockchain outperforms the global one and then gets deleted and part of the balance is lost.
The system after each finding of the block tries to connect to the global network for one minute,
transfer the actual blockchain there. If, for example, there are 20 blocks on the global server,
and you are trying to add N blocks, but their index continues the global blockchain. 21, 22, .... etc.
Then when your wallet can connect, your block will be added.
If your branch is different from the global one and perhaps someone has already added 21 blocks, then your
blocks are removed and your balance is lost because of this. How so your balance contained not up-to-date
blockchain.