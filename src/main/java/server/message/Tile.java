package server.message;

import java.util.List;

public class Tile {
    private String type;
    private String isOnBoard;
    private List<String> orientations;  // List of orientations (e.g., "top", "right")
    private List<Integer> registers;    // List of registers (e.g., [2, 4])
    private Integer count;              // Count for Laser or other types

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsOnBoard() {
        return isOnBoard;
    }

    public void setIsOnBoard(String isOnBoard) {
        this.isOnBoard = isOnBoard;
    }

    public List<String> getOrientations() {
        return orientations;
    }

    public void setOrientations(List<String> orientations) {
        this.orientations = orientations;
    }

    public List<Integer> getRegisters() {
        return registers;
    }

    public void setRegisters(List<Integer> registers) {
        this.registers = registers;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
