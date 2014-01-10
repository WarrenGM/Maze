import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Panel extends JPanel implements KeyListener {

	private Maze maze;

	private GridVertex current;

	private int cellSize, rows, columns, delay;
	
	private boolean shift, ctrl;
	
	public Panel(){
		this(50, 720/55, 1040/55);
	}
	
	public Panel(int cellSize){
		this(cellSize, 720/(cellSize + 1), 1040/(cellSize + 1));
	}
	
	public Panel(int cellSize, int gridSize){
		this(cellSize, gridSize, gridSize);
	}
	
	public Panel(int cellSize, int rows, int columns){
		this.setSize(1040, 720);
		this.addKeyListener(this);
		
		this.cellSize = cellSize;
		this.rows = rows;
		this.columns = columns;
		
		maze = new Maze(10, 10, cellSize, rows, columns, this);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//g.drawRect(0, 0, this.getWidth(), this.getHeight());

		if(maze != null){
			maze.draw(g);
		}
	}

	@Override
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_SHIFT){
			shift = true;
		} else if(e.getKeyCode() == KeyEvent.VK_CONTROL){
			ctrl = true;
		}
		
		if(shift){
			if(e.getKeyCode() == KeyEvent.VK_0){
				cellSize = 5;
			} else if(e.getKeyCode() == KeyEvent.VK_1){
				cellSize = 10;
			} else if(e.getKeyCode() == KeyEvent.VK_2){
				cellSize = 20;
			} else if(e.getKeyCode() == KeyEvent.VK_3){
				cellSize = 30;
			} else if(e.getKeyCode() == KeyEvent.VK_4){
				cellSize = 40;
			} else if(e.getKeyCode() == KeyEvent.VK_5){
				cellSize = 50;
			} else if(e.getKeyCode() == KeyEvent.VK_6){
				cellSize = 60;
			} else if(e.getKeyCode() == KeyEvent.VK_7){
				cellSize = 70;
			} else if(e.getKeyCode() == KeyEvent.VK_8){
				cellSize = 80;
			} else if(e.getKeyCode() == KeyEvent.VK_9){
				cellSize = 90;
			} else {
				return;
			}

			rows = (int)(getHeight()/(cellSize + 1));
			columns = (int)(getWidth()/(cellSize + 1));
			
			maze = new Maze(10, 10, cellSize, rows, columns, this, delay);
			repaint();
			return;
		} else if(ctrl){
			if(e.getKeyCode() == KeyEvent.VK_0){
				delay = 0;
			} else if(e.getKeyCode() == KeyEvent.VK_1){
				delay = 10;
			} else if(e.getKeyCode() == KeyEvent.VK_2){
				delay = 20;
			} else if(e.getKeyCode() == KeyEvent.VK_3){
				delay = 30;
			} else if(e.getKeyCode() == KeyEvent.VK_4){
				delay = 40;
			} else if(e.getKeyCode() == KeyEvent.VK_5){
				delay = 50;
			} else if(e.getKeyCode() == KeyEvent.VK_6){
				delay = 60;
			} else if(e.getKeyCode() == KeyEvent.VK_7){
				delay = 70;
			} else if(e.getKeyCode() == KeyEvent.VK_8){
				delay = 80;
			} else if(e.getKeyCode() == KeyEvent.VK_9){
				delay = 90;
			} else {
				return;
			}
			maze.setDelay(delay);
			repaint();
		}

		System.out.println("Space?");
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			System.out.println("Yes");
			new Thread(new Runnable(){
				public void run(){
					System.out.println("New Thread");
					
					maze.generate();
					current = maze.getOrigin();
					repaint();
				}
			}).start();
		}

		if(current == null){
			System.out.println("Is null");
			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W){
			if(current.getTop() != null && !current.topWall()){
				current.setColor(Color.ORANGE);
				current = current.getTop();
				current.setColor(Color.RED);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S){
			System.out.println("Down");
			if(current.getBottom() != null && !current.bottomWall()){
				current.setColor(Color.ORANGE);
				current = current.getBottom();
				current.setColor(Color.RED);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A){
			if(current.getLeft() != null && !current.leftWall()){
				current.setColor(Color.ORANGE);
				current = current.getLeft();
				current.setColor(Color.RED);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D){
			if(current.getRight() != null && !current.rightWall()){
				current.setColor(Color.ORANGE);
				current = current.getRight();
				current.setColor(Color.RED);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_ENTER){
			new Thread(new Runnable(){
				public void run(){
					maze.solve();
					repaint();
				}
			}).start();

		}

		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_SHIFT){
			shift = false;
		} else if(e.getKeyCode() == KeyEvent.VK_CONTROL){
			ctrl = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0){}
	
	public static void main(String[] a){
		JFrame frame = new JFrame();
		Panel p = null;
		if(a.length == 1){
			p = new Panel(Integer.parseInt(a[0]));
		} else if(a.length == 2){
			p = new Panel(Integer.parseInt(a[0]), Integer.parseInt(a[1]));
		} else if(a.length == 3){
			p = new Panel(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
		} else {
			p = new Panel();
		}
		frame.addKeyListener(p);

		frame.setSize(p.getSize());
		frame.add(p);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
