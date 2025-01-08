package server.game;

import content.OrientationEnum;
import server.game.celltypes.Antenna;
import server.game.celltypes.Cell;
import server.game.celltypes.Wall;
import server.game.celltypes.WallWithLaser;

public class Move {
    public static Position calculateNewPosition(OrientationEnum orientation, Position currentPos, int amount) {
        Position newPos = new Position(currentPos.getPositionX(), currentPos.getPositionY());
        System.out.print("MOVE.calculateNewPosition (" + orientation.toString() + ", pos: (" + currentPos.getPositionX() + ", " + currentPos.getPositionY() + "), " + amount + ") ");
        //this switch takes the robot Orientation given by the Card class, and returns the newPosition based on this orientation

        for (int i = 0; i < Math.abs(amount); i++) {
            switch (orientation) {
                case U:
                    newPos.setPositionY(newPos.getPositionY() - 1);
                    break;
                case R:
                    newPos.setPositionX(newPos.getPositionX() + 1);
                    break;
                case D:
                    newPos.setPositionY(newPos.getPositionY() + 1);
                    break;
                case L:
                    newPos.setPositionX(newPos.getPositionX() - 1);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + orientation + " is different from N/S/E/W");
            }
        }
        System.out.println("returns: (" + newPos.getPositionX() + ", " + newPos.getPositionY() + ")");
        return newPos;
    }

    public static boolean validateMove(Robot r, int row, int col, int movement) {
        // robot out of board
        if (!(row >= 0 && row < Course.getInstance().getHeight() && col >= 0 && col < Course.getInstance().getWidth())) {
            System.out.println("MOVE.validateMove() detected walk off map");
            if (!Course.getInstance().getRebootPoints().isEmpty()) {
                Position rebootPosition = Course.getInstance().getRebootPoints().get(0).getPosition();
                System.out.println("current reboot position: " + rebootPosition.getPositionX() + ", " + rebootPosition.getPositionY());
                r.rebootRobot(rebootPosition);
            }
            return false;
        }
        Cell currentCell = Course.getInstance().getCellAtPosition(r.getRobotPosition());
        if (currentCell instanceof Wall || currentCell instanceof WallWithLaser) { // current position is a wall
            System.out.println("MOVE.validateMove() detected wall");
            OrientationEnum wallOrientation;
            if (currentCell instanceof Wall) {
                wallOrientation = ((Wall) currentCell).getOrientation();
            } else {
                wallOrientation = ((WallWithLaser) currentCell).getOrientation();
            }
                if (movement == 1 && wallOrientation.equals(r.getOrientation())) {
                    return false;
                    /*return !((Wall) cell).getOrientation().equals(r.getOrientation());*/
                } else if (movement == -1 && wallOrientation.equals(r.getOrientation().getOpposite())) {
                    return false;
                    /*return !((Wall) cell).getOrientation().getOpposite().equals(r.getOrientation());*/
                }
            }
            // if a wall is on the next tile and blocks the robot from moving
            Cell newCell = Course.getInstance().getCellAtPosition(new Position(col, row)); // switched col and row
            if (newCell instanceof Wall || newCell instanceof WallWithLaser) {  // next position is wall
                System.out.println("MOVE.validateMove() detected wall");
                OrientationEnum wallOrientation2;
                if (newCell instanceof Wall) {
                    wallOrientation2 = ((Wall) newCell).getOrientation();
                } else if (newCell instanceof WallWithLaser) {
                    wallOrientation2 = ((WallWithLaser) newCell).getOrientation();
                } else {
                    // Handle other cases if necessary
                    wallOrientation2 = null;
                }
                if (movement == 1 && wallOrientation2.equals(r.getOrientation().getOpposite())) {
                    return false;
                /*return tileOrientation.equals(r.getOrientation().getLeft()) || tileOrientation.equals(r.getOrientation().getRight())
                        || (tileOrientation.equals(r.getOrientation()));*/
                } else if (movement == -1 && wallOrientation2.equals(r.getOrientation())) {
                    return false;
                /*return tileOrientation.equals(r.getOrientation().getLeft()) || tileOrientation.equals(r.getOrientation().getRight())
                        || (tileOrientation.equals(r.getOrientation().getOpposite()));*/
                }
            }
            if (newCell instanceof Antenna){
                System.out.println("MOVE.validateMove() detected antenna. Move not allowed.");
                return false;
            }
        /*if (Math.abs(r.getRobotPosition().getPositionX() - col) > 1 || Math.abs(r.getRobotPosition().getPositionY() - row) > 1) {
            return false;
        }*/
            return true;
        }
    }

