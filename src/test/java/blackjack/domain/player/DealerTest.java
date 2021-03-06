package blackjack.domain.player;

import blackjack.domain.card.Card;
import blackjack.domain.card.Deck;
import blackjack.domain.card.RandomGenerator;
import blackjack.domain.card.Score;
import blackjack.domain.card.Type;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DealerTest {

    @Test
    @DisplayName("딜러는 시작시 카드를 2장 받는다.")
    void checkParticipantCardSize() {
        Deck deck = new Deck(new RandomGenerator());
        List<Card> initCards = new ArrayList<>();
        initCards.add(deck.draw());
        initCards.add(deck.draw());
        Dealer dealer = new Dealer(initCards);

        assertThat(dealer.getCards().size()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("cardListAndAcceptable")
    @DisplayName("딜러는 자신의 점수가 16이하인지 확인한다.")
    void addParticipantCard(List<Card> cards, boolean acceptable) {
        Dealer dealer = new Dealer(cards);

        assertThat(dealer.acceptableCard()).isEqualTo(acceptable);
    }

    private static Stream<Arguments> cardListAndAcceptable() {
        return Stream.of(
                Arguments.of(List.of(
                        new Card(Type.SPADE, Score.EIGHT),
                        new Card(Type.HEART, Score.EIGHT)
                ), true),
                Arguments.of(List.of(
                        new Card(Type.SPADE, Score.EIGHT),
                        new Card(Type.HEART, Score.NINE)
                ), false),
                Arguments.of(List.of(
                        new Card(Type.SPADE, Score.ACE),
                        new Card(Type.HEART, Score.FIVE)
                ), true),
                Arguments.of(List.of(
                        new Card(Type.SPADE, Score.ACE),
                        new Card(Type.HEART, Score.SIX)
                ), false)
        );
    }

    @ParameterizedTest
    @MethodSource("dealerList")
    void calculateParticipantScore(List<Card> cards, Card addCard, int score) {
        Dealer dealer = new Dealer(cards);

        if (dealer.acceptableCard()) {
            dealer.addCard(addCard);
        }

        assertThat(dealer.calculateFinalScore()).isEqualTo(score);
    }

    private static Stream<Arguments> dealerList() {
        return Stream.of(
                Arguments.of(List.of(
                        new Card(Type.SPADE, Score.EIGHT),
                        new Card(Type.HEART, Score.EIGHT)
                ), new Card(Type.SPADE, Score.EIGHT), 24),
                Arguments.of(List.of(
                        new Card(Type.SPADE, Score.EIGHT),
                        new Card(Type.HEART, Score.NINE)
                ), new Card(Type.SPADE, Score.EIGHT), 17),
                Arguments.of(List.of(
                        new Card(Type.SPADE, Score.ACE),
                        new Card(Type.HEART, Score.ACE)
                ), new Card(Type.HEART, Score.NINE), 21),
                Arguments.of(List.of(
                        new Card(Type.SPADE, Score.ACE),
                        new Card(Type.HEART, Score.JACK)
                ), new Card(Type.HEART, Score.ACE), 21),
                Arguments.of(List.of(
                        new Card(Type.SPADE, Score.TWO),
                        new Card(Type.HEART, Score.EIGHT)
                ), new Card(Type.HEART, Score.ACE), 21)
        );
    }

    @Test
    @DisplayName("딜러는 참여자와 점수를 비교해 승패를 결정한다(딜러, 참가자 둘다 블랙잭).")
    void calculateBothBlackjack() {
        Participant participant = new Participant(List.of(
                new Card(Type.SPADE, Score.ACE),
                new Card(Type.HEART, Score.TEN)), "zero", new Bet(1000));
        Dealer dealer = new Dealer(List.of(
                new Card(Type.DIAMOND, Score.ACE),
                new Card(Type.CLOVER, Score.JACK)
        ));
        dealer.compete(participant);
        assertThat(participant.getBet().getProfit()).isEqualTo(0);
    }

    @Test
    @DisplayName("딜러는 참여자와 점수를 비교해 승패를 결정한다(참가자만 블랙잭).")
    void calculateBlackjack() {
        Participant participant = new Participant(List.of(
                new Card(Type.SPADE, Score.ACE),
                new Card(Type.HEART, Score.TEN)), "zero", new Bet(2000));
        Dealer dealer = new Dealer(List.of(
                new Card(Type.DIAMOND, Score.ACE),
                new Card(Type.CLOVER, Score.TWO)
        ));
        dealer.compete(participant);
        assertThat(participant.getBet().getProfit()).isEqualTo(3000);
    }

    @ParameterizedTest
    @MethodSource("participantAndResultWin")
    @DisplayName("딜러는 참여자와 점수를 비교해 승패를 결정한다(승리 테스트).")
    void calculateWinner(Participant participant, Dealer dealer, int result) {
        dealer.compete(participant);
        assertThat(participant.getBet().getProfit()).isEqualTo(result);
    }

    private static Stream<Arguments> participantAndResultWin() {
        return Stream.of(
                Arguments.of(
                        new Participant(List.of(
                                new Card(Type.SPADE, Score.EIGHT),
                                new Card(Type.HEART, Score.NINE)
                        ), "zero", new Bet(1000)), new Dealer(List.of(
                                new Card(Type.SPADE, Score.EIGHT),
                                new Card(Type.HEART, Score.EIGHT)
                        )), 1000),
                Arguments.of(
                        new Participant(List.of(
                                new Card(Type.SPADE, Score.TEN),
                                new Card(Type.HEART, Score.TEN)
                        ), "zero", new Bet(1000)), new Dealer(List.of(
                                new Card(Type.SPADE, Score.TEN),
                                new Card(Type.HEART, Score.NINE)
                        )), 1000)
        );
    }

    @ParameterizedTest
    @MethodSource("participantAndResultLose")
    @DisplayName("딜러는 참여자와 점수를 비교해 승패를 결정한다(패배 테스트).")
    void calculateLoser(Participant participant, Dealer dealer, int result) {
        dealer.compete(participant);
        assertThat(participant.getBet().getProfit()).isEqualTo(result);
    }

    private static Stream<Arguments> participantAndResultLose() {
        Participant participant = new Participant(List.of(
                new Card(Type.SPADE, Score.SIX),
                new Card(Type.HEART, Score.SIX)), "zero", new Bet(1000));
        participant.addCard(new Card(Type.HEART, Score.TWO));
        Dealer dealer = new Dealer(List.of(
                new Card(Type.SPADE, Score.ACE),
                new Card(Type.HEART, Score.FOUR)
        ));
        dealer.addCard(new Card(Type.DIAMOND, Score.KING));

        return Stream.of(
                Arguments.of(
                        new Participant(List.of(
                                new Card(Type.SPADE, Score.SEVEN),
                                new Card(Type.HEART, Score.EIGHT)
                        ), "zero", new Bet(1000)),
                        new Dealer(List.of(
                                new Card(Type.SPADE, Score.EIGHT),
                                new Card(Type.HEART, Score.EIGHT)
                        )),
                        -1000
                ),
                Arguments.of(
                        new Participant(List.of(
                                new Card(Type.SPADE, Score.EIGHT),
                                new Card(Type.HEART, Score.EIGHT)
                        ), "zero", new Bet(1000)),
                        new Dealer(List.of(
                                new Card(Type.SPADE, Score.EIGHT),
                                new Card(Type.HEART, Score.EIGHT)
                        )),
                        -1000
                ),
                Arguments.of(
                        new Participant(List.of(
                                new Card(Type.SPADE, Score.JACK),
                                new Card(Type.HEART, Score.TEN)
                        ), "zero", new Bet(1000)),
                        new Dealer(List.of(
                                new Card(Type.SPADE, Score.ACE),
                                new Card(Type.HEART, Score.JACK)
                        )),
                        -1000
                ),
                Arguments.of(
                        new Participant(List.of(
                                new Card(Type.SPADE, Score.TEN),
                                new Card(Type.HEART, Score.TEN)
                        ),"zero", new Bet(1000)),
                        new Dealer(List.of(
                                new Card(Type.SPADE, Score.TEN),
                                new Card(Type.HEART, Score.ACE)
                        )),
                        -1000
                ),
                Arguments.of(participant, dealer, -1000)
        );
    }
}
