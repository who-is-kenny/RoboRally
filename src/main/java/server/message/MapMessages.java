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

//    public String createDeathTrap() {
        // Create the game map
//        List<List<List<Tile>>> gameMap = new ArrayList<>();


//        return gson.toJson(message);
//    }




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




