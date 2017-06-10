package com.github.vrcca.goos.application.service.xmpp;

import com.github.vrcca.goos.domain.utils.Announcer;
import com.github.vrcca.goos.domain.Auction;
import com.github.vrcca.goos.domain.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {

    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/";

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";

    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; " +
            "Price: %d;";

    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private Chat chat;

    public XMPPAuction(XMPPConnection connection, String resource, String itemId) {
        chat = connection.getChatManager()
                .createChat(auctionId(itemId, connection, resource), null);

        chat.addMessageListener(
                new AuctionMessageTranslator(
                        connection.getUser(),
                        auctionEventListeners.announce()));
    }

    private static String auctionId(String itemId, XMPPConnection connection, String resource) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName()) + resource;
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