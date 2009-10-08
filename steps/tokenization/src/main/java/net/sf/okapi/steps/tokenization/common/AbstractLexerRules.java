/*===========================================================================
  Copyright (C) 2008-2009 by the Okapi Framework contributors
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

package net.sf.okapi.steps.tokenization.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

import net.sf.okapi.common.ParametersString;
import net.sf.okapi.lib.extra.AbstractParameters;

/**
 * Lexer rules
 * 
 * @version 0.1 06.07.2009
 */

public abstract class AbstractLexerRules extends AbstractParameters implements List<LexerRule> {
	
	private List<LexerRule> items = new ArrayList<LexerRule>();
	
	private TreeMap<Integer, LexerRule> idMap = new TreeMap<Integer, LexerRule>();
	
	public abstract Class<? extends LexerRule> getRuleClass();
	
	@Override
	protected void parameters_init() {
		
	}

	@Override
	public void parameters_reset() {

		if (items != null)
			items.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void parameters_load(ParametersString buffer) {
		
		loadGroup(buffer, "Rule", items, (Class<LexerRule>) getRuleClass());
		
		idMap.clear();
		
		for (LexerRule item : items)			
			idMap.put(item.getLexemId(), item);
	}
	
	@Override
	public void parameters_save(ParametersString buffer) {
		
		saveGroup(buffer, "Rule", items);
	}	
	
	/**
	 * Gets a lexer rule for the given lexem ID.
	 * @param lexemId ID of the rule.
	 * @return LexerRule object of null if no rule has been assigned to the given lexem ID.  
	 */
	public LexerRule getRule(int lexemId) {
		
		return idMap.get(lexemId);
	}

	
//	public List<LexerRule> getItems() {
//		
//		return items;
//	}	

	// List implementation 
	public boolean add(LexerRule o) {
		
		return items.add(o);
	}

	public void add(int index, LexerRule element) {
		
		items.add(index, element);
	}

	public boolean addAll(Collection<? extends LexerRule> c) {

		return items.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends LexerRule> c) {
		
		return items.addAll(index, c);
	}

	public void clear() {
		
		items.clear();
	}

	public boolean contains(Object o) {
		
		return items.contains(o);
	}

	public boolean containsAll(Collection<?> c) {

		return items.containsAll(c);
	}

	public LexerRule get(int index) {
		
		return items.get(index);
	}

	public int indexOf(Object o) {

		return items.indexOf(o);
	}

	public boolean isEmpty() {

		return items.isEmpty();
	}

	public Iterator<LexerRule> iterator() {
	
		return items.iterator();
	}

	public int lastIndexOf(Object o) {

		return items.lastIndexOf(o);
	}

	public ListIterator<LexerRule> listIterator() {

		return items.listIterator();
	}

	public ListIterator<LexerRule> listIterator(int index) {

		return items.listIterator(index);
	}

	public boolean remove(Object o) {
	
		return items.remove(o);
	}

	public LexerRule remove(int index) {

		return items.remove(index);
	}

	public boolean removeAll(Collection<?> c) {

		return items.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {

		return items.retainAll(c);
	}

	public LexerRule set(int index, LexerRule element) {

		return items.set(index, element);
	}

	public int size() {

		return items.size();
	}

	public List<LexerRule> subList(int fromIndex, int toIndex) {

		return items.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {

		return items.toArray();
	}

	public <T> T[] toArray(T[] a) {

		return items.toArray(a);
	}
}
