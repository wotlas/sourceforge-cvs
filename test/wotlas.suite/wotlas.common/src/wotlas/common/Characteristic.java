/**
 * 
 */
package wotlas.common;

/**
 * Any object that have specific properties that needs to be evaluate before being used.
 * Use classical java property as String, int, short if the property never changes as soon as it is set.
 * Use Characteristic if you need a property that can be changed upon some conditions or time elapsed.
 * 
 * @author SleepingOwl
 *
 */
public class Characteristic {

    private final WotlasId id;

    private String textValue;

    private int intValue;

    /**
     * @param id unique identifier of the property among all the objects that can used it.
     * @param textValue default value when representing in text
     * @param intValue default value when representing as integer
     */
    public Characteristic(WotlasId id, String textValue, int intValue) {
        super();
        this.id = id;
        this.textValue = textValue;
        this.intValue = intValue;
    }

}
