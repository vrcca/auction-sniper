package com.github.vrcca.goos.application.service.xmpp;

import com.github.vrcca.goos.application.infrastructure.LoggingXMPPFailureReporter;
import com.github.vrcca.goos.domain.Auction;
import com.github.vrcca.goos.domain.AuctionHouse;
import com.github.vrcca.goos.domain.Item;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.apache.commons.io.FilenameUtils.getFullPath;

public class XMPPAuctionHouse implements AuctionHouse {

    public static final String LOG_FILE_NAME = "auction-sniper.log";

    private static final String LOGGER_NAME = "auctionSniper";
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/";

    private final XMPPConnection connection;
    private final XMPPFailureReporter failureReporter;

    public XMPPAuctionHouse(final String hostname,
                            final String username,
                            final String password) throws XMPPAuctionException, XMPPException {
        this.connection = connection(hostname, username, password);
        this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
    }

    private XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private Logger makeLogger() throws XMPPAuctionException {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        return logger;
    }

    private FileHandler simpleFileHandler() throws XMPPAuctionException {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (Exception e) {
            throw new XMPPAuctionException(" Could not create logger FileHandler " + getFullPath(LOG_FILE_NAME), e);
        }
    }


    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, auctionId(item.identifier, connection), failureReporter);
    }


    private String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName()) + AUCTION_RESOURCE;
    }

    public static AuctionHouse connect(String host, String user, String password) throws XMPPException, XMPPAuctionException {
        return new XMPPAuctionHouse(host, user, password);
    }

    public void disconnect() {
        this.connection.disconnect();
    }
}
