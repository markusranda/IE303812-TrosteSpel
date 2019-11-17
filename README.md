Trostespel
-
Files needed to be created before building project, these should be placed in 
root folder and contain provided properties.
- client.properties
  - host = [server_ip]
  - CLIENT_TCP_MESSAGE_RECEIVE_PORT      = [portnumber]
  - CLIENT_UDP_GAMEDATA_PORT             = [portnumber]
  - CLIENT_TCP_CONNECTION_RECEIVE_PORT   = [portnumber]
- server.properties
  - SERVER_UDP_GAMEDATA_RECEIVE_PORT     = [portnumber]
  - SERVER_TCP_CONNECTION_RECEIVE_PORT   = [portnumber]
  
  
The remoteDeploy Gradle task pushes and executes the server jar to your remote server.
To use this task, create following file in root folder with the provided properties
- sshprofile.properties
    - user       = [your_username]
    - password   = [your_password]
    - host       = [server_ip]

![](https://i.imgur.com/nhezhDD.png)
![](https://i.imgur.com/1bfFCTh.png)
![](https://i.imgur.com/jx84ioD.png)
