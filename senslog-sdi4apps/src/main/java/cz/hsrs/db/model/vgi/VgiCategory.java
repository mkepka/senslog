/**
 * 
 */
package cz.hsrs.db.model.vgi;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author mkepka
 *
 */
@XmlRootElement
public class VgiCategory {

	@XmlElement(name = "categoryID")
    private int categoryId;
    @XmlElement(name = "category_name")
    private String categoryName;
    private String description;
    @XmlElement(name = "parentID")
    private Integer parentId; // top-level categories has NULL
    private Integer level;
    private Integer lft;
    private Integer rgt;
    
    /**
     * Empty constructor
     */
    public VgiCategory(){
    }
    
    /**
     * Constructor creates instance from fields, for selecting from DB
     * @param categoryId - ID of VgiCategory, mandatory
     * @param categoryName - name of VgiCategory, optional
     * @param description - description of VgiCategory, optional
     * @param parentId - ID of parent VgiCategory, top-level VgiCategory has NULL parent
     * @param level - ID of level of VgiCategory, can be NULL, top-level has 0
     * @param lft - left value of preoder tree traversal, can be NULL
     * @param rgt - right value of preoder tree traversal, can be NULL
     */
    public VgiCategory(int categoryId, String categoryName, String description,
    		Integer parentId, Integer level, Integer lft, Integer rgt) {
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
     * @param categoryName - name of VgiCategory, optional
     * @param description - description of VgiCategory, optional
     * @param parentId - ID of parent VgiCategory, top-level VgiCategory has NULL parent
     */
    public VgiCategory(String categoryName, String description, Integer parentId) {
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
    public Integer getParentId() {
        return parentId;
    }

    /**
     * @return the level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * @return the lft
     */
    public Integer internalGetLft() {
        return lft;
    }

    /**
     * @return the rgt
     */
    public Integer internalGetRgt() {
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