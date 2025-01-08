package content;

public enum OrientationEnum {
    U(0), D(180), R(90), L(270);

    private final int angle;

    OrientationEnum(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }

    public static OrientationEnum getFromString(String orientation) {
        switch (orientation) {
            case "right":
                return R;
            case "left":
                return L;
            case "up":
                return U;
            case "down":
                return D;
            default:
                return null;
        }
    }

    public static OrientationEnum matchOrientation(int angle) {
        switch (angle) {
            case 0:
                return U;
            case 90:
                return R;
            case 180:
                return D;
            case 270:
                return L;
        }
        return null;
    }

    public OrientationEnum getRight() {
        return matchOrientation((this.angle + 90) % 360);
}
    public OrientationEnum getLeft() {
        return matchOrientation((this.angle + 270) % 360);
    }

    public OrientationEnum getOpposite() {
        return matchOrientation((this.angle + 180) % 360);
    }

    @Override
    public String toString() {
        switch (this) {
            case U:
                return "up";
            case D:
                return "down";
            case R:
                return "right";
            case L:
                return "left";
            default:
                return ""; // Optional, though this case won't occur.
        }
    }
}
