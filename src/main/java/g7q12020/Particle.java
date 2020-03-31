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
    private boolean crashed;

    public Particle(long id, double x, double y, double speedModule, double angle, double radius, double mass) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.vx = speedModule*Math.cos(angle);
        this.vy = speedModule*Math.sin(angle);
        this.radius = radius;
        this.mass = mass;
        this.crashed = false;
    }

    public double getSpeedModule() {
        return Math.sqrt(Math.pow(vx,2)+Math.pow(vy, 2));
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

    public void invertVx() {
        vx = (-1)*vx;
    }

    public void invertVy() {
        vy = (-1)*vy;
    }

    public double getXat(double deltaT) {
        return this.x + this.vx*deltaT;
    }

    public double getYat(double deltaT) {
        return this.y + this.vy*deltaT;
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

    public boolean isCrashed() {
        return crashed;
    }

    public void setCrashed(boolean c) {
        crashed = c;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}