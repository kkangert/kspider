package top.kangert.kspider.model;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import top.kangert.kspider.executor.function.extension.WebElementWrapper;
import top.kangert.kspider.io.SeleniumResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WebElements extends ArrayList<WebElement> {

    private SeleniumResponse response;

    public WebElements() {
    }

    public WebElements(SeleniumResponse response, List<WebElement> elements) {
        super(elements == null ? Collections.emptyList() : elements.stream().map(e -> new WebElementWrapper(response, e)).collect(Collectors.toList()));
        this.response = response;
    }

    public List<String> html() {
        return attr("innerHTML");
    }

    public List<String> attr(String attr) {
        return this.stream().map(element -> element.getAttribute(attr)).collect(Collectors.toList());
    }

    public List<String> text() {
        return this.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public WebElements selectors(String css) {
        WebElements elements = new WebElements();
        this.stream().map(element -> findElements(element, By.cssSelector(css))).forEach(elements::addAll);
        return elements;
    }

    public WebElement selector(String css) {
        for (WebElement element : this) {
            WebElement ele = findElement(element, By.cssSelector(css));
            if (ele != null) {
                return ele;
            }
        }
        return null;
    }

    public WebElements xpaths(String xpath) {
        WebElements elements = new WebElements();
        this.stream().map(element -> findElements(element, By.xpath(xpath))).forEach(elements::addAll);
        return elements;
    }

    public WebElement xpath(String xpath) {
        for (WebElement element : this) {
            WebElement ele = findElement(element, By.xpath(xpath));
            if (ele != null) {
                return ele;
            }
        }
        return null;
    }

    private WebElement findElement(WebElement element, By by) {
        try {
            return new WebElementWrapper(this.response, element.findElement(by));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private List<WebElement> findElements(WebElement element, By by) {
        try {
            List<WebElement> elements = element.findElements(by);
            if (elements != null) {
                return elements.stream().map(ele -> new WebElementWrapper(this.response, ele)).collect(Collectors.toList());
            }
        } catch (NoSuchElementException ignored) {

        }
        return Collections.emptyList();
    }
}
