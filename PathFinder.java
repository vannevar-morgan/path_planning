import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.*; //Collection;

public class PathFinder{
    
    private static final String WINDOW_TITLE = "PathFinder";
    private static final int MAIN_WINDOW_WIDTH = 1000;
    private static final int MAIN_WINDOW_HEIGHT = 700;
    private final int ARENA_ROWS;
    private final int ARENA_COLS;
    private static final int ARENA_HGAP = 1;
    private static final int ARENA_VGAP = 1;
    private static final String[] ALGO_ENTRIES = {"Select Algorithm...", "A*", "Dijkstra"};
    private static final Color ORG_COLOR = Color.green;
    private static final Color WALL_COLOR = Color.blue;
    private static final Color DST_COLOR = Color.red;
    private static final Color BLANK_COLOR = Color.white;
    private static final Color EVAL_COLOR = Color.lightGray;
    private static final Color PATH_COLOR = Color.orange;
    private static final Color PATH_LOCUS_COLOR = Color.yellow; // color of the cell currently being evaluated

    private JFrame mainWindow;
    JComboBox<String> algSelect;
    private ArrayList<ArenaCell> aCells = new ArrayList<ArenaCell>(); // all cells in the arena
    private boolean draggingArenaCells = false;
    private Color orgColor = ORG_COLOR;
    private Color wallColor = WALL_COLOR;
    private Color dstColor = DST_COLOR;
    private Color blankColor = BLANK_COLOR;
    private Color evalColor = EVAL_COLOR;
    private Color pathColor = PATH_COLOR;
    private Color pathLocusColor = PATH_LOCUS_COLOR;
    
    // booleans indicating button state
    private boolean settingOrg = false;
    private boolean settingDst = false;
    private boolean settingWalls = false;    
    private boolean erasingCells = false;
    
    private ArenaCell orgCell = null;
    private ArenaCell dstCell = null;
    private ArrayList<ArenaCell> wCells = new ArrayList<ArenaCell>(); // wall cells
    
    public PathFinder(){
	this(50, 50);
    }
    
    public PathFinder(int rows, int cols){
	this.ARENA_ROWS = rows;
	this.ARENA_COLS = cols;
    }
    
    public static void main(String[] args){
	int ar_rows;
	int ar_cols;
	if(args.length == 2){
	    ar_rows = Integer.parseInt(args[0]);
	    ar_cols = Integer.parseInt(args[1]);
	}else{
	    ar_rows = 50;
	    ar_cols = 50;
	}
	PathFinder pf = new PathFinder(ar_rows, ar_cols);
	pf.initGui();
    }
    
