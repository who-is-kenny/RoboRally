package server.game;

public class Position {
    private int positionX;
    private int positionY;


    public Position() {}

    public Position(int positionX, int positionY){
        this.positionY = positionY;
        this.positionX = positionX;
    }


    //set new position
    public void setPosition(int newPositionX, int newPositionY){
        positionY = newPositionY;
        positionX = newPositionX;
    }

    //set x position
    public void setPositionX(int newPositionX){
        positionX = newPositionX;
    }

    //set y position
    public void setPositionY(int newPositionY){
        positionY = newPositionY;
    }

    //get x position
    public int getPositionX(){
        return positionX;
    }

    //get y position
    public int getPositionY(){
        return positionY;
    }

}
