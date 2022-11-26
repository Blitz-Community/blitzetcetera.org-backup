Type Parent 
 Field Name$ 
 Field kid.child 
 Field spouse.parent 
End Type 
 
Type Child 
 Field Name$ 
 Field mom.parent,dad.parent 
End Type 
 
papa.parent=New parent 
mama.parent=New parent 
lamer.child=New child 
 
papa\name="papa" 
papa\spouse.parent=mama.parent 
papa\kid.child=lamer.child 
 
mama\name="mama" 
mama\spouse.parent=papa.parent 
mama\kid.child=lamer.child 
 
lamer\name="lamer" 
lamer\mom.parent=mama.parent 
lamer\dad.parent=papa.parent 
 
For c.child=Each child 
 DebugLog c\name+"'s dad is "+c\dad\name 
 DebugLog c\name+"'s mom is "+c\mom\name 
Next 
For p.parent=Each parent 
 DebugLog p\name+" is married to "+p\spouse\name+" ("+p\kid\name+" is there child)" 
Next 