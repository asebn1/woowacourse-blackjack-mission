package blackjack.domain.player;

import blackjack.domain.card.Card;

import java.util.List;

public interface Player {

    int MAX_SCORE = 21;

    void addCard(final Card card);

    int calculateFinalScore();

    List<Card> getCards();

    String getName();
}
