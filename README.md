# CosaP

This program can be run in an Eclipse environment. It is assumed that the program will be run on Eclipse 2018-2019 version.
It is expected that Google Web Toolkit will be installed via the Eclipse marketplace. It is expected that a remote/local db is available to connect to.
Remote/local db uses repo db.

Otherwise all neccessary files are available with this repo download.

Overview

## File Structure
#### >.metadata
-->Purpose: Eclipse Workspace. Nothing to concern self with.
#### >Hello Triangle
-->Purpose: Eclipse project. Source of the program. Everything built around this.
##### ---->.settings
------>Eclipse fluff.
##### ---->Shaders
------>Directory for shader program src files.
##### ---->bin
------>binary fluff.
##### ---->src
### ------>guiPackage

###### -------->StrokeListener

---------->Class that handles user I/O.

### ------>mainPackage

###### -------->FaceNormal

---------->Class holding normal vector

###### -------->ModelObject

---------->Class representing a 3d model

###### -------->ObjectFace

---------->Class representing 3 vertices that make a face.

###### -------->StaticClass

---------->Main class, main method.

###### -------->Vertice

---------->Class holding xyz coords.
###### ---->tmp
------>fluff

---->.classpath

------>fluff

---->.project

------>fluff

#### >JOML - Java open math library
-->Purpose: This directory contains the joml library in a maven project. Import the project for use as Joml library. Will need to delete sections of pom.xml. They will show up as errors after importing maven project.
Put this imported project in classpath.
#### >SWT
-->Purpose: This directory has the SWT jar to add to classpath.
#### html
-->Purpose: This directory holds the /var/www/html folder located on an Ubuntu 18.04 server VM that the program uses as a src to download 3d model files.
#### lwjgl-release-3.2.0-custom
-->Purpose: This directory holds all neccessary jars for lightweight java opengl.
#### appdb.sql
-->Purpose: The database to load into the Ubuntu (or any really that run PHP) server for the program to get files from.

## Things To Do on startup
1. Setup vm.
2. Make sure GWT is installed in eclipse
3. Setup classpath for SWT, JOML, JOGL.
