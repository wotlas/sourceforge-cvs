package wotlas.utils;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

/**
 * see http://www.javaworld.com/javatips/javatip109/javatip109.zip
 */

public class MyHTMLEditorKit extends HTMLEditorKit {

    /**
     */
    @Override
    public ViewFactory getViewFactory() {
        return new HTMLFactoryX();
    }

    /**
     */
    public static class HTMLFactoryX extends HTMLFactory implements ViewFactory {

        @Override
        public View create(Element elem) {
            Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.IMG)
                    return new MyImageView(elem);
            }
            return super.create(elem);
        }
    }

}
