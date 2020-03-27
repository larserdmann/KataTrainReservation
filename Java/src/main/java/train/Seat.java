package train;

public class Seat {
    public final String coach;
    public final int seatNumber;
    public String bookingReference;

    public Seat(String coach, int seatNumber, String bookingReference) {
        this.coach = coach;
        this.seatNumber = seatNumber;
        this.bookingReference = bookingReference;
    }

    public boolean equals(Object o) {
        Seat other = (Seat) o;
        return coach == other.coach && seatNumber == other.seatNumber;
    }

    public boolean isFree() {
        return bookingReference == null;
    }
}