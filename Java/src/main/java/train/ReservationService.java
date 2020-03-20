package train;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    // result in list of free seats
    public List<Seat> tryToReserve(final ReservationRequest request, final TrainData trainData) {
        List<Seat> seatsToReserve = new ArrayList<>();

        for (int i = 0; i < request.seatCount; i++) {
            for (Seat seat : trainData.getSeats()) {
                seatsToReserve.add(seat);
                break;
            }

        }

        return seatsToReserve;
    }

    // reservation is done in ticket office
}
