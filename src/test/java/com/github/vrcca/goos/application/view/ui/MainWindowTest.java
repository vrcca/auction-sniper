package com.github.vrcca.goos.application.view.ui;

import _support.application.view.AuctionSniperDriver;
import com.github.vrcca.goos.domain.Item;
import com.github.vrcca.goos.domain.SniperPortfolio;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {

    private MainWindow mainWindow = new MainWindow(new SniperPortfolio());
    private AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() throws Exception {
        // given
        final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<>(
                equalTo(new Item("item", 789)),
                "join request");
        mainWindow.addUserRequestListener(itemProbe::setReceivedValue);

        // when
        driver.startBiddingFor("item", 789);

        // then
        driver.check(itemProbe);
    }
}