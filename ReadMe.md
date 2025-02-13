# Chat con arquitectura Cliente/Servidor 🤓🥵

## Flujo del programa 🖥️

- El servidor se iniciará con 10 hilos (de límite) en pausa (para que se conecten los usuarios) asegurándose de que cada vez que se desconecte un usuario el hilo quede disponible para otro.

- Cada cliente enviará un nickname que el servidor asignará al 1er hilo que esté disponible.

- Cada mensaje se guardará en un hashMap teniendo como clave el nickname.

- Mostrar todos los mensajes en las terminales de cada cliente, pero intentando que no se muestren todos cada vez que el se escribe uno nuevo.

- Si se intenta conectar un nuevo usuario pasando el límite se le postrará un mensaje por parte del server de que vuelva más tarde.

## Características 🔑

- Los comandos se pondrán con el caracter **/** por lo que si no se ponen ningún comando (por ejemplo /bye) se escaneará la cadena de texto para indicarle al servidor que no es un comando sino que es solo un caracter más.

- Ponerle al cliente cada vez que se conecta un texto de normas como respetar a cada integrante **(pero solo en la terminal del cliente que se conecta)**.

## Comandos 🔡
- help -> muestra los comandos existentes.
- all -> muestra todos los usuarios existentes.
- bye -> se desconecta del servidor.
  
#### Tener en cuenta que algunos comandos solo se mostrarán al cliente que los pone, por lo que solo se podrá enviar el comando sin mensaje y saltará un error si se inteta otra cosa.
