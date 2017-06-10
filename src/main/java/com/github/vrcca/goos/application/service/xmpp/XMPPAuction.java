package com.github.vrcca.goos.application.service.xmpp;

import com.github.vrcca.goos.domain.Auction;
import com.github.vrcca.goos.domain.AuctionEventListener;
import com.github.vrcca.goos.domain.utils.Announcer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";

    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; " +
            "Price: %d;";

    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private Chat chat;

    public XMPPAuction(final XMPPConnection connection, final String auctionJID, XMPPFailureReporter failureReporter) {
        final AuctionMessageTranslator translator = translatorFor(connection, failureReporter);
        chat = connection
                .getChatManager()
                .createChat(
                        auctionJID,
                        translator);
        addAuctionEventListener(chatDisconnectorFor(translator));
    }

    private AuctionMessageTranslator translatorFor(final XMPPConnection connection, XMPPFailureReporter failureReporter) {
        return new AuctionMessageTranslator(
                connection.getUser(),
                auctionEventListeners.announce(),
                failureReporter);
    }

    private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                // empty method
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {
                // empty method
            }

            @Override
            public void auctionFailed() {
                chat.removeMessageListener(translator);
            }
        };
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        this.auctionEventListeners.addListener(listener);
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    private void sendMessage(String text) {
        try {
            chat.sendMessage(text);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}