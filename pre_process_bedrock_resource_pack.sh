# Filled Maps, because items that replace filled maps do not work correctly in Bedrock (you can't see the map image)
rm assets/minecraft/items/filled_map.json

# Copy the overlay bedrock folder to here folder
cp -r overlay_bedrock/* .