package com.lachlanhurst.client.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.lachlanhurst.client.data.BikeStack;

/**
 * creates and manages a quad tree full of bike stacks based on their
 * geometric location.  This class is designed to hide the implementation
 * of the actual quadtree.
 * @author lachlan
 *
 */
public class QuadTreeManager 
{
	public static final int MAX_DEPTH_HARD = 10;
	public static final int MIN_DEPTH_HARD = 3;
	
	protected List _leafNodes = null;
	protected QuadTreeNode _head = null;
	
	protected int _maxDepth = 5;
	
	protected int _maxEntriesInAnyNode = 0;
	
	/**
	 * constructs a quadtree with the given original bounds
	 * @param originalBounds
	 */
	public QuadTreeManager(LatLngBounds originalBounds)
	{
		_leafNodes = new ArrayList();
		
		_head = new QuadTreeNode(originalBounds,0,null);
		
	}
	
	public QuadTreeManager(LatLngBounds originalBounds, int maxDepth)
	{
		_leafNodes = new ArrayList();
		_maxDepth = maxDepth;
		_head = new QuadTreeNode(originalBounds,0,null);
		
	}
	
	/**
	 * gets the currently set maximum depth
	 * @return
	 */
	public int getMaxDepth()
	{
		return _maxDepth;
	}
	
	/**
	 * adds a stack to this quad tree
	 * @param stack
	 */
	public void addBikeStack(BikeStack stack)
	{
		_head.addStack(stack, stack.getPosition());
	}
	
	/**
	 * increased the max depth of this quad tree by one if possible.  This
	 * causes the quad tree to grow an extra level, leaf nodes list will also
	 * be updated.  This should be quicker than reconstructing the entire quad
	 * tree.
	 */
	public void increaseDepth()
	{
		if (_maxDepth == MAX_DEPTH_HARD)
		{
			return;
		}
		_maxDepth++;
		_maxEntriesInAnyNode = 0;
		
		Iterator oldLeaves = _leafNodes.iterator();
		_leafNodes = new ArrayList();
		while (oldLeaves.hasNext())
		{
			QuadTreeNode aLeaf = (QuadTreeNode)oldLeaves.next();
			Iterator stacks = aLeaf.getStacks().iterator();
			aLeaf.setIsLeaf(false);
			aLeaf.clearStacksList();
			while (stacks.hasNext())
			{
				BikeStack aStack = (BikeStack)stacks.next();
				aLeaf.addStack(aStack, aStack.getPosition());
			}
		}
		
	}
	
	/**
	 * decrease the depth of the quad tree by one, this pulls all the nodes
	 * stacks into its parent node.  The leaf nodes list is also rebuilt.
	 */
	public void decreaseDepth()
	{
		if (_maxDepth == MIN_DEPTH_HARD)
		{
			return;
		}
		_maxDepth--;
		_maxEntriesInAnyNode = 0;
		
		Iterator oldLeaves = _leafNodes.iterator();
		_leafNodes = new ArrayList();
		while (oldLeaves.hasNext())
		{
			QuadTreeNode aLeaf = (QuadTreeNode)oldLeaves.next();
			QuadTreeNode aParent = aLeaf.getParent();
			if (!_leafNodes.contains(aParent))
			{
				aParent.setIsLeaf(true);
				_leafNodes.add(aParent);
			}
			Iterator leafStacks = aLeaf.getStacks().iterator();
			while (leafStacks.hasNext())
			{
				BikeStack aStack = (BikeStack)leafStacks.next();
				aParent.addStack(aStack, aStack.getPosition());
			}
			
		}
		
		
	}
	
	/**
	 * draws the representation of this quad tree onto the given map using
	 * overlays.
	 * @param map
	 */
	public void drawOnMap(MapWidget map)
	{
		Iterator leaves = _leafNodes.iterator();
		while (leaves.hasNext())
		{
			QuadTreeNode leafNode = (QuadTreeNode)leaves.next(); 
			LatLng[] pts = getBoundsAsPoints(leafNode.getBounds());
			//GWT.log(leafNode.getBounds().toString(), null);
			
			String colour = ColourMap.getColour(leafNode.getStacks().size(), _maxEntriesInAnyNode);
			
			Polygon poly = new Polygon(pts,"#ff0000",0,0.7,colour,0.4);

			//Polyline poly = new Polyline(pts,"#ff0000",3,0.7);
			//PolyStyleOptions pso = PolyStyleOptions.newInstance("#ff0000",3, 0.7);
			//poly.setStrokeStyle(pso);
			map.addOverlay(poly);
		}
	}
	
	/**
	 * extracts the corner coordinates of the bounds.  Corners are returned
	 * in the following order;  NE, SE, SW, NW.
	 * @param bounds
	 * @return
	 */
	public LatLng[] getBoundsAsPoints(LatLngBounds bounds)
	{
		LatLng ne = bounds.getNorthEast();
		LatLng sw = bounds.getSouthWest();
		LatLng se = LatLng.newInstance(ne.getLatitude(), sw.getLongitude());
		LatLng nw = LatLng.newInstance(sw.getLatitude(), ne.getLongitude());
		
		LatLng[] res = {ne,se,sw,nw};
		return res;
	}
	
