package train;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationService {

    // result in list of free seats
    public List<Seat> tryToReserve(final ReservationRequest request, final TrainData trainData, String bookingReference) {

        if (trainData == null || isMaximumTrainThresholdExceeded(request, trainData)) {
            return emptyList();
        }

        Map<String, Integer> freeSeatsByCoach = countFreeSeatsByCoach(trainData);
        String targetCoach = computeFirstPossibleCoach(freeSeatsByCoach, request.seatCount);

        if (targetCoach == null) {
            return emptyList();
        }

        final List<Seat> seatsOfTargetCoach = trainData.getSeats().stream()
                .filter(seat -> seat.coach.equals(targetCoach))
                .collect(toList());

        return reserveFreeSeats(request, bookingReference, seatsOfTargetCoach);
    }

    private List<Seat> reserveFreeSeats(final ReservationRequest request, final String bookingReference, final List<Seat> seats) {
        List<Seat> seatsToReserve = new ArrayList<>();
        for (int i = 0; i < request.seatCount; i++) {
            for (Seat seat : seats) {
                if(seat.isFree()) {
                    seatsToReserve.add(seat);
                    seat.bookingReference = bookingReference;
                    break;
                }
            }
        }
        return seatsToReserve;
    }

    private String computeFirstPossibleCoach(final Map<String, Integer> freeSeatsByCoach, final int seatCount) {
        for (Map.Entry<String, Integer> entry : freeSeatsByCoach.entrySet()) {
            if (entry.getValue() >= seatCount) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Map<String, Integer> countFreeSeatsByCoach(final TrainData trainData) {
        Map<String, Integer> freeSeatsByCoach = new HashMap<>();
        for (Seat seat : trainData.getSeats()) {
            if (seat.isFree()) {
                freeSeatsByCoach.compute(seat.coach, (coach, count) -> count == null ? 1 : count+1);
            }
        }
        return freeSeatsByCoach;
    }

    private boolean isMaximumTrainThresholdExceeded(final ReservationRequest request, final TrainData trainData) {
        final int resultingSeatCount = request.seatCount + trainData.getReservedSeatCount();
        final double seventyPercentSeatCount = 0.7 * trainData.getTotalSeatCount();

        return resultingSeatCount > seventyPercentSeatCount;
    }

    // reservation is done in ticket office
}
