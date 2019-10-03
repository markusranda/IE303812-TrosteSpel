# IE303812-TrosteSpel-Server
This is the serer which will host the gameserver used in this project.

Dependencies:
  - Vagrant 2.2.5
  - Virtualbox 6.0.4
  - OpenJDK:8

To run this project:
  1. Clone the repository
  2. Open up a terminal
  3. Change directory to the newly cloned project directory.
  4. Run the command "vagrant up".
  5. Then the vagrant will run for a while downloading, installing and configuring the system.
  6. Meanwhile build artifact from the class Main.java with out/artifacts/{JARNAME}/ as destination
  6. When the installation is finished, you can access the server with "vagrant ssh".
  7. Go to "/src/artifacts/{JARNAME}/" and run "java -jar server.jar" to run the server.



opened ports 
  - UDP: [7080, 7081, 7082]
  - TCP: [7083]
