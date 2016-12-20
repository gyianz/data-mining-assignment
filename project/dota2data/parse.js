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

	printHeroRadiant(data);
	printHeroDire(data);
    });
});





function printHeroRadiant(data)
{
	var outputString = "";
	for (var i = 0; i < 5; i++)
	{
		if(data.players[i].leaver_status !== 0)
		{
			return;
		}
		outputString += "\""+data.players[i].hero_name+"\""+",";
	}

	outputString += (data.radiant_score - data.dire_score)+",";
	outputString += data.duration+",";

	if(data.radiant_win === false)
	{
		outputString += "\"lose\"";
	}
	else
	{
		outputString += "\"win\"";
	}

	outputString = outputString.replace(/ /gi,"");
	console.log(outputString);
}

function printHeroDire(data)
{
	var outputString = "";
	for (var i = 5; i < 10; i++)
	{
		outputString += "\""+data.players[i].hero_name+"\""+",";
	}

	outputString += (data.dire_score - data.radiant_score)+",";
	outputString += data.duration+",";

	if(data.radiant_win === false)
	{
		outputString += "\"win\"";
	}
	else
	{
		outputString += "\"lose\"";
	}
	outputString = outputString.replace(/ /gi,"");
	console.log(outputString);
}

function printSkillBuild()
{
	for (var i = 0; i < 5; i++)
	{
	  var playerID = data.players[i];
	  console.log(playerID.hero_name);
	  console.log("=====SKILL BUILD=====");
	  for(var j = 0, level = playerID.ability_upgrades.length; j < level; j++)
	  {
		  var skillBuild = playerID.ability_upgrades[j];
		  console.log(getSkillName(skillBuild.ability,playerID.hero_name.toLowerCase().replace(" ","_"))+" "+skillBuild.level+" "+skillBuild.time);
		  console.log(skillBuild.ability+" "+skillBuild.level+" "+skillBuild.time);
		  console.log("=================");
	  }
	}
}

function getSkillName(id,hero_name) {
    var abilityDB = fs.readFileSync("abilities.json");
    var abilityList = JSON.parse(abilityDB).abilities;

    for (var i in abilityList) {
        if (abilityList[i].id == id) {
            return abilityList[i].name.replace(hero_name+"_","");
        }
    }
}
