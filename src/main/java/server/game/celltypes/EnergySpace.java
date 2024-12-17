package server.game.celltypes;


import server.game.Position;
import server.game.Robot;

public class EnergySpace extends Cell{

    //constructors
    public EnergySpace(Position position){
        super(position);
    }

    public EnergySpace(Integer positionX, Integer positionY){
        this(new Position(positionX,positionY));
    }


    public void doEnergyEffect() {}
}
