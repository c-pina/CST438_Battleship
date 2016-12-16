package battleship;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Servlet implementation class GameController
 */
public class GameController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Hashtable<UUID, GameModel> games = new Hashtable<UUID, GameModel>();

	public GameController() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RestRequest resourceValues = new RestRequest(request.getPathInfo());

		UUID gameId = resourceValues.getId();

		if (gameId.equals(null)) {
			throw new IllegalArgumentException("A valid game id is required");
		}

		GameModel game = games.get(gameId);

		JSONObject json = new JSONObject();
		json.put("game_id", gameId.toString());
		json.put("game_state", game.getGameState());
		json.put("player1_ships", game.getPlayer1().getShips());
		json.put("player2_ships", game.getPlayer2().getShips());

		response.addHeader("Content-Type", "application/json");

		response.getWriter().append(json.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Create a new game
		UUID newGameId = UUID.randomUUID();
		GameModel newGame = new GameModel(1);

		// Create players
		newGame.addPlayer1(new Player());
		newGame.addPlayer2(new ComputerPlayer());

		/* note the 1990 Milton Bradley version of the rules specifies the following ships */

		// Create player 1's ships
		Hashtable<String, Ship> p1 = new Hashtable<String, Ship>();		
		p1.put("Carrier", new Ship("Carrier", 5));
		p1.put("Battleship", new Ship("Battleship", 4));
		p1.put("Cruiser", new Ship("Cruiser", 3));
		p1.put("Submarine", new Ship("Submarine", 3));
		p1.put("Destroyer", new Ship("Destroyer", 2));

		// Create player 2's ships
		Hashtable<String, Ship> p2 = new Hashtable<String, Ship>();
		p2.put("Carrier", new Ship("Carrier", 5));
		p2.put("Battleship", new Ship("Battleship", 4));
		p2.put("Cruiser", new Ship("Cruiser", 3));
		p2.put("Submarine", new Ship("Submarine", 3));
		p2.put("Destroyer", new Ship("Destroyer", 2));

		// Add the ship tables to the game object
		newGame.getPlayer1().setShips(p1);
		newGame.getPlayer2().setShips(p2);

		// Automatically select ship locations
		placeShips(newGame,1);
		placeShips(newGame,2);

		// Store the game in the games Hashtable
		games.put(newGameId, newGame);

		//Test returning ship
		//String playerShips = "{\"Ships\": [{\"ship\": \"Submarine\",\"hits\": \"1\",\"location\": [{\"x\": 1,\"y\": 1,\"hit\": false}, {\"x\": 1,\"y\": 2,\"hit\": true}, {\"x\": 1,\"y\": 3,\"hit\": false}]},{\"ship\":\"Destroyer\",\"hits\": \"1\",\"location\": [{\"x\": 3,\"y\": 1,\"hit\": false}, {\"x\": 4,\"y\": 1,\"hit\": true}]}]}";
		//String opponentShips = "{\"Ships\": [{\"ship\": \"Submarine\",\"hits\": \"1\",\"location\": [{\"x\": 1,\"y\": 1,\"hit\": false}, {\"x\": 1,\"y\": 2,\"hit\": true}, {\"x\": 1,\"y\": 3,\"hit\": false}]},{\"ship\":\"Destroyer\",\"hits\": \"1\",\"location\": [{\"x\": 3,\"y\": 1,\"hit\": false}, {\"x\": 4,\"y\": 1,\"hit\": true}]}]}";
		JSONObject json = new JSONObject();
		json.put("game_id", newGameId.toString());
		json.put("player_fleet", this.shipsToJson(newGame.getPlayer1().getShips()));
		json.put("opponent_fleet", this.shipsToJson(newGame.getPlayer2().getShips()));

		response.addHeader("Content-Type", "application/json");
		response.getWriter().append(json.toString());
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RestRequest resourceValues = new RestRequest(request.getPathInfo());

		UUID gameId = resourceValues.getId();

		if (gameId.equals(null)) {
			throw new IllegalArgumentException("A valid game id is required");
		}

		// Fetch game data
		GameModel game = games.get(gameId);
		Player human = game.getPlayer1();
		ComputerPlayer computer = (ComputerPlayer)game.getPlayer2();
		game.setGameState(2);

		StringBuffer body = new StringBuffer();
		String line = null;

		// Display winner as "human" or "computer" if game is flagged as over
		Boolean gameOver = false;
		String winner = "";

		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				body.append(line);
		} catch (Exception e) {
			throw new IOException("Error processing request string");
		}

		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(body.toString());

			switch(game.getGameState()) {
			case 1:
				// TODO?
				// setup
				break;
			case 2:
				// Play round
				// The human always goes first	
				// Get the player's guessed coordinates from the JSON
				int x = Integer.parseInt((String)jsonObject.get("x"));
				int y = Integer.parseInt((String)jsonObject.get("y"));

				// Create a Point and save to guesses list
				Point humanGuess = new Point(x,y,false); 
				human.addGuess(humanGuess);

				// Attempt to get info for a ship at that location
				Ship computerShip = computer.getShipOnPoint(humanGuess);
				if (computerShip != null){
					// This was a hit, mark the Point as hit
					computerShip.getLocation().getSinglePoint(x,y).setHit(true);
					computerShip.decrementHits();
					if (computerShip.getHits() == 0) {
						// Ship was destroyed
						computer.decrementShipCount();
						if (computer.hasShips() == false) {
							// Game is over, human won
							game.setGameState(3);
							winner = "human";
							gameOver = true;
						}
					}
				}

				// Computer turn
				// The computer will automatically guess a random location
				Point computerGuess = computer.makeGuess();

				// Attempt to get info for a ship at that location
				Ship humanShip = human.getShipOnPoint(computerGuess);
				if (humanShip != null) {
					// This is a hit, mark the Point as hit
					humanShip.getLocation().getSinglePoint(computerGuess.getX(),computerGuess.getY()).setHit(true);
					humanShip.decrementHits();
					if (humanShip.getHits() == 0) {
						// Ship was destroyed
						human.decrementShipCount();
						if (human.hasShips() == false) {
							// Game is over
							game.setGameState(3);
							winner = "computer";
							gameOver = true;
						}
					}
				}

				break;
			case 3:
				// TODO?
				// Game is over
				break;
			}

		} catch (org.json.simple.parser.ParseException e) {
			throw new IOException("Error parsing JSON request string");
		}

		JSONObject json = new JSONObject();
		json.put("game_id", gameId.toString());
		json.put("game_state", game.getGameState());
		if(gameOver) {
			json.put("winner", winner);
		}
		json.put("player_fleet", this.shipsToJson(game.getPlayer1().getShips()));
		json.put("opponent_fleet", this.shipsToJson(game.getPlayer2().getShips()));

		response.addHeader("Content-Type", "application/json");

		response.getWriter().append(json.toString());
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private boolean placeShips(GameModel game,int playerNumber) {
		// Get the player's table of ships and game grid
		Hashtable<String,Ship> ships;
		Player player;
		if (playerNumber == 1) {
			player = game.getPlayer1();
			ships = player.getShips();
		} else if (playerNumber == 2) {
			player = game.getPlayer2();
			ships = player.getShips();
		} else {
			// Return false if an invalid player number is provided
			return false;
		}

		// Iterate through the Hashtable of ships to place them
		Set<String> keys = ships.keySet();
		for(String key : keys){
			Ship currentShip = ships.get(key);
			int length = ((Ship) currentShip).getHits();

			// Select a random number 0-1 to determine orientation of ship
			Random rand = new Random();
			int orientation = rand.nextInt(2);

			// Loop picking random locations until we find one that is not already occupied
			while (true) {
				Point[] currentPoints = new Point[length];

				if (orientation == 0) {
					// Horizontal orientation
					// Select a random starting point
					int x = rand.nextInt(10 - length); // subtract the length so it won't overflow the board
					int y = rand.nextInt(10);					

					// Create points
					for (int i = 0; i < length; i++) {
						currentPoints[i] = new Point(x+i,y,false);
					}
				} else {
					// Vertical orientation
					// Select a random starting point
					int x = rand.nextInt(10);
					int y = rand.nextInt(10 - length); // subtract the length so it won't overflow the board

					// Create points
					for (int i = 0; i < length; i++) {
						currentPoints[i] = new Point(x,y+i,false);
					}
				}

				// Check if there is already a ship in this location
				boolean shipOverlaps = false;
				for (Point p : currentPoints) {
					shipOverlaps = player.hasShipOnPoint(p);
					if (shipOverlaps) break; // stop checking if we already know one overlaps
				}

				// If no ships overlap, set the location for this ship
				// Otherwise, while loop will restart
				if (shipOverlaps == false) {
					currentShip.setLocation(new Location(currentPoints));
					break;
				}
			}
		} 
		return true;
	}

	private class RestRequest {
		// Accommodate two requests, one for all resources, another for a
		// specific resource
		private Pattern regExAllPattern = Pattern.compile("/b");
		private Pattern regExIdPattern = Pattern
				.compile("/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})");

		private UUID id;

		public RestRequest(String pathInfo) throws ServletException {
			// regex parse pathInfo
			Matcher matcher;
			try {
				// Check for ID case first, since the All pattern would also match
				matcher = regExIdPattern.matcher(pathInfo);
				if (matcher.find()) {
					this.id = UUID.fromString(matcher.group(1));
					return;
				}

				matcher = regExAllPattern.matcher(pathInfo);
				if (matcher.find())
					return;				
			} catch(NullPointerException e) {
				//blah
			}


			throw new ServletException("Invalid URI");
		}

		public UUID getId() {
			return this.id;
		}
	}

	private String shipsToJson(Hashtable<String, Ship> ships) {

		String shipList[] = new String[5]; 

		Set<String> keys = ships.keySet();
		int count = 0;
		for(String key: keys){
			shipList[count] = ships.get(key).toString();
			count++;
		}
		String out = "{\"Ships\":" + Arrays.toString(shipList) + "}";
		return out;
	}
}
