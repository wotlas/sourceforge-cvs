/*
 * Light And Shadow. A Persistent Universe based on Robert Jordan's Wheel of Time Books.
 * Copyright (C) 2001-2002 WOTLAS Team
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

import java.lang.*;
import java.util.*;
import java.awt.Point;
import java.awt.image.BufferedImage;

import wotlas.utils.List;

/** A* algorithm finds the optimal path between 2 points 
 *
 * usage:
 * - create a AStar object
 * - initialize the mask with a buffered image
 *   AStar.initMask(BufferedImage maskBuffImg, int imgWidth, int imgHeight)
 * - start the search
 *   AStarObject.findPath(startPoint, goalPoint);
 *
 * @author Petrus
 * @see wotlas.libs.pathfinding.Node
 */

public class AStar
{
 /*------------------------------------------------------------------------------------*/

  /** mask of the image : mask[i][j] is true if pixel(i,j) is not blocked
   */
  private boolean[][] map = new boolean[100][100];

  /** width of the map
   */
  private int mapWidth;

  /** height of the map
   */
  private int mapHeight;

  /** start of the path
   */  
  private Point pointStart;

  /** goal of the path
   */
  private Point pointGoal;
  
  /** list of not visited {@link Node Nodes}
   */
  private Hashtable open = new Hashtable(500);

  /** list of visited {@link Node Nodes}
   */
  private Hashtable closed = new Hashtable(500);

  /** sorted open {@link Node Node}
   */
  //private Vector nodes = new Vector();
  private List nodes = new List();
   
 /*------------------------------------------------------------------------------------*/
  
  /**
   * Estimates the distance between 2 points
   * 
   * @param poinFrom first point
   * @param pointTo second point
   * @return the distance between the 2 points
   */
  private int estimate(Point pointFrom, Point pointTo)
  {
    return (int) pointFrom.distanceSq(pointTo);
    /** 
     * Other distances:
     * return Math.max(Math.abs(pointFrom.x-pointTo.x),Math.abs(pointFrom.y-pointTo.y));
     * return (pointFrom.x-pointTo.x)*(pointFrom.x-pointTo.x)+(pointFrom.y-pointTo.y)*(pointFrom.y-pointTo.y);
     */    
  }
  
