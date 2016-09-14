/**
 * 
 */
package cz.hsrs.db.model.vgi;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author mkepka
 *
 */
@XmlRootElement
public class VgiCategory {

    private int categoryId;
    private String categoryName;
    private String description;
    private int parentId;
    private int level;
    private int lft;
    private int rgt;
    
    /**
     * Empty constructor
     */
    public VgiCategory(){
    }
    
    /**
     * @param categoryId
     * @param categoryName
     * @param description
     * @param parentId
     * @param level
     * @param lft
     * @param rgt
     */
    public VgiCategory(int categoryId, String categoryName, String description,
            int parentId, int level, int lft, int rgt) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.parentId = parentId;
        this.level = level;
        this.lft = lft;
        this.rgt = rgt;
    }

    /**
     * Constructor for insert new Category by user
     * @param categoryName
     * @param description
     * @param parentId
     */
    public VgiCategory(String categoryName, String description, int parentId) {
        this.categoryName = categoryName;
        this.description = description;
        this.parentId = parentId;
    }

    /**
     * @return the categoryId
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the parentId
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the lft
     */
    public int internalGetLft() {
        return lft;
    }

    /**
     * @return the rgt
     */
    public int internalGetRgt() {
        return rgt;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CategoryVgi [categoryId=" + categoryId + ", categoryName="
                + categoryName + ", description=" + description + ", parentId="
                + parentId + ", level=" + level + ", lft=" + lft + ", rgt="
                + rgt + "]";
    }
}