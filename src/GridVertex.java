import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A node in a grid-graph.
 * 
 * @author Warren
 */
public class GridVertex {
	/** The neighbors. There exists an edge from this vertex to any of the
	 * 	following vertices that are not null.								 */
	private GridVertex left, right, top, bottom;

	private int x, y, width;

	private Color color;

	private int visits = 0;

	/** The number of times in which this vertex was drawn.					*/
	private int drawVisits;

	/**
	 * Holds whether or not the wall between the top (or other) node should be
	 * drawn.
	 */
	private boolean topWall = true, bottomWall = true, 
			leftWall = true, rightWall = true;

	public GridVertex(int x, int y, int width, Color color){
		this.x = x;
		this.y = y;
		this.width = width;
		this.color = color;
	}

	public void visit(){
		visits++;
	}

	public void drawCell(Graphics pane){
		pane.setColor(color);
		pane.fillRect(x, y, width, width);
	}

	public void drawOutline(Graphics pane){
		drawVisits++;

		pane.setColor(Color.BLACK);
		Graphics2D g = (Graphics2D)pane;
		g.setStroke(new BasicStroke(width/4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

		if(top == null || topWall){
			g.drawLine(x, y, x + width, y);
		}
		if(right == null || rightWall){
			g.drawLine(x + width, y, x + width, y + width);
		}
		if(bottom == null || bottomWall){
			g.drawLine(x, y + width, x + width, y + width);
		}
		if(left == null || leftWall){
			g.drawLine(x, y, x, y + width);
		}
	}

	/**
	 * Removes the wall between this vertex and the given neighbor. The boolean
	 * variable that determines whether or not the wall be drawn will be set to
	 * false in this vertex and in neighbor.
	 */
	public void knockDownWall(GridVertex neighbor){
		if(neighbor == null){
			return;
		}

		if(neighbor == bottom){
			bottomWall = false;
			bottom.setTopWall(false);
		} else if(neighbor == top){
			topWall = false;
			top.setBottomWall(false);
		} else if(neighbor == left){
			leftWall = false;
			left.setRightWall(false);
		} else if(neighbor == right){
			rightWall = false;
			right.setLeftWall(false);
		}
	}

	public void raiseWall(GridVertex neighbor){
		if(neighbor == null){
			return;
		}

		if(neighbor == bottom){
			bottomWall = true;
			bottom.setTopWall(true);
		} else if(neighbor == top){
			topWall = true;
			top.setBottomWall(true);
		} else if(neighbor == left){
			leftWall = true;
			left.setRightWall(true);
		} else if(neighbor == right){
			rightWall = true;
			right.setLeftWall(true);
		}
	}

	/**
	 * A random, non-null neighbor.
	 * @return null if this vertex has no neighbors.
	 */
	public GridVertex randomNeighbor(){
		List<GridVertex> temp = new ArrayList<GridVertex>(4);

		if(top != null)
			temp.add(top);
		if(right != null)
			temp.add(right);
		if(bottom != null)
			temp.add(bottom);
		if(left != null)
			temp.add(left);

		if(temp.size() == 0){
			return null;
		}
		return temp.get((int)(Math.random() * temp.size()));
	}

	/**
	 * Returns a set containing all nonempty neighbors. If this vertex has no
	 * neighbors, the set will be empty.
	 * @return
	 */
	public Set<GridVertex> getNeighborSet(){
		Set<GridVertex> temp = new HashSet<GridVertex>(4);

		if(top != null)
			temp.add(top);
		if(right != null)
			temp.add(right);
		if(bottom != null)
			temp.add(bottom);
		if(left != null)
			temp.add(left);

		return temp;
	}

	public Set<GridVertex> getWalledSet(){
		Set<GridVertex> temp = new HashSet<GridVertex>(4);

		if(top != null && topWall)
			temp.add(top);
		if(right != null && rightWall)
			temp.add(right);
		if(bottom != null && bottomWall)
			temp.add(bottom);
		if(left != null && leftWall)
			temp.add(left);

		return temp;
	}

	public Set<GridVertex> getUnWalledSet(){
		Set<GridVertex> temp = new HashSet<GridVertex>(4);

		if(top != null && !topWall)
			temp.add(top);
		if(right != null && !rightWall)
			temp.add(right);
		if(bottom != null && !bottomWall)
			temp.add(bottom);
		if(left != null && !leftWall)
			temp.add(left);

		return temp;
	}

	/**
	 * Returns a set containing all non-null neighbors that have been visited
	 * fewer times than this vertex.
	 */
	public Set<GridVertex> getUnVisitedNeighborSet(){
		Set<GridVertex> temp = new HashSet<GridVertex>(4);

		if(top != null && top.getVisits() < visits)
			temp.add(top);
		if(right != null && right.getVisits() < visits)
			temp.add(right);
		if(bottom != null && bottom.getVisits() < visits)
			temp.add(bottom);
		if(left != null && left.getVisits() < visits)
			temp.add(left);

		return temp;
	}

	public Set<GridVertex> getConnectedSet(){
		return getConnectedSet(this, new HashSet<GridVertex>());
	}

	private Set<GridVertex> getConnectedSet(GridVertex vertex, Set<GridVertex> theSet){
		theSet.add(vertex);

		for(GridVertex v : vertex.getUnWalledSet()){
			if(theSet.add(v)){
				getConnectedSet(v, theSet);
			}
		}

		return theSet;
	}

	public GridVertex getLeft(){
		return left;
	}

	public void setLeft(GridVertex left){
		this.left = left;
	}

	public void removeLeft(){
		if(left != null){
			left.setRight(null);
		}
		left = null;
	}

	public GridVertex getRight(){
		return right;
	}

	public void setRight(GridVertex right){
		this.right = right;
	}

	public void removeRight(){
		if(right != null){
			right.setLeft(null);
		}
		right = null;
	}

	public GridVertex getTop(){
		return top;
	}

	public void setTop(GridVertex top){
		this.top = top;
	}

	public void removeTop(){
		if(top != null){
			top.setBottom(null);
		}
		top = null;
	}

	public GridVertex getBottom(){
		return bottom;
	}

	public void setBottom(GridVertex bottom){
		this.bottom = bottom;
	}

	public void removeBottom(){
		if(bottom != null){
			bottom.setTop(null);
		}
		bottom = null;
	}

	public int getX(){
		return x;
	}

	public void setX(int x){
		this.x = x;
	}

	public int getY(){
		return y;
	}

	public void setY(int y){
		this.y = y;
	}

	public int getWidth(){
		return width;
	}

	public void setWidth(int width){
		this.width = width;
	}

	public Color getColor(){
		return color;
	}

	public void setColor(Color color){
		this.color = color;
	}

	public int getVisits(){
		return visits;
	}

	public boolean topWall() {
		return topWall;
	}

	public void setTopWall(boolean topWall) {
		this.topWall = topWall;
	}

	public boolean bottomWall() {
		return bottomWall;
	}

	public void setBottomWall(boolean bottomWall) {
		this.bottomWall = bottomWall;
	}

	public boolean leftWall() {
		return leftWall;
	}

	public void setLeftWall(boolean leftWall) {
		this.leftWall = leftWall;
	}

	public boolean rightWall() {
		return rightWall;
	}

	public void setRightWall(boolean rightWall) {
		this.rightWall = rightWall;
	}

	public int getDrawVisits() {
		return drawVisits;
	}

	public void resetVisits(){
		visits = 0;
	}
}
