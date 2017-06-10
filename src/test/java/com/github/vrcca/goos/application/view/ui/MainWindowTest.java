package com.github.vrcca.goos.application.view.ui;

import _support.application.view.AuctionSniperDriver;
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
        final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<>(equalTo("item"), "join request");
        mainWindow.addUserRequestListener(buttonProbe::setReceivedValue);

        // when
        driver.startBiddingFor("item");

        // then
        driver.check(buttonProbe);
    }
}