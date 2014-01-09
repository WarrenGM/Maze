import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;


public class Grid {

	protected GridVertex origin;

	protected int x, y, width;

	protected int rows, columns;
	
	protected Component component;
	
	//GridVertex[][] theGrid TODO;

	public Grid(int x, int y, int cellWidth, int rows, int columns, Component component){
		this.x = x;
		this.y = y;
		width = cellWidth;
		this.rows = rows;
		this.columns = columns;
		this.component = component;
		makeGrid();
	}

	protected void makeGrid(){
		GridVertex[] prevRow = new GridVertex[columns];
		GridVertex[] newRow = new GridVertex[columns];

		for(int i = 0; i < rows; i++){
			newRow = new GridVertex[columns];
			for(int j = 0; j < columns; j++){
				//newRow is the i-th row.
				newRow[j] = new GridVertex(x + j*width, y + i*width, width, new Color(240, 240, 240));

				if(j > 0){
					newRow[j].setLeft(newRow[j-1]);
					newRow[j - 1].setRight(newRow[j]);
				}

				if(i > 0){
					newRow[j].setTop(prevRow[j]);
					prevRow[j].setBottom(newRow[j]);
				} else {
					origin = newRow[0];
				}
			}
			prevRow = newRow;		
		}
	}

	public Set<GridVertex> getVertexSet(){
		return getVertexSet(origin, new HashSet<GridVertex>());
	}
	
	private Set<GridVertex> getVertexSet(GridVertex vertex, Set<GridVertex> theSet){
		theSet.add(vertex);
		
		for(GridVertex v : vertex.getNeighborSet()){
			if(theSet.add(v)){
				getVertexSet(v, theSet);
			}
		}
		
		return theSet;
	}
	
	public void draw(Graphics pane){
		for(GridVertex column = origin; column != null; column = column.getRight()){
			for(GridVertex row = column; row != null; row = row.getBottom()){
				row.draw(pane);
			}
		}
	}
}
