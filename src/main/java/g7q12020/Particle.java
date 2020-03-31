package g7q12020;

import java.util.Objects;

public class Particle {
    long id;
    double radius;
    double x;
    double y;
    double angle;
    double vx;
    double vy;
    double mass;
    private boolean isBig;

    public Particle(long id, double x, double y, double speedModule, double angle, double radius, double mass, boolean isBig) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.vx = speedModule*Math.cos(angle);
        this.vy = speedModule*Math.sin(angle);
        this.radius = radius;
        this.mass = mass;
        this.isBig = isBig;
    }

    public double getSpeedModule() {
        return Math.sqrt(Math.pow(vx,2)+Math.pow(vy, 2));
    }

    public void crash(double jx, double jy) {
        vx += jx/mass;
        vy += jy/mass;
    }

    public void crashVx (double j) {
        vx += j/mass;
    }

    public void crashVy (double j) {
        vy += j/mass;
    }

    public void moveX(double dt){
        x += vx*dt;
    }

    public void moveY(double dt){
        y += vy*dt;
    }

    public boolean isBig() {
        return isBig;
    }

    public void invertVx() {
        vx = (-1)*vx;
    }

    public void invertVy() {
        vy = (-1)*vy;
    }

    public long getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMass() {
        return mass;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}