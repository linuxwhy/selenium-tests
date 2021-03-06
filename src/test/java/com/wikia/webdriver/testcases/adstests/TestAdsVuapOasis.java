package com.wikia.webdriver.testcases.adstests;

import com.wikia.webdriver.common.contentpatterns.AdsContent;
import com.wikia.webdriver.common.core.Assertion;
import com.wikia.webdriver.common.core.annotations.NetworkTrafficDump;
import com.wikia.webdriver.common.core.url.Page;
import com.wikia.webdriver.common.dataprovider.ads.AdsDataProvider;
import com.wikia.webdriver.common.templates.TemplateNoFirstLoad;
import com.wikia.webdriver.pageobjectsfactory.componentobject.ad.VideoFanTakeover;
import com.wikia.webdriver.pageobjectsfactory.pageobject.adsbase.AdsBaseObject;

import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(
        groups = "AdsVuapDesktop"
)
public class TestAdsVuapOasis extends TemplateNoFirstLoad {

  private static final Dimension DESKTOP_SIZE = new Dimension(1920, 1080);
  private static final String URL_FIRSTQUARTILE = "ad_vast_point=firstquartile";
  private static final String URL_MIDPOINT = "ad_vast_point=midpoint";
  private static final int DELAY = 2;
  private static final int VIDEO_START_TIME = 0;

  @Test(
          dataProviderClass = AdsDataProvider.class,
          dataProvider = "adsVuapDesktop",
          groups = "AdsVideoClosesWhenFinishPlaysOasis"
  )
  public void adsVideoClosedAfterPlayingOasis(Page page, String slotName, String iframeId) {
    AdsBaseObject ads = new AdsBaseObject(driver, urlBuilder.getUrlForPage(page), DESKTOP_SIZE);
    scrollToSlot(slotName, ads);
    VideoFanTakeover videoFanTakeover = new VideoFanTakeover(driver, iframeId);

    videoFanTakeover.play();

    videoFanTakeover.waitForVideoStart(slotName);
    videoFanTakeover.waitForVideoPlayerHidden(slotName);
  }

  @Test(
          dataProviderClass = AdsDataProvider.class,
          dataProvider = "adsVuapDesktop",
          groups = "AdsImageClickedOpensNewPageOasis"
  )
  public void adsImageClickedOpensNewPageOasis(Page page, String slotName, String iframeId) {
    AdsBaseObject ads = new AdsBaseObject(driver, urlBuilder.getUrlForPage(page), DESKTOP_SIZE);
    scrollToSlot(slotName, ads);
    VideoFanTakeover videoFanTakeover = new VideoFanTakeover(driver, iframeId);

    videoFanTakeover.clickOnAdImage();

    String tabUrl = ads.switchToNewBrowserTab();
    videoFanTakeover.verifyFandomTabOpened(tabUrl);
  }

  @Test(
          dataProviderClass = AdsDataProvider.class,
          dataProvider = "adsVuapDesktop",
          groups = "AdsVuapVideoClosesWhenTapCloseButtonOasis"
  )
  public void adsVuapVideoClosesWhenTapCloseButtonOasis(Page page, String slotName, String iframeId) {
    AdsBaseObject ads = new AdsBaseObject(driver, urlBuilder.getUrlForPage(page), DESKTOP_SIZE);
    scrollToSlot(slotName, ads);
    VideoFanTakeover videoFanTakeover = new VideoFanTakeover(driver, iframeId);

    videoFanTakeover.play();

    videoFanTakeover.waitForVideoStart(slotName);

    videoFanTakeover.clickOnVideoCloseButon();

    videoFanTakeover.waitForVideoPlayerHidden(slotName);
  }

  @Test(
          dataProviderClass = AdsDataProvider.class,
          dataProvider = "adsVuapDesktop",
          groups = "AdsVuapCheckSlotSizesOasis"
  )
  public void adsVuapCheckSlotSizesOasis(Page page, String slotName, String iframeId) throws InterruptedException {
    AdsBaseObject ads = new AdsBaseObject(driver, urlBuilder.getUrlForPage(page), DESKTOP_SIZE);
    scrollToSlot(slotName, ads);
    VideoFanTakeover videoFanTakeover = new VideoFanTakeover(driver, iframeId);

    videoFanTakeover.waitforAdToLoad();
    double imageHeight = videoFanTakeover.getAdSlotHeight(slotName);

    videoFanTakeover.play();

    videoFanTakeover.waitForVideoStart(slotName);
    double videoHeight = videoFanTakeover.getAdVideoHeight(slotName);
    Assertion.assertTrue(videoFanTakeover.isVideoAdBiggerThanImageAdOasis(videoHeight, imageHeight ));

    videoFanTakeover.waitForVideoEnd(slotName);
    Assertion.assertTrue(videoFanTakeover.isImageAdInCorrectSize(imageHeight, slotName));
  }

  @NetworkTrafficDump
  @Test(
          dataProviderClass = AdsDataProvider.class,
          dataProvider = "adsVuapDesktop",
          groups = "AdsVuapTimeProgressingOasis"
  )
  public void adsVuapTimeProgressingOasis(Page page, String slotName, String iframeId) throws InterruptedException {
    networkTrafficInterceptor.startIntercepting();
    AdsBaseObject ads = new AdsBaseObject(driver, urlBuilder.getUrlForPage(page), DESKTOP_SIZE);
    scrollToSlot(slotName, ads);
    VideoFanTakeover videoFanTakeover = new VideoFanTakeover(driver, iframeId);

    videoFanTakeover.play();

    videoFanTakeover.waitForVideoStart(slotName);
    ads.wait.forSuccessfulResponse(networkTrafficInterceptor, URL_FIRSTQUARTILE);
    double quartileTime = videoFanTakeover.getCurrentVideoTimeOnDesktop(slotName).doubleValue();

    ads.wait.forSuccessfulResponse(networkTrafficInterceptor, URL_MIDPOINT);
    double midTime = videoFanTakeover.getCurrentVideoTimeOnDesktop(slotName).doubleValue();
    Assertion.assertTrue(videoFanTakeover.isTimeProgressing(quartileTime, midTime));
  }

  @NetworkTrafficDump
  @Test(
          dataProviderClass = AdsDataProvider.class,
          dataProvider = "adsVuapDesktop",
          groups = "AdsVuapVideoPauseOasis"
  )
  public void adsVuapVideoPauseOasis(Page page, String slotName, String iframeId) throws InterruptedException {
    networkTrafficInterceptor.startIntercepting();
    AdsBaseObject ads = new AdsBaseObject(driver, urlBuilder.getUrlForPage(page), DESKTOP_SIZE);
    scrollToSlot(slotName, ads);
    VideoFanTakeover videoFanTakeover = new VideoFanTakeover(driver, iframeId);

    videoFanTakeover.play();

    videoFanTakeover.waitForVideoStart(slotName);
    ads.wait.forSuccessfulResponse(networkTrafficInterceptor, URL_FIRSTQUARTILE);

    videoFanTakeover.pause();

    double time = videoFanTakeover.getCurrentVideoTimeOnDesktop(slotName).doubleValue();

    Thread.sleep(DELAY * 1000);

    Assert.assertNotEquals(VIDEO_START_TIME, videoFanTakeover.getCurrentVideoTimeOnDesktop(slotName).doubleValue());
    Assert.assertEquals(time, videoFanTakeover.getCurrentVideoTimeOnDesktop(slotName).doubleValue());
  }

  private void scrollToSlot(String slotName, AdsBaseObject ads) {
    if (slotName == AdsContent.BOTTOM_LB) {
      ads.triggerComments();
    }
  }
}
