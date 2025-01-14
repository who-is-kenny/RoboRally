package server.game;

import content.CourseNameEnum;
import content.OrientationEnum;
import server.game.celltypes.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class MapReader {

    private static final String PATH_TO_MAP_FILE = "/maps/";
    private static final String FULLY_QUALIFIED_NAME_OF_CELLTYPE = "server.game.celltypes.";
    private static final String FULLY_QUALIFIED_NAME_OF_CHECKPOINT = "server.game.celltypes.Checkpoint";
    private static final String FULLY_QUALIFIED_NAME_OF_CONVEYERBELT = "server.game.celltypes.ConveyerBelt";
    private static final String FULLY_QUALIFIED_NAME_OF_WALL = "server.game.celltypes.Wall";
    private static final String FULLY_QUALIFIED_NAME_OF_LASER = "server.game.celltypes.Laser";
    private static final String FULLY_QUALIFIED_NAME_OF_GEAR = "server.game.celltypes.Gear";
    private static final String FULLY_QUALIFIED_NAME_OF_PIT = "server.game.celltypes.Pit";
    private static final String FULLY_QUALIFIED_NAME_OF_PUSHPANEL = "server.game.celltypes.PushPanel";
    private static final String FULLY_QUALIFIED_NAME_OF_CONVEYERBELT_WITH_LASER = "server.game.celltypes.ConveyerBeltWithLaser";
    private static final String FULLY_QUALIFIED_NAME_OF_WALL_WITH_LASER = "server.game.celltypes.WallWithLaser";
    private static final String FULLY_QUALIFIED_NAME_OF_CONVEYERBELT_WITH_TURN = "server.game.celltypes.ConveyerBeltWithTurn";



    private static Cell createInstance(String str, int positionY, int positionX) {
        if ("Antenna".equals(str)) return Antenna.getInstance();
        try {
            if (str.startsWith("CheckPoint")) {
                Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_CHECKPOINT);
                Checkpoint checkPoint = (Checkpoint) clz.getDeclaredConstructor(Integer.class, Integer.class).newInstance(positionX, positionY);
                checkPoint.setCheckPointNum(Integer.parseInt("" + str.charAt(str.length() - 1)));
                return checkPoint;
            } else if (str.equals("RightOne")) {
                return createConveyerBelt(positionX, positionY, OrientationEnum.R, 1);
            } else if (str.equals("RightTwo")) {
                return createConveyerBelt(positionX, positionY, OrientationEnum.R, 2);
            } else if (str.equals("LeftOne")) {
                return createConveyerBelt(positionX, positionY, OrientationEnum.L, 1);
            } else if (str.equals("LeftTwo")) {
                return createConveyerBelt(positionX, positionY, OrientationEnum.L, 2);
            } else if (str.equals("UpOne")) {
                return createConveyerBelt(positionX, positionY, OrientationEnum.U, 1);
            } else if (str.equals("UpTwo")) {
                return createConveyerBelt(positionX, positionY, OrientationEnum.U, 2);
            } else if (str.equals("DownOne")) {
                return createConveyerBelt(positionX, positionY, OrientationEnum.D, 1);
            } else if (str.equals("DownTwo")) {
                return createConveyerBelt(positionX, positionY, OrientationEnum.D, 2);
            } else if (str.equals("RTRightOne")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.R, 1,true);
            } else if (str.equals("RTRightTwo")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.R, 2,true);
            } else if (str.equals("RTLeftOne")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.L, 1,true);
            } else if (str.equals("RTLeftTwo")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.L, 2,true);
            } else if (str.equals("RTUpOne")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.U, 1,true);
            } else if (str.equals("RTUpTwo")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.U, 2,true);
            } else if (str.equals("RTDownOne")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.D, 1,true);
            } else if (str.equals("RTDownTwo")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.D, 2,true);
            } else if (str.equals("LTRightOne")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.R, 1,false);
            } else if (str.equals("LTRightTwo")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.R, 2,false);
            } else if (str.equals("LTLeftOne")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.L, 1,false);
            } else if (str.equals("LTLeftTwo")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.L, 2,false);
            } else if (str.equals("LTUpOne")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.U, 1,false);
            } else if (str.equals("LTUpTwo")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.U, 2,false);
            } else if (str.equals("LTDownOne")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.D, 1,false);
            } else if (str.equals("LTDownTwo")) {
                return createConveyerBeltWithTurn(positionX, positionY, OrientationEnum.D, 2,false);
            } else if (str.equals("WallRight")) {
                return createWall(positionX, positionY, OrientationEnum.R);
            } else if (str.equals("WallUp")) {
                return createWall(positionX, positionY, OrientationEnum.U);
            } else if (str.equals("WallLeft")) {
                return createWall(positionX, positionY, OrientationEnum.L);
            } else if (str.equals("WallDown")) {
                return createWall(positionX, positionY, OrientationEnum.D);
            } else if (str.equals("LaserVertical")) {
                return createLaser(positionX, positionY, true);
            } else if (str.equals("LaserHorizontal")) {
                return createLaser(positionX, positionY, false);
            } else if (str.equals("GearClockwise")) {
                return createGear(positionX, positionY, true);
            } else if (str.equals("GearCounterClockwise")) {
                return createGear(positionX, positionY, false);
            } else if (str.equals("PushPanelRightEven")) {
                return createPushPanel(positionX, positionY, OrientationEnum.L, 1 ,true);
            } else if (str.equals("PushPanelLeftEven")) {
                return createPushPanel(positionX, positionY, OrientationEnum.R, 1 ,true);
            } else if (str.equals("PushPanelUpEven")) {
                return createPushPanel(positionX, positionY, OrientationEnum.D, 1 ,true);
            } else if (str.equals("PushPanelDownEven")) {
                return createPushPanel(positionX, positionY, OrientationEnum.U, 1 ,true);
            } else if (str.equals("PushPanelRightOdd")) {
                return createPushPanel(positionX, positionY, OrientationEnum.L, 1 ,false);
            } else if (str.equals("PushPanelLeftOdd")) {
                return createPushPanel(positionX, positionY, OrientationEnum.R, 1 ,false);
            } else if (str.equals("PushPanelUpOdd")) {
                return createPushPanel(positionX, positionY, OrientationEnum.D, 1 ,false);
            } else if (str.equals("PushPanelDownOdd")) {
                return createPushPanel(positionX, positionY, OrientationEnum.U, 1 ,false);
            } else if (str.equals("RightTwoLaser")){
                return createConveyerBeltWithLaser(positionX, positionY, OrientationEnum.R, 2);
            } else if (str.equals("LeftTwoLaser")) {
                return createConveyerBeltWithLaser(positionX, positionY, OrientationEnum.L, 2);
            } else if (str.equals("UpTwoLaser")) {
                return createConveyerBeltWithLaser(positionX, positionY, OrientationEnum.U, 2);
            } else if (str.equals("DownTwoLaser")) {
                return createConveyerBeltWithLaser(positionX, positionY, OrientationEnum.D, 2);
            } else if (str.equals("WallRightLaser")) {
                return createWallWithLaser(positionX, positionY, OrientationEnum.R, false);
            } else if (str.equals("WallLeftLaser")) {
                return createWallWithLaser(positionX, positionY, OrientationEnum.L, false);
            } else if (str.equals("WallUpLaser")) {
                return createWallWithLaser(positionX, positionY, OrientationEnum.U, true);
            } else if (str.equals("WallDownLaser")) {
                return createWallWithLaser(positionX, positionY, OrientationEnum.D, true);
            } else if (str.equals("Pit")) {
                return createPit(positionX, positionY);
            } else {
                Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_CELLTYPE + str);
                return (Cell) clz.getDeclaredConstructor(Integer.class, Integer.class).newInstance(positionX, positionY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Gear createGear(int row, int col, boolean isClockwise) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_GEAR);
        Gear gear = (Gear) clz.getDeclaredConstructor(Integer.class, Integer.class, Boolean.class).newInstance(row, col, isClockwise);
        return gear;
    }

    private static Cell createLaser(int row, int col, boolean isVertical) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_LASER);
        Laser laser = (Laser) clz.getDeclaredConstructor(Integer.class, Integer.class, Boolean.class).newInstance(row, col, isVertical);
        return laser;
    }

    private static Cell createWall(int row, int col, OrientationEnum orientation) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_WALL);
        Wall wall = (Wall) clz.getDeclaredConstructor(Integer.class, Integer.class, OrientationEnum.class).newInstance(row, col, orientation);
        return wall;
    }

    private static Cell createConveyerBelt(int row, int col, OrientationEnum orientation, int distance) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_CONVEYERBELT);
        ConveyerBelt conveyerBelt = (ConveyerBelt) clz.getDeclaredConstructor(Integer.class, Integer.class, OrientationEnum.class, Integer.class).newInstance(row, col, orientation, distance);
        return conveyerBelt;
    }

    private static Cell createPushPanel(int positionX, int positionY, OrientationEnum orientation, int distance , boolean isEven) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_PUSHPANEL);
        PushPanel pushPanel = (PushPanel) clz.getDeclaredConstructor(Integer.class, Integer.class, OrientationEnum.class, Integer.class , boolean.class).newInstance(positionX, positionY, orientation, distance , isEven);
        return pushPanel;
    }

    private static Cell createConveyerBeltWithLaser(int row, int col, OrientationEnum orientation, int distance) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_CONVEYERBELT_WITH_LASER);
        ConveyerBeltWithLaser conveyerBeltWithLaser = (ConveyerBeltWithLaser) clz.getDeclaredConstructor(Integer.class, Integer.class, OrientationEnum.class, Integer.class).newInstance(row, col, orientation, distance);
        return conveyerBeltWithLaser;
    }

    private static Cell createWallWithLaser(int row, int col, OrientationEnum orientation, boolean isVertical) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_WALL_WITH_LASER);
        WallWithLaser wallWithLaser = (WallWithLaser) clz.getDeclaredConstructor(Integer.class, Integer.class, OrientationEnum.class, Boolean.class).newInstance(row, col, orientation, isVertical);
        return wallWithLaser;
    }

    private static Cell createPit(int row, int col) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_PIT);
        Pit pit = (Pit) clz.getDeclaredConstructor(Integer.class, Integer.class).newInstance(row, col);
        return pit;
    }

    private static Cell createConveyerBeltWithTurn(int row, int col, OrientationEnum orientation, int distance, boolean isClockwise) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clz = Class.forName(FULLY_QUALIFIED_NAME_OF_CONVEYERBELT_WITH_TURN);
        ConveyerBeltWithTurn conveyerBeltWithTurn = (ConveyerBeltWithTurn) clz.getDeclaredConstructor(Integer.class, Integer.class, OrientationEnum.class, Integer.class, Boolean.class).newInstance(row, col, orientation, distance , isClockwise);
        return conveyerBeltWithTurn;
    }

    public static Cell[][] txtToCellMatrix(CourseNameEnum courseName) throws ClassNotFoundException, IOException {
        Cell[][] result = null;
        ArrayList<String> strList = MapReader.readLineToStringArray(courseName);

        // The number of columns
        String s = strList.get(0);
        int columnNum = s.split(",").length;

        result = new Cell[strList.size()][columnNum];

        // Put data into a 2D array
        int count = 0;
        for (String line : strList) {
            String[] strs = line.split(",");
            for (int i = 0; i < columnNum; i++) {
                int priority = determinePriority(strs[i]);
                result[count][i] = MapReader.createInstance(strs[i], count, i);
            }
            count++;
        }
        return result;

    }

    //versuch hier irgendwas
    public int getPriorityFromTile(Position positionOfRobot){
        return 0;
    }


    private static int determinePriority(String cellType){
        switch (cellType){
            case "UpTwo":
                return 1;
            case "DownTwo":
                return 1;
            case "RightTwo":
                return 1;
            case "LeftTwo":
                return 1;
            case "UpOne":
                return 2;
            case "DownOne":
                return 2;
            case "RightOne":
                return 2;
            case "LeftOne":
                return 2;
            case "PushPanelUp":
                return 3;
            case "PushPanelDown":
                return 3;
            case "PushPanelRight":
                return 3;
            case "PushPanelLeft":
                return 3;
            case "GearClockwise":
                return 4;
            case "GearCounterClockwise":
                return 4;
            case "LaserVertical":
                return 5;
            case "LaserHorizontal":
                return 5;
                //todo priority shoot laser
            case "EnergySpace":
                return 7;
            case "CheckPoint":
                return 8;

            default:
                return 0;
        }
    }

    private static ArrayList<String> readLineToStringArray(CourseNameEnum courseName) throws IOException {
        ArrayList<String> strList = new ArrayList<>();
        InputStream is = (MapReader.class.getResourceAsStream(PATH_TO_MAP_FILE + courseName.getCourseName() + ".txt"));
        if (is == null) {
            throw new IOException("Course file not found: " + PATH_TO_MAP_FILE + courseName.getCourseName() + ".txt");
        }
        BufferedReader readerBuf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String lineStr;
        while ((lineStr = readerBuf.readLine()) != null) {
            strList.add(lineStr);
        }
        readerBuf.close();
        return strList;

//        public static void printCellMatrix(Cell[][] cellMatrix) {
//            if (cellMatrix == null || cellMatrix.length == 0) {
//                System.out.println("The cell matrix is empty or null.");
//                return;
//            }
//
//            for (int row = 0; row < cellMatrix.length; row++) {
//                for (int col = 0; col < cellMatrix[row].length; col++) {
//                    Cell cell = cellMatrix[row][col];
//                    if (cell != null) {
//                        System.out.print("[" + cell.toString() + "] ");
//                    } else {
//                        System.out.print("[null] ");
//                    }
//                }
//                System.out.println(); // Move to the next row
//            }
//        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        MapReader mapReader = new MapReader();
        Cell[][] cells = MapReader.txtToCellMatrix(CourseNameEnum.getFromString("DizzyHighway"));

    }

}