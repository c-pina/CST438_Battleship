<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script src="_js/battleship.js"></script>
<title>Battleship</title>
<link rel="stylesheet" type="text/css" href="_css/main.css" />
</head>
<body>
   <div id = "rules">
	   <h2>Welcome to Battleship</h2>
	   <form>
	      <input type=button id="submit" value="Start Game"/>
	   </form>
   </div>
   <div class = "board" id = "player-grid">
      <p>Your Grid</p>
      <div class = "table">
         <% 
         for(int r = 0; r < 10; r++){
            out.print("<div class = \"row\">");
            for(int c = 0; c < 10; c++){
               out.print("<div id = player-grid-" + c + r + " class = \"cell\"></div>");
            }
            out.print("</div>");
         } 
         %>
      </div>
   </div>
   <div class = "board" id = "opponent-grid">
      <p>Opponent's Grid</p>
      <div class = "table">
         <% 
         for(int r = 0; r < 10; r++){
            out.print("<div class = \"row\">");
            for(int c = 0; c < 10; c++){
               out.print("<div id = opponent-grid-" + c + r + " class = \"cell\"></div>");
            }
            out.print("</div>");
         } 
         %>
      </div>
      <div id = "hints"></div>
   </div>
</body>
</html>
