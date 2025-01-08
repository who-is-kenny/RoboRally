package server.game;

import content.CourseNameEnum;
import server.game.celltypes.Cell;
import server.game.celltypes.Checkpoint;
import server.game.celltypes.RebootPoint;
import server.game.celltypes.StartPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;


public class Course {
    private static Course INSTANCE;

    public static Course getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Course();
        }
        return INSTANCE;
    }

    public void removeData() {
        INSTANCE = new Course();
    }

    private Course() {
    }
    private String courseName;
    private Cell[][] content;
    private ArrayList<StartPoint> startPoints;
    private ArrayList<RebootPoint> rebootPoints;
    private ArrayList<Checkpoint> checkPoints;
    private int height;
    private int width;



    public Course init(CourseNameEnum courseName) throws IOException, ClassNotFoundException {
        this.courseName = courseName.getCourseName();
        this.content = MapReader.txtToCellMatrix(courseName);
        this.height = this.content.length;
        this.width = this.content[0].length;
        this.startPoints = new ArrayList<>();
        this.rebootPoints = new ArrayList<>();
        this.checkPoints = new ArrayList<>();
        ArrayList<Cell> allCells = new ArrayList<>();
        for (Cell[] cells : content){
            System.out.println("COURSE.init(): ");
            for (Cell cell : cells) {
                System.out.print(cell.toString() + ", ");
                allCells.add(cell);
                if (RebootPoint.class.getName().equals(cell.getClass().getName()))
                    this.rebootPoints.add((RebootPoint) cell);
                else if (StartPoint.class.getName().equals(cell.getClass().getName()))
                    this.startPoints.add((StartPoint) cell);
                else if (Checkpoint.class.getName().equals(cell.getClass().getName()))
                    this.checkPoints.add((Checkpoint) cell);
            }
    }
        System.out.println("_--------------------------------------------------------------------");
        System.out.println("_--------------------------------------------------------------------");

        System.out.println("_--------------------------------------------------------------------");


        printCellMatrix(content);

        System.out.println("h:" + getHeight());
        System.out.println("2:" + getWidth());

        System.out.println(getCellAtPosition(new Position(12,0)).toString());
        this.checkPoints.sort(new Comparator<Checkpoint>() {
            @Override
            public int compare(Checkpoint o1, Checkpoint o2) {
                return o1.getCheckPointNum() - o2.getCheckPointNum();
            }
        });
        return INSTANCE;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Cell getCellAtPosition(Position position) {
        return content[position.getPositionY()][position.getPositionX()];
    }

    public ArrayList<RebootPoint> getRebootPoints() {
        return rebootPoints;
    }
    //todo tiles übereinander

    public static void printCellMatrix(Cell[][] cellMatrix) {
        if (cellMatrix == null || cellMatrix.length == 0) {
            System.out.println("The cell matrix is empty or null.");
            return;
        }

        for (int row = 0; row < cellMatrix.length; row++) {
            for (int col = 0; col < cellMatrix[row].length; col++) {
                Cell cell = cellMatrix[row][col];
                if (cell != null) {
                    System.out.print("[" + cell.toString() + "] ");
                } else {
                    System.out.print("[null] ");
                }
            }
            System.out.println(); // Move to the next row
        }
    }
}

