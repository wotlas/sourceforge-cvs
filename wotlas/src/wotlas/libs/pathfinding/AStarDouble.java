/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001 - WOTLAS Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package wotlas.libs.pathfinding;

import wotlas.utils.Debug;
import wotlas.utils.List;

import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Rectangle;

import java.lang.*;

import java.util.*;

/** A* algorithm finds the optimal path between 2 points
 *
 * usage:
 * - create a AStar object
 *   AStar astar;
 * - initialize the mask with:
 *    a buffered image
 *   astar.initMask(BufferedImage maskBuffImg, int imgWidth, int imgHeight)
 *    or a boolean array
 *   aStar.setMask( boolean mask[][] )
 * - set the sprite size
 *   astar.setSpriteSize(int size)
 * - start the search
 *   AStarObject.findPath(startPoint, goalPoint);
 *
 * @author Petrus, Aldiss
 * @see wotlas.libs.pathfinding.NodeDouble
 */

public class AStarDouble
{
 /*------------------------------------------------------------------------------------*/

  /** Our AStarDouble object.
   */
  private static AStarDouble aStar=null;

  /** mask of the image : mask[i][j] is true if pixel(i,j) is not blocked
   */
  static private boolean[][] map;

  /** width of the map
   */
  static private int mapWidth;

  /** height of the map
   */
  static private int mapHeight;

  /** size of the sprite (in CELL units)
   */
  static private int SPRITE_SIZE = 4;

  /** size of a mask's cell (in pixels)
   */
  static private int tileSize = -1;

  /** True if we show debug informations
   */
  static public boolean SHOW_DEBUG = false;

 /*------------------------------------------------------------------------------------*/

  /** To set sprite size
   */
  static public void setSpriteSize(int size) {
    SPRITE_SIZE = size-1;
  }

  /** To get sprite size
   */
  static public int getSpriteSize() {
    return (SPRITE_SIZE+1);
  }

 /*------------------------------------------------------------------------------------*/

  /** To set the tile size
   */
  static public void setTileSize(int tileSize) {
     AStarDouble.tileSize = tileSize;
  }

  /** To get the tile size
   */
  static public int getTileSize() {
    return tileSize;
  }

 /*------------------------------------------------------------------------------------*/

  /** Empty constructor.
   */
  public AStarDouble() {
  }

 /*------------------------------------------------------------------------------------*/

  /** To get AStarDouble object
   */
  static public AStarDouble getAStar() {
    return aStar;
  }

  /** Test to know if pathFollower is used by client (initialized) or server (not initialized)
   *
   * @returns true if AStarDouble have been initialized
   */
  static public boolean isInitialized() {
    return !(aStar==null);
  }

 /*------------------------------------------------------------------------------------*/

  /**
   * Estimates the distance between 2 points
   *
   * @param poinFrom first point
   * @param pointTo second point
   * @return the distance between the 2 points
   */
  private double estimate(Point pointFrom, Point pointTo) {
    //return (int) pointFrom.distanceSq(pointTo);
    return pointFrom.distance(pointTo);
    /**
     * Other distances:
     * return Math.max(Math.abs(pointFrom.x-pointTo.x),Math.abs(pointFrom.y-pointTo.y));
     * return (pointFrom.x-pointTo.x)*(pointFrom.x-pointTo.x)+(pointFrom.y-pointTo.y)*(pointFrom.y-pointTo.y);
     */
  }

 /*------------------------------------------------------------------------------------*/

