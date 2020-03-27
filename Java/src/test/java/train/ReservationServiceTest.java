package train;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReservationService")
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    // Rule 1:  train -> no more than 70% of seats may be reserved in advance
    // Rule 2:  coach -> reservation only if already reserved seats < 70%
    // Rule 3:  one reservation have to be in the same coach

    @InjectMocks
    ReservationService reservationService;

    @Test
    @DisplayName("request with zero seats results in empty reservation list.")
    void requestWithZeroSeatsResultsInEmptyReservationList() {
        // given
        final ReservationRequest request = new ReservationRequest("RB-123", 0);
        final TrainData trainData = null;

        // when
        final List<Seat> reservedSeats = reservationService.tryToReserve(request, trainData, "AFFE00");

        // then
        assertThat(reservedSeats).isEmpty();
    }

    @Test
    @DisplayName("request with one seat in an empty two-seated train results in single reservation")
    void requestWithOneSeatInAnEmptyTwoSeatedTrainResultsInSingleReservation() {
        // given
        final ReservationRequest request = new ReservationRequest("RB-123", 1);
        final List<Seat> freeSeats = asList(new Seat("A", 0, null),
                new Seat("A", 1, null));
        final TrainData trainData = new TrainData(freeSeats);

        // when
        final List<Seat> reservedSeats = reservationService.tryToReserve(request, trainData, "AFFE00");

        // then
        assertThat(reservedSeats).hasSize(1);
        assertThat(freeSeats).contains(reservedSeats.get(0));
    }

    @Test
    @DisplayName("request with two seats in single reservation")
    void requestWithTwoSeatsInSingleReservation() {
        // given
        final ReservationRequest request = new ReservationRequest("RB-123", 2);
        final List<Seat> freeSeats = asList(new Seat("A", 0, null),
                new Seat("A", 1, null),
                new Seat("A", 2, null));
        final TrainData trainData = new TrainData(freeSeats);

        // when
        final List<Seat> reservedSeats = reservationService.tryToReserve(request, trainData, "AFFE00");

        // then
        Condition<Seat> condition = new Condition<>(freeSeats::contains, "free seats contains ...");

        assertThat(reservedSeats).hasSize(2);
        assertThat(reservedSeats.get(0)).has(condition);
        assertThat(reservedSeats.get(1)).has(condition);
        assertThat(reservedSeats.get(0)).isNotEqualTo(reservedSeats.get(1));

        // equivalent
        assertThat(reservedSeats)
                .has(new Condition<>(freeSeats::containsAll, "some description"))
                .has(new Condition<>(seats -> new HashSet<>(seats).size() == seats.size(), "some other desc."));
    }

    @Test
    @DisplayName("")
    void requestWithThreeSeatsInTwoCoaches() {
    }
}