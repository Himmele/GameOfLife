import java.util.Random;

class Cell {
    private boolean mIsAlive;
    private int mNumAliveNeighbours;
    
    public boolean isAlive() {
        return mIsAlive;
    }
    
    public int getNumAliveNeighbours() {
        return mNumAliveNeighbours;
    }
    
    public void setNumAliveNeighbours(int numAliveNeighbours) {
        mNumAliveNeighbours = numAliveNeighbours;
    }    
    
    public void birth() {
        mIsAlive = true;
    }
    
    public void death() {
        mIsAlive = false;
    }    
}

public class GameOfLife {
    private static final int SIZE = 50;
    private static final float CELL_DENSITY = 0.42f;
    private Cell[][] mCells = new Cell[SIZE][SIZE];
    
    interface CellRunnable {
        public void run(Cell cell, int x, int y);
    }
    
    private void forEachCell(CellRunnable runnable) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                runnable.run(mCells[y][x], x, y);
            }
        }
    }
    
    public void init() {
        final Random rand = new Random();

        forEachCell(new CellRunnable() {
            @Override
            public void run(Cell cell, int x, int y) {
                mCells[y][x] = new Cell();                
                if (rand.nextFloat() < CELL_DENSITY) {
                    mCells[y][x].birth();
                } else {
                    mCells[y][x].death();
                }
            }
        });
    }
    
    public void runOnce() {       
        forEachCell(new CellRunnable() {
            @Override
            public void run(Cell cell, int x, int y) {
                Cell[] neighbours = getNeighbours(x, y);
                int sum = 0;
                for (Cell neighbourCell : neighbours) {
                    sum += neighbourCell.isAlive() ? 1 : 0;
                }
                mCells[y][x].setNumAliveNeighbours(sum);
            }
        });
        
        forEachCell(new CellRunnable() {
            @Override
            public void run(Cell cell, int x, int y) {
                if (cell.getNumAliveNeighbours() == 3) {
                    cell.birth();
                } else {
                    if (cell.getNumAliveNeighbours() != 2) {
                        cell.death();
                    }
                }
            }
        });
    }   
    
    private Cell[] getNeighbours(int x, int y) {
        Cell[] neighbours = new Cell[8];
        neighbours[0] = mCells[y-1 != -1 ? (y-1) % SIZE : SIZE-1][x-1 != -1 ? (x-1) % SIZE : SIZE-1];
        neighbours[1] = mCells[y-1 != -1 ? (y-1) % SIZE : SIZE-1][x];
        neighbours[2] = mCells[y-1 != -1 ? (y-1) % SIZE : SIZE-1][(x+1) % SIZE];
        neighbours[3] = mCells[y][x-1 != -1 ? (x-1) % SIZE : SIZE-1];
        neighbours[4] = mCells[y][(x+1) % SIZE];
        neighbours[5] = mCells[(y+1) % SIZE][x-1 != -1 ? (x-1) % SIZE : SIZE-1];
        neighbours[6] = mCells[(y+1) % SIZE][x];
        neighbours[7] = mCells[(y+1) % SIZE][(x+1) % SIZE];
        return neighbours;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        forEachCell(new CellRunnable() {
            @Override
            public void run(Cell cell, int x, int y) {
                sb.append(cell.isAlive() ? "X" : " ");
                if (x == SIZE - 1) {
                    sb.append('\n');
                }                            
            }
        });
        return sb.toString();
    }
    
    public static void main(String[] args) throws Exception {
        GameOfLife gol = new GameOfLife();
        gol.init();
        System.out.println(gol.toString());

        for(;;) {
            gol.runOnce();

            // Clear the console
            System.out.println(((char) 27)+"[2J");
            // Set the cursor to position 1,1
            System.out.println(((char) 27)+"[1;1H");
            System.out.println(gol.toString());
            
            Thread.sleep(100);
        }
    }
}