  /**
   * begins optimal path search
   */
  private NodeDouble searchNode(Point pointStart, Point pointGoal) {
    /* list of not visited Nodes */
    Hashtable open = new Hashtable(300);
    /* list of visited Nodes */
    Hashtable closed = new Hashtable(300);
    /* sorted open Nodes */
    List nodes = new List();

    double cost = 0;
    double estimation = estimate(pointStart, pointGoal);
    NodeDouble firstNode = new NodeDouble();
    firstNode.point = pointStart;
    firstNode.g = cost;
    firstNode.h = estimation;
    firstNode.f = cost + estimation;
    firstNode.parent = null;
    open.put(pointStart, firstNode);
    nodes.addElement(firstNode);

    NodeDouble bestNode;			         // best node of Vector "nodes" (the lowest f)
    List childPoints;                  // children Points of "bestNode"
    List children = new List(8);       // children Nodes of "bestNode"

    while (!nodes.isEmpty())
    {
      bestNode = (NodeDouble) nodes.elementAt(0);

      //System.out.println("bestNode : ("+bestNode.point.x+","+bestNode.point.y+")");

      /* to avoid having to remove ?? */
      if (closed.get(bestNode.point) != null) // if bestNode was in closed
      {
        nodes.removeFirstElement();   // remove "bestNode" from "nodes"
        continue;
      }

      //System.out.print("Test : has the goal been reached ? ");
      //if ((bestNode.point.x == pointGoal.x) && (bestNode.point.y == pointGoal.y))
      if (bestNode.point.equals(pointGoal))
      {
        //System.out.println("yes");
        nodes.removeAllElements();
        open.clear();
        closed.clear();
        return bestNode;
      }
      else
      {
        //System.out.println("no");
      }

      children.removeAllElements();

      childPoints = generateChildren(bestNode.point);

      Point childPoint;       // child Point of "bestNode"
      NodeDouble closedNode;	// not null if childPoint was in closed
      NodeDouble openNode;	  // not null if childPoint was in open
      NodeDouble oldNode;	    // not null if childPoint was in open or closed
      double childCost;       // cost of the children of "bestNode"
      //double estimation;

      for (int i=0; i<childPoints.size(); i++)
      {
        closedNode = null;	  // not null if childPoint was in closed
        openNode = null;	    // not null if childPoint was in open
        oldNode = null;	      // not null if childPoint was in open or closed

        childPoint = (Point) childPoints.elementAt(i);
        //System.out.println("  -> exploration of child ("+childPoint.x+","+childPoint.y+")");

        childCost = bestNode.g + 1; // ?? In fact, we have to calculate the childCost
        //childCost = bestNode.g + estimate(childPoint, bestNode.point);

        // test if childPoint was in closed or open
        if ( (closedNode = (NodeDouble) closed.get(childPoint)) == null ) {
          openNode = (NodeDouble) open.get(childPoint);
        }
        oldNode = (openNode != null) ? openNode : closedNode;

        if (oldNode != null) // childPoint was in open or closed
        {
          //System.out.println("Test : was childPoint in open or closed ? yes");
          if (childCost < oldNode.g) // we have found a more economic path in open
          {
            if (closedNode != null)  // childPoint was in closed
            {
              // we have found a more economic path in closed
              // we move the oldNode from closed to open
              open.put(childPoint, oldNode);
              closed.remove(childPoint);
            }
            else  // childPoint was in open
            {
              estimation = oldNode.h;
              oldNode = new NodeDouble();
              oldNode.point = childPoint;
              oldNode.parent = bestNode;
              oldNode.g = childCost;
              oldNode.h = estimation;
              oldNode.f = childCost + estimation;
              open.put(childPoint, oldNode);
            }
            oldNode.parent = bestNode;
            oldNode.g = childCost;
            oldNode.f = childCost + oldNode.h;
            children.addElement(oldNode);
          }
          // if childCost > oldNode.g ie if newcost > oldcost => do nothing
        }
        else // if childPoint was not in open or closed
        {
          //System.out.println("Test : childPoint was in open or closed ? no");
          NodeDouble newNode = new NodeDouble();

          newNode.point = childPoint;
          newNode.parent = bestNode;
          estimation = estimate(childPoint, pointGoal);
          newNode.h = estimation;
          newNode.g = childCost;
          newNode.f = childCost + estimation;
          open.put(childPoint, newNode);

          children.addElement(newNode);
        }
      } // we have explored all the children of bestNode

      open.remove(bestNode.point);
      closed.put(bestNode.point, bestNode);
      nodes.removeFirstElement();
      addToNodes(children, nodes);
    }
    if (SHOW_DEBUG)
      System.out.println("no path found");

    nodes.removeAllElements();
    open.clear();
    closed.clear();
    return null; // no solution
  }

