package com.github.vrcca.goos;

import _support.application.service.FakeAuctionServer;
import _support.application.view.ApplicationRunner;
import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {

    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperBidsForMultipleItems() throws Exception {
        // given
        auction.startSellingItem();
        auction2.startSellingItem();
        // when
        application.startBiddingIn(auction, auction2);
        // then
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        // when
        auction.reportPrice(1000, 98, "other bidder");
        // then
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        // when
        auction2.reportPrice(500, 21, "other bidder");
        // then
        auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);

        // when
        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);
        // then
        application.hasShownSniperIsWinning(auction, 1098);
        application.hasShownSniperIsWinning(auction2, 521);

        // when
        auction.announceClosed();
        auction2.announceClosed();

        // then
        application.showsSniperHasWonAuction(auction, 1098);
        application.showsSniperHasWonAuction(auction2, 521);
    }

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        // given
        auction.startSellingItem();
        // when
        application.startBiddingIn(auction);
        // then
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        // when
        auction.announceClosed();
        // then
        application.showsSnipperHasLostAuction();
    }

    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        // when
        auction.startSellingItem();
        application.startBiddingIn(auction);
        //then
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        // when
        auction.reportPrice(1000, 98, "other bidder");
        // then
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        // when
        auction.announceClosed();
        // then
        application.showsSnipperHasLostAuction();
    }

    @Test
    public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
        // when
        auction.startSellingItem();
        application.startBiddingIn(auction);
        //then
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        // when
        auction.reportPrice(1000, 98, "other bidder");
        // then
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        // when
        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        // then
        application.hasShownSniperIsWinning(auction, 1098);

        // when
        auction.announceClosed();
        // then
        application.showsSniperHasWonAuction(auction, 1098);
    }

    @After
    public void stopAuction() throws Exception {
        auction.stop();
    }

    @After
    public void stopApplication() throws Exception {
        application.stop();
    }
}
