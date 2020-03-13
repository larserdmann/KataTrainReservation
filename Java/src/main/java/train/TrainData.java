package train;

import java.util.List;

public class TrainData {

    private List<Seat> seats;

    public TrainData(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Seat> getSeats() {
        return seats;
    }
}
