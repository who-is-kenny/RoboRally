package server.game;

import content.OrientationEnum;

public class ChangeDirectionCard extends Cards {

    private final int turnAmount; //left is -1 right is +1 and U-turn is +2 = -2

    public ChangeDirectionCard(int turnAmount) {
        super("turn");
        this.turnAmount = turnAmount;
        super.setCardName(getCardType()+"turn");
    }

    private String getCardType(){
        if(turnAmount == 1){
            return  "right";
        }else if(turnAmount == -1){
            return "left";
        }else if(turnAmount == 2){
            return "u";
        }else{
            System.out.println("card type doesnt exist");
        }
        return "";
    }

    @Override
    public void playCardEffect(Player player){
        String currentDirection = player.getPlayerRobot().getFacingDirection();
        changeDirectionOfRobot(player, currentDirection);
    }

    private void changeDirectionOfRobot(Player player, String currentDirection){
        if(currentDirection != null){
            switch (currentDirection){
                case "up":
                    setNewFacingDirection(currentDirection, player);
                    break;
                case "down":
                    setNewFacingDirection(currentDirection, player);
                    break;
                case "left":
                    setNewFacingDirection(currentDirection, player);
                    break;
                case "right":
                    setNewFacingDirection(currentDirection, player);
                    break;
            }
        }else{
            System.out.println("direction is not initialized");
        }
    }

    private void setNewFacingDirection(String currentDirection, Player player) {
        if(turnAmount == -1){
            switch (currentDirection){
                case "up":
                    //player.getPlayerRobot().setFacingDirection("left");
                    player.getPlayerRobot().setOrientation(OrientationEnum.U);
                    break;
                case "down":
                    //player.getPlayerRobot().setFacingDirection("right");
                    player.getPlayerRobot().setOrientation(OrientationEnum.D);
                    break;
                case "left":
                    //player.getPlayerRobot().setFacingDirection("down");
                    player.getPlayerRobot().setOrientation(OrientationEnum.L);
                    break;
                case "right":
                    //player.getPlayerRobot().setFacingDirection("up");
                    player.getPlayerRobot().setOrientation(OrientationEnum.R);
                    break;
            }
        }else if(turnAmount == 1){
            switch (currentDirection){
                case "up":
                    //player.getPlayerRobot().setFacingDirection("right");
                    player.getPlayerRobot().setOrientation(OrientationEnum.R);
                    break;
                case "down":
                    //player.getPlayerRobot().setFacingDirection("left");
                    player.getPlayerRobot().setOrientation(OrientationEnum.D);
                    break;
                case "left":
                    //player.getPlayerRobot().setFacingDirection("up");
                    player.getPlayerRobot().setOrientation(OrientationEnum.L);
                    break;
                case "right":
                    //player.getPlayerRobot().setFacingDirection("down");
                    player.getPlayerRobot().setOrientation(OrientationEnum.R);
                    break;
            }
        }else if(turnAmount == 2){
            switch (currentDirection){
                case "up":
                    //player.getPlayerRobot().setFacingDirection("down");
                    player.getPlayerRobot().setOrientation(OrientationEnum.U);
                    break;
                case "down":
                    //player.getPlayerRobot().setFacingDirection("up");
                    player.getPlayerRobot().setOrientation(OrientationEnum.D);
                    break;
                case "left":
                    //player.getPlayerRobot().setFacingDirection("right");
                    player.getPlayerRobot().setOrientation(OrientationEnum.L);
                    break;
                case "right":
                    //player.getPlayerRobot().setFacingDirection("left");
                    player.getPlayerRobot().setOrientation(OrientationEnum.R);
                    break;
            }
        }
    }

}
