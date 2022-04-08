# Info4B Project

## Client

### Quick start

1. Launch the client with `java client <serverIP>` and replace `<serverIP>` with your server public IP.
2. Type `help` to prompt the list of commands you can use in the client.
3. Type `exit` to quit.

## Server

### Quick start

1. Place all your .pgn files in Src/
2. Launch DBReader2 with `java DBReader2 <nbThread>` and replace `<nbThread>` with the number of cores your CPU has.
3. Wait for DBReader2 to finish. For each .pgn file in Src/ you should have 3 .dat files created.
4. You're all done, you can now start the server with `java server`.
5. The server shoud start and prompt "Server listenning on port 1085".

### Disclaimer

Don't start the server without first running DBReader2 ! The server can't work without the .dat files !
