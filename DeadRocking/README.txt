in this project we try to apply dead rocking alogrithm on data gathered from caire city at burg alarb. where we have the start
locaiton of tracked object (car). and has gathered data of sensors like(gyro, accele, compass, barometer). all we need to do is
use the first location and the gathered sensor data to make offline tracking of the car.

we use accelermoter readings to compute the displacement from timestamp (may be window contains many timestamps) to anthor timestam
and the azimuth angle that refers to in which direction the displacement occurs.

by passing them to VincentyDistance we get the new latitude and longitude. continue our compution with this new location and so on