package train;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    // result in list of free seats
    public List<Seat> tryToReserve(final ReservationRequest request, final TrainData trainData, String bookingReference) {

        if (trainData == null || isMaximumTrainThresholdExceeded(request, trainData)) {
            return emptyList();
        }

        List<Seat> seatsToReserve = new ArrayList<>();

        for (int i = 0; i < request.seatCount; i++) {
            for (Seat seat : trainData.getSeats()) {

                if(seat.isFree()) {
                    seatsToReserve.add(seat);
                    seat.bookingReference = bookingReference;
                    break;
                }
            }
        }

        return seatsToReserve;
    }

    private boolean isMaximumTrainThresholdExceeded(final ReservationRequest request, final TrainData trainData) {
        final int resultingSeatCount = request.seatCount + trainData.getReservedSeatCount();
        final double seventyPercentSeatCount = 0.7 * trainData.getTotalSeatCount();

        return resultingSeatCount > seventyPercentSeatCount;
    }

    // reservation is done in ticket office
}