	/**
	 * divides the given bounds into quadrants.  The returned array
	 * comes back in the following order NE, SE, SW, NW.
	 * @param bounds an array of four bounds
	 * @return
	 */
	public LatLngBounds[] getBoundsQuadded(LatLngBounds bounds)
	{
		LatLng ne = bounds.getNorthEast();
		LatLng sw = bounds.getSouthWest();
		LatLng center = bounds.getCenter();
		
		LatLng n = LatLng.newInstance(center.getLatitude(), ne.getLongitude());
		LatLng e = LatLng.newInstance(ne.getLatitude(), center.getLongitude());
		LatLng s = LatLng.newInstance(center.getLatitude(), sw.getLongitude());
		LatLng w = LatLng.newInstance(sw.getLatitude(), center.getLongitude());
		
		LatLngBounds boundsNe = LatLngBounds.newInstance(center, ne);
		LatLngBounds boundsSe = LatLngBounds.newInstance(s, e);
		LatLngBounds boundsSw = LatLngBounds.newInstance(sw, center);
		LatLngBounds boundsNw = LatLngBounds.newInstance(w, n);
		
		LatLngBounds[] res = {boundsNe,boundsSe,boundsSw,boundsNw};
		return res;
	}
	
	private class QuadTreeNode
	{
		private QuadTreeNode _parent;
		private LatLngBounds _myBounds;
		private int _myDepth;
		private boolean _isLeaf = false;
		private List _stacks = null;
		
		private LatLngBounds _ne;
		private LatLngBounds _se;
		private LatLngBounds _sw;
		private LatLngBounds _nw;
		
		private QuadTreeNode _qTne = null;
		private QuadTreeNode _qTse = null;
		private QuadTreeNode _qTsw = null;
		private QuadTreeNode _qTnw = null;
		
		private QuadTreeNode(LatLngBounds bounds, int depth, QuadTreeNode parent)
		{
			_myBounds = bounds;
			_myDepth = depth;
			_parent = parent;
			
			if (_myDepth >= _maxDepth)
			{
				_stacks = new ArrayList();
				_isLeaf = true;
				_leafNodes.add(this);
			}
			else
			{
				//now set the quadrants
				LatLngBounds[] tmp = getBoundsQuadded(bounds);
				_ne = tmp[0];
				_se = tmp[1];
				_sw = tmp[2];
				_nw = tmp[3];
			}
		}
		
		private QuadTreeNode getParent()
		{
			return _parent;
		}
		
		private LatLngBounds getBounds()
		{
			return _myBounds;
		}
		
		private List getStacks()
		{
			if (!_isLeaf)
				throw new RuntimeException("no stacks on a non leaf node");
			return _stacks;
		}
		
		private void setIsLeaf(boolean isLeaf)
		{
			_isLeaf = isLeaf;
			if (!_isLeaf)
			{
				LatLngBounds[] tmp = getBoundsQuadded(_myBounds);
				_ne = tmp[0];
				_se = tmp[1];
				_sw = tmp[2];
				_nw = tmp[3];
			}
			else
			{
				_ne = null;
				_se = null;
				_sw = null;
				_nw = null;
				_qTne = null;
				_qTse = null;
				_qTsw = null;
				_qTnw = null;
				_stacks = new ArrayList();
			}
		}
		
		private void clearStacksList()
		{
			_stacks = null;
		}
		
		private void addStack(BikeStack stack, LatLng position)
		{
			if (_isLeaf)
			{
				_stacks.add(stack);
				if (_stacks.size() > _maxEntriesInAnyNode)
				{
					_maxEntriesInAnyNode = _stacks.size();
				}
			}
			else
			{
				if (_ne.containsLatLng(position))
				{
					if (_qTne == null)
						_qTne = new QuadTreeNode(_ne,_myDepth+1,this);
					_qTne.addStack(stack, position);
				}
				else if (_se.containsLatLng(position))
				{
					if (_qTse == null)
						_qTse = new QuadTreeNode(_se,_myDepth+1,this);
					_qTse.addStack(stack, position);
				}
				else if (_sw.containsLatLng(position))
				{
					if (_qTsw == null)
						_qTsw = new QuadTreeNode(_sw,_myDepth+1,this);
					_qTsw.addStack(stack, position);
				}
				else if (_nw.containsLatLng(position))
				{
					if (_qTnw == null)
						_qTnw = new QuadTreeNode(_nw,_myDepth+1,this);
					_qTnw.addStack(stack, position);
				}
				else
				{
					//throw new RuntimeException("Quad tree error, point out of bounds");
				}
			}
		}
		
	}
	
	
}
