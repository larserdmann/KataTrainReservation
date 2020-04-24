package train;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public class TrainData {

    private List<Seat> seats;

    public TrainData(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Seat> getSeats() {
        return seats;
    }

	public int getReservedSeatCount() {
		return getTotalSeatCount() - (int) seats.stream().filter(Seat::isFree).count();
	}

	public int getTotalSeatCount() {
		return seats.size();
	}

	public Seat getSeat(final int i) {
		return seats.get(i);
	}

	public List<Seat> getSeats(final int... indices) {
		return Arrays.stream(indices).boxed().map(this::getSeat).collect(toList());
    }
}
