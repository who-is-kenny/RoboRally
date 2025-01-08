package server.game;

import content.OrientationEnum;

import static content.OrientationEnum.U;

public class MoveCard extends Cards {

    private final int moveAmount;

    public MoveCard(int moveAmount) {
        super("move" + moveAmount);
        this.moveAmount = moveAmount;
        if(moveAmount == -1){
            super.setCardName("moveback");
        }
    }

    //move robot moveAmount in the direction
    @Override
    public void playCardEffect(Player player) {
        OrientationEnum direction = player.getPlayerRobot().getOrientation();
        moveRobotInFacedDirection(player, direction);
    }

    //checks the direction the robot is facing and moves the Robot corresponding to the moveAmount
    private void moveRobotInFacedDirection(Player player, OrientationEnum direction){

        if(direction != null){
            switch (direction){
                case U:
                    player.getPlayerRobot().setRobotPosition(new Position(player.getPlayerRobot().getRobotPosition().getPositionX(),
                            player.getPlayerRobot().getRobotPosition().getPositionY() - moveAmount));
                    break;
                case D:
                    player.getPlayerRobot().setRobotPosition(new Position(player.getPlayerRobot().getRobotPosition().getPositionX(),
                            player.getPlayerRobot().getRobotPosition().getPositionY() + moveAmount));
                    break;
                case L:
                    player.getPlayerRobot().setRobotPosition(new Position(player.getPlayerRobot().getRobotPosition().getPositionX() - moveAmount,
                            player.getPlayerRobot().getRobotPosition().getPositionY()));
                    break;
                case R:
                    player.getPlayerRobot().setRobotPosition(new Position(player.getPlayerRobot().getRobotPosition().getPositionX() + moveAmount,
                            player.getPlayerRobot().getRobotPosition().getPositionY()));
                    break;
                default:
                    System.out.println("false direction case");
            }
        }else {
            System.out.println("direction is not initialized");
        }
    }
}
