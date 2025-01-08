package server.game.celltypes;

import server.game.Interact;
import server.game.Position;
import server.game.Robot;

public class EnergySpace extends Cell implements Interact {
    public EnergySpace(Position position){
        super(position);
    }

    public EnergySpace(Integer positionX, Integer positionY){
        this(new Position(positionX,positionY));
    }

@Override
    public void robotMovement(Robot r){
        r.collectEnergyCube();
        r.ownedBy().passEnergySpaceMessage();
        System.out.println("robot has energy: " + r.getEnergy()); // todo remove print later
    }



}
