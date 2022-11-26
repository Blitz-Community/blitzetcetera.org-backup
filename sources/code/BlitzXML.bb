;============================= BlitzXML =================================

;BlitzXML is an XML (eXtendable Markup Language) function library for
;blitz. You don't even need to have any knowledge of the syntax of XML
;to use BlitzXML, although an understanding of the terms and structure
;is helpful.

;At the user's (programmer's) point of view,
;BlitzXML is a way of storing bits of data similar to the way folders
;are stored on a hard drive. For example an node (item) named "inventory" may contain
;child nodes (sub-items) such as, "key". Each node may have an unlimited
;amount of sub-nodes, and each sub-node may have sub-sub-nodes, etc.
;Each node may have a name, and a number of attributes. Nodes may
;contain both sub-nodes (called children), and text data. To get a
;better idea how xml works, look at "example.xml".

;When a data structure is constructed with BltizXML, it may be saved
;to a file using XML syntax, which is really just a more strict form of
;HTML. XML files may also be loaded and parsed into BlitzXML just as
;easily. Due to the flexibility of XML, this library should be very
;useful for level editors, games that load levels from XML files,
;or any program requiring structured Or
;complex data to be saved to and loaded from a file.

;========================================================================



;**** Constant Declarations =============================================

Const MAX_ATTRIBUTES = 32	;The maximum number of attributes a xmlNode may have
Const MAX_ERRORS = 64		;The maximum number of errors and warnings that will be processed until the xml parser aborts the operation
Const PARSER_RECURSE = 1024 ;The maximum number of virtually "recursive" steps for the parser.

;**** Type Declarations =================================================

;xmlNode Type - This is the main building block of BlitzXML. All xml data
;is stored with this Type, which gets manipulated by the user. The xml data
;can then be loaded from and saved to files.
Type xmlNode
	Field Name$								;The name of the node
	Field AttributeCount					;The number of attributes for the node
	Field AttributeName$[MAX_ATTRIBUTES]	;The attribute name array
	Field AttributeValue$[MAX_ATTRIBUTES]	;The attribute value array
	Field Contents$							;The data contents of the node
	Field Parent.xmlNode					;This node's parent node. If this is set to Null, then this node is the "root" node
	Field Level								;This is the node's level
	
	;These fields are manipulated by xml_RegisterChild(), xml_GetChild(), and xml_UnregisterChild()
	Field ChildCount						;The number of the node's children
	Field ChildBank							;A memory bank of handles to the node's children
End Type


;**** Global Declarations ===============================================

Global xml_Error$[MAX_ERRORS]
Global xml_ErrorPos[MAX_ERRORS]
Global xml_ErrorCount


;**** Interface Functions ===============================================
;Interface functions are the functions the user (the programmer) uses

;in their program, unlike the internal functions, which are only called
;by these functions.

;This function deletes ALL xml nodes and their data
Function xmlReset()
	For this.xmlNode = Each xmlNode
		FreeBank this\ChildBank
		Delete this
	Next
End Function

;This function gets the "root" node. The root node is the node at the
;topmost level, which has no parent or siblings - only children. If
;no root node could be found, 0 is returned. If a 0 is returned from
;this function, then there is no xml node data in memory.
Function xmlRoot()
	For this.xmlNode = Each xmlNode
		If this\Level = 0 Then Return Handle(this)
	Next
	Return 0
End Function

;This function returns the level the node is at. If the node is the
;root node, it is at level 0. If it is a child of the root node,
;the level will be 1. If it is a child of a child of the root node,
;the level of 2 will be returned, etc.
Function xmlNodeLevel(Node)
	this.xmlNode = Object.xmlNode(Node)
	Return this\Level
End Function

;This returns a node's parent node. If the node has no parent (if it's
;the root node), 0 will be returned.
Function xmlNodeParent(Node)
	this.xmlNode = Object.xmlNode(Node)
	If this\Parent = Null Then Return 0
	Return Handle(this\Parent)
