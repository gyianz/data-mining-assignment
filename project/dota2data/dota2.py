import dota2api
import json

api = dota2api.Initialise()


for num in range(2589990581,2590040581):
	try:
  		match = api.get_match_details(num)
		output = json.loads(match.json)
		filename = str(num)+".json"
		target = open(filename,'w')
		target.write(json.dumps(output, indent=2))
	except:
  		pass