  /**
   *
   */
  private int rbsearch(int l, int h, double tot, double costs, List nodes) {
    if (l>h)
      return l; //insert before l
    int cur = (l+h)/2;
    double ot = ((NodeDouble) nodes.elementAt(cur)).f;
    if ((tot < ot) || (tot == ot && costs >= ((NodeDouble) nodes.elementAt(cur)).g))
      return rbsearch(l, cur-1, tot, costs, nodes);
    return rbsearch(cur+1, h, tot, costs, nodes);
  }

  /**
   *
   */
  private int bsearch(int l, int h, double tot, double costs, List nodes) {
   int lo = l;
   int hi = h;

    while (lo<=hi)
    {
      int cur = (lo+hi)/2;
      double ot = ((NodeDouble) nodes.elementAt(cur)).f;
      if ((tot < ot) || (tot == ot && costs >= ((NodeDouble) nodes.elementAt(cur)).g))
	      hi = cur - 1;
      else
	      lo = cur + 1;
    }
    return lo; //insert before lo
  }

  /**
   *
   */
  private void addToNodes(List children, List nodes) {
    NodeDouble newNode;
    int idx;
    int idxEnd = nodes.size()-1;
    for (int i=0; i<children.size(); i++)
    {
      newNode = (NodeDouble) children.elementAt(i);
      idx = bsearch(0, idxEnd, newNode.f, newNode.g, nodes);
      nodes.insertElementAt(newNode, idx);
    }
  }

 /*------------------------------------------------------------------------------------*/

  /**
   * test if a point is valid for the path
   * regarding the sprite size
   *
   * @param x the x coordinate (in CELL units)
   * @param y the y coordinate (in CELL units)
   * @return true if point is valid (not blocked) in the {@link #mask mask}
   */
  public boolean isNotBlock(int x, int y) {
    /*if ( (x+SPRITE_SIZE==mapWidth) && (y+SPRITE_SIZE<mapHeight) )
      return (map[x][y] && map[x][y+SPRITE_SIZE]);

    if ( (x+SPRITE_SIZE<mapWidth) && (y+SPRITE_SIZE==mapHeight) )
      return (map[x][y] && map[x+SPRITE_SIZE][y]);

    if ( (x+SPRITE_SIZE==mapWidth) && (y+SPRITE_SIZE==mapHeight) )
      return map[x][y];*/

    if ( (x<0) || (x+SPRITE_SIZE>=mapWidth) || (y<0) || (y+SPRITE_SIZE>=mapHeight) )
      return false;

    return (map[x][y] && map[x][y+SPRITE_SIZE] && map[x+SPRITE_SIZE][y] && map[x+SPRITE_SIZE][y+SPRITE_SIZE] && map[x+SPRITE_SIZE/2][y+SPRITE_SIZE/2]);
  }

  /** test if a point is valid for the path
   * regarding the sprite size
   *
   * @param pt the point (in CELL units)
   * @return true if point is valid (not blocked) in the {@link #mask mask}
   */
  public boolean isNotBlock(Point pt) {
    return isNotBlock(pt.x, pt.y);
  }

 /*------------------------------------------------------------------------------------*/

