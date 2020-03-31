package g7q12020;

public class Crash {
    final Particle a;
    final Particle b;
    final double time;
    Double j;
    Wall wall;

    public Crash(Particle a, Wall wall, double time){
        this.a = a;
        this.b = null;
        this.time = time;
        this.wall = wall;
        this.j = null;
    }

    public Crash(Particle a, Particle b, double time) {
        this.a = a;
        this.b = b;
        this.time = time;
        this.wall = null;
        this.j = null;
    }

    public double jx() {
        if (j == null) {
            double dVx = b.getVx() - a.getVx();
            double dVy = b.getVy() - a.getVy();

            double dx =  b.getX() - a.getX();
            double dy = b.getY() - a.getY();

            double vr =  dVx*dx + dVy*dy;
            j = 2*a.getMass()*b.getMass()*vr;
            j /= (a.getRadius() + b.getRadius())*(a.getMass() + b.getMass());
        }
        return (j*(b.getX()-a.getX()))/(a.getRadius() + b.getRadius());
    }

    public double jy() {
        if (j == null) {
            double dVx = b.getVx() - a.getVx();
            double dVy = b.getVy() - a.getVy();

            double dx =  b.getX() - a.getX();
            double dy = b.getY() - a.getY();

            double vr =  dVx*dx + dVy*dy;
            j = 2*a.getMass()*b.getMass()*vr;
            j /= (a.getRadius() + b.getRadius())*(a.getMass() + b.getMass());
        }
        return (j*(b.getY()-a.getY()))/(a.getRadius() + b.getRadius());
    }

    public Particle getA() {
        return a;
    }

    public Particle getB() {
        return b;
    }

    public Wall getWall() {
        return wall;
    }

    public double getTime() {
        return time;
    }
}
