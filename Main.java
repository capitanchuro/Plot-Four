package application;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	private int width = 500, height = 500, n = 0, turn = 0;
	
	private class Position {
		public Rectangle sqr = null;
		public int row = 0, col = 0;
		public Unit unit = null;
		Position(Rectangle sqr, Unit unit, int row, int col){
			this.sqr = sqr;
			this.unit = unit;
			this.row = row;
			this.col = col;
		}
	}
	
	private class Unit {
		public Circle disc = null;
		public Position position = null;
		Unit(Circle disc, Position position){
			this.disc = disc;
			this.position = position;
		}
		
		public boolean win(Position[][] grid, Position pos, int v, int u, int n) {
			int r = pos.row, c = pos.col, count = 0;

			//Checks half a vertical, diagonal, or horizontal line based on the arguments assigned to r and c
			while ((r >= 0 && c >= 0) && (r < grid.length && c < grid[r].length)) {

				if (grid[r][c].unit == this)
					count++;
				else
					break;

				r += v;
				c += u;
			}
			
			//Re-assigns the values of y and x to r and c to check the other half of the line
			r = pos.row; 
			c = pos.col; 

			while ((r >= 0 && c >= 0) && (r < grid.length && c < grid[r].length)) {

				if (grid[r][c].unit == this && (r != pos.row || c != pos.col))
					count++;
				else if (r != pos.row || c != pos.col)
					break;

				r += v * -1;
				c += u * -1;

			}

			//Recursively checks the area centering a unit before returning the value of the unit
			//that won or 0.
			if (count == 4)
				return true;
			else if (n < 4) {
				if (v + u == -1)
					return win(grid, pos, v, 1, ++n);
				else if (v + u == 0)
					return win(grid, pos, 0, u, ++n);
				else
					return win(grid, pos, 1, u, ++n);
			}
			else
				return false;
		}
		
		public boolean move(Position[][] grid, Position pos) {
			if (grid[pos.row][pos.col].unit == null) {

				//Drops down the rows before plotting a piece (if the column is open)
				while (pos.row < grid.length && grid[pos.row][pos.col].unit == null)
					pos.row++;
				
				//Plots the unit in the matrix
				grid[--pos.row][pos.col].unit = this; 

				//Checks to see if there is a victor or if there are no more moves left
				if (win(grid, pos, -1, 0, 0) || ++n >= 42) {
					return false;
				}
				
				//Changes whose turn it is
				turn = (turn < 1) ? 1 : 0; 

				//Returns that there are moves to make
				return true;
			}
			else if (n < 42)
				return true; //Column is empty but there are still moves to make 
			else
				return false; // No more moves to make
		}
	}
	
	private Unit[][] units = new Unit[1][2];
	private Position [][] grid = new Position[6][7];
	private Group plot_four = new Group();
	
	private void unitSetup() {
		units[0][0] = new Unit(new Circle(100,100,(width/16)/2, Color.RED),null);
		units[0][1] = new Unit(new Circle(100,100,(width/16)/2, Color.YELLOW),null);
		plot_four.getChildren().add(units[0][0].disc);
		plot_four.getChildren().add(units[0][1].disc);
	}
	
	private void gridSetup() {
		
		double x = (width/2) - ((width/16)*7)/2, y = height/3;
		
		//Creates a 2 dimensional array with y rows and x columns
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 7; c++) {
				grid[r][c] = new Position(new Rectangle(x, y, width/16, height/16), null, r, c);
				grid[r][c].sqr.setFill(Color.TRANSPARENT);
				grid[r][c].sqr.setStroke(Color.WHITE);
				plot_four.getChildren().add(grid[r][c].sqr);
				x += width/16;
			}
			x = (width/2) - ((width/16)*7)/2;
			y += width/16;
		}
	}
	
	private EventHandler<MouseEvent> Handler() {
	      EventHandler<MouseEvent> handler = 
	    		  new EventHandler<MouseEvent>() { 
	    	         
	    	         @Override 
	    	         public void handle(MouseEvent mouse) { 
	    	        	 int c = 0;
	    	        	 while((((width/2) - (grid[0][0].sqr.getWidth() * 7)/2) < mouse.getX() && 
	    	        			 mouse.getX() < ((width/2) + (grid[0][0].sqr.getWidth() * 7)/2)) && 
	    	        			 !(mouse.getX() < grid[0][c].sqr.getX() + (width/16)) &&
	    	        			 (grid[0][0].sqr.getY() > mouse.getY())&&(mouse.getY() > (grid[0][0].sqr.getY() - (width/12)))) 
	    	        	 {
	    	        		 c++;
	    	        	 }
	    	        	 System.out.println("MOUSE: (" +mouse.getX()+ ","+mouse.getY()+") C = "+c);
	    	         } 
	    	      };
		return handler;
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		unitSetup();
		gridSetup();
		
		try {
			Scene scene = new Scene(plot_four,width,height,Color.BLACK);
			scene.addEventFilter(MouseEvent.MOUSE_MOVED,Handler());
			primaryStage.getIcons().add(new Image("/plot_four.png"));
			primaryStage.setTitle("Connect Four");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