End Function

;This returns the number of children the node has. In many cases,
;the node will contain no children, therefore returning 0.
Function xmlNodeChildCount(Node)
	this.xmlNode = Object.xmlNode(Node)
	Return this\ChildCount
End Function

;This returns one of the node's children, specified by ChildIndex.
;ChildIndex may be set anywhere from 1 to the amount of children
;the node has, which can be obtained from the xmlNodeChildCount()
;function.
Function xmlNodeChild(Node, ChildIndex)
	this.xmlNode = Object.xmlNode(Node)
	Return Handle(xml_GetChild(this, ChildIndex))
End Function

;This function will search for the first node matching the specified
;name and parent. Specifying a parent (optional) will only search
;nodes that are children of the specified parent node.
Function xmlNodeFind(Name$, Parent = 0)
	parentnode.xmlNode = Object.xmlNode(Parent)
	For this.xmlNode = Each xmlNode
		If Parent = 0 Or this\Parent = parentnode Then
			If this\Name = Name Then
				Return Handle(this)
			End If
		End If
	Next
End Function

;This function adds a new node to the "tree" of existing xml nodes. Set
;ParentNode to the node you would like this to be a child of, or set it
;to 0 if this is the "root" node. Note: only one root node is allowed.
;Optionally, Name$ can be set to a name the node will initially be given,
;although the node can be renamed later with xmlNodeNameSet()
Function xmlNodeAdd(ParentNode, Name$="NewNode")
	this.xmlNode = New xmlNode
	parent.xmlNode = Object.xmlNode(ParentNode)
	
	this\Parent = parent
	If parent = Null Then
		this\Level = 0
	Else
		top.xmlNode = parent
		;If parent\ChildCount = 0 Then top = parent Else top = xml_GetChild(parent, 1)
		Insert this After top
		this\Level = parent\Level + 1
		xml_RegisterChild(parent, this)
	End If
	
	this\Name = Name
	Return Handle(this)
End Function

;This function deletes the given node, including all of it's children
;(sub-nodes), if there are any. Ignore the ChildIndex variable, as it
;is used internally when recursively deleting the node's children.
Function xmlNodeDelete(Node, ChildIndex = 0)
	this.xmlNode = Object.xmlNode(Node)
	
	For i = 1 To this\ChildCount
		xmlNodeDelete(Handle(xml_GetChild(this, 1)), 1)
	Next
	
	If ChildIndex = 0 Then
		For i = 1 To this\Parent\ChildCount
			If xml_GetChild(this\Parent, i) = this Then ChildIndex = i:Exit
		Next
	End If
	
	xml_UnregisterChild(this\Parent, ChildIndex)
	FreeBank this\ChildBank
End Function

;This sets a node's name. Note: A node's name must not be a blank string
Function xmlNodeNameSet(Node, Name$)
	this.xmlNode = Object.xmlNode(Node)
	this\Name = Name
End Function

;This returns the name of a node
Function xmlNodeNameGet$(Node)
	this.xmlNode = Object.xmlNode(Node)
	Return this\Name
End Function

;This sets the value of an attribute of a node. If the attribute does
;not exist, it will be created. The attribute's value may be any valid
;string of characters, not including double quotes. The value is allowed
;to be a blank string.
;Example:
;xmlNodeAttributeSet(node, "alpha", "0.7")
Function xmlNodeAttributeValueSet(Node, Attribute$, Value$)
	this.xmlNode = Object.xmlNode(Node)
	
	;Check if the attribute exists or not
	indx = 0
	For i = 1 To this\AttributeCount
		If Attribute = this\AttributeName[i] Then indx = i:Exit
	Next
	
	;Create a new attribute if it doesn't exist
	If indx = 0 Then
		this\AttributeCount = this\AttributeCount + 1
		this\AttributeName[this\AttributeCount] = Attribute
		indx = this\AttributeCount
	End If
	
	;Set the attribute's value
	this\AttributeValue[indx] = Value
