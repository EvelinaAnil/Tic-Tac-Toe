# Uitleg over de server

Om online te kunnen spelen via de server, moet je deze eerst opstarten.

Open eerst je terminal. Vervolgens open het mapje "Server" binnen het project map "Tic-Tac-Toe" via deze command: 

`cd Server`

Om een wachtwoord aan te maken, zet deze regel in je terminal. de ... moet je vervangen met je eigen wachtwoord naar keuze: 

``java -jar newgameserver-release-V1.jar --set-password ...``

Om de server dan te starten, zet het volgende in je terminal: 

``java -jar newgameserver-release-V1.jar``

Nu kan je in je game client een willekeurige online modus kiezen. Er word dan automatisch naar een andere speler gezocht.

Belangrijke informatie staat op de pagina: http://127.0.0.1:8081

## Soorten menu's op de website
### Games
Hier worden actieve games live weergegeven

### Admin
Hiermee kan je verschillende dingen instellen, maar het meeste is niet belangrijk. 
In dit menu is het mogelijk om een tournament te starten aan de rechter kant. Kies als spel "Tic-Tac-Toe"
en als het goed is zou hij moeten starten. Het is wel belangrijk om minimaal 2 clients open te hebben die 
via de "Computer vs Computer" -> "Online" keuze verbonden zijn (Deze staan dan ook links bij de playerlist)

### Tournament 
Hier kan je tournaments inzien die live updaten. Je kan het spelbord zien met de zetten van de Ai's 
en ook de verleden games die al klaar zijn. Ook een overzicht met games en de gegeven punten (Dit is altijd 0)
