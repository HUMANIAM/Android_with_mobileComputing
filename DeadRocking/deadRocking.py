import json
import csv
import pandas as pd
import requests
import gmplot
import numpy as np
import time
import geopy
import geopy.distance
import math
import os

def findSynTime(gpsSeconds, sensorSeconds):
	#get indices of matched time if you inspect gps data, you will find the first reading of gps
	# starts at 4.746289895 and the first one of sensors starts at 0, so we need to find the syc time
	#where gpsTime is about equal to sensorTime 
	syncTimes = [i for i, item in enumerate(gpsSeconds) if item in sensorSeconds]	
	for i in range(len(sensorSeconds)):
		if(gpsSeconds[0]-sensorSeconds[i] < 0.1):		#if diff=0.1 we take this time of sensor as start time
			return i
		pass

	return -1				

def readLatAndLong():
	# Initialize two empty lists to hold the latitude and longitude values
	dfGps 	  = pd.read_csv('df_gps.csv')
	dfSensors = pd.read_csv('df_sensors.csv')

	#read latitudes and longitude
	latitude = dfGps[["gps_latitude"]].values
	longitude = dfGps[["gps_longitude"]].values

	#read accelermoter readings and azimuth readings over time
	acceleReadings = dfSensors[["Accelerator_x", "Accelerator_y", "Accelerator_z"]].values
	azimuth 	   = dfSensors[["Azimuth"]].values
	timeStamp 	   = dfSensors[["seconds"]].values

	# get the synchronized time between GPS readings and sensors Readings
	index = findSynTime(dfGps["seconds"], dfSensors["seconds"])
	if(index == -1):
		print("we need to change threshold of time difference")
		exit(0)

	return latitude[0], longitude[0], timeStamp, azimuth, acceleReadings, index

def removeGravityForce(accelermeter, gravity):
	alpha = 0.7
	#  Isolate the force of gravity with the low-pass filter.
	gravity[0] = alpha * gravity[0] + (1 - alpha) * accelermeter[0];
	gravity[1] = alpha * gravity[0] + (1 - alpha) * accelermeter[1];
	gravity[2] = alpha * gravity[0] + (1 - alpha) * accelermeter[2];

	# Remove the gravity contribution with the high-pass filter.
	accelermeter[0] = accelermeter[0] - gravity[0];
	accelermeter[1] = accelermeter[1] - gravity[1];
	accelermeter[2] = accelermeter[2] - gravity[2];

def deadRocking(startLat, startLon, timeStamp, azimuth, acceleReadings, j):
	new_lat = [startLat]
	new_lon = [startLon]
	gravity = [0.0, 9.806, 0.0]					#intial gravity vector
	Vi 		= [0.0, 0.0, 0.0]      			    #intial velocity, at every new location we update it to (Vi = Vi + a*dT)
	dT 		= 0.0	   		    				#delta t (time(current location) - time(previous location))
	dD 		= [0.0, 0.0, 0.0]       			#displacement between current location and previous location [x, y, z]
	mD, premD = 0.0, 0.0 				    	#magintude of displacement
	k = 1
	"""*every time update current location of gps by using 
		*geopy.distance.VincentyDistance(meters = distance beween the new locaiton and the previous location)
		*this will return new location in directions(E, W, S, N) in degrees (d m s) so convert it to float values
	"""	

	# read new location with step 20 sample
	STEP = 1
	for i in range(j+STEP, len(azimuth), STEP):
		dT    = timeStamp[i] - timeStamp[i-1]

		#remove gravity force from the accelerometer readings
		#print("befor ", acceleReadings[i])
		removeGravityForce(acceleReadings[i], gravity)
		#print("after ", acceleReadings[i])

		# displacement in directions x, y, z
		dD[0] = Vi[0]*dT + 0.5*float(acceleReadings[i][0])*(dT**2)			#displacement in x direction
		dD[1] = Vi[1]*dT + 0.5*float(acceleReadings[i][1])*(dT**2)			#displacement in y direction
		dD[2] = Vi[2]*dT + 0.5*float(acceleReadings[i][2])*(dT**2)			#displacement in z direction
		mD = math.sqrt(float((dD[0]**2 + dD[1]**2 + dD[2]**2)[0]))

		if(i>500000):
			break

		#update intial velocity
		Vi[0] = Vi[0] + dT*float(acceleReadings[i][0])					#intial velocity in x direction
		Vi[1] = Vi[1] + dT*float(acceleReadings[i][1])					#intial velocity in y direction
		Vi[2] = Vi[2] + dT*float(acceleReadings[i][2])					#intial velocity in z direction

		# evaluate the new latitude and longitude
		#azim = float(sum(azimuth[i-STEP : i])) / STEP   #operate on average azimuth
		calculator  = geopy.distance.VincentyDistance(meters = mD)
		newloc      = calculator.destination((new_lat[k-1], new_lon[k-1]), float(azimuth[k-1])+180)
		k += 1

		#append new location based on accelermeter readings and azimuth angle
		new_lat.append(newloc.latitude)
		new_lon.append(newloc.longitude)
			
	return new_lat, new_lon

def writeMap(nlat, nlong):
	# Initialize the map to the first location in the list
	gmap = gmplot.GoogleMapPlotter(nlat[0], nlong[0], 16)

	#draw
	gmap.polygon(nlat, nlong, '#FF6666', edge_width=10)
	# Write the map in an HTML file
	gmap.draw('mapWithDeadRocking.html')

def main():
	startLat, startLon, timeStamp, azimuth, acceleReadings, index = readLatAndLong()
	nlat, nlong = deadRocking(startLat, startLon, timeStamp, azimuth, acceleReadings, index)
	writeMap(nlat, nlong)

if __name__ == '__main__':
	main()