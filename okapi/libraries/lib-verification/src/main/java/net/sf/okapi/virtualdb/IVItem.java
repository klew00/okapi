/*===========================================================================
  Copyright (C) 2010 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  This library is free software; you can redistribute it and/or modify it 
  under the terms of the GNU Lesser General Public License as published by 
  the Free Software Foundation; either version 2.1 of the License, or (at 
  your option) any later version.

  This library is distributed in the hope that it will be useful, but 
  WITHOUT ANY WARRANTY; without even the implied warranty of 
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License 
  along with this library; if not, write to the Free Software Foundation, 
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

  See also the full LGPL text here: http://www.gnu.org/copyleft/lesser.html
===========================================================================*/

package net.sf.okapi.virtualdb;

public interface IVItem {

	public enum ItemType {
		DOCUMENT,
		SUB_DOCUMENT,
		GROUP,
		TEXT_UNIT
	};

	/**
	 * Gets the extraction id for this item. The extraction id is unique per document.
	 * @return the extraction id for this item.
	 */
	public String getId ();
	
	/**
	 * Gets the storage key for this item. This key is unique for each virtual repository.
	 * @return the key for this item.
	 */
	public long getKey ();
	
	/**
	 * Gets the resource name of this item. This name depends on the type of item and can be null.
	 * @return the resource name of the item (can be null).
	 */
	public String getName ();
	
	/**
	 * Gets the resource type of this item. This type depends on the type of item and can be null.
	 * @return the resource type of this item.
	 */
	public String getType ();
	
	/**
	 * Gets the parent item of this item.
	 * @return the parent item of this item, or null if it has no parent (e.g. for a IVDocument).
	 */
	public IVItem getParent ();
	
	/**
	 * Gets the next item (on the same level) of this item.
	 * @return the next item (on the same level) of this item, or null.
	 */
	public IVItem getNextSibling ();
	
	/**
	 * Gets the previous item (on the same level) of this item.
	 * @return the previous item (on the same level) of this item, or null.
	 */
	public IVItem getPreviousSibling ();
	
	/**
	 * Gets the first child item of this item.
	 * @return the first child item of this item, or null.
	 */
	public IVItem getFirstChild ();
	
	/**
	 * Gets the document containing this item.
	 * @return the document containing this item.
	 */
	public IVDocument getDocument ();
	
	/**
	 * Gets the type of item this item is (document, group, text unit, etc.)
	 * @return one of the ItemType values.
	 */
	public ItemType getItemType ();

	/**
	 * Saves into the repository the modifiable data associated with this virtual item.
	 */
	public void save ();

}
