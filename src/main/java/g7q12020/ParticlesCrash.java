package g7q12020;

public class ParticlesCrash implements Collision {
    final Particle a;
    final Particle b;
    final double time;
    Double j;

    public ParticlesCrash(Particle a, Particle b, double time) {
        this.a = a;
        this.b = b;
        this.time = time;
        this.j = null;
    }

    public void crash() {
        a.crash(jx(), jy());
        b.crash(jx()*(-1), jy()*(-1));
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

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public boolean isParticlesCollision() {
        return true;
    }

}
