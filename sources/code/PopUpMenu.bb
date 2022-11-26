;PopupMenu Example
;
;Main Program
;Create standard B+ stuff
win = CreateWindow ("Popup Menu Example",0,0,206,250,Desktop(),1)
label = CreateLabel("Right-Click the window for a pop-up menu",0,0,200,20,win)
;Assign a few constants, relating to the popup menu
Const PUM_NEW 		= 1
Const PUM_OPEN 		= 2
;Seperator			= 3	
Const PUM_SAVE 		= 4
Const PUM_SAVEAS 	= 5
;Seperator			= 6
Const PUM_CUT 		= 7
Const PUM_COPY 		= 8
Const PUM_PASTE 	= 9
;Seperator			= 10
Const PUM_LOCKPAD 	= 11
;Seperator			= 12
Const PUM_PRINT 	= 13
;Seperator			= 14
Const PUM_ABOUT 	= 15



;Main loop
Repeat
	id = WaitEvent(50)
	If id = $803 Then Exit 
	If id = $201 
	 If EventData() = 2
		popupMenu = CreatePopupMenu(win,"New|Open||Save|Save As...||Cut|Copy|Paste||Lock Pad||Print||About PopupMenu")
		timeCount = MilliSecs()
		Repeat
			id = WaitEvent(1) 
			If id = $206 Then Exit
			popupMenuID = PopupMenuEvent()
			If MouseHit(1)
				Select popupMenuID
					Case PUM_NEW
						Notify "New"
						Exit
					Case PUM_OPEN
						Notify "Open"
						Exit
					Case PUM_ABOUT
						Notify "Popup Menu Functions"+Chr$(13)+"Written by Michael Denathorn 2005"
						Exit
				End Select
			End If
			Forever
		DisableGadget(popupMenu )
		HideGadget(popupMenu )
		popupMenu = 0
		FreeGadget popupMenu 
	End If
	End If
Forever
End



;I recommend banging the below code in an include file

;Popup Menu related Arrays
Dim popupMenuImages(0)
Dim popupInvertedMenuImages(0)
Dim popupMenuItemsStr$(0)
;Popup Menu related Globals
Global popupMenuCanvas,popupMenuBorder,popupMenuItemHeight,popupMenuItemCount,popupMenuDot


;CreatePopupMenu(hWin,strItemList$)
;hWin			-	Parent window handle
;strItemList$	-	String containing the list of items for the
;					popup menu, each should be seperated by a '|'
;					character. A null item ('||') in the list will
;					produce a seperator line.
;
;The 'CreatePopupMenu' function returns a handle to the new popup menu
Function CreatePopupMenu(winHandle,menuString$)
	
	popWinTitle$ = "PopWin"
	my = MouseY()
	mx = MouseX()
	
	deskWidth = GadgetWidth(Desktop())
		
	popupMenuItemCount = 0
	
	For loop = 1 To Len(menuString$)
		char$ = Mid$(menuString$,loop,1)
		If char$ = "|"
			popupMenuItemCount = popupMenuItemCount + 1
		End If
	Next
	
	Dim popupMenuItemsStr$(popupMenuItemCount)
	
	popupMenuItemCount = 0
	
	For loop = 1 To Len(menuString$)
		char$ = Mid$(menuString$,loop,1)
		If char$ = "|"
			popupMenuItemCount = popupMenuItemCount + 1
		Else
			popupMenuItemsStr$(popupMenuItemCount) = popupMenuItemsStr$(popupMenuItemCount) + char$
		End If
	Next
	tempCanvas = CreateCanvas(0,0,0,0,winHandle)
	SetBuffer CanvasBuffer(tempCanvas)
	
	For loop = 0 To popupMenuItemCount
		MenuLength = StringWidth(popupMenuItemsStr$(loop))
		If MenuLength > tempMenuLength Then tempMenuLength = MenuLength
	Next
	
	popWinXsize = tempMenuLength+10
	popWinYsize = (StringHeight(popupMenuItemsStr$(0)) * (popupMenuItemCount+1))+10
	popupMenuItemHeight = StringHeight(popupMenuItemsStr$(0))
	FreeGadget tempCanvas
	
	If  (mx+popWinXsize) > deskWidth
		popWinHandle = CreateWindow (popWinTitle$,(mx-popWinXsize),(my-popWinYsize),popWinXsize,popWinYsize,winHandle,16) ; create a window first
		HideGadget popWinHandle
		CreatePopupMenuItems(popWinXsize,popWinYsize,popupMenuItemCount,popupMenuItemHeight,popWinHandle)
		ShowGadget popWinHandle
		Return popWinHandle 
	End If

		popWinHandle = CreateWindow (popWinTitle$,mx,(my-popWinYsize),popWinXsize,popWinYsize,winHandle,16) ; create a window first
		HideGadget popWinHandle
		CreatePopupMenuItems(popWinXsize,popWinYsize,popupMenuItemCount,popupMenuItemHeight,popWinHandle)
		ShowGadget popWinHandle
		Return popWinHandle
End Function


;PopupMenuEvent()
;
;The 'PopupMenuEvent' function returns the selected popup menu item code
;which is based on how the user creates the popup menu (See example)
Function PopupMenuEvent()
	SetBuffer CanvasBuffer(popupMenuCanvas)
	mx = MouseX(popupMenuCanvas)
	my = MouseY(popupMenuCanvas)
	ClsColor 255,255,255
	Cls
	DrawImage(popupMenuBorder,0,0)
	DrawBlock(popupMenuDot,MouseX(),MouseY())
	For loop = 0 To popupMenuItemCount
		If ImagesCollide(popupMenuImages(loop),5,(popupMenuItemHeight*loop)+5,0,popupMenuDot,mx,my,0)
			DrawBlock popupInvertedMenuImages(loop),5,(popupMenuItemHeight*loop)+5 
		Else
			DrawBlock popupMenuImages(loop),5,(popupMenuItemHeight*loop)+5
		End If
	Next
	
	For loop = 0 To popupMenuItemCount
		If ImagesCollide(popupMenuImages(loop),5,(popupMenuItemHeight*loop)+5,0,popupMenuDot,mx,my,0)
			popupMenuEventID = loop+1
		End If
	Next
FlipCanvas popupMenuCanvas
Return popupMenuEventID
End Function

;Internal Function
Function CreatePopupMenuItems(x,y,count,textHeight,winHandle)
	Dim popupMenuImages(count)
	Dim popupInvertedMenuImages(count)
	popupMenuCanvas = CreateCanvas(0,0,x,y,winHandle)
	popupMenuBorder = CreateImage(x,y)
	popupMenuDot = CreateImage(1,1)
	
	SetBuffer ImageBuffer(popupMenuDot)
	Color 50,50,50
	Rect 0,0,1,1
	
	SetBuffer ImageBuffer(popupMenuBorder)	
	Color 172,168,153
	Rect 0,0,x,y,False

	For loop = 0 To count
		popupMenuImages(loop) = CreateImage(x-10,textHeight)
		SetBuffer ImageBuffer(popupMenuImages(loop))
		
		ClsColor 255,255,255 
		If popupMenuItemsStr$(loop) <> ""
			ClsColor 255,255,255
			Color 20,20,20
			Cls
			Text 0,0,popupMenuItemsStr$(loop)
		Else
			Color 172,168,153
			Cls
			Line 0,(textHeight/2),(x-10),(textHeight/2)
		End If
	Next
	
	;Inverted
	For loop = 0 To count
		popupInvertedMenuImages(loop) = CreateImage(x-10,textHeight)
		SetBuffer ImageBuffer(popupInvertedMenuImages(loop))
		If popupMenuItemsStr$(loop) <> ""
			ClsColor 49,106,197
			Color 255,255,255
			Cls
			Text 0,0,popupMenuItemsStr$(loop)
		Else
			ClsColor 255,255,255
			Color 172,168,153
			Cls
			Line 0,(textHeight/2),(x-10),(textHeight/2)
		End If		
	Next
End Function