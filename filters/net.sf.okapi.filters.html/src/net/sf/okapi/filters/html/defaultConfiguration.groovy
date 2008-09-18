//Rule types that drive HTML parser behavior
INLINE = 0  // inline element
GROUP = 2 // group element
ATTRIBUTE = 3 // the rule lists an attribute, not an element
ATTRIBUTES_ONLY = 4 // only attribute is translatable or localizable 
EXCLUDE = 5 // exclude this element and all children
INCLUDE = 6 // exceptions to the exclude rules
TEXTUNIT = 7 // make this element a textunit with skeleton before/after
PRESERVE_WHITESPACE = 8 // turn on preserve whitespace.
SCRIPT = 9 // Embedded scripting languatge - pass to another extractor
SERVER = 10 // Embedded server language tags such as JSP, PHP, Mason etc.

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

ALWAYS means the attribute is always extracted.

********************************************************************************************/
EQUALS = 0
NOT_EQUALS = 1 
MATCH = 3 // regex match. Must match the entire attribute value
ALWAYS = 4

// attributes that occur on many elements
title  {
	ruleTypes = [ATTRIBUTE]
	onlyTheseElements = []
	allElementsExcept = ['base', 'basefront', 'head', 'html', 'meta', 'param', 'script', 'title']
}

// inline tags
a {
	ruleTypes = [INLINE]
	translatableAttributes = ['title':ALWAYS, 'accesskey':ALWAYS]
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
	translatableAttributes = ['alt':ALWAYS]
}

acronym {
	ruleTypes = [INLINE]
} 

area {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['accesskey':ALWAYS, 'area':ALWAYS]
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
	translatableAttributes = ['accesskey':ALWAYS, 'value':ALWAYS]
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
	translatableAttributes = ['title':ALWAYS, 'alt':ALWAYS]
}

input {
	ruleTypes = [INLINE]
	translatableAttributes = ['accesskey':ALWAYS, 'alt':ALWAYS, 'value':ALWAYS]
} 

ins {
	ruleTypes = [INLINE]
}

isindex {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['prompt':ALWAYS]
}

acronym {
	ruleTypes = [INLINE]
} 

kbd {
	ruleTypes = [INLINE]
}

label {
	ruleTypes = [INLINE]
	translatableAttributes = ['accesskey':ALWAYS]
} 

legend {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['accesskey':ALWAYS]
}

li {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['value':ALWAYS]
}

map {
	ruleTypes = [INLINE]
}

meta {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['content':['http-equiv', EQUALS, 'keywords'], 'name':ALWAYS]
	localizableAttributes = ['content':['http-equiv', EQUALS, ['content-language', 'content-type']]]
}

nobr {
	ruleTypes = [INLINE]
} 

object {
	ruleTypes = [INLINE]
	translatableAttributes = ['standby':ALWAYS]
} 

option {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['label':ALWAYS, 'value':ALWAYS]
}

optgroup {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['label':ALWAYS]
}

param {
	ruleTypes = [INLINE]
	translatableAttributes = ['value':ALWAYS]
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
	translatableAttributes = ['summary':ALWAYS]
} 

textarea {
	ruleTypes = [INLINE]
	translatableAttributes = ['accesskey':ALWAYS]
} 

tt {
	ruleTypes = [INLINE]
} 

td {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['abbr':ALWAYS]
} 

th {
	ruleTypes = [ATTRIBUTES_ONLY]
	translatableAttributes = ['abbr':ALWAYS]
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
