  /**
   * test if a point (in CELL units) is a valid goal,
   * and correct the position regarding the sprite size
   *
   * @param pointGoal the point (in CELL units)
   * @return true if point is valid (not blocked) in the {@link #mask mask}
   */
  public boolean isValidGoal(Point pointGoal) {
    int x = pointGoal.x;
    int y = pointGoal.y;

    if (isNotBlock(x,y)) {
      if (SHOW_DEBUG)
        System.out.println("AStarDouble \t (" + x + "," + y + ") is valid point");
      return true;
    } else {
      if (SHOW_DEBUG) {
        System.out.println("AStarDouble \t (" + x + "," + y + ") is not a valid point -> search a valid point");
        System.out.println("\ttileSize = " + tileSize);
        System.out.println("\tSPRITE_SIZE = " + SPRITE_SIZE);
        System.out.println("\tmapWidth = " + mapWidth);
        System.out.println("\tmapHeight = " + mapHeight);
      }
      Debug.signal( Debug.NOTICE, null, "not a valid goal point -> search a valid point");              
    }

    if(x<0) pointGoal.x = 0;
    if(y<0) pointGoal.y = 0;

    // test if player is near border    
    if (x+SPRITE_SIZE>=mapWidth) {
//      if (SHOW_DEBUG)
//        System.out.println("test x near border");
//      if (map[x-SPRITE_SIZE-1][y]) {
        if (SHOW_DEBUG)
          System.out.print("player near border -> change x=mapWidth-SPRITE_SIZE-1");
        pointGoal.x = mapWidth-SPRITE_SIZE-1;
//        return true;
//      }
    }
    if (y+SPRITE_SIZE>=mapHeight) {
//      if (SHOW_DEBUG)
//        System.out.println("test y near border");
//      if (map[x][mapHeight-SPRITE_SIZE-1]) {
        if (SHOW_DEBUG)
          System.out.print("player near border -> change y=mapHeight-SPRITE_SIZE-1");
        pointGoal.y = mapHeight-SPRITE_SIZE-1;
//        return true;
//      }
    }

    // Correct the position
    if (x+SPRITE_SIZE<mapWidth) {
      if (isNotBlock(x+SPRITE_SIZE,y)) {
        if (SHOW_DEBUG)
          System.out.print("change x+");
        pointGoal.x += SPRITE_SIZE;
        return true;
      }
    }
    if ((x+SPRITE_SIZE<mapWidth) && (y+SPRITE_SIZE<mapHeight)) {
      if (isNotBlock(x+SPRITE_SIZE,y+SPRITE_SIZE)) {
        if (SHOW_DEBUG)
          System.out.print("change x+ y+");
        pointGoal.x += SPRITE_SIZE;
        pointGoal.y += SPRITE_SIZE;
        return true;
      }
    }
    if (y+SPRITE_SIZE<mapHeight) {
      if (isNotBlock(x,y+SPRITE_SIZE)) {
        if (SHOW_DEBUG)
          System.out.print("change y+");
        pointGoal.y += SPRITE_SIZE;
        return true;
      }
    }
    if ((x>SPRITE_SIZE) && (y+SPRITE_SIZE<mapHeight)) {
      if (isNotBlock(x-SPRITE_SIZE,y+SPRITE_SIZE)) {
        if (SHOW_DEBUG)
          System.out.print("change x- y+");
        pointGoal.x -= SPRITE_SIZE;
        pointGoal.y += SPRITE_SIZE;
        return true;
      }
    }
    if (x>SPRITE_SIZE) {
      if (isNotBlock(x-SPRITE_SIZE,y)) {
        if (SHOW_DEBUG)
          System.out.print("change x-");
        pointGoal.x -= SPRITE_SIZE;
        return true;
      }
    }
    if ((x>SPRITE_SIZE) && (y>SPRITE_SIZE)) {
      if (isNotBlock(x-SPRITE_SIZE,y-SPRITE_SIZE)) {
        if (SHOW_DEBUG)
          System.out.print("change x- y-");
        pointGoal.x -= SPRITE_SIZE;
        pointGoal.y -= SPRITE_SIZE;
        return true;
      }
    }
    if (y>SPRITE_SIZE) {
      if (isNotBlock(x,y-SPRITE_SIZE)) {
        if (SHOW_DEBUG)
          System.out.print("change y-");
        pointGoal.y -= SPRITE_SIZE;
        return true;
      }
    }
    if ((x+SPRITE_SIZE<mapWidth) && (y>SPRITE_SIZE)) {
      if (isNotBlock(x+SPRITE_SIZE,y-SPRITE_SIZE)) {
        if (SHOW_DEBUG)
         System.out.print("change x+ y-");
        pointGoal.x += SPRITE_SIZE;
        pointGoal.y -= SPRITE_SIZE;
        return true;
      }
    }

    return false;
  }

