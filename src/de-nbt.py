# simple script to convert NBT data to JSON data. Meant for level.json.
import sys
import nbtlib
import json
nbt_data = nbtlib.load(sys.argv[1])
nbt_dict = nbt_data.unpack(json = True)
json_str = json.dumps(nbt_dict)
with open(sys.argv[2], "w+") as f:
    f.write(json_str);