// rule types that drive HTML parser behavior
enum RULE_TYPE {INLINE, GROUP, EXCLUDE, INCLUDE, TEXTUNIT, PRESERVE_WHITESPACE, SCRIPT, SERVER}

// inline tags
a {
	ruleTypes = [INLINE]
	translatableAttributes = ['title']
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
	ruleTypes = [INLINE, GROUP]
}

acronym {
	ruleTypes = [INLINE]
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
} 

ins {
	ruleTypes = [INLINE]
} 

acronym {
	ruleTypes = [INLINE]
} 

kbd {
	ruleTypes = [INLINE]
}

label {
	ruleTypes = [INLINE]
} 

map {
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

span {
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

u {
	ruleTypes = [INLINE]
}

// Ruby inline tags
ruby {
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
