End Function

;This returns the value of the specified attribute, if it exists. If it
;doesn't exist, a blank string will be returned.
;Example:
;EntityAlpha Entity\Mesh, xmlNodeAttributeGet(Entity\Node, "alpha")
Function xmlNodeAttributeValueGet$(Node, Attribute$)
	this.xmlNode = Object.xmlNode(Node)
	
	;Find the attribute
	indx=0
	For i = 1 To this\AttributeCount
		If Attribute = this\AttributeName[i] Then indx = i:Exit
	Next
	
	;If the attribute exists, return it's value. If not, return a blank string
	If indx = 0 Then
		Return ""
	Else
		Return this\AttributeValue[indx]
	End If
End Function

;This sets the name of an attribute (NOT it's value). Note: attribute
;names are case sensitive
;Example:
;xmlNodeAttributeNameSet(node,"pitch","Xang")
Function xmlNodeAttributeNameSet(Node, Attribute$, NewName$)
	this.xmlNode = Object.xmlNode(Node)
	
	;Find the attribute
	indx = 0
	For i = 1 To this\AttributeCount
		If Attribute = this\AttributeName[i] Then indx = i:Exit
	Next

	;If the attribute exists, rename it
	If indx <> 0 Then
		this\AttributeName[indx] = NewName
	End If
End Function

;This deletes an attribute. Once a new attribute is created when
;using the xmlNodeAttributeSet() function, it will continue to
;reside in memory, and be saved to a file even if it's value is 
;blank. To remove an un-used (or used) attribute of a node, use
;this function.
;Example:
;xmlNodeAttributeDelete(node, "hidden")
Function xmlNodeAttributeDelete(Node, Attribute$)
	this.xmlNode = Object.xmlNode(Node)
	
	;Find the attribute
	indx = 0
	For i = 1 To this\AttributeCount
		If Attribute = this\AttributeName[i] Then indx = i:Exit
	Next
	
	;Delete the attribute, if it exists
	If indx <> 0 Then
		this\AttributeName[indx] = this\AttributeName[this\AttributeCount]
		this\AttributeValue[indx] = this\AttributeValue[this\AttributeCount]
		this\AttributeCount = this\AttributeCount - 1
	End If
End Function

;This sets a node's data string. A node's data is a string of
;text contained within the opening and closing node tags.
;Example:
;xmlNodeDataSet(titlenode, "BlitzXML")
Function xmlNodeDataSet(Node, NodeData$)
	this.xmlNode = Object.xmlNode(Node)
	this\Contents = NodeData
End Function

;This returns a node's data string. A node's data is a string
;of text contained within the opening and closing node tags.
Function xmlNodeDataGet$(Node)
	this.xmlNode = Object.xmlNode(Node)
	Return this\Contents
End Function

;This function saves all XML nodes to the specified file.
;Ignore all parameters but FileName, because they are used
;internally while recursively saving nodes. If any errors
;occur, false will be returned, if not, true will be returned.
Function xmlSave(FileName$, level = 0, file = 0, thisnode = 0)
	If file = 0 Then
		file = WriteFile(FileName)
		If file = 0 Then xml_AddError("Error writing XML file (possibly, file is in use, or is the folder/drive/file is write protected).", 0):Return
	End If

	If thisnode = 0 Then this.xmlNode = First xmlNode Else this.xmlNode = Object.xmlNode(thisnode)
	While this.xmlNode <> Null
		If this\Level < level Then Exit
		If this\Level = level Then
			tag$ = this\Name
			closetag$ = "/" + this\Name
			For i = 1 To this\AttributeCount
				tag = tag + " " + this\AttributeName[i] + "=" + Chr$(34) + this\AttributeValue[i] + Chr$(34)
			Next
			indent$ = String$("  ", this\Level)
			If this\Contents = "" And this\ChildCount = 0 Then
				WriteLine file, indent + "<" + tag + " />"
			Else
				If this\ChildCount <> 0 Then
					WriteLine file, indent + "<" + tag + ">"
					If this\Contents<>"" Then WriteLine file, indent + "  " + this\Contents
					xmlSave("", this\Level + 1, file, Handle(After this))
					WriteLine file, indent + "<" + closetag + ">"
				Else
					WriteLine file, indent + "<" + tag + ">" + this\Contents + "<" + closetag + ">"
				End If
			End If
		End If
		this = After this
	Wend
	
	If level = 0 Then CloseFile file
End Function

;This function loads and parses XML nodes from the specified XML file.
;Note: This (BlitzXML's xml parser) only supports xml files with standard
;xml tags and attributes with values enclosed in quotes. If the file
;is loaded successfully with no errors, True will be returned. If
;errors occur, False is returned. Errors can be accessed
;using the xmlError$() and xmlErrorCount() functions.
Function xmlLoad(FileName$)
	Local attribute$[MAX_ATTRIBUTES]
	Local value$[MAX_ATTRIBUTES]
	Local nodestack[PARSER_RECURSE]
	
	xml_ClearErrors()
	
	;Open the file
	file = ReadFile(FileName)
	If file = False Then
		xml_AddError("Error opening XML file: File does not exist.", 0)
		Return False
	End If
	If Eof(file) = -1 Then
		xml_AddError("Error opening XML file: File is already in use by another program.", 0)
		Return False
	End If
	
	;Clear all existing xml nodes
	xmlReset()
	
	;Read in all tags
	stacklevel = 0
	While Eof(file) = False
		;Get the next tag or data section
		tag$ = xml_NextItem(file)
		
		If xml_ItemType = 2 Then
			;Node contents
			xmlNodeDataSet(nodestack[stacklevel - 1], Trim(Trim(xmlNodeDataGet(nodestack[stacklevel - 1])) + " " + Trim(tag)))
		Else
			;Check if it's a closing tag, opening tag, or stand-alone tag
			If Left(tag,1) = "/" Then
				;Closing tag
				stacklevel = stacklevel - 1
				
				tmp.xmlNode = Object.xmlNode(nodestack[stacklevel])
				If tag <> "/" + tmp\Name Then xml_AddError("Unclosed tag (found <"+tag+">, expected </"+tmp\Name+">", FilePos(file))
			Else
				;Create a new node
				If stacklevel > 0 Then parent = nodestack[stacklevel - 1] Else parent = 0
				node = xmlNodeAdd(parent) ;For now, node structure is not loaded
				
				;Get the name and attributes from the tag
				For i = 0 To attr:attribute[i] = "":value[i] = "":Next:attr = 0:opened = False:name$ = ""
				length = Len(tag)
				For i = 1 To length
					ch$ = Mid(tag, i, 1)
					If attr = 0 And ch = " " Then attr = attr + 1
					If ch = "=" Then attr = -attr
					If ch = Chr(34) Then
						If attr > 0 Then xml_AddError("Expecting equals symbol", FilePos(file))
						opened = 1 - opened
						If opened = False Then attr = Abs(attr):attr = attr + 1
					End If
					If ch <> Chr(34) And attr < 0 And opened Then value[-attr] = value[-attr] + ch
					If attr = 0 Then
						name = name + ch
					Else
						If attr > 0 And ch <> Chr(34) And ch<>" " Then attribute[attr] = attribute[attr] + ch
					End If
				Next
				For i = 1 To attr-1
					xmlNodeAttributeValueSet(node, attribute[i], value[i])
				Next
				xmlNodeNameSet(node, name)
				nodestack[stacklevel] = node
				
				If Right(tag,1) = "/" Then
					;Stand-alone tag
				Else
					;Opening tag
					stacklevel = stacklevel + 1
				End If
			End If
		End If
	Wend
	
	CloseFile file
	If xml_ErrorCount > 0 Then Return False Else Return True
End Function

;This function returns the number of errors and warnings from the last
;file parse performed.
Function xmlErrorCount()
	Return xml_ErrorCount
End Function

;This returns the position of the specified error (in characters from
;the beginning of the file
Function xmlErrorPosition(ErrorNumber)
	If ErrorNumber > xml_ErrorCount Then Return 0
	Return xml_ErrorPos[ErrorNumber]
End Function

;This returns the description of the requested error.
Function xmlError$(ErrorNumber)
	If ErrorNumber > xml_ErrorCount Then Return ""
	Return xml_Error[ErrorNumber]
End Function



;**** Internal Functions ================================================
;Internal functions should not be called from ANYWHERE but from other
;BlitzXML functions. These functions are undocumented, and you should
;NOT use them.

Global xml_ItemType
Function xml_NextItem$(file)
	Local tag$
	While Eof(file) = False
		ch = ReadByte(file)
		If txt$ <> "" And (ch = 60 Or ch = 13) Then xml_ItemType = 2:SeekFile file,FilePos(file)-1:Return txt
		If ch <> 13 And ch <> 15 And ch <> 10 Then txt$ = txt$ + Chr(ch)
		If ch = 13 And txt <> "" Then txt = txt + " "
		
		If ch = 60 Then ;<
			If opened = True Then xml_AddError("Expecting closing bracket (>)", FilePos(file))
			opened = True
		End If
		If ch = 62 Then ;>
			txt = ""
			If opened = False Then xml_AddError("Expecting opening bracket (<)", FilePos(file))
			opened = False
			If Left(tag,4) = "<!--" Then
				If Right(tag,2) <> "--" Then xml_AddError("Expecting correct comment closure (-->)", FilePos(file))
				tag = ""
			Else
				xml_ItemType = 1
				Return Right(tag,Len(tag)-1)
			End If
		End If
		If opened Then tag = tag + Chr(ch)
	Wend
End Function

Function xml_RegisterChild(Node.xmlNode, Child.xmlNode)
	;Incriment the child count
	Node\ChildCount = Node\ChildCount + 1
	
	;Allocate memory for the data
	If Node\ChildBank = False Then
		Node\ChildBank = CreateBank(4)
	Else
		ResizeBank Node\ChildBank, Node\ChildCount * 4
	End If
	
	;Write the data
	Value = Handle(Child)
	PokeInt Node\ChildBank, (Node\ChildCount - 1) * 4, Value
End Function

Function xml_GetChild.xmlNode(Node.xmlNode, ChildIndex)
	;Check if the ChildIndex is valid
	If ChildIndex > Node\ChildCount Then Return Null
	
	;Get the child xmlNode object and return it
	Value = PeekInt(Node\ChildBank, (ChildIndex - 1) * 4)
	this.xmlNode = Object.xmlNode(Value)
	Return this
End Function

Function xml_UnregisterChild(Node.xmlNode, ChildIndex)
	;Check if the ChildIndex is valid
	If ChildIndex > Node\ChildCount Then Return False

	;"Swap" the child-to-be-deleted with the last child on the list, so the last child on the list is now the child to be deleted
	;(actually, it doesn't swap - to optimize it a little, the child-to-be-deleted doesn't get copied anywhere because it's not gonna be used)
	Value = PeekInt(Node\ChildBank, (Node\ChildCount - 1) * 4)
	PokeInt Node\ChildBank, (ChildIndex - 1) * 4, Value
	
	;Downsize the bank, erasing the last child on the list which would be the child-to-be-deleted
	Node\ChildCount = Node\ChildCount - 1
	ResizeBank Node\ChildBank, (Node\ChildCount - 1) * 4
	
	Return True
End Function

Function xml_ClearErrors()
	xml_ErrorCount = 0
End Function

Function xml_AddError(Description$, pos)
	xml_ErrorCount = xml_ErrorCound + 1
	xml_ErrorPos[xml_ErrorCount] = pos
	xml_Error[xml_ErrorCount] = Description
End Function
