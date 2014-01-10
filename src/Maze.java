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

		solved = false;
		double rand = Math.random();
		if(rand < 0.25){
			method = "Alduos Broder";
			alduosBroderAlgo();
		} else if(rand < 0.5){
			method = "Prim's";
			primsAlgo();
		} else if (rand < .75){
			method = "Hunt and Kill";
			huntAndKillAlgo();
		} else {
			method = "Recursive Backtracker";
			backtrackerAlgo();
		}
	}

	private void backtrackerAlgo(){
		//TODO random vertex method in the Grid Class
		GridVertex[][] grid = makeGrid();
		repaint();

		Set<GridVertex> unVisited = getVertexSet();
		unVisited.remove(origin);


		int randI = (int)(Math.random()*rows), randJ = (int)(Math.random()*columns);
		GridVertex current = grid[randI][randJ];;
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
				current.setColor(Color.CYAN);
			} else {
				stack.push(current);

				GridVertex chosen = tempSet.iterator().next();
				current.knockDownWall(chosen);

				unVisited.remove(chosen);
				current = chosen;
				current.visit();

				current.setColor(Color.CYAN);
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
		makeGrid();
		repaint();

		Set<GridVertex> maze = new HashSet<GridVertex>();
		maze.add(origin);

		Set<GridVertex> frontier = new HashSet<GridVertex>();
		frontier.addAll(origin.getNeighborSet());
		origin.setColor(Color.WHITE);

		//We color the frontier
		for(GridVertex v : frontier){
			v.setColor(Color.RED);
		}
		repaint();
		while(!frontier.isEmpty()){
			repaint();

			GridVertex current = frontier.iterator().next();//TODO Make more random.

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
		current.setColor(Color.CYAN);

		while(visited < numOfVertices){
			repaint();

			GridVertex next = current.randomNeighbor();


			if(next.getVisits() == visits - 1){
				next.visit();
				visited++;
				current.knockDownWall(next);
			}
			current.setColor(Color.WHITE);
			next.setColor(Color.CYAN);
			current = next;
		}
		current.setColor(Color.WHITE);
		repaint();

	}

	private void huntAndKillAlgo(){
		GridVertex[][] grid = makeGrid();
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

					found = grid[i][j].getVisits() < visits;
					Set<GridVertex> temp = grid[i][j].getNeighborSet();
					temp.removeAll(unVisited);
					found &= temp.size() > 0;

					if(found){
						current = grid[i][j];
						current.knockDownWall(temp.iterator().next());
						unVisited.remove(current);
						current.visit();
					}
				}
				System.out.println(found);
				current.setColor(Color.CYAN);
			} else {
				GridVertex chosen = tempSet.iterator().next();
				current.knockDownWall(chosen);

				current.setColor(Color.WHITE);
				unVisited.remove(chosen);
				current = chosen;
				current.visit();

				current.setColor(Color.CYAN);
			}
		}

		current.setColor(Color.WHITE);

		System.out.println("Generated");
		component.repaint();
	}

	public void solve(){
		if(end == null){ //
			return;
		}

		solved = !solved;

		GridVertex current = origin;
		Deque<GridVertex> deque = new LinkedList<GridVertex>();
		current.setColor(Color.GREEN);
		current.visit();

		while(current != end){
			if(solved){
				repaint();
			}
			Set<GridVertex> tempSet = current.getUnVisitedNeighborSet();
			tempSet.removeAll(current.getWalledSet());

			if(tempSet.isEmpty()){
				current.setColor(Color.WHITE);
				current = deque.pop();
			} else {
				deque.push(current);

				GridVertex chosen = tempSet.iterator().next();
				current = chosen;
				current.visit();
			}
			if(solved){
				current.setColor(Color.GREEN);
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
		draw(pane, origin, origin.getDrawVisits());

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
	}

	private void draw(Graphics pane, GridVertex vertex, int visits){
		vertex.draw(pane);
		//vertex.visit();

		for(GridVertex v : vertex.getNeighborSet()){
			if(v.getDrawVisits() == visits){
				draw(pane, v, visits);
			}
		}
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
		this.delay = Math.max(0, delay - (int)Math.log(rows * columns));
		System.out.println("Delay: " + this.delay);
	}
}