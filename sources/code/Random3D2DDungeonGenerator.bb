;a very simple dungeon generator
;by theduck - 2005
;updated: 05 jun 28

;you may use this for stuff, but please tell me about your game :) by email or something
;So I can see it in action in some sorta RPG or something like that like or
;a hack 'n' slash or something...   or like a bloodwytch game (I think i know what I'm talking
;about there.... pretty sure it uses mazes that are randomly generatred... but what do i know?
;i've never played the game.... ok.... i'm done...)

;i got this idea while doing a super simple maze..... i wrote this a while
;ago, but i accidently deleted hal.dll and it crashed my computer.... so i
;re-wrote it today in about 3 minutes... :)

;the idea of it: just get an array and make it have x and y elements and start from one side
;filling in random stuffs and make it go across so its a hallway and do it
;from both sides 2 times to make it more "dungeony"  ....wow.... i'm bad at explaining.... well,
;anyway, here it is....

;also, if you make a modification to it, please post it up for all to see, but you don't have
;to do that if you don't wanna.....
;i just made this really quickly for an RPG my cousin wants me to make.... :D

;anyway, if you are still reading this.... youre nuts.... why would you want to read it?!?!?
;ok i'm done....
;go use it now.... :)



;controls: mouse look + up/down/left/right
Graphics3D 640,480,32,2
AppTitle "A Auto-Dungeon Generator by TheDuck","Exit? NOOOO!!!!!!"
SeedRnd MilliSecs()




Dim level$(20,20)  ;our level
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;go from left to right
y=Rand(5,14)
level$(x,y)="O"
For x=1 To 19
level$(x,y)="O"
y=y+Rand(-1,1)
If y<0 y=0
If y>20 y=20
level$(x,y)="O"
Next
y=Rand(5,14)
x=0
level$(x,y)="O"
For x=1 To 19
level$(x,y)="O"
y=y+Rand(-1,1)
If y<0 y=0
If y>20 y=20
level$(x,y)="O"
Next
;go from top to bottom
y=0
x=Rand(0,19)
level$(x,y)="O"
For y=1 To 19
level$(x,y)="O"
x=x+Rand(-1,1)
If x<0 x=0
If x>20 x=20
level$(x,y)="O"
Next
y=0
x=Rand(0,19)
level$(x,y)="O"
For y=1 To 19
level$(x,y)="O"
x=x+Rand(-1,1)
If x<0 x=0
If x>20 x=20
level$(x,y)="O"
Next
;fill in remaining places
For i=0 To 19
For p=0 To 19
If level$(i,p)<>"O" level$(i,p)="X"
Next
Next


For i=0 To 19
For p=0 To 19
   If level$(i,p)="O" Color 0,255,0:Else:Color 255,0,0
   Write level$(i,p)
Next
Print
Next 