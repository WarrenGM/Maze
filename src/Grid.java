import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

public class Grid {

	protected GridVertex origin, end;

	protected int x, y, width;

	protected int rows, columns;
	
	protected Component component;
	
	protected GridVertex[][] theGrid;

	public Grid(int x, int y, int cellWidth, int rows, int columns, Component component){
		this.x = x;
		this.y = y;
		width = cellWidth;
		this.rows = rows;
		this.columns = columns;
		this.component = component;
		makeGrid();
	}

	/**
	 * Makes a grid of {@link GridVertex}s.
	 * 
	 * @return A 2d array of {@link GridVertex}s containg each vertex.
	 */
	protected GridVertex[][] makeGrid(){
		//GridVertex[] prevRow = new GridVertex[columns];
		//GridVertex[] newRow = new GridVertex[columns];
		
		GridVertex[][] matrix = new GridVertex[rows][columns];

		for(int i = 0; i < rows; i++){
			//newRow = new GridVertex[columns];
			for(int j = 0; j < columns; j++){
				//newRow is the i-th row.
				matrix[i][j] = new GridVertex(x + j*width, y + i*width, width, Color.LIGHT_GRAY);

				if(j > 0){
					matrix[i][j].setLeft(matrix[i][j-1]);
					matrix[i][j - 1].setRight(matrix[i][j]);
				}

				if(i > 0){
					matrix[i][j].setTop(matrix[i - 1][j]);
					matrix[i - 1][j].setBottom(matrix[i][j]);
				}
			}
		}
		origin = matrix[0][0];
		origin.setColor(Color.PINK);
		end = matrix[rows - 1][columns - 1];
		end.setColor(Color.BLUE);
		
		return (theGrid = matrix);
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
	
	public GridVertex[][] getTheGrid() {
		return theGrid;
	}

	public void draw(Graphics pane){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				theGrid[i][j].drawCell(pane);
			}
		}
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				theGrid[i][j].drawOutline(pane);
			}
		}
	}
}
