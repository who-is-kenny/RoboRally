package server.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapMessages {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class , new MessageSerializer()).create();


    public String createDizzyHighway(){
        // Create the game map
        List<List<List<Tile>>> gameMap = new ArrayList<>();

        // Row 1
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Antenna", "Start A", "right"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}})
        ));

        // Row 2
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "top"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "bottom"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "Start A", "right", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "right"}}),
                createTiles(new String[][]{{"Wall", "Start A", "right"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "Start A", "right", "left"}, {"speed", "1"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "right", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "right", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"EnergySpace", "5B"}, {"count", "1"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "5B", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "bottom", "top", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "bottom", "left", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "right", "left", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "5B", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "left", "top", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"EnergySpace", "5B"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "right", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "left", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Wall", "5B", "top"}}),
                createTiles(new String[][]{{"Laser", "5B", "top"}, {"count", "1"}, {"Wall", "5B", "bottom"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Wall", "5B", "left"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "right", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "left", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}, {"RestartPoint", "DizzyHighway", "bottom"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"EnergySpace", "5B"}, {"count", "1"}}),
                createTiles(new String[][]{{"Laser", "5B", "left"}, {"count", "1"}, {"Wall", "5B", "right"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "right", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "left", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Laser", "5B", "right"}, {"count", "1"}, {"Wall", "5B", "left"}}),
                createTiles(new String[][]{{"EnergySpace", "5B"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "right", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "left", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Wall", "5B", "right"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Laser", "5B", "bottom"}, {"count", "1"}, {"Wall", "5B", "top"}}),
                createTiles(new String[][]{{"Wall", "5B", "bottom"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "right", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "left", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"EnergySpace", "5B"}, {"count", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "right", "bottom", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "top", "bottom"}, {"speed", "2"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "left", "right", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "top", "right", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "top", "bottom", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "top", "bottom"}, {"speed", "2"}})
        ));

        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"EnergySpace", "5B"}, {"count", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "left", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "5B", "left", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "5B"}, {"CheckPoint", "DizzyHighway", "1"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}}),
                createTiles(new String[][]{{"Empty", "5B"}})
        ));


        // Set game map to the MessageBody
        MessageBody messageBody = new MessageBody();
        messageBody.setGameMap(gameMap);

        // Set MessageBody to the Message
        Message message = new Message();
        message.setMessageType("GameStarted");
        message.setMessageBody(messageBody);

        return gson.toJson(message);
    }


    public String createDeathTrap() {
        // Create the game map
        List<List<List<Tile>>> gameMap = new ArrayList<>();

        // Row 1
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}, {"CheckPoint", "DeathTrap"}, {"count", "5"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "bottom", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "bottom", "top"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "bottom", "top"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "bottom", "top"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}})
        ));

        // Row 2
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "bottom"}, {"Wall", "2A", "top"}, {"registers", "1,3,5"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "bottom", "top"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "left", "top"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Wall", "2A", "right"}}),
                createTiles(new String[][]{{"Empty", "2A"}, {"CheckPoint", "DeathTrap"}, {"count", "1"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "right"}, {"Wall", "2A", "left"}, {"registers", "1,3,5"}}),
                createTiles(new String[][]{{"Empty", "2A"}})
        ));

        // Row 3
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "2A", "left", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Wall", "2A", "bottom"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "right"}, {"Wall", "2A", "left"}, {"registers", "2,4"}}),
                createTiles(new String[][]{{"EnergySpace", "2A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "top"}, {"Wall", "2A", "bottom"}, {"registers", "2,4"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}})
        ));

        // Row 4
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "2A", "left", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "top"}, {"Wall", "2A", "bottom"}, {"registers", "1,3,5"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"EnergySpace", "2A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}})
        ));

        // Row 5
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "2A", "left", "bottom"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "top", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"EnergySpace", "2A"}, {"count", "1"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "top"}, {"Wall", "2A", "bottom"}, {"registers", "2,4"}}),
                createTiles(new String[][]{{"Empty", "2A"}, {"CheckPoint", "DeathTrap", "count", "2"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "bottom"}, {"Wall", "2A", "top"}, {"registers", "2,4"}}),
                createTiles(new String[][]{{"EnergySpace", "2A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "right", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}})
        ));

        // Row 6
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "left", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Wall", "2A", "bottom"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "bottom", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "right", "top"}, {"speed", "1"}})
        ));

        //Row 7
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"EnergySpace", "2A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "bottom"}, {"Wall", "2A", "top"}, {"registers", "1,3,5"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"Wall", "2A", "top"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "right", "left"}, {"speed", "1"}})
        ));

        //Row 8
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "bottom"}, {"Wall", "2A", "top"}, {"registers", "2,4"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"EnergySpace", "2A"}, {"count", "1"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "left"}, {"Wall", "2A", "right"}, {"registers", "2,4"}}),
                createTiles(new String[][]{{"Empty", "2A"} ,{"CheckPoint", "DeathTrap"}, {"count", "3"} }),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "right", "left"}, {"speed", "1"}})
        ));

        //Row 9
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "left"}, {"Wall", "2A", "right"}, {"registers", "1,3,5"}}),
                createTiles(new String[][]{{"Empty", "2A"},{"CheckPoint", "DeathTrap", "count", "4"}}),
                createTiles(new String[][]{{"Wall", "2A", "left"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "right", "bottom"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "top", "bottom"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Pit", "2A"}}),
                createTiles(new String[][]{{"PushPanel", "2A", "top"}, {"Wall", "2A", "bottom"}, {"registers", "1,3,5"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "right", "left"}, {"speed", "1"}})
        ));
        //Row 10
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "top", "bottom"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "top", "bottom"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "top", "bottom"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "2A", "top", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}}),
                createTiles(new String[][]{{"Empty", "2A"}})
        ));
        //Row 11
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "Start A", "left", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "left"}}),
                createTiles(new String[][]{{"Wall", "Start A", "left"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "Start A", "left", "right"}, {"speed", "1"}})
        ));
        //Row 12
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "top"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "bottom"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}})
        ));

        //Row 13
        gameMap.add(Arrays.asList(
                // Row of tiles
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Antenna", "Start A", "left"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{
                        {"Empty", "Start A"},
                        {"RestartPoint", "DeathTrap", "left"}
                })
        ));

        // Set game map to the MessageBody
        MessageBody messageBody = new MessageBody();
        messageBody.setGameMap(gameMap);

        // Set MessageBody to the Message
        Message message = new Message();
        message.setMessageType("GameStarted");
        message.setMessageBody(messageBody);

        return gson.toJson(message);
    }



    public String createExtraCrispy() {
        // Create the game map
        List<List<List<Tile>>> gameMap = new ArrayList<>();

        // Row 1
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"RestartPoint", "ExtraCrispy", "right"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Antenna", "Start A", "right"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}})
        ));

        // Row 2
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "top"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "bottom"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}})
        ));

        // Row 3
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "Start A", "right", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "right"}}),
                createTiles(new String[][]{{"Wall", "Start A", "right"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "Start A", "right", "left"}, {"speed", "1"}})
        ));

        // Row 4
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"EnergySpace", "4A"}, {"count", "1"}, {"Wall", "4A", "top"}}),
                createTiles(new String[][]{{"Wall", "4A", "bottom"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"EnergySpace", "4A"}, {"count", "1"}})
        ));

        // Row 5
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "4A", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "right", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "right", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Laser", "4A", "right"}, {"count", "1"}, {"Wall", "4A", "left"}})
        ));

        // Row 6
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Laser", "4A", "bottom"}, {"count", "1"}, {"Wall", "4A", "top"}, {"CheckPoint", "ExtraCrispy", "4"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "right", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "right", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Laser", "4A", "top"}, {"count", "1"}, {"Wall", "4A", "bottom"}, {"CheckPoint", "ExtraCrispy", "2"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}})
        ));

        // Row 7
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "bottom", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Pit", "4A"}}),
                createTiles(new String[][]{{"Pit", "4A"}}),
                createTiles(new String[][]{{"Gear", "4A", "clockwise"}}),
                createTiles(new String[][]{{"Wall", "4A", "left"}}),
                createTiles(new String[][]{{"Pit", "4A"}}),
                createTiles(new String[][]{{"Pit", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Laser", "4A", "left"}, {"count", "1"}, {"Wall", "4A", "right"}})
        ));

        // Row 8
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "left", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Laser", "4A", "right"}, {"count", "1"}, {"Wall", "4A", "left"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"EnergySpace", "4A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Laser", "4A", "right"}, {"count", "1"}, {"Wall", "4A", "left"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}})
        ));

        // Row 9
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"EnergySpace", "4A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Laser", "4A", "left"}, {"Wall", "4A", "right"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Laser", "4A", "left"}, {"Wall", "4A", "right"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "right", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "4A"}})
        ));

        // Row 10
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Laser", "4A", "right"}, {"Wall", "4A", "left"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Pit", "4A"}}),
                createTiles(new String[][]{{"Pit", "4A"}}),
                createTiles(new String[][]{{"Wall", "4A", "right"}}),
                createTiles(new String[][]{{"Gear", "4A", "counterclockwise"}}),
                createTiles(new String[][]{{"Pit", "4A"}}),
                createTiles(new String[][]{{"Pit", "4A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "top", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "4A"}})
        ));

        // Row 11
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Laser", "4A", "bottom"}, {"Wall", "4A", "top"}, {"CheckPoint", "ExtraCrispy", "1"}, {"count", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "left", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "left", "right"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Laser", "4A", "top"}, {"Wall", "4A", "bottom"}, {"CheckPoint", "ExtraCrispy", "3"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}})
        ));

        // Row 12
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Laser", "4A", "left"}, {"Wall", "4A", "right"}, {"count", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "left", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"EnergySpace", "4A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "bottom", "left"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"ConveyorBelt", "4A", "top", "bottom"}, {"speed", "2"}})
        ));

        //Row 13
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Wall", "4A", "top"}}),
                createTiles(new String[][]{{"Wall", "4A", "bottom"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}}),
                createTiles(new String[][]{{"Empty", "4A"}})
        ));


        // Set game map to the MessageBody
        MessageBody messageBody = new MessageBody();
        messageBody.setGameMap(gameMap);

        // Set MessageBody to the Message
        Message message = new Message();
        message.setMessageType("GameStarted");
        message.setMessageBody(messageBody);

        return gson.toJson(message);
    }

    public String createLostBearingsMap() {
        // Create the game map
        List<List<List<Tile>>> gameMap = new ArrayList<>();

        // Row 1
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"RestartPoint", "LostBearings", "right"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Antenna", "Start A", "right"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}})
        ));

        // Row 2
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "top"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "bottom"}}),
                createTiles(new String[][]{{"StartPoint", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}})
        ));

        // Row 3
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "Start A", "right", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Wall", "Start A", "right"}}),
                createTiles(new String[][]{{"Wall", "Start A", "right"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"Empty", "Start A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "Start A", "right", "left"}, {"speed", "1"}})
        ));

        // Row 4
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "left", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "right", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}})
        ));

        // Row 5
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "1A", "bottom", "top"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "left", "top"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}, {"CheckPoint", "LostBearings"}, {"count", "2"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "bottom", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "bottom", "top"}, {"speed", "1"}})
        ));

        // Row 6
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"EnergySpace", "1A"}, {"count", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Gear", "1A", "counterclockwise"}}),
                createTiles(new String[][]{{"Gear", "1A", "clockwise"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"EnergySpace", "1A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}})
        ));

        // Row 7
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "left", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Pit", "1A"}}),
                createTiles(new String[][]{{"Wall", "1A", "left"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Laser", "1A", "right"}, {"count", "1"}, {"Wall", "1A", "left"}}),
                createTiles(new String[][]{{"Pit", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}})
        ));

        // Row 8
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "left", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"EnergySpace", "1A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Gear", "1A", "clockwise"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}})
        ));

        // Row 9
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "right", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}, {"CheckPoint", "LostBearings", "3"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Gear", "1A", "counterclockwise"}}),
                createTiles(new String[][]{{"EnergySpace", "1A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}, {"CheckPoint", "LostBearings", "4"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}})
        ));

        // Row 10
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "right", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Pit", "1A"}}),
                createTiles(new String[][]{{"Laser", "1A", "left"}, {"count", "1"}, {"Wall", "1A", "right"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Wall", "1A", "right"}}),
                createTiles(new String[][]{{"Pit", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}})
        ));

        // Row 11
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"EnergySpace", "1A"}, {"count", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "bottom", "top"}, {"speed", "2"}}),
                createTiles(new String[][]{{"Gear", "1A", "clockwise"}}),
                createTiles(new String[][]{{"Gear", "1A", "counterclockwise"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "top", "bottom"}, {"speed", "2"}}),
                createTiles(new String[][]{{"EnergySpace", "1A"}, {"count", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}})
        ));

        // Row 12
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"ConveyorBelt", "1A", "top", "bottom"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "top", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}, {"CheckPoint", "LostBearings", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "right", "bottom"}, {"speed", "1"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "top", "bottom"}, {"speed", "1"}})
        ));
        // Row 13
        gameMap.add(Arrays.asList(
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "left", "right"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"Empty", "1A"}}),
                createTiles(new String[][]{{"ConveyorBelt", "1A", "right", "left"}, {"speed", "1"}}),
                createTiles(new String[][]{{"Empty", "1A"}})
        ));



        // Set game map to the MessageBody
        MessageBody messageBody = new MessageBody();
        messageBody.setGameMap(gameMap);

        // Set MessageBody to the Message
        Message message = new Message();
        message.setMessageType("GameStarted");
        message.setMessageBody(messageBody);

        return gson.toJson(message);
    }

    private static List<Tile> createTiles(String[][] tileInfo) {
        List<Tile> tiles = new ArrayList<>();
        for (String[] info : tileInfo) {
            Tile tile = new Tile();
            tile.setType(info[0]);
            tile.setIsOnBoard(info[1]);
            if (info.length > 2) {
                tile.setOrientations(Arrays.asList(Arrays.copyOfRange(info, 2, info.length)));
            }
            tiles.add(tile);
        }
        return tiles;
    }
}




