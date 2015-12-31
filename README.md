Prototype of Auginte: Desktop application part
==============================================

This is predecessor of [Auginte Zooming based organiser](https://github.com/Auginte/zooming-based-organizer)
 
**DO NOT WASTE YOUR TIME IMPROVING THIS PROJECT**

This project is left for:
 * historical reasons, eventually all functionality will be migrated to [Auginte Zooming based organiser](https://github.com/Auginte/zooming-based-organizer)
 * backward compatibility reasons. There are projects and examples generated with this version, so it is usefull for import/export functions.

Known issues
------------

* When zooming a lot in or out **data become corrupted**.
  Main reason for descendant project with *Infinity zooming*.
* Dragging objects near border program could crash because of **concurrent modification**.
  Main reason for choosing `Scala` with immutability and actor model for easier reasoning about asynchronous behaviour.  
 
License
-------

[Apache 2.0](LICENSE.txt)

Author
------

Aurelijus Banelis
http://auginte.com

 