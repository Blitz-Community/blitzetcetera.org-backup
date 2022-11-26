
; ------------------------------------------------------------------------------
; Initialization
; ------------------------------------------------------------------------------

; run test plan
list_test()


; ------------------------------------------------------------------------------
; Types
; ------------------------------------------------------------------------------

Type listType
	Field elements%							; number of elements in list
	Field first_node.listNodeType
	Field last_node.listNodeType
End Type

Type listNodeType
	Field list.listType                     ; parent list
	Field prev_node.listNodeType		; or Null if this is the first node
	Field next_node.listNodeType		; or Null if this is the last node
	Field value
End Type

type listIteratorType
	field list.listType
	field forwards                  ; bool to keep track of which direction we're going
	field current_node.listNodeType
	field next_node.listNodeType    ; keep track of this so we can safely delete the current_node
end type


; ------------------------------------------------------------------------------
; Function    : push
; Description : add a new node to the end of a linked list
; ------------------------------------------------------------------------------

Function list_push.listNodeType(our_list.listType, value)

	; create a new node
	Local our_node.listNodeType = New listNodeType
	our_node\list = our_list
	our_node\value = value

	; if the list is empty, set our node as the first and last
	If (our_list\first_node = Null) Then

		our_list\first_node = our_node
		our_list\last_node  = our_node
		our_list\elements   = 1

	; otherwise, add our node after the last
	Else

		our_list\last_node\next_node = our_node
		our_node\prev_node           = our_list\last_node
		our_list\last_node           = our_node

		our_list\elements = our_list\elements + 1

	EndIf

	Return our_node

End Function

; ------------------------------------------------------------------------------
; Function    : unshift
; Description : add a new node to the beginning of a linked list
; ------------------------------------------------------------------------------

Function list_unshift.listNodeType(our_list.listType, value)

	; create a new node
	Local our_node.listNodeType = New listNodeType
	our_node\list = our_list
	our_node\value = value

	; if the list is empty, set our node as the first and last
	If (our_list\first_node = Null) Then

		our_list\first_node = our_node
		our_list\last_node  = our_node
		our_list\elements   = 1

	; otherwise, add our node before the first
	Else

		our_list\first_node\prev_node = our_node
		our_node\next_node            = our_list\first_node
		our_list\first_node           = our_node

		our_list\elements = our_list\elements + 1

	EndIf

	Return our_node

End Function

; ------------------------------------------------------------------------------
; Function    : pop
; Description : remove an element from the end of a linked list, returning its value
; ------------------------------------------------------------------------------

Function list_pop(our_list.listType)
	Return list_removeNode(our_list\last_node)
End Function

; ------------------------------------------------------------------------------
; Function    : shift
; Description : remove an element from the beginning of a linked list, returning its value
; ------------------------------------------------------------------------------

Function list_shift(our_list.listType)
	Return list_removeNode(our_list\first_node)
End Function

; ------------------------------------------------------------------------------
; Function    : insertAfter
; Description : add a new node after an existing one
; ------------------------------------------------------------------------------

function list_insertAfter.listNodeType(reference_node.listNodeType, value)

	; create a new node
	local our_node.listNodeType = new listNodeType
	our_node\list = reference_node\list
	our_node\value = value

	our_node\prev_node = reference_node
	our_node\next_node = reference_node\next_node
	reference_node\next_node = our_node
	
	if our_node\next_node <> null then
		our_node\next_node\prev_node = our_node
	else
		our_node\list\last_node = our_node
	endif

	our_node\list\elements = our_node\list\elements + 1

	return our_node

end function

; ------------------------------------------------------------------------------
; Function    : insertBefore
; Description : add a new node before an existing one
; ------------------------------------------------------------------------------

function list_insertBefore.listNodeType(reference_node.listNodeType, value)

	; create a new node
	local our_node.listNodeType = new listNodeType
	our_node\list = reference_node\list
	our_node\value = value

	our_node\next_node = reference_node
	our_node\prev_node = reference_node\prev_node
	reference_node\prev_node = our_node
	
	if our_node\prev_node <> null then
		our_node\prev_node\next_node = our_node
	else
		our_node\list\first_node = our_node
	endif

	our_node\list\elements = our_node\list\elements + 1

	return our_node

end function

; ------------------------------------------------------------------------------
; Function    : removeNode
; Description : remove an arbitrary element from a linked list, returning its value
; ------------------------------------------------------------------------------

