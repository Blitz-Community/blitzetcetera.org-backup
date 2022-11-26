	Graphics 640,480,0,2

	Type characterT
		Field name$							; Descriptive name of character.
		Field inventory.itemT		; Points to first item in character's inventory list.
	End Type
	
	Type itemT
		Field name$							; Descriptive name of item.
		Field container.itemT		; Points to the container this item is held within.
		Field owner.characterT	; Points to the character who currently owns this item.
		Field contents.itemT		; Points to first item contained within this item.
		Field before_item.itemT	; Points to item before this one in linked list.
		Field after_item.itemT	; Points to item after this one in linked list.
	End Type
	
	; Create some characters.
	wizard.characterT = New characterT
	wizard\name$ = "Murthlok the Wizard"
	elf.characterT = New characterT
	elf\name$ = "Giffin the Elf"
	
	; Create some items.
	Const NUM_ITEMS%			= 7
	Const ITEM_FOOD%			= 0
	Const ITEM_STAFF%			= 1
	Const ITEM_BAG%				= 2
	Const ITEM_MAP%				= 3
	Const ITEM_SWORD%			= 4
	Const ITEM_BOX%				= 5
	Const ITEM_TALISMAN%	= 6
	Dim game_item.itemT(NUM_ITEMS-1)
	Restore item_data
	For n = 0 To NUM_ITEMS-1
		game_item(n) = New itemT
		Read game_item(n)\name$
	Next
	
	; Give everything to the wizard.
	add_item_to_inventory(game_item(ITEM_BAG), wizard)
	add_item_to_inventory(game_item(ITEM_STAFF), wizard)
	add_item_to_inventory(game_item(ITEM_FOOD), wizard)
	add_item_to_inventory(game_item(ITEM_MAP), wizard)
	add_item_to_inventory(game_item(ITEM_SWORD), wizard)
	add_item_to_inventory(game_item(ITEM_BOX), wizard)
	add_item_to_inventory(game_item(ITEM_TALISMAN), wizard)
	print_inventories()
	Color 255,0,0
	Print "Press a key to put the TALISMAN into the BOX."
	Print
	Color 255,255,255
	WaitKey()

	put_item_in_container(game_item(ITEM_TALISMAN), game_item(ITEM_BOX))
	print_inventories()
	Color 255,0,0
	Print "Press a key to put the BOX, FOOD and MAP into the BAG."
	Print
	Color 255,255,255
	WaitKey()

	put_item_in_container(game_item(ITEM_BOX), game_item(ITEM_BAG))
	put_item_in_container(game_item(ITEM_FOOD), game_item(ITEM_BAG))
	put_item_in_container(game_item(ITEM_MAP), game_item(ITEM_BAG))
	print_inventories()
	Color 255,0,0
	Print "Press a key to give the BAG and SWORD to the Elf"
	Print
	Color 255,255,255
	WaitKey()

	add_item_to_inventory(game_item(ITEM_BAG), elf)
	add_item_to_inventory(game_item(ITEM_SWORD), elf)
	print_inventories()
	Color 255,0,0
	Print "The Elf bogs off on an amazing adventure..."
	Color 255,255,255
	WaitKey()
	End


;
; Adds an item to a character's inventory linked list.
;
Function add_item_to_inventory(item.itemT, character.characterT)	

	release_item(item)

	If character\inventory <> Null Then character\inventory\before_item = item
	
	item\after_item = character\inventory
	character\inventory = item
	
	set_item_owner(item, character)
	
End Function


;
; Adds an item to a container item's contents linked list.
;
Function put_item_in_container(item.itemT, container.itemT)	

	release_item(item)

	If container\contents <> Null Then container\contents\before_item = item
	
	item\after_item = container\contents
	container\contents = item
	item\container = container
	
	set_item_owner(item, container\owner)
	
End Function


;
; Cleanly removes a given item from any linked list it may be part of.
;
Function release_item(item.itemT)

	If item\container <> Null ; Item is inside a container...

		If item\container\contents = item	; Item is first in containers contents list...
			item\container\contents = item\after_item
		EndIf

	ElseIf item\owner <> Null ; Item is in owner's direct inventory...
	
		If item\owner\inventory = item ; Item is first in owner's inventory list...
			item\owner\inventory = item\after_item
		EndIf
		
	Else
	
		Return
	
	EndIf

	If item\after_item <> Null Then item\after_item\before_item = item\before_item
	If item\before_item <> Null Then item\before_item\after_item = item\after_item

	item\container = Null
	item\before_item = Null
	item\after_item = Null

	set_item_owner(item, Null)

End Function


;
; Sets the owner of a given item.
; Note: this function is recursive so any items contained in the
; given item are also set to have the same owner.
;
Function set_item_owner(item.itemT, owner.characterT)

	While item <> Null

		item\owner = owner
		
		If item\contents <> Null Then set_item_owner(item\contents, owner)
	
		item = item\after_item

	Wend
	
End Function


;
; Prints all characters' inventories.
;
Function print_inventories()

	For character.characterT = Each characterT

		Print character\name$ + " is carrying:"
		print_items(character\inventory)
		Print

	Next

End Function


;
; Prints a given item's name
; Note: this function is recursive so any items contained in the
; given item are also printed.
;
Function print_items(item.itemT, tabs% = 2)

	If item = Null
		Print String$(" ",tabs) + "Nothing!"
		Return
	EndIf
	
	While item <> Null

		If item\contents <> Null
			Print String$(" ",tabs) + item\name$ + ", inside is:"
			print_items(item\contents, tabs + 2)
		Else
			Print String$(" ",tabs) + item\name$
		EndIf
	
		item = item\after_item

	Wend
		
End Function

	
.item_data
Data "Some food"
Data "An old staff"
Data "A leather bag"
Data "A map"
Data "A short sword"
Data "A small box"
Data "A ruby talisman"
