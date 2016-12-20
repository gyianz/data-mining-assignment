var fs = require("fs");
var data = fs.readFileSync("heroes.json");

var list = JSON.parse(data);

var stringBuilder = "";

for(var i = 0; i < list.heroes.length; i++)
{
	stringBuilder += "\""+list.heroes[i].localized_name.replace(" ","")+"\"";
	stringBuilder += ",";
}

console.log(stringBuilder);