Function list_removeNode(our_node.listNodeType)

	; return 0 if we're trying to return an element that doesn't exist (e.g. empty_list\last_node)
	If (our_node = Null) Then Return 0

	; if there's a node before this one, it gets our next_node as its new next_node (or Null if this is the last node)
	If (our_node\prev_node <> Null) Then
		our_node\prev_node\next_node = our_node\next_node
	EndIf

	; if there's a node after this one, it gets our prev_node as its new prev_node (or Null if this is the first node)
	If (our_node\next_node <> Null) Then
		our_node\next_node\prev_node = our_node\prev_node
	EndIf

	; if this was the first node, the next node is now the first node (or Null if the list is now empty)
	If (our_node = our_node\list\first_node) Then
		our_node\list\first_node = our_node\next_node
	EndIf

	; if this was the last node, the prev node is now the last node (or Null if the list is now empty)
	If (our_node = our_node\list\last_node) Then
		our_node\list\last_node = our_node\prev_node
	EndIf

	; update the number of elements in the list
	our_node\list\elements = our_node\list\elements - 1

	; destroy the node, returning its value
	Local value = our_node\value
	Delete our_node
	Return value

End Function

; ------------------------------------------------------------------------------
; Function    : destroy
; Description : delete a list and all of its nodes
; ------------------------------------------------------------------------------

Function list_destroy(our_list.listType)
	this_node.listNodeType = our_list\first_node
	While (this_node <> Null)
		old_node.listNodeType = this_node
		this_node = this_node\next_node
		Delete old_node
	Wend
	Delete our_list
End Function

; ------------------------------------------------------------------------------
; Function    : findNodeByValue
; Description : search through all nodes in a list to find
; ------------------------------------------------------------------------------

function list_findNodeByValue.listNodeType(our_list.listType, value)
	this_node.listNodeType = our_list\first_node
	while (this_node <> null)
		if this_node\value = value then return this_node
		; advance
		this_node = this_node\next_node
	wend
	return null
end function

; ------------------------------------------------------------------------------
; Function    : iterator_begin
; Description : create a new iterator for traversing the list forwards
; ------------------------------------------------------------------------------

Function list_iterator_begin.listIteratorType(our_list.listType)
	it.listIteratorType = new listIteratorType
	it\list = our_list
	it\forwards = true
	it\current_node = null
	it\next_node = our_list\first_node
	return it
End Function

; ------------------------------------------------------------------------------
; Function    : iterator_begin_reverse
; Description : create a new iterator for traversing the list backwards
; ------------------------------------------------------------------------------

function list_iterator_begin_reverse.listIteratorType(our_list.listType)
	it.listIteratorType = new listIteratorType
	it\list = our_list
	it\forwards = false
	it\current_node = null
	it\next_node = our_list\last_node
	return it
end function

; ------------------------------------------------------------------------------
; Function    : iterator_next
; Description : advance the iterator to the next item in the list
; ------------------------------------------------------------------------------

Function list_iterator_next(it.listIteratorType)
	; if there's a next node, advance to it and return true
	if it\next_node <> null then
		it\current_node = it\next_node
		if it\forwards then
			it\next_node = it\current_node\next_node
		else
			it\next_node = it\current_node\prev_node
		endif
		return true
	; otherwise, destroy the iterator and return false
	else
		delete it
		return false
	endif
End Function

; ------------------------------------------------------------------------------
; Function    : iterator_get
; Description : return the next element in the list or Null if we've reached the end
; ------------------------------------------------------------------------------

Function list_iterator_get(it.listIteratorType)
	return it\current_node\value
End Function



; ------------------------------------------------------------------------------
; Function    : test_listType
; Description : 
; ------------------------------------------------------------------------------
type listTestTypeType
	field sample_list_node.listNodeType ; keep track of the node in sample_list which points to us
	field value$
end type
Function list_test()
	Print "list_test()"

	sample_list.listType = new listType

	x.listTestTypeType = new listTestTypeType : x\value$ = "bar"
	y.listTestTypeType = new listTestTypeType : y\value$ = "baaz"
	z.listTestTypeType = new listTestTypeType : z\value$ = "quux"

	x\sample_list_node = list_push(sample_list, handle(x))
	z\sample_list_node = list_push(sample_list, handle(z))
	y\sample_list_node = list_insertAfter(x\sample_list_node, handle(y))

	it.listIteratorType = list_iterator_begin(sample_list)
	while list_iterator_next(it)
		this_element.listTestTypeType = object.listTestTypeType(list_iterator_get(it))
		it2.listIteratorType = list_iterator_begin_reverse(sample_list)
		while list_iterator_next(it2)
			this_element2.listTestTypeType = object.listTestTypeType(list_iterator_get(it2))
			print this_element\value$ + ", " + this_element2\value$
		wend
	wend

	while (sample_list\elements > 0)
		this_element.listTestTypeType = object.listTestTypeType(list_pop(sample_list))
		print this_element\value$
		delete this_element
	wend
	
	Print "Press any key to exit..."
	WaitKey
	End

End Function