package server.game.celltypes;

public class Antenna extends Cell {
        private static Antenna instance;

        public static Antenna getInstance(){
            if(instance == null){
                instance = new Antenna();
            }
            return instance;
        }
        private Antenna(){
            super(0,0);
        }
    }

