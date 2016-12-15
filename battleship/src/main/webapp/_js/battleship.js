/**
 * 
 */
$(document).ready(function() {
	//Process request for new game
	$('#submit').click(function(event) {
		$(this).hide();
		//No data to send, but post request initializes game
		$.ajax({
	         method: "POST",
	         url: "game",
	         error : function() {alert("Error Occured");},
	         success : function(data){
	        	 	//Receive player fleet information from servlet
	        		var player_fleet = jQuery.parseJSON(data.player_fleet);
	        		var opponent_fleet = jQuery.parseJSON(data.opponent_fleet);

	        		//Process player fleet: display ship placement as well as any hit
		        	for(var i = 0; i < player_fleet.Ships.length; i++) {
	        			for(var j = 0; j < player_fleet.Ships[i].location.length; j++){
	        				var board_id = player_fleet.Ships[i].location[j].x.toString() + player_fleet.Ships[i].location[j].y.toString();
	        				if(player_fleet.Ships[i].location[j].hit) {
	        					$("#player-grid-" + board_id).addClass("hit");
	        				} else {
	        					$("#player-grid-" + board_id).addClass("occupied");
	        				}
	        			}
	        		};
	        	   
	        		//Process opponent fleet. 
	        	    for(var i = 0; i < opponent_fleet.Ships.length; i++) {
                    for(var j = 0; j < opponent_fleet.Ships[i].location.length; j++){
                      var board_id = opponent_fleet.Ships[i].location[j].x.toString() + opponent_fleet.Ships[i].location[j].y.toString();
                      if(opponent_fleet.Ships[i].location[j].hit) {
                         $("#opponent-grid-" + board_id).addClass("hit");
                      }
                    }
                };
                
                /*
        	    * Process player grid objects and display game state in hints area. 
        	    * */
                for(var i = 0; i < opponent_fleet.Ships.length; i++) {
                	 var ship_id = opponent_fleet.Ships[i].ship;
                	 var hits = opponent_fleet.Ships[i].hits;
                	 $("#hints").append($('<div id = ' + ship_id +' class = "ship"></div>'));
	                
                	 //Mark all hits with hit class
                	 for(var j = 0; j < hits; j++){ 
                		$("#" + ship_id).append($('<div class = \"hint\"></div>'));
	                 }
	                
                	 //Add blank spaces for non-hits
                	 //Use locations - hits to calculate blank spaces
	                 for(var j = 0; j < (opponent_fleet.Ships[i].location.length - hits); j++){ 
	                	$("#" + ship_id).append($('<div class = \"hint hit\"></div>'));
	                 }	                 
                };
                
	        	   /*
	        	    * Allows user to click opponent grid.
	        	    * Processes grid point and passes coordinate to servlet view json/ajax.
	        	    * Alerts failure and success. 
	        	    * */
	        	   $("#opponent-grid .cell").each(function() {
	        		   $(this).click(function(event){
	        			$(this).addClass("guessed");
        	            var str =$(this).attr('id');
        	            var x_coord = str[str.length - 2];
        	            var y_coord = str[str.length - 1];
        	            console.log(x_coord, y_coord);
        	            $.ajax({
        	               url: "game/" + data.game_id,   
        	               method: "PUT",
        	               data : JSON.stringify({ 
        	            	  x: x_coord,  
        	                  y: y_coord
        	               }),
        	               dataType: 'json',
        	               error : function() {alert("Error Occured");},
        	               success : function(data) {
        	            	   //Receive player fleet information from servlet
        	            	   var player_fleet = jQuery.parseJSON(data.player_fleet);
        		        	   var opponent_fleet = jQuery.parseJSON(data.opponent_fleet);
        		        	   var game_state = data.game_state;
        		        		
        		        	 
    		        		 //Process player fleet: display ship placement as well as any hit
    				        	for(var i = 0; i < player_fleet.Ships.length; i++) {
    			        			for(var j = 0; j < player_fleet.Ships[i].location.length; j++){
    			        				var board_id = player_fleet.Ships[i].location[j].x.toString() + player_fleet.Ships[i].location[j].y.toString();
    			        				if(player_fleet.Ships[i].location[j].hit) {
    			        					$("#player-grid-" + board_id).addClass("hit");
    			        				} else {
    			        					$("#player-grid-" + board_id).addClass("occupied");
    			        				}
    			        			}
    			        		};
    			        	   
    			        		//Process opponent fleet. 
    			        	    for(var i = 0; i < opponent_fleet.Ships.length; i++) {
        		                   for(var j = 0; j < opponent_fleet.Ships[i].location.length; j++){
        		                      var board_id = opponent_fleet.Ships[i].location[j].x.toString() + opponent_fleet.Ships[i].location[j].y.toString();
        		                      if(opponent_fleet.Ships[i].location[j].hit) {
        		                         $("#opponent-grid-" + board_id).addClass("hit");
        		                      }
        		                   }
    		                    };
    		                
    		                    /*
    		             	    * Display game state in hints area. 
    		             	    * */
    		                     $("#hints").empty();
    		                     for(var i = 0; i < opponent_fleet.Ships.length; i++) {
    		                     	 var ship_id = opponent_fleet.Ships[i].ship;
    		                     	 var hits = opponent_fleet.Ships[i].hits;
    		                     	 $("#hints").append($('<div id = ' + ship_id +' class = "ship"></div>'));
    		     	                
    		                     	 //Mark all hits with hit class
    		                     	 for(var j = 0; j < hits; j++){ 
    		                     		$("#" + ship_id).append($('<div class = \"hint\"></div>'));
    		     	                 }
    		     	                
    		                     	 //Add blank spaces for non-hits
    		                     	 //Use locations - hits to calculate blank spaces
    		     	                 for(var j = 0; j < (opponent_fleet.Ships[i].location.length - hits); j++){ 
    		     	                	$("#" + ship_id).append($('<div class = \"hint hit\"></div>'));
    		     	                 }	                 
    		                     };
    		                     
    		                     if(game_state == 3) {
    	    		        		   var winner = data.winner;
    	    		        		   alert("Game Over! " + winner + " has won the game!" );
    	    		        	 }
        	               }
        	            });
	        	      });
	        	   });
	         }
	      });
	   });
	});