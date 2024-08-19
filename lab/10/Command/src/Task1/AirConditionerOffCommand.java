package Task1;

public class AirConditionerOffCommand implements Command {
    AirConditioner airConditioner;

    public AirConditionerOffCommand(AirConditioner airConditioner) {
        this.airConditioner = airConditioner;
    }

    @Override
    public void execute() {

    }
}
