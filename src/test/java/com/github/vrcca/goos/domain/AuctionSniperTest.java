package com.github.vrcca.goos.domain;

import com.github.vrcca.goos.domain.AuctionEventListener.PriceSource;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.github.vrcca.goos.domain.SniperState.BIDDING;
import static com.github.vrcca.goos.domain.SniperState.LOSING;
import static com.github.vrcca.goos.domain.SniperState.LOST;
import static com.github.vrcca.goos.domain.SniperState.WINNING;
import static com.github.vrcca.goos.domain.SniperState.WON;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class AuctionSniperTest {

    private static final String ITEM_ID = "item-54321";

    @Mock
    private SniperListener sniperListener;

    @Mock
    private Auction auction;
    private AuctionSniper sniper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        sniper = new AuctionSniper(new Item(ITEM_ID, 168), auction);
        sniper.addSniperListener(sniperListener);
    }

    @Test
    public void reportsLostIfAuctionClosesImmediately() throws Exception {
        // when
        sniper.auctionClosed();

        // then
        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(aSniperThatHas(LOST)));
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() throws Exception {
        // when
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        // then
        verify(sniperListener).sniperStateChanged(argThat(aSniperThatIs(BIDDING)));

        // when
        sniper.auctionClosed();
        // then
        verify(sniperListener).sniperStateChanged(argThat(aSniperThatHas(LOST)));
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
        // given
        final int price = 10;
        final int increment = 25;
        final int bid = price + increment;

        // when
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

        // then
        verify(sniperListener).sniperStateChanged(argThat(aSniperThatIs(BIDDING)));
        verify(auction, times(1)).bid(bid);
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() throws Exception {
        // when
        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);

        // then
        verify(sniperListener).sniperStateChanged(argThat(aSniperThatIs(BIDDING)));

        // when
        sniper.currentPrice(135, 45, PriceSource.FromSniper);

        // then
        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(aSniperThatIs(WINNING)));
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() throws Exception {
        // when
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        // then
        verify(sniperListener).sniperStateChanged(argThat(aSniperThatIs(WINNING)));

        // when
        sniper.auctionClosed();
        // then
        verify(sniperListener).sniperStateChanged(argThat(aSniperThatHas(WON)));
    }

    @Test
    public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() throws Exception {
        // when
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        // then
        int bid = 123 + 45;
        verify(auction).bid(bid);
        verify(sniperListener, times(1)).sniperStateChanged(argThat(aSniperThatIs(BIDDING)));

        // when
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
        // then
        verify(sniperListener, times(1)).sniperStateChanged(argThat(aSniperThatIs(LOSING)));
        verifyNoMoreInteractions(auction);
    }

    @Test
    public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() throws Exception {
        // when
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
        // then
        verify(sniperListener, times(1)).sniperStateChanged(argThat(aSniperThatIs(LOSING)));
        verifyNoMoreInteractions(auction);
    }

    @Test
    public void reportsFailedIfAuctionFailsWhenBidding() throws Exception {
        // when
        sniper.currentPrice(123,    45, PriceSource.FromOtherBidder);
        // then
        verify(sniperListener, times(1)).sniperStateChanged(argThat(aSniperThatIs(BIDDING)));

        // when
        sniper.auctionFailed();
        // then
        verify(sniperListener, times(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, SniperState.FAILED));
    }

    private Matcher<SniperSnapshot> aSniperThatHas(final SniperState state) {
        return aSniperThatIs(state);
    }

    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(
                equalTo(state),
                "sniper that is ",
                "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }

}