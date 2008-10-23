//Rule types that drive HTML parser behavior
INLINE = 1  // inline element
GROUP = 2 // group element
EXCLUDE = 3 // exclude this element and all children
INCLUDE = 4 // exceptions to the exclude rules
TEXTUNIT = 5 // make this element a textunit with skeleton before/after
PRESERVE_WHITESPACE = 6 // turn on preserve whitespace.
SCRIPT = 7 // Embedded scripting languatge - pass to another extractor
SERVER = 8 // Embedded server language tags such as JSP, PHP, Mason etc.
ATTRIBUTE = 9 // the rule lists an attribute, not an element
ATTRIBUTES_ONLY = 10 // only attribute is translatable or localizable 

/********************************************************************************************* 
Operators for attribute value compare

Rules are of the form: 
TO_EXTRACT_ATTRIBUTE:[IF_HAS_ATTRIBUTE, OPERATOR, VALUE]
'content':['http-equiv', EQUALS, 'keywords']

This rule would read:
extract the value of 'content' if the value of 'http-equiv' equals 'keywords'

Multiple attribute values may be included in a list:
'content':['http-equiv', EQUALS, ['content-language', 'content-type']

This rule would be read: 
extract the value of 'content' if the value of 'http-equiv' equals 'content-language' or 'content-type' 
********************************************************************************************/
EQUALS = 1
NOT_EQUALS = 2 
MATCH = 3 // regex match. Must match the entire attribute value


pic_cnvpr {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['name']
}

wp_docpr {
	ruleTypes = [ATTRIBUTE]
	onlyTheseElements = ['name']
}

w_b {
  ruleTypes = [INLINE]
}

w_bcs {
  ruleTypes = [INLINE]
}

w_bdr {
  ruleTypes = [INLINE]
}

w_color {
  ruleTypes = [INLINE]
}

w_commentrangeend {
  ruleTypes = [INLINE]
}

w_commentrangestart {
  ruleTypes = [INLINE]
}

w_commentreference {
  ruleTypes = [INLINE]
}

w_endnotereference {
  ruleTypes = [INLINE]
}

w_fldchar {
  ruleTypes = [INLINE]
}

w_footnotereference {
  ruleTypes = [INLINE]
}

w_highlight {
  ruleTypes = [INLINE]
}

w_hps {
  ruleTypes = [INLINE]
}

w_hpsbasetext {
  ruleTypes = [INLINE]
}

w_hpsraise {
  ruleTypes = [INLINE]
}

w_i {
  ruleTypes = [INLINE]
}

w_instrtext {
  ruleTypes = [INLINE]
}

w_ics {
  ruleTypes = [INLINE]
}

w_lang {
  ruleTypes = [INLINE]
}

w_lid {
  ruleTypes = [INLINE]
}

w_noproof {
  ruleTypes = [INLINE]
}

w_position {
  ruleTypes = [INLINE]
}

w_r {
  ruleTypes = [INLINE]
}

w_rfonts {
  ruleTypes = [INLINE]
}

w_rpr {
  ruleTypes = [INLINE]
}

w_prooferr {
  ruleTypes = [INLINE]
}

w_rstyle {
  ruleTypes = [INLINE]
}

w_rt {
  ruleTypes = [INLINE]
}

w_ruby {
  ruleTypes = [INLINE]
}

w_rubyalign {
  ruleTypes = [INLINE]
}

w_rubybase {
  ruleTypes = [INLINE]
}

w_rubypr {
  ruleTypes = [INLINE]
}

w_strike {
  ruleTypes = [INLINE]
}

w_style {
  ruleTypes = [INLINE]
}

w_sz {
  ruleTypes = [INLINE]
}

w_szcs {
  ruleTypes = [INLINE]
}

w_t {
  ruleTypes = [INLINE]
}

w_u {
  ruleTypes = [INLINE]
}

w_vertalign {
  ruleTypes = [INLINE]
}



