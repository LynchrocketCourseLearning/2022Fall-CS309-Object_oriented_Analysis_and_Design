import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class WhiteRandomBall extends Ball implements Subject<Ball> {
    List<Ball> observers = new ArrayList<Ball>();

    public WhiteRandomBall(Color color, int xSpeed, int ySpeed, int ballSize) {
        super(color, xSpeed, ySpeed, ballSize);
    }

    @Override
    public void registerObserver(Ball ball) {
        this.observers.add(ball);
    }

    @Override
    public void removeObserver(Ball ball) {
        this.observers.remove(ball);
    }

    @Override
    public void notifyObservers(char keyChar) {

    }

    @Override
    public void notifyObservers() {
        observers.forEach(o -> o.setVisible(this.isIntersect(o)));
    }

    @Override
    public void update(char keyChar) {
        switch (keyChar) {
            case 'a':
                this.setXSpeed(-8);
                break;
            case 'd':
                this.setXSpeed(8);
                break;
            case 'w':
                this.setYSpeed(-8);
                break;
            case 's':
                this.setYSpeed(8);
                break;
        }
    }

    @Override
    public void update(int x, int y) {

    }
}
