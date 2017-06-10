package com.github.vrcca.goos;

import com.github.vrcca.goos.application.service.xmpp.XMPPAuctionHouse;
import com.github.vrcca.goos.application.view.ui.MainWindow;
import com.github.vrcca.goos.domain.AuctionHouse;
import com.github.vrcca.goos.domain.SniperLauncher;
import com.github.vrcca.goos.domain.SniperPortfolio;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static javax.swing.SwingUtilities.invokeAndWait;

public class Main {

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    private final SniperPortfolio portfolio = new SniperPortfolio();
    private MainWindow ui;

    public Main() throws Exception {
        invokeAndWait(() -> ui = new MainWindow(portfolio));
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        AuctionHouse auctionHouse = XMPPAuctionHouse.connect(
                args[ARG_HOSTNAME],
                args[ARG_USERNAME],
                args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));
    }

    private void disconnectWhenUICloses(AuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ((XMPPAuctionHouse) auctionHouse).disconnect();
            }
        });
    }
}
