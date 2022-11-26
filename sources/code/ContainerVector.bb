vector_test()

; ------------------------------------------------------------------------------
;= TO DO
; ------------------------------------------------------------------------------
; iterator sanity checking (try to catch problems which might be caused by deleting elements while iterating)

; ------------------------------------------------------------------------------
;= CHANGE LOG
; ------------------------------------------------------------------------------
; 12/11/2005 - iterator sample usage for quick copying and pasting
; 12/11/2005 - negative index values now represent elements in reverse order,
;              added remove_element(), insert_element_*(), shift(), unshift(),
;              and iterators
; 12/11/2005 - calling new() is now required
; 06/11/2005 - new() for forwards compatibility
; 03/11/2005 - renamed class from vectorType

; ------------------------------------------------------------------------------
;= CONSTANTS
; ------------------------------------------------------------------------------
Const VECTORS_EXPAND_BY = 20
	; instead of resizing our bank every push() operation, grab some extra space

; ------------------------------------------------------------------------------
;= TYPES
; ------------------------------------------------------------------------------
Type vectorC
	Field last_element%                   ; size of the container - 1
	Field elements_allocated%             ; size of the bank (not in bytes)
	Field bank
End Type

Type vectorC_iter
	Field vector.vectorC
	Field forwards                        ; bool to keep track of which direction we're going
	Field current_index%
End Type

; ------------------------------------------------------------------------------
;= FUNDAMENTAL
; ------------------------------------------------------------------------------

; ------------------------------------------------------------------------------
Function vector_new.vectorC(elements% = 0)
; create a new vector

	our_vector.vectorC = New vectorC
	our_vector\bank = CreateBank(0)
	vector_resize(our_vector, elements)   ; also sets \last_element to elements-1
	Return our_vector
End Function

; ------------------------------------------------------------------------------
Function vector_destroy(our_vector.vectorC)
; delete a vector and all of its elements

	FreeBank(our_vector\bank)
	Delete our_vector
End Function

; ------------------------------------------------------------------------------
Function vector_set(our_vector.vectorC, index%, value)
; set value at specified index

	If our_vector = Null Then RuntimeError("not a valid vectorC: null")
	If (index < 0) Then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	If (index < 0 Or index > our_vector\last_element) Then RuntimeError("element out of range: "+index)
	PokeInt(our_vector\bank, 4 * index, value)
End Function

; ------------------------------------------------------------------------------
Function vector_get(our_vector.vectorC, index%)
; get value at specified index

	If our_vector = Null Then RuntimeError("not a valid vectorC: null")
	If (index < 0) Then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	If (index < 0 Or index > our_vector\last_element) Then RuntimeError("element out of range: "+index)
	Return PeekInt(our_vector\bank, 4 * index)
End Function

; ------------------------------------------------------------------------------
Function vector_resize(our_vector.vectorC, elements%)
; set the size of the vector

	If our_vector = Null Then RuntimeError("not a valid vectorC: null")
	ResizeBank(our_vector\bank, 4 * elements)
	our_vector\elements_allocated = elements
	our_vector\last_element = elements - 1
End Function

; ------------------------------------------------------------------------------
Function vector_count(our_vector.vectorC)
; return the number of elements in the vector

	If our_vector = Null Then RuntimeError("not a valid vectorC: null")
	Return our_vector\last_element + 1
End Function

; ------------------------------------------------------------------------------
;= STACK OPERATIONS
; ------------------------------------------------------------------------------
Function vector_push(our_vector.vectorC, value)
; add a new element to the end of the vector

	If our_vector = Null Then RuntimeError("not a valid vectorC: null")
	; allocate more space if required
	If our_vector\elements_allocated-1 = our_vector\last_element Then
		our_vector\elements_allocated = our_vector\elements_allocated + VECTORS_EXPAND_BY
		ResizeBank(our_vector\bank, 4 * our_vector\elements_allocated)
	EndIf
	; store the new value
	our_vector\last_element = our_vector\last_element + 1
	PokeInt(our_vector\bank, 4 * our_vector\last_element, value)
End Function

; ------------------------------------------------------------------------------
Function vector_pop(our_vector.vectorC)
; remove an element from the end of the vector

	If our_vector = Null Then RuntimeError("not a valid vectorC: null")
	If our_vector\last_element = -1 Then Return 0
	our_vector\last_element = our_vector\last_element - 1
	Return PeekInt(our_vector\bank, 4 * (our_vector\last_element + 1))
End Function

; ------------------------------------------------------------------------------
Function vector_alloc(our_vector.vectorC, elements%)
; preallocate a bunch of space for push() operations

	If our_vector = Null Then RuntimeError("not a valid vectorC: null")
	If elements < our_vector\last_element + 1 Then RuntimeError("cannot alloc less space than is currently being used")
	our_vector\elements_allocated = elements
	ResizeBank(our_vector\bank, 4 * our_vector\elements_allocated)
End Function

