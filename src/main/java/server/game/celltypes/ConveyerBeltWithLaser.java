package server.game.celltypes;

import content.OrientationEnum;
import server.game.*;


public class ConveyerBeltWithLaser extends Cell implements Interact {
    private OrientationEnum orientation;
    private int distance;
    private boolean isVertical;

    public ConveyerBeltWithLaser(Position position, OrientationEnum orientation, Integer distance) {
        super(position);
        this.orientation = orientation;
        this.distance = distance;
        this.isVertical = isVertical;
    }

    public ConveyerBeltWithLaser(Integer row, Integer col, OrientationEnum orientation, Integer distance) {
        this(new Position(row, col), orientation, distance);
    }


        @Override
        public void robotMovement (Robot r){
            for (int i = 0; i < distance; i++) {
                Position newPos = Move.calculateNewPosition(orientation, r.getRobotPosition(), 1);
                System.out.println("Attempting to move robot to: " + newPos);
                if (Move.validateMove(r, newPos.getPositionX(), newPos.getPositionY(), 1)) {
                    r.setRobotPosition(newPos);
                    System.out.println("Robot moved to: " + newPos);
                } else {
                    System.out.println("Move out of bounds or invalid: " + newPos);
                    break;
                }
            }
            Player player = Game.getInstance().getPlayerByRobot(r);
            if (player != null) {
                applyEffect(player);
                player.passDrawDamage(); // for laser effect
            }
        }

        public void applyEffect (Player player){
            if (player.getRobot().getRobotPosition().equals(this.getPosition())) {
                Game.getInstance().givePlayerSpamCard(player);
            }
        }
    }

