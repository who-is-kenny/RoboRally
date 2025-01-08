package server.game.celltypes;

import content.OrientationEnum;
import server.game.*;

public class WallWithLaser extends Cell implements Interact {

    private OrientationEnum orientation;
    private boolean isVertical;

    public WallWithLaser(Position position, OrientationEnum orientation, boolean isVertical) {
        super(position);
        this.create(orientation);
        this.isVertical = isVertical;
    }

    public WallWithLaser(Integer positionX, Integer positionY, OrientationEnum orientation, Boolean isVertical) {
        this(new Position(positionX, positionY), orientation, isVertical);
    }

    private void create(OrientationEnum orientation) {
        this.orientation = orientation;
    }

    @Override
    public void robotMovement(Robot r) {

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
    public OrientationEnum getOrientation() {
        return orientation;
    }

    }

