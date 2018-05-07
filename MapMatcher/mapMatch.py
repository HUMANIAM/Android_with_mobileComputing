import json
import csv
import pandas as pd
import requests
import gmplot
import numpy as np
import time
def readLatAndLong():
	# Initialize two empty lists to hold the latitude and longitude values
	df = pd.read_csv('df_gps.csv')
	latitude = df[["gps_latitude"]].values
	longitude = df[["gps_longitude"]].values
	
	return latitude, longitude

def mapMatching(latitude, longitude):
	startTime = time.time()
	params = {"interpolate": "true", "key": "AIzaSyAOUROM-SAYlA5MqR_aF4rbdkMXbj_ZUpQ"}
	new_lat = []
	new_lon = []
	lenn = 5;
	trace = ""
	for i in range(len(latitude)):
		lenn -= 1
		trace += str(longitude[i][0]) + "," + str(latitude[i][0]) + "|"

		if lenn<0:
			
			lenn = 5
			trace = trace[0:-1]
			params["path"] = trace

			response = requests.get("https://roads.googleapis.com/v1/snapToRoads", params = params)
			mappedGps = json.loads(response.text)
			
			trace = ""
			try:
				for k in range(len(mappedGps["snappedPoints"])):
					new_lat.append(mappedGps["snappedPoints"][k]["location"]["latitude"])
					new_lon.append(mappedGps["snappedPoints"][k]["location"]["longitude"])
					print(k)
				
			except Exception as e:
				print(mappedGps)
				return new_lon, new_lat

			
	return new_lon, new_lat

def main():
	lat, lon = readLatAndLong()
	nlat, nlong = mapMatching(lat, lon)

	# Initialize the map to the first location in the list
	gmap = gmplot.GoogleMapPlotter(nlat[0], nlong[0], 16)

	#draw
	gmap.polygon(nlat, nlong, '#FF6666', edge_width=10)
	# Write the map in an HTML file
	gmap.draw('map.html')
if __name__ == '__main__':
	main()