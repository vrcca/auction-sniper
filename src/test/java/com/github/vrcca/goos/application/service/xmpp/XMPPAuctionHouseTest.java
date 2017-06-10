package com.github.vrcca.goos.application.service.xmpp;

import _support.application.service.FakeAuctionServer;
import _support.application.view.ApplicationRunner;
import com.github.vrcca.goos.domain.Auction;
import com.github.vrcca.goos.domain.AuctionEventListener;
import com.github.vrcca.goos.domain.AuctionHouse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static _support.application.view.ApplicationRunner.SNIPER_ID;
import static _support.application.view.ApplicationRunner.SNIPER_PASSWORD;
import static _support.application.view.ApplicationRunner.XMPP_HOSTNAME;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XMPPAuctionHouseTest {

    private final FakeAuctionServer server = new FakeAuctionServer("item-54321");
    private AuctionHouse auctionHouse;

    @Before
    public void setUp() throws Exception {
        auctionHouse = XMPPAuctionHouse.connect(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
        server.startSellingItem();
    }

    @After
    public void disconnectFromServer() throws Exception {
        ((XMPPAuctionHouse) auctionHouse).disconnect();
    }

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        // given
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = auctionHouse.auctionFor(server.getItemId());
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        // when
        auction.join();

        // then
        server.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        server.announceClosed();
        assertThat("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS), is(true));
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {
                // not implemented
            }
        };
    }
}