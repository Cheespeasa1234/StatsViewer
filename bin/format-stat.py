# simple script to format the statistics file as a JSON file. Meant for player stats.
import sys
import json
jason = {}
with open(sys.argv[1], "r") as f:
    jason = json.load(f)
    jason = jason["stats"]
with open(sys.argv[2], "w+") as f:
    out = json.dumps(jason).replace(" ", "");
    f.write(out);