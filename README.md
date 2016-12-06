#### 
<img src="https://dl2.pushbulletusercontent.com/KbQ1LfBQ4PvfBj4LmbcYmXDPBHlTsnHR/Passageway.png" width="500px">

### Project Description
Passageway is an attempt to provide a cost-effective, scalable, and automated method of aggregating foot traffic data within a facility. Such data aggregation offers meaningful statistics about the foot traffic of individuals within a defined space. With Passageway, users are able to see heat maps and view graphs that represent the foot traffic in a building, and analyze it on a website with various building floor-plans layered onto a Google Map. Users may also specify a certain time frame to see specific temporal data. All of the data is collected within the facilities through a system including CHIP computers connected to a pair of Infrared break beams. Ultimately, the data may be used to determine the frequency of activity for specific rooms, hallways, or entryways. Foot traffic data may be useful for understanding activity within a facility, seeing popularity of certain areas during specific times, or identifying times where room capacity is exceeded and thus presents a fire code violation.

### Architecture Overview
<center><img src="http://i.imgur.com/LuledZu.png" width="700px"></center>

### Repositories

 - [Android](https://github.com/Passageway/PassagewayAndroid) *You are here*
 - [Field Unit](https://github.com/Passageway/PassagewayFieldUnit) 
 - [Web](https://github.com/Passageway/PassagewayWeb)

### Repo Overview
This is the Android part of the Passageway project. The overall goal of this app is to provide users to administrate Passageway Field Units without the need to go into Firebase. This provides a simple, easy-to-use user interface that makes setup a breeze.

### What it Looks Like
Here are some pictures to give a better understanding of what this app looks like when set up. The left shows the homescreen of the app. Here, all field units that have been connected to the Firebase database will appear. The green color on the left of each listing indicates that the unit has GPS coordinates associated with it. It does *not* indicate online status. 
<center><img src="http://i.imgur.com/g0qwYri.png" width="600px"></center>

Once you click on a unit, you arrive to a page like what is displayed on the right. Here, you can edit all aspects of the field unit except IP Address and MAC Address, which are assigned by the network and hardware itself. You can set the unit's location to your current location using the top button in the bottom right. You can get finer detail by editing the latitude and longitude manually or drag the pin on the map. Once you save, that information is pushed and updated in the Firebase Database.


### Detailed Set-up Instructions
The wiki section will have more details on how to exactly set the system up in your own environment for testing. it is worth noting that this system is very much still in early stages and is not perfect by any means.
