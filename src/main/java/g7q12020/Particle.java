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

    public double getTimeToVerticalWallCollision(double l) {
        return getTimeToWallCollision(l, x, radius, vx);
    }

    private double getTimeToWallCollision(double length, double pos, double radius, double vel) {
        return Math.abs((vel > 0) ? ((length - radius - pos)/vel ) : ((radius - pos)/vel));

    }

    public double getTimeToHorizontalWallCollision(double length) {
        return getTimeToWallCollision(length, y, radius, vy);
    }

    public double getTimeToParticleCollision(Particle q) {
        double time;
        double dRadius = radius + q.getRadius();
        double Δx = x -q.getX();
        double Δvx = vx-q.getVx();
        double Δy = y-q.getY();
        double Δvy = vy-q.getVy();
        double ΔvΔr = (Δvy*Δy)+(Δvx*Δx);

        if (ΔvΔr >= 0) return Double.MAX_VALUE;

        double ΔrΔr = Math.pow(Δy, 2) + Math.pow(Δx, 2);
        double ΔvΔv = Math.pow(Δvy, 2) + Math.pow(Δvx, 2);
        double d = Math.pow(ΔvΔr, 2) - ΔvΔv * (ΔrΔr - Math.pow(dRadius, 2));

        if (d < 0) return Double.MAX_VALUE;

        time = -(ΔvΔr + Math.sqrt(d))/ΔvΔv;

        return time;
    }
}