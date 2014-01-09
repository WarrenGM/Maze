import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Panel extends JPanel implements KeyListener {

	private Maze theGrid;

	private GridVertex current;

	public Panel(){
		this.setSize(1040, 720);
		this.addKeyListener(this);
		theGrid = new Maze(10, 10, 10, 720/11, 1100/11, this);

		/*new Thread(new Runnable(){
			public void run(){
				while(true){
					repaint();
				}
			}
		}).start();*/
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRect(0, 0, this.getWidth(), this.getHeight());

		System.out.println("Hello");

		theGrid.draw(g);
	}

	public static void main(String[] a){
		JFrame frame = new JFrame();
		Panel p = new Panel();
		frame.addKeyListener(p);

		frame.setSize(p.getSize());
		frame.add(p);
		frame.setVisible(true);

	}

	@Override
	public void keyPressed(KeyEvent e){
		System.out.println("Key pressed");

		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			theGrid.makeMaze();
			current = theGrid.getOrigin();
			repaint();
		}

		if(current == null){
			System.out.println("Is null");
			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_UP){
			if(current.getTop() != null && !current.topWall()){
				current.setColor(Color.ORANGE);
				current = current.getTop();
				current.setColor(Color.RED);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			System.out.println("Down");
			if(current.getBottom() != null && !current.bottomWall()){
				current.setColor(Color.ORANGE);
				current = current.getBottom();
				current.setColor(Color.RED);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			if(current.getLeft() != null && !current.leftWall()){
				current.setColor(Color.ORANGE);
				current = current.getLeft();
				current.setColor(Color.RED);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			if(current.getRight() != null && !current.rightWall()){
				current.setColor(Color.ORANGE);
				current = current.getRight();
				current.setColor(Color.RED);
			}
		}

		repaint();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		System.out.println("Checking");
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
