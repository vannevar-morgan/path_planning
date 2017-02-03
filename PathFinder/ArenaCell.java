import javax.swing.*;
import java.awt.*;

class ArenaCell extends JPanel{
    private Color cellColor;
    private Pair<Integer, Integer> cellCoords;
    //    private int cellCol;
    //    private int cellRow;
    private double cellDistance;
    private ArenaCell cellPrev;
    
    public ArenaCell(){
	this.cellInit(-1, -1, Color.white, -1.0, null);
    }
    
    public ArenaCell(final int x, final int y){
	this.cellInit(x, y, Color.white, -1.0, null);
    }
    
    public ArenaCell(final int x, final int y, final Color c){
	this.cellInit(x, y, c, -1.0, null);
    }
    
    public ArenaCell(final int x, final int y, final Color c, final double distance){
	this.cellInit(x, y, c, distance, null);
    }
    
    public ArenaCell(final int x, final int y, final Color c, final double distance, final ArenaCell prev){
	this.cellInit(x, y, c, distance, prev);
    }
    
    private void cellInit(final int x, final int y, final Color c, final double distance, final ArenaCell prev){
	// note that cellCoords is stored as (row, col), NOT (x, y)
	this.cellColor = c;
	this.cellCoords = new Pair<Integer, Integer>(y, x);
	this.cellDistance = distance;
	this.cellPrev = prev;
    }
    
    public Color getCellColor(){
	return this.cellColor;
    }
    
    public Pair<Integer, Integer> getCellCoords(){
	return this.cellCoords;
    }
        
    public double getCellDistance(){
	return this.cellDistance;
    }
    
    public ArenaCell getCellPrev(){
	return this.cellPrev;
    }
    
    public void paintComponent(Graphics g){
	g.setColor(this.cellColor);
	g.fillRect(0,0,this.getWidth(),this.getHeight());
    }
    
    public void reset(){
	this.reset(Color.white);
    }
    
    public void reset(Color newColor){
	this.cellInit(this.cellCoords.second(), this.cellCoords.first(), newColor, -1.0, null);
	this.repaint();
    }
    
    public void setCellColor(final Color c){
	this.cellColor = c;
    }
    
    public void setCellDistance(final double d){
	this.cellDistance = d;
    }
    
    public void setCellPrev(final ArenaCell prev){
	this.cellPrev = prev;
    }
}
