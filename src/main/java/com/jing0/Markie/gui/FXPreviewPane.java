package com.jing0.Markie.gui;

import com.jing0.Markie.gui.preferences.PreferencesManager;
import com.jing0.Markie.parser.MarkieProcessor;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.pegdown.Extensions;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jackie
 * @date 2/11/16
 */
public class FXPreviewPane extends JFXPanel implements Observer {

    private WebView preview = null;
    private WebEngine webEngine = null;
    private final MarkieProcessor markieProcessor = new MarkieProcessor(Extensions.ALL);
    private String htmlFormat = "";
    private String inputContent = "";
    private int scrollBarPosition = 0;

    public FXPreviewPane() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                preview = new WebView();
                preview.getStyleClass().add("browser");
                setScene(new Scene(preview));
                webEngine = preview.getEngine();
            }
        });

        loadConfigs();
    }

    public void loadConfigs() {
        PreferencesManager cssPrefs = new PreferencesManager(PreferencesManager.CSS_NODE);
        String cssSetting = cssPrefs.getStringInNode(PreferencesManager.CSS, "GitHub2");

        PreferencesManager generalPrefs = new PreferencesManager(PreferencesManager.GENERAL_NODE);
        boolean enableMath = generalPrefs.getBooleanInNode(PreferencesManager.ENABLE_MATH, false);
        boolean enableHighlight = generalPrefs.getBooleanInNode(PreferencesManager.ENABLE_SYNTAX_HIGHLIGHT, false);

        StringBuilder htmlFormatBuilder = new StringBuilder();

        htmlFormatBuilder.append("<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<meta charset=\"utf-8\">\n" +
                        "<link rel=\"stylesheet\" " +
                        "href=\"file:" + OsCheck.getAppDataDirectory() + "/Markie/css/" + cssSetting + ".css" + "\">"
        );

        if (enableHighlight) {
            htmlFormatBuilder.append("<link rel=\"stylesheet\" " +
                    "href=\"file:" + OsCheck.getAppDataDirectory() + "/Markie/prism/themes/" + "prism" + ".css" + "\">");
        }

        htmlFormatBuilder.append(
                "</head>" +
                        "<body>" +
                        "%s"
        );

        if (enableHighlight) {
            htmlFormatBuilder.append("<script type=\"text/javascript\">\n" +
                    "var _self=\"undefined\"!=typeof window?window:\"undefined\"!=typeof WorkerGlobalScope&&self instanceof WorkerGlobalScope?self:{},Prism=function(){var e=/\\blang(?:uage)?-(?!\\*)(\\w+)\\b/i,t=_self.Prism={util:{encode:function(e){return e instanceof n?new n(e.type,t.util.encode(e.content),e.alias):\"Array\"===t.util.type(e)?e.map(t.util.encode):e.replace(/&/g,\"&amp;\").replace(/</g,\"&lt;\").replace(/\\u00a0/g,\" \")},type:function(e){return Object.prototype.toString.call(e).match(/\\[object (\\w+)\\]/)[1]},clone:function(e){var n=t.util.type(e);switch(n){case\"Object\":var a={};for(var r in e)e.hasOwnProperty(r)&&(a[r]=t.util.clone(e[r]));return a;case\"Array\":return e.map&&e.map(function(e){return t.util.clone(e)})}return e}},languages:{extend:function(e,n){var a=t.util.clone(t.languages[e]);for(var r in n)a[r]=n[r];return a},insertBefore:function(e,n,a,r){r=r||t.languages;var l=r[e];if(2==arguments.length){a=arguments[1];for(var i in a)a.hasOwnProperty(i)&&(l[i]=a[i]);return l}var o={};for(var s in l)if(l.hasOwnProperty(s)){if(s==n)for(var i in a)a.hasOwnProperty(i)&&(o[i]=a[i]);o[s]=l[s]}return t.languages.DFS(t.languages,function(t,n){n===r[e]&&t!=e&&(this[t]=o)}),r[e]=o},DFS:function(e,n,a){for(var r in e)e.hasOwnProperty(r)&&(n.call(e,r,e[r],a||r),\"Object\"===t.util.type(e[r])?t.languages.DFS(e[r],n):\"Array\"===t.util.type(e[r])&&t.languages.DFS(e[r],n,r))}},plugins:{},highlightAll:function(e,n){for(var a,r=document.querySelectorAll('code[class*=\"language-\"], [class*=\"language-\"] code, code[class*=\"lang-\"], [class*=\"lang-\"] code'),l=0;a=r[l++];)t.highlightElement(a,e===!0,n)},highlightElement:function(n,a,r){for(var l,i,o=n;o&&!e.test(o.className);)o=o.parentNode;o&&(l=(o.className.match(e)||[,\"\"])[1],i=t.languages[l]),n.className=n.className.replace(e,\"\").replace(/\\s+/g,\" \")+\" language-\"+l,o=n.parentNode,/pre/i.test(o.nodeName)&&(o.className=o.className.replace(e,\"\").replace(/\\s+/g,\" \")+\" language-\"+l);var s=n.textContent,u={element:n,language:l,grammar:i,code:s};if(!s||!i)return t.hooks.run(\"complete\",u),void 0;if(t.hooks.run(\"before-highlight\",u),a&&_self.Worker){var g=new Worker(t.filename);g.onmessage=function(e){u.highlightedCode=e.data,t.hooks.run(\"before-insert\",u),u.element.innerHTML=u.highlightedCode,r&&r.call(u.element),t.hooks.run(\"after-highlight\",u),t.hooks.run(\"complete\",u)},g.postMessage(JSON.stringify({language:u.language,code:u.code,immediateClose:!0}))}else u.highlightedCode=t.highlight(u.code,u.grammar,u.language),t.hooks.run(\"before-insert\",u),u.element.innerHTML=u.highlightedCode,r&&r.call(n),t.hooks.run(\"after-highlight\",u),t.hooks.run(\"complete\",u)},highlight:function(e,a,r){var l=t.tokenize(e,a);return n.stringify(t.util.encode(l),r)},tokenize:function(e,n){var a=t.Token,r=[e],l=n.rest;if(l){for(var i in l)n[i]=l[i];delete n.rest}e:for(var i in n)if(n.hasOwnProperty(i)&&n[i]){var o=n[i];o=\"Array\"===t.util.type(o)?o:[o];for(var s=0;s<o.length;++s){var u=o[s],g=u.inside,c=!!u.lookbehind,f=0,h=u.alias;u=u.pattern||u;for(var p=0;p<r.length;p++){var d=r[p];if(r.length>e.length)break e;if(!(d instanceof a)){u.lastIndex=0;var m=u.exec(d);if(m){c&&(f=m[1].length);var y=m.index-1+f,m=m[0].slice(f),v=m.length,k=y+v,b=d.slice(0,y+1),w=d.slice(k+1),P=[p,1];b&&P.push(b);var A=new a(i,g?t.tokenize(m,g):m,h);P.push(A),w&&P.push(w),Array.prototype.splice.apply(r,P)}}}}}return r},hooks:{all:{},add:function(e,n){var a=t.hooks.all;a[e]=a[e]||[],a[e].push(n)},run:function(e,n){var a=t.hooks.all[e];if(a&&a.length)for(var r,l=0;r=a[l++];)r(n)}}},n=t.Token=function(e,t,n){this.type=e,this.content=t,this.alias=n};if(n.stringify=function(e,a,r){if(\"string\"==typeof e)return e;if(\"Array\"===t.util.type(e))return e.map(function(t){return n.stringify(t,a,e)}).join(\"\");var l={type:e.type,content:n.stringify(e.content,a,r),tag:\"span\",classes:[\"token\",e.type],attributes:{},language:a,parent:r};if(\"comment\"==l.type&&(l.attributes.spellcheck=\"true\"),e.alias){var i=\"Array\"===t.util.type(e.alias)?e.alias:[e.alias];Array.prototype.push.apply(l.classes,i)}t.hooks.run(\"wrap\",l);var o=\"\";for(var s in l.attributes)o+=(o?\" \":\"\")+s+'=\"'+(l.attributes[s]||\"\")+'\"';return\"<\"+l.tag+' class=\"'+l.classes.join(\" \")+'\" '+o+\">\"+l.content+\"</\"+l.tag+\">\"},!_self.document)return _self.addEventListener?(_self.addEventListener(\"message\",function(e){var n=JSON.parse(e.data),a=n.language,r=n.code,l=n.immediateClose;_self.postMessage(t.highlight(r,t.languages[a],a)),l&&_self.close()},!1),_self.Prism):_self.Prism;var a=document.getElementsByTagName(\"script\");return a=a[a.length-1],a&&(t.filename=a.src,document.addEventListener&&!a.hasAttribute(\"data-manual\")&&document.addEventListener(\"DOMContentLoaded\",t.highlightAll)),_self.Prism}();\"undefined\"!=typeof module&&module.exports&&(module.exports=Prism),\"undefined\"!=typeof global&&(global.Prism=Prism);\n" +
                    "</script>");
        }

        if (enableMath) {
            htmlFormatBuilder.append("<script type=\"text/javascript\" " +
                    "src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js" +
                    "?config=TeX-AMS-MML_HTMLorMML\"></script>"
            );
        }

        htmlFormatBuilder.append("</body>" +
                "</html>");

        htmlFormat = htmlFormatBuilder.toString();

        //TODO 输出测试显示,这里执行了两次,不知为什么,同时每次也执行一次reload

        reload();
    }

    /**
     * Updates the content of the label by converting the input data to html and setting them to the label.
     * <p>
     * This method will be called by an {@code InputTextArea} observable.
     * </p>
     *
     * @param o    the observable element which will notify this class.
     * @param data a String object containing the input data to be converted into HTML.
     */
    @Override
    public void update(final Observable o, final Object data) {
        if (o instanceof InputTextArea) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!(data instanceof String)) {
                        return;
                    }
                    inputContent = (String) data;
                    reload();
                }
            });
        } else if (o instanceof InputScrollPane) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    final int[] maxVal = new int[1];
                    maxVal[0] = (Integer) webEngine.executeScript("document.body.scrollHeight");
                    scrollBarPosition = (int) ((Double) data * maxVal[0]);
                    adjustScrollBar();
                }
            });

        }
    }

    public void reload() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String regex = "src=\"(?!http:|https:|ftp:|file:)";

                String htmlContent = String.format(htmlFormat,
                        markieProcessor.markdownToHtml(inputContent).replaceAll(regex, "src=\"file:"));

                webEngine.loadContent(htmlContent);
                System.out.println(htmlContent);
                /**
                 * (?<=\"language-)[^\"]+
                 * matches code language names
                 */
                Pattern pattern = Pattern.compile("(?<=\\\"language-)[^\\\"]+");
                Matcher matcher = pattern.matcher(htmlContent);
                if (matcher.find()) {
                    System.out.println(matcher.groupCount());
                    System.out.println(matcher.toMatchResult());
                    System.out.println(matcher.toString());
                }
/*                if () {
                    for () {
                        webEngine.executeScript();
                    }
                }*/
            }
        });
    }

    public void adjustScrollBar() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int currentValue = (Integer) webEngine.executeScript("document.body.scrollTop");
                if (currentValue != scrollBarPosition) {
                    webEngine.executeScript("window.scrollTo( 0, " + scrollBarPosition + ")");
                }
            }
        });
    }

}