 /*------------------------------------------------------------------------------------*/

  /**
   * test if a point (in CELL units) is a valid start,
   * and correct the position regarding the sprite size
   *
   * @param pointGoal the point (in CELL units)
   * @return true if point is valid (not blocked) in the {@link #mask mask}
   */
  static public boolean isValidStart(Point pointGoal) {
    return aStar.isValid(pointGoal);
  }

  /**
   * test if a point (in pixels coordinate) is a valid start.
   * If it's an invalid point, search a valid point near it,
   * and correct the position regarding the sprite size
   *
   * @param pointGoal the point
   * @return true if point is valid or has been corrected, false otherwise
   */
  private boolean isValid(Point pointGoal) {
    // We search up to 3 pixels around the original point
    int radius = 3;

    int x = pointGoal.x/tileSize;
    int y = pointGoal.y/tileSize;

    if (isNotBlock(x,y)) {
      //if (SHOW_DEBUG)
      //  System.out.println("valid point");
      return true;
    } else {
      //if (SHOW_DEBUG)
      //  System.out.println("not a valid point -> search a valid point");
      Debug.signal( Debug.NOTICE, null, "not a valid start point -> search a valid point");  
      //Debug.signal( Debug.NOTICE, null, "  x = " + x + " ,  y = " + y);
    }

    // test if player is near border
    if (x+SPRITE_SIZE>mapWidth) {
      if (SHOW_DEBUG)
        System.out.println("test x near border");
      if (map[x-SPRITE_SIZE-1][y]) {
        if (SHOW_DEBUG)
          System.out.print("player near border -> change x=mapWidth-SPRITE_SIZE-1");
        pointGoal.x = mapWidth-SPRITE_SIZE-1;
        return true;
      }
    }
    if (y+SPRITE_SIZE>mapHeight) {
      if (SHOW_DEBUG)
        System.out.println("test y near border");
      if (map[x][mapHeight-SPRITE_SIZE-1]) {
        if (SHOW_DEBUG)
          System.out.print("player near border -> change y=mapHeight-SPRITE_SIZE-1");
        pointGoal.y = mapHeight-SPRITE_SIZE-1;
        return true;
      }
    }

    // We search a valid point around the original point

    //pointGoal.y = y-1;
    if (isNotBlock(x,y-1)) {pointGoal.y-=tileSize;return true;}
    //pointGoal.y = y+1;
    if (isNotBlock(x,y+1)) {pointGoal.y+=tileSize;return true;}

    //pointGoal.x = x-1;
    //pointGoal.y = y-1;
    if (isNotBlock(x-1,y-1)) {pointGoal.x-=tileSize;pointGoal.y-=tileSize;return true;}
    //pointGoal.y = y+1;
    if (isNotBlock(x-1,y+1)) {pointGoal.x-=tileSize;pointGoal.y+=tileSize;return true;}

    //pointGoal.x = x+1;
    //pointGoal.y = y-1;
    if (isNotBlock(x+1,y-1)) {pointGoal.x+=tileSize;pointGoal.y-=tileSize;return true;}
    //pointGoal.y = y+1;
    if (isNotBlock(x+1,y+1)) {pointGoal.x+=tileSize;pointGoal.y+=tileSize;return true;}

    //pointGoal.x = x-2;
    //for (pointGoal.y=y-2;pointGoal.y<y+3;y++) {
    for (int step=-2;step<3;step++) {
      if (isNotBlock(x-2,y+step)) {
        pointGoal.x-=2*tileSize;
        pointGoal.y+=step*tileSize;
        return true;
      }
    }

    //for (pointGoal.x=x-1;pointGoal.x<x+2;x++) {
    for (int step=-1;step<2;step++) {
      //pointGoal.y=y-2;
      if (isNotBlock(x+step,y-2)) {
        pointGoal.x+=step*tileSize;
        pointGoal.y-=2*tileSize;
        return true;
      }
      //pointGoal.y=y+2;
      if (isNotBlock(x+step,y+2)) {
        pointGoal.x+=step*tileSize;
        pointGoal.y+=2*tileSize;
        return true;
      }
    }

    //pointGoal.x = x+2;
    //for (pointGoal.y=y-2;pointGoal.y<y+3;y++) {
    for (int step=-2;step<3;step++) {
      if (isNotBlock(x+2,y+step)) {
        pointGoal.x+=2*tileSize;
        pointGoal.y+=step*tileSize;
        return true;
      }
    }

    return false;
  }

