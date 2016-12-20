var fs = require("fs");
console.log("\n *STARTING* \n");

fs.readdir("./data/", function(err, files) {
    if (err)
    {
	   //pass;
    }
    files.forEach(function(f) {
	var contents = fs.readFileSync("./data/"+f);
        var data = JSON.parse(contents);
		if(data.game_mode_name != "All Pick" && data.game_mode_name != "Captain’s Mode")
		{
			return;
		}
	    printHeroResult(data);
    });
});

function printHeroResult(data)
{
  var outputString = "";
  var outputRequire = ["hero_id", "kills","deaths","assists","last_hits", "gold_per_min","xp_per_min","hero_damage","tower_damage","gold","gold_spent"];

  var start;
  var end;
  if(data.radiant_win)
  {
	  start = 5;
	  end = 10;
  }
  else if(data.radiant_win === false)
  {
	  start = 0;
	  end = 5;
  }

  var players = data.players.concat();

  for(key = start ; key < end ; key++)
  {

    if(players[key].hasOwnProperty("hero_id"))
    {
      outputString += players[key].hero_id + ",";
    }
    if(players[key].hasOwnProperty("kills"))
    {
      outputString += players[key].kills + ",";
    }
    if(players[key].hasOwnProperty("deaths"))
    {
      outputString += players[key].deaths + ",";
    }
    if(players[key].hasOwnProperty("assists"))
    {
      outputString += players[key].assists + ",";
    }
    if(players[key].hasOwnProperty("last_hits"))
    {
      outputString += players[key].last_hits + ",";
    }
    if(players[key].hasOwnProperty("gold_per_min"))
    {
      outputString += players[key].gold_per_min + ",";
    }
    if(players[key].hasOwnProperty("xp_per_min"))
    {
      outputString += players[key].xp_per_min + ",";
    }
    if(players[key].hasOwnProperty("hero_damage"))
    {
      outputString += players[key].hero_damage + ",";
    }
    if(players[key].hasOwnProperty("tower_damage"))
    {
      outputString += players[key].tower_damage + ",";
    }
    if(players[key].hasOwnProperty("gold"))
    {
      outputString += players[key].gold + ",";
    }
    if(players[key].hasOwnProperty("gold_spent"))
    {
      outputString += players[key].gold_spent;
    }
	if(players[key].leaver_status === 0)
	{
	    console.log(outputString);
	}
	outputString = "";
  }
}
