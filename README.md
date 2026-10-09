# Yu-Gi-Oh

1 Al iniciar, tú y la máquina reciben 3 cartas Monster al azar. Cada carta muestra su imagen, nombre, ATK y DEF.
2 El turno inicial se sortea al azar 
3 Cada ronda
Elige una de tus cartas disponibles.
Elige el modo de la carta: Ataque o Defensa.
La máquina elige al azar una de sus cartas y su modo.
Se comparan las cartas y el ganador de la ronda suma 1 punto.

. Fin del duelo
El primero en ganar 2 rondas gana el duelo. Si se acaban las cartas sin que nadie llegue a 2 puntos, gana quien tenga más puntos, o es empate.

Mensajes de error:
Si falla la red o la API no responde, el juego muestra el mensaje en pantalla en lugar de cerrarse. Vuelve a iniciar para repartir cartas de nuevo.
Se implemento la logica para cumplir como decia el pdf para saber quien gano 
Ataque	Ataque	El de mayor ATK (si empatan, nadie suma)
Ataque	Defensa	El atacante si su ATK es mayor que la DEF del defensor; si no, el defensor
Defensa	Ataque	Igual que arriba, con la máquina como atacante
Defensa	Defensa	Nadie suma punto

Diseño del proyecto
Card: modelo de datos de una carta (nombre, ATK, DEF, imagen, URL de imagen).
YgoApiClient: consulta la API, reintenta hasta obtener cartas Monster y descarga las imágenes. Bloquea, así que se usa desde un hilo de fondo para no congelar la ventana.
CardException: error con mensaje legible para el usuario.
Duel: reglas del duelo (turno inicial, comparación ATK/DEF, puntos, cartas gastadas). No usa Swing; devuelve el texto que la ventana escribe en el log.
YuGioGUI: ventana que muestra las cartas, recibe las elecciones del jugador y pinta el log.