    private void initGui(){
	// Configure the main window
	mainWindow = new JFrame(WINDOW_TITLE);
	mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	mainWindow.setSize(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
	
	// Configure the command panel
	JPanel cmdPanel = new JPanel();
	cmdPanel.setBackground(Color.lightGray);
	//	cmdPanel.setLayout(new BoxLayout(cmdPanel, BoxLayout.Y_AXIS));
	
	algSelect = new JComboBox<String>(ALGO_ENTRIES);
	algSelect.setSelectedIndex(0);
	//	algSelect.setMaximumSize(algSelect.getPreferredSize());
	
	JButton orgButton = new JButton("Set Origin");
	JButton dstButton = new JButton("Set Goal");
	JButton wallButton = new JButton("Set Walls");
	JButton goButton = new JButton("Go...");
	JButton eraseButton = new JButton("Erase Cell(s)");
	JButton resetButton = new JButton("Reset");

	orgButton.addActionListener(new OrgButtonListener());
	dstButton.addActionListener(new DstButtonListener());
	wallButton.addActionListener(new WallButtonListener());
	goButton.addActionListener(new GoButtonListener());
	eraseButton.addActionListener(new EraseButtonListener());
	resetButton.addActionListener(new ResetButtonListener());
	
	cmdPanel.add(algSelect);
	cmdPanel.add(orgButton);
	cmdPanel.add(dstButton);
	cmdPanel.add(wallButton);
	cmdPanel.add(goButton);
	cmdPanel.add(eraseButton);
	cmdPanel.add(resetButton);
		
	// Configure the arena panel
	JPanel arenaPanel = new JPanel();
	arenaPanel.setBackground(Color.black);
	arenaPanel.setLayout(new GridLayout(this.ARENA_ROWS, this.ARENA_COLS, ARENA_HGAP, ARENA_VGAP));
	for(int i = 0; i < this.ARENA_ROWS; ++i){
	    for(int j = 0; j < this.ARENA_COLS; ++j){
		//int r = (int) (Math.random() * 255);
		//int g = (int) (Math.random() * 255);
		//int b = (int) (Math.random() * 255);
		//ArenaCell cellPanel = new ArenaCell(j, i, new Color(r,g,b));
		ArenaCell cellPanel = new ArenaCell(j, i, blankColor);
		CellPanelListener cpl = new CellPanelListener();
		cellPanel.addMouseListener(cpl);
		cellPanel.addMouseMotionListener(cpl);
		aCells.add(cellPanel);
		arenaPanel.add(cellPanel);
	    }
	}
	
	
	mainWindow.getContentPane().add(BorderLayout.NORTH, cmdPanel);
	mainWindow.getContentPane().add(BorderLayout.CENTER, arenaPanel);
	mainWindow.setVisible(true);
    }
    
    class CellPanelListener extends MouseAdapter{
	public void mouseClicked(MouseEvent e){
	    ArenaCell cell = (ArenaCell) e.getSource();
	    if(settingOrg){
		if(orgCell != null){
		    orgCell.reset(blankColor);
		}
		orgCell = cell;
		if(cell == dstCell){
		    dstCell = null;
		    System.out.println("nulled the dst cell because overlap...");
		}else if(wCells.contains(cell)){
		    wCells.remove(cell);
		}
		orgCell.reset(orgColor);
	    }else if(settingDst){
		if(dstCell != null){
		    dstCell.reset(blankColor);
		}
		dstCell = cell;
		if(cell == orgCell){
		    orgCell = null;
		    System.out.println("nulled the org cell because overlap...");
		}else if(wCells.contains(cell)){
		    wCells.remove(cell);
		}
		dstCell.reset(dstColor);
	    }else if(settingWalls){
		if(!wCells.contains(cell)){
		    if(cell == orgCell){
			orgCell = null;
		    }else if(cell == dstCell){
			dstCell = null;
		    }
		    cell.reset(wallColor);
		    wCells.add(cell);
		}
	    }else if(erasingCells){
		if(wCells.contains(cell)){
		    cell.reset(blankColor);
		    wCells.remove(cell);
		    //		    aCells.get(aCells.indexOf(cell)).reset(blankColor);
		    //		    System.out.println("cell previous: " + cell.getCellPrev());
		}
	    }
	}
	
	public void mouseEntered(MouseEvent e){
	    if(draggingArenaCells){
		ArenaCell cell = (ArenaCell) e.getSource();
		if(settingWalls){
		    if(!wCells.contains(cell)){
			if(cell == orgCell){
			    orgCell = null;
			}else if(cell == dstCell){
			    dstCell = null;
			}
			cell.reset(wallColor);
			wCells.add(cell);
		    }
		}else if(erasingCells){
		    if(wCells.contains(cell)){
			cell.reset(blankColor);
			wCells.remove(cell);
			//			aCells.get(aCells.indexOf(cell)).reset(blankColor);
		    }
		}
	    }
	}
	
	public void mousePressed(MouseEvent e){
	    ArenaCell cell = (ArenaCell) e.getSource();
	    if(settingWalls){
		if(!wCells.contains(cell)){
		    if(cell == orgCell){
			orgCell = null;
		    }else if(cell == dstCell){
			dstCell = null;
		    }
		    cell.reset(wallColor);
		    wCells.add(cell);
		    draggingArenaCells = true;
		}
	    }else if(erasingCells){
		if(wCells.contains(cell)){
		    cell.reset(blankColor);
		    wCells.remove(cell);
		    //		    aCells.get(aCells.indexOf(cell)).reset(blankColor);
		    System.out.println("cell previous: " + cell.getCellPrev());
		    draggingArenaCells = true;
		}
	    }
	}
	
	public void mouseReleased(MouseEvent e){
	    draggingArenaCells = false;
	}
	
	//	public void updateCell(ArenaCell ac, Color c){
	//	    ac.setCellColor(c);
	//	    ac.repaint();
	//	}
    }

    
    class OrgButtonListener implements ActionListener{
	public void actionPerformed(ActionEvent e){
	    settingOrg = true;
	    settingDst = false;
	    settingWalls = false;
	    erasingCells = false;
	}
    }
    
    class DstButtonListener implements ActionListener{
	public void actionPerformed(ActionEvent e){
	    settingOrg = false;
	    settingDst = true;
	    settingWalls = false;
	    erasingCells = false;
	}
    }
    
    class WallButtonListener implements ActionListener{
	public void actionPerformed(ActionEvent e){
	    settingOrg = false;
	    settingDst = false;
	    settingWalls = true;
	    erasingCells = false;
	}
    }
    
    class GoButtonListener implements ActionListener{
	public void actionPerformed(ActionEvent e){
	    // reset flags
	    settingOrg = false;
	    settingDst = false;
	    settingWalls = false;
	    erasingCells = false;
	    // verify an org cell, dst cell, and algorithm are selected
	    if(orgCell == null){
		System.out.println("origin cell is null... set the origin cell.");
		return;
	    }
	    if(dstCell == null){
		System.out.println("goal cell is null... set the goal cell.");
		return;
	    }
	    if(algSelect.getSelectedItem() == "Select Algorithm..."){
		System.out.println("you need to select a pathfinding algorithm...");
		return;
	    }
	    // reset the dstCell to itself to clear any previous cell pointed to by the last run
	    dstCell.reset(dstColor);
	    
	    // reset any previous coloured cells (which should be empty) to the blank color before illustrating
	    ArrayList<ArenaCell> emptyCells = makeEmptyCellsSet(aCells, wCells, orgCell, dstCell);
	    for(ArenaCell cell : emptyCells){
		cell.reset(blankColor);
		System.out.println("empty cell: " + cell.getCellCoords().first() + ", " + cell.getCellCoords().second());
	    }
	    // perform selected pathfinding algorithm
	    if(algSelect.getSelectedItem() == "Dijkstra"){
		System.out.println("finding path using dijkstra...");
		findPathDijkstra();
	    }else{
		System.out.println("not yet implemented :(...");
	    }
	}
    }
    
    class EraseButtonListener implements ActionListener{
	public void actionPerformed(ActionEvent e){
	    settingOrg = false;
	    settingDst = false;
	    settingWalls = false;
	    erasingCells = true;
	}
    }
    
    class ResetButtonListener implements ActionListener{
	public void actionPerformed(ActionEvent e){
	    draggingArenaCells = false;
	    orgColor = ORG_COLOR;
	    wallColor = WALL_COLOR;
	    dstColor = DST_COLOR;
	    blankColor = BLANK_COLOR;
	    evalColor = EVAL_COLOR;
	    pathColor = PATH_COLOR;
	    pathLocusColor = PATH_LOCUS_COLOR;
	    for(ArenaCell cell : aCells){
		cell.reset(blankColor);
	    }
	    /*
	    // debugging code, verify values after reset
	    for(int i = 0; i < aCells.size(); ++i){
		System.out.println("updated, color: " + aCells.get(i).getCellColor());
	    }
	    for(int i = 0; i < aCells.size(); ++i){
		System.out.println("updated, coords: " + aCells.get(i).getCellCoords().first() + ", " + aCells.get(i).getCellCoords().second());
	    }
	    for(int i = 0; i < aCells.size(); ++i){
		System.out.println("updated, distance: " + aCells.get(i).getCellDistance());
	    }
	    for(int i = 0; i < aCells.size(); ++i){
		System.out.println("updated, prev: " + aCells.get(i).getCellPrev());
	    }
	    */
	    orgCell = null;
	    dstCell = null;
	    wCells.clear();
	    settingOrg = false;
	    settingDst = false;
	    settingWalls = false;
	    erasingCells = false;
	}
    }
    
    private void findPathDijkstra(){
	// debugging code, verify values are reset
	for(int i = 0; i < aCells.size(); ++i){
	    System.out.println("dijkstra, color: " + aCells.get(i).getCellColor());
	}
	for(int i = 0; i < aCells.size(); ++i){
	    System.out.println("dijkstra, coords: " + aCells.get(i).getCellCoords().first() + ", " + aCells.get(i).getCellCoords().second());
	}
	for(int i = 0; i < aCells.size(); ++i){
	    System.out.println("dijkstra, distance: " + aCells.get(i).getCellDistance());
	}
	for(int i = 0; i < aCells.size(); ++i){
	    System.out.println("dijkstra, prev: " + aCells.get(i).getCellPrev());
	}

	HashMap<Pair<Integer, Integer>, ArenaCell> uCells = makeUnvisitedSet(aCells, wCells, orgCell);
	//	HashMap<Pair<Integer, Integer>, ArenaCell> vCells = new HashMap<Pair<Integer, Integer>, ArenaCell>();
	//	ArrayList<Pair<ArenaCell, ArenaCell>> history = new ArrayList<Pair<ArenaCell, ArenaCell>>();
	ArenaCell currentCell = orgCell;
	currentCell.setCellDistance(0.0);
	//	for(ArenaCell ac : neighbors){
	//	    Pair<Integer, Integer> coords = ac.getCellCoords();
	//	    System.out.println("row: " + coords.first() + "\tcol: " + coords.second());
	//	}
	while(!currentCell.equals(dstCell)){
	    ArrayList<ArenaCell> neighbors = makeNeighborsSet(currentCell, uCells);
	    for(final ArenaCell nc : neighbors){
		// Make an entry for history with this currentCell and this neighbor to illustrate later.
		//history.add(new Pair<ArenaCell, ArenaCell>(currentCell, nc));
		
		//vCells.put(nc.getCellCoords(), nc);
		//nc.setCellPrev(currentCell);
		
		// Paint the neighbor cell the evalColor to indicate it's currently being evaluated...
		nc.setCellColor(evalColor);
		nc.repaint();
		//mainWindow.repaint(10, nc.getX() * nc.getCellCoords().second(), nc.getY() * nc.getCellCoords().first(), nc.getWidth(), nc.getHeight());
		/*
		new Thread(){
		    public void run(){
			final ArenaCell cell = nc;
			SwingUtilities.invokeLater(new Runnable() {
				public void run(){
				    cell.setCellColor(evalColor);
				    cell.repaint();
				}
			    });
		    }
		}.run();
		
		try{
		    Thread.sleep(250);
		}catch(InterruptedException e){
		    System.out.println("thread was interrupted...");
		    }*/

		// Calculate the tentative distance of the currently evaluated cell from the origin.
		Pair<Integer, Integer> cc_coords = currentCell.getCellCoords();
		Pair<Integer, Integer> nc_coords = nc.getCellCoords();
		double temp_distance = currentCell.getCellDistance() + 
		    Math.sqrt(Math.pow(Math.abs(cc_coords.first() - nc_coords.first()), 2) + 
			      Math.pow(Math.abs(cc_coords.second() - nc_coords.second()), 2));
		System.out.println("row: " + nc_coords.first() + "\tcol: " + nc_coords.second() + "\tdist: " + temp_distance);
		// Compare the tentative distance of the cell to the current distance and update if the tentative value is less.
		double nc_dist = nc.getCellDistance();
		if(temp_distance < nc_dist || nc_dist == -1.0){
		    nc.setCellDistance(temp_distance);
		    // Update the previous cell for the current neighbor cell to be the current cell.
		    aCells.get(aCells.indexOf(nc)).setCellPrev(currentCell);
		}
		
		// If goal cell is reached, no need to continue searching neighbors.
		if(nc.equals(dstCell)){
		    break;
		}
	    }
	    
	    // If neighbors contains destination, no need to continue searching other cells.
	    if(neighbors.contains(dstCell)){
		break;
	    }
	    
	    // Remove the current (just evaluated) cell from the unvisited set.
	    uCells.remove(currentCell.getCellCoords());
	    
	    // Update the currently evaluating cell.
	    // Choose the unvisited cell with the least distance from the origin.
	    currentCell = getLeastDist(uCells.values());
	    if(currentCell == null){
		// then all unvisited cells have distance -1, meaning they are not reachable
		System.out.println("goal is not reachable!");
		break;
	    }
	}
	
	//	playSequence(history, 250);
	printPath(dstCell, orgCell);
	System.out.println("Distance travelled is: " + dstCell.getCellDistance());
    }
   
    /* 
    private void playSequence(final ArrayList<Pair<ArenaCell, ArenaCell>> seq, final int d){
	// Plays a sequence of history events with a delay, d between events.
	// First ArenaCell is assumed to represent the currentCell for that timestep,
	// second ArenaCell represents the neighborCell for that timestep.
	//	new Thread(){
	//	    public void run(){
		final ArrayList<Pair<ArenaCell, ArenaCell>> history = seq;
		final int delay = d;
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
			    for(Pair<ArenaCell, ArenaCell> p : history){
				System.out.println("printing history...");
				ArenaCell c = p.first();
				ArenaCell n = p.second();
				c.setCellColor(pathLocusColor);
				n.setCellColor(evalColor);
				c.repaint();
				n.repaint();
				
				try{
				    Thread.sleep(delay);
				}catch(InterruptedException e){
				    System.out.println("thread was interrupted while sleeping...");
				}
			    }
			    
			}
		    });
	    }
    //	}.run();
	
    }
*/
    private void printPath(ArenaCell dst, ArenaCell src){
	// Illustrate the path from destination cell to source.
	dst.setCellColor(DST_COLOR);
	src.setCellColor(ORG_COLOR);
	ArenaCell currentCell;
	if(dst.getCellPrev() != null){
	    currentCell = dst.getCellPrev();
	}else{
	    System.out.println("Goal Cell is not reachable, therefore there is no path to illustrate...");
	    return;
	}
	
	while(currentCell.getCellPrev() != null){
	    currentCell.setCellColor(pathColor);
	    currentCell.repaint();
	    currentCell = currentCell.getCellPrev();
	    /*
	    try{
		Thread.sleep(100);
	    }catch(Exception e){
	    }
	    */
	}
	
	
    }
    
    private ArenaCell getLeastDist(final Collection<ArenaCell> cells){
	// Returns the cell with the least distance in the map.
	double leastDist = -1.0;
	ArenaCell leastCell = null;
	Iterator<ArenaCell> iter = cells.iterator();
	while(iter.hasNext()){
	    ArenaCell ac = (ArenaCell) iter.next();
	    double ac_dist = ac.getCellDistance();
	    if(leastDist == -1.0 && ac_dist != -1.0){
		// for initial value
		leastDist = ac_dist;
		leastCell = ac;
	    }else if(ac_dist != -1.0 && ac_dist < leastDist){
		// for later values, leastDist is > 0.0
		leastDist = ac_dist;
		leastCell = ac;
	    }
	}
	
	return leastCell;
    }
    
    private ArrayList<ArenaCell> makeNeighborsSet(final ArenaCell cell, final HashMap<Pair<Integer, Integer>, ArenaCell> cellMap){
	// Returns the existing neighbors (no walls or off-grid cells) of the 8-neighborhood of a cell.
	Pair<Integer, Integer> cellCoords = cell.getCellCoords();
	int row = cellCoords.first();
	int col = cellCoords.second();

	ArrayList<ArenaCell> neighbors = new ArrayList<ArenaCell>();
	for(int i = row - 1; i <= row + 1; ++i){
	    for(int j = col - 1; j <= col + 1; ++j){
		if(!(i == row && j == col)){
		    Pair<Integer, Integer> nCell = new Pair<Integer, Integer>(i, j);
		    if(cellMap.containsKey(nCell)){
			neighbors.add(cellMap.get(nCell));
		    }
		}
	    }
	}
		
	return neighbors;
    }
    
    private HashMap<Pair<Integer, Integer>, ArenaCell> makeUnvisitedSet(final ArrayList<ArenaCell> allCells, final ArrayList<ArenaCell> wallCells, final ArenaCell srcCell){
	// Returns a hashmap representing the unvisited cells with the cell coordinate as the key.
	//
	// Note that the unvisited set does not include walls or the current cell.
	//
	ArrayList<ArenaCell> cells = new ArrayList<ArenaCell>();
	cells.addAll(allCells);
	cells.remove(srcCell);
	for(ArenaCell ac : wallCells){
	    cells.remove(ac);
	    System.out.println("removing cell, it's a wall");
	}
	System.out.println("allCells size: " + aCells.size());
	System.out.println("cells size: " + cells.size());
	
	
	HashMap<Pair<Integer, Integer>, ArenaCell> cellMap = new HashMap<Pair<Integer, Integer>, ArenaCell>();
	for(ArenaCell ac : cells){
	    cellMap.put(ac.getCellCoords(), ac);
	}
	return cellMap;
    }

    private ArrayList<ArenaCell> makeEmptyCellsSet(final ArrayList<ArenaCell> allCells, final ArrayList<ArenaCell> wallCells, final ArenaCell srcCell, final ArenaCell goalCell){
	// Returns an ArrayList representing the empty cells that can be moved through.
	//
	// Note that the empty cells set does not include walls, or the source cell, or the goal cell.
	//
	ArrayList<ArenaCell> cells = new ArrayList<ArenaCell>();
	cells.addAll(allCells);
	cells.remove(srcCell);
	cells.remove(goalCell);
	for(ArenaCell ac : wallCells){
	    cells.remove(ac);
	    System.out.println("removing cell, it's a wall");
	}
	System.out.println("allCells size: " + aCells.size());
	System.out.println("cells size: " + cells.size());
	
	return cells;
    }
}
