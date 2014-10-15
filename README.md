DNW-depmap
=============

DNW-depmap is an Eclipse plug-in to help create Java code dependency map. Our goal is to help developer easily identify the areas of influence when they change code.

Development Environment
-----------------------
- JDK 1.7 (Neo4j graph database need)
- Eclipse 3.7 (indigo)
  - import [codetemplates.xml](https://github.com/manbaum/dnw-depmap/blob/develop/codetemplates.xml) into ```Preferences|Java|Code Style|Code Templates```
  - import [codestyle.xml](https://github.com/manbaum/dnw-depmap/blob/develop/codestyle.xml) into ```Preferences|Java|Code Style|Formatter```
- Format code before commit

Getting Start
-------------
1. Download Neo4j 2.1.5 from [www.neo4j.com](http://neo4j.com/download/).
2. Follows [the instruction](http://neo4j.com/docs/2.1.5/deployment.html) to install and deploy Neo4j server.
3. Check out the project into Eclipse.
4. Configure ```DBPATH``` in ```Activator.java``` to the database store directory of your installation of Neo4j server.
4. Compile and Run the project as a Eclipse Application.
5. In running workspace, import a Java project.
6. Compile the Java project to make sure no syntax error in it.
7. Select any Java element (Java file, or package, or even the whole Java Project) and click the mouse right key, from the pop-up context menu select "Analysis Dependency".
8. Wait for done.
9. Close the running Eclipse Application.
10. Startup the Neo4j server, and open a browser, writing Cypher query to manipulate the data generated by this program.
 ```
 match p=((:Class)-[:Declares]->(:Method)-[:Invokes]->(:Method)<-[:Declares]-(:Class {name:"java.lang.Object"})) return p
 ```

Data Model
----------
- Nodes labeled ```Type``` must be labeled either ```Class``` or ```Interface```.
- Nodes labeled ```Class``` denotes Java classes.
 - property ```name```: the qualified name of the class, with all type parameters replaced with ```'?'```. e.g. ```'java.util.ArrayList<?>'```.
 - property ```caption```: the simple name (no package name) of the class, with the bound type parameters. e.g. ```'ArrayList<Object>'```.
 - property ```extends```: the qualified name of its superclass. e.g. ```'java.lang.Object'```. 
 - property ```implements```: an array contains all names of interfaces which implements. e.g. ```['java.util.List<?>']```. 
- Nodes labeled ```Interface``` denotes Java interfaces. 
 - property ```name```: the qualified name of the interface, with all type parameters replaced with ```'?'```. e.g. ```'java.util.List<?>'```.
 - property ```caption```: the simple name (no package name) of the interface, with the bound type parameters. e.g. ```'List<Object>'```. 
 - property ```extends```: an array contains all names of interfaces which extends. e.g. ```['java.util.Collection<?>']```.
- Relation type ```Extends``` denotes the class inheritance.
- Relation type ```Implements``` denotes the interface implementation.
- Nodes labeled ```Method``` denotes Java methods.
 - property ```name```: the full name consists of two parts: 1) the qualified name of its declaring class, 2) the method declaring, made by the method name and all the arguments' type names in parentheses. The naming convention defines as below.
  - For static method, uses ```'/'``` separate  the two parts. e.g. ```'java.util.Arrays/asList(java.lang.Object[])'```.
  - For non-static method, uses ```'#'``` separate the two parts. e.g. ```'java.lang.Object#wait()'```.
- Relation type ```Declares``` denotes the method declaration, the from side should be a ```Type``` node, and the to side should be a ```Method``` node.
- Relation type ```Invokes``` denotes the method invocation, it happens between two ```Method``` nodes.
 - property ```args```: an array contains all arguments passed in when the invocation happens.

License
-------
Copyright (c) 2014 DNW Technologies and others.<br/>
All rights reserved. This program and the accompanying materials<br/>
are made available under the terms of the Eclipse Public License v1.0<br/>
which accompanies this distribution, and is available at<br/>
http://www.eclipse.org/legal/epl-v10.html
