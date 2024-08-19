import java.awt.*;

public class BlueBall extends Ball {
    public BlueBall(Color color, int xSpeed, int ySpeed, int ballSize) {
        super(color, xSpeed, ySpeed, ballSize);
    }

    @Override
    public void update(char keyChar) {
        this.setXSpeed(-1 * this.getXSpeed());
        this.setYSpeed(-1 * this.getYSpeed());
    }

    @Override
    public void update(int x, int y) {
//        int whiteRandomBallSize = 300;
//        int diffX = this.getX() - x;
//        int diffY = this.getY() - y;
//        double dis = (this.getBallSize() - whiteRandomBallSize) / 2.0;
//        this.setVisible((diffX * diffX) + (diffY * diffY) <= dis * dis);
    }
}
