import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Formatter;
import java.util.Scanner;

import javax.swing.*;

import java.io.File;
import java.io.FileNotFoundException;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener
{
	static final int SCREEN_WIDTH=500;
	static final int SCREEN_HEIGHT=500;
	static final int UNIT_SIZE=20; //GameBoard is composed of small units like pixels but for the game
	static final int GAME_UNITS=(SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	//Number of objects there are in the GameBoard- Total Area/Area of each object
	static final int DELAY=105; //Delay before the next event takes place in milliseconds
	//x coordinates of snake
	final int x[]=new int[GAME_UNITS]; //Array= GAME_UNITS as snake cannot be bigger than the game-board itself
	final int y[]=new int[GAME_UNITS];
	//y coordinates of parts of snake
	int bodyParts=5; //We will begin with six bodyParts of the snake
	int applesEaten=0;
	int appleX; //X coordinate of where apple appears
	int appleY; //Y coordinate of where apple appears
	char direction='R';
	//Direction of snake L-Left, R-Right, U-Up, D-Down
	String High_score;
	boolean running=false;
	Timer timer; //Takes care of all the flow of events in the game
	//Fires one or more ActionEvents at specified intervals. An example use is an animation object that uses a Timer as the trigger for drawing its frames.
	//Delay is the number of milliseconds waited before firing the next event
	Random random;
	File file=new File("S01101110ake.txt"); //Snake.txt but n is in binary lol
	//Over here, I am using relative file path not absolute
	Formatter format;
	Scanner reader;
	String current_high;
	
	public GamePanel()
	{
		if(!file.exists())
		{
			try {
				format=new Formatter(file);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			format.format("%s", String.valueOf(0));
			format.close();
		}
		try {
			reader=new Scanner(file);
		} 
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		current_high=reader.next();
		reader.close();
		
		random=new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		
		startGame();	
	}
	
	public void startGame()
	{
		newApple();
		running=true;
		timer=new Timer(DELAY,this);
		timer.start(); //Starts all the functions in the program
	}
	
	public void newApple()
	{
		//Getting the coordinates of the apple
		appleX=random.nextInt((int)SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE; 
		//500/25=20, Range of apple is 1 to 20*UNIT_SIZE which will give the coordinate X
		appleY=random.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
	}
	
	@Override
	public void paintComponent(Graphics g)
	//Override basically tells that during run-time this part of the code is active and can override other methods to perform its function.
	//Override means this part of code is always active even when not called
	//IMP*
	{
		super.paintComponent(g);
		//super refers to the parent class: can invoke functions, refer to immediate parent class variable from sub class
		draw(g);
	}
	
	public void draw(Graphics g)
	{
		if(running)
		{
			/* This GRIDS that I drew were just for understanding
			 
			for(int i=0; i<(SCREEN_WIDTH/UNIT_SIZE); i++) //for number of horizontal boxes
			{
				g.drawLine(i*UNIT_SIZE,0,i*UNIT_SIZE,SCREEN_HEIGHT);
				//starting x,y and ending x,y
			}
			
			for(int i=0; i<(SCREEN_HEIGHT/UNIT_SIZE); i++)
			{
				g.drawLine(0,i*UNIT_SIZE,SCREEN_WIDTH,i*UNIT_SIZE);
			}
			*/
			
			//Drawing apple
			g.setColor(Color.RED);
			g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);
			//x coordinate, Y coordinate, width and height
			
			//Drawing the snake
			for(int i=0; i<bodyParts; i++)
			{
				if(i==0) //Head of the snake
				{
					g.setColor(Color.GREEN);
					g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
				}
				else //Body of the snake
				{
					g.setColor(new Color(45,180,0));
					g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
				}
			}
			
			//This shows the score
			g.setColor(Color.BLUE); //For apples
			g.setFont(new Font("MV Boli", Font.BOLD, 35));
			FontMetrics metrics = getFontMetrics(g.getFont()); 
			g.drawString("Score: "+applesEaten,(SCREEN_WIDTH- metrics.stringWidth("Score: "+applesEaten)), g.getFont().getSize());
			
			g.setColor(Color.BLUE); //For apples
			g.setFont(new Font("MV Boli", Font.BOLD, 35));
			g.drawString("High: "+current_high,15,g.getFont().getSize());
		}
		else
		{
			gameOver(g);
		}
	}
	
	public void move()
	{
		for(int i=bodyParts; i>0; i--)
		{
			//Shifting all the coordinates in this array by 1
			//The previous part like the part before the head, will come at the head's index. And in the next part of the code, the head moves one step forward
			//x[1]=x[0] coordinate of bodyPart after head comes to the head's coordinate
			x[i]=x[i-1];
			y[i]=y[i-1];
		}
		switch(direction)
		{
			//This just moves the head all the other parts follow with the previous section
			case 'U':
				y[0]=y[0]-UNIT_SIZE;
				//y coordinate decreases towards the top
				break;
			case 'D':
				y[0]=y[0]+UNIT_SIZE;
				break;
			case 'L':
				x[0]=x[0]-UNIT_SIZE;
				break;
			case 'R':
				x[0]= x[0]+UNIT_SIZE;
				break;	
		}
	}
	
	public void checkApple()
	{
		if((x[0]==appleX)&&(y[0]==appleY))
		{
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	public void checkCollisions()
	{
		for(int i=bodyParts; i>0; i--)
		{
			//This is to check if the head of the snake collides with the body
			//Basically checking if any part is at the same coordinates as the head
			if((x[0]==x[i])&&(y[0]==y[i]))
				running=false;
			
			//Checks if head collides with borders
			
			//Checks to see if head collides with left border
			if(x[0]<0) //If the coordinate of head is less than that of left border
				running=false;
			//Checks for right border
			if(x[0]>SCREEN_WIDTH)
				running=false;
			//Checks for top
			if(y[0]<0)
				running=false;
			//Checks for bottom
			if(y[0]>SCREEN_HEIGHT)
				running=false;
		}
		
		if(!running)
			timer.stop(); //Stops all the functions in the program
	}
	
	public void gameOver(Graphics g)
	{
		try {
			reader=new Scanner(file);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		High_score=reader.next();
		int high=Integer.parseInt(High_score);
		
		if(applesEaten>high)
		{
			g.setColor(Color.GREEN); //For apples
			g.setFont(new Font("MV Boli", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont()); 
			g.drawString("New High Score!",(SCREEN_WIDTH- metrics.stringWidth("New High Score"))/2, (g.getFont().getSize())*3);
			
			try {
				format=new Formatter(file);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			format.format("%s",String.valueOf(applesEaten));
			format.close();
		}
		
		//Score
		g.setColor(Color.BLUE); //For apples
		g.setFont(new Font("MV Boli", Font.BOLD, 40));
		FontMetrics metrics = getFontMetrics(g.getFont()); 
		g.drawString("Score: "+applesEaten,(SCREEN_WIDTH- metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
		
		//GameOver Message
		g.setColor(Color.RED);
		g.setFont(new Font("MV Boli", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont()); 
		g.drawString("Game Over",(SCREEN_WIDTH- metrics2.stringWidth("Game Over!"))/2, SCREEN_HEIGHT/2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(running)
		{
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
		// java.awt.Component.repaint()- Repaints this component.
		//When call to repaint method is made, it performs a request to erase and perform redraw of the component after a small delay in time.
	}
	
	//There are two ways to register keyboard input- KeyListener and KeyAdapter
	//KeyAdapter is a class and KeyListener is a interface
	//This is why KeyAdapter is extended and KeyListener is implemented
	
	public class MyKeyAdapter extends KeyAdapter //Subclass of GamePanel
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			switch(e.getKeyCode())
			{
				case KeyEvent.VK_LEFT:
					if(direction!='R') //This is so that the player does not run into themselves
						direction='L';
					break;
				case KeyEvent.VK_RIGHT:
					if(direction!='L')
						direction='R';
					break;
				case KeyEvent.VK_UP:
					if(direction!='D')
						direction='U';
					break;
				case KeyEvent.VK_DOWN:
					if(direction!='U')
						direction='D';
					break;
				
				//W,A,S,D
				case 65:
					if(direction!='R') //This is so that the player does not run into themselves
						direction='L';
					break;
				case 68:
					if(direction!='L')
						direction='R';
					break;
				case 87:
					if(direction!='D')
						direction='U';
					break;
				case 83:
					if(direction!='U')
						direction='D';
					break;
			}
		}
	}

}
