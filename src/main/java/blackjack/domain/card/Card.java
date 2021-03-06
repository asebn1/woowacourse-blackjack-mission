package blackjack.domain.card;

import java.util.Objects;

public final class Card {

    private final Type type;
    private final Score score;

    public Card(final Type type, final Score score) {
        this.type = type;
        this.score = score;
    }

    public boolean isScoreAce() {
        return score == Score.ACE;
    }

    public Score getScore() {
        return score;
    }

    public Type getType() {
        return type;
    }

    public int getScoreAmount() {
        return score.getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        Card card = (Card) o;
        return type == card.type && score == card.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, score);
    }

    @Override
    public String toString() {
        return "Card{" +
                "type=" + type +
                ", score=" + score +
                '}';
    }
}
