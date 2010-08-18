/**
 * 
 */
package wotlas.common;

/**
 * Unique identifier among the properties and characteristics used in Wotlas framework.
 * @author SleepingOwl
 *
 */
public class WotlasId {
    /**
     * Unique short name that can be used in different objects and means the same thing.
     */
    private final String name;

    /**
     * Unique id that makes a reference to such characteristic. Must be as unique as its name.
     */
    private final int id;

    /**
     * @param name
     * @param id
     */
    public WotlasId(int id, String name) {
        super();
        this.name = name;
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.id;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WotlasId other = (WotlasId) obj;
        if (this.id != other.id)
            return false;
        if (this.name == null) {
            if (other.name != null)
                return false;
        } else if (!this.name.equals(other.name))
            return false;
        return true;
    }

}
