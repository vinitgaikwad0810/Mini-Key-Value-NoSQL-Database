# Mini-Key-Value-NoSQL-Database

Objectives:  In this project, I will be extending an existing key/value database store written in Java and using the Shore DB API Design.  

The starter source code is available in GitHub at:  https://github.com/paulnguyen/data/tree/master/nosql (Links to an external site.) and is also in the course Cloud9 Workspace https://ide.c9.io/paulnguyen/data (Links to an external site.) (read-only).  


Requirements:  Implement a "three node" instance of the database on AWS EC2 free-tier instances.  Design should decide on either AP or CP based replication. All three instances must be public accessible (via public IP) and must be in different sub-nets in the same VPC External access must be via REST API and should expose full CRUD operations for Key/Value documents Replication must be between the nodes using the internal network The document format must be in JSON


# Source Code Directory -> https://github.com/vinitgaikwad0810/Mini-Key-Value-NoSQL-Database/tree/master/nosql_master/src

Following classes are authored by me.

ClientDataHandler.java

ClientRestServer.java

ConfService.java

DataMapper.java

InternalDataHandler.java

InternalRestServer.java

NodeHelper.java


