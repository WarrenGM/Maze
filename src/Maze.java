import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.Stack;

import javax.swing.Timer;


public class Maze extends Grid {

	public Maze(int x, int y, int cellWidth, int rows, int columns, Component component){
		super(x, y, cellWidth, rows, columns, component);
		//makeMaze();
	}

	public void makeMaze(){
		makeGrid();

		generate();


	}

	private synchronized void generate(){
		Set<GridVertex> unVisited = getVertexSet();
		unVisited.remove(origin);

		GridVertex current = origin;
		Stack<GridVertex> stack = new Stack<GridVertex>();

		current.visit();
		current.setColor(Color.RED);

		while(!unVisited.isEmpty()){
			Set<GridVertex> tempSet = current.getUnVisitedNeighborSet();
			tempSet.retainAll(current.getWalledSet());

			if(tempSet.isEmpty()){
				if(stack.isEmpty()){
					current = unVisited.iterator().next();
					current.visit();
					unVisited.remove(current);
				} else {
					current = stack.pop();
				}
			} else {
				stack.push(current);

				GridVertex chosen = tempSet.iterator().next();
				current.knockDownWall(chosen);

				unVisited.remove(chosen);
				current = chosen;
				current.visit();
			}

		}
	}

	@Override
	public void draw(Graphics pane) {
		draw(pane, origin, origin.getVisits());
	}

	private void draw(Graphics pane, GridVertex vertex, int visits){
		vertex.draw(pane);
		vertex.visit();

		for(GridVertex v : vertex.getNeighborSet()){
			if(v.getVisits() == visits){
				draw(pane, v, visits);
			}
		}
	}


	public GridVertex getOrigin(){
		return origin;
	}
}
