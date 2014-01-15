import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

/**
 * A maze class. This is meant to be drawn on a component. When generate() is
 * called, one of four algorithms will be selected at random to make the maze.
 * When solve() is called, a path from the top left corner to the bottom right
 * corner will be drawn or erased. Additionally a delay can be set so that these
 * processes can be animated. A delay of 0 will stop any animations from occurring.
 * 
 * @author Warren Godone-Maresca
 */
public class Maze extends Grid {

	/** Holds whether the solution has been drawn.							 */
	private boolean solved;

	/** Holds the delay of the animation.									 */
	private int delay;

	/** Holds the name of the method used to generate the maze.				 */
	private String method = "";

	private Color green = new Color(0, 255, 0);

	public Maze(int x, int y, int cellWidth, int rows, int columns, Component component){
		this(x, y, cellWidth, rows, columns, component, 50);
	}

	public Maze(int x, int y, int cellWidth, int rows, int columns, 
			Component component, int maxDelay){
		super(x, y, cellWidth, rows, columns, component);
		setDelay(maxDelay);
	}

	public synchronized void generate(){
		System.out.println("Generating");
		makeGrid();

		solved = false;
		double rand = Math.random();
		
		recursiveDivisionAlgo();
		if(rand < 2){
			return;
		}
		
		if(rand < 0.05){ //Alduos-Broder isn't liked very much.
			method = "Alduos-Broder";
			alduosBroderAlgo();
		} else if(rand < 0.25){
			method = "Prim's";
			primsAlgo();
		} else if(rand < 0.50){
			method = "Hunt and Kill";
			huntAndKillAlgo();
		} else if(rand < 0.75){
			method = "DFS / Backtracker";
			backtrackerAlgo();
			//knockDownRandomWalls();
		} else {
			method = "Recursive Division";
			recursiveDivisionAlgo();
		}
	}

	private void backtrackerAlgo(){
		//TODO random vertex method in the Grid Class
		GridVertex[][] grid = makeGrid();
		repaint();

		Set<GridVertex> unVisited = getVertexSet();
		unVisited.remove(origin);


		int randI = (int)(Math.random()*rows), randJ = (int)(Math.random()*columns);
		GridVertex current = grid[randI][randJ];
		Stack<GridVertex> stack = new Stack<GridVertex>();

		current.visit();
		current.setColor(Color.RED);


		while(!unVisited.isEmpty()){
			Set<GridVertex> tempSet = current.getUnVisitedNeighborSet();
			tempSet.retainAll(current.getWalledSet());

			repaint();

			if(tempSet.isEmpty()){
				current.setColor(Color.WHITE);

				if(stack.isEmpty()){
					current = unVisited.iterator().next();
					current.visit();
					unVisited.remove(current);
				} else {
					current = stack.pop();
				}
				current.setColor(green);
			} else {
				stack.push(current);

				GridVertex chosen = tempSet.iterator().next();
				current.knockDownWall(chosen);

				unVisited.remove(chosen);
				current = chosen;
				current.visit();

				current.setColor(green);
			}
		}

		current.setColor(Color.WHITE);

		while(stack.size() > 0){
			stack.pop().setColor(Color.WHITE);
			repaint();
		}

		System.out.println("Generated");
		component.repaint();
	}

	private void primsAlgo(){
		GridVertex[][] grid = makeGrid();
		repaint();

		Set<GridVertex> maze = new HashSet<GridVertex>();

		Set<GridVertex> frontier = new HashSet<GridVertex>();

		int randI = (int)(Math.random()*rows), randJ = (int)(Math.random()*columns);
		GridVertex current = grid[randI][randJ];

		maze.add(current);

		frontier.addAll(current.getNeighborSet());
		current.setColor(Color.WHITE);

		//We color the frontier
		for(GridVertex v : frontier){
			v.setColor(Color.CYAN);
		}
		repaint();
		while(!frontier.isEmpty()){
			//repaint();

			//A random cell in the frontier which is not part of the maze yet.
			current = frontier.iterator().next();//TODO Make this more random.
			current.setColor(Color.RED);
			repaint();

			Set<GridVertex> temp = current.getNeighborSet();
			temp.retainAll(maze);

			//Random cell in maze that is adjacent to current.
			GridVertex random = temp.iterator().next();
			current.knockDownWall(random);

			frontier.remove(current);
			maze.add(current);
			current.setColor(Color.WHITE);

			//Reset temp
			temp = current.getNeighborSet();
			temp.removeAll(maze);
			for(GridVertex v : temp){
				v.setColor(Color.CYAN);
			}

			frontier.addAll(temp);
		}
	}

