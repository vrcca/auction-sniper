package com.github.vrcca.goos.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SniperLauncherTest {

    @Mock
    private AuctionHouse auctionHouse;

    @Mock
    private SniperCollector collector;

    @Captor
    private ArgumentCaptor<AuctionSniper> argumentCaptor;

    private SniperLauncher launcher;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        launcher = new SniperLauncher(auctionHouse, collector);
    }

    @Test
    public void addsNewSniperToCollectorAndThenJoinsAuction() throws Exception {
        // given
        final Item item = new Item("item 123", 111);

        final Auction auction = mock(Auction.class);
        when(auctionHouse.auctionFor(item)).thenReturn(auction);

        // when
        launcher.joinAuction(item);

        // then
        InOrder inOrder = Mockito.inOrder(auction, collector, auction);

        inOrder.verify(auction).addAuctionEventListener(argumentCaptor.capture());
        inOrder.verify(collector).addSniper(argumentCaptor.getValue());
        inOrder.verify(auction).join();
    }
}