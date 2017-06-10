package com.github.vrcca.goos.application.view.ui;

import com.github.vrcca.goos.domain.Item;
import com.github.vrcca.goos.domain.SniperPortfolio;
import com.github.vrcca.goos.domain.UserRequestListener;
import com.github.vrcca.goos.domain.utils.Announcer;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.HeadlessException;

public class MainWindow extends JFrame {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String APPLICATION_TITLE = "Auction Sniper";
    private static final String SNIPERS_TABLE_NAME = "Snipers Table";
    public static final String NEW_ITEM_ID_NAME = "newItemIdTextField";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "newItemStopPriceTextField";
    public static final String JOIN_BUTTON_NAME = "joinButton";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);
    private JTextField itemIdField;
    private JFormattedTextField stopPriceField;

    public MainWindow(SniperPortfolio portfolio) throws HeadlessException {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        System.setProperty("com.objogate.wl.keyboard", "Mac-GB");
        fillContentPane(makeSnipersTable(portfolio), makeControls());
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());

        controls.add(new JLabel("Item"));
        itemIdField = new JTextField();
        itemIdField.setColumns(10);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        controls.add(new JLabel("Stop Price"));
        stopPriceField = new JFormattedTextField(new NumberFormatter());
        stopPriceField.setColumns(10);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        controls.add(stopPriceField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(event ->
                userRequests.announce().joinAuction(new Item(itemId(), stopPrice())));
        controls.add(joinAuctionButton);
        return controls;
    }

    private String itemId() {
        return itemIdField.getText();
    }

    private int stopPrice() {
        return ((Number) stopPriceField.getValue()).intValue();
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(controls), BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.SOUTH);
    }

    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addPortfolioListener(model);

        final JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void addUserRequestListener(UserRequestListener listener) {
        userRequests.addListener(listener);
    }
}