	private void alduosBroderAlgo(){
		makeGrid();
		repaint();

		int numOfVertices = rows * columns;
		origin.visit();
		int visited = 1;
		int visits = origin.getVisits();

		GridVertex current = origin;
		current.setColor(green);

		while(visited < numOfVertices){
			repaint();

			GridVertex next = current.randomNeighbor();


			if(next.getVisits() == visits - 1){
				next.visit();
				visited++;
				current.knockDownWall(next);
			}
			current.setColor(Color.WHITE);
			next.setColor(green);
			current = next;
		}
		current.setColor(Color.WHITE);
		repaint();

	}

	private void huntAndKillAlgo(){
		repaint();

		Set<GridVertex> unVisited = getVertexSet();
		unVisited.remove(origin);

		GridVertex current = origin;//TODO

		current.visit();
		current.setColor(Color.RED);
		int visits = current.getVisits();

		while(unVisited.size() > 0){
			Set<GridVertex> tempSet = current.getUnVisitedNeighborSet();
			tempSet.retainAll(current.getWalledSet());

			repaint();

			if(tempSet.isEmpty()){		
				current.setColor(Color.WHITE);

				//We hunt for the new current
				boolean found = false;
				while(!found){
					int i = (int)(Math.random()*rows);
					int j = (int)(Math.random()*columns);

					found = theGrid[i][j].getVisits() < visits;
					Set<GridVertex> temp = theGrid[i][j].getNeighborSet();
					temp.removeAll(unVisited);
					found &= temp.size() > 0;

					if(found){
						current = theGrid[i][j];
						current.knockDownWall(temp.iterator().next());
						unVisited.remove(current);
						current.visit();
					}
				}
				current.setColor(green);
			} else {
				GridVertex chosen = tempSet.iterator().next();
				current.knockDownWall(chosen);

				current.setColor(Color.WHITE);
				unVisited.remove(chosen);
				current = chosen;
				current.visit();

				current.setColor(green);
			}
		}

		current.setColor(Color.WHITE);

		System.out.println("Generated");
		component.repaint();
	}
	
	private static double randomNum(){
		return (Math.random() + Math.random())/2;
	}

	private void recursiveDivisionAlgo(){
		knockDownAllWalls(origin);
		recursiveDivisionAlgo(0, rows - 1, 0, columns - 1, 0.5 + randomNum()/2);
		//uniteCells();
		System.out.println("Done!");
	}

	private void recursiveDivisionAlgo(int top, int bottom, int left, int right,
			double texture){
		if(bottom - top < 1 || right - left < 1){
			return;
		}
		
		
		//The following Math.random() conditions slightly vary the texture of
		//the maze.
		
		int height = bottom - top, width = right - left;
		boolean verticalLine = width > height;
		
		//Math.random() < (texture)/Math.log(1 + width * height)
		if(Math.random() < Math.pow(texture, Math.cbrt(width * height))){
			verticalLine = !verticalLine;
		}
		
		if(height < 3){
			verticalLine = Math.random() > texture;
		} else if(width < 3){
			verticalLine = Math.random() < texture;
		}
		
		if(!verticalLine){
			//Note: bottom > top because the origin is in the top left.
			int randRow = top + (int)(Math.random()*(bottom - top));
			int skipColumn = left + (int)(Math.random()*(right - left));

			for(int i = top; i <= bottom; i++){
				for(int j = left; j <= right; j++){
					theGrid[i][j].setColor(green);
				}
			}
			repaint();

			for(int i = left; i <= right; i ++){
				if(i != skipColumn){
					theGrid[randRow][i].raiseWall(theGrid[randRow + 1][i]);
					repaint();
				}
			}

			repaint();

			for(int i = top; i <= bottom; i++){
				for(int j = left; j <= right; j++){
					theGrid[i][j].setColor(Color.WHITE);
				}
			}
			//repaint();

			recursiveDivisionAlgo(top, randRow, left, right, texture);
			recursiveDivisionAlgo(randRow + 1, bottom, left, right, texture);

		} else if(right - left > 0){

			int randColumn = left + (int)(Math.random()*(right - left));
			int skipRow = top + (int)(Math.random()*(bottom - top));

			for(int i = top; i <= bottom; i++){
				for(int j = left; j <= right; j++){
					theGrid[i][j].setColor(green);
				}
			}
			repaint();


			for(int i = top; i <= bottom; i ++){
				if(i != skipRow){
					theGrid[i][randColumn].raiseWall(theGrid[i][randColumn + 1]);
					repaint();
				}
			}

			repaint();

			for(int i = top; i <= bottom; i++){
				for(int j = left; j <= right; j++){
					theGrid[i][j].setColor(Color.WHITE);
				}
			}
			//repaint();

			recursiveDivisionAlgo(top, bottom, left, randColumn, texture);
			recursiveDivisionAlgo(top, bottom, randColumn + 1, right, texture);

		}
	}

	private void knockDownAllWalls(GridVertex vertex){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				for(GridVertex v : theGrid[i][j].getWalledSet()){
					theGrid[i][j].knockDownWall(v);
					theGrid[i][j].setColor(Color.WHITE);
				}
			}
		}
	}

	private void knockDownRandomWalls(){
		int ln = (int)(Math.random()*(Math.sqrt(rows * columns)));
		for(int i = 0; i < ln; i++){
			int randI = (int)(Math.random()*rows), randJ = (int)(Math.random()*columns);

			try {
				Thread.sleep(delay * delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(theGrid[randI][randJ].getWalledSet().size() > 1){
				if(theGrid[randI][randJ].getColor() == Color.RED){
					continue;
				}
				
				theGrid[randI][randJ].setColor(Color.RED);
				repaint();
				
				GridVertex target = theGrid[randI][randJ].getWalledSet().iterator().next();
				if(target.getWalledSet().size() > 1){
					theGrid[randI][randJ].knockDownWall(target);
					target.setColor(Color.RED);
				} else {
					theGrid[randI][randJ].setColor(Color.WHITE);
				}
			}
			repaint();
		}
	}

	public void solve(){
		if(end == null){ //
			return;
		}

		solved = !solved;

		GridVertex current = origin;
		Deque<GridVertex> deque = new LinkedList<GridVertex>();
		current.setColor(green);
		current.visit();

		while(current != end){
			if(solved){
				repaint();
			}
			Set<GridVertex> tempSet = current.getUnVisitedNeighborSet();
			tempSet.removeAll(current.getWalledSet());

			if(tempSet.isEmpty()){
				current.setColor(Color.WHITE);

				try {
					current = deque.pop();
				} catch(NoSuchElementException e){
					for(int i = 0; i < 40; i++){
						System.out.print("*");
					}
					System.out.println("\nNo solution exists.");
					for(int i = 0; i < 40; i++){
						System.out.print("*");
					}
					return;
				}
			} else {
				deque.push(current);

				GridVertex chosen = tempSet.iterator().next();
				current = chosen;
				current.visit();
			}
			if(solved){
				current.setColor(green);
			}
		}

		while(!deque.isEmpty()){
			deque.removeLast().setColor(solved ? Color.ORANGE : Color.WHITE);
			repaint();
		}
		end.setColor(solved ? Color.ORANGE : Color.WHITE);
		component.repaint();
		System.out.println("Solved!");
	}

	@Override
	public void draw(Graphics pane){
		//Next we draw the method name.
		Graphics2D g = (Graphics2D)pane;
		g.setColor(Color.RED);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setFont(new Font("default", Font.BOLD, 12));
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(method, g);

		g.setColor(Color.BLACK);
		g.fillRoundRect(10, component.getHeight() - fm.getAscent() - 5, 
				(int)rect.getWidth() + 10, (int)rect.getHeight(), 2, 2);

		g.setColor(Color.WHITE);
		g.drawString(method, 15, component.getHeight() - 5);
		
		super.draw(pane);
	}

	public void repaint(){
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		component.repaint();
	}

	public GridVertex getOrigin(){
		return origin;
	}

	public void setDelay(int delay){
		this.delay = Math.max(delay, delay - (int)Math.log(rows * columns));//TODO
		System.out.println("Delay: " + this.delay);
	}
}