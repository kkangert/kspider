package top.kangert.kspider.executor.function.extension;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import top.kangert.kspider.io.SeleniumResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * selenium-java库的WebElement对象包装类
 */
public class WebElementWrapper implements WebElement {

    private WebElement element;

    private SeleniumResponse response;

    public WebElementWrapper(SeleniumResponse response, WebElement element) {
        this.response = response;
        this.element = element;
    }

    @Override
    public void click() {
        element.click();
    }

    @Override
    public void submit() {
        element.submit();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        element.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        element.click();
    }

    @Override
    public String getTagName() {
        return element.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return element.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return element.isEnabled();
    }

    @Override
    public String getText() {
        return element.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = element.findElements(by);
        if (elements != null) {
            return elements.stream().map(element -> new WebElementWrapper(this.response, element)).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public WebElement findElement(By by) {
        return new WebElementWrapper(this.response, element.findElement(by));
    }

    @Override
    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    @Override
    public Point getLocation() {
        return element.getLocation();
    }

    @Override
    public Dimension getSize() {
        return element.getSize();
    }

    @Override
    public Rectangle getRect() {
        return element.getRect();
    }

    @Override
    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return element.getScreenshotAs(target);
    }

    public Actions action() {
        return this.response.action();
    }

    public void clearAction() {
        this.response.clearAction();
    }

    public WebElement element() {
        return this.element;
    }

    public Select select() {
        return new Select(this.element);
    }

    public SeleniumResponse getResponse() {
        return response;
    }

    public String html() {
        return element.getAttribute("innerHTML");
    }

    public String text() {
        return element.getAttribute("innerText");
    }

    public String css(String style) {
        return element.getCssValue(style);
    }

    public String attr(String value) {
        return element.getAttribute(value);
    }
}
