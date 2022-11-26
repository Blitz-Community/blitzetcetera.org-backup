;------------------------------------
;  Coded by Genexi2
;------------------------------------

; Anyhow, this is my method to do static without the use of any actual pictures
; (yes, i realize using a goto command aint the best way to reperform a loop

;  ----------------------------------------------------
; ATTENTION :                                         
; If your computer is below 1000mhz, I reccommend NOT 
; to have the static pic's size over 300x300, you WILL
; experiance long waiting periods when the the loop   
; performs the larger sized pics.                     
;  ----------------------------------------------------



AppTitle "STATIC"
Graphics 640,480

Global counterx=0
Global countery=0

Global xsize=10        ;Set the Value of the X axis of our static
Global ysize=10       ;Set the Value of the Y axis of our static
Global frames=100       ;Set the # of frames is in the static
Global maxframes=25     ;Set the limit for the # of frames


Global static1 = CreateImage(xsize,ysize,frames) ;create an image using the above variables

Global colorvalue ;Have a variable to hold the color value
                  ;Used to produce grayscale colors for static color effect


SeedRnd MilliSecs()

.loop                                    ;goto ; used to make changes during play
For frames=0 To maxframes                ;For our current frame to reach the max. frame
SetBuffer ImageBuffer(static1,frames)    ;Buffer our Image at the current frame

                       
                      
For y=0 To ysize                         ;For y to = our pic's y axis
For x=0 To xsize                         ;For x to = our pic's x axis

colorvalue=Rnd(1,255)                    ;Randomize the colorvalue's value for the colors 

Color colorvalue,colorvalue,colorvalue   ;Change the current drawing color to the colorvalues value
Rect x,y,10,10                       ;Draw a rectangle at the x and y pos.
Next
Next
Next 




SetBuffer BackBuffer()

While KeyHit(1)=False          ;if escape isn't hit
Cls                            ;clear the screen

If KeyHit(200)                 ;if up is hit
maxframes=maxframes+1          ;increase the max. # of frames
frames=0                       ;reset the frames # for loop
Goto loop                      ;proceeds back to the loop to make changes
EndIf

If KeyHit (208)                ;if down is hit
If maxframes>1                 ;if maxframe is more than 1 (used to prevent image frame # errors)
maxframes=maxframes-1          ;decrease the maxframe 
EndIf 
frames=0                       ;reset the # of frames
Goto loop                      ;proceeds back to the loop to make changes

EndIf 

If KeyHit(203)                 ;if left is hit
xsize=xsize-25
ysize=ysize-25                 ;decrease the pics size by 25 pixels
frames=0                       ;reset the # of frames
Goto loop                      ;proceeds back to the loop to make changes

EndIf 

If KeyHit(205)                 ;if right is hit
xsize=xsize+25                 ;increase the pics size by 25 pixels
ysize=ysize+25
frames=0                       ;reset the # of frames
Goto loop                      ;proceeds back to the loop to make changes

EndIf 

For a =(0) To (640/xsize)  
For b =(0) To (480/ysize) 

DrawImage static1,(a*xsize),(b*ysize),Rnd(maxframes)  ;tile out image across the whole screen with random frame
Next
Next 

Flip 
Wend                                 
    