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

# 25.1413° N, 55.1853° E of burg alarab
acceleReadings = [[]]
gravity        = [0.3151, 9.896491, 0.5697]		#intial gravity vector
Vi 			   = [0.0, 0.0, 0.0]      			#intial velocity vector
timeStamp      = [] 
STEP 		   = 50							    #step window in updating gps location 
ALPHA 		   = 0.9982							#used in low pass filter to remove gravity force
DEG2RAD         = 3.14159/180

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


def readData():
	global acceleReadings, timeStamp
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
		print("we need to change threshold of beginning matched time ")
		exit(0)

	return latitude[0], longitude[0], azimuth, index

#******************************************************************************************
def removeGravityForce(accelermeter):
	global ALPHA, gravity
	#  Isolate the force of gravity with the low-pass filter.
	gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * accelermeter[0];
	gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * accelermeter[1];
	gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * accelermeter[2];

	# Remove the gravity contribution with the high-pass filter.
	accelermeter[0] = accelermeter[0] - gravity[0];
	accelermeter[1] = accelermeter[1] - gravity[1];
	accelermeter[2] = accelermeter[2] - gravity[2];

#$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

def getDisplacement(j):
	global acceleReadings, timeStamp, Vi, STEP
	dD = [0.0, 0.0, 0.0]       			#displacement between current location and previous location [x, y, z]

	#evaluate distance of window with size STEP
	for k in range(j+1, j+STEP+1, 1):  
		dT = timeStamp[k] - timeStamp[k-1]

		#remove gravity force from the accelerometer readings
		removeGravityForce(acceleReadings[k])
		# displacement in directions x, y, z
		dD[0] += Vi[0]*dT + 0.5*float(acceleReadings[k][0])*(dT**2)			#displacement in x direction
		dD[1] += Vi[1]*dT + 0.5*float(acceleReadings[k][1])*(dT**2)			#displacement in y direction
		dD[2] += Vi[2]*dT + 0.5*float(acceleReadings[k][2])*(dT**2)			#displacement in z direction

		#update intial velocity
		Vi[0] = Vi[0] + dT*float(acceleReadings[k][0])						#intial velocity in x direction
		Vi[1] = Vi[1] + dT*float(acceleReadings[k][1])						#intial velocity in y direction
		Vi[2] = Vi[2] + dT*float(acceleReadings[k][2])						#intial velocity in z direction

	#return displacement according to this new window
	return math.sqrt(float((dD[0]**2 + dD[1]**2 + dD[2]**2)))
########################################################################################

"""*every time update current location of gps by using 
		*geopy.distance.VincentyDistance(meters = distance beween the new locaiton and the previous location)
		*this will return new location in directions(E, W, S, N) in degrees (d m s) so convert it to float values
"""		
def deadRocking(startLat, startLon, azimuth, j):
	new_lat = [startLat]
	new_lon = [startLon]
	k = 1

	# read new location with step 20 sample
	for i in range(j, len(azimuth)-STEP, STEP):
		dD = getDisplacement(i) 		#displacement to the new location
		dazim = 0.0
		azim = 0.0

		#evaluate the average change through the window and add it to the angle of current locaiton
		for m in range(1, STEP+1):
			dazim += azimuth[i+m]-azimuth[i+m-1]

		azim = azimuth[i] + dazim/(2*STEP)
		
		# if(i>12800):
		# 	azim = -20
		if(i>8000):
			break

		# evaluate the new latitude and longitude
		#azim = float(sum(azimuth[i : i+STEP+1])) / STEP   #operate on average azimuth
		calculator  = geopy.distance.VincentyDistance(meters = dD)


		newloc = calculator.destination((new_lat[k-1], new_lon[k-1]), azim)
		k += 1

		#append new location based on accelermeter readings and azimuth angle
		new_lat.append(newloc.latitude)
		new_lon.append(newloc.longitude)

	return new_lat, new_lon

#%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
def writeMap(nlat, nlong):
	# Initialize the map to the first location in the list
	gmap = gmplot.GoogleMapPlotter(nlat[0], nlong[0], 16)

	#draw
	gmap.polygon(nlat, nlong, '#FF6666', edge_width=5)
	# Write the map in an HTML file
	gmap.draw('mapWithDeadRocking.html')

def main():
	startLat, startLon, azimuth, index = readData()
	nlat, nlong = deadRocking(startLat, startLon, azimuth, index)
	writeMap(nlat, nlong)

if __name__ == '__main__':
	main()