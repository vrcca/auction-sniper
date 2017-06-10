package com.github.vrcca.goos.application.service.xmpp;

import com.github.vrcca.goos.domain.Auction;
import com.github.vrcca.goos.domain.AuctionHouse;
import com.github.vrcca.goos.domain.Item;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionHouse implements AuctionHouse {

    private static final String AUCTION_RESOURCE = "Auction";
    private final XMPPConnection connection;

    public XMPPAuctionHouse(final String hostname,
                            final String username,
                            final String password) throws XMPPException {
        this.connection = connection(hostname, username, password);
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, AUCTION_RESOURCE, item.identifier);
    }

    public static AuctionHouse connect(String host, String user, String password) throws XMPPException {
        return new XMPPAuctionHouse(host, user, password);
    }

    private XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    public void disconnect() {
        this.connection.disconnect();
    }
}
