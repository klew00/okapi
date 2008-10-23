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

a:graphic {
  ruleTypes = [EXCLUDE]
}

pic:cnvpr {
	ruleTypes = [INCLUDE,INLINE]
	translatableAttributes = ['name']
}

w:t {
  ruleTypes = [TEXTUNIT]
}

wp:docpr {
	ruleTypes = [ATTRIBUTE]
	onlyTheseElements = ['name']
}

wp:drawing {
  ruleTypes = [EXCLUDE]
}