 /*------------------------------------------------------------------------------------*/

  /**
   * Generates all the not blocked children of a Node
   *
   * @param p Node.point
   * returns a Vector of child Points of the point "p"
   */
  private List generateChildren(Point p) {
    List listChildren = new List(8);
    int x = p.x;
    int y = p.y;
    if (isNotBlock(x,y-1))   {listChildren.addElement(new Point(x,y-1));}
    if (isNotBlock(x+1,y))   {listChildren.addElement(new Point(x+1,y));}
    if (isNotBlock(x,y+1))   {listChildren.addElement(new Point(x,y+1));}
    if (isNotBlock(x-1,y))   {listChildren.addElement(new Point(x-1,y));}
    if (isNotBlock(x-1,y-1)) {listChildren.addElement(new Point(x-1,y-1));}
    if (isNotBlock(x-1,y+1)) {listChildren.addElement(new Point(x-1,y+1));}
    if (isNotBlock(x+1,y+1)) {listChildren.addElement(new Point(x+1,y+1));}
    if (isNotBlock(x+1,y-1)) {listChildren.addElement(new Point(x+1,y-1));}
    return listChildren;
  }

 /*------------------------------------------------------------------------------------*/

  /**
   * Finds the optimal path between 2 points.
   *
   * @param pointStart baginning of the path (in CELL units)
   * @param pointGoal end of the path (in CELL units)
   */
  static public List findPath(Point pointStart, Point pointGoal) {
    if (SHOW_DEBUG)
      System.out.println("AStarDouble::findPath");

    if (aStar==null)
      aStar=new AStarDouble();

    //if ( (!isNotBlock(pointStart.x, pointStart.y)) || (!isNotBlock(pointGoal.x, pointGoal.y)) )

    if (SHOW_DEBUG)
      System.out.print(pointGoal);
    if ( (!aStar.isValidGoal(pointGoal)) ) {
      if (SHOW_DEBUG)
        System.err.println("error : invalid point");
      return null;
    }
    if (SHOW_DEBUG)
      System.out.println(" -> " + pointGoal);

    NodeDouble solution = aStar.searchNode(pointStart, pointGoal);
    return aStar.getPath(solution);

  }

 /*------------------------------------------------------------------------------------*/

  /**
   * constructs the path from the start node to the node n
   */
  private List getPath(NodeDouble n) {
    List result;
    if (n == null) {
      result = new List();
    } else {
      result = getPath(n.parent);
      result.addElement(n.point);
    }
    return result;
  }

  /**
   * prints the AStar path
   */
  public String toString(List path) {
    Point pathPoint;
    String result = "";
    for (int i=0; i<path.size(); i++) {
      pathPoint = (Point) path.elementAt(i);
      result += "path["+i+"] = (" + pathPoint.x + "," + pathPoint.y + ")\n";
    }
    return result;
  }

 /*------------------------------------------------------------------------------------*/

  /**
   * initializes the array "map" with a BufferedImage
   */
  public void initMask(BufferedImage maskBuffImg, int myMapWidth, int myMapHeight)
  {
    mapWidth = myMapWidth;
    mapHeight = myMapHeight;
    map = null;
    map = new boolean[myMapWidth][myMapHeight];
    for (int i=0; i<myMapWidth; i++)
    {
      for (int j=0; j<myMapHeight; j++)
      {
        map[i][j] = (maskBuffImg.getRGB(i, j)==-1) ? false: true; // not blocked
      }
    }
  }

  /** To set the mask
   */
  static public void setMask( boolean mask[][] ) {
    if (aStar==null)
      aStar = new AStarDouble();
    map = mask;
    mapWidth = mask.length;
    mapHeight = mask[0].length;
  }

 /*------------------------------------------------------------------------------------*/

  static public List smoothPath(List path) {
    return aStar.smoothPath1(path);
  }

 /** SMOOTH PATH ALGO FROM GAMASUTRA **/

  /** To smooth a path.
   * @param path a previously created path via Astar
   * @return smoothed path...
   */
  public List smoothPath1( List path ) {
    if ( (path==null) || (path.size()<3) )
      return path;

    List smoothedPath = new List( path.size() );

    Point checkPoint = (Point) path.elementAt(0);
    int index = 1;

    smoothedPath.addElement( path.elementAt(0) ); // first point

    while ( index+1<path.size() )
      if ( walkable( checkPoint, (Point) path.elementAt(index+1) ) )
        index++; // no need for this point, we check the next one
      else {
        checkPoint = (Point) path.elementAt(index);
        smoothedPath.addElement( checkPoint  ); // point needed
        index++;
      }

    smoothedPath.addElement( path.elementAt(index) ); // end point
    return smoothedPath;
  }

  /** Returns true if we can walk directly from point A to point B.
   */
  private boolean walkable( Point a, Point b ) {
    float cosinus = 0;
    float sinus = 0;

    if ((a.x == b.x) && (a.y == b.y)) {
      return true;
    }

    // 1 - compute angle between the two points...
    if (b.x == a.x) {
      if (b.y>a.y) {
        cosinus = 0;
        sinus = 1;
        }
      else if (b.y<a.y) {
        cosinus = 0;
        sinus = -1;
        }
      //else angle = 0.0f;
    } else {
      double angle = Math.atan( (double) (b.y-a.y)/(b.x-a.x) );
      if (b.x<a.x) {
        cosinus = (float) -Math.cos(angle);
        sinus   = (float) -Math.sin(angle);
      } else {
        cosinus = (float) Math.cos(angle);
        sinus   = (float) Math.sin(angle);
      }
    }

    // 2 - check points along the path every 0.25 pixels...

    float rfin = (float) Math.sqrt( (b.y-a.y)*(b.y-a.y) + (b.x-a.x)*(b.x-a.x) );

    for ( float r=0.25f; r<rfin; r +=0.25f ) {
      if ( !isNotBlock( (int) (a.x+r*cosinus), (int) (a.y+r*sinus) ) ) {
        return false;
      }
    }

    return true; // success ! found valid path between these two points!
  }

 /*------------------------------------------------------------------------------------*/

  /** To dynamically modify the mask setting all pixels of a rectangle to a boolean value
   *
   * @param r the rectangle to fill (in screen pixels coordinate)
   * @param maskTileSize the CELL size (in pixel units)
   * @param value new value of the pixels
   */
  private synchronized void changeRectangle(Rectangle r, int maskTileSize, boolean value) {
    int cx = (int) (r.x/maskTileSize);
    int cy = (int) (r.y/maskTileSize);

    int cWidth = (int) (r.getWidth()/maskTileSize);
    if( cWidth==0 ) cWidth=1;

    int cHeight = (int) (r.getHeight()/maskTileSize);
    if( cHeight==0 ) cHeight=1;

    if (SHOW_DEBUG)
      System.out.println("cWidth = " + cWidth + " cHeight = " + cHeight);

    for (int i=0; i<cWidth; i++)
      for (int j=0; j<cHeight; j++)
        if(cx+i<map.length && cy+j<map[0].length)
           map[cx+i][cy+j] = value;
  }

  /** To dynamically modify the mask setting all pixels
   * of a rectangle to false
   *
   * @param r the rectangle to fill (in screen pixels coordinate)
   */
  static public void fillRectangle(Rectangle r) {
    aStar.changeRectangle(r, tileSize, false);
  }
  
  /** To dynamically modify the mask setting all pixels
   * of a rectangle to true
   *
   * @param r the rectangle to clean (in screen pixels coordinate)
   */
  static public void cleanRectangle(Rectangle r) {
    aStar.changeRectangle(r, tileSize, true);
  }

 /*------------------------------------------------------------------------------------*/

}