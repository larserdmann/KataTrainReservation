package train;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

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

    @Test @DisplayName("request with one seats and one seat is already reserved")
    void requestWithOneSeatsAndOneSeatIsAlreadyReserved() {
        // given
        final ReservationRequest request = new ReservationRequest("RB-123", 1);

        final Seat alreadyReservedSeat = new Seat("A", 0, "AFFE00");
        final Seat freeSeatOne = new Seat("A", 1, null);
        final Seat freeSeatTwo = new Seat("A", 2, null);

        final TrainData trainData = new TrainData(asList(alreadyReservedSeat, freeSeatOne, freeSeatTwo));

        // when
        final List<Seat> reservedSeats = reservationService.tryToReserve(request, trainData, "AFFE01");

        // then
        assertThat(reservedSeats).hasSize(1);
        assertThat(reservedSeats.get(0)).isIn(freeSeatOne, freeSeatTwo);

    }

    @Test @DisplayName("one reservation have to be in the same coach")
    void oneReservationHaveToBeInTheSameCoach() {
    	// given
        final ReservationRequest request = new ReservationRequest("RB-123", 2);

        final TrainData trainData = TrainDataBuilder.addFirstCoach().addReservedSeats(2).addFreeSeat()
                .addCoach().addFreeSeats(3)
                .build();

        // when
        final List<Seat> reservedSeats = reservationService.tryToReserve(request, trainData, "AFFE01");

    	// then
        List<Seat> coachTwoSeats = trainData.getSeats(3,4,5);

        assertThat(reservedSeats).hasSize(2);
        assertThat(reservedSeats.get(0)).isIn(coachTwoSeats);
        assertThat(reservedSeats.get(1)).isIn(coachTwoSeats);
        assertThat(reservedSeats.get(0)).isNotEqualTo(reservedSeats.get(1));
    }

    @Nested @DisplayName("failing request")
    class FailingRequest {

        @Nested @DisplayName("because of 70 % train limitation")
        class TrainLimitation {

            @Test @DisplayName("with two seats in four-seated train with three free seats.")
            void withTwoSeatsAndOneAlreadyReservedSeat() {
                // given
                final ReservationRequest request = new ReservationRequest("RB-123", 2);

                final Seat alreadyReservedSeat = new Seat("A", 0, "AFFE00");
                final Seat freeSeatOne = new Seat("A", 1, null);
                final Seat freeSeatTwo = new Seat("A", 2, null);
                final Seat freeSeatThree = new Seat("A", 3, null);

                final TrainData trainData = new TrainData(asList(alreadyReservedSeat, freeSeatOne, freeSeatTwo, freeSeatThree));

                // when
                final List<Seat> reservedSeats = reservationService.tryToReserve(request, trainData, "AFFE01");

                // then
                assertThat(reservedSeats).isEmpty();
            }

        }

    }

    private static class TrainDataBuilder{

        final List<Seat> seats;

        int nextSeatNumber;
        char nextCoachNumber;

        private TrainDataBuilder() {
            seats = new ArrayList<>();
            nextSeatNumber = 0;
            nextCoachNumber = 'A';
        }

        public static TrainDataBuilder addFirstCoach() {
            return  new TrainDataBuilder();
        }

        public TrainDataBuilder addCoach() {
            nextCoachNumber++;
            return this;
        }

        public TrainDataBuilder addFreeSeat() {
            seats.add(new Seat(String.valueOf(nextCoachNumber), nextSeatNumber, null));
            nextSeatNumber++;
            return this;
        }

        public TrainDataBuilder addFreeSeats(final int n) {
            IntStream.range(0,n).forEachOrdered(i -> addFreeSeat());
            return this;
        }

        public TrainDataBuilder addReservedSeat() {
            seats.add(new Seat(String.valueOf(nextCoachNumber), nextSeatNumber, "AFFE01"));
            nextSeatNumber++;
            return this;
        }

        public TrainDataBuilder addReservedSeats(final int n) {
            IntStream.range(0,n).forEachOrdered(i -> addReservedSeat());
            return this;
        }

        public TrainData build() {
            return new TrainData(seats);
        }

    }
}