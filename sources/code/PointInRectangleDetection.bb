;Coded by Stephen Greener
;Shagwana@sublimegames.com

;Returns 0 if inside the box else another number if outside
Function DetectPointInsideRect(iPointX,iPointY,iXPos1,iYPos1,iXPos2,iYPos2)  ;[best if we can INLINE it *evil grin*]
	Return ((((iPointX-iXPos1) Xor (iPointX-iXPos2)) And ((iPointY-iYPos1) Xor (iPointY-iYPos2))) And $80000000)
End Function 
	
;Can be edited to make a faster rectoverlap	command for blitz!