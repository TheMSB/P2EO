package util;

/*
    Versie 1.0

    ########  #### ##    ##  ######    ######   ########
    ##     ##  ##  ###   ## ##    ##  ##    ##       ## 
    ##     ##  ##  ####  ## ##        ##            ##  
    ########   ##  ## ## ## ##   #### ##   ####    ##   
    ##   ##    ##  ##  #### ##    ##  ##    ##    ##    
    ##    ##   ##  ##   ### ##    ##  ##    ##   ##     
    ##     ## #### ##    ##  ######    ######   ########

    Protocol Eindopdracht Programmeren 2 INF 2
    Maintainer: Dennis Eijkel <d.j.eijkel@student.utwente.nl>
 */
public class Protocol {

	/*
	 * =========================================================================
	 * ===== INTRODUCTIE =======================================================
	 * =========================================================================
	 * Dit bestand is ter beschijving van het protocol, er zijn ook constanten
	 * in gedefineerd. Er staan in deze klasse constanten die gebruikt kunnen
	 * worden voor de implementatie van het protocol. Men is niet verplicht deze
	 * klasse te gebruiken voor een implementatie maar het voorkomt wel
	 * typefouten.
	 * 
	 * Alle berichten die er met dit commando worden verstuurd moeten UTF-8
	 * gecodeerd zijn.
	 * 
	 * Alle berichten zijn case-sensitive en commando's worden in ALL CAPS
	 * verstuurd.
	 * 
	 * De standaard-poort voor communicatie is 4242, maar het is niet verplicht
	 * deze te gebruiken.
	 * 
	 * Alle berichten moeten als strings verstuurd en ontvangen worden, een
	 * commando wordt altijd afgesloten door een newline (\n) en/of carriage
	 * return (\r), allebei mag dus ook.
	 * 
	 * De woorden moeten gescheiden worden door een spatie. Er mogen dus GEEN
	 * spaties in de parameters voorkomen, tenzij anders aangegeven.
	 * 
	 * Parameters die omringt zijn met <> zijn verplicht, parameters omringt
	 * door [] zijn optioneel. Als een bericht door de server naar meerdere
	 * clients moet worden gestuurd staat dit ook aangegeven. Ook staat er
	 * aangegeven of de client of de server het bericht stuurt.
	 */

	/**
	 * <h2>POORT</h2>
	 * <p>
	 * De standaardpoort is 4242, maar het is niet verplicht deze poort te
	 * gebruiken
	 * </p>
	 */
	public static final int PORT = 4242;

	/**
	 * <h2>DELIMITER</h2>
	 * <p>
	 * De delimiter in dit protocol is spatie, het is dus ook NIET toegestaan
	 * spaties in parameters te gebruiken tenzij anders aangegeven
	 * </p>
	 */
	public static final String DELIM = " ";

