# -*- coding: utf-8 -*-
"""
Created on Tue Feb 27 02:36:07 2018

@author: Sara Hussien
"""

import gmplot

import csv
import pandas as pd
# Fetch all the data returned from gps file

df = pd.read_csv('df_gps.csv')

# Initialize two empty lists to hold the latitude and longitude values
latitude = df[["gps_latitude"]].values
longitude = df[["gps_longitude"]].values


# Initialize the map to the first location in the list
gmap = gmplot.GoogleMapPlotter(latitude[0],longitude[0],16)

# Draw the points on the map. I created my own marker for '#FF66666'. 
# You can use other markers from the available list of markers. 
# Another option is to place your own marker in the folder - 
# /usr/local/lib/python3.5/dist-packages/gmplot/markers/
gmap.polygon(latitude[0:2500], longitude[0:2500], '#FF6666', edge_width=5)
# gmap.polygon(latitude[834:2809], longitude[834:2809], '#FF6666', edge_width=5)

# Write the map in an HTML file
gmap.draw('map.html')