; ------------------------------------------------------------------------------
;= SLOW OPERATIONS
; ------------------------------------------------------------------------------
Function vector_remove_element(our_vector.vectorC, index%)
; remove an element from anywhere in a vector

	If our_vector = Null Then RuntimeError("not a valid vectorC: null")
	If (index < 0) Then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	If (index < 0 Or index > our_vector\last_element) Then RuntimeError("element out of range: "+index)
	value = PeekInt(our_vector\bank, 4 * index)
	our_vector\last_element = our_vector\last_element - 1
	; move elements backward to cover our hole
	CopyBank(our_vector\bank, 4*(index+1), our_vector\bank, 4*index, 4*(our_vector\last_element+1 - index))
	Return value
End Function

; ------------------------------------------------------------------------------
Function vector_insert_before(our_vector.vectorC, index%, value)
; add a new element before an existing one
; note that we allow a reference index of our_vector\last_element+1 (which is dispatched to vector_push() anyways)


	If our_vector = Null Then RuntimeError("not a valid vectorC: null")
	If (index < 0) Then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	If index = our_vector\last_element+1 Then Return vector_push(our_vector, value) ; much faster alternative!
	If (index < 0 Or index > our_vector\last_element) Then RuntimeError("element out of range: "+index)
	; allocate more space if required
	If our_vector\elements_allocated-1 = our_vector\last_element Then
		our_vector\elements_allocated = our_vector\elements_allocated + VECTORS_EXPAND_BY
		ResizeBank(our_vector\bank, 4 * our_vector\elements_allocated)
	EndIf
	; move elements forward
	CopyBank(our_vector\bank, 4*(index), our_vector\bank, 4*(index+1), 4*(our_vector\last_element+1 - index))
	our_vector\last_element = our_vector\last_element + 1
	; set our new value
	PokeInt(our_vector\bank, 4 * index, value)
End Function

; ------------------------------------------------------------------------------
Function vector_insert_after(our_vector.vectorC, index%, value)
; add a new element after an existing one
	If (index < 0) Then index = our_vector\last_element+1 + index                   ; negative indexes reference elements in reverse order
	Return vector_insert_before(our_vector, index+1, value)
End Function

; ------------------------------------------------------------------------------
Function vector_shift(our_vector.vectorC)
; add a new element after an existing one
	Return vector_remove_element(our_vector, 0)
End Function

; ------------------------------------------------------------------------------
Function vector_unshift(our_vector.vectorC, value)
; add a new element to the front of the vector
	vector_insert_before(our_vector, 0, value)
End Function

; ------------------------------------------------------------------------------
;= ITERATORS
; ------------------------------------------------------------------------------

; sample usage

; ; for item.type = each vector
;	it.vectorC_iter = vector_iterator_begin(vector)
;	while vector_iterator_next(it)
;		item.type = object.type(vector_iterator_get(it))
;	wend


; ------------------------------------------------------------------------------
Function vector_iterator_begin.vectorC_iter(our_vector.vectorC)
; create a new iterator for traversing the vector forwards

	it.vectorC_iter = New vectorC_iter
	If our_vector <> Null Then
		it\vector = our_vector
		it\forwards = True
		it\current_index = 0-1
	EndIf
	Return it
End Function

; ------------------------------------------------------------------------------
Function vector_iterator_begin_reverse.vectorC_iter(our_vector.vectorC)
; create a new iterator for traversing the vector backwards

	it.vectorC_iter = New vectorC_iter
	If our_vector <> Null Then
		it\vector = our_vector
		it\forwards = False
		it\current_index = our_vector\last_element+1
	EndIf
	Return it
End Function

; ------------------------------------------------------------------------------
Function vector_iterator_next(it.vectorC_iter)
; advance the iterator to the next element in the vector

	; drop out immediately if this iterator is void
	If it\vector = Null Then Return False
	
	If it\forwards = True Then
		it\current_index = it\current_index + 1
		If it\current_index > it\vector\last_element Then Delete it : Return False
	Else
		it\current_index = it\current_index - 1
		If it\current_index < 0 Then Delete it : Return False
	EndIf
	Return True
End Function

; ------------------------------------------------------------------------------
Function vector_iterator_get(it.vectorC_iter)
; return the value of the element the iterator is currently on

	Return PeekInt(it\vector\bank, 4 * it\current_index)
End Function

; ------------------------------------------------------------------------------
;= TESTING
; ------------------------------------------------------------------------------
Function vector_test()
	Print "vector_test()"

	sample_vector.vectorC = vector_new(2)

	vector_set(sample_vector, 0, 123)
	vector_set(sample_vector, 1, 456)
	vector_push(sample_vector, 789)
	vector_unshift(sample_vector, 321)
	vector_insert_after(sample_vector, -1, -100)

	it.vectorC_iter = vector_iterator_begin(sample_vector)
	While vector_iterator_next(it)
		value = vector_iterator_get(it)
		Print value
	Wend
	

	Print "press any key to exit..."
	WaitKey
	End

End Function