In this project I try to gather data of mobile sensors (gps, accelerometer, gyroscope, compass, barometer,wifi) then save these gathered data into csv files where we can use them later in something like activity recognition, offline tracking, deadrocking or other object localiztion in self driven cars or any other future benefits we may explore.

1- accelerometer: read change rate in acceleration force in 3 directions (x, y, z). we get the new gravity force by using low pass filter and then use high pass filter to remove this gravity from the acceleration readings 

2- gyroscope: read the change rate in the angular velocity in the 3 direction. then integrate it to get the change in the angle around (x, y, z) axises.

3-compass: read three components azimuth ,angle between your mobile and the earth's north pole, pitch ,the agnle between your mobile and the plane parallel to the ground, and the last one is roll, the angle between your mobile and the perpendicular plane on the ground. this is occured by using software sensor composed from magnetometer and accelerometer. mapping their readings relative to the mobile cooridinates system to the earth coordinates system

4-GPS: reading the location of the mobile (latitude, longitude, altitude) 

5-barometer: reading the height from the sea level by a specific equation
