package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    private final Invoice invoice;
    private final Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public Map<String, Play> getPlays() {
        return plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        final int totalAmount = getTotalAmount();

        final int volumeCredits = getTotalVolumeCredits();

        final StringBuilder result = new StringBuilder("Statement for " + getInvoice().getCustomer() +
                System.lineSeparator());
        for (Performance p : getInvoice().getPerformances()) {
            final Play play = getPlays().get(p.getPlayID());
            final int thisAmount = getThisAmount(p, play);
            result.append(String.format("  %s: %s (%s seats)%n", play.getName(), usd(thisAmount), p.getAudience()));
        }
        result.append(String.format("Amount owed is %s%n", usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));

        return result.toString();
    }

    private int getTotalVolumeCredits() {
        int volumeCredits = 0;
        for (Performance p : getInvoice().getPerformances()) {
            volumeCredits += getVolumeCredits(p, getPlays().get(p.getPlayID()));
        }
        return volumeCredits;
    }

    private int getTotalAmount() {
        int totalAmount = 0;
        for (Performance p : getInvoice().getPerformances()) {
            totalAmount += getThisAmount(p, getPlays().get(p.getPlayID()));
        }
        return totalAmount;
    }

    public int getAmount(Performance p) {
        return getThisAmount(p, getPlays().get(p.getPlayID()));
    }

    public Play getPlay(Performance p) {
        return getPlays().get(p.getPlayID());
    }

    private static int getVolumeCredits(Performance p, Play play) {
        int result = Math.max(p.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        if ("comedy".equals(play.getType())) {
            result += p.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    private static int getThisAmount(Performance p, Play play) {
        int thisAmount;
        switch (play.getType()) {
            case "tragedy":
                thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                if (p.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON *
                            (p.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case "comedy":
                thisAmount = Constants.COMEDY_BASE_AMOUNT;
                if (p.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (p.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * p.getAudience();
                break;
            default:
                throw new RuntimeException(String.format("unknown type: %s", play.getType()));
        }
        return thisAmount;
    }

    private static String usd(int amount){
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount / Constants.PERCENT_FACTOR);
    }
}
