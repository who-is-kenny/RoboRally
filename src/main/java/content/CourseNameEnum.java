package content;

public enum CourseNameEnum {
    DIZZY_HIGHWAY("DizzyHighway"),
    LOST_BEARINGS("LostBearings"),
    EXTRA_CRISPY("ExtraCrispy"),
    DEATH_TRAP("DeathTrap");


    private final String courseName;

    CourseNameEnum(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    /**
     * returns enum depending on Course name sent by client. Coded for Client
     * @param courseName
     * @return
     */
    public static CourseNameEnum getFromString(String courseName) {
        switch (courseName){
            case "DizzyHighway":
                return CourseNameEnum.DIZZY_HIGHWAY;
            case "LostBearings":
                return CourseNameEnum.LOST_BEARINGS;
            case "ExtraCrispy":
                return CourseNameEnum.EXTRA_CRISPY;
            case "DeathTrap":
                return CourseNameEnum.DEATH_TRAP;
            default:
                return null;
        }
    }
}