  /**
   * begins optimal path search
   */
  private Node searchNode()
  {
    Node bestNode;			           // best node of Vector "nodes" (the lowest f)
    //Vector childPoints;			        // children Points of "bestNode"
    List childPoints;
    int childCost;			           // cost of the children of "bestNode"
    //Vector children = new Vector();	  // children Nodes of "bestNode"    
    List children = new List();
    
    //while (!nodes.isEmpty())    
    while (!nodes.isEmpty())
    {    
      //bestNode = (Node) nodes.firstElement();
      bestNode = (Node) nodes.elementAt(0);
      
      //System.out.println("bestNode : ("+bestNode.point.x+","+bestNode.point.y+")");
      
      /* to avoid having to remove ?? */
      if (closed.get(bestNode.point) != null) // if bestNode was in closed
      {
        //nodes.removeElementAt(0);		// remove "bestNode" from "nodes"
        nodes.removeFirstElement();
        continue;
      }
      
      //System.out.print("Test : has the goal been reached ? ");
      if ((bestNode.point.x == pointGoal.x) && (bestNode.point.y == pointGoal.y))
      {
        //System.out.println("yes");
        return bestNode;
      }
      else 
      {
        //System.out.println("no");
      }
      
      children.removeAllElements();
      
      childPoints = generateChildren(bestNode.point);
      
      for (int i=0; i<childPoints.size(); i++)
      {
        Point childPoint;  	      // child Point of "bestNode"
        Node closedNode = null;	   // not null if childPoint was in closed
        Node openNode = null;	      // not null if childPoint was in open
        Node oldNode = null;	      // not null if childPoint was in open or closed
        
        childPoint = (Point) childPoints.elementAt(i);
        //System.out.println("  -> exploration of child ("+childPoint.x+","+childPoint.y+")");
        
        childCost = bestNode.g + 1; // ?? In fact, we have to calculate the childCost
        
        // test if childPoint was in closed or open
        if ( (closedNode = (Node) closed.get(childPoint)) == null ) {
          openNode = (Node) open.get(childPoint);
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
              int estimation = oldNode.h;
              oldNode = new Node();        
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
          int estimation;
          Node newNode = new Node();
          
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
      //nodes.removeElementAt(0);
      nodes.removeFirstElement();
      addToNodes(children);
    }
    System.out.println("no path found");
    return null; // no solution
  }

  /**
   *
   */
  private int rbsearch(int l, int h, int tot, int costs)
  {
    if (l>h)
      return l; //insert before l
    int cur = (l+h)/2;
    int ot = ((Node) nodes.elementAt(cur)).f;
    if ((tot < ot) || (tot == ot && costs >= ((Node) nodes.elementAt(cur)).g))
      return rbsearch(l, cur-1, tot, costs);
    return rbsearch(cur+1, h, tot, costs);
  }
  
  /**
   *
   */
  private int bsearch(int l, int h, int tot, int costs)
  {
    int lo = l;
    int hi = h;
    while (lo<=hi)
    {
      int cur = (lo+hi)/2;
      int ot = ((Node) nodes.elementAt(cur)).f;
      if ((tot < ot) || (tot == ot && costs >= ((Node) nodes.elementAt(cur)).g))
	      hi = cur - 1;
      else
	      lo = cur + 1;
    }
    return lo; //insert before lo
  }
  
  /**
   *
   */
  //private void addToNodes(Vector children)
  private void addToNodes(List children)
  {
    Node newNode;
    int idx;
    for (int i=0; i<children.size(); i++)
    {      
      newNode = (Node) children.elementAt(i);
      idx = bsearch(0, nodes.size()-1, newNode.f, newNode.g);
      nodes.insertElementAt(newNode, idx);    
    }
  }
  
  /**
   * test if a point is valid for the path
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @return true if point is valid (not blocked) in the {@link #mask mask}
   */
  public boolean isNotBlock(int x, int y)
  {
    if ( (x<0) || (x==mapWidth) || (y<0) || (y==mapHeight))
      return false;
    return map[x][y];
  }
  
  /**
   * Generates all the not blocked children of a Node
   *
   * @param p Node.point
   * returns a Vector of child Points of the point "p"
   */
  //private Vector generateChildren(Point p)
  private List generateChildren(Point p)
  {
    //Vector listChildren = new Vector();        
    List listChildren = new List(8);        
    int x=p.x;
    int y=p.y;    
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
  
  /**
   * finds the optimal path between 2 points
   *
   * @param pStart baginning of the path
   * @param pGoal end of the path
   */
  //public Vector findPath(Point pStart, Point pGoal)
  public List findPath(Point pStart, Point pGoal)
  {
    Node firstNode = new Node();
    Node solution = new Node();
    
    int estimation;
    int cost;    

    pointStart = pStart;
    pointGoal = pGoal;
    
    if ( (!isNotBlock(pStart.x, pStart.y)) || (!isNotBlock(pGoal.x, pGoal.y)) ) {
      System.err.println("error : invalid point");
      return null;
    }
    
    //System.out.println("Creation of first node");
    firstNode.point = pointStart;
    cost = 0;
    estimation = estimate(pointStart, pointGoal);   
    firstNode.g = cost;
    firstNode.h = estimation;
    firstNode.f = cost + estimation;
    firstNode.parent = null;
    
    open.put(pointStart, firstNode);
    nodes.addElement(firstNode);    
    
    //System.out.println("Beginning of the search");
    solution = searchNode();
    
    //System.out.println("End");        
    nodes.removeAllElements();
    open.clear();
    closed.clear();
        
    return getPath(solution);
  }
    
  /**
   * constructs the path from the start node to the node n
   */
  //private Vector getPath(Node n)
  private List getPath(Node n)
  {
    //Vector result;
    List result;
    if (n == null)
    {
      //result = new Vector();
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
  //public void showPath(Vector path)
  public void showPath(List path)
  {
    Point pathPoint;
    for (int i=0; i<path.size(); i++)
    {
      pathPoint = (Point) path.elementAt(i);
      System.out.print("(" + pathPoint.x + "," + pathPoint.y + ") ");
    }
  }

  /**
   * initializes the array "map" with a BufferedImage
   */
  public void initMask(BufferedImage maskBuffImg, int imgWidth, int imgHeight)
  {
    mapWidth = imgWidth;
    mapHeight = imgHeight;
    for (int i=0; i<imgWidth; i++)
    {
      for (int j=0; j<imgHeight; j++)
      {
        map[i][j] = (maskBuffImg.getRGB(i, j)==-1) ? false: true; // not blocked        
      }
    }
  }
  
}