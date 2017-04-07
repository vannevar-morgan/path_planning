Path Planning
=============================================

Implementations of various path planning algorithms.  PathFinder is a simulator to illustrate the algorithms.  It was intended originally to be more flexible so that users could implement their own algorithms but it needs some improvement.
- The only algorithm implemented is Dijkstra.  The algorithm itself works but the implementation is mixed across the UI which is amateurish and prevents other path planning algorithms from easily being implemented.
- It isn't extensible.
- I had wanted the illustration to proceed step by step but instead it finishes all of Dijkstra, then repaints the cells.  As far as I can tell this is a limitation of swing.  I might reimplement in sfml for more control over graphics.


Repository Contents
-------------------
* **/PathFinder** - Simulator to illustrate path planning algorithms.



License Information
-------------------

All code is released under [GNU GPLv3.0](http://www.gnu.org/copyleft/gpl.html).

If you find any errors please message about them.
