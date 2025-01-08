package server.game.celltypes;

import server.game.Game;
import server.game.Interact;
import server.game.Position;
import server.game.Robot;

public class Pit extends Cell implements Interact {

    public Pit(Position position) {
        super(position);
    }

    public Pit(Integer positionX, Integer positionY) {
        this(new Position(positionX, positionY));
    }

    public void robotMovement(Robot r) {
        /*System.out.println("Checking if robot is on a pit...");
        if (r.getRobotPosition().equals(this.getPosition())) {*/
            System.out.println("Robot landed on a pit. Moving to reboot point...");
            Position rebootPoint = Game.getInstance().getRebootPoint();
            r.rebootRobot(rebootPoint);
        /*} else {
            System.out.println("Robot is not on a pit.");
        }*/
    }
}
