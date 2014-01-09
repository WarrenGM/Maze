import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridVertex {
	private GridVertex left, right, top, bottom;

	private int x, y, width;

	private Color color;

	private int visits = 0;
	
	private boolean topWall = true, bottomWall = true, 
			leftWall = true, rightWall = true;

	public GridVertex(int x, int y, int width, Color color){
		this.x = x;
		this.y = y;
		this.width = width;
		this.color = color;
	}

	public boolean isCorner(){
		return (left == null || right == null) && (top == null || bottom == null);
	}

	public boolean isBoundary(){
		return left == null || right == null || top == null || bottom == null;
	}

	public void visit(){
		visits++;
	}

	public void draw(Graphics pane){
		pane.setColor(color);
		pane.fillRect(x, y, width, width);

		pane.setColor(Color.BLACK);//TODO change to black
		Graphics2D g = (Graphics2D)pane;
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		if(top == null || topWall){
			//pane.fillRect(x - width/4, y - width/4, width + width/2, width/2);
			g.drawLine(x, y, x + width, y);
		}
		if(right == null || rightWall){
			//pane.fillRect(x + (width*3)/4, y - width/4, width/2, width + width/2);
			g.drawLine(x + width, y, x + width, y + width);
		}
		if(bottom == null || bottomWall){
			//pane.fillRect(x - width/4, y + (width*3)/4, width + width/2, width/2);
			g.drawLine(x, y + width, x + width, y + width);
		}
		if(left == null || leftWall){
			//pane.fillRect(x - width/4, y - width/4, width/2, width + width/2);
			g.drawLine(x, y, x, y + width);
		}
	}
	
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
}
