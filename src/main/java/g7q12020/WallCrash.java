package g7q12020;

public class WallCrash implements Collision {
    final Particle a;
    final double time;
    Wall wall;

    public WallCrash(Particle a, Wall wall, double time){
        this.a = a;
        this.time = time;
        this.wall = wall;
    }

    public Particle getA() {
        return a;
    }

    public Wall getWall() {
        return wall;
    }

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public boolean isParticlesCollision() {
        return false;
    }

}
