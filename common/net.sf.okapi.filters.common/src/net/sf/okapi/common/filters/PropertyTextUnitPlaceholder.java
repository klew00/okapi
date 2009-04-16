package net.sf.okapi.common.filters;

import net.sf.okapi.common.HashCodeUtil;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.TextUnit;

/**
 * This class acts as a placeholder for both {@link Property}s and
 * {@link TextUnit}s that are found within tags. HTML and XML attributes are the
 * canonical case. Along with the attribute name, value and type this class
 * stores offset information for the name and value that can be used by the
 * {@link AbstractBaseFilter} to automatically generate proper attribute-based
 * {@link IResource}s
 */
/**
 * @author HargraveJE
 *
 */
/**
 * @author HargraveJE
 * 
 */
public class PropertyTextUnitPlaceholder implements Comparable<PropertyTextUnitPlaceholder> {
	public enum PlaceholderType {
		TRANSLATABLE, READ_ONLY_PROPERTY, WRITABLE_PROPERTY
	}

	private PlaceholderType type;

	private String name;
	private String value;
	private String mimeType;

	private int mainStartPos;
	private int mainEndPos;
	private int valueStartPos;
	private int valueEndPos;

	/**
	 * Constructor for {@link Property} only. All offsets are the same, useful
	 * for creating placeholders for read-only {@link Property}s
	 * 
	 * @param type
	 *            - a {@link PlaceholderType}
	 * @param name
	 *            - attribute name
	 * @param value
	 *            - attribute value
	 */
	public PropertyTextUnitPlaceholder(PlaceholderType type, String name, String value) {
		this(type, name, value, -1, -1, -1, -1);
	}

	/**
	 * Constructor for {@link Property} and {@link TextUnit} without a main
	 * offsets. This is useful for cases where values are not delimited by any
	 * formatting
	 * 
	 * @param type
	 *            - a {@link PlaceholderType}
	 * @param name
	 *            - attribute name
	 * @param value
	 *            - attribute value
	 * @param valueStartPos
	 *            - start offset of the value
	 * @param valueEndPos
	 *            - ending offset of the value
	 */
	public PropertyTextUnitPlaceholder(PlaceholderType type, String name, String value, int valueStartPos,
			int valueEndPos) {
		this(type, name, value, valueStartPos, valueEndPos, valueStartPos, valueEndPos);
	}

	/**
	 * Constructor for {@link Property} and {@link TextUnit} that are delimited
	 * by formatting (i.e., name="value"). The offset 'n' in name is the
	 * mainStartPos, the offset 'v' in value is the valueStartPos.
	 * 
	 * @param type
	 *            - a {@link PlaceholderType}
	 * @param name
	 *            - attribute name
	 * @param value
	 *            - attribute value
	 * @param mainStartPos
	 *            - start offset of the value delimiter
	 * @param mainEndPos
	 *            - end offset of the entire attribute
	 * @param valueStartPos
	 *            - start offset of the value
	 * @param valueEndPos
	 *            - ending offset of the value
	 */
	public PropertyTextUnitPlaceholder(PlaceholderType type, String name, String value, int mainStartPos,
			int mainEndPos, int valueStartPos, int valueEndPos) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.mainStartPos = mainStartPos;
		this.mainEndPos = mainEndPos;
		this.valueStartPos = valueStartPos;
		this.valueEndPos = valueEndPos;
	}

	/**
	 * Get the placeholder {@link PlaceholderType}.
	 * 
	 * @return one of TRANSLATABLE, READ_ONLY_PROPERTY, WRITABLE_PROPERTY
	 */
	public PlaceholderType getType() {
		return type;
	}

	/**
	 * Set the {@link PlaceholderType}
	 * 
	 * @param type
	 *            - the type, one of TRANSLATABLE, READ_ONLY_PROPERTY,
	 *            WRITABLE_PROPERTY
	 */
	public void setType(PlaceholderType type) {
		this.type = type;
	}

	/**
	 * Get the offset to the beginning of the attribute.
	 * 
	 * @return offset as int
	 */
	public int getMainStartPos() {
		return mainStartPos;
	}

	/**
	 * Set the offset to the beginning of the attribute.
	 * 
	 * @param mainStartPos
	 *            - the offset as int
	 */
	public void setMainStartPos(int mainStartPos) {
		this.mainStartPos = mainStartPos;
	}

	/**
	 * Get the ending offset of the attribute
	 * 
	 * @return offset as int
	 */
	public int getMainEndPos() {
		return mainEndPos;
	}

	/**
	 * Set the ending offset of the attribute
	 * 
	 * @param mainEndPos
	 *            - the ending offset as an int
	 */
	public void setMainEndPos(int mainEndPos) {
		this.mainEndPos = mainEndPos;
	}

	/**
	 * Set the attribute name
	 * 
	 * @param name
	 *            - the attribute name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the attribute name
	 * 
	 * @return the attribute name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the attribute value
	 * 
	 * @param value
	 *            - the attribute value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get the attribute value
	 * 
	 * @return the attribute value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Get the starting offset of the attribute value.
	 * 
	 * @return the starting offset as int
	 */
	public int getValueStartPos() {
		return valueStartPos;
	}

	/**
	 * Set the starting offset of the attribute value.
	 * 
	 * @param valueStartPos
	 *            - the start offset as int
	 */
	public void setValueStartPos(int valueStartPos) {
		this.valueStartPos = valueStartPos;
	}

	/**
	 * Get the ending offset of the attribute value
	 * 
	 * @return the ending offset as int
	 */
	public int getValueEndPos() {
		return valueEndPos;
	}

	/**
	 * Set the ending offset of the attribute value
	 * 
	 * @param valueEndPos
	 *            - the ending offset as int
	 */
	public void setValueEndPos(int valueEndPos) {
		this.valueEndPos = valueEndPos;
	}

	/**
	 * Set the attribute values mimetype
	 * 
	 * @param mimeType
	 *            the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Get the attribute values mimetype
	 * 
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Compare two {@link PropertyTextUnitPlaceholder}s. Compare is based in
	 * MainStartPos order only. Allows sorting of many
	 * {@link PropertyTextUnitPlaceholder}s in the order they appear in the
	 * input
	 * 
	 * @param aThat
	 *            - the {@link PropertyTextUnitPlaceholder} used to compare to
	 *            this object
	 */
	public int compareTo(PropertyTextUnitPlaceholder aThat) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;

		// this optimization is usually worthwhile, and can
		// always be added
		if (this.getMainStartPos() == aThat.getMainStartPos())
			return EQUAL;

		// primitive numbers follow this form
		if (this.getMainStartPos() < aThat.getMainStartPos())
			return BEFORE;
		if (this.getMainStartPos() > aThat.getMainStartPos())
			return AFTER;

		return EQUAL;
	}

	/**
	 * Define equality of state. Equality is based on the MainStartPos of the
	 * attribute.
	 * 
	 * @param aThat
	 *            - the {@link PropertyTextUnitPlaceholder} that is compared to
	 *            this object
	 */
	@Override
	public boolean equals(Object aThat) {
		if (this == aThat)
			return true;
		if (!(aThat instanceof PropertyTextUnitPlaceholder))
			return false;

		PropertyTextUnitPlaceholder that = (PropertyTextUnitPlaceholder) aThat;
		return (this.getMainStartPos() == that.getMainStartPos());
	}

	/**
	 * <b>A class that overrides equals must also override hashCode.</b> Return
	 * a hash code based on the MainStartPos only
	 */
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, this.getMainStartPos());
		return result;
	}

}
