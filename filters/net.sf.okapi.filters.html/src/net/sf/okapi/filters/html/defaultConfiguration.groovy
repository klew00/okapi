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

// attributes that occur on many elements
title  {
	ruleTypes = [ATTRIBUTE]
	onlyTheseElements = []
	allElementsExcept = ['base', 'basefront', 'head', 'html', 'meta', 'param', 'script', 'title']
}

// inline tags
a {
	ruleTypes = [INLINE]
	translatableAttributes = ['title', 'accesskey']
}

abbr {
	ruleTypes = [INLINE]
}

acronym {
	ruleTypes = [INLINE]
} 

acronym {
	ruleTypes = [INLINE]
} 

applet {
	ruleTypes = [INLINE]
	translatableAttributes = ['alt']
}

acronym {
	ruleTypes = [INLINE]
} 

area {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['accesskey', 'area']
} 

b {
	ruleTypes = [INLINE]
}

bdo {
	ruleTypes = [INLINE]
} 

big {
	ruleTypes = [INLINE]
}

blink {
	ruleTypes = [INLINE]
} 

br {
	ruleTypes = [INLINE]
} 

button {
	ruleTypes = [INLINE]
	translatableAttributes = ['accesskey', 'value']
} 

cite {
	ruleTypes = [INLINE]
} 

code {
	ruleTypes = [INLINE]
} 

del {
	ruleTypes = [INLINE]
}

dfn {
	ruleTypes = [INLINE]
} 

em {
	ruleTypes = [INLINE]
}

embed {
	ruleTypes = [INLINE]
} 

font {
	ruleTypes = [INLINE]
}

i {
	ruleTypes = [INLINE]
}

iframe {
	ruleTypes = [INLINE]
} 

img {
	ruleTypes = [INLINE]
	translatableAttributes = ['title', 'alt']
}

input {
	ruleTypes = [INLINE]
	translatableAttributes = ['accesskey', 'alt', 'value']
	// value if type equals button, default, submit, reset
	// title if type equals button, default, submit, reset
} 

ins {
	ruleTypes = [INLINE]
}

isindex {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['prompt']
}

acronym {
	ruleTypes = [INLINE]
} 

kbd {
	ruleTypes = [INLINE]
}

label {
	ruleTypes = [INLINE]
	translatableAttributes = ['accesskey']
} 

legend {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['accesskey']
}

li {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['value']
}

map {
	ruleTypes = [INLINE]
}

meta {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['content':['http-equiv', EQUALS, 'keywords'], 'name':null]
	localizableAttributes = ['content':['http-equiv', EQUALS, ['content-language', 'content-type']]]
}

nobr {
	ruleTypes = [INLINE]
} 

object {
	ruleTypes = [INLINE]
	translatableAttributes = ['standby']
} 

option {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['label', 'value']
}

optgroup {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['label']
}

param {
	ruleTypes = [INLINE]
	translatableAttributes = ['value']
} 

q {
	ruleTypes = [INLINE]
} 

s {
	ruleTypes = [INLINE]
}

samp {
	ruleTypes = [INLINE]
}

small {
	ruleTypes = [INLINE]
}

select {
	ruleTypes = [INLINE]
} 

span {
	ruleTypes = [INLINE]
}

spacer {
	ruleTypes = [INLINE]
} 

strike {
	ruleTypes = [INLINE]
}

strong {
	ruleTypes = [INLINE]
}

sub {
	ruleTypes = [INLINE]
}

sup {
	ruleTypes = [INLINE]
}

symbol {
	ruleTypes = [INLINE]
} 

table {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['summary']
} 

textarea {
	ruleTypes = [INLINE]
	translatableAttributes = ['accesskey']
} 

tt {
	ruleTypes = [INLINE]
} 

td {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['abbr']
} 

th {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['abbr']
} 

u {
	ruleTypes = [INLINE]
}

var {
	ruleTypes = [INLINE]
} 

wbr {
	ruleTypes = [INLINE]
} 

// Ruby inline tags
ruby {
	ruleTypes = [INLINE]
}

rb {
	ruleTypes = [INLINE]
} 

rt {
	ruleTypes = [INLINE]
}

rc {
	ruleTypes = [INLINE]
}

rp {
	ruleTypes = [INLINE]
}

rbc {
	ruleTypes = [INLINE]
}

rtc {
	ruleTypes = [INLINE]
}

// Robo help inline tags
symbol {
	ruleTypes = [INLINE]
}

face {
	ruleTypes = [INLINE]
}

// Excluded elements
style {
	ruleTypes = [EXCLUDE]
}

stylesheet {
	ruleTypes = [EXCLUDE]
}

// javascript etc.
script {
	ruleTypes = [SCRIPT]
}

// preserve whitespace
pre {
	ruleTypes = [PRESERVE_WHITESPACE]
}
