	/*
	 * =========================================================================
	 * ===== COMMANDOS BASIS PROTOCOL ==========================================
	 * =========================================================================
	 */

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * CONNECT - een client die met een server wil verbinden
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -&gt; S
	 * </p>
	 * <p>
	 * CONNECT &lt;name&gt;
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * name: verplicht - naam van de speler die wil verbinden
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * Als een client met een server wil verbinden gebruikt hij dit commando. De
	 * client stuurt zijn naam mee.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * CONNECT felix
	 * </p>
	 */
	public static final String CMD_CONNECT = "CONNECT";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * CONNECTED - de server accepteert het verzoek tot verbinding
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;- S
	 * </p>
	 * <p>
	 * CONNECTED [motd]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * motd: optioneel, spaties toegestaan - message of the day
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * Als de server het verzoek tot verbinding van de client accepteerd stuur
	 * deze dit bericht terug naar de client.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * CONNECTED Welkom op de server, vandaag is het mooi weer buiten :)
	 * </p>
	 */
	public static final String CMD_CONNECTED = "CONNECTED";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * FEATURES - lijst met features door de server ondersteund
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;- S
	 * </p>
	 * <p>
	 * FEATURES [feature] [...]
	 * </p>
	 * 
	 * <h2>PARAMTERES</h2>
	 * <p>
	 * feature: optioneel - feature die de server ondersteund
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De server stuurt na het versturen van een CONNECTED commando ook dit
	 * commando, hiermee wordt aangegeven dat de server de in de parameters
	 * beschreven features ondersteund. De lijst met features moet door spaties
	 * gescheiden worden.
	 * </p>
	 * 
	 * <h2>FEATURES</h2>
	 * <p>
	 * CHAT - Chat functionaliteit, hier kan mee gechat worden
	 * </p>
	 * <p>
	 * CHALLENGE - Challenge functionaliteit, extra functionaliteit waarmee
	 * clients kunnen worden uitgenodigd voor een spel
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * FEATURES CHAT CHALLENGE
	 * </p>
	 */
	public static final String CMD_FEATURES = "FEATURES";

	/**
	 * <h2>FEATURE CHAT</h2>
	 * <p>
	 * Chat feature
	 * </p>
	 */
	public static final String FEAT_CHAT = "CHAT";

	/**
	 * <h2>FEATURE CHALLENGE</h2>
	 * <p>
	 * Challenge feature
	 * </p>
	 */
	public static final String FEAT_CHALLENGE = "CHALLENGE";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * FEATURED - client geeft aan welke features hij ondersteund
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -&gt; S
	 * </p>
	 * <p>
	 * FEATURED [feature] [...]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * feature: optioneel - feature die de client ondersteund
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * Hetzelfde als FEATURES, alleen nu geeft de client aan welke features
	 * ondersteund worden. De server moet voor elke client bijhouden welke
	 * features ondersteund worden.
	 * </p>
	 * 
	 * <h2>FEATURES</h2>
	 * <p>
	 * CHAT - Chat functionaliteit, hier kan mee gechat worden
	 * <p>
	 * CHALLENGE - Challenge functionaliteit, extra functionaliteit waarmee
	 * clients kunnen worden uitgenodigd voor een spel
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * FEATURES CHALLENGE
	 * </p>
	 */
	public static final String CMD_FEATURED = "FEATURED";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * JOIN - client geeft aan dat hij een spel wil spelen
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -&gt; S
	 * </p>
	 * <p>
	 * JOIN [player_count]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * player_count: optioneel, integer - het aantal speler waarmee de client
	 * wil spelen
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * Als een client een nieuw spel wil spelen stuurt deze dit commando. De
	 * client mag aangeven met hoeveel spelers deze wil spelen, maar als deze
	 * parameter weggelaten wordt zegt de client dat het niet uitmaakt met
	 * hoeveel spelers er gespeeld moet worden. Ook als het aantal spelers niet
	 * 2, 3 of 4 is betekent dat ook dat het aantal spelers niet uitmaakt.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * JOIN 2
	 * </p>
	 */
	public static final String CMD_JOIN = "JOIN";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * START - server start een nieuw spel
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;&lt;- S - broadcast naar alle clients die in het spel meedoen
	 * </p>
	 * <p>
	 * START &lt;x&gt; &lt;y&gt; &lt;name1&gt; &lt;name2&gt; [name3] [name4]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * x: verplicht, integer - x-coordinaat van de start steen
	 * </p>
	 * <p>
	 * y: verplicht, integer - y-coordinaat van de start steen
	 * </p>
	 * <p>
	 * name1: verplicht - naam van speler 1
	 * </p>
	 * <p>
	 * name2: verplicht - naam van speler 2
	 * </p>
	 * <p>
	 * name3: optioneel - naam van speler 3
	 * </p>
	 * <p>
	 * name4: optioneel - naam van speler 4
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De server start een nieuw spel en geeft dit bericht door aan alle speler
	 * die aan het spel meedoen. De volgorde van de namen is willekeurig. Ook
	 * geeft de server aan waar de start steen komt te liggen. De kleuren zijn
	 * integers 0, 1, 2 en 3, de client is vrij om een eigen kleur aan een van
	 * deze integers te koppelen. De kleuren worden verdeeld volgens het
	 * onderstaande schema. Bij 3 spelers krijgen alle spelers een deel van
	 * kleur nummer 3 en bij 2 spelers krijgen alle spelers 2 kleuren. Direct na
	 * het versturen van dit commando wordt er ook een TURN commando verstuurd.
	 * </p>
	 * 
	 * <h2>KLEUREN TOEWIJZING</h2>
	 * <table border=1>
	 * <tr>
	 * <th>aantal spelers</th>
	 * <th>kleur naam 1</th>
	 * <th>kleur naam 2</th>
	 * <th>kleur naam 3</th>
	 * <th>kleur naam 4</th>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td>0</td>
	 * <td>1</td>
	 * <td>2</td>
	 * <td>3</td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td>0:3</td>
	 * <td>1:3</td>
	 * <td>2:3</td>
	 * <td></td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>0,1</td>
	 * <td>2,3</td>
	 * <td></td>
	 * <td></td>
	 * </tr>
	 * </table>
	 * 
	 * <h2>BORD INDELING</h2>
	 * <p>
	 * Een vlak op het bord word gerepresenteerd door een x- en y-coorinaat op
	 * de volgende manier:
	 * <table border=1>
	 * <tr>
	 * <td>y\x</td>
	 * <td>0</td>
	 * <td>1</td>
	 * <td>2</td>
	 * <td>3</td>
	 * <td>4</td>
	 * </tr>
	 * <tr>
	 * <td>0</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * <td>.</td>
	 * </tr>
	 * </table>
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * START 2 2 felix wolfgang ryan
	 * </p>
	 */
	public static final String CMD_START = "START";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * TURN - de server wijst de beurt aan een speler toe
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;&lt;- S - broadcast naar alle spelers in het spel
	 * </p>
	 * <p>
	 * TURN &lt;name&gt;
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * name: verplict - naam van de speler aan de beurt
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De server wijst de beurt toe aan een speler in het spel en vertelt dit
	 * aan alle spelers in het spel.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * TURN felix
	 * </p>
	 */
	public static final String CMD_TURN = "TURN";

	/***
	 * <h2>NAAM</h2>
	 * <p>
	 * MOVE - de client doet een zet
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -&gt; S
	 * </p>
	 * <p>
	 * MOVE &lt;x&gt; &lt;y&gt; &lt;type&gt; &lt;color&gt;
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * x: verplicht, integer - x-coordinaat van de zet
	 * </p>
	 * <p>
	 * y: verplicht, integer - y-coordinaat van de zet
	 * </p>
	 * <p>
	 * type: verplict, integer - soort steen
	 * <p>
	 * color: verplicht, integer - kleur van de steen
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De client geeft zijn zet door aan de server. Hij geeft coordinaten, een
	 * soort steen en een kleur mee. Het is aan te raden om te wachten met het
	 * updaten van de UI van de client omdat de server later nog een bevestiging
	 * zal sturen als de zet geaccepteerd is.
	 * </p>
	 * 
	 * <h2>SOORT STEEN</h2>
	 * <p>
	 * 0 - kleinste ring
	 * </p>
	 * <p>
	 * 1 - grotere ring
	 * </p>
	 * <p>
	 * 2 - nog grotere ring
	 * </p>
	 * <p>
	 * 3 - grootste ring
	 * </p>
	 * <p>
	 * 4 - volle steen van dezelfde grootte als steen 3
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * MOVE 2 2 4 0
	 * </p>
	 */
	public static final String CMD_MOVE = "MOVE";

	/**
	 * <p>
	 * 0 - kleinste ring
	 * </p>
	 */
	public static final int RING_0 = 0;
	/**
	 * <p>
	 * 1 - grotere ring
	 * </p>
	 */

	public static final int RING_1 = 1;
	/**
	 * <p>
	 * 2 - nog grotere ring
	 * </p>
	 */

	public static final int RING_2 = 2;
	/**
	 * <p>
	 * 3 - grootste ring
	 * </p>
	 */

	public static final int RING_3 = 3;
	/**
	 * <p>
	 * 4 - volle steen van dezelfde grootte als steen 3
	 * </p>
	 */
	public static final int RING_4 = 4;

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * MOVED - de server geeft aan dat er een zet gedaan is
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;&lt;- S - broadcast naar alle spelers in het spel
	 * </p>
	 * <p>
	 * MOVED &lt;x&gt; &lt;y&gt; &lt;type&gt; &lt;color&gt;
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * x: verplicht, integer - x-coordinaat van de zet
	 * </p>
	 * <p>
	 * y: verplicht, integer - y-coordinaat van de zet
	 * </p>
	 * <p>
	 * type: verplict, integer - soort steen
	 * </p>
	 * <p>
	 * color: verplicht, integer - kleur van de steen
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * Formaat hetzelfde als het commando MOVE. De server geeft aan dat een
	 * client een zet heeft gedaan en deze zet valide is. Hij stuurt dit bericht
	 * naar alle clients die aan het spel meedoen.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * MOVED 2 2 4 0
	 * </p>
	 */
	public static final String CMD_MOVED = "MOVED";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * END - het spel is afgelopen
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;&lt;- S - broadcast naar alle spelers in het spel
	 * </p>
	 * <p>
	 * END &lt;score1&gt; &lt;score2&gt; [score3] [score4]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * score1: verplicht, integer - score van name1
	 * </p>
	 * <p>
	 * score2: verplicht, integer - score van name2
	 * </p>
	 * <p>
	 * score3: verplicht indien 3e speler, integer - score van name3
	 * </p>
	 * <p>
	 * score4: verplicht indien 4e speler, integer - score van name4
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De server geeft aan dat het spel is afgelopen. De scores van de spelers
	 * worden meegegeven. Deze zijn gekoppeld aan de namen die meegegeven zijn
	 * in het START commando en staan ook in dezelfde volgorde.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * END 3 5 0
	 * </p>
	 */
	public static final String CMD_END = "END";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * DISCONNECT - client geeft aan dat hij de verbinding wil verbreken
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -> S
	 * </p>
	 * <p>
	 * DISCONNECT [message]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * message: optioneel, spaties toegestaan - bericht
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * Als de client de verbinding wil verbreken stuurt deze dit commando. Als
	 * de client nog in een lopend spel zit dan zal de server deze ook
	 * beeindigen door middel van het END commando.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * DISCONNECT Ik haat dit spel
	 * </p>
	 */
	public static final String CMD_DISCONNECT = "DISCONNECT";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * DISCONNECT - client geeft aan dat hij de verbinding wil verbreken
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;&lt;- S - broadcast naar alle clients in het spel
	 * </p>
	 * <p>
	 * DISCONNECT &lt;name&gt; [message]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * name: verplicht - naam van de client
	 * </p>
	 * <p>
	 * message: optioneel, spaties toegestaan - bericht
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * Als een client de verbinding heeft verbroken of als om een andere reden
	 * de verbinding is verbroken met een client stuurt de server dit bericht.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * DISCONNECT felix Ik haat dit spel
	 * </p>
	 */
	public static final String CMD_DISCONNECTED = "DISCONNECTED";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * ERROR - server is een fout tegengekomen
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;- S
	 * </p>
	 * <p>
	 * ERROR &lt;code&gt; [message]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * code: verplicht, integer - error code die het soort fout aangeeft
	 * </p>
	 * <p>
	 * message: optioneel, spaties toegestaan - bericht met toelichting fout
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * Als een bericht van de client niet door de server verwerkt kan worden om
	 * willekeurige reden, dan zal de server een error bericht terugsturen. Dit
	 * bericht bevat altijd een error code om aan te geven om wat voor soort
	 * fout het gaat. Error codes staan hieronder beschreven. Er mag ook een
	 * bericht worden meegestuurd die de fout verder toelicht.
	 * </p>
	 * 
	 * <h2>ERROR CODES</h2>
	 * <p>
	 * 0 - UNDEFINED: Voor fouten waar geen specifieke error code voor is
	 * </p>
	 * <p>
	 * 1 - COMMAND_NOT_FOUND: Als de server een commando niet herkent
	 * </p>
	 * <p>
	 * 2 - COMMAND_UNEXPECTED: Als een commando dat door de client heeft
	 * gestuurd niet van toepassing is in de huidige context
	 * </p>
	 * <p>
	 * 3 - INVALID_COMMAND: Als de parameters van een commando niet kloppen
	 * </p>
	 * <p>
	 * 4 - INVALID_MOVE: Als de speler tijdens het spel een zet doet die niet is
	 * toegestaan
	 * </p>
	 * <p>
	 * 5 - NAME_IN_USE: Als de client wil verbinden met de server en een naam
	 * meegeeft die al door een andere client gebruikt wordt
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * ERROR 3 De parameter code moet een nummer zijn
	 * </p>
	 */
	public static final String CMD_ERROR = "ERROR";

	/**
	 * <h2>ERROR UNDEFINED</h2>
	 * <p>
	 * Voor fouten waar geen specifieke error code voor is
	 * </p>
	 */
	public static final int ERR_UNDEFINED = 0;

	/**
	 * <h2>ERROR COMMAND NOT FOUND</h2>
	 * <p>
	 * Als de server een commando niet herkent
	 * </p>
	 */
	public static final int ERR_COMMAND_NOT_FOUND = 1;

	/**
	 * <h2>ERROR COMMAND UNEXPECTED</h2>
	 * <p>
	 * Als een commando dat door de client heeft gestuurd niet van toepassing is
	 * in de huidige context
	 * </p>
	 */
	public static final int ERR_COMMAND_UNEXPECTED = 2;

	/**
	 * <h2>ERROR INVALID COMMAND</h2>
	 * <p>
	 * Als de parameters van een commando niet kloppen
	 * </p>
	 */
	public static final int ERR_INVALID_COMMAND = 3;

	/**
	 * <h2>ERROR INVALID MOVE</h2>
	 * <p>
	 * Als de speler tijdens het spel een zet doet die niet is toegestaan
	 * </p>
	 */
	public static final int ERR_INVALID_MOVE = 4;

	/**
	 * <h2>ERROR NAME IN USE</h2>
	 * <p>
	 * Als de client wil verbinden met de server en een naam meegeeft die al
	 * door een andere client gebruikt wordt
	 * </p>
	 */
	public static final int ERR_NAME_IN_USE = 5;

	/*
	 * =========================================================================
	 * ===== COMMANDOS CHAT FEATURE ============================================
	 * =========================================================================
	 */

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * SAY - client verstuurt een chatbericht
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -&gt; S
	 * </p>
	 * <p>
	 * SAY &lt;message&gt;
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * message: verplicht - chatbericht
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De client stuurt een chat bericht
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * CHAT Wie wil er een spelletje spelen?
	 * </p>
	 */
	public static final String CMD_SAY = "SAY";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * SAID - de server broadcast het chatbericht van een client
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;&lt;- S - broadcast naar alle relevante clients
	 * </p>
	 * <p>
	 * SAID &lt;name&gt;
	 * </p>
	 * <message>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * name: verplicht - naam van de client die het bericht stuurde
	 * </p>
	 * <p>
	 * message: verplicht - chatbericht van de client
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De server stuurt het chatbericht van een speler door naar alle relevante
	 * clients, als de sturende client in een spel zit, wordt het bericht alleen
	 * naar de andere spelers in het spel verstuurd. Hetzelfde geldt voor de
	 * algemene chat lobby
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * CHAT wolfgang Wie wil er een spelletje spelen?
	 * </p>
	 */
	public static final String CMD_SAID = "SAID";

	/*
	 * =========================================================================
	 * ===== COMMANDOS CHALLENGE FEATURE =======================================
	 * =========================================================================
	 */

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * LIST - client vraagt aan server de lijst van alle spelers
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -&gt; S
	 * </p>
	 * <p>
	 * LIST
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * Dit commando heeft geen parameters.
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De client vraagt een lijst van alle spelers op, zelfs van spelers die
	 * niet de challenge feature ondersteunen. De client krijgt dan een aantal
	 * LISTED berichten terug.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * LIST
	 * </p>
	 */
	public static final String CMD_LIST = "LIST";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * LISTED - de server geeft een aangemelde client door aan een client
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;- S
	 * </p>
	 * <p>
	 * LISTED &lt;name&gt; &lt;status&gt;
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * name: verplicht - naam van de client
	 * </p>
	 * <p>
	 * status: verplicht, integer - status van de client
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De server geeft aan een client aan dat er een client verbonden is met de
	 * server, het bericht mag ook naar de client die in het bericht staat
	 * gestuurd worden. Als er een nieuwe client met de server verbinding maakt
	 * dan stuurt de server een LISTED naar alle clients toe die de challenge
	 * feature ondersteunen.
	 * </p>
	 * 
	 * <h2>STATUS</h2>
	 * <p>
	 * 0 - Verbonden, maar ondersteund geen challenge feature
	 * </p>
	 * <p>
	 * 1 - Verbonden en ondersteund wel challenge feature
	 * </p>
	 * <p>
	 * 2 - Verbonden en in game, ook clients zonder challenge feature kunnen
	 * deze status hebben
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * LISTED wolfgang 1
	 * </p>
	 */
	public static final String CMD_LISTED = "LISTED";

	/**
	 * <h2>STATUS CONNECTED</h2>
	 * <p>
	 * Verbonden, maar ondersteund geen challenge feature
	 * </p>
	 */
	public static final int STATUS_CONNECTED = 0;

	/**
	 * <h2>STATUS CHALLENGE</h2>
	 * <p>
	 * Verbonden en ondersteund wel challenge feature
	 * </p>
	 */
	public static final int STATUS_CHALLENGE = 1;

	/**
	 * <h2>STATUS INGAME</h2>
	 * <p>
	 * Verbonden en in game, ook clients zonder challenge feature kunnen deze
	 * status hebben
	 * </p>
	 */
	public static final int STATUS_INGAME = 2;

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * CHALLENGE - een client nodigt een aantal andere clients uit om te spelen
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -&gt; S
	 * </p>
	 * <p>
	 * CHALLENGE &lt;name1&gt; [name2] [name3]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * name1: verplicht - naam van de eerste uit te nodige speler
	 * </p>
	 * <p>
	 * name2: optioneel - naam van de tweede uit te nodige speler
	 * </p>
	 * <p>
	 * name3: optioneel - naam van de derde uit te nodige speler
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * Een client nodigt een aantal andere spelers uit om te gaan spelen. De
	 * server wijst deze challenge vervolgens een uniek challenge id aan en
	 * stuurt de alle betrokken clients, ook de client die de uitnodiging
	 * verstuurde een CHALLENGED bericht
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * CHALLENGE wolfgang
	 * </p>
	 */
	public static final String CMD_CHALLENGE = "CHALLENGE";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * CHALLENGED - de server vertelt dat een client uitgenodigd is
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;&lt;- S - broadcast naar alle uitgenodigde spelers
	 * </p>
	 * <p>
	 * CHALLENGED &lt;challenge_id&gt; &lt;challenger_name&gt; &lt;name2&gt;
	 * [name3] [name4]
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * challenge_id: verplicht, integer - uniek nummer van de uitnodiging
	 * </p>
	 * <p>
	 * challenger_name: verplicht - naam van de client die de uitnodiging
	 * verstuurde
	 * </p>
	 * <p>
	 * name2: verplicht - naam van de tweede speler
	 * <p>
	 * name3: optioneel - naam van de derde speler
	 * </p>
	 * <p>
	 * name4: optioneel - naam van de vierde speler
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De server geeft aan alle uitgenodigde clients aan dat er een uitnodiging
	 * naar hem is verstuurd. De challenger krijgt ook dit bericht, maar zal
	 * direct reageren met een ACCEPT. De challenge id zorgt ervoor dat
	 * challenges uit elkaar gehouden kunnen worden door de clients.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * CHALLENGED 4 felix wolfgang
	 * </p>
	 */
	public static final String CMD_CHALLENGED = "CHALLENGED";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * ACCEPT - client accepteert de challenge
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -&gt; S
	 * </p>
	 * <p>
	 * ACCEPT &lt;challenge_id&gt;
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * challenge_id: verplicht, integer - uniek challenge id
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De client accepteert een uitnodiging, vervolgens wacht deze totdat de
	 * server een START bericht stuurt. De server stuurt een START bericht als
	 * alle clients de uitnodiging hebben geaccepteerd.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * ACCEPT 4
	 * </p>
	 */
	public static final String CMD_ACCEPT = "ACCEPT";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * DECLINE - client wijst een uitnodiging af
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C -&gt; S
	 * </p>
	 * <p>
	 * DECLINE &lt;challenge_id&gt;
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * challenge_id: verplicht, integer - uniek challenge id
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De client wijst een uitnodiging af, de server zal vervolgens alle andere
	 * client hiervan op de hoogte brengen door middel van een DECLINED bericht.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * DECLINE 4
	 * </p>
	 */
	public static final String CMD_DECLINE = "DECLINE";

	/**
	 * <h2>NAAM</h2>
	 * <p>
	 * DECLINED - server geeft aan dat de uitnodiging vervalt
	 * </p>
	 * 
	 * <h2>FORMAAT</h2>
	 * <p>
	 * C &lt;&lt;- S - broadcast naar alle uitgenodigde clients
	 * </p>
	 * <p>
	 * DECLINED &lt;challenge_id&gt;
	 * </p>
	 * 
	 * <h2>PARAMETERS</h2>
	 * <p>
	 * challenge_id: verplicht, integer - uniek challenge id
	 * </p>
	 * 
	 * <h2>BESCHRIJVING</h2>
	 * <p>
	 * De server geeft aan alle uitgenodigde spelers aan dat de uitnodiging voor
	 * de aangegeven challenge komt te vervallen, de server stuurt dit nadat het
	 * een DISCONNECT of DECLINE bericht heeft ontvangen van een van de
	 * genodigden en als de verbinding verbroken is of de client in een ander
	 * spel terecht is gekomen.
	 * </p>
	 * 
	 * <h2>VOORBEELD</h2>
	 * <p>
	 * DECLINED 4
	 * </p>
	 */
	public static final String CMD_DECLINED = "DECLINED";

}